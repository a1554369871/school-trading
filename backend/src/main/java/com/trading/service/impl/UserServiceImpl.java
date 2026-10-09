package com.trading.service.impl;

import com.trading.common.BusinessException;
import com.trading.dto.*;
import com.trading.entity.User;
import com.trading.mapper.UserMapper;
import com.trading.security.JwtUtil;
import com.trading.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CACHE_KEY = "user:";
    private static final String USER_STATS_KEY = "stats:user_trade";

    @Override
    public User register(RegisterRequest request) {
        if (userMapper.findByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setNickname(request.getNickname());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(0);
        user.setStatus(1);
        userMapper.insert(user);
        return user;
    }

    @Override
    public String login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被冻结");
        }
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public User getCurrentUser(Integer userId) {
        try {
            String cacheKey = USER_CACHE_KEY + userId;
            User cached = (User) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis读取用户缓存失败: {}", e.getMessage());
        }
        User user = userMapper.findById(userId);
        if (user != null) {
            try {
                redisTemplate.opsForValue().set(USER_CACHE_KEY + userId, user, 10, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("Redis写入用户缓存失败: {}", e.getMessage());
            }
        }
        return user;
    }

    @Override
    public User updateProfile(Integer userId, User user) {
        user.setId(userId);
        userMapper.update(user);
        try {
            redisTemplate.delete(USER_CACHE_KEY + userId);
        } catch (Exception e) {
            log.warn("Redis删除用户缓存失败: {}", e.getMessage());
        }
        return userMapper.findById(userId);
    }

    @Override
    public void changePassword(Integer userId, ChangePasswordRequest request) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userMapper.update(user);
        try {
            redisTemplate.delete(USER_CACHE_KEY + userId);
        } catch (Exception e) {
            log.warn("Redis删除用户缓存失败: {}", e.getMessage());
        }
    }

    @Override
    public List<UserStats> getUserTradeStats() {
        try {
            @SuppressWarnings("unchecked")
            List<UserStats> cached = (List<UserStats>) (List<?>) redisTemplate.opsForValue().get(USER_STATS_KEY);
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis读取统计缓存失败: {}", e.getMessage());
        }
        List<UserStats> stats = userMapper.getUserTradeStats();
        try {
            redisTemplate.opsForValue().set(USER_STATS_KEY, stats, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis写入统计缓存失败: {}", e.getMessage());
        }
        return stats;
    }

    @Override
    public List<UserStats> getActiveUsersAboveAvg() {
        return userMapper.getActiveUsersAboveAvg();
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.getUserTradeStats().stream().map(s -> {
            User u = new User();
            u.setId(s.getUserId());
            u.setNickname(s.getNickname());
            return u;
        }).toList();
    }

    @Override
    public User findById(Integer userId) {
        return userMapper.findById(userId);
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
        try {
            redisTemplate.delete(USER_CACHE_KEY + user.getId());
        } catch (Exception e) {
            log.warn("Redis删除用户缓存失败: {}", e.getMessage());
        }
    }

    @Override
    public void freezeUser(Integer userId) {
        userMapper.updateStatus(userId, 0);
        try {
            redisTemplate.delete(USER_CACHE_KEY + userId);
        } catch (Exception e) {
            log.warn("Redis删除用户缓存失败: {}", e.getMessage());
        }
    }

    @Override
    public void unfreezeUser(Integer userId) {
        userMapper.updateStatus(userId, 1);
        try {
            redisTemplate.delete(USER_CACHE_KEY + userId);
        } catch (Exception e) {
            log.warn("Redis删除用户缓存失败: {}", e.getMessage());
        }
    }
}
