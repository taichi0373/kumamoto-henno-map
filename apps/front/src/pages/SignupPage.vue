<template>
  <div class="page">
    <AppBlockUI :blocked="isLoading" />
    <div class="whole">
      <AppCard title="新規登録" :inputStyle="{ width: '100%', maxWidth: '600px' }">

        <div class="form-row-2">
          <div class="form-col">
            <AppLabel :id="'username'" :required="true">ユーザー名</AppLabel>
            <AppTextField :input-id="'username'" :type="'text'" v-model="usersModel.username"
              placeholder="ユーザー名を入力してください" :required="true" :error="usernameErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'password'" :required="true">パスワード</AppLabel>
            <AppPassword :input-id="'password'" v-model="usersModel.password" placeholder="パスワードを入力してください"
              :error="passwordErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'confirmPassword'" :required="true">パスワード確認</AppLabel>
            <AppPassword :input-id="'confirmPassword'" v-model="usersModel.confirmPassword"
              placeholder="パスワードを再入力してください" :error="confirmPasswordErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'email'" :required="true">メールアドレス</AppLabel>
            <AppTextField :input-id="'email'" :type="'text'" v-model="usersModel.email"
              placeholder="メールアドレスを入力してください" :required="true" :error="emailErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'address'" :required="true">居住地域</AppLabel>
            <AppSelect id="address" v-model="usersModel.address" placeholder="選択してください" :options="addressOptions"
              :filter="true" :required="true" :error="addressErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'birthDate'" :required="true">生年月日</AppLabel>
            <AppCalendar :input-id="'birthDate'" v-model="usersModel.birthDate" :required="true" :error="birthDateErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'licenseStatus'" :required="true">運転免許の所持状況</AppLabel>
            <AppSelect :id="'licenseStatus'" v-model="usersModel.licenseStatus" placeholder="選択してください"
              :options="licenseOptions" :required="true" :error="licenseStatusErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'licenseSurrenderedAt'">運転免許返納日</AppLabel>
            <AppCalendar :input-id="'licenseSurrenderedAt'" v-model="usersModel.licenseSurrenderedAt"
              :error="licenseSurrenderedAtErrorDto" />
          </div>
        </div>

        <div class="form-btn">
          <AppButton :primary="true" label="新規登録" @click="onClick()" />
        </div>

        <div class="form-link">
          <AppLink @click="router.push('/login')">すでにアカウントをお持ちの方はこちら</AppLink>
        </div>

      </AppCard>
    </div>
    <!-- トーストメッセージ -->
    <AppToastMessage />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Ref } from 'vue'
import { useRouter } from 'vue-router'
import AppBlockUI from '@/components/atoms/AppBlockUI.vue'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppToastMessage from '@/components/atoms/AppToastMessage.vue'
import AppCalendar from '@/components/atoms/AppCalendar.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppPassword from '@/components/atoms/AppPassword.vue'
import apiClient from '@/utils/api'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import { codeConstant } from '@/utils/codeConstant'
import { responseStatusConstant } from '@/utils/responseStatusConstant'
import { InputFormErrorDto } from '@/dto/InputFormErrorDto'
import { API_RESPONSE_MESSAGE, MESSAGE_LIST, MESSAGE_NO } from '@/utils/messageConstant'
import { ValidateUtils } from '@/utils/validateUtils'
import { UsersDto } from '@/dto/usersDto'
import { TypeConvertUtils } from '@/utils/typeConvertUtils'
import { SelectDto } from '@/dto/selectDto'
import { MunicipalityDto } from '@/dto/municipalityDto'
import { MessageUtils } from '@/utils/messageUtils'

/** ルーター */
const router = useRouter()

/** ユーザー情報 */
const usersModel = ref<UsersDto>(new UsersDto())

/** エラーオブジェクト */
const usernameErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const passwordErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const confirmPasswordErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const emailErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const birthDateErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const addressErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const licenseStatusErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const licenseSurrenderedAtErrorDto = ref([]) as Ref<InputFormErrorDto[]>

/** 居住地域プルダウン */
const addressOptions = ref([]) as Ref<SelectDto[]>

/** ローディング */
const isLoading = ref(false)

/** 運転免許の所持状況のプルダウン */
const licenseStatusLabels = {
  [codeConstant.LICENSE_STATUS.UNLICENSED]: '所持していない',
  [codeConstant.LICENSE_STATUS.LICENSED]: '所持している',
  [codeConstant.LICENSE_STATUS.RETURNED]: '返納',
  [codeConstant.LICENSE_STATUS.EXPIRED]: '失効',
  [codeConstant.LICENSE_STATUS.SUSPENDED]: '停止',
  [codeConstant.LICENSE_STATUS.OTHER]: 'その他',
}
const licenseOptions = ref([]) as Ref<SelectDto[]>


