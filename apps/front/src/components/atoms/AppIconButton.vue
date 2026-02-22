<template>
  <PButton
    :icon="icon"
    :type="type"
    :disabled="disabled"
    :loading="loading"
    :severity="severity"
    :outlined="outlined"
    :text="text"
    :rounded="rounded"
    :tabindex="computedTabindex"
    :aria-label="tooltip || ariaLabel"
    :title="tooltip"
    class="app-icon-button"
    :class="[inputClass, `size-${size}`, `shape-${shape}`]"
    :style="inputStyle"
    @click="onClick"
  />
</template>

<script lang="ts">
import { defineComponent, computed, PropType } from 'vue';
import PButton from 'primevue/button';

export default defineComponent({
  name: "AppIconButton",
  components: {
    PButton,
  },
  props: {
    // アイコン
    icon: {
      type: String,
      required: true,
    },
    // タイプ
    type: {
      type: String as PropType<'button' | 'submit' | 'reset'>,
      default: 'button',
    },
    // 無効化
    disabled: {
      type: Boolean,
      default: false,
    },
    // ローディング
    loading: {
      type: Boolean,
      default: false,
    },
    // バリアント
    severity: {
      type: String,
      default: 'primary',
    },
    // アウトライン
    outlined: {
      type: Boolean,
      default: false,
    },
    // テキストボタン
    text: {
      type: Boolean,
      default: false,
    },
    // 丸いボタン
    rounded: {
      type: Boolean,
      default: false,
    },
    // ツールチップ
    tooltip: {
      type: String,
      default: "",
    },
    // アクセシビリティ用ラベル
    ariaLabel: {
      type: String,
      default: "",
    },
    // サイズ
    size: {
      type: String as PropType<'small' | 'medium' | 'large' | 'sidebarToggle'>,
      default: 'medium',
    },
    // 形状
    shape: {
      type: String as PropType<'square' | 'rounded' | 'circle'>,
      default: 'square',
    },
    // インプットのスタイル
    inputStyle: {
      type: [Object, String] as PropType<Record<string, string> | string>,
      required: false,
      default: "",
    },
    // インプットのクラス
    inputClass: {
      type: String,
      required: false,
      default: "",
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
    const computedTabindex = computed(() => {
      return props.disabled ? -1 : props.tabindex;
    });

    const onClick = (e: Event) => {
      if (!props.disabled) {
        context.emit('click', e);
      }
    };

    return {
      computedTabindex,
      onClick,
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
</style>