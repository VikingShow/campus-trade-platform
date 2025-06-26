package com.campus.trade.mapper;

import com.campus.trade.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> findProducts(@Param("keyword") String keyword, @Param("categoryId") String categoryId);
    Product findProductById(String productId);
    void insertProduct(Product product);
    void updateProduct(Product product);
    void updateProductStatus(@Param("productId") String productId, @Param("status") String status);

    // 【新增】为管理员查询所有商品的方法，支持按标题关键词搜索
    List<Product> findAllForAdmin(@Param("keyword") String keyword);

    long countTotalProducts();

    // 【新增】基于协同过滤的商品推荐查询
    List<Product> findRecommendedProducts(@Param("productId") String productId, @Param("limit") int limit);

}
