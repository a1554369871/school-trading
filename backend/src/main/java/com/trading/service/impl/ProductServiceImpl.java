package com.trading.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.trading.common.BusinessException;
import com.trading.common.PageResult;
import com.trading.dto.*;
import com.trading.entity.Product;
import com.trading.entity.User;
import com.trading.mapper.InteractionMapper;
import com.trading.mapper.ProductMapper;
import com.trading.mapper.UserMapper;
import com.trading.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final InteractionMapper interactionMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PRODUCT_CACHE_KEY = "product:";
    private static final String PRODUCT_LIST_KEY = "product:list:";
    private static final String CATEGORY_CACHE_KEY = "category:list";
    private static final String STATS_CATEGORY_KEY = "stats:category";

    @Override
    public Product create(Integer userId, ProductDTO dto) {
        Product product = new Product();
        product.setUserId(userId);
        product.setProductName(dto.getProductName());
        product.setProductImage(dto.getProductImage());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setCategoryId(dto.getCategoryId());
        product.setCondition(dto.getCondition());
        product.setStatus(1);
        productMapper.insert(product);
        invalidateListCache();
        return product;
    }

    @Override
    public Product update(Integer userId, Integer productId, ProductDTO dto) {
        Product existing = productMapper.findById(productId);
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此商品");
        }
        dto.setId(productId);
        existing.setProductName(dto.getProductName());
        existing.setProductImage(dto.getProductImage());
        existing.setPrice(dto.getPrice());
        existing.setDescription(dto.getDescription());
        existing.setCategoryId(dto.getCategoryId());
        existing.setCondition(dto.getCondition());
        productMapper.update(existing);
        try {
            redisTemplate.delete(PRODUCT_CACHE_KEY + productId);
        } catch (Exception e) {
            log.warn("Redis删除商品缓存失败: {}", e.getMessage());
        }
        invalidateListCache();
        return productMapper.findById(productId);
    }

    @Override
    public void delete(Integer userId, Integer productId) {
        Product existing = productMapper.findById(productId);
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此商品");
        }
        productMapper.updateStatusWithVersion(productId, 0, existing.getVersion());
        try {
            redisTemplate.delete(PRODUCT_CACHE_KEY + productId);
        } catch (Exception e) {
            log.warn("Redis删除商品缓存失败: {}", e.getMessage());
        }
        invalidateListCache();
    }

    public void updateStatus(Integer userId, Integer productId, Integer status) {
        Product existing = productMapper.findById(productId);
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此商品");
        }
        productMapper.updateStatusWithVersion(productId, status, existing.getVersion());
        try {
            redisTemplate.delete(PRODUCT_CACHE_KEY + productId);
        } catch (Exception e) {
            log.warn("Redis删除商品缓存失败: {}", e.getMessage());
        }
        invalidateListCache();
    }

    @Override
    public Product getById(Integer id) {
        try {
            String cacheKey = PRODUCT_CACHE_KEY + id;
            Product cached = (Product) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis读取商品缓存失败: {}", e.getMessage());
        }
        Product product = productMapper.findById(id);
        if (product != null) {
            try {
                redisTemplate.opsForValue().set(PRODUCT_CACHE_KEY + id, product, 30, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("Redis写入商品缓存失败: {}", e.getMessage());
            }
        }
        return product;
    }

    @Override
    public PageResult<Product> search(ProductQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Product> list = productMapper.searchProducts(query);
        PageInfo<Product> pageInfo = new PageInfo<>(list);
        return PageResult.of(list, pageInfo.getTotal(), query.getPageNum(), query.getPageSize());
    }

    @Override
    public PageResult<Product> fullTextSearch(String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int total = productMapper.countFullTextSearch(keyword);
        List<Product> list = productMapper.fullTextSearch(keyword, offset, pageSize);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> getDetail(Integer id, Integer currentUserId) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 浏览量自增（同一用户5分钟内不重复增加）
        try {
            String viewKey = "product:view:" + id + ":" + (currentUserId != null ? currentUserId : "anonymous");
            Boolean alreadyViewed = redisTemplate.hasKey(viewKey);
            if (Boolean.FALSE.equals(alreadyViewed)) {
                productMapper.incrementViewCount(id);
                redisTemplate.opsForValue().set(viewKey, "1", 5, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.warn("Redis浏览量统计失败: {}", e.getMessage());
            // 继续执行，不影响主流程
        }

        Map<String, Object> detail = new LinkedHashMap<>();
        detail.put("product", product);

        User seller = userMapper.findById(product.getUserId());
        detail.put("seller", seller);

        Map<String, Object> interactions = interactionMapper.countByProduct(id);
        if (interactions == null) {
            interactions = new LinkedHashMap<>();
            interactions.put("like_count", 0);
            interactions.put("favorite_count", 0);
            interactions.put("want_count", 0);
        }
        detail.put("interactions", interactions);

        if (currentUserId != null) {
            detail.put("userLiked", interactionMapper.checkInteraction(currentUserId, id, "like") > 0);
            detail.put("userFavorited", interactionMapper.checkInteraction(currentUserId, id, "favorite") > 0);
            detail.put("userWanted", interactionMapper.checkInteraction(currentUserId, id, "want") > 0);
        }

        return detail;
    }

    @Override
    public void toggleInteraction(Integer userId, Integer productId, String type) {
        if (interactionMapper.checkInteraction(userId, productId, type) > 0) {
            interactionMapper.deleteByUserAndProduct(userId, productId, type);
        } else {
            interactionMapper.insert(new com.trading.entity.Interaction() {{
                setUserId(userId);
                setProductId(productId);
                setType(type);
            }});
        }
        try {
            redisTemplate.delete(PRODUCT_CACHE_KEY + productId);
        } catch (Exception e) {
            log.warn("Redis删除商品缓存失败: {}", e.getMessage());
        }
    }

    @Override
    public PageResult<Product> getMyProducts(Integer userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int total = productMapper.countMyProducts(userId);
        List<Product> list = productMapper.getMyProducts(userId, offset, pageSize);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    @Override
    public List<CategoryStats> getCategoryStats() {
        try {
            @SuppressWarnings("unchecked")
            List<CategoryStats> cached = (List<CategoryStats>) (List<?>) redisTemplate.opsForValue().get(STATS_CATEGORY_KEY);
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis读取分类统计缓存失败: {}", e.getMessage());
        }
        List<CategoryStats> stats = productMapper.getCategoryStats();
        try {
            redisTemplate.opsForValue().set(STATS_CATEGORY_KEY, stats, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis写入分类统计缓存失败: {}", e.getMessage());
        }
        return stats;
    }

    @Override
    public PageResult<Product> getBelowAvgPrice(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int total = productMapper.countBelowAvgPriceProducts();
        List<Product> list = productMapper.findBelowAvgPriceProducts(offset, pageSize);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    @Override
    public List<Product> getPopular(Integer limit) {
        return productMapper.findPopularProducts(limit);
    }

    private void invalidateListCache() {
        try {
            Set<String> keys = redisTemplate.keys(PRODUCT_LIST_KEY + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            redisTemplate.delete(STATS_CATEGORY_KEY);
        } catch (Exception e) {
            log.warn("Redis清除列表缓存失败: {}", e.getMessage());
        }
    }

    @Override
    public PageResult<Product> getMyFavorites(Integer userId, int pageNum, int pageSize) {
        List<Integer> productIds = interactionMapper.findProductIdsByUserAndType(userId, "favorite");
        if (productIds.isEmpty()) {
            return PageResult.of(new ArrayList<>(), 0, pageNum, pageSize);
        }
        int offset = (pageNum - 1) * pageSize;
        List<Product> list = productMapper.findByIds(productIds, offset, pageSize);
        return PageResult.of(list, productIds.size(), pageNum, pageSize);
    }

    @Override
    public boolean isFavorited(Integer userId, Integer productId) {
        return interactionMapper.checkInteraction(userId, productId, "favorite") > 0;
    }
}
