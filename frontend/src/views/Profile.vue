<template>
  <div class="min-h-screen bg-[#FAFAFA]">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8 fade-in">
      <!-- 用户信息卡片 -->
      <div class="profile-card mb-6">
        <div class="profile-card-bg"></div>
        <div class="relative flex items-center gap-6 p-6 md:p-8">
          <div
            class="relative group cursor-pointer flex-shrink-0"
            @click="triggerAvatarUpload"
          >
            <div class="avatar-ring">
              <div
                class="w-20 h-20 md:w-24 md:h-24 bg-gray-100 rounded-full flex items-center justify-center overflow-hidden"
              >
                <img
                  v-if="user?.avatar"
                  :src="user.avatar"
                  class="w-full h-full rounded-full object-cover"
                />
                <span
                  v-else
                  class="text-[#18181B] text-2xl md:text-3xl font-bold"
                  >{{ user?.nickname?.charAt(0) || "U" }}</span
                >
              </div>
            </div>
            <div
              class="absolute inset-0 bg-black/50 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all duration-300"
            >
              <svg
                class="w-5 h-5 text-white"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0018.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z"
                />
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M15 13a3 3 0 11-6 0 3 3 0 016 0z"
                />
              </svg>
            </div>
            <input
              ref="avatarInput"
              type="file"
              accept="image/*"
              class="hidden"
              @change="handleAvatarUpload"
            />
          </div>
          <div class="flex-1 min-w-0">
            <h1
              class="text-xl md:text-2xl font-bold text-[#09090B] tracking-tight"
            >
              {{ user?.nickname || "用户" }}
            </h1>
            <p class="text-[#3F3F46] mt-0.5 text-sm">@{{ user?.username }}</p>
            <div
              class="flex flex-wrap items-center gap-x-4 gap-y-1 mt-3 text-xs text-[#3F3F46]"
            >
              <span v-if="user?.phone" class="flex items-center gap-1.5">
                <svg
                  class="w-3.5 h-3.5 opacity-60"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
                  />
                </svg>
                {{ user.phone }}
              </span>
              <span v-if="user?.email" class="flex items-center gap-1.5">
                <svg
                  class="w-3.5 h-3.5 opacity-60"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                  />
                </svg>
                {{ user.email }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Tab 导航 + 内容 -->
      <div
        class="bg-white rounded-2xl border border-gray-200/80 overflow-hidden shadow-sm"
      >
        <div class="border-b border-gray-100">
          <nav class="flex">
            <button
              v-for="tab in tabs"
              :key="tab.key"
              @click="activeTab = tab.key"
              class="flex-1 py-3.5 px-4 text-center text-sm font-medium transition-all duration-300 relative cursor-pointer"
              :class="
                activeTab === tab.key
                  ? 'text-[#18181B]'
                  : 'text-[#3F3F46] hover:text-[#09090B]'
              "
            >
              <div class="flex items-center justify-center gap-2">
                <span v-html="tab.icon" class="w-4 h-4"></span>
                <span>{{ tab.label }}</span>
              </div>
              <div
                v-if="activeTab === tab.key"
                class="tab-indicator absolute bottom-0 left-0 right-0 h-[2px] bg-[#18181B]"
              ></div>
            </button>
          </nav>
        </div>

        <div class="p-6 md:p-8">
          <!-- 个人信息 Tab -->
          <div v-if="activeTab === 'profile'" class="tab-content">
            <form @submit.prevent="handleUpdate" class="space-y-6">
              <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                <div class="form-group">
                  <label class="form-label">昵称</label>
                  <input
                    v-model="form.nickname"
                    type="text"
                    class="form-input"
                    placeholder="请输入昵称"
                  />
                </div>
                <div class="form-group">
                  <label class="form-label">手机号</label>
                  <input
                    v-model="form.phone"
                    type="tel"
                    class="form-input"
                    placeholder="请输入手机号"
                  />
                </div>
                <div class="form-group md:col-span-2">
                  <label class="form-label">邮箱</label>
                  <input
                    v-model="form.email"
                    type="email"
                    class="form-input"
                    placeholder="请输入邮箱"
                  />
                </div>
              </div>
              <div class="flex justify-end pt-2">
                <button type="submit" class="btn-modern" :disabled="isUpdating">
                  <span v-if="isUpdating" class="flex items-center gap-2">
                    <svg
                      class="animate-spin w-4 h-4"
                      fill="none"
                      viewBox="0 0 24 24"
                    >
                      <circle
                        class="opacity-25"
                        cx="12"
                        cy="12"
                        r="10"
                        stroke="currentColor"
                        stroke-width="4"
                      />
                      <path
                        class="opacity-75"
                        fill="currentColor"
                        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
                      />
                    </svg>
                    保存中...
                  </span>
                  <span v-else>保存修改</span>
                </button>
              </div>
            </form>
          </div>

          <!-- 地址管理 Tab -->
          <div v-if="activeTab === 'address'" class="tab-content">
            <div class="flex items-center justify-between mb-6">
              <h2 class="text-base font-semibold text-[#09090B]">收货地址</h2>
              <button
                @click="openAddAddress"
                class="btn-modern-outline text-sm"
              >
                <svg
                  class="w-4 h-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="2"
                    d="M12 4v16m8-8H4"
                  />
                </svg>
                新增地址
              </button>
            </div>

            <div v-if="addressLoading" class="text-center py-16">
              <div
                class="animate-spin w-8 h-8 border-[3px] border-gray-200 border-t-[#18181B] rounded-full mx-auto"
              ></div>
            </div>

            <div v-else-if="addresses.length === 0" class="text-center py-16">
              <div
                class="w-16 h-16 mx-auto mb-4 rounded-full bg-gray-100 flex items-center justify-center"
              >
                <svg
                  class="w-7 h-7 text-[#3F3F46]"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="1.5"
                    d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                  />
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    stroke-width="1.5"
                    d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                  />
                </svg>
              </div>
              <p class="text-[#09090B] font-medium">暂无收货地址</p>
              <p class="text-sm text-[#3F3F46] mt-1">点击上方按钮添加新地址</p>
            </div>

            <div v-else class="space-y-3">
              <div
                v-for="addr in addresses"
                :key="addr.id"
                class="address-card group"
              >
                <div class="flex items-start justify-between">
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2 mb-1.5">
                      <span class="font-semibold text-[#09090B] text-sm">{{
                        addr.consignee
                      }}</span>
                      <span class="text-[#3F3F46] text-xs">{{
                        addr.phone
                      }}</span>
                      <span v-if="addr.isDefault === 1" class="badge-default"
                        >默认</span
                      >
                    </div>
                    <p class="text-xs text-[#3F3F46] leading-relaxed">
                      {{ addr.address }}
                    </p>
                  </div>
                  <div class="flex items-center gap-1 ml-4">
                    <button
                      v-if="addr.isDefault !== 1"
                      @click="setDefault(addr)"
                      class="action-btn"
                    >
                      设为默认
                    </button>
                    <button
                      @click="editAddress(addr)"
                      class="admin-icon-btn"
                      title="编辑"
                    >
                      <svg
                        class="w-4 h-4"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                        />
                      </svg>
                    </button>
                    <button
                      @click="handleDeleteAddress(addr.id)"
                      class="admin-icon-btn admin-icon-btn-danger"
                      title="删除"
                    >
                      <svg
                        class="w-4 h-4"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                        />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 修改密码 Tab -->
          <div v-if="activeTab === 'password'" class="tab-content">
            <form
              @submit.prevent="handleChangePassword"
              class="space-y-6 max-w-md"
            >
              <div class="form-group">
                <label class="form-label">当前密码</label>
                <input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  class="form-input"
                  placeholder="请输入当前密码"
                  required
                />
              </div>
              <div class="form-group">
                <label class="form-label">新密码</label>
                <input
                  v-model="passwordForm.newPassword"
                  type="password"
                  class="form-input"
                  placeholder="请输入新密码（至少6位）"
                  required
                  minlength="6"
                />
              </div>
              <div class="form-group">
                <label class="form-label">确认新密码</label>
                <input
                  v-model="confirmPassword"
                  type="password"
                  class="form-input"
                  placeholder="请再次输入新密码"
                  required
                />
              </div>
              <div class="flex justify-end pt-2">
                <button
                  type="submit"
                  class="btn-modern"
                  :disabled="isChangingPassword"
                >
                  <span
                    v-if="isChangingPassword"
                    class="flex items-center gap-2"
                  >
                    <svg
                      class="animate-spin w-4 h-4"
                      fill="none"
                      viewBox="0 0 24 24"
                    >
                      <circle
                        class="opacity-25"
                        cx="12"
                        cy="12"
                        r="10"
                        stroke="currentColor"
                        stroke-width="4"
                      />
                      <path
                        class="opacity-75"
                        fill="currentColor"
                        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
                      />
                    </svg>
                    修改中...
                  </span>
                  <span v-else>修改密码</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- 地址编辑弹窗 -->
      <Teleport to="body">
        <Transition name="modal">
          <div
            v-if="showAddressModal"
            class="fixed inset-0 z-50 flex items-center justify-center p-4"
          >
            <div
              class="absolute inset-0 bg-black/30 backdrop-blur-sm"
              @click="closeAddressModal"
            ></div>
            <div
              class="modal-panel relative bg-white rounded-2xl w-full max-w-md shadow-2xl overflow-hidden"
            >
              <div class="p-6">
                <h3 class="text-base font-bold text-[#09090B] mb-6">
                  {{ editingAddressId ? "编辑地址" : "新增地址" }}
                </h3>
                <form @submit.prevent="handleSaveAddress" class="space-y-4">
                  <div class="form-group">
                    <label class="form-label">收货人</label>
                    <input
                      v-model="addressForm.consignee"
                      type="text"
                      class="form-input"
                      placeholder="请输入收货人姓名"
                      required
                    />
                  </div>
                  <div class="form-group">
                    <label class="form-label">手机号</label>
                    <input
                      v-model="addressForm.phone"
                      type="tel"
                      class="form-input"
                      placeholder="请输入手机号"
                      required
                    />
                  </div>
                  <div class="form-group">
                    <label class="form-label">详细地址</label>
                    <textarea
                      v-model="addressForm.address"
                      class="form-input resize-none"
                      rows="3"
                      placeholder="请输入详细地址"
                      required
                    ></textarea>
                  </div>
                  <div class="flex items-center gap-2.5">
                    <input
                      v-model="addressForm.isDefault"
                      :true-value="1"
                      :false-value="0"
                      type="checkbox"
                      id="isDefault"
                      class="form-checkbox"
                    />
                    <label
                      for="isDefault"
                      class="text-sm text-[#3F3F46] cursor-pointer"
                      >设为默认地址</label
                    >
                  </div>
                  <div class="flex justify-end gap-3 pt-4">
                    <button
                      type="button"
                      @click="closeAddressModal"
                      class="btn-ghost"
                    >
                      取消
                    </button>
                    <button
                      type="submit"
                      class="btn-modern"
                      :disabled="isAddressSaving"
                    >
                      {{ isAddressSaving ? "保存中..." : "保存" }}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </Transition>
      </Teleport>
    </div>
  </div>
</template>

<style scoped>
/* Profile card with subtle gradient header */
.profile-card {
  position: relative;
  background: white;
  border-radius: 1rem;
  border: 1px solid rgba(0, 0, 0, 0.06);
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}

.profile-card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #18181b, #3f3f46, #18181b);
}

