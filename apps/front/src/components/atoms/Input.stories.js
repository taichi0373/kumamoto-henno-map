import Input from './Input.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/Input',
  component: Input,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    id: {
      control: 'text',
      description: 'インプット要素のid',
    },
    type: {
      control: { type: 'select' },
      options: ['text', 'email', 'password', 'number', 'tel', 'url', 'search', 'date', 'time', 'datetime-local'],
      description: 'インプットのタイプ',
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
    required: {
      control: 'boolean',
      description: '必須項目',
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large'],
      description: 'サイズ',
    },
    variant: {
      control: { type: 'select' },
      options: ['default', 'success', 'error'],
      description: 'バリアント',
    },
    errorMessage: {
      control: 'text',
      description: 'エラーメッセージ',
    },
    helpText: {
      control: 'text',
      description: 'ヘルプテキスト',
    },
  },
};

const Template = (args) => ({
  components: { Input },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <Input 
      v-bind="args" 
      v-model="value"
      @change="(value) => console.log('Changed:', value)"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  id: 'default-input',
  type: 'text',
  modelValue: '',
  placeholder: 'テキストを入力してください',
  disabled: false,
  readonly: false,
  required: false,
  size: 'medium',
  variant: 'default',
  errorMessage: '',
  helpText: '',
};

export const WithValue = Template.bind({});
WithValue.args = {
  id: 'with-value-input',
  type: 'text',
  modelValue: '入力された値',
  placeholder: 'テキストを入力してください',
  disabled: false,
  readonly: false,
  required: false,
  size: 'medium',
  variant: 'default',
  errorMessage: '',
  helpText: '',
};

export const Email = Template.bind({});
Email.args = {
  id: 'email-input',
  type: 'email',
  modelValue: '',
  placeholder: 'example@email.com',
  disabled: false,
  readonly: false,
  required: true,
  size: 'medium',
  variant: 'default',
  errorMessage: '',
  helpText: 'メールアドレスを入力してください',
};

export const Password = Template.bind({});
Password.args = {
  id: 'password-input',
  type: 'password',
  modelValue: '',
  placeholder: 'パスワードを入力',
  disabled: false,
  readonly: false,
  required: true,
  size: 'medium',
  variant: 'default',
  errorMessage: '',
  helpText: '8文字以上で入力してください',
};

export const Number = Template.bind({});
Number.args = {
  id: 'number-input',
  type: 'number',
  modelValue: '',
  placeholder: '数値を入力',
  disabled: false,
  readonly: false,
  required: false,
  size: 'medium',
  variant: 'default',
  errorMessage: '',
  helpText: '',
  min: 0,
  max: 100,
};

export const Disabled = Template.bind({});
Disabled.args = {
  id: 'disabled-input',
  type: 'text',
  modelValue: '無効な入力フィールド',
  placeholder: 'テキストを入力してください',
  disabled: true,
  readonly: false,
  required: false,
  size: 'medium',
  variant: 'default',
  errorMessage: '',
  helpText: '',
};

export const Readonly = Template.bind({});
Readonly.args = {
  id: 'readonly-input',
  type: 'text',
  modelValue: '読み取り専用の値',
  placeholder: 'テキストを入力してください',
  disabled: false,
  readonly: true,
  required: false,
  size: 'medium',
  variant: 'default',
  errorMessage: '',
  helpText: '',
};

export const Small = Template.bind({});
Small.args = {
  id: 'small-input',
  type: 'text',
  modelValue: '',
  placeholder: '小さな入力フィールド',
  disabled: false,
  readonly: false,
  required: false,
  size: 'small',
  variant: 'default',
  errorMessage: '',
  helpText: '',
};

export const Large = Template.bind({});
Large.args = {
  id: 'large-input',
  type: 'text',
  modelValue: '',
  placeholder: '大きな入力フィールド',
  disabled: false,
  readonly: false,
  required: false,
  size: 'large',
  variant: 'default',
  errorMessage: '',
  helpText: '',
};

export const Success = Template.bind({});
Success.args = {
  id: 'success-input',
  type: 'text',
  modelValue: '正しい値',
  placeholder: 'テキストを入力してください',
  disabled: false,
  readonly: false,
  required: false,
  size: 'medium',
  variant: 'success',
  errorMessage: '',
  helpText: '入力が正常です',
};

export const WithError = Template.bind({});
WithError.args = {
  id: 'error-input',
  type: 'email',
  modelValue: 'invalid-email',
  placeholder: 'example@email.com',
  disabled: false,
  readonly: false,
  required: true,
  size: 'medium',
  variant: 'error',
  errorMessage: '有効なメールアドレスを入力してください',
  helpText: '',
};

export const WithHelpText = Template.bind({});
WithHelpText.args = {
  id: 'help-input',
  type: 'text',
  modelValue: '',
  placeholder: 'ユーザー名を入力',
  disabled: false,
  readonly: false,
  required: false,
  size: 'medium',
  variant: 'default',
  errorMessage: '',
  helpText: '3文字以上20文字以下で入力してください',
};
