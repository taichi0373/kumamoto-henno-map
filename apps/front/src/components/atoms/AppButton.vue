<template>
  <PButton
    :class="classes"
    class="app-button p-button-text"
    :label="modelValue"
    :icon="icon"
    :disabled="disabled"
    :loading="loading"
    :tabindex="tabindex"
    @click="onClick"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue';
import PButton from 'primevue/button';

const props = withDefaults(defineProps<{
  icon?: string;
  label: string;
  primary?: boolean;
  disabled?: boolean;
  ellipsis?: boolean;
  loading?: boolean;
  tabindex?: number;
}>(), {
  icon: "",
  primary: false,
  disabled: false,
  ellipsis: false,
  loading: false,
  tabindex: 0,
});

const emit = defineEmits<{
  (e: 'click'): void;
}>();

const modelValue = computed(() => {
  if (props.ellipsis) return `${props.label}...`;
  return props.label;
});

const classes = computed(() => ({
  "p-app-button-primary": props.primary,
}));

const onClick = () => {
  emit('click');
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.p-button.app-button {
  background-color: base.$base-100;
  color: base.$base-700;
  border: 1px solid;
  border-color: base.$base-400;
  font-size: base.$fontsize-button;
  font-weight: bold;
  :deep(.pi.p-button-icon) {
    font-size: base.$fontsize-button;
  }
  &:hover,
  &:active {
    background-color: base.$base-300;
    color: base.$base-700;
    border: 1px solid;
    border-color: base.$base-400;
  }
  &:focus {
    outline: 0 none;
    outline-offset: 0;
  }
  &.p-app-button-primary {
    background-color: base.$base-700;
    color: base.$base-100;
    &:hover,
    &:active {
      background-color: base.$base-600;
      color: base.$base-100;
    }
  }
}
</style>
