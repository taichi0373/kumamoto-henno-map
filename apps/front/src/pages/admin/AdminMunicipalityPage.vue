<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">自治体管理</AppTitle>

    <AppMessageBar v-if="errorMessage" mode="error" :message="errorMessage" />

    <!-- ツールバー -->
    <!-- ツールバー -->
    <AppToolbar class="mb-4">
      <template #start>
        <AppButton label="新規登録" :primary="true" icon="pi pi-plus" @click="openCreateDialog" style="margin-right: 8px" />
        <AppButton
          label="削除"
          icon="pi pi-trash"
          :disabled="!selectedItems.length"
          @click="isDeleteSelectedDialogVisible = true"
        />
      </template>
      <template #end>
        <AppButton label="インポート" icon="pi pi-download" @click="openImportDialog" style="margin-right: 8px" />
        <AppButton label="エクスポート" icon="pi pi-upload" @click="exportCSV" />
      </template>
    </AppToolbar>

    <AppDataTable
      ref="appDtRef"
      :value="items"
      :columns="columns"
      exportFilename="admin-municipalities"
      :loading="isLoading"
      :totalRecords="total"
      :rows="size"
      :first="page * size"
      v-model:filters="filters"
      v-model:selection="selectedItems"
      filterDisplay="menu"
      :globalFilterFields="['municipalityName']"
      :sortField="sortField"
      :sortOrder="sortOrder"
      dataKey="municipalityCd"
      @page-change="onPageChange"
      @filter="onFilter"
      @sort="onSort"
    >
      <template #header>
        <div class="table-header">
          <AppButton label="クリア" icon="pi pi-filter-slash" @click="clearFilter" />
          <AppTextField
            v-model="filters['global'].value"
            class="table-header__search"
            placeholder="キーワード検索"
            @input="onGlobalSearch"
          />
        </div>
      </template>
      <Column field="actions" header="操作">
        <template #body="{ data }">
          <div class="action-buttons">
            <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as MunicipalityAdminDto)" />
            <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as MunicipalityAdminDto)" />
          </div>
        </template>
      </Column>
    </AppDataTable>

    <AppDialog
      v-model="isDialogVisible"
      :header="editTarget?.municipalityCd ? '自治体編集' : '自治体登録'"
      :dialogStyle="{ width: '500px' }"
    >
      <div class="form-grid">
        <label>自治体コード *</label>
        <AppTextField v-model="form.municipalityCd" :disabled="!!editTarget?.municipalityCd" placeholder="43100" />
        <label>自治体名称 *</label>
        <AppTextField v-model="form.municipalityName" placeholder="熊本市" />
        <label>自治体名称かな</label>
        <AppTextField v-model="form.municipalityKana" placeholder="くまもとし" />
        <label>自治体区分 *</label>
        <AppSelect
          v-model="form.municipalityType"
          :options="municipalityTypeOptions"
          placeholder="選択してください"
          :showClear="false"
        />
      </div>
      <template #footer>
        <AppButton label="キャンセル" @click="isDialogVisible = false" />
        <AppButton label="保存" :primary="true" @click="save" />
      </template>
    </AppDialog>

    <AppDialog v-model="isDeleteDialogVisible" header="削除確認" :dialogStyle="{ width: '400px' }">
      <p>自治体「{{ deleteTarget?.municipalityName }}」を削除しますか？</p>
      <template #footer>
        <AppButton label="キャンセル" @click="isDeleteDialogVisible = false" />
        <AppButton label="削除" :primary="true" @click="confirmDelete" />
      </template>
    </AppDialog>

    <!-- 一括削除確認ダイアログ -->
    <AppDialog v-model="isDeleteSelectedDialogVisible" header="一括削除確認" :dialogStyle="{ width: '400px' }">
      <p>選択した {{ selectedItems.length }} 件を削除しますか？</p>
      <template #footer>
        <AppButton label="キャンセル" @click="isDeleteSelectedDialogVisible = false" />
        <AppButton label="削除" :primary="true" @click="deleteSelectedItems" />
      </template>
    </AppDialog>

    <AppDialog v-model="isImportDialogVisible" header="CSVインポート" :dialogStyle="{ width: '500px' }">
      <p class="import-desc">CSVファイルを選択してインポートします。既存データは上書き更新されます。</p>
      <p class="import-desc import-desc--columns">
        必須列: <code>municipalityCd</code> 任意列: municipalityName / municipalityKana / municipalityType
      </p>
      <AppFileUpload ref="fileUploadRef" v-model="importFile" accept=".csv" chooseLabel="CSVを選択" />
      <div v-if="importResult" class="import-result">
        <p>登録: {{ importResult.inserted }} 件 / 更新: {{ importResult.updated }} 件 / 失敗: {{ importResult.failed }} 件</p>
        <ul v-if="importResult.errors.length" class="import-result__errors">
          <li v-for="(err, i) in importResult.errors" :key="i">{{ err }}</li>
        </ul>
      </div>
      <template #footer>
        <AppButton label="キャンセル" @click="closeImportDialog" />
        <AppButton label="インポート実行" icon="pi pi-upload" :primary="true" :disabled="!importFile || isImporting" @click="importCSV" />
      </template>
    </AppDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { FilterMatchMode } from '@primevue/core/api'
