<template>
  <div class="login-page">
    <div class="login-container">
      <BaseCard title="ログイン" variant="default" size="medium" :hoverable="true" shadow="medium">
        <form @submit.prevent="handleLogin">
          <BaseFormGroup size="medium">
            <BaseLabel text="ユーザー名" html-for="username" :required="true" alignConfig="center" size="medium"
              variant="default" />
            <BaseInput id="username" type="text" v-model="credentials.username" placeholder="ユーザー名を入力してください"
              :required="true" alignConfig="center" size="medium" variant="default"
              :error-message="validationErrors.username" />
          </BaseFormGroup>

          <BaseFormGroup size="medium">
            <BaseLabel text="パスワード" html-for="password" :required="true" alignConfig="center" size="medium"
              variant="default" />
            <BaseInput id="password" type="password" v-model="credentials.password" placeholder="パスワードを入力してください"
              :required="true" size="medium" variant="default" :error-message="validationErrors.password" />
          </BaseFormGroup>

          <div class="submit-button">
            <BaseButton type="submit" :label="isLoading ? 'ログイン中...' : 'ログイン'" :primary="true" :disabled="isLoading"
              :elipsis="isLoading" />
          </div>
        </form>

        <div class="register-link">
          <BaseLink to="/register" text="新規登録はこちら" variant="primary" size="medium" underline="hover" />
        </div>

        <BaseAlert v-if="errorMessage" :message="errorMessage" variant="error" size="medium" :show="!!errorMessage" />
      </BaseCard>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import BaseLabel from '@/components/atoms/Label.vue'
import BaseInput from '@/components/atoms/Input.vue'
import BaseButton from '@/components/atoms/Button.vue'
import BaseCard from '@/components/atoms/Card.vue'
import BaseLink from '@/components/atoms/Link.vue'
import BaseAlert from '@/components/atoms/Alert.vue'
import BaseFormGroup from '@/components/atoms/FormGroup.vue'
import apiClient from '@/utils/api'
import { AuthUtils } from '@/utils/auth'

export default {
  name: 'LoginPage',
  components: {
    BaseLabel,
    BaseInput,
    BaseButton,
    BaseCard,
    BaseLink,
    BaseAlert,
    BaseFormGroup,
  },
  setup() {
    const router = useRouter()

    const credentials = ref({
      username: '',
      password: ''
    })
    const isLoading = ref(false)
    const errorMessage = ref('')
    const validationErrors = ref({
      username: '',
      password: ''
    })

    // 既にログイン済みの場合はホームにリダイレクト
    onMounted(() => {
      if (AuthUtils.isLoggedIn()) {
        router.push('/')
      }
    })

    const validateForm = () => {
      validationErrors.value = {
        username: '',
        password: ''
      }

      let isValid = true

      if (!credentials.value.username.trim()) {
        validationErrors.value.username = 'ユーザー名は必須です'
        isValid = false
      } else if (credentials.value.username.length < 3) {
        validationErrors.value.username = 'ユーザー名は3文字以上で入力してください'
        isValid = false
      }

      if (!credentials.value.password.trim()) {
        validationErrors.value.password = 'パスワードは必須です'
        isValid = false
      } else if (credentials.value.password.length < 6) {
        validationErrors.value.password = 'パスワードは6文字以上で入力してください'
        isValid = false
      }

      return isValid
    }

    const handleLogin = async () => {
      if (!validateForm()) {
        return
      }

      isLoading.value = true
      errorMessage.value = ''

      try {
        const response = await apiClient.post('/auth/login', {
          username: credentials.value.username,
          password: credentials.value.password
        })

        if (response.data.success) {
          // セッションストレージに認証情報を保存
          AuthUtils.login(response.data.user, response.data.token)

          // ユーザープロフィール情報も保存
          if (response.data.user) {
            sessionStorage.setItem('userProfile', JSON.stringify(response.data.user))
          }
          // ホームページにリダイレクト
          router.push('/')
        } else {
          errorMessage.value = response.data.message || 'ログインに失敗しました'
        }
      } catch (error) {
        console.error('Login error:', error)
        errorMessage.value = 'ログインに失敗しました。ネットワーク接続を確認してください。'
      } finally {
        isLoading.value = false
      }
    }

    return {
      credentials,
      isLoading,
      errorMessage,
      validationErrors,
      handleLogin
    }
  }
}
</script>

<style scoped>
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
.submit-button {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.register-link {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}
</style>