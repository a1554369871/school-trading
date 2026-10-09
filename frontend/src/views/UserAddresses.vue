<template>
  <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="bg-white rounded-xl p-6 border border-gray-200">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold text-gray-900">地址管理</h1>
        <button @click="showAddModal = true" class="btn-primary">
          + 新增地址
        </button>
      </div>

      <!-- 地址列表 -->
      <div v-if="loading" class="text-center py-12">
        <div
          class="animate-spin w-8 h-8 border-4 border-white border-t-transparent rounded-full mx-auto"
        ></div>
      </div>

      <div v-else-if="addresses.length === 0" class="text-center py-12">
        <p class="text-gray-500">暂无地址</p>
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="addr in addresses"
          :key="addr.id"
          class="border border-gray-200 rounded-lg p-4"
        >
          <div class="flex items-center justify-between">
            <div>
              <div class="flex items-center gap-2 mb-1">
                <span class="font-medium text-gray-900">{{
                  addr.consignee
                }}</span>
                <span class="text-gray-500 text-sm">{{ addr.phone }}</span>
                <span v-if="addr.isDefault === 1" class="tag tag-green text-xs"
                  >默认</span
                >
              </div>
              <p class="text-sm text-gray-500">{{ addr.address }}</p>
            </div>
            <div class="flex gap-2">
              <button
                @click="editAddress(addr)"
                class="text-sm text-primary-500 hover:text-primary-600"
              >
                编辑
              </button>
              <button
                @click="handleDelete(addr.id)"
                class="text-sm text-red-500 hover:text-red-600"
              >
                删除
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增/编辑地址弹窗 -->
    <div
      v-if="showAddModal"
      class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
    >
      <div
        class="bg-white rounded-xl p-6 w-full max-w-md mx-4 border border-gray-200"
      >
        <h3 class="text-lg font-bold text-gray-800 mb-4">
          {{ editingId ? "编辑地址" : "新增地址" }}
        </h3>

        <div class="space-y-4">
          <div>
            <label class="block text-sm text-gray-600 mb-1">收货人</label>
            <input
              v-model="form.consignee"
              type="text"
              class="input-field"
              placeholder="请输入收货人姓名"
            />
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">手机号</label>
            <input
              v-model="form.phone"
              type="tel"
              class="input-field"
              placeholder="请输入手机号"
            />
          </div>
          <div>
            <label class="block text-sm text-gray-600 mb-1">详细地址</label>
            <textarea
              v-model="form.address"
              rows="3"
              class="input-field resize-none"
              placeholder="请输入详细地址"
            ></textarea>
          </div>
          <div class="flex items-center gap-2">
            <input
              v-model="form.isDefault"
              type="checkbox"
              :true-value="1"
              :false-value="0"
              id="isDefault"
              class="rounded"
            />
            <label for="isDefault" class="text-sm text-gray-500"
              >设为默认地址</label
            >
          </div>
        </div>

        <div class="flex gap-4 mt-6">
          <button
            @click="closeModal"
            class="flex-1 h-10 border border-gray-200 text-gray-600 rounded-lg hover:bg-gray-50"
          >
            取消
          </button>
          <button
            @click="handleSave"
            class="flex-1 h-10 bg-primary-500 text-gray-900 rounded-lg hover:bg-primary-600 disabled:opacity-50"
            :disabled="
              isSaving || !form.consignee || !form.phone || !form.address
            "
          >
            {{ isSaving ? "保存中..." : "保存" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  getAddresses,
  addAddress,
  updateAddress,
  deleteAddress,
} from "@/api/address";

const addresses = ref([]);
const loading = ref(true);
const showAddModal = ref(false);
const editingId = ref(null);
const isSaving = ref(false);

const form = reactive({
  consignee: "",
  phone: "",
  address: "",
  isDefault: 0,
});

function editAddress(addr) {
  editingId.value = addr.id;
  Object.assign(form, {
    consignee: addr.consignee,
    phone: addr.phone,
    address: addr.address,
    isDefault: addr.isDefault,
  });
  showAddModal.value = true;
}

function closeModal() {
  showAddModal.value = false;
  editingId.value = null;
  Object.assign(form, {
    consignee: "",
    phone: "",
    address: "",
    isDefault: 0,
  });
}

async function handleSave() {
  if (!form.consignee.trim()) {
    ElMessage.warning("请输入收货人姓名");
    return;
  }
  if (!form.phone.trim()) {
    ElMessage.warning("请输入手机号");
    return;
  }
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.warning("请输入正确的11位手机号");
    return;
  }
  if (!form.address.trim()) {
    ElMessage.warning("请输入详细地址");
    return;
  }

  isSaving.value = true;
  try {
    if (editingId.value) {
      await updateAddress(editingId.value, form);
      ElMessage.success("地址更新成功");
    } else {
      await addAddress(form);
      ElMessage.success("地址添加成功");
    }
    closeModal();
    await fetchAddresses();
  } catch (e) {
    ElMessage.error(e.message || "保存失败");
  } finally {
    isSaving.value = false;
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm(
      "确定要删除这个地址吗？删除后不可恢复。",
      "删除确认",
      {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning",
      },
    );
  } catch {
    return; // 用户点击取消
  }
  try {
    await deleteAddress(id);
    await fetchAddresses();
    ElMessage.success("地址删除成功");
  } catch (e) {
    ElMessage.error(e.message || "删除失败");
  }
}

async function fetchAddresses() {
  loading.value = true;
  try {
    const res = await getAddresses();
    addresses.value = res.data || [];
  } catch (e) {
    console.error("获取地址失败:", e);
  } finally {
    loading.value = false;
  }
}

onMounted(fetchAddresses);
</script>
