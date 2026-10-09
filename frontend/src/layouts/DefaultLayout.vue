<template>
  <div class="min-h-screen">
    <!-- Navigation -->
    <nav class="bg-white border-b border-gray-200 shadow-sm">
      <div class="container-custom">
        <div class="flex items-center justify-between h-16">
          <div class="flex items-center gap-2">
            <router-link
              to="/"
              class="nav-flow-btn"
              :class="{ 'nav-flow-active': $route.path === '/' }"
            >
              <span class="nav-flow-text">首页</span>
              <span class="nav-flow-circle"></span>
            </router-link>
            <router-link
              to="/products"
              class="nav-flow-btn"
            >
              <span class="nav-flow-text">浏览商品</span>
              <span class="nav-flow-circle"></span>
            </router-link>
            <router-link
              to="/publish"
              class="nav-flow-btn"
            >
              <span class="nav-flow-text">发布商品</span>
              <span class="nav-flow-circle"></span>
            </router-link>
          </div>
          <div class="flex items-center gap-4">
            <template v-if="userStore.user">
              <router-link
                to="/notifications"
                class="relative text-gray-700 hover:text-gray-900"
              >
                <svg
                  class="w-6 h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
                  />
                </svg>
                <span
                  v-if="unreadCount > 0"
                  class="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full flex items-center justify-center"
                >
                  <span class="text-white text-xs">{{
                    unreadCount > 99 ? "99+" : unreadCount
                  }}</span>
                </span>
              </router-link>
              <div class="relative" ref="userMenuRef">
                <button
                  @click="showUserMenu = !showUserMenu"
                  class="flex items-center gap-2 text-gray-600 hover:text-gray-900"
                >
                  <img
                    v-if="userStore.user.avatar"
                    :src="userStore.user.avatar"
                    class="w-8 h-8 rounded-full object-cover"
                  />
                  <div
                    v-else
                    class="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center"
                  >
                    <span class="text-sm font-medium text-primary-600">{{
                      userStore.user.nickname?.charAt(0) || "用"
                    }}</span>
                  </div>
                  <svg
                    class="w-4 h-4"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M19 9l-7 7-7-7"
                    />
                  </svg>
                </button>
                <transition
                  enter-active-class="transition ease-out duration-100"
                  enter-from-class="transform opacity-0 scale-95"
                  enter-to-class="transform opacity-100 scale-100"
                  leave-active-class="transition ease-in duration-75"
                  leave-from-class="transform opacity-100 scale-100"
                  leave-to-class="transform opacity-0 scale-95"
                >
                  <div
                    v-if="showUserMenu"
                    class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-[9999]"
                  >
                    <div class="px-4 py-2 border-b border-gray-100">
                      <p class="text-sm font-medium text-gray-900">
                        {{ userStore.user.nickname }}
                      </p>
                      <p class="text-xs text-gray-500">
                        {{ userStore.user.email }}
                      </p>
                    </div>
                    <router-link
                      to="/user/profile"
                      class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                      @click="showUserMenu = false"
                      >个人中心</router-link
                    >
                    <router-link
                      to="/user/products"
                      class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                      @click="showUserMenu = false"
                      >我的发布</router-link
                    >
                    <router-link
                      to="/orders/buy"
                      class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                      @click="showUserMenu = false"
                      >我的订单</router-link
                    >
                    <router-link
                      to="/user/favorites"
                      class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                      @click="showUserMenu = false"
                      >我的收藏</router-link
                    >
                    <router-link
                      to="/chat"
                      class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                      @click="showUserMenu = false"
                      >我的私聊</router-link
                    >
                    <router-link
                      to="/orders/sell"
                      class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                      @click="showUserMenu = false"
                      >卖出订单</router-link
                    >
                    <router-link
                      v-if="userStore.isAdmin"
                      to="/admin"
                      class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                      @click="showUserMenu = false"
                      >管理后台</router-link
                    >
                    <button
                      @click="handleLogout"
                      class="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                    >
                      退出登录
                    </button>
                  </div>
                </transition>
              </div>
            </template>
            <template v-else>
              <router-link
                to="/login"
                class="text-gray-600 hover:text-gray-900 text-sm"
                >登录</router-link
              >
              <router-link
                to="/register"
                class="bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 rounded-lg text-sm"
                >注册</router-link
              >
            </template>
          </div>
        </div>
      </div>
    </nav>

    <main>
      <router-view />
    </main>

    <StickyFooter />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import StickyFooter from "@/components/StickyFooter.vue";

const router = useRouter();
const userStore = useUserStore();
const showUserMenu = ref(false);
const userMenuRef = ref(null);
const unreadCount = computed(() => userStore.unreadCount);

function handleLogout() {
  userStore.logout();
  showUserMenu.value = false;
  router.push("/");
}

function handleClickOutside(event) {
  if (userMenuRef.value && !userMenuRef.value.contains(event.target)) {
    showUserMenu.value = false;
  }
}

let unreadTimer = null;

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
  userStore.fetchUnreadCount();
  unreadTimer = setInterval(() => userStore.fetchUnreadCount(), 60000);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
  if (unreadTimer) clearInterval(unreadTimer);
});
</script>

<style scoped>
.nav-flow-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  overflow: hidden;
  border-radius: 100px;
  border: 1.5px solid rgba(51, 51, 51, 0.3);
  background: transparent;
  padding: 6px 16px;
  font-size: 13px;
  font-weight: 600;
  color: #111111;
  cursor: pointer;
  transition: all 600ms cubic-bezier(0.23, 1, 0.32, 1);
  text-decoration: none;
  white-space: nowrap;
}

.nav-flow-btn:hover {
  border-color: transparent;
  color: white;
  border-radius: 10px;
}

.nav-flow-btn:active {
  transform: scale(0.95);
}

.nav-flow-active {
  border-color: transparent;
  color: white;
  background: #18181B;
  border-radius: 10px;
}

.nav-flow-active .nav-flow-circle {
  width: 180px;
  height: 180px;
  opacity: 1;
  background: #18181B;
}

.nav-flow-text {
  position: relative;
  z-index: 1;
  transition: all 600ms ease-out;
}

.nav-flow-circle {
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

.nav-flow-btn:hover .nav-flow-circle {
  width: 180px;
  height: 180px;
  opacity: 1;
}
</style>
