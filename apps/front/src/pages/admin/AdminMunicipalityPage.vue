<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <h1 class="admin-page__title">自治体管理</h1>

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
          <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as MunicipalityAdminDto)" />
          <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as MunicipalityAdminDto)" />
        </template>
      </Column>
    </AppDataTable>

    <AppPaginator :first="page * size" :rows="size" :totalRecords="total" @page="onPageChange" />

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
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import { SelectDto } from '@/dto/selectDto'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import type { MunicipalityAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

const items = ref<MunicipalityAdminDto[]>([])
const total = ref(0)
const page = ref(0)
const size = ref(20)
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isDialogVisible = ref(false)
const isDeleteDialogVisible = ref(false)
const editTarget = ref<MunicipalityAdminDto | null>(null)
const deleteTarget = ref<MunicipalityAdminDto | null>(null)
const form = ref<Partial<MunicipalityAdminDto>>({})

/** 自治体区分選択肢（1: 都道府県 / 2: 区 / 3: 市町村） */
const municipalityTypeOptions: SelectDto[] = [
  new SelectDto({ label: '都道府県', text: '都道府県', value: '1' }),
  new SelectDto({ label: '区', text: '区', value: '2' }),
  new SelectDto({ label: '市町村', text: '市町村', value: '3' }),
]

/** テーブルカラム定義 */
const columns: AppDataTableColumn[] = [
  { field: 'municipalityCd', header: '自治体コード' },
  { field: 'municipalityName', header: '自治体名称' },
  { field: 'municipalityKana', header: '自治体名称かな' },
  { field: 'municipalityType', header: '自治体区分' },
]

/** 一覧取得 */
const fetchItems = async (targetPage: number) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const response = await apiClient.get<{ data: AdminPagedResponse<MunicipalityAdminDto> }>(
      '/admin/municipalities',
      { params: { page: targetPage, size: size.value } }
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
const openEditDialog = (item: MunicipalityAdminDto) => {
  editTarget.value = item
  form.value = { ...item }
  isDialogVisible.value = true
}

/** 削除確認ダイアログを開く */
const openDeleteDialog = (item: MunicipalityAdminDto) => {
  deleteTarget.value = item
  isDeleteDialogVisible.value = true
}

/** 保存（登録・更新） */
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

/** 削除実行 */
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
