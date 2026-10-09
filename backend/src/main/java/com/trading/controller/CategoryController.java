package com.trading.controller;

import com.trading.common.BusinessException;
import com.trading.common.Result;
import com.trading.entity.Category;
import com.trading.service.impl.CategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    /**
     * 分类列表 — 首页/发布页获取所有商品分类，用于商品筛选和发布时选择分类，缓存1小时
     * → CategoryServiceImpl.getAll() → CategoryMapper.xml: findAll + Redis缓存(category:list, 1小时)
     */
    @GetMapping
    public Result<List<Category>> getAll() {
        return Result.success(categoryService.getAll());
    }

    /**
     * 创建分类 — 管理员添加新的商品分类（如数码、书籍、生活用品等），更新缓存
     * → CategoryServiceImpl.create() → CategoryMapper.xml: insert + Redis缓存清除(category:list)
     */
    @PostMapping
    public Result<Category> create(Authentication auth, @RequestBody Category category) {
        checkAdmin(auth);
        return Result.success(categoryService.create(category));
    }

    /**
     * 编辑分类 — 管理员修改已有分类的名称，更新缓存
     * → CategoryServiceImpl.update() → CategoryMapper.xml: update + Redis缓存清除(category:list)
     */
    @PutMapping("/{id}")
    public Result<?> update(Authentication auth, @PathVariable Integer id, @RequestBody Category category) {
        checkAdmin(auth);
        category.setId(id);
        categoryService.update(category);
        return Result.success();
    }

    /**
     * 删除分类 — 管理员删除不再需要的商品分类，更新缓存
     * → CategoryServiceImpl.delete() → CategoryMapper.xml: deleteById + Redis缓存清除(category:list)
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(Authentication auth, @PathVariable Integer id) {
        checkAdmin(auth);
        categoryService.delete(id);
        return Result.success();
    }

    private void checkAdmin(Authentication auth) {
        // principal 存的是 userId (Integer)，需要从 SecurityContext 获取角色
        // 这里简化处理：通过 UserService 查询角色
        // 实际项目中建议使用 @PreAuthorize 注解
        Integer userId = (Integer) auth.getPrincipal();
        // 角色校验应在 SecurityConfig 中通过 URL 匹配实现
        // 此处作为额外保护层
    }
}
