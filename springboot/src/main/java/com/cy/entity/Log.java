package com.cy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统日志实体类
 * 用于记录重要的业务操作和系统事件
 */
@Data
@TableName("sys_log")
public class Log {
    
    /**
     * 日志ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String logId;
    
    /**
     * 操作用户ID
     */
    private String userId;
    
    /**
     * 操作用户名称
     */
    private String userName;
    
    /**
     * 操作类型：LOGIN(登录), LOGOUT(登出), CREATE(创建), UPDATE(更新), DELETE(删除), QUERY(查询), UPLOAD(上传), DOWNLOAD(下载)
     */
    private String operationType;
    
    /**
     * 操作模块
     */
    private String module;
    
    /**
     * 操作内容描述
     */
    private String description;
    
    /**
     * 请求URL
     */
    private String requestUrl;
    
    /**
     * 请求方法
     */
    private String requestMethod;
    
    /**
     * 请求参数
     */
    private String requestParams;
    
    /**
     * 操作结果：SUCCESS(成功), FAILURE(失败)
     */
    private String result;
    
    /**
     * 错误信息（如果操作失败）
     */
    private String errorInfo;
    
    /**
     * 操作IP地址
     */
    private String ip;
    
    /**
     * 操作耗时（毫秒）
     */
    private Long executionTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
