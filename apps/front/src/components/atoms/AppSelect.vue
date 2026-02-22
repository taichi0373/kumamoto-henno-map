<template>
  <div class="p-field">
    <Dropdown
      :id="inputId"
      v-model="computedModel"
      :options="options"
      :optionLabel="optionLabel"
      :optionValue="optionValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :readonly="readonly"
      :showClear="showClear"
      :filter="filter"
      class="select-field"
      :class="[inputClass, { error: errorType == 1, warning: errorType == 2 }]"
      :style="inputStyle"
      :tabindex="computedTabindex"
      @focus="onFocus"
      @blur="onBlur"
      @change="onChange"
    />
    <AppFormError v-if="showError" :error="errors" />
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, PropType } from 'vue';
import Dropdown from 'primevue/dropdown';
import type { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import AppFormError from '@/components/atoms/AppFormError.vue';

interface SelectOption {
  label?: string;
  text?: string;
  value?: string | number | boolean | Record<string, unknown>;
  [key: string]: unknown;
}

export default defineComponent({
  name: "AppSelect",
  components: {
    Dropdown,
    AppFormError,
  },
  props: {
    // バインド値
    modelValue: {
      type: [String, Number, Object] as PropType<string | number | Record<string, unknown> | null>,
      default: null,
    },
    // オプション
    options: {
      type: Array as PropType<SelectOption[]>,
      default: () => [],
    },
    // 表示ラベルキー
    optionLabel: {
      type: String,
      default: "label",
    },
    // 値キー
    optionValue: {
      type: String,
      default: "value",
    },
    // プレースホルダー
    placeholder: {
      type: String,
      default: "",
    },
    // フィルタ表示
    filter: {
      type: Boolean,
      default: false,
    },
    // クリアボタン
    showClear: {
      type: Boolean,
      default: false,
    },
    // エラー情報
    error: {
      type: [Array, Object],
      default: () => [],
    },
    // エラー表示フラグ
    showError: {
      type: Boolean,
      default: true,
    },
    // 入力ID
    inputId: {
      type: String,
      required: false,
      default: undefined,
    },
    // 読み取り専用フラグ
    readonly: {
      type: Boolean,
      default: false,
    },
    // 無効化フラグ
    disabled: {
      type: Boolean,
      default: false,
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
    /** 入力時 */
    "update:modelValue",
    /** フォーカス時 */
    "focus",
    /** ブラー時 */
    "blur",
    /** 変更時 */
    "change",
  ],
  setup(props, context) {
    const computedModel = computed({
      get: () => props.modelValue,
      set: (value) => {
        if (value !== props.modelValue) {
          context.emit('update:modelValue', value);
        }
      },
    });


    /** フォーカス時の処理 */
    const onFocus = (e: any) => {
      context.emit('focus', e);
    };

    /** ブラー時の処理 */
    const onBlur = (e: any) => {
      context.emit('blur', e);
    };

    /** 変更時の処理 */
    const onChange = (e: unknown) => {
      context.emit('change', e);
    };

    /** エラー情報 */
    const errors = computed(() => {
      return props.error instanceof Array ? props.error : [props.error];
    }) as any;

    /** エラータイプ */
    const errorType = computed(() => {
      let type = Number.MAX_VALUE;
      if (props.error instanceof Array) {
        props.error.forEach((err) => {
          const errorType = (err as InputFormErrorDto).type;
          if (errorType != 0) {
            type = Math.min(type, errorType);
          }
        });
      } else {
        type = props.error.type;
      }
      return type;
    });

    const computedTabindex = computed(() => {
      return props.disabled ? -1 : props.tabindex;
    });

    return {
      errors,
      errorType,
      computedModel,
      computedTabindex,
      onFocus,
      onBlur,
      onChange,
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.p-field {
  display: inline-block;
  width: 100%;
}

.select-field {
  width: 100%;
  height: base.$input-height;
}

.select-field :deep(.p-dropdown) {
  width: 100%;
  border-color: base.$base-400;
  border-radius: 6px;
  color: #333;

  &:hover {
    background-color: base.$base-100;
    border-color: base.$base-400;
  }

  &:focus {
    background-color: base.$base-100;
    border-color: base.$base-700;
    box-shadow: none;
  }

  &.p-disabled {
    background-color: base.$base-200;
    border-color: base.$base-200;
  }
}

.select-field :deep(.p-inputtext::placeholder) {
  color: base.$placeholder-color;
}

.select-field.error :deep(.p-dropdown) {
  background-color: base.$error-200;
  border: 1px solid base.$error-100;

  &:hover {
    background-color: base.$error-200;
    border-color: base.$error-100;
  }

  &:focus {
    background-color: base.$error-200;
    border-color: base.$error-100;
  }
}

.select-field.warning :deep(.p-dropdown) {
  background-color: base.$warning-200;
  border: 1px solid base.$warning-100;

  &:hover {
    background-color: base.$warning-200;
    border-color: base.$warning-100;
  }

  &:focus {
    background-color: base.$warning-200;
    border-color: base.$warning-100;
  }
}
</style>
