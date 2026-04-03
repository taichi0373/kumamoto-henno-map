<template>
  <div class="page">
    <AppBlockUI :blocked="isLoading" />
    <div class="whole">
      <AppCard title="パスワード変更" :inputStyle="{ width: '100%', maxWidth: '600px', padding: '16px' }">

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
            <AppLabel :id="'currentPassword'" :required="true">現在のパスワード</AppLabel>
            <AppPassword :input-id="'currentPassword'" v-model="model.currentPassword" placeholder=""
              :error="currentPasswordErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'newPassword'" :required="true">新しいパスワード</AppLabel>
            <AppPassword :input-id="'newPassword'" v-model="model.newPassword" placeholder=""
              :error="newPasswordErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'confirmNewPassword'" :required="true">新しいパスワード（確認）</AppLabel>
            <AppPassword :input-id="'confirmNewPassword'" v-model="model.confirmNewPassword" placeholder=""
              :error="confirmNewPasswordErrorDto" />
          </div>
        </div>

        <div class="form-btn">
          <AppButton :primary="true" label="変更する" @click="onSubmit()" />
        </div>

        <div class="form-link">
          <AppLink @click="router.push('/profile')">プロフィールに戻る</AppLink>
        </div>

      </AppCard>
      <AppToastMessage />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { Ref } from 'vue'
import { useRouter } from 'vue-router'
import type { AxiosError } from 'axios'
import AppBlockUI from '@/components/atoms/AppBlockUI.vue'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppPassword from '@/components/atoms/AppPassword.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppToastMessage from '@/components/atoms/AppToastMessage.vue'
import { ChangePasswordDto } from '@/dto/changePasswordDto'
import { InputFormErrorDto } from '@/dto/InputFormErrorDto'
import { API_RESPONSE_MESSAGE, MESSAGE_LIST, MESSAGE_NO } from '@/utils/messageConstant'
import { ValidateUtils } from '@/utils/validateUtils'
import { MessageUtils } from '@/utils/messageUtils'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import { responseStatusConstant } from '@/utils/responseStatusConstant'

/** ルーター */
const router = useRouter()

/** パスワード変更モデル */
const model = ref<ChangePasswordDto>(new ChangePasswordDto())

/** フィールドエラー */
const currentPasswordErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const newPasswordErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const confirmNewPasswordErrorDto = ref([]) as Ref<InputFormErrorDto[]>

/** エラーバー */
const barErrMode = ref('') as Ref<string>
const barErrMsg = ref('') as Ref<string>

/** ローディング */
const isLoading = ref(false)

/**
 * パスワード変更処理
 */
const onSubmit = async () => {
  barErrMode.value = ''
  barErrMsg.value = ''

  const hasError = checkError()
  if (hasError) return

  isLoading.value = true
  try {
    const response = await apiClient.put('/users/password', {
      currentPassword: model.value.currentPassword,
      newPassword: model.value.newPassword,
      confirmNewPassword: model.value.confirmNewPassword
    })
    if (response.status === responseStatusConstant.OK) {
      ToastMessageUtils.success(API_RESPONSE_MESSAGE.PASSWORD_CHANGE_SUCCESS)
      isLoading.value = false
      router.push('/profile')
    }
  } catch (error: unknown) {
    const axiosError = error as AxiosError<{ message: string }>
    if (axiosError.response?.status === responseStatusConstant.CONFLICT) {
      barErrMode.value = 'error'
      barErrMsg.value = API_RESPONSE_MESSAGE.PASSWORD_CURRENT_WRONG
    } else {
      barErrMode.value = 'error'
      barErrMsg.value = API_RESPONSE_MESSAGE.PASSWORD_CHANGE_FAILED
    }
  } finally {
    isLoading.value = false
  }
}

/**
 * エラークリア
 */
const clearError = () => {
  currentPasswordErrorDto.value.splice(0)
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

  if (ValidateUtils.isNullOrEmpty(model.value.currentPassword)) {
    currentPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "現在のパスワード")
    )
    hasError = true
  }

  if (ValidateUtils.isNullOrEmpty(model.value.newPassword)) {
    newPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "新しいパスワード")
    )
    hasError = true
  } else if ((model.value.newPassword ?? '').length < 8) {
    newPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_002, "新しいパスワード", "8")
    )
    hasError = true
  }

  if (ValidateUtils.isNullOrEmpty(model.value.confirmNewPassword)) {
    confirmNewPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "新しいパスワード（確認）")
    )
    hasError = true
  } else if (model.value.newPassword !== model.value.confirmNewPassword) {
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
