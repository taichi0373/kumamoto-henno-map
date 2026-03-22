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
  inputStyle?: Record<string, string> | string;
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
  (e: 'update:modelValue', value: number | null): void;
  (e: 'input', value: unknown): void;
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

const computedTabindex = computed(() => {
  return props.disabled ? -1 : props.tabindex;
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

// エラー・警告
@mixin select-state($bg, $border) {
  background-color: $bg;
  border: 1px solid $border;
  border-radius: 6px;

  &:hover {
    background-color: $bg;
    border-color: $border;
  }
  &:focus {
    background-color: $bg;
    border-color: $border;
  }
}

.p-field {
  display: inline-block;
  width: 100%;
}

.p-field :deep(.p-inputnumber) {
  width: 100%;
}

.p-field :deep(.p-inputtext) {
  width: 100%;
  height: base.$input-height;
  border-color: base.$base-400;
  border-radius: 6px;
  padding: 4px 4px 4px 12px;

  &::placeholder {
    color: base.$placeholder-color;
  }

  &:focus {
    background-color: base.$base-100;
    border-color: base.$base-500;
  }
}

.p-field :deep(.p-inputnumber.error .p-inputtext) {
  @include select-state(base.$error-200, base.$error-100);
}

.p-field :deep(.p-inputnumber.warning .p-inputtext) {
  @include select-state(base.$warning-200, base.$warning-100);
}
</style>
