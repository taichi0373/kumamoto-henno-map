import Card from './Card.vue';

export default {
  title: 'Design System/Atoms/Card',
  component: Card,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    title: {
      control: 'text',
    },
    variant: {
      control: { type: 'select' },
      options: ['default', 'primary', 'secondary', 'success', 'warning', 'error'],
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large'],
    },
    hoverable: {
      control: 'boolean',
    },
    shadow: {
      control: { type: 'select' },
      options: ['none', 'small', 'medium', 'large'],
    },
  },
};

export const Default = {
  args: {
    title: 'カードタイトル',
  },
  render: (args) => ({
    components: { Card },
    setup() {
      return { args };
    },
    template: `
      <Card v-bind="args" style="width: 300px;">
        <p>これはカードの内容です。ここに任意のコンテンツを配置できます。</p>
        <p>複数の段落や他の要素も含めることができます。</p>
      </Card>
    `,
  }),
};

export const WithoutTitle = {
  args: {},
  render: (args) => ({
    components: { Card },
    setup() {
      return { args };
    },
    template: `
      <Card v-bind="args" style="width: 300px;">
        <h4 style="margin-top: 0;">カスタムヘッダー</h4>
        <p>タイトルプロパティを使わずに、カスタムコンテンツでカードを作成できます。</p>
      </Card>
    `,
  }),
};

export const WithFooter = {
  args: {
    title: 'フッター付きカード',
  },
  render: (args) => ({
    components: { Card },
    setup() {
      return { args };
    },
    template: `
      <Card v-bind="args" style="width: 300px;">
        <p>カードの本文コンテンツです。</p>
        <template #footer>
          <div style="display: flex; justify-content: space-between;">
            <button style="padding: 8px 16px; border: none; border-radius: 4px; background: #007bff; color: white;">確認</button>
            <button style="padding: 8px 16px; border: 1px solid #ccc; border-radius: 4px; background: white;">キャンセル</button>
          </div>
        </template>
      </Card>
    `,
  }),
};

export const Sizes = {
  render: () => ({
    components: { Card },
    template: `
      <div style="display: flex; gap: 20px; align-items: flex-start;">
        <Card size="small" title="小サイズ" style="width: 200px;">
          <p style="margin: 0;">小さいカードです。</p>
        </Card>
        <Card size="medium" title="中サイズ" style="width: 250px;">
          <p style="margin: 0;">中サイズのカードです。</p>
        </Card>
        <Card size="large" title="大サイズ" style="width: 300px;">
          <p style="margin: 0;">大きいカードです。</p>
        </Card>
      </div>
    `,
  }),
};

export const Variants = {
  render: () => ({
    components: { Card },
    template: `
      <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; width: 800px;">
        <Card variant="default" title="デフォルト">
          <p style="margin: 0;">デフォルトのカードです。</p>
        </Card>
        <Card variant="primary" title="プライマリ">
          <p style="margin: 0;">プライマリカードです。</p>
        </Card>
        <Card variant="secondary" title="セカンダリ">
          <p style="margin: 0;">セカンダリカードです。</p>
        </Card>
        <Card variant="success" title="成功">
          <p style="margin: 0;">成功を表すカードです。</p>
        </Card>
        <Card variant="warning" title="警告">
          <p style="margin: 0;">警告を表すカードです。</p>
        </Card>
        <Card variant="error" title="エラー">
          <p style="margin: 0;">エラーを表すカードです。</p>
        </Card>
      </div>
    `,
  }),
};

export const ShadowVariants = {
  render: () => ({
    components: { Card },
    template: `
      <div style="display: flex; gap: 30px; align-items: flex-start;">
        <Card shadow="none" title="影なし" style="width: 200px;">
          <p style="margin: 0;">影のないカードです。</p>
        </Card>
        <Card shadow="small" title="小さい影" style="width: 200px;">
          <p style="margin: 0;">小さい影のカードです。</p>
        </Card>
        <Card shadow="medium" title="中くらいの影" style="width: 200px;">
          <p style="margin: 0;">中くらいの影のカードです。</p>
        </Card>
        <Card shadow="large" title="大きい影" style="width: 200px;">
          <p style="margin: 0;">大きい影のカードです。</p>
        </Card>
      </div>
    `,
  }),
};

export const HoverEffect = {
  args: {
    title: 'ホバーエフェクト',
    hoverable: true,
  },
  render: (args) => ({
    components: { Card },
    setup() {
      return { args };
    },
    template: `
      <div style="display: flex; gap: 30px;">
        <Card v-bind="args" style="width: 250px;">
          <p style="margin: 0;">ホバーで浮き上がります</p>
        </Card>
        <Card title="ホバーなし" :hoverable="false" style="width: 250px;">
          <p style="margin: 0;">ホバーエフェクトがありません</p>
        </Card>
      </div>
    `,
  }),
};

export const CustomHeader = {
  render: () => ({
    components: { Card },
    template: `
      <Card style="width: 350px;">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <h3 style="margin: 0; color: white;">カスタムヘッダー</h3>
            <span style="color: rgba(255,255,255,0.8);">📊</span>
          </div>
        </template>
        <p>ヘッダースロットを使ってカスタムヘッダーを作成できます。</p>
        <p>アイコンやボタンなどの追加要素も含められます。</p>
        <template #footer>
          <small style="color: #666;">最終更新: 2025年8月10日</small>
        </template>
      </Card>
    `,
  }),
};
