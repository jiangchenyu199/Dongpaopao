package com.cy.service.admin;

import com.alibaba.fastjson2.JSONObject;
import com.cy.entity.admin.AdminStatistics;

import java.util.List;

public interface AdminStatisticsService {

    List<AdminStatistics> getAllStatistics();

    AdminStatistics getLatestStatistics();

    JSONObject getDashboardCharts();
}
