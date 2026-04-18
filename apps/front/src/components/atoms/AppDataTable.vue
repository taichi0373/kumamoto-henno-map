<template>
  <DataTable
    :value="value"
    :loading="loading"
    :totalRecords="totalRecords"
    :rows="rows"
    :first="first"
    :paginator="false"
    :lazy="true"
    :rowHover="true"
    stripedRows
    class="app-data-table"
    @page="onPageChange"
  >
    <Column
      v-for="col in columns"
      :key="col.field"
      :field="col.field"
      :header="col.header"
      :sortable="col.sortable ?? false"
    />
    <template #empty>
      <div class="empty-message">データがありません</div>
    </template>
    <slot />
  </DataTable>
</template>

<script setup lang="ts">
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'

/** カラム定義 */
export interface AppDataTableColumn {
  /** データのキー名 */
  field: string
  /** 表示ヘッダー名 */
  header: string
  /** ソート可否 */
  sortable?: boolean
}

withDefaults(defineProps<{
  /** 表示するデータ配列 */
  value: Record<string, unknown>[]
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
}>(), {
  loading: false,
  totalRecords: 0,
  rows: 20,
  first: 0,
})

const emit = defineEmits<{
  (e: 'page-change', event: { first: number; rows: number }): void
}>()

/** ページ変更時の処理 */
const onPageChange = (event: { first: number; rows: number }) => {
  emit('page-change', event)
}
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.app-data-table {
  width: 100%;

  :deep(.p-datatable-table) {
    border-collapse: collapse;
  }

  :deep(.p-datatable-thead > tr > th) {
    background-color: base.$header-background-color;
    color: base.$base-100;
    font-weight: bold;
    padding: 0.75rem 1rem;
  }

  :deep(.p-datatable-tbody > tr > td) {
    padding: 0.6rem 1rem;
    border-bottom: 1px solid base.$base-300;
  }

  :deep(.p-datatable-tbody > tr:hover) {
    background-color: base.$base-200;
  }
}

.empty-message {
  text-align: center;
  padding: 2rem;
  color: base.$base-500;
}
</style>
