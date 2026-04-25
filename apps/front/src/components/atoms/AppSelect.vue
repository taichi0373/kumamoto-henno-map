<template>
  <div class="p-field">
    <Select
      :id="inputId"
      v-model="computedModel"
      :options="computedOptions"
      :optionLabel="optionLabel"
      :optionValue="optionValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :showClear="showClear"
      :filter="filter"
      :filterFields="computedFilterFields"
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
import Select from 'primevue/select';
import { SelectDto } from '@/dto/selectDto';
import type { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import AppFormError from '@/components/atoms/AppFormError.vue';

const props = withDefaults(defineProps<{
  modelValue?: string | number | Object | null;
  options?: Array<SelectDto> | null;
  optionLabel?: string;
  optionValue?: string;
  placeholder?: string;
  filter?: boolean;
  filterFields?: string[] | null;
  showClear?: boolean;
  error?: InputFormErrorDto | InputFormErrorDto[];
  showError?: boolean;
  inputId?: string;
  disabled?: boolean;
  inputStyle?: Record<string, string> | string;
  inputClass?: string;
  tabindex?: string | number | undefined;
}>(), {
  modelValue: null,
  options: null,
  optionLabel: "label",
  optionValue: "value",
  placeholder: "",
  filter: false,
  filterFields: null,
  showClear: true,
  error: () => [],
  showError: true,
  inputId: undefined,
  disabled: false,
  inputStyle: "",
  inputClass: "",
  tabindex: 0,
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number | Object | null): void;
  (e: 'change', event: unknown): void;
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
  
/** オプションが null の場合は空配列に変換 */
const computedOptions = computed<SelectDto[]>(() => {
  return props.options ?? [];
});

/** フィルター対象フィールド */
const computedFilterFields = computed<string[] | undefined>(() => {
  if (!props.filter) {
    return undefined;
  }
  return props.filterFields ?? ['label', 'text', 'value'];
});
</script>
<style lang="scss" scoped>
@use "@/assets/scss/base";

// エラー・警告
@mixin select-state($bg, $border) {
  background-color: $bg;
  border: 1px solid $border;
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

.p-field :deep(.p-select) {
  width: 100%;
  height: base.$input-height;
}

.p-field :deep(.p-select:not(:disabled).p-focus) {
  border-color: base.$base-500;
}

.p-field :deep(.p-select.error) {
  @include select-state(base.$error-200, base.$error-100);
}

.p-field :deep(.p-select.warning) {
  @include select-state(base.$warning-200, base.$warning-100);
}
</style>
