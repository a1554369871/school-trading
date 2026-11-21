<template>
  <div>
    <!-- Page Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-[#09090B] tracking-tight">商品管理</h1>
        <p class="text-sm text-[#3F3F46] mt-0.5">管理平台上架的所有商品</p>
      </div>
    </div>

    <!-- Search Bar -->
    <div class="bg-white rounded-2xl border border-gray-200/80 shadow-sm mb-4">
      <div class="p-4">
        <div class="flex items-center gap-3">
          <div class="relative flex-1 max-w-xs">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索商品名称..."
              class="admin-input"
              @keyup.enter="handleSearch"
            />
          </div>
          <button @click="handleSearch" class="admin-btn-primary">搜索</button>
        </div>
      </div>
    </div>

    <!-- Table Card -->
    <div class="bg-white rounded-2xl border border-gray-200/80 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-gray-100">
              <th class="admin-th">图片</th>
              <th class="admin-th">商品名称</th>
              <th class="admin-th">价格</th>
              <th class="admin-th">分类</th>
              <th class="admin-th">状态</th>
              <th class="admin-th">发布时间</th>
              <th class="admin-th">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="products.length === 0">
              <td colspan="7" class="py-16 text-center text-[#3F3F46] text-sm">暂无商品数据</td>
            </tr>
            <tr v-for="product in products" :key="product.id" class="admin-row">
              <td class="admin-td">
                <img v-if="product.productImage" :src="product.productImage" class="w-10 h-10 object-cover rounded-lg" />
                <div v-else class="w-10 h-10 bg-gray-100 rounded-lg flex items-center justify-center">
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg>
                </div>
              </td>
              <td class="admin-td font-medium text-[#09090B] max-w-[200px] truncate">{{ product.productName }}</td>
              <td class="admin-td font-semibold text-[#09090B]">¥{{ product.price }}</td>
              <td class="admin-td text-[#3F3F46]">{{ getCategoryName(product.categoryId) }}</td>
              <td class="admin-td">
                <span :class="product.status === 1 ? 'admin-badge-green' : 'admin-badge-gray'">
                  {{ getStatusText(product.status) }}
                </span>
              </td>
              <td class="admin-td text-[#3F3F46] text-xs">{{ formatDateTime(product.publishTime) }}</td>
              <td class="admin-td">
                <div class="flex items-center gap-1">
                  <button @click="viewProduct(product.id)" class="admin-icon-btn" title="查看"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/></svg></button>
                  <button v-if="product.status === 1" @click="toggleProductStatus(product)" class="admin-icon-btn" title="下架"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"/></svg></button>
                  <button v-else-if="product.status === 0" @click="toggleProductStatus(product)" class="admin-icon-btn admin-icon-btn-green" title="上架"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg></button>
                  <button @click="handleDelete(product.id)" class="admin-icon-btn admin-icon-btn-danger" title="删除"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg></button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="flex justify-center py-4 border-t border-gray-100">
        <Pagination :current-page="page" :total-pages="totalPages" @change="changePage" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProducts, getCategories } from '@/api/product'
import { updateProductStatus, deleteAdminProduct } from '@/api/admin'
import Pagination from '@/components/ui/Pagination.vue'

const router = useRouter()
const products = ref([])
const categoryMap = ref({})
const page = ref(1)
const totalPages = ref(1)
const searchKeyword = ref('')

function formatDateTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

function getCategoryName(categoryId) {
  if (!categoryId) return '-'
  return categoryMap.value[categoryId] || '-'
}

function getStatusText(status) {
  const map = { 0: '已下架', 1: '在售', 2: '已售出', 3: '已预留' }
  return map[status] || '未知'
}

async function fetchCategories() {
  try {
    const res = await getCategories()
    const list = res.data || []
    const map = {}
    list.forEach(c => { map[c.id] = c.categoryName })
    categoryMap.value = map
  } catch (e) {
    console.error('获取分类失败:', e)
  }
}

async function fetchProducts() {
  try {
    const res = await getProducts({ page: page.value, pageSize: 10, keyword: searchKeyword.value || undefined, admin: true })
    products.value = res.data.list || []
    totalPages.value = Math.ceil((res.data.total || 0) / 10)
  } catch (e) {
    ElMessage.error('获取商品列表失败')
  }
}

function handleSearch() { page.value = 1; fetchProducts() }
function changePage(p) { page.value = p; fetchProducts() }
function viewProduct(id) { router.push(`/products/${id}`) }

async function toggleProductStatus(product) {
  const newStatus = product.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '上架' : '下架'
  try {
    await ElMessageBox.confirm(`确定要${statusText}商品 "${product.productName}" 吗？`, '操作确认', { confirmButtonText: statusText, cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  try {
    await updateProductStatus(product.id, newStatus)
    ElMessage.success(`商品已${statusText}`)
    fetchProducts()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？此操作不可恢复。', '操作确认', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  try {
    await deleteAdminProduct(id)
    ElMessage.success('商品已删除')
    fetchProducts()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>
