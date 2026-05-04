<template>
  <PButton class="app-link p-button-link" @contextmenu.prevent :tabindex="tabindex" @click="onClick">
    <slot></slot>
  </PButton>
</template>

<script setup lang="ts">
import PButton from 'primevue/button';
import { useRouter } from 'vue-router';

const props = withDefaults(defineProps<{
  to?: string;
  target?: string;
  tabindex?: number;
}>(), {
  to: '',
  target: '_blank',
  tabindex: 0,
});

const emit = defineEmits<{
  (e: 'click', event: Event): void;
}>();

const router = useRouter();

const onClick = (e: Event) => {
  emit('click', e);
  if (!props.to) return;
  if (props.to.startsWith('/')) {
    router.push(props.to);
  } else {
    window.open(props.to, props.target, 'noopener,noreferrer');
  }
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.p-button-link {
  text-align: left;
  padding: 2px;
  color: base.$text-primary;
  text-decoration: underline;
  align-items: baseline;
  &:hover {
    color: base.$text-secondary !important;
  }
}
</style>