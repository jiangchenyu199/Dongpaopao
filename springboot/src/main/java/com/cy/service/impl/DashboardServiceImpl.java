package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cy.common.R;
import com.cy.entity.Order;
import com.cy.mapper.OrderMapper;
import com.cy.mapper.UserMapper;
import com.cy.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    @Override
    public R stats() {
        Map<String, Object> data = new HashMap<>();
        // 今日订单
        QueryWrapper<Order> todayQw = new QueryWrapper<>();
        todayQw.apply("DATE(create_time) = CURDATE()");
        data.put("todayOrders", orderMapper.selectCount(todayQw));
        // 用户总数
        data.put("totalUsers", userMapper.selectCount(null));
        // 已完成订单
        QueryWrapper<Order> completedQw = new QueryWrapper<>();
        completedQw.eq("status", 'S');
        data.put("completedOrders", orderMapper.selectCount(completedQw));
        return R.success(data);
    }
}
