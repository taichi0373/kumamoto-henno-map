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

<script lang="ts">
import { defineComponent, PropType } from 'vue';
import Card from 'primevue/card';

export default defineComponent({
  name: "AppCard",
  components: {
    Card,
  },
  props: {
    // タイトル
    title: {
      type: String,
      default: "",
    },
    // サブタイトル
    subtitle: {
      type: String,
      default: "",
    },
    // ヘッダー
    header: {
      type: String,
      default: "",
    },
    // インプットスタイル
    inputStyle: {
      type: [Object, String] as PropType<Record<string, string> | string>,
      required: false,
      default: "",
    },
    // インプットクラス
    inputClass: {
      type: String,
      required: false,
      default: "",
    },
  },
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
</style>