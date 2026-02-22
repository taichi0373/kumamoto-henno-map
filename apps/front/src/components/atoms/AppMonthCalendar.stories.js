import AppMonthCalendar from './AppMonthCalendar.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppMonthCalendar',
  component: AppMonthCalendar,
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
      description: '入力値 (YYYYMM形式)',
    },
    placeholder: {
      control: 'text',
      description: 'プレースホルダー',
    },
    dateFormat: {
      control: 'text',
      description: '表示フォーマット',
    },
    showIcon: {
      control: 'boolean',
      description: 'アイコン表示',
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
  components: { AppMonthCalendar },
  setup() {
    const value = ref(args.modelValue ?? null);
    return { args, value };
  },
  template: `
    <AppMonthCalendar
      v-bind="args"
      v-model="value"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-month-calendar',
  modelValue: null,
  placeholder: '月を選択してください',
  dateFormat: 'yy-mm',
  showIcon: true,
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
  inputId: 'value-month-calendar',
  modelValue: new Date(2026, 1, 1),
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-month-calendar',
  modelValue: new Date(2026, 1, 1),
  disabled: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-month-calendar',
  error: [{ type: 1, message: '月を選択してください' }],
  showError: true,
};
