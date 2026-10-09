package com.trading.entity;

import lombok.Data;

@Data
public class Category {
    private Integer id;
    private String categoryName;
    private Long productCount;
}
