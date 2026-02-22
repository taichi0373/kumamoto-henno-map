<template>
  <PCheckbox
    v-model="computedModel"
    :input-id="inputId"
    :name="name"
    :value="value"
    :binary="binary"
    :disabled="disabled"
    :tabindex="computedTabindex"
    @change="onChange"
  />
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue';
import PCheckbox from 'primevue/checkbox';
import AppFormError from '@/components/atoms/AppFormError.vue';

export default defineComponent({
  name: "AppCheckbox",
  components: {
    PCheckbox,
    AppFormError,
  },
  props: {
    // バインド値
    modelValue: {
      type: [Array, String, Number, Boolean],
      required: true,
    },
    // 入力ID
    inputId: {
      type: String,
      required: false,
      default: undefined,
    },
    // 入力名
    name: {
      type: String,
      required: false,
      default: undefined,
    },
    // 必須表示
    required: {
      type: Boolean,
      default: false,
    },
    // チェック時の値
    value: {
      type: [String, Number],
      default: null,
    },
    // boolean値として扱うかどうか
    binary: {
      type: Boolean,
      default: false,
    },
    // 無効化フラグ
    disabled: {
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
    /** 入力時 */
    "update:modelValue",
    /** 変更時 */
    "change",
  ],
  setup(props, context) {
    const computedModel = computed({
      get: () => {
        if (props.modelValue instanceof Array || (props.binary && typeof props.modelValue === 'boolean')) {
          return props.modelValue;
        } else {
          return [props.modelValue];
        }
      },
      set: (value) => {
        if (value !== props.modelValue) {
          context.emit('update:modelValue', value);
        }
      },
    });

    const onChange = () => {
      context.emit('change');
    };

    const computedTabindex = computed(() => {
      return props.disabled ? -1 : props.tabindex;
    });

    return {
      computedModel,
      computedTabindex,
      onChange,
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.p-checkbox {
  .p-checkbox-box {
    border-color: base.$base-500;
    &.p-highlight {
      border-color: base.$chose-100;
      background-color: base.$chose-100;
    }

    &.p-disabled {
      border-color: base.$base-500;
      background-color: base.$base-300;
      opacity: 0.4;
      &.p-highlight {
        background-color: base.$base-500;
        .p-checkbox-icon {
          color: base.$base-100;
      }
      }
    }
  }
  &:not(.p-checkbox-disabled) {
    :deep(.p-checkbox-box:not(.p-highlight)) {
      &:hover {
        border-color: base.$chose-100;
      }
    }
  }
}
</style>
