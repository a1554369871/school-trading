package com.trading.mapper;

import com.trading.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {

    Order findById(@Param("id") Integer id);

    Order findByOrderNo(@Param("orderNo") String orderNo);

    int insert(Order order);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 订单详情（含买家、卖家、商品、地址信息）
     */
    Map<String, Object> findDetailById(@Param("id") Integer id);

    /**
     * JOIN 查询：获取订单列表（含买家、卖家、商品信息）
     */
    List<Map<String, Object>> findByBuyerId(@Param("buyerId") Integer buyerId,
                                              @Param("offset") int offset,
                                              @Param("pageSize") int pageSize);

    int countByBuyerId(@Param("buyerId") Integer buyerId);

    List<Map<String, Object>> findBySellerId(@Param("sellerId") Integer sellerId,
                                              @Param("offset") int offset,
                                              @Param("pageSize") int pageSize);

    int countBySellerId(@Param("sellerId") Integer sellerId);

    /**
     * GROUP BY + 聚合：按月统计订单量和交易额
     */
    List<Map<String, Object>> getMonthlyStats();

    /**
     * GROUP BY + SUM：统计每个卖家的总销售额
     */
    List<Map<String, Object>> getSellerRevenue();

    /**
     * 管理员查询所有订单（含买家、卖家、商品信息）
     */
    List<Map<String, Object>> findAll(@Param("offset") int offset,
                                       @Param("pageSize") int pageSize);

    int countAll();
}
