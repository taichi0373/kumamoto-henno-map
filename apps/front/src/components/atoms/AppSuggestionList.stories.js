import AppSuggestionList from './AppSuggestionList.vue';

export default {
  title: 'Design System/Atoms/AppSuggestionList',
  component: AppSuggestionList,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    modelValue: {
      control: 'object',
      description: '選択値',
    },
    tabindex: {
      control: 'number',
      description: 'タブインデックス',
    },
    inputClass: {
      control: 'text',
      description: '追加CSSクラス',
    },
    inputStyle: {
      control: 'object',
      description: '追加スタイル',
    },
    onSelect: { action: 'selected' },
  },
};

// サンプルデータ
const sampleSuggestions = [
  {
    id: 1,
    name: '熊本市役所',
    address: '熊本県熊本市中央区手取本町1-1',
  },
  {
    id: 2,
    name: '熊本県庁',
    address: '熊本県熊本市中央区水前寺6丁目18-1',
  },
  {
    id: 3,
    name: '熊本城',
    address: '熊本県熊本市中央区本丸1-1',
  },
  {
    id: 4,
    name: '熊本駅',
    address: '熊本県熊本市西区春日3丁目15-1',
  },
];

export const Default = {
  args: {
    modelValue: sampleSuggestions,
    tabindex: -1,
  },
  render: (args) => ({
    components: { AppSuggestionList },
    setup() {
      return { args };
    },
    template: `
      <div style="width: 400px; position: relative;">
        <div style="padding: 12px; border: 2px solid #ddd; border-radius: 8px; background: #f9f9f9; margin-bottom: 8px;">
          検索フィールド
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
        modelValue: sampleSuggestions,
        tabindex: -1,
        selectedItem: null,
      };
    },
    methods: {
      onSelect(suggestion) {
        this.selectedItem = suggestion;
        this.query = suggestion.name;
        this.modelValue = [];
      },
      onInput() {
        // 実際の実装では、ここで検索APIを呼び出します
        this.modelValue = this.query ? sampleSuggestions : [];
        this.tabindex = -1;
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
            :modelValue="modelValue"
            :tabindex="tabindex"
            @select="onSelect"
          />
        </div>
        <div v-if="selectedItem" style="padding: 12px; background: #e8f5e8; border-radius: 8px;">
          <strong>選択された項目:</strong><br>
          {{ selectedItem.name }}<br>
          <small>{{ selectedItem.address }}</small>
        </div>
      </div>
    `,
  }),
};
