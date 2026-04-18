import { ref } from 'vue';
import AdminSidebar from './AdminSidebar.vue';

export default {
  title: 'Design System/Organisms/AdminSidebar',
  component: AdminSidebar,
  parameters: {
    layout: 'fullscreen',
  },
  tags: ['autodocs'],
  argTypes: {
    visible: {
      control: 'boolean',
      description: 'サイドバーの表示状態',
    },
  },
};

/** 展開状態（デフォルト） */
export const Default = {
  render: (args) => ({
    components: { AdminSidebar },
    setup() {
      const visible = ref(args.visible ?? true);
      return { visible };
    },
    template: `
      <div style="position: relative; min-height: 100vh; background: #f5f5f5;">
        <AdminSidebar v-model:visible="visible" />
        <div :style="{ marginLeft: visible ? '220px' : '0', padding: '2rem', transition: 'margin-left 0.3s ease' }">
          <button @click="visible = !visible" style="margin-bottom: 1rem; padding: 0.4rem 0.8rem; cursor: pointer;">
            ☰ サイドバー切替
          </button>
          <h2 style="margin-top: 0;">メインコンテンツエリア</h2>
          <p>PrimeVue Drawer をラップした管理者サイドバーです。</p>
          <p>左上のボタンでサイドバーの開閉を切り替えられます。</p>
        </div>
      </div>
    `,
  }),
  args: { visible: true },
};

/** 閉じた状態 */
export const Closed = {
  render: (args) => ({
    components: { AdminSidebar },
    setup() {
      const visible = ref(args.visible ?? false);
      return { visible };
    },
    template: `
      <div style="position: relative; min-height: 100vh; background: #f5f5f5;">
        <AdminSidebar v-model:visible="visible" />
        <div :style="{ marginLeft: visible ? '220px' : '0', padding: '2rem', transition: 'margin-left 0.3s ease' }">
          <button @click="visible = !visible" style="margin-bottom: 1rem; padding: 0.4rem 0.8rem; cursor: pointer;">
            ☰ サイドバーを開く
          </button>
          <h2 style="margin-top: 0;">サイドバー非表示状態</h2>
          <p>メインコンテンツがフル幅で表示されます。</p>
        </div>
      </div>
    `,
  }),
  args: { visible: false },
};
