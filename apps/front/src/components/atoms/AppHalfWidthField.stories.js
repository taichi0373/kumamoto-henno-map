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
  showError: true,
  inputClass: '',
  inputStyle: '',
  tabindex: 0,
};

export const WithValue = Template.bind({});
WithValue.args = {
  ...Default.args,
  inputId: 'value-half-width-field',
  modelValue: '入力済み',
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-half-width-field',
  modelValue: '無効な入力フィールド',
  disabled: true,
};

export const Readonly = Template.bind({});
Readonly.args = {
  ...Default.args,
  inputId: 'readonly-half-width-field',
  modelValue: '読み取り専用の値',
  readonly: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-half-width-field',
  modelValue: '入力内容に問題があります',
  rule: 'required',
  showError: true,
};

export const WithWarning = Template.bind({});
WithWarning.args = {
  ...Default.args,
  inputId: 'warning-half-width-field',
  modelValue: '注意が必要です',
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
