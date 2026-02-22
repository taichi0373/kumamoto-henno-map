<template>
  <PButton class="app-link p-button-link" @contextmenu.prevent :tabindex="tabindex" @click="onClick">
    <slot></slot>
  </PButton>
</template>

<script setup lang="ts">
import PButton from 'primevue/button';
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
    color: base.$base-500 !important;
  }
}
</style>