package com.trading.service;

import com.trading.common.PageResult;
import com.trading.dto.*;
import com.trading.entity.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    Product create(Integer userId, ProductDTO dto);
    Product update(Integer userId, Integer productId, ProductDTO dto);
    void delete(Integer userId, Integer productId);
    Product getById(Integer id);
    PageResult<Product> search(ProductQuery query);
    PageResult<Product> fullTextSearch(String keyword, int pageNum, int pageSize);
    Map<String, Object> getDetail(Integer id, Integer currentUserId);
    void toggleInteraction(Integer userId, Integer productId, String type);
    PageResult<Product> getMyProducts(Integer userId, int pageNum, int pageSize);
    List<CategoryStats> getCategoryStats();
    PageResult<Product> getBelowAvgPrice(int pageNum, int pageSize);
    List<Product> getPopular(Integer limit);
    PageResult<Product> getMyFavorites(Integer userId, int pageNum, int pageSize);
    boolean isFavorited(Integer userId, Integer productId);
}
