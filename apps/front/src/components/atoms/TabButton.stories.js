import TabButton from './TabButton.vue';

export default {
  title: 'Design System/Atoms/TabButton',
  component: TabButton,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    type: {
      control: { type: 'select' },
      options: ['button', 'submit', 'reset'],
    },
    label: {
      control: 'text',
    },
    active: {
      control: 'boolean',
    },
    disabled: {
      control: 'boolean',
    },
    variant: {
      control: { type: 'select' },
      options: ['default'],
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large'],
    },
    onClick: { action: 'clicked' },
  },
};

export const Default = {
  args: {
    label: 'タブボタン',
  },
};

export const Active = {
  args: {
    label: 'アクティブタブ',
    active: true,
  },
};

export const Disabled = {
  args: {
    label: '無効タブ',
    disabled: true,
  },
};

export const Sizes = {
  render: () => ({
    components: { TabButton },
    template: `
      <div style="display: flex; gap: 16px; align-items: center;">
        <TabButton size="small" label="小サイズ" />
        <TabButton size="medium" label="中サイズ" />
        <TabButton size="large" label="大サイズ" />
      </div>
    `,
  }),
};

export const States = {
  render: () => ({
    components: { TabButton },
    template: `
      <div style="display: flex; gap: 16px; align-items: center;">
        <TabButton label="通常" />
        <TabButton label="アクティブ" :active="true" />
        <TabButton label="無効" :disabled="true" />
      </div>
    `,
  }),
};

export const TabGroup = {
  render: () => ({
    components: { TabButton },
    data() {
      return {
        activeTab: 'home',
      };
    },
    methods: {
      setActiveTab(tab) {
        this.activeTab = tab;
      },
    },
    template: `
      <div style="display: flex; background-color: #f8f9fa; border-radius: 8px; padding: 4px;">
        <TabButton 
          label="ホーム" 
          :active="activeTab === 'home'"
          @click="setActiveTab('home')"
        />
        <TabButton 
          label="プロフィール" 
          :active="activeTab === 'profile'"
          @click="setActiveTab('profile')"
        />
        <TabButton 
          label="設定" 
          :active="activeTab === 'settings'"
          @click="setActiveTab('settings')"
        />
        <TabButton 
          label="ヘルプ" 
          :active="activeTab === 'help'"
          @click="setActiveTab('help')"
        />
      </div>
    `,
  }),
};

export const NavigationTabs = {
  render: () => ({
    components: { TabButton },
    data() {
      return {
        activeTab: 'dashboard',
        tabs: [
          { id: 'dashboard', label: 'ダッシュボード' },
          { id: 'users', label: 'ユーザー管理' },
          { id: 'analytics', label: '分析' },
          { id: 'reports', label: 'レポート' },
          { id: 'settings', label: '設定' },
        ],
      };
    },
    methods: {
      setActiveTab(tabId) {
        this.activeTab = tabId;
      },
    },
    template: `
      <div style="width: 600px;">
        <div style="display: flex; background-color: #f8f9fa; border-radius: 8px; padding: 4px; margin-bottom: 20px;">
          <TabButton 
            v-for="tab in tabs"
            :key="tab.id"
            :label="tab.label"
            :active="activeTab === tab.id"
            @click="setActiveTab(tab.id)"
          />
        </div>
        
        <div style="padding: 20px; border: 1px solid #dee2e6; border-radius: 8px; background-color: white;">
          <h3 style="margin-top: 0;">{{ tabs.find(tab => tab.id === activeTab)?.label }}の内容</h3>
          <p>現在「{{ tabs.find(tab => tab.id === activeTab)?.label }}」タブが選択されています。</p>
        </div>
      </div>
    `,
  }),
};

export const ResponsiveTabsExample = {
  render: () => ({
    components: { TabButton },
    data() {
      return {
        activeTab: 'route',
        tabs: [
          { id: 'route', label: '経路案内' },
          { id: 'benefits', label: '利用できる特典' },
          { id: 'search', label: '特典を探す' },
        ],
      };
    },
    methods: {
      setActiveTab(tabId) {
        this.activeTab = tabId;
      },
    },
    template: `
      <div style="width: 100%; max-width: 500px;">
        <div style="display: flex; background: linear-gradient(90deg, #333333 0%, #555555 100%); border-radius: 8px 8px 0 0;">
          <TabButton 
            v-for="tab in tabs"
            :key="tab.id"
            :label="tab.label"
            :active="activeTab === tab.id"
            size="medium"
            @click="setActiveTab(tab.id)"
            style="flex: 1;"
          />
        </div>
        
        <div style="padding: 25px; background: white; border-radius: 0 0 8px 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
          <div v-if="activeTab === 'route'">
            <h4 style="margin-top: 0;">経路案内</h4>
            <p>目的地への最適なルートを案内します。</p>
          </div>
          <div v-else-if="activeTab === 'benefits'">
            <h4 style="margin-top: 0;">利用できる特典</h4>
            <p>あなたが利用できる特典の一覧を表示します。</p>
          </div>
          <div v-else-if="activeTab === 'search'">
            <h4 style="margin-top: 0;">特典を探す</h4>
            <p>条件を指定して特典を検索できます。</p>
          </div>
        </div>
      </div>
    `,
  }),
};

