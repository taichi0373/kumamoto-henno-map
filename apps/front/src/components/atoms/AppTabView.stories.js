import AppTabView from './AppTabView.vue';
import { ref } from 'vue';

const tabs = [
  { label: '概要', content: 'アプリの概要を表示します。' },
  { label: '特典', content: '特典一覧を表示します。' },
  { label: '設定', content: '設定画面を表示します。' },
];

export default {
  title: 'Design System/Atoms/AppTabView',
  component: AppTabView,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    modelValue: {
      control: { type: 'number', min: 0, max: 10, step: 1 },
      description: 'アクティブタブ',
    },
    tabs: {
      control: 'object',
      description: 'タブ一覧',
    },
    scrollable: {
      control: 'boolean',
      description: 'スクロール',
    },
    inputClass: {
      control: 'text',
      description: 'クラス',
    },
    inputStyle: {
      control: 'object',
      description: 'スタイル',
    },
  },
};

const Template = (args) => ({
  components: { AppTabView },
  setup() {
    const activeIndex = ref(args.modelValue);
    return { args, activeIndex };
  },
  template: `
    <div style="width: 480px;">
      <AppTabView v-bind="args" v-model="activeIndex" />
    </div>
  `,
});

export const Default = Template.bind({});
Default.args = {
  modelValue: 0,
  tabs,
  scrollable: false,
  inputClass: '',
  inputStyle: '',
};

export const Scrollable = Template.bind({});
Scrollable.args = {
  ...Default.args,
  tabs: [
    { label: 'タブ1', content: 'コンテンツ1' },
    { label: 'タブ2', content: 'コンテンツ2' },
    { label: 'タブ3', content: 'コンテンツ3' },
    { label: 'タブ4', content: 'コンテンツ4' },
    { label: 'タブ5', content: 'コンテンツ5' },
  ],
  scrollable: true,
};
