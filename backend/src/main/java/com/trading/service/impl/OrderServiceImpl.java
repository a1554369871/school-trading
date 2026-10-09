package com.trading.service.impl;

import com.trading.common.BusinessException;
import com.trading.common.PageResult;
import com.trading.dto.OrderCreateRequest;
import com.trading.entity.Order;
import com.trading.entity.Product;
import com.trading.entity.SystemNotification;
import com.trading.mapper.OrderMapper;
import com.trading.mapper.ProductMapper;
import com.trading.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MessageServiceImpl messageService;

    private static final String ORDER_LOCK_KEY = "lock:order:product:";
    private static final String STATS_MONTHLY_KEY = "stats:monthly";
    private static final String STATS_SELLER_KEY = "stats:seller_revenue";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Integer buyerId, OrderCreateRequest request) {
        String lockKey = ORDER_LOCK_KEY + request.getProductId();
        Boolean locked = false;
        try {
            locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis获取锁失败，降级为无锁模式: {}", e.getMessage());
        }

        if (locked != null && !locked) {
            throw new BusinessException("请勿重复提交");
        }

        try {
            Product product = productMapper.findById(request.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在");
            }
            if (product.getStatus() != 1) {
                throw new BusinessException("商品已下架");
            }
            if (product.getUserId().equals(buyerId)) {
                throw new BusinessException("不能购买自己的商品");
            }

            int rows = productMapper.updateStatusWithVersion(product.getId(), 3, product.getVersion());
            if (rows == 0) {
                throw new BusinessException("商品已被其他人购买");
            }

            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setProductId(product.getId());
            order.setBuyerId(buyerId);
            order.setSellerId(product.getUserId());
            order.setDealPrice(product.getPrice());
            order.setStatus(0);
            order.setAddressId(request.getAddressId());
            order.setDeliveryMethod(request.getDeliveryMethod());
            order.setRemark(request.getRemark());
            orderMapper.insert(order);
            
            // 发送系统通知给卖家
            try {
                SystemNotification notification = new SystemNotification();
                notification.setReceiverId(product.getUserId());
                notification.setTitle("新订单通知");
                notification.setContent("您的商品【" + product.getProductName() + "】已被用户下单，订单号：" + order.getOrderNo());
                notification.setIsRead(0);
                messageService.sendNotification(notification);
            } catch (Exception e) {
                log.error("发送订单通知失败", e);
            }

            invalidateOrderCache();
            return order;
        } finally {
            if (locked != null && locked) {
                try {
                    redisTemplate.delete(lockKey);
                } catch (Exception e) {
                    log.warn("Redis释放锁失败: {}", e.getMessage());
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Integer userId, Integer orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (!order.getBuyerId().equals(userId)) throw new BusinessException("无权操作");
        if (order.getStatus() != 0) throw new BusinessException("订单状态异常");
        orderMapper.updateStatus(orderId, 1);
        invalidateOrderCache();

        // 通知卖家：买家已支付，请尽快发货
        try {
            Product product = productMapper.findById(order.getProductId());
            String productName = product != null ? product.getProductName() : "商品";
            SystemNotification notification = new SystemNotification();
            notification.setReceiverId(order.getSellerId());
            notification.setTitle("买家已支付");
            notification.setContent("您的商品【" + productName + "】已被买家支付，订单号：" + order.getOrderNo() + "，请尽快发货。");
            notification.setIsRead(0);
            messageService.sendNotification(notification);
        } catch (Exception e) {
            log.error("发送支付通知失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipOrder(Integer userId, Integer orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (!order.getSellerId().equals(userId)) throw new BusinessException("无权操作");
        if (order.getStatus() != 1) throw new BusinessException("订单状态异常");
        orderMapper.updateStatus(orderId, 2);
        invalidateOrderCache();

        // 通知买家：卖家已发货
        try {
            Product product = productMapper.findById(order.getProductId());
            String productName = product != null ? product.getProductName() : "商品";
            SystemNotification notification = new SystemNotification();
            notification.setReceiverId(order.getBuyerId());
            notification.setTitle("卖家已发货");
            notification.setContent("您的商品【" + productName + "】卖家已发货，订单号：" + order.getOrderNo() + "，请注意查收。");
            notification.setIsRead(0);
            messageService.sendNotification(notification);
        } catch (Exception e) {
            log.error("发送发货通知失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Integer userId, Integer orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (!order.getBuyerId().equals(userId)) throw new BusinessException("无权操作");
        if (order.getStatus() != 2) throw new BusinessException("订单状态异常");
        orderMapper.updateStatus(orderId, 3);
        productMapper.updateStatusWithVersion(order.getProductId(), 2,
            productMapper.findById(order.getProductId()).getVersion());
        invalidateOrderCache();

        // 通知卖家：买家已确认收货，订单完成
        try {
            Product product = productMapper.findById(order.getProductId());
            String productName = product != null ? product.getProductName() : "商品";
            SystemNotification notification = new SystemNotification();
            notification.setReceiverId(order.getSellerId());
            notification.setTitle("订单已完成");
            notification.setContent("您的商品【" + productName + "】买家已确认收货，订单号：" + order.getOrderNo() + "，交易完成。");
            notification.setIsRead(0);
            messageService.sendNotification(notification);
        } catch (Exception e) {
            log.error("发送确认收货通知失败", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Integer userId, Integer orderId) {
        Order order = orderMapper.findById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException("无权操作");
        }
        if (order.getStatus() > 2) throw new BusinessException("订单状态异常");
        orderMapper.updateStatus(orderId, 4);
        productMapper.updateStatusWithVersion(order.getProductId(), 1,
            productMapper.findById(order.getProductId()).getVersion());
        invalidateOrderCache();

        // 通知对方：订单已取消
        try {
            Product product = productMapper.findById(order.getProductId());
            String productName = product != null ? product.getProductName() : "商品";
            // 通知对方（谁取消的就通知另一个）
            Integer notifyUserId = userId.equals(order.getBuyerId()) ? order.getSellerId() : order.getBuyerId();
            SystemNotification notification = new SystemNotification();
            notification.setReceiverId(notifyUserId);
            notification.setTitle("订单已取消");
            notification.setContent("商品【" + productName + "】的订单已被取消，订单号：" + order.getOrderNo() + "。");
            notification.setIsRead(0);
            messageService.sendNotification(notification);
        } catch (Exception e) {
            log.error("发送取消通知失败", e);
        }
    }

    @Override
    public Map<String, Object> getOrderDetail(Integer orderId) {
        return orderMapper.findDetailById(orderId);
    }

    @Override
    public PageResult<Map<String, Object>> getMyBuyOrders(Integer userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int total = orderMapper.countByBuyerId(userId);
        List<Map<String, Object>> list = orderMapper.findByBuyerId(userId, offset, pageSize);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    @Override
    public PageResult<Map<String, Object>> getMySellOrders(Integer userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int total = orderMapper.countBySellerId(userId);
        List<Map<String, Object>> list = orderMapper.findBySellerId(userId, offset, pageSize);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    @Override
    public PageResult<Map<String, Object>> getAllOrders(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int total = orderMapper.countAll();
        List<Map<String, Object>> list = orderMapper.findAll(offset, pageSize);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    @Override
    public List<Map<String, Object>> getMonthlyStats() {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cached = (List<Map<String, Object>>) (List<?>) redisTemplate.opsForValue().get(STATS_MONTHLY_KEY);
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis读取月度统计缓存失败: {}", e.getMessage());
        }
        List<Map<String, Object>> stats = orderMapper.getMonthlyStats();
        try {
            redisTemplate.opsForValue().set(STATS_MONTHLY_KEY, stats, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis写入月度统计缓存失败: {}", e.getMessage());
        }
        return stats;
    }

    @Override
    public List<Map<String, Object>> getSellerRevenue() {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cached = (List<Map<String, Object>>) (List<?>) redisTemplate.opsForValue().get(STATS_SELLER_KEY);
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis读取卖家收入缓存失败: {}", e.getMessage());
        }
        List<Map<String, Object>> stats = orderMapper.getSellerRevenue();
        try {
            redisTemplate.opsForValue().set(STATS_SELLER_KEY, stats, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis写入卖家收入缓存失败: {}", e.getMessage());
        }
        return stats;
    }

    private String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private void invalidateOrderCache() {
        try {
            redisTemplate.delete(STATS_MONTHLY_KEY);
            redisTemplate.delete(STATS_SELLER_KEY);
        } catch (Exception e) {
            log.warn("Redis清除订单缓存失败: {}", e.getMessage());
        }
    }
}
