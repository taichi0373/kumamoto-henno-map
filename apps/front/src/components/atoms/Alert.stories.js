import Alert from './Alert.vue';

export default {
  title: 'Design System/Atoms/Alert',
  component: Alert,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    message: {
      control: 'text',
    },
    title: {
      control: 'text',
    },
    variant: {
      control: { type: 'select' },
      options: ['success', 'info', 'warning', 'error'],
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large'],
    },
    dismissible: {
      control: 'boolean',
    },
    show: {
      control: 'boolean',
    },
    onDismiss: { action: 'dismissed' },
  },
};

export const Default = {
  args: {
    message: 'これはデフォルトのアラートメッセージです。',
  },
};

export const Success = {
  args: {
    variant: 'success',
    message: '操作が正常に完了しました。',
  },
};

export const Info = {
  args: {
    variant: 'info',
    message: '追加情報があります。',
  },
};

export const Warning = {
  args: {
    variant: 'warning',
    message: '注意が必要な操作です。',
  },
};

export const Error = {
  args: {
    variant: 'error',
    message: 'エラーが発生しました。',
  },
};

export const WithTitle = {
  args: {
    title: '重要なお知らせ',
    message: 'タイトル付きのアラートメッセージです。',
    variant: 'info',
  },
};

export const Dismissible = {
  args: {
    message: '閉じることができるアラートです。',
    dismissible: true,
    variant: 'warning',
  },
};

export const Sizes = {
  render: () => ({
    components: { Alert },
    template: `
      <div style="display: flex; flex-direction: column; gap: 16px; width: 500px;">
        <Alert size="small" variant="info" message="小サイズのアラート" />
        <Alert size="medium" variant="info" message="中サイズのアラート" />
        <Alert size="large" variant="info" message="大サイズのアラート" />
      </div>
    `,
  }),
};

export const AllVariants = {
  render: () => ({
    components: { Alert },
    template: `
      <div style="display: flex; flex-direction: column; gap: 20px; width: 600px;">
        <h3 style="margin: 0; color: #333;">バリアント別表示</h3>
        <Alert variant="success" message="正常に処理が完了しました。" />
        <Alert variant="info" message="詳細情報を確認してください。" />
        <Alert variant="warning" message="この操作には注意が必要です。" />
        <Alert variant="error" message="エラーが発生しました。" />
        
        <h3 style="margin: 0; color: #333;">タイトル付き & 閉じる機能付き</h3>
        <Alert 
          variant="success" 
          title="成功" 
          message="ファイルのアップロードが完了しました。" 
          :dismissible="true" 
        />
        <Alert 
          variant="warning" 
          title="警告" 
          message="この操作は元に戻すことができません。" 
          :dismissible="true" 
        />
        
        <h3 style="margin: 0; color: #333;">サイズ比較</h3>
        <Alert size="small" variant="info" message="小サイズ" />
        <Alert size="medium" variant="info" message="中サイズ（デフォルト）" />
        <Alert size="large" variant="info" message="大サイズ" />
      </div>
    `,
  }),
};