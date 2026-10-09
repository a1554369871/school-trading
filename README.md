# 校园二手交易平台

一个面向高校学生的二手商品交易平台，采用前后端分离架构，支持商品发布与浏览、下单交易、私信沟通、评论互动、收藏、通知以及后台管理等功能。

## 技术栈

### 后端
- **框架**：Spring Boot 3.2.5（Java 17）
- **持久层**：MyBatis 3.0.3 + MySQL，分页使用 PageHelper
- **数据库连接池**：HikariCP
- **缓存**：Redis（Lettuce 连接池）+ Spring Cache
- **安全**：Spring Security + JWT（jjwt 0.12.5）
- **其他**：Lombok、Spring Validation、Spring Boot Actuator

### 前端
- **框架**：Vue 3.4
- **构建工具**：Vite 5
- **状态管理**：Pinia
- **路由**：Vue Router 4
- **UI 组件**：Element Plus + Tailwind CSS
- **HTTP 请求**：Axios

## 功能模块

- **用户**：注册、登录（JWT 鉴权）、个人信息修改、修改密码、头像上传
- **商品**：发布、编辑、下架、搜索、分类筛选、商品详情与统计
- **订单**：下单、支付、发货、确认收货、取消订单，查看买入/卖出订单
- **互动**：收藏、点赞等互动，商品评论
- **私信**：用户间私聊、会话列表、消息已读
- **通知**：系统通知与公告，未读数量、标记已读
- **收货地址**：地址的增删改查
- **管理后台**：数据看板、用户管理（冻结/解冻/角色）、商品管理、订单管理、评论管理、公告管理、分类管理

## 项目结构

```
second-hand-trading/
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/com/trading/
│   │   ├── common/          # 通用响应、异常处理、分页
│   │   ├── config/          # 安全、跨域、Redis、MVC 配置
│   │   ├── controller/      # REST 接口层
│   │   ├── dto/             # 请求/响应对象
│   │   ├── entity/          # 实体类
│   │   ├── mapper/          # MyBatis Mapper 接口
│   │   ├── security/        # JWT 过滤器与工具类
│   │   └── service/         # 业务逻辑层
│   ├── src/main/resources/
│   │   ├── mapper/          # MyBatis XML 映射文件
│   │   └── application.yml  # 应用配置
│   └── pom.xml
└── frontend/                # Vue 3 前端
    ├── src/
    │   ├── api/             # 接口封装
    │   ├── components/      # 通用组件
    │   ├── layouts/         # 布局组件
    │   ├── router/          # 路由配置
    │   ├── store/           # Pinia 状态
    │   ├── utils/           # 请求工具
    │   └── views/           # 页面（含 admin 后台）
    ├── vite.config.js
    └── package.json
```

## 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+

## 快速开始

### 1. 准备数据库
创建数据库 `second_hand_trading`，并导入表结构（需自行准备 SQL 脚本）。

### 2. 启动后端
在 `backend/src/main/resources/application.yml` 中配置数据库与 Redis 连接信息，然后执行：

```bash
cd backend
mvn spring-boot:run
```

后端默认运行在 `http://localhost:8080`。

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 `http://localhost:3000`，开发环境下 `/api` 与 `/uploads` 已通过 Vite 代理转发到后端。

### 4. 构建前端

```bash
cd frontend
npm run build
```

## 接口约定

- 所有接口以 `/api` 为前缀，例如 `/api/products`、`/api/orders`、`/api/auth/login`。
- 除注册、登录等公开接口外，均需在请求头中携带 JWT：`Authorization: Bearer <token>`。
- 管理后台接口位于 `/api/admin/**`，需要管理员角色（`role = 1`）。

## 主要接口模块

| 模块 | 路径前缀 | 说明 |
| --- | --- | --- |
| 认证 | `/api/auth` | 注册、登录 |
| 用户 | `/api/users` | 个人信息、密码、上传 |
| 商品 | `/api/products` | 商品增删改查、搜索、互动、统计 |
| 分类 | `/api/categories` | 分类管理 |
| 订单 | `/api/orders` | 下单与订单流转 |
| 评论 | `/api/comments` | 商品评论 |
| 私信 | `/api/messages` | 会话与消息 |
| 通知 | `/api/notifications` | 系统通知 |
| 地址 | `/api/addresses` | 收货地址 |
| 管理 | `/api/admin` | 后台统计与管理 |
