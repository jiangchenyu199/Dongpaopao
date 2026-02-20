package com.cy.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.common.R;
import com.cy.mapper.OrderMapper;
import com.cy.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.cy.common.R.success;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderMapper orderMapper;

    @Override
    public R list(Integer pageNum, Integer pageSize, String keyword, String status) {
        Page<JSONObject> page = new Page<>(pageNum, pageSize);
        Page<JSONObject> result = orderMapper.getAdminOrderList(page, null, keyword, status);
        return success(result);
    }

    @Override
    public R detail(String oid) {
        return success(orderMapper.getAdminOrderDetail(oid));
    }
}
