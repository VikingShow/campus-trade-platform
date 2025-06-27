package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.PageResult;
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
}
