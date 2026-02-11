<template>
  <div class="signup-page">
    <div class="signup-container">
      <BaseCard title="新規登録" variant="default" size="medium" :hoverable="true" shadow="medium">
        <form @submit.prevent="handleSignup">
          <div class="form-row">
            <div class="form-col">
              <BaseFormGroup size="medium">
                <BaseLabel text="ユーザー名" html-for="username" :required="true" alignConfig="center" size="medium"
                  variant="default" />
                <BaseInput id="username" type="text" v-model="registrationData.username" placeholder="ユーザー名を入力してください"
                  :required="true" size="medium" variant="default" :error-message="validationErrors.username" />
              </BaseFormGroup>
            </div>
            <div class="form-col">
              <BaseFormGroup size="medium">
                <BaseLabel text="パスワード" html-for="password" :required="true" alignConfig="center" size="medium"
                  variant="default" />
                <BaseInput id="password" type="password" v-model="registrationData.password"
                  placeholder="パスワードを入力してください" :required="true" size="medium" variant="default"
                  :error-message="validationErrors.password" />
              </BaseFormGroup>
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
              <BaseFormGroup size="medium">
                <BaseLabel text="パスワード確認" html-for="confirmPassword" :required="true" alignConfig="center" size="medium"
                  variant="default" />
                <BaseInput id="confirmPassword" type="password" v-model="registrationData.confirmPassword"
                  placeholder="パスワードを再入力してください" :required="true" size="medium" variant="default"
                  :error-message="validationErrors.confirmPassword" />
              </BaseFormGroup>
            </div>
            <div class="form-col">
              <BaseFormGroup size="medium">
                <BaseLabel text="生年月日" html-for="birthDate" :required="true" alignConfig="center" size="medium"
                  variant="default" />
                <BaseInput id="birthDate" type="date" v-model="registrationData.birthDate" :required="true"
                  alignConfig="center" size="medium" variant="default" :error-message="validationErrors.birthDate" />
              </BaseFormGroup>
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
              <BaseFormGroup size="medium">
                <BaseLabel text="居住地域" html-for="address" :required="true" alignConfig="center" size="medium"
                  variant="default" />
                <BaseSelect id="address" v-model="registrationData.address" placeholder="選択してください"
                  :options="addressOptions" :required="true" size="medium" variant="default"
                  :error-message="validationErrors.address" />
              </BaseFormGroup>
            </div>
            <div class="form-col">
              <BaseFormGroup size="medium">
                <BaseLabel text="運転免許の所持状況" html-for="licenseStatus" :required="true" alignConfig="center" size="medium"
                  variant="default" />
                <BaseSelect id="licenseStatus" v-model="registrationData.licenseStatus" placeholder="選択してください"
                  :options="licenseOptions" :required="true" size="medium" variant="default"
                  :error-message="validationErrors.licenseStatus" />
              </BaseFormGroup>
            </div>
          </div>

          <div class="submit-button">
            <BaseButton type="submit" :label="isLoading ? '登録中...' : '新規登録'" :primary="true" :disabled="isLoading"
              :elipsis="isLoading" />
          </div>
        </form>

        <div class="login-link">
          <BaseLink to="/login" text="すでにアカウントをお持ちの方はこちら" variant="primary" size="medium" underline="hover" />
        </div>

        <BaseAlert v-if="errorMessage" :message="errorMessage" variant="error" size="medium" :show="!!errorMessage" />

        <BaseAlert v-if="successMessage" :message="successMessage" variant="success" size="medium"
          :show="!!successMessage" />
      </BaseCard>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import BaseLabel from '@/components/atoms/Label.vue'
