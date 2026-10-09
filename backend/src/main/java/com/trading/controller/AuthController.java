package com.trading.controller;

import com.trading.common.Result;
import com.trading.dto.LoginRequest;
import com.trading.dto.RegisterRequest;
import com.trading.entity.User;
import com.trading.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserServiceImpl userService;

    /**
     * 用户注册 — 新用户填写用户名、昵称、密码等信息完成注册
     * → UserServiceImpl.register() → UserMapper.xml: findByUsername(检查重复) + insert(插入新用户)
     */
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 用户登录 — 用户通过用户名和密码登录，成功后返回JWT Token用于后续鉴权
     * → UserServiceImpl.login() → UserMapper.xml: findByUsername(验证用户名和密码)
     */
    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        return Result.success(Map.of("token", token));
    }
}
