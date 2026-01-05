package com.github.solanej.service.admin.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.solanej.entity.admin.AdminStatistics;
import com.github.solanej.entity.app.Order;
import com.github.solanej.entity.app.Service;
import com.github.solanej.entity.app.Transaction;
import com.github.solanej.mapper.admin.AdminStatisticsMapper;
import com.github.solanej.mapper.app.OrderMapper;
import com.github.solanej.mapper.app.ServiceMapper;
import com.github.solanej.mapper.app.TransactionMapper;
import com.github.solanej.service.admin.AdminStatisticsService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AdminStatisticsServiceImpl implements AdminStatisticsService {

    private final AdminStatisticsMapper adminStatisticsMapper;
    private final TransactionMapper transactionMapper;
    private final OrderMapper orderMapper;
    private final ServiceMapper serviceMapper;

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

        QueryWrapper<Service> serviceQueryWrapper = new QueryWrapper<>();
        serviceQueryWrapper.eq("enabled", true);
        List<Service> services = serviceMapper.selectList(serviceQueryWrapper);

        Map<String, String> serviceIdToNameMap = services.stream()
                .collect(Collectors.toMap(Service::getServiceId, Service::getServiceName));

        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.select("service_id", "count(*) as count");
        orderQueryWrapper.groupBy("service_id");
        List<Map<String, Object>> orderServiceList = orderMapper.selectMaps(orderQueryWrapper);

        Map<String, Long> orderTypeMap = orderServiceList.stream()
                .filter(map -> map.get("service_id") != null)
                .collect(Collectors.toMap(
                        map -> map.get("service_id").toString(),
                        map -> ((Number) map.get("count")).longValue()
                ));

        List<JSONObject> orderTypes = new ArrayList<>();
        for (Service service : services) {
            String serviceId = service.getServiceId();
            String serviceName = service.getServiceName();
            String bgColor = service.getBgColor();
            Long count = orderTypeMap.getOrDefault(serviceId, 0L);
            orderTypes.add(new JSONObject().fluentPut("name", serviceName).fluentPut("value", count).fluentPut("bgColor", bgColor));
        }

        result.put("orderTypes", orderTypes);

        return result;
    }
}
