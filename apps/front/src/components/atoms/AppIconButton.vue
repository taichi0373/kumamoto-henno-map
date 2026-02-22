<template>
  <PButton
    :icon="icon"
    :type="type"
    :disabled="disabled"
    :loading="loading"
    :severity="severity"
    :outlined="outlined"
    :text="text"
    :rounded="rounded"
    :tabindex="computedTabindex"
    :aria-label="tooltip || ariaLabel"
    :title="tooltip"
    class="app-icon-button"
    :class="[inputClass, `size-${size}`, `shape-${shape}`]"
    :style="inputStyle"
    @click="onClick"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue';
import PButton from 'primevue/button';

const props = withDefaults(defineProps<{
  icon: string;
  type?: 'button' | 'submit' | 'reset';
  disabled?: boolean;
  loading?: boolean;
  severity?: string;
  outlined?: boolean;
  text?: boolean;
  rounded?: boolean;
  tooltip?: string;
  ariaLabel?: string;
  size?: 'small' | 'medium' | 'large' | 'sidebarToggle';
  shape?: 'square' | 'rounded' | 'circle';
  inputStyle?: Record<string, string> | string;
  inputClass?: string;
  tabindex?: number;
}>(), {
  type: 'button',
  disabled: false,
  loading: false,
  severity: 'primary',
  outlined: false,
  text: false,
  rounded: false,
  tooltip: "",
  ariaLabel: "",
  size: 'medium',
  shape: 'square',
  inputStyle: "",
  inputClass: "",
  tabindex: 0,
});

const emit = defineEmits<{
  (e: 'click', event: Event): void;
}>();

const computedTabindex = computed(() => {
  return props.disabled ? -1 : props.tabindex;
});

const onClick = (e: Event) => {
  if (!props.disabled) {
    emit('click', e);
  }
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
</style>