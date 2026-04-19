<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">特典カテゴリ管理</AppTitle>

    <AppToolbar class="mb-4">
      <template #start>
        <AppButton label="新規登録" :primary="true" icon="pi pi-plus" @click="openCreateDialog" />
      </template>
      <template #end>
        <AppButton label="インポート" icon="pi pi-download" @click="openImportDialog" style="margin-right: 8px" />
        <AppButton label="エクスポート" icon="pi pi-upload" @click="exportCSV" />
      </template>
    </AppToolbar>

    <AppMessageBar v-if="errorMessage" mode="error" :message="errorMessage" />

    <AppDataTable
      ref="appDtRef"
      :value="items"
      :columns="columns"
      exportFilename="admin-benefit-categories"
      :loading="isLoading"
      :totalRecords="total"
      :rows="size"
      :first="page * size"
      v-model:filters="filters"
      filterDisplay="menu"
      :globalFilterFields="['categoryName']"
      :sortField="sortField"
      :sortOrder="sortOrder"
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
      <Column field="isActive" header="有効">
        <template #body="{ data }">
          <span :class="['status-badge', data.isActive === '1' ? 'status-badge--active' : 'status-badge--inactive']">
            {{ data.isActive === '1' ? '有効' : '無効' }}
          </span>
        </template>
      </Column>
      <Column field="actions" header="操作">
        <template #body="{ data }">
          <div class="action-buttons">
            <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as BenefitCategoryAdminDto)" />
            <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as BenefitCategoryAdminDto)" />
          </div>
        </template>
      </Column>
    </AppDataTable>

    <AppDialog
      v-model="isDialogVisible"
      :header="editTarget?.categoryCd ? '特典カテゴリ編集' : '特典カテゴリ登録'"
      :dialogStyle="{ width: '500px' }"
    >
      <div class="form-grid">
        <label>カテゴリコード *</label>
        <AppTextField v-model="form.categoryCd" :disabled="!!editTarget?.categoryCd" placeholder="01" />
        <label>カテゴリ名称 *</label>
        <AppTextField v-model="form.categoryName" placeholder="カテゴリ名称" />
        <label>表示順</label>
        <AppNumberField v-model="form.displayOrder" :useGrouping="false" />
        <label>有効フラグ *</label>
        <AppSelect v-model="form.isActive" :options="isActiveOptions" placeholder="選択してください" :showClear="false" />
      </div>
      <template #footer>
        <AppButton label="キャンセル" @click="isDialogVisible = false" />
        <AppButton label="保存" :primary="true" @click="save" />
      </template>
    </AppDialog>

    <AppDialog v-model="isDeleteDialogVisible" header="削除確認" :dialogStyle="{ width: '400px' }">
      <p>特典カテゴリ「{{ deleteTarget?.categoryName }}」を削除しますか？</p>
      <template #footer>
        <AppButton label="キャンセル" @click="isDeleteDialogVisible = false" />
        <AppButton label="削除" :primary="true" @click="confirmDelete" />
      </template>
    </AppDialog>

    <AppDialog v-model="isImportDialogVisible" header="CSVインポート" :dialogStyle="{ width: '500px' }">
      <p class="import-desc">CSVファイルを選択してインポートします。既存データは上書き更新されます。</p>
      <p class="import-desc import-desc--columns">
        必須列: <code>categoryCd</code> 任意列: categoryName / displayOrder / isActive
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
import AppNumberField from '@/components/atoms/AppNumberField.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppTitle from '@/components/atoms/AppTitle.vue'
import AppFileUpload from '@/components/atoms/AppFileUpload.vue'
import { SelectDto } from '@/dto/selectDto'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import { codeConstant } from '@/utils/codeConstant'
import type { BenefitCategoryAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

/** データ一覧 */
const items = ref<BenefitCategoryAdminDto[]>([])
/** 総件数 */
const total = ref(0)
/** 現在ページ */
const page = ref(0)
/** 1ページあたり件数 */
const size = ref(codeConstant.PAGINATION.ADMIN_PAGE_SIZE)
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isDialogVisible = ref(false)
const isDeleteDialogVisible = ref(false)
const editTarget = ref<BenefitCategoryAdminDto | null>(null)
const deleteTarget = ref<BenefitCategoryAdminDto | null>(null)
const form = ref<Partial<BenefitCategoryAdminDto>>({})
const appDtRef = ref()
const isImportDialogVisible = ref(false)
const isImporting = ref(false)
const importFile = ref<File | null>(null)
const importResult = ref<{ inserted: number; updated: number; failed: number; errors: string[] } | null>(null)
const fileUploadRef = ref()

/** フィルター（global: キーワード検索、各カラムフィルター） */
const filters = ref({
  global: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  categoryCd: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  categoryName: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  displayOrder: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
})

/** フィルター初期化 */
const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    categoryCd: { value: null, matchMode: FilterMatchMode.CONTAINS },
    categoryName: { value: null, matchMode: FilterMatchMode.CONTAINS },
    displayOrder: { value: null, matchMode: FilterMatchMode.CONTAINS },
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

/** 有効フラグ選択肢 */
const isActiveOptions: SelectDto[] = [
  new SelectDto({ label: '有効', text: '有効', value: '1' }),
  new SelectDto({ label: '無効', text: '無効', value: '0' }),
]

/** テーブルカラム定義 */
const columns: AppDataTableColumn[] = [
  { field: 'categoryCd', header: 'カテゴリコード', sortable: true, filterPlaceholder: 'カテゴリコードで検索' },
  { field: 'categoryName', header: 'カテゴリ名称', sortable: true, filterPlaceholder: 'カテゴリ名称で検索' },
  { field: 'displayOrder', header: '表示順', sortable: true, filterPlaceholder: '表示順で検索' },
]

/** 一覧取得 */
const fetchItems = async (targetPage = page.value) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const keyword = filters.value.global?.value ?? null
    const response = await apiClient.get<{ data: AdminPagedResponse<BenefitCategoryAdminDto> }>('/admin/benefit-categories', {
      params: {
        page: targetPage,
        size: size.value,
        categoryCd: filters.value.categoryCd?.value ?? undefined,
        categoryName: filters.value.categoryName?.value ?? keyword ?? undefined,
        displayOrder: filters.value.displayOrder?.value ?? undefined,
        sort: sortField.value ?? undefined,
        order: sortOrder.value === 1 ? 'asc' : sortOrder.value === -1 ? 'desc' : undefined,
      },
    })
    const paged = response.data?.data
    items.value = paged?.items ?? []
    total.value = paged?.total ?? 0
    page.value = targetPage
  } catch {
    errorMessage.value = '特典カテゴリ一覧の取得に失敗しました'
  } finally {
    isLoading.value = false
  }
}

