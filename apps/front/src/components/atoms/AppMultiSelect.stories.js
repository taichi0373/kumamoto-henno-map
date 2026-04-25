import AppMultiSelect from './AppMultiSelect.vue';
import { ref } from 'vue';

const options = [
  { label: '熊本市', value: 'kumamoto', text: 'くまもとし' },
  { label: '八代市', value: 'yatsushiro', text: 'やつしろし' },
  { label: '天草市', value: 'amakusa', text: 'あまくさし' },
  { label: '人吉市', value: 'hitoyoshi', text: 'ひとよしし' },
];

export default {
  title: 'Design System/Atoms/AppMultiSelect',
  component: AppMultiSelect,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    inputId: {
      control: 'text',
      description: '入力要素のid',
    },
    modelValue: {
      control: 'object',
      description: '選択値',
    },
    options: {
      control: 'object',
      description: '選択肢',
    },
    optionLabel: {
      control: 'text',
      description: '表示ラベルキー',
    },
    optionValue: {
      control: 'text',
      description: '値キー',
    },
    optionGroupLabel: {
      control: 'text',
      description: 'グループラベルキー',
    },
    optionGroupChildren: {
      control: 'text',
      description: 'グループ子要素キー',
    },
    placeholder: {
      control: 'text',
      description: 'プレースホルダー',
    },
    filter: {
      control: 'boolean',
      description: 'フィルタ表示',
    },
    filterFields: {
      control: 'object',
      description: 'フィルタ対象フィールド',
    },
    filterPlaceholder: {
      control: 'text',
      description: 'フィルタのプレースホルダー',
    },
    scrollHeight: {
      control: 'text',
      description: 'ドロップダウンのスクロール高さ(px)',
    },
    virtualScrollerOptions: {
      control: 'object',
      description: 'バーチャルスクローラーオプション',
    },
    maxSelectedLabels: {
      control: { type: 'number', min: 1, max: 10, step: 1 },
      description: '表示ラベル数',
    },
    minOptionsForFilter: {
      control: { type: 'number', min: 0, max: 50, step: 1 },
      description: 'フィルタを自動表示する最小選択肢数',
    },
    selectionLimit: {
      control: { type: 'number', min: 1, max: 10, step: 1 },
      description: '最大選択数',
    },
    showClear: {
      control: 'boolean',
      description: 'クリアボタン表示',
    },
    disabled: {
      control: 'boolean',
      description: '無効状態',
    },
    showError: {
      control: 'boolean',
      description: 'エラー表示フラグ',
    },
    error: {
      control: 'object',
      description: 'エラー情報',
    },
    inputClass: {
      control: 'text',
      description: 'インプットのクラス',
    },
    inputStyle: {
      control: 'object',
      description: 'インプットのスタイル',
    },
    tabindex: {
      control: { type: 'number', min: -1, max: 20, step: 1 },
      description: 'タブインデックス',
    },
  },
};

const Template = (args) => ({
  components: { AppMultiSelect },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <AppMultiSelect
      v-bind="args"
      v-model="value"
      style="width: 360px"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-multi-select',
  modelValue: [],
  placeholder: '市区町村を選択してください',
  options,
  optionLabel: 'label',
  optionValue: 'value',
  optionGroupLabel: undefined,
  optionGroupChildren: undefined,
  filter: false,
  filterFields: undefined,
  filterPlaceholder: '',
  scrollHeight: '200',
  virtualScrollerOptions: undefined,
  maxSelectedLabels: 3,
  minOptionsForFilter: 10,
  selectionLimit: undefined,
  showClear: true,
  disabled: false,
  showError: true,
  inputClass: '',
  inputStyle: '',
  tabindex: 0,
};

export const WithValue = Template.bind({});
WithValue.args = {
  ...Default.args,
  inputId: 'value-multi-select',
  modelValue: ['kumamoto', 'yatsushiro'],
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-multi-select',
  disabled: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-multi-select',
  error: [{ type: 1, message: '1つ以上選択してください' }],
  showError: true,
};

export const WithWarning = Template.bind({});
WithWarning.args = {
  ...Default.args,
  inputId: 'warning-multi-select',
  error: [{ type: 2, message: '3件まで選択できます' }],
  showError: true,
};

export const Filterable = Template.bind({});
Filterable.args = {
  ...Default.args,
  inputId: 'filter-multi-select',
  filter: true,
  filterPlaceholder: '市区町村を検索してください',
};

export const WithSelectionLimit = Template.bind({});
WithSelectionLimit.args = {
  ...Default.args,
  inputId: 'selection-limit-multi-select',
  selectionLimit: 2,
  placeholder: '最大2件まで選択できます',
};
