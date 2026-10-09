<template>
  <div class="min-h-screen bg-[#F8FAFB]">
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <form @submit.prevent="handleSubmit">
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- 左侧：图片上传区域 -->
          <div class="lg:col-span-1">
            <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 sticky top-24">
              <h2 class="text-sm font-semibold text-gray-900 mb-4">商品图片</h2>

              <!-- 已上传图片预览 -->
              <div v-if="form.productImage" class="relative w-full rounded-xl overflow-hidden border border-gray-200 mb-3 bg-gray-50">
                <img :src="form.productImage" class="w-full h-auto max-h-80 object-contain" loading="lazy" />
                <button
                  type="button"
                  @click="form.productImage = ''"
                  class="absolute top-2 right-2 w-7 h-7 bg-black/50 backdrop-blur-sm text-white rounded-lg flex items-center justify-center hover:bg-black/70 transition-colors cursor-pointer"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <!-- 上传区域 -->
              <label
                v-if="!form.productImage"
                class="w-full h-48 rounded-xl border-2 border-dashed border-gray-200 bg-gray-50/50 flex flex-col items-center justify-center cursor-pointer hover:border-gray-400 hover:bg-gray-50 transition-all duration-200"
              >
                <div class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center mb-2">
                  <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
                <span class="text-sm font-medium text-gray-600">点击上传图片</span>
                <span class="text-xs text-gray-400 mt-1">支持 JPG、PNG，不超过 5MB</span>
                <input
                  type="file"
                  accept="image/*"
                  class="hidden"
                  @change="handleImageUpload"
                />
              </label>
            </div>
          </div>

          <!-- 右侧：表单区域 -->
          <div class="lg:col-span-2 space-y-5">
            <!-- 基本信息卡片 -->
            <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
              <div class="flex items-center gap-2 mb-5">
                <div class="w-1 h-5 rounded-full bg-gray-900"></div>
                <h2 class="text-sm font-semibold text-gray-900">基本信息</h2>
              </div>

              <div class="space-y-5">
                <!-- 商品名称 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">商品名称</label>
                  <input
                    v-model="form.productName"
                    type="text"
                    placeholder="给你的宝贝起个名字"
                    class="w-full h-11 px-4 rounded-xl border border-gray-200 bg-gray-50/50 text-sm text-gray-900 placeholder:text-gray-400 focus:outline-none focus:border-gray-400 focus:bg-white focus:ring-2 focus:ring-gray-900/5 transition-all duration-200"
                    required
                  />
                </div>

                <!-- 分类和成色 -->
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1.5">商品分类</label>
                    <div class="relative" ref="categoryRef">
                      <button
                        type="button"
                        @click="toggleCategory"
                        class="w-full h-11 px-3 rounded-xl border border-gray-200 bg-gray-50/50 text-sm text-left flex items-center justify-between focus:outline-none focus:border-gray-400 focus:bg-white focus:ring-2 focus:ring-gray-900/5 transition-all duration-200 cursor-pointer"
                        :class="form.categoryId ? 'text-gray-900' : 'text-gray-400'"
                      >
                        <span class="truncate">{{ categoryLabel || '请选择分类' }}</span>
                        <svg class="w-4 h-4 shrink-0 text-gray-400 transition-transform duration-200" :class="{ 'rotate-180': categoryOpen }" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 9l6 6 6-6"/></svg>
                      </button>
                      <Transition
                        enter-active-class="transition ease-out duration-150"
                        enter-from-class="opacity-0 scale-95"
                        enter-to-class="opacity-100 scale-100"
                        leave-active-class="transition ease-in duration-100"
                        leave-from-class="opacity-100 scale-100"
                        leave-to-class="opacity-0 scale-95"
                      >
                        <div v-if="categoryOpen" class="absolute z-50 mt-1.5 w-full rounded-xl border border-gray-200 bg-white shadow-lg p-1 max-h-60 overflow-y-auto" style="transform-origin: top;">
                          <div
                            v-for="cat in categories"
                            :key="cat.id"
                            @click="selectCategory(cat)"
                            class="flex items-center justify-between px-3 py-2 rounded-lg text-sm cursor-pointer transition-colors duration-150"
                            :class="form.categoryId === cat.id ? 'bg-gray-100 text-gray-900 font-medium' : 'text-gray-700 hover:bg-gray-50'"
                          >
                            <span>{{ cat.categoryName }}</span>
                            <svg v-if="form.categoryId === cat.id" class="w-4 h-4 text-gray-900" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                          </div>
                        </div>
                      </Transition>
                    </div>
                  </div>
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1.5">成色</label>
                    <div class="relative" ref="conditionRef">
                      <button
                        type="button"
                        @click="toggleCondition"
                        class="w-full h-11 px-3 rounded-xl border border-gray-200 bg-gray-50/50 text-sm text-left flex items-center justify-between focus:outline-none focus:border-gray-400 focus:bg-white focus:ring-2 focus:ring-gray-900/5 transition-all duration-200 cursor-pointer"
                        :class="form.condition ? 'text-gray-900' : 'text-gray-400'"
                      >
                        <span class="truncate">{{ form.condition || '请选择成色' }}</span>
                        <svg class="w-4 h-4 shrink-0 text-gray-400 transition-transform duration-200" :class="{ 'rotate-180': conditionOpen }" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 9l6 6 6-6"/></svg>
                      </button>
                      <Transition
                        enter-active-class="transition ease-out duration-150"
                        enter-from-class="opacity-0 scale-95"
                        enter-to-class="opacity-100 scale-100"
                        leave-active-class="transition ease-in duration-100"
                        leave-from-class="opacity-100 scale-100"
                        leave-to-class="opacity-0 scale-95"
                      >
                        <div v-if="conditionOpen" class="absolute z-50 mt-1.5 w-full rounded-xl border border-gray-200 bg-white shadow-lg p-1 max-h-60 overflow-y-auto" style="transform-origin: top;">
                          <div
                            v-for="item in conditionOptions"
                            :key="item"
                            @click="selectCondition(item)"
                            class="flex items-center justify-between px-3 py-2 rounded-lg text-sm cursor-pointer transition-colors duration-150"
                            :class="form.condition === item ? 'bg-gray-100 text-gray-900 font-medium' : 'text-gray-700 hover:bg-gray-50'"
                          >
                            <span>{{ item }}</span>
                            <svg v-if="form.condition === item" class="w-4 h-4 text-gray-900" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                          </div>
                        </div>
                      </Transition>
                    </div>
                  </div>
                </div>

                <!-- 价格 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1.5">出售价格</label>
                  <div class="relative">
                    <span class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 text-sm font-medium">¥</span>
                    <input
                      v-model="form.price"
                      type="number"
                      step="0.01"
                      min="0"
                      placeholder="0.00"
                      class="w-full h-11 pl-8 pr-4 rounded-xl border border-gray-200 bg-gray-50/50 text-sm text-gray-900 placeholder:text-gray-400 focus:outline-none focus:border-gray-400 focus:bg-white focus:ring-2 focus:ring-gray-900/5 transition-all duration-200"
                      required
                    />
                  </div>
                </div>
              </div>
            </div>

            <!-- 描述卡片 -->
            <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
              <div class="flex items-center gap-2 mb-5">
                <div class="w-1 h-5 rounded-full bg-gray-900"></div>
                <h2 class="text-sm font-semibold text-gray-900">详细描述</h2>
              </div>

              <textarea
                v-model="form.description"
                rows="5"
                placeholder="详细描述商品的品牌、型号、使用情况等信息，有助于更快成交..."
                class="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50/50 text-sm text-gray-900 placeholder:text-gray-400 focus:outline-none focus:border-gray-400 focus:bg-white focus:ring-2 focus:ring-gray-900/5 transition-all duration-200 resize-none leading-relaxed"
                required
              ></textarea>
            </div>

            <!-- 操作按钮 -->
            <div class="flex gap-3 pt-2">
              <router-link
                to="/products"
                class="flex-1 h-12 rounded-xl border border-gray-200 bg-white text-gray-600 text-sm font-medium hover:bg-gray-50 transition-colors duration-200 flex items-center justify-center cursor-pointer"
              >
                取消
              </router-link>
              <button
                type="submit"
                class="flex-1 h-12 rounded-xl bg-gray-900 text-white text-sm font-semibold hover:bg-gray-800 active:scale-[0.98] transition-all duration-200 shadow-sm disabled:opacity-50 disabled:active:scale-100 flex items-center justify-center gap-2 cursor-pointer"
                :disabled="isSubmitting"
              >
                <svg v-if="!isSubmitting" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
                </svg>
                <svg v-else class="w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
                </svg>
                {{ isSubmitting ? '发布中...' : '发布商品' }}
              </button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { publishProduct } from '@/api/product'
