<template>
  <div class="page">
    <div class="whole">
      <AppCard title="新規登録">

        <div class="form-col">
          <AppMessageBar v-if="barErrMode != ''" :mode="barErrMode" :message="barErrMsg" style="width: 100%;">
          </AppMessageBar>
        </div>

        <div class="form-row">
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
            <AppLabel :id="'birthDate'" :required="true">生年月日</AppLabel>
            <AppCalendar :input-id="'birthDate'" v-model="usersModel.birthDate" :required="true" :error="birthDateErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'address'" :required="true">居住地域</AppLabel>
            <AppSelect id="address" v-model="usersModel.address" placeholder="選択してください" :options="addressOptions"
              :required="true" :error="addressErrorDto" />
          </div>

          <div class="form-col">
            <AppLabel :id="'licenseStatus'" :required="true">運転免許の所持状況</AppLabel>
            <AppSelect :id="'licenseStatus'" v-model="usersModel.licenseStatus" placeholder="選択してください"
              :options="licenseOptions" :required="true" :error="licenseStatusErrorDto" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Ref } from 'vue'
import { useRouter } from 'vue-router'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppCalendar from '@/components/atoms/AppCalendar.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppPassword from '@/components/atoms/AppPassword.vue'
import apiClient from '@/utils/api'
import { codeConstant } from '@/utils/codeConstant'
import { responseStatusConstant } from '@/utils/responseStatusConstant'
import { InputFormErrorDto } from '@/dto/InputFormErrorDto'
import { ErrorMessageKbn, MESSAGE_LIST, MESSAGE_NO } from '@/utils/messageConstant'
import { ValidateUtils } from '@/utils/validateUtils'
import { UsersDto } from '@/dto/usersDto'
import { MessageUtils } from '@/utils/messageUtils'

/** ルーター */
const router = useRouter()

/** ユーザー情報 */
const usersModel = ref<UsersDto>(new UsersDto())

/** エラーオブジェクト */
const usernameErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const passwordErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const confirmPasswordErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const birthDateErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const addressErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const licenseStatusErrorDto = ref([]) as Ref<InputFormErrorDto[]>

/** エラーバー */
const warningMsgList = ref([]) as Ref<InputFormErrorDto[]>
const barErrMode = ref('') as Ref<string>
const barErrMsg = ref('') as Ref<string>

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

// 初期表示
onMounted(() => {
  // 自治体データの取得
  getMunicipalities()
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

/**
 * 新規登録処理
  */
const onClick = () => {
  console.log('登録ボタンがクリックされました')
  // エラーチェック
  checkError()
}

/**
 * エラークリア
  */
const clearError = () => {
  usernameErrorDto.value.splice(0)
  passwordErrorDto.value.splice(0)
  confirmPasswordErrorDto.value.splice(0)
  birthDateErrorDto.value.splice(0)
  addressErrorDto.value.splice(0)
  licenseStatusErrorDto.value.splice(0)
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
  // パスワード確認が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.confirmPassword)) {
    confirmPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "パスワード確認")
    );
    if (result !== ErrorMessageKbn.ERROR) {
      result = MessageUtils.getMessageType(MESSAGE_LIST, MESSAGE_NO.MSG_001);
      warningMsgList.value.push(
        MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "パスワード確認")
      );
    }

  }
  // パスワードとパスワード確認が一致しない場合はエラー
  if (!ValidateUtils.isNullOrEmpty(usersModel.value.password) &&
    !ValidateUtils.isNullOrEmpty(usersModel.value.confirmPassword) &&
    usersModel.value.password !== usersModel.value.confirmPassword) {
    confirmPasswordErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_008, "パスワード", "パスワード確認")
    );
    if (result !== ErrorMessageKbn.ERROR) {
      result = MessageUtils.getMessageType(MESSAGE_LIST, MESSAGE_NO.MSG_008);
      warningMsgList.value.push(
        MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_008, "パスワード", "パスワード確認")
      );
    }
  }
  // 生年月日が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.birthDate)) {
    birthDateErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "生年月日")
    );
    if (result !== ErrorMessageKbn.ERROR) {
      result = MessageUtils.getMessageType(MESSAGE_LIST, MESSAGE_NO.MSG_001);
      warningMsgList.value.push(
        MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "生年月日")
      );
    }
  }
  // 居住地域が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.address)) {
    addressErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "居住地域")
    );
    if (result !== ErrorMessageKbn.ERROR) {
      result = MessageUtils.getMessageType(MESSAGE_LIST, MESSAGE_NO.MSG_001);
      warningMsgList.value.push(
        MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "居住地域")
      );
    }
  }
  // 運転免許の所持状況が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(usersModel.value.licenseStatus)) {
    licenseStatusErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "運転免許の所持状況")
    );
    if (result !== ErrorMessageKbn.ERROR) {
      result = MessageUtils.getMessageType(MESSAGE_LIST, MESSAGE_NO.MSG_001);
      warningMsgList.value.push(
        MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "運転免許の所持状況")
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
