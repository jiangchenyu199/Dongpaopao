package com.cy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    /** 0成功 1失败 */
    private Integer status;
    private String ip;
    private String location;
    private String browser;
    private String msg;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime loginTime;
}
