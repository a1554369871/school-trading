package com.trading.mapper;

import com.trading.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnnouncementMapper {

    List<Announcement> findAll(@Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    int countAll();

    Announcement findById(@Param("id") Integer id);

    int insert(Announcement announcement);

    int update(Announcement announcement);

    int deleteById(@Param("id") Integer id);
}
