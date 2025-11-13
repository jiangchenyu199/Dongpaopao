package com.github.solanej.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mysql.cj.xdevapi.JsonArray;
import lombok.Data;

@Data
public class Service {

    @TableId(type = IdType.ASSIGN_ID)
    private String serviceId;

    private String serviceName;

    private String icon;

    private String bgColor;

    private String form;

    private Boolean enable;
}