/** フィルター変更時 */
const onFilter = () => {
  fetchItems(0)
}

/** ソート変更時 */
const onSort = (event: { sortField: string; sortOrder: number }) => {
  sortField.value = event.sortField
  sortOrder.value = event.sortOrder
  fetchItems(0)
}

/** ページ変更 */
const onPageChange = (event: { first: number; rows: number }) => {
  size.value = event.rows
  page.value = Math.floor(event.first / event.rows)
  fetchItems(page.value)
}

/** 新規登録ダイアログを開く */
const openCreateDialog = () => {
  editTarget.value = null
  form.value = { isActive: '1' }
  isDialogVisible.value = true
}

/** 編集ダイアログを開く */
const openEditDialog = (item: BenefitCategoryAdminDto) => {
  editTarget.value = item
  form.value = { ...item }
  isDialogVisible.value = true
}

/** 削除確認ダイアログを開く */
const openDeleteDialog = (item: BenefitCategoryAdminDto) => {
  deleteTarget.value = item
  isDeleteDialogVisible.value = true
}

/** 保存（登録・更新） */
const save = async () => {
  isLoading.value = true
  try {
    if (editTarget.value?.categoryCd) {
      await apiClient.put(`/admin/benefit-categories/${editTarget.value.categoryCd}`, form.value as Record<string, unknown>)
      ToastMessageUtils.success('特典カテゴリを更新しました')
    } else {
      await apiClient.post('/admin/benefit-categories', form.value as Record<string, unknown>)
      ToastMessageUtils.success('特典カテゴリを登録しました')
    }
    isDialogVisible.value = false
    await fetchItems()
  } catch (error: unknown) {
    const msg = (error as { response?: { data?: { message?: string } } })?.response?.data?.message
    errorMessage.value = msg ?? '保存に失敗しました'
  } finally {
    isLoading.value = false
  }
}

/** 削除実行 */
const confirmDelete = async () => {
  if (!deleteTarget.value) return
  isLoading.value = true
  try {
    await apiClient.delete(`/admin/benefit-categories/${deleteTarget.value.categoryCd}`)
    ToastMessageUtils.success('特典カテゴリを削除しました')
    isDeleteDialogVisible.value = false
    await fetchItems()
  } catch (error: unknown) {
    const msg = (error as { response?: { data?: { message?: string } } })?.response?.data?.message
    errorMessage.value = msg ?? '削除に失敗しました'
    isDeleteDialogVisible.value = false
  } finally {
    isLoading.value = false
  }
}

const exportCSV = () => {
  appDtRef.value?.exportCSV()
}

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
      '/admin/benefit-categories/import',
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    )
    importResult.value = response.data?.data
    ToastMessageUtils.success(
      `インポート完了: 登録 ${importResult.value?.inserted} 件 / 更新 ${importResult.value?.updated} 件`
    )
    await fetchItems()
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
.action-buttons {
  display: flex;
  gap: 8px;
}
.form-grid { display: grid; grid-template-columns: 140px 1fr; gap: 0.5rem 1rem; align-items: center; }
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.table-header__search {
  width: 16rem;
  max-width: 100%;
}
.status-badge {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: bold;
  &--active { background: #d4edda; color: #155724; }
  &--inactive { background: #f8d7da; color: #721c24; opacity: 0.7; }
}
.import-desc { margin-bottom: 0.5rem; font-size: 0.875rem; &--columns { color: var(--p-text-color-secondary, #6c757d); } }
.import-result { margin-top: 1rem; padding: 0.75rem; background: var(--p-surface-100, #f8f9fa); border-radius: 4px; font-size: 0.875rem; &__errors { margin-top: 0.5rem; padding-left: 1.25rem; color: var(--p-red-500, #ef4444); } }
</style>
