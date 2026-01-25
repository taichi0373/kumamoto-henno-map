import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// CSS Import
import './assets/css/base.css'

// FontAwesome Import
import '@fortawesome/fontawesome-free/css/all.css'

// MapLibre GL CSS Import
import 'maplibre-gl/dist/maplibre-gl.css'

// アプリケーションインスタンスの作成
const app = createApp(App)

// ルーターの使用
app.use(router)

// アプリケーションのマウント
app.mount('#app')

// 開発環境での設定
if (process.env.NODE_ENV === 'development') {
  // デバッグ用の設定
  app.config.performance = true
  
  // グローバルエラーハンドラー
  app.config.errorHandler = (err: unknown, vm, info: string) => {
    console.error('Vue Global Error:', err)
    console.error('Vue Instance:', vm)
    console.error('Error Info:', info)
  }
  
  // 警告ハンドラー
  app.config.warnHandler = (msg: string, vm, trace: string) => {
    console.warn('Vue Warning:', msg)
    console.warn('Vue Instance:', vm)
    console.warn('Trace:', trace)
  }
}

// プロダクション環境での設定
if (process.env.NODE_ENV === 'production') {
  // プロダクション固有の設定
  app.config.performance = false
  
  // グローバルエラーハンドラー（ログ送信など）
  app.config.errorHandler = (err: unknown, vm, info: string) => {
    // エラーオブジェクトの型チェック
    const error = err instanceof Error ? err : new Error(String(err))
    
    // TODO: エラー追跡サービスへの送信
    console.error('Production Error:', error)
    
    // ユーザーに分かりやすいエラーメッセージを表示
    // 必要に応じてトースト通知などで表示
  }
}

// サービスワーカーの登録（将来的なPWA対応）
if ('serviceWorker' in navigator && process.env.NODE_ENV === 'production') {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js')
      .then((registration) => {
        console.log('SW registered: ', registration)
      })
      .catch((registrationError) => {
        console.log('SW registration failed: ', registrationError)
      })
  })
}

// Vueアプリケーションをウィンドウオブジェクトに追加（デバッグ用）
if (process.env.NODE_ENV === 'development') {
  (window as any).app = app
}
