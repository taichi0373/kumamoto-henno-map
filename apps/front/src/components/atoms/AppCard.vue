<template>
  <Card 
    class="app-card"
    :class="inputClass"
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

withDefaults(defineProps<{
  title?: string;
  subtitle?: string;
  header?: string;
  inputStyle?: Record<string, string> | string;
  inputClass?: string;
}>(), {
  title: "",
  subtitle: "",
  header: "",
  inputStyle: "",
  inputClass: "",
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
</style>