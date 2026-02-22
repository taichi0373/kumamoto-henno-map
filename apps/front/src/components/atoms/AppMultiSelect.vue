<template>
  <div class="p-field">
    <MultiSelect
      v-if="!readonly"
      v-model="computedModel"
      :input-id="inputId"
      :options="options"
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
      class="multi-select-field"
      :class="[inputClass, { error: errorType == 1, warning: errorType == 2 }]"
      :style="inputStyle"
      :tabindex="tabindex"
      :filter-fields="filterFields"
      :max-selected-labels="maxSelectedLabels"
      :selection-limit="selectionLimit"
      selected-items-label="{0}個選択中"
      @filter="onFilterChange"
      @change="onChange"
      @focus="onFocus"
      @blur="onBlur"
    >
        <template v-if="$slots.option" #option="slotProps">
            <slot name="option" v-bind="slotProps || {}"></slot>
            <slot name="optiongroup" v-bind="slotProps || {}"></slot>
        </template>
        <template v-if="$slots.value" #value="slotProps">
            <slot name="value" v-bind="slotProps || {}"></slot>
        </template>
        <template v-if="$slots.header" #header="slotProps">
            <slot name="header" v-bind="slotProps || {}"></slot>
        </template>
        <template v-if="$slots.footer" #footer="slotProps">
            <slot name="footer" v-bind="slotProps || {}"></slot>
        </template>
        <template v-if="$slots.emptyfilter" #emptyfilter>
            <slot name="emptyfilter"></slot>
        </template>
        <template v-if="$slots.empty" #empty>
            <slot name="empty"></slot>
        </template>
        <template v-if="$slots.loader" #loader="slotProps">
            <slot name="loader" v-bind="slotProps || {}"></slot>
        </template>
    </MultiSelect>
    <AppTextField
        v-if="readonly"
        v-model="readonlyValue"
        class="app-multiselect-readonly"
        :class="[inputClass, {error: errorType == 1, warning: errorType == 2}]"
        readonly
        :tabindex="-1"
        :style="inputStyle"
        :input-id="inputId"
    />
    <AppFormError v-if="showError" :error="errors" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import MultiSelect, { MultiSelectChangeEvent, MultiSelectFilterEvent } from 'primevue/multiselect';
import AppFormError from '@/components/atoms/AppFormError.vue';
import AppTextField from './AppTextField.vue';
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';

const props = withDefaults(defineProps<{
  modelValue?: unknown[];
  options?: unknown[] | null;
  optionLabel?: string | ((data: unknown) => string) | null;
  optionValue?: string | ((data: unknown) => unknown) | null;
  optionGroupLabel?: string | ((data: unknown) => string) | null;
  optionGroupChildren?: string | ((data: unknown) => unknown[]) | null;
  tabindex?: number;
  placeholder?: string;
  filter?: boolean;
  filterPlaceholder?: string;
  scrollHeight?: string;
  virtualScrollerOptions?: object | null;
  readonly?: boolean;
  disabled?: boolean;
  error?: InputFormErrorDto | InputFormErrorDto[];
  showError?: boolean;
  inputId?: string;
  filterFields?: string[] | null;
  maxSelectedLabels?: number | null;
  inputStyle?: object | string;
  inputClass?: string;
  minOptionsForFilter?: number;
  selectionLimit?: number | null;
}>(), {
  modelValue: () => [],
  options: null,
  optionLabel: null,
  optionValue: null,
  optionGroupLabel: null,
  optionGroupChildren: null,
  tabindex: 0,
  placeholder: "",
  filter: false,
  filterPlaceholder: "",
  scrollHeight: "200",
  virtualScrollerOptions: null,
  readonly: false,
  disabled: false,
  error: () => [],
  showError: true,
  inputId: undefined,
  filterFields: null,
  maxSelectedLabels: null,
  inputStyle: "",
  inputClass: "",
  minOptionsForFilter: 10,
  selectionLimit: null,
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: unknown[]): void;
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

const computedFilter = computed(() => {
  if (props.minOptionsForFilter == 0) {
    return props.filter;
  }
  const optionCount = Array.isArray(props.options) ? props.options.length : 0;
  return optionCount >= props.minOptionsForFilter ? props.filter : false;
});

/** フィールドの値を解決する関数 */
const resolveField = (data: unknown, field: unknown): unknown => {
  let result: unknown = null;
  if (data && typeof data === 'object' && Object.keys(data as Record<string, unknown>).length && field) {
    if (typeof field === 'function') {
      result = field(data);
    } else if (typeof field === 'string' && field.indexOf('.') === -1) {
      result = (data as Record<string, unknown>)[field];
    } else if (typeof field === 'string') {
      const fields = field.split('.');
      let value: unknown = data;
      for (let i = 0, len = fields.length; i < len; i++) {
        if (value == null || typeof value !== 'object') {
          return null;
        }
        value = (value as Record<string, unknown>)[fields[i]];
      }
      result = value;
    }
  }
  return result;
};

const isObjectLike = (value: unknown): value is Record<string, unknown> => {
  return typeof value === 'object' && value !== null;
};

const readonlyValue = computed(() => {
  const labels: string[] = [];

  let options: unknown[] = [];
  if (Array.isArray(props.options)) {
    if (props.optionGroupLabel) {
      for (const optionGroup of props.options) {
        const children = resolveField(optionGroup, props.optionGroupChildren);
        if (Array.isArray(children)) {
          options = [...options, ...children];
        }
      }
    } else {
      options = props.options;
    }
  }

  if (Array.isArray(props.modelValue) && props.modelValue.length > 0) {
    for (const element of props.modelValue) {
      const val = element;
      for (const option of options) {
        const optionValue = props.optionValue ? resolveField(option, props.optionValue) : option;
        const optionLabel = props.optionLabel ? resolveField(option, props.optionLabel) : JSON.stringify(option);
        const labelText = optionLabel == null ? '' : String(optionLabel);
        if (typeof val === "number") {
          if (Number(val) === Number(optionValue)) {
            if (labelText) {
              labels.push(labelText);
            }
          }
        } else if (
          isObjectLike(val) &&
          isObjectLike(optionValue) &&
          JSON.stringify(Object.entries(val).sort()) === JSON.stringify(Object.entries(optionValue).sort())
        ) {
          if (labelText) {
            labels.push(labelText);
          }
        }
      }
    }
  }
  return labels.join(', ');
});

/** フォーカス時の処理 */
const onFocus = (e: any) => {
  emit('focus', e);
};

/** ブラー時の処理 */
const onBlur = (e: any) => {
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
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.p-field {
  display: inline-block;
  width: 100%;
}

.multi-select-field {
  width: 100%;
  height: base.$input-height;
}

.p-multiselect-panel {
    .p-multiselect-filter.p-inputtext {
        border-color: base.$base-400;
        border-radius: 6px;
        padding: 4px 1.5rem 4px 12px;
        &:focus {
            border-color: base.$base-700;
            box-shadow: none;
        }
    }
    .p-multiselect-items {
        .p-multiselect-item.p-highlight {
            background-color: base.$base-300;
        }

        .p-multiselect-item:not(.p-highlight):not(.p-disabled):hover {
            background-color: base.$base-300;
        }

        .p-multiselect-item,
        .p-multiselect-item.p-highlight,
        .p-multiselect-item:not(.p-highlight):not(.p-disabled):hover,
        .p-multiselect-item:not(.p-highlight):not(.p-disabled).p-focus,
        .p-multiselect-item-group {
            color: base.$base-700;
        }
    }
}
</style>
