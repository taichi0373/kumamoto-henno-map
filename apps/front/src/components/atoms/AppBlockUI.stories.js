import { ref, onUnmounted } from 'vue';
import AppBlockUI from './AppBlockUI.vue';
import AppButton from './AppButton.vue';

/** body に残った PrimeVue fullScreen オーバーレイを強制削除 */
const removeBlockUIOverlay = () => {
  document.querySelectorAll('.p-blockui-document').forEach((el) => el.remove());
};

export default {
  title: 'Design System/Atoms/AppBlockUI',
  component: AppBlockUI,
  parameters: {
    layout: 'fullscreen',
  },
  tags: ['autodocs'],
  decorators: [
    (story) => ({
      components: { story },
      setup() {
        onUnmounted(() => {
          removeBlockUIOverlay();
        });
      },
      template: '<story />',
    }),
  ],
};

export const Default = {
  render: () => ({
    components: { AppBlockUI, AppButton },
    setup() {
      const blocked = ref(false);
      let timer = null;
      const showLoading = () => {
        blocked.value = true;
        timer = setTimeout(() => {
          blocked.value = false;
        }, 1000);
      };
      onUnmounted(() => {
        clearTimeout(timer);
        blocked.value = false;
      });
      return { blocked, showLoading };
    },
    template: `
      <div style="width: 100%; height: 50vh; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 0;">
        <div style="padding: 48px 40px; background: #fff; border-radius: 12px; box-shadow: 0 4px 24px rgba(0,0,0,0.1); text-align: center; min-width: 320px;">
          <p style="margin: 0 0 8px; font-size: 18px; font-weight: bold; color: #212529;">AppBlockUI</p>
          <p style="margin: 0 0 28px; font-size: 14px; color: #6c757d;">ボタンを押すと1秒間ローディングが表示されます</p>
          <AppButton :label="'ローディングを表示'" :primary="true" :disabled="blocked" @click="showLoading" />
        </div>
        <AppBlockUI :blocked="blocked" />
      </div>
    `,
  }),
};
