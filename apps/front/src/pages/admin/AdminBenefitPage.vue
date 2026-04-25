<template>
  <div class="admin-benefit-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">特典管理</AppTitle>

    <!-- エラー表示 -->
    <AppMessageBar v-if="errorMessage" mode="error" :message="errorMessage" />

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

    <!-- データテーブル -->
    <AppDataTable
      ref="appDtRef"
      :value="items"
      :columns="columns"
      exportFilename="admin-benefits"
      :loading="isLoading"
      :totalRecords="total"
      :rows="size"
      :first="page * size"
      v-model:filters="filters"
      v-model:selection="selectedItems"
      filterDisplay="menu"
      :globalFilterFields="['benefitId', 'municipalityCd', 'categoryCd', 'benefitName', 'expDetail']"
      :sortField="sortField"
      :sortOrder="sortOrder"
      dataKey="benefitId"
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
            <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as BenefitAdminDto)" />
            <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as BenefitAdminDto)" />
          </div>
        </template>
      </Column>
    </AppDataTable>

    <!-- 登録・編集ダイアログ -->
    <AppDialog
      v-model="isDialogVisible"
      :header="editTarget?.benefitId ? '特典編集' : '特典登録'"
      :dialogStyle="{ width: '600px' }"
    >
      <div class="form-grid">
        <label>特典ID *</label>
        <AppTextField v-model="form.benefitId" :disabled="!!editTarget?.benefitId" placeholder="benefit_001" />
        <label>自治体コード *</label>
        <AppTextField v-model="form.municipalityCd" placeholder="43100" />
        <label>カテゴリコード *</label>
        <AppTextField v-model="form.categoryCd" placeholder="01" />
        <label>特典名称</label>
        <AppTextField v-model="form.benefitName" placeholder="特典名称" />
        <label>短縮名称</label>
        <AppTextField v-model="form.benefitShortName" placeholder="短縮名称" />
        <label>特典内容</label>
        <AppTextarea v-model="form.benefitDetail" placeholder="特典内容" />
        <label>有効期限詳細</label>
        <AppTextField v-model="form.expDetail" placeholder="有効期限" />
        <label>電話番号</label>
        <AppTextField v-model="form.phoneNumber" placeholder="096-XXX-XXXX" />
        <label>URL</label>
        <AppTextField v-model="form.benefitUrl" placeholder="https://..." />
      </div>
      <template #footer>
        <AppButton label="キャンセル" @click="isDialogVisible = false" />
        <AppButton label="保存" :primary="true" @click="save" />
      </template>
    </AppDialog>

    <!-- 削除確認ダイアログ -->
    <AppDialog
      v-model="isDeleteDialogVisible"
      header="削除確認"
      :dialogStyle="{ width: '400px' }"
    >
      <p>「{{ deleteTarget?.benefitName ?? deleteTarget?.benefitId }}」を削除しますか？</p>
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

    <!-- CSVインポートダイアログ -->
    <AppDialog v-model="isImportDialogVisible" header="CSVインポート" :dialogStyle="{ width: '520px' }">
      <div class="import-dialog">
        <p class="import-dialog__desc">CSVファイルを選択してインポートします。</p>
        <div class="import-dialog__columns">
          <div class="import-dialog__columns-row">
            <span class="import-dialog__columns-badge import-dialog__columns-badge--required">必須</span>
            <div class="import-dialog__columns-tags">
              <code>benefitId</code>
            </div>
          </div>
          <div class="import-dialog__columns-row">
            <span class="import-dialog__columns-badge">任意</span>
            <div class="import-dialog__columns-tags">
              <code>municipalityCd</code>
              <code>categoryCd</code>
              <code>benefitName</code>
              <code>benefitShortName</code>
              <code>benefitDetail</code>
              <code>expDetail</code>
              <code>phoneNumber</code>
              <code>benefitUrl</code>
            </div>
          </div>
        </div>
        <div class="import-dialog__upload">
          <AppFileUpload ref="fileUploadRef" v-model="importFile" accept=".csv" chooseLabel="CSVを選択" />
        </div>
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
import AppTextarea from '@/components/atoms/AppTextarea.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppTitle from '@/components/atoms/AppTitle.vue'
import AppFileUpload from '@/components/atoms/AppFileUpload.vue'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import { codeConstant } from '@/utils/codeConstant'
import type { BenefitAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

/** データ一覧 */
const items = ref<BenefitAdminDto[]>([])
/** 総件数 */
const total = ref(0)
/** 現在ページ */
const page = ref(0)
/** 1ページあたり件数 */
const size = ref(codeConstant.PAGINATION.ADMIN_PAGE_SIZE)
/** ローディング中 */
const isLoading = ref(false)
/** エラーメッセージ */
const errorMessage = ref<string | null>(null)
/** 登録・編集ダイアログ表示 */
const isDialogVisible = ref(false)
/** 削除確認ダイアログ表示 */
const isDeleteDialogVisible = ref(false)
/** 一括削除確認ダイアログ表示 */
const isDeleteSelectedDialogVisible = ref(false)
/** 編集対象 */
const editTarget = ref<BenefitAdminDto | null>(null)
/** 削除対象 */
const deleteTarget = ref<BenefitAdminDto | null>(null)
/** フォームデータ */
const form = ref<Partial<BenefitAdminDto>>({})
/** 選択中の行 */
const selectedItems = ref<BenefitAdminDto[]>([])
/** AppDataTable の ref（exportCSV 呼び出し用） */
const appDtRef = ref()
/** CSVインポートダイアログ表示 */
const isImportDialogVisible = ref(false)
/** インポート中フラグ */
const isImporting = ref(false)
/** 選択中のインポートファイル */
const importFile = ref<File | null>(null)
/** AppFileUpload の ref */
const fileUploadRef = ref()

/** フィルター（global: キーワード検索、各カラムフィルター） */
const filters = ref({
  global: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  benefitId: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  municipalityCd: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  categoryCd: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  benefitName: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  expDetail: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
})

/** フィルター初期化 */
const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    benefitId: { value: null, matchMode: FilterMatchMode.CONTAINS },
    municipalityCd: { value: null, matchMode: FilterMatchMode.CONTAINS },
    categoryCd: { value: null, matchMode: FilterMatchMode.CONTAINS },
    benefitName: { value: null, matchMode: FilterMatchMode.CONTAINS },
    expDetail: { value: null, matchMode: FilterMatchMode.CONTAINS },
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

/** テーブルカラム定義 */
const columns: AppDataTableColumn[] = [
  { field: 'benefitId', header: '特典ID', sortable: true, filterPlaceholder: '特典IDで検索' },
  { field: 'municipalityCd', header: '自治体コード', sortable: true, filterPlaceholder: '自治体コードで検索' },
  { field: 'categoryCd', header: 'カテゴリコード', sortable: true, filterPlaceholder: 'カテゴリコードで検索' },
  { field: 'benefitName', header: '特典名称', sortable: true, filterPlaceholder: '特典名称で検索' },
  { field: 'expDetail', header: '有効期限', sortable: true, filterPlaceholder: '有効期限で検索' },
]

/** 一覧取得 */
const fetchItems = async (targetPage: number) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const keyword = filters.value.global?.value ?? null
    const response = await apiClient.get<{ data: AdminPagedResponse<BenefitAdminDto> }>(
      '/admin/benefits',
      {
        params: {
          page: targetPage,
          size: size.value,
          benefitId: filters.value.benefitId?.value ?? undefined,
          municipalityCd: filters.value.municipalityCd?.value ?? undefined,
          categoryCd: filters.value.categoryCd?.value ?? undefined,
          benefitName: filters.value.benefitName?.value ?? undefined,
          keyword: keyword ?? undefined,
          expDetail: filters.value.expDetail?.value ?? undefined,
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
    errorMessage.value = '特典一覧の取得に失敗しました'
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
  fetchItems(Math.floor(event.first / event.rows))
}

/** 新規登録ダイアログを開く */
const openCreateDialog = () => {
  editTarget.value = null
  form.value = {}
  isDialogVisible.value = true
}

/** 編集ダイアログを開く */
const openEditDialog = (item: BenefitAdminDto) => {
  editTarget.value = item
  form.value = { ...item }
  isDialogVisible.value = true
}

/** 削除確認ダイアログを開く */
const openDeleteDialog = (item: BenefitAdminDto) => {
  deleteTarget.value = item
  isDeleteDialogVisible.value = true
}

/** 保存（登録・更新） */
const save = async () => {
  isLoading.value = true
  try {
    if (editTarget.value?.benefitId) {
      await apiClient.put(`/admin/benefits/${editTarget.value.benefitId}`, form.value as Record<string, unknown>)
      ToastMessageUtils.success('特典を更新しました')
    } else {
      await apiClient.post('/admin/benefits', form.value as Record<string, unknown>)
      ToastMessageUtils.success('特典を登録しました')
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

/** 削除実行 */
const confirmDelete = async () => {
  if (!deleteTarget.value) return
  isLoading.value = true
  try {
    await apiClient.delete(`/admin/benefits/${deleteTarget.value.benefitId}`)
    ToastMessageUtils.success('特典を削除しました')
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

/** 一括削除実行 */
const deleteSelectedItems = async () => {
  isLoading.value = true
  let successCount = 0
  let failCount = 0
  for (const item of selectedItems.value) {
    try {
      await apiClient.delete(`/admin/benefits/${item.benefitId}`)
      successCount++
    } catch {
      failCount++
    }
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

/** CSVエクスポート */
const exportCSV = () => {
  appDtRef.value?.exportCSV()
}

/** インポートダイアログを開く */
const openImportDialog = () => {
  importFile.value = null
  isImportDialogVisible.value = true
}

/** インポートダイアログを閉じる */
const closeImportDialog = () => {
  isImportDialogVisible.value = false
  fileUploadRef.value?.clear()
}

/** CSVインポート実行 */
const importCSV = async () => {
  if (!importFile.value) return
  isImporting.value = true
  try {
    const formData = new FormData()
    formData.append('file', importFile.value)
    const response = await apiClient.axios.post<{ data: { inserted: number; updated: number } }>(
      '/admin/benefits/import',
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    )
    const result = response.data?.data
    ToastMessageUtils.success(
      `インポート完了: 登録 ${result?.inserted ?? 0} 件`
    )
    closeImportDialog()
    await fetchItems(page.value)
  } catch (error: unknown) {
    const msg = (error as { response?: { data?: { message?: string } } })?.response?.data?.message
    ToastMessageUtils.error('登録に失敗しました')
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

.form-grid {
  display: grid;
  grid-template-columns: 140px 1fr;
  gap: 0.5rem 1rem;
  align-items: center;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.table-header__search {
  width: 16rem;
  max-width: 100%;
}

.import-dialog {
  display: flex;
  flex-direction: column;
  gap: 1rem;

  &__desc {
    margin: 0;
    font-size: 0.875rem;
    color: var(--p-text-color-secondary, #6c757d);
  }

  &__columns {
    padding: 0.75rem;
    background: var(--p-surface-50, #f9fafb);
    border: 1px solid var(--p-surface-200, #e5e7eb);
    border-radius: 6px;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  &__columns-row {
    display: flex;
    align-items: flex-start;
    gap: 0.5rem;
  }

  &__columns-badge {
    flex-shrink: 0;
    font-size: 0.7rem;
    font-weight: 600;
    padding: 0.15rem 0.4rem;
    border-radius: 3px;
    background: var(--p-surface-300, #d1d5db);
    color: var(--p-text-color-secondary, #6c757d);
    margin-top: 2px;

    &--required {
      background: var(--p-red-100, #fee2e2);
      color: var(--p-red-700, #b91c1c);
    }
  }

  &__columns-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 0.25rem;

    code {
      font-size: 0.8rem;
      padding: 0.1rem 0.35rem;
      background: var(--p-surface-200, #e5e7eb);
      border-radius: 3px;
    }
  }

  &__upload {
    display: flex;
    align-items: center;
  }
}
</style>
