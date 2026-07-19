<template>
  <div class="p-2">
   <form @submit.prevent="searchBenefits()">
      <div class="form-row-1">
        <div class="form-col form-col--wide">
          <AppLabel :id="'keyword'">フリーワード</AppLabel>
          <AppTextField :input-id="'keyword'" :type="'text'" v-model="searchBenefit.keyword"
             :placeholder="'商品券'" />
        </div>
      </div>
      <div class="form-row-sidebar-2 mt-4">
        <div class="form-col">
          <AppLabel :id="'category'">カテゴリ</AppLabel>
          <AppSelect :input-id="'category'" v-model="searchBenefit.categoryCd"
            :options="categoryOptions" :placeholder="''" />
        </div>
        <div class="form-col">
          <AppLabel :id="'address'">居住地域</AppLabel>
          <AppSelect :input-id="'address'" v-model="searchBenefit.address" :options="addressOptions" :filter="true"
            :placeholder="''" />
        </div>
      </div>
      <div class="form-row-sidebar-2 mt-4">
        <div class="form-col">
          <AppLabel :id="'license-status'">運転免許の所持状況</AppLabel>
          <AppSelect :input-id="'license-status'" v-model="searchBenefit.licenseStatus" :options="licenseOptions"
            :optionValue="'value'" :placeholder="''" />
        </div>
        <div class="form-col">
          <AppLabel :id="'age'">年齢</AppLabel>
          <AppNumberField :input-id="'age'" v-model="searchBenefit.age" :max="999" :placeholder="''" />
        </div>
      </div>
      <div class="form-row-1 mt-4">
        <div class="form-col form-col--wide">
          <AppLabel :id="'radius'">
            現在地からの距離：
            <span class="radius-value">{{ radiusLabel }}</span>
          </AppLabel>
          <AppSlider
            input-id="radius"
            v-model="radiusKmValue"
            :min="0"
            :max="10"
            :step="1"
          />
        </div>
      </div>
      <div class="form-btn">
        <AppButton :label="'クリア'" :primary="false" :icon="'pi pi-trash'" @click="clearConditions" />
        <AppButton type="submit" :label="'検索'" :primary="true" :icon="'pi pi-search'" :disabled="isLoading" />
      </div>
      <!-- 現在地周辺フィルターボタン -->
      <div class="form-btn mt-2">
        <AppButton
          label="現在地周辺"
          severity="secondary"
          icon="pi pi-map-marker"
          @click="filterByCurrentLocation"
        />
      </div>
    </form>
  </div>

  <div class="p-2" v-if="hasSearched">
    <!-- ローディング -->
    <div v-if="isLoading" class="loading-icon">
      <AppProgressSpinner/>
    </div>
    <!-- 検索結果 -->
    <AppAlert v-if="benefitResults.length === 0 && !isLoading" :variant="'error'"
      :message="'条件に一致する特典がありません'" />

    <div v-if="!isLoading">
      <!-- カテゴリ未指定: アコーディオン表示 -->
      <template v-if="!searchBenefit.categoryCd">
        <div
          v-for="group in groupedBenefitResults"
          :key="group.categoryCd"
          class="category-accordion mb-3"
        >
          <!-- カテゴリヘッダー -->
          <button
            type="button"
            class="category-accordion__header"
            @click="toggleCategory(group.categoryCd)"
          >
            <span class="category-accordion__title">
              {{ group.categoryName }} <span class="category-accordion__count">{{ group.benefits.length }}件</span>
            </span>
            <i :class="openCategories.has(group.categoryCd) ? 'pi pi-chevron-down' : 'pi pi-chevron-right'" />
          </button>
          <!-- 特典リスト -->
          <Transition name="accordion">
            <div v-show="openCategories.has(group.categoryCd)" class="category-accordion__body">
              <template v-for="benefit in group.benefits" :key="`${benefit.benefitId}_${benefit.eligibilityId ?? ''}`">
                <AppCard class="mb-3" :hoverable="true" @click="emit('show-benefit-on-map', benefit)">
                  <template #title>{{ benefit.benefitName }}</template>
                  <!-- 特典内容： -->
                  <p>特典内容：{{ benefit.benefitDetail }}</p>
                  <!-- 条件 -->
                  <ul style="list-style: none; padding-left: 0;">
                    <!-- 対象年齢 -->
                    <li v-if="benefit.minAge != null || benefit.maxAge != null">
                      <i class="pi pi-user"></i>
                      対象年齢：{{ formatAgeRange(benefit.minAge, benefit.maxAge) }}
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
                  <!-- 最終確認日 -->
                  <div :class="getLastConfirmedClass(benefit.lastConfirmedDate)" class="last-confirmed">
                    最終確認日: {{ formatLastConfirmedDate(benefit.lastConfirmedDate) }}
                  </div>
                  <!-- 情報報告ボタン -->
                  <AppButton
                    label="情報を報告"
                    :primary="false"
                    size="small"
                    @click.stop="openReportDialog(benefit)"
                  />
                </AppCard>
              </template>
            </div>
          </Transition>
        </div>
      </template>

      <!-- カテゴリ指定済み: フラット表示 -->
      <template v-else>
        <template v-for="benefit in benefitResults" :key="`${benefit.benefitId}_${benefit.eligibilityId ?? ''}`">
          <AppCard class="mb-3" :hoverable="true" @click="emit('show-benefit-on-map', benefit)">
            <template #title>{{ benefit.benefitName }}</template>
            <!-- 特典内容： -->
            <p>特典内容：{{ benefit.benefitDetail }}</p>
            <!-- 条件 -->
            <ul style="list-style: none; padding-left: 0;">
              <!-- 対象年齢 -->
              <li v-if="benefit.minAge != null || benefit.maxAge != null">
                <i class="pi pi-user"></i>
                対象年齢：{{ formatAgeRange(benefit.minAge, benefit.maxAge) }}
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
            <!-- 最終確認日 -->
            <div :class="getLastConfirmedClass(benefit.lastConfirmedDate)" class="last-confirmed">
              最終確認日: {{ formatLastConfirmedDate(benefit.lastConfirmedDate) }}
            </div>
            <!-- 情報報告ボタン -->
            <AppButton
              label="情報を報告"
              :primary="false"
              @click.stop="openReportDialog(benefit)"
            />
          </AppCard>
        </template>
      </template>
    </div>
  </div>

  <!-- 情報報告確認ダイアログ -->
  <AppDialog
    v-model="isReportDialogVisible"
    header="情報を報告"
    :modal="true"
  >
    <p>「{{ reportTarget?.benefitName }}」の情報に誤りがありますか？</p>
    <p>管理者に報告します。</p>
    <template #footer>
      <AppButton
        label="キャンセル"
        :primary="false"
        @click="isReportDialogVisible = false"
      />
      <AppButton
        label="報告する"
        :primary="true"
        :loading="isReporting"
        @click="submitReport"
      />
    </template>
  </AppDialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { Ref } from 'vue'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppAlert from '@/components/atoms/AppAlert.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import AppNumberField from '@/components/atoms/AppNumberField.vue'
