package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.AdminProductDTO;
import com.campus.trade.dto.PageResult;
import com.campus.trade.dto.ProductDTO;
import com.campus.trade.entity.Product;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/products")
@PreAuthorize("hasRole('ADMIN')") // 保护整个Controller，只有ADMIN角色能访问
public class AdminProductController {

    private final ProductService productService;

    @Autowired
    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    // 获取所有商品列表（带搜索）
    @GetMapping
    public Result<PageResult<Product>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(productService.findAllProductsForAdmin(keyword, page, size));
    }

    // 【修改】调用新的、专门为管理员设计的 service 方法
    @PutMapping("/{id}/status")
    public Result<Void> updateProductStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal AuthenticatedUser user) {
        String status = payload.get("status");
        productService.updateProductStatus(id, status, user);
        return Result.success();
    }
    // 【新增】管理员更新商品详情的接口
    @PutMapping("/{id}")
    public Result<Product> updateProduct(@PathVariable String id, @RequestBody ProductDTO productDTO) {
        return Result.success(productService.updateProductByAdmin(id, productDTO));
    }

    // 【新增】管理员删除商品的接口
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @PostMapping
    public Result<Product> createProduct(@RequestBody AdminProductDTO adminProductDTO) {
        return Result.success(productService.createProductByAdmin(adminProductDTO));
    }

    // 【新增】获取单个商品详情（含主图和所有附图）
    @GetMapping("/{id}")
    public Result<Product> getProductDetail(@PathVariable String id) {
        return Result.success(productService.getProductById(id));
    }
}
