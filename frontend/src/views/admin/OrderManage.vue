<template>
  <div>
    <!-- Page Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-[#09090B] tracking-tight">订单管理</h1>
        <p class="text-sm text-[#3F3F46] mt-0.5">查看和管理所有交易订单</p>
      </div>
      <div class="flex items-center gap-3">
        <div class="relative" ref="statusRef">
          <button
            type="button"
            @click="statusOpen = !statusOpen"
            class="h-9 px-3 rounded-lg border border-gray-200 bg-white text-sm text-left flex items-center justify-between gap-2 focus:outline-none focus:border-gray-400 focus:ring-2 focus:ring-gray-900/5 transition-all duration-200 cursor-pointer min-w-[120px]"
            :class="statusFilter !== '' ? 'text-gray-900' : 'text-gray-400'"
          >
            <span class="truncate">{{ statusLabel || '全部状态' }}</span>
            <svg class="w-4 h-4 shrink-0 text-gray-400 transition-transform duration-200" :class="{ 'rotate-180': statusOpen }" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 9l6 6 6-6"/></svg>
          </button>
          <Transition
            enter-active-class="transition ease-out duration-150"
            enter-from-class="opacity-0 scale-95"
            enter-to-class="opacity-100 scale-100"
            leave-active-class="transition ease-in duration-100"
            leave-from-class="opacity-100 scale-100"
            leave-to-class="opacity-0 scale-95"
          >
            <div v-if="statusOpen" class="absolute z-50 mt-1.5 right-0 rounded-xl border border-gray-200 bg-white shadow-lg p-1 min-w-[140px]" style="transform-origin: top;">
              <div
                v-for="opt in statusOptions"
                :key="opt.value"
                @click="selectStatus(opt)"
                class="flex items-center justify-between px-3 py-2 rounded-lg text-sm cursor-pointer transition-colors duration-150"
                :class="statusFilter === opt.value ? 'bg-gray-100 text-gray-900 font-medium' : 'text-gray-700 hover:bg-gray-50'"
              >
                <span>{{ opt.label }}</span>
                <svg v-if="statusFilter === opt.value" class="w-4 h-4 text-gray-900" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
              </div>
            </div>
          </Transition>
        </div>
        <button @click="fetchOrders()" class="admin-btn-outline">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/></svg>
          刷新
        </button>
      </div>
    </div>

    <!-- Table Card -->
    <div class="bg-white rounded-2xl border border-gray-200/80 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-gray-100">
              <th class="admin-th">订单号</th>
              <th class="admin-th">商品</th>
              <th class="admin-th">买家</th>
              <th class="admin-th">卖家</th>
              <th class="admin-th">金额</th>
              <th class="admin-th">状态</th>
              <th class="admin-th">创建时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="orders.length === 0">
              <td colspan="7" class="py-16 text-center text-[#3F3F46] text-sm">暂无订单数据</td>
            </tr>
            <tr v-for="order in orders" :key="order.id" class="admin-row">
              <td class="admin-td text-[#3F3F46] font-mono text-xs">{{ order.orderNo || '#' + order.id }}</td>
              <td class="admin-td font-medium text-[#09090B] max-w-[150px] truncate">{{ order.productName || '-' }}</td>
              <td class="admin-td">{{ order.buyerNickname || '-' }}</td>
              <td class="admin-td">{{ order.sellerNickname || '-' }}</td>
              <td class="admin-td font-semibold text-[#09090B]">¥{{ order.dealPrice }}</td>
              <td class="admin-td">
                <span :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
              </td>
              <td class="admin-td text-[#3F3F46] text-xs">{{ formatDateTime(order.createTime) }}</td>
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
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { getAdminOrders } from '@/api/order'
import Pagination from '@/components/ui/Pagination.vue'

const orders = ref([])
const allOrders = ref([])
const page = ref(1)
const totalPages = ref(1)
const statusFilter = ref('')
const statusOpen = ref(false)
const statusRef = ref(null)

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: '0', label: '待付款' },
  { value: '1', label: '待发货' },
  { value: '2', label: '待收货' },
  { value: '3', label: '已完成' },
  { value: '4', label: '已取消' }
]

const statusLabel = computed(() => {
  const opt = statusOptions.find(o => o.value === statusFilter.value)
  return opt ? opt.label : ''
})

function selectStatus(opt) {
  statusFilter.value = opt.value
  statusOpen.value = false
  handleFilter()
}

function onClickOutside(e) {
  if (statusRef.value && !statusRef.value.contains(e.target)) {
    statusOpen.value = false
  }
}

onMounted(() => document.addEventListener('click', onClickOutside))
onBeforeUnmount(() => document.removeEventListener('click', onClickOutside))

function formatDateTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

function getStatusText(status) {
  const map = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

function getStatusClass(status) {
  const map = {
    0: 'admin-badge-yellow',
    1: 'admin-badge-blue',
    2: 'admin-badge-dark',
    3: 'admin-badge-green',
    4: 'admin-badge-gray'
  }
  return map[status] || 'admin-badge-gray'
}

async function fetchOrders() {
  try {
    const res = await getAdminOrders()
    allOrders.value = res.data?.list || []
    applyFilter()
  } catch (e) {
    console.error('获取订单失败:', e)
  }
}

function applyFilter() {
  let filtered = [...allOrders.value]
  if (statusFilter.value !== '') {
    filtered = filtered.filter(o => o.status === Number(statusFilter.value))
  }
  const total = filtered.length
  const start = (page.value - 1) * 10
  orders.value = filtered.slice(start, start + 10)
  totalPages.value = Math.ceil(total / 10)
}

function handleFilter() { page.value = 1; applyFilter() }
function changePage(p) { page.value = p; applyFilter() }

onMounted(fetchOrders)
</script>
