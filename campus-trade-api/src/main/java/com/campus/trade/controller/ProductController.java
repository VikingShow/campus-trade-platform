package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.entity.Product;
import com.campus.trade.exception.CustomException;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired private ProductService productService;

    private String getUserId(AuthenticatedUser user) {
        if (user == null) throw new CustomException("用户未登录");
        return user.getUserId();
    }

    @GetMapping
    public Result<List<Product>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId) {
        return Result.success(productService.getProducts(keyword, categoryId));
    }

    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable String id) {
        return Result.success(productService.getProductById(id));
    }

    @PostMapping
    public Result<Product> createProduct(@RequestBody Product product, @AuthenticationPrincipal AuthenticatedUser user) {
        Product createdProduct = productService.createProduct(product, getUserId(user));
        return Result.success(createdProduct);
    }

    @PutMapping("/{id}")
    public Result<Product> updateProduct(@PathVariable String id, @RequestBody Product product, @AuthenticationPrincipal AuthenticatedUser user) {
        product.setId(id);
        Product updatedProduct = productService.updateProduct(product, getUserId(user));
        return Result.success(updatedProduct);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable String id, @RequestBody Map<String, String> payload, @AuthenticationPrincipal AuthenticatedUser user) {
        String status = payload.get("status");
        productService.updateProductStatus(id, status, getUserId(user));
        return Result.success();
    }

    /**
     * 【新增】获取商品推荐的公开接口
     */
    @GetMapping("/{id}/recommendations")
    public Result<List<Product>> getRecommendations(@PathVariable String id) {
        return Result.success(productService.getRecommendedProducts(id));
    }
}