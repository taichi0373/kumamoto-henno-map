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

export const WithContent = {
  render: () => ({
    components: { AppHeader },
    template: `
      <div style="min-height: 100vh;">
        <AppHeader />
        <main style="padding: 40px 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; min-height: calc(100vh - 80px);">
          <div style="max-width: 1200px; margin: 0 auto;">
            <h1 style="font-size: 3rem; text-align: center; margin-bottom: 2rem;">
              運転免許返納特典マップ
            </h1>
            <p style="font-size: 1.2rem; text-align: center; margin-bottom: 3rem; opacity: 0.9;">
              運転免許を返納した方が利用できる特典を地図で探せるサービスです
            </p>
            
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 30px; margin-top: 4rem;">
              <div style="background: rgba(255,255,255,0.1); padding: 30px; border-radius: 12px; backdrop-filter: blur(10px);">
                <h3 style="margin-top: 0; font-size: 1.5rem;">🗺️ 地図表示</h3>
                <p>特典提供店舗の位置を地図上で確認できます</p>
              </div>
              
              <div style="background: rgba(255,255,255,0.1); padding: 30px; border-radius: 12px; backdrop-filter: blur(10px);">
                <h3 style="margin-top: 0; font-size: 1.5rem;">🎁 特典検索</h3>
                <p>条件に応じて利用可能な特典を検索できます</p>
              </div>
              
              <div style="background: rgba(255,255,255,0.1); padding: 30px; border-radius: 12px; backdrop-filter: blur(10px);">
                <h3 style="margin-top: 0; font-size: 1.5rem;">🚌 経路案内</h3>
                <p>公共交通機関を使った経路をご案内します</p>
              </div>
            </div>
          </div>
        </main>
      </div>
    `,
  }),
};

export const NavigationStates = {
  render: () => ({
    components: { AppHeader },
    data() {
      return {
        currentRoute: 'home',
      };
    },
    template: `
      <div>
        <div style="padding: 20px; background: #e9ecef; margin-bottom: 20px;">
          <h4 style="margin: 0 0 16px 0;">ナビゲーション状態テスト</h4>
          <p style="margin: 0; font-size: 14px; color: #6c757d;">
            実際のルーターナビゲーションの代わりに、現在のルート状態をシミュレートします
          </p>
        </div>
        <AppHeader />
        <div style="padding: 40px 20px; background: #f8f9fa;">
          <h2>ページコンテンツエリア</h2>
          <div style="display: flex; gap: 12px; margin: 20px 0; flex-wrap: wrap;">
            <button 
              v-for="route in routes" 
              :key="route.name"
              @click="currentRoute = route.name"
              :style="\`padding: 8px 16px; border: 1px solid #007bff; border-radius: 4px; background: \${currentRoute === route.name ? '#007bff' : 'white'}; color: \${currentRoute === route.name ? 'white' : '#007bff'}; cursor: pointer;\`"
            >
              {{ route.label }}
            </button>
          </div>
          <div style="background: white; padding: 20px; border-radius: 8px; margin-top: 20px;">
            <h3>現在のページ: {{ getCurrentRouteLabel() }}</h3>
            <p>ヘッダーのナビゲーションリンクがアクティブ状態を適切に反映しているかを確認できます。</p>
          </div>
        </div>
      </div>
    `,
    computed: {
      routes() {
        return [
          { name: 'home', label: 'ホーム' },
          { name: 'signup', label: '新規登録' },
          { name: 'login', label: 'ログイン' },
          { name: 'profile', label: 'プロフィール' },
        ];
      },
    },
    methods: {
      getCurrentRouteLabel() {
        const route = this.routes.find(r => r.name === this.currentRoute);
        return route ? route.label : '不明';
      },
    },
  }),
};