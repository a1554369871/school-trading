<template>
  <div class="h-screen w-screen overflow-hidden bg-white flex items-center justify-center">
    <!-- 返回首页按钮 -->
    <router-link to="/" class="absolute top-6 left-6 z-50 flex items-center gap-2 text-sm text-gray-500 hover:text-gray-800 transition-colors bg-white/80 backdrop-blur-sm px-4 py-2 rounded-full shadow-sm hover:shadow-md">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
      </svg>
      返回首页
    </router-link>
    <div class="w-full max-w-[1100px] min-h-[680px] grid lg:grid-cols-2 shadow-2xl rounded-2xl overflow-hidden">
    <!-- 左侧卡通角色区域 -->
    <div class="relative hidden lg:flex flex-col justify-between bg-[#1a1a1a] p-16 text-white">


      <!-- 卡通角色容器 -->
      <div class="relative z-20 flex items-center justify-center h-[380px] mt-auto">
        <div class="relative" style="width:550px;height:400px">
          
          <!-- 紫色高个子角色（最后面） -->
          <div 
            ref="purpleRef"
            class="absolute bottom-0 transition-all duration-[1200ms] ease-in-out"
            :style="{
              left: '70px',
              width: '180px',
              height: isTyping ? '440px' : '400px',
              backgroundColor: '#6C3FF5',
              borderRadius: '10px 10px 0 0',
              zIndex: 1,
              transform: isLoginError
                ? 'skewX(0deg) translateX(15px)'
                : (password.length > 0 && showPassword)
                  ? 'skewX(0deg)'
                  : isTyping
                    ? `skewX(${purplePos.bodySkew - 12}deg) translateX(40px)`
                    : `skewX(${purplePos.bodySkew}deg)`,
              transformOrigin: 'bottom center'
            }"
          >
            <!-- 紫色角色眼睛 -->
            <div 
              class="absolute flex gap-8 transition-all duration-700 ease-in-out"
              :style="{
                left: (password.length > 0 && showPassword) ? '20px' : isLookingAtEachOther ? '55px' : `${45 + purplePos.faceX}px`,
                top: (password.length > 0 && showPassword) ? '35px' : isLookingAtEachOther ? '65px' : `${40 + purplePos.faceY}px`
              }"
            >
              <EyeBall 
                :size="18" :pupilSize="7" :maxDistance="5" 
                eyeColor="white" pupilColor="#2D2D2D" 
                :isBlinking="isPurpleBlinking"
                :forceLookX="(password.length > 0 && showPassword) ? (isPurplePeeking ? 4 : -4) : isLookingAtEachOther ? 3 : undefined"
                :forceLookY="(password.length > 0 && showPassword) ? (isPurplePeeking ? 5 : -4) : isLookingAtEachOther ? 4 : undefined"
              />
              <EyeBall 
                :size="18" :pupilSize="7" :maxDistance="5" 
                eyeColor="white" pupilColor="#2D2D2D"
                :isBlinking="isPurpleBlinking"
                :forceLookX="(password.length > 0 && showPassword) ? (isPurplePeeking ? 4 : -4) : isLookingAtEachOther ? 3 : undefined"
                :forceLookY="(password.length > 0 && showPassword) ? (isPurplePeeking ? 5 : -4) : isLookingAtEachOther ? 4 : undefined"
              />
            </div>
          </div>

          <!-- 黑色高个子角色（中间） -->
          <div 
            ref="blackRef"
            class="absolute bottom-0 transition-all duration-700 ease-in-out"
            :style="{
              left: '240px',
              width: '120px',
              height: '310px',
              backgroundColor: '#2D2D2D',
              borderRadius: '8px 8px 0 0',
              zIndex: 2,
              transform: isLoginError
                ? 'skewX(0deg) translateX(-10px)'
                : (password.length > 0 && showPassword)
                  ? 'skewX(0deg)'
                  : isLookingAtEachOther
                    ? `skewX(${blackPos.bodySkew * 1.5 + 10}deg) translateX(20px)`
                    : isTyping
                      ? `skewX(${blackPos.bodySkew * 1.5}deg)`
                      : `skewX(${blackPos.bodySkew}deg)`,
              transformOrigin: 'bottom center'
            }"
          >
            <!-- 黑色角色眼睛 -->
            <div 
              class="absolute flex gap-6 transition-all duration-700 ease-in-out"
              :style="{
                left: (password.length > 0 && showPassword) ? '10px' : isLookingAtEachOther ? '32px' : `${26 + blackPos.faceX}px`,
                top: (password.length > 0 && showPassword) ? '28px' : isLookingAtEachOther ? '12px' : `${32 + blackPos.faceY}px`
              }"
            >
              <EyeBall 
                :size="16" :pupilSize="6" :maxDistance="4" 
                eyeColor="white" pupilColor="#2D2D2D"
                :isBlinking="isBlackBlinking"
                :forceLookX="(password.length > 0 && showPassword) ? -4 : isLookingAtEachOther ? 0 : undefined"
                :forceLookY="(password.length > 0 && showPassword) ? -4 : isLookingAtEachOther ? -4 : undefined"
              />
              <EyeBall 
                :size="16" :pupilSize="6" :maxDistance="4" 
                eyeColor="white" pupilColor="#2D2D2D"
                :isBlinking="isBlackBlinking"
                :forceLookX="(password.length > 0 && showPassword) ? -4 : isLookingAtEachOther ? 0 : undefined"
                :forceLookY="(password.length > 0 && showPassword) ? -4 : isLookingAtEachOther ? -4 : undefined"
              />
            </div>
          </div>

          <!-- 橙色半圆形角色（前左） -->
          <div 
            ref="orangeRef"
            class="absolute bottom-0 transition-all duration-700 ease-in-out"
            :style="{
              left: '0px',
              width: '240px',
              height: '200px',
              zIndex: 3,
              backgroundColor: '#FF9B6B',
              borderRadius: '120px 120px 0 0',
              transform: isLoginError
                ? 'skewX(0deg) translateX(-15px)'
                : (password.length > 0 && showPassword) ? 'skewX(0deg)' : `skewX(${orangePos.bodySkew}deg)`,
              transformOrigin: 'bottom center'
            }"
          >
            <!-- 橙色角色眼睛（只有瞳孔） -->
            <div 
              class="absolute flex gap-8 transition-all duration-200 ease-out"
              :style="{
                left: (password.length > 0 && showPassword) ? '50px' : `${82 + orangePos.faceX}px`,
                top: (password.length > 0 && showPassword) ? '85px' : `${90 + orangePos.faceY}px`
              }"
            >
              <Pupil :size="12" :maxDistance="5" pupilColor="#2D2D2D" :forceLookX="(password.length > 0 && showPassword) ? -5 : undefined" :forceLookY="(password.length > 0 && showPassword) ? -4 : undefined" />
              <Pupil :size="12" :maxDistance="5" pupilColor="#2D2D2D" :forceLookX="(password.length > 0 && showPassword) ? -5 : undefined" :forceLookY="(password.length > 0 && showPassword) ? -4 : undefined" />
            </div>
          </div>

          <!-- 黄色高个子角色（前右） -->
          <div 
            ref="yellowRef"
            class="absolute bottom-0 transition-all duration-700 ease-in-out"
            :style="{
              left: '310px',
              width: '140px',
              height: '230px',
              backgroundColor: '#E8D754',
              borderRadius: '70px 70px 0 0',
              zIndex: 4,
              transform: isLoginError
                ? 'skewX(0deg) translateX(10px)'
                : (password.length > 0 && showPassword) ? 'skewX(0deg)' : `skewX(${yellowPos.bodySkew}deg)`,
              transformOrigin: 'bottom center'
            }"
          >
            <!-- 黄色角色眼睛（只有瞳孔） -->
            <div 
              class="absolute flex gap-6 transition-all duration-200 ease-out"
              :style="{
                left: (password.length > 0 && showPassword) ? '20px' : `${52 + yellowPos.faceX}px`,
                top: (password.length > 0 && showPassword) ? '35px' : `${40 + yellowPos.faceY}px`
              }"
            >
              <Pupil :size="12" :maxDistance="5" pupilColor="#2D2D2D" :forceLookX="(password.length > 0 && showPassword) ? -5 : undefined" :forceLookY="(password.length > 0 && showPassword) ? -4 : undefined" />
              <Pupil :size="12" :maxDistance="5" pupilColor="#2D2D2D" :forceLookX="(password.length > 0 && showPassword) ? -5 : undefined" :forceLookY="(password.length > 0 && showPassword) ? -4 : undefined" />
            </div>
            <!-- 黄色角色嘴巴 -->
            <div 
              class="absolute w-20 h-1 bg-gray-800 rounded-full transition-all duration-200 ease-out"
              :style="{
                left: (password.length > 0 && showPassword) ? '10px' : `${40 + yellowPos.faceX}px`,
                top: (password.length > 0 && showPassword) ? '88px' : `${88 + yellowPos.faceY}px`
              }"
            />
          </div>
        </div>
      </div>



      <!-- 装饰元素 -->
      <div class="absolute top-1/4 right-1/4 w-64 h-64 bg-white/10 rounded-full blur-3xl"></div>
      <div class="absolute bottom-1/4 left-1/4 w-96 h-96 bg-white/5 rounded-full blur-3xl"></div>
    </div>

    <!-- 右侧登录区域 -->
    <div class="flex items-center justify-center p-8 bg-white">
      <div class="w-full max-w-[420px]">
        <!-- 标题 -->
        <div class="text-center mb-10">
          <h1 class="text-3xl font-bold tracking-tight mb-2">欢迎回来</h1>
          <p class="text-muted-foreground text-sm">请输入您的用户名和密码</p>
        </div>

        <!-- 登录表单 -->
        <form @submit.prevent="handleSubmit" class="space-y-5">
          <!-- 用户名 -->
          <div class="space-y-2">
            <label for="username" class="text-sm font-medium">用户名</label>
            <div class="relative">
              <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
              <input
                id="username"
                v-model="username"
                type="text"
                placeholder="请输入用户名"
                autocomplete="off"
                class="w-full h-12 pl-10 pr-4 bg-background border border-input rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
              />
            </div>
          </div>

          <!-- 密码 -->
          <div class="space-y-2">
            <label for="password" class="text-sm font-medium">密码</label>
            <div class="relative">
              <input
                id="password"
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="••••••••"
                class="w-full h-12 px-4 pr-12 bg-background border border-input rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
              />
              <button 
                type="button"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                @click="showPassword = !showPassword"
              >
                <svg v-if="showPassword" class="size-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                </svg>
                <svg v-else class="size-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              </button>
            </div>
          </div>

          <!-- 错误提示 -->
          <div v-if="error" class="flex items-center gap-3 p-4 text-sm text-red-600 bg-red-50 border border-red-200 rounded-xl shadow-sm animate-fade-in">
            <div class="flex-shrink-0 w-8 h-8 rounded-full bg-red-100 flex items-center justify-center">
              <svg class="w-4 h-4 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <span class="font-medium">{{ error }}</span>
          </div>

          <!-- 登录按钮 -->
          <button 
            type="submit" 
            class="w-full h-12 text-base font-medium bg-black text-white rounded-lg hover:bg-gray-800 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            :disabled="isLoading"
          >
            {{ isLoading ? '登录中...' : '登录' }}
          </button>
        </form>

        <!-- 注册链接 -->
        <div class="text-center text-sm text-muted-foreground mt-8">
          还没有账号？
          <router-link to="/register" class="text-foreground font-medium hover:underline">
            立即注册
          </router-link>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import { login } from '@/api/auth'
