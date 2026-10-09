<template>
  <div class="relative min-h-[calc(100vh-6rem)] overflow-hidden">
    <!-- Animated Gradient Background -->
    <div class="absolute inset-0 pointer-events-none" ref="gradientContainer">
      <div
        v-for="(circle, i) in gradientCircles"
        :key="i"
        class="absolute rounded-full"
        :style="{
          width: circle.size + 'px',
          height: circle.size + 'px',
          left: circle.x + '%',
          top: circle.y + '%',
          background: circle.color,
          filter: 'blur(80px)',
          opacity: 0.3,
          animation: `float-${i} ${circle.duration}s ease-in-out infinite`,
        }"
      ></div>
    </div>

    <!-- Page Title -->
    <div class="relative z-10 mb-8">
      <h1 class="text-3xl font-bold text-gray-900 tracking-tight animate-fade-in">
        数据概览
      </h1>
      <p class="text-gray-500 mt-1 text-sm animate-fade-in" style="animation-delay: 0.1s">
        实时监控平台运营数据
      </p>
    </div>

    <!-- Bento Grid -->
    <div class="relative z-10 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
      <!-- 用户总数 - 跨2列 -->
      <div
        class="md:col-span-2 bento-card animate-slide-up"
        style="animation-delay: 0.1s"
      >
        <div class="bento-card-bg" style="background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%)"></div>
        <div class="relative z-10 flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-blue-600/80">用户总数</p>
            <p class="text-5xl font-bold text-gray-900 mt-2 tabular-nums">
              {{ animatedStats.totalUsers }}
            </p>
            <p class="text-xs text-gray-400 mt-2">平台注册用户</p>
          </div>
          <div class="w-16 h-16 bg-blue-100 rounded-2xl flex items-center justify-center">
            <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"/></svg>
          </div>
        </div>
      </div>

      <!-- 评论总数 -->
      <div
        class="bento-card animate-slide-up"
        style="animation-delay: 0.15s"
      >
        <div class="bento-card-bg" style="background: linear-gradient(135deg, #ede9fe 0%, #ddd6fe 100%)"></div>
        <div class="relative z-10">
          <p class="text-sm font-medium text-purple-600/80">评论总数</p>
          <p class="text-4xl font-bold text-gray-900 mt-2 tabular-nums">
            {{ animatedStats.totalComments }}
          </p>
          <p class="text-xs text-gray-400 mt-2">用户互动评论</p>
        </div>
      </div>

      <!-- 商品总数 -->
      <div
        class="bento-card animate-slide-up"
        style="animation-delay: 0.2s"
      >
        <div class="bento-card-bg" style="background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%)"></div>
        <div class="relative z-10">
          <p class="text-sm font-medium text-emerald-600/80">商品总数</p>
          <p class="text-4xl font-bold text-gray-900 mt-2 tabular-nums">
            {{ animatedStats.totalProducts }}
          </p>
          <p class="text-xs text-gray-400 mt-2">在售与已售商品</p>
        </div>
      </div>

      <!-- 订单总数 -->
      <div
        class="bento-card animate-slide-up"
        style="animation-delay: 0.25s"
      >
        <div class="bento-card-bg" style="background: linear-gradient(135deg, #ffedd5 0%, #fed7aa 100%)"></div>
        <div class="relative z-10">
          <p class="text-sm font-medium text-orange-600/80">订单总数</p>
          <p class="text-4xl font-bold text-gray-900 mt-2 tabular-nums">
            {{ animatedStats.totalOrders }}
          </p>
          <p class="text-xs text-gray-400 mt-2">累计交易订单</p>
        </div>
      </div>

      <!-- 卖家收入排行 - 跨2列 -->
      <div
        class="md:col-span-2 bento-card animate-slide-up"
        style="animation-delay: 0.3s"
      >
        <div class="bento-card-bg" style="background: linear-gradient(135deg, #fce7f3 0%, #fbcfe8 100%)"></div>
        <div class="relative z-10">
          <p class="text-sm font-medium text-pink-600/80 mb-4">卖家收入排行</p>
          <div v-if="sellerRevenue.length" class="space-y-3">
            <div
              v-for="(item, i) in sellerRevenue.slice(0, 5)"
              :key="i"
              class="flex items-center gap-3"
            >
              <span
                class="w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold text-white"
                :class="i === 0 ? 'bg-yellow-500' : i === 1 ? 'bg-gray-400' : i === 2 ? 'bg-orange-500' : 'bg-gray-300'"
              >
                {{ i + 1 }}
              </span>
              <span class="text-sm text-gray-700 flex-1 truncate">{{ item.sellerNickname || '未知' }}</span>
              <span class="text-sm font-bold text-gray-900 tabular-nums">¥{{ formatNumber(item.totalRevenue || 0) }}</span>
            </div>
          </div>
          <p v-else class="text-xs text-gray-400">暂无数据</p>
        </div>
      </div>

      <!-- 活跃用户 -->
      <div
        class="bento-card animate-slide-up"
        style="animation-delay: 0.35s"
      >
        <div class="bento-card-bg" style="background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%)"></div>
        <div class="relative z-10">
          <p class="text-sm font-medium text-amber-600/80 mb-3">活跃用户</p>
          <div v-if="activeUsers.length" class="space-y-2">
            <div
              v-for="(user, i) in activeUsers.slice(0, 4)"
              :key="i"
              class="flex items-center gap-2"
            >
              <div class="w-7 h-7 bg-amber-200 rounded-full flex items-center justify-center">
                <span class="text-xs font-medium text-amber-700">{{ (user.nickname || '用').charAt(0) }}</span>
              </div>
              <span class="text-sm text-gray-700 truncate">{{ user.nickname }}</span>
              <span class="text-xs text-gray-400 ml-auto">{{ user.publishCount || 0 }}件</span>
            </div>
          </div>
          <p v-else class="text-xs text-gray-400">暂无数据</p>
        </div>
      </div>

      <!-- 月度订单趋势 - 整行通栏 -->
      <div
        class="md:col-span-2 lg:col-span-3 bento-card animate-slide-up"
        style="animation-delay: 0.4s"
      >
        <div class="bento-card-bg" style="background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%)"></div>
        <div class="relative z-10">
          <p class="text-sm font-medium text-indigo-600/80 mb-4">月度订单趋势</p>
          <div v-if="monthlyStats.length" class="flex items-end gap-2 h-32">
            <div
              v-for="(item, i) in monthlyStats"
              :key="i"
              class="flex-1 flex flex-col items-center gap-1"
            >
              <span class="text-xs font-bold text-gray-700 tabular-nums">{{ item.orderCount }}</span>
              <div
                class="w-full rounded-t-lg transition-all duration-500"
                :style="{
                  height: barHeight(item.orderCount) + 'px',
                  background: 'linear-gradient(to top, #818cf8, #a5b4fc)',
                }"
              ></div>
              <span class="text-[10px] text-gray-400 truncate w-full text-center">
                {{ item.month || item.stat_month || '' }}
              </span>
            </div>
          </div>
          <p v-else class="text-xs text-gray-400">暂无数据</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import {
  getOverview,
  getUserTradeStats,
  getActiveUsers,
  getMonthlyStats,
  getSellerRevenue,
} from "@/api/admin";

