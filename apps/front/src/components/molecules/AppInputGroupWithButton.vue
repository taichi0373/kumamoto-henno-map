<template>
  <div class="p-field">
    <InputGroup class="text-field-with-button">
    <AppTextField
        v-model="computedModel"
        :input-id="inputId"
        :placeholder="placeholder"
        :type="type"
        :maxlength="maxlength"
        :disabled="disabled"
        :readonly="readonly"
        :class="inputClass"
        :style="inputStyle"
        :tabindex="tabindex ?? undefined"
        :show-error="showError"
        :error="error"
        @focus="onFocus"
        @blur="onBlur"
        @input="onInput"
        @keydown="onKeydown"
        />
      <AppButton
        :primary="buttonPrimary"
        :label="buttonLabel"
        :icon="buttonIcon"
        :disabled="buttonDisabled"
        :loading="buttonLoading"
        :class="buttonClass"
        :tabindex="computedTabindex"
        @click="onButtonClick"
      />
    </InputGroup>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import InputGroup from 'primevue/inputgroup';
import AppTextField from '@/components/atoms/AppTextField.vue';
import AppButton from '@/components/atoms/AppButton.vue';
import type { InputFormErrorDto } from '@/dto/InputFormErrorDto';

const props = withDefaults(defineProps<{
  modelValue?: string | null;
  type?: string;
  placeholder?: string;
  showError?: boolean;
  error?: InputFormErrorDto | InputFormErrorDto[];
  disabled?: boolean;
  readonly?: boolean;
  inputId?: string;
  maxlength?: number | null;
  tabindex?: number | null;
  errorType?: number;
  inputClass?: string;
  inputStyle?: Record<string, string> | string;
  required?: boolean;
  buttonPrimary?: boolean;
  buttonIcon: string;
  buttonLabel?: string;
  buttonDisabled?: boolean;
  buttonLoading?: boolean;
  buttonClass?: string;
}>(), {
  modelValue: "",
  type: "text",
  placeholder: "",
  showError: true,
  error: () => [],
  disabled: false,
  readonly: false,
  inputId: "",
  maxlength: null,
  tabindex: null,
  errorType: 0,
  inputClass: "",
  inputStyle: () => ({}),
  required: false,
  buttonPrimary: false,
  buttonLabel: "",
  buttonDisabled: false,
  buttonLoading: false,
  buttonClass: "",
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | null): void;
  (e: 'focus', event: Event): void;
  (e: 'blur', event: Event): void;
  (e: 'input', event: unknown): void;
  (e: 'keydown', event: KeyboardEvent): void;
  (e: 'button-click', event: unknown): void;
}>();

const computedModel = computed({
  get: () => props.modelValue,
  set: (value: string | null) => emit('update:modelValue', value)
});

/** フォーカス時の処理 */   
const onFocus = (e: Event) => {
  emit('focus', e);
};

/** ブラー時の処理 */
const onBlur = (e: Event) => {
  emit('blur', e);
};

/** 入力イベントの処理 */
const onInput = (e: unknown) => {
  emit('input', e);
};

/** キーダウンイベントの処理 */
const onKeydown = (e: KeyboardEvent) => {
  emit('keydown', e);
};

/** ボタンクリック時の処理 */
const onButtonClick = (e: unknown) => {
  emit('button-click', e);
};

/** タブインデックス */
const computedTabindex = computed(() => {
  if (props.disabled) {
    return -1;
  }
  return props.tabindex ?? undefined;
});
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.p-field {
  .text-field-with-button {
    display: flex;
    align-items: stretch;
    
    // InputGroup内の子要素の高さを統一
    :deep(.p-inputtext) {
      height: 40px;
      padding: 8px 12px;
      border-top-right-radius: 0;
      border-bottom-right-radius: 0;
    }
    
    :deep(.p-button) {
      height: 40px;
      padding: 8px 16px;
      border-top-left-radius: 0;
      border-bottom-left-radius: 0;
      border-left: 0;
      
      // ボタン内のテキストとアイコンの配置調整
      .p-button-label {
        line-height: 1;
      }
      
      .p-button-icon {
        line-height: 1;
      }
    }
  }
}

</style>