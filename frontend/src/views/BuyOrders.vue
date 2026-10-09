<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">我买到的</h1>
    
    <div v-if="orders.length === 0" class="text-center py-12">
      <div class="text-gray-500 text-lg">暂无购买记录</div>
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="order in orders"
        :key="order.id"
        class="bg-white rounded-xl border border-gray-200 p-4"
      >
        <div class="flex items-start gap-4">
          <div class="w-20 h-20 bg-gray-100 rounded-lg flex items-center justify-center flex-shrink-0">
            <img v-if="order.productImage" :src="order.productImage" class="w-full h-full object-cover rounded-lg" />
            <span v-else class="text-2xl">📦</span>
          </div>
          <div class="flex-1">
            <h3 class="text-gray-900 font-medium">{{ order.productName }}</h3>
            <p class="text-gray-500 text-sm mt-1">卖家：{{ order.sellerNickname || '-' }}</p>
            <p class="text-primary-500 font-bold mt-1">¥{{ order.dealPrice }}</p>
            <div class="flex items-center justify-between mt-3">
              <div class="flex items-center gap-3">
                <span :class="['tag', getStatusClass(order.status)]">
                  {{ getStatusText(order.status) }}
                </span>
                <!-- 待支付：可支付或取消 -->
                <template v-if="order.status === 0">
                  <button
                    class="px-4 py-1.5 bg-primary-500 text-white text-sm rounded-lg hover:bg-primary-600 transition-colors"
                    @click="handlePay(order)"
                  >
                    立即支付
                  </button>
                  <button
                    class="px-4 py-1.5 bg-gray-100 text-gray-600 text-sm rounded-lg hover:bg-gray-200 transition-colors"
                    @click="handleCancel(order)"
                  >
                    取消订单
                  </button>
                </template>
                <!-- 待收货：可确认收货 -->
                <template v-if="order.status === 2">
                  <button
                    class="px-4 py-1.5 bg-green-500 text-white text-sm rounded-lg hover:bg-green-600 transition-colors"
                    @click="handleConfirm(order)"
                  >
                    确认收货
                  </button>
                </template>
              </div>
              <span class="text-xs text-gray-500">{{ formatTime(order.createTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBuyOrders, payOrder, confirmOrder, cancelOrder } from '@/api/order'

const orders = ref([])

function formatTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

function getStatusText(status) {
  const map = { 0: '待支付', 1: '已支付', 2: '已发货', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

function getStatusClass(status) {
  const map = { 0: 'tag-yellow', 1: 'tag-blue', 2: 'tag-orange', 3: 'tag-green', 4: 'tag-red' }
  return map[status] || 'tag-gray'
}

async function fetchOrders() {
  try {
    const res = await getBuyOrders()
    orders.value = res.data?.list || []
  } catch (e) {
    console.error('获取购买订单失败:', e)
  }
}

async function handlePay(order) {
  try {
    await ElMessageBox.confirm(`确认支付订单 ¥${order.dealPrice}？`, '确认支付', { type: 'info' })
    await payOrder(order.id)
    ElMessage.success('支付成功')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '支付失败')
  }
}

async function handleConfirm(order) {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '确认收货', { type: 'info' })
    await confirmOrder(order.id)
    ElMessage.success('已确认收货')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '确认失败')
  }
}

async function handleCancel(order) {
  try {
    await ElMessageBox.confirm('确认取消此订单？', '取消订单', { type: 'warning' })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '取消失败')
  }
}

onMounted(fetchOrders)
</script>
