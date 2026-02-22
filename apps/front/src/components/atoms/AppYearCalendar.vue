<template>
  <div class="p-field">
    <DatePicker
      :id="inputId"
      v-model="computedModel"
      view="year"
      :disabled="disabled"
      :readonly="readonly"
      :placeholder="placeholder"
      :dateFormat="dateFormat"
      :showIcon="true"
      class="calendar-field"
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
import DatePicker from 'primevue/datepicker';
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import AppFormError from '@/components/atoms/AppFormError.vue';

const props = withDefaults(defineProps<{
  modelValue?: Date | null;
  placeholder?: string;
  dateFormat?: string;
  error?: InputFormErrorDto | InputFormErrorDto[];
  showError?: boolean;
  inputId?: string;
  readonly?: boolean;
  disabled?: boolean;
  inputStyle?: object | string;
  inputClass?: string;
  tabindex?: number;
}>(), {
  modelValue: null,
  placeholder: "",
  dateFormat: "yy",
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
  (e: 'update:modelValue', value: Date | null): void;
  (e: 'focus'): void;
  (e: 'blur'): void;
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
const onFocus = () => {
  emit('focus');
};

/** ブラー時の処理 */
const onBlur = () => {
  emit('blur');
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
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.p-field {
  display: inline-block;
  width: 100%;
}

.p-datepicker {
  width: 100%;
  height: base.$input-height;
}

.p-datepicker :deep(.p-datepicker-calendar) {
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

.p-datepicker :deep(.p-inputtext::placeholder) {
  color: base.$placeholder-color;
}

.p-datepicker.error :deep(.p-datepicker-calendar) {
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

.p-datepicker.warning :deep(.p-datepicker-calendar) {
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
