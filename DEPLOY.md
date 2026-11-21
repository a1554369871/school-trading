# 部署指南：GitHub Pages + Supabase

本项目已从「Spring Boot + MySQL + Redis」改写为「Vue 静态前端 + Supabase（Auth / Postgres / Storage / RPC）」。
GitHub Pages 只托管 `frontend/dist` 静态文件，所有数据与鉴权都在 Supabase。

- 前端地址：`https://a1554369871.github.io/school-trading/`
- Supabase 项目：需要你自己创建一个（免费版即可）
- 旧 `backend/` 目录保留作参考，不再参与部署

---

## 一、创建 Supabase 项目

1. 打开 <https://supabase.com/dashboard>，新建项目。
   - 区域建议选 `Southeast Asia (Singapore)`，国内访问相对稳定。
   - 记下数据库密码（本项目前端不直接连数据库，密码仅备用）。
2. 进入 **Project Settings → API**，记下：
   - `Project URL`，形如 `https://abcdefgh.supabase.co`
   - `anon public` key（很长的一串 JWT）
3. 进入 **SQL Editor**，把仓库根目录的 `supabase/schema.sql` **整段粘贴并执行**。
   - 成功后会创建 10 张表、RLS 策略、订单 RPC、存储桶 `images`，并写入 8 个初始分类。
   - 脚本可重复执行（表用 `if not exists`，函数用 `create or replace`）。
4. 进入 **Authentication → Providers → Email**：
   - **关闭 `Confirm email`**（必须）。本项目用「用户名 + 密码」登录，内部把用户名哈希成
     `u<sha256>@users.example.com` 作为 Supabase 邮箱，无法收验证邮件。
   - 其他保持默认即可。
5. 进入 **Authentication → URL Configuration**：
   - `Site URL` 填 `https://a1554369871.github.io/school-trading/`
   - `Redirect URLs` 可加同一个地址（本项目使用 hash 路由，正常登录不依赖跳转）。

> 免费版项目连续 7 天无请求会被暂停，进入控制台点一次 Restore 即可恢复。

---

## 二、本地运行

```bash
cd frontend
copy .env.example .env.local     # Windows
# cp .env.example .env.local      # macOS / Linux
```

编辑 `frontend/.env.local`：

```env
VITE_SUPABASE_URL=https://你的项目.supabase.co
VITE_SUPABASE_ANON_KEY=你的 anon public key
VITE_BASE=/
```

然后：

```bash
npm install
npm run dev
```

打开 <http://localhost:3000>，先注册一个账号，再登录验证。

### 设置管理员

注册后，在 Supabase SQL Editor 执行（把用户名换成你的）：

```sql
update public.profiles
set role = 1
where lower(username) = lower('你的用户名');
```

重新登录后，右上角会出现「管理后台」入口。

---

## 三、部署到 GitHub Pages

### 1. 推送代码

仓库名必须是 `school-trading`（工作流会用仓库名自动计算 `/school-trading/` 路径）：

```bash
git remote add school https://github.com/a1554369871/school-trading.git
git push school main
```

如果 `school` 已存在，用 `git remote set-url school ...`。

### 2. 打开 Pages

GitHub 仓库 → **Settings → Pages** → **Build and deployment → Source** 选择 **GitHub Actions**。

### 3. 配置 Supabase 变量

GitHub 仓库 → **Settings → Secrets and variables → Actions → Variables**，新建两个 **Repository variable**：

| 名称 | 值 |
| --- | --- |
| `VITE_SUPABASE_URL` | `https://你的项目.supabase.co` |
| `VITE_SUPABASE_ANON_KEY` | 你的 anon public key |

> 这两个值本来就会打包进前端，不是机密；**不要**把 `service_role` key 放进来。

### 4. 触发部署

推送 `main` 分支会自动运行 `.github/workflows/deploy-pages.yml`，也可以在 **Actions** 页手动
`Run workflow`。完成后访问：

```
https://a1554369871.github.io/school-trading/
```

工作流会：
1. `npm ci` + `npm run build`
2. 用 `VITE_BASE=/<仓库名>/` 构建（`vite.config.js` 读取该变量）
3. 上传 `frontend/dist` 并用 `actions/deploy-pages` 发布

### 5. 如果换了仓库名

- 工作流的 `VITE_BASE` 会自动跟着仓库名走，无需改代码。
- 需要同步修改 Supabase 的 `Site URL`，否则登录后的跳转地址可能不对。

---

## 四、目录说明

