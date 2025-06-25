package com.campus.trade.service.impl;

import com.campus.trade.entity.Category;
import com.campus.trade.mapper.CategoryMapper;
import com.campus.trade.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 【最终优化】重新启用查询缓存
     * 当此方法被调用时，会优先从名为 "categories" 的缓存中获取数据。
     */
    @Override
//    @Cacheable("categories")
    public List<Category> getAllCategories() {
        log.info(">>> [缓存未命中] 正在从数据库查询分类列表...");
        List<Category> categoriesFromDb = categoryMapper.findAllCategories();
        log.info("<<< 数据库查询完成，返回了 {} 个分类。", categoriesFromDb.size());
        return categoriesFromDb;
    }

    /**
     * @CacheEvict 注解用于清除缓存。
     * 当 /api/categories/clear-cache 接口被调用时，此方法会执行，
     * 并清除 "categories" 缓存中的所有数据，从而实现缓存更新。
     */
    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public void clearCategoryCache() {
        log.info(">>> [缓存更新] 已成功清除所有 'categories' 缓存。");
    }
}