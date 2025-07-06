package com.campus.trade.dto;

import java.math.BigDecimal;
import java.util.List;

// 这个 DTO 用于从前端接收创建或更新商品时的数据
public class ProductDTO {
    private String title;
    private String description;
    private BigDecimal price;
    private Integer categoryId;
    private Integer conditionLevel;
    private String coverImage; // 主图
    private List<String> imageUrls; // 其他附图列表
    private List<String> deliveryOptions;

    // Getters and Setters
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
    public List<String> getDeliveryOptions() { return deliveryOptions; }
    public void setDeliveryOptions(List<String> deliveryOptions) { this.deliveryOptions = deliveryOptions; }
}