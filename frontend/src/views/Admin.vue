<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航 -->
    <header class="bg-white/5 backdrop-blur-lg border-b border-gray-200/60 sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center gap-6">
            <router-link to="/admin" class="font-bold text-lg text-gray-900">
              管理后台
            </router-link>
            <nav class="hidden md:flex items-center gap-1 bg-white/40 backdrop-blur-md border border-gray-200/50 rounded-full py-1 px-1 shadow-sm overflow-visible">
              <router-link
                v-for="item in navItems"
                :key="item.path"
                :to="item.path"
                class="admin-nav-link"
                :class="{ 'admin-nav-active': isNavActive(item) }"
              >
                {{ item.label }}
              </router-link>
            </nav>
          </div>
          <div class="flex items-center gap-3">
            <router-link to="/" class="admin-nav-link">
              返回前台
            </router-link>
            <button @click="handleLogout" class="admin-nav-link admin-nav-danger">
              退出登录
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 主内容 - 子路由渲染区域 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const navItems = [
  { path: '/admin', label: '数据概览', exact: true },
  { path: '/admin/users', label: '用户管理' },
  { path: '/admin/products', label: '商品管理' },
  { path: '/admin/orders', label: '订单管理' },
  { path: '/admin/comments', label: '评论管理' },
  { path: '/admin/announcements', label: '公告管理' },
  { path: '/admin/categories', label: '分类管理' },
]

function isNavActive(item) {
  if (item.exact) return route.path === item.path
  return route.path.startsWith(item.path)
}

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.admin-nav-link {
  position: relative;
  font-size: 0.875rem;
  color: rgba(17, 24, 39, 0.7);
  font-weight: 600;
  padding: 0.375rem 1rem;
  border-radius: 9999px;
  transition: color 0.2s, background-color 0.2s;
  text-decoration: none;
  outline: none;
  white-space: nowrap;
}

.admin-nav-link:hover {
  color: #111827;
}

.admin-nav-link:focus {
  outline: none;
  box-shadow: none;
}

.admin-nav-active {
  color: #111827;
  background-color: rgba(17, 24, 39, 0.05);
}

.admin-nav-link:hover::before {
  content: '';
  position: absolute;
  top: -6px;
  left: 50%;
  transform: translateX(-50%);
  width: 2rem;
  height: 3px;
  background: #111827;
  border-radius: 9999px 9999px 0 0;
  box-shadow:
    0 -2px 8px rgba(17, 24, 39, 0.2),
    0 -4px 16px rgba(17, 24, 39, 0.1);
}

.admin-nav-link:hover::after {
  content: '';
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 3rem;
  height: 8px;
  background: rgba(17, 24, 39, 0.15);
  border-radius: 9999px;
  filter: blur(6px);
}

.admin-nav-danger:hover {
  color: #ef4444;
  background-color: rgba(239, 68, 68, 0.05);
}

.admin-nav-danger:hover::before,
.admin-nav-danger:hover::after {
  display: none;
}
</style>
