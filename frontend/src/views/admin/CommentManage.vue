<template>
  <div>
    <!-- Page Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-[#09090B] tracking-tight">评论管理</h1>
        <p class="text-sm text-[#3F3F46] mt-0.5">查看和管理用户评论</p>
      </div>
    </div>

    <!-- Table Card -->
    <div class="bg-white rounded-2xl border border-gray-200/80 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-gray-100">
              <th class="admin-th">ID</th>
              <th class="admin-th">用户</th>
              <th class="admin-th">商品</th>
              <th class="admin-th">内容</th>
              <th class="admin-th">时间</th>
              <th class="admin-th">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="6" class="py-16 text-center text-[#3F3F46] text-sm">
                <div class="flex items-center justify-center gap-2">
                  <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
                  加载中...
                </div>
              </td>
            </tr>
            <tr v-else-if="comments.length === 0">
              <td colspan="6" class="py-16 text-center text-[#3F3F46] text-sm">暂无评论数据</td>
            </tr>
            <tr v-for="comment in comments" :key="comment.id" class="admin-row">
              <td class="admin-td text-[#3F3F46]">#{{ comment.id }}</td>
              <td class="admin-td font-medium text-[#09090B]">{{ comment.nickname || comment.username || '-' }}</td>
              <td class="admin-td text-[#3F3F46] max-w-[150px] truncate">{{ comment.productName || '-' }}</td>
              <td class="admin-td text-[#3F3F46] max-w-[200px] truncate">{{ comment.content }}</td>
              <td class="admin-td text-[#3F3F46] text-xs">{{ formatDateTime(comment.createTime) }}</td>
              <td class="admin-td">
                <button @click="handleDelete(comment.id)" class="admin-icon-btn admin-icon-btn-danger" title="删除"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg></button>
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminComments, deleteAdminComment } from '@/api/admin'
import Pagination from '@/components/ui/Pagination.vue'

const comments = ref([])
const page = ref(1)
const totalPages = ref(1)
const loading = ref(false)

function formatDateTime(time) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

async function fetchComments() {
  loading.value = true
  try {
    const res = await getAdminComments({ page: page.value, pageSize: 10 })
    comments.value = res.data.list || []
    totalPages.value = Math.ceil((res.data.total || 0) / 10)
  } catch (e) {
    ElMessage.error('获取评论列表失败')
  } finally {
    loading.value = false
  }
}

function changePage(p) {
  page.value = p
  fetchComments()
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '操作确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return // 用户点取消
  }
  try {
    await deleteAdminComment(id)
    ElMessage.success('评论已删除')
    fetchComments()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(fetchComments)
</script>
