package com.github.solanej.service.admin;

import com.alibaba.fastjson2.JSONObject;
import com.github.solanej.entity.admin.AdminStatistics;

import java.util.List;

public interface AdminStatisticsService {

    List<AdminStatistics> getAllStatistics();

    AdminStatistics getLatestStatistics();

    JSONObject getDashboardCharts();
}
