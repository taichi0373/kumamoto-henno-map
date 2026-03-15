import PButton from 'primevue/button';
import AppToastMessage from './AppToastMessage.vue';
import { ToastMessageUtils } from '@/utils/toastMessageUtils';

export default {
  title: 'Design System/Atoms/AppToastMessage',
  component: AppToastMessage,
  parameters: {
    layout: 'fullscreen',
  },
  tags: ['autodocs'],
};

export const Default = {
  render: () => ({
    components: {
      AppToastMessage,
      PButton,
    },
    methods: {
      showSuccess() {
        ToastMessageUtils.success('保存処理が正常に完了しました。');
      },
      showNotice() {
        ToastMessageUtils.notice('新しいお知らせがあります。');
      },
      showWarning() {
        ToastMessageUtils.warning('入力内容を確認してください。');
      },
      showError() {
        ToastMessageUtils.error('エラーが発生しました。もう一度お試しください。');
      },
      clearAll() {
        ToastMessageUtils.remove();
      },
    },
    template: `
      <div style="padding: 20px;">
        <AppToastMessage />
        <div style="display: flex; gap: 12px; flex-wrap: wrap;">
          <PButton @click="showSuccess" label="成功" severity="success" />
          <PButton @click="showNotice" label="お知らせ" severity="info" />
          <PButton @click="showWarning" label="警告" severity="warn" />
          <PButton @click="showError" label="エラー" severity="danger" />
          <PButton @click="clearAll" label="全て削除" severity="secondary" />
        </div>
      </div>
    `,
  }),
};