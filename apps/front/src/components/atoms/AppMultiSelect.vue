<template>
  <div class="p-field">
    <MultiSelect
      v-model="computedModel"
      :input-id="inputId"
      :options="computedOptions"
      :option-label="optionLabel"
      :option-value="optionValue"
      :option-group-label="optionGroupLabel"
      :option-group-children="optionGroupChildren"
      :placeholder="placeholder"
      :filter="filter"
      :filter-placeholder="filterPlaceholder"
      :scroll-height="scrollHeight + 'px'"
      :virtual-scroller-options="virtualScrollerOptions"
      :reset-filter-on-hide="true"
      :disabled="disabled"
      :class="[inputClass, { error: errorType == 1, warning: errorType == 2 }]"
      :style="inputStyle"
      :tabindex="tabindex"
      :filter-fields="computedFilterFields"
      :max-selected-labels="maxSelectedLabels"
      :selection-limit="selectionLimit"
      selected-items-label="{0}個選択中"
      @filter="onFilterChange"
      @change="onChange"
      @focus="onFocus"
      @blur="onBlur"
    />
    <AppFormError v-if="showError" :error="errors" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import MultiSelect, { MultiSelectChangeEvent, MultiSelectFilterEvent } from 'primevue/multiselect';
import AppFormError from '@/components/atoms/AppFormError.vue';
import { SelectDto } from '@/dto/selectDto';
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';

const props = withDefaults(defineProps<{
  modelValue?: string | number | Object | null;
  options?: Array<SelectDto> | null;
  optionLabel?: string;
  optionValue?: string;
  optionGroupLabel?: string;
  optionGroupChildren?: string;
  placeholder?: string;
  filter?: boolean;
  filterFields?: string[] | undefined;
  filterPlaceholder?: string;
  scrollHeight?: string;
  virtualScrollerOptions?: object | undefined;
  maxSelectedLabels?: number | undefined;
  minOptionsForFilter?: number;
  selectionLimit?: number | undefined;
  showClear?: boolean;
  error?: InputFormErrorDto | InputFormErrorDto[];
  showError?: boolean;
  inputId?: string;
  disabled?: boolean;
  inputStyle?: Record<string, string> | string;
  inputClass?: string;
  tabindex?: string | number | undefined;
}>(), {
  modelValue: () => [],
  options: null,
  optionLabel: "label",
  optionValue: "value",
  optionGroupLabel: undefined,
  optionGroupChildren: undefined,
  placeholder: "",
  filter: false,
  filterFields: undefined,
  filterPlaceholder: "",
  scrollHeight: "200",
  virtualScrollerOptions: undefined,
  maxSelectedLabels: undefined,
  minOptionsForFilter: 10,
  selectionLimit: undefined,
  error: () => [],
  showError: true,
  readonly: false,
  inputId: undefined,
  disabled: false,
  inputStyle: "",
  inputClass: "",
  tabindex: 0,
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number | Object | null): void;
  (e: 'change', event: MultiSelectChangeEvent): void;
  (e: 'filterChange', event: MultiSelectFilterEvent): void;
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

/** フォーカス時の処理 */
const onFocus = (e: Event) => {
  emit('focus', e);
};

/** ブラー時の処理 */
const onBlur = (e: Event) => {
  emit('blur', e);
};

/** 変更時の処理 */
const onChange = (e: MultiSelectChangeEvent) => {
  emit('change', e);
};

/** フィルター変更時の処理 */
const onFilterChange = (e: MultiSelectFilterEvent) => {
  emit('filterChange', e);
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

.p-multiselect {
  width: 100%;
  height: base.$input-height;
}

.p-field :deep(.p-placeholder) {
  color: base.$placeholder-color;
}

.p-field :deep(.p-multiselect.error) {
  @include select-state(base.$error-200, base.$error-100);
}

.p-field :deep(.p-multiselect.warning) {
  @include select-state(base.$warning-200, base.$warning-100);
}
</style>
