<template>
  <div class="page">
    <div class="whole">
      <AppCard title="ログイン" :inputStyle="{ width: '100%', maxWidth: '600px', padding: '16px' }">

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
            <AppLabel :id="'username'" :required="true">ユーザ名</AppLabel>
            <AppTextField :input-id="'username'" :type="'text'" v-model="usersModel.username" placeholder=""
              :required="true" :error="usernameErrorDto"/>
          </div>

          <div class="form-col">
            <AppLabel :id="'password'" :required="true">パスワード</AppLabel>
            <AppPassword :input-id="'password'" v-model="usersModel.password" placeholder=""
              :error="passwordErrorDto" />
          </div>
        </div>

        <div class="form-btn">
          <AppButton :primary="true" label="ログイン" @click="onClick()"/>
        </div>

        <div class="form-link">
          <AppLink @click="router.push('/signup')">新規登録はこちら</AppLink>
        </div>

      </AppCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { AxiosError } from 'axios'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import AppPassword from '@/components/atoms/AppPassword.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import { UsersDto } from '@/dto/usersDto'
import { InputFormErrorDto } from '@/dto/InputFormErrorDto'
import { AuthUtils } from '@/utils/auth'
import { MESSAGE_LIST, MESSAGE_NO } from '@/utils/messageConstant'
import { ValidateUtils } from '@/utils/validateUtils'
import { MessageUtils } from '@/utils/messageUtils'
import apiClient from '@/utils/api'
import { responseStatusConstant } from '@/utils/responseStatusConstant'

/** ルーター */
const router = useRouter()
const route = useRoute()

/** ユーザー情報 */
const usersModel = ref<UsersDto>(new UsersDto())

/** エラーオブジェクト */
const usernameErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const passwordErrorDto = ref([]) as Ref<InputFormErrorDto[]>

/** エラーバー */
const barErrMode = ref('') as Ref<string>
const barErrMsg = ref('') as Ref<string>

// 既にログイン済みの場合はホームにリダイレクト
onMounted(() => {
  if (AuthUtils.isLoggedIn()) {
    router.push('/')
  }
})

/**
 * ログイン処理
 */
const onClick = async () => {
  // エラーバーをリセット
  barErrMode.value = ''
  barErrMsg.value = ''

  // エラーチェック
  const hasError = checkError()
  if (hasError) return

  // APIリクエスト
  try {
    const response = await apiClient.post('/users/login', {
      username: usersModel.value.username,
      password: usersModel.value.password
    })

    if (response.status === responseStatusConstant.OK) {
      const userData = (response.data as { data: { username: string; userId: number } }).data
      AuthUtils.login({ username: userData.username, id: String(userData.userId) })
      const redirect = route.query.redirect as string | undefined
      router.push(redirect || '/')
    }
  } catch (error: unknown) {
    const axiosError = error as AxiosError<{ message: string }>
    if (axiosError.response?.status === responseStatusConstant.UNAUTHORIZED) {
      barErrMode.value = MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_009, "ユーザ名またはパスワード").type === 1 ? 'error' : 'warning'
      barErrMsg.value = MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_009, "ユーザ名またはパスワード").message
    } else {
      barErrMode.value = 'error'
      barErrMsg.value = 'ログイン中にエラーが発生しました'
    }
  }
}

/**
 * エラークリア
 */
const clearError = () => {
  usernameErrorDto.value.splice(0)
  passwordErrorDto.value.splice(0)
}

/**
 * エラーチェック
 * @returns {boolean} エラーがある場合はtrue
 */
function checkError(): boolean {
  clearError()
  let hasError = false

  // ユーザ名が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.username)) {
    usernameErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "ユーザ名")
    )
    hasError = true
  }

  // パスワードが未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.password)) {
    passwordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "パスワード")
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
