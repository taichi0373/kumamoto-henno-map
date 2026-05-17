import { ref } from 'vue';
import AppLicenseModal from './AppLicenseModal.vue';
import AppButton from '../atoms/AppButton.vue';

export default {
  title: 'Design System/Organisms/AppLicenseModal',
  component: AppLicenseModal,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

/** デフォルト（モーダル表示状態） */
export const Default = {
  render: () => ({
    components: { AppLicenseModal, AppButton },
    setup() {
      const visible = ref(true);
      return { visible };
    },
    template: `
      <div style="width: 700px; height: 500px; display: flex; align-items: center; justify-content: center;">
        <AppButton label="出典・ライセンス情報を開く" :primary="true" @click="visible = true" />
        <AppLicenseModal v-model="visible" />
      </div>
    `,
  }),
};

/** 閉じた状態からボタンで開く */
export const WithTriggerButton = {
  render: () => ({
    components: { AppLicenseModal, AppButton },
    setup() {
      const visible = ref(false);
      return { visible };
    },
    template: `
      <div style="width: 600px; height: 200px; display: flex; align-items: center; justify-content: center; border: 1px solid #dee2e6; border-radius: 8px;">
        <AppButton label="出典・ライセンス情報" @click="visible = true" />
        <AppLicenseModal v-model="visible" />
      </div>
    `,
  }),
};
