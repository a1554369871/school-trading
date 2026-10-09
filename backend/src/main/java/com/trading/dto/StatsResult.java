package com.trading.dto;

import lombok.Data;

@Data
public class StatsResult {
    private Long totalUsers;
    private Long totalProducts;
    private Long totalOrders;
    private Long totalComments;
    private Long soldProducts;
    private Long activeProducts;
}
