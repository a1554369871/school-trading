<template>
  <div class="min-h-screen bg-white">
    <nav class="absolute top-0 left-0 right-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <div class="flex items-center gap-6">
            <router-link to="/" class="text-white/90 hover:text-white text-sm"
              >首页</router-link
            >
            <router-link
              to="/products"
              class="text-white/90 hover:text-white text-sm"
              >浏览商品</router-link
            >
            <router-link
              to="/publish"
              class="text-white/90 hover:text-white text-sm"
              >发布商品</router-link
            >
          </div>
          <div class="flex items-center gap-4">
            <template v-if="userStore.user">
              <router-link
                to="/notifications"
                class="text-white/90 hover:text-white"
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
              </router-link>
              <div class="relative user-menu-wrapper">
                <button
                  @click="showUserMenu = !showUserMenu"
                  class="flex items-center gap-2 text-white/90 hover:text-white"
                >
                  <div
                    class="w-8 h-8 bg-white/20 rounded-full flex items-center justify-center overflow-hidden"
                  >
                    <img v-if="userStore.user.avatar" :src="userStore.user.avatar" class="w-full h-full object-cover" />
                    <span v-else class="text-sm font-medium">{{ userStore.user.nickname?.charAt(0) || "用" }}</span>
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
                    class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-50"
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
                      class="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                      @click="handleLogout"
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
                class="text-white/90 hover:text-white text-sm"
                >登录</router-link
              >
              <router-link
                to="/register"
                class="bg-white/20 hover:bg-white/30 text-white px-4 py-2 rounded-lg text-sm"
                >注册</router-link
              >
            </template>
          </div>
        </div>
      </div>
    </nav>

    <section
      class="relative h-[70vh] min-h-[500px] flex items-center z-20"
    >
      <div class="absolute inset-0 overflow-hidden">
        <img
          src="/hero.webp"
          alt="Campus"
          class="w-full h-full object-cover"
        />
        <div
          class="absolute inset-0 bg-gradient-to-b from-black/40 via-black/20 to-black/60"
        ></div>
      </div>
      <div class="relative z-10 w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1
          class="text-4xl md:text-6xl font-bold text-white mb-10 font-display leading-tight"
        >
          Second Hand Trading<br />Platform
        </h1>
        <!-- OTA 风格搜索栏 -->
        <div
          class="relative z-10 bg-white/15 backdrop-blur-xl rounded-2xl px-3 py-1.5 flex flex-col sm:flex-row items-stretch gap-1.5 max-w-5xl shadow-2xl"
        >
          <!-- 搜索输入 -->
          <div class="flex-1 flex items-center gap-3 px-4 py-1">
            <svg
              class="w-5 h-5 text-white/70 shrink-0"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索你想要的宝贝..."
              class="w-full outline-none text-white placeholder-white/50 bg-transparent text-sm"
              @keyup.enter="handleSearch"
            />
          </div>
          <!-- 分类选择 -->
          <div
            class="relative category-dropdown-wrapper shrink-0 flex items-center px-2 py-0.5"
          >
            <button
              @click="showCategoryDropdown = !showCategoryDropdown"
              class="flex items-center gap-2 text-white/90 hover:text-white text-sm px-3 py-1.5 rounded-lg hover:bg-white/10 transition-colors whitespace-nowrap"
            >
              <svg
                class="w-4 h-4 text-white/60"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M4 6h16M4 12h16M4 18h16"
                />
              </svg>
              {{ selectedCategoryLabel }}
              <svg
                class="w-3 h-3 text-white/50 transition-transform"
                :class="{ 'rotate-180': showCategoryDropdown }"
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
            <div
              v-if="showCategoryDropdown"
              class="absolute top-full left-0 mt-2 w-40 rounded-xl bg-white/15 backdrop-blur-xl border border-white/20 shadow-2xl z-50 max-h-60 overflow-y-auto"
            >
              <button
                class="w-full text-left px-4 py-2.5 text-sm text-white/90 hover:bg-white/20 transition-colors first:rounded-t-xl"
                :class="{
                  'text-white bg-white/20 font-medium': selectedCategory === '',
                }"
                @click="selectCategory('')"
              >
                全部分类
              </button>
              <button
                v-for="cat in displayCategories.slice(1)"
                :key="cat.id"
                class="w-full text-left px-4 py-2.5 text-sm text-white/90 hover:bg-white/20 transition-colors last:rounded-b-xl"
                :class="{
                  'text-white bg-white/20 font-medium':
                    selectedCategory === cat.id,
                }"
                @click="selectCategory(cat.id)"
              >
                {{ cat.categoryName || cat.name }}
              </button>
            </div>
          </div>
          <!-- 价格区间 -->
          <div class="flex items-center gap-2 px-3 py-1 shrink-0">
            <svg
              class="w-4 h-4 text-white/60 shrink-0"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
            <input
              v-model="minPrice"
              type="number"
              placeholder="¥最低"
              class="w-20 outline-none text-white placeholder-white/50 bg-white/10 rounded-lg px-2 py-1 text-sm border border-white/10"
            />
            <span class="text-white/40">-</span>
            <input
              v-model="maxPrice"
              type="number"
              placeholder="¥最高"
              class="w-20 outline-none text-white placeholder-white/50 bg-white/10 rounded-lg px-2 py-1 text-sm border border-white/10"
            />
          </div>
          <!-- 搜索按钮 -->
          <button
            @click="handleSearch"
            class="shrink-0 bg-white/80 text-blue-600 font-semibold px-10 py-1 rounded-full hover:bg-white/90 transition-colors shadow-lg text-sm"
          >
            搜索
          </button>
        </div>

        <!-- 热门分类横向导航 -->
        <div class="mt-10">
          <h2 class="text-xl font-bold text-white mb-5 font-display">
            热门分类
          </h2>
          <div class="relative">
            <div
              ref="categoryScrollRef"
              class="flex items-end gap-14 overflow-x-auto pb-2 scrollbar-hide"
            >
              <div
                v-for="cat in displayCategories"
                :key="cat.id"
                class="flex flex-col items-center gap-2.5 cursor-pointer group shrink-0"
                @click="selectCategory(cat.id)"
              >
                <!-- 线性轮廓图标 -->
                <svg
                  v-if="cat.id === '' || cat.name === '全部'"
                  class="w-7 h-7 transition-colors"
                  :class="
                    selectedCategory === cat.id
                      ? 'text-white'
                      : 'text-white/50 group-hover:text-white/80'
                  "
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M3.75 6A2.25 2.25 0 016 3.75h2.25A2.25 2.25 0 0110.5 6v2.25a2.25 2.25 0 01-2.25 2.25H6a2.25 2.25 0 01-2.25-2.25V6zM3.75 15.75A2.25 2.25 0 016 13.5h2.25a2.25 2.25 0 012.25 2.25V18a2.25 2.25 0 01-2.25 2.25H6A2.25 2.25 0 013.75 18v-2.25zM13.5 6a2.25 2.25 0 012.25-2.25H18A2.25 2.25 0 0120.25 6v2.25A2.25 2.25 0 0118 10.5h-2.25a2.25 2.25 0 01-2.25-2.25V6zM13.5 15.75a2.25 2.25 0 012.25-2.25H18a2.25 2.25 0 012.25 2.25V18A2.25 2.25 0 0118 20.25h-2.25A2.25 2.25 0 0113.5 18v-2.25z"
                  />
                </svg>
                <svg
                  v-else-if="
                    cat.name === '电子产品' || cat.categoryName === '电子产品'
                  "
                  class="w-7 h-7 transition-colors"
                  :class="
                    selectedCategory === cat.id
                      ? 'text-white'
                      : 'text-white/50 group-hover:text-white/80'
                  "
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M8.25 3v1.5M4.5 8.25H3m18 0h-1.5M4.5 12H3m18 0h-1.5m-15 3.75H3m18 0h-1.5M8.25 19.5V21M12 3v1.5m0 15V21m3.75-18v1.5m0 15V21m-9-1.5h10.5a2.25 2.25 0 002.25-2.25V6.75a2.25 2.25 0 00-2.25-2.25H6.75A2.25 2.25 0 004.5 6.75v10.5a2.25 2.25 0 002.25 2.25zm.75-12h9v9h-9v-9z"
                  />
                </svg>
                <svg
                  v-else-if="
                    cat.name === '图书教材' ||
                    cat.categoryName === '图书教材' ||
                    cat.name === '书籍教材' ||
                    cat.categoryName === '书籍教材'
                  "
                  class="w-7 h-7 transition-colors"
                  :class="
                    selectedCategory === cat.id
                      ? 'text-white'
                      : 'text-white/50 group-hover:text-white/80'
                  "
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M12 6.042A8.967 8.967 0 006 3.75c-1.052 0-2.062.18-3 .512v14.25A8.987 8.987 0 016 18c2.305 0 4.408.867 6 2.292m0-14.25a8.966 8.966 0 016-2.292c1.052 0 2.062.18 3 .512v14.25A8.987 8.987 0 0018 18a8.967 8.967 0 00-6 2.292m0-14.25v14.25"
                  />
                </svg>
                <svg
                  v-else-if="
                    cat.name === '服装鞋帽' ||
                    cat.categoryName === '服装鞋帽' ||
                    cat.name === '服饰鞋包' ||
                    cat.categoryName === '服饰鞋包'
                  "
                  class="w-7 h-7 transition-colors"
                  :class="
                    selectedCategory === cat.id
                      ? 'text-white'
                      : 'text-white/50 group-hover:text-white/80'
                  "
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M15.75 10.5V6a3.75 3.75 0 10-7.5 0v4.5m11.356-1.993l1.263 12c.07.665-.45 1.243-1.119 1.243H4.25a1.125 1.125 0 01-1.12-1.243l1.264-12A1.125 1.125 0 015.513 7.5h12.974c.576 0 1.059.435 1.119 1.007zM8.625 10.5a.375.375 0 11-.75 0 .375.375 0 01.75 0zm7.5 0a.375.375 0 11-.75 0 .375.375 0 01.75 0z"
                  />
                </svg>
                <svg
                  v-else-if="
                    cat.name === '生活用品' || cat.categoryName === '生活用品'
                  "
                  class="w-7 h-7 transition-colors"
                  :class="
                    selectedCategory === cat.id
                      ? 'text-white'
                      : 'text-white/50 group-hover:text-white/80'
                  "
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M2.25 12l8.954-8.955c.44-.439 1.152-.439 1.591 0L21.75 12M4.5 9.75v10.125c0 .621.504 1.125 1.125 1.125H9.75v-4.875c0-.621.504-1.125 1.125-1.125h2.25c.621 0 1.125.504 1.125 1.125V21h4.125c.621 0 1.125-.504 1.125-1.125V9.75M8.25 21h8.25"
                  />
                </svg>
                <svg
                  v-else-if="
                    cat.name === '运动户外' || cat.categoryName === '运动户外'
                  "
                  class="w-7 h-7 transition-colors"
                  :class="
                    selectedCategory === cat.id
                      ? 'text-white'
                      : 'text-white/50 group-hover:text-white/80'
                  "
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9S9.515 3 12 3m0 0a8.997 8.997 0 017.843 4.582M12 3a8.997 8.997 0 00-7.843 4.582m15.686 0A11.953 11.953 0 0112 10.5c-2.998 0-5.74-1.1-7.843-2.918m15.686 0A8.959 8.959 0 0121 12c0 .778-.099 1.533-.284 2.253m0 0A17.919 17.919 0 0112 16.5c-3.162 0-6.133-.815-8.716-2.247m0 0A9.015 9.015 0 013 12c0-1.605.42-3.113 1.157-4.418"
                  />
                </svg>
                <svg
                  v-else-if="
                    cat.name === '美妆护肤' || cat.categoryName === '美妆护肤'
                  "
                  class="w-7 h-7 transition-colors"
                  :class="
                    selectedCategory === cat.id
                      ? 'text-white'
                      : 'text-white/50 group-hover:text-white/80'
                  "
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M9.813 15.904L9 18.75l-.813-2.846a4.5 4.5 0 00-3.09-3.09L2.25 12l2.846-.813a4.5 4.5 0 003.09-3.09L9 5.25l.813 2.846a4.5 4.5 0 003.09 3.09L15.75 12l-2.846.813a4.5 4.5 0 00-3.09 3.09zM18.259 8.715L18 9.75l-.259-1.035a3.375 3.375 0 00-2.455-2.456L14.25 6l1.036-.259a3.375 3.375 0 002.455-2.456L18 2.25l.259 1.035a3.375 3.375 0 002.455 2.456L21.75 6l-1.036.259a3.375 3.375 0 00-2.455 2.456zM16.894 20.567L16.5 21.75l-.394-1.183a2.25 2.25 0 00-1.423-1.423L13.5 18.75l1.183-.394a2.25 2.25 0 001.423-1.423l.394-1.183.394 1.183a2.25 2.25 0 001.423 1.423l1.183.394-1.183.394a2.25 2.25 0 00-1.423 1.423z"
                  />
                </svg>
                <svg
                  v-else-if="
                    cat.name === '家用电器' || cat.categoryName === '家用电器'
                  "
                  class="w-7 h-7 transition-colors"
                  :class="
                    selectedCategory === cat.id
                      ? 'text-white'
                      : 'text-white/50 group-hover:text-white/80'
                  "
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M8.25 18.75a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 01-1.125-1.125V14.25m17.25 4.5a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h1.125c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H18.75m-7.5-2.25h7.5"
                  />
                </svg>
                <svg
                  v-else
                  class="w-7 h-7 transition-colors"
                  :class="
                    selectedCategory === cat.id
                      ? 'text-white'
                      : 'text-white/50 group-hover:text-white/80'
                  "
                  fill="none"
                  stroke="currentColor"
                  stroke-width="1.5"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M20.25 7.5l-.625 10.632a2.25 2.25 0 01-2.247 2.118H6.622a2.25 2.25 0 01-2.247-2.118L3.75 7.5M10 11.25h4M3.375 7.5h17.25c.621 0 1.125-.504 1.125-1.125v-1.5c0-.621-.504-1.125-1.125-1.125H3.375c-.621 0-1.125.504-1.125 1.125v1.5c0 .621.504 1.125 1.125 1.125z"
                  />
                </svg>
                <!-- 分类名称 -->
                <span
                  :class="[
                    'text-xs transition-all whitespace-nowrap relative',
                    selectedCategory === cat.id
                      ? 'text-white font-semibold'
                      : 'text-white/50 group-hover:text-white/80',
                  ]"
                >
                  {{ cat.categoryName || cat.name }}
                  <!-- 选中下划线 -->
                  <span
                    v-if="selectedCategory === cat.id"
                    class="absolute -bottom-1.5 left-1/2 -translate-x-1/2 w-5 h-0.5 bg-white rounded-full"
                  ></span>
                </span>
              </div>
            </div>
            <!-- 右侧翻页箭头 -->
            <button
              @click="scrollCategories(1)"
              class="absolute right-0 top-1/2 -translate-y-1/2 w-8 h-8 rounded-full bg-white/15 backdrop-blur-sm border border-white/20 flex items-center justify-center text-white/70 hover:text-white hover:bg-white/25 transition-colors shrink-0"
            >
              <svg
                class="w-4 h-4"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  d="M9 5l7 7-7 7"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </section>

    <section class="py-12">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between mb-8">
          <h2 class="text-2xl font-bold text-gray-900 font-display">
            热门好物
          </h2>
          <button
            @click="router.push('/products')"
            class="flow-btn"
          >
            <!-- 左箭头 -->
            <svg class="flow-btn-arrow-left" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
            <!-- 文字 -->
            <span class="flow-btn-text">查看全部</span>
            <!-- 圆形背景 -->
            <span class="flow-btn-circle"></span>
            <!-- 右箭头 -->
            <svg class="flow-btn-arrow-right" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
          </button>
        </div>
        <div v-if="loading" class="text-center py-12">
          <div class="text-gray-400">加载中...</div>
        </div>
        <div v-else-if="products.length === 0" class="text-center py-12">
          <div class="text-6xl mb-4">📦</div>
          <div class="text-gray-500 text-lg">暂无商品</div>
        </div>
        <div
          v-else
          class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6"
        >
          <div
            v-for="product in products"
            :key="product.id"
            class="group cursor-pointer bg-white rounded-[18px] shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden"
            @click="viewProduct(product.id)"
          >
            <!-- 图片区域 -->
            <div class="relative aspect-[4/3] overflow-hidden bg-gray-100">
              <img
                v-if="product.productImage"
                :src="product.productImage"
                class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              />
              <div
                v-else
                class="w-full h-full flex items-center justify-center text-4xl bg-gradient-to-br from-primary-50 to-primary-100"
              >
                📦
              </div>
              <!-- 收藏按钮 -->
              <div
                @click="(e) => toggleFavorite(e, product)"
                :class="[
                  'absolute top-3 right-3 w-8 h-8 rounded-full backdrop-blur-sm flex items-center justify-center transition-colors',
                  favoritedSet.has(product.id)
                    ? 'bg-red-500 text-white'
                    : 'bg-black/40 text-white hover:bg-red-500',
                ]"
              >
                <svg
                  class="w-4 h-4"
                  :fill="favoritedSet.has(product.id) ? 'currentColor' : 'none'"
                  stroke="currentColor"
                  stroke-width="2"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
                  />
                </svg>
              </div>
              <!-- 成色标签 -->
              <div
                v-if="product.condition"
                class="absolute top-3 left-3 bg-white/90 backdrop-blur-sm px-2.5 py-1 rounded-full text-xs font-medium text-gray-700"
              >
                {{ product.condition }}
              </div>
            </div>
            <!-- 文字区域 -->
            <div class="p-4">
              <!-- 标题 + 评分行 -->
              <div class="flex items-center justify-between mb-1">
                <h3
                  class="text-[15px] font-bold text-[#111111] truncate group-hover:text-primary-500 transition-colors"
                >
                  {{ product.productName }}
                </h3>
                <div class="flex items-center gap-0.5 flex-shrink-0 ml-2">
                  <svg
                    class="w-3.5 h-3.5 text-yellow-500"
                    fill="currentColor"
                    viewBox="0 0 20 20"
                  >
                    <path
                      d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"
                    />
                  </svg>
                  <span class="text-xs font-medium text-gray-700">{{
                    product.condition === "全新"
                      ? "5.0"
                      : product.condition === "9成新"
                        ? "4.8"
                        : product.condition === "8成新"
                          ? "4.5"
                          : "4.0"
                  }}</span>
                </div>
              </div>
              <!-- 描述 -->
              <p
                v-if="product.description"
                class="text-[#666666] text-[13px] mb-2 line-clamp-1"
              >
                {{ product.description }}
              </p>
              <p v-else class="text-[#666666] text-[13px] mb-2">
                {{ product.condition || "二手好物" }}
              </p>
              <!-- 分类标签 -->
              <div class="flex items-center gap-2 mb-3">
                <span
                  class="inline-block px-2.5 py-0.5 text-[11px] rounded-full bg-gray-100 text-gray-600"
                  >{{ product.categoryName || "未分类" }}</span
                >
              </div>
              <!-- 底部价格区域 -->
              <div
                class="flex items-end justify-between pt-2 border-t border-gray-100"
              >
                <div>
                  <span class="text-[11px] text-gray-400 line-through mr-1"
                    >¥{{ (product.price * 1.3).toFixed(0) }}</span
                  >
                  <span class="text-[18px] font-bold text-[#111111]"
                    >¥{{ product.price }}</span
                  >
                </div>
                <span class="text-[11px] text-gray-400">{{
                  formatTime(product.createTime)
                }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <StickyFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/store/user";
import StickyFooter from "@/components/StickyFooter.vue";
import {
  getProducts,
  getCategories,
  interactProduct,
  getMyFavorites,
} from "@/api/product";

const router = useRouter();
const userStore = useUserStore();
const searchKeyword = ref("");
const selectedCategory = ref("");
const showCategoryDropdown = ref(false);
const showUserMenu = ref(false);
const minPrice = ref("");
const maxPrice = ref("");
const products = ref([]);
const categories = ref([]);
const loading = ref(false);
const page = ref(1);
const favoritedSet = ref(new Set());
const categoryScrollRef = ref(null);

function scrollCategories(direction) {
  if (categoryScrollRef.value) {
    categoryScrollRef.value.scrollBy({
      left: direction * 200,
      behavior: "smooth",
    });
  }
}

const selectedCategoryLabel = computed(() => {
  if (!selectedCategory.value) return "全部分类";
  const cat = displayCategories.value.find((c) => c.id === selectedCategory.value);
  return cat ? cat.categoryName || cat.name : "全部分类";
});

function selectCategory(val) {
  selectedCategory.value = val;
  showCategoryDropdown.value = false;
  handleSearch();
}

function handleLogout() {
  showUserMenu.value = false;
  userStore.logout();
  router.push("/login");
}

// 点击外部关闭下拉
if (typeof document !== "undefined") {
  document.addEventListener("click", (e) => {
    if (!e.target.closest(".category-dropdown-wrapper")) {
      showCategoryDropdown.value = false;
    }
    if (!e.target.closest(".user-menu-wrapper")) {
      showUserMenu.value = false;
    }
  });
}

const categoryIcons = {
  电子产品: "💻",
  书籍教材: "📚",
  生活用品: "🏠",
  服饰鞋包: "👕",
  运动户外: "⚽",
  美妆护肤: "💄",
  食品饮料: "🍜",
  其他: "📦",
};

const displayCategories = computed(() => {
  return [
    { id: "", name: "全部", icon: "🔥" },
    ...categories.value.map((cat) => ({
      ...cat,
      icon: categoryIcons[cat.categoryName || cat.name] || "📦",
    })),
  ];
});

function formatTime(time) {
  if (!time) return "-";
  const d = new Date(time);
  const days = Math.floor((new Date() - d) / (1000 * 60 * 60 * 24));
  if (days === 0) return "今天";
  if (days === 1) return "昨天";
  if (days < 7) return days + "天前";
  return d.toLocaleDateString("zh-CN");
}

async function toggleFavorite(e, product) {
  e.stopPropagation();
  if (!userStore.user) {
    router.push("/login");
    return;
  }
  try {
    await interactProduct(product.id, "favorite");
    const s = new Set(favoritedSet.value);
    if (s.has(product.id)) {
      s.delete(product.id);
    } else {
      s.add(product.id);
    }
    favoritedSet.value = s;
  } catch {
    // ignore
  }
}

async function fetchProducts() {
  loading.value = true;
  try {
    const params = { page: page.value, pageSize: 8 };
    if (searchKeyword.value) params.keyword = searchKeyword.value;
    if (selectedCategory.value) params.categoryId = selectedCategory.value;
    if (minPrice.value) params.minPrice = minPrice.value;
    if (maxPrice.value) params.maxPrice = maxPrice.value;
    const res = await getProducts(params);
    products.value = res.data.list || [];
  } catch (e) {
    console.error("获取商品失败:", e);
  } finally {
    loading.value = false;
  }
}

async function loadUserFavorites() {
  if (!userStore.user) return;
  try {
    const res = await getMyFavorites();
    const list = res.data?.list || [];
    favoritedSet.value = new Set(list.map((p) => p.id));
  } catch {
    // ignore
  }
}

async function fetchCategories() {
  try {
    const res = await getCategories();
    categories.value = res.data || [];
  } catch (e) {
    console.error("获取分类失败:", e);
  }
}

function handleSearch() {
  page.value = 1;
  fetchProducts();
}
function viewProduct(id) {
  router.push("/products/" + id);
}

onMounted(() => {
  fetchProducts();
  fetchCategories();
  loadUserFavorites();
});
</script>

<style scoped>
.flow-btn {
  position: relative;
  display: flex;
  align-items: center;
  gap: 4px;
  overflow: hidden;
  border-radius: 100px;
  border: 1.5px solid rgba(51, 51, 51, 0.4);
  background: transparent;
  padding: 12px 32px;
  font-size: 14px;
  font-weight: 600;
  color: #111111;
  cursor: pointer;
  transition: all 600ms cubic-bezier(0.23, 1, 0.32, 1);
}

.flow-btn:hover {
  border-color: transparent;
  color: white;
  border-radius: 12px;
}

.flow-btn:active {
  transform: scale(0.95);
}

.flow-btn-arrow-left {
  position: absolute;
  width: 16px;
  height: 16px;
  left: -25%;
  stroke: #111111;
  fill: none;
  z-index: 9;
  transition: all 800ms cubic-bezier(0.34, 1.56, 0.64, 1);
}

.flow-btn:hover .flow-btn-arrow-left {
  left: 16px;
  stroke: white;
}

.flow-btn-text {
  position: relative;
  z-index: 1;
  transform: translateX(-12px);
  transition: all 800ms ease-out;
}

.flow-btn:hover .flow-btn-text {
  transform: translateX(12px);
}

.flow-btn-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 16px;
  height: 16px;
  background: #111111;
  border-radius: 50%;
  opacity: 0;
  transition: all 800ms cubic-bezier(0.19, 1, 0.22, 1);
}

.flow-btn:hover .flow-btn-circle {
  width: 220px;
  height: 220px;
  opacity: 1;
}

.flow-btn-arrow-right {
  position: absolute;
  width: 16px;
  height: 16px;
  right: 16px;
  stroke: #111111;
  fill: none;
  z-index: 9;
  transition: all 800ms cubic-bezier(0.34, 1.56, 0.64, 1);
}

.flow-btn:hover .flow-btn-arrow-right {
  right: -25%;
  stroke: white;
}
</style>
