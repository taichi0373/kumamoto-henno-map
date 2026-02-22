import AppSuggestionList from './AppSuggestionList.vue';

export default {
  title: 'Design System/Atoms/AppSuggestionList',
  component: AppSuggestionList,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    suggestions: {
      control: 'object',
      description: '提案リスト',
    },
    activeIndex: {
      control: 'number',
      description: 'アクティブなインデックス（-1は非選択状態）',
    },
    inputClass: {
      control: 'text',
      description: '追加CSSクラス',
    },
    onSelect: { action: 'selected' },
  },
};

// サンプルデータ
const sampleSuggestions = [
  {
    id: 1,
    name: '熊本市役所',
    formattedAddress: '熊本県熊本市中央区手取本町1-1',
  },
  {
    id: 2,
    name: '熊本県庁',
    formattedAddress: '熊本県熊本市中央区水前寺6丁目18-1',
  },
  {
    id: 3,
    name: '熊本城',
    formattedAddress: '熊本県熊本市中央区本丸1-1',
  },
  {
    id: 4,
    name: '熊本駅',
    formattedAddress: '熊本県熊本市西区春日3丁目15-1',
  },
];

const shortSuggestions = [
  {
    id: 1,
    name: '熊本市役所',
    formattedAddress: '熊本県熊本市中央区手取本町1-1',
  },
  {
    id: 2,
    name: '熊本県庁',
    formattedAddress: '熊本県熊本市中央区水前寺6丁目18-1',
  },
];

const longSuggestions = [
  ...sampleSuggestions,
  {
    id: 5,
    name: '水前寺成趣園',
    formattedAddress: '熊本県熊本市中央区水前寺公園8-1',
  },
  {
    id: 6,
    name: '阿蘇山',
    formattedAddress: '熊本県阿蘇市',
  },
  {
    id: 7,
    name: '通町筋交差点',
    formattedAddress: '熊本県熊本市中央区通町筋',
  },
  {
    id: 8,
    name: '上通りアーケード',
    formattedAddress: '熊本県熊本市中央区上通町',
  },
  {
    id: 9,
    name: '下通りアーケード',
    formattedAddress: '熊本県熊本市中央区下通2丁目',
  },
  {
    id: 10,
    name: '桜町バスターミナル',
    formattedAddress: '熊本県熊本市中央区桜町3-10',
  },
];

export const Default = {
  args: {
    suggestions: sampleSuggestions,
    activeIndex: -1,
  },
  render: (args) => ({
    components: { AppSuggestionList },
    setup() {
      return { args };
    },
    template: `
      <div style="width: 400px; position: relative;">
        <div style="padding: 12px; border: 2px solid #ddd; border-radius: 8px; background: #f9f9f9; margin-bottom: 8px;">
          検索フィールドのイメージ（実際の入力フィールドではありません）
        </div>
        <AppSuggestionList v-bind="args" />
      </div>
    `,
  }),
};

export const WithActiveIndex = {
  args: {
    suggestions: sampleSuggestions,
    activeIndex: 1,
  },
  render: (args) => ({
    components: { AppSuggestionList },
    setup() {
      return { args };
    },
    template: `
      <div style="width: 400px; position: relative;">
        <div style="padding: 12px; border: 2px solid #ddd; border-radius: 8px; background: #f9f9f9; margin-bottom: 8px;">
          検索フィールド（2番目の項目がアクティブ）
        </div>
        <AppSuggestionList v-bind="args" />
      </div>
    `,
  }),
};

export const ShortList = {
  args: {
    suggestions: shortSuggestions,
    activeIndex: -1,
  },
  render: (args) => ({
    components: { AppSuggestionList },
    setup() {
      return { args };
    },
    template: `
      <div style="width: 400px; position: relative;">
        <div style="padding: 12px; border: 2px solid #ddd; border-radius: 8px; background: #f9f9f9; margin-bottom: 8px;">
          短いリスト（2件）
        </div>
        <AppSuggestionList v-bind="args" />
      </div>
    `,
  }),
};

export const LongList = {
  args: {
    suggestions: longSuggestions,
    activeIndex: 3,
  },
  render: (args) => ({
    components: { AppSuggestionList },
    setup() {
      return { args };
    },
    template: `
      <div style="width: 400px; position: relative;">
        <div style="padding: 12px; border: 2px solid #ddd; border-radius: 8px; background: #f9f9f9; margin-bottom: 8px;">
          長いリスト（スクロール可能）
        </div>
        <AppSuggestionList v-bind="args" />
      </div>
    `,
  }),
};

