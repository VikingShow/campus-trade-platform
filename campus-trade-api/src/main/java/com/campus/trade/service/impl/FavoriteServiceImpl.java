package com.campus.trade.service.impl;

import com.campus.trade.entity.Product;
import com.campus.trade.mapper.FavoriteMapper;
import com.campus.trade.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    @Autowired
    public FavoriteServiceImpl(FavoriteMapper favoriteMapper) {
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    public void addFavorite(String userId, String productId) {
        favoriteMapper.addFavorite(userId, productId);
    }

    @Override
    public void removeFavorite(String userId, String productId) {
        favoriteMapper.removeFavorite(userId, productId);
    }

    @Override
    public Set<String> getUserFavoriteProductIds(String userId) {
        return favoriteMapper.findUserFavoriteProductIds(userId);
    }

    @Override
    public List<Product> getUserFavoriteProducts(String userId) {
        return favoriteMapper.findUserFavoriteProducts(userId);
    }
}