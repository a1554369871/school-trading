package com.trading.service.impl;

import com.trading.common.PageResult;
import com.trading.entity.PrivateMessage;
import com.trading.entity.SystemNotification;
import com.trading.mapper.PrivateMessageMapper;
import com.trading.mapper.SystemNotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl {

    private final PrivateMessageMapper messageMapper;
    private final SystemNotificationMapper notificationMapper;

    // 私聊
    public void sendMessage(Integer senderId, PrivateMessage message) {
        if (message.getReceiverId() != null && message.getReceiverId().equals(senderId)) {
            throw new RuntimeException("不能给自己发送私信");
        }
        message.setSenderId(senderId);
        messageMapper.insert(message);
    }

    public PageResult<Map<String, Object>> getConversation(Integer user1Id, Integer user2Id, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int total = messageMapper.countConversation(user1Id, user2Id);
        List<Map<String, Object>> list = messageMapper.findConversation(user1Id, user2Id, offset, pageSize);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    public PageResult<Map<String, Object>> getRecentConversations(Integer userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int total = messageMapper.countRecentConversations(userId);
        List<Map<String, Object>> list = messageMapper.getRecentConversations(userId, offset, pageSize);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    public void markConversationAsRead(Integer userId, Integer otherId) {
        messageMapper.markConversationAsRead(userId, otherId);
    }

    // 系统通知
    public void sendNotification(SystemNotification notification) {
        notificationMapper.insert(notification);
    }

    public PageResult<SystemNotification> getNotifications(Integer userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int total = notificationMapper.countByReceiverId(userId);
        List<SystemNotification> list = notificationMapper.findByReceiverId(userId, offset, pageSize);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    public void markAsRead(Integer userId, Integer id) {
        // 简化实现：直接标记已读（后续可加权限校验）
        notificationMapper.markAsRead(id);
    }

    public void markAllAsRead(Integer userId) {
        notificationMapper.markAllAsRead(userId);
    }

    public int countUnread(Integer userId) {
        return notificationMapper.countUnread(userId);
    }

    public void deleteNotification(Integer userId, Integer id) {
        notificationMapper.deleteById(id);
    }

    public void deleteNotificationsByContent(String keyword) {
        notificationMapper.deleteByContentLike(keyword);
    }
}
