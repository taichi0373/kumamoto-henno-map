<template>
  <pButton class="app-link p-button-link" oncontextmenu="return false;" :tabindex="tabindex" @click="onClick">
    <slot></slot>
  </pButton>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: "AppLink",
  props: {
    /** タブインデックス */
    tabindex: {
      type: Number,
      required: false,
      default: 0,
    },
  },
  emits: [
    /** クリック時 */
    "click",
  ],
  setup(props, context) {
    const onClick = (e: Event) => {
      context.emit('click', e);
    };

    return {
      onClick,
    };
  }
});
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