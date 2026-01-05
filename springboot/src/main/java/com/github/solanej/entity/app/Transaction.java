package com.github.solanej.entity.app;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Transaction {

    @TableId(type = IdType.ASSIGN_ID)
    private String tid;

    private String oid;

    private String uid;

    private BigDecimal amount;

    private String type;

    private LocalDateTime createTime;
}
