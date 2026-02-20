package com.cy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cy.common.R;
import com.cy.entity.Log;
import com.cy.mapper.LogMapper;
import com.cy.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

    @Async("logExecutor")
    @Override
    public void saveLog(Log log) {
        log.setCreateTime(LocalDateTime.now());
        this.save(log);
    }

    @Override
    public R queryLogPage(Map<String, Object> params) {
        int pageNum = params.getOrDefault("pageNum", 1) instanceof Integer ? 
                (Integer) params.get("pageNum") : Integer.parseInt(params.get("pageNum").toString());
        int pageSize = params.getOrDefault("pageSize", 10) instanceof Integer ? 
                (Integer) params.get("pageSize") : Integer.parseInt(params.get("pageSize").toString());
        
        LambdaQueryWrapper<Log> queryWrapper = new LambdaQueryWrapper<>();
        
        if (params.containsKey("userId") && params.get("userId") != null && !params.get("userId").toString().trim().isEmpty()) {
            queryWrapper.eq(Log::getUserId, params.get("userId"));
        }
        
        if (params.containsKey("operationType") && params.get("operationType") != null && !params.get("operationType").toString().trim().isEmpty()) {
            queryWrapper.eq(Log::getOperationType, params.get("operationType"));
        }
        
        if (params.containsKey("module") && params.get("module") != null && !params.get("module").toString().trim().isEmpty()) {
            queryWrapper.eq(Log::getModule, params.get("module"));
        }
        
        if (params.containsKey("startTime") && params.get("startTime") != null) {
            queryWrapper.ge(Log::getCreateTime, params.get("startTime"));
        }
        
        if (params.containsKey("endTime") && params.get("endTime") != null) {
            queryWrapper.le(Log::getCreateTime, params.get("endTime"));
        }
        
        queryWrapper.orderByDesc(Log::getCreateTime);
        
        Page<Log> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        return R.success(page);
    }

    @Override
    public void cleanLog(int days) {
        LocalDateTime beforeDays = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<Log> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(Log::getCreateTime, beforeDays);
        this.remove(queryWrapper);
    }
}
