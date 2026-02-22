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

<script setup lang="ts">
import { computed } from 'vue';
import AppTextField from './AppTextField.vue';
import { StringEditUtils } from '@/utils/stringEditUtils';
import { TypeConvertUtils } from '@/utils/typeConvertUtils';

const props = withDefaults(defineProps<{
  modelValue?: string;
  disabled?: boolean;
  readonly?: boolean;
  placeholder?: string;
  inputId?: string;
  halfWidthKana?: boolean;
  inputStyle?: object | string;
  inputClass?: string;
  tabindex?: number;
  maxlength?: number | null;
  rule?: string | null;
  showError?: boolean;
}>(), {
  modelValue: "",
  disabled: false,
  readonly: false,
  placeholder: "",
  inputId: undefined,
  halfWidthKana: false,
  inputStyle: "",
  inputClass: "",
  tabindex: 0,
  maxlength: null,
  rule: null,
  showError: true,
});

const emit = defineEmits<{
  (e: 'input', value: string): void;
  (e: 'update:modelValue', value: string): void;
  (e: 'focus', event: Event): void;
  (e: 'blur', event: Event): void;
}>();

const computedModel = computed({
  get: () => props.modelValue,
  set: (value) => {
    if (value !== props.modelValue) {
      emit('update:modelValue', value);
    }
  },
});

/**
 * 入力値を半角に変換する関数
 * @param value 
 */
const format = (value: string | null) => {
  // 全角ひらがな → 半角カタカナに変換
  let replaceValue = StringEditUtils.replaceFullWidthHiraganaToHalfWidthKatakana(
    TypeConvertUtils.toStringNullToEmpty(value)
  );
  // 全角記号 → 半角記号に変換
  replaceValue = StringEditUtils.replaceFullWidthCharToHalfWidthChar(replaceValue);
  if (props.halfWidthKana) {
    // 半角カタカナ（記号含む）以外を削除
    return replaceValue.replace(/([^ｦ-ﾟ])/g, "");
  } else {
    // 半角英数記号 + 半角カタカナ 以外（＝全角文字など）を削除
    return replaceValue.replace(/([^ -~｡-ﾟ])/g, "");
  }
};

const onFocus = (e: Event) => {
  emit('focus', e);
};

const onBlur = (e: Event) => {
  emit('update:modelValue', format(computedModel.value));
  emit('blur', e);
};

const onInput = (value: string) => {
  emit('update:modelValue', format(value));
  emit('input', format(value));
};
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
