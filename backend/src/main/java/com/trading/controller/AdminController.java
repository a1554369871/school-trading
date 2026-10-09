package com.trading.controller;

import com.trading.common.BusinessException;
import com.trading.common.PageResult;
import com.trading.common.Result;
import com.trading.dto.StatsResult;
import com.trading.entity.Announcement;
import com.trading.entity.User;
import com.trading.mapper.CommentMapper;
import com.trading.mapper.ProductMapper;
import com.trading.mapper.UserMapper;
import com.trading.service.impl.AnnouncementServiceImpl;
import com.trading.service.impl.OrderServiceImpl;
import com.trading.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserServiceImpl userService;
    private final OrderServiceImpl orderService;
    private final AnnouncementServiceImpl announcementService;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;
    private final CommentMapper commentMapper;

    /**
     * 数据总览 — 管理员后台首页展示总用户数、总商品数、总订单数、总评论数
     * → UserMapper.xml: getUserTradeStats + ProductMapper.xml: getCategoryStats
     *   → OrderServiceImpl.getMonthlyStats() → OrderMapper.xml: getMonthlyStats + Redis缓存
     *   → CommentMapper.xml: countAll
     */
    @GetMapping("/stats/overview")
    public Result<StatsResult> overview() {
        StatsResult stats = new StatsResult();
        stats.setTotalUsers((long) userMapper.getUserTradeStats().size());
        stats.setTotalProducts(productMapper.getCategoryStats().stream()
                .mapToLong(s -> s.getProductCount()).sum());
        stats.setTotalOrders(orderService.getMonthlyStats().stream()
                .mapToLong(s -> ((Number) s.get("orderCount")).longValue()).sum());
        stats.setTotalComments((long) commentMapper.countAll());
        return Result.success(stats);
    }

    /**
     * 用户交易统计 — 管理员后台查看每个用户的发布数、已售数、买入数、收入、支出等交易明细
     * → UserMapper.xml: getUserTradeStats + Redis缓存(stats:user_trade, 5分钟)
     */
    @GetMapping("/stats/user-trade")
    public Result<?> userTradeStats() {
        return Result.success(userMapper.getUserTradeStats());
    }

    /**
     * 活跃用户统计 — 管理员后台查看发布商品数高于平均水平的活跃用户
     * → UserMapper.xml: getActiveUsersAboveAvg
     */
    @GetMapping("/stats/active-users")
    public Result<?> activeUsers() {
        return Result.success(userMapper.getActiveUsersAboveAvg());
    }

    /**
     * 月度订单趋势 — 管理员后台统计每月的订单数量和交易金额变化趋势
     * → OrderServiceImpl.getMonthlyStats() → OrderMapper.xml: getMonthlyStats + Redis缓存(stats:monthly, 5分钟)
     */
    @GetMapping("/stats/monthly")
    public Result<?> monthlyStats() {
        return Result.success(orderService.getMonthlyStats());
    }

    /**
     * 卖家收入排行 — 管理员后台统计各卖家的累计销售收入排行
     * → OrderServiceImpl.getSellerRevenue() → OrderMapper.xml: getSellerRevenue + Redis缓存(stats:seller_revenue, 5分钟)
     */
    @GetMapping("/stats/seller-revenue")
    public Result<?> sellerRevenue() {
        return Result.success(orderService.getSellerRevenue());
    }

    /**
     * 所有订单管理 — 管理员后台查看平台所有订单列表，分页展示
     * → OrderServiceImpl.getAllOrders() → OrderMapper.xml: countAll + findAll
     */
    @GetMapping("/orders")
    public Result<?> allOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.getAllOrders(page, pageSize));
    }

    /**
     * 用户管理 — 管理员后台分页查看所有用户列表，支持按用户名/昵称搜索
     * → UserMapper.xml: countPage + findPage (SQL级分页)
     */
    @GetMapping("/users")
    public Result<?> listUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        int offset = (page - 1) * pageSize;
        int total = userMapper.countPage(keyword.isEmpty() ? null : keyword);
        List<User> users = userMapper.findPage(keyword.isEmpty() ? null : keyword, offset, pageSize);
        return Result.success(PageResult.of(users, (long) total, page, pageSize));
    }

    /**
     * 冻结用户 — 管理员冻结违规用户的账号，冻结后该用户无法登录
     * → UserServiceImpl.freezeUser() → UserMapper.xml: updateStatus
     */
    @PutMapping("/users/{id}/freeze")
    public Result<?> freezeUser(@PathVariable Integer id) {
        userService.freezeUser(id);
        return Result.success();
    }

    /**
     * 解冻用户 — 管理员解除冻结用户的账号限制，恢复其正常登录能力
     * → UserServiceImpl.unfreezeUser() → UserMapper.xml: updateStatus
     */
    @PutMapping("/users/{id}/unfreeze")
    public Result<?> unfreezeUser(@PathVariable Integer id) {
        userService.unfreezeUser(id);
        return Result.success();
    }

    /**
     * 公告列表管理 — 管理员后台查看所有公告列表，支持分页展示
     * → AnnouncementServiceImpl.getAll() → AnnouncementMapper.xml: countAll + findAll
     */
    @GetMapping("/announcements")
    public Result<?> listAnnouncements(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(announcementService.getAll(page, pageSize));
    }

    /**
     * 发布公告 — 管理员发布系统公告，同时自动向所有用户发送系统通知
     * → AnnouncementServiceImpl.create() → AnnouncementMapper.xml: insert + UserMapper.xml: findAll
     *   → MessageServiceImpl.sendNotification() → SystemNotificationMapper.xml: insert(群发所有用户)
     */
    @PostMapping("/announcements")
    public Result<?> createAnnouncement(Authentication auth, @RequestBody Announcement announcement) {
        Integer userId = (Integer) auth.getPrincipal();
        announcementService.create(userId, announcement);
        return Result.success();
    }

    /**
     * 编辑公告 — 管理员修改已发布公告的标题和内容
     * → AnnouncementServiceImpl.update() → AnnouncementMapper.xml: findById + update
     */
    @PutMapping("/announcements/{id}")
    public Result<?> updateAnnouncement(@PathVariable Integer id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcementService.update(announcement);
        return Result.success();
    }

    /**
     * 删除公告 — 管理员删除公告，同时清除该公告关联的系统通知
     * → AnnouncementServiceImpl.delete() → AnnouncementMapper.xml: findById + deleteById
     *   → MessageServiceImpl.deleteNotificationsByContent() → SystemNotificationMapper.xml: deleteByContentLike
     */
    @DeleteMapping("/announcements/{id}")
    public Result<?> deleteAnnouncement(@PathVariable Integer id) {
        announcementService.delete(id);
        return Result.success();
    }

    /**
     * 修改用户角色 — 管理员将普通用户提升为管理员，或将管理员降为普通用户
     * → UserServiceImpl.findById() → UserMapper.xml: findById + UserServiceImpl.update() → UserMapper.xml: update
     */
    @PutMapping("/users/{id}/role")
    public Result<?> updateUserRole(@PathVariable Integer id, @RequestBody java.util.Map<String, Object> body) {
        Integer role = (Integer) body.get("role");
        if (role == null || (role != 0 && role != 1)) {
            throw new BusinessException("角色值无效，0=普通用户，1=管理员");
        }
        User user = userService.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setRole(role);
        userService.update(user);
        return Result.success();
    }

    /**
     * 评论管理 — 管理员后台查看平台所有评论列表，支持分页，可删除违规评论
     * → CommentMapper.xml: countAllPage + findAllPage
     */
    @GetMapping("/comments")
    public Result<?> listComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        int offset = (page - 1) * pageSize;
        int total = commentMapper.countAllPage();
        List<Map<String, Object>> comments = commentMapper.findAllPage(offset, pageSize);
        return Result.success(PageResult.of(comments, (long) total, page, pageSize));
    }

    /**
     * 删除评论 — 管理员直接删除违规或不当评论，无需校验评论作者
     * → CommentMapper.xml: deleteById
     */
    @DeleteMapping("/comments/{id}")
    public Result<?> deleteComment(@PathVariable Integer id) {
        commentMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 管理员商品上下架 — 管理员强制修改商品的在售状态（上架/下架），使用乐观锁防止并发冲突
     * → ProductMapper.xml: findById + updateStatusWithVersion
     */
    @PutMapping("/products/{id}/status")
    public Result<?> updateProductStatus(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Integer status = (Integer) body.get("status");
        if (status == null) {
            throw new BusinessException("状态值不能为空");
        }
        com.trading.entity.Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        productMapper.updateStatusWithVersion(id, status, product.getVersion());
        return Result.success();
    }

    /**
     * 删除商品 — 管理员强制下架违规商品（逻辑删除，将status设为0），使用乐观锁防止并发冲突
     * → ProductMapper.xml: findById + updateStatusWithVersion(逻辑删除status=0)
     */
    @DeleteMapping("/products/{id}")
    public Result<?> deleteProduct(@PathVariable Integer id) {
        com.trading.entity.Product product = productMapper.findById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        productMapper.updateStatusWithVersion(id, 0, product.getVersion());
        return Result.success();
    }
}
