<template>
  <div class="p-field">
    <Password
      :inputId="inputId"
      v-model="computedModel"
      :disabled="disabled"
      :readonly="readonly"
      :placeholder="placeholder"
      :feedback="false"
      :toggleMask="true"
      class="password-field"
      :inputClass="['password-input', inputClass, { error: errorType == 1, warning: errorType == 2 }]"
      :inputStyle="inputStyle"
      :tabindex="computedTabindex"
      @focus="onFocus"
      @blur="onBlur"
    />
    <AppFormError v-if="showError" :error="errors" />
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue';
import Password from 'primevue/password';
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import AppFormError from '@/components/atoms/AppFormError.vue';

export default defineComponent({
  name: "AppPassword",
  components: {
    Password,
    AppFormError,
  },
  props: {
    /** バインド値 */
    modelValue: {
      type: String as () => string | null,
      default: "",
    },
    /** プレースホルダー */
    placeholder: {
      type: String,
      default: "パスワードを入力してください",
    },
    /** エラー情報 */
    error: {
      type: [Array, Object],
      default: () => [],
    },
    /** エラー表示フラグ */
    showError: {
      type: Boolean,
      default: true,
    },
    /** 入力ID */
    inputId: {
      type: String,
      required: false,
      default: undefined,
    },
    /** 読み取り専用フラグ */
    readonly: {
      type: Boolean,
      default: false,
    },
    /** 無効化フラグ */
    disabled: {
      type: Boolean,
      default: false,
    },
    /** インプットのスタイル */
    inputStyle: {
      type: [Object, String],
      required: false,
      default: "",
    },
    /** インプットのクラス */
    inputClass: {
      type: String,
      required: false,
      default: "",
    },
    /** タブインデックス */
    tabindex: {
      type: Number,
      required: false,
      default: 0,
    },
  },
  emits: [
    /** 値の変更時 */
    "update:modelValue",
    /** フォーカス時 */
    "focus",
    /** ブラー時 */
    "blur",
  ],
  setup(props, context) {
    /** 双方向バインディングのための計算プロパティ */
    const computedModel = computed({
      get: () => props.modelValue,
      set: (value) => {
        if (value !== props.modelValue) {
          context.emit('update:modelValue', value);
        }
      },
    });

    /** フォーカス時の処理 */
    const onFocus = (e: Event) => {
      context.emit('focus', e);
    };

    /** ブラー時の処理 */
    const onBlur = (e: Event) => {
      context.emit('blur', e);
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
        type = (props.error as InputFormErrorDto).type;
      }
      return type;
    });

    /** タブインデックス */
    const computedTabindex = computed(() => {
      return props.disabled ? -1 : props.tabindex;
    });

    return {
      computedModel,
      computedTabindex,
      errors,
      errorType,
      onFocus,
      onBlur,
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.p-field {
  display: inline-block;
  width: 100%;

  .password-field {
    width: 100%;
    height: base.$input-height;

    :deep(.password-input) {
      width: 100%;
      border-radius: 6px;
      padding: 4px 2.5rem 4px 12px;

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
        &:hover {
          background-color: base.$base-200;
          border-color: base.$base-200;
        }
      }
      &::placeholder {
        color: base.$placeholder-color;
      }
      &.error {
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
      &.warning {
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
    }
  }
}
</style>
