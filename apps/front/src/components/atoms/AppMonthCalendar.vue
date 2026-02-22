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

<script lang="ts">
import { defineComponent, computed } from 'vue';
import DatePicker, { DatePickerBlurEvent } from 'primevue/datepicker';
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import AppFormError from '@/components/atoms/AppFormError.vue';

export default defineComponent({
  name: "AppMonthCalendar",
  components: {
    DatePicker,
    AppFormError,
  },
  props: {
    // バインド値
    modelValue: {
      type: Date,
      default: null,
    },
    // プレースホルダー
    placeholder: {
      type: String,
      default: "",
    },
    // 表示フォーマット
    dateFormat: {
      type: String,
      default: "yy/mm",
    },
    // アイコン表示
    showIcon: {
      type: Boolean,
      default: true,
    },
    // エラー情報
    error: {
      type: [Array, Object],
      default: () => [],
    },
    // エラー表示フラグ
    showError: {
      type: Boolean,
      default: true,
    },
    // 入力ID
    inputId: {
      type: String,
      required: false,
      default: undefined,
    },
    // 読み取り専用フラグ
    readonly: {
      type: Boolean,
      default: false,
    },
    // 無効化フラグ
    disabled: {
      type: Boolean,
      default: false,
    },
    // インプットのスタイル
    inputStyle: {
      type: [Object, String],
      required: false,
      default: "",
    },
    // インプットのクラス
    inputClass: {
      type: String,
      required: false,
      default: "",
    },
    // タブインデックス
    tabindex: {
      type: Number,
      required: false,
      default: 0,
    },
  },
  emits: [
    /** 入力時 */
    "input",
    /** 入力時 */
    "update:modelValue",
    /** フォーカス時 */
    "focus",
    /** ブラー時 */
    "blur",
  ],
  setup(props, context) {
    const computedModel = computed({
      get: () => props.modelValue,
      set: (value) => {
        if (value !== props.modelValue) {
          context.emit('update:modelValue', value);
        }
      },
    });
    const onFocus = (e: Event) => {
      context.emit('focus', e);
    };

    const onBlur = (e: DatePickerBlurEvent) => {
      context.emit('blur', e);
    };

    const errors = computed(() => {
      return props.error instanceof Array ? props.error : [props.error];
    }) as any;

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
        type = props.error.type;
      }
      return type;
    });

    const computedTabindex = computed(() => {
      return props.disabled ? -1 : props.tabindex;
    });

    return {
      errors,
      errorType,
      computedModel,
      computedTabindex,
      onFocus,
      onBlur,
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.p-field {
  display: inline-block;
  width: 100%;
}

.calendar-field {
  width: 100%;
}

.calendar-field :deep(.p-inputtext) {
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

.calendar-field.error :deep(.p-inputtext) {
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

.calendar-field.warning :deep(.p-inputtext) {
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
