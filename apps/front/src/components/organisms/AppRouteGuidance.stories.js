import AppRouteGuidance from './AppRouteGuidance.vue';

export default {
  title: 'Design System/Organisms/AppRouteGuidance',
  component: AppRouteGuidance,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export const Default = {
  render: () => ({
    components: { AppRouteGuidance },
    template: `
      <div style="width: 500px; min-height: 600px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <div style="background: #f8f9fa; padding: 16px; border-bottom: 1px solid #dee2e6;">
          <h3 style="margin: 0; color: #495057;">経路案内</h3>
        </div>
        <div style="padding: 20px;">
          <AppRouteGuidance />
        </div>
      </div>
    `,
  }),
};

export const InSidebar = {
  render: () => ({
    components: { AppRouteGuidance },
    template: `
      <div style="display: flex; height: 700px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <!-- サイドバー -->
        <div style="width: 400px; background: #f8f9fa; border-right: 1px solid #dee2e6; overflow-y: auto;">
          <div style="background: #e3f2fd; padding: 16px; border-bottom: 1px solid #dee2e6;">
            <h3 style="margin: 0; color: #1976d2;">経路案内パネル</h3>
          </div>
          <div style="padding: 20px;">
            <AppRouteGuidance />
          </div>
        </div>
        
        <!-- 地図エリア（模擬） -->
        <div style="flex: 1; position: relative; background: linear-gradient(45deg, #e8f5e8, #c8e6c9);">
          <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); text-align: center; color: #2e7d32;">
            <div style="font-size: 4rem; margin-bottom: 1rem;">🗺️</div>
            <h3>地図表示エリア</h3>
            <p>ここに実際の地図とルート表示が行われます</p>
          </div>
          
          <!-- ルート情報表示（模擬） -->
          <div style="position: absolute; top: 20px; right: 20px; background: white; padding: 16px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.15); max-width: 300px;">
            <h4 style="margin: 0 0 12px 0; color: #1976d2;">検索結果</h4>
            <div style="font-size: 14px; color: #666;">
              <div style="margin-bottom: 8px;">🚌 バス利用</div>
              <div style="margin-bottom: 8px;">⏱️ 約25分</div>
              <div style="margin-bottom: 8px;">💰 料金: 320円</div>
              <div style="margin-bottom: 8px;">🚶 徒歩: 5分</div>
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};

export const WithSteps = {
  render: () => ({
    components: { AppRouteGuidance },
    template: `
      <div style="display: flex; flex-direction: column; gap: 20px; max-width: 800px;">
        <h2 style="margin: 0;">経路案内の使い方</h2>
        
        <!-- ステップ1 -->
        <div style="display: flex; gap: 20px; align-items: flex-start;">
          <div style="background: #e3f2fd; padding: 20px; border-radius: 8px; flex: 1; min-width: 400px;">
            <h4 style="margin: 0 0 16px 0; color: #1976d2;">ステップ1: 条件設定</h4>
            <AppRouteGuidance />
          </div>
          
          <div style="flex: 1; padding: 20px; background: #f5f5f5; border-radius: 8px;">
            <h4 style="margin: 0 0 16px 0;">💡 使い方ガイド</h4>
            <ol style="margin: 0; padding-left: 20px; font-size: 14px; line-height: 1.6;">
              <li>交通手段を選択</li>
              <li>出発地を入力または地図で選択</li>
              <li>目的地を入力または地図で選択</li>
              <li>検索ボタンをクリック</li>
            </ol>
            
            <div style="margin-top: 20px; padding: 12px; background: #fff3cd; border-radius: 4px; font-size: 13px;">
              <strong>ヒント:</strong> マップで選択ボタンを使うと、地図上で直接場所を選ぶことができます
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};

export const FormStates = {
  render: () => ({
    components: { AppRouteGuidance },
    data() {
      return {
        currentState: 'empty',
        states: {
          empty: '初期状態（空のフォーム）',
          filled: '入力済み状態',
          searching: '検索中状態',
          results: '結果表示状態',
        },
      };
    },
    template: `
      <div style="max-width: 900px;">
        <h3 style="margin: 0 0 20px 0;">フォーム状態の表示例</h3>
        
        <div style="margin-bottom: 20px;">
          <label style="font-weight: bold; margin-right: 12px;">表示状態:</label>
          <select v-model="currentState" style="padding: 8px 12px; border: 1px solid #ccc; border-radius: 4px;">
            <option v-for="(label, key) in states" :key="key" :value="key">
              {{ label }}
            </option>
          </select>
        </div>
        
        <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
          <div style="background: #f8f9fa; padding: 16px; border-bottom: 1px solid #dee2e6;">
            <h4 style="margin: 0;">{{ states[currentState] }}</h4>
          </div>
          
          <div style="padding: 20px;">
            <AppRouteGuidance />
            
            <!-- 状態説明 -->
            <div style="margin-top: 24px; padding: 16px; background: #f8f9fa; border-radius: 8px;">
              <h5 style="margin: 0 0 12px 0;">この状態について</h5>
              <div v-if="currentState === 'empty'" style="font-size: 14px; color: #6c757d;">
                初期表示状態です。すべてのフィールドが空で、ユーザーの入力を待っています。
              </div>
              <div v-else-if="currentState === 'filled'" style="font-size: 14px; color: #6c757d;">
                ユーザーが必要な情報を入力し、検索準備が整った状態です。
              </div>
              <div v-else-if="currentState === 'searching'" style="font-size: 14px; color: #6c757d;">
                検索ボタンがクリックされ、経路を検索中の状態です。ローディング表示などが表示されます。
              </div>
              <div v-else-if="currentState === 'results'" style="font-size: 14px; color: #6c757d;">
                検索が完了し、経路の候補が表示された状態です。
              </div>
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};

export const ResponsiveView = {
  render: () => ({
    components: { AppRouteGuidance },
    template: `
      <div style="display: flex; flex-direction: column; gap: 24px;">
        <h3 style="margin: 0;">レスポンシブ表示確認</h3>
        
        <!-- モバイルサイズ -->
        <div style="width: 360px; border: 2px dashed #007bff; padding: 12px;">
          <div style="font-size: 12px; color: #007bff; font-weight: bold; margin-bottom: 8px;">
            Mobile (360px)
          </div>
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
            <div style="background: #f8f9fa; padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6;">
              <strong>経路案内</strong>
            </div>
            <div style="padding: 16px;">
              <AppRouteGuidance />
            </div>
          </div>
        </div>
        
        <!-- タブレットサイズ -->
        <div style="width: 768px; border: 2px dashed #28a745; padding: 12px;">
          <div style="font-size: 12px; color: #28a745; font-weight: bold; margin-bottom: 8px;">
            Tablet (768px)
          </div>
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
            <div style="background: #f8f9fa; padding: 16px; text-align: center; border-bottom: 1px solid #dee2e6;">
              <strong>経路案内</strong>
            </div>
            <div style="padding: 20px;">
              <AppRouteGuidance />
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};

export const FeatureHighlight = {
  render: () => ({
    components: { AppRouteGuidance },
    template: `
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; max-width: 1000px;">
        <!-- 機能説明 -->
        <div style="padding: 24px;">
          <h3 style="margin: 0 0 24px 0; color: #1976d2;">🚌 経路案内機能</h3>
          
          <div style="margin-bottom: 24px;">
            <h4 style="margin: 0 0 12px 0; color: #333;">主な特徴</h4>
            <ul style="margin: 0; color: #6c757d; line-height: 1.6;">
              <li>複数の交通手段に対応</li>
              <li>地図連携での場所選択</li>
              <li>リアルタイム検索</li>
              <li>詳細な経路情報</li>
            </ul>
          </div>
          
          <div style="margin-bottom: 24px;">
            <h4 style="margin: 0 0 12px 0; color: #333;">対応交通手段</h4>
            <div style="display: flex; flex-wrap: wrap; gap: 8px;">
              <span style="background: #e3f2fd; color: #1976d2; padding: 4px 8px; border-radius: 4px; font-size: 12px;">🚌 バス</span>
              <span style="background: #f3e5f5; color: #7b1fa2; padding: 4px 8px; border-radius: 4px; font-size: 12px;">🚊 電車</span>
              <span style="background: #e8f5e8; color: #388e3c; padding: 4px 8px; border-radius: 4px; font-size: 12px;">🚶 徒歩</span>
              <span style="background: #fff3e0; color: #f57c00; padding: 4px 8px; border-radius: 4px; font-size: 12px;">🚴 自転車</span>
            </div>
          </div>
          
          <div style="background: #f0f8ff; padding: 16px; border-radius: 8px; border-left: 4px solid #1976d2;">
            <h5 style="margin: 0 0 8px 0; color: #1976d2;">💡 使い方のコツ</h5>
            <p style="margin: 0; font-size: 14px; color: #555;">
              地図での場所選択を使うと、より正確な位置指定が可能です。
              また、複数の経路候補から最適なものを選択できます。
            </p>
          </div>
        </div>
        
        <!-- コンポーネント -->
        <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
          <div style="background: linear-gradient(135deg, #1976d2, #42a5f5); color: white; padding: 16px; text-align: center;">
            <h4 style="margin: 0;">経路検索フォーム</h4>
          </div>
          <div style="padding: 20px;">
            <AppRouteGuidance />
          </div>
        </div>
      </div>
    `,
  }),
};