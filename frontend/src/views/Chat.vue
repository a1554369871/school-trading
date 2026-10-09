<template>
  <div class="min-h-screen bg-[#F8FAFB]">
    <div class="max-w-6xl mx-auto h-[calc(100vh-64px)] flex">
      <!-- 左侧聊天列表 -->
      <div class="w-80 bg-white border-r border-gray-100 flex flex-col rounded-l-2xl overflow-hidden shrink-0">
        <div class="p-5 border-b border-gray-100">
          <h2 class="text-lg font-bold text-gray-900">消息</h2>
        </div>
        <div class="flex-1 overflow-y-auto">
          <div
            v-for="chat in recentChats"
            :key="chat.other_id"
            @click="openChat(chat.other_id, chat.other_nickname, chat.other_avatar)"
            class="flex items-center gap-3 px-4 py-3 cursor-pointer transition-colors duration-200"
            :class="targetUserId === chat.other_id ? 'bg-gray-50' : 'hover:bg-gray-50/50'"
          >
            <div class="w-11 h-11 rounded-full bg-gray-900 flex items-center justify-center shrink-0 overflow-hidden">
              <img v-if="chat.other_avatar" :src="chat.other_avatar" class="w-full h-full object-cover" />
              <span v-else class="text-white text-sm font-bold">{{ chat.other_nickname?.charAt(0) || '?' }}</span>
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center justify-between mb-0.5">
                <span class="text-sm font-semibold text-gray-900 truncate">{{ chat.other_nickname }}</span>
                <span class="text-[11px] text-gray-400 shrink-0 ml-2">{{ formatTime(chat.send_time) }}</span>
              </div>
              <p class="text-xs text-gray-400 truncate">{{ chat.content }}</p>
            </div>
          </div>
          <div v-if="recentChats.length === 0" class="p-8 text-center text-gray-400 text-sm">
            暂无聊天记录
          </div>
        </div>
      </div>

      <!-- 右侧聊天窗口 -->
      <div class="flex-1 flex flex-col bg-white rounded-r-2xl overflow-hidden">
        <template v-if="targetUserId">
          <!-- 顶部栏 -->
          <div class="px-6 py-4 border-b border-gray-100 flex items-center gap-3">
            <div class="w-9 h-9 rounded-full bg-gray-900 flex items-center justify-center shrink-0 overflow-hidden">
              <img v-if="targetAvatar" :src="targetAvatar" class="w-full h-full object-cover" />
              <span v-else class="text-white text-xs font-bold">{{ targetNickname?.charAt(0) || '?' }}</span>
            </div>
            <h3 class="text-sm font-semibold text-gray-900">{{ targetNickname }}</h3>
          </div>

          <!-- 消息区域 -->
          <div ref="messagesContainer" class="flex-1 overflow-y-auto px-6 py-4 space-y-4">
            <div
              v-for="msg in messages"
              :key="msg.id"
              :class="['flex', msg.sender_id === userStore.user?.id ? 'justify-end' : 'justify-start']"
            >
              <div
                :class="[
                  'max-w-[70%] px-4 py-2.5 text-sm leading-relaxed',
                  msg.sender_id === userStore.user?.id
                    ? 'bg-gray-900 text-white rounded-2xl rounded-br-md'
                    : 'bg-gray-100 text-gray-700 rounded-2xl rounded-bl-md'
                ]"
              >
                <p>{{ msg.content }}</p>
                <div
                  :class="[
                    'text-[10px] mt-1',
                    msg.sender_id === userStore.user?.id ? 'text-gray-400' : 'text-gray-400'
                  ]"
                >{{ formatMsgTime(msg.send_time) }}</div>
              </div>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="px-6 py-4 border-t border-gray-100">
            <div class="flex gap-3 items-center">
              <input
                v-model="newMessage"
                type="text"
                placeholder="输入消息..."
                class="flex-1 h-11 px-4 rounded-xl border border-gray-200 bg-gray-50/50 text-sm text-gray-900 placeholder:text-gray-400 focus:outline-none focus:border-gray-400 focus:bg-white focus:ring-2 focus:ring-gray-900/5 transition-all duration-200"
                @keyup.enter="handleSend"
              />
              <button
                @click="handleSend"
                class="h-11 px-6 rounded-xl bg-gray-900 text-white text-sm font-semibold hover:bg-gray-800 active:scale-[0.98] transition-all duration-200 cursor-pointer shrink-0"
              >
                发送
              </button>
            </div>
          </div>
        </template>

        <!-- 空状态 -->
        <div v-else class="flex-1 flex flex-col items-center justify-center text-gray-400">
          <div class="w-16 h-16 rounded-full bg-gray-100 flex items-center justify-center mb-4">
            <svg class="w-8 h-8 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
            </svg>
          </div>
          <p class="text-sm">选择一个聊天开始对话</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { sendMessage, getConversation, getRecentChats, markConversationRead, getUnreadCount } from '@/api/message'

const route = useRoute()
const userStore = useUserStore()

const recentChats = ref([])
const messages = ref([])
const targetUserId = ref(route.params.userId ? Number(route.params.userId) : null)
const targetNickname = ref('')
const targetAvatar = ref('')
const newMessage = ref('')
const messagesContainer = ref(null)

function formatTime(time) {
  if (!time) return '-'
  const d = new Date(time)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return d.toLocaleDateString('zh-CN')
}

function formatMsgTime(time) {
  if (!time) return ''
  const d = new Date(time)
  return d.toLocaleDateString('zh-CN') + ' ' + d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

async function fetchRecentChats() {
  try {
    const res = await getRecentChats()
    const myId = userStore.user?.id
    recentChats.value = (res.data?.list || []).filter(c => c.other_id !== myId)
  } catch (e) {
    console.error('获取聊天列表失败:', e)
  }
}

async function openChat(userId, nickname, avatar) {
  targetUserId.value = userId
  targetNickname.value = nickname
  targetAvatar.value = avatar || ''
  await fetchConversation(userId)
  await markConversationRead(userId)
  await fetchRecentChats()
}

async function fetchConversation(userId) {
  try {
    const res = await getConversation(userId)
    messages.value = res.data?.list || []
    await nextTick()
    scrollToBottom()
  } catch (e) {
    console.error('获取对话失败:', e)
  }
}

function scrollToBottom() {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

async function handleSend() {
  if (!newMessage.value.trim() || !targetUserId.value) return
  try {
    await sendMessage({ receiverId: targetUserId.value, content: newMessage.value.trim() })
    newMessage.value = ''
    await fetchConversation(targetUserId.value)
    await fetchRecentChats()
  } catch (e) {
    console.error('发送失败:', e)
  }
}

watch(() => route.params.userId, (newVal) => {
  if (newVal) {
    const userId = Number(newVal)
    targetUserId.value = userId
    fetchConversation(userId)
  }
})

onMounted(async () => {
  await fetchRecentChats()
  if (targetUserId.value) {
    // 从聊天列表中找到匹配的用户信息
    const chat = recentChats.value.find(c => c.other_id === targetUserId.value)
    if (chat) {
      targetNickname.value = chat.other_nickname || ''
      targetAvatar.value = chat.other_avatar || ''
    }
    await fetchConversation(targetUserId.value)
    await markConversationRead(targetUserId.value)
  }
})
</script>
