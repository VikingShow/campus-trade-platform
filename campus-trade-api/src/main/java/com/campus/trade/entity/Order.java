package com.campus.trade.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String productId;
    private String buyerId;
    private String sellerId;
    private String orderStatus;
    private BigDecimal totalPrice;
    private String deliveryMethod;
    private Integer meetupLocationId;
    private String meetupTimeSlot;
    private Date createTime;

    // Related fields
    private String productTitle;
    private String productImage;
    private String buyerNickname;
    private String sellerNickname;
    private String meetupLocationName;

    // --- Getters and Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }
    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public Integer getMeetupLocationId() { return meetupLocationId; }
    public void setMeetupLocationId(Integer meetupLocationId) { this.meetupLocationId = meetupLocationId; }
    public String getMeetupTimeSlot() { return meetupTimeSlot; }
    public void setMeetupTimeSlot(String meetupTimeSlot) { this.meetupTimeSlot = meetupTimeSlot; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public String getBuyerNickname() { return buyerNickname; }
    public void setBuyerNickname(String buyerNickname) { this.buyerNickname = buyerNickname; }
    public String getSellerNickname() { return sellerNickname; }
    public void setSellerNickname(String sellerNickname) { this.sellerNickname = sellerNickname; }
    public String getMeetupLocationName() { return meetupLocationName; }
    public void setMeetupLocationName(String meetupLocationName) { this.meetupLocationName = meetupLocationName; }
}