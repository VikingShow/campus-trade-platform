package com.campus.trade.dto;

import java.math.BigDecimal;

// 这个 DTO 用于管理员创建订单时，接收前端传来的数据
public class AdminOrderCreateDTO {
    private String productId;
    private String buyerId;
    private String deliveryMethod; // 【新增】
    private Integer meetupLocationId;
    private String meetupTimeSlot;

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }
    public String getDeliveryMethod() { return deliveryMethod; } // 【新增】
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; } // 【新增】
    public Integer getMeetupLocationId() { return meetupLocationId; }
    public void setMeetupLocationId(Integer meetupLocationId) { this.meetupLocationId = meetupLocationId; }
    public String getMeetupTimeSlot() { return meetupTimeSlot; }
    public void setMeetupTimeSlot(String meetupTimeSlot) { this.meetupTimeSlot = meetupTimeSlot; }
}
