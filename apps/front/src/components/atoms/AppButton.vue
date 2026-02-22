<template>
  <PButton
    :class="classes"
    class="app-button p-button-text"
    :label="modelValue"
    :icon="icon"
    :disabled="disabled"
    :loading="loading"
    :tabindex="tabindex"
    @click="onClick"
  />
</template>

<script lang="ts">
import { defineComponent, computed, reactive } from 'vue';
import PButton from 'primevue/button';

export default defineComponent({
  name: "AppButton",
  components: {
    PButton,
  },
  props: {
    // アイコン
    icon: {
      type: String,
      default: "",
    },
    // ラベル
    label: {
      type: String,
      required: true,
    },
    // プライマリー表示フラグ
    primary: {
      type: Boolean,
      default: false,
    },
    // 無効化フラグ
    disabled: {
      type: Boolean,
      default: false,
    },
    // 省略表示フラグ
    ellipsis: {
      type: Boolean,
      default: false,
    },
    // ローディングフラグ
    loading: {
      type: Boolean,
      default: false,
    },
    // タブインデックス
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
    props = reactive(props);

    const modelValue = computed(() => {
      if (props.ellipsis) return `${props.label}...`;
      return props.label;
    });

    const classes = computed(() => ({
      "p-app-button-primary": props.primary,
    }));

    const onClick = () => {
      context.emit('click');
    };

    return {
      modelValue,
      classes,
      onClick,
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.p-button.app-button {
  background-color: base.$base-100;
  color: base.$base-700;
  border: 1px solid;
  border-color: base.$base-400;
  font-size: base.$fontsize-button;
  font-weight: bold;
  :deep(.pi.p-button-icon) {
    font-size: base.$fontsize-button;
  }
  &:hover,
  &:active {
    background-color: base.$base-300;
    color: base.$base-700;
    border: 1px solid;
    border-color: base.$base-400;
  }
  &:focus {
    outline: 0 none;
    outline-offset: 0;
    box-shadow: 0 0 0 0.2rem base.$forcus-color;
  }
  &.p-app-button-primary {
    background-color: base.$base-700;
    color: base.$base-100;
    &:hover,
    &:active {
      background-color: base.$base-400;
      color: base.$base-100;
    }
  }
}
</style>
