import AppTextarea from './AppTextarea.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppTextarea',
  component: AppTextarea,
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
    cols: {
      control: { type: 'number', min: 10, max: 100, step: 1 },
      description: 'テキストの桁数',
    },
    rows: {
      control: { type: 'number', min: 2, max: 20, step: 1 },
      description: 'テキストの行数',
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
  components: { AppTextarea },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <AppTextarea
      v-bind="args"
      v-model="value"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-textarea',
  modelValue: '',
  placeholder: 'テキストを入力してください',
  cols: 30,
  rows: 5,
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
  inputId: 'value-textarea',
  modelValue: '複数行のサンプルテキストです。\n改行も表示されます。',
};

export const RowsAndCols = Template.bind({});
RowsAndCols.args = {
  ...Default.args,
  inputId: 'rows-cols-textarea',
  cols: 50,
  rows: 8,
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-textarea',
  modelValue: '無効な入力フィールド',
  disabled: true,
};

export const Readonly = Template.bind({});
Readonly.args = {
  ...Default.args,
  inputId: 'readonly-textarea',
  modelValue: '読み取り専用の値',
  readonly: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-textarea',
  modelValue: '入力内容に問題があります',
  error: [{ type: 1, message: '必須項目です' }],
  showError: true,
};

export const WithWarning = Template.bind({});
WithWarning.args = {
  ...Default.args,
  inputId: 'warning-textarea',
  modelValue: '注意が必要です',
  error: [{ type: 2, message: '30文字以内で入力してください' }],
  showError: true,
};

export const Maxlength = Template.bind({});
Maxlength.args = {
  ...Default.args,
  inputId: 'maxlength-textarea',
  maxlength: 50,
  placeholder: '50文字以内で入力してください',
};

export const CustomStyle = Template.bind({});
CustomStyle.args = {
  ...Default.args,
  inputId: 'style-textarea',
  inputStyle: {
    minHeight: '140px',
  },
};
