<template>
  <Dialog
    v-model:visible="computedVisible"
    :header="header"
    :modal="modal"
    :closable="closable"
    :draggable="draggable"
    :dismissableMask="dismissableMask"
    :style="dialogStyle"
    class="app-dialog"
    :class="dialogClass"
    @show="emit('show')"
    @hide="onHide"
  >
    <slot />
    <template #footer>
      <slot name="footer" />
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import Dialog from 'primevue/dialog';

const props = withDefaults(defineProps<{
  /** 表示フラグ */
  modelValue?: boolean;
  /** ヘッダー */
  header?: string;
  /** モーダル */
  modal?: boolean;
  /** 閉じるボタン */
  closable?: boolean;
  /** ドラッグ可 */
  draggable?: boolean;
  /** マスククリックで閉じる */
  dismissableMask?: boolean;
  /** スタイル */
  dialogStyle?: Record<string, string> | string;
  /** クラス */
  dialogClass?: string;
}>(), {
  modelValue: false,
  header: '',
  modal: true,
  closable: true,
  draggable: false,
  dismissableMask: false,
  dialogStyle: '',
  dialogClass: '',
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'show'): void;
  (e: 'hide'): void;
  (e: 'close'): void;
}>();

const computedVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
});

const onHide = () => {
  emit('update:modelValue', false);
  emit('hide');
  emit('close');
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.app-dialog :deep(.p-dialog) {
  border-radius: 12px;
}

.app-dialog :deep(.p-dialog-header) {
  background-color: base.$base-700;
  color: base.$base-100;
}

.app-dialog :deep(.p-dialog-content) {
  background-color: base.$base-100;
  color: base.$base-600;
}

.app-dialog :deep(.p-dialog-footer) {
  background-color: base.$base-200;
}
</style>
