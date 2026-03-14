<template>
  <Card
    :class="computedClass"
    :style="inputStyle"
  >
    <template #header v-if="header || $slots.header">
      <slot name="header">{{ header }}</slot>
    </template>
    <template #title v-if="title || $slots.title">
      <slot name="title">{{ title }}</slot>
    </template>
    <template #subtitle v-if="subtitle || $slots.subtitle">
      <slot name="subtitle">{{ subtitle }}</slot>
    </template>
    <template #content>
      <slot></slot>
    </template>
    <template #footer v-if="$slots.footer">
      <slot name="footer"></slot>
    </template>
  </Card>
</template>

<script setup lang="ts">
import Card from 'primevue/card';
import { computed } from 'vue';

const props = withDefaults(defineProps<{
  title?: string;
  subtitle?: string;
  header?: string;
  inputStyle?: Record<string, string> | string;
  inputClass?: string;
  hoverable?: boolean;
}>(), {
  title: "",
  subtitle: "",
  header: "",
  inputStyle: "",
  inputClass: "",
  hoverable: false,
});

const computedClass = computed(() => [
  props.inputClass,
  { 'app-card--hoverable': props.hoverable },
]);
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

:deep(.p-card-title) {
  text-align: center;
}

.app-card--hoverable {
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.16);
    transform: translateY(-1px);
  }
}
</style>