// 初期表示
onMounted(() => {
  // 自治体データの取得
  getMunicipalities()
  // 運転免許の所持状況プルダウン取得
  getLicenseStatusOptions()
})

// 自治体データを取得
const getMunicipalities = async () => {
  try {
    const response = await apiClient.get('/municipality/all')
    if (response.status === responseStatusConstant.OK && response.data) {
      const municipalities = ((response.data as unknown) as { data: MunicipalityDto[] }).data
      addressOptions.value = municipalities.map((dto) => ({
        value: dto.municipalityCd,
        label: dto.municipalityName,
        text: dto.municipalityKana
      }))
    } else {
      ToastMessageUtils.error(API_RESPONSE_MESSAGE.DATA_NOT_FOUND)
    }
  } catch (error) {
    ToastMessageUtils.error(API_RESPONSE_MESSAGE.API_ERROR)
  }
}

// 運転免許の所持状況プルダウン取得
const getLicenseStatusOptions = () => {
  licenseOptions.value = Object.entries(codeConstant.LICENSE_STATUS).map(([key, value]) => ({
    value: value.toString(),
    label: licenseStatusLabels[value],
    text: licenseStatusLabels[value]
  }));
}

/**
 * 新規登録処理
  */
const onClick = async () => {
  // エラーチェック
  const hasError = checkError()
  // エラーがない場合はAPIを呼び出す
  if (!hasError) {
    // APIリクエスト用データの作成
    const requestData = {
      username: usersModel.value.username,
      password: usersModel.value.password,
      confirmPassword: usersModel.value.confirmPassword,
      email: usersModel.value.email,
      birthDate: TypeConvertUtils.toStringFromDate(usersModel.value.birthDate),
      address: usersModel.value.address,
      licenseStatus: usersModel.value.licenseStatus,
      licenseSurrenderedAt: TypeConvertUtils.toStringFromDate(usersModel.value.licenseSurrenderedAt)
    }

    isLoading.value = true
    try {
      const response = await apiClient.post('/users/signup', requestData)
      if (response.status === responseStatusConstant.CREATED) {
        isLoading.value = false
        router.push('/login')
      } else {
        ToastMessageUtils.error(API_RESPONSE_MESSAGE.CREATE_FAILED)
      }
    } catch {
      ToastMessageUtils.error(API_RESPONSE_MESSAGE.API_ERROR)
    } finally {
      isLoading.value = false
    }
  }
}

/**
 * エラークリア
  */
const clearError = () => {
  usernameErrorDto.value.splice(0)
  passwordErrorDto.value.splice(0)
  confirmPasswordErrorDto.value.splice(0)
  emailErrorDto.value.splice(0)
  birthDateErrorDto.value.splice(0)
  addressErrorDto.value.splice(0)
  licenseStatusErrorDto.value.splice(0)
  licenseSurrenderedAtErrorDto.value.splice(0)
}

/**
 * エラーチェック
 * @returns {boolean} エラーがある場合はtrue
  */
function checkError(): boolean {
  // エラー初期化
  clearError()
  let hasError = false

  // ユーザ名が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.username)) {
    usernameErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "ユーザ名")
    );
    hasError = true
  }

  // パスワードが未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.password)) {
    passwordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "パスワード")
    );
    hasError = true
  }
  // パスワード確認が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.confirmPassword)) {
    confirmPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "パスワード確認")
    );
    hasError = true
  }
  // パスワードとパスワード確認が一致しない場合はエラー
  if (!ValidateUtils.isNullOrEmpty(usersModel.value.password) &&
    !ValidateUtils.isNullOrEmpty(usersModel.value.confirmPassword) &&
    usersModel.value.password !== usersModel.value.confirmPassword) {
    confirmPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_008, "パスワード", "パスワード確認")
    );
    hasError = true
  }
  // メールアドレスが未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.email)) {
    emailErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "メールアドレス")
    );
    hasError = true
  } else if (!ValidateUtils.isEmail(usersModel.value.email ?? '')) {
    // メールアドレスの形式チェック
    emailErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_004, "メールアドレス")
    );
    hasError = true
  }
  // 生年月日が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.birthDate)) {
    birthDateErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "生年月日")
    );
    hasError = true
  }
  // 居住地域が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.address)) {
    addressErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "居住地域")
    );
    hasError = true
  }
  // 運転免許の所持状況が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.licenseStatus)) {
    licenseStatusErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "運転免許の所持状況")
    );
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
