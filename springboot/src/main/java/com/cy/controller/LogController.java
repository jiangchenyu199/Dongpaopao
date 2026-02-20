package com.cy.controller;

import com.cy.common.R;
import com.cy.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping("/page")
    public R queryLogPage(@RequestParam Map<String, Object> params) {
        return logService.queryLogPage(params);
    }

    @DeleteMapping("/{logId}")
    public R deleteLog(@PathVariable String logId) {
        boolean result = logService.removeById(logId);
        return result ? R.success("删除成功") : R.error("删除失败");
    }

    @DeleteMapping("/batch")
    public R deleteLogBatch(@RequestBody String[] logIds) {
        boolean result = logService.removeByIds(Arrays.asList(logIds));
        return result ? R.success("批量删除成功") : R.error("批量删除失败");
    }

    @DeleteMapping("/clean")
    public R cleanLog(@RequestParam(defaultValue = "30") int days) {
        logService.cleanLog(days);
        return R.success("日志清理成功");
    }
}
