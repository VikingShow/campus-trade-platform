package com.campus.trade.mapper;

import com.campus.trade.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Set;

@Mapper
public interface FavoriteMapper {
    void addFavorite(@Param("userId") String userId, @Param("productId") String productId);
    void removeFavorite(@Param("userId") String userId, @Param("productId") String productId);
    Integer findFavorite(@Param("userId") String userId, @Param("productId") String productId);
    Set<String> findUserFavoriteProductIds(String userId);
    List<Product> findUserFavoriteProducts(String userId);
}