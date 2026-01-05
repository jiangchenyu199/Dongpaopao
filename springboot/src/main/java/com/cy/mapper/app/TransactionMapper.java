package com.cy.mapper.app;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cy.entity.app.Transaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {
}
