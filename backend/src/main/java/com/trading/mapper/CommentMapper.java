package com.trading.mapper;

import com.trading.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {

    int insert(Comment comment);

    int deleteById(@Param("id") Integer id);

    /**
     * 查询评论归属（用于权限校验）
     */
    Map<String, Object> findOwnerById(@Param("id") Integer id);

    /**
     * JOIN 查询：获取评论列表（含评论者昵称、头像）
     */
    List<Map<String, Object>> findByProductId(@Param("productId") Integer productId);

    /**
     * GROUP BY：统计每个商品的评论数
     */
    List<Map<String, Object>> countByProduct();

    int countAll();

    /**
     * 子查询：查找评论数最多的商品
     */
    List<Map<String, Object>> findMostCommentedProducts(@Param("limit") Integer limit);

    /**
     * 管理员分页查询所有评论
     */
    List<Map<String, Object>> findAllPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    int countAllPage();
}
