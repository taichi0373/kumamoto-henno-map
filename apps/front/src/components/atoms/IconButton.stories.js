import IconButton from './IconButton.vue';

export default {
  title: 'Design System/Atoms/IconButton',
  component: IconButton,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    type: {
      control: { type: 'select' },
      options: ['button', 'submit', 'reset'],
    },
    icon: {
      control: 'text',
    },
    label: {
      control: 'text',
    },
    variant: {
      control: { type: 'select' },
      options: ['primary', 'secondary', 'success', 'warning', 'error', 'info', 'transparent'],
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large'],
    },
    shape: {
      control: { type: 'select' },
      options: ['rounded', 'circle', 'square'],
    },
    disabled: {
      control: 'boolean',
    },
    tooltip: {
      control: 'text',
    },
    onClick: { action: 'clicked' },
  },
};

export const Primary = {
  args: {
    icon: 'fa-solid fa-heart',
    variant: 'primary',
    size: 'medium',
    shape: 'rounded',
    tooltip: 'お気に入りに追加',
  },
};

export const WithLabel = {
  args: {
    icon: 'fa-solid fa-download',
    label: 'ダウンロード',
    variant: 'primary',
    size: 'medium',
    shape: 'rounded',
  },
};

export const Sizes = {
  render: () => ({
    components: { IconButton },
    template: `
      <div style="display: flex; gap: 16px; align-items: center;">
        <IconButton icon="fa-solid fa-star" size="small" variant="primary" tooltip="小サイズ" />
        <IconButton icon="fa-solid fa-star" size="medium" variant="primary" tooltip="中サイズ" />
        <IconButton icon="fa-solid fa-star" size="large" variant="primary" tooltip="大サイズ" />
      </div>
    `,
  }),
};

export const Shapes = {
  render: () => ({
    components: { IconButton },
    template: `
      <div style="display: flex; gap: 16px; align-items: center;">
        <IconButton icon="fa-solid fa-heart" shape="rounded" variant="primary" tooltip="角丸" />
        <IconButton icon="fa-solid fa-heart" shape="circle" variant="primary" tooltip="円形" />
        <IconButton icon="fa-solid fa-heart" shape="square" variant="primary" tooltip="四角" />
      </div>
    `,
  }),
};

export const Variants = {
  render: () => ({
    components: { IconButton },
    template: `
      <div style="display: flex; gap: 16px; align-items: center; flex-wrap: wrap;">
        <IconButton icon="fa-solid fa-check" variant="primary" tooltip="プライマリ" />
        <IconButton icon="fa-solid fa-info" variant="secondary" tooltip="セカンダリ" />
        <IconButton icon="fa-solid fa-check-circle" variant="success" tooltip="成功" />
        <IconButton icon="fa-solid fa-exclamation-triangle" variant="warning" tooltip="警告" />
        <IconButton icon="fa-solid fa-times-circle" variant="error" tooltip="エラー" />
        <IconButton icon="fa-solid fa-info-circle" variant="info" tooltip="情報" />
        <IconButton icon="fa-solid fa-cog" variant="transparent" tooltip="透明" />
      </div>
    `,
  }),
};

export const States = {
  render: () => ({
    components: { IconButton },
    template: `
      <div style="display: flex; gap: 16px; align-items: center;">
        <IconButton icon="fa-solid fa-play" variant="primary" tooltip="通常状態" />
        <IconButton icon="fa-solid fa-play" variant="primary" :disabled="true" tooltip="無効状態" />
      </div>
    `,
  }),
};

export const AllVariants = {
  render: () => ({
    components: { IconButton },
    template: `
      <div style="display: flex; flex-direction: column; gap: 20px; width: 600px;">
        <h3 style="margin: 0; color: #333;">サイズバリエーション</h3>
        <div style="display: flex; gap: 16px; align-items: center;">
          <IconButton icon="fa-solid fa-star" size="small" variant="primary" />
          <IconButton icon="fa-solid fa-star" size="medium" variant="primary" />
          <IconButton icon="fa-solid fa-star" size="large" variant="primary" />
        </div>
        
        <h3 style="margin: 0; color: #333;">形状バリエーション</h3>
        <div style="display: flex; gap: 16px; align-items: center;">
          <IconButton icon="fa-solid fa-heart" shape="rounded" variant="primary" />
          <IconButton icon="fa-solid fa-heart" shape="circle" variant="primary" />
          <IconButton icon="fa-solid fa-heart" shape="square" variant="primary" />
        </div>
        
        <h3 style="margin: 0; color: #333;">カラーバリエーション</h3>
        <div style="display: flex; gap: 16px; align-items: center; flex-wrap: wrap;">
          <IconButton icon="fa-solid fa-check" variant="primary" />
          <IconButton icon="fa-solid fa-info" variant="secondary" />
          <IconButton icon="fa-solid fa-check-circle" variant="success" />
          <IconButton icon="fa-solid fa-exclamation-triangle" variant="warning" />
          <IconButton icon="fa-solid fa-times-circle" variant="error" />
          <IconButton icon="fa-solid fa-info-circle" variant="info" />
        </div>
        
        <h3 style="margin: 0; color: #333;">ラベル付き</h3>
        <div style="display: flex; gap: 16px; align-items: center; flex-wrap: wrap;">
          <IconButton icon="fa-solid fa-download" label="ダウンロード" variant="primary" />
          <IconButton icon="fa-solid fa-upload" label="アップロード" variant="success" />
          <IconButton icon="fa-solid fa-trash" label="削除" variant="error" />
        </div>
      </div>
    `,
  }),
};
