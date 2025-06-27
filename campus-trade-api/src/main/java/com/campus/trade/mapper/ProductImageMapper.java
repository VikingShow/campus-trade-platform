package com.campus.trade.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductImageMapper {
    void batchInsert(@Param("productId") String productId, @Param("imageUrls") List<String> imageUrls);
    void deleteByProductId(String productId);
}
