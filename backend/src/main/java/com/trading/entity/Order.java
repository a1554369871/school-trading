package com.trading.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Integer id;
    private Integer buyerId;
    private Integer sellerId;
    private String orderNo;
    private Integer status;    // 0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消
    private String remark;
    private Integer productId;
    private BigDecimal dealPrice;
    private String deliveryMethod;  // 面交/邮寄/自提
    private Integer addressId;
    private LocalDateTime createTime;
    private LocalDateTime tradeTime;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime confirmTime;
    private LocalDateTime cancelTime;
}