/* Avatar */
.avatar-ring {
  padding: 3px;
  border-radius: 9999px;
  background: linear-gradient(135deg, #18181b 0%, #3f3f46 100%);
}

/* Tab indicator animation */
.tab-indicator {
  animation: tabSlideIn 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

@keyframes tabSlideIn {
  from {
    transform: scaleX(0);
    opacity: 0;
  }
  to {
    transform: scaleX(1);
    opacity: 1;
  }
}

/* Tab content transition */
.tab-content {
  animation: fadeUp 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(12px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Page entrance */
.fade-in {
  animation: pageFadeIn 0.6s cubic-bezier(0.23, 1, 0.32, 1);
}

@keyframes pageFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Form elements */
.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  font-size: 0.8125rem;
  font-weight: 600;
  color: #09090b;
  margin-bottom: 0.5rem;
  letter-spacing: -0.01em;
}

.form-input {
  width: 100%;
  padding: 0.625rem 0.875rem;
  border: 1.5px solid #e5e5e5;
  border-radius: 0.75rem;
  font-size: 0.875rem;
  color: #09090b;
  background: #fafafa;
  outline: none;
  transition: all 200ms cubic-bezier(0.23, 1, 0.32, 1);
}

.form-input::placeholder {
  color: #a1a1aa;
}

.form-input:hover {
  border-color: #d4d4d8;
}

.form-input:focus {
  border-color: #18181b;
  background: white;
  box-shadow: 0 0 0 3px rgba(24, 24, 27, 0.08);
}

.form-checkbox {
  width: 1rem;
  height: 1rem;
  border-radius: 0.25rem;
  border: 1.5px solid #d4d4d8;
  accent-color: #18181b;
  cursor: pointer;
}

/* Modern button */
.btn-modern {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.625rem 1.5rem;
  background: #18181b;
  color: white;
  font-size: 0.875rem;
  font-weight: 600;
  border-radius: 0.75rem;
  border: none;
  cursor: pointer;
  transition: all 200ms cubic-bezier(0.23, 1, 0.32, 1);
  letter-spacing: -0.01em;
}

.btn-modern:hover {
  background: #27272a;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(24, 24, 27, 0.15);
}

.btn-modern:active {
  transform: translateY(0);
}

.btn-modern:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-modern-outline {
  display: inline-flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 1rem;
  background: transparent;
  color: #18181b;
  font-size: 0.8125rem;
  font-weight: 600;
  border-radius: 0.625rem;
  border: 1.5px solid #e5e5e5;
  cursor: pointer;
  transition: all 200ms;
}

.btn-modern-outline:hover {
  border-color: #18181b;
  background: #fafafa;
}

.btn-ghost {
  padding: 0.625rem 1.25rem;
  background: #f4f4f5;
  color: #3f3f46;
  font-size: 0.875rem;
  font-weight: 500;
  border-radius: 0.75rem;
  border: none;
  cursor: pointer;
  transition: all 200ms;
}

.btn-ghost:hover {
  background: #e4e4e7;
  color: #09090b;
}

/* Address card */
.address-card {
  padding: 1rem 1.25rem;
  border: 1.5px solid #f4f4f5;
  border-radius: 0.875rem;
  transition: all 250ms cubic-bezier(0.23, 1, 0.32, 1);
  cursor: default;
}

.address-card:hover {
  border-color: #d4d4d8;
  background: #fafafa;
}

.badge-default {
  display: inline-flex;
  align-items: center;
  padding: 0.125rem 0.5rem;
  border-radius: 9999px;
  font-size: 0.6875rem;
  font-weight: 600;
  background: #18181b;
  color: white;
  letter-spacing: 0.02em;
}

.action-btn {
  font-size: 0.75rem;
  color: #3f3f46;
  padding: 0.25rem 0.5rem;
  border-radius: 0.375rem;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: all 150ms;
}

.action-btn:hover {
  color: #18181b;
  background: #f4f4f5;
}

.action-btn-danger {
  font-size: 0.75rem;
  color: #3f3f46;
  padding: 0.25rem 0.5rem;
  border-radius: 0.375rem;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: all 150ms;
}

.action-btn-danger:hover {
  color: #dc2626;
  background: #fef2f2;
}

/* Modal transitions */
.modal-enter-active {
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

.modal-leave-active {
  transition: all 0.2s ease-in;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-panel {
  transform: translateY(16px) scale(0.97);
}

.modal-leave-to .modal-panel {
  transform: translateY(8px) scale(0.98);
}

.modal-panel {
  transition: transform 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}
</style>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { useUserStore } from "@/store/user";
import { updateUserInfo, changePassword, uploadFile } from "@/api/user";
import {
  getAddresses,
  addAddress,
  updateAddress,
  deleteAddress,
} from "@/api/address";
import { ElMessage, ElMessageBox } from "element-plus";

const userStore = useUserStore();
const user = computed(() => userStore.user);
const avatarInput = ref(null);

// Tab 配置
const activeTab = ref("profile");
const tabs = [
  {
    key: "profile",
    label: "个人信息",
    icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>',
  },
  {
    key: "address",
    label: "地址管理",
    icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" /></svg>',
  },
  {
    key: "password",
    label: "修改密码",
    icon: '<svg fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" /></svg>',
  },
];

// 个人信息表单
const form = reactive({
  nickname: "",
  phone: "",
  email: "",
});
const isUpdating = ref(false);

// 密码表单
const passwordForm = reactive({ oldPassword: "", newPassword: "" });
const confirmPassword = ref("");
const isChangingPassword = ref(false);

// 地址相关
const addresses = ref([]);
const addressLoading = ref(true);
const showAddressModal = ref(false);
const editingAddressId = ref(null);
const isAddressSaving = ref(false);
const addressForm = reactive({
  consignee: "",
  phone: "",
  address: "",
  isDefault: 0,
});

// 初始化表单
function initForm() {
  if (user.value) {
    form.nickname = user.value.nickname || "";
    form.phone = user.value.phone || "";
    form.email = user.value.email || "";
  }
}

function triggerAvatarUpload() {
  avatarInput.value?.click();
}

async function handleAvatarUpload(event) {
  const file = event.target.files?.[0];
  if (!file) return;
  if (!file.type.startsWith("image/")) {
    ElMessage.error("请选择图片文件");
    return;
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error("图片大小不能超过2MB");
    return;
  }
  try {
    ElMessage.info("正在上传头像...");
    const res = await uploadFile(file);
    const avatarUrl = res.data?.url;
    if (avatarUrl) {
      await updateUserInfo({ avatar: avatarUrl });
      await userStore.fetchUserInfo();
      ElMessage.success("头像更新成功");
    }
  } catch (e) {
    ElMessage.error("头像上传失败: " + (e.message || "未知错误"));
  }
  event.target.value = "";
}

async function handleUpdate() {
  isUpdating.value = true;
  try {
    await updateUserInfo(form);
    await userStore.fetchUserInfo();
    ElMessage.success("修改成功");
  } catch (e) {
    ElMessage.error("修改失败: " + (e.message || "未知错误"));
  } finally {
    isUpdating.value = false;
  }
}

async function handleChangePassword() {
  if (passwordForm.newPassword !== confirmPassword.value) {
    ElMessage.error("两次输入的密码不一致");
    return;
  }
  isChangingPassword.value = true;
  try {
    await changePassword(passwordForm);
    ElMessage.success("密码修改成功");
    passwordForm.oldPassword = "";
    passwordForm.newPassword = "";
    confirmPassword.value = "";
  } catch (e) {
    ElMessage.error("修改失败: " + (e.message || "未知错误"));
  } finally {
    isChangingPassword.value = false;
  }
}

// 地址管理
async function fetchAddresses() {
  addressLoading.value = true;
  try {
    const res = await getAddresses();
    addresses.value = res.data || [];
  } catch (e) {
    console.error("获取地址失败:", e);
  } finally {
    addressLoading.value = false;
  }
}

function openAddAddress() {
  editingAddressId.value = null;
  Object.assign(addressForm, {
    consignee: "",
    phone: "",
    address: "",
    isDefault: 0,
  });
  showAddressModal.value = true;
}

function editAddress(addr) {
  editingAddressId.value = addr.id;
  Object.assign(addressForm, {
    consignee: addr.consignee,
    phone: addr.phone,
    address: addr.address,
    isDefault: addr.isDefault,
  });
  showAddressModal.value = true;
}

function closeAddressModal() {
  showAddressModal.value = false;
  editingAddressId.value = null;
  Object.assign(addressForm, {
    consignee: "",
    phone: "",
    address: "",
    isDefault: 0,
  });
}

/**
 * 保存地址 — 新增或编辑收货地址，保存成功后刷新地址列表
 * → AddressMapper.xml: insert / update
 */
async function handleSaveAddress() {
  isAddressSaving.value = true;
  try {
    if (editingAddressId.value) {
      await updateAddress(editingAddressId.value, addressForm);
    } else {
      await addAddress(addressForm);
    }
    closeAddressModal();
    fetchAddresses();
    ElMessage.success("保存成功");
  } catch (e) {
    ElMessage.error(e.message || "保存失败");
  } finally {
    isAddressSaving.value = false;
  }
}

async function handleDeleteAddress(id) {
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
    ElMessage.success("删除成功");
  } catch (e) {
    ElMessage.error(e.message || "删除失败");
  }
}

async function setDefault(addr) {
  try {
    await updateAddress(addr.id, { ...addr, isDefault: 1 });
    fetchAddresses();
    ElMessage.success("已设为默认地址");
  } catch (e) {
    ElMessage.error(e.message || "设置失败");
  }
}

onMounted(() => {
  initForm();
  fetchAddresses();
});
</script>
