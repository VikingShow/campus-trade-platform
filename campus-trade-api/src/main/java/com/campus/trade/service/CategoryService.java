package com.campus.trade.service;

import com.campus.trade.entity.Category;
import java.util.List;

public interface CategoryService {

    /**
     * 获取所有分类的方法
     */
    List<Category> getAllCategories();

    /**
     * 【新增】声明一个用于清除分类缓存的方法
     */
    void clearCategoryCache();
}
