import SidebarTab from './SidebarTab.vue';

export default {
  title: 'Design System/Molecules/SidebarTab',
  component: SidebarTab,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    activeTab: {
      control: { type: 'select' },
      options: ['route-guidance', 'users-benefit', 'search-benefit'],
    },
    onSetActiveTab: { action: 'tab changed' },
  },
};

export const RouteGuidanceActive = {
  args: {
    activeTab: 'route-guidance',
  },
};

export const UsersBenefitActive = {
  args: {
    activeTab: 'users-benefit',
  },
};

export const SearchBenefitActive = {
  args: {
    activeTab: 'search-benefit',
  },
};

export const Interactive = {
  render: () => ({
    components: { SidebarTab },
    data() {
      return {
        currentTab: 'route-guidance',
      };
    },
    methods: {
      handleTabChange(tabId) {
        this.currentTab = tabId;
      },
    },
    template: `
      <div style="width: 500px;">
        <SidebarTab 
          :activeTab="currentTab"
          @setActiveTab="handleTabChange"
        />
        
        <div style="margin-top: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px;">
          <h4 style="margin: 0 0 10px 0;">現在のタブ: {{ currentTab }}</h4>
          <div v-if="currentTab === 'route-guidance'">
            <h5>経路案内</h5>
            <p>目的地への最適なルートを案内します。</p>
          </div>
          <div v-else-if="currentTab === 'users-benefit'">
            <h5>利用できる特典</h5>
            <p>あなたが利用できる特典の一覧を表示します。</p>
          </div>
          <div v-else-if="currentTab === 'search-benefit'">
            <h5>特典を探す</h5>
            <p>条件を指定して特典を検索できます。</p>
          </div>
        </div>
      </div>
    `,
  }),
};

