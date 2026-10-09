package com.trading.service.impl;

import com.trading.common.BusinessException;
import com.trading.entity.Comment;
import com.trading.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl {

    private final CommentMapper commentMapper;

    public Comment addComment(Integer userId, Comment comment) {
        comment.setCommenterId(userId);
        commentMapper.insert(comment);
        return comment;
    }

    public List<Map<String, Object>> getByProductId(Integer productId) {
        return commentMapper.findByProductId(productId);
    }

    /**
     * 删除评论 — 仅允许评论作者本人或管理员删除
     */
    public void delete(Integer userId, Integer commentId, Integer userRole) {
        Map<String, Object> comment = commentMapper.findOwnerById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        Integer commenterId = (Integer) comment.get("commenter_id");
        if (!commenterId.equals(userId) && (userRole == null || userRole != 1)) {
            throw new BusinessException("无权删除此评论");
        }
        commentMapper.deleteById(commentId);
    }

    /**
     * 兼容旧版调用（无角色参数，默认不校验管理员）
     */
    public void delete(Integer userId, Integer commentId) {
        Map<String, Object> comment = commentMapper.findOwnerById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        Integer commenterId = (Integer) comment.get("commenter_id");
        if (!commenterId.equals(userId)) {
            throw new BusinessException("无权删除此评论");
        }
        commentMapper.deleteById(commentId);
    }

    public List<Map<String, Object>> getMostCommented(Integer limit) {
        return commentMapper.findMostCommentedProducts(limit);
    }
}