import Column from 'primevue/column'
import AppToolbar from '@/components/atoms/AppToolbar.vue'
import AppBlockUI from '@/components/atoms/AppBlockUI.vue'
import AppToastMessage from '@/components/atoms/AppToastMessage.vue'
import AppDataTable from '@/components/atoms/AppDataTable.vue'
import type { AppDataTableColumn } from '@/components/atoms/AppDataTable.vue'
import AppDialog from '@/components/atoms/AppDialog.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppTitle from '@/components/atoms/AppTitle.vue'
import AppFileUpload from '@/components/atoms/AppFileUpload.vue'
import { SelectDto } from '@/dto/selectDto'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import { codeConstant } from '@/utils/codeConstant'
import type { MunicipalityAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

const items = ref<MunicipalityAdminDto[]>([])
const total = ref(0)
const page = ref(0)
/** 1ページあたり件数 */
const size = ref(codeConstant.PAGINATION.ADMIN_PAGE_SIZE)
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isDialogVisible = ref(false)
const isDeleteDialogVisible = ref(false)
/** 一括削除確認ダイアログ表示 */
const isDeleteSelectedDialogVisible = ref(false)
const editTarget = ref<MunicipalityAdminDto | null>(null)
const deleteTarget = ref<MunicipalityAdminDto | null>(null)
const form = ref<Partial<MunicipalityAdminDto>>({})
/** 選択中の行 */
const selectedItems = ref<MunicipalityAdminDto[]>([])
/** AppDataTable の ref（exportCSV 呼び出し用） */
const appDtRef = ref()
const isImportDialogVisible = ref(false)
const isImporting = ref(false)
const importFile = ref<File | null>(null)
const importResult = ref<{ inserted: number; updated: number; failed: number; errors: string[] } | null>(null)
const fileUploadRef = ref()

/** フィルター（global: キーワード検索、各カラムフィルター） */
const filters = ref({
  global: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  municipalityCd: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  municipalityName: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  municipalityKana: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  municipalityType: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
})

/** フィルター初期化 */
const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    municipalityCd: { value: null, matchMode: FilterMatchMode.CONTAINS },
    municipalityName: { value: null, matchMode: FilterMatchMode.CONTAINS },
    municipalityKana: { value: null, matchMode: FilterMatchMode.CONTAINS },
    municipalityType: { value: null, matchMode: FilterMatchMode.CONTAINS },
  }
}

/** ソートフィールド */
const sortField = ref<string | undefined>(undefined)
/** ソート順（1: 昇順, -1: 降順） */
const sortOrder = ref<number>(0)

/** グローバル検索デバウンスタイマー */
let debounceTimer: ReturnType<typeof setTimeout> | null = null

/** フィルタークリア */
const clearFilter = () => {
  if (debounceTimer) clearTimeout(debounceTimer)
  initFilters()
  fetchItems(0)
}

/** グローバルキーワード検索（デバウンス） */
const onGlobalSearch = () => {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => fetchItems(0), 300)
}

/** 自治体区分選択肢（1: 都道府県 / 2: 区 / 3: 市町村） */
const municipalityTypeOptions: SelectDto[] = [
  new SelectDto({ label: '都道府県', text: '都道府県', value: '1' }),
  new SelectDto({ label: '区', text: '区', value: '2' }),
  new SelectDto({ label: '市町村', text: '市町村', value: '3' }),
]

/** テーブルカラム定義 */
const columns: AppDataTableColumn[] = [
  { field: 'municipalityCd', header: '自治体コード', sortable: true, filterPlaceholder: '自治体コードで検索' },
  { field: 'municipalityName', header: '自治体名称', sortable: true, filterPlaceholder: '自治体名称で検索' },
  { field: 'municipalityKana', header: '自治体名称かな', sortable: true, filterPlaceholder: '自治体名称かなで検索' },
  { field: 'municipalityType', header: '自治体区分', sortable: true, filterPlaceholder: '自治体区分で検索' },
]

/** 一覧取得 */
const fetchItems = async (targetPage: number) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const keyword = filters.value.global?.value ?? null
    const response = await apiClient.get<{ data: AdminPagedResponse<MunicipalityAdminDto> }>(
      '/admin/municipalities',
      {
        params: {
          page: targetPage,
          size: size.value,
          municipalityCd: filters.value.municipalityCd?.value ?? undefined,
          municipalityName: filters.value.municipalityName?.value ?? keyword ?? undefined,
          municipalityKana: filters.value.municipalityKana?.value ?? undefined,
          municipalityType: filters.value.municipalityType?.value ?? undefined,
          sort: sortField.value ?? undefined,
          order: sortOrder.value === 1 ? 'asc' : sortOrder.value === -1 ? 'desc' : undefined,
        },
      }
    )
    const data = response.data?.data
    items.value = data?.items ?? []
    total.value = data?.total ?? 0
    page.value = targetPage
  } catch {
    errorMessage.value = '自治体一覧の取得に失敗しました'
  } finally {
    isLoading.value = false
  }
}

