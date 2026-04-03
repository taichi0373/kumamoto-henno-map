<template>
  <div class="page">
    <AppBlockUI :blocked="isLoading" />
    <div class="whole">
      <AppCard title="パスワードを忘れた方" :inputStyle="{ width: '100%', maxWidth: '600px', padding: '16px' }">

        <div class="form-col">
          <AppMessageBar
            v-if="barMode != ''"
            :mode="barMode"
            :message="barMsg"
            style="width: 100%;"
          ></AppMessageBar>
        </div>

        <div class="form-row-1">
          <div class="form-col">
            <AppLabel :id="'email'" :required="true">メールアドレス</AppLabel>
            <AppTextField :input-id="'email'" type="text" v-model="email" placeholder=""
              :required="true" :error="emailErrorDto" />
          </div>
        </div>

        <div class="form-btn">
          <AppButton :primary="true" label="リセットメールを送信" @click="onSubmit()" />
        </div>

        <div class="form-link">
          <AppLink @click="router.push('/login')">ログインに戻る</AppLink>
        </div>

      </AppCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { Ref } from 'vue'
import { useRouter } from 'vue-router'
import AppBlockUI from '@/components/atoms/AppBlockUI.vue'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import { InputFormErrorDto } from '@/dto/InputFormErrorDto'
import { API_RESPONSE_MESSAGE, MESSAGE_LIST, MESSAGE_NO } from '@/utils/messageConstant'
import { ValidateUtils } from '@/utils/validateUtils'
import { MessageUtils } from '@/utils/messageUtils'
import apiClient from '@/utils/api'
import { responseStatusConstant } from '@/utils/responseStatusConstant'

/** ルーター */
const router = useRouter()

/** メールアドレス */
const email = ref<string | null>(null)

/** フィールドエラー */
const emailErrorDto = ref([]) as Ref<InputFormErrorDto[]>

/** メッセージバー */
const barMode = ref('') as Ref<string>
const barMsg = ref('') as Ref<string>

/** ローディング */
const isLoading = ref(false)

/**
 * リセットメール送信処理
 */
const onSubmit = async () => {
  barMode.value = ''
  barMsg.value = ''

  const hasError = checkError()
  if (hasError) return

  isLoading.value = true
  try {
    const response = await apiClient.post('/auth/password-reset/request', {
      email: email.value
    })
    if (response.status === responseStatusConstant.OK) {
      // ユーザー存在有無に関わらず同一メッセージを表示（列挙攻撃対策）
      barMode.value = 'success'
      barMsg.value = API_RESPONSE_MESSAGE.PASSWORD_RESET_MAIL_SENT
    }
  } catch {
    barMode.value = 'error'
    barMsg.value = API_RESPONSE_MESSAGE.API_ERROR
  } finally {
    isLoading.value = false
  }
}

/**
 * エラークリア
 */
const clearError = () => {
  emailErrorDto.value.splice(0)
}

/**
 * エラーチェック
 * @returns エラーがある場合はtrue
 */
function checkError(): boolean {
  clearError()
  let hasError = false

  if (ValidateUtils.isNullOrEmpty(email.value)) {
    emailErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "メールアドレス")
    )
    hasError = true
  } else if (!ValidateUtils.isEmail(email.value ?? '')) {
    emailErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_004, "メールアドレス")
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
