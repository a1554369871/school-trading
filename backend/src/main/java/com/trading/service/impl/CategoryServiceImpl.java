package com.trading.service.impl;

import com.trading.entity.Category;
import com.trading.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl {

    private final CategoryMapper categoryMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CATEGORY_CACHE_KEY = "category:list";

    /**
     * 获取所有分类（Redis 缓存，TTL 1小时）
     * 分类数据变更频率极低，适合长时间缓存
     */
    public List<Category> getAll() {
        try {
            @SuppressWarnings("unchecked")
            List<Category> cached = (List<Category>) (List<?>) redisTemplate.opsForValue().get(CATEGORY_CACHE_KEY);
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis读取分类缓存失败: {}", e.getMessage());
        }
        List<Category> categories = categoryMapper.findAll();
        try {
            redisTemplate.opsForValue().set(CATEGORY_CACHE_KEY, categories, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("Redis写入分类缓存失败: {}", e.getMessage());
        }
        return categories;
    }

    public Category create(Category category) {
        categoryMapper.insert(category);
        try {
            redisTemplate.delete(CATEGORY_CACHE_KEY);
        } catch (Exception e) {
            log.warn("Redis删除分类缓存失败: {}", e.getMessage());
        }
        return category;
    }

    public void update(Category category) {
        categoryMapper.update(category);
        try {
            redisTemplate.delete(CATEGORY_CACHE_KEY);
        } catch (Exception e) {
            log.warn("Redis删除分类缓存失败: {}", e.getMessage());
        }
    }

    public void delete(Integer id) {
        categoryMapper.deleteById(id);
        try {
            redisTemplate.delete(CATEGORY_CACHE_KEY);
        } catch (Exception e) {
            log.warn("Redis删除分类缓存失败: {}", e.getMessage());
        }
    }
}
