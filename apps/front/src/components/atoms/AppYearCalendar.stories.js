import AppYearCalendar from './AppYearCalendar.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppYearCalendar',
  component: AppYearCalendar,
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
      description: '入力値 (string | Date | null)',
    },
    placeholder: {
      control: 'text',
      description: 'プレースホルダー',
    },
    dateFormat: {
      control: 'text',
      description: '表示フォーマット',
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
  components: { AppYearCalendar },
  setup() {
    const value = ref(args.modelValue ?? null);
    return { args, value };
  },
  template: `
    <AppYearCalendar
      v-bind="args"
      v-model="value"
      @focus="() => console.log('Focused')"
      @blur="() => console.log('Blurred')"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-year-calendar',
  modelValue: null,
  placeholder: '年を選択してください',
  dateFormat: 'yy',
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
  inputId: 'value-year-calendar',
  modelValue: new Date(2026, 0, 1),
};

export const WithStringValue = Template.bind({});
WithStringValue.args = {
  ...Default.args,
  inputId: 'string-value-year-calendar',
  modelValue: '2026-01-01',
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-year-calendar',
  modelValue: new Date(2026, 0, 1),
  disabled: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-year-calendar',
  error: [{ type: 1, message: '年を選択してください' }],
  showError: true,
};

export const WithWarning = Template.bind({});
WithWarning.args = {
  ...Default.args,
  inputId: 'warning-year-calendar',
  error: [{ type: 2, message: '年が選択されていません。よろしいですか？' }],
  showError: true,
};
