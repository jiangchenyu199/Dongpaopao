package com.cy.entity.admin;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin_statistics")
public class AdminStatistics {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String data;

    private LocalDateTime createTime;
}
