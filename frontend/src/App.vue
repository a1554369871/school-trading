<template>
  <div id="app">
    <DefaultLayout v-if="showDefaultLayout">
      <router-view />
    </DefaultLayout>
    <router-view v-else />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import DefaultLayout from '@/layouts/DefaultLayout.vue'

const route = useRoute()
const userStore = useUserStore()

// 首页和认证页面不使用 DefaultLayout
const showDefaultLayout = computed(() => {
  const isAuthPage = route.meta.guest === true
  const isHomePage = route.name === 'Home'
  return !isAuthPage && !isHomePage
})

onMounted(() => {
  const token = localStorage.getItem('token')
  if (token) {
    userStore.token = token
    userStore.fetchUserInfo()
  }
})
</script>
