import type { Preview } from '@storybook/vue3';
import { setup } from '@storybook/vue3';

// PrimeVue Import
import PrimeVue from 'primevue/config'
import ToastService from 'primevue/toastservice'
import Ripple from 'primevue/ripple'
import BadgeDirective from 'primevue/badgedirective'
import Aura from '@primevue/themes/aura'
import 'primeicons/primeicons.css'
import 'primeflex/primeflex.css'

// Storybookプレビュー用のSCSSをインポート
import './preview.scss';

// Vue Router（プロジェクトで使用している場合）
import { createRouter, createWebHistory } from 'vue-router';

// ルーターのモック設定
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: { template: '<div></div>' } },
  ],
});

// Vue 3アプリケーションのセットアップ
setup((app) => {
  app.use(router);
  app.use(PrimeVue, { ripple: true, theme: { preset: Aura } })
  app.use(ToastService)
  app.directive('ripple', Ripple);
  app.directive('badge', BadgeDirective);
});

const preview: Preview = {
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
    // Viewport設定
    viewport: {
      viewports: {
        mobile: {
          name: 'Mobile',
          styles: {
            width: '375px',
            height: '667px',
          },
        },
        tablet: {
          name: 'Tablet',
          styles: {
            width: '768px',
            height: '1024px',
          },
        },
        desktop: {
          name: 'Desktop',
          styles: {
            width: '1024px',
            height: '768px',
          },
        },
      },
    },
  },
  decorators: [
    (story) => ({
      components: { story },
      template: '<div class="storybook-preview"><story /></div>',
    }),
  ],
};

export default preview;