import BaseInput from '@/components/atoms/Input.vue'
import BaseButton from '@/components/atoms/Button.vue'
import BaseCard from '@/components/atoms/Card.vue'
import BaseLink from '@/components/atoms/Link.vue'
import BaseAlert from '@/components/atoms/Alert.vue'
import BaseFormGroup from '@/components/atoms/FormGroup.vue'
import BaseSelect from '@/components/atoms/Select.vue'
import apiClient from '@/utils/api'
import { codeConstants } from '@/utils/codeConstants'
export default {
  name: 'SignupPage',
  components: {
    BaseLabel,
    BaseInput,
    BaseButton,
    BaseCard,
    BaseLink,
    BaseAlert,
    BaseFormGroup,
    BaseSelect,
  },
  setup() {
    const registrationData = ref({
      username: '',
      password: '',
      confirmPassword: '',
      birthDate: '',
      address: '',
      licenseStatus: ''
    })

    const isLoading = ref(false)
    const errorMessage = ref('')
    const successMessage = ref('')
    const validationErrors = ref({
      username: '',
      password: '',
      confirmPassword: '',
      birthDate: '',
      address: '',
      licenseStatus: ''
    })

    // 居住地域プルダウン
    const addressOptions = ref([])
    // 自治体データを取得
    const getMunicipalities = async () => {
      try {
        const response = await apiClient.get('/municipality/all')
        if (response.municipalities) {
          addressOptions.value = response.municipalities.map(dto => ({
            value: dto.municipalityCd,
            label: dto.municipalityName
          }))
        }
      } catch (error) {
        console.error('自治体データの取得に失敗しました:', error)
      }
    }

    // 運転免許の所持状況のプルダウン
    const licenseStatusLabels = {
      [codeConstants.LICENSE_STATUS.UNLICENSED]: '所持していない',
      [codeConstants.LICENSE_STATUS.LICENSED]: '所持している',
      [codeConstants.LICENSE_STATUS.RETURNED]: '返納',
      [codeConstants.LICENSE_STATUS.EXPIRED]: '失効',
      [codeConstants.LICENSE_STATUS.SUSPENDED]: '停止',
      [codeConstants.LICENSE_STATUS.OTHER]: 'その他',
    }
    const licenseOptions = ref(
      Object.entries(codeConstants.LICENSE_STATUS).map(([key, value]) => ({
        value: value.toString(),
        label: licenseStatusLabels[value]
      }))
    )

    // 初期表示
    onMounted(() => {
      getMunicipalities()
    })

    return {
      registrationData,
      isLoading,
      errorMessage,
      successMessage,
      validationErrors,
      addressOptions,
      licenseOptions
    }
  },
  methods: {
    validateForm() {
      this.validationErrors = {
        username: '',
        password: '',
        confirmPassword: '',
        birthDate: '',
        address: '',
        licenseStatus: ''
      }

      let isValid = true

      if (!this.registrationData.username.trim()) {
        this.validationErrors.username = 'ユーザー名は必須です'
        isValid = false
      } else if (this.registrationData.username.length < 3) {
        this.validationErrors.username = 'ユーザー名は3文字以上で入力してください'
        isValid = false
      }

      if (!this.registrationData.password.trim()) {
        this.validationErrors.password = 'パスワードは必須です'
        isValid = false
      } else if (this.registrationData.password.length < 6) {
        this.validationErrors.password = 'パスワードは6文字以上で入力してください'
        isValid = false
      }

      if (this.registrationData.password !== this.registrationData.confirmPassword) {
        this.validationErrors.confirmPassword = 'パスワードが一致しません'
        isValid = false
      }

      if (!this.registrationData.birthDate) {
        this.validationErrors.birthDate = '生年月日は必須です'
        isValid = false
      }

      if (!this.registrationData.address) {
        this.validationErrors.address = '居住地域を選択してください'
        isValid = false
      }

      if (!this.registrationData.licenseStatus) {
        this.validationErrors.licenseStatus = '運転免許の所持状況を選択してください'
        isValid = false
      }

      return isValid
    },

    async handleSignup() {
      if (!this.validateForm()) {
        return
      }
      this.isLoading = true
      this.errorMessage = ''
      this.successMessage = ''

      try {
        // confirmPasswordを除外してリクエストデータを作成
        const { confirmPassword, ...requestData } = this.registrationData;

        // デバッグ用: 送信データをログ出力
        console.log('送信するデータ:', requestData);

        const response = await apiClient.post('/auth/signup', requestData);
        if (response.data.success) {
          this.successMessage = '登録が完了しました。ログインページに移動してください。'
          setTimeout(() => {
            this.$router.push('/login')
          }, 2000)
        } else {
          this.errorMessage = response.data.message || '登録に失敗しました'
        }
      } catch (error) {
        this.errorMessage = '登録に失敗しました'
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
.signup-page {
  min-height: calc(100vh - 70px);
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.signup-container {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

/* フォームレイアウト */
.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-col {
  flex: 1;
  min-width: 0;
}
.submit-button {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.login-link {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}
</style>