import EyeBall from '@/components/cartoon/EyeBall.vue'
import Pupil from '@/components/cartoon/Pupil.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 表单状态
const username = ref('')
const password = ref('')
const showPassword = ref(false)
const error = ref('')
const isLoading = ref(false)

// 动画状态
const mouseX = ref(0)
const mouseY = ref(0)
const isTyping = ref(false)
const isLookingAtEachOther = ref(false)
const isPurpleBlinking = ref(false)
const isBlackBlinking = ref(false)
const isPurplePeeking = ref(false)
const hasLookedAtEachOther = ref(false)
const hasStretched = ref(false)
const isLoginError = ref(false)

// 角色引用
const purpleRef = ref(null)
const blackRef = ref(null)
const yellowRef = ref(null)
const orangeRef = ref(null)

// 超时引用
let typingTimeout = null
let peekingTimeout = null
let purpleBlinkTimeout = null
let blackBlinkTimeout = null

// 计算角色位置（对照 React 的 calculatePosition）
function calculatePosition(ref) {
  if (!ref.value) return { faceX: 0, faceY: 0, bodySkew: 0 }
  
  const rect = ref.value.getBoundingClientRect()
  const centerX = rect.left + rect.width / 2
  const centerY = rect.top + rect.height / 3 // 关注头部区域
  
  const deltaX = mouseX.value - centerX
  const deltaY = mouseY.value - centerY
  
  // 面部移动（限制范围）
  const faceX = Math.max(-15, Math.min(15, deltaX / 20))
  const faceY = Math.max(-10, Math.min(10, deltaY / 30))
  
  // 身体倾斜
  const bodySkew = Math.max(-6, Math.min(6, -deltaX / 120))
  
  return { faceX, faceY, bodySkew }
}

