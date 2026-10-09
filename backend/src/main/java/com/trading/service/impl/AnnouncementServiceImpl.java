package com.trading.service.impl;

import com.trading.common.BusinessException;
import com.trading.common.PageResult;
import com.trading.entity.Announcement;
import com.trading.entity.SystemNotification;
import com.trading.entity.User;
import com.trading.mapper.AnnouncementMapper;
import com.trading.mapper.UserMapper;
import com.trading.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;
    private final MessageServiceImpl messageService;
    private final UserMapper userMapper;

    @Override
    public List<Announcement> getAll() {
        return announcementMapper.findAll(0, Integer.MAX_VALUE);
    }

    @Override
    public PageResult<Announcement> getAll(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        int total = announcementMapper.countAll();
        List<Announcement> list = announcementMapper.findAll(offset, pageSize);
        return PageResult.of(list, total, pageNum, pageSize);
    }

    @Override
    public Announcement getById(Integer id) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        return announcement;
    }

    @Override
    public Announcement create(Integer publisherId, Announcement announcement) {
        announcement.setPublisherId(publisherId);
        announcementMapper.insert(announcement);
        
        // 发送系统通知给所有用户
        try {
            List<User> users = userMapper.findAll(null);
            for (User user : users) {
                SystemNotification notification = new SystemNotification();
                notification.setReceiverId(user.getId());
                notification.setTitle("新公告发布");
                notification.setContent("管理员发布了新公告：" + announcement.getTitle());
                notification.setIsRead(0);
                messageService.sendNotification(notification);
            }
            log.info("公告发布通知已发送给 {} 个用户", users.size());
        } catch (Exception e) {
            log.error("发送公告通知失败", e);
        }
        
        return announcement;
    }

    @Override
    public void update(Announcement announcement) {
        if (announcementMapper.findById(announcement.getId()) == null) {
            throw new BusinessException("公告不存在");
        }
        announcementMapper.update(announcement);
    }

    @Override
    public void delete(Integer id) {
        Announcement announcement = announcementMapper.findById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        // 删除关联的系统通知
        try {
            String keyword = "管理员发布了新公告：" + announcement.getTitle();
            messageService.deleteNotificationsByContent(keyword);
        } catch (Exception e) {
            log.error("删除公告关联通知失败", e);
        }
        announcementMapper.deleteById(id);
    }
}