const onFilter = () => { fetchItems(0) }
const onSort = (event: { sortField: string; sortOrder: number }) => {
  sortField.value = event.sortField
  sortOrder.value = event.sortOrder
  fetchItems(0)
}
const onPageChange = (event: { first: number; rows: number }) => {
  size.value = event.rows
  fetchItems(Math.floor(event.first / event.rows))
}
const openCreateDialog = () => { editTarget.value = null; form.value = {}; isDialogVisible.value = true }
const openEditDialog = (item: MunicipalityAdminDto) => { editTarget.value = item; form.value = { ...item }; isDialogVisible.value = true }
const openDeleteDialog = (item: MunicipalityAdminDto) => { deleteTarget.value = item; isDeleteDialogVisible.value = true }

const save = async () => {
  isLoading.value = true
  try {
    if (editTarget.value?.municipalityCd) {
      await apiClient.put(`/admin/municipalities/${editTarget.value.municipalityCd}`, form.value as Record<string, unknown>)
      ToastMessageUtils.success('自治体を更新しました')
    } else {
      await apiClient.post('/admin/municipalities', form.value as Record<string, unknown>)
      ToastMessageUtils.success('自治体を登録しました')
    }
    isDialogVisible.value = false
    await fetchItems(page.value)
  } catch (error: unknown) {
    const msg = (error as { response?: { data?: { message?: string } } })?.response?.data?.message
    errorMessage.value = msg ?? '保存に失敗しました'
  } finally {
    isLoading.value = false
  }
}
const confirmDelete = async () => {
  if (!deleteTarget.value) return
  isLoading.value = true
  try {
    await apiClient.delete(`/admin/municipalities/${deleteTarget.value.municipalityCd}`)
    ToastMessageUtils.success('自治体を削除しました')
    isDeleteDialogVisible.value = false
    await fetchItems(page.value)
  } catch (error: unknown) {
    const msg = (error as { response?: { data?: { message?: string } } })?.response?.data?.message
    errorMessage.value = msg ?? '削除に失敗しました'
    isDeleteDialogVisible.value = false
  } finally {
    isLoading.value = false
  }
}
const deleteSelectedItems = async () => {
  isLoading.value = true
  let successCount = 0
  let failCount = 0
  for (const item of selectedItems.value) {
    try {
      await apiClient.delete(`/admin/municipalities/${item.municipalityCd}`)
      successCount++
    } catch { failCount++ }
  }
  selectedItems.value = []
  isDeleteSelectedDialogVisible.value = false
  await fetchItems(page.value)
  isLoading.value = false
  if (failCount === 0) {
    ToastMessageUtils.success(`${successCount}件を削除しました`)
  } else {
    errorMessage.value = `${successCount}件削除成功、${failCount}件失敗しました`
  }
}
const exportCSV = () => { appDtRef.value?.exportCSV() }

const openImportDialog = () => {
  importFile.value = null
  importResult.value = null
  isImportDialogVisible.value = true
}

const closeImportDialog = () => {
  isImportDialogVisible.value = false
  fileUploadRef.value?.clear()
}

const importCSV = async () => {
  if (!importFile.value) return
  isImporting.value = true
  importResult.value = null
  try {
    const formData = new FormData()
    formData.append('file', importFile.value)
    const response = await apiClient.axios.post<{ data: { inserted: number; updated: number; failed: number; errors: string[] } }>(
      '/admin/municipalities/import',
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    )
    importResult.value = response.data?.data
    ToastMessageUtils.success(`インポート完了: 登録 ${importResult.value?.inserted} 件 / 更新 ${importResult.value?.updated} 件`)
    await fetchItems(page.value)
  } catch (error: unknown) {
    const msg = (error as { response?: { data?: { message?: string } } })?.response?.data?.message
    errorMessage.value = msg ?? 'CSVインポートに失敗しました'
  } finally {
    isImporting.value = false
  }
}

onMounted(() => fetchItems(0))
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.action-buttons { display: flex; gap: 8px; }
.form-grid { display: grid; grid-template-columns: 140px 1fr; gap: 0.5rem 1rem; align-items: center; }
.table-header { display: flex; justify-content: space-between; align-items: center; }
.table-header__search { width: 16rem; max-width: 100%; }
.import-desc { margin-bottom: 0.5rem; font-size: 0.875rem; &--columns { color: var(--p-text-color-secondary, #6c757d); } }
.import-result { margin-top: 1rem; padding: 0.75rem; background: var(--p-surface-100, #f8f9fa); border-radius: 4px; font-size: 0.875rem; &__errors { margin-top: 0.5rem; padding-left: 1.25rem; color: var(--p-red-500, #ef4444); } }
</style>
