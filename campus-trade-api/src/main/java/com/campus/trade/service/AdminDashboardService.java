package com.campus.trade.service;

import com.campus.trade.dto.AdminDashboardStatsDTO;
import com.campus.trade.dto.DailyStatsDTO;
import java.util.List;

public interface AdminDashboardService {
    AdminDashboardStatsDTO getSummaryStats();
    List<DailyStatsDTO> getDailyRegistrationStats();
}