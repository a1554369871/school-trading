package com.trading.mapper;

import com.trading.entity.SystemNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SystemNotificationMapper {

    List<SystemNotification> findByReceiverId(@Param("receiverId") Integer receiverId,
                                              @Param("offset") int offset,
                                              @Param("pageSize") int pageSize);

    int countByReceiverId(@Param("receiverId") Integer receiverId);

    int insert(SystemNotification notification);

    int markAsRead(@Param("id") Integer id);

    int markAllAsRead(@Param("receiverId") Integer receiverId);

    int countUnread(@Param("receiverId") Integer receiverId);

    int deleteById(@Param("id") Integer id);

    int deleteByContentLike(@Param("keyword") String keyword);
}
