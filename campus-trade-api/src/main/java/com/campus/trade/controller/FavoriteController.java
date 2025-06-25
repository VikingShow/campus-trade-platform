package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.entity.Product;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@PreAuthorize("isAuthenticated()")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // 获取当前用户收藏的所有商品ID
    @GetMapping("/me/favorites/ids")
    public Result<Set<String>> getMyFavoriteIds(@AuthenticationPrincipal AuthenticatedUser user) {
        return Result.success(favoriteService.getUserFavoriteProductIds(user.getUserId()));
    }

    // 获取当前用户收藏的所有商品详情
    @GetMapping("/me/favorites")
    public Result<List<Product>> getMyFavorites(@AuthenticationPrincipal AuthenticatedUser user) {
        return Result.success(favoriteService.getUserFavoriteProducts(user.getUserId()));
    }

    // 添加收藏
    @PostMapping("/products/{productId}/favorite")
    public Result<Void> addFavorite(@PathVariable String productId, @AuthenticationPrincipal AuthenticatedUser user) {
        favoriteService.addFavorite(user.getUserId(), productId);
        return Result.success();
    }

    // 取消收藏
    @DeleteMapping("/products/{productId}/favorite")
    public Result<Void> removeFavorite(@PathVariable String productId, @AuthenticationPrincipal AuthenticatedUser user) {
        favoriteService.removeFavorite(user.getUserId(), productId);
        return Result.success();
    }
}
