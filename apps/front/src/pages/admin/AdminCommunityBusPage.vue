<template>
  <div class="admin-page">
    <AppBlockUI :blocked="isLoading" />
    <AppToastMessage />
    <AppTitle :size="'large'" style="margin-bottom: 1rem">コミュニティバス路線管理</AppTitle>

    <Toolbar class="mb-4">
      <template #start>
        <AppButton label="新規登録" :primary="true" icon="pi pi-plus" @click="openCreateDialog" />
      </template>
      <template #end>
        <AppButton label="エクスポート" icon="pi pi-upload" @click="exportCSV" />
      </template>
    </Toolbar>

    <AppMessageBar v-if="errorMessage" mode="error" :message="errorMessage" />

    <AppDataTable
      ref="appDtRef"
      :value="items"
      :columns="columns"
      exportFilename="admin-community-buses"
      :loading="isLoading"
      :totalRecords="total"
      :rows="size"
      :first="page * size"
      v-model:filters="filters"
      filterDisplay="menu"
      :globalFilterFields="['routeName']"
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
            <AppButton label="編集" icon="pi pi-pencil" @click="openEditDialog(data as CommunityBusAdminDto)" />
            <AppButton label="削除" icon="pi pi-trash" @click="openDeleteDialog(data as CommunityBusAdminDto)" />
          </div>
        </template>
      </Column>
    </AppDataTable>

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
import { FilterMatchMode } from '@primevue/core/api'
import Column from 'primevue/column'
import Toolbar from 'primevue/toolbar'
import AppBlockUI from '@/components/atoms/AppBlockUI.vue'
import AppToastMessage from '@/components/atoms/AppToastMessage.vue'
import AppDataTable from '@/components/atoms/AppDataTable.vue'
import type { AppDataTableColumn } from '@/components/atoms/AppDataTable.vue'
import AppDialog from '@/components/atoms/AppDialog.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppTextField from '@/components/atoms/AppTextField.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import AppTitle from '@/components/atoms/AppTitle.vue'
import { SelectDto } from '@/dto/selectDto'
import { ToastMessageUtils } from '@/utils/toastMessageUtils'
import apiClient from '@/utils/api'
import { codeConstant } from '@/utils/codeConstant'
import type { CommunityBusAdminDto, AgencyAdminDto, AdminPagedResponse } from '@/dto/admin/adminDto'

const items = ref<CommunityBusAdminDto[]>([])
const total = ref(0)
const page = ref(0)
/** 1ページあたり件数 */
const size = ref(codeConstant.PAGINATION.ADMIN_PAGE_SIZE)
const isLoading = ref(false)
const errorMessage = ref<string | null>(null)
const isDialogVisible = ref(false)
const isDeleteDialogVisible = ref(false)
const editTarget = ref<CommunityBusAdminDto | null>(null)
const deleteTarget = ref<CommunityBusAdminDto | null>(null)
const form = ref<Partial<CommunityBusAdminDto>>({})
const appDtRef = ref()

/** フィルター（global: キーワード検索、各カラムフィルター） */
const filters = ref({
  global: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  routeId: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  communityBusId: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
  routeName: { value: null as string | null, matchMode: FilterMatchMode.CONTAINS },
})

/** フィルター初期化 */
const initFilters = () => {
  filters.value = {
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
    routeId: { value: null, matchMode: FilterMatchMode.CONTAINS },
    communityBusId: { value: null, matchMode: FilterMatchMode.CONTAINS },
    routeName: { value: null, matchMode: FilterMatchMode.CONTAINS },
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

/** 事業者選択肢（コミュニティバスID用） */
const agencyOptions = ref<SelectDto[]>([])

/** テーブルカラム定義 */
const columns: AppDataTableColumn[] = [
  { field: 'routeId', header: '路線ID', sortable: true, filterPlaceholder: '路線IDで検索' },
  { field: 'communityBusId', header: '事業者ID', sortable: true, filterPlaceholder: '事業者IDで検索' },
  { field: 'routeName', header: '行先名称', sortable: true, filterPlaceholder: '行先名称で検索' },
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
    const keyword = filters.value.global?.value ?? null
    const response = await apiClient.get<{ data: AdminPagedResponse<CommunityBusAdminDto> }>(
      '/admin/community-buses',
      {
        params: {
          page: targetPage,
          size: size.value,
          routeId: filters.value.routeId?.value ?? undefined,
          communityBusId: filters.value.communityBusId?.value ?? undefined,
          routeName: filters.value.routeName?.value ?? keyword ?? undefined,
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
    errorMessage.value = 'コミュニティバス路線一覧の取得に失敗しました'
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

const exportCSV = () => {
  appDtRef.value?.exportCSV()
}

onMounted(async () => {
  await fetchAgencies()
  await fetchItems(0)
})
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.action-buttons {
  display: flex;
  gap: 8px;
}
.form-grid { display: grid; grid-template-columns: 160px 1fr; gap: 0.5rem 1rem; align-items: center; }
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
