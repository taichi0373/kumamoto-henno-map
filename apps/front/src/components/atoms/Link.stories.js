import Link from './Link.vue';

export default {
  title: 'Design System/Atoms/Link',
  component: Link,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    text: {
      control: 'text',
    },
    to: {
      control: 'text',
    },
    href: {
      control: 'text',
    },
    target: {
      control: { type: 'select' },
      options: ['_self', '_blank', '_parent', '_top'],
    },
    rel: {
      control: 'text',
    },
    icon: {
      control: 'text',
    },
    variant: {
      control: { type: 'select' },
      options: ['primary', 'secondary', 'success', 'warning', 'error', 'muted'],
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large'],
    },
    underline: {
      control: { type: 'select' },
      options: ['none', 'always', 'hover'],
    },
    disabled: {
      control: 'boolean',
    },
    onClick: { action: 'clicked' },
  },
};

export const Default = {
  args: {
    text: '基本的なリンク',
    href: '#',
  },
};

export const WithIcon = {
  args: {
    text: 'アイコン付きリンク',
    icon: 'fa-solid fa-external-link-alt',
    href: '#',
  },
};

export const ExternalLink = {
  args: {
    text: '外部サイトへのリンク',
    href: 'https://example.com',
    target: '_blank',
    rel: 'noopener noreferrer',
    icon: 'fa-solid fa-external-link-alt',
  },
};

export const InternalLink = {
  args: {
    text: '内部ページへのリンク',
    to: '/home',
    icon: 'fa-solid fa-home',
  },
};

export const Disabled = {
  args: {
    text: '無効化されたリンク',
    href: '#',
    disabled: true,
  },
};

export const Variants = {
  render: () => ({
    components: { Link },
    template: `
      <div style="display: flex; flex-direction: column; gap: 16px; align-items: flex-start;">
        <Link variant="primary" text="プライマリリンク" href="#" />
        <Link variant="secondary" text="セカンダリリンク" href="#" />
        <Link variant="success" text="成功リンク" href="#" />
        <Link variant="warning" text="警告リンク" href="#" />
        <Link variant="error" text="エラーリンク" href="#" />
        <Link variant="muted" text="控えめなリンク" href="#" />
      </div>
    `,
  }),
};

export const Sizes = {
  render: () => ({
    components: { Link },
    template: `
      <div style="display: flex; flex-direction: column; gap: 16px; align-items: flex-start;">
        <Link size="small" text="小さいリンク" href="#" />
        <Link size="medium" text="中サイズのリンク" href="#" />
        <Link size="large" text="大きいリンク" href="#" />
      </div>
    `,
  }),
};

export const UnderlineVariants = {
  render: () => ({
    components: { Link },
    template: `
      <div style="display: flex; flex-direction: column; gap: 16px; align-items: flex-start;">
        <Link underline="none" text="下線なしリンク" href="#" />
        <Link underline="always" text="常に下線ありリンク" href="#" />
        <Link underline="hover" text="ホバー時下線リンク" href="#" />
      </div>
    `,
  }),
};

export const WithIcons = {
  render: () => ({
    components: { Link },
    template: `
      <div style="display: flex; flex-direction: column; gap: 16px; align-items: flex-start;">
        <Link text="ホームページ" icon="fa-solid fa-home" href="#" />
        <Link text="設定" icon="fa-solid fa-cog" href="#" variant="secondary" />
        <Link text="ダウンロード" icon="fa-solid fa-download" href="#" variant="success" />
        <Link text="外部リンク" icon="fa-solid fa-external-link-alt" href="#" target="_blank" />
        <Link text="メール送信" icon="fa-solid fa-envelope" href="mailto:example@email.com" />
      </div>
    `,
  }),
};

export const NavigationExample = {
  render: () => ({
    components: { Link },
    template: `
      <nav style="padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
        <h4 style="margin-top: 0; margin-bottom: 16px;">ナビゲーション例</h4>
        <div style="display: flex; gap: 24px; flex-wrap: wrap;">
          <Link text="ホーム" to="/home" icon="fa-solid fa-home" />
          <Link text="プロフィール" to="/profile" icon="fa-solid fa-user" />
          <Link text="設定" to="/settings" icon="fa-solid fa-cog" variant="secondary" />
          <Link text="ヘルプ" href="/help" icon="fa-solid fa-question-circle" variant="muted" />
        </div>
      </nav>
    `,
  }),
};

export const AllVariants = {
  render: () => ({
    components: { Link },
    template: `
      <div style="display: flex; flex-direction: column; gap: 30px; width: 500px;">
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">カラーバリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 12px; align-items: flex-start;">
            <Link variant="primary" text="プライマリリンク" href="#" />
            <Link variant="secondary" text="セカンダリリンク" href="#" />
            <Link variant="success" text="成功リンク" href="#" />
            <Link variant="warning" text="警告リンク" href="#" />
            <Link variant="error" text="エラーリンク" href="#" />
            <Link variant="muted" text="控えめなリンク" href="#" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">サイズバリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 12px; align-items: flex-start;">
            <Link size="small" text="小さいリンク" href="#" />
            <Link size="medium" text="標準サイズリンク" href="#" />
            <Link size="large" text="大きいリンク" href="#" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">下線バリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 12px; align-items: flex-start;">
            <Link underline="none" text="下線なし" href="#" />
            <Link underline="always" text="常に下線" href="#" />
            <Link underline="hover" text="ホバー時下線" href="#" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">アイコン付きリンク</h4>
          <div style="display: flex; flex-direction: column; gap: 12px; align-items: flex-start;">
            <Link text="ダウンロード" icon="fa-solid fa-download" href="#" variant="success" />
            <Link text="外部サイト" icon="fa-solid fa-external-link-alt" href="#" target="_blank" />
            <Link text="メール送信" icon="fa-solid fa-envelope" href="mailto:example@email.com" />
            <Link text="無効化リンク" icon="fa-solid fa-ban" href="#" disabled />
          </div>
        </div>
      </div>
    `,
  }),
};
