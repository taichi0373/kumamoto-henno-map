<template>
  <AppDialog
    v-model="computedVisible"
    header="ご意見・ご要望"
    :dialog-style="{ width: 'min(480px, 90vw)' }"
    :dismissable-mask="true"
    @hide="onHide"
  >
    <!-- 送信完了メッセージ -->
    <div v-if="isSubmitted" class="feedback-result feedback-result--success">
      <span class="pi pi-check-circle result-icon" />
      <p class="result-message">送信が完了しました。ご意見ありがとうございます。</p>
    </div>

    <!-- エラーメッセージ -->
    <div v-if="submitError" class="feedback-result feedback-result--error">
      <span class="pi pi-exclamation-circle result-icon" />
      <p class="result-message">{{ submitError }}</p>
    </div>

    <!-- フォーム -->
    <form v-if="!isSubmitted" class="feedback-form" @submit.prevent="handleSubmit">
      <!-- カテゴリ -->
      <div class="form-item">
        <AppLabel :required="true">カテゴリ</AppLabel>
        <AppSelect
          v-model="form.category"
          :options="categoryOptions"
          placeholder="カテゴリを選択"
          :show-clear="false"
        />
      </div>

      <!-- お名前 -->
      <div class="form-item">
        <AppLabel>お名前</AppLabel>
        <AppTextField
          v-model="form.name"
          placeholder="山田太郎"
          :maxlength="100"
          :show-error="false"
        />
      </div>

      <!-- メールアドレス -->
      <div class="form-item">
        <AppLabel>メールアドレス</AppLabel>
        <AppTextField
          v-model="form.email"
          type="email"
          placeholder="example@mail.com"
          :maxlength="255"
          :error="emailError"
          :show-error="true"
        />
      </div>

      <!-- 内容 -->
      <div class="form-item">
        <AppLabel :required="true">内容</AppLabel>
        <AppTextarea
          v-model="form.content"
          placeholder="ご意見・ご要望をご記入ください"
          :rows="5"
          :maxlength="2000"
          :error="contentError"
          :show-error="true"
        />
      </div>
    </form>

    <template #footer>
      <div v-if="!isSubmitted" class="footer-buttons">
        <AppButton label="キャンセル" @click="onClose" />
        <AppButton
          label="送信"
          :primary="true"
          :loading="isLoading"
          @click="handleSubmit"
        />
      </div>
      <div v-else class="footer-buttons">
        <AppButton label="閉じる" :primary="true" @click="onClose" />
      </div>
    </template>
  </AppDialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import AppDialog from '@/components/atoms/AppDialog.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppTextarea from '@/components/atoms/AppTextarea.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppLabel from '@/components/atoms/AppLabel.vue'
import { FeedbackDto } from '@/dto/feedbackDto'
import { InputFormErrorDto } from '@/dto/InputFormErrorDto'
import { SelectDto } from '@/dto/selectDto'
import { useAuthStore } from '@/stores/auth'
import apiClient from '@/utils/api'
import { API_RESPONSE_MESSAGE, MESSAGE_LIST, MESSAGE_NO } from '@/utils/messageConstant'
import { ValidateUtils } from '@/utils/validateUtils'
import { MessageUtils } from '@/utils/messageUtils'

const props = defineProps<{
  /** 表示フラグ */
  modelValue: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

const auth = useAuthStore()

/** 表示フラグ（双方向バインド） */
const computedVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

/** カテゴリ選択肢 */
const categoryOptions: SelectDto[] = [
  new SelectDto({ label: 'バグ報告', text: 'バグ報告', value: 'BUG' }),
  new SelectDto({ label: '要望', text: '要望', value: 'REQUEST' }),
  new SelectDto({ label: 'その他', text: 'その他', value: 'OTHER' }),
]

/** フォームデータ */
const form = ref(new FeedbackDto({ category: 'BUG', name: null, email: null, content: null }))

/** 送信中フラグ */
const isLoading = ref(false)

/** 送信完了フラグ */
const isSubmitted = ref(false)

/** 送信エラーメッセージ */
const submitError = ref<string | null>(null)

/** メールアドレスバリデーションエラー */
const emailError = ref<InputFormErrorDto[]>([])

/** 内容バリデーションエラー */
const contentError = ref<InputFormErrorDto[]>([])

/** モーダル表示時にログイン済みの場合は初期値をセット */
watch(
  () => props.modelValue,
  (visible) => {
    if (visible) {
      resetForm()
    }
  }
)

/**
 * エラーをクリアする
 */
const clearError = (): void => {
  emailError.value.splice(0)
  contentError.value.splice(0)
}

/**
 * フォームをリセットする
 */
const resetForm = (): void => {
  form.value = new FeedbackDto({
    category: 'BUG',
    name: auth.user?.username ?? null,
    email: auth.user?.email ?? null,
    content: null,
  })
  clearError()
  submitError.value = null
  isSubmitted.value = false
}

/**
 * 入力チェックを実行する
 * @returns エラーがない場合 true
 */
const checkError = (): boolean => {
  clearError()
  let hasError = false

  if (!ValidateUtils.isNullOrEmpty(form.value.email) && !ValidateUtils.isEmail(form.value.email ?? '')) {
    emailError.value.push(MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_004, 'メールアドレス'))
    hasError = true
  }

  if (ValidateUtils.isNullOrEmpty(form.value.content)) {
    contentError.value.push(MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, '内容'))
    hasError = true
  }

  return !hasError
}

/**
 * フォーム送信処理
 */
const handleSubmit = async (): Promise<void> => {
  if (!checkError()) return

  isLoading.value = true
  submitError.value = null

  try {
    await apiClient.post('/feedback', {
      category: form.value.category,
      name: form.value.name,
      email: form.value.email,
      content: form.value.content,
    })
    isSubmitted.value = true
  } catch {
    submitError.value = API_RESPONSE_MESSAGE.API_ERROR
  } finally {
    isLoading.value = false
  }
}

/**
 * モーダルを閉じる（キャンセル・閉じるボタン共通）
 */
const onClose = (): void => {
  emit('update:modelValue', false)
}

/**
 * ダイアログ非表示時の後処理
 */
const onHide = (): void => {
  resetForm()
}
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.feedback-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 8px 0;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.footer-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  width: 100%;

  :deep(.p-button) {
    min-width: 80px;
  }
}

.feedback-result {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 8px;

  &--success {
    background-color: base.$success-200;
    color: base.$success-100;
  }

  &--error {
    background-color: base.$error-200;
    color: base.$error-100;
  }
}

.result-icon {
  font-size: 20px;
  flex-shrink: 0;
  margin-top: 2px;
}

.result-message {
  margin: 0;
  font-size: base.$fontsize-medium;
  line-height: 1.5;
}
</style>
