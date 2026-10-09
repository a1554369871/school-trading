package com.trading.entity;

import lombok.Data;

@Data
public class Address {
    private Integer id;
    private Integer userId;
    private String consignee;
    private String phone;
    private String address;
    private Integer isDefault;  // 0=否, 1=是
}
