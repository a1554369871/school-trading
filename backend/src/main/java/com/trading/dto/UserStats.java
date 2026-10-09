package com.trading.dto;

import lombok.Data;

@Data
public class UserStats {
    private Integer userId;
    private String nickname;
    private Long publishCount;
    private Long soldCount;
    private Long buyCount;
    private Double totalEarning;
    private Double totalSpending;
}
