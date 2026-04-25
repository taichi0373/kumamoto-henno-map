import AppHalfWidthField from './AppHalfWidthField.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppHalfWidthField',
  component: AppHalfWidthField,
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
    placeholder: {
      control: 'text',
      description: 'プレースホルダー',
    },
    maxlength: {
      control: { type: 'number', min: 0, max: 2000, step: 10 },
      description: '最大文字数',
    },
    disabled: {
      control: 'boolean',
      description: '無効状態',
    },
    readonly: {
      control: 'boolean',
      description: '読み取り専用',
    },
    halfWidthKana: {
      control: 'boolean',
      description: '半角カタカナのみ許容（trueの場合、半角カタカナ以外を削除）',
    },
    rule: {
      control: 'text',
      description: '許容文字の正規表現パターン',
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
  components: { AppHalfWidthField },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <AppHalfWidthField
      v-bind="args"
      v-model="value"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-half-width-field',
  modelValue: '',
  placeholder: '半角の入力欄',
  maxlength: null,
  disabled: false,
  readonly: false,
  halfWidthKana: false,
  rule: null,
  showError: true,
  inputClass: '',
  inputStyle: '',
  tabindex: 0,
};

export const WithValue = Template.bind({});
WithValue.args = {
  ...Default.args,
  inputId: 'value-half-width-field',
  modelValue: 'ABC123',
};

export const HalfWidthKana = Template.bind({});
HalfWidthKana.args = {
  ...Default.args,
  inputId: 'kana-half-width-field',
  halfWidthKana: true,
  placeholder: '半角カタカナのみ入力可',
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-half-width-field',
  modelValue: 'ABC123',
  disabled: true,
};

export const Readonly = Template.bind({});
Readonly.args = {
  ...Default.args,
  inputId: 'readonly-half-width-field',
  modelValue: 'ABC123',
  readonly: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-half-width-field',
  error: [{ type: 1, message: '○○が入力されていません。' }],
  showError: true,
};

export const WithWarning = Template.bind({});
WithWarning.args = {
  ...Default.args,
  inputId: 'warning-half-width-field',
  error: [{ type: 2, message: '○○が入力されていません。よろしいですか？' }],
  showError: true,
};

export const CustomStyle = Template.bind({});
CustomStyle.args = {
  ...Default.args,
  inputId: 'style-half-width-field',
  inputStyle: {
    maxWidth: '360px',
  },
};