export const InSidebarLayout = {
  render: () => ({
    components: { SidebarTab },
    data() {
      return {
        activeTab: 'route-guidance',
        sidebarCollapsed: false,
      };
    },
    methods: {
      setActiveTab(tabId) {
        this.activeTab = tabId;
      },
      toggleSidebar() {
        this.sidebarCollapsed = !this.sidebarCollapsed;
      },
    },
    template: `
      <div style="display: flex; height: 500px; border: 2px solid #ddd; border-radius: 8px; overflow: hidden;">
        <!-- サイドバー -->
        <div :class="['sidebar', { collapsed: sidebarCollapsed }]" style="width: 400px; min-width: 400px; background: linear-gradient(180deg, #ffffff 0%, #f8f8f8 100%); border-right: 2px solid #ddd; display: flex; flex-direction: column; transition: all 0.3s ease;">
          <div style="background: linear-gradient(90deg, #333333 0%, #555555 100%); color: white;">
            <SidebarTab 
              :activeTab="activeTab"
              @setActiveTab="setActiveTab"
            />
          </div>
          
          <div style="flex: 1; padding: 20px; overflow-y: auto;">
            <div v-if="activeTab === 'route-guidance'">
              <h4 style="margin-top: 0; color: #333;">📍 経路案内</h4>
              <p style="color: #666;">出発地と目的地を入力して、最適なルートを検索できます。</p>
              <div style="margin-top: 15px;">
                <input type="text" placeholder="出発地を入力" style="width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ddd; border-radius: 4px;" />
                <input type="text" placeholder="目的地を入力" style="width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ddd; border-radius: 4px;" />
                <button style="width: 100%; padding: 10px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">ルート検索</button>
              </div>
            </div>
            
            <div v-else-if="activeTab === 'users-benefit'">
              <h4 style="margin-top: 0; color: #333;">🎫 利用できる特典</h4>
              <p style="color: #666;">あなたが利用できる特典の一覧です。</p>
              <div style="display: flex; flex-direction: column; gap: 10px; margin-top: 15px;">
                <div style="padding: 15px; background: white; border: 1px solid #ddd; border-radius: 6px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                  <h5 style="margin: 0 0 8px 0; color: #333;">バス運賃割引</h5>
                  <p style="margin: 0; font-size: 14px; color: #666;">市内バス運賃が50％割引</p>
                </div>
                <div style="padding: 15px; background: white; border: 1px solid #ddd; border-radius: 6px; box-shadow: 0 2px 4px rgba(0,0,0,0.05);">
                  <h5 style="margin: 0 0 8px 0; color: #333;">タクシー割引</h5>
                  <p style="margin: 0; font-size: 14px; color: #666;">登録タクシー会社で10％割引</p>
                </div>
              </div>
            </div>
            
            <div v-else-if="activeTab === 'search-benefit'">
              <h4 style="margin-top: 0; color: #333;">🔍 特典を探す</h4>
              <p style="color: #666;">条件を指定して特典を検索できます。</p>
              <div style="margin-top: 15px;">
                <select style="width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ddd; border-radius: 4px;">
                  <option>カテゴリを選択</option>
                  <option>交通</option>
                  <option>買い物</option>
                  <option>食事</option>
                  <option>娯楽</option>
                </select>
                <select style="width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ddd; border-radius: 4px;">
                  <option>地域を選択</option>
                  <option>熊本市中央区</option>
                  <option>熊本市東区</option>
                  <option>熊本市西区</option>
                </select>
                <button style="width: 100%; padding: 10px; background: #28a745; color: white; border: none; border-radius: 4px; cursor: pointer;">検索する</button>
              </div>
            </div>
          </div>
          
          <!-- ライセンス情報エリア（簡略版） -->
          <div style="padding: 15px; background: white; border-top: 1px solid #ddd; font-size: 12px; color: #666;">
            <p style="margin: 0;">© 2025 Benefit Map</p>
          </div>
        </div>
        
        <!-- メインコンテンツエリア -->
        <div style="flex: 1; background: #f0f0f0; position: relative;">
          <button 
            @click="toggleSidebar"
            style="position: absolute; top: 20px; left: 20px; z-index: 10; padding: 8px 12px; background: white; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; box-shadow: 0 2px 4px rgba(0,0,0,0.1);"
          >
            {{ sidebarCollapsed ? '►' : '◄' }}
          </button>
          
          <div style="height: 100%; display: flex; align-items: center; justify-content: center; color: #666; font-size: 18px;">
            <div style="text-align: center;">
              <div style="font-size: 48px; margin-bottom: 20px;">🗺️</div>
              <p>マップエリア</p>
              <p style="font-size: 14px;">ここに地図が表示されます</p>
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};

export const ResponsiveLayout = {
  render: () => ({
    components: { SidebarTab },
    data() {
      return {
        activeTab: 'route-guidance',
      };
    },
    methods: {
      setActiveTab(tabId) {
        this.activeTab = tabId;
      },
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 20px;">
        <div style="text-align: center;">
          <h3 style="margin: 0;">レスポンシブ表示の確認</h3>
          <p style="margin: 10px 0; color: #666;">異なる画面サイズでのタブ表示</p>
        </div>
        
        <!-- デスクトップサイズ -->
        <div style="border: 2px dashed #007bff; padding: 15px; border-radius: 8px;">
          <h4 style="margin: 0 0 15px 0; color: #007bff;">デスクトップ (800px)</h4>
          <div style="width: 100%; background: linear-gradient(90deg, #333333 0%, #555555 100%);">
            <SidebarTab 
              :activeTab="activeTab"
              @setActiveTab="setActiveTab"
            />
          </div>
        </div>
        
        <!-- タブレットサイズ -->
        <div style="border: 2px dashed #28a745; padding: 15px; border-radius: 8px;">
          <h4 style="margin: 0 0 15px 0; color: #28a745;">タブレット (500px)</h4>
          <div style="width: 500px; background: linear-gradient(90deg, #333333 0%, #555555 100%);">
            <SidebarTab 
              :activeTab="activeTab"
              @setActiveTab="setActiveTab"
            />
          </div>
        </div>
        
        <!-- モバイルサイズ -->
        <div style="border: 2px dashed #dc3545; padding: 15px; border-radius: 8px;">
          <h4 style="margin: 0 0 15px 0; color: #dc3545;">モバイル (300px)</h4>
          <div style="width: 300px; background: linear-gradient(90deg, #333333 0%, #555555 100%);">
            <SidebarTab 
              :activeTab="activeTab"
              @setActiveTab="setActiveTab"
            />
          </div>
        </div>
      </div>
    `,
  }),
};

