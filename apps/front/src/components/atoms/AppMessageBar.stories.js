import AppMessageBar from './AppMessageBar.vue';

export default {
  title: 'Design System/Atoms/AppMessageBar',
  component: AppMessageBar,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    message: {
      control: 'text',
      description: 'メッセージ内容',
    },
    mode: {
      control: { type: 'select' },
      options: [undefined, 'warning', 'error', 'success'],
      description: 'メッセージの種別（未指定の場合は通常メッセージ）',
    },
  },
};

export const Default = {
  args: {
    message: 'デフォルトのメッセージです。',
  },
};

export const Warning = {
  args: {
    message: '入力内容を確認してください。',
    mode: 'warning',
  },
};

export const Error = {
  args: {
    message: 'エラーが発生しました。',
    mode: 'error',
  },
};

export const AllVariants = {
  render: () => ({
    components: { AppMessageBar },
    template: `
      <div style="display: flex; flex-direction: column; gap: 16px; width: 400px;">
        <AppMessageBar message="通常のお知らせメッセージです。" />
        <AppMessageBar message="警告メッセージです。" mode="warning" />
        <AppMessageBar message="エラーメッセージです。" mode="error" />
        <AppMessageBar message="成功メッセージです。" mode="success" />
      </div>
    `,
  }),
};
