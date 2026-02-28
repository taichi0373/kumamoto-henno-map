<template>
  <div class="p-field">
    <PInputText 
      v-model="model"
      :id="inputId"
      :type="type"
      :disabled="disabled"
      :readonly="readonly"
      :placeholder="placeholder"
      :class="[inputClass, { error: errorType == 1, warning: errorType == 2 }]"
      :style="inputStyle"
      :maxlength="maxlength ?? undefined"
      :tabindex="computedTabindex"
      @input="onInput"
      @focus="onFocus"
      @blur="onBlur"
      @compositionstart="onCompositionStart"
      @compositionend="onCompositionEnd"
    />
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
  maxlength?: number | null;
  rule?: string | null;
  inputStyle?: Record<string, string> | string;
  inputClass?: string;
  tabindex?: number;
}>(), {
  modelValue: null,
  type: "text",
  placeholder: "",
  error: () => [],
  showError: true,
  inputId: undefined,
  readonly: false,
  disabled: false,
  maxlength: null,
  rule: null,
  inputStyle: "",
  inputClass: "",
  tabindex: 0,
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

/** IME入力中フラグ */
const isComposing = ref(false);

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
    model.value = props.modelValue ?? value;
  });
};

/** IME変換開始時の処理 */
const onCompositionStart = () => {
  isComposing.value = true;
};

/** IME変換確定時の処理 */
const onCompositionEnd = (e: CompositionEvent) => {
  isComposing.value = false;
  const target = e.target as HTMLInputElement;
  handleInput(target.value);
};

/** 入力時の処理 */
const onInput = (e: Event) => {
  if (isComposing.value) return;
  const target = e.target as HTMLInputElement;
  handleInput(target.value);
};

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

watch(modelValue, () => {
  if (model.value !== modelValue.value) {
    model.value = modelValue.value;
  }
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

.p-field :deep(.p-inputtext) {
  width: 100%;
  height: base.$input-height;
  border-color: base.$base-400;
  border-radius: 6px;
  padding: 4px 4px 4px 12px;

  &::placeholder {
    color: base.$placeholder-color;
  }
}

.p-field :deep(.p-inputtext:enabled) {
  &:hover {
    background-color: base.$base-100;
    border-color: base.$base-400;
  }

  &:focus {
    background-color: base.$base-100;
    border-color: base.$base-700;
    box-shadow: none;
  }
}

.p-field :deep(.p-inputtext.error) {
  @include select-state(base.$error-200, base.$error-100);
}

.p-field :deep(.p-inputtext.warning) {
  @include select-state(base.$warning-200, base.$warning-100);
}
</style>
