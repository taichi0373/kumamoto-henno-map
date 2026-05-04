import AppMenubar from './AppMenubar.vue';

export default {
  title: 'Design System/Atoms/AppMenubar',
  component: AppMenubar,
  parameters: {
    layout: 'fullscreen',
  },
  tags: ['autodocs'],
};

export const Default = {
  render: () => ({
    components: { AppMenubar },
    template: '<AppMenubar />',
  }),
};
