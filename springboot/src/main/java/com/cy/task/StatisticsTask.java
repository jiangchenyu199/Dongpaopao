package com.cy.task;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cy.entity.admin.AdminStatistics;
import com.cy.entity.app.Order;
import com.cy.entity.app.Transaction;
import com.cy.entity.app.User;
import com.cy.mapper.admin.AdminStatisticsMapper;
import com.cy.mapper.app.OrderMapper;
import com.cy.mapper.app.TransactionMapper;
import com.cy.mapper.app.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsTask {

    private final AdminStatisticsMapper adminStatisticsMapper;
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final TransactionMapper transactionMapper;

    @Scheduled(cron = "0 0 2 1 * ?")
    public void monthlyStatistics() {
        log.info("开始执行每月统计任务");

        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);
        LocalDate lastMonthFirstDay = lastMonth.withDayOfMonth(1);
        LocalDate lastMonthLastDay = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());

        LocalDateTime startOfLastMonth = lastMonthFirstDay.atStartOfDay();
        LocalDateTime endOfLastMonth = lastMonthLastDay.plusDays(1).atStartOfDay();

        try {
            JSONObject statistics = new JSONObject();

            Long totalUsers = userMapper.selectCount(null);
            statistics.put("totalUsers", totalUsers);

            Long totalOrders = orderMapper.selectCount(null);
            statistics.put("totalOrders", totalOrders);

            List<Transaction> totalTransactions = transactionMapper.selectList(null);
            BigDecimal totalAmount = totalTransactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.put("totalAmount", totalAmount);

            QueryWrapper<User> lastMonthUserQueryWrapper = new QueryWrapper<>();
            lastMonthUserQueryWrapper.between("ctime", startOfLastMonth, endOfLastMonth);
            Long lastMonthUsers = userMapper.selectCount(lastMonthUserQueryWrapper);
            statistics.put("lastMonthUsers", lastMonthUsers);

            QueryWrapper<Order> lastMonthOrderQueryWrapper = new QueryWrapper<>();
            lastMonthOrderQueryWrapper.between("create_time", startOfLastMonth, endOfLastMonth);
            Long lastMonthOrders = orderMapper.selectCount(lastMonthOrderQueryWrapper);
            statistics.put("lastMonthOrders", lastMonthOrders);

            QueryWrapper<Transaction> lastMonthTransactionQueryWrapper = new QueryWrapper<>();
            lastMonthTransactionQueryWrapper.between("create_time", startOfLastMonth, endOfLastMonth);
            List<Transaction> lastMonthTransactions = transactionMapper.selectList(lastMonthTransactionQueryWrapper);
            BigDecimal lastMonthAmount = lastMonthTransactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            statistics.put("lastMonthAmount", lastMonthAmount);

            statistics.put("month", lastMonth.toString());
            statistics.put("type", "monthly");

            AdminStatistics adminStatistics = new AdminStatistics();
            adminStatistics.setData(statistics.toJSONString());
            adminStatistics.setCreateTime(LocalDateTime.now());

            adminStatisticsMapper.insert(adminStatistics);

            log.info("每月统计任务执行成功: {}", statistics);
        } catch (Exception e) {
            log.error("每月统计任务执行失败: {}", e.getMessage(), e);
        }
    }
}
