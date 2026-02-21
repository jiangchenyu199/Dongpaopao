package com.cy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("app_notice")
public class AppNotice {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String content;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
}
