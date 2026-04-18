<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">ユーザー管理</AppTitle>

    <AppMessageBar v-if="errorMessage" mode="error" :message="errorMessage" />

    <AppDataTable
      :value="items"
      :columns="columns"
      :loading="isLoading"
      :totalRecords="total"
      :rows="size"
      :first="page * size"
      v-model:filters="filters"
      filterDisplay="menu"
      :globalFilterFields="['username', 'email']"
      @page-change="onPageChange"
      @filter="onFilter"
    >
      <template #header>
        <div class="table-header">
          <AppButton label="クリア" icon="pi pi-filter-slash" @click="clearFilter" />
          <IconField>
            <InputIcon><i class="pi pi-search" /></InputIcon>
            <InputText v-model="filters['global'].value" placeholder="キーワード検索" @input="onGlobalSearch" />
          </IconField>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { FilterMatchMode } from '@primevue/core/api'
import Column from 'primevue/column'
import InputText from 'primevue/inputtext'
import IconField from 'primevue/iconfield'
import InputIcon from 'primevue/inputicon'
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
const editTarget = ref<UserAdminDto | null>(null)
const deleteTarget = ref<UserAdminDto | null>(null)
const form = ref<Partial<UserAdminDto>>({})

/** フィルター（global: キーワード検索、username/email: カラムフィルター） */
const filters = ref({
  global: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  username: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  email: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
})

/** フィルター初期化 */
const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    username: { value: null, matchMode: FilterMatchMode.CONTAINS },
    email: { value: null, matchMode: FilterMatchMode.CONTAINS },
  }
}

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
  { field: 'userId', header: 'ユーザーID' },
  { field: 'username', header: 'ユーザー名', filterPlaceholder: 'ユーザー名で検索' },
  { field: 'email', header: 'メールアドレス', filterPlaceholder: 'メールアドレスで検索' },
  { field: 'birthDate', header: '生年月日' },
  { field: 'municipalityCd', header: '自治体コード' },
  { field: 'licenseStatus', header: '免許状況' },
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
          username: filters.value.username?.value ?? keyword ?? undefined,
          email: filters.value.email?.value ?? keyword ?? undefined,
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
</style>
