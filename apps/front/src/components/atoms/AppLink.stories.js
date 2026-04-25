import AppLink from './AppLink.vue';

export default {
  title: 'Design System/Atoms/AppLink',
  component: AppLink,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    to: {
      control: 'text',
      description: 'リンク先のURL（/始まりはVueRouter、それ以外はwindow.open）',
    },
    target: {
      control: { type: 'select' },
      options: ['_blank', '_self', '_parent', '_top'],
      description: 'リンクを開くターゲット（外部リンクの場合に使用）',
    },
    tabindex: {
      control: 'number',
      description: 'タブインデックス',
    },
  },
};

export const Default = {
  args: {
    to: '#',
    target: '_blank',
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

export const ExternalLink = {
  args: {
    to: 'https://www.google.com/',
    target: '_blank',
    tabindex: 0,
  },
  render: (args) => ({
    components: { AppLink },
    setup() {
      return { args };
    },
    template: `<AppLink v-bind="args">Google（外部リンク）</AppLink>`,
  }),
};

export const TabIndex2 = {
  args: {
    to: 'https://www.google.com/',
    target: '_blank',
    tabindex: 2,
  },
  render: (args) => ({
    components: { AppLink },
    setup() {
      return { args };
    },
    template: `<AppLink v-bind="args">google.com</AppLink>`,
  }),
};
