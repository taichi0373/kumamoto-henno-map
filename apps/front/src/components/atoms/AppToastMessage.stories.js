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
        ToastMessageUtils.success('保存しました');
      },
      showNotice() {
        ToastMessageUtils.notice('通知があります');
      },
      showWarning() {
        ToastMessageUtils.warning('未来日が入力されています。よろしいですか？');
      },
      showError() {
        ToastMessageUtils.error('日付が入力されていません');
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