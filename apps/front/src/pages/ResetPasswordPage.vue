<template>
  <div class="page">
    <AppBlockUI :blocked="isLoading" />
    <div class="whole">
      <AppCard title="新しいパスワードの設定" :inputStyle="{ width: '100%', maxWidth: '600px', padding: '16px' }">

        <div class="form-col">
          <AppMessageBar
            v-if="barErrMode != ''"
            :mode="barErrMode"
            :message="barErrMsg"
            style="width: 100%;"
          ></AppMessageBar>
        </div>

        <div class="form-row-1">
          <div class="form-col">
            <AppLabel :id="'newPassword'" :required="true">新しいパスワード</AppLabel>
            <AppPassword :input-id="'newPassword'" v-model="newPassword" placeholder=""
              :error="newPasswordErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'confirmNewPassword'" :required="true">新しいパスワード（確認）</AppLabel>
            <AppPassword :input-id="'confirmNewPassword'" v-model="confirmNewPassword" placeholder=""
              :error="confirmNewPasswordErrorDto" />
          </div>
        </div>

        <div class="form-btn">
          <AppButton :primary="true" label="パスワードを変更する" @click="onSubmit()" />
        </div>

      </AppCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import AppBlockUI from '@/components/atoms/AppBlockUI.vue'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppPassword from '@/components/atoms/AppPassword.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import { InputFormErrorDto } from '@/dto/InputFormErrorDto'
import { API_RESPONSE_MESSAGE, MESSAGE_LIST, MESSAGE_NO } from '@/utils/messageConstant'
import { ValidateUtils } from '@/utils/validateUtils'
import { MessageUtils } from '@/utils/messageUtils'
import apiClient from '@/utils/api'
import { responseStatusConstant } from '@/utils/responseStatusConstant'

/** ルーター */
const router = useRouter()
const route = useRoute()

/** パスワードフィールド */
const newPassword = ref<string | null>(null)
const confirmNewPassword = ref<string | null>(null)

/** フィールドエラー */
const newPasswordErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const confirmNewPasswordErrorDto = ref([]) as Ref<InputFormErrorDto[]>

/** エラーバー */
const barErrMode = ref('') as Ref<string>
const barErrMsg = ref('') as Ref<string>

/** ローディング */
const isLoading = ref(false)

/** リセットトークン */
const token = ref<string>('')

// トークンが存在しない場合はパスワード忘れページにリダイレクト
onMounted(() => {
  const queryToken = route.query.token as string | undefined
  if (!queryToken) {
    router.push('/forgot-password')
    return
  }
  token.value = queryToken
})

/**
 * パスワードリセット処理
 */
const onSubmit = async () => {
  barErrMode.value = ''
  barErrMsg.value = ''

  const hasError = checkError()
  if (hasError) return

  isLoading.value = true
  try {
    const response = await apiClient.post('/auth/password-reset/confirm', {
      token: token.value,
      newPassword: newPassword.value,
      confirmNewPassword: confirmNewPassword.value
    })
    if (response.status === responseStatusConstant.OK) {
      router.push({ path: '/login', query: { resetSuccess: '1' } })
    }
  } catch {
    barErrMode.value = 'error'
    barErrMsg.value = API_RESPONSE_MESSAGE.INVALID_OR_EXPIRED_TOKEN
  } finally {
    isLoading.value = false
  }
}

/**
 * エラークリア
 */
const clearError = () => {
  newPasswordErrorDto.value.splice(0)
  confirmNewPasswordErrorDto.value.splice(0)
}

/**
 * エラーチェック
 * @returns エラーがある場合はtrue
 */
function checkError(): boolean {
  clearError()
  let hasError = false

  if (ValidateUtils.isNullOrEmpty(newPassword.value)) {
    newPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "新しいパスワード")
    )
    hasError = true
  } else if ((newPassword.value ?? '').length < 8) {
    newPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_002, "新しいパスワード", "8")
    )
    hasError = true
  }

  if (ValidateUtils.isNullOrEmpty(confirmNewPassword.value)) {
    confirmNewPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "新しいパスワード（確認）")
    )
    hasError = true
  } else if (newPassword.value !== confirmNewPassword.value) {
    confirmNewPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_008, "新しいパスワード", "新しいパスワード（確認）")
    )
    hasError = true
  }

  return hasError
}
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.page {
  height: calc(100vh - base.$header-height);
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.whole {
  width: 100%;
  max-width: 600px;
  display: flex;
  justify-content: center;
}
</style>
