package com.cy.controller.app;

import com.cy.common.R;
import com.cy.service.app.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Arrays;

/**
 * 日志管理控制器
 * 提供日志查询、删除和清理功能
 */
@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    /**
     * 分页查询日志
     * @param params 查询参数
     * @return 日志列表和分页信息
     */
    @GetMapping("/page")
    public R queryLogPage(@RequestParam Map<String, Object> params) {
        return logService.queryLogPage(params);
    }

    /**
     * 删除日志
     * @param logId 日志ID
     * @return 操作结果
     */
    @DeleteMapping("/{logId}")
    public R deleteLog(@PathVariable String logId) {
        // 直接使用IService提供的removeById方法
        boolean result = logService.removeById(logId);
        return result ? R.success("删除成功") : R.error("删除失败");
    }

    /**
     * 批量删除日志
     * @param logIds 日志ID数组
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public R deleteLogBatch(@RequestBody String[] logIds) {
        // 直接使用IService提供的removeByIds方法
        boolean result = logService.removeByIds(Arrays.asList(logIds));
        return result ? R.success("批量删除成功") : R.error("批量删除失败");
    }

    /**
     * 清理日志
     * @param days 保留最近多少天的日志
     * @return 操作结果
     */
    @DeleteMapping("/clean")
    public R cleanLog(@RequestParam(defaultValue = "30") int days) {
        logService.cleanLog(days);
        return R.success("日志清理成功");
    }
}