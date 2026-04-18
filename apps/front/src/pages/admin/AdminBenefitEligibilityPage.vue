<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">特典条件管理</AppTitle>

    <div class="admin-page__actions">
      <AppButton label="新規登録" :primary="true" icon="pi pi-plus" @click="openCreateDialog" />
    </div>

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
      :globalFilterFields="['benefitId']"
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
            <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as BenefitEligibilityAdminDto)" />
            <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as BenefitEligibilityAdminDto)" />
          </div>
        </template>
      </Column>
    </AppDataTable>

    <AppDialog v-model="isDialogVisible" :header="editTarget?.id ? '特典条件編集' : '特典条件登録'" :dialogStyle="{ width: '500px' }">
      <div class="form-grid">
        <label>特典ID *</label>
        <AppTextField v-model="form.benefitId" placeholder="benefit_001" />
        <label>免許状況</label>
        <AppTextField v-model="form.licenseStatus" placeholder="0,1,2,3" />
        <label>最低年齢</label>
        <AppNumberField v-model="form.minAge" :useGrouping="false" />
        <label>最高年齢</label>
        <AppNumberField v-model="form.maxAge" :useGrouping="false" />
        <label>自治体コード</label>
        <AppTextField v-model="form.municipalityCd" placeholder="43100" />
        <label>備考</label>
        <AppTextField v-model="form.note" placeholder="備考" />
      </div>
      <template #footer>
        <AppButton label="キャンセル" @click="isDialogVisible = false" />
        <AppButton label="保存" :primary="true" @click="save" />
      </template>
    </AppDialog>

    <AppDialog v-model="isDeleteDialogVisible" header="削除確認" :dialogStyle="{ width: '400px' }">
      <p>特典条件（ID: {{ deleteTarget?.id }}）を削除しますか？</p>
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
import AppNumberField from '@/components/atoms/AppNumberField.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppTitle from '@/components/atoms/AppTitle.vue'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import { codeConstant } from '@/utils/codeConstant'
import type { BenefitEligibilityAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

const items = ref<BenefitEligibilityAdminDto[]>([])
const total = ref(0)
const page = ref(0)
/** 1ページあたり件数 */
const size = ref(codeConstant.PAGINATION.ADMIN_PAGE_SIZE)
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isDialogVisible = ref(false)
const isDeleteDialogVisible = ref(false)
const editTarget = ref<BenefitEligibilityAdminDto | null>(null)
const deleteTarget = ref<BenefitEligibilityAdminDto | null>(null)
const form = ref<Partial<BenefitEligibilityAdminDto>>({})

/** フィルター（global: キーワード検索、benefitId: カラムフィルター） */
const filters = ref({
  global: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  benefitId: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
})

/** フィルター初期化 */
const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    benefitId: { value: null, matchMode: FilterMatchMode.CONTAINS },
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

const columns: AppDataTableColumn[] = [
  { field: 'id', header: 'ID' },
  { field: 'benefitId', header: '特典ID', filterPlaceholder: '特典IDで検索' },
  { field: 'licenseStatus', header: '免許状況' },
  { field: 'minAge', header: '最低年齢' },
  { field: 'maxAge', header: '最高年齢' },
  { field: 'municipalityCd', header: '自治体コード' },
]

const fetchItems = async (targetPage: number) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const keyword = filters.value.global?.value ?? null
    const response = await apiClient.get<{ data: AdminPagedResponse<BenefitEligibilityAdminDto> }>(
      '/admin/benefit-eligibilities',
      {
        params: {
          page: targetPage,
          size: size.value,
          benefitId: filters.value.benefitId?.value ?? keyword ?? undefined,
        },
      }
    )
    const data = response.data?.data
    items.value = data?.items ?? []
    total.value = data?.total ?? 0
    page.value = targetPage
  } catch {
    errorMessage.value = '特典条件一覧の取得に失敗しました'
  } finally {
    isLoading.value = false
  }
}

/** フィルター変更時 */
const onFilter = () => {
  fetchItems(0)
}

const onPageChange = (event: { first: number; rows: number }) => {
  size.value = event.rows
  fetchItems(Math.floor(event.first / event.rows))
}

const openCreateDialog = () => {
  editTarget.value = null
  form.value = {}
  isDialogVisible.value = true
}

const openEditDialog = (item: BenefitEligibilityAdminDto) => {
  editTarget.value = item
  form.value = { ...item }
  isDialogVisible.value = true
}

const openDeleteDialog = (item: BenefitEligibilityAdminDto) => {
  deleteTarget.value = item
  isDeleteDialogVisible.value = true
}

const save = async () => {
  /** MIN_AGE > MAX_AGE バリデーション */
  if (form.value.minAge != null && form.value.maxAge != null && form.value.minAge > form.value.maxAge) {
    errorMessage.value = '最低年齢が最高年齢より大きい値です'
    return
  }
  isLoading.value = true
  try {
    if (editTarget.value?.id) {
      await apiClient.put(`/admin/benefit-eligibilities/${editTarget.value.id}`, form.value as Record<string, unknown>)
      ToastMessageUtils.success('特典条件を更新しました')
    } else {
      await apiClient.post('/admin/benefit-eligibilities', form.value as Record<string, unknown>)
      ToastMessageUtils.success('特典条件を登録しました')
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

const confirmDelete = async () => {
  if (!deleteTarget.value) return
  isLoading.value = true
  try {
    await apiClient.delete(`/admin/benefit-eligibilities/${deleteTarget.value.id}`)
    ToastMessageUtils.success('特典条件を削除しました')
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
  &__actions {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-bottom: 1rem;
  }
}
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
