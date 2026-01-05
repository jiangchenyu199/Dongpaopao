package com.github.solanej.service.admin.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.solanej.common.R;
import com.github.solanej.entity.app.Order;
import com.github.solanej.mapper.app.OrderMapper;
import com.github.solanej.service.admin.AdminOrderService;
import com.github.solanej.service.admin.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.solanej.common.R.success;

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
