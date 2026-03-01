<template>
  <div class="page">

    <div class="whole">
      <AppCard title="プロフィール編集" :inputStyle="{ width: '100%', maxWidth: '600px' }">
        <form @submit.prevent="handleUpdateProfile">
          <div class="form-row-2">
            <div class="form-col">
              <AppLabel :required="true">ユーザー名</AppLabel>
              <AppTextField :input-id="'username'" type="text" v-model="usersModel.username"
                placeholder="ユーザー名を入力してください" :required="true" :error="usernameErrorDto" />
            </div>
            <div class="form-col">
              <AppLabel :id="'birthDate'" :required="true">生年月日</AppLabel>
              <AppCalendar :input-id="'birthDate'" v-model="usersModel.birthDate" :required="true"
                :error="birthDateErrorDto" />
            </div>
          </div>

          <div class="form-row-2">
            <div class="form-col">
              <AppLabel :required="true">居住地域</AppLabel>
              <AppSelect :input-id="'address'" v-model="usersModel.address" placeholder="選択してください"
                :options="addressOptions" :required="true" :error="addressErrorDto" />
            </div>
            <div class="form-col">
              <AppLabel :required="true">運転免許の所持状況</AppLabel>
              <AppSelect :input-id="'licenseStatus'" v-model="usersModel.licenseStatus" placeholder="選択してください"
                :options="licenseOptions" :required="true" :error="licenseStatusErrorDto" />
            </div>
          </div>
          <div class="form-btn">
            <AppButton type="submit" :label="'更新'" />
          </div>
        </form>

      </AppCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Ref } from 'vue'
import { useRouter } from 'vue-router'
import apiClient from '@/utils/api'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppCalendar from '@/components/atoms/AppCalendar.vue'
import { InputFormErrorDto } from '@/dto/InputFormErrorDto'
import { codeConstant } from '@/utils/codeConstant'
import { UsersDto } from '@/dto/usersDto'
import { SelectDto } from '@/dto/selectDto'
import { MunicipalityDto } from '@/dto/municipalityDto'
import { responseStatusConstant } from '@/utils/responseStatusConstant'

/** ルーター */
const router = useRouter()

/** ユーザー情報 */
const usersModel = ref<UsersDto>(new UsersDto())

/** エラーオブジェクト */
const usernameErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const birthDateErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const addressErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const licenseStatusErrorDto = ref([]) as Ref<InputFormErrorDto[]>

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
  // ユーザーデータの取得
  await loadProfile()
  // 自治体データの取得
  await getMunicipalities()
  // 運転免許の所持状況データの取得
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
    }
  } catch (error) {
    console.error('自治体データの取得に失敗しました:', error)
  }
}

// 運転免許の所持状況データを取得
const getLicenseStatusOptions = () => {
  licenseOptions.value = Object.entries(codeConstant.LICENSE_STATUS).map(([key, value]) => ({
    value: value.toString(),
    label: licenseStatusLabels[value],
    text: licenseStatusLabels[value]
  }));
}

/**
 * プロフィール情報のロード
 */
const loadProfile = async () => {
  console.log("プロフィール情報のロード")
}

/**
 * プロフィール更新処理
 */
const handleUpdateProfile = async () => {
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