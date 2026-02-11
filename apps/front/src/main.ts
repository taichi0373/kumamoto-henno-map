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
