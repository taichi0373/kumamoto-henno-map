<template>
  <div class="p-2">
    <AppAlert v-if="usersBenefits.length === 0" :variant="'error'" :message="'現在利用できる特典がありません'" />

    <div v-else>
      <template v-for="benefit in filteredBenefits" :key="benefit.benefitId ?? undefined">
        <AppCard class="mb-3" :hoverable="true">
          <template #title>{{ benefit.benefitName }}</template>
          <!-- 特典内容 -->
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
            <li v-if="benefit.note">
              <small>※{{ benefit.note }}</small>
            </li>
          </ul>
          <AppLink v-if="benefit.benefitUrl" :to="benefit.benefitUrl">詳細を見る</AppLink>
        </AppCard>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppAlert from '@/components/atoms/AppAlert.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import { BenefitDto } from '@/dto/benefitDto'
import { codeConstant } from '@/utils/codeConstant'
import AppTitle from '../atoms/AppTitle.vue'

const props = withDefaults(defineProps<{
  usersBenefits?: BenefitDto[];
}>(), {
  usersBenefits: () => [],
});

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

/** 特典リスト */
const filteredBenefits = computed(() => props.usersBenefits ?? [])
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";
</style>
