package com.trading.dto;

import lombok.Data;

@Data
public class CategoryStats {
    private Integer categoryId;
    private String categoryName;
    private Long productCount;
    private Long soldCount;
    private Double avgPrice;
    private Double totalPrice;
}
