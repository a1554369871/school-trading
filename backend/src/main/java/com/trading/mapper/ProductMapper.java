package com.trading.mapper;

import com.trading.dto.ProductQuery;
import com.trading.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    Product findById(@Param("id") Integer id);

    List<Product> findByIds(@Param("ids") List<Integer> ids,
                            @Param("offset") int offset,
                            @Param("pageSize") int pageSize);

    int insert(Product product);

    int update(Product product);

    int updateStatusWithVersion(@Param("id") Integer id,
                                @Param("status") Integer status,
                                @Param("version") Integer version);

    List<Product> searchProducts(ProductQuery query);

    long countProducts(ProductQuery query);

    /** 我的商品：查看自己发布的所有商品（不限status） */
    List<Product> getMyProducts(@Param("userId") Integer userId,
                                @Param("offset") int offset,
                                @Param("pageSize") int pageSize);

    int countMyProducts(@Param("userId") Integer userId);

    List<Product> fullTextSearch(@Param("keyword") String keyword,
                                 @Param("offset") int offset,
                                 @Param("pageSize") int pageSize);

    int countFullTextSearch(@Param("keyword") String keyword);

    Product findDetailById(@Param("id") Integer id);

    List<com.trading.dto.CategoryStats> getCategoryStats();

    List<Product> findBelowAvgPriceProducts(@Param("offset") int offset,
                                             @Param("pageSize") int pageSize);

    int countBelowAvgPriceProducts();

    List<Product> findPopularProducts(@Param("limit") Integer limit);

    int incrementViewCount(@Param("id") Integer id);
}
