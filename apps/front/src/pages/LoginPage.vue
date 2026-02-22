<template>
  <div class="page">
    <div class="whole">
      <AppCard title="ログイン">

        <div class="form-col">
          <AppMessageBar
            v-if="barErrMode != ''"
            :mode="barErrMode"
            :message="barErrMsg"
            style="width: 100%;"
          ></AppMessageBar>
        </div>

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
import { ref, onMounted, warn } from 'vue'
import type { Ref } from 'vue'
import { useRouter } from 'vue-router'
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
import { ErrorMessageKbn, MESSAGE_LIST, MESSAGE_NO } from '@/utils/messageConstant'
import { ValidateUtils } from '@/utils/validateUtils'
import { MessageUtils } from '@/utils/messageUtils'

/** ルーター */
const router = useRouter()

/** ユーザー情報 */
const usersModel = ref<UsersDto>(new UsersDto())

/** エラーオブジェクト */
const usernameErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const passwordErrorDto = ref([]) as Ref<InputFormErrorDto[]>

/** エラーバー */
const warningMsgList = ref([]) as Ref<InputFormErrorDto[]>
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
const onClick = () => {
  console.log("ログイン処理")
  // エラーチェック
  checkError()
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
  */
 async function checkError() {
  // 初期値
  let result = ErrorMessageKbn.SUCCESS as number;
  // エラー初期化
  clearError()

  // ユーザ名が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.username)) {
    usernameErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "ユーザ名")
    );
    if (result !== ErrorMessageKbn.ERROR) {
      result = MessageUtils.getMessageType(MESSAGE_LIST, MESSAGE_NO.MSG_001);
      warningMsgList.value.push(
        MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "ユーザ名")
      );
    }
  }

  // パスワードが未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.password)) {
    passwordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "パスワード")
    );
    if (result !== ErrorMessageKbn.ERROR) {
      result = MessageUtils.getMessageType(MESSAGE_LIST, MESSAGE_NO.MSG_001);
      warningMsgList.value.push(
        MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "パスワード")
      );
    }
  }
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
  max-width: 800px;
  margin: 0 auto;
}
</style>
