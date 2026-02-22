import AppLink from './AppLink.vue';

export default {
  title: 'Design System/Atoms/AppLink',
  component: AppLink,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    tabindex: {
      control: 'number',
      description: 'タブインデックス',
      defaultValue: 0,
    },
    onClick: { action: 'clicked' },
  },
};

export const Default = {
  args: {
    tabindex: 0,
  },
  render: (args) => ({
    components: { AppLink },
    setup() {
      return { args };
    },
    template: `<AppLink v-bind="args">デフォルトリンク</AppLink>`,
  }),
};

export const TabIndex2 = {
  args: {
    tabindex: 2,
  },
  render: (args) => ({
    components: { AppLink },
    setup() {
      return { args };
    },
    template: `<AppLink v-bind="args">タブインデックス2</AppLink>`,
  }),
};

export const CustomSlot = {
  args: {
    tabindex: 0,
  },
  render: (args) => ({
    components: { AppLink },
    setup() {
      return { args };
    },
    template: `<AppLink v-bind="args"><span style="color: red;">カスタムスロット内容</span></AppLink>`,
  }),
};
