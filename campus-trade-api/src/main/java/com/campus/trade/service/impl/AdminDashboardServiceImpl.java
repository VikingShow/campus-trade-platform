package com.campus.trade.service.impl;

import com.campus.trade.dto.AdminDashboardStatsDTO;
import com.campus.trade.dto.DailyStatsDTO;
import com.campus.trade.mapper.OrderMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.AdminDashboardService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    public AdminDashboardServiceImpl(UserMapper userMapper, ProductMapper productMapper, OrderMapper orderMapper) {
        this.userMapper = userMapper;
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public AdminDashboardStatsDTO getSummaryStats() {
        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO();
        stats.setUserCount(userMapper.countTotalUsers());
        stats.setProductCount(productMapper.countTotalProducts());
        stats.setOrderCount(orderMapper.countTotalOrders());
        return stats;
    }

    @Override
    public List<DailyStatsDTO> getDailyRegistrationStats() {
        return userMapper.countDailyRegistrations();
    }
}