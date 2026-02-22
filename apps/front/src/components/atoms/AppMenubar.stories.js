import AppMenubar from './AppMenubar.vue';

export default {
  title: 'Atoms/AppMenubar',
  component: AppMenubar,
};

const Template = (args) => ({
  components: { AppMenubar },
  setup() {
    return { args };
  },
  template: '<AppMenubar v-bind="args" />',
});

export const Default = Template.bind({});
Default.args = {
  // メニュー項目の例
  items: [
    { label: 'ホーム', icon: 'pi pi-fw pi-home', to: '/' },
    { label: '特典', icon: 'pi pi-fw pi-star', to: '/benefits' },
    { label: 'お問い合わせ', icon: 'pi pi-fw pi-envelope', to: '/contact' },
  ],
};