import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { setTokenProvider, setUnauthorizedHandler } from '@/utils/api'
import { useAuthStore } from '@/stores/auth'
// vite-plugin-pwa が自動生成する Service Worker 登録モジュール
import { registerSW } from 'virtual:pwa-register'

// maplibre-gl Worker
import { setWorkerUrl } from 'maplibre-gl'
import maplibreglWorkerUrl from 'maplibre-gl/dist/maplibre-gl-worker.mjs?url'

// PrimeVue Import
import PrimeVue from 'primevue/config'
import ToastService from 'primevue/toastservice'
import Ripple from 'primevue/ripple'
import Aura from '@primevue/themes/aura'
import 'primeicons/primeicons.css'
import 'primeflex/primeflex.css'

// SCSS Import
import './assets/scss/base.scss'


const app = createApp(App)
const pinia = createPinia()

// Piniaをルーターより先に初期化（useAuthStore()の前提）
app.use(pinia)

// APIクライアントの初期化
// ここで登録しておくことで、restoreSession()呼び出し時点で設定済みになる
const authStore = useAuthStore()
setTokenProvider(() => authStore.getToken)
setUnauthorizedHandler(() => {
  authStore.logout().finally(() => {
    router.push('/login')
  })
})

// maplibre-gl の WebWorker URL を明示的に指定
setWorkerUrl(maplibreglWorkerUrl)

// Service Worker を登録（PWA対応）
// prompt モードのため、新バージョン検知時はユーザーの同意を得てから更新・再読み込みする
// 開きっぱなしの PWA でも更新に気づけるよう、定期的に新バージョンを確認する
const SW_UPDATE_CHECK_INTERVAL_MS = 60 * 60 * 1000
let hasPendingUpdate = false

const confirmUpdate = () => {
  if (window.confirm('新しいバージョンがあります。更新しますか？')) {
    updateSW(true)
  } else {
    // 見送った場合は次回の定期確認時に再度案内する
    hasPendingUpdate = true
  }
}

const updateSW = registerSW({
  immediate: true,
  onNeedRefresh: confirmUpdate,
  onRegisteredSW(_swUrl, registration) {
    if (!registration) return
    setInterval(() => {
      if (hasPendingUpdate) {
        confirmUpdate()
        return
      }
      if (navigator.onLine) {
        registration.update()
      }
    }, SW_UPDATE_CHECK_INTERVAL_MS)
  },
})

  // ページ直叩き（例: /profile）時、router.beforeEach はマウント前に実行されるため
  // ここでセッションを復元してから mount する。
  // こうすることでルートガードが token の有無を正しく判定できる。
  ; (async () => {
    await authStore.restoreSession()

    app.use(router)
    app.use(PrimeVue, {
      ripple: true,
      // ダークモード設定
      theme: {
        preset: Aura,
        options: {
          darkModeSelector: '.my-app-dark',
        }
      },
    })
    app.use(ToastService)
    app.directive('ripple', Ripple)

    app.mount('#app')
  })()
