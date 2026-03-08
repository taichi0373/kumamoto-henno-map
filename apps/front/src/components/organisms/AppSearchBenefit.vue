<template>
  <div class="p-2">
    <form @submit.prevent="searchBenefits(searchBenefit)">

      <div class="form-row-1">
        <div class="form-col">
          <AppLabel :id="'address'">居住地域</AppLabel>
          <AppSelect id="address" v-model="searchBenefit.address" :options="addressOptions" :filter="true"
            :placeholder="'選択してください'" />
        </div>
        <div class="form-col">
          <AppLabel :id="'license-status'">運転免許の所持状況</AppLabel>
          <AppSelect id="license-status" v-model="searchBenefit.licenseStatus" :options="licenseOptions"
            :optionValue="'value'" :placeholder="'選択してください'" />
        </div>
        <div class="form-col">
          <AppLabel :id="'age'">年齢</AppLabel>
          <AppNumberField id="age" v-model="searchBenefit.age" :max="999" :placeholder="'年齢を入力してください'" />
        </div>
      </div>

      <div class="form-btn">
        <AppButton type="button" :label="'クリア'" :primary="false" :icon="'pi pi-trash'" @click="clearConditions" />
        <AppButton type="submit" :label="'検索'" :primary="true" :icon="'pi pi-search'" :loading="isLoading"
          :disabled="isLoading" />
      </div>

    </form>
  </div>

  <div class="p-2" v-if="hasSearched">
    <AppAlert v-if="benefitResults.length === 0 && !isLoading" :variant="'error'"
      :message="'条件に一致する特典がありません'" />

    <div v-if="!isLoading">
      <template v-for="benefit in benefitResults" :key="`${benefit.benefitId}_${benefit.eligibilityId ?? ''}`">
        <AppCard class="mb-3">
          <template #title>{{ benefit.benefitName }}</template>
          <!-- 特典内容： -->
          <p>特典内容：{{ benefit.benefitDetail }}</p>
          <!-- 条件 -->
          <ul style="list-style: none; padding-left: 0;">
            <!-- 対象年齢 -->
            <li v-if="benefit.minAge || benefit.maxAge">
              <i class="pi pi-user"></i>
              対象年齢：{{
                benefit.minAge ? `${benefit.minAge}歳以上` : '' }} ～ {{ benefit.maxAge ? `${benefit.maxAge}歳以下` : '' }}
            </li>
            <!-- 自治体名 -->
            <li v-if="benefit.municipalityName">
              <i class="pi pi-map-marker"></i>
              対象地域：{{ benefit.municipalityName }}
            </li>
            <!-- 運転免許の所持状況 -->
            <li v-if="benefit.licenseStatus">
              <i class="pi pi-id-card"></i>
              運転免許の所持状況：{{ getLicenseStatusName(benefit.licenseStatus) }}
            </li>
            <!-- 電話番号 -->
            <li v-if="benefit.phoneNumber">
              <i class="pi pi-phone"></i>
              電話番号：{{ benefit.phoneNumber }}
            </li>
            <!-- 備考 -->
            <li v-if="benefit.eligibilityNote">
              <small>※{{ benefit.eligibilityNote }}</small>
            </li>
          </ul>
          <AppLink v-if="benefit.benefitUrl" :to="benefit.benefitUrl">詳細を見る</AppLink>
        </AppCard>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Ref } from 'vue'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppAlert from '@/components/atoms/AppAlert.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import AppNumberField from '@/components/atoms/AppNumberField.vue'
import apiClient from '@/utils/api'
import ToastMessageUtils from '@/utils/toastMessageUtils'
import { codeConstant } from '@/utils/codeConstant'
import { responseStatusConstant } from '@/utils/responseStatusConstant'
import { API_RESPONSE_MESSAGE } from '@/utils/messageConstant'
import { SelectDto } from '@/dto/selectDto'
import { MunicipalityDto } from '@/dto/municipalityDto'
import { SearchBenefitDto } from '@/dto/searchBenefitDto'
import { BenefitDetailDto } from '@/dto/benefitDetailDto'

const emit = defineEmits<{
  (e: 'show-benefit-on-map', benefit: BenefitDetailDto): void;
}>();

/** 検索結果があるかどうか */
const hasSearched = ref(false)

/** ローディング状態 */
const isLoading = ref(false)

/** 検索条件 */
const searchBenefit = ref<SearchBenefitDto>(new SearchBenefitDto())
/** 検索結果 */
const benefitResults = ref([]) as Ref<BenefitDetailDto[]>

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

// 自治体データを取得
const getMunicipalities = async () => {
  try {
    const response = await apiClient.get('/municipality/all')
    if (response.status === responseStatusConstant.OK && response.data) {
      console.log('自治体データの取得に成功しました:', response.data)
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

// 運転免許の所持状況コードから表示名を取得
const getLicenseStatusName = (code: string) => {
  return licenseStatusLabels[code] || '不明';
}

// 特典検索API呼び出し
const searchBenefits = async (conditions) => {
  isLoading.value = true
  const requestData = {
    age: conditions.age,
    licenseStatus: conditions.licenseStatus,
    municipalityCd: conditions.address,
  }

  apiClient.post('/benefit/search', requestData)
    .then((response) => {
      if (response.status === responseStatusConstant.OK) {
        const data = ((response.data as unknown) as { data: BenefitDetailDto[] }).data
        benefitResults.value = data || []
        hasSearched.value = true
      } else {
        ToastMessageUtils.error(API_RESPONSE_MESSAGE.READ_FAILED)
      }
    })
    .catch(() => {
      ToastMessageUtils.error(API_RESPONSE_MESSAGE.API_ERROR)
    })
    .finally(() => {
      isLoading.value = false
    });
}

// 条件クリア
const clearConditions = () => {
  searchBenefit.value = new SearchBenefitDto()
}

// 初期表示
onMounted(() => {
  // 自治体データの取得
  getMunicipalities()
  // 運転免許の所持状況データの取得
  getLicenseStatusOptions()
})

</script>

<style scoped lang="scss">
@use "@/assets/scss/base";
</style>