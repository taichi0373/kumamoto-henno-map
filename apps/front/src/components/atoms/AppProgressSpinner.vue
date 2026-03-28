<template>
  <PProgressSpinner
    :style="{ width: size, height: size }"
    :strokeWidth="strokeWidth"
    :animationDuration="animationDuration"
  />
</template>

<script setup lang="ts">
import PProgressSpinner from 'primevue/progressspinner';

withDefaults(defineProps<{
  /** スピナーのサイズ（幅・高さ） */
  size?: string;
  /** 線の太さ */
  strokeWidth?: string;
  /** アニメーション時間 */
  animationDuration?: string;
}>(), {
  size: '50px',
  strokeWidth: '4',
  animationDuration: '.8s',
});
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.p-progressspinner:deep(.p-progressspinner-circle) {
  --p-progressspinner-color-one: #{base.$text-primary};
  --p-progressspinner-color-two: #{base.$text-primary};
  --p-progressspinner-color-three: #{base.$text-primary};
  --p-progressspinner-color-four: #{base.$text-primary};
}

</style>
