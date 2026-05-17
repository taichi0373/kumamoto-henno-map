import { ref } from 'vue';
import AppFeedbackModal from './AppFeedbackModal.vue';
import AppButton from '../atoms/AppButton.vue';

export default {
  title: 'Design System/Organisms/AppFeedbackModal',
  component: AppFeedbackModal,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

/** デフォルト（フォーム入力状態） */
export const Default = {
  render: () => ({
    components: { AppFeedbackModal, AppButton },
    setup() {
      const visible = ref(true);
      return { visible };
    },
    template: `
      <div style="width: 600px; height: 500px; display: flex; align-items: center; justify-content: center;">
        <AppButton label="ご意見・ご要望を開く" :primary="true" @click="visible = true" />
        <AppFeedbackModal v-model="visible" />
      </div>
    `,
  }),
};

/** 閉じた状態からボタンで開く */
export const WithTriggerButton = {
  render: () => ({
    components: { AppFeedbackModal, AppButton },
    setup() {
      const visible = ref(false);
      return { visible };
    },
    template: `
      <div style="width: 600px; height: 200px; display: flex; align-items: center; justify-content: center; border: 1px solid #dee2e6; border-radius: 8px;">
        <AppButton label="ご意見・ご要望" :primary="true" @click="visible = true" />
        <AppFeedbackModal v-model="visible" />
      </div>
    `,
  }),
};
