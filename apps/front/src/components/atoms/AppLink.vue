<template>
  <pButton class="app-link p-button-link" oncontextmenu="return false;" :tabindex="tabindex" @click="onClick">
    <slot></slot>
  </pButton>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  tabindex?: number;
}>(), {
  tabindex: 0,
});

const emit = defineEmits<{
  (e: 'click', event: Event): void;
}>();

const onClick = (e: Event) => {
  emit('click', e);
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.p-button-link {
  text-align: left;
  padding: 2px;
  color: base.$base-700;
  text-decoration: underline;
  align-items: baseline;
  &:hover {
    color: base.$base-400 !important;
  }
  &:focus {
    box-shadow: 0 0 0 0.2rem base.$base-400 !important;
  }
}
</style>