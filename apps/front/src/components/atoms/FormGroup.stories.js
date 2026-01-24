import FormGroup from './FormGroup.vue';
import Input from './Input.vue';
import Label from './Label.vue';
import Button from './Button.vue';

export default {
  title: 'Design System/Atoms/FormGroup',
  component: FormGroup,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large'],
    },
    variant: {
      control: { type: 'select' },
      options: ['default', 'inline', 'floating'],
    },
    required: {
      control: 'boolean',
    },
    disabled: {
      control: 'boolean',
    },
  },
};

export const Default = {
  args: {
    size: 'medium',
    variant: 'default',
  },
  render: (args) => ({
    components: { FormGroup, Input, Label },
    setup() {
      return { args };
    },
    template: `
      <FormGroup v-bind="args" style="width: 300px;">
        <Label>メールアドレス</Label>
        <Input type="email" placeholder="example@email.com" />
      </FormGroup>
    `,
  }),
};

export const WithRequiredField = {
  args: {
    required: true,
  },
  render: (args) => ({
    components: { FormGroup, Input, Label },
    setup() {
      return { args };
    },
    template: `
      <FormGroup v-bind="args" style="width: 300px;">
        <Label required>パスワード</Label>
        <Input type="password" placeholder="パスワードを入力してください" />
      </FormGroup>
    `,
  }),
};

export const DisabledState = {
  args: {
    disabled: true,
  },
  render: (args) => ({
    components: { FormGroup, Input, Label },
    setup() {
      return { args };
    },
    template: `
      <FormGroup v-bind="args" style="width: 300px;">
        <Label>無効化されたフィールド</Label>
        <Input placeholder="編集できません" disabled />
      </FormGroup>
    `,
  }),
};

export const InlineLayout = {
  args: {
    variant: 'inline',
  },
  render: (args) => ({
    components: { FormGroup, Input, Label },
    setup() {
      return { args };
    },
    template: `
      <FormGroup v-bind="args" style="width: 400px;">
        <Label>ユーザー名:</Label>
        <Input placeholder="ユーザー名を入力" />
      </FormGroup>
    `,
  }),
};

export const FloatingLayout = {
  args: {
    variant: 'floating',
  },
  render: (args) => ({
    components: { FormGroup, Input, Label },
    setup() {
      return { args };
    },
    template: `
      <FormGroup v-bind="args" style="width: 300px;">
        <Input placeholder=" " />
        <Label>フローティングラベル</Label>
      </FormGroup>
    `,
  }),
};

export const Sizes = {
  render: () => ({
    components: { FormGroup, Input, Label },
    template: `
      <div style="display: flex; flex-direction: column; gap: 0; width: 400px;">
        <FormGroup size="small">
          <Label>小サイズ</Label>
          <Input size="small" placeholder="小さいフィールド" />
        </FormGroup>
        
        <FormGroup size="medium">
          <Label>中サイズ（デフォルト）</Label>
          <Input placeholder="標準的なフィールド" />
        </FormGroup>
        
        <FormGroup size="large">
          <Label>大サイズ</Label>
          <Input size="large" placeholder="大きいフィールド" />
        </FormGroup>
      </div>
    `,
  }),
};

export const CompleteForm = {
  render: () => ({
    components: { FormGroup, Input, Label, Button },
    template: `
      <div style="width: 400px; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
        <h3 style="margin-top: 0;">ユーザー登録フォーム</h3>
        
        <FormGroup>
          <Label required>氏名</Label>
          <Input placeholder="山田 太郎" />
        </FormGroup>
        
        <FormGroup>
          <Label required>メールアドレス</Label>
          <Input type="email" placeholder="example@email.com" />
        </FormGroup>
        
        <FormGroup variant="inline">
          <Label>年齢:</Label>
          <Input type="number" placeholder="25" style="width: 100px;" />
        </FormGroup>
        
        <FormGroup>
          <Label>備考</Label>
          <Input placeholder="任意のメッセージ" />
        </FormGroup>
        
        <FormGroup>
          <Button :primary="true" label="登録する" />
        </FormGroup>
      </div>
    `,
  }),
};

export const AllVariants = {
  render: () => ({
    components: { FormGroup, Input, Label, Button },
    template: `
      <div style="display: flex; flex-direction: column; gap: 40px; width: 500px;">
        <div>
          <h4 style="margin-bottom: 20px; color: #333;">レイアウトバリエーション</h4>
          
          <FormGroup variant="default">
            <Label>デフォルトレイアウト</Label>
            <Input placeholder="縦に配置されます" />
          </FormGroup>
          
          <FormGroup variant="inline">
            <Label>インラインレイアウト:</Label>
            <Input placeholder="横に並びます" />
          </FormGroup>
          
          <FormGroup variant="floating">
            <Input placeholder=" " />
            <Label>フローティングラベル</Label>
          </FormGroup>
        </div>
        
        <div>
          <h4 style="margin-bottom: 20px; color: #333;">サイズバリエーション</h4>
          
          <FormGroup size="small">
            <Label>小サイズ</Label>
            <Input size="small" placeholder="小さなマージン" />
          </FormGroup>
          
          <FormGroup size="medium">
            <Label>中サイズ</Label>
            <Input placeholder="標準マージン" />
          </FormGroup>
          
          <FormGroup size="large">
            <Label>大サイズ</Label>
            <Input size="large" placeholder="大きなマージン" />
          </FormGroup>
        </div>
        
        <div>
          <h4 style="margin-bottom: 20px; color: #333;">状態バリエーション</h4>
          
          <FormGroup required>
            <Label required>必須フィールド</Label>
            <Input placeholder="入力が必要です" />
          </FormGroup>
          
          <FormGroup disabled>
            <Label>無効化フィールド</Label>
            <Input placeholder="編集不可" disabled />
          </FormGroup>
        </div>
      </div>
    `,
  }),
};
