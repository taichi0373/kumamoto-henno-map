<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <h1 class="admin-page__title">ユーザー管理</h1>

    <div class="admin-page__filter">
      <AppTextField v-model="filterUsername" placeholder="ユーザー名" :inputStyle="{ width: '180px' }" />
      <AppTextField v-model="filterEmail" placeholder="メールアドレス" :inputStyle="{ width: '220px' }" />
      <AppButton label="検索" @click="fetchItems(0)" />
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
          <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as UserAdminDto)" />
          <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as UserAdminDto)" />
        </template>
      </Column>
    </AppDataTable>

    <AppPaginator :first="page * size" :rows="size" :totalRecords="total" @page="onPageChange" />

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
import type { UserAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

const items = ref<UserAdminDto[]>([])
const total = ref(0)
const page = ref(0)
const size = ref(20)
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isDialogVisible = ref(false)
const isDeleteDialogVisible = ref(false)
const editTarget = ref<UserAdminDto | null>(null)
const deleteTarget = ref<UserAdminDto | null>(null)
const form = ref<Partial<UserAdminDto>>({})

/** フィルター */
const filterUsername = ref<string | null>(null)
const filterEmail = ref<string | null>(null)

/** テーブルカラム定義（パスワードハッシュ・管理者フラグは表示しない） */
const columns: AppDataTableColumn[] = [
  { field: 'userId', header: 'ユーザーID' },
  { field: 'username', header: 'ユーザー名' },
  { field: 'email', header: 'メールアドレス' },
  { field: 'birthDate', header: '生年月日' },
  { field: 'municipalityCd', header: '自治体コード' },
  { field: 'licenseStatus', header: '免許状況' },
]

/** 一覧取得 */
const fetchItems = async (targetPage: number) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const response = await apiClient.get<{ data: AdminPagedResponse<UserAdminDto> }>(
      '/admin/users',
      {
        params: {
          page: targetPage,
          size: size.value,
          username: filterUsername.value ?? undefined,
          email: filterEmail.value ?? undefined,
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
.admin-page {
  &__title { font-size: 1.5rem; font-weight: bold; margin-bottom: 1rem; color: base.$base-700; }
  &__filter { display: flex; gap: 0.5rem; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; }
}
.form-grid { display: grid; grid-template-columns: 140px 1fr; gap: 0.5rem 1rem; align-items: center; }
</style>
