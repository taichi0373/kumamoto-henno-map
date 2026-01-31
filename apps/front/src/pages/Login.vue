<template>
  <div class="login-page">
    <div class="login-container">
      <BaseCard title="管理者ログイン" variant="default" size="medium" :hoverable="true" shadow="medium">
        <form @submit.prevent="handleLogin">
          <BaseFormGroup size="medium">
            <BaseLabel text="API Key" html-for="apikey" :required="true" alignConfig="center" size="medium"
              variant="default" />
            <BaseInput id="apikey" type="password" v-model="credentials.apiKey" placeholder="API Keyを入力してください"
              :required="true" alignConfig="center" size="medium" variant="default"
              :error-message="validationErrors.apiKey" />
          </BaseFormGroup>

          <div class="submit-button">
            <BaseButton type="submit" :label="isLoading ? '認証中...' : 'ログイン'" :primary="true" :disabled="isLoading"
              :elipsis="isLoading" />
          </div>
        </form>

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
    BaseAlert,
    BaseFormGroup,
  },
  setup() {
    const router = useRouter()

    const credentials = ref({
      apiKey: ''
    })
    const isLoading = ref(false)
    const errorMessage = ref('')
    const validationErrors = ref({
      apiKey: ''
    })

    // 既にログイン済みの場合はホームにリダイレクト
    onMounted(() => {
      if (AuthUtils.isLoggedIn()) {
        router.push('/')
      }
    })

    const validateForm = () => {
      validationErrors.value = {
        apiKey: ''
      }

      let isValid = true

      if (!credentials.value.apiKey.trim()) {
        validationErrors.value.apiKey = 'API Keyは必須です'
        isValid = false
      } else if (credentials.value.apiKey.length < 10) {
        validationErrors.value.apiKey = 'API Keyは10文字以上で入力してください'
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
          apiKey: credentials.value.apiKey
        })

        if (response.data.success) {
          // API Keyを保存
          AuthUtils.login(credentials.value.apiKey)
          // ホームページにリダイレクト
          router.push('/')
        } else {
          errorMessage.value = response.data.message || 'ログインに失敗しました'
        }
      } catch (error) {
        console.error('Login error:', error)
        errorMessage.value = 'ログインに失敗しました。API Keyを確認してください。'
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