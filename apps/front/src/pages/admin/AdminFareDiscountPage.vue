<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">運賃割引管理</AppTitle>

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
          <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as FareDiscountAdminDto)" />
          <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as FareDiscountAdminDto)" />
        </template>
      </Column>
    </AppDataTable>

    <AppPaginator :first="page * size" :rows="size" :totalRecords="total" @page="onPageChange" />

    <AppDialog
      v-model="isDialogVisible"
      :header="editTarget?.benefitId && editTarget?.agencyId ? '運賃割引編集' : '運賃割引登録'"
      :dialogStyle="{ width: '500px' }"
    >
      <div class="form-grid">
        <label>特典ID *</label>
        <AppTextField v-model="form.benefitId" :disabled="!!(editTarget?.benefitId && editTarget?.agencyId)" placeholder="benefit_001" />
        <label>事業者ID *</label>
        <AppTextField v-model="form.agencyId" :disabled="!!(editTarget?.benefitId && editTarget?.agencyId)" placeholder="agency_001" />
        <label>割引種別</label>
        <AppSelect
          v-model="form.discountType"
          :options="discountTypeOptions"
          placeholder="選択してください"
          :showClear="true"
        />
        <label>割引値</label>
        <AppNumberField v-model="form.discountValue" :useGrouping="false" />
      </div>
      <template #footer>
        <AppButton label="キャンセル" @click="isDialogVisible = false" />
        <AppButton label="保存" :primary="true" @click="save" />
      </template>
    </AppDialog>

    <AppDialog v-model="isDeleteDialogVisible" header="削除確認" :dialogStyle="{ width: '400px' }">
      <p>運賃割引（特典ID: {{ deleteTarget?.benefitId }} / 事業者ID: {{ deleteTarget?.agencyId }}）を削除しますか？</p>
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
import AppNumberField from '@/components/atoms/AppNumberField.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppTitle from '@/components/atoms/AppTitle.vue'
import { SelectDto } from '@/dto/selectDto'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import type { FareDiscountAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

const items = ref<FareDiscountAdminDto[]>([])
const total = ref(0)
const page = ref(0)
const size = ref(20)
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isDialogVisible = ref(false)
const isDeleteDialogVisible = ref(false)
const editTarget = ref<FareDiscountAdminDto | null>(null)
const deleteTarget = ref<FareDiscountAdminDto | null>(null)
const form = ref<Partial<FareDiscountAdminDto>>({})

/** 割引種別選択肢 */
const discountTypeOptions: SelectDto[] = [
  new SelectDto({ label: '割引率', text: '割引率', value: '0' }),
  new SelectDto({ label: '無料', text: '無料', value: '1' }),
]

/** テーブルカラム定義 */
const columns: AppDataTableColumn[] = [
  { field: 'benefitId', header: '特典ID' },
  { field: 'agencyId', header: '事業者ID' },
  { field: 'discountType', header: '割引種別' },
  { field: 'discountValue', header: '割引値' },
]

/** 一覧取得 */
const fetchItems = async (targetPage: number) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const response = await apiClient.get<{ data: AdminPagedResponse<FareDiscountAdminDto> }>(
      '/admin/fare-discounts',
      { params: { page: targetPage, size: size.value } }
    )
    const data = response.data?.data
    items.value = data?.items ?? []
    total.value = data?.total ?? 0
    page.value = targetPage
  } catch {
    errorMessage.value = '運賃割引一覧の取得に失敗しました'
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
const openEditDialog = (item: FareDiscountAdminDto) => {
  editTarget.value = item
  form.value = { ...item }
  isDialogVisible.value = true
}

/** 削除確認ダイアログを開く */
const openDeleteDialog = (item: FareDiscountAdminDto) => {
  deleteTarget.value = item
  isDeleteDialogVisible.value = true
}

/** 保存（登録・更新） - 複合PKのため判定はbenefitIdとagencyIdの両方 */
const save = async () => {
  isLoading.value = true
  try {
    if (editTarget.value?.benefitId && editTarget.value?.agencyId) {
      await apiClient.put(
        `/admin/fare-discounts/${editTarget.value.benefitId}/${editTarget.value.agencyId}`,
        form.value as Record<string, unknown>
      )
      ToastMessageUtils.success('運賃割引を更新しました')
    } else {
      await apiClient.post('/admin/fare-discounts', form.value as Record<string, unknown>)
      ToastMessageUtils.success('運賃割引を登録しました')
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

/** 削除実行 - 複合PKのためbenefitIdとagencyIdを両方パスパラメータとして送信 */
const confirmDelete = async () => {
  if (!deleteTarget.value) return
  isLoading.value = true
  try {
    await apiClient.delete(`/admin/fare-discounts/${deleteTarget.value.benefitId}/${deleteTarget.value.agencyId}`)
    ToastMessageUtils.success('運賃割引を削除しました')
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
  &__filter { display: flex; gap: 0.5rem; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; }
}
.form-grid { display: grid; grid-template-columns: 140px 1fr; gap: 0.5rem 1rem; align-items: center; }
</style>
