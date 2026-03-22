import AppHeader from './AppHeader.vue';

export default {
  title: 'Design System/Organisms/AppHeader',
  component: AppHeader,
  parameters: {
    layout: 'fullscreen',
  },
  tags: ['autodocs'],
};

export const Default = {
  render: () => ({
    components: { AppHeader },
    template: `
      <div>
        <AppHeader />
        <div style="padding: 20px; margin-top: 20px; background: #f8f9fa; min-height: 400px;">
          <h2>メインコンテンツエリア</h2>
          <p>ヘッダーコンポーネントの表示例です。</p>
          <p>このコンポーネントは認証状態やユーザー情報を内部的に管理しています。</p>
        </div>
      </div>
    `,
  }),
};

export const LoggedIn = {
  render: () => ({
    components: { AppHeader },
    beforeCreate() {
      // モック認証状態の設定
      sessionStorage.setItem('authToken', 'mock-token');
      sessionStorage.setItem('username', 'テストユーザー');
    },
    template: `
      <div>
        <AppHeader />
        <div style="padding: 20px; margin-top: 20px; background: #f8f9fa; min-height: 400px;">
          <h2>メインコンテンツエリア（ログイン状態）</h2>
          <p>ログイン済みの状態でのヘッダー表示例です。</p>
          <p>ドロップダウンメニューやユーザー名が表示されます。</p>
          <div style="background: #d4edda; padding: 16px; border-radius: 8px; margin-top: 20px;">
            <strong>注意:</strong> この例では、StorybookでのテストのためにモックのSessionStorageを使用しています。
          </div>
        </div>
      </div>
    `,
  }),
};

export const LoggedOut = {
  render: () => ({
    components: { AppHeader },
    beforeCreate() {
      // セッションストレージをクリア（ログアウト状態）
      sessionStorage.removeItem('authToken');
      sessionStorage.removeItem('username');
    },
    template: `
      <div>
        <AppHeader />
        <div style="padding: 20px; margin-top: 20px; background: #f8f9fa; min-height: 400px;">
          <h2>メインコンテンツエリア（ログアウト状態）</h2>
          <p>ログアウト状態でのヘッダー表示例です。</p>
          <p>ログインリンクが表示され、ユーザー情報は非表示になります。</p>
        </div>
      </div>
    `,
  }),
};

export const MobileView = {
  parameters: {
    viewport: {
      defaultViewport: 'mobile1',
    },
  },
  render: () => ({
    components: { AppHeader },
    template: `
      <div>
        <AppHeader />
        <div style="padding: 20px; margin-top: 20px; background: #f8f9fa; min-height: 400px;">
          <h2>モバイルビュー</h2>
          <p>モバイル画面でのヘッダー表示例です。</p>
          <p>ハンバーガーメニューが表示され、サイドメニューでナビゲーションが可能になります。</p>
          <div style="background: #fff3cd; padding: 16px; border-radius: 8px; margin-top: 20px;">
            <strong>ヒント:</strong> 右上のハンバーガーメニューアイコンをタップしてメニューを開いてください。
          </div>
        </div>
      </div>
    `,
  }),
};
