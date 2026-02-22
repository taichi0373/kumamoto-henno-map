<template>
  <div class="login-page">
    <div class="login-container">
      <AppCard title="ログイン">

        <div class="form-field">
          <AppLabel :id="'username'" :required="true">ユーザ名</AppLabel>
          <AppTextField :input-id="'username'" :type="'text'" v-model="usersModel.username" placeholder="ユーザー名を入力してください"
            :required="true" :error="usernameErrorDto" />
        </div>

        <div class="form-field">
          <AppLabel :id="'password'" :required="true">パスワード</AppLabel>
          <AppPassword :input-id="'password'" v-model="usersModel.password" placeholder="パスワードを入力してください"
            :error="passwordErrorDto" />
        </div>

        <div class="submit-button">
          <AppButton :primary="true" label="ログイン" @click="onClick()"/>
        </div>

        <div class="signup-link">
          <AppLink @click="router.push('/signup')">新規登録はこちら</AppLink>
        </div>

      </AppCard>
    </div>
  </div>
</template>

<script lang="ts">
import { ref, onMounted, Ref } from 'vue'
import { useRouter } from 'vue-router'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import AppPassword from '@/components/atoms/AppPassword.vue'

import { UsersDto } from '@/dto/usersDto'
import { InputFormErrorDto } from '@/dto/InputFormErrorDto'

import { AuthUtils } from '@/utils/auth'

export default {
  name: 'LoginPage',
  components: {
    AppLabel,
    AppTextField,
    AppButton,
    AppCard,
    AppLink,
    AppPassword,
  },
  setup() {
    /** ルーター */
    const router = useRouter()

    /** ユーザー情報 */
    const usersModel = ref<UsersDto>(new UsersDto());

    // エラーオブジェクト
    const usernameErrorDto = ref([]) as Ref<InputFormErrorDto[]>
    const passwordErrorDto = ref([]) as Ref<InputFormErrorDto[]>

    // ローディング状態
    const isLoading = ref(false)
    const errorMessage = ref('')

    // 既にログイン済みの場合はホームにリダイレクト
    onMounted(() => {
      if (AuthUtils.isLoggedIn()) {
        router.push('/')
      }
    })

    const clearError = () => {
      usernameErrorDto.value.splice(0);
      passwordErrorDto.value.splice(0);
    }

    /**
     * ログイン処理
     */
    const onClick = () => {
      clearError()
      console.log(usersModel.value)
    }

    return {
      router,
      usersModel,
      usernameErrorDto,
      passwordErrorDto,
      clearError,
      onClick,
    }
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.login-page {
  min-height: calc(100vh - 70px);
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 16px;
}

.submit-button {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.signup-link {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}
</style>
