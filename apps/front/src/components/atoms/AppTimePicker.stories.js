import AppTimePicker from './AppTimePicker.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppTimePicker',
  component: AppTimePicker,
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
      description: '入力値 (HH:mm形式)',
    },
    placeholder: {
      control: 'text',
      description: 'プレースホルダー',
    },
    hourFormat: {
      control: { type: 'select' },
      options: ['12', '24'],
      description: '時刻表記',
    },
    showSeconds: {
      control: 'boolean',
      description: '秒表示',
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
  components: { AppTimePicker },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <AppTimePicker
      v-bind="args"
      v-model="value"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-time-picker',
  modelValue: null,
  placeholder: '時刻を選択してください',
  hourFormat: '24',
  showSeconds: false,
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
  inputId: 'value-time-picker',
  modelValue: new Date(2026, 1, 14, 9, 30),
};

export const WithSeconds = Template.bind({});
WithSeconds.args = {
  ...Default.args,
  inputId: 'seconds-time-picker',
  showSeconds: true,
  modelValue: new Date(2026, 1, 14, 9, 30, 45),
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-time-picker',
  modelValue: new Date(2026, 1, 14, 9, 30),
  disabled: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-time-picker',
  error: [{ type: 1, message: '時刻を選択してください' }],
  showError: true,
};
