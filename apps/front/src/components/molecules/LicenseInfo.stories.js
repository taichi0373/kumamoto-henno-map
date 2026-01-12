import LicenseInfo from './LicenseInfo.vue';

export default {
  title: 'Design System/Molecules/LicenseInfo',
  component: LicenseInfo,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export const Default = {
  render: () => ({
    components: { LicenseInfo },
    template: `
      <div style="width: 600px; min-height: 400px;">
        <LicenseInfo />
      </div>
    `,
  }),
};

export const InSidebar = {
  render: () => ({
    components: { LicenseInfo },
    template: `
      <div style="width: 400px; height: 600px; background: #f5f5f5; padding: 20px; overflow-y: auto;">
        <h3 style="margin: 0 0 20px 0; color: #333;">サイドバー内での表示例</h3>
        <div style="flex: 1; display: flex; flex-direction: column;">
          <div style="flex: 1; background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
            <h4>メインコンテンツエリア</h4>
            <p>ここにページの主要なコンテンツが表示されます。</p>
            <p>ライセンス情報は下部に固定表示されます。</p>
          </div>
          <LicenseInfo />
        </div>
      </div>
    `,
  }),
};

export const Responsive = {
  render: () => ({
    components: { LicenseInfo },
    template: `
      <div>
        <h3 style="margin-bottom: 20px; text-align: center;">レスポンシブ対応の確認</h3>
        <div style="display: flex; flex-direction: column; gap: 20px;">
          <div style="width: 800px; border: 2px dashed #ccc; padding: 20px;">
            <h4 style="margin-top: 0;">デスクトップサイズ (800px)</h4>
            <LicenseInfo />
          </div>
          
          <div style="width: 400px; border: 2px dashed #ccc; padding: 20px;">
            <h4 style="margin-top: 0;">タブレットサイズ (400px)</h4>
            <LicenseInfo />
          </div>
          
          <div style="width: 300px; border: 2px dashed #ccc; padding: 20px;">
            <h4 style="margin-top: 0;">モバイルサイズ (300px)</h4>
            <LicenseInfo />
          </div>
        </div>
      </div>
    `,
  }),
};

export const WithCustomContent = {
  render: () => ({
    components: { LicenseInfo },
    template: `
      <div style="width: 700px;">
        <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; border-radius: 12px 12px 0 0; color: white; text-align: center;">
          <h2 style="margin: 0; font-size: 24px;">ナビゲーションアプリ</h2>
          <p style="margin: 10px 0 0 0; opacity: 0.9;">自主返納支援制度案内システム</p>
        </div>
        
        <div style="background: white; padding: 30px; border-radius: 0 0 12px 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.1);">
          <div style="margin-bottom: 30px;">
            <h3 style="color: #333; margin-bottom: 15px;">アプリケーション情報</h3>
            <p style="color: #666; line-height: 1.6;">
              このアプリケーションは、運転免許証を自主返納された方を対象とした特典情報の提供と
              公共交通機関を利用した経路案内を行うシステムです。
            </p>
          </div>
          
          <LicenseInfo />
        </div>
      </div>
    `,
  }),
};

export const Interactive = {
  render: () => ({
    components: { LicenseInfo },
    data() {
      return {
        showInstructions: true,
      };
    },
    template: `
      <div style="width: 600px;">
        <div v-if="showInstructions" style="background: #e3f2fd; border: 1px solid #2196f3; border-radius: 8px; padding: 15px; margin-bottom: 20px;">
          <h4 style="margin: 0 0 10px 0; color: #1976d2;">💡 操作方法</h4>
          <ul style="margin: 0; padding-left: 20px; color: #1565c0;">
            <li>「その他ライセンス情報」ボタンをクリックしてモーダルを開いてください</li>
            <li>モーダル内のリンクをクリックして外部サイトの確認ができます</li>
            <li>モーダルはオーバーレイクリックまたは×ボタンで閉じられます</li>
          </ul>
          <button 
            @click="showInstructions = false"
            style="margin-top: 10px; padding: 5px 10px; background: #2196f3; color: white; border: none; border-radius: 4px; cursor: pointer;"
          >
            理解しました
          </button>
        </div>
        
        <LicenseInfo />
      </div>
    `,
  }),
};

export const AllVariants = {
  render: () => ({
    components: { LicenseInfo },
    template: `
      <div style="display: flex; flex-direction: column; gap: 30px; width: 800px;">
        <div>
          <h3 style="margin-bottom: 15px; color: #333;">基本表示</h3>
          <LicenseInfo />
        </div>
        
        <div>
          <h3 style="margin-bottom: 15px; color: #333;">アプリケーション下部での表示例</h3>
          <div style="background: #f8f9fa; border-radius: 8px; padding: 20px;">
            <div style="background: white; padding: 30px; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
              <h4>メインコンテンツ</h4>
              <p style="color: #666; line-height: 1.6;">
                ここにアプリケーションのメイン機能（経路検索、特典情報など）が表示されます。
              </p>
              <div style="display: flex; gap: 10px; margin: 20px 0;">
                <div style="flex: 1; height: 60px; background: #e9ecef; border-radius: 4px; display: flex; align-items: center; justify-content: center; color: #6c757d;">
                  機能エリア1
                </div>
                <div style="flex: 1; height: 60px; background: #e9ecef; border-radius: 4px; display: flex; align-items: center; justify-content: center; color: #6c757d;">
                  機能エリア2
                </div>
              </div>
            </div>
            
            <LicenseInfo />
          </div>
        </div>
        
        <div>
          <h3 style="margin-bottom: 15px; color: #333;">モバイルレイアウトでの表示</h3>
          <div style="width: 350px; margin: 0 auto;">
            <LicenseInfo />
          </div>
        </div>
      </div>
    `,
  }),
};
