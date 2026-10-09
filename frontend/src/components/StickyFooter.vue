<template>
  <footer
    ref="footerRef"
    :class="['footer-container', { 'is-visible': isVisible }]"
    class="bg-gradient-to-br from-gray-50 via-gray-100 to-gray-50/90 py-8 md:py-12 px-4 md:px-12 relative overflow-hidden"
  >
    <!-- Animated Background Elements -->
    <div class="absolute inset-0 bg-gradient-to-t from-white/20 to-transparent pointer-events-none" />
    <div class="bg-orb bg-orb-1" />
    <div class="bg-orb bg-orb-2" />

    <!-- Navigation Section -->
    <div class="relative z-10 max-w-7xl mx-auto">
      <div class="grid grid-cols-2 md:grid-cols-3 gap-6 md:gap-12 lg:gap-20">
        <div
          v-for="(section, index) in footerSections"
          :key="section.title"
          class="footer-section"
          :style="{ transitionDelay: `${index * 100}ms` }"
        >
          <h3
            class="mb-2 uppercase text-gray-400 text-xs font-semibold tracking-wider border-b border-gray-200 pb-1 hover:text-gray-700 transition-colors duration-300"
          >
            {{ section.title }}
          </h3>
          <a
            v-for="(link, linkIndex) in section.links"
            :key="linkIndex"
            :href="link.href"
            class="block text-gray-500 hover:text-gray-900 transition-all duration-300 font-sans text-xs md:text-sm py-0.5 hover:translate-x-2"
          >
            <span class="relative">
              {{ link.label }}
              <span class="absolute bottom-0 left-0 h-0.5 bg-primary-500 w-0 hover:w-full transition-all duration-300" />
            </span>
          </a>
        </div>
      </div>
    </div>

    <!-- Footer Bottom Section -->
    <div class="footer-bottom relative z-10 max-w-7xl mx-auto mt-8">
      <div class="border-t border-gray-200 pt-6 flex flex-col md:flex-row justify-between items-center gap-4">
        <div class="flex items-center gap-3">
          <span class="font-bold text-xl md:text-2xl text-gray-900 font-display">Second Hand Trading Platform</span>
        </div>

        <div class="flex gap-2 md:gap-3">
          <a
            v-for="social in socialLinks"
            :key="social.label"
            :href="social.href"
            :aria-label="social.label"
            class="social-btn"
          >
            <svg v-if="social.icon === 'wechat'" class="social-btn-text" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M8.691 2.188C3.891 2.188 0 5.476 0 9.53c0 2.212 1.17 4.203 3.002 5.55a.59.59 0 0 1 .213.665l-.39 1.48c-.019.07-.048.141-.048.213 0 .163.13.295.29.295a.326.326 0 0 0 .167-.054l1.903-1.114a.864.864 0 0 1 .717-.098 10.16 10.16 0 0 0 2.837.403c.276 0 .543-.027.811-.05a6.33 6.33 0 0 1-.252-1.795c0-3.58 3.35-6.49 7.488-6.49.271 0 .537.017.799.04C16.603 4.393 13.027 2.188 8.691 2.188zm-3.11 5.06c-.626 0-1.133-.507-1.133-1.133S4.955 4.982 5.58 4.982s1.134.508 1.134 1.133-.508 1.133-1.134 1.133zm5.995 0c-.626 0-1.133-.507-1.133-1.133s.507-1.133 1.133-1.133 1.134.508 1.134 1.133-.508 1.133-1.134 1.133zM15.24 10.66c-3.67 0-6.647 2.538-6.647 5.67 0 3.133 2.977 5.67 6.647 5.67.73 0 1.434-.11 2.09-.316a.672.672 0 0 1 .557.075l1.476.864a.253.253 0 0 0 .13.042.228.228 0 0 0 .226-.228c0-.056-.023-.111-.037-.165l-.303-1.148a.459.459 0 0 1 .165-.516C21.19 19.688 22 18.082 22 16.33c0-3.132-2.977-5.67-6.647-5.67h-.114zm-2.34 3.694a.88.88 0 1 1 0-1.76.88.88 0 0 1 0 1.76zm4.68 0a.88.88 0 1 1 0-1.76.88.88 0 0 1 0 1.76z"/>
            </svg>
            <svg v-else-if="social.icon === 'weibo'" class="social-btn-text" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M10.098 20.323c-3.977.391-7.414-1.406-7.672-4.02-.259-2.609 2.759-5.047 6.74-5.441 3.979-.394 7.413 1.404 7.671 4.018.259 2.6-2.759 5.049-6.739 5.443zM20.196 11.048c-.124-.358-.516-.546-.874-.422-3.604 1.24-5.737 3.12-5.502 4.213.118.547.617.868 1.116.717 2.173-.66 3.87-2.38 4.26-3.508zM17.05 8.287c-.206-.07-.348-.266-.319-.435.153-.885-.096-1.633-.677-2.119-.702-.589-1.674-.633-2.59-.127-.262.144-.322.484-.134.759.187.275.527.335.789.191.513-.277 1.067-.25 1.452.077.385.327.496.812.332 1.249-.075.199-.265.316-.467.286a.552.552 0 0 1-.18-.075c-.255-.136-.553-.039-.663.219-.11.258.008.553.263.659.806.336 1.683.375 2.486.108.803-.267 1.37-.86 1.527-1.556.078-.343-.097-.69-.415-.845l-.231-.074zM9.67 16.24c-1.563.47-3.095-.233-3.437-1.571-.342-1.339.672-2.626 2.257-2.87 1.538-.237 3.018.493 3.306 1.759.295 1.303-.553 2.213-2.126 2.682zm7.562-7.352c-.349-.104-.59-.378-.621-.656a6.472 6.472 0 0 0-2.225-3.816C12.528 2.787 9.886 2.4 7.57 3.12c-.653.202-.99.913-.754 1.584.236.671.973.996 1.626.794 1.576-.48 3.284-.217 4.616.742 1.332.959 2.097 2.447 2.074 3.989a1.16 1.16 0 0 0 .56 1.051c.567.344 1.287.138 1.54-.392.072-.152.098-.315.098-.474 0-.085-.009-.171-.029-.254l-.026-.023z"/>
            </svg>
            <svg v-else class="social-btn-text" viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M12.003 2C6.175 2 3.015 5.365 3.015 9.408c0 2.562 1.44 4.878 3.684 6.356-.156.54-.564 1.866-.644 2.14-.096.328.12.328.252.236.104-.072 1.648-1.132 2.36-1.62.676.1 1.38.156 2.096.156h.24c5.04 0 7.996-2.808 7.996-6.268C20.996 5.836 18.032 2 12.003 2zm-3.08 7.176c-.548 0-.992-.444-.992-.992s.444-.992.992-.992.992.444.992.992-.444.992-.992.992zm6.16 0c-.548 0-.992-.444-.992-.992s.444-.992.992-.992.992.444.992.992-.444.992-.992.992z"/>
            </svg>
            <span class="social-btn-circle"></span>
          </a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const footerRef = ref(null)
