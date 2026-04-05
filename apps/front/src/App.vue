<template>
  <div id="app">
    <AppHeader />
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from './components/organisms/AppHeader.vue'
import { setUnauthorizedHandler, setTokenProvider } from '@/utils/api'
import { useAuthStore } from '@/stores/auth'

/** 認証ストア */
const authStore = useAuthStore()
const router = useRouter()

/** 401発生時のハンドラーを登録（setup()トップレベルで実行し未登録状態をなくす） */
setUnauthorizedHandler(() => {
  authStore.logout().finally(() => {
    router.push('/login')
  })
})

/** JWTトークンプロバイダーを登録 */
setTokenProvider(() => authStore.getToken)

/**
 * リサイズイベントハンドラ
 */
const handleResize = () => {
  const formSelectElements = document.querySelectorAll('.form-select')
  if (window.innerWidth < 360) {
    formSelectElements.forEach(element => {
      element.classList.add('form-select-sm')
    })
  } else {
    formSelectElements.forEach(element => {
      element.classList.remove('form-select-sm')
    })
  }

  // vh設定
  const height = window.innerHeight
  document.documentElement.style.setProperty('--vh', height / 100 + 'px')
}

const setupResponsiveDesign = () => {
  window.addEventListener('resize', handleResize)
  handleResize()
}

onMounted(() => {
  // ウィンドウリサイズイベントの設定
  setupResponsiveDesign()
})

onUnmounted(() => {
  // リサイズイベントの解除
  window.removeEventListener('resize', handleResize)
})
</script>

<style>
/* libraries */
@import 'maplibre-gl/dist/maplibre-gl.css';

#app {
  font-family: "Helvetica Neue", Arial, "Hiragino Kaku Gothic ProN", "Hiragino Sans", Meiryo, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  width: 100vw;
  height: calc(var(--vh) * 100);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
</style>
