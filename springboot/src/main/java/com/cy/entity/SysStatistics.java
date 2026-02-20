package com.cy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 统计快照
 */
@Data
@TableName("sys_statistics")
public class SysStatistics {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String snapshotType;
    private String data;  // JSON 字符串

    private LocalDate snapshotDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
