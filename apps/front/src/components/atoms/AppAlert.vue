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

<script lang="ts">
import { defineComponent, PropType } from 'vue';
import Message from 'primevue/message';

export default defineComponent({
  name: "AppAlert",
  components: {
    Message,
  },
  props: {
    // メッセージ
    message: {
      type: String,
      required: true,
    },
    // 表示フラグ
    show: {
      type: Boolean,
      default: true,
    },
    // バリアント
    variant: {
      type: String as PropType<'success' | 'info' | 'warn' | 'error'>,
      default: 'info',
    },
    // 閉じるボタンの表示フラグ
    closable: {
      type: Boolean,
      default: true,
    },
    // サイズ
    size: {
      type: String as PropType<'small' | 'medium' | 'large'>,
      default: 'medium',
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
  emits: [
    /** 閉じるボタンクリック時 */
    "close",
  ],
  computed: {
    severity() {
      return this.variant;
    }
  },
  setup(props, context) {
    const onClose = () => {
      context.emit('close');
    };

    return {
      onClose,
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
</style>