export const EmptyList = {
  args: {
    suggestions: [],
    activeIndex: -1,
  },
  render: (args) => ({
    components: { AppSuggestionList },
    setup() {
      return { args };
    },
    template: `
      <div style="width: 400px; position: relative;">
        <div style="padding: 12px; border: 2px solid #ddd; border-radius: 8px; background: #f9f9f9; margin-bottom: 8px;">
          空のリスト（何も表示されません）
        </div>
        <AppSuggestionList v-bind="args" />
      </div>
    `,
  }),
};

export const WithoutAddress = {
  args: {
    suggestions: [
      { id: 1, name: '熊本市役所' },
      { id: 2, name: '熊本県庁' },
      { id: 3, name: '熊本城' },
    ],
    activeIndex: -1,
  },
  render: (args) => ({
    components: { AppSuggestionList },
    setup() {
      return { args };
    },
    template: `
      <div style="width: 400px; position: relative;">
        <div style="padding: 12px; border: 2px solid #ddd; border-radius: 8px; background: #f9f9f9; margin-bottom: 8px;">
          住所なしの提案（名前のみ）
        </div>
        <AppSuggestionList v-bind="args" />
      </div>
    `,
  }),
};

export const SearchExample = {
  render: () => ({
    components: { AppSuggestionList },
    data() {
      return {
        query: '熊本',
        suggestions: sampleSuggestions,
        activeIndex: -1,
        selectedItem: null,
      };
    },
    methods: {
      onSelect(suggestion) {
        this.selectedItem = suggestion;
        this.query = suggestion.name;
        this.suggestions = [];
      },
      onInput() {
        // 実際の実装では、ここで検索APIを呼び出します
        this.suggestions = this.query ? sampleSuggestions : [];
        this.activeIndex = -1;
      },
    },
    template: `
      <div style="width: 400px; position: relative;">
        <h4>検索インターフェースの例</h4>
        <div style="margin-bottom: 16px;">
          <label for="search-input">検索:</label>
          <input 
            id="search-input"
            v-model="query"
            @input="onInput"
            type="text" 
            placeholder="場所を検索..." 
            style="width: 100%; padding: 12px; border: 2px solid #ddd; border-radius: 8px; margin-top: 4px;"
          >
          <AppSuggestionList 
            :suggestions="suggestions"
            :activeIndex="activeIndex"
            @select="onSelect"
          />
        </div>
        <div v-if="selectedItem" style="padding: 12px; background: #e8f5e8; border-radius: 8px;">
          <strong>選択された項目:</strong><br>
          {{ selectedItem.name }}<br>
          <small>{{ selectedItem.formattedAddress }}</small>
        </div>
      </div>
    `,
  }),
};

export const KeyboardNavigation = {
  render: () => ({
    components: { AppSuggestionList },
    data() {
      return {
        suggestions: sampleSuggestions,
        activeIndex: 0,
      };
    },
    methods: {
      moveUp() {
        if (this.activeIndex > 0) {
          this.activeIndex--;
        }
      },
      moveDown() {
        if (this.activeIndex < this.suggestions.length - 1) {
          this.activeIndex++;
        }
      },
      selectCurrent() {
        if (this.activeIndex >= 0 && this.activeIndex < this.suggestions.length) {
          alert(`選択: ${this.suggestions[this.activeIndex].name}`);
        }
      },
    },
    template: `
      <div style="width: 400px; position: relative;">
        <h4>キーボードナビゲーションの例</h4>
        <div style="margin-bottom: 12px; display: flex; gap: 8px;">
          <button @click="moveUp" style="padding: 8px 12px;">↑ 上へ</button>
          <button @click="moveDown" style="padding: 8px 12px;">↓ 下へ</button>
          <button @click="selectCurrent" style="padding: 8px 12px;">選択</button>
        </div>
        <div style="padding: 12px; border: 2px solid #ddd; border-radius: 8px; background: #f9f9f9; margin-bottom: 8px;">
          現在のインデックス: {{ activeIndex }}
        </div>
        <AppSuggestionList 
          :suggestions="suggestions"
          :activeIndex="activeIndex"
        />
      </div>
    `,
  }),
};