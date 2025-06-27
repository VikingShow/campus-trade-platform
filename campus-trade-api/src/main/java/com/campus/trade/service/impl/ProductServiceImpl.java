package com.campus.trade.service.impl;

import com.campus.trade.dto.PageResult;
import com.campus.trade.dto.ProductDTO;
import com.campus.trade.entity.Product;
import com.campus.trade.exception.CustomException;
import com.campus.trade.mapper.ProductImageMapper;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.security.AuthenticatedUser;
import com.campus.trade.service.ProductService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, ProductImageMapper productImageMapper) {
        this.productMapper = productMapper;
        this.productImageMapper = productImageMapper;
    }

    @Override
    @Cacheable("products")
    public List<Product> getProducts(String keyword, Integer categoryId, Double minPrice, Double maxPrice, String orderBy) {
        return productMapper.findProducts(keyword, categoryId, minPrice, maxPrice, orderBy);
    }

    @Override
    @Cacheable(value = "product", key = "#productId")
    public Product getProductById(String productId) {
        return productMapper.findProductById(productId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(ProductDTO productDTO, String sellerId) {
        log.info(">>> [新增商品] 清除 'products' 缓存。");
        Product product = new Product();
        product.setTitle(productDTO.getTitle());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategoryId(productDTO.getCategoryId());
        product.setConditionLevel(productDTO.getConditionLevel());
        product.setCoverImage(productDTO.getCoverImage());
        product.setSellerId(sellerId);

        productMapper.insertProduct(product);

        List<String> imageUrls = productDTO.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            productImageMapper.batchInsert(product.getId(), imageUrls);
        }

        return productMapper.findProductById(product.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"product::#productId", "products"}, allEntries = true)
    public Product updateProduct(String productId, ProductDTO productDTO, String currentUserId) {
        Product existingProduct = productMapper.findProductById(productId);
        if (existingProduct == null) {
            throw new CustomException("商品不存在");
        }
        if (!Objects.equals(existingProduct.getSellerId(), currentUserId)) {
            throw new CustomException("无权修改他人的商品");
        }

        existingProduct.setTitle(productDTO.getTitle());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setCategoryId(productDTO.getCategoryId());
        existingProduct.setConditionLevel(productDTO.getConditionLevel());
        if (productDTO.getCoverImage() != null && !productDTO.getCoverImage().isEmpty()) {
            existingProduct.setCoverImage(productDTO.getCoverImage());
        }

        productMapper.updateProduct(existingProduct);

        productImageMapper.deleteByProductId(productId);
        if (!CollectionUtils.isEmpty(productDTO.getImageUrls())) {
            productImageMapper.batchInsert(productId, productDTO.getImageUrls());
        }

        return productMapper.findProductById(productId);
    }

    @Override
    @CacheEvict(value = {"product::#productId", "products"}, allEntries = true)
    public void updateProductStatus(String productId, String status, AuthenticatedUser user) {
        Product existingProduct = productMapper.findProductById(productId);
        if (existingProduct == null) {
            throw new CustomException("商品不存在");
        }

        log.info(">>> [权限诊断] 正在为用户 '{}' (ID: {}) 更新商品状态...", user.getUsername(), user.getUserId());
        String authoritiesString = user.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.joining(", "));
        log.info(">>> [权限诊断] 该用户持有的权限为: [{}]", authoritiesString);

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        log.info(">>> [权限诊断] isAdmin 检查结果为: {}", isAdmin);

        if (!isAdmin && !Objects.equals(existingProduct.getSellerId(), user.getUserId())) {
            throw new CustomException("无权修改他人的商品");
        }

        log.info(">>> [权限诊断] 权限检查通过，正在执行数据库更新...");
        productMapper.updateProductStatus(productId, status);
    }

    @Override
    public PageResult<Product> findAllProductsForAdmin(String keyword, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        Page<Product> productPage = (Page<Product>) productMapper.findAllForAdmin(keyword);
        return new PageResult<>(productPage);
    }

    @Override
    @Cacheable(value = "recommendations", key = "#productId")
    public List<Product> getRecommendedProducts(String productId) {
        return productMapper.findRecommendedProducts(productId, 5);
    }
}
