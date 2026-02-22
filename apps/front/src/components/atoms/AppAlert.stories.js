import AppAlert from './AppAlert.vue';

export default {
  title: 'Design System/Atoms/AppAlert',
  component: AppAlert,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    message: {
      control: 'text',
      description: 'メッセージ',
    },
    variant: {
      control: { type: 'select' },
      options: ['success', 'info', 'warn', 'error'],
      description: 'バリアント（severity）',
    },
    show: {
      control: 'boolean',
      description: '表示・非表示',
    },
    closable: {
      control: 'boolean',
      description: '閉じることができるか',
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large'],
      description: 'サイズ',
    },
    onClose: { action: 'close' },
  },
};

export const Info = {
  args: {
    message: 'こちらは情報メッセージです。',
    variant: 'info',
    show: true,
    closable: true,
    size: 'medium',
  },
};

export const Success = {
  args: {
    message: '操作が正常に完了しました。',
    variant: 'success',
    show: true,
    closable: true,
    size: 'medium',
  },
};

export const Warning = {
  args: {
    message: '注意が必要な状況があります。',
    variant: 'warn',
    show: true,
    closable: true,
    size: 'medium',
  },
};

export const Error = {
  args: {
    message: 'エラーが発生しました。',
    variant: 'error',
    show: true,
    closable: true,
    size: 'medium',
  },
};

export const Small = {
  args: {
    message: '小さいサイズのアラート',
    variant: 'info',
    show: true,
    closable: true,
    size: 'small',
  },
};

export const Large = {
  args: {
    message: '大きいサイズのアラート',
    variant: 'info',
    show: true,
    closable: true,
    size: 'large',
  },
};

export const NotClosable = {
  args: {
    message: '閉じることができないアラート',
    variant: 'info',
    show: true,
    closable: false,
    size: 'medium',
  },
};

export const AllVariants = {
  render: () => ({
    components: { AppAlert },
    template: `
      <div style="display: flex; flex-direction: column; gap: 16px; width: 500px;">
        <AppAlert message="情報メッセージ" variant="info" />
        <AppAlert message="成功メッセージ" variant="success" />
        <AppAlert message="警告メッセージ" variant="warn" />
        <AppAlert message="エラーメッセージ" variant="error" />
      </div>
    `,
  }),
};