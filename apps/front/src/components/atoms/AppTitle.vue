<template>
  <h1 class="app-title" :class="[`app-title--${size}`]">
    <i v-if="icon" class="app-title__icon pi" :class="icon"></i>
    <slot></slot>
  </h1>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  /** サイズ */
  size?: 'large' | 'medium' | 'small';
  /** PrimeIconsのアイコンクラス */
  icon?: string;
}>(), {
  size: 'medium',
  icon: '',
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.app-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: base.$text-primary;

  &__icon {
    color: base.$text-primary;
    flex-shrink: 0;
  }

  &--large {
    font-size: base.$title-font-size-large;

    .app-title__icon {
      font-size: calc(base.$title-font-size-large * 1.1);
    }
  }

  &--medium {
    font-size: base.$title-font-size-medium;

    .app-title__icon {
      font-size: base.$title-font-size-medium;
    }
  }

  &--small {
    font-size: base.$title-font-size-small;

    .app-title__icon {
      font-size: base.$title-font-size-small;
    }
  }
}
</style>
