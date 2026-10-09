<template>
  <div class="min-h-screen bg-[#F8FAFB]">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 筛选栏 -->
      <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 mb-6">
        <div class="flex flex-wrap items-center gap-4">
          <!-- 分类筛选 -->
          <div class="flex items-center gap-2 flex-wrap">
            <span class="text-sm font-medium text-gray-500 mr-1">分类</span>
            <button
              @click="selectCategory(null)"
              class="px-3.5 py-1.5 rounded-full text-sm font-medium transition-all duration-200 cursor-pointer"
              :class="selectedCategory === null
                ? 'bg-gray-900 text-white shadow-sm'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
            >
              全部
            </button>
            <button
              v-for="cat in categories"
              :key="cat.id"
              @click="selectCategory(cat.id)"
              class="px-3.5 py-1.5 rounded-full text-sm font-medium transition-all duration-200 cursor-pointer"
              :class="selectedCategory === cat.id
                ? 'bg-gray-900 text-white shadow-sm'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
            >
              {{ cat.categoryName }}
            </button>
          </div>

          <!-- 排序方式 -->
          <div class="flex items-center gap-2 ml-auto">
            <span class="text-sm font-medium text-gray-500 mr-1">排序</span>
            <button
              v-for="sort in sortOptions"
              :key="sort.value"
              @click="selectSort(sort.value)"
              class="px-3.5 py-1.5 rounded-full text-sm font-medium transition-all duration-200 cursor-pointer"
              :class="selectedSort === sort.value
                ? 'bg-gray-900 text-white shadow-sm'
                : 'bg-gray-100 text-gray-600 hover:bg-gray-200'"
            >
              {{ sort.label }}
            </button>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="text-center py-20">
        <div class="animate-spin w-8 h-8 border-[3px] border-gray-200 border-t-gray-600 rounded-full mx-auto"></div>
        <p class="text-gray-400 mt-4 text-sm">加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="products.length === 0" class="text-center py-20">
        <div class="w-20 h-20 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-4">
          <svg class="w-10 h-10 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
        </div>
        <p class="text-gray-400 text-sm">暂无商品</p>
      </div>

      <!-- 商品列表 -->
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-5">
        <div
          v-for="product in products"
          :key="product.id"
          class="group cursor-pointer bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-md hover:border-gray-200 transition-all duration-250 overflow-hidden"
          @click="goToDetail(product.id)"
        >
          <!-- 图片区域 -->
          <div class="relative aspect-[4/3] overflow-hidden bg-gray-50">
            <img
              v-if="product.productImage"
              :src="product.productImage"
              :alt="product.productName"
              class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
              loading="lazy"
            />
            <div v-else class="w-full h-full flex items-center justify-center">
              <svg class="w-10 h-10 text-gray-200" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>

            <!-- 成色标签 -->
            <span
              v-if="product.condition"
              class="absolute top-3 left-3 bg-white/90 backdrop-blur-sm text-gray-700 text-xs font-medium px-2.5 py-1 rounded-full"
            >
              {{ product.condition }}
            </span>

            <!-- 收藏按钮 -->
            <button
              @click.stop="toggleFavorite(product)"
              class="absolute top-3 right-3 w-8 h-8 rounded-full backdrop-blur-sm flex items-center justify-center transition-colors duration-200 cursor-pointer"
              :class="favoritedSet.has(product.id)
                ? 'bg-red-500 text-white'
                : 'bg-black/40 text-white hover:bg-red-500'"
            >
              <svg
                class="w-4 h-4"
                :fill="favoritedSet.has(product.id) ? 'currentColor' : 'none'"
                stroke="currentColor"
                stroke-width="2"
                viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
              </svg>
            </button>
          </div>

          <!-- 文字区域 -->
          <div class="p-4">
            <!-- 标题 -->
            <h3 class="text-sm font-bold text-gray-900 truncate group-hover:text-gray-600 transition-colors duration-200 mb-1">
              {{ product.productName }}
            </h3>

            <!-- 描述 -->
            <p v-if="product.description" class="text-gray-400 text-xs mb-2.5 line-clamp-1">
              {{ product.description }}
            </p>
            <p v-else class="text-gray-400 text-xs mb-2.5">
              {{ product.condition || '二手好物' }}
            </p>

            <!-- 分类标签 -->
            <span class="inline-block px-2 py-0.5 text-[11px] rounded-md bg-gray-100 text-gray-500 font-medium mb-3">
              {{ getCategoryName(product.categoryId) }}
            </span>

            <!-- 底部价格 -->
            <div class="flex items-center justify-between pt-2.5 border-t border-gray-100">
              <div class="flex items-baseline gap-1">
                <span class="text-xs text-gray-400 line-through">¥{{ (product.price * 1.3).toFixed(0) }}</span>
                <span class="text-lg font-bold text-gray-900">¥{{ product.price }}</span>
              </div>
              <span class="text-[11px] text-gray-400">{{ formatTime(product.publishTime) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="flex justify-center mt-10">
        <Pagination
          :current-page="page"
          :total-pages="totalPages"
          @change="changePage"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { getProducts, interactProduct, getMyFavorites } from "@/api/product";
import { getCategories } from "@/api/category";
import { useUserStore } from "@/store/user";
import Pagination from "@/components/ui/Pagination.vue";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const products = ref([]);
const categories = ref([]);
const loading = ref(false);
const page = ref(1);
const pageSize = 12;
const total = ref(0);
const selectedCategory = ref(
  route.query.categoryId ? Number(route.query.categoryId) : null,
);
const selectedSort = ref("latest");
const favoritedSet = ref(new Set());

const sortOptions = [
  { label: "最新", value: "latest" },
  { label: "价格低", value: "price_asc" },
  { label: "价格高", value: "price_desc" },
];

const totalPages = computed(() => Math.ceil(total.value / pageSize));

function getCategoryName(categoryId) {
  if (!categoryId) return "未分类";
  const cat = categories.value.find((c) => c.id === categoryId);
  return cat ? cat.categoryName || cat.name : "未分类";
}

function selectCategory(id) {
  selectedCategory.value = selectedCategory.value === id ? null : id;
  page.value = 1;
  fetchProducts();
}

function selectSort(value) {
  selectedSort.value = value;
  page.value = 1;
  fetchProducts();
}

function changePage(newPage) {
  page.value = newPage;
  fetchProducts();
  window.scrollTo(0, 0);
}

function goToDetail(id) {
  router.push(`/products/${id}`);
}

function formatTime(time) {
  if (!time) return "-";
  const d = new Date(time);
  const days = Math.floor((new Date() - d) / (1000 * 60 * 60 * 24));
  if (days === 0) return "今天";
  if (days === 1) return "昨天";
  if (days < 7) return days + "天前";
  return d.toLocaleDateString("zh-CN");
}

async function toggleFavorite(product) {
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

async function fetchProducts() {
  loading.value = true;
  try {
    const params = {
      pageNum: page.value,
      pageSize,
    };
    if (selectedSort.value === "price_asc") {
      params.sortBy = "price";
      params.sortOrder = "asc";
    } else if (selectedSort.value === "price_desc") {
      params.sortBy = "price";
      params.sortOrder = "desc";
    } else {
      params.sortBy = "publishTime";
      params.sortOrder = "desc";
    }
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value;
    }
    if (route.query.keyword) {
      params.keyword = route.query.keyword;
    }
    const res = await getProducts(params);
    products.value = res.data?.list || [];
    total.value = res.data?.total || 0;
  } catch (e) {
    console.error("获取商品列表失败:", e);
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  try {
    const res = await getCategories();
    categories.value = res.data || [];
  } catch (e) {
    console.error("获取分类失败:", e);
  }
  fetchProducts();
  loadUserFavorites();
});

watch(
  () => route.query.keyword,
  () => {
    page.value = 1;
    fetchProducts();
  },
);
</script>
