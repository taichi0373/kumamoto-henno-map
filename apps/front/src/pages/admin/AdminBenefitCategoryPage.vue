<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">特典カテゴリ管理</AppTitle>

    <div class="admin-page__filter">
      <AppButton label="新規登録" :primary="true" icon="pi pi-plus" @click="openCreateDialog" />
    </div>

    <AppMessageBar v-if="errorMessage" mode="error" :message="errorMessage" />

    <AppDataTable
      :value="items as Record<string, unknown>[]"
      :columns="columns"
      :loading="isLoading"
    >
      <Column field="isActive" header="有効">
        <template #body="{ data }">
          <span :class="['status-badge', data.isActive === '1' ? 'status-badge--active' : 'status-badge--inactive']">
            {{ data.isActive === '1' ? '有効' : '無効' }}
          </span>
        </template>
      </Column>
      <Column field="actions" header="操作">
        <template #body="{ data }">
          <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as BenefitCategoryAdminDto)" />
          <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as BenefitCategoryAdminDto)" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Column from 'primevue/column'
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
import { SelectDto } from '@/dto/selectDto'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import type { BenefitCategoryAdminDto } from '@/dto/admin/adminDto'

const items = ref<BenefitCategoryAdminDto[]>([])
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isDialogVisible = ref(false)
const isDeleteDialogVisible = ref(false)
const editTarget = ref<BenefitCategoryAdminDto | null>(null)
const deleteTarget = ref<BenefitCategoryAdminDto | null>(null)
const form = ref<Partial<BenefitCategoryAdminDto>>({})

/** 有効フラグ選択肢 */
const isActiveOptions: SelectDto[] = [
  new SelectDto({ label: '有効', text: '有効', value: '1' }),
  new SelectDto({ label: '無効', text: '無効', value: '0' }),
]

/** テーブルカラム定義 */
const columns: AppDataTableColumn[] = [
  { field: 'categoryCd', header: 'カテゴリコード' },
  { field: 'categoryName', header: 'カテゴリ名称' },
  { field: 'displayOrder', header: '表示順' },
]

/** 一覧取得 */
const fetchItems = async () => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const response = await apiClient.get<{ data: BenefitCategoryAdminDto[] }>('/admin/benefit-categories')
    items.value = response.data?.data ?? []
  } catch {
    errorMessage.value = '特典カテゴリ一覧の取得に失敗しました'
  } finally {
    isLoading.value = false
  }
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

onMounted(() => fetchItems())
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.admin-page {
  &__filter { display: flex; gap: 0.5rem; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; }
}
.form-grid { display: grid; grid-template-columns: 140px 1fr; gap: 0.5rem 1rem; align-items: center; }
.status-badge {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: bold;
  &--active { background: #d4edda; color: #155724; }
  &--inactive { background: #f8d7da; color: #721c24; opacity: 0.7; }
}
</style>
