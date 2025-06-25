package com.campus.trade.entity;

import java.io.Serializable;
import java.util.Date;

public class Rating implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String orderId;
    private String raterId;
    private String rateeId;
    private Integer score;
    private String comment;
    private Date createTime;

    // 关联查询出的评价者信息
    private String raterNickname;
    private String raterAvatar;

    // 【新增】关联查询出的商品信息
    private String productId;
    private String productTitle;

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getRaterId() { return raterId; }
    public void setRaterId(String raterId) { this.raterId = raterId; }
    public String getRateeId() { return rateeId; }
    public void setRateeId(String rateeId) { this.rateeId = rateeId; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getRaterNickname() { return raterNickname; }
    public void setRaterNickname(String raterNickname) { this.raterNickname = raterNickname; }
    public String getRaterAvatar() { return raterAvatar; }
    public void setRaterAvatar(String raterAvatar) { this.raterAvatar = raterAvatar; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
}