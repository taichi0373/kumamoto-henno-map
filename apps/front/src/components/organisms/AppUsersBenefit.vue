<template>
  <div class="app-users-benefit">
    <div class="section-header">
      <h3 class="section-title">
        <i class="pi pi-gift"></i>
        利用できる特典
      </h3>
      <p class="section-description">
        あなたのプロフィールに基づいて利用できる特典一覧です。
      </p>
    </div>

    <!-- 特典がない場合 -->
    <div v-if="!usersBenefits || usersBenefits.length === 0" class="no-benefits">
      <i class="pi pi-info-circle"></i>
      <p>現在利用できる特典がありません。</p>
      <p class="suggestion">プロフィールを更新すると、より多くの特典が表示される場合があります。</p>
      <AppButton
        type="button"
        label="プロフィールを編集"
        severity="primary"
        outlined
        icon="pi pi-user-edit"
        @click="$router.push('/profile')"
      />
    </div>

    <!-- 特典リスト -->
    <div v-else class="benefits-list">
      <div class="benefits-summary">
        <span class="benefits-count">{{ usersBenefits.length }}件の特典が利用可能です</span>
      </div>

      <div
        v-for="benefit in usersBenefits"
        :key="benefit.benefitId"
        :data-benefit-id="benefit.benefitId"
        class="benefit-card"
      >
        <div class="benefit-header">
          <h4 class="benefit-title">{{ benefit.benefitName }}</h4>
          <span class="benefit-category">{{ benefit.categoryCd }}</span>
        </div>

        <div class="benefit-content">
          <div class="benefit-description">
            {{ benefit.benefitDetail }}
          </div>

          <!-- 特典詳細 -->
          <div class="benefit-details">
            <div v-if="benefit.expDetail" class="detail-item">
              <i class="pi pi-calendar"></i>
              <span>有効期限: {{ formatDate(benefit.expDetail) }}</span>
            </div>
          </div>

          <!-- アクション -->
          <div class="benefit-actions">
            <AppButton
              v-if="benefit.benefitUrl"
              type="button"
              label="詳細を見る"
              severity="primary"
              outlined
              size="small"
              icon="pi pi-external-link"
              @click="openWebsite(benefit.benefitUrl)"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- フィルター・ソート -->
    <div v-if="usersBenefits && usersBenefits.length > 0" class="benefits-controls">
      <div class="controls-row">
        <div class="filter-group">
          <AppLabel text="カテゴリー" size="small" />
          <AppSelect
            v-model="selectedCategory"
            :options="categoryOptions"
            placeholder="すべて"
            size="small"
          />
        </div>
        <div class="sort-group">
          <AppLabel text="並び順" size="small" />
          <AppSelect
            v-model="sortOrder"
            :options="sortOptions"
            placeholder="名前順"
            size="small"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import AppButton from '../atoms/AppButton.vue';
import AppLabel from '../atoms/AppLabel.vue';
import AppSelect from '../atoms/AppSelect.vue';
import { BenefitDto } from '@/dto/benefitDto'

const emit = defineEmits<{
  (e: 'show-benefit-on-map', benefit: BenefitDto): void;
}>();

const props = withDefaults(defineProps<{
  usersBenefits?: BenefitDto[];
}>(), {
  usersBenefits: () => [],
});

const selectedCategory = ref('');
const sortOrder = ref('name');

const categoryOptions = computed(() => {
  if (!props.usersBenefits) return [];
  const categories = [...new Set(props.usersBenefits.map(b => b.categoryCd))];
  return [
    { label: 'すべて', text: 'すべて', value: '' },
    ...categories.map(cat => ({ label: cat, text: cat, value: cat }))
  ];
});

const sortOptions = [
  { label: '名前順', text: 'name', value: 'name' },
  { label: 'カテゴリー順', text: 'categoryCd', value: 'categoryCd' },
  { label: '有効期限順', text: 'validPeriod', value: 'validPeriod' }
];

const formatDate = (dateString: string) => {
  try {
    return new Intl.DateTimeFormat('ja-JP').format(new Date(dateString));
  } catch {
    return dateString;
  }
};

const openWebsite = (url: string) => {
  window.open(url, '_blank', 'noopener,noreferrer');
};
  
const showOnMap = (benefit: BenefitDto) => {
  emit('show-benefit-on-map', benefit);
};
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.app-users-benefit {
  padding: 1.5rem;
}

.section-header {
  margin-bottom: 2rem;
  text-align: center;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-size: 1.5rem;
  font-weight: 600;
}

.section-description {
  color: #6c757d;
  font-size: 0.9rem;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 3rem;
  color: #6c757d;
}

.loading-container i {
  font-size: 2rem;
}

.no-benefits {
  text-align: center;
  padding: 3rem 1rem;
  color: #6c757d;
}

.no-benefits i {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: #adb5bd;
}

.suggestion {
  margin: 1rem 0;
  font-size: 0.9rem;
}

.benefits-list {
  margin-bottom: 2rem;
}

.benefits-summary {
  margin-bottom: 1.5rem;
  text-align: center;
}

.benefits-count {
  background: #e7f3ff;
  color: #0066cc;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.benefit-card {
  background: white;
  border: 1px solid #e0e6ed;
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.2s;
}

.benefit-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.benefit-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.benefit-title {
  color: #2c3e50;
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0;
}

.benefit-category {
  background: #f8f9fa;
  color: #6c757d;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  white-space: nowrap;
}

.benefit-description {
  color: #495057;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.benefit-details {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1rem;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #28a745;
  font-weight: 500;
}

.detail-item i {
  width: 16px;
  font-size: 0.9rem;
}

.store-info {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
}

.store-name {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
}

.store-address,
.store-contact {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.25rem;
  color: #6c757d;
  font-size: 0.9rem;
}

.store-contact a {
  color: #007bff;
  text-decoration: none;
}

.store-contact a:hover {
  text-decoration: underline;
}

.benefit-conditions {
  margin-bottom: 1rem;
}

.conditions-title {
  margin-bottom: 0.5rem;
  color: #6c757d;
  font-size: 0.9rem;
  font-weight: 600;
}

.conditions-list {
  padding-left: 1rem;
  color: #6c757d;
  font-size: 0.85rem;
}

.conditions-list li {
  margin-bottom: 0.25rem;
}

.benefit-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
  border-top: 1px solid #e0e6ed;
  padding-top: 1rem;
}

.benefits-controls {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #e0e6ed;
}

.controls-row {
  display: flex;
  gap: 1rem;
}

.filter-group,
.sort-group {
  flex: 1;
}
</style>