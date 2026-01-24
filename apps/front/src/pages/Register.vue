<template>
  <div class="register-page">
    <div class="register-container">
      <BaseCard title="新規登録" variant="default" size="medium" :hoverable="true" shadow="medium">
        <form @submit.prevent="handleRegister">
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
import { ref } from 'vue'
import BaseLabel from '@/components/atoms/Label.vue'
import BaseInput from '@/components/atoms/Input.vue'
import BaseButton from '@/components/atoms/Button.vue'
import BaseCard from '@/components/atoms/Card.vue'
import BaseLink from '@/components/atoms/Link.vue'
import BaseAlert from '@/components/atoms/Alert.vue'
import BaseFormGroup from '@/components/atoms/FormGroup.vue'
import BaseSelect from '@/components/atoms/Select.vue'
import apiClient from '@/utils/api'
import { AuthUtils } from '@/utils/auth'
export default {
  name: 'RegisterPage',
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

    const addressOptions = ref([
      // 政令指定都市
      { value: '431001', label: '熊本市中央区' },
      { value: '431002', label: '熊本市東区' },
      { value: '431003', label: '熊本市西区' },
      { value: '431004', label: '熊本市南区' },
      { value: '431005', label: '熊本市北区' },
      // 市部
      { value: '432024', label: '八代市' },
      { value: '432032', label: '人吉市' },
      { value: '432041', label: '荒尾市' },
      { value: '432059', label: '水俣市' },
      { value: '432067', label: '玉名市' },
      { value: '432091', label: '山鹿市' },
      { value: '432105', label: '菊池市' },
      { value: '432113', label: '宇土市' },
      { value: '432121', label: '上天草市' },
      { value: '432130', label: '宇城市' },
      { value: '432148', label: '阿蘇市' },
      { value: '432156', label: '天草市' },
      { value: '432164', label: '合志市' },
      // 郡部（一部）
      { value: '433616', label: '美里町' },
      { value: '434035', label: '玉東町' },
      { value: '434051', label: '南関町' },
      { value: '434060', label: '長洲町' },
      { value: '434078', label: '和水町' },
      { value: '434213', label: '大津町' },
      { value: '434221', label: '菊陽町' },
      { value: '434311', label: '南小国町' },
      { value: '434329', label: '小国町' },
      { value: '434337', label: '産山村' },
      { value: '434345', label: '高森町' },
      { value: '434361', label: '西原村' },
      { value: '434370', label: '南阿蘇村' },
      { value: '434434', label: '御船町' },
      { value: '434442', label: '嘉島町' },
      { value: '434451', label: '益城町' },
      { value: '434469', label: '甲佐町' },
      { value: '434493', label: '山都町' },
      { value: '434639', label: '氷川町' },
      { value: '434647', label: '芦北町' },
      { value: '434655', label: '津奈木町' },
      { value: '434671', label: '錦町' },
      { value: '434680', label: '多良木町' },
      { value: '434698', label: '湯前町' },
      { value: '434701', label: '水上村' },
      { value: '434710', label: '相良村' },
      { value: '434728', label: '五木村' },
      { value: '434736', label: '山江村' },
      { value: '434744', label: '球磨村' },
      { value: '434752', label: 'あさぎり町' },
      { value: '434876', label: '苓北町' },
    ])

    const licenseOptions = ref([
      { value: 'licensed', label: '所持している' },
      { value: 'unlicensed', label: '所持していない' },
      { value: 'returned', label: '返納済み' },
      { value: 'suspension', label: '停止している' },
      { value: 'expired', label: '失効している' },
    ])

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

    async handleRegister() {
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

        const response = await apiClient.post('/auth/register', requestData);
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
.register-page {
  min-height: calc(100vh - 70px);
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-container {
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
