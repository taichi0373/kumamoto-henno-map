<template>
  <div class="page">
    <AppBlockUI :blocked="isLoading" />
    <div class="whole">
      <AppCard title="プロフィール編集" :inputStyle="{ width: '100%', maxWidth: '600px' }">
          <div class="form-col">
            <AppMessageBar
              v-if="barErrMode != ''"
              :mode="barErrMode"
              :message="barErrMsg"
              style="width: 100%;"
            ></AppMessageBar>
          </div>

          <div class="form-row-2">
            <div class="form-col">
              <AppLabel :required="true">ユーザー名</AppLabel>
              <AppTextField :input-id="'username'" type="text" v-model="usersModel.username"
                placeholder="ユーザー名を入力してください" :required="true" :error="usernameErrorDto" />
            </div>
            <div class="form-col">
              <AppLabel :id="'email'" :required="true">メールアドレス</AppLabel>
              <AppTextField :input-id="'email'" type="text" v-model="usersModel.email"
                placeholder="メールアドレスを入力してください" :required="true" :error="emailErrorDto" />
            </div>
          </div>
          <div class="form-row-2">
            <div class="form-col">
              <AppLabel :required="true">居住地域</AppLabel>
              <AppSelect :input-id="'address'" v-model="usersModel.address" placeholder="選択してください"
                :options="addressOptions" :filter="true" :required="true" :error="addressErrorDto" />
            </div>
            <div class="form-col">
              <AppLabel :id="'birthDate'" :required="true">生年月日</AppLabel>
              <AppCalendar :input-id="'birthDate'" v-model="usersModel.birthDate" :required="true"
                :error="birthDateErrorDto" />
            </div>
          </div>
          <div class="form-row-2">
            <div class="form-col">
              <AppLabel :required="true">運転免許の所持状況</AppLabel>
              <AppSelect :input-id="'licenseStatus'" v-model="usersModel.licenseStatus" placeholder="選択してください"
                :options="licenseOptions" :required="true" :error="licenseStatusErrorDto" />
            </div>
            <div class="form-col">
              <AppLabel :id="'licenseSurrenderedAt'">運転免許返納日</AppLabel>
              <AppCalendar :input-id="'licenseSurrenderedAt'" v-model="usersModel.licenseSurrenderedAt"
                :error="licenseSurrenderedAtErrorDto" />
            </div>
          </div>
          <div class="form-btn">
            <AppButton :primary="false" label="クリア" @click="clearForm" />
            <AppButton :primary="true" :label="'更新'" @click="updateUsersInfo" />
          </div>

          <div class="form-link">
            <AppLink @click="router.push('/change-password')">パスワードを変更する</AppLink>
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
import apiClient from '@/utils/api'
import AppBlockUI from '@/components/atoms/AppBlockUI.vue'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppCalendar from '@/components/atoms/AppCalendar.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppToastMessage from '@/components/atoms/AppToastMessage.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import { InputFormErrorDto } from '@/dto/InputFormErrorDto'
import { codeConstant } from '@/utils/codeConstant'
import { UsersDto } from '@/dto/usersDto'
import { SelectDto } from '@/dto/selectDto'
import { MunicipalityDto } from '@/dto/municipalityDto'
import { responseStatusConstant } from '@/utils/responseStatusConstant'
import { useAuthStore } from '@/stores/auth'
import { ValidateUtils } from '@/utils/validateUtils'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import { TypeConvertUtils } from '@/utils/typeConvertUtils'
import { MessageUtils } from '@/utils/messageUtils'
import { API_RESPONSE_MESSAGE, MESSAGE_LIST, MESSAGE_NO } from '@/utils/messageConstant'

/** ルーター */
const router = useRouter()
const auth = useAuthStore()

/** ユーザー情報 */
const usersModel = ref<UsersDto>(new UsersDto())

/** エラーオブジェクト */
const usernameErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const emailErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const birthDateErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const addressErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const licenseStatusErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const licenseSurrenderedAtErrorDto = ref([]) as Ref<InputFormErrorDto[]>

