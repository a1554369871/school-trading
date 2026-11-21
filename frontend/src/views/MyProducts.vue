<template>
  <div class="min-h-screen bg-[#F8FAFB]">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Header -->
      <div class="flex items-center justify-between mb-6">
        <div>
          <h1 class="text-xl font-bold text-[#09090B] tracking-tight">我的发布</h1>
          <p class="text-sm text-[#3F3F46] mt-0.5">管理你发布的所有商品</p>
        </div>
        <router-link
          to="/publish"
          class="inline-flex items-center gap-1.5 px-4 py-2 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
          发布商品
        </router-link>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="text-center py-20">
        <div class="animate-spin w-8 h-8 border-[3px] border-gray-200 border-t-gray-600 rounded-full mx-auto"></div>
        <p class="text-gray-400 mt-4 text-sm">加载中...</p>
      </div>

      <!-- Empty -->
      <div v-else-if="products.length === 0" class="bg-white rounded-2xl border border-gray-100 shadow-sm text-center py-20">
        <div class="w-20 h-20 rounded-full bg-gray-100 flex items-center justify-center mx-auto mb-4">
          <svg class="w-10 h-10 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
        </div>
        <p class="text-gray-500 mb-4">还没有发布任何商品</p>
        <router-link
          to="/publish"
          class="inline-flex items-center gap-1.5 px-5 py-2 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
        >
          去发布
        </router-link>
      </div>

      <!-- Product List -->
      <div v-else class="space-y-4">
        <div
          v-for="product in products"
          :key="product.id"
          class="bg-white rounded-2xl border border-gray-100 shadow-sm hover:shadow-md hover:border-gray-200 transition-all duration-200 overflow-hidden"
        >
          <div class="flex items-start gap-4 p-5">
            <!-- Image -->
            <div class="w-24 h-24 bg-gray-50 rounded-xl overflow-hidden flex-shrink-0">
              <img
                v-if="product.productImage"
                :src="product.productImage"
                :alt="product.productName"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center">
                <svg class="w-8 h-8 text-gray-200" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
            </div>

            <!-- Info -->
            <div class="flex-1 min-w-0">
              <div class="flex items-start justify-between gap-3">
                <div class="min-w-0">
                  <h3 class="text-sm font-bold text-gray-900 truncate">{{ product.productName }}</h3>
                  <p v-if="product.description" class="text-gray-400 text-xs mt-0.5 line-clamp-1">{{ product.description }}</p>
                </div>
                <span
                  :class="getStatusClass(product.status)"
                  class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium whitespace-nowrap flex-shrink-0"
                >
                  {{ getStatusText(product.status) }}
                </span>
              </div>

              <div class="flex items-center gap-3 mt-2">
                <span class="text-lg font-bold text-gray-900">¥{{ product.price }}</span>
                <span v-if="product.categoryName" class="inline-block px-2 py-0.5 text-[11px] rounded-md bg-gray-100 text-gray-500 font-medium">
                  {{ product.categoryName }}
                </span>
              </div>

              <div class="flex items-center justify-between mt-3 pt-3 border-t border-gray-100">
                <span class="text-xs text-gray-400">{{ formatTime(product.publishTime) }}</span>
                <div class="flex items-center gap-2">
                  <button
                    @click="router.push(`/products/${product.id}`)"
                    class="admin-icon-btn"
                    title="查看"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg>
                  </button>
                  <button
                    v-if="product.status === 1"
                    @click="router.push(`/products/${product.id}/edit`)"
                    class="admin-icon-btn"
                    title="编辑"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                  </button>
                  <button
                    v-if="product.status === 1"
                    @click="handleOffshelf(product)"
                    class="admin-icon-btn admin-icon-btn-danger"
                    title="下架"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"/></svg>
                  </button>
                  <button
                    v-if="product.status === 0"
                    @click="handleReonline(product)"
                    class="admin-icon-btn admin-icon-btn-green"
                    title="重新上架"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
                  </button>
                  <button
                    v-if="product.status === 0"
                    @click="handleDelete(product)"
                    class="admin-icon-btn admin-icon-btn-danger"
                    title="删除"
                  >
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyProducts, updateProductStatus, deleteProduct } from '@/api/product'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const products = ref([])
const loading = ref(false)

function getStatusText(status) {
  const map = { 0: '已下架', 1: '在售', 2: '已售出', 3: '已预留' }
  return map[status] || '未知'
}

function getStatusClass(status) {
  const map = {
    0: 'bg-gray-100 text-gray-600',
    1: 'bg-green-50 text-green-700',
    2: 'bg-blue-50 text-blue-700'
  }
  return map[status] || 'bg-gray-100 text-gray-600'
}

function formatTime(time) {
  if (!time) return '-'
  const d = new Date(time)
  const days = Math.floor((new Date() - d) / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return days + '天前'
  return d.toLocaleDateString('zh-CN')
}

async function loadData() {
  loading.value = true
  try {
    const res = await getMyProducts()
    products.value = res.data?.list || []
  } finally {
    loading.value = false
  }
}

async function handleOffshelf(product) {
  try {
    await ElMessageBox.confirm(
      `确认下架「${product.productName}」？`,
      '下架确认',
      { confirmButtonText: '确认下架', cancelButtonText: '取消', type: 'warning' }
    )
    await updateProductStatus(product.id, 0)
    ElMessage.success('已下架')
    await loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

async function handleReonline(product) {
  try {
    await updateProductStatus(product.id, 1)
    ElMessage.success('已重新上架')
    await loadData()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function handleDelete(product) {
  try {
    await ElMessageBox.confirm(
      `确认删除「${product.productName}」？删除后不可恢复。`,
      '删除确认',
      { confirmButtonText: '确认删除', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteProduct(product.id)
    ElMessage.success('已删除')
    await loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

onMounted(loadData)
</script>
