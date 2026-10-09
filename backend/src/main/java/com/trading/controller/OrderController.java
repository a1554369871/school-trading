package com.trading.controller;

import com.trading.common.BusinessException;
import com.trading.common.Result;
import com.trading.dto.OrderCreateRequest;
import com.trading.entity.Order;
import com.trading.service.impl.OrderServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    /**
     * 创建订单 — 商品详情页点击"立即购买"后提交订单，使用Redis分布式锁+乐观锁防止重复下单和超卖
     * → OrderServiceImpl.createOrder() → ProductMapper.xml: findById + updateStatusWithVersion(乐观锁)
     *   → OrderMapper.xml: insert + MessageServiceImpl.sendNotification() → SystemNotificationMapper.xml: insert
     */
    @PostMapping
    public Result<Order> createOrder(Authentication auth,
                                     @Valid @RequestBody OrderCreateRequest request) {
        Integer userId = (Integer) auth.getPrincipal();
        return Result.success(orderService.createOrder(userId, request));
    }

    /**
     * 订单详情 — 买家/卖家查看订单完整信息（商品信息、价格、收货地址、订单状态、交易时间等）
     * → OrderServiceImpl.getOrderDetail() → OrderMapper.xml: findDetailById
     */
    @GetMapping("/{id}")
    public Result<?> getOrderDetail(Authentication auth, @PathVariable Integer id) {
        Integer userId = (Integer) auth.getPrincipal();
        Map<String, Object> order = orderService.getOrderDetail(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        // 校验权限：仅买家或卖家可查看
        Object buyerId = order.get("buyerId");
        Object sellerId = order.get("sellerId");
        if ((buyerId != null && !(((Number) buyerId).intValue() == userId))
            && (sellerId != null && !(((Number) sellerId).intValue() == userId))) {
            throw new BusinessException("无权查看此订单");
        }
        return Result.success(order);
    }

    /**
     * 支付订单 — 买家确认支付，订单状态从待付款(0)变为待发货(1)，通知卖家尽快发货
     * → OrderServiceImpl.payOrder() → OrderMapper.xml: findById + updateStatus
     *   → ProductMapper.xml: findById + MessageServiceImpl.sendNotification() → SystemNotificationMapper.xml: insert
     */
    @PutMapping("/{id}/pay")
    public Result<?> payOrder(Authentication auth, @PathVariable Integer id) {
        orderService.payOrder((Integer) auth.getPrincipal(), id);
        return Result.success();
    }

    /**
     * 卖家发货 — 卖家确认发货，订单状态从待发货(1)变为待收货(2)，通知买家商品已发出
     * → OrderServiceImpl.shipOrder() → OrderMapper.xml: findById + updateStatus
     *   → ProductMapper.xml: findById + MessageServiceImpl.sendNotification() → SystemNotificationMapper.xml: insert
     */
    @PutMapping("/{id}/ship")
    public Result<?> shipOrder(Authentication auth, @PathVariable Integer id) {
        orderService.shipOrder((Integer) auth.getPrincipal(), id);
        return Result.success();
    }

    /**
     * 确认收货 — 买家确认收到商品，订单状态从待收货(2)变为已完成(3)，商品状态改为已售出(2)，通知卖家交易完成
     * → OrderServiceImpl.confirmOrder() → OrderMapper.xml: findById + updateStatus
     *   → ProductMapper.xml: findById + updateStatusWithVersion(商品状态改为已售出)
     *   → MessageServiceImpl.sendNotification() → SystemNotificationMapper.xml: insert
     */
    @PutMapping("/{id}/confirm")
    public Result<?> confirmOrder(Authentication auth, @PathVariable Integer id) {
        orderService.confirmOrder((Integer) auth.getPrincipal(), id);
        return Result.success();
    }

    /**
     * 取消订单 — 买家或卖家在待付款/待发货/待收货阶段取消订单，商品恢复在售状态，通知对方
     * → OrderServiceImpl.cancelOrder() → OrderMapper.xml: findById + updateStatus
     *   → ProductMapper.xml: findById + updateStatusWithVersion(商品恢复在售)
     *   → MessageServiceImpl.sendNotification() → SystemNotificationMapper.xml: insert
     */
    @PutMapping("/{id}/cancel")
    public Result<?> cancelOrder(Authentication auth, @PathVariable Integer id) {
        orderService.cancelOrder((Integer) auth.getPrincipal(), id);
        return Result.success();
    }

    /**
     * 我的买入订单 — 个人中心查看当前用户作为买家的所有订单列表，分页展示
     * → OrderServiceImpl.getMyBuyOrders() → OrderMapper.xml: countByBuyerId + findByBuyerId
     */
    @GetMapping("/buy")
    public Result<?> myBuyOrders(Authentication auth,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.getMyBuyOrders((Integer) auth.getPrincipal(), page, pageSize));
    }

    /**
     * 我的卖出订单 — 个人中心查看当前用户作为卖家的所有订单列表，分页展示
     * → OrderServiceImpl.getMySellOrders() → OrderMapper.xml: countBySellerId + findBySellerId
     */
    @GetMapping("/sell")
    public Result<?> mySellOrders(Authentication auth,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.getMySellOrders((Integer) auth.getPrincipal(), page, pageSize));
    }

    /**
     * 月度订单趋势统计 — 管理员后台按月统计订单数量和交易金额
     * → OrderServiceImpl.getMonthlyStats() → OrderMapper.xml: getMonthlyStats + Redis缓存(stats:monthly, 5分钟)
     */
    @GetMapping("/stats/monthly")
    public Result<?> monthlyStats() {
        return Result.success(orderService.getMonthlyStats());
    }

    /**
     * 卖家收入排行 — 管理员后台统计各卖家的累计收入排行
     * → OrderServiceImpl.getSellerRevenue() → OrderMapper.xml: getSellerRevenue + Redis缓存(stats:seller_revenue, 5分钟)
     */
    @GetMapping("/stats/seller-revenue")
    public Result<?> sellerRevenue() {
        return Result.success(orderService.getSellerRevenue());
    }
}
