package com.trading.mapper;

import com.trading.entity.User;
import com.trading.dto.UserStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);

    User findById(@Param("id") Integer id);

    int insert(User user);

    int update(User user);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    /**
     * 分页查询用户列表（SQL级别分页，替代Java内存分页）
     */
    List<User> findPage(@Param("keyword") String keyword,
                        @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

    /**
     * 统计用户总数（支持关键词筛选）
     */
    int countPage(@Param("keyword") String keyword);

    /**
     * 兼容旧版：查询所有用户（含关键词筛选）
     */
    List<User> findAll(@Param("keyword") String keyword);

    int countAll(@Param("keyword") String keyword);

    List<UserStats> getUserTradeStats();

    List<UserStats> getActiveUsersAboveAvg();
}
