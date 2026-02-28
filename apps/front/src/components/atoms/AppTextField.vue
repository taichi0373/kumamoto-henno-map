<template>
  <div class="p-field">
    <PInputText :id="inputId" v-model="model" :type="type" :disabled="disabled" :readonly="readonly"
      :placeholder="placeholder" class="text-field"
      :class="[inputClass, { error: errorType == 1, warning: errorType == 2 }]" :style="inputStyle"
      :maxlength="maxlength" :tabindex="computedTabindex" @input="onInput" @focus="onFocus" @blur="onBlur" />
    <AppFormError v-if="showError" :error="errors" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, toRefs, nextTick, watch } from 'vue';
import PInputText from 'primevue/inputtext';
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import AppFormError from '@/components/atoms/AppFormError.vue';
import { ValidateUtils } from '@/utils/validateUtils';

const props = withDefaults(defineProps<{
  modelValue?: string | null;
  type?: string;
  placeholder?: string;
  error?: InputFormErrorDto | InputFormErrorDto[];
  showError?: boolean;
  inputId?: string;
  readonly?: boolean;
  disabled?: boolean;
  inputStyle?: object | string;
  inputClass?: string;
  maxlength?: number | null;
  tabindex?: number;
  rule?: string | null;
}>(), {
  modelValue: null,
  type: "text",
  placeholder: "",
  error: () => [],
  showError: true,
  inputId: undefined,
  readonly: false,
  disabled: false,
  inputStyle: "",
  inputClass: "",
  maxlength: null,
  tabindex: 0,
  rule: null,
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | null): void;
  (e: 'input', value: unknown): void;
  (e: 'focus', event: Event): void;
  (e: 'blur', event: Event): void;
}>();

const inputControl = (input: string | null) => {
  if (input == null || ValidateUtils.isNullOrEmpty(props.rule)) {
    return input;
  }
  return input.replace(new RegExp(`[^${props.rule}]+`, "g"), "");
};

/** 初期値の設定 */
const initValue = inputControl(props.modelValue);
/** 入力値を管理するリアクティブな変数 */
const model = ref(initValue);

if (initValue !== props.modelValue) {
  emit('update:modelValue', initValue);
}
const { modelValue } = toRefs(props);

const handleInput = (targetValue: string) => {
  const value = inputControl(targetValue);
  emit('update:modelValue', value);
  nextTick().then(() => {
    if (props.modelValue == model.value) {
      emit("input", value);
    }
    model.value = value;
  });
};

const onInput = (e: Event) => {
  const target = e.target as HTMLInputElement;
  handleInput(target.value);
};

const onFocus = (e: Event) => {
  emit('focus', e);
};

const onBlur = (e: Event) => {
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

watch(modelValue, () => {
  if (model.value !== modelValue.value) {
    model.value = modelValue.value;
  }
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.p-field {
  display: inline-block;
  width: 100%;

  .text-field {
    width: 100%;
    height: base.$input-height;
    border-color: base.$base-400;
    border-radius: 6px;
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

    &.p-inputtext-disabled,
    &.p-inputtext-readonly {
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

    &.error {
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

    &.warning {
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
  }
}
</style>
