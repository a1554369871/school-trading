package com.trading.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PrivateMessage {
    private Integer id;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private LocalDateTime sendTime;
    private Integer isRead;
}
