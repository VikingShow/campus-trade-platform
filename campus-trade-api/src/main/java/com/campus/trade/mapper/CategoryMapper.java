package com.campus.trade.mapper;

import com.campus.trade.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findAllCategories();
}