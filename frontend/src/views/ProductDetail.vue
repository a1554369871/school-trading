<template>
  <div class="min-h-screen bg-[#F8FAFB]">
    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- 加载状态 -->
      <div v-if="loading" class="text-center py-20">
        <div class="animate-spin w-8 h-8 border-[3px] border-gray-200 border-t-gray-600 rounded-full mx-auto"></div>
        <p class="text-gray-400 mt-4 text-sm">加载中...</p>
      </div>

      <template v-if="detail">
        <!-- 商品主体 -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <!-- 左侧：图片 -->
          <div class="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
            <div v-if="detail.product?.productImage" class="w-full h-80 bg-gray-50 flex items-center justify-center">
              <img :src="detail.product.productImage" :alt="detail.product.productName" class="w-full h-full object-contain" />
            </div>
            <div v-else class="w-full aspect-square bg-gray-50 flex items-center justify-center">
              <svg class="w-16 h-16 text-gray-200" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
          </div>

          <!-- 右侧：商品信息 -->
          <div class="space-y-5">
            <!-- 标题和价格卡片 -->
            <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
              <h1 class="text-2xl font-bold text-gray-900 mb-3">{{ detail.product?.productName }}</h1>

              <div class="flex items-center gap-3 mb-4">
                <span class="text-3xl font-bold text-gray-900">¥{{ detail.product?.price }}</span>
                <span v-if="detail.product?.condition" class="px-3 py-1 rounded-full bg-gray-100 text-gray-600 text-xs font-medium">
                  {{ detail.product.condition }}
                </span>
                <span class="px-3 py-1 rounded-full bg-gray-100 text-gray-500 text-xs font-medium">
                  浏览 {{ detail.product?.viewCount || 0 }}
                </span>
              </div>

              <p v-if="detail.product?.description" class="text-gray-500 text-sm leading-relaxed mb-5">
                {{ detail.product.description }}
              </p>

              <!-- 点赞 / 收藏 -->
              <div class="flex gap-3">
                <button
                  @click="doInteract('like')"
                  class="inline-flex items-center gap-2 h-9 px-4 rounded-lg border text-sm font-medium transition-all duration-200 cursor-pointer"
                  :class="detail.userLiked
                    ? 'border-gray-900 bg-gray-900 text-white'
                    : 'border-gray-200 bg-white text-gray-600 hover:bg-gray-50'"
                >
                  <svg class="w-4 h-4" :fill="detail.userLiked ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M7 10v12M15 5.88 14 10h5.83a2 2 0 0 1 1.92 2.56l-2.33 8A2 2 0 0 1 17.5 22H4a2 2 0 0 1-2-2v-8a2 2 0 0 1 2-2h2.76a2 2 0 0 0 1.79-1.11L12 2h0a3.13 3.13 0 0 1 3 3.88Z"/>
                  </svg>
                  点赞
                  <span class="ml-1 text-xs opacity-70">{{ detail.interactions?.like_count || 0 }}</span>
                </button>
                <button
                  @click="doInteract('favorite')"
                  class="inline-flex items-center gap-2 h-9 px-4 rounded-lg border text-sm font-medium transition-all duration-200 cursor-pointer"
                  :class="detail.userFavorited
                    ? 'border-gray-900 bg-gray-900 text-white'
                    : 'border-gray-200 bg-white text-gray-600 hover:bg-gray-50'"
                >
                  <svg class="w-4 h-4" :fill="detail.userFavorited ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                  </svg>
                  收藏
                  <span class="ml-1 text-xs opacity-70">{{ detail.interactions?.favorite_count || 0 }}</span>
                </button>
              </div>
            </div>

            <!-- 卖家信息 -->
            <div class="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-full bg-gray-900 overflow-hidden flex items-center justify-center">
                  <img v-if="detail.seller?.avatar" :src="detail.seller.avatar" class="w-full h-full object-cover" />
                  <span v-else class="text-white text-sm font-bold">{{ detail.seller?.nickname?.charAt(0) }}</span>
                </div>
                <span class="text-sm font-medium text-gray-900">{{ detail.seller?.nickname }}</span>
                <button
                  v-if="detail.seller?.id !== userStore.user?.id"
                  @click="goChat"
                  class="ml-auto h-8 px-3 rounded-lg border border-gray-200 bg-white text-gray-600 text-xs font-medium hover:bg-gray-50 transition-colors cursor-pointer"
                >
                  私聊
                </button>
              </div>
            </div>

            <!-- 操作按钮 -->
            <!-- 商品所有者操作 -->
            <div v-if="isOwner" class="flex gap-3">
              <button
                @click="router.push(`/products/${detail.product?.id}/edit`)"
                class="flex-1 h-12 rounded-xl bg-gray-900 text-white text-sm font-semibold hover:bg-gray-800 active:scale-[0.98] transition-all duration-200 cursor-pointer flex items-center justify-center gap-2"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                </svg>
                编辑
              </button>
              <button
                @click="handleOffshelf"
                class="flex-1 h-12 rounded-xl border border-gray-200 bg-white text-gray-600 text-sm font-semibold hover:bg-gray-50 active:scale-[0.98] transition-all duration-200 cursor-pointer flex items-center justify-center gap-2"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                </svg>
                下架
              </button>
            </div>

            <!-- 非所有者：购买按钮 -->
            <button
              v-if="!isOwner && detail.product?.status === 1"
              @click="showOrderDialog"
              class="w-full h-12 rounded-xl bg-gray-900 text-white text-sm font-semibold hover:bg-gray-800 active:scale-[0.98] transition-all duration-200 cursor-pointer"
            >
              立即购买
            </button>
            <div v-if="detail.product?.status === 2" class="w-full h-12 rounded-xl bg-gray-100 text-gray-400 text-sm font-medium flex items-center justify-center">
              已售出
            </div>
            <div v-if="detail.product?.status === 0" class="w-full h-12 rounded-xl bg-gray-100 text-gray-400 text-sm font-medium flex items-center justify-center">
              已下架
            </div>
          </div>
        </div>

        <!-- 评论区域 -->
        <div class="mt-8 bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
          <h3 class="text-lg font-bold text-gray-900 mb-5">评论 ({{ totalCount }})</h3>

          <!-- 评论输入 -->
          <div v-if="userStore.token" class="mb-6">
            <textarea
              v-model="newComment"
              :rows="2"
              :placeholder="replyTarget ? '回复 ' + replyTarget.commenter_nickname + '...' : '写评论...'"
              class="w-full px-4 py-3 rounded-xl border border-gray-200 bg-gray-50/50 text-sm text-gray-900 placeholder:text-gray-400 focus:outline-none focus:border-gray-400 focus:bg-white focus:ring-2 focus:ring-gray-900/5 transition-all duration-200 resize-none leading-relaxed"
            ></textarea>
            <div class="flex gap-2 mt-3">
              <button
                @click="submitComment"
                :disabled="submitting"
                class="h-9 px-4 rounded-lg bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors disabled:opacity-50 cursor-pointer"
              >
                {{ submitting ? '发表中...' : '发表评论' }}
              </button>
              <button
                v-if="replyTarget"
                @click="cancelReply"
                class="h-9 px-4 rounded-lg border border-gray-200 bg-white text-gray-600 text-sm font-medium hover:bg-gray-50 transition-colors cursor-pointer"
              >
                取消回复
              </button>
            </div>
          </div>

          <!-- 评论列表 -->
          <div v-if="rootComments.length === 0" class="text-center py-10 text-gray-400 text-sm">
            暂无评论，快来抢沙发吧~
          </div>

          <div v-else class="divide-y divide-gray-100">
            <div v-for="c in rootComments" :key="c.id" class="py-4 first:pt-0">
              <div class="flex gap-3">
                <div class="w-8 h-8 rounded-full bg-gray-900 overflow-hidden shrink-0 flex items-center justify-center">
                  <img v-if="c.commenter_avatar" :src="c.commenter_avatar" class="w-full h-full object-cover" />
                  <span v-else class="text-white text-xs font-bold">{{ c.commenter_nickname?.charAt(0) }}</span>
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-2 mb-1">
                    <span class="text-sm font-semibold text-gray-900">{{ c.commenter_nickname }}</span>
                    <span class="text-xs text-gray-400">{{ formatTime(c.comment_time) }}</span>
                  </div>
                  <p class="text-sm text-gray-600 leading-relaxed mb-2">{{ c.content }}</p>
                  <div class="flex gap-2">
                    <button @click="startReply(c)" class="text-xs text-gray-400 hover:text-gray-900 transition-colors cursor-pointer">回复</button>
                    <button
                      v-if="userStore.user?.id === c.commenter_id"
                      @click="handleDelete(c)"
                      class="text-xs text-gray-400 hover:text-red-500 transition-colors cursor-pointer"
                    >删除</button>
                  </div>

                  <!-- 回复列表 -->
                  <div v-if="getReplies(c.id).length > 0" class="mt-3 ml-2 pl-3 border-l-2 border-gray-100 space-y-3">
                    <div v-for="r in getReplies(c.id)" :key="r.id">
                      <div class="flex gap-2.5">
                        <div class="w-6 h-6 rounded-full bg-gray-200 overflow-hidden shrink-0 flex items-center justify-center">
                          <img v-if="r.commenter_avatar" :src="r.commenter_avatar" class="w-full h-full object-cover" />
                          <span v-else class="text-gray-600 text-[10px] font-bold">{{ r.commenter_nickname?.charAt(0) }}</span>
                        </div>
                        <div class="flex-1 min-w-0">
                          <div class="flex items-center gap-1.5 mb-0.5">
                            <span class="text-xs font-semibold text-gray-900">{{ r.commenter_nickname }}</span>
                            <span class="text-xs text-gray-400">回复</span>
                            <span class="text-xs font-semibold text-gray-900">{{ r.replier_nickname }}</span>
                            <span class="text-xs text-gray-400">{{ formatTime(r.comment_time) }}</span>
                          </div>
                          <p class="text-sm text-gray-600 leading-relaxed mb-1">{{ r.content }}</p>
                          <div class="flex gap-2">
                            <button @click="startReply(r)" class="text-xs text-gray-400 hover:text-gray-900 transition-colors cursor-pointer">回复</button>
                            <button
                              v-if="userStore.user?.id === r.commenter_id"
                              @click="handleDelete(r)"
                              class="text-xs text-gray-400 hover:text-red-500 transition-colors cursor-pointer"
                            >删除</button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>

    <!-- 下单弹窗 -->
    <Transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="orderVisible" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
        <div class="bg-white rounded-2xl w-full max-w-md shadow-xl">
          <div class="p-6 border-b border-gray-100 flex items-center justify-between">
            <h3 class="text-lg font-bold text-gray-900">确认下单</h3>
            <button @click="orderVisible = false" class="w-8 h-8 rounded-lg flex items-center justify-center hover:bg-gray-100 transition-colors cursor-pointer">
              <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
          <div class="p-6 space-y-4">
            <!-- 取货方式 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">取货方式</label>
              <div class="flex gap-2">
                <button
                  v-for="method in ['面交', '邮寄', '自提']"
                  :key="method"
                  @click="orderForm.deliveryMethod = method"
                  class="px-4 py-2 rounded-lg border text-sm font-medium transition-all duration-200 cursor-pointer"
                  :class="orderForm.deliveryMethod === method
                    ? 'border-gray-900 bg-gray-900 text-white'
                    : 'border-gray-200 bg-white text-gray-600 hover:bg-gray-50'"
                >
                  {{ method }}
                </button>
              </div>
            </div>
            <!-- 收货地址 -->
            <div v-if="orderForm.deliveryMethod === '邮寄'">
              <label class="block text-sm font-medium text-gray-700 mb-2">收货地址</label>
              <div v-if="addresses.length === 0" class="text-gray-400 text-sm">
                暂无地址，请先
                <router-link to="/user/profile" class="text-gray-900 underline font-medium">添加地址</router-link>
              </div>
              <div v-else class="space-y-2">
                <button
                  v-for="addr in addresses"
                  :key="addr.id"
                  @click="orderForm.addressId = addr.id"
                  class="w-full text-left px-4 py-3 rounded-xl border text-sm transition-all duration-200 cursor-pointer"
                  :class="orderForm.addressId === addr.id
                    ? 'border-gray-900 bg-gray-50'
                    : 'border-gray-200 hover:bg-gray-50'"
                >
                  <span class="font-medium text-gray-900">{{ addr.consignee }}</span>
                  <span class="text-gray-500 ml-2">{{ addr.phone }}</span>
                  <p class="text-gray-400 text-xs mt-0.5">{{ addr.address }}</p>
                </button>
              </div>
            </div>
            <!-- 备注 -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">备注</label>
              <input
                v-model="orderForm.remark"
                type="text"
                placeholder="选填，如约定时间地点等"
                class="w-full h-11 px-4 rounded-xl border border-gray-200 bg-gray-50/50 text-sm text-gray-900 placeholder:text-gray-400 focus:outline-none focus:border-gray-400 focus:bg-white focus:ring-2 focus:ring-gray-900/5 transition-all duration-200"
              />
            </div>
          </div>
          <div class="p-6 pt-0 flex gap-3">
            <button
              @click="orderVisible = false"
              class="flex-1 h-11 rounded-xl border border-gray-200 bg-white text-gray-600 text-sm font-medium hover:bg-gray-50 transition-colors cursor-pointer"
            >取消</button>
            <button
              @click="submitOrder"
              :disabled="orderLoading"
              class="flex-1 h-11 rounded-xl bg-gray-900 text-white text-sm font-semibold hover:bg-gray-800 transition-colors disabled:opacity-50 cursor-pointer"
            >{{ orderLoading ? '提交中...' : '确认下单' }}</button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { getProductDetail, interactProduct, offshelfProduct } from '../api/product'
