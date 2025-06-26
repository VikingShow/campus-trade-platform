package com.campus.trade.service.impl;

import com.campus.trade.dto.PageResult;
import com.campus.trade.entity.Product;
import com.campus.trade.exception.CustomException;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.ProductService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    // 【修改】getProducts 方法实现，传递所有参数
    @Override
    @Cacheable("products")
    public List<Product> getProducts(String keyword, Integer categoryId, Double minPrice, Double maxPrice, String orderBy) {
        return productMapper.findProducts(keyword, categoryId, minPrice, maxPrice, orderBy);
    }

    /**
     * 【最终优化】
     * 重新启用了 @Cacheable 注解。
     * 现在，当此方法被调用时，它会首先根据 productId 在名为 "product" 的缓存中查找。
     * 只有当缓存中没有对应ID的商品时，才会去查询数据库。
     */
    @Override
    @Cacheable(value = "product", key = "#productId")
    public Product getProductById(String productId) {
        log.info(">>> [缓存未命中] 正在为商品ID {} 从数据库查询详情...", productId);
        Product product = productMapper.findProductById(productId);
        if (product == null) {
            throw new CustomException("商品不存在或已下架");
        }
        return product;
    }

    /**
     * @CacheEvict 会在方法执行后，清除指定的缓存。
     * allEntries = true 表示清除 "products" 缓存中的所有条目。
     * 这确保了在新增商品后，首页的商品列表缓存会被清空，从而能显示出最新的商品。
     */
    @Override
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(Product product, String sellerId) {
        product.setSellerId(sellerId);
        productMapper.insertProduct(product);
        return product;
    }

    /**
     * 同时清除两个缓存：
     * "product::#product.id": 清除这个特定ID的商品详情缓存。
     * "products": 清除整个商品列表的缓存。
     */
    @Override
    @CacheEvict(value = {"product", "products"}, key = "#product.id", allEntries = true)
    public Product updateProduct(Product product, String currentUserId) {
        Product existingProduct = productMapper.findProductById(product.getId());
        if (existingProduct == null) {
            throw new CustomException("商品不存在");
        }
        if (!Objects.equals(existingProduct.getSellerId(), currentUserId)) {
            throw new CustomException("无权修改他人的商品");
        }
        productMapper.updateProduct(product);
        return productMapper.findProductById(product.getId());
    }

    @Override
    @CacheEvict(value = {"product", "products"}, key = "#productId", allEntries = true)
    public void updateProductStatus(String productId, String status, String currentUserId) {
        Product existingProduct = productMapper.findProductById(productId);
        if (existingProduct == null) {
            throw new CustomException("商品不存在");
        }
        if (!Objects.equals(existingProduct.getSellerId(), currentUserId)) {
            // 在实际项目中，这里应该加入对管理员角色的判断
            throw new CustomException("无权修改他人的商品");
        }
        productMapper.updateProductStatus(productId, status);
    }


    @Override
    public PageResult<Product> findAllProductsForAdmin(String keyword, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        Page<Product> productPage = (Page<Product>) productMapper.findAllForAdmin(keyword);
        return new PageResult<>(productPage);
    }

    // 【新增】获取推荐商品的方法实现
    @Override
    @Cacheable(value = "recommendations", key = "#productId") // 对推荐结果进行缓存
    public List<Product> getRecommendedProducts(String productId) {
        // 推荐5个商品
        return productMapper.findRecommendedProducts(productId, 5);
    }
}
