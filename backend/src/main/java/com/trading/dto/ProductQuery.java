package com.trading.dto;

import lombok.Data;

@Data
public class ProductQuery {
    private String keyword;         // 搜索关键词
    private Integer categoryId;     // 分类筛选
    private String condition;       // 新旧程度
    private Double minPrice;        // 最低价
    private Double maxPrice;        // 最高价
    private String sortBy;          // 排序字段: price, publishTime
    private String sortOrder;       // asc, desc
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
