<template>
  <AppTextField
    v-model="computedModel"
    :type="'text'"
    :disabled="disabled"
    :readonly="readonly"
    :placeholder="placeholder"
    :input-id="inputId"
    :input-style="inputStyle"
    :input-class="inputClass"
    :maxlength="maxlength"
    :tabindex="tabindex"
    :rule="rule"
    :show-error="showError"
    @input="onInput"
    @focus="onFocus"
    @blur="onBlur"
  />
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue';
import AppTextField from './AppTextField.vue';
import { StringEditUtils } from '@/utils/stringEditUtils';
import { TypeConvertUtils } from '@/utils/typeConvertUtils';

export default defineComponent({
  name: "AppHalfWidthField",
  components: {
    AppTextField,
  },
  props: {
    // バインド値
    modelValue: {
      type: String,
      default: "",
    },
    // 無効化フラグ
    disabled: {
      type: Boolean,
      default: false,
    },
    // 読み取り専用フラグ
    readonly: {
      type: Boolean,
      default: false,
    },
    // プレースホルダー
    placeholder: {
      type: String,
      default: "",
    },
    // 入力ID
    inputId: {
      type: String,
      required: false,
      default: undefined,
    },
    // 半角カナ入力フラグ
    halfWidthKana: {
      type: Boolean,
      default: false,
    },
    // インプットのスタイル
    inputStyle: {
      type: [Object, String],
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
    // 最大文字数
    maxlength: {
      type: [String, Number],
      required: false,
      default: null,
    },
    // バリデーションルール
    rule: {
      type: String,
      required: false,
      default: null,
    },
    // エラー表示フラグ
    showError: {
      type: Boolean,
      default: true,
    },
  },
  emits: [
    /** 入力時 */
    "input",
    /** 入力時 */
    "update:modelValue",
    /** フォーカス時 */
    "focus",
    /** ブラー時 */
    "blur",
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

    const format = (value: string | null) => {
      let replaceValue = StringEditUtils.replaceFullWidthHiraganaToHalfWidthKatakana(
        TypeConvertUtils.toStringNullToEmpty(value)
      );
      replaceValue = StringEditUtils.replaceFullWidthCharToHalfWidthChar(replaceValue);
      if (props.halfWidthKana) {
        return replaceValue.replace(/[^ -~]/g, "");
      } else {
        return replaceValue.replace(/[^ -~｡-ﾟ]/g, "");
      }
    };

    const onFocus = (e: Event) => {
      context.emit('focus', e);
    };

    const onBlur = (e: Event) => {
      context.emit('blur', e);
    };

    const onInput = (value: string) => {
      context.emit('update:modelValue', format(value));
      context.emit('input', format(value));
    };

    return {
      computedModel,
      onFocus,
      onBlur,
      onInput,
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.p-field {
  display: inline-block;
}

.half-width-field {
  width: 50%;
}

.text-field {
  width: 100%;
  border-color: base.$base-400;
  border-radius: 6px;
  color: #333;
  padding: 4px 4px 4px 12px;

  &:hover {
    background-color: base.$base-100;
    border-color: base.$base-400;
  }
  &:focus {
    background-color: base.$base-100;
    border-color: base.$base-700;
    box-shadow: none;
  }
  &.p-inputtext-disabled, &.p-inputtext-readonly {
    &:hover {
      background-color: base.$base-200;
      border-color: base.$base-200;
    }
    &:focus {
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

@media (max-width: 768px) {
  .half-width-field {
    width: 100%;
  }
}
</style>
