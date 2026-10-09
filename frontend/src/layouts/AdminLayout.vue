<template>
  <div class="min-h-screen bg-[#FAFAFA]">
    <!-- 顶部导航 -->
    <header class="bg-white border-b border-gray-200/80 shadow-sm sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center gap-8">
            <router-link to="/admin" class="font-bold text-lg text-[#09090B] tracking-tight">
              管理后台
            </router-link>
            <nav class="hidden md:flex items-center gap-1.5">
              <router-link
                v-for="item in navItems"
                :key="item.path"
                :to="item.path"
                class="admin-flow-btn"
                :class="{ 'admin-flow-active': isActive(item.path) }"
              >
                <span class="admin-flow-icon" v-html="item.icon"></span>
                <span class="admin-flow-text">{{ item.label }}</span>
                <span class="admin-flow-circle"></span>
              </router-link>
            </nav>
          </div>
          <div class="flex items-center gap-3">
            <router-link to="/" class="admin-flow-btn">
              <svg class="admin-flow-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/></svg>
              <span class="admin-flow-text">返回前台</span>
              <span class="admin-flow-circle"></span>
            </router-link>
            <button @click="handleLogout" class="admin-flow-btn admin-flow-danger">
              <svg class="admin-flow-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/></svg>
              <span class="admin-flow-text">退出</span>
              <span class="admin-flow-circle admin-flow-circle-red"></span>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 主内容 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 fade-in">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const navItems = [
  { path: '/admin', label: '数据概览', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/></svg>' },
  { path: '/admin/users', label: '用户管理', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"/></svg>' },
  { path: '/admin/products', label: '商品管理', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg>' },
  { path: '/admin/orders', label: '订单管理', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/></svg>' },
  { path: '/admin/comments', label: '评论管理', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/></svg>' },
  { path: '/admin/announcements', label: '公告管理', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5.882V19.24a1.76 1.76 0 01-3.417.592l-2.147-6.15M18 13a3 3 0 100-6M5.436 13.683A4.001 4.001 0 017 6h1.832c4.1 0 7.625-1.234 9.168-3v14c-1.543-1.766-5.067-3-9.168-3H7a3.988 3.988 0 01-1.564-.317z"/></svg>' },
  { path: '/admin/categories', label: '分类管理', icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z"/></svg>' },
]

function isActive(path) {
  if (path === '/admin') return route.path === '/admin'
  return route.path.startsWith(path)
}

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.admin-flow-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  overflow: hidden;
  border-radius: 100px;
  border: 1.5px solid rgba(51, 51, 51, 0.3);
  background: transparent;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 600;
  color: #111111;
  cursor: pointer;
  transition: all 600ms cubic-bezier(0.23, 1, 0.32, 1);
  text-decoration: none;
  white-space: nowrap;
}

.admin-flow-btn:hover {
  border-color: transparent;
  color: white;
  border-radius: 10px;
}

.admin-flow-btn:active {
  transform: scale(0.95);
}

/* 选中态：黑色背景白色文字 */
.admin-flow-active {
  border-color: transparent;
  color: white;
  background: #18181B;
  border-radius: 10px;
}

.admin-flow-active .admin-flow-icon {
  stroke: white;
}

.admin-flow-icon {
  width: 14px;
  height: 14px;
  stroke: #111111;
  fill: none;
  flex-shrink: 0;
  transition: all 600ms cubic-bezier(0.23, 1, 0.32, 1);
  z-index: 2;
  position: relative;
}

.admin-flow-btn:hover .admin-flow-icon {
  stroke: white;
}

.admin-flow-text {
  position: relative;
  z-index: 1;
  transition: all 600ms ease-out;
}

.admin-flow-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 10px;
  height: 10px;
  background: #111111;
  border-radius: 50%;
  opacity: 0;
  transition: all 800ms cubic-bezier(0.19, 1, 0.22, 1);
}

.admin-flow-btn:hover .admin-flow-circle {
  width: 180px;
  height: 180px;
  opacity: 1;
}

.admin-flow-active .admin-flow-circle {
  width: 180px;
  height: 180px;
  opacity: 1;
  background: #18181B;
}

.admin-flow-circle-red {
  background: #dc2626 !important;
}

.admin-flow-danger:hover .admin-flow-circle-red {
  background: #dc2626;
}

/* 退出按钮 hover 态 */
.admin-flow-danger:hover {
  color: white;
}

.fade-in {
  animation: pageFadeIn 0.5s cubic-bezier(0.23, 1, 0.32, 1);
}

@keyframes pageFadeIn {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
