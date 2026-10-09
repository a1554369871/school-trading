package com.trading.service;

import com.trading.dto.*;
import com.trading.entity.User;

import java.util.List;

public interface UserService {
    User register(RegisterRequest request);
    String login(LoginRequest request);
    User getCurrentUser(Integer userId);
    User updateProfile(Integer userId, User user);
    void changePassword(Integer userId, ChangePasswordRequest request);
    List<UserStats> getUserTradeStats();
    List<UserStats> getActiveUsersAboveAvg();
    List<User> getAllUsers();
    User findById(Integer userId);
    void update(User user);
    void freezeUser(Integer userId);
    void unfreezeUser(Integer userId);
}
