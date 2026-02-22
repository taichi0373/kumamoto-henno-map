<template>
  <div class="p-field">
    <DatePicker
      v-if="!readonly"
      v-model="computedModel"
      :dateFormat="format"
      :showIcon="true"
      :disabled="disabled"
      :readonly="readonly"
      :inputId="inputId"
      :class="[inputClass, {error: errorType == 1, warning: errorType == 2}]"
      :style="inputStyle"
      :tabindex="computedTabindex"
      :showTime="false"
      @focus="onFocus"
      @blur="onBlur"
    >
    </DatePicker>
    <AppFormError v-if="showError" :error="errors" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import DatePicker from 'primevue/datepicker';
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import AppFormError from '@/components/atoms/AppFormError.vue';
import { usePrimeVue } from 'primevue';

const props = withDefaults(defineProps<{
  modelValue?: Date | null;
  placeholder?: string;
  format?: string;
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
  placeholder: "日付を選択してください",
  format: "yy/mm/dd",
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

/**
 * 双方向バインディングのための計算プロパティ
 */
const computedModel = computed({
  // DatePickerはDate | undefinedを期待するため、nullをundefinedに変換する
  get: () => props.modelValue ?? undefined,
  set: (value: Date | string | null | undefined) => {
    // DatePickerからはDate以外が渡される場合があるため、Date以外はnullに変換する
    const newValue = value instanceof Date ? value : null;
    if (newValue !== props.modelValue) {
      emit('update:modelValue', newValue);
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
}

.p-datepicker :deep(.p-datepicker-calendar) {
  width: 100%;
  border-color: base.$base-400;
  border-radius: 6px;

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
