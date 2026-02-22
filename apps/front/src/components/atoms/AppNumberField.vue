<template>
  <div class="p-field">
    <PInputNumber
      :id="inputId"
      v-model="computedModel"
      :disabled="disabled"
      :readonly="readonly"
      :placeholder="placeholder"
      :min="min"
      :max="max"
      :step="step"
      :useGrouping="useGrouping"
      class="number-field"
      :class="[inputClass, { error: errorType == 1, warning: errorType == 2 }]"
      :style="inputStyle"
      :tabindex="computedTabindex"
      @focus="onFocus"
      @blur="onBlur"
    />
    <AppFormError v-if="showError" :error="errors" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import PInputNumber from 'primevue/inputnumber';
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import AppFormError from '@/components/atoms/AppFormError.vue';

const props = withDefaults(defineProps<{
  modelValue?: number | null;
  placeholder?: string;
  error?: InputFormErrorDto | InputFormErrorDto[];
  showError?: boolean;
  inputId?: string;
  readonly?: boolean;
  disabled?: boolean;
  min?: number;
  max?: number;
  step?: number;
  useGrouping?: boolean;
  inputStyle?: object | string;
  inputClass?: string;
  tabindex?: number;
}>(), {
  modelValue: null,
  placeholder: "",
  error: () => [],
  showError: true,
  inputId: undefined,
  readonly: false,
  disabled: false,
  min: undefined,
  max: undefined,
  step: 1,
  useGrouping: false,
  inputStyle: "",
  inputClass: "",
  tabindex: 0,
});

const emit = defineEmits<{
  (e: 'input', value: unknown): void;
  (e: 'update:modelValue', value: number | null): void;
  (e: 'focus', event: unknown): void;
  (e: 'blur', event: unknown): void;
}>();

const computedModel = computed({
  get: () => props.modelValue,
  set: (value) => {
    if (value !== props.modelValue) {
      emit('update:modelValue', value);
    }
  },
});

/** フォーカス時の処理 */
const onFocus = (e: any) => {
  emit('focus', e);
};

/** ブラー時の処理 */
const onBlur = (e: any) => {
  emit('blur', e);
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

const computedTabindex = computed(() => {
  return props.disabled ? -1 : props.tabindex;
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.p-field {
  display: inline-block;
  width: 100%;
}

.number-field {
  width: 100%;
}

.number-field :deep(.p-inputtext) {
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
}

.number-field.error :deep(.p-inputtext) {
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

.number-field.warning :deep(.p-inputtext) {
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
