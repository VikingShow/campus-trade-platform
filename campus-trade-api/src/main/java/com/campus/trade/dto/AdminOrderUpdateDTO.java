package com.campus.trade.dto;

import java.math.BigDecimal;

public class AdminOrderUpdateDTO {
    private String orderStatus;
    private BigDecimal totalPrice;
    private String deliveryMethod; // 【新增】
    private Integer meetupLocationId;
    private String meetupTimeSlot;
    private String shippingProvider;
    private String trackingNumber;

    // Getters and Setters
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getDeliveryMethod() { return deliveryMethod; } // 【新增】
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; } // 【新增】
    public Integer getMeetupLocationId() { return meetupLocationId; }
    public void setMeetupLocationId(Integer meetupLocationId) { this.meetupLocationId = meetupLocationId; }
    public String getMeetupTimeSlot() { return meetupTimeSlot; }
    public void setMeetupTimeSlot(String meetupTimeSlot) { this.meetupTimeSlot = meetupTimeSlot; }
    public String getShippingProvider() { return shippingProvider; }
    public void setShippingProvider(String shippingProvider) { this.shippingProvider = shippingProvider; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
}