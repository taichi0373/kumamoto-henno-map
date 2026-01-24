import Radio from './Radio.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/Radio',
  component: Radio,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    id: {
      control: 'text',
      description: 'ラジオボタンのid',
    },
    modelValue: {
      control: 'text',
      description: '選択された値',
    },
    label: {
      control: 'text',
      description: 'ラベルテキスト',
    },
    value: {
      control: 'text',
      description: 'ラジオボタンの値',
    },
    name: {
      control: 'text',
      description: 'グループ名',
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
  components: { Radio },
  setup() {
    const selected = ref(args.modelValue);
    return { args, selected };
  },
  template: `
    <Radio 
      v-bind="args" 
      v-model="selected"
      @change="(event) => console.log('Changed:', event)"
    />
  `,
});

const GroupTemplate = (args) => ({
  components: { Radio },
  setup() {
    const selected = ref('option1');
    return { args, selected };
  },
  template: `
    <div style="display: flex; flex-direction: column; gap: 12px;">
      <Radio 
        id="option1"
        v-model="selected"
        label="オプション 1"
        value="option1"
        name="radio-group"
        :size="args.size"
        :variant="args.variant"
      />
      <Radio 
        id="option2"
        v-model="selected"
        label="オプション 2"
        value="option2"
        name="radio-group"
        :size="args.size"
        :variant="args.variant"
      />
      <Radio 
        id="option3"
        v-model="selected"
        label="オプション 3"
        value="option3"
        name="radio-group"
        :size="args.size"
        :variant="args.variant"
      />
    </div>
  `,
});

export const Default = Template.bind({});
Default.args = {
  id: 'default-radio',
  modelValue: '',
  label: 'ラジオボタン',
  value: 'default',
  name: 'default-group',
  disabled: false,
  required: false,
  size: 'medium',
  variant: 'primary',
};

export const Selected = Template.bind({});
Selected.args = {
  id: 'selected-radio',
  modelValue: 'selected',
  label: '選択済み',
  value: 'selected',
  name: 'selected-group',
  disabled: false,
  required: false,
  size: 'medium',
  variant: 'primary',
};

export const Required = Template.bind({});
Required.args = {
  id: 'required-radio',
  modelValue: '',
  label: '性別を選択してください',
  value: 'male',
  name: 'gender',
  disabled: false,
  required: true,
  size: 'medium',
  variant: 'primary',
};

export const Disabled = Template.bind({});
Disabled.args = {
  id: 'disabled-radio',
  modelValue: '',
  label: '無効なラジオボタン',
  value: 'disabled',
  name: 'disabled-group',
  disabled: true,
  required: false,
  size: 'medium',
  variant: 'primary',
};

export const Small = Template.bind({});
Small.args = {
  id: 'small-radio',
  modelValue: '',
  label: '小さなラジオボタン',
  value: 'small',
  name: 'small-group',
  disabled: false,
  required: false,
  size: 'small',
  variant: 'primary',
};

export const Large = Template.bind({});
Large.args = {
  id: 'large-radio',
  modelValue: '',
  label: '大きなラジオボタン',
  value: 'large',
  name: 'large-group',
  disabled: false,
  required: false,
  size: 'large',
  variant: 'primary',
};

export const Secondary = Template.bind({});
Secondary.args = {
  id: 'secondary-radio',
  modelValue: 'secondary',
  label: 'セカンダリカラー',
  value: 'secondary',
  name: 'secondary-group',
  disabled: false,
  required: false,
  size: 'medium',
  variant: 'secondary',
};

export const Success = Template.bind({});
Success.args = {
  id: 'success-radio',
  modelValue: 'success',
  label: '成功カラー',
  value: 'success',
  name: 'success-group',
  disabled: false,
  required: false,
  size: 'medium',
  variant: 'success',
};

export const Error = Template.bind({});
Error.args = {
  id: 'error-radio',
  modelValue: 'error',
  label: 'エラーカラー',
  value: 'error',
  name: 'error-group',
  disabled: false,
  required: false,
  size: 'medium',
  variant: 'error',
};

export const RadioGroup = GroupTemplate.bind({});
RadioGroup.args = {
  size: 'medium',
  variant: 'primary',
};

export const CompleteExample = {
  render: () => ({
    components: { Radio },
    data() {
      return {
        gender: '',
        age: '',
        plan: 'basic',
      };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 30px; width: 500px;">
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">性別を選択してください</h4>
          <div style="display: flex; flex-direction: column; gap: 8px;">
            <Radio v-model="gender" value="male" name="gender" label="男性" />
            <Radio v-model="gender" value="female" name="gender" label="女性" />
            <Radio v-model="gender" value="other" name="gender" label="その他" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">年齢層を選択してください</h4>
          <div style="display: flex; flex-direction: column; gap: 8px;">
            <Radio v-model="age" value="under20" name="age" label="20歳未満" size="small" />
            <Radio v-model="age" value="20-39" name="age" label="20-39歳" />
            <Radio v-model="age" value="40-59" name="age" label="40-59歳" />
            <Radio v-model="age" value="over60" name="age" label="60歳以上" size="large" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">料金プランを選択してください</h4>
          <div style="display: flex; flex-direction: column; gap: 8px;">
            <Radio v-model="plan" value="basic" name="plan" label="ベーシックプラン（無料）" variant="secondary" />
            <Radio v-model="plan" value="standard" name="plan" label="スタンダードプラン（月額500円）" variant="primary" />
            <Radio v-model="plan" value="premium" name="plan" label="プレミアムプラン（月額1000円）" variant="success" />
          </div>
        </div>
        
        <div style="padding: 15px; background-color: #f8f9fa; border-radius: 4px;">
          <h5 style="margin: 0 0 10px 0;">選択された値:</h5>
          <p style="margin: 0;"><strong>性別:</strong> {{ gender || '未選択' }}</p>
          <p style="margin: 0;"><strong>年齢層:</strong> {{ age || '未選択' }}</p>
          <p style="margin: 0;"><strong>プラン:</strong> {{ plan }}</p>
        </div>
      </div>
    `,
  }),
};

export const AllVariants = {
  render: () => ({
    components: { Radio },
    data() {
      return {
        selectedColor: 'primary',
        selectedSize: 'medium',
        selectedState: 'normal',
      };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 30px; width: 500px;">
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">カラーバリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 8px;">
            <Radio v-model="selectedColor" value="primary" name="color" label="プライマリ" variant="primary" />
            <Radio v-model="selectedColor" value="secondary" name="color" label="セカンダリ" variant="secondary" />
            <Radio v-model="selectedColor" value="success" name="color" label="成功" variant="success" />
            <Radio v-model="selectedColor" value="error" name="color" label="エラー" variant="error" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">サイズバリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 8px;">
            <Radio v-model="selectedSize" value="small" name="size" label="小サイズ" size="small" />
            <Radio v-model="selectedSize" value="medium" name="size" label="中サイズ" size="medium" />
            <Radio v-model="selectedSize" value="large" name="size" label="大サイズ" size="large" />
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">状態バリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 8px;">
            <Radio v-model="selectedState" value="normal" name="state" label="通常状態" />
            <Radio v-model="selectedState" value="required" name="state" label="必須項目" :required="true" />
            <Radio modelValue="disabled" name="disabled-group" label="無効状態" :disabled="true" />
            <Radio modelValue="disabled-selected" name="disabled-selected-group" label="無効状態（選択済み）" :disabled="true" />
          </div>
        </div>
      </div>
    `,
  }),
};
