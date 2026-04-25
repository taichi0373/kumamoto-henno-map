<template>
  <div class="app-data-table-wrapper">
    <DataTable
      ref="dtRef"
      :value="value"
      :loading="loading"
      :totalRecords="totalRecords"
      :rows="rows"
      :first="first"
      :sortField="sortField"
      :sortOrder="sortOrder"
      :paginator="true"
      :lazy="true"
      :rowHover="true"
      :filterDisplay="filterDisplay"
      :globalFilterFields="globalFilterFields"
      :dataKey="dataKey"
      :exportFilename="exportFilename"
      tableStyle="min-width: 1300px"
      stripedRows
      class="app-data-table"
      v-model:filters="proxyFilters"
      v-model:selection="proxySelection"
      @page="onPageChange"
      @filter="$emit('filter', $event)"
      @sort="$emit('sort', $event)"
    >
    <template #header>
      <slot name="header" />
    </template>

    <Column v-if="dataKey" selectionMode="multiple" style="width: 3rem" :exportable="false" />

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
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
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
  /** ソートフィールド（lazy モードで視覚状態を制御） */
  sortField?: string
  /** ソート順（1: 昇順, -1: 降順、lazy モードで視覚状態を制御） */
  sortOrder?: number
  /** 選択のキーとなるフィールド名（設定するとチェックボックス選択が有効になる） */
  dataKey?: string
  /** CSVエクスポート時のファイル名 */
  exportFilename?: string
  /** 選択中の行（v-model:selection） */
  selection?: object[]
}>(), {
  loading: false,
  totalRecords: 0,
  rows: 20,
  first: 0,
  filters: undefined,
  filterDisplay: undefined,
  globalFilterFields: undefined,
  sortField: undefined,
  sortOrder: undefined,
  dataKey: undefined,
  exportFilename: 'download',
  selection: undefined,
})

const emit = defineEmits<{
  (e: 'page-change', event: { first: number; rows: number }): void
  (e: 'update:filters', filters: DataTableFilters): void
  (e: 'filter', event: unknown): void
  (e: 'sort', event: { sortField: string; sortOrder: number }): void
  (e: 'update:selection', selection: object[]): void
}>()

/** DataTable の内部 ref（exportCSV 呼び出し用） */
const dtRef = ref()

/** フィルターをv-modelで双方向バインディング */
const proxyFilters = computed({
  get: () => props.filters ?? {},
  set: (val: DataTableFilters) => emit('update:filters', val),
})

/** 選択行をv-modelで双方向バインディング */
const proxySelection = computed({
  get: () => props.selection ?? [],
  set: (val: object[]) => emit('update:selection', val),
})

/** ページ変更時の処理 */
const onPageChange = (event: { first: number; rows: number }) => {
  emit('page-change', event)
}

/** CSVエクスポート（親から呼び出し可能） */
defineExpose({
  exportCSV: (options?: { selectionOnly: boolean }) => dtRef.value?.exportCSV(options),
})
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.app-data-table-wrapper {
  overflow-x: auto;
}

.app-data-table {
  :deep(.p-datatable-filter-buttonbar) {
    background-color: #ff8888;
  }
  :deep(.p-datatable-column-sorted) {
    color: inherit;
    background-color: base.$base-200;
    * {
        color: inherit;
      }
  }
}
</style>
