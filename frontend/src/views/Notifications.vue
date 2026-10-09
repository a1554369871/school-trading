<template>
  <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="bg-white rounded-xl border border-gray-200 overflow-hidden">
      <div class="p-6 border-b border-gray-200 flex justify-between items-center">
        <h1 class="text-2xl font-bold text-gray-900">通知</h1>
        <el-button @click="markAll" size="small" :disabled="notifications.length === 0">全部已读</el-button>
      </div>

      <div v-if="notifications.length === 0" class="p-12 text-center text-gray-500">
        <svg class="w-16 h-16 mx-auto mb-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
        </svg>
        <p>暂无通知</p>
      </div>

      <div v-else class="divide-y divide-gray-50">
        <div
          v-for="n in notifications"
          :key="n.id"
          :class="['p-4 hover:bg-gray-50 transition-colors', !n.isRead ? 'bg-blue-900/20' : '']"
        >
          <div class="flex justify-between items-start">
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-1">
                <span class="font-medium text-gray-900">{{ n.title }}</span>
                <el-tag size="small" v-if="!n.isRead" type="danger">未读</el-tag>
              </div>
              <p class="text-sm text-gray-500">{{ n.content }}</p>
              <p class="text-xs text-gray-500 mt-1">{{ formatTime(n.createTime) }}</p>
            </div>
            <div class="flex items-center gap-2">
              <el-button v-if="!n.isRead" size="small" text @click="markRead(n.id)">标为已读</el-button>
              <el-button size="small" text type="danger" @click="handleDelete(n.id)">删除</el-button>
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
import { getNotifications, markAsRead, markAllAsRead, deleteNotification } from '@/api/message'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const notifications = ref([])

async function load() {
  try {
    const res = await getNotifications()
    notifications.value = res.data?.list || []
  } catch { /* ignore */ }
}

async function markRead(id) {
  try {
    await markAsRead(id)
    await load()
    userStore.fetchUnreadCount()
  } catch { /* ignore */ }
}

async function markAll() {
  try {
    await markAllAsRead()
    ElMessage.success('已全部标为已读')
    await load()
    userStore.setUnreadCount(0)
  } catch { /* ignore */ }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除这条通知吗？', '操作确认', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  try {
    await deleteNotification(id)
    ElMessage.success('已删除')
    await load()
    userStore.fetchUnreadCount()
  } catch { /* ignore */ }
}

function formatTime(time) {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

onMounted(load)
</script>
