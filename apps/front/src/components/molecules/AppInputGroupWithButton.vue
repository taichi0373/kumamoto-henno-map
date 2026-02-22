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
        :tabindex="tabindex"
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
        :tabindex="computedTabindex"
        @click="onButtonClick"
      />
    </InputGroup>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue';
import InputGroup from 'primevue/inputgroup';
import AppTextField from '@/components/atoms/AppTextField.vue';
import AppButton from '@/components/atoms/AppButton.vue';

export default defineComponent({
  name: "AppInputGroupWithButton",
  components: {
    AppTextField,
    InputGroup,
    AppButton,
  },
  props: {
    // バインド値
    modelValue: {
      type: String as () => string | null,
      default: "",
    },
    // 入力タイプ
    type: {
      type: String,
      default: "text",
    },
    // プレースホルダー
    placeholder: {
      type: String,
      default: "",
    },
    // エラー表示フラグ
    showError: {
      type: Boolean,
      default: true,
    },
    // エラー情報
    error: {
      type: [Array, Object],
      default: () => ({}),
    },
    // 無効化フラグ
    disabled: {
      type: Boolean,
      default: false,
    },
    // 読み取り専用フラグ
    readonly: {
      type: Boolean,
      default: false,
    },
    // ID
    inputId: {
      type: String,
      default: "",
    },
    // 入力制限文字数
    maxlength: {
      type: Number,
      default: null,
    },
    // タブインデックス
    tabindex: {
      type: Number,
      default: null,
    },
    // エラータイプ（1:エラー, 2:警告）
    errorType: {
      type: Number,
      default: 0,
    },
    // インプットのクラス名
    inputClass: {
      type: String,
      default: "",
    },
    // インプットのスタイル
    inputStyle: {
      type: Object,
      default: () => ({}),
    },
    // 必須项目化フラグ
    required: {
      type: Boolean,
      default: false,
    },
    // ボタンのスタイル
    buttonPrimary: {
      type: Boolean,
      default: false,
    },
    // ボタン関連のプロパティ
    buttonIcon: {
      type: String,
      required: true,
    },
    // ボタンのラベル
    buttonLabel: {
      type: String,
      default: "",
    },
    // ボタンの無効化フラグ
    buttonDisabled: {
      type: Boolean,
      default: false,
    },
    // ボタンのローディングフラグ
    buttonLoading: {
      type: Boolean,
      default: false,
    },
    // ボタンのクラス名
    buttonClass: {
      type: String,
      default: "",
    },
  },
  emits: [
    /** 入力時 */
    "update:modelValue",
    /** フォーカス時 */
    "focus",
    /** ブラー時 */
    "blur",
    /** 入力イベント */
    "input",
    /** キーダウンイベント */
    "keydown",
    /** ボタンクリック時 */
    "button-click",
  ],
  setup(props, { emit }) {
    // computed
    const computedModel = computed({
      get: () => props.modelValue,
      set: (value: string | null) => emit('update:modelValue', value)
    });

    /** フォーカス時の処理 */
    const onFocus = (e: any) => {
      emit('focus', e);
    };

    /** ブラー時の処理 */
    const onBlur = (e: any) => {
      emit('blur', e);
    };

    /** 入力イベントの処理 */
    const onInput = (e: any) => {
      emit('input', e);
    };

    /** キーダウンイベントの処理 */
    const onKeydown = (e: any) => {
      emit('keydown', e);
    };

    /** ボタンクリック時の処理 */
    const onButtonClick = (e: any) => {
      emit('button-click', e);
    };

    /** タブインデックス */
    const computedTabindex = computed(() => {
      return props.disabled ? -1 : props.tabindex;
    });

    return {
      computedModel,
      computedTabindex,
      onFocus,
      onBlur,
      onInput,
      onKeydown,
      onButtonClick,
    };
  },
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