package com.trading.service;

import com.trading.common.PageResult;
import com.trading.dto.OrderCreateRequest;
import com.trading.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrder(Integer buyerId, OrderCreateRequest request);

    /**
     * 获取订单详情（含买家、卖家、商品完整信息）
     */
    Map<String, Object> getOrderDetail(Integer orderId);

    void payOrder(Integer userId, Integer orderId);
    void shipOrder(Integer userId, Integer orderId);
    void confirmOrder(Integer userId, Integer orderId);
    void cancelOrder(Integer userId, Integer orderId);

    PageResult<Map<String, Object>> getMyBuyOrders(Integer userId, int pageNum, int pageSize);
    PageResult<Map<String, Object>> getMySellOrders(Integer userId, int pageNum, int pageSize);
    PageResult<Map<String, Object>> getAllOrders(int pageNum, int pageSize);
    List<Map<String, Object>> getMonthlyStats();
    List<Map<String, Object>> getSellerRevenue();
}
