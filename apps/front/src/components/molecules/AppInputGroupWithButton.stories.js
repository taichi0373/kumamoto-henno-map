import AppInputGroupWithButton from './AppInputGroupWithButton.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Molecules/AppInputGroupWithButton',
  component: AppInputGroupWithButton,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
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
    inputId: {
      control: 'text',
      description: '入力要素のid',
    },
    inputClass: {
      control: 'text',
      description: '入力要素のクラス',
    },
    inputStyle: {
      control: 'object',
      description: '入力要素のスタイル',
    },
    tabindex: {
      control: { type: 'number', min: -1, max: 20, step: 1 },
      description: 'タブインデックス',
    },
    buttonIcon: {
      control: 'text',
      description: 'ボタンのアイコン（PrimeIconsクラス名）',
    },
    buttonLabel: {
      control: 'text',
      description: 'ボタンのラベル',
    },
    buttonPrimary: {
      control: 'boolean',
      description: 'ボタンをプライマリスタイルにする',
    },
    buttonDisabled: {
      control: 'boolean',
      description: 'ボタンの無効状態',
    },
    buttonLoading: {
      control: 'boolean',
      description: 'ボタンのローディング状態',
    },
    buttonClass: {
      control: 'text',
      description: 'ボタンのクラス',
    },
  },
};

const Template = (args) => ({
  components: { AppInputGroupWithButton },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <div style="width: 400px;">
      <AppInputGroupWithButton
        v-bind="args"
        v-model="value"
        @button-click="() => console.log('Button clicked')"
      />
    </div>
  `,
});

export const Default = Template.bind({});
Default.args = {
  modelValue: '',
  type: 'text',
  placeholder: 'テキストを入力',
  showError: true,
  error: [],
  disabled: false,
  readonly: false,
  inputId: '',
  maxlength: null,
  tabindex: null,
  inputClass: '',
  inputStyle: '',
  buttonIcon: 'pi pi-map-marker',
  buttonLabel: '',
  buttonPrimary: false,
  buttonDisabled: false,
  buttonLoading: false,
  buttonClass: '',
};

export const WithPrimaryButton = Template.bind({});
WithPrimaryButton.args = {
  ...Default.args,
  buttonIcon: 'pi pi-map-marker',
  buttonPrimary: true,
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  modelValue: '無効化されたフィールド',
  disabled: true,
  buttonDisabled: true,
};

export const Loading = Template.bind({});
Loading.args = {
  ...Default.args,
  modelValue: '検索中...',
  buttonLoading: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  error: [{ type: 1, message: '入力値が正しくありません' }],
  showError: true,
};
