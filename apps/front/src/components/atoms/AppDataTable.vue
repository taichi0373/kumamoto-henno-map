<template>
  <DataTable
    :value="value"
    :loading="loading"
    :totalRecords="totalRecords"
    :rows="rows"
    :first="first"
    :paginator="true"
    :lazy="true"
    :rowHover="true"
    :filterDisplay="filterDisplay"
    :globalFilterFields="globalFilterFields"
    stripedRows
    class="app-data-table"
    v-model:filters="proxyFilters"
    @page="onPageChange"
    @filter="$emit('filter', $event)"
  >
    <template #header>
      <slot name="header" />
    </template>

    <template v-for="col in columns" :key="col.field">
      <Column
        :field="col.field"
        :header="col.header"
        :sortable="col.sortable ?? false"
        :showFilterMatchModes="false"
      >
        <template v-if="col.filterPlaceholder" #filter="{ filterModel, filterCallback }">
          <AppTextField
            v-model="filterModel.value"
            :placeholder="col.filterPlaceholder"
            style="width: 100%; font-size: 13px; padding: 4px 8px;"
            @keydown.enter="filterCallback"
          />
        </template>
        <template #filterapply="{ filterCallback }">
          <AppButton :primary="true" label="検索" @click="filterCallback"/>
        </template>
        <template #filterclear="{ filterModel, filterCallback }">
          <AppButton :primary="false" label="閉じる" @click="filterModel.value = null; filterCallback()"/>
        </template>
      </Column>
    </template>

    <template #empty>
      <div class="empty-message">データがありません</div>
    </template>
    <slot />
  </DataTable>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import AppTextField from './AppTextField.vue'
import AppButton from './AppButton.vue'

/** カラム定義 */
export interface AppDataTableColumn {
  /** データのキー名 */
  field: string
  /** 表示ヘッダー名 */
  header: string
  /** ソート可否 */
  sortable?: boolean
  /** フィルタープレースホルダー（設定するとカラムフィルターメニューが有効になる） */
  filterPlaceholder?: string
}

/** DataTable フィルター型 */
type DataTableFilters = Record<string, { value: unknown; matchMode: string }>

const props = withDefaults(defineProps<{
  /** 表示するデータ配列 */
  value: object[]
  /** カラム定義 */
  columns: AppDataTableColumn[]
  /** ローディング状態 */
  loading?: boolean
  /** 総レコード数 */
  totalRecords?: number
  /** 1ページあたりの行数 */
  rows?: number
  /** 先頭インデックス */
  first?: number
  /** フィルター（v-model:filters） */
  filters?: DataTableFilters
  /** フィルター表示モード（'row' | 'menu'） */
  filterDisplay?: string
  /** グローバルフィルター対象フィールド */
  globalFilterFields?: string[]
}>(), {
  loading: false,
  totalRecords: 0,
  rows: 20,
  first: 0,
  filters: undefined,
  filterDisplay: undefined,
  globalFilterFields: undefined,
})

const emit = defineEmits<{
  (e: 'page-change', event: { first: number; rows: number }): void
  (e: 'update:filters', filters: DataTableFilters): void
  (e: 'filter', event: unknown): void
}>()

/** フィルターをv-modelで双方向バインディング */
const proxyFilters = computed({
  get: () => props.filters ?? {},
  set: (val: DataTableFilters) => emit('update:filters', val),
})

/** ページ変更時の処理 */
const onPageChange = (event: { first: number; rows: number }) => {
  emit('page-change', event)
}
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.app-data-table {
  :deep(.p-datatable-filter-buttonbar) {
    background-color: #ff8888;
  }
}
</style>
