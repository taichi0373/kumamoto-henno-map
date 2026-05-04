import AppLabel from './AppLabel.vue';

export default {
  title: 'Design System/Atoms/AppLabel',
  component: AppLabel,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    id: {
      control: 'text',
      description: 'リンクされる入力フィールドのID',
    },
    required: {
      control: 'boolean',
      description: '必須項目かどうか',
    },
  },
};

export const Default = {
  render: (args) => ({
    components: { AppLabel },
    setup() {
      return { args };
    },
    template: `<AppLabel v-bind="args">ラベルテキスト</AppLabel>`,
  }),
  args: {
    id: 'example-field',
    required: false,
  },
};

export const Required = {
  render: (args) => ({
    components: { AppLabel },
    setup() {
      return { args };
    },
    template: `<AppLabel v-bind="args">必須項目</AppLabel>`,
  }),
  args: {
    id: 'required-field',
    required: true,
  },
};

export const WithFormField = {
  render: () => ({
    components: { AppLabel },
    template: `
      <div style="width: 300px;">
        <AppLabel id="name-input" :required="true">お名前</AppLabel>
        <input
          id="name-input"
          type="text"
          placeholder="名前を入力してください"
          style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; margin-top: 4px;"
        >
      </div>
    `,
  }),
};

export const FormExample = {
  render: () => ({
    components: { AppLabel },
    template: `
      <form style="display: flex; flex-direction: column; gap: 16px; width: 400px; padding: 20px; border: 1px solid #ccc; border-radius: 8px;">
        <h3 style="margin-top: 0;">お問い合わせフォーム</h3>

        <div>
          <AppLabel id="form-name" :required="true">お名前</AppLabel>
          <input
            id="form-name"
            type="text"
            required
            style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; margin-top: 4px;"
          >
        </div>

        <div>
          <AppLabel id="form-email" :required="true">メールアドレス</AppLabel>
          <input
            id="form-email"
            type="email"
            required
            style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; margin-top: 4px;"
          >
        </div>

        <div>
          <AppLabel id="form-phone" :required="false">電話番号</AppLabel>
          <input
            id="form-phone"
            type="tel"
            style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; margin-top: 4px;"
          >
        </div>

        <div>
          <AppLabel id="form-message" :required="true">お問い合わせ内容</AppLabel>
          <textarea
            id="form-message"
            required
            style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; margin-top: 4px; min-height: 80px;"
          ></textarea>
        </div>

        <button type="submit" style="padding: 12px 24px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer;">
          送信
        </button>
      </form>
    `,
  }),
};