// 计算各角色位置
const purplePos = computed(() => calculatePosition(purpleRef))
const blackPos = computed(() => calculatePosition(blackRef))
const yellowPos = computed(() => calculatePosition(yellowRef))
const orangePos = computed(() => calculatePosition(orangeRef))

// 鼠标移动处理
function handleMouseMove(e) {
  mouseX.value = e.clientX
  mouseY.value = e.clientY
}

// 紫色角色眨眼（递归 setTimeout，随机间隔 3-7 秒）
function schedulePurpleBlink() {
  const interval = Math.random() * 4000 + 3000
  purpleBlinkTimeout = setTimeout(() => {
    isPurpleBlinking.value = true
    setTimeout(() => {
      isPurpleBlinking.value = false
      schedulePurpleBlink() // 递归
    }, 150)
  }, interval)
}

// 黑色角色眨眼（递归 setTimeout，随机间隔 3-7 秒）
function scheduleBlackBlink() {
  const interval = Math.random() * 4000 + 3000
  blackBlinkTimeout = setTimeout(() => {
    isBlackBlinking.value = true
    setTimeout(() => {
      isBlackBlinking.value = false
      scheduleBlackBlink() // 递归
    }, 150)
  }, interval)
}

// 监听输入状态：输入用户名时伸头，输入密码时头回去
watch([username, password], ([user, pwd]) => {
  // 输入用户名时伸头
  if (user.length > 0 && !isTyping.value) {
    isTyping.value = true
    if (typingTimeout) clearTimeout(typingTimeout)
  }
  
  // 输入密码时头回去
  if (pwd.length > 0 && isTyping.value) {
    isTyping.value = false
  }
  
  // 清空输入后重置
  if (user.length === 0 && pwd.length === 0) {
    isTyping.value = false
    isLookingAtEachOther.value = false
    hasLookedAtEachOther.value = false
    hasStretched.value = false
  }
  
  // 只在第一次输入时对视
  const hasInput = user.length > 0 || pwd.length > 0
  if (hasInput && !hasLookedAtEachOther.value) {
    isLookingAtEachOther.value = true
    hasLookedAtEachOther.value = true
    setTimeout(() => {
      isLookingAtEachOther.value = false
    }, 800)
  }
})

