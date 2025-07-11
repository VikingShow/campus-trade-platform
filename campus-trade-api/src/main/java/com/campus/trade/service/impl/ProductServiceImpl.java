package com.campus.trade.service.impl;

import com.campus.trade.dto.AdminProductDTO;
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
import org.springframework.cache.annotation.Caching;
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
//    @Caching
    @Cacheable("products")
    public List<Product> getProducts(String keyword, Integer categoryId, Double minPrice, Double maxPrice, String orderBy) {
        return productMapper.findProducts(keyword, categoryId, minPrice, maxPrice, orderBy);
    }

    /**
     * 【最终诊断修正】
     * 我已将 @Cacheable 注解暂时注释掉。
     * 这会强制此方法在每一次被调用时，都必须去数据库执行一次全新的查询，
     * 从而彻底绕过任何可能存在的、被污染的旧缓存。
     */
    @Override
    // @Cacheable(value = "product", key = "#productId")
    public Product getProductById(String productId) {
        log.info(">>> [缓存已禁用] 正在为商品ID {} 从数据库强制查询详情...", productId);
        Product product = productMapper.findProductById(productId);
        if (product == null) {
            throw new CustomException("商品不存在或已下架");
        }
        log.info("<<< 数据库查询成功，商品附图数量为: {}", (product.getImageUrls() != null ? product.getImageUrls().size() : 0));
        return product;
    }

    /**
     * 【最终修正】
     * 使用 @CacheEvict 注解，在创建新商品后，同时清除 "products" (列表) 和 "product" (详情) 这两个缓存。
     * allEntries = true 表示将这两个缓存中的所有条目全部清除。
     */
    @Override
    @Transactional
    @CacheEvict(cacheNames = {"products", "product"}, allEntries = true)
    public Product createProduct(ProductDTO productDTO, String sellerId) {
        log.info(">>> [新增商品] 清除 'products' 和 'product' 缓存。");
        Product product = new Product();
        product.setTitle(productDTO.getTitle());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategoryId(productDTO.getCategoryId());
        product.setConditionLevel(productDTO.getConditionLevel());
        product.setCoverImage(productDTO.getCoverImage());
        if (productDTO.getDeliveryOptions() != null) {
            product.setDeliveryOptions(String.join(",", productDTO.getDeliveryOptions()));
        }
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
        // 【最终修正】采用与 UserServiceImpl 完全一致的、更健壮的实现方式
        List<Product> productList = productMapper.findAllForAdmin(keyword);
        return new PageResult<>(productList);
    }

    @Override
    @Cacheable(value = "recommendations", key = "#productId")
    public List<Product> getRecommendedProducts(String productId) {
        return productMapper.findRecommendedProducts(productId, 5);
    }

    /**
     * 【新增】为管理员提供的更新方法。
     * 它不检查当前用户是否是商品所有者，因为管理员有权编辑任何商品。
     */
    @Override
    @Transactional
    @CacheEvict(value = {"product::#productId", "products"}, allEntries = true)
    public Product updateProductByAdmin(String productId, ProductDTO productDTO) {
        Product existingProduct = productMapper.findProductById(productId);
        if (existingProduct == null) {
            throw new CustomException("商品不存在");
        }

        // 基本信息
        existingProduct.setTitle(productDTO.getTitle());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setCategoryId(productDTO.getCategoryId());
        existingProduct.setConditionLevel(productDTO.getConditionLevel());

        // 主图（无论是否为空都 set，交由 SQL 控制）
        existingProduct.setCoverImage(productDTO.getCoverImage());

        // 配送方式（为空时 set 空字符串，保证 SQL 能更新，只用'SHIPPING'）
        if (productDTO.getDeliveryOptions() != null) {
            existingProduct.setDeliveryOptions(String.join(",", productDTO.getDeliveryOptions()));
        } else {
            existingProduct.setDeliveryOptions("");
        }

        // 更新商品主表
        productMapper.updateProduct(existingProduct);

        // 附图（最多3张，前端应限制）
        productImageMapper.deleteByProductId(productId);
        if (productDTO.getImageUrls() != null && !productDTO.getImageUrls().isEmpty()) {
            List<String> images = productDTO.getImageUrls();
            if (images.size() > 3) {
                images = images.subList(0, 3);
            }
            productImageMapper.batchInsert(productId, images);
        }

        return productMapper.findProductById(productId);
    }

    /**
     * 【新增】为管理员提供的删除方法。
     * 注意：这将永久删除商品及其所有关联数据（如收藏、订单等，取决于数据库的外键级联设置）。
     */
    @Override
    @CacheEvict(value = {"product::#productId", "products"}, allEntries = true)
    public void deleteProduct(String productId) {
        productMapper.deleteById(productId);
    }

    /**
     * 【新增】为管理员创建商品的方法实现。
     * 它直接使用 DTO 中提供的 sellerId。
     */
    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product createProductByAdmin(AdminProductDTO adminProductDTO) {
        Product product = new Product();
        product.setTitle(adminProductDTO.getTitle());
        product.setDescription(adminProductDTO.getDescription());
        product.setPrice(adminProductDTO.getPrice());
        product.setCategoryId(adminProductDTO.getCategoryId());
        product.setConditionLevel(adminProductDTO.getConditionLevel());
        product.setCoverImage(adminProductDTO.getCoverImage());
        product.setSellerId(adminProductDTO.getSellerId()); // 使用指定的卖家ID
        if (adminProductDTO.getDeliveryOptions() != null) {
            product.setDeliveryOptions(adminProductDTO.getDeliveryOptions());
        }

        productMapper.insertProduct(product);

        List<String> imageUrls = adminProductDTO.getImageUrls();
        if (imageUrls != null && !imageUrls.isEmpty()) {
            productImageMapper.batchInsert(product.getId(), imageUrls);
        }

        return productMapper.findProductById(product.getId());
    }
}
