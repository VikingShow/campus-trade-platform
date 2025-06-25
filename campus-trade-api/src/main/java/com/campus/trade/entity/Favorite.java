package com.campus.trade.entity;

import java.io.Serializable;
import java.util.Date;

public class Favorite implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String userId;
    private String productId;
    private Date createTime;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}