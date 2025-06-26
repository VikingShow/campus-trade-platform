package com.campus.trade.service;

import com.campus.trade.dto.PageResult; // 【新增】
import com.campus.trade.entity.Product;
import java.util.List;

public interface ProductService {
    // 【修改】getProducts 方法签名
    List<Product> getProducts(String keyword, Integer categoryId, Double minPrice, Double maxPrice, String orderBy);
    Product getProductById(String productId);
    Product createProduct(Product product, String sellerId);
    Product updateProduct(Product product, String currentUserId);
    void updateProductStatus(String productId, String status, String currentUserId);

    // 【修改】为管理员查询所有商品的方法，增加分页参数
    PageResult<Product> findAllProductsForAdmin(String keyword, Integer page, Integer size);

    // 【新增】获取推荐商品的方法声明
    List<Product> getRecommendedProducts(String productId);
}