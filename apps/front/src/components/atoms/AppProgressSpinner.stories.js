import AppProgressSpinner from './AppProgressSpinner.vue';

export default {
  title: 'Design System/Atoms/AppProgressSpinner',
  component: AppProgressSpinner,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    size: {
      control: 'text',
    },
    strokeWidth: {
      control: 'text',
    },
    animationDuration: {
      control: 'text',
    },
  },
};

export const Default = {
  args: {},
};

export const Small = {
  args: {
    size: '30px',
    strokeWidth: '4',
  },
};

export const Large = {
  args: {
    size: '80px',
    strokeWidth: '4',
  },
};

export const Slow = {
  args: {
    animationDuration: '2s',
  },
};

export const AllVariants = {
  render: () => ({
    components: { AppProgressSpinner },
    template: `
      <div style="display: flex; gap: 24px; align-items: center;">
        <div style="text-align: center;">
          <p style="margin-bottom: 8px;">Small</p>
          <AppProgressSpinner size="30px" />
        </div>
        <div style="text-align: center;">
          <p style="margin-bottom: 8px;">Default</p>
          <AppProgressSpinner />
        </div>
        <div style="text-align: center;">
          <p style="margin-bottom: 8px;">Large</p>
          <AppProgressSpinner size="80px" />
        </div>
      </div>
    `,
  }),
};