// 密码可见时偷看效果（对照 React 的 peeking useEffect）
watch([password, showPassword, isPurplePeeking], ([pwd, show, peeking]) => {
  if (peekingTimeout) clearTimeout(peekingTimeout)
  
  if (pwd.length > 0 && show) {
    // 随机延迟后开始偷看
    peekingTimeout = setTimeout(() => {
      isPurplePeeking.value = true
      setTimeout(() => {
        isPurplePeeking.value = false
      }, 800)
    }, Math.random() * 3000 + 2000)
  } else {
    isPurplePeeking.value = false
  }
})

onMounted(() => {
  window.addEventListener('mousemove', handleMouseMove)
  schedulePurpleBlink()
  scheduleBlackBlink()
})

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove)
  if (purpleBlinkTimeout) clearTimeout(purpleBlinkTimeout)
  if (blackBlinkTimeout) clearTimeout(blackBlinkTimeout)
  if (typingTimeout) clearTimeout(typingTimeout)
  if (peekingTimeout) clearTimeout(peekingTimeout)
})

// 登录提交
async function handleSubmit() {
  if (!username.value || !password.value) {
    error.value = '请输入用户名和密码'
    return
  }
  
  error.value = ''
  isLoading.value = true
  try {
    const res = await login({ username: username.value, password: password.value })
    userStore.setToken(res.data.token)
    await userStore.fetchUserInfo()
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    error.value = e.message || '登录失败，请重试'
    // 触发小动物摇头动画
    isLoginError.value = true
    setTimeout(() => {
      isLoginError.value = false
    }, 600)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.relative.z-20.flex.items-end.justify-center {
  overflow: visible;
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fade-in 0.3s ease-out;
}
</style>