import { getComments, addComment, deleteComment } from '../api/comment'
import { createOrder } from '../api/order'
import { getAddresses } from '../api/address'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const detail = ref(null)
const comments = ref([])
const addresses = ref([])
const loading = ref(false)
const newComment = ref('')
const replyTarget = ref(null)
const submitting = ref(false)
const orderVisible = ref(false)
const orderLoading = ref(false)
const orderForm = reactive({ productId: null, addressId: null, deliveryMethod: '面交', remark: '' })

const rootComments = computed(() => comments.value.filter(c => !c.parent_id))
const totalCount = computed(() => comments.value.length)
const getReplies = (parentId) => comments.value.filter(c => c.parent_id === parentId)
const isOwner = computed(() => userStore.user?.id && detail.value?.product?.userId === userStore.user?.id)

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return d.toLocaleDateString('zh-CN')
}

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    detail.value = res.data
  } catch (e) {
    ElMessage.error(e.message || '加载商品详情失败')
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    const res = await getComments(route.params.id)
    comments.value = res.data || []
  } catch (e) {
    console.error('加载评论失败', e)
  }
}

const loadAddresses = async () => {
  try {
    const res = await getAddresses()
    addresses.value = res.data || []
  } catch (e) {
    console.error('加载地址失败', e)
  }
}

