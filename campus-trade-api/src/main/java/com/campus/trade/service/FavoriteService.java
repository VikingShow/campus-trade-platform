package com.campus.trade.service;

import com.campus.trade.entity.Product;
import java.util.List;
import java.util.Set;

public interface FavoriteService {
    void addFavorite(String userId, String productId);
    void removeFavorite(String userId, String productId);
    Set<String> getUserFavoriteProductIds(String userId);
    List<Product> getUserFavoriteProducts(String userId);
}