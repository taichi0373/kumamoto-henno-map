import AppButton from './AppButton.vue';
import AppToolbar from './AppToolbar.vue';

export default {
  title: 'Design System/Atoms/AppToolbar',
  component: AppToolbar,
  parameters: {
    layout: 'padded',
  },
  tags: ['autodocs'],
};

/** デフォルト：start / end スロットにボタンを配置 */
export const Default = {
  render: () => ({
    components: { AppToolbar, AppButton },
    template: `
      <AppToolbar>
        <template #start>
          <AppButton label="新規作成" icon="pi pi-plus" />
        </template>
        <template #end>
          <AppButton label="削除" icon="pi pi-trash" />
        </template>
      </AppToolbar>
    `,
  }),
};

/** start のみ */
export const StartOnly = {
  render: () => ({
    components: { AppToolbar, AppButton },
    template: `
      <AppToolbar>
        <template #start>
          <AppButton label="新規作成" icon="pi pi-plus" />
          <AppButton label="インポート" icon="pi pi-upload" style="margin-left: 8px;" />
        </template>
      </AppToolbar>
    `,
  }),
};

/** end のみ */
export const EndOnly = {
  render: () => ({
    components: { AppToolbar, AppButton },
    template: `
      <AppToolbar>
        <template #end>
          <AppButton label="CSVエクスポート" icon="pi pi-download" />
        </template>
      </AppToolbar>
    `,
  }),
};

/** center スロットにタイトルを表示 */
export const WithCenter = {
  render: () => ({
    components: { AppToolbar, AppButton },
    template: `
      <AppToolbar>
        <template #start>
          <AppButton label="戻る" icon="pi pi-arrow-left" />
        </template>
        <template #center>
          <span style="font-size: 1.1rem; font-weight: bold;">ユーザー管理</span>
        </template>
        <template #end>
          <AppButton label="保存" icon="pi pi-save" />
        </template>
      </AppToolbar>
    `,
  }),
};

/** 複数ボタン：start と end に複数ボタンを並べる */
export const MultipleButtons = {
  render: () => ({
    components: { AppToolbar, AppButton },
    template: `
      <AppToolbar>
        <template #start>
          <div style="display: flex; gap: 8px;">
            <AppButton label="新規作成" icon="pi pi-plus" />
            <AppButton label="インポート" icon="pi pi-upload" />
          </div>
        </template>
        <template #end>
          <div style="display: flex; gap: 8px;">
            <AppButton label="CSVエクスポート" icon="pi pi-download" />
            <AppButton label="削除" icon="pi pi-trash" />
          </div>
        </template>
      </AppToolbar>
    `,
  }),
};

/** スロットなし：空のツールバー */
export const Empty = {
  render: () => ({
    components: { AppToolbar },
    template: `<AppToolbar />`,
  }),
};

/** 全バリエーション一覧 */
export const AllVariants = {
  render: () => ({
    components: { AppToolbar, AppButton },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1.5rem;">
        <div>
          <p style="margin-bottom: 0.5rem; font-weight: bold;">デフォルト（start + end）</p>
          <AppToolbar>
            <template #start>
              <AppButton label="新規作成" icon="pi pi-plus" />
            </template>
            <template #end>
              <AppButton label="削除" icon="pi pi-trash" />
            </template>
          </AppToolbar>
        </div>
        <div>
          <p style="margin-bottom: 0.5rem; font-weight: bold;">center スロットあり</p>
          <AppToolbar>
            <template #start>
              <AppButton label="戻る" icon="pi pi-arrow-left" />
            </template>
            <template #center>
              <span style="font-size: 1.1rem; font-weight: bold;">ユーザー管理</span>
            </template>
            <template #end>
              <AppButton label="保存" icon="pi pi-save" />
            </template>
          </AppToolbar>
        </div>
        <div>
          <p style="margin-bottom: 0.5rem; font-weight: bold;">複数ボタン</p>
          <AppToolbar>
            <template #start>
              <div style="display: flex; gap: 8px;">
                <AppButton label="新規作成" icon="pi pi-plus" />
                <AppButton label="インポート" icon="pi pi-upload" />
              </div>
            </template>
            <template #end>
              <div style="display: flex; gap: 8px;">
                <AppButton label="CSVエクスポート" icon="pi pi-download" />
                <AppButton label="削除" icon="pi pi-trash" />
              </div>
            </template>
          </AppToolbar>
        </div>
        <div>
          <p style="margin-bottom: 0.5rem; font-weight: bold;">空のツールバー</p>
          <AppToolbar />
        </div>
      </div>
    `,
  }),
};
