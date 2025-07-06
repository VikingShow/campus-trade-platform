package com.campus.trade.dto;

/**
 * 配送统计数据传输对象
 */
public class DeliveryStatsDTO {
    private Long awaitingShipment; // 待发货订单数
    private Long shipped; // 已发货订单数
    private Long completed; // 已完成订单数
    private Long total; // 总订单数

    // 构造函数
    public DeliveryStatsDTO() {}

    public DeliveryStatsDTO(Long awaitingShipment, Long shipped, Long completed, Long total) {
        this.awaitingShipment = awaitingShipment;
        this.shipped = shipped;
        this.completed = completed;
        this.total = total;
    }

    // Getters and Setters
    public Long getAwaitingShipment() {
        return awaitingShipment;
    }

    public void setAwaitingShipment(Long awaitingShipment) {
        this.awaitingShipment = awaitingShipment;
    }

    public Long getShipped() {
        return shipped;
    }

    public void setShipped(Long shipped) {
        this.shipped = shipped;
    }

    public Long getCompleted() {
        return completed;
    }

    public void setCompleted(Long completed) {
        this.completed = completed;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
} 