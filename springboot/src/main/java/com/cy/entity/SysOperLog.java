package com.cy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String businessType;
    private String method;
    private String requestMethod;
    private Long operatorId;
    private String operatorName;
    private String requestUrl;
    private String requestParam;
    private String responseResult;

    /** 0成功 1失败 */
    private Integer status;
    private String errorMsg;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
