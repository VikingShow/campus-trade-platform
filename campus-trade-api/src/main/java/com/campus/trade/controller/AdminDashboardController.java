package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.AdminDashboardStatsDTO;
import com.campus.trade.dto.DailyStatsDTO;
import com.campus.trade.service.AdminDashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public Result<AdminDashboardStatsDTO> getSummary() {
        return Result.success(dashboardService.getSummaryStats());
    }

    @GetMapping("/daily-registrations")
    public Result<List<DailyStatsDTO>> getDailyRegistrations() {
        return Result.success(dashboardService.getDailyRegistrationStats());
    }

    @GetMapping("/user-growth")
    public Result<List<DailyStatsDTO>> getUserGrowth(@RequestParam(defaultValue = "15") int days) {
        return Result.success(dashboardService.getUserGrowthStats(days));
    }

    @GetMapping("/order-trends")
    public Result<List<DailyStatsDTO>> getOrderTrends(@RequestParam(defaultValue = "15") int days) {
        return Result.success(dashboardService.getOrderTrendStats(days));
    }

    @GetMapping("/product-trends")
    public Result<List<DailyStatsDTO>> getProductTrends(@RequestParam(defaultValue = "15") int days) {
        return Result.success(dashboardService.getProductTrendStats(days));
    }
}
