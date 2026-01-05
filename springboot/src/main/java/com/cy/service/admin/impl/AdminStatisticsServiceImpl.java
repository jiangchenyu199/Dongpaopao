package com.cy.service.admin.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cy.entity.admin.AdminStatistics;
import com.cy.entity.app.Order;
import com.cy.entity.app.OrderType;
import com.cy.entity.app.Transaction;
import com.cy.mapper.admin.AdminStatisticsMapper;
import com.cy.mapper.app.OrderMapper;
import com.cy.mapper.app.OrderTypeMapper;
import com.cy.mapper.app.TransactionMapper;
import com.cy.service.admin.AdminStatisticsService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    private final AdminStatisticsMapper adminStatisticsMapper;
    private final TransactionMapper transactionMapper;
    private final OrderMapper orderMapper;
    private final OrderTypeMapper orderTypeMapper;

    @Override
    public List<AdminStatistics> getAllStatistics() {
        QueryWrapper<AdminStatistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return adminStatisticsMapper.selectList(queryWrapper);
    }

    @Override
    public AdminStatistics getLatestStatistics() {
        QueryWrapper<AdminStatistics> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("LIMIT 1");
        return adminStatisticsMapper.selectOne(queryWrapper);
    }

    @Override
    public JSONObject getDashboardCharts() {
        JSONObject result = new JSONObject();

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);

        List<String> dates = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = 0; i < 7; i++) {
            LocalDate date = sevenDaysAgo.plusDays(i);
            dates.add(date.format(formatter));

            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

            QueryWrapper<Transaction> queryWrapper = new QueryWrapper<>();
            queryWrapper.between("create_time", startOfDay, endOfDay);
            List<Transaction> transactions = transactionMapper.selectList(queryWrapper);

            BigDecimal totalAmount = transactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            amounts.add(totalAmount);
        }

        JSONObject transactionTrend = new JSONObject();
        transactionTrend.put("dates", dates);
        transactionTrend.put("amounts", amounts);
        result.put("transactionTrend", transactionTrend);

        QueryWrapper<OrderType> orderTypeQueryWrapper = new QueryWrapper<>();
        orderTypeQueryWrapper.eq("enabled", true);
        List<OrderType> orderTypesList = orderTypeMapper.selectList(orderTypeQueryWrapper);

        Map<String, String> orderTypeIdToNameMap = orderTypesList.stream()
                .collect(Collectors.toMap(OrderType::getOrderTypeId, OrderType::getTypeName));

        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.select("order_type_id", "count(*) as count");
        orderQueryWrapper.groupBy("order_type_id");
        List<Map<String, Object>> orderTypeList = orderMapper.selectMaps(orderQueryWrapper);

        Map<String, Long> orderTypeMap = orderTypeList.stream()
                .filter(map -> map.get("order_type_id") != null)
                .collect(Collectors.toMap(
                        map -> map.get("order_type_id").toString(),
                        map -> ((Number) map.get("count")).longValue()
                ));

        List<JSONObject> orderTypes = new ArrayList<>();
        for (OrderType orderType : orderTypesList) {
            String orderTypeId = orderType.getOrderTypeId();
            String typeName = orderType.getTypeName();
            String bgColor = orderType.getBgColor();
            Long count = orderTypeMap.getOrDefault(orderTypeId, 0L);
            orderTypes.add(new JSONObject().fluentPut("name", typeName).fluentPut("value", count).fluentPut("bgColor", bgColor));
        }

        result.put("orderTypes", orderTypes);

        return result;
    }
}