import AppSlider from '@/components/atoms/AppSlider.vue'
import AppProgressSpinner from '@/components/atoms/AppProgressSpinner.vue'
import AppDialog from '@/components/atoms/AppDialog.vue'
import apiClient from '@/utils/api'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import { codeConstant } from '@/utils/codeConstant'
import { responseStatusConstant } from '@/utils/responseStatusConstant'
import { API_RESPONSE_MESSAGE } from '@/utils/messageConstant'
import { SelectDto } from '@/dto/selectDto'
import { MunicipalityDto } from '@/dto/municipalityDto'
import { SearchBenefitDto } from '@/dto/searchBenefitDto'
import { BenefitDetailDto } from '@/dto/benefitDetailDto'

const emit = defineEmits<{
  (e: 'show-benefit-on-map', benefit: BenefitDetailDto): void;
  (e: 'update-benefit-markers', benefits: BenefitDetailDto[]): void;
  (e: 'clear-benefit-markers'): void;
}>();

/** 検索結果があるかどうか */
const hasSearched = ref(false)

/** ローディング状態 */
const isLoading = ref(false)

/** 検索条件 */
const searchBenefit = ref<SearchBenefitDto>(new SearchBenefitDto())
/** 検索結果 */
const benefitResults = ref([]) as Ref<BenefitDetailDto[]>

