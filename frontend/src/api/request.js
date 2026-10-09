import axios from 'axios'
import { useUserStore } from '@/store/user'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      // 提取后端返回的业务错误消息
      const msg = data?.message || data?.msg || ''
      if (status === 401) {
        const userStore = useUserStore()
        userStore.logout()
        router.push({ name: 'Login', query: { redirect: router.currentRoute.value.fullPath } })
        return Promise.reject(new Error(msg || '登录已过期，请重新登录'))
      } else if (status === 403) {
        return Promise.reject(new Error(msg || '没有权限访问'))
      } else if (status === 400) {
        return Promise.reject(new Error(msg || '请求参数错误'))
      } else if (status === 500) {
        return Promise.reject(new Error(msg || '服务器内部错误'))
      }
      return Promise.reject(new Error(msg || `请求失败 (${status})`))
    }
    return Promise.reject(error)
  }
)

export default request
