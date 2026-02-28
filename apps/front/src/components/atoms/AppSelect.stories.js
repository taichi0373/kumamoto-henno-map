import AppSelect from './AppSelect.vue';
import { ref } from 'vue';

const options = [
  { label: '熊本市', value: 'kumamoto', text: 'くまもとし' },
  { label: '八代市', value: 'yatsushiro', text: 'やつしろし' },
  { label: '天草市', value: 'amakusa', text: 'あまくさし' },
  { label: '人吉市', value: 'hitoyoshi', text: 'ひとよしし' },
];

export default {
  title: 'Design System/Atoms/AppSelect',
  component: AppSelect,
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
      control: 'text',
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
    placeholder: {
      control: 'text',
      description: 'プレースホルダー',
    },
    filter: {
      control: 'boolean',
      description: 'フィルタ表示',
    },
    showClear: {
      control: 'boolean',
      description: 'クリアボタン',
    },
    disabled: {
      control: 'boolean',
      description: '無効状態',
    },
    readonly: {
      control: 'boolean',
      description: '読み取り専用',
    },
    showError: {
      control: 'boolean',
      description: 'エラー表示フラグ',
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
  components: { AppSelect },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <AppSelect
      v-bind="args"
      v-model="value"
      style="width: 320px"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-select',
  modelValue: '',
  placeholder: '市区町村を選択してください',
  options,
  optionLabel: 'label',
  optionValue: 'value',
  filter: false,
  showClear: false,
  disabled: false,
  readonly: false,
  showError: true,
  inputClass: '',
  inputStyle: '',
  tabindex: 0,
};

export const WithValue = Template.bind({});
WithValue.args = {
  ...Default.args,
  inputId: 'value-select',
  modelValue: 'kumamoto',
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-select',
  disabled: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-select',
  error: [{ type: 1, message: '選択が必要です' }],
};

export const WithWarning = Template.bind({});
WithWarning.args = {
  ...Default.args,
  inputId: 'warning-select',
  error: [{ type: 2, message: '候補を確認してください' }],
};

export const Filterable = Template.bind({});
Filterable.args = {
  ...Default.args,
  inputId: 'filter-select',
  filter: true,
};
