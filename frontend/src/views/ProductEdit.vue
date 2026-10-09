<template>
  <div class="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="bg-white rounded-xl border border-gray-200 p-6">
      <h1 class="text-2xl font-bold text-gray-900 mb-6">编辑商品</h1>

      <div v-if="loading" class="text-center py-12">
        <div class="animate-spin w-8 h-8 border-4 border-primary-500 border-t-transparent rounded-full mx-auto"></div>
      </div>

      <form v-else @submit.prevent="handleSubmit" class="space-y-5">
        <!-- 商品名称 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">商品名称</label>
          <el-input v-model="form.productName" placeholder="请输入商品名称" maxlength="50" show-word-limit />
        </div>

        <!-- 价格 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">价格（元）</label>
          <el-input-number v-model="form.price" :min="0.01" :precision="2" :step="1" />
        </div>

        <!-- 分类 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">分类</label>
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.categoryName" :value="cat.id" />
          </el-select>
        </div>

        <!-- 成色 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">成色</label>
          <el-select v-model="form.condition" placeholder="请选择成色">
            <el-option label="全新" value="全新" />
            <el-option label="几乎全新" value="几乎全新" />
            <el-option label="轻微使用痕迹" value="轻微使用痕迹" />
            <el-option label="明显使用痕迹" value="明显使用痕迹" />
            <el-option label="功能正常" value="功能正常" />
          </el-select>
        </div>

        <!-- 交易方式 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">交易方式</label>
          <el-select v-model="form.deliveryMethod" placeholder="请选择交易方式">
            <el-option label="面交" value="面交" />
            <el-option label="邮寄" value="邮寄" />
            <el-option label="自提" value="自提" />
          </el-select>
        </div>

        <!-- 商品图片 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">商品图片</label>
          <div class="flex items-center gap-4">
            <div v-if="form.productImage" class="relative">
              <img :src="form.productImage" class="w-24 h-24 object-cover rounded-lg" />
              <button type="button" @click="form.productImage = ''" class="absolute -top-2 -right-2 w-5 h-5 bg-red-500 text-gray-900 rounded-full text-xs flex items-center justify-center">×</button>
            </div>
            <label class="cursor-pointer">
              <div class="w-24 h-24 border-2 border-dashed border-gray-300 rounded-lg flex items-center justify-center text-gray-500 hover:border-primary-400 hover:text-primary-400 transition-colors">
                <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
              </div>
              <input type="file" accept="image/*" class="hidden" @change="handleUpload" />
            </label>
          </div>
        </div>

        <!-- 商品描述 -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">商品描述</label>
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="描述商品的详细信息..." maxlength="500" show-word-limit />
        </div>

        <!-- 提交按钮 -->
        <div class="flex gap-3 pt-4">
          <el-button type="primary" @click="handleSubmit" :loading="submitting" class="flex-1">保存修改</el-button>
          <el-button @click="router.back()" class="flex-1">取消</el-button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail, updateProduct } from '@/api/product'
import { getCategories } from '@/api/category'
import { uploadFile } from '@/api/user'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const categories = ref([])

const form = reactive({
  productName: '',
  price: 0,
  categoryId: null,
  condition: '',
  deliveryMethod: '面交',
  productImage: '',
  description: ''
})

async function fetchData() {
  try {
    const [detailRes, catRes] = await Promise.all([
      getProductDetail(route.params.id),
      getCategories()
    ])
    const detail = detailRes.data || detailRes
    const product = detail.product || detail
    form.productName = product.productName
    form.price = product.price
    form.categoryId = product.categoryId
    form.condition = product.condition
    form.deliveryMethod = product.deliveryMethod || '面交'
    form.productImage = product.productImage || ''
    form.description = product.description || ''
    categories.value = catRes.data || []
  } catch (e) {
    ElMessage.error('加载商品信息失败')
  } finally {
    loading.value = false
  }
}

async function handleUpload(e) {
  const file = e.target.files[0]
  if (!file) return
  try {
    const res = await uploadFile(file)
    form.productImage = res.data?.url || res.data
    ElMessage.success('图片上传成功')
  } catch (err) {
    ElMessage.error('图片上传失败')
  }
}

async function handleSubmit() {
  if (!form.productName.trim()) return ElMessage.warning('请输入商品名称')
  if (!form.price || form.price <= 0) return ElMessage.warning('请输入有效价格')
  if (!form.categoryId) return ElMessage.warning('请选择分类')

  submitting.value = true
  try {
    await updateProduct(route.params.id, { ...form })
    ElMessage.success('修改成功')
    router.push(`/products/${route.params.id}`)
  } catch (e) {
    ElMessage.error(e.message || '修改失败')
  } finally {
    submitting.value = false
  }
}

onMounted(fetchData)
</script>
