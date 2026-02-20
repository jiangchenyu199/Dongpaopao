package com.cy.entity;

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

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 期望完成时间
     */
    private LocalDateTime expectTime;

    private String orderTypeId;

    private String img;

    private String aid;

    private String detail;

    private BigDecimal amount;

    private String jdr;

    /**
     * 接单时间
     */
    private LocalDateTime acceptTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    private Character status;

    private BigDecimal rating;
}