export const AllVariants = {
  render: () => ({
    components: { SidebarTab },
    data() {
      return {
        tabs: {
          demo1: 'route-guidance',
          demo2: 'users-benefit',
          demo3: 'search-benefit',
        }
      };
    },
    methods: {
      setActiveTab(demo, tabId) {
        this.tabs[demo] = tabId;
      },
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 40px; width: 700px;">
        <div>
          <h3 style="margin-bottom: 20px; color: #333;">各タブがアクティブな状態</h3>
          
          <div style="display: flex; flex-direction: column; gap: 20px;">
            <div>
              <h4 style="margin: 0 0 10px 0; color: #666;">経路案内がアクティブ</h4>
              <div style="background: linear-gradient(90deg, #333333 0%, #555555 100%);">
                <SidebarTab 
                  :activeTab="'route-guidance'"
                  @setActiveTab="() => {}"
                />
              </div>
            </div>
            
            <div>
              <h4 style="margin: 0 0 10px 0; color: #666;">利用できる特典がアクティブ</h4>
              <div style="background: linear-gradient(90deg, #333333 0%, #555555 100%);">
                <SidebarTab 
                  :activeTab="'users-benefit'"
                  @setActiveTab="() => {}"
                />
              </div>
            </div>
            
            <div>
              <h4 style="margin: 0 0 10px 0; color: #666;">特典を探すがアクティブ</h4>
              <div style="background: linear-gradient(90deg, #333333 0%, #555555 100%);">
                <SidebarTab 
                  :activeTab="'search-benefit'"
                  @setActiveTab="() => {}"
                />
              </div>
            </div>
          </div>
        </div>
        
        <div>
          <h3 style="margin-bottom: 20px; color: #333;">インタラクティブなデモ</h3>
          <div style="border: 1px solid #ddd; border-radius: 8px; overflow: hidden;">
            <div style="background: linear-gradient(90deg, #333333 0%, #555555 100%);">
              <SidebarTab 
                :activeTab="tabs.demo1"
                @setActiveTab="(tab) => setActiveTab('demo1', tab)"
              />
            </div>
            
            <div style="padding: 25px; background: white;">
              <div v-if="tabs.demo1 === 'route-guidance'">
                <h4 style="margin-top: 0; color: #333;">🚌 経路案内機能</h4>
                <p style="color: #666; line-height: 1.6;">
                  公共交通機関を利用した最適なルートを検索・案内します。
                  バス、電車、徒歩を組み合わせた複数の選択肢を提供します。
                </p>
                <div style="display: flex; gap: 10px; margin-top: 15px;">
                  <span style="padding: 4px 8px; background: #e3f2fd; color: #1976d2; border-radius: 4px; font-size: 12px;">リアルタイム情報</span>
                  <span style="padding: 4px 8px; background: #e8f5e8; color: #2e7d32; border-radius: 4px; font-size: 12px;">バリアフリー対応</span>
                </div>
              </div>
              
              <div v-else-if="tabs.demo1 === 'users-benefit'">
                <h4 style="margin-top: 0; color: #333;">🎁 利用可能特典</h4>
                <p style="color: #666; line-height: 1.6;">
                  運転免許証返納者向けの特典情報を一覧表示します。
                  交通費割引、店舗での優待サービスなど多様な特典があります。
                </p>
                <div style="display: flex; gap: 10px; margin-top: 15px;">
                  <span style="padding: 4px 8px; background: #fff3e0; color: #ef6c00; border-radius: 4px; font-size: 12px;">割引特典</span>
                  <span style="padding: 4px 8px; background: #f3e5f5; color: #7b1fa2; border-radius: 4px; font-size: 12px;">無料サービス</span>
                </div>
              </div>
              
              <div v-else-if="tabs.demo1 === 'search-benefit'">
                <h4 style="margin-top: 0; color: #333;">🔍 特典検索機能</h4>
                <p style="color: #666; line-height: 1.6;">
                  カテゴリ、地域、キーワードなどの条件を指定して
                  あなたのニーズに合った特典を効率的に探すことができます。
                </p>
                <div style="display: flex; gap: 10px; margin-top: 15px;">
                  <span style="padding: 4px 8px; background: #e8f5e8; color: #2e7d32; border-radius: 4px; font-size: 12px;">高度な検索</span>
                  <span style="padding: 4px 8px; background: #e3f2fd; color: #1976d2; border-radius: 4px; font-size: 12px;">位置情報連携</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};
