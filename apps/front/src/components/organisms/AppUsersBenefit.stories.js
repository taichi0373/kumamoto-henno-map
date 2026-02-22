import AppUsersBenefit from './AppUsersBenefit.vue';

export default {
  title: 'Design System/Organisms/AppUsersBenefit',
  component: AppUsersBenefit,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    userBenefits: {
      control: 'object',
      description: 'ユーザーが利用できる特典リスト',
    },
    loading: {
      control: 'boolean',
      description: 'ローディング状態',
    },
  },
};

// サンプルデータ
const sampleUserBenefits = [
  {
    id: 1,
    name: 'レストラン田舎',
    category: '飲食',
    description: '会計から10%割引いたします。',
    conditions: '免許返納証明書の提示',
    validPeriod: '2024年12月31日まで',
    address: '熊本市中央区上通町5-15',
    phone: '096-123-4567',
    website: 'https://restaurant-inaka.example.com',
    latitude: 32.8031,
    longitude: 130.7097,
    distance: 0.8,
    rating: 4.2,
    imageUrl: '/images/restaurants/inaka.jpg',
  },
  {
    id: 2,
    name: 'ハートフル美容室',
    category: '美容・健康',
    description: 'カット料金20%OFF、パーマ・カラー15%OFF',
    conditions: '65歳以上、免許返納証明書提示',
    validPeriod: '期限なし',
    address: '熊本市中央区下通1-12-7',
    phone: '096-987-6543',
    website: null,
    latitude: 32.7969,
    longitude: 130.7095,
    distance: 1.2,
    rating: 4.5,
    imageUrl: '/images/beauty/heartful.jpg',
  },
  {
    id: 3,
    name: 'フレッシュマート熊本',
    category: 'ショッピング',
    description: '毎週火曜日、全商品5%割引',
    conditions: 'ゴールドカード会員（60歳以上）',
    validPeriod: '2025年3月31日まで',
    address: '熊本市中央区新町2-7-1',
    phone: '096-555-0123',
    website: 'https://freshmart-kumamoto.example.com',
    latitude: 32.8045,
    longitude: 130.7041,
    distance: 0.5,
    rating: 4.0,
    imageUrl: '/images/shops/freshmart.jpg',
  },
  {
    id: 4,
    name: 'シルバー温泉の湯',
    category: 'レジャー・娯楽',
    description: '入浴料半額（通常800円→400円）',
    conditions: '平日限定、65歳以上',
    validPeriod: '期限なし',
    address: '熊本市北区植木町米塚1-2-3',
    phone: '096-777-8899',
    website: 'https://silver-onsen.example.com',
    latitude: 32.8892,
    longitude: 130.6949,
    distance: 12.5,
    rating: 4.7,
    imageUrl: '/images/leisure/onsen.jpg',
  },
  {
    id: 5,
    name: '熊本バス',
    category: '交通',
    description: '運賃50%割引（シルバーパス）',
    conditions: '70歳以上、専用パス購入必要',
    validPeriod: '年度更新',
    address: '熊本市中央区桜町3-10',
    phone: '096-100-0001',
    website: 'https://kumamoto-bus.example.com',
    latitude: 32.7919,
    longitude: 130.7081,
    distance: 2.1,
    rating: 4.1,
    imageUrl: '/images/transport/bus.jpg',
  },
];

const shortUserBenefits = sampleUserBenefits.slice(0, 2);

export const Default = {
  args: {
    userBenefits: sampleUserBenefits,
    loading: false,
  },
  render: (args) => ({
    components: { AppUsersBenefit },
    setup() {
      return { args };
    },
    template: `
      <div style="width: 600px; min-height: 400px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <div style="background: #f3e5f5; padding: 16px; border-bottom: 1px solid #dee2e6;">
          <h3 style="margin: 0; color: #7b1fa2;">利用可能な特典</h3>
        </div>
        <div style="padding: 20px;">
          <AppUsersBenefit v-bind="args" />
        </div>
      </div>
    `,
  }),
};

export const Loading = {
  args: {
    userBenefits: [],
    loading: true,
  },
  render: (args) => ({
    components: { AppUsersBenefit },
    setup() {
      return { args };
    },
    template: `
      <div style="width: 600px; min-height: 400px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <div style="background: #f3e5f5; padding: 16px; border-bottom: 1px solid #dee2e6;">
          <h3 style="margin: 0; color: #7b1fa2;">利用可能な特典（読み込み中）</h3>
        </div>
        <div style="padding: 20px;">
          <AppUsersBenefit v-bind="args" />
        </div>
      </div>
    `,
  }),
};

