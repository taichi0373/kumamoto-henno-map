import AppTab from './AppTab.vue';
import { ref } from 'vue';

const tabs = [
  { id: 'overview', label: '概要', title: '概要' },
  { id: 'benefit', label: '特典', title: '特典' },
  { id: 'settings', label: '設定', title: '設定' },
];

export default {
  title: 'Design System/Atoms/AppTab',
  component: AppTab,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    modelValue: {
      control: { type: 'number', min: 0, max: 10, step: 1 },
      description: 'アクティブタブのインデックス',
    },
    tabs: {
      control: 'object',
      description: 'タブ一覧',
    },
    scrollable: {
      control: 'boolean',
      description: 'スクロール有効',
    },
    inputClass: {
      control: 'text',
      description: '追加クラス',
    },
    inputStyle: {
      control: 'object',
      description: '追加スタイル',
    },
  },
};

const Template = (args) => ({
  components: { AppTab },
  setup() {
    const activeIndex = ref(args.modelValue);
    return { args, activeIndex };
  },
  template: `
    <div style="width: 480px;">
      <AppTab v-bind="args" v-model="activeIndex" />
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
    { id: 'tab1', label: 'タブ1', title: 'タブ1' },
    { id: 'tab2', label: 'タブ2', title: 'タブ2' },
    { id: 'tab3', label: 'タブ3', title: 'タブ3' },
    { id: 'tab4', label: 'タブ4', title: 'タブ4' },
    { id: 'tab5', label: 'タブ5', title: 'タブ5' },
  ],
  scrollable: true,
};

export const WithActiveTab = Template.bind({});
WithActiveTab.args = {
  ...Default.args,
  modelValue: 1,
};
