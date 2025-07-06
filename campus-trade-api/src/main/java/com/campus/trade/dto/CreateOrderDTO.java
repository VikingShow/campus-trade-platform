package com.campus.trade.dto;

public class CreateOrderDTO {
    private String productId;
    private String deliveryMethod; // 【新增】明确指定本次下单选择的配送方式
    private Integer meetupLocationId; // 如果是线下面交
    private Long shippingAddressId; // 如果是快递配送

    // Getters and Setters...
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public Integer getMeetupLocationId() { return meetupLocationId; }
    public void setMeetupLocationId(Integer meetupLocationId) { this.meetupLocationId = meetupLocationId; }
    public Long getShippingAddressId() { return shippingAddressId; }
    public void setShippingAddressId(Long shippingAddressId) { this.shippingAddressId = shippingAddressId; }
}