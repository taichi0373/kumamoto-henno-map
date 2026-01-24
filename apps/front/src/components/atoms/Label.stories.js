import Label from './Label.vue';

export default {
  title: 'Design System/Atoms/Label',
  component: Label,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    text: {
      control: 'text',
      description: 'ラベルのテキスト',
    },
    htmlFor: {
      control: 'text',
      description: 'ラベルに関連付けるinput要素のid',
    },
    required: {
      control: 'boolean',
      description: '必須項目かどうか',
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large'],
      description: 'ラベルのサイズ',
    },
    variant: {
      control: { type: 'select' },
      options: ['default', 'primary', 'error', 'success'],
      description: 'ラベルのバリアント',
    },
  },
};

const Template = (args) => ({
  components: { Label },
  setup() {
    return { args };
  },
  template: '<Label v-bind="args" />',
});

export const Default = Template.bind({});
Default.args = {
  text: 'ラベル',
  htmlFor: 'example-input',
  required: false,
  size: 'medium',
  variant: 'default',
};

export const Required = Template.bind({});
Required.args = {
  text: 'ユーザー名',
  htmlFor: 'username',
  required: true,
  size: 'medium',
  variant: 'default',
};

export const Small = Template.bind({});
Small.args = {
  text: '小さなラベル',
  htmlFor: 'small-input',
  required: false,
  size: 'small',
  variant: 'default',
};

export const Large = Template.bind({});
Large.args = {
  text: '大きなラベル',
  htmlFor: 'large-input',
  required: false,
  size: 'large',
  variant: 'default',
};

export const Primary = Template.bind({});
Primary.args = {
  text: 'プライマリラベル',
  htmlFor: 'primary-input',
  required: false,
  size: 'medium',
  variant: 'primary',
};

export const Error = Template.bind({});
Error.args = {
  text: 'エラーラベル',
  htmlFor: 'error-input',
  required: true,
  size: 'medium',
  variant: 'error',
};

export const Success = Template.bind({});
Success.args = {
  text: '成功ラベル',
  htmlFor: 'success-input',
  required: false,
  size: 'medium',
  variant: 'success',
};

export const AllVariants = {
  render: () => ({
    components: { Label },
    template: `
      <div style="display: flex; flex-direction: column; gap: 30px; width: 400px;">
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">基本的なラベル</h4>
          <div style="display: flex; flex-direction: column; gap: 12px;">
            <Label text="標準ラベル" htmlFor="standard" />
            <Label text="必須ラベル" htmlFor="required" :required="true" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">サイズバリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 12px;">
            <Label text="小サイズラベル" htmlFor="small" size="small" />
            <Label text="中サイズラベル" htmlFor="medium" size="medium" />
            <Label text="大サイズラベル" htmlFor="large" size="large" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">カラーバリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 12px;">
            <Label text="デフォルト" htmlFor="default" variant="default" />
            <Label text="プライマリ" htmlFor="primary" variant="primary" />
            <Label text="成功" htmlFor="success-label" variant="success" />
            <Label text="エラー" htmlFor="error-label" variant="error" :required="true" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">実用例</h4>
          <div style="display: flex; flex-direction: column; gap: 20px;">
            <div>
              <Label text="メールアドレス" htmlFor="email" :required="true" variant="default" />
              <input id="email" type="email" placeholder="example@email.com" style="margin-top: 8px; width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px;" />
            </div>
            
            <div>
              <Label text="パスワード" htmlFor="password" :required="true" variant="error" />
              <input id="password" type="password" placeholder="パスワード" style="margin-top: 8px; width: 100%; padding: 8px; border: 1px solid #dc3545; border-radius: 4px;" />
              <small style="color: #dc3545; font-size: 12px; margin-top: 4px; display: block;">パスワードが正しくありません</small>
            </div>
            
            <div>
              <Label text="ユーザー名" htmlFor="username" variant="success" />
              <input id="username" type="text" value="利用可能な名前" style="margin-top: 8px; width: 100%; padding: 8px; border: 1px solid #28a745; border-radius: 4px;" />
              <small style="color: #28a745; font-size: 12px; margin-top: 4px; display: block;">この名前は利用可能です</small>
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};
