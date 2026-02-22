<template>
  <div class="signup-page">
    <div class="signup-container">
      <AppCard title="新規登録">

        <div class="form-grid">
          <div class="form-field">
            <AppLabel :id="'username'" :required="true">ユーザー名</AppLabel>
            <AppTextField :input-id="'username'" :type="'text'" v-model="usersModel.username" placeholder="ユーザー名を入力してください"
              :required="true" :error="usernameErrorDto" />
          </div>

          <div class="form-field">
            <AppLabel :id="'password'" :required="true">パスワード</AppLabel>
            <AppPassword :input-id="'password'" v-model="usersModel.password"
              placeholder="パスワードを入力してください" :error="passwordErrorDto" />
          </div>

          <div class="form-field">
            <AppLabel :id="'confirmPassword'" :required="true">パスワード確認</AppLabel>
            <AppPassword :input-id="'confirmPassword'" v-model="usersModel.confirmPassword"
              placeholder="パスワードを再入力してください" :error="confirmPasswordErrorDto" />
          </div>

          <div class="form-field">
            <AppLabel :id="'birthDate'" :required="true">生年月日</AppLabel>
            <AppCalendar :input-id="'birthDate'" v-model="birthDateModel" :required="true" :error="birthDateErrorDto" />
          </div>

          <div class="form-field">
            <AppLabel :id="'address'" :required="true">居住地域</AppLabel>
            <AppSelect id="address" v-model="usersModel.address" placeholder="選択してください"
              :options="addressOptions" :required="true" :error="addressErrorDto" />
          </div>

          <div class="form-field">
            <AppLabel :id="'licenseStatus'" :required="true">運転免許の所持状況</AppLabel>
            <AppSelect :id="'licenseStatus'" v-model="usersModel.licenseStatus" placeholder="選択してください"
              :options="licenseOptions" :required="true" :error="licenseStatusErrorDto" />
          </div>
        </div>

        <div class="submit-button">
          <AppButton :primary="true" label="新規登録" @click="onClick()"/>
        </div>

        <div class="login-link">
          <AppLink @click="router.push('/login')">すでにアカウントをお持ちの方はこちら</AppLink>
        </div>

      </AppCard>
    </div>
  </div>
</template>

<script lang="ts">
import { ref, computed, onMounted, Ref } from 'vue'
import { useRouter } from 'vue-router'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppCalendar from '@/components/atoms/AppCalendar.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppPassword from '@/components/atoms/AppPassword.vue'
import apiClient from '@/utils/api'
import { codeConstant } from '@/utils/codeConstant'
import { responseStatusConstant } from '@/utils/responseStatusConstant'
import { InputFormErrorDto } from '@/dto/InputFormErrorDto';
import { UsersDto } from '@/dto/usersDto'
export default {
  name: 'SignupPage',
  components: {
    AppLabel,
    AppTextField,
    AppCalendar,
    AppButton,
    AppCard,
    AppLink,
    AppSelect,
    AppPassword,
  },
  setup() {
    /** ルーター */
    const router = useRouter()

    /** ユーザー情報 */
    const usersModel = ref<UsersDto>(new UsersDto());

    /** ローディング状態 */
    const isLoading = ref(false)

    /** エラーオブジェクト */
    const usernameErrorDto = ref([]) as Ref<InputFormErrorDto[]>;
    const passwordErrorDto = ref([]) as Ref<InputFormErrorDto[]>;
    const confirmPasswordErrorDto = ref([]) as Ref<InputFormErrorDto[]>;
    const birthDateErrorDto = ref([]) as Ref<InputFormErrorDto[]>;
    const addressErrorDto = ref([]) as Ref<InputFormErrorDto[]>;
    const licenseStatusErrorDto = ref([]) as Ref<InputFormErrorDto[]>;

    /** 居住地域プルダウン */
    const addressOptions = ref([])

    /** 運転免許の所持状況のプルダウン */
    const licenseStatusLabels = {
      [codeConstant.LICENSE_STATUS.UNLICENSED]: '所持していない',
      [codeConstant.LICENSE_STATUS.LICENSED]: '所持している',
      [codeConstant.LICENSE_STATUS.RETURNED]: '返納',
      [codeConstant.LICENSE_STATUS.EXPIRED]: '失効',
      [codeConstant.LICENSE_STATUS.SUSPENDED]: '停止',
      [codeConstant.LICENSE_STATUS.OTHER]: 'その他',
    }
    const licenseOptions = ref(
      Object.entries(codeConstant.LICENSE_STATUS).map(([key, value]) => ({
        value: value.toString(),
        label: licenseStatusLabels[value]
      }))
    )

    /**
     * 生年月日のcomputedモデル（型安全な変換）
     * UsersDto.birthDateはstring|Date|nullだが、AppCalendarはDate|nullを期待するため変換する
     */
    const birthDateModel = computed({
      get: () => usersModel.value.birthDate instanceof Date ? usersModel.value.birthDate : null,
      set: (value: Date | null) => { usersModel.value.birthDate = value; },
    });

    // 初期表示
    onMounted(() => {
      // 自治体データの取得
      getMunicipalities();
    })

    // 自治体データを取得
    const getMunicipalities = async () => {
      try {
        const response = await apiClient.get('/municipality/all')
        if (response.status === responseStatusConstant.OK) {
          addressOptions.value = response.data.municipalities.map(dto => ({
            value: dto.municipalityCd,
            label: dto.municipalityName
          }))
        }
      } catch (error) {
        console.error('自治体データの取得に失敗しました:', error)
      }
    }

    const onClick = () => {
      console.log('登録ボタンがクリックされました');
    }

    return {
      router,
      usersModel,
      isLoading,
      addressOptions,
      licenseOptions,
      usernameErrorDto,
      passwordErrorDto,
      confirmPasswordErrorDto,
      birthDateErrorDto,
      addressErrorDto,
      licenseStatusErrorDto,
      birthDateModel,
      onClick,
    }
  },
}
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

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

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px 20px;
  margin-bottom: 8px;
}

.form-field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.submit-button {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.login-link {
  display: flex;
  justify-content: center;
  margin-top: 10px;
}
</style>
