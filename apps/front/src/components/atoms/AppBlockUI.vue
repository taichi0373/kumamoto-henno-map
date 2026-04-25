<template>
  <Teleport to="body">
    <div v-if="blocked" class="blockui-overlay">
      <AppProgressSpinner size="80px" />
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import AppProgressSpinner from '@/components/atoms/AppProgressSpinner.vue';

/** ローディング中かどうか */
defineProps<{
  blocked: boolean;
}>();
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.blockui-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
