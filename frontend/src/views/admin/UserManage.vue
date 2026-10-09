<template>
  <div>
    <!-- Page Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-[#09090B] tracking-tight">用户管理</h1>
        <p class="text-sm text-[#3F3F46] mt-0.5">管理平台所有注册用户</p>
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
              placeholder="搜索用户名/昵称..."
              class="admin-input"
              @keyup.enter="handleSearch"
            />
          </div>
          <button @click="handleSearch" class="admin-btn-primary">
            搜索
          </button>
        </div>
      </div>
    </div>

    <!-- Table Card -->
    <div class="bg-white rounded-2xl border border-gray-200/80 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-gray-100">
              <th class="admin-th">ID</th>
              <th class="admin-th">用户名</th>
              <th class="admin-th">昵称</th>
              <th class="admin-th">角色</th>
              <th class="admin-th">状态</th>
              <th class="admin-th">注册时间</th>
              <th class="admin-th">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="users.length === 0">
              <td colspan="7" class="py-16 text-center text-[#3F3F46] text-sm">暂无用户数据</td>
            </tr>
            <tr v-for="user in users" :key="user.id" class="admin-row">
              <td class="admin-td text-[#3F3F46]">{{ user.id }}</td>
              <td class="admin-td font-medium text-[#09090B]">{{ user.username }}</td>
              <td class="admin-td">{{ user.nickname || '-' }}</td>
              <td class="admin-td">
                <span :class="user.role === 1 ? 'admin-badge-dark' : 'admin-badge-gray'">
                  {{ user.role === 1 ? '管理员' : '普通用户' }}
                </span>
              </td>
              <td class="admin-td">
                <span :class="user.status === 1 ? 'admin-badge-green' : 'admin-badge-red'">
                  {{ user.status === 1 ? '正常' : '冻结' }}
                </span>
              </td>
              <td class="admin-td text-[#3F3F46] text-xs">{{ formatDateTime(user.registerTime) }}</td>
              <td class="admin-td">
                <div class="flex items-center gap-1">
                  <button v-if="user.role !== 1" @click="handleSetAdmin(user)" class="admin-action-btn">设为管理员</button>
                  <button v-if="user.role === 1" @click="handleCancelAdmin(user)" class="admin-action-btn">取消管理员</button>
                  <button v-if="user.status === 1" @click="handleFreeze(user)" class="admin-action-btn-danger">冻结</button>
                  <button v-else @click="handleUnfreeze(user)" class="admin-action-btn-green">解冻</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="flex justify-center py-4 border-t border-gray-100">
        <Pagination :current-page="page" :total-pages="totalPages" @change="changePage" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, updateUserRole, freezeUser, unfreezeUser } from '@/api/admin'
import Pagination from '@/components/ui/Pagination.vue'

const users = ref([])
const page = ref(1)
const totalPages = ref(1)
const searchKeyword = ref('')

function formatDateTime(time) {
  if (!time) return '-'
  const d = new Date(time)
  return d.toLocaleString('zh-CN')
}

async function fetchUsers() {
  try {
    const res = await getUsers({ page: page.value, pageSize: 10, keyword: searchKeyword.value || undefined })
    users.value = res.data.list || []
    totalPages.value = Math.ceil((res.data.total || 0) / 10)
  } catch (e) {
    ElMessage.error('获取用户列表失败')
  }
}

function handleSearch() {
  page.value = 1
  fetchUsers()
}

function changePage(p) {
  page.value = p
  fetchUsers()
}

async function handleSetAdmin(user) {
  try {
    await ElMessageBox.confirm(`确定将 ${user.username} 设为管理员吗？`, '操作确认', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  try {
    await updateUserRole(user.id, 1)
    ElMessage.success('已设为管理员')
    fetchUsers()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function handleCancelAdmin(user) {
  try {
    await ElMessageBox.confirm(`确定取消 ${user.username} 的管理员权限吗？`, '操作确认', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  try {
    await updateUserRole(user.id, 0)
    ElMessage.success('已取消管理员')
    fetchUsers()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function handleFreeze(user) {
  try {
    await ElMessageBox.confirm(`确定冻结用户 ${user.username} 吗？`, '操作确认', { confirmButtonText: '冻结', cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  try {
    await freezeUser(user.id)
    ElMessage.success('用户已冻结')
    fetchUsers()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function handleUnfreeze(user) {
  try {
    await ElMessageBox.confirm(`确定解冻用户 ${user.username} 吗？`, '操作确认', { confirmButtonText: '解冻', cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  try {
    await unfreezeUser(user.id)
    ElMessage.success('用户已解冻')
    fetchUsers()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

onMounted(fetchUsers)
</script>