const isVisible = ref(false)
let observer = null

const footerSections = [
  {
    title: '快速链接',
    links: [
      { label: '浏览商品', href: '/products' },
      { label: '发布商品', href: '/publish' },
      { label: '我的订单', href: '/orders/buy' },
      { label: '个人中心', href: '/user/profile' },
    ],
  },
  {
    title: '帮助中心',
    links: [
      { label: '使用指南', href: '/guide' },
      { label: '常见问题', href: '#' },
      { label: '联系客服', href: '#' },
      { label: '反馈建议', href: '#' },
    ],
  },
  {
    title: '关于我们',
    links: [
      { label: '用户协议', href: '#' },
      { label: '隐私政策', href: '#' },
      { label: '平台规则', href: '#' },
      { label: '加入我们', href: '#' },
    ],
  },
]

const socialLinks = [
  { href: '#', label: '微信', icon: 'wechat' },
  { href: '#', label: '微博', icon: 'weibo' },
  { href: '#', label: 'QQ', icon: 'qq' },
]

onMounted(() => {
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          isVisible.value = true
        }
      })
    },
    { threshold: 0.1 }
  )
  if (footerRef.value) {
    observer.observe(footerRef.value)
  }
})

onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})
</script>

<style scoped>
.footer-container {
  opacity: 0;
  transform: translateY(30px);
  transition: opacity 0.8s ease-out, transform 0.8s ease-out;
}

.footer-container.is-visible {
  opacity: 1;
  transform: translateY(0);
}

.footer-section {
  opacity: 0;
  transform: translateX(-20px);
  transition: opacity 0.6s ease-out, transform 0.6s ease-out;
}

.is-visible .footer-section {
  opacity: 1;
  transform: translateX(0);
}

.footer-bottom {
  opacity: 0;
  transition: opacity 0.8s ease-out 0.3s;
}

.is-visible .footer-bottom {
  opacity: 1;
}

.bg-orb {
  position: absolute;
  border-radius: 9999px;
  filter: blur(48px);
  pointer-events: none;
}

.bg-orb-1 {
  top: 0;
  right: 0;
  width: 12rem;
  height: 12rem;
  background: rgba(217, 119, 6, 0.05);
  animation: orb-float-1 4s ease-in-out infinite;
}

@media (min-width: 768px) {
  .bg-orb-1 {
    width: 24rem;
    height: 24rem;
  }
}

.bg-orb-2 {
  bottom: 0;
  left: 0;
  width: 12rem;
  height: 12rem;
  background: rgba(236, 72, 153, 0.05);
  animation: orb-float-2 5s ease-in-out infinite 1s;
}

@media (min-width: 768px) {
  .bg-orb-2 {
    width: 24rem;
    height: 24rem;
  }
}

@keyframes orb-float-1 {
  0%, 100% { transform: scale(1); opacity: 0.3; }
  50% { transform: scale(1.1); opacity: 0.6; }
}

@keyframes orb-float-2 {
  0%, 100% { transform: scale(1); opacity: 0.2; }
  50% { transform: scale(1.2); opacity: 0.5; }
}

.social-btn {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 1.5px solid rgba(100, 100, 100, 0.4);
  background: transparent;
  cursor: pointer;
  overflow: hidden;
  transition: all 600ms cubic-bezier(0.23, 1, 0.32, 1);
}

.social-btn:hover {
  border-color: transparent;
  transform: scale(1.1);
}

.social-btn-text {
  position: relative;
  z-index: 1;
  font-size: 14px;
  font-weight: 700;
  color: #9ca3af;
  transition: color 400ms ease-out;
  display: flex;
  align-items: center;
  justify-content: center;
}

.social-btn:hover .social-btn-text {
  color: white;
}

.social-btn-circle {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 16px;
  height: 16px;
  background: #111;
  border-radius: 50%;
  opacity: 0;
  transition: all 800ms cubic-bezier(0.19, 1, 0.22, 1);
}

.social-btn:hover .social-btn-circle {
  width: 60px;
  height: 60px;
  opacity: 1;
}
</style>
