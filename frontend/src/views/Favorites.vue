<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">我的收藏</h1>

    <div v-if="loading" class="text-center py-12">
      <div
        class="animate-spin w-8 h-8 border-4 border-primary-500 border-t-transparent rounded-full mx-auto"
      ></div>
      <p class="text-gray-500 mt-4">加载中...</p>
    </div>

    <div v-else-if="products.length === 0" class="text-center py-12">
      <svg
        class="w-16 h-16 text-gray-300 mx-auto mb-4"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
        />
      </svg>
      <p class="text-gray-400">暂无收藏</p>
      <router-link
        to="/products"
        class="text-primary-500 hover:text-primary-600 text-sm mt-2 inline-block"
        >去逛逛 →</router-link
      >
    </div>

    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <div
        v-for="product in products"
        :key="product.id"
        class="group cursor-pointer bg-white rounded-[18px] shadow-md hover:shadow-xl transition-all duration-300 overflow-hidden"
        @click="router.push(`/products/${product.id}`)"
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
          <!-- 取消收藏按钮 -->
          <div
            @click="(e) => removeFavorite(e, product)"
            class="absolute top-3 right-3 w-8 h-8 rounded-full bg-red-500 backdrop-blur-sm flex items-center justify-center text-white hover:bg-red-600 transition-colors"
          >
            <svg class="w-4 h-4" fill="currentColor" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
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
          <h3
            class="text-[15px] font-bold text-gray-900 truncate group-hover:text-primary-500 transition-colors mb-1"
          >
            {{ product.productName }}
          </h3>
          <p
            v-if="product.description"
            class="text-gray-500 text-[13px] mb-2 line-clamp-1"
          >
            {{ product.description }}
          </p>
          <!-- 底部价格 -->
          <div class="flex items-end justify-between pt-2 border-t border-gray-100">
            <span class="text-[18px] font-bold text-gray-900"
              >¥{{ product.price }}</span
            >
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getMyFavorites, interactProduct } from "@/api/product";

const router = useRouter();
const products = ref([]);
const loading = ref(true);

async function removeFavorite(e, product) {
  e.stopPropagation();
  try {
    await interactProduct(product.id, "favorite");
    products.value = products.value.filter((p) => p.id !== product.id);
  } catch {
    // ignore
  }
}

onMounted(async () => {
  try {
    const res = await getMyFavorites();
    products.value = res.data?.list || [];
  } catch {
    products.value = [];
  } finally {
    loading.value = false;
  }
});
</script>