export const AllVariants = {
  render: () => ({
    components: { TabButton },
    data() {
      return {
        activeTabGroup1: 'tab1',
        activeTabGroup2: 'option1',
        activeTabGroup3: 'small1',
      };
    },
    methods: {
      setActiveTab(group, tab) {
        this[group] = tab;
      },
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 40px; width: 700px;">
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">基本的な使用例</h4>
          <div style="display: flex; background-color: #f8f9fa; border-radius: 8px; padding: 4px;">
            <TabButton 
              label="タブ1" 
              :active="activeTabGroup1 === 'tab1'"
              @click="setActiveTab('activeTabGroup1', 'tab1')"
            />
            <TabButton 
              label="タブ2" 
              :active="activeTabGroup1 === 'tab2'"
              @click="setActiveTab('activeTabGroup1', 'tab2')"
            />
            <TabButton 
              label="無効タブ" 
              :disabled="true"
            />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">サイズバリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 16px;">
            <div style="display: flex; background-color: #f8f9fa; border-radius: 6px; padding: 2px;">
              <TabButton 
                size="small" 
                label="小サイズ1" 
                :active="activeTabGroup3 === 'small1'"
                @click="setActiveTab('activeTabGroup3', 'small1')"
              />
              <TabButton 
                size="small" 
                label="小サイズ2" 
                :active="activeTabGroup3 === 'small2'"
                @click="setActiveTab('activeTabGroup3', 'small2')"
              />
            </div>
            
            <div style="display: flex; background-color: #f8f9fa; border-radius: 8px; padding: 4px;">
              <TabButton 
                size="medium" 
                label="中サイズ1" 
                :active="activeTabGroup2 === 'option1'"
                @click="setActiveTab('activeTabGroup2', 'option1')"
              />
              <TabButton 
                size="medium" 
                label="中サイズ2" 
                :active="activeTabGroup2 === 'option2'"
                @click="setActiveTab('activeTabGroup2', 'option2')"
              />
            </div>
            
            <div style="display: flex; background-color: #f8f9fa; border-radius: 10px; padding: 6px;">
              <TabButton size="large" label="大サイズ1" :active="true" />
              <TabButton size="large" label="大サイズ2" />
            </div>
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">実際のアプリケーションライクな例</h4>
          <div style="background: linear-gradient(90deg, #333333 0%, #555555 100%); border-radius: 8px 8px 0 0; display: flex;">
            <TabButton 
              label="経路案内" 
              :active="activeTabGroup1 === 'route'"
              @click="setActiveTab('activeTabGroup1', 'route')"
              style="flex: 1; color: white;"
            />
            <TabButton 
              label="利用特典" 
              :active="activeTabGroup1 === 'benefits'"
              @click="setActiveTab('activeTabGroup1', 'benefits')"
              style="flex: 1; color: white;"
            />
            <TabButton 
              label="特典検索" 
              :active="activeTabGroup1 === 'search'"
              @click="setActiveTab('activeTabGroup1', 'search')"
              style="flex: 1; color: white;"
            />
          </div>
          <div style="background: white; padding: 20px; border: 1px solid #ddd; border-top: none; border-radius: 0 0 8px 8px;">
            <div v-if="activeTabGroup1 === 'route'">
              <h5 style="margin-top: 0;">経路案内コンテンツ</h5>
              <p style="margin: 0;">目的地への最適なルートを表示します。</p>
            </div>
            <div v-else-if="activeTabGroup1 === 'benefits'">
              <h5 style="margin-top: 0;">利用特典コンテンツ</h5>
              <p style="margin: 0;">あなたが利用できる特典を表示します。</p>
            </div>
            <div v-else-if="activeTabGroup1 === 'search'">
              <h5 style="margin-top: 0;">特典検索コンテンツ</h5>
              <p style="margin: 0;">条件を指定して特典を検索できます。</p>
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};
