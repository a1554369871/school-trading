import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { supabase } from '@/lib/supabase'
import {
  login as apiLogin,
  register as apiRegister,
  getUserInfo,
} from '@/api/auth'
import { getUnreadCount } from '@/api/message'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)
  const unreadCount = ref(0)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 1)

  function setToken(value) {
    token.value = value || ''
    if (value) localStorage.setItem('token', value)
    else localStorage.removeItem('token')
  }

  /** 应用启动时恢复 Supabase 会话并拉取当前用户 */
  async function initAuth() {
    const { data } = await supabase.auth.getSession()
    if (data.session) {
      setToken(data.session.access_token)
      try {
        await fetchUserInfo()
      } catch {
        // 会话失效或账号被冻结时 fetchUserInfo 内部已登出
      }
    } else {
      setToken('')
      user.value = null
    }

    supabase.auth.onAuthStateChange((_event, session) => {
      setToken(session?.access_token || '')
      if (!session) {
        user.value = null
        unreadCount.value = 0
      }
    })
  }

  async function login(username, password) {
    const res = await apiLogin({ username, password })
    setToken(res.data.token)
    await fetchUserInfo()
    return res
  }

  async function register(data) {
    return apiRegister(data)
  }

  async function fetchUserInfo() {
    try {
      const res = await getUserInfo()
      if (res.data?.status === 0) {
        await logout()
        throw new Error('账号已被冻结')
      }
      user.value = res.data
      await fetchUnreadCount()
      return res.data
    } catch (error) {
      await logout()
      throw error
    }
  }

  async function fetchUnreadCount() {
    try {
      const res = await getUnreadCount()
      unreadCount.value = res.data || 0
    } catch {
      unreadCount.value = 0
    }
  }

  async function logout() {
    try {
      await supabase.auth.signOut()
    } catch {
      // 忽略网络错误，本地状态照常清理
    }
    setToken('')
    user.value = null
    unreadCount.value = 0
  }

  return {
    token,
    user,
    unreadCount,
    isLoggedIn,
    isAdmin,
    setToken,
    initAuth,
    login,
    register,
    fetchUserInfo,
    fetchUnreadCount,
    logout,
  }
})
