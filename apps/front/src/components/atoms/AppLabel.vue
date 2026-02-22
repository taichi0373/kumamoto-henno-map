<template>
  <label :for="id" class="app-label" @click="onClick">
    <span v-if="required" class="asterisk">*</span>
    <slot></slot>
  </label>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: "AppLabel",
  props: {
    // リンクされる入力フィールドのID
    id: {
      type: String,
      default: "",
    },
    // 必須項目かどうか
    required: {
      type: Boolean,
      default: false,
    },
  },
  emits: [
    /** クリック時 */
    "click",
  ],
  setup(props, context) {
    /** クリック時 */
    const onClick = () => {
      context.emit("click");
    };

    return {
      onClick,
    };
  },
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.app-label {
  display: inline-block;
  padding-left: 12px;
}
</style>