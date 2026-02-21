package com.cy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("app_splash")
public class AppSplash {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String logoUrl;

    private String slogan;

    private String backgroundColor;

    private String primaryColor;

    private Integer sloganFontSize;

    private String sloganColor;

    private Integer showCopyright;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
