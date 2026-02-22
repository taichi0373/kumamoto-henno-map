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
    @show="onShow"
    @hide="onHide"
  >
    <slot />
    <template #footer>
      <slot name="footer" />
    </template>
  </Dialog>
</template>

<script lang="ts">
import { defineComponent, computed, PropType } from 'vue';
import Dialog from 'primevue/dialog';

export default defineComponent({
  name: "AppDialog",
  components: {
    Dialog,
  },
  props: {
    // 表示フラグ
    modelValue: {
      type: Boolean,
      default: false,
    },
    // ヘッダー
    header: {
      type: String,
      default: "",
    },
    // モーダル
    modal: {
      type: Boolean,
      default: true,
    },
    // 閉じるボタン
    closable: {
      type: Boolean,
      default: true,
    },
    // ドラッグ可
    draggable: {
      type: Boolean,
      default: false,
    },
    // マスククリックで閉じる
    dismissableMask: {
      type: Boolean,
      default: false,
    },
    // スタイル
    dialogStyle: {
      type: [Object, String] as PropType<Record<string, string> | string>,
      required: false,
      default: "",
    },
    // クラス
    dialogClass: {
      type: String,
      default: "",
    },
  },
  emits: [
    /** 変更時 */
    "update:modelValue",
    /** 表示時 */
    "show",
    /** 非表示時 */
    "hide",
    /** 閉じる時 */
    "close",
  ],
  setup(props, context) {
    const computedVisible = computed({
      get: () => props.modelValue,
      set: (value) => {
        context.emit('update:modelValue', value);
      },
    });

    const onShow = () => {
      context.emit('show');
    };

    const onHide = () => {
      context.emit('update:modelValue', false);
      context.emit('hide');
      context.emit('close');
    };

    return {
      computedVisible,
      onShow,
      onHide,
    };
  }
});
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