const doInteract = async (type) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await interactProduct(route.params.id, type)
    const res = await getProductDetail(route.params.id)
    detail.value = res.data
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  submitting.value = true
  try {
    await addComment(route.params.id, {
      content: newComment.value,
      parentId: getReplyParentId(),
      replierId: getReplierId()
    })
    ElMessage.success('评论成功')
    newComment.value = ''
    replyTarget.value = null
    await loadComments()
  } catch (e) {
    ElMessage.error(e.message || '评论失败')
  } finally {
    submitting.value = false
  }
}

const startReply = (comment) => {
  replyTarget.value = comment
}

const getReplyParentId = () => {
  if (!replyTarget.value) return null
  // 如果回复的是嵌套评论（已有 parent_id），挂到其所属根评论下
  return replyTarget.value.parent_id || replyTarget.value.id
}

const getReplierId = () => {
  if (!replyTarget.value) return null
  // 被回复者的用户ID
  return replyTarget.value.commenter_id || null
}

const cancelReply = () => {
  replyTarget.value = null
}

const handleDelete = async (comment) => {
  try {
    await ElMessageBox.confirm('确认删除此评论？', '提示', { type: 'warning' })
    await deleteComment(comment.id)
    ElMessage.success('删除成功')
    await loadComments()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

const showOrderDialog = () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  orderForm.productId = detail.value.product.id
  orderForm.addressId = null
  orderForm.deliveryMethod = '面交'
  orderForm.remark = ''
  orderVisible.value = true
  loadAddresses()
}

const submitOrder = async () => {
  if (orderForm.deliveryMethod === '邮寄' && !orderForm.addressId) {
    ElMessage.warning('请选择收货地址')
    return
  }
  orderLoading.value = true
  try {
    await createOrder(orderForm)
    ElMessage.success('下单成功')
    orderVisible.value = false
    await loadDetail()
  } catch (e) {
    ElMessage.error(e.message || '下单失败')
  } finally {
    orderLoading.value = false
  }
}

const handleOffshelf = async () => {
  try {
    await ElMessageBox.confirm('确认下架此商品？', '提示', { type: 'warning' })
    await offshelfProduct(detail.value.product.id)
    ElMessage.success('已下架')
    await loadDetail()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '操作失败')
    }
  }
}

const goChat = () => {
  if (detail.value?.seller?.id) {
    router.push(`/chat/${detail.value.seller.id}`)
  }
}

onMounted(() => {
  loadDetail()
  loadComments()
})
</script>
