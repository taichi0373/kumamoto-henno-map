import AppPassword from './AppPassword.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppPassword',
  component: AppPassword,
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
  components: { AppPassword },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <AppPassword
      v-bind="args"
      v-model="value"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-password',
  modelValue: '',
  placeholder: 'パスワードを入力してください',
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
  inputId: 'value-password',
  modelValue: 'password123',
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-password',
  modelValue: 'password123',
  disabled: true,
};

export const Readonly = Template.bind({});
Readonly.args = {
  ...Default.args,
  inputId: 'readonly-password',
  modelValue: 'password123',
  readonly: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-password',
  error: [{ type: 1, message: 'パスワードを入力してください' }],
  showError: true,
};

export const WithWarning = Template.bind({});
WithWarning.args = {
  ...Default.args,
  inputId: 'warning-password',
  error: [{ type: 2, message: '8文字以上で入力してください' }],
  showError: true,
};
