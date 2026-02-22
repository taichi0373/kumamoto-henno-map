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

<script lang="ts">
import { computed, defineComponent, PropType } from 'vue';
import type { InputFormErrorDto } from '@/dto/InputFormErrorDto';

export default defineComponent({
  name: "AppFormError",
  props: {
    // エラーオブジェクト
    error: {
      type: [Object, Array] as PropType<InputFormErrorDto | InputFormErrorDto[]>,
      default: () => [],
    },
  },
  setup(props) {
    const errors = computed(() => {
      return props.error instanceof Array ? props.error : [props.error];
    });

    return {
      errors,
    };
  },
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
