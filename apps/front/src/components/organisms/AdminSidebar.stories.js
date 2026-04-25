import AdminSidebar from './AdminSidebar.vue';

export default {
  title: 'Design System/Organisms/AdminSidebar',
  component: AdminSidebar,
  parameters: {
    layout: 'fullscreen',
  },
  tags: ['autodocs'],
  argTypes: {
  },
};

/** 展開状態（デフォルト） */
export const Default = {
  render: () => ({
    components: { AdminSidebar },
    template: `
      <div style="position: relative; min-height: 100vh; background: #f5f5f5;">
        <AdminSidebar />
        <div style="margin-left: 220px; padding: 2rem; transition: margin-left 0.3s ease;">
          <h2 style="margin-top: 0;">メインコンテンツエリア</h2>
          <p>管理者サイドバーの表示状態です。</p>
        </div>
      </div>
    `,
  }),
};

/** 閉じた状態 */
export const Closed = {
  render: () => ({
    template: `
      <div style="position: relative; min-height: 100vh; background: #f5f5f5;">
        <div style="padding: 2rem;">
          <h2 style="margin-top: 0;">サイドバー非表示状態</h2>
          <p>メインコンテンツがフル幅で表示されます。</p>
        </div>
      </div>
    `,
  }),
};
