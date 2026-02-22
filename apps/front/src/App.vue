<template>
  <div id="app">
    <AppHeader />
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import AppHeader from './components/organisms/AppHeader.vue'

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

const setupResponsiveDesign = () => {
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
