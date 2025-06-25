package com.campus.trade.dto;

public class MessageDTO {
    private String receiverId;
    private String content;
    private String productId;

    // Getters and Setters
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
}