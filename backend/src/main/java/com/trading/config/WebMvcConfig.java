package com.trading.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    private String resolvedAbsolutePath;

    @PostConstruct
    public void init() {
        // 解析为绝对路径，确保无论从哪个目录启动都能正确定位
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        File dir = path.toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        resolvedAbsolutePath = path.toUri().toString();
        System.out.println("[WebMvcConfig] 上传目录映射: " + resolvedAbsolutePath);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resolvedAbsolutePath)
                .setCachePeriod(3600);  // 浏览器缓存1小时
    }
}
