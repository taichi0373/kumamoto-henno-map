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
 * アンマウント時にオーバーレイを除去
 */
onUnmounted(() => {
  document.querySelectorAll('.p-blockui-mask').forEach((el) => el.remove());
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
