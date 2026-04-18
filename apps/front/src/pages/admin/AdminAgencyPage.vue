<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <h1 class="admin-page__title">事業者管理</h1>

    <div class="admin-page__filter">
      <AppButton label="新規登録" :primary="true" icon="pi pi-plus" @click="openCreateDialog" />
    </div>

    <AppMessageBar v-if="errorMessage" mode="error" :message="errorMessage" />

    <AppDataTable
      :value="items as Record<string, unknown>[]"
      :columns="columns"
      :loading="isLoading"
      :totalRecords="total"
      :rows="size"
      :first="page * size"
      @page-change="onPageChange"
    >
      <Column field="actions" header="操作">
        <template #body="{ data }">
          <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as AgencyAdminDto)" />
          <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as AgencyAdminDto)" />
        </template>
      </Column>
    </AppDataTable>

    <AppPaginator :first="page * size" :rows="size" :totalRecords="total" @page="onPageChange" />

    <AppDialog
      v-model="isDialogVisible"
      :header="editTarget?.agencyId ? '事業者編集' : '事業者登録'"
      :dialogStyle="{ width: '600px' }"
    >
      <div class="form-grid">
        <label>事業者ID *</label>
        <AppTextField v-model="form.agencyId" :disabled="!!editTarget?.agencyId" placeholder="agency_001" />
        <label>事業者名称 *</label>
        <AppTextField v-model="form.agencyName" placeholder="熊本バス" />
        <label>事業者名称かな</label>
        <AppTextField v-model="form.agencyKana" placeholder="くまもとばす" />
        <label>電話番号</label>
        <AppTextField v-model="form.phoneNumber" placeholder="096-XXX-XXXX" />
        <label>URL</label>
        <AppTextField v-model="form.agencyUrl" placeholder="https://..." />
        <label>運行事業者ID</label>
        <AppTextField v-model="form.operatorId" placeholder="operator_001" />
      </div>
      <template #footer>
        <AppButton label="キャンセル" @click="isDialogVisible = false" />
        <AppButton label="保存" :primary="true" @click="save" />
      </template>
    </AppDialog>

    <AppDialog v-model="isDeleteDialogVisible" header="削除確認" :dialogStyle="{ width: '400px' }">
      <p>事業者「{{ deleteTarget?.agencyName }}」を削除しますか？</p>
      <template #footer>
        <AppButton label="キャンセル" @click="isDeleteDialogVisible = false" />
        <AppButton label="削除" :primary="true" @click="confirmDelete" />
      </template>
    </AppDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Column from 'primevue/column'
import AppBlockUI from '@/components/atoms/AppBlockUI.vue'
import AppToastMessage from '@/components/atoms/AppToastMessage.vue'
import AppDataTable from '@/components/atoms/AppDataTable.vue'
import type { AppDataTableColumn } from '@/components/atoms/AppDataTable.vue'
import AppPaginator from '@/components/atoms/AppPaginator.vue'
import AppDialog from '@/components/atoms/AppDialog.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import type { AgencyAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

const items = ref<AgencyAdminDto[]>([])
const total = ref(0)
const page = ref(0)
const size = ref(20)
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isDialogVisible = ref(false)
const isDeleteDialogVisible = ref(false)
const editTarget = ref<AgencyAdminDto | null>(null)
const deleteTarget = ref<AgencyAdminDto | null>(null)
const form = ref<Partial<AgencyAdminDto>>({})

/** テーブルカラム定義 */
const columns: AppDataTableColumn[] = [
  { field: 'agencyId', header: '事業者ID' },
  { field: 'agencyName', header: '事業者名称' },
  { field: 'agencyKana', header: '事業者名称かな' },
  { field: 'phoneNumber', header: '電話番号' },
  { field: 'operatorId', header: '運行事業者ID' },
]

/** 一覧取得 */
const fetchItems = async (targetPage: number) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const response = await apiClient.get<{ data: AdminPagedResponse<AgencyAdminDto> }>(
      '/admin/agencies',
      { params: { page: targetPage, size: size.value } }
    )
    const data = response.data?.data
    items.value = data?.items ?? []
    total.value = data?.total ?? 0
    page.value = targetPage
  } catch {
    errorMessage.value = '事業者一覧の取得に失敗しました'
  } finally {
    isLoading.value = false
  }
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
const openEditDialog = (item: AgencyAdminDto) => {
  editTarget.value = item
  form.value = { ...item }
  isDialogVisible.value = true
}

/** 削除確認ダイアログを開く */
const openDeleteDialog = (item: AgencyAdminDto) => {
  deleteTarget.value = item
  isDeleteDialogVisible.value = true
}

/** 保存（登録・更新） */
const save = async () => {
  isLoading.value = true
  try {
    if (editTarget.value?.agencyId) {
      await apiClient.put(`/admin/agencies/${editTarget.value.agencyId}`, form.value as Record<string, unknown>)
      ToastMessageUtils.success('事業者を更新しました')
    } else {
      await apiClient.post('/admin/agencies', form.value as Record<string, unknown>)
      ToastMessageUtils.success('事業者を登録しました')
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
    await apiClient.delete(`/admin/agencies/${deleteTarget.value.agencyId}`)
    ToastMessageUtils.success('事業者を削除しました')
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
.admin-page {
  &__title { font-size: 1.5rem; font-weight: bold; margin-bottom: 1rem; color: base.$base-700; }
  &__filter { display: flex; gap: 0.5rem; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; }
}
.form-grid { display: grid; grid-template-columns: 140px 1fr; gap: 0.5rem 1rem; align-items: center; }
</style>
