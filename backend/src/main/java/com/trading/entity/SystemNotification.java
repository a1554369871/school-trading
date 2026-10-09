package com.trading.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemNotification {
    private Integer id;
    private Integer receiverId;
    private String title;
    private String content;
    private Integer isRead;     // 0=未读, 1=已读
    private LocalDateTime createTime;
}
