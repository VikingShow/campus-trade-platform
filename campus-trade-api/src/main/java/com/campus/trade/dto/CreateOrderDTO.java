package com.campus.trade.dto;

import lombok.Data;

@Data
public class CreateOrderDTO {
    private String productId;
    private Integer meetupLocationId;
    private String meetupTimeSlot;
}