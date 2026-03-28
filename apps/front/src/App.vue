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
import { setUnauthorizedHandler } from '@/utils/api'
import { useAuthStore } from '@/stores/auth'

/** 401発生時のハンドラーを登録（setup()トップレベルで実行し未登録状態をなくす） */
const authStore = useAuthStore()
const router = useRouter()
setUnauthorizedHandler(() => {
  authStore.logout().finally(() => {
    router.push('/login')
  })
})

const restoreUserSession = () => {
  // サーバー側でトークンの有効性を確認
  const userToken = sessionStorage.getItem('userToken')
  const userId = sessionStorage.getItem('userId')

  if (userToken && userId) {
    // サーバーAPIでセッション確認
    validateSession(userToken)
  }
}

const validateSession = async (token: string) => {
  try {
    const response = await fetch('/api/validate-session', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    if (!response.ok) {
      // トークンが無効な場合はクリア
      clearSession()
    }
  } catch (error) {
    console.error('Session validation error:', error)
    clearSession()
  }
}

const clearSession = () => {
  sessionStorage.removeItem('userToken')
  sessionStorage.removeItem('userId')
}

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
  // ユーザートークンの確認
  const userToken = sessionStorage.getItem('userToken')
  if (userToken) {
    // セッション復元のロジック
    restoreUserSession()
  }
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
