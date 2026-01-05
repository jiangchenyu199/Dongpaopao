package com.cy.service.app.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cy.common.R;
import com.cy.entity.app.Log;
import com.cy.mapper.app.LogMapper;
import com.cy.service.app.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 日志服务实现类
 * 继承ServiceImpl，利用MyBatis-Plus提供的完整服务能力
 */
@Service
@RequiredArgsConstructor
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

    /**
     * 异步保存日志，避免影响主业务流程
     * 使用专门配置的日志线程池
     */
    @Async("logExecutor")
    @Override
    public void saveLog(Log log) {
        log.setCreateTime(LocalDateTime.now());
        // 使用IService提供的save方法
        this.save(log);
    }

    /**
     * 分页查询日志
     */
    @Override
    public R queryLogPage(Map<String, Object> params) {
        // 获取分页参数
        int pageNum = params.getOrDefault("pageNum", 1) instanceof Integer ? 
                (Integer) params.get("pageNum") : Integer.parseInt(params.get("pageNum").toString());
        int pageSize = params.getOrDefault("pageSize", 10) instanceof Integer ? 
                (Integer) params.get("pageSize") : Integer.parseInt(params.get("pageSize").toString());
        
        // 创建查询条件
        LambdaQueryWrapper<Log> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
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
        
        // 按创建时间倒序排序
        queryWrapper.orderByDesc(Log::getCreateTime);
        
        // 使用IService提供的page方法执行分页查询
        Page<Log> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);
        
        return R.success(page);
    }

    /**
     * 清理指定天数前的日志
     */
    @Override
    public void cleanLog(int days) {
        LocalDateTime beforeDays = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<Log> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(Log::getCreateTime, beforeDays);
        // 使用IService提供的remove方法
        this.remove(queryWrapper);
    }
}