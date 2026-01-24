import Checkbox from './Checkbox.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/Checkbox',
  component: Checkbox,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    id: {
      control: 'text',
      description: 'チェックボックスのid',
    },
    modelValue: {
      control: 'boolean',
      description: 'チェック状態',
    },
    label: {
      control: 'text',
      description: 'ラベルテキスト',
    },
    value: {
      control: 'text',
      description: 'チェックボックスの値',
    },
    disabled: {
      control: 'boolean',
      description: '無効状態',
    },
    required: {
      control: 'boolean',
      description: '必須項目',
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large'],
      description: 'サイズ',
    },
    variant: {
      control: { type: 'select' },
      options: ['primary', 'secondary', 'success', 'error'],
      description: 'カラーバリアント',
    },
  },
};

const Template = (args) => ({
  components: { Checkbox },
  setup() {
    const checked = ref(args.modelValue);
    return { args, checked };
  },
  template: `
    <Checkbox 
      v-bind="args" 
      v-model="checked"
      @change="(event) => console.log('Changed:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  id: 'default-checkbox',
  modelValue: false,
  label: 'チェックボックス',
  value: 'default',
  disabled: false,
  required: false,
  size: 'medium',
  variant: 'primary',
};

export const Checked = Template.bind({});
Checked.args = {
  id: 'checked-checkbox',
  modelValue: true,
  label: 'チェック済み',
  value: 'checked',
  disabled: false,
  required: false,
  size: 'medium',
  variant: 'primary',
};

export const Required = Template.bind({});
Required.args = {
  id: 'required-checkbox',
  modelValue: false,
  label: '利用規約に同意する',
  value: 'terms',
  disabled: false,
  required: true,
  size: 'medium',
  variant: 'primary',
};

export const Disabled = Template.bind({});
Disabled.args = {
  id: 'disabled-checkbox',
  modelValue: false,
  label: '無効なチェックボックス',
  value: 'disabled',
  disabled: true,
  required: false,
  size: 'medium',
  variant: 'primary',
};

export const Small = Template.bind({});
Small.args = {
  id: 'small-checkbox',
  modelValue: false,
  label: '小さなチェックボックス',
  value: 'small',
  disabled: false,
  required: false,
  size: 'small',
  variant: 'primary',
};

export const Large = Template.bind({});
Large.args = {
  id: 'large-checkbox',
  modelValue: false,
  label: '大きなチェックボックス',
  value: 'large',
  disabled: false,
  required: false,
  size: 'large',
  variant: 'primary',
};

export const Secondary = Template.bind({});
Secondary.args = {
  id: 'secondary-checkbox',
  modelValue: false,
  label: 'セカンダリカラー',
  value: 'secondary',
  disabled: false,
  required: false,
  size: 'medium',
  variant: 'secondary',
};

export const Success = Template.bind({});
Success.args = {
  id: 'success-checkbox',
  modelValue: true,
  label: '成功カラー',
  value: 'success',
  disabled: false,
  required: false,
  size: 'medium',
  variant: 'success',
};

export const Error = Template.bind({});
Error.args = {
  id: 'error-checkbox',
  modelValue: false,
  label: 'エラーカラー',
  value: 'error',
  disabled: false,
  required: false,
  size: 'medium',
  variant: 'error',
};

export const CheckboxGroup = {
  render: () => ({
    components: { Checkbox },
    data() {
      return {
        hobbies: ['reading', 'music'],
        terms: false,
      };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 20px; width: 400px;">
        <div>
          <h4 style="margin: 0 0 12px 0;">趣味を選択してください（複数選択可）</h4>
          <div style="display: flex; flex-direction: column; gap: 8px;">
            <Checkbox v-model="hobbies" value="reading" label="読書" />
            <Checkbox v-model="hobbies" value="music" label="音楽鑑賞" />
            <Checkbox v-model="hobbies" value="sports" label="スポーツ" />
            <Checkbox v-model="hobbies" value="travel" label="旅行" />
            <Checkbox v-model="hobbies" value="cooking" label="料理" />
          </div>
        </div>
        
        <div>
          <Checkbox 
            v-model="terms" 
            :required="true" 
            label="利用規約に同意します" 
            variant="primary" 
          />
        </div>
        
        <div style="padding: 15px; background-color: #f8f9fa; border-radius: 4px;">
          <h5 style="margin: 0 0 10px 0;">選択された値:</h5>
          <p style="margin: 0;"><strong>趣味:</strong> {{ hobbies.join(', ') || '未選択' }}</p>
          <p style="margin: 0;"><strong>利用規約:</strong> {{ terms ? '同意済み' : '未同意' }}</p>
        </div>
      </div>
    `,
  }),
};

export const AllVariants = {
  render: () => ({
    components: { Checkbox },
    data() {
      return {
        values: {
          primary: true,
          secondary: false,
          success: true,
          error: false,
          small: false,
          large: true,
          disabled: true,
          required: false,
        }
      };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 30px; width: 500px;">
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">カラーバリエーション</h4>
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px;">
            <Checkbox v-model="values.primary" variant="primary" label="プライマリ" />
            <Checkbox v-model="values.secondary" variant="secondary" label="セカンダリ" />
            <Checkbox v-model="values.success" variant="success" label="成功" />
            <Checkbox v-model="values.error" variant="error" label="エラー" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">サイズバリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 12px;">
            <Checkbox v-model="values.small" size="small" label="小サイズ" />
            <Checkbox modelValue="true" size="medium" label="中サイズ（デフォルト）" />
            <Checkbox v-model="values.large" size="large" label="大サイズ" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">状態バリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 12px;">
            <Checkbox v-model="values.required" :required="true" label="必須項目" />
            <Checkbox v-model="values.disabled" :disabled="true" label="無効状態" />
            <Checkbox modelValue="false" :disabled="true" label="無効状態（未チェック）" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">実用例</h4>
          <div style="display: flex; flex-direction: column; gap: 12px;">
            <Checkbox label="ニュースレターを受け取る" />
            <Checkbox label="プッシュ通知を許可する" />
            <Checkbox :required="true" label="プライバシーポリシーに同意する" />
            <Checkbox label="マーケティング情報の受信を許可する" />
          </div>
        </div>
      </div>
    `,
  }),
};
