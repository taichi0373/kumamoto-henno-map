import { ref } from 'vue';
import { FilterMatchMode } from '@primevue/core/api';
import Column from 'primevue/column';
import InputText from 'primevue/inputtext';
import IconField from 'primevue/iconfield';
import InputIcon from 'primevue/inputicon';
import AppButton from './AppButton.vue';
import AppDataTable from './AppDataTable.vue';

/** サンプルカラム定義（フィルターなし） */
const sampleColumns = [
  { field: 'id',     header: 'ID',             sortable: true  },
  { field: 'name',   header: '名前',           sortable: true  },
  { field: 'email',  header: 'メールアドレス', sortable: false },
  { field: 'status', header: 'ステータス',      sortable: true  },
];

/** サンプルカラム定義（フィルターあり） */
const sampleColumnsWithFilter = [
  { field: 'id',     header: 'ID',             sortable: true  },
  { field: 'name',   header: '名前',           sortable: true,  filterPlaceholder: '名前で検索' },
  { field: 'email',  header: 'メールアドレス', sortable: false, filterPlaceholder: 'メールで検索' },
  { field: 'status', header: 'ステータス',      sortable: true  },
];

/** サンプルデータ */
const sampleData = [
  { id: 1, name: '山田 太郎', email: 'yamada@example.com', status: '有効' },
  { id: 2, name: '鈴木 花子', email: 'suzuki@example.com', status: '無効' },
  { id: 3, name: '田中 一郎', email: 'tanaka@example.com', status: '有効' },
  { id: 4, name: '佐藤 次郎', email: 'sato@example.com',   status: '有効' },
  { id: 5, name: '伊藤 三郎', email: 'ito@example.com',    status: '無効' },
];

export default {
  title: 'Design System/Atoms/AppDataTable',
  component: AppDataTable,
  parameters: {
    layout: 'padded',
  },
  tags: ['autodocs'],
  argTypes: {
    value: {
      control: 'object',
      description: '表示するデータ配列',
    },
    columns: {
      control: 'object',
      description: 'カラム定義（field / header / sortable / filterPlaceholder）',
    },
    loading: {
      control: 'boolean',
      description: 'ローディング状態',
    },
    totalRecords: {
      control: { type: 'number', min: 0 },
      description: '総レコード数',
    },
    rows: {
      control: { type: 'number', min: 1, max: 100 },
      description: '1ページあたりの行数',
    },
    first: {
      control: { type: 'number', min: 0 },
      description: '先頭インデックス',
    },
    filterDisplay: {
      control: { type: 'select' },
      options: [undefined, 'row', 'menu'],
      description: 'フィルター表示モード（menu 推奨）',
    },
    globalFilterFields: {
      control: 'object',
      description: 'グローバルフィルター対象フィールド',
    },
    onPageChange: { action: 'page-change' },
    onFilter:     { action: 'filter' },
  },
};

/** デフォルト：データあり */
export const Default = {
  args: {
    value: sampleData,
    columns: sampleColumns,
    loading: false,
    totalRecords: sampleData.length,
    rows: 20,
    first: 0,
  },
};

/** データなし：空状態メッセージを表示 */
export const Empty = {
  args: {
    value: [],
    columns: sampleColumns,
    loading: false,
    totalRecords: 0,
    rows: 20,
    first: 0,
  },
};

/** ローディング中 */
export const Loading = {
  args: {
    ...Default.args,
    loading: true,
  },
};

/** 操作カラムあり：スロットを使用して編集・削除ボタンを追加 */
export const WithActions = {
  render: (args) => ({
    components: { AppDataTable, AppButton, Column },
    setup() {
      return { args };
    },
    template: `
      <AppDataTable v-bind="args" @page-change="args.onPageChange">
        <Column field="actions" header="操作">
          <template #body>
            <div style="display: flex; gap: 8px;">
              <AppButton label="編集" icon="pi pi-pencil" />
              <AppButton label="削除" icon="pi pi-trash" />
            </div>
          </template>
        </Column>
      </AppDataTable>
    `,
  }),
  args: {
    ...Default.args,
  },
};

/** メニューフィルター＋キーワード検索 */
export const WithMenuFilter = {
  render: (args) => ({
    components: { AppDataTable, AppButton, Column, InputText, IconField, InputIcon },
    setup() {
      const filters = ref({
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
        name:   { value: null, matchMode: FilterMatchMode.CONTAINS },
        email:  { value: null, matchMode: FilterMatchMode.CONTAINS },
      });
      const clearFilter = () => {
        filters.value = {
          global: { value: null, matchMode: FilterMatchMode.CONTAINS },
          name:   { value: null, matchMode: FilterMatchMode.CONTAINS },
          email:  { value: null, matchMode: FilterMatchMode.CONTAINS },
        };
      };
      let debounceTimer = null;
      const onGlobalSearch = () => {
        if (debounceTimer) clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
          // 実際のページでは fetchItems(0) を呼ぶ
          console.log('global search:', filters.value.global.value);
        }, 300);
      };
      return { args, filters, clearFilter, onGlobalSearch };
    },
    template: `
      <AppDataTable
        v-bind="args"
        v-model:filters="filters"
        filterDisplay="menu"
        :globalFilterFields="['name', 'email']"
        @page-change="args.onPageChange"
        @filter="args.onFilter"
      >
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <AppButton label="クリア" icon="pi pi-filter-slash" @click="clearFilter" />
            <IconField>
              <InputIcon><i class="pi pi-search" /></InputIcon>
              <InputText v-model="filters['global'].value" placeholder="キーワード検索" @input="onGlobalSearch" />
            </IconField>
          </div>
        </template>
        <Column field="actions" header="操作">
          <template #body>
            <div style="display: flex; gap: 8px;">
              <AppButton label="編集" icon="pi pi-pencil" />
              <AppButton label="削除" icon="pi pi-trash" />
            </div>
          </template>
        </Column>
      </AppDataTable>
    `,
  }),
  args: {
    value: sampleData,
    columns: sampleColumnsWithFilter,
    loading: false,
    totalRecords: sampleData.length,
    rows: 20,
    first: 0,
  },
};

/** 大量データ（ページネーション情報付き） */
export const LargeDataset = {
  args: {
    value: sampleData,
    columns: sampleColumns,
    loading: false,
    totalRecords: 98,
    rows: 5,
    first: 0,
  },
};

/** 全バリエーション一覧 */
export const AllVariants = {
  render: () => ({
    components: { AppDataTable },
    setup() {
      return {
        columns: sampleColumns,
        data: sampleData,
      };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 2rem;">
        <div>
          <p style="margin-bottom: 0.5rem; font-weight: bold;">データあり（通常）</p>
          <AppDataTable :value="data" :columns="columns" :totalRecords="5" />
        </div>
        <div>
          <p style="margin-bottom: 0.5rem; font-weight: bold;">ローディング中</p>
          <AppDataTable :value="[]" :columns="columns" :loading="true" :totalRecords="0" />
        </div>
        <div>
          <p style="margin-bottom: 0.5rem; font-weight: bold;">データなし</p>
          <AppDataTable :value="[]" :columns="columns" :totalRecords="0" />
        </div>
      </div>
    `,
  }),
};
