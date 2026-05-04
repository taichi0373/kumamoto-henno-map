import AppTitle from './AppTitle.vue';

export default {
  title: 'Design System/Atoms/AppTitle',
  component: AppTitle,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    size: {
      control: 'select',
      options: ['large', 'medium', 'small'],
      description: 'フォントサイズ',
    },
    icon: {
      control: 'text',
      description: 'PrimeIconsのアイコンクラス（例: pi-map, pi-star）',
    },
  },
};

export const Default = {
  render: (args) => ({
    components: { AppTitle },
    setup() {
      return { args };
    },
    template: `<AppTitle v-bind="args">タイトルテキスト</AppTitle>`,
  }),
  args: {
    size: 'medium',
    icon: '',
  },
};

export const WithIcon = {
  render: (args) => ({
    components: { AppTitle },
    setup() {
      return { args };
    },
    template: `<AppTitle v-bind="args">特典マップ</AppTitle>`,
  }),
  args: {
    size: 'medium',
    icon: 'pi-map',
  },
};

export const Large = {
  render: (args) => ({
    components: { AppTitle },
    setup() {
      return { args };
    },
    template: `<AppTitle v-bind="args">大きなタイトル</AppTitle>`,
  }),
  args: {
    size: 'large',
    icon: 'pi-star',
  },
};

export const Small = {
  render: (args) => ({
    components: { AppTitle },
    setup() {
      return { args };
    },
    template: `<AppTitle v-bind="args">小さなタイトル</AppTitle>`,
  }),
  args: {
    size: 'small',
    icon: 'pi-info-circle',
  },
};

export const AllSizes = {
  render: () => ({
    components: { AppTitle },
    template: `
      <div style="display: flex; flex-direction: column; gap: 16px; width: 400px;">
        <AppTitle size="large" icon="pi-map">Large タイトル</AppTitle>
        <AppTitle size="medium" icon="pi-star">Medium タイトル</AppTitle>
        <AppTitle size="small" icon="pi-info-circle">Small タイトル</AppTitle>
        <AppTitle size="medium">アイコンなし</AppTitle>
      </div>
    `,
  }),
};
