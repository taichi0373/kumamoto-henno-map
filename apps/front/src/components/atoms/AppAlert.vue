<template>
  <Message
    v-show="show"
    :severity="severity"
    :closable="closable"
    class="app-alert"
    :class="[inputClass, `size-${size}`]"
    :style="inputStyle"
    @close="onClose"
  >
    <template #default>
      <span>{{ message }}</span>
    </template>
  </Message>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import Message from 'primevue/message';

const props = withDefaults(defineProps<{
  message: string;
  show?: boolean;
  variant?: 'success' | 'info' | 'warn' | 'error';
  closable?: boolean;
  size?: 'small' | 'medium' | 'large';
  inputStyle?: Record<string, string> | string;
  inputClass?: string;
}>(), {
  show: true,
  variant: 'info',
  closable: true,
  size: 'medium',
  inputStyle: "",
  inputClass: "",
});

const emit = defineEmits<{
  (e: 'close'): void;
}>();

const severity = computed(() => props.variant);

const onClose = () => {
  emit('close');
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
</style>