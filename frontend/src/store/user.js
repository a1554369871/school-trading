import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, register as apiRegister, getUserInfo } from '../api/auth'
import { getUnreadCount as apiGetUnreadCount } from '../api/message'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)
  const unreadCount = ref(0)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 1)

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUser(newUser) {
    user.value = newUser
  }

  async function login(username, password) {
    const res = await apiLogin({ username, password })
    setToken(res.data.token)
    await fetchUser()
  }

  async function register(data) {
    await apiRegister(data)
  }

  async function fetchUser() {
    try {
      const res = await getUserInfo()
      user.value = res.data
    } catch (e) {
      logout()
    }
  }

  async function fetchUserInfo() {
    await fetchUser()
  }

  async function fetchUnreadCount() {
    if (!token.value) { unreadCount.value = 0; return }
    try {
      const res = await apiGetUnreadCount()
      unreadCount.value = res.data || 0
    } catch { /* ignore */ }
  }

  function setUnreadCount(val) {
    unreadCount.value = val
  }

  function logout() {
    token.value = ''
    user.value = null
    unreadCount.value = 0
    localStorage.removeItem('token')
  }

  return { token, user, unreadCount, isLoggedIn, isAdmin, setToken, setUser, login, register, fetchUser, fetchUserInfo, fetchUnreadCount, setUnreadCount, logout }
})
