import AppSearchBenefit from './AppSearchBenefit.vue';

export default {
  title: 'Design System/Organisms/AppSearchBenefit',
  component: AppSearchBenefit,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export const Default = {
  render: () => ({
    components: { AppSearchBenefit },
    template: `
      <div style="width: 500px; min-height: 600px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <div style="background: #f8f9fa; padding: 16px; border-bottom: 1px solid #dee2e6;">
          <h3 style="margin: 0; color: #495057;">特典検索</h3>
        </div>
        <div style="padding: 20px;">
          <AppSearchBenefit />
        </div>
      </div>
    `,
  }),
};

export const InSidebar = {
  render: () => ({
    components: { AppSearchBenefit },
    template: `
      <div style="display: flex; height: 700px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <!-- 検索サイドバー -->
        <div style="width: 400px; background: #f8f9fa; border-right: 1px solid #dee2e6; overflow-y: auto;">
          <div style="background: #e8f5e8; padding: 16px; border-bottom: 1px solid #dee2e6;">
            <h3 style="margin: 0; color: #388e3c;">特典検索パネル</h3>
          </div>
          <div style="padding: 20px;">
            <AppSearchBenefit />
          </div>
        </div>
        
        <!-- 結果表示エリア（模擬） -->
        <div style="flex: 1; position: relative; background: linear-gradient(45deg, #fff9c4, #f0f4c3);">
          <div style="padding: 24px;">
            <h3 style="color: #689f38; margin: 0 0 20px 0;">検索結果</h3>
            
            <!-- 模擬検索結果 -->
            <div style="display: flex; flex-direction: column; gap: 16px;">
              <div style="background: white; padding: 16px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                <h4 style="margin: 0 0 8px 0; color: #333;">🍽️ れすとらん田舎</h4>
                <p style="margin: 0 0 8px 0; color: #666; font-size: 14px;">居酒屋・レストラン</p>
                <p style="margin: 0 0 12px 0; color: #888; font-size: 13px;">📍 熊本市中央区上通町5-15</p>
                <div style="background: #4caf50; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; display: inline-block;">
                  10%割引
                </div>
              </div>
              
              <div style="background: white; padding: 16px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                <h4 style="margin: 0 0 8px 0; color: #333;">🏨 ホテルサンルート熊本</h4>
                <p style="margin: 0 0 8px 0; color: #666; font-size: 14px;">宿泊施設</p>
                <p style="margin: 0 0 12px 0; color: #888; font-size: 13px;">📍 熊本市中央区上林町3-40</p>
                <div style="background: #ff9800; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; display: inline-block;">
                  宿泊費20%OFF
                </div>
              </div>
              
              <div style="background: white; padding: 16px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                <h4 style="margin: 0 0 8px 0; color: #333;">🛒 イオン熊本店</h4>
                <p style="margin: 0 0 8px 0; color: #666; font-size: 14px;">ショッピング</p>
                <p style="margin: 0 0 12px 0; color: #888; font-size: 13px;">📍 熊本市中央区大江4-2-1</p>
                <div style="background: #e91e63; color: white; padding: 4px 8px; border-radius: 4px; font-size: 12px; display: inline-block;">
                  ポイント2倍
                </div>
              </div>
            </div>
          </div>
          
          <!-- 検索統計（模擬） -->
          <div style="position: absolute; top: 20px; right: 20px; background: white; padding: 12px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.15);">
            <div style="font-size: 14px; color: #666; text-align: center;">
              <div style="font-weight: bold; color: #4caf50; font-size: 18px;">12</div>
              <div>件の特典が見つかりました</div>
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};

export const SearchSteps = {
  render: () => ({
    components: { AppSearchBenefit },
    template: `
      <div style="display: flex; flex-direction: column; gap: 24px; max-width: 900px;">
        <h2 style="margin: 0;">特典検索の使い方</h2>
        
        <!-- ステップガイド -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; align-items: flex-start;">
          <div style="background: #e8f5e8; padding: 20px; border-radius: 8px;">
            <h4 style="margin: 0 0 16px 0; color: #388e3c;">検索フォーム</h4>
            <AppSearchBenefit />
          </div>
          
          <div style="padding: 20px; background: #f5f5f5; border-radius: 8px;">
            <h4 style="margin: 0 0 16px 0;">🔍 検索のコツ</h4>
            
            <div style="margin-bottom: 20px;">
              <h5 style="margin: 0 0 8px 0; color: #333;">1. 居住地域を選択</h5>
              <p style="margin: 0; font-size: 14px; color: #666;">
                お住まいの地域を選択することで、利用できる特典が絞り込まれます
              </p>
            </div>
            
            <div style="margin-bottom: 20px;">
              <h5 style="margin: 0 0 8px 0; color: #333;">2. 免許の状況を設定</h5>
              <p style="margin: 0; font-size: 14px; color: #666;">
                返納済み・未返納で利用可能な特典が異なります
              </p>
            </div>
            
            <div style="margin-bottom: 20px;">
              <h5 style="margin: 0 0 8px 0; color: #333;">3. 年齢を入力</h5>
              <p style="margin: 0; font-size: 14px; color: #666;">
                年齢に応じた特典やサービスを表示します
              </p>
            </div>
            
            <div style="background: #fff3cd; padding: 12px; border-radius: 4px; font-size: 13px; margin-top: 16px;">
              <strong>💡 ヒント:</strong> すべての条件を設定すると、より正確な検索結果が得られます
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};

export const FormValidation = {
  render: () => ({
    components: { AppSearchBenefit },
    template: `
      <div style="max-width: 800px;">
        <h3 style="margin: 0 0 20px 0;">フォームバリデーション表示例</h3>
        
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
          <!-- 正常状態 -->
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
            <div style="background: #d4edda; padding: 12px; border-bottom: 1px solid #dee2e6; text-align: center;">
              <strong style="color: #155724;">✅ 正常入力状態</strong>
            </div>
            <div style="padding: 20px;">
              <AppSearchBenefit />
              <div style="margin-top: 16px; padding: 12px; background: #d4edda; border-radius: 4px; font-size: 14px; color: #155724;">
                すべての必須項目が正しく入力されています
              </div>
            </div>
          </div>
          
          <!-- エラー状態の説明 -->
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
            <div style="background: #f8d7da; padding: 12px; border-bottom: 1px solid #dee2e6; text-align: center;">
              <strong style="color: #721c24;">⚠️ バリデーションエラー例</strong>
            </div>
            <div style="padding: 20px;">
              <h5 style="margin: 0 0 12px 0; color: #333;">よくあるエラー</h5>
              
              <div style="margin-bottom: 16px; padding: 12px; background: #f8d7da; border-radius: 4px;">
                <div style="font-weight: bold; color: #721c24; margin-bottom: 4px;">居住地域が未選択</div>
                <div style="font-size: 14px; color: #721c24;">「選択してください」から地域を選択してください</div>
              </div>
              
              <div style="margin-bottom: 16px; padding: 12px; background: #f8d7da; border-radius: 4px;">
                <div style="font-weight: bold; color: #721c24; margin-bottom: 4px;">運転免許状況が未選択</div>
                <div style="font-size: 14px; color: #721c24;">免許の所持状況を選択してください</div>
              </div>
              
              <div style="margin-bottom: 16px; padding: 12px; background: #f8d7da; border-radius: 4px;">
                <div style="font-weight: bold; color: #721c24; margin-bottom: 4px;">年齢が未入力</div>
                <div style="font-size: 14px; color: #721c24;">年齢を数値で入力してください</div>
              </div>
              
              <div style="background: #fff3cd; padding: 12px; border-radius: 4px; font-size: 13px;">
                <strong>ℹ️ 注意:</strong> すべての項目が必須入力です
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
    components: { AppSearchBenefit },
    template: `
      <div style="display: flex; flex-direction: column; gap: 24px;">
        <h3 style="margin: 0;">レスポンシブ表示確認</h3>
        
        <!-- モバイルサイズ -->
        <div style="width: 360px; border: 2px dashed #28a745; padding: 12px;">
          <div style="font-size: 12px; color: #28a745; font-weight: bold; margin-bottom: 8px;">
            Mobile (360px)
          </div>
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
            <div style="background: #e8f5e8; padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6;">
              <strong>特典検索</strong>
            </div>
            <div style="padding: 16px;">
              <AppSearchBenefit />
            </div>
          </div>
        </div>
        
        <!-- タブレットサイズ -->
        <div style="width: 768px; border: 2px dashed #17a2b8; padding: 12px;">
          <div style="font-size: 12px; color: #17a2b8; font-weight: bold; margin-bottom: 8px;">
            Tablet (768px)
          </div>
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
            <div style="background: #e8f5e8; padding: 16px; text-align: center; border-bottom: 1px solid #dee2e6;">
              <strong>特典検索</strong>
            </div>
            <div style="padding: 20px;">
              <AppSearchBenefit />
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};

export const FeatureHighlight = {
  render: () => ({
    components: { AppSearchBenefit },
    template: `
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; max-width: 1000px;">
        <!-- 機能説明 -->
        <div style="padding: 24px;">
          <h3 style="margin: 0 0 24px 0; color: #388e3c;">🔍 特典検索機能</h3>
          
          <div style="margin-bottom: 24px;">
            <h4 style="margin: 0 0 12px 0; color: #333;">主な特徴</h4>
            <ul style="margin: 0; color: #6c757d; line-height: 1.6;">
              <li>地域に応じた特典絞り込み</li>
              <li>年齢・免許状況による条件分岐</li>
              <li>リアルタイム検索</li>
              <li>詳細な特典情報表示</li>
            </ul>
          </div>
          
          <div style="margin-bottom: 24px;">
            <h4 style="margin: 0 0 12px 0; color: #333;">検索条件</h4>
            <div style="display: flex; flex-wrap: wrap; gap: 8px;">
              <span style="background: #e8f5e8; color: #388e3c; padding: 4px 8px; border-radius: 4px; font-size: 12px;">📍 居住地域</span>
              <span style="background: #e3f2fd; color: #1976d2; padding: 4px 8px; border-radius: 4px; font-size: 12px;">🚗 免許状況</span>
              <span style="background: #fff3e0; color: #f57c00; padding: 4px 8px; border-radius: 4px; font-size: 12px;">👤 年齢</span>
              <span style="background: #f3e5f5; color: #7b1fa2; padding: 4px 8px; border-radius: 4px; font-size: 12px;">🏷️ カテゴリー</span>
            </div>
          </div>
          
          <div style="background: #f0fff4; padding: 16px; border-radius: 8px; border-left: 4px solid #388e3c;">
            <h5 style="margin: 0 0 8px 0; color: #388e3c;">💡 検索のメリット</h5>
            <p style="margin: 0; font-size: 14px; color: #555;">
              詳細な条件設定により、あなたに最適な特典だけを効率的に見つけることができます。
              時間を節約し、お得な情報を見逃しません。
            </p>
          </div>
        </div>
        
        <!-- コンポーネント -->
        <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
          <div style="background: linear-gradient(135deg, #388e3c, #66bb6a); color: white; padding: 16px; text-align: center;">
            <h4 style="margin: 0;">特典検索フォーム</h4>
          </div>
          <div style="padding: 20px;">
            <AppSearchBenefit />
          </div>
        </div>
      </div>
    `,
  }),
};