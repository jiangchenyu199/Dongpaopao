package com.cy.entity.app;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Service {

    @TableId(type = IdType.ASSIGN_ID)
    private String serviceId;

    private String serviceName;

    private String icon;

    private String bgColor;

    private String form;

    private Boolean enabled;
}
