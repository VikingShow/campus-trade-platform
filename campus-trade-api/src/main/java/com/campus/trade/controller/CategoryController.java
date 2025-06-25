package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.entity.Category;
import com.campus.trade.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 获取所有分类列表的接口 (会使用缓存)
     */
    @GetMapping
    public Result<List<Category>> listCategories() {
        return Result.success(categoryService.getAllCategories());
    }

    /**
     * 【新增】一个专门用于清空/更新分类缓存的接口
     * 在实际项目中，这个接口应该被保护起来，只允许管理员调用。
     * 当您手动修改了数据库中的分类后，只需调用一次这个接口，缓存就会被更新。
     */
    @PostMapping("/clear-cache")
    public Result<String> clearCache() {
        categoryService.clearCategoryCache();
        return Result.success("分类缓存已成功清除");
    }
}