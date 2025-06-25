package com.campus.trade.controller;

import com.campus.trade.common.Result;
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
    public Result<List<Product>> getAllProducts(@RequestParam(required = false) String keyword) {
        return Result.success(productService.findAllProductsForAdmin(keyword));
    }

    // 更新商品状态（复用现有Service方法，但通过此接口暴露给管理员）
    @PutMapping("/{id}/status")
    public Result<Void> updateProductStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal AuthenticatedUser user) {
        String status = payload.get("status");
        // 调用service时，传入管理员的ID，service层可以根据角色进一步判断权限
        productService.updateProductStatus(id, status, user.getUserId());
        return Result.success();
    }
}
