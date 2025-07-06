package com.campus.trade.dto;

/**
 * 发货信息数据传输对象
 */
public class ShipmentDTO {
    private String shippingProvider; // 快递公司
    private String trackingNumber; // 快递单号

    // 构造函数
    public ShipmentDTO() {}

    public ShipmentDTO(String shippingProvider, String trackingNumber) {
        this.shippingProvider = shippingProvider;
        this.trackingNumber = trackingNumber;
    }

    // Getters and Setters
    public String getShippingProvider() {
        return shippingProvider;
    }

    public void setShippingProvider(String shippingProvider) {
        this.shippingProvider = shippingProvider;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
} 