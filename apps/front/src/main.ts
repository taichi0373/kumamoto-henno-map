import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

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

// アプリケーションインスタンスの作成
const app = createApp(App);

// ルーターの使用
app.use(router);
// PrimeVueのプラグイン使用
app.use(PrimeVue, { ripple: true, theme: { preset: Aura } });
app.use(ToastService);
// PrimeVueのディレクティブの登録
app.directive('ripple', Ripple);
app.directive('badge', BadgeDirective);

// アプリケーションのマウント
app.mount('#app');
