package com.trading.controller;

import com.trading.common.Result;
import com.trading.entity.PrivateMessage;
import com.trading.entity.SystemNotification;
import com.trading.service.impl.MessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageServiceImpl messageService;

    // 私聊
    /**
     * 发送私信 — 用户在聊天页面向其他用户发送私信消息（不能给自己发送）
     * → MessageServiceImpl.sendMessage() → PrivateMessageMapper.xml: insert
     */
    @PostMapping("/send")
    public Result<?> sendMessage(Authentication auth, @RequestBody PrivateMessage message) {
        messageService.sendMessage((Integer) auth.getPrincipal(), message);
        return Result.success();
    }

    /**
     * 聊天记录 — 查看与指定用户的聊天记录，分页展示，按时间正序排列
     * → MessageServiceImpl.getConversation() → PrivateMessageMapper.xml: countConversation + findConversation
     */
    @GetMapping("/conversation/{otherId}")
    public Result<?> getConversation(Authentication auth, @PathVariable Integer otherId,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "20") int pageSize) {
        Integer userId = (Integer) auth.getPrincipal();
        return Result.success(messageService.getConversation(userId, otherId, page, pageSize));
    }

    /**
     * 最近会话列表 — 聊天页面左侧展示最近的聊天对象列表，含最新消息预览和未读数
     * → MessageServiceImpl.getRecentConversations() → PrivateMessageMapper.xml: countRecentConversations + getRecentConversations
     */
    @GetMapping("/recent")
    public Result<?> getRecent(Authentication auth,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(messageService.getRecentConversations((Integer) auth.getPrincipal(), page, pageSize));
    }

    /**
     * 标记会话已读 — 进入聊天页面时将与该用户的所有未读消息标记为已读
     * → MessageServiceImpl.markConversationAsRead() → PrivateMessageMapper.xml: markConversationAsRead
     */
    @PutMapping("/conversation/{otherId}/read")
    public Result<?> markConversationRead(Authentication auth, @PathVariable Integer otherId) {
        Integer userId = (Integer) auth.getPrincipal();
        messageService.markConversationAsRead(userId, otherId);
        return Result.success();
    }

    // 系统通知
    /**
     * 通知列表 — 消息通知中心展示系统通知（订单状态变更、新公告发布等），分页展示
     * → MessageServiceImpl.getNotifications() → SystemNotificationMapper.xml: countByReceiverId + findByReceiverId
     */
    @GetMapping("/notifications")
    public Result<?> getNotifications(Authentication auth,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(messageService.getNotifications((Integer) auth.getPrincipal(), page, pageSize));
    }

    /**
     * 未读消息数量 — 导航栏消息图标显示的未读通知数量角标
     * → MessageServiceImpl.countUnread() → SystemNotificationMapper.xml: countUnread
     */
    @GetMapping("/notifications/unread-count")
    public Result<Integer> unreadCount(Authentication auth) {
        return Result.success(messageService.countUnread((Integer) auth.getPrincipal()));
    }

    /**
     * 标记单条通知已读 — 用户点击某条通知后将其标记为已读状态
     * → MessageServiceImpl.markAsRead() → SystemNotificationMapper.xml: markAsRead
     */
    @PutMapping("/notifications/{id}/read")
    public Result<?> markRead(Authentication auth, @PathVariable Integer id) {
        Integer userId = (Integer) auth.getPrincipal();
        messageService.markAsRead(userId, id);
        return Result.success();
    }

    /**
     * 全部标记已读 — 一键将当前用户的所有未读通知标记为已读
     * → MessageServiceImpl.markAllAsRead() → SystemNotificationMapper.xml: markAllAsRead
     */
    @PutMapping("/notifications/read-all")
    public Result<?> markAllRead(Authentication auth) {
        messageService.markAllAsRead((Integer) auth.getPrincipal());
        return Result.success();
    }

    /**
     * 删除通知 — 用户删除指定的系统通知记录
     * → MessageServiceImpl.deleteNotification() → SystemNotificationMapper.xml: deleteById
     */
    @DeleteMapping("/notifications/{id}")
    public Result<?> deleteNotification(Authentication auth, @PathVariable Integer id) {
        messageService.deleteNotification((Integer) auth.getPrincipal(), id);
        return Result.success();
    }
}
