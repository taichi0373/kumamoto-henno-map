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
      description: '入力値 (YYYY形式)',
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
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
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