/** 展開中カテゴリコードのセット */
const openCategories = ref(new Set<string>())

/** 居住地域プルダウン */
const addressOptions = ref([]) as Ref<SelectDto[]>

/** カテゴリプルダウン */
const categoryOptions = ref([]) as Ref<SelectDto[]>

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

/** 報告ダイアログの表示状態 */
const isReportDialogVisible = ref(false)
/** 報告対象の特典 */
const reportTarget = ref<BenefitDetailDto | null>(null)
/** 報告送信中フラグ */
const isReporting = ref(false)

/** カテゴリ別にグループ化した検索結果（displayOrder 昇順） */
const groupedBenefitResults = computed(() => {
  const groups = new Map<string, {
    categoryCd: string; categoryName: string; displayOrder: number; benefits: BenefitDetailDto[]
  }>()
  for (const benefit of benefitResults.value) {
    const cd = benefit.categoryCd ?? 'OTHER'
    if (!groups.has(cd)) {
      groups.set(cd, {
        categoryCd: cd,
        categoryName: benefit.categoryName ?? 'その他',
        displayOrder: benefit.displayOrder ?? 9999,
        benefits: []
      })
    }
    groups.get(cd)!.benefits.push(benefit)
  }
  return Array.from(groups.values()).sort((a, b) => a.displayOrder - b.displayOrder)
})

/** スライダー値（0 = 距離指定なし、1〜20 = 実際のkm） */
const radiusKmValue = computed({
  get: () => searchBenefit.value.radiusKm ?? 0,
  set: (value: number) => {
    searchBenefit.value.radiusKm = value === 0 ? undefined : value
  },
})

/** 距離ラベル表示文言 */
const radiusLabel = computed(() =>
  radiusKmValue.value === 0 ? '距離指定なし' : `${radiusKmValue.value} km 以内`
)

