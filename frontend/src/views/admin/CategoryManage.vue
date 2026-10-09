<template>
  <div>
    <!-- Page Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-[#09090B] tracking-tight">分类管理</h1>
        <p class="text-sm text-[#3F3F46] mt-0.5">管理商品分类</p>
      </div>
      <button @click="showDialog = true" class="admin-btn-primary">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
        添加分类
      </button>
    </div>

    <!-- Table Card -->
    <div class="bg-white rounded-2xl border border-gray-200/80 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-gray-100">
              <th class="admin-th">ID</th>
              <th class="admin-th">分类名称</th>
              <th class="admin-th">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="3" class="py-16 text-center text-[#3F3F46] text-sm">
                <div class="flex items-center justify-center gap-2">
                  <svg class="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"/></svg>
                  加载中...
                </div>
              </td>
            </tr>
            <tr v-else-if="categories.length === 0">
              <td colspan="3" class="py-16 text-center text-[#3F3F46] text-sm">暂无分类数据</td>
            </tr>
            <tr v-for="item in categories" :key="item.id" class="admin-row">
              <td class="admin-td text-[#3F3F46]">{{ item.id }}</td>
              <td class="admin-td font-medium text-[#09090B]">{{ item.categoryName }}</td>
              <td class="admin-td">
                <div class="flex items-center gap-1">
                  <button @click="handleEdit(item)" class="admin-icon-btn" title="编辑"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg></button>
                  <button @click="handleDelete(item.id)" class="admin-icon-btn admin-icon-btn-danger" title="删除"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg></button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal -->
    <Teleport to="body">
      <Transition name="modal">
        <div v-if="showDialog" class="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div class="absolute inset-0 bg-black/30 backdrop-blur-sm" @click="closeModal"></div>
          <div class="modal-panel relative bg-white rounded-2xl w-full max-w-sm shadow-2xl overflow-hidden">
            <div class="p-6">
              <h3 class="text-base font-bold text-[#09090B] mb-6">{{ editingId ? '编辑分类' : '添加分类' }}</h3>
              <div class="form-group mb-6">
                <label class="form-label">分类名称</label>
                <input
                  v-model="newName"
                  type="text"
                  class="admin-input"
                  placeholder="请输入分类名称"
                  @keyup.enter="handleCreate"
                />
              </div>
              <div class="flex justify-end gap-3">
                <button @click="closeModal" class="admin-btn-ghost">取消</button>
                <button @click="handleSave" class="admin-btn-primary" :disabled="!newName.trim()">确定</button>
              </div>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategories, createCategory, deleteCategory, updateCategory } from '@/api/category'
import { ElMessage, ElMessageBox } from 'element-plus'

const categories = ref([])
const loading = ref(false)
const showDialog = ref(false)
const editingId = ref(null)
const newName = ref('')

const loadCategories = async () => {
  loading.value = true
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleEdit = (item) => {
  editingId.value = item.id
  newName.value = item.categoryName
  showDialog.value = true
}

const closeModal = () => {
  showDialog.value = false
  editingId.value = null
  newName.value = ''
}

const handleSave = async () => {
  if (!newName.value.trim()) return
  try {
    if (editingId.value) {
      await updateCategory(editingId.value, { categoryName: newName.value.trim() })
      ElMessage.success('分类已更新')
    } else {
      await createCategory({ categoryName: newName.value.trim() })
      ElMessage.success('分类已添加')
    }
    closeModal()
    await loadCategories()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || (editingId.value ? '更新失败' : '添加失败'))
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '操作确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await deleteCategory(id)
    ElMessage.success('已删除')
    categories.value = categories.value.filter(c => c.id !== id)
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '删除失败')
  }
}

onMounted(loadCategories)
</script>

<style scoped>
.form-group {
  display: flex;
  flex-direction: column;
}
.form-label {
  font-size: 0.8125rem;
  font-weight: 600;
  color: #09090B;
  margin-bottom: 0.375rem;
}
.modal-enter-active { transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1); }
.modal-leave-active { transition: all 0.2s ease-in; }
.modal-enter-from, .modal-leave-to { opacity: 0; }
.modal-enter-from .modal-panel { transform: translateY(16px) scale(0.97); }
.modal-leave-to .modal-panel { transform: translateY(8px) scale(0.98); }
.modal-panel { transition: transform 0.3s cubic-bezier(0.23, 1, 0.32, 1); }
</style>
