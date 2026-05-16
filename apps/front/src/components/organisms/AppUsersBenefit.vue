<template>
  <div class="p-2">
    <AppAlert v-if="usersBenefits.length === 0" class="mt-2" :variant="'error'" :message="'現在利用できる特典がありません'" />

    <div v-else>
      <div
        v-for="group in groupedBenefits"
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
            <template v-for="(benefit, index) in group.benefits" :key="benefit.benefitId ?? `benefit-${index}`">
              <AppCard class="mb-3" :hoverable="true">
                <template #title>{{ benefit.benefitName }}</template>
                <!-- 特典内容 -->
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
                  <li v-if="benefit.note">
                    <small>※{{ benefit.note }}</small>
                  </li>
                </ul>
                <AppLink v-if="benefit.benefitUrl" :to="benefit.benefitUrl">詳細を見る</AppLink>
              </AppCard>
            </template>
          </div>
        </Transition>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, onMounted } from 'vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppAlert from '@/components/atoms/AppAlert.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import { BenefitDto } from '@/dto/benefitDto'
import { codeConstant } from '@/utils/codeConstant'
import apiClient from '@/utils/api'

/** カテゴリ情報 */
interface BenefitCategory {
  /** カテゴリコード */
  categoryCd: string
  /** カテゴリ名称 */
  categoryName: string
  /** 表示順 */
  displayOrder: number
}

const props = withDefaults(defineProps<{
  usersBenefits?: BenefitDto[];
}>(), {
  usersBenefits: () => [],
});

/** カテゴリリスト */
const categories = ref<BenefitCategory[]>([])

/** 展開中カテゴリコードのセット */
const openCategories = ref(new Set<string>())

/** カテゴリコードからカテゴリ情報へのマップ */
const categoryMap = computed(() =>
  new Map(categories.value.map(c => [c.categoryCd, c]))
)

/** カテゴリ別にグループ化した特典リスト（displayOrder 昇順） */
const groupedBenefits = computed(() => {
  const groups = new Map<string, { categoryCd: string; categoryName: string; displayOrder: number; benefits: BenefitDto[] }>()
  for (const benefit of props.usersBenefits) {
    const cd = benefit.categoryCd ?? 'OTHER'
    if (!groups.has(cd)) {
      const cat = categoryMap.value.get(cd)
      groups.set(cd, {
        categoryCd: cd,
        categoryName: cat?.categoryName ?? 'その他',
        displayOrder: cat?.displayOrder ?? 9999,
        benefits: []
      })
    }
    groups.get(cd)!.benefits.push(benefit)
  }
  return Array.from(groups.values())
    .sort((a, b) => a.displayOrder - b.displayOrder)
})

/** 運転免許の所持状況ラベル */
const licenseStatusLabels: Record<string, string> = {
  [codeConstant.LICENSE_STATUS.UNLICENSED]: '所持していない',
  [codeConstant.LICENSE_STATUS.LICENSED]: '所持している',
  [codeConstant.LICENSE_STATUS.RETURNED]: '返納',
  [codeConstant.LICENSE_STATUS.EXPIRED]: '失効',
  [codeConstant.LICENSE_STATUS.SUSPENDED]: '停止',
  [codeConstant.LICENSE_STATUS.OTHER]: 'その他',
}

/** 運転免許の所持状況コードから表示名を取得 */
const getLicenseStatusName = (code: string) => {
  return licenseStatusLabels[code] || '不明'
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

onMounted(async () => {
  try {
    const res = await apiClient.get<{ success: boolean; data: BenefitCategory[] }>('/benefit/categories')
    if (res.data.success) {
      categories.value = res.data.data
    }
  } catch (error) {
    console.error('カテゴリデータの取得に失敗しました:', error)
  }
  /** 初期状態: 全カテゴリ展開 */
  openCategories.value = new Set(groupedBenefits.value.map(g => g.categoryCd))
})

/** props 変化時に展開状態をリセット（全展開） */
watch(() => props.usersBenefits, () => {
  openCategories.value = new Set(groupedBenefits.value.map(g => g.categoryCd))
})
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

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
</style>
