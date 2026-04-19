<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">運賃割引管理</AppTitle>

    <AppToolbar class="mb-4">
      <template #start>
        <AppButton label="新規登録" :primary="true" icon="pi pi-plus" @click="openCreateDialog" />
      </template>
      <template #end>
        <AppButton label="エクスポート" icon="pi pi-upload" @click="exportCSV" />
      </template>
    </AppToolbar>

    <AppMessageBar v-if="errorMessage" mode="error" :message="errorMessage" />

    <AppDataTable
      ref="appDtRef"
      :value="items"
      :columns="columns"
      exportFilename="admin-fare-discounts"
      :loading="isLoading"
      :totalRecords="total"
      :rows="size"
      :first="page * size"
      v-model:filters="filters"
      filterDisplay="menu"
      :globalFilterFields="['benefitId']"
      :sortField="sortField"
      :sortOrder="sortOrder"
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
            <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as FareDiscountAdminDto)" />
            <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as FareDiscountAdminDto)" />
          </div>
        </template>
      </Column>
    </AppDataTable>

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
import AppNumberField from '@/components/atoms/AppNumberField.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppTitle from '@/components/atoms/AppTitle.vue'
import { SelectDto } from '@/dto/selectDto'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import { codeConstant } from '@/utils/codeConstant'
import type { FareDiscountAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

const items = ref<FareDiscountAdminDto[]>([])
const total = ref(0)
const page = ref(0)
/** 1ページあたり件数 */
const size = ref(codeConstant.PAGINATION.ADMIN_PAGE_SIZE)
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isDialogVisible = ref(false)
const isDeleteDialogVisible = ref(false)
const editTarget = ref<FareDiscountAdminDto | null>(null)
const deleteTarget = ref<FareDiscountAdminDto | null>(null)
const form = ref<Partial<FareDiscountAdminDto>>({})
const appDtRef = ref()

/** フィルター（global: キーワード検索、各カラムフィルター） */
const filters = ref({
  global: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  benefitId: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  agencyId: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  discountType: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  discountValue: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
})

/** フィルター初期化 */
const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    benefitId: { value: null, matchMode: FilterMatchMode.CONTAINS },
    agencyId: { value: null, matchMode: FilterMatchMode.CONTAINS },
    discountType: { value: null, matchMode: FilterMatchMode.CONTAINS },
    discountValue: { value: null, matchMode: FilterMatchMode.CONTAINS },
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

/** 割引種別選択肢 */
const discountTypeOptions: SelectDto[] = [
  new SelectDto({ label: '割引率', text: '割引率', value: '0' }),
  new SelectDto({ label: '無料', text: '無料', value: '1' }),
]

/** テーブルカラム定義 */
const columns: AppDataTableColumn[] = [
  { field: 'benefitId', header: '特典ID', sortable: true, filterPlaceholder: '特典IDで検索' },
  { field: 'agencyId', header: '事業者ID', sortable: true, filterPlaceholder: '事業者IDで検索' },
  { field: 'discountType', header: '割引種別', sortable: true, filterPlaceholder: '割引種別で検索' },
  { field: 'discountValue', header: '割引値', sortable: true, filterPlaceholder: '割引値で検索' },
]

/** 一覧取得 */
const fetchItems = async (targetPage: number) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const keyword = filters.value.global?.value ?? null
    const response = await apiClient.get<{ data: AdminPagedResponse<FareDiscountAdminDto> }>(
      '/admin/fare-discounts',
      {
        params: {
          page: targetPage,
          size: size.value,
          benefitId: filters.value.benefitId?.value ?? keyword ?? undefined,
          agencyId: filters.value.agencyId?.value ?? undefined,
          discountType: filters.value.discountType?.value ?? undefined,
          discountValue: filters.value.discountValue?.value ?? undefined,
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
    errorMessage.value = '運賃割引一覧の取得に失敗しました'
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