export const Empty = {
  args: {
    userBenefits: [],
    loading: false,
  },
  render: (args) => ({
    components: { AppUsersBenefit },
    setup() {
      return { args };
    },
    template: `
      <div style="width: 600px; min-height: 400px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <div style="background: #f3e5f5; padding: 16px; border-bottom: 1px solid #dee2e6;">
          <h3 style="margin: 0; color: #7b1fa2;">利用可能な特典（空の状態）</h3>
        </div>
        <div style="padding: 20px;">
          <AppUsersBenefit v-bind="args" />
        </div>
      </div>
    `,
  }),
};

export const ShortList = {
  args: {
    userBenefits: shortUserBenefits,
    loading: false,
  },
  render: (args) => ({
    components: { AppUsersBenefit },
    setup() {
      return { args };
    },
    template: `
      <div style="width: 600px; min-height: 400px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <div style="background: #f3e5f5; padding: 16px; border-bottom: 1px solid #dee2e6;">
          <h3 style="margin: 0; color: #7b1fa2;">利用可能な特典（少ない件数）</h3>
        </div>
        <div style="padding: 20px;">
          <AppUsersBenefit v-bind="args" />
        </div>
      </div>
    `,
  }),
};

export const InSidebar = {
  args: {
    userBenefits: sampleUserBenefits,
    loading: false,
  },
  render: (args) => ({
    components: { AppUsersBenefit },
    setup() {
      return { args };
    },
    template: `
      <div style="display: flex; height: 700px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <!-- 特典サイドバー -->
        <div style="width: 450px; background: #f8f9fa; border-right: 1px solid #dee2e6; overflow-y: auto;">
          <div style="background: #f3e5f5; padding: 16px; border-bottom: 1px solid #dee2e6; position: sticky; top: 0; z-index: 1;">
            <h3 style="margin: 0; color: #7b1fa2;">あなたの特典</h3>
          </div>
          <div style="padding: 20px;">
            <AppUsersBenefit v-bind="args" />
          </div>
        </div>
        
        <!-- 地図エリア（模擬） -->
        <div style="flex: 1; position: relative; background: linear-gradient(45deg, #fce4ec, #f8bbd9);">
          <div style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); text-align: center; color: #ad1457;">
            <div style="font-size: 4rem; margin-bottom: 1rem;">🗺️</div>
            <h3>特典店舗マップ</h3>
            <p>ここに実際の地図と店舗マーカーが表示されます</p>
          </div>
          
          <!-- 選択された特典の詳細（模擬） -->
          <div style="position: absolute; bottom: 20px; left: 20px; background: white; padding: 16px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.15); max-width: 300px;">
            <h4 style="margin: 0 0 12px 0; color: #7b1fa2;">選択中の特典</h4>
            <div style="font-size: 14px; color: #666;">
              <div style="margin-bottom: 8px;"><strong>レストラン田舎</strong></div>
              <div style="margin-bottom: 8px;">🏷️ 10%割引</div>
              <div style="margin-bottom: 8px;">📍 0.8km</div>
              <div style="margin-bottom: 8px;">⭐ 4.2</div>
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};

export const CategoryFiltering = {
  args: {
    userBenefits: sampleUserBenefits,
    loading: false,
  },
  render: (args) => ({
    components: { AppUsersBenefit },
    setup() {
      return { args };
    },
    template: `
      <div style="max-width: 800px;">
        <h3 style="margin: 0 0 20px 0;">カテゴリーフィルタリング機能</h3>
        
        <div style="margin-bottom: 20px; padding: 16px; background: #f8f9fa; border-radius: 8px;">
          <h4 style="margin: 0 0 12px 0;">利用可能なカテゴリー</h4>
          <div style="display: flex; flex-wrap: wrap; gap: 8px;">
            <span style="background: #e3f2fd; color: #1976d2; padding: 4px 8px; border-radius: 4px; font-size: 12px;">🍽️ 飲食</span>
            <span style="background: #f3e5f5; color: #7b1fa2; padding: 4px 8px; border-radius: 4px; font-size: 12px;">💄 美容・健康</span>
            <span style="background: #e8f5e8; color: #388e3c; padding: 4px 8px; border-radius: 4px; font-size: 12px;">🛒 ショッピング</span>
            <span style="background: #fff3e0; color: #f57c00; padding: 4px 8px; border-radius: 4px; font-size: 12px;">🎯 レジャー・娯楽</span>
            <span style="background: #fce4ec; color: #c2185b; padding: 4px 8px; border-radius: 4px; font-size: 12px;">🚌 交通</span>
          </div>
        </div>
        
        <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
          <div style="background: #f3e5f5; padding: 16px; border-bottom: 1px solid #dee2e6;">
            <h4 style="margin: 0;">特典一覧（フィルタ・ソート機能付き）</h4>
          </div>
          <div style="padding: 20px;">
            <AppUsersBenefit v-bind="args" />
          </div>
        </div>
      </div>
    `,
  }),
};

export const RealTimeUpdates = {
  render: () => ({
    components: { AppUsersBenefit },
    data() {
      return {
        benefits: [...sampleUserBenefits],
        loading: false,
        updateInterval: null,
      };
    },
    mounted() {
      this.startUpdates();
    },
    beforeUnmount() {
      this.stopUpdates();
    },
    methods: {
      startUpdates() {
        this.updateInterval = setInterval(() => {
          // ランダムに特典を追加・削除・更新
          const action = Math.floor(Math.random() * 3);
          
          if (action === 0 && this.benefits.length < 8) {
            // 新しい特典を追加
            const newBenefit = {
              id: Date.now(),
              name: `新規店舗 ${Math.floor(Math.random() * 100)}`,
              category: ['飲食', '美容・健康', 'ショッピング'][Math.floor(Math.random() * 3)],
              description: '期間限定の特別割引！',
              conditions: '新規加入特典',
              validPeriod: '2025年12月31日まで',
              address: '熊本市内',
              phone: '096-000-0000',
              distance: Math.round(Math.random() * 10 * 10) / 10,
              rating: Math.round((3.5 + Math.random() * 1.5) * 10) / 10,
            };
            this.benefits.push(newBenefit);
          } else if (action === 1 && this.benefits.length > 2) {
            // ランダムに1つ削除
            const index = Math.floor(Math.random() * this.benefits.length);
            this.benefits.splice(index, 1);
          } else if (this.benefits.length > 0) {
            // ランダムに1つ更新
            const index = Math.floor(Math.random() * this.benefits.length);
            this.benefits[index].rating = Math.round((3.5 + Math.random() * 1.5) * 10) / 10;
            this.benefits[index].description = `更新: ${new Date().toLocaleTimeString()}`;
          }
        }, 3000);
      },
      stopUpdates() {
        if (this.updateInterval) {
          clearInterval(this.updateInterval);
        }
      },
      reset() {
        this.benefits = [...sampleUserBenefits];
      },
    },
    template: `
      <div style="max-width: 800px;">
        <div style="margin-bottom: 20px; padding: 16px; background: #e8f4fd; border-radius: 8px; border-left: 4px solid #2196f3;">
          <h4 style="margin: 0 0 12px 0; color: #1976d2;">🔄 リアルタイム更新デモ</h4>
          <p style="margin: 0 0 12px 0; font-size: 14px; color: #1565c0;">
            3秒ごとに特典リストが自動的に更新されます（追加・削除・情報更新）
          </p>
          <button 
            @click="reset" 
            style="padding: 8px 16px; background: #2196f3; color: white; border: none; border-radius: 4px; cursor: pointer;"
          >
            リセット
          </button>
        </div>
        
        <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
          <div style="background: #f3e5f5; padding: 16px; border-bottom: 1px solid #dee2e6;">
            <h4 style="margin: 0;">特典一覧（{{ benefits.length }}件）</h4>
          </div>
          <div style="padding: 20px;">
            <AppUsersBenefit :userBenefits="benefits" :loading="loading" />
          </div>
        </div>
      </div>
    `,
  }),
};

export const ResponsiveView = {
  args: {
    userBenefits: sampleUserBenefits,
    loading: false,
  },
  render: (args) => ({
    components: { AppUsersBenefit },
    setup() {
      return { args };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 24px;">
        <h3 style="margin: 0;">レスポンシブ表示確認</h3>
        
        <!-- モバイルサイズ -->
        <div style="width: 360px; border: 2px dashed #9c27b0; padding: 12px;">
          <div style="font-size: 12px; color: #9c27b0; font-weight: bold; margin-bottom: 8px;">
            Mobile (360px)
          </div>
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden; height: 400px;">
            <div style="background: #f3e5f5; padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6;">
              <strong>特典一覧</strong>
            </div>
            <div style="padding: 16px; height: calc(100% - 60px); overflow-y: auto;">
              <AppUsersBenefit v-bind="args" />
            </div>
          </div>
        </div>
        
        <!-- タブレットサイズ -->
        <div style="width: 768px; border: 2px dashed #673ab7; padding: 12px;">
          <div style="font-size: 12px; color: #673ab7; font-weight: bold; margin-bottom: 8px;">
            Tablet (768px)
          </div>
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden; height: 500px;">
            <div style="background: #f3e5f5; padding: 16px; text-align: center; border-bottom: 1px solid #dee2e6;">
              <strong>特典一覧</strong>
            </div>
            <div style="padding: 20px; height: calc(100% - 80px); overflow-y: auto;">
              <AppUsersBenefit v-bind="args" />
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};