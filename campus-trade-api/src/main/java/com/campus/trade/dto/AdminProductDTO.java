package com.campus.trade.dto;

import java.math.BigDecimal;
import java.util.List;

// 这个 DTO 用于管理员创建或更新商品时，接收前端传来的数据
public class AdminProductDTO {
    private String sellerId; // 【新增】允许管理员指定卖家ID
    private String title;
    private String description;
    private BigDecimal price;
    private Integer categoryId;
    private Integer conditionLevel;
    private String coverImage;
    private List<String> imageUrls;

    // Getters and Setters
    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getConditionLevel() { return conditionLevel; }
    public void setConditionLevel(Integer conditionLevel) { this.conditionLevel = conditionLevel; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}
