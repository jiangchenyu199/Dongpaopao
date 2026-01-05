package com.github.solanej.controller.admin;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.common.R;
import com.github.solanej.entity.admin.AdminStatistics;
import com.github.solanej.service.admin.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.github.solanej.common.R.success;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final AdminStatisticsService adminStatisticsService;

    @GetMapping("/list")
    public R list() {
        return success(adminStatisticsService.getAllStatistics());
    }

    @GetMapping("/latest")
    public R latest() {
        AdminStatistics statistics = adminStatisticsService.getLatestStatistics();
        if (statistics != null && statistics.getData() != null) {
            JSONObject data = JSONObject.parseObject(statistics.getData());
            return success(data);
        }
        return success(null);
    }

    @GetMapping("/dashboard")
    public R dashboard() {
        return success(adminStatisticsService.getDashboardCharts());
    }
}
