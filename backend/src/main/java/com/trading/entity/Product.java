package com.trading.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Integer id;
    private Integer userId;
    private String productName;
    private String productImage;
    private LocalDateTime publishTime;
    private BigDecimal price;
    private String description;
    private Integer categoryId;
    private String categoryName; // 非数据库字段，JOIN查询时填充
    private String condition;   // 新旧程度
    private Integer status;     // 0=已下架, 1=在售, 2=已售出, 3=预留中

    // 乐观锁版本号（并发控制）
    private Integer version;

    // 浏览量
    private Integer viewCount;
}