// カテゴリデータを取得
const getCategories = async () => {
  try {
    const response = await apiClient.get('/benefit/categories')
    if (response.status === responseStatusConstant.OK && response.data) {
      const categories = ((response.data as unknown) as { data: { categoryCd: string; categoryName: string }[] }).data
      categoryOptions.value = categories.map((c) => ({
        value: c.categoryCd,
        label: c.categoryName,
        text: c.categoryName
      }))
    }
  } catch (error) {
    console.error('カテゴリデータの取得に失敗しました:', error)
  }
}

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
  licenseOptions.value = Object.entries(codeConstant.LICENSE_STATUS).map(([, value]) => ({
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
const searchBenefits = async () => {
  // 距離が指定されている場合、GPS位置情報を取得してから検索
  if (searchBenefit.value.radiusKm) {
    if (!navigator.geolocation) {
      ToastMessageUtils.error('このブラウザはGPS位置情報に対応していません')
      return
    }
    try {
      const position = await new Promise<GeolocationPosition>((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(resolve, reject)
      })
      searchBenefit.value.latitude = position.coords.latitude
      searchBenefit.value.longitude = position.coords.longitude
    } catch {
      ToastMessageUtils.error('位置情報の取得に失敗しました。ブラウザの設定で位置情報の使用を許可してください')
      return
    }
  } else {
    searchBenefit.value.latitude = undefined
    searchBenefit.value.longitude = undefined
  }

  isLoading.value = true
  hasSearched.value = true
  const conditions = searchBenefit.value
  const requestData = {
    age: conditions.age,
    licenseStatus: conditions.licenseStatus,
    municipalityCd: conditions.address,
    keyword: conditions.keyword,
    categoryCd: conditions.categoryCd,
    latitude: conditions.latitude,
    longitude: conditions.longitude,
    radiusKm: conditions.radiusKm,
  }

  try {
    const response = await apiClient.post('/benefit/search', requestData)
    if (response.status === responseStatusConstant.OK) {
      const data = ((response.data as unknown) as { data: BenefitDetailDto[] }).data
      benefitResults.value = data || []
      emit('update-benefit-markers', benefitResults.value)
    } else {
      ToastMessageUtils.error(API_RESPONSE_MESSAGE.READ_FAILED)
      hasSearched.value = false
    }
  } catch {
    ToastMessageUtils.error(API_RESPONSE_MESSAGE.API_ERROR)
    hasSearched.value = false
  } finally {
    isLoading.value = false
    /** 検索完了後: 全カテゴリ展開 */
    openCategories.value = new Set(groupedBenefitResults.value.map(g => g.categoryCd))
  }
}

// 条件クリア
const clearConditions = () => {
  searchBenefit.value = new SearchBenefitDto()
  searchBenefit.value.latitude = undefined
  searchBenefit.value.longitude = undefined
  searchBenefit.value.radiusKm = undefined
  benefitResults.value = []
  hasSearched.value = false
  openCategories.value = new Set()
  emit('clear-benefit-markers')
}

/** 対象年齢の表示用文言を組み立て */
const formatAgeRange = (minAge?: number | null, maxAge?: number | null): string => {
  const hasMin = minAge != null
  const hasMax = maxAge != null
  if (hasMin && hasMax) {
    return `${minAge}歳以上～${maxAge}歳以下`
  }
  if (hasMin) {
    return `${minAge}歳以上`
  }
  if (hasMax) {
    return `${maxAge}歳以下`
  }
  return ''
}

/**
 * 最終確認日のCSSクラスを返す（半年以上前: 警告色）
 */
const getLastConfirmedClass = (date: string | null | undefined): string => {
  if (!date) return 'last-confirmed--unknown';
  const sixMonthsAgo = new Date();
  sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6);
  return new Date(date) < sixMonthsAgo
    ? 'last-confirmed--warning'
    : 'last-confirmed--normal';
};

/**
 * 最終確認日を日本語形式でフォーマットする
 */
const formatLastConfirmedDate = (date: string | null | undefined): string => {
  if (!date) return '未確認';
  return new Date(date).toLocaleDateString('ja-JP', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
};

/** アコーディオンの開閉を切り替える */
const toggleCategory = (cd: string) => {
  if (openCategories.value.has(cd)) {
    openCategories.value.delete(cd)
  } else {
    openCategories.value.add(cd)
  }
  /** Set の変更を Vue に検知させるため再代入 */
  openCategories.value = new Set(openCategories.value)
}

/** 報告ダイアログを開く */
const openReportDialog = (benefit: BenefitDetailDto) => {
  reportTarget.value = benefit
  isReportDialogVisible.value = true
}

/** 報告を送信する */
const submitReport = async () => {
  if (!reportTarget.value) return
  isReporting.value = true
  try {
    await apiClient.post(`/benefit/${reportTarget.value.benefitId}/report`, {})
    ToastMessageUtils.success('報告を送信しました。ご協力ありがとうございます')
    isReportDialogVisible.value = false
  } catch {
    ToastMessageUtils.error('報告の送信に失敗しました')
  } finally {
    isReporting.value = false
  }
}

// 初期表示
onMounted(() => {
  // 自治体データの取得
  getMunicipalities()
  // 運転免許の所持状況データの取得
  getLicenseStatusOptions()
  // カテゴリデータの取得
  getCategories()
})

</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

// ローディング
.loading-icon {
  display: flex;
  justify-content: center;
  margin: 24px 0;
}

.category-accordion {
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  overflow: hidden;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 12px 16px;
    background-color: #f5f5f5;
    border: none;
    cursor: pointer;
    text-align: left;
    font-size: 1rem;
    font-weight: bold;

    &:hover {
      background-color: #e8e8e8;
    }
  }

  &__title {
    color: base.$text-primary;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  &__count {
    font-weight: normal;
    color: base.$text-secondary;
  }

  &__body {
    padding: 8px;
  }
}

.accordion-enter-active,
.accordion-leave-active {
  transition: opacity 0.2s ease;
}

.accordion-enter-from,
.accordion-leave-to {
  opacity: 0;
}

.radius-value {
  font-weight: normal;
  color: base.$text-secondary;
}

.last-confirmed {
  font-size: 0.8rem;
  margin-top: 4px;

  &--normal {
    color: base.$text-secondary;
  }

  &--warning {
    color: #e67e22;
    font-weight: bold;
  }

  &--unknown {
    color: base.$text-secondary;
  }
}
</style>
