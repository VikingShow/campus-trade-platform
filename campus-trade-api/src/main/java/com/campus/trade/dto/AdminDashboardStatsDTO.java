package com.campus.trade.dto;

public class AdminDashboardStatsDTO {
    private long userCount;
    private long productCount;
    private long orderCount;

    // Getters and Setters
    public long getUserCount() { return userCount; }
    public void setUserCount(long userCount) { this.userCount = userCount; }
    public long getProductCount() { return productCount; }
    public void setProductCount(long productCount) { this.productCount = productCount; }
    public long getOrderCount() { return orderCount; }
    public void setOrderCount(long orderCount) { this.orderCount = orderCount; }
}