import { getCategories } from '@/api/category'
import { uploadFile } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()

const form = reactive({
  productName: '',
  categoryId: '',
  price: '',
  condition: '',
  productImage: '',
  description: ''
})

const categories = ref([])
const isSubmitting = ref(false)

const categoryOpen = ref(false)
const conditionOpen = ref(false)
const categoryRef = ref(null)
const conditionRef = ref(null)

const conditionOptions = ['全新', '几乎全新', '轻微使用痕迹', '明显使用痕迹', '功能完好但外观较差']

const categoryLabel = computed(() => {
  const cat = categories.value.find(c => String(c.id) === String(form.categoryId))
  return cat ? cat.categoryName : ''
})

function toggleCategory() {
  categoryOpen.value = !categoryOpen.value
  conditionOpen.value = false
}

function toggleCondition() {
  conditionOpen.value = !conditionOpen.value
  categoryOpen.value = false
}

function selectCategory(cat) {
  form.categoryId = cat.id
  categoryOpen.value = false
}

function selectCondition(item) {
  form.condition = item
  conditionOpen.value = false
}

function handleClickOutside(e) {
  if (categoryRef.value && !categoryRef.value.contains(e.target)) {
    categoryOpen.value = false
  }
  if (conditionRef.value && !conditionRef.value.contains(e.target)) {
    conditionOpen.value = false
  }
}

async function handleImageUpload(e) {
  const file = e.target.files[0]
  if (!file) return

  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过5MB')
    return
  }

  try {
    const res = await uploadFile(file)
    form.productImage = res.data.url
  } catch (err) {
    ElMessage.error('上传失败: ' + (err.message || '未知错误'))
  }
}

async function handleSubmit() {
  if (!form.productName || !form.categoryId || !form.price || !form.condition || !form.description) {
    ElMessage.warning('请填写完整信息')
    return
  }

  isSubmitting.value = true

  try {
    await publishProduct({
      productName: form.productName,
      categoryId: Number(form.categoryId),
      price: Number(form.price),
      condition: form.condition,
      productImage: form.productImage,
      description: form.description
    })
    ElMessage.success('发布成功')
    router.push('/products')
  } catch (e) {
    ElMessage.error('发布失败: ' + (e.message || '未知错误'))
  } finally {
    isSubmitting.value = false
  }
}

onMounted(async () => {
  document.addEventListener('click', handleClickOutside)
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch (e) {
    console.error('获取分类失败:', e)
  }
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>
