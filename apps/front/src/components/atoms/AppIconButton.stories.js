import AppIconButton from './AppIconButton.vue';

export default {
  title: 'Design System/Atoms/AppIconButton',
  component: AppIconButton,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    icon: {
      control: 'text',
      description: 'アイコン（PrimeIcons名、例: pi-plus）',
    },
    type: {
      control: { type: 'select' },
      options: ['button', 'submit', 'reset'],
      description: 'ボタンタイプ',
    },
    severity: {
      control: { type: 'select' },
      options: ['primary', 'secondary', 'success', 'info', 'warn', 'danger', 'help', 'contrast'],
      description: 'バリアント',
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large', 'sidebarToggle'],
      description: 'サイズ',
    },
    shape: {
      control: { type: 'select' },
      options: ['square', 'rounded', 'circle'],
      description: '形状',
    },
    disabled: {
      control: 'boolean',
      description: '無効化',
    },
    loading: {
      control: 'boolean',
      description: 'ローディング状態',
    },
    outlined: {
      control: 'boolean',
      description: 'アウトライン',
    },
    text: {
      control: 'boolean',
      description: 'テキストボタン',
    },
    rounded: {
      control: 'boolean',
      description: '丸いボタン',
    },
    tooltip: {
      control: 'text',
      description: 'ツールチップ',
    },
    ariaLabel: {
      control: 'text',
      description: 'アクセシビリティ用ラベル',
    },
    onClick: { action: 'clicked' },
  },
};

export const Primary = {
  args: {
    icon: 'pi pi-plus',
    severity: 'primary',
    size: 'medium',
    shape: 'square',
    tooltip: '新規作成',
    ariaLabel: '新規作成ボタン',
  },
};

export const Secondary = {
  args: {
    icon: 'pi pi-pencil',
    severity: 'secondary',
    size: 'medium',
    shape: 'square',
    tooltip: '編集',
    ariaLabel: '編集ボタン',
  },
};

export const Circle = {
  args: {
    icon: 'pi pi-search',
    severity: 'primary',
    size: 'medium',
    shape: 'circle',
    tooltip: '検索',
    ariaLabel: '検索ボタン',
  },
};

export const Rounded = {
  args: {
    icon: 'pi pi-cog',
    severity: 'secondary',
    size: 'medium',
    rounded: true,
    tooltip: '設定',
    ariaLabel: '設定ボタン',
  },
};

export const Small = {
  args: {
    icon: 'pi pi-times',
    severity: 'danger',
    size: 'small',
    shape: 'circle',
    tooltip: '削除',
    ariaLabel: '削除ボタン',
  },
};

export const Large = {
  args: {
    icon: 'pi pi-download',
    severity: 'success',
    size: 'large',
    shape: 'square',
    tooltip: 'ダウンロード',
    ariaLabel: 'ダウンロードボタン',
  },
};

export const Outlined = {
  args: {
    icon: 'pi pi-heart',
    severity: 'primary',
    size: 'medium',
    shape: 'circle',
    outlined: true,
    tooltip: 'お気に入り',
    ariaLabel: 'お気に入りボタン',
  },
};

export const Text = {
  args: {
    icon: 'pi pi-info-circle',
    severity: 'info',
    size: 'medium',
    text: true,
    tooltip: '情報',
    ariaLabel: '情報ボタン',
  },
};

export const Loading = {
  args: {
    icon: 'pi pi-refresh',
    severity: 'primary',
    size: 'medium',
    loading: true,
    tooltip: '読み込み中',
    ariaLabel: '更新ボタン',
  },
};

export const Disabled = {
  args: {
    icon: 'pi pi-save',
    severity: 'primary',
    size: 'medium',
    disabled: true,
    tooltip: '保存（無効）',
    ariaLabel: '保存ボタン（無効）',
  },
};

export const SidebarToggle = {
  args: {
    icon: 'pi pi-bars',
    severity: 'secondary',
    size: 'sidebarToggle',
    shape: 'square',
    tooltip: 'サイドバー切り替え',
    ariaLabel: 'サイドバー切り替えボタン',
  },
};

export const AllShapes = {
  render: () => ({
    components: { AppIconButton },
    template: `
      <div style="display: flex; align-items: center; gap: 16px;">
        <div>
          <div style="margin-bottom: 8px; font-weight: bold;">Square</div>
          <AppIconButton icon="pi pi-home" severity="primary" shape="square" tooltip="ホーム"/>
        </div>
        <div>
          <div style="margin-bottom: 8px; font-weight: bold;">Rounded</div>
          <AppIconButton icon="pi pi-home" severity="primary" shape="rounded" tooltip="ホーム"/>
        </div>
        <div>
          <div style="margin-bottom: 8px; font-weight: bold;">Circle</div>
          <AppIconButton icon="pi pi-home" severity="primary" shape="circle" tooltip="ホーム"/>
        </div>
      </div>
    `,
  }),
};

export const AllSizes = {
  render: () => ({
    components: { AppIconButton },
    template: `
      <div style="display: flex; align-items: center; gap: 16px;">
        <div>
          <div style="margin-bottom: 8px; font-weight: bold;">Small</div>
          <AppIconButton icon="pi pi-star" severity="primary" size="small" tooltip="お気に入り"/>
        </div>
        <div>
          <div style="margin-bottom: 8px; font-weight: bold;">Medium</div>
          <AppIconButton icon="pi pi-star" severity="primary" size="medium" tooltip="お気に入り"/>
        </div>
        <div>
          <div style="margin-bottom: 8px; font-weight: bold;">Large</div>
          <AppIconButton icon="pi pi-star" severity="primary" size="large" tooltip="お気に入り"/>
        </div>
      </div>
    `,
  }),
};

export const AllSeverities = {
  render: () => ({
    components: { AppIconButton },
    template: `
      <div style="display: flex; flex-wrap: wrap; gap: 12px;">
        <AppIconButton icon="pi pi-check" severity="primary" tooltip="Primary"/>
        <AppIconButton icon="pi pi-check" severity="secondary" tooltip="Secondary"/>
        <AppIconButton icon="pi pi-check" severity="success" tooltip="Success"/>
        <AppIconButton icon="pi pi-check" severity="info" tooltip="Info"/>
        <AppIconButton icon="pi pi-check" severity="warn" tooltip="Warning"/>
        <AppIconButton icon="pi pi-check" severity="danger" tooltip="Danger"/>
        <AppIconButton icon="pi pi-check" severity="help" tooltip="Help"/>
        <AppIconButton icon="pi pi-check" severity="contrast" tooltip="Contrast"/>
      </div>
    `,
  }),
};