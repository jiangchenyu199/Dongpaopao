package com.cy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
@TableName("address")
public class Address {

    @TableId(type = IdType.ASSIGN_ID)
    private String aid;

    private String uid;

    private String sjr;

    private String phone;

    private String detail;

    private Boolean isDefault;
}
