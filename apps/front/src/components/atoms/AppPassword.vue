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

<script setup lang="ts">
import { computed } from 'vue';
import Password from 'primevue/password';
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import AppFormError from '@/components/atoms/AppFormError.vue';

const props = withDefaults(defineProps<{
  modelValue?: string | null;
  placeholder?: string;
  error?: InputFormErrorDto | InputFormErrorDto[];
  showError?: boolean;
  inputId?: string;
  readonly?: boolean;
  disabled?: boolean;
  inputStyle?: object | string;
  inputClass?: string;
  tabindex?: number;
}>(), {
  modelValue: "",
  placeholder: "パスワードを入力してください",
  error: () => [],
  showError: true,
  inputId: undefined,
  readonly: false,
  disabled: false,
  inputStyle: "",
  inputClass: "",
  tabindex: 0,
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | null): void;
  (e: 'focus', event: Event): void;
  (e: 'blur', event: Event): void;
}>();

/** 双方向バインディングのための計算プロパティ */
const computedModel = computed({
  get: () => props.modelValue,
  set: (value) => {
    if (value !== props.modelValue) {
      emit('update:modelValue', value);
    }
  },
});

/** フォーカス時の処理 */
const onFocus = (e: Event) => {
  emit('focus', e);
};

/** ブラー時の処理 */
const onBlur = (e: Event) => {
  emit('blur', e);
};

/** エラー情報 */
const errors = computed<InputFormErrorDto[]>(() => {
  return props.error instanceof Array ? props.error : [props.error];
});

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
