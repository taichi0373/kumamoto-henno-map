import AppCard from './AppCard.vue';

export default {
  title: 'Design System/Atoms/AppCard',
  component: AppCard,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    title: {
      control: 'text',
      description: 'カードタイトル',
    },
    subtitle: {
      control: 'text',
      description: 'カードサブタイトル',
    },
    header: {
      control: 'text',
      description: 'カードヘッダー',
    },
    inputStyle: {
      control: 'object',
      description: 'カスタムスタイル',
    },
    inputClass: {
      control: 'text',
      description: 'カスタムクラス',
    },
  },
};

export const Default = {
  args: {
    title: 'カードタイトル',
    subtitle: 'カードのサブタイトル',
  },
  render: (args) => ({
    components: { AppCard },
    setup() {
      return { args };
    },
    template: `
      <AppCard v-bind="args" style="width: 400px;">
        <p>これはカードのメインコンテンツです。カード内に任意のコンテンツを配置できます。</p>
      </AppCard>
    `,
  }),
};

export const WithHeader = {
  args: {
    title: 'ヘッダー付きカード',
    subtitle: 'ヘッダーセクションがあります',
    header: 'カードのヘッダー',
  },
  render: (args) => ({
    components: { AppCard },
    setup() {
      return { args };
    },
    template: `
      <AppCard v-bind="args" style="width: 400px;">
        <p>ヘッダー付きのカードコンテンツです。</p>
        <p>複数の段落を含むことができます。</p>
      </AppCard>
    `,
  }),
};

export const OnlyTitle = {
  args: {
    title: 'タイトルのみ',
  },
  render: (args) => ({
    components: { AppCard },
    setup() {
      return { args };
    },
    template: `
      <AppCard v-bind="args" style="width: 400px;">
        <p>タイトルのみが設定されたカードです。</p>
      </AppCard>
    `,
  }),
};

export const WithSlots = {
  render: () => ({
    components: { AppCard },
    template: `
      <AppCard style="width: 400px;">
        <template #header>
          <div style="background: linear-gradient(45deg, #007bff, #0056b3); color: white; padding: 20px; text-align: center;">
            カスタムヘッダースロット
          </div>
        </template>
        <template #title>
          <h3 style="margin: 0; color: #007bff;">スロットを使用したタイトル</h3>
        </template>
        <template #subtitle>
          <em>カスタマイズされたサブタイトル</em>
        </template>
        <div>
          <p>メインコンテンツエリアです。</p>
          <p>スロット機能を活用した柔軟なレイアウトが可能です。</p>
        </div>
        <template #footer>
          <div style="display: flex; gap: 10px; justify-content: flex-end; padding: 10px 0;">
            <button style="padding: 8px 16px; border: 1px solid #007bff; background: white; color: #007bff; border-radius: 4px;">
              キャンセル
            </button>
            <button style="padding: 8px 16px; border: 1px solid #007bff; background: #007bff; color: white; border-radius: 4px;">
              保存
            </button>
          </div>
        </template>
      </AppCard>
    `,
  }),
};

export const PlainContent = {
  render: () => ({
    components: { AppCard },
    template: `
      <AppCard style="width: 400px;">
        <h3>プレーンなコンテンツ</h3>
        <p>タイトルやサブタイトルなしで、メインコンテンツのみを表示するカードです。</p>
        <ul>
          <li>リスト項目 1</li>
          <li>リスト項目 2</li>
          <li>リスト項目 3</li>
        </ul>
      </AppCard>
    `,
  }),
};