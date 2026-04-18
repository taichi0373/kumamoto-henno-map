<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">コミュニティバス路線管理</AppTitle>

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
          <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as CommunityBusAdminDto)" />
          <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as CommunityBusAdminDto)" />
        </template>
      </Column>
    </AppDataTable>

    <AppPaginator :first="page * size" :rows="size" :totalRecords="total" @page="onPageChange" />

    <AppDialog
      v-model="isDialogVisible"
      :header="editTarget?.routeId ? 'コミュニティバス路線編集' : 'コミュニティバス路線登録'"
      :dialogStyle="{ width: '500px' }"
    >
      <div class="form-grid">
        <label>路線ID *</label>
        <AppTextField v-model="form.routeId" :disabled="!!editTarget?.routeId" placeholder="route_001" />
        <label>事業者ID（コミュニティバスID）</label>
        <AppSelect
          v-model="form.communityBusId"
          :options="agencyOptions"
          placeholder="事業者を選択"
          :filter="true"
          :showClear="true"
        />
        <label>行先名称</label>
        <AppTextField v-model="form.routeName" placeholder="行先名称" />
      </div>
      <template #footer>
        <AppButton label="キャンセル" @click="isDialogVisible = false" />
        <AppButton label="保存" :primary="true" @click="save" />
      </template>
    </AppDialog>

    <AppDialog v-model="isDeleteDialogVisible" header="削除確認" :dialogStyle="{ width: '400px' }">
      <p>コミュニティバス路線「{{ deleteTarget?.routeName ?? deleteTarget?.routeId }}」を削除しますか？</p>
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
import AppTitle from '@/components/atoms/AppTitle.vue'
import { SelectDto } from '@/dto/selectDto'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import type { CommunityBusAdminDto, AgencyAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

const items = ref<CommunityBusAdminDto[]>([])
const total = ref(0)
const page = ref(0)
const size = ref(20)
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isDialogVisible = ref(false)
const isDeleteDialogVisible = ref(false)
const editTarget = ref<CommunityBusAdminDto | null>(null)
const deleteTarget = ref<CommunityBusAdminDto | null>(null)
const form = ref<Partial<CommunityBusAdminDto>>({})

/** 事業者選択肢（コミュニティバスID用） */
const agencyOptions = ref<SelectDto[]>([])

/** テーブルカラム定義 */
const columns: AppDataTableColumn[] = [
  { field: 'routeId', header: '路線ID' },
  { field: 'communityBusId', header: '事業者ID' },
  { field: 'routeName', header: '行先名称' },
]

/** 事業者一覧を取得してセレクト選択肢を生成する */
const fetchAgencies = async () => {
  try {
    const response = await apiClient.get<{ data: AgencyAdminDto[] }>('/admin/agencies/all')
    const agencies = response.data?.data ?? []
    agencyOptions.value = agencies.map(
      a => new SelectDto({ label: a.agencyName, text: a.agencyId, value: a.agencyId })
    )
  } catch {
    // 事業者取得失敗は警告のみ（ページ表示は継続）
  }
}

/** 一覧取得 */
const fetchItems = async (targetPage: number) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const response = await apiClient.get<{ data: AdminPagedResponse<CommunityBusAdminDto> }>(
      '/admin/community-buses',
      { params: { page: targetPage, size: size.value } }
    )
    const data = response.data?.data
    items.value = data?.items ?? []
    total.value = data?.total ?? 0
    page.value = targetPage
  } catch {
    errorMessage.value = 'コミュニティバス路線一覧の取得に失敗しました'
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
const openEditDialog = (item: CommunityBusAdminDto) => {
  editTarget.value = item
  form.value = { ...item }
  isDialogVisible.value = true
}

/** 削除確認ダイアログを開く */
const openDeleteDialog = (item: CommunityBusAdminDto) => {
  deleteTarget.value = item
  isDeleteDialogVisible.value = true
}

/** 保存（登録・更新） */
const save = async () => {
  isLoading.value = true
  try {
    if (editTarget.value?.routeId) {
      await apiClient.put(`/admin/community-buses/${editTarget.value.routeId}`, form.value as Record<string, unknown>)
      ToastMessageUtils.success('コミュニティバス路線を更新しました')
    } else {
      await apiClient.post('/admin/community-buses', form.value as Record<string, unknown>)
      ToastMessageUtils.success('コミュニティバス路線を登録しました')
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
    await apiClient.delete(`/admin/community-buses/${deleteTarget.value.routeId}`)
    ToastMessageUtils.success('コミュニティバス路線を削除しました')
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

onMounted(async () => {
  await fetchAgencies()
  await fetchItems(0)
})
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.admin-page {
  &__filter { display: flex; gap: 0.5rem; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; }
}
.form-grid { display: grid; grid-template-columns: 160px 1fr; gap: 0.5rem 1rem; align-items: center; }
</style>
