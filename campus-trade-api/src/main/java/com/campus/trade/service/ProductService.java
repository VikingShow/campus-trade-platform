package com.campus.trade.service;

import com.campus.trade.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> getProducts(String keyword, String categoryId);
    Product getProductById(String productId);
    Product createProduct(Product product, String sellerId);
    Product updateProduct(Product product, String currentUserId);
    void updateProductStatus(String productId, String status, String currentUserId);

    // 【新增】为管理员查询所有商品的方法声明
    List<Product> findAllProductsForAdmin(String keyword);
}
