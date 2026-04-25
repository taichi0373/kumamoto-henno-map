<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">ユーザー管理</AppTitle>

    <AppMessageBar v-if="errorMessage" mode="error" :message="errorMessage" />

    <!-- ツールバー -->
    <AppToolbar>
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
        <AppButton label="エクスポート" icon="pi pi-upload" @click="exportCSV" />
      </template>
    </AppToolbar>

    <AppDataTable
      ref="appDtRef"
      :value="items"
      :columns="columns"
      exportFilename="admin-users"
      :loading="isLoading"
      :totalRecords="total"
      :rows="size"
      :first="page * size"
      v-model:filters="filters"
      v-model:selection="selectedItems"
      filterDisplay="menu"
      :globalFilterFields="['username', 'email', 'municipalityCd', 'licenseStatus']"
      :sortField="sortField"
      :sortOrder="sortOrder"
      dataKey="userId"
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
            <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as UserAdminDto)" />
            <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as UserAdminDto)" />
          </div>
        </template>
      </Column>
    </AppDataTable>

    <!-- 編集ダイアログ（新規登録なし・ユーザーはセルフ登録のみ） -->
    <AppDialog
      v-model="isDialogVisible"
      header="ユーザー編集"
      :dialogStyle="{ width: '600px' }"
    >
      <div class="form-grid">
        <label>ユーザー名</label>
        <AppTextField v-model="form.username" placeholder="ユーザー名" />
        <label>メールアドレス</label>
        <AppTextField v-model="form.email" placeholder="email@example.com" />
        <label>生年月日</label>
        <AppTextField v-model="form.birthDate" placeholder="1990-01-01" />
        <label>自治体コード</label>
        <AppTextField v-model="form.municipalityCd" placeholder="43100" />
        <label>免許状況</label>
        <AppTextField v-model="form.licenseStatus" placeholder="0,1,2,3" />
        <label>免許返納日</label>
        <AppTextField v-model="form.licenseSurrenderedAt" placeholder="2023-04-01" />
      </div>
      <template #footer>
        <AppButton label="キャンセル" @click="isDialogVisible = false" />
        <AppButton label="保存" :primary="true" @click="save" />
      </template>
    </AppDialog>

    <AppDialog v-model="isDeleteDialogVisible" header="削除確認" :dialogStyle="{ width: '400px' }">
      <p>ユーザー「{{ deleteTarget?.username }}」を削除しますか？</p>
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
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppTitle from '@/components/atoms/AppTitle.vue'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import { codeConstant } from '@/utils/codeConstant'
import type { UserAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

const items = ref<UserAdminDto[]>([])
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
const editTarget = ref<UserAdminDto | null>(null)
const deleteTarget = ref<UserAdminDto | null>(null)
const form = ref<Partial<UserAdminDto>>({})
/** 選択中の行 */
const selectedItems = ref<UserAdminDto[]>([])
/** AppDataTable の ref（exportCSV 呼び出し用） */
const appDtRef = ref()

/** フィルター（global: キーワード検索、各カラムフィルター） */
const filters = ref({
  global: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  userId: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  username: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  email: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  birthDate: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  municipalityCd: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  licenseStatus: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
})

/** フィルター初期化 */
const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    userId: { value: null, matchMode: FilterMatchMode.CONTAINS },
    username: { value: null, matchMode: FilterMatchMode.CONTAINS },
    email: { value: null, matchMode: FilterMatchMode.CONTAINS },
    birthDate: { value: null, matchMode: FilterMatchMode.CONTAINS },
    municipalityCd: { value: null, matchMode: FilterMatchMode.CONTAINS },
    licenseStatus: { value: null, matchMode: FilterMatchMode.CONTAINS },
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

/** テーブルカラム定義（パスワードハッシュ・管理者フラグは表示しない） */
const columns: AppDataTableColumn[] = [
  { field: 'userId', header: 'ユーザーID', sortable: true, filterPlaceholder: 'ユーザーIDで検索' },
  { field: 'username', header: 'ユーザー名', sortable: true, filterPlaceholder: 'ユーザー名で検索' },
  { field: 'email', header: 'メールアドレス', sortable: true, filterPlaceholder: 'メールアドレスで検索' },
  { field: 'birthDate', header: '生年月日', sortable: true, filterPlaceholder: '生年月日で検索' },
  { field: 'municipalityCd', header: '自治体コード', sortable: true, filterPlaceholder: '自治体コードで検索' },
  { field: 'licenseStatus', header: '免許状況', sortable: true, filterPlaceholder: '免許状況で検索' },
]

/** 一覧取得 */
const fetchItems = async (targetPage: number) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const keyword = filters.value.global?.value ?? null
    const response = await apiClient.get<{ data: AdminPagedResponse<UserAdminDto> }>(
      '/admin/users',
      {
        params: {
          page: targetPage,
          size: size.value,
          userId: filters.value.userId?.value ?? undefined,
          username: filters.value.username?.value ?? undefined,
          email: filters.value.email?.value ?? undefined,
          keyword: keyword ?? undefined,
          birthDate: filters.value.birthDate?.value ?? undefined,
          municipalityCd: filters.value.municipalityCd?.value ?? undefined,
          licenseStatus: filters.value.licenseStatus?.value ?? undefined,
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
    errorMessage.value = 'ユーザー一覧の取得に失敗しました'
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

/** 編集ダイアログを開く */
const openEditDialog = (item: UserAdminDto) => {
  editTarget.value = item
  form.value = { ...item }
  isDialogVisible.value = true
}

/** 削除確認ダイアログを開く */
const openDeleteDialog = (item: UserAdminDto) => {
  deleteTarget.value = item
  isDeleteDialogVisible.value = true
}

/** 保存（更新のみ・新規登録なし） */
const save = async () => {
  if (!editTarget.value?.userId) return
  isLoading.value = true
  try {
    await apiClient.put(`/admin/users/${editTarget.value.userId}`, form.value as Record<string, unknown>)
    ToastMessageUtils.success('ユーザーを更新しました')
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
    await apiClient.delete(`/admin/users/${deleteTarget.value.userId}`)
    ToastMessageUtils.success('ユーザーを削除しました')
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
      await apiClient.delete(`/admin/users/${item.userId}`)
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
</style>
