<template>
  <div class="p-field">
    <DatePicker
      :id="inputId"
      v-model="computedModel"
      view="month"
      :disabled="disabled"
      :readonly="readonly"
      :placeholder="placeholder"
      :dateFormat="dateFormat"
      :showIcon="showIcon"
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
import { computed, onMounted } from 'vue';
import DatePicker, { DatePickerBlurEvent } from 'primevue/datepicker';
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import AppFormError from '@/components/atoms/AppFormError.vue';
import { usePrimeVue } from 'primevue';
import { TypeConvertUtils } from '@/utils/typeConvertUtils';

const props = withDefaults(defineProps<{
  modelValue?: string | Date | null;
  placeholder?: string;
  dateFormat?: string;
  showIcon?: boolean;
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
  placeholder: "",
  dateFormat: "yy/mm",
  showIcon: true,
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
  (e: 'update:modelValue', value: string | Date | null): void;
  (e: 'input', value: unknown): void;
  (e: 'focus', event: Event): void;
  (e: 'blur', event: DatePickerBlurEvent): void;
}>();

const computedModel = computed({
  get: () => TypeConvertUtils.toDateFromString(props.modelValue),
  set: (value: Date | null) => {
    if (value !== props.modelValue) {
      emit('update:modelValue', value);
    }
  },
});

/**
 * マウント時の処理
 */
onMounted(() => {
  const primevue = usePrimeVue();
  if (primevue && primevue.config && primevue.config.locale) {
    primevue.config.locale.firstDayOfWeek = 0;
    primevue.config.locale.monthNames = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
    primevue.config.locale.monthNamesShort = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
    primevue.config.locale.dayNamesMin = ['日', '月', '火', '水', '木', '金', '土'];
    primevue.config.locale.dayNamesShort = ['日', '月', '火', '水', '木', '金', '土'];
  }
});

const onFocus = (e: Event) => {
  emit('focus', e);
};

const onBlur = (e: DatePickerBlurEvent) => {
  emit('blur', e);
};

const errors = computed<InputFormErrorDto[]>(() => {
  return props.error instanceof Array ? props.error : [props.error];
});

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

// エラー・警告（入力欄の外側）
@mixin select-state-out($bg, $border) {
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

// エラー・警告（入力欄の内側）
@mixin select-state-in($bg, $border) {
  background-color: $bg;
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

.p-field :deep(.p-datepicker) {
  width: 100%;
  height: base.$input-height;
}

.p-field :deep(.p-inputtext:enabled:focus) {
    border-color: base.$base-500;
}

.p-field :deep(.p-datepicker.error) {
  .p-inputtext, .p-datepicker-dropdown{
    @include select-state-in(base.$error-200, base.$error-100);
  }
  @include select-state-out(base.$error-200, base.$error-100);
}

.p-field :deep(.p-datepicker.warning) {
  .p-inputtext, .p-datepicker-dropdown{
    @include select-state-in(base.$warning-200, base.$warning-100);
  }
  @include select-state-out(base.$warning-200, base.$warning-100);
}
</style>
