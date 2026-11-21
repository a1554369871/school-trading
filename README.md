# 校园二手交易平台

面向高校学生的二手商品交易平台：商品发布与浏览、下单交易、私信、评论、收藏、通知、后台管理。

前端是纯静态 Vue 3 应用，部署在 **GitHub Pages**；后端与数据库使用 **Supabase**
（Auth + Postgres + Row Level Security + Storage + RPC），无需自建服务器。

- 在线地址：`https://a1554369871.github.io/school-trading/`
- 部署步骤见 **[DEPLOY.md](./DEPLOY.md)**

## 技术栈

### 前端
- Vue 3.4 + Vite 5
- Pinia + Vue Router 4（hash 路由，适配 GitHub Pages 刷新）
- Element Plus + Tailwind CSS
- `@supabase/supabase-js`

### 后端（Supabase）
- Supabase Auth：用户名 + 密码登录（内部合成邮箱）
- Postgres + RLS：所有业务表启用行级安全
- RPC：订单状态流转、互动、统计等写操作
- Storage：商品图与头像，公共桶 `images`

> 仓库里的 `backend/` 是原 Spring Boot + MySQL + Redis 版本，仅作参考，不再参与部署。

## 功能模块

- **用户**：注册、登录、资料修改、修改密码、头像上传
- **商品**：发布、编辑、下架、搜索、分类/价格/成色筛选、详情与浏览统计
- **订单**：下单、支付、发货、确认收货、取消，买入/卖出订单
- **互动**：点赞、收藏、想要、商品评论与回复
- **私信**：会话列表、聊天记录、已读状态
- **通知**：系统通知、公告、未读数、标记已读
- **收货地址**：增删改查、默认地址
- **管理后台**：数据看板、用户（冻结/解冻/角色）、商品、订单、评论、公告、分类

## 项目结构

```
second-hand-trading/
├── supabase/
│   └── schema.sql              # 表、RLS、RPC、Storage、初始分类（一键执行）
├── frontend/
│   ├── src/
│   │   ├── api/                # 9 个接口模块，沿用旧 {code,data} 结构
│   │   ├── lib/supabase.js     # Supabase 客户端、合成邮箱、当前用户
│   │   ├── router/             # hash 路由 + 登录/管理员守卫
│   │   ├── store/user.js       # Session、用户资料、未读数
│   │   └── views/              # 页面（含 admin 后台）
│   ├── .env.example
│   └── vite.config.js
├── .github/workflows/
│   └── deploy-pages.yml        # GitHub Pages 自动构建部署
├── DEPLOY.md                   # 完整部署指南
└── backend/                    # 旧 Spring Boot 后端（不再部署）
```

## 本地运行

1. 按 [DEPLOY.md](./DEPLOY.md) 创建 Supabase 项目，并在 SQL Editor 执行
   `supabase/schema.sql`。
2. 在 **Authentication → Providers → Email** 关闭 `Confirm email`。
3. 配置环境变量：

   ```bash
   cd frontend
   cp .env.example .env.local
   ```

   ```env
   VITE_SUPABASE_URL=https://你的项目.supabase.co
   VITE_SUPABASE_ANON_KEY=你的 anon public key
   VITE_BASE=/
   ```

4. 启动：

   ```bash
   npm install
   npm run dev
   ```

   打开 <http://localhost:3000>。

5. 设置管理员（注册后执行）：

   ```sql
   update public.profiles
   set role = 1
   where lower(username) = lower('你的用户名');
   ```

## 部署

推送到 `main` 后，GitHub Actions 自动构建并发布到 Pages。首次需要在仓库
**Settings → Pages** 把 Source 设为 **GitHub Actions**，并在
**Settings → Secrets and variables → Actions → Variables** 配置：

| 变量 | 说明 |
| --- | --- |
| `VITE_SUPABASE_URL` | Supabase Project URL |
| `VITE_SUPABASE_ANON_KEY` | Supabase anon public key |

详细步骤、权限设计、常见问题见 **[DEPLOY.md](./DEPLOY.md)**。

## 说明

- 「支付」仅做订单状态流转，未接入真实支付渠道。
- 用户名登录使用 SHA-256 合成邮箱，**无法通过邮件找回密码**；忘记密码需在
  Supabase 控制台重置。
- `anon key` 会公开在浏览器中，安全性完全依赖 RLS；请勿把 `service_role` key 放入前端。
- 按约定不迁移旧 MySQL 数据；旧库用户需要重新注册。
