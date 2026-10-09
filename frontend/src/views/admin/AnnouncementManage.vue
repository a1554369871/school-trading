<template>
  <div>
    <!-- Page Header -->
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-xl font-bold text-[#09090B] tracking-tight">公告管理</h1>
        <p class="text-sm text-[#3F3F46] mt-0.5">发布和管理平台公告</p>
      </div>
      <button @click="showAddModal = true" class="admin-btn-primary">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
        新增公告
      </button>
    </div>

    <!-- Table Card -->
    <div class="bg-white rounded-2xl border border-gray-200/80 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="border-b border-gray-100">
              <th class="admin-th">ID</th>
              <th class="admin-th">标题</th>
              <th class="admin-th">状态</th>
              <th class="admin-th">发布时间</th>
              <th class="admin-th">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="announcements.length === 0">
              <td colspan="5" class="py-16 text-center text-[#3F3F46] text-sm">暂无公告数据</td>
            </tr>
            <tr v-for="item in announcements" :key="item.id" class="admin-row">
              <td class="admin-td text-[#3F3F46]">{{ item.id }}</td>
              <td class="admin-td font-medium text-[#09090B]">{{ item.title }}</td>
              <td class="admin-td">
                <span :class="item.status === 1 ? 'admin-badge-green' : 'admin-badge-gray'">
                  {{ item.status === 1 ? '已发布' : '草稿' }}
                </span>
              </td>
              <td class="admin-td text-[#3F3F46] text-xs">{{ (item.publishTime || '').replace('T', ' ') }}</td>
              <td class="admin-td">
                <div class="flex items-center gap-1">
                  <button @click="editAnnouncement(item)" class="admin-icon-btn" title="编辑"><svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg></button>
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
        <div v-if="showAddModal" class="fixed inset-0 z-50 flex items-center justify-center p-4">
          <div class="absolute inset-0 bg-black/30 backdrop-blur-sm" @click="closeModal"></div>
          <div class="modal-panel relative bg-white rounded-2xl w-full max-w-lg shadow-2xl overflow-hidden">
            <div class="p-6">
              <h3 class="text-base font-bold text-[#09090B] mb-6">
                {{ editingId ? '编辑公告' : '新增公告' }}
              </h3>
              <form @submit.prevent="handleSave" class="space-y-4">
                <div class="form-group">
                  <label class="form-label">标题</label>
                  <input v-model="form.title" type="text" class="admin-input" required />
                </div>
                <div class="form-group">
                  <label class="form-label">内容</label>
                  <textarea v-model="form.content" rows="5" class="admin-input resize-none" required></textarea>
                </div>
                <div class="flex items-center gap-2.5">
                  <input v-model="form.status" type="checkbox" id="publish" class="form-checkbox" />
                  <label for="publish" class="text-sm text-[#3F3F46] cursor-pointer">立即发布</label>
                </div>
                <div class="flex justify-end gap-3 pt-4">
                  <button type="button" @click="closeModal" class="admin-btn-ghost">取消</button>
                  <button type="submit" class="admin-btn-primary" :disabled="isSaving">
                    {{ isSaving ? '保存中...' : '保存' }}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAnnouncements, createAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/admin'

const announcements = ref([])
const showAddModal = ref(false)
const editingId = ref(null)
const isSaving = ref(false)

const form = reactive({ title: '', content: '', status: 0 })

function editAnnouncement(item) {
  editingId.value = item.id
  form.title = item.title
  form.content = item.content
  form.status = item.status
  showAddModal.value = true
}

function closeModal() {
  showAddModal.value = false
  editingId.value = null
  form.title = ''
  form.content = ''
  form.status = 0
}

async function handleSave() {
  isSaving.value = true
  try {
    const data = { title: form.title, content: form.content, status: form.status ? 1 : 0 }
    if (editingId.value) {
      await updateAnnouncement(editingId.value, data)
    } else {
      await createAnnouncement(data)
    }
    ElMessage.success(editingId.value ? '更新成功' : '创建成功')
    closeModal()
    fetchAnnouncements()
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    isSaving.value = false
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除这条公告吗？', '操作确认', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await deleteAnnouncement(id)
    ElMessage.success('删除成功')
    fetchAnnouncements()
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

async function fetchAnnouncements() {
  try {
    const res = await getAnnouncements()
    announcements.value = res.data?.list || []
  } catch (e) {
    console.error('获取公告失败:', e)
  }
}

onMounted(fetchAnnouncements)
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
.form-checkbox {
  width: 1rem;
  height: 1rem;
  border-radius: 0.25rem;
  border: 1.5px solid #d4d4d8;
  accent-color: #18181B;
  cursor: pointer;
}
.modal-enter-active { transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1); }
.modal-leave-active { transition: all 0.2s ease-in; }
.modal-enter-from, .modal-leave-to { opacity: 0; }
.modal-enter-from .modal-panel { transform: translateY(16px) scale(0.97); }
.modal-leave-to .modal-panel { transform: translateY(8px) scale(0.98); }
.modal-panel { transition: transform 0.3s cubic-bezier(0.23, 1, 0.32, 1); }
</style>
