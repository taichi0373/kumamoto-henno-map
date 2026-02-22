import AppButton from './AppButton.vue';

export default {
  title: 'Design System/Atoms/AppButton',
  component: AppButton,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    label: {
      control: 'text',
    },
    icon: {
      control: 'text',
    },
    primary: {
      control: 'boolean',
    },
    disabled: {
      control: 'boolean',
    },
    ellipsis: {
      control: 'boolean',
    },
    loading: {
      control: 'boolean',
    },
    tabindex: {
      control: 'number',
    },
    onClick: { action: 'clicked' },
  },
};

export const Primary = {
  args: {
    primary: true,
    label: 'プライマリボタン',
  },
};

export const Secondary = {
  args: {
    primary: false,
    label: 'セカンダリボタン',
  },
};

export const WithIcon = {
  args: {
    primary: true,
    icon: 'pi pi-check',
    label: 'アイコン付きボタン',
  },
};

export const Disabled = {
  args: {
    label: '無効化ボタン',
    disabled: true,
  },
};

export const Loading = {
  args: {
    label: '読み込み中',
    loading: true,
  },
};

export const Ellipsis = {
  args: {
    label: '省略表示ボタン',
    ellipsis: true,
  },
};

export const WithTabindex = {
  args: {
    label: 'タブインデックス設定',
    tabindex: 5,
  },
};

export const AllVariants = {
  render: () => ({
    components: { AppButton },
    template: `
      <div style="display: flex; flex-direction: column; gap: 16px; width: 420px;">
        <div style="display: flex; gap: 8px; flex-wrap: wrap;">
          <AppButton :primary="true" label="Primary" />
          <AppButton :primary="false" label="Secondary" />
          <AppButton icon="pi pi-check" label="With Icon" />
          <AppButton :disabled="true" label="Disabled" />
          <AppButton :loading="true" label="Loading" />
          <AppButton :ellipsis="true" label="Ellipsis Button" />
        </div>
      </div>
    `,
  }),
};
