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
    :error="error"
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
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';

const props = withDefaults(defineProps<{
  modelValue?: string;
  placeholder?: string;
  error?: InputFormErrorDto | InputFormErrorDto[];
  showError?: boolean;
  inputId?: string;
  readonly?: boolean;
  disabled?: boolean;
  halfWidthKana?: boolean;
  maxlength?: number | null;
  rule?: string | null;
  inputStyle?: Record<string, string> | string;
  inputClass?: string;
  tabindex?: number;
}>(), {
  modelValue: "",
  placeholder: "",
  error: () => [],
  showError: true,
  inputId: undefined,
  readonly: false,
  disabled: false,
  halfWidthKana: false,
  maxlength: null,
  rule: null,
  inputStyle: "",
  inputClass: "",
  tabindex: 0,
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
  (e: 'input', value: string): void;
  (e: 'focus', event: Event): void;
  (e: 'blur', event: Event): void;
}>();

const computedModel = computed({
  get: () => props.modelValue,
  set: (value) => {
    const strValue = typeof value === 'string' ? value : String(value ?? '');
    const formatted = format(strValue);
    if (formatted !== props.modelValue) {
      emit('update:modelValue', formatted);
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

/**
 * 入力イベントハンドラ
 * @param value 入力値
 */
const onInput = (value: unknown) => {
  const strValue = typeof value === 'string' ? value : String(value ?? '');
  emit('input', format(strValue));
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
</style>