const overview = ref({
  totalUsers: 0,
  totalProducts: 0,
  totalOrders: 0,
  totalComments: 0,
});
const animatedStats = reactive({
  totalUsers: 0,
  totalProducts: 0,
  totalOrders: 0,
  totalComments: 0,
});
const sellerRevenue = ref([]);
const activeUsers = ref([]);
const monthlyStats = ref([]);
const gradientContainer = ref(null);

// Animated gradient circles - macaron colors
const gradientCircles = [
  { size: 300, x: -5, y: 10, color: "radial-gradient(circle, #93c5fd, transparent)", duration: 20 },
  { size: 250, x: 60, y: -10, color: "radial-gradient(circle, #86efac, transparent)", duration: 25 },
  { size: 280, x: 30, y: 60, color: "radial-gradient(circle, #fda4af, transparent)", duration: 22 },
  { size: 200, x: 80, y: 50, color: "radial-gradient(circle, #c4b5fd, transparent)", duration: 18 },
  { size: 220, x: 10, y: 80, color: "radial-gradient(circle, #fcd34d, transparent)", duration: 28 },
];

// Generate float keyframe animations dynamically
const styleSheet = document.createElement("style");
let css = "";
gradientCircles.forEach((_, i) => {
  const x1 = Math.random() * 20 - 10;
  const y1 = Math.random() * 20 - 10;
  const x2 = Math.random() * 20 - 10;
  const y2 = Math.random() * 20 - 10;
  css += `
    @keyframes float-${i} {
      0%, 100% { transform: translate(0, 0); }
      25% { transform: translate(${x1}px, ${y1}px); }
      50% { transform: translate(${x2}px, ${y2}px); }
      75% { transform: translate(${-x1}px, ${-y2}px); }
    }
  `;
});
styleSheet.textContent = css;
document.head.appendChild(styleSheet);

// Animate numbers
function animateNumber(target, endValue, duration = 800) {
  const start = animatedStats[target];
  const diff = endValue - start;
  if (diff === 0) return;
  const startTime = Date.now();
  function step() {
    const elapsed = Date.now() - startTime;
    const progress = Math.min(elapsed / duration, 1);
    const eased = 1 - Math.pow(1 - progress, 3); // ease-out cubic
    animatedStats[target] = Math.round(start + diff * eased);
    if (progress < 1) requestAnimationFrame(step);
  }
  requestAnimationFrame(step);
}

function formatNumber(n) {
  if (n >= 10000) return (n / 10000).toFixed(1) + "w";
  return Number(n).toLocaleString();
}

const maxOrder = computed(() => {
  if (!monthlyStats.value.length) return 1;
  return Math.max(...monthlyStats.value.map((m) => Number(m.orderCount) || 1));
});

function barHeight(count) {
  return Math.max(8, (Number(count) / maxOrder.value) * 100);
}

onMounted(async () => {
  try {
    const [overviewRes, revenueRes, activeRes, monthlyRes] = await Promise.all([
      getOverview(),
      getSellerRevenue(),
      getActiveUsers(),
      getMonthlyStats(),
    ]);

    const data = overviewRes.data || {};
    overview.value = data;
    animateNumber("totalUsers", data.totalUsers || 0);
    animateNumber("totalProducts", data.totalProducts || 0);
    animateNumber("totalOrders", data.totalOrders || 0);
    animateNumber("totalComments", data.totalComments || 0);

    sellerRevenue.value = revenueRes.data || [];
    activeUsers.value = activeRes.data || [];
    monthlyStats.value = monthlyRes.data || [];
  } catch (e) {
    console.error("获取统计数据失败:", e);
  }
});
</script>

<style scoped>
.bento-card {
  @apply relative rounded-2xl overflow-hidden p-6 backdrop-blur-sm;
  @apply border border-white/60;
  @apply hover:shadow-lg hover:-translate-y-0.5 transition-all duration-300;
  min-height: 120px;
}

.bento-card-bg {
  @apply absolute inset-0 opacity-60;
}

.animate-fade-in {
  animation: fadeIn 0.6s ease-out both;
}

.animate-slide-up {
  animation: slideUp 0.6s ease-out both;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
