package com.trading.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;
    private Integer productId;
    private Integer parentId;       // NULL=一级评论
    private Integer commenterId;
    private Integer replierId;
    private String content;
    private LocalDateTime commentTime;
}
