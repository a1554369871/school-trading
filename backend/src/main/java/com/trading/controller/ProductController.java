package com.trading.controller;

import com.trading.common.PageResult;
import com.trading.common.Result;
import com.trading.dto.ProductDTO;
import com.trading.dto.ProductQuery;
import com.trading.entity.Product;
import com.trading.service.impl.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceImpl productService;

    /**
     * 首页商品列表浏览 — 支持按关键词、分类、价格区间、新旧程度组合筛选，分页展示
     * → ProductServiceImpl.search() → ProductMapper.xml: searchProducts (PageHelper分页)
     */
    @GetMapping
    public Result<PageResult<Product>> search(ProductQuery query) {
        return Result.success(productService.search(query));
    }

    /**
     * 商品关键词全文搜索 — 使用MySQL FULLTEXT索引，按相关度排序返回结果
     * → ProductServiceImpl.fullTextSearch() → ProductMapper.xml: countFullTextSearch + fullTextSearch
     */
    @GetMapping("/search")
    public Result<?> fullTextSearch(@RequestParam String keyword,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(productService.fullTextSearch(keyword, page, pageSize));
    }

    /**
     * 商品详情查看 — 展示商品图片、价格、描述、卖家信息、互动数据（点赞/收藏/想要），自动记录浏览量
     * → ProductServiceImpl.getDetail() → ProductMapper.xml: findById(含Redis缓存) + incrementViewCount
     *   → UserMapper.xml: findById(卖家信息) + InteractionMapper.xml: countByProduct + checkInteraction
     */
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Integer id,
                             Authentication auth) {
        Integer userId = auth != null ? (Integer) auth.getPrincipal() : null;
        return Result.success(productService.getDetail(id, userId));
    }

    /**
     * 发布商品 — 用户填写商品名称、图片、价格、分类、新旧程度、描述后提交发布
     * → ProductServiceImpl.create() → ProductMapper.xml: insert + Redis列表缓存清除
     */
    @PostMapping
    public Result<Product> create(Authentication auth,
                                  @Valid @RequestBody ProductDTO dto) {
        Integer userId = (Integer) auth.getPrincipal();
        return Result.success(productService.create(userId, dto));
    }

    /**
     * 编辑商品 — 商品所有者修改已发布商品的信息（名称、图片、价格、描述等）
     * → ProductServiceImpl.update() → ProductMapper.xml: findById + update + Redis缓存清除
     */
    @PutMapping("/{id}")
    public Result<Product> update(Authentication auth,
                                  @PathVariable Integer id,
                                  @Valid @RequestBody ProductDTO dto) {
        Integer userId = (Integer) auth.getPrincipal();
        return Result.success(productService.update(userId, id, dto));
    }

    /**
     * 删除商品 — 商品所有者下架/删除已发布的商品（逻辑删除，status设为0）
     * → ProductServiceImpl.delete() → ProductMapper.xml: findById + updateStatusWithVersion (逻辑删除status=0)
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(Authentication auth, @PathVariable Integer id) {
        Integer userId = (Integer) auth.getPrincipal();
        productService.delete(userId, id);
        return Result.success();
    }

    /**
     * 商品上架/下架 — 商品所有者切换商品的在售状态
     * → ProductServiceImpl.updateStatus() → ProductMapper.xml: findById + updateStatusWithVersion
     */
    @PutMapping("/{id}/status")
    public Result<?> updateStatus(Authentication auth,
                                  @PathVariable Integer id,
                                  @RequestBody Map<String, Object> body) {
        Integer userId = (Integer) auth.getPrincipal();
        Integer status = (Integer) body.get("status");
        if (status == null) {
            throw new com.trading.common.BusinessException("状态值不能为空");
        }
        productService.updateStatus(userId, id, status);
        return Result.success();
    }

    /**
     * 点赞/收藏/想要 — 商品详情页用户对商品进行互动操作（点击切换状态，已互动则取消）
     * → ProductServiceImpl.toggleInteraction() → InteractionMapper.xml: checkInteraction + deleteByUserAndProduct + insert
     */
    @PostMapping("/{id}/interact")
    public Result<?> interact(Authentication auth,
                              @PathVariable Integer id,
                              @RequestParam String type) {
        if (!java.util.Set.of("like", "favorite", "want").contains(type)) {
            throw new com.trading.common.BusinessException("无效的互动类型");
        }
        Integer userId = (Integer) auth.getPrincipal();
        productService.toggleInteraction(userId, id, type);
        return Result.success();
    }

    /**
     * 我的发布商品 — 个人中心查看当前用户已发布的所有商品列表，分页展示
     * → ProductServiceImpl.getMyProducts() → ProductMapper.xml: countMyProducts + getMyProducts
     */
    @GetMapping("/my")
    public Result<?> myProducts(Authentication auth,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int pageSize) {
        Integer userId = (Integer) auth.getPrincipal();
        return Result.success(productService.getMyProducts(userId, page, pageSize));
    }

    /**
     * 分类商品统计 — 首页左侧/顶部展示各分类的商品数量，用于分类导航
     * → ProductServiceImpl.getCategoryStats() → ProductMapper.xml: getCategoryStats + Redis缓存(stats:category, 5分钟)
     */
    @GetMapping("/stats/categories")
    public Result<?> categoryStats() {
        return Result.success(productService.getCategoryStats());
    }

    /**
     * 低于均价推荐 — 首页推荐板块展示价格低于同分类均价的划算商品
     * → ProductServiceImpl.getBelowAvgPrice() → ProductMapper.xml: countBelowAvgPriceProducts + findBelowAvgPriceProducts
     */
    @GetMapping("/stats/below-avg")
    public Result<?> belowAvgPrice(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(productService.getBelowAvgPrice(page, pageSize));
    }

    /**
     * 热门商品推荐 — 首页推荐板块展示互动量（点赞/收藏/想要）最高的商品
     * → ProductServiceImpl.getPopular() → ProductMapper.xml: findPopularProducts
     */
    @GetMapping("/stats/popular")
    public Result<?> popular(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(productService.getPopular(limit));
    }

    /**
     * 我的收藏列表 — 个人中心查看当前用户收藏的所有商品，分页展示
     * → ProductServiceImpl.getMyFavorites() → InteractionMapper.xml: findProductIdsByUserAndType + ProductMapper.xml: findByIds
     */
    @GetMapping("/favorites")
    public Result<?> myFavorites(Authentication auth,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int pageSize) {
        Integer userId = (Integer) auth.getPrincipal();
        return Result.success(productService.getMyFavorites(userId, page, pageSize));
    }

    /**
     * 检查收藏状态 — 商品详情页判断当前用户是否已收藏该商品，用于显示收藏按钮状态
     * → ProductServiceImpl.isFavorited() → InteractionMapper.xml: checkInteraction
     */
    @GetMapping("/{id}/favorite-status")
    public Result<?> favoriteStatus(Authentication auth, @PathVariable Integer id) {
        Integer userId = (Integer) auth.getPrincipal();
        boolean isFavorited = productService.isFavorited(userId, id);
        return Result.success(java.util.Map.of("favorited", isFavorited));
    }
}
