import AppCalendar from './AppCalendar.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppCalendar',
  component: AppCalendar,
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
      description: '入力値 (YYYY/MM/DD形式)',
    },
    placeholder: {
      control: 'text',
      description: 'プレースホルダー',
    },
    format: {
      control: 'text',
      description: '日付表示フォーマット',
    },
    error: {
      control: 'object',
      description: 'エラー情報',
    },
    showError: {
      control: 'boolean',
      description: 'エラー表示フラグ',
    },
    readonly: {
      control: 'boolean',
      description: '読み取り専用フラグ',
    },
    disabled: {
      control: 'boolean',
      description: '無効化フラグ',
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
  components: { AppCalendar },
  setup() {
    const value = ref(args.modelValue ?? null);
    return { args, value };
  },
  template: `
    <AppCalendar
      v-bind="args"
      v-model="value"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-calendar',
  modelValue: null,
  placeholder: '日付を選択してください',
  format: 'yy/mm/dd',
  error: [],
  showError: true,
  readonly: false,
  disabled: false,
  inputClass: '',
  inputStyle: {},
  tabindex: 0,
  showTime: false,
};

export const WithValue = Template.bind({});
WithValue.args = {
  ...Default.args,
  inputId: 'value-calendar',
  modelValue: new Date(2026, 1, 14),
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-calendar',
  modelValue: new Date(2026, 1, 14),
  disabled: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-calendar',
  error: [{ type: 1, message: '日付を選択してください' }],
  showError: true,
};

export const WithWarning = Template.bind({});
WithWarning.args = {
  ...Default.args,
  inputId: 'warning-calendar',
  error: [{ type: 2, message: '未来日が選択されています。よろしいですか？' }],
  showError: true,
};

export const ReadOnly = Template.bind({});
ReadOnly.args = {
  ...Default.args,
  inputId: 'readonly-calendar',
  modelValue: new Date(2026, 1, 14),
  readonly: true,
};
