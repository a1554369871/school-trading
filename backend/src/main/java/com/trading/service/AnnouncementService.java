package com.trading.service;

import com.trading.common.PageResult;
import com.trading.entity.Announcement;
import java.util.List;

public interface AnnouncementService {
    List<Announcement> getAll();
    PageResult<Announcement> getAll(int pageNum, int pageSize);
    Announcement getById(Integer id);
    Announcement create(Integer publisherId, Announcement announcement);
    void update(Announcement announcement);
    void delete(Integer id);
}
