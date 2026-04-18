import Column from 'primevue/column';
import AppButton from './AppButton.vue';
import AppDataTable from './AppDataTable.vue';

/** サンプルカラム定義 */
const sampleColumns = [
  { field: 'id',     header: 'ID',           sortable: true  },
  { field: 'name',   header: '名前',         sortable: true  },
  { field: 'email',  header: 'メールアドレス', sortable: false },
  { field: 'status', header: 'ステータス',    sortable: true  },
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
      description: 'カラム定義（field / header / sortable）',
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
    onPageChange: { action: 'page-change' },
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

/** ソート可能カラム */
export const Sortable = {
  args: {
    value: sampleData,
    columns: [
      { field: 'id',     header: 'ID',           sortable: true  },
      { field: 'name',   header: '名前',         sortable: true  },
      { field: 'email',  header: 'メールアドレス', sortable: false },
      { field: 'status', header: 'ステータス',    sortable: true  },
    ],
    loading: false,
    totalRecords: sampleData.length,
    rows: 20,
    first: 0,
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
            <AppButton label="編集" icon="pi pi-pencil" style="margin-right: 4px;" />
            <AppButton label="削除" icon="pi pi-trash" />
          </template>
        </Column>
      </AppDataTable>
    `,
  }),
  args: {
    ...Default.args,
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
