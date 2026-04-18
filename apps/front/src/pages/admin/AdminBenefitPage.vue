<template>
  <div class="admin-benefit-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">特典管理</AppTitle>

    <div class="admin-benefit-page__actions">
      <AppButton label="新規登録" :primary="true" icon="pi pi-plus" @click="openCreateDialog" />
    </div>

    <!-- エラー表示 -->
    <AppMessageBar v-if="errorMessage" mode="error" :message="errorMessage" />

    <!-- データテーブル -->
    <AppDataTable
      :value="items"
      :columns="columns"
      :loading="isLoading"
      :totalRecords="total"
      :rows="size"
      :first="page * size"
      v-model:filters="filters"
      filterDisplay="menu"
      :globalFilterFields="['municipalityCd', 'categoryCd']"
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
            <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as BenefitAdminDto)" />
            <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as BenefitAdminDto)" />
          </div>
        </template>
      </Column>
    </AppDataTable>

    <!-- 登録・編集ダイアログ -->
    <AppDialog
      v-model="isDialogVisible"
      :header="editTarget?.benefitId ? '特典編集' : '特典登録'"
      :dialogStyle="{ width: '600px' }"
    >
      <div class="form-grid">
        <label>特典ID *</label>
        <AppTextField v-model="form.benefitId" :disabled="!!editTarget?.benefitId" placeholder="benefit_001" />
        <label>自治体コード *</label>
        <AppTextField v-model="form.municipalityCd" placeholder="43100" />
        <label>カテゴリコード *</label>
        <AppTextField v-model="form.categoryCd" placeholder="01" />
        <label>特典名称</label>
        <AppTextField v-model="form.benefitName" placeholder="特典名称" />
        <label>短縮名称</label>
        <AppTextField v-model="form.benefitShortName" placeholder="短縮名称" />
        <label>特典内容</label>
        <AppTextarea v-model="form.benefitDetail" placeholder="特典内容" />
        <label>有効期限詳細</label>
        <AppTextField v-model="form.expDetail" placeholder="有効期限" />
        <label>電話番号</label>
        <AppTextField v-model="form.phoneNumber" placeholder="096-XXX-XXXX" />
        <label>URL</label>
        <AppTextField v-model="form.benefitUrl" placeholder="https://..." />
      </div>
      <template #footer>
        <AppButton label="キャンセル" @click="isDialogVisible = false" />
        <AppButton label="保存" :primary="true" @click="save" />
      </template>
    </AppDialog>

    <!-- 削除確認ダイアログ -->
    <AppDialog
      v-model="isDeleteDialogVisible"
      header="削除確認"
      :dialogStyle="{ width: '400px' }"
    >
      <p>「{{ deleteTarget?.benefitName ?? deleteTarget?.benefitId }}」を削除しますか？</p>
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
import AppTextarea from '@/components/atoms/AppTextarea.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppTitle from '@/components/atoms/AppTitle.vue'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import { codeConstant } from '@/utils/codeConstant'
import type { BenefitAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

/** データ一覧 */
const items = ref<BenefitAdminDto[]>([])
/** 総件数 */
const total = ref(0)
/** 現在ページ */
const page = ref(0)
/** 1ページあたり件数 */
const size = ref(codeConstant.PAGINATION.ADMIN_PAGE_SIZE)
/** ローディング中 */
const isLoading = ref(false)
/** エラーメッセージ */
const errorMessage = ref<string | null>(null)
/** 登録・編集ダイアログ表示 */
const isDialogVisible = ref(false)
/** 削除確認ダイアログ表示 */
const isDeleteDialogVisible = ref(false)
/** 編集対象 */
const editTarget = ref<BenefitAdminDto | null>(null)
/** 削除対象 */
const deleteTarget = ref<BenefitAdminDto | null>(null)
/** フォームデータ */
const form = ref<Partial<BenefitAdminDto>>({})

/** フィルター（global: キーワード検索、municipalityCd/categoryCd: カラムフィルター） */
const filters = ref({
  global: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  municipalityCd: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  categoryCd: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
})

/** フィルター初期化 */
const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    municipalityCd: { value: null, matchMode: FilterMatchMode.CONTAINS },
    categoryCd: { value: null, matchMode: FilterMatchMode.CONTAINS },
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

/** テーブルカラム定義 */
const columns: AppDataTableColumn[] = [
  { field: 'benefitId', header: '特典ID' },
  { field: 'municipalityCd', header: '自治体コード', filterPlaceholder: '自治体コードで検索' },
  { field: 'categoryCd', header: 'カテゴリコード', filterPlaceholder: 'カテゴリコードで検索' },
  { field: 'benefitName', header: '特典名称' },
  { field: 'expDetail', header: '有効期限' },
]

/** 一覧取得 */
const fetchItems = async (targetPage: number) => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const keyword = filters.value.global?.value ?? null
    const response = await apiClient.get<{ data: AdminPagedResponse<BenefitAdminDto> }>(
      '/admin/benefits',
      {
        params: {
          page: targetPage,
          size: size.value,
          municipalityCd: filters.value.municipalityCd?.value ?? keyword ?? undefined,
          categoryCd: filters.value.categoryCd?.value ?? keyword ?? undefined,
        },
      }
    )
    const data = response.data?.data
    items.value = data?.items ?? []
    total.value = data?.total ?? 0
    page.value = targetPage
  } catch {
    errorMessage.value = '特典一覧の取得に失敗しました'
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

/** 新規登録ダイアログを開く */
const openCreateDialog = () => {
  editTarget.value = null
  form.value = {}
  isDialogVisible.value = true
}

/** 編集ダイアログを開く */
const openEditDialog = (item: BenefitAdminDto) => {
  editTarget.value = item
  form.value = { ...item }
  isDialogVisible.value = true
}

/** 削除確認ダイアログを開く */
const openDeleteDialog = (item: BenefitAdminDto) => {
  deleteTarget.value = item
  isDeleteDialogVisible.value = true
}

/** 保存（登録・更新） */
const save = async () => {
  isLoading.value = true
  try {
    if (editTarget.value?.benefitId) {
      await apiClient.put(`/admin/benefits/${editTarget.value.benefitId}`, form.value as Record<string, unknown>)
      ToastMessageUtils.success('特典を更新しました')
    } else {
      await apiClient.post('/admin/benefits', form.value as Record<string, unknown>)
      ToastMessageUtils.success('特典を登録しました')
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
    await apiClient.delete(`/admin/benefits/${deleteTarget.value.benefitId}`)
    ToastMessageUtils.success('特典を削除しました')
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

.admin-benefit-page {
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

.form-grid {
  display: grid;
  grid-template-columns: 140px 1fr;
  gap: 0.5rem 1rem;
  align-items: center;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