/** ローディング */
const isLoading = ref(false)

/** エラーバー */
const barErrMode = ref('') as Ref<string>
const barErrMsg = ref('') as Ref<string>

/** 居住地域プルダウン */
const addressOptions = ref([]) as Ref<SelectDto[]>

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

/**
 * 初期表示
 */
onMounted(async () => {
  // ユーザー情報の取得
  await getUsersInfo()
  // 自治体データの取得
  await getMunicipalities()
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
      barErrMode.value = 'error'
      barErrMsg.value = API_RESPONSE_MESSAGE.DATA_NOT_FOUND
    }
  } catch {
    barErrMode.value = 'error'
    barErrMsg.value = API_RESPONSE_MESSAGE.API_ERROR
  }
}

// 運転免許の所持状況データを取得
const getLicenseStatusOptions = () => {
  licenseOptions.value = Object.entries(codeConstant.LICENSE_STATUS).map(([, value]) => ({
    value: value.toString(),
    label: licenseStatusLabels[value],
    text: licenseStatusLabels[value]
  }));
}

/**
 * ユーザー情報の取得
 */
const getUsersInfo = async () => {
  const userId = auth.user?.id
  if (ValidateUtils.isNullOrEmpty(userId)) {
    return
  }
  try {
    const response = await apiClient.get(`/users/${userId}`)
    if (response.status === responseStatusConstant.OK && response.data) {
      usersModel.value = (response.data as unknown as { data: UsersDto }).data
    } else {
      barErrMode.value = 'error'
      barErrMsg.value = API_RESPONSE_MESSAGE.DATA_NOT_FOUND
    }
  } catch {
    barErrMode.value = 'error'
    barErrMsg.value = API_RESPONSE_MESSAGE.API_ERROR
  }
}

/**
 * ユーザー情報の更新
 */
const updateUsersInfo = async () => {
  // エラーバーをリセット
  barErrMode.value = ''
  barErrMsg.value = ''
  // エラーチェック
  const hasError = checkError()
  // エラーがない場合はAPIを呼び出す
  if (!hasError) {
    const userId = auth.user?.id
    if (ValidateUtils.isNullOrEmpty(userId)) {
      return
    }
    const requestData = {
      userId: usersModel.value.userId,
      username: usersModel.value.username,
      email: usersModel.value.email,
      birthDate: TypeConvertUtils.toStringFromDate(usersModel.value.birthDate),
      address: usersModel.value.address,
      licenseStatus: usersModel.value.licenseStatus,
      licenseSurrenderedAt: TypeConvertUtils.toStringFromDate(usersModel.value.licenseSurrenderedAt)
    }
    isLoading.value = true
    try {
      const response = await apiClient.put(`/users`, requestData)
      if (response.status === responseStatusConstant.OK) {
        ToastMessageUtils.success(API_RESPONSE_MESSAGE.UPDATE_SUCCESS)
        // ユーザー情報の再取得
        await getUsersInfo()
      } else {
        barErrMode.value = 'error'
        barErrMsg.value = API_RESPONSE_MESSAGE.UPDATE_FAILED
      }
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } }
      barErrMode.value = 'error'
      barErrMsg.value = axiosError?.response?.data?.message ?? API_RESPONSE_MESSAGE.API_ERROR
    } finally {
      isLoading.value = false
    }
  }
}

/**
 * フォームのクリア
 */
const clearForm = () => {
  clearError()
  usersModel.value.username = ''
  usersModel.value.email = null
  usersModel.value.birthDate = null
  usersModel.value.address = ''
  usersModel.value.licenseStatus = ''
  usersModel.value.licenseSurrenderedAt = null
}

/**
 * エラークリア
  */
const clearError = () => {
  usernameErrorDto.value.splice(0)
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
  // メールアドレスが未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.email)) {
    emailErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "メールアドレス")
    );
    hasError = true
  } else if (!ValidateUtils.isEmail(usersModel.value.email ?? '')) {
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