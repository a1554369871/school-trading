package com.trading.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Interaction {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String type;    // like, favorite, want
    private LocalDateTime createTime;
}
