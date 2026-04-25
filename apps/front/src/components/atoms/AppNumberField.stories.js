import AppNumberField from './AppNumberField.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppNumberField',
  component: AppNumberField,
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
      control: 'number',
      description: '入力値',
    },
    placeholder: {
      control: 'text',
      description: 'プレースホルダー',
    },
    min: {
      control: 'number',
      description: '最小値',
    },
    max: {
      control: 'number',
      description: '最大値',
    },
    step: {
      control: 'number',
      description: 'ステップ',
    },
    useGrouping: {
      control: 'boolean',
      description: '3桁区切り',
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
  components: { AppNumberField },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <AppNumberField
      v-bind="args"
      v-model="value"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-number-field',
  modelValue: null,
  placeholder: '数値を入力してください',
  min: 0,
  max: 9999,
  step: 1,
  useGrouping: false,
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
  inputId: 'value-number-field',
  modelValue: 1200,
};

export const WithGrouping = Template.bind({});
WithGrouping.args = {
  ...Default.args,
  inputId: 'grouping-number-field',
  modelValue: 1200000,
  useGrouping: true,
  placeholder: '3桁区切りで表示',
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-number-field',
  modelValue: 500,
  disabled: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-number-field',
  modelValue: 0,
  error: [{ type: 1, message: '0より大きい値を入力してください' }],
  showError: true,
};

export const WithWarning = Template.bind({});
WithWarning.args = {
  ...Default.args,
  inputId: 'warning-number-field',
  modelValue: 10000,
  error: [{ type: 2, message: '上限を超えています' }],
  showError: true,
};
