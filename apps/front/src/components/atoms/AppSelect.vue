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

<script setup lang="ts">
import { computed } from 'vue';
import Dropdown from 'primevue/dropdown';
import type { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import AppFormError from '@/components/atoms/AppFormError.vue';

interface SelectOption {
  label?: string;
  text?: string;
  value?: string | number | boolean | Record<string, unknown>;
  [key: string]: unknown;
}

const props = withDefaults(defineProps<{
  modelValue?: string | number | Record<string, unknown> | null;
  options?: SelectOption[];
  optionLabel?: string;
  optionValue?: string;
  placeholder?: string;
  filter?: boolean;
  showClear?: boolean;
  error?: InputFormErrorDto | InputFormErrorDto[];
  showError?: boolean;
  inputId?: string;
  readonly?: boolean;
  disabled?: boolean;
  inputStyle?: Record<string, string> | string;
  inputClass?: string;
  tabindex?: number;
}>(), {
  modelValue: null,
  options: () => [],
  optionLabel: "label",
  optionValue: "value",
  placeholder: "",
  filter: false,
  showClear: false,
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
  (e: 'update:modelValue', value: string | number | Record<string, unknown> | null): void;
  (e: 'focus', event: unknown): void;
  (e: 'blur', event: unknown): void;
  (e: 'change', event: unknown): void;
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
const onFocus = (e: Event) => {
  emit('focus', e);
};

/** ブラー時の処理 */
const onBlur = (e: Event) => {
  emit('blur', e);
};

/** 変更時の処理 */
const onChange = (e: unknown) => {
  emit('change', e);
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
