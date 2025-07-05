package com.campus.trade.dto;

import java.math.BigDecimal;

// 这个 DTO 用于管理员更新订单时，接收前端传来的数据
public class AdminOrderUpdateDTO {
    private String orderStatus;
    private BigDecimal totalPrice;
    private Integer meetupLocationId;
    private String meetupTimeSlot;

    // Getters and Setters
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public Integer getMeetupLocationId() { return meetupLocationId; }
    public void setMeetupLocationId(Integer meetupLocationId) { this.meetupLocationId = meetupLocationId; }
    public String getMeetupTimeSlot() { return meetupTimeSlot; }
    public void setMeetupTimeSlot(String meetupTimeSlot) { this.meetupTimeSlot = meetupTimeSlot; }
}
