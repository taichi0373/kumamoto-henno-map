import AppTextField from './AppTextField.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppTextField',
  component: AppTextField,
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
      description: '入力値',
    },
    type: {
      control: { type: 'select' },
      options: ['text', 'password', 'email', 'tel', 'search'],
      description: '入力タイプ',
    },
    placeholder: {
      control: 'text',
      description: 'プレースホルダー',
    },
    maxlength: {
      control: { type: 'number', min: 0, max: 2000, step: 10 },
      description: '最大文字数',
    },
    rule: {
      control: 'text',
      description: '許容文字の正規表現パターン（例: a-zA-Z0-9）',
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
  components: { AppTextField },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <AppTextField
      v-bind="args"
      v-model="value"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-text-field',
  modelValue: '',
  placeholder: 'テキストを入力してください',
  type: 'text',
  maxlength: null,
  rule: null,
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
  inputId: 'value-text-field',
  modelValue: '入力済みのテキスト',
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-text-field',
  modelValue: '無効な入力フィールド',
  disabled: true,
};

export const Readonly = Template.bind({});
Readonly.args = {
  ...Default.args,
  inputId: 'readonly-text-field',
  modelValue: '読み取り専用の値',
  readonly: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-text-field',
  modelValue: '',
  error: [{ type: 1, message: '○○が入力されていません。' }],
  showError: true,
};

export const WithWarning = Template.bind({});
WithWarning.args = {
  ...Default.args,
  inputId: 'warning-text-field',
  modelValue: '',
  error: [{ type: 2, message: '○○が入力されていません。よろしいですか？' }],
  showError: true,
};

export const Maxlength = Template.bind({});
Maxlength.args = {
  ...Default.args,
  inputId: 'maxlength-text-field',
  maxlength: 10,
  placeholder: '10文字以内で入力してください',
};
