package com.trading.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Announcement {
    private Integer id;
    private Integer publisherId;
    private String title;
    private String content;
    private Integer status;     // 0=草稿, 1=已发布
    private LocalDateTime publishTime;
}
