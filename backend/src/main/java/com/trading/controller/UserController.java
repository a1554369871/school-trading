package com.trading.controller;

import com.trading.common.Result;
import com.trading.dto.ChangePasswordRequest;
import com.trading.dto.UploadResult;
import com.trading.entity.User;
import com.trading.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    /**
     * 获取当前登录用户信息 — 个人中心页面展示用户昵称、头像、手机号等基本信息
     * → UserServiceImpl.getCurrentUser() → UserMapper.xml: findById + Redis缓存(user:{id}, 10分钟)
     */
    @GetMapping("/me")
    public Result<?> getCurrentUser(Authentication auth) {
        Integer userId = (Integer) auth.getPrincipal();
        User user = userService.getCurrentUser(userId);
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 修改个人信息 — 个人中心页面修改昵称、手机号、邮箱等资料
     * → UserServiceImpl.updateProfile() → UserMapper.xml: update + Redis缓存清除
     */
    @PutMapping("/me")
    public Result<?> updateProfile(Authentication auth, @RequestBody User user) {
        Integer userId = (Integer) auth.getPrincipal();
        User updated = userService.updateProfile(userId, user);
        updated.setPassword(null);
        return Result.success(updated);
    }

    /**
     * 修改密码 — 个人中心页面验证原密码后设置新密码
     * → UserServiceImpl.changePassword() → UserMapper.xml: findById + update + Redis缓存清除
     */
    @PutMapping("/me/password")
    public Result<?> changePassword(Authentication auth,
                                    @Valid @RequestBody ChangePasswordRequest request) {
        Integer userId = (Integer) auth.getPrincipal();
        userService.changePassword(userId, request);
        return Result.success();
    }

    /**
     * 上传文件 — 发布商品/修改头像时上传图片，返回图片访问URL
     * 无Mapper调用，直接写入文件系统uploads目录，返回URL路径
     */
    @PostMapping("/upload")
    public Result<UploadResult> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 使用配置的上传目录，确保与 WebMvcConfig 一致
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            
            // 确保上传目录存在
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("创建上传目录: {}", uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path dest = uploadPath.resolve(filename);
            file.transferTo(dest.toFile());
            
            log.info("文件上传成功: {} -> {}", originalFilename, dest);

            // 返回访问路径
            String url = "/uploads/" + filename;
            return Result.success(new UploadResult(url, originalFilename, file.getSize()));
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传异常", e);
            return Result.error("文件上传异常: " + e.getMessage());
        }
    }
}
