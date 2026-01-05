package com.cy.service.admin.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cy.common.R;
import com.cy.entity.app.Order;
import com.cy.mapper.app.OrderMapper;
import com.cy.service.admin.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.cy.common.R.success;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderMapper orderMapper;

    @Override
    public R list(Integer pageNum, Integer pageSize, String keyword, String status) {

        Page<Order> page = orderMapper.selectPage(new Page<>(pageNum, pageSize), null);
        page.setRecords(page.getRecords());
        page.setTotal(page.getTotal());
        return success(page);
    }

    @Override
    public R detail(String oid) {
        return success(orderMapper.getAdminOrderDetail(oid));
    }
}
