package com.trading.mapper;

import com.trading.entity.PrivateMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PrivateMessageMapper {

    int insert(PrivateMessage message);

    /**
     * JOIN 查询：获取两个用户之间的聊天记录
     */
    List<Map<String, Object>> findConversation(@Param("user1Id") Integer user1Id,
                                                @Param("user2Id") Integer user2Id,
                                                @Param("offset") int offset,
                                                @Param("pageSize") int pageSize);

    int countConversation(@Param("user1Id") Integer user1Id,
                          @Param("user2Id") Integer user2Id);

    /**
     * JOIN + GROUP BY：获取用户的最近聊天列表
     */
    List<Map<String, Object>> getRecentConversations(@Param("userId") Integer userId,
                                                      @Param("offset") int offset,
                                                      @Param("pageSize") int pageSize);

    int countRecentConversations(@Param("userId") Integer userId);

    /**
     * 标记与某用户的对话为已读
     */
    int markConversationAsRead(@Param("userId") Integer userId, @Param("otherId") Integer otherId);
}
