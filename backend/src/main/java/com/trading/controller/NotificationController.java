package com.trading.controller;

import com.trading.common.Result;
import com.trading.service.impl.MessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final MessageServiceImpl messageService;

    /**
     * 我的通知列表 — 消息通知中心展示当前用户收到的所有系统通知，分页展示
     * → MessageServiceImpl.getNotifications() → SystemNotificationMapper.xml: countByReceiverId + findByReceiverId
     */
    @GetMapping
    public Result<?> myNotifications(Authentication auth,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(messageService.getNotifications((Integer) auth.getPrincipal(), page, pageSize));
    }

    /**
     * 未读通知数量 — 导航栏消息图标角标展示未读通知数量
     * → MessageServiceImpl.countUnread() → SystemNotificationMapper.xml: countUnread
     */
    @GetMapping("/unread-count")
    public Result<Integer> unreadCount(Authentication auth) {
        return Result.success(messageService.countUnread((Integer) auth.getPrincipal()));
    }

    /**
     * 标记单条通知已读 — 用户点击某条通知后将其标记为已读状态
     * → MessageServiceImpl.markAsRead() → SystemNotificationMapper.xml: markAsRead
     */
    @PutMapping("/{id}/read")
    public Result<?> markRead(@PathVariable Integer id, Authentication auth) {
        messageService.markAsRead((Integer) auth.getPrincipal(), id);
        return Result.success();
    }

    /**
     * 全部标记已读 — 一键将当前用户的所有未读通知标记为已读
     * → MessageServiceImpl.markAllAsRead() → SystemNotificationMapper.xml: markAllAsRead
     */
    @PutMapping("/read-all")
    public Result<?> markAllRead(Authentication auth) {
        messageService.markAllAsRead((Integer) auth.getPrincipal());
        return Result.success();
    }

    /**
     * 删除通知 — 用户删除指定的系统通知记录
     * → MessageServiceImpl.deleteNotification() → SystemNotificationMapper.xml: deleteById
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteNotification(Authentication auth, @PathVariable Integer id) {
        messageService.deleteNotification((Integer) auth.getPrincipal(), id);
        return Result.success();
    }
}
