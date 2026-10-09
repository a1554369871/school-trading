package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.Comment;
import com.trading.service.impl.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentServiceImpl commentService;

    /**
     * 发表评论 — 用户在商品详情页对商品发表评论或回复其他评论（二级嵌套）
     * → CommentServiceImpl.addComment() → CommentMapper.xml: insert
     */
    @PostMapping
    public Result<?> addComment(Authentication auth, @RequestBody Comment comment) {
        Integer userId = (Integer) auth.getPrincipal();
        return Result.success(commentService.addComment(userId, comment));
    }

    /**
     * 商品评论列表 — 商品详情页底部展示该商品的所有评论及回复，含评论者信息
     * → CommentServiceImpl.getByProductId() → CommentMapper.xml: findByProductId
     */
    @GetMapping("/product/{productId}")
    public Result<List<Map<String, Object>>> getByProduct(@PathVariable Integer productId) {
        return Result.success(commentService.getByProductId(productId));
    }

    /**
     * 删除评论 — 评论作者本人删除自己发表的评论
     * → CommentServiceImpl.delete() → CommentMapper.xml: findOwnerById + deleteById
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(Authentication auth, @PathVariable Integer id) {
        commentService.delete((Integer) auth.getPrincipal(), id);
        return Result.success();
    }

    /**
     * 热门评论商品 — 管理员后台/统计页展示评论数最多的商品排行
     * → CommentServiceImpl.getMostCommented() → CommentMapper.xml: findMostCommentedProducts
     */
    @GetMapping("/stats/most-commented")
    public Result<?> mostCommented(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(commentService.getMostCommented(limit));
    }
}
