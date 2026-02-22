<template>
  <div class="app-form-error">
    <span
      v-for="(e, index) in errors"
      :key="index"
      class="error-message"
      :class="{ error: e.type === 1, warning: e.type === 2 }"
    >
      {{ e.message }}
    </span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { InputFormErrorDto } from '@/dto/InputFormErrorDto';

const props = withDefaults(defineProps<{
  error?: InputFormErrorDto | InputFormErrorDto[];
}>(), {
  error: () => [],
});

const errors = computed(() => {
  return props.error instanceof Array ? props.error : [props.error];
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.app-form-error {
  display: block;
}

.error-message {
  display: block;
  padding-left: 4px;

  &.error {
    color: base.$error-100;
  }

  &.warning {
    color: base.$warning-100;
  }
}
</style>