```
second-hand-trading/
├── supabase/
│   └── schema.sql              # 数据库、RLS、RPC、存储桶、初始分类
├── frontend/
│   ├── .env.example            # 本地环境变量模板
│   ├── src/lib/supabase.js     # Supabase 客户端 + 用户名合成邮箱
│   ├── src/api/                # 9 个模块，保持旧 {code,data} 返回结构
│   ├── src/store/user.js       # Supabase Session + 用户资料
│   ├── src/router/index.js     # hash 路由（Pages 刷新不 404）
│   └── vite.config.js          # base 来自 VITE_BASE
├── .github/workflows/
│   └── deploy-pages.yml        # Pages 自动部署
└── backend/                    # 旧 Spring Boot 后端（不再部署）
```

---

## 五、关键设计

### 登录

- 用户输入 `username + password`。
- 前端把 `username.trim().toLowerCase()` 做 SHA-256，取前 40 位生成
  `u<hex>@users.example.com`，调用 `supabase.auth.signInWithPassword`。
- 注册时 `signUp` 的 `user_metadata` 带 `username / nickname / phone / email`。
- 数据库触发器 `on_auth_user_created` 自动写入 `public.profiles`。
- 账号被冻结（`profiles.status = 0`）时，登录会被拒绝并立即登出。

### 订单

- `orders` 表**禁止前端直接 insert/update/delete**（已 `revoke`）。
- 下单、支付、发货、确认收货、取消订单全部走 `security definer` RPC：
  `create_order` / `pay_order` / `ship_order` / `confirm_order` / `cancel_order`。
- 下单时用 `select ... for update` 锁商品行，避免并发超卖；商品状态 1 → 3（预留）。
- 取消恢复为 1；确认收货后商品变为 2（已售出）。
- 每个状态变化都会写入 `notifications`。

### 图片

- 使用 Supabase Storage 公共桶 `images`。
- 上传路径 `images/<user_id>/<uuid>.<ext>`，RLS 允许登录用户上传，只有本人可改/删。
- 前端拿到 `getPublicUrl()` 后把完整 URL 存进 `product_image` / `avatar`。
- 旧数据里的 `/uploads/xxx` 相对路径不会自动迁移（按「全新开始」约定）。

### 权限

- 所有业务表启用 RLS。
- 商品：在售公开；本人、管理员、订单买卖双方、收藏者可见非在售商品。
- 订单：仅买卖双方与管理员可读。
- 私信：仅收发双方可读，接收方只能改 `is_read`。
- 地址、通知：仅本人。
- 管理员判定使用 `is_admin()`（`security definer`，避免 RLS 递归）。
- `profiles` 对匿名用户只开放 `id/username/nickname/avatar/role/status/register_time`，
  `phone/email` 仅本人通过 `get_my_profile()` RPC 读取。

---

## 六、常见问题

**页面空白 / 静态资源 404**
- 确认构建时 `VITE_BASE` 是 `/<仓库名>/`。
- 确认 Pages Source 是「GitHub Actions」，不是「Deploy from a branch」。

**注册后没有会话**
- Supabase 的 `Confirm email` 没关。关闭后重新注册（已注册的用户名会占用，可在
  Authentication → Users 删除后重试）。

**登录提示「用户名或密码错误」**
- 用户名大小写不敏感，但合成邮箱依赖哈希；确认注册和登录用的是同一个用户名。
- 老 MySQL 的用户密码无法迁移，需要重新注册。

**忘记密码**
- 合成邮箱收不到邮件，无法自助找回。可在 Supabase 控制台
  Authentication → Users 里给该用户设置新密码，或删除后重新注册。

**`new row violates row-level security policy`**
- 说明当前操作没有走对应的 RPC，或登录态已过期。刷新页面重新登录；订单相关操作必须走
  `pay_order` 等 RPC，不能直接更新 `orders`。

**管理后台 403 / 看不到入口**
- `profiles.role` 是否为 1；`status` 是否为 1；修改后重新登录刷新资料。

---

## 七、安全提醒

- `anon public key` 会公开在浏览器里，这是设计如此；真正兜底的是 RLS，**不要**在 RLS 不完整时上线。
- `service_role` key 拥有绕过 RLS 的权限，**永远不要**写进前端或 GitHub Variables。
- 旧 `backend/src/main/resources/application.yml` 里有明文 MySQL 密码，建议尽快修改数据库密码。
- 本项目为课程/演示用途，未接入支付渠道，「支付」只是状态流转。
