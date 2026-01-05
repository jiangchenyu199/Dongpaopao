package com.cy.entity.app;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("order_status")
public class OrderStatus {

    private String status;

    private String description;

    private String color;
}
