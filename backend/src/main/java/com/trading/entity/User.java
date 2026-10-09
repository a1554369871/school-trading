package com.trading.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String nickname;
    private String username;
    private String password;
    private String avatar;
    private Integer role;       // 0=普通用户, 1=管理员
    private Integer status;     // 0=冻结, 1=正常
    private String phone;
    private String email;
    private LocalDateTime registerTime;
}
