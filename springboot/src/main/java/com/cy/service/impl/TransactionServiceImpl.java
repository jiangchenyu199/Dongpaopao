package com.cy.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cy.common.R;
import com.cy.entity.Transaction;
import com.cy.mapper.TransactionMapper;
import com.cy.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;

    @Override
    public R create(JSONObject param) {
        Transaction transaction = param.to(Transaction.class);
        validateTransaction(transaction);
        transactionMapper.insert(transaction);
        return R.success();
    }

    @Override
    public R list(String uid) {
        return R.success(transactionMapper.selectList(new LambdaQueryWrapper<Transaction>().eq(Transaction::getUid, uid)));
    }

    private void validateTransaction(Transaction transaction) {

        if (transaction.getOid() == null) {
            throw new IllegalArgumentException("订单ID不能为空");
        }
        if (transaction.getUid() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (transaction.getType() == null) {
            throw new IllegalArgumentException("交易类型不能为空");
        }
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("金额不能为空且必须大于0");
        }
    }
}
