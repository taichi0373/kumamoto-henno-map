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
import { definePreset } from '@primeuix/themes';
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

const MyPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '{cyan.50}',
      100: '{cyan.50}',
      200: '{cyan.100}',
      300: '{cyan.200}',
      400: '{cyan.300}',
      500: '{cyan.400}',
      600: '{cyan.500}',
      700: '{cyan.600}',
      800: '{cyan.700}',
      900: '{cyan.800}'
    }
  }
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
        preset: MyPreset,
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
