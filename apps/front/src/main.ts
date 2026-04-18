import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { setTokenProvider, setUnauthorizedHandler } from '@/utils/api'
import { useAuthStore } from '@/stores/auth'

// PrimeVue Import
import PrimeVue from 'primevue/config'
import ToastService from 'primevue/toastservice'
import Ripple from 'primevue/ripple'
import BadgeDirective from 'primevue/badgedirective'
import Aura from '@primevue/themes/aura'
import 'primeicons/primeicons.css'
import 'primeflex/primeflex.css'

// SCSS Import
import './assets/scss/base.scss'

// MapLibre GL CSS Import
import 'maplibre-gl/dist/maplibre-gl.css'

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

// ページ直叩き（例: /profile）時、router.beforeEach はマウント前に実行されるため
// ここでセッションを復元してから mount する。
// こうすることでルートガードが token の有無を正しく判定できる。
;(async () => {
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
  app.directive('badge', BadgeDirective)

  app.mount('#app')
})()
