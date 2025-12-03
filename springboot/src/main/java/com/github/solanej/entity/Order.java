package com.github.solanej.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("`order`")
public class Order {

    @TableId(type = IdType.ASSIGN_ID)
    private String oid;

    private String xdr;

    private LocalDateTime createTime;

    private LocalDateTime expectTime;

    private String serviceId;

    private String aid;

    private String detail;

    private BigDecimal amount;

    private String jdr;

    private LocalDateTime acceptTime;

    private LocalDateTime completeTime;

    private Character status;
}
