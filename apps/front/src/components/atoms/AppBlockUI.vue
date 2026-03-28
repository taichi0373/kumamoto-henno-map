<template>
  <PBlockUI :blocked="blocked" :full-screen="true">
    <div v-if="blocked" class="spinner-wrapper">
      <AppProgressSpinner size="80px" />
    </div>
  </PBlockUI>
</template>

<script setup lang="ts">
import { onUnmounted } from 'vue';
import PBlockUI from 'primevue/blockui';
import AppProgressSpinner from '@/components/atoms/AppProgressSpinner.vue';

/** ローディング中かどうか */
defineProps<{
  blocked: boolean;
}>();

/**
 * アンマウント時に full-screen モードで body 直下に残留した
 * PrimeVue オーバーレイ (.p-blockui-document) を確実に除去する。
 * ページ遷移などで blocked=true のままコンポーネントが破棄された場合でも
 * 画面操作不能状態が残らないようにする。
 */
onUnmounted(() => {
  document.querySelectorAll('.p-blockui-document').forEach((el) => el.remove());
});
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

// ローディングアイコン
.spinner-wrapper {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 9999;
}
</style>
