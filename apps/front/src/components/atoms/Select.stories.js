import Select from './Select.vue';

export default {
  title: 'Design System/Atoms/Select',
  component: Select,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    id: {
      control: 'text',
    },
    modelValue: {
      control: 'text',
    },
    options: {
      control: 'object',
    },
    placeholder: {
      control: 'text',
    },
    disabled: {
      control: 'boolean',
    },
    required: {
      control: 'boolean',
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large'],
    },
    variant: {
      control: { type: 'select' },
      options: ['default', 'success', 'error'],
    },
    errorMessage: {
      control: 'text',
    },
    helpText: {
      control: 'text',
    },
    'onUpdate:modelValue': { action: 'modelValue updated' },
    onChange: { action: 'changed' },
    onFocus: { action: 'focused' },
    onBlur: { action: 'blurred' },
  },
};

const prefectureOptions = [
  { value: 'tokyo', text: '東京都' },
  { value: 'osaka', text: '大阪府' },
  { value: 'kyoto', text: '京都府' },
  { value: 'fukuoka', text: '福岡県' },
  { value: 'hokkaido', text: '北海道' },
];

const ageOptions = [
  { value: '18-29', text: '18-29歳' },
  { value: '30-39', text: '30-39歳' },
  { value: '40-49', text: '40-49歳' },
  { value: '50-59', text: '50-59歳' },
  { value: '60+', text: '60歳以上' },
];

export const Default = {
  args: {
    placeholder: 'オプションを選択してください',
    options: prefectureOptions,
  },
};

export const WithValue = {
  args: {
    modelValue: 'tokyo',
    options: prefectureOptions,
  },
};

export const WithPlaceholder = {
  args: {
    placeholder: '都道府県を選択',
    options: prefectureOptions,
  },
};

export const Disabled = {
  args: {
    placeholder: '選択できません',
    options: prefectureOptions,
    disabled: true,
  },
};

export const WithError = {
  args: {
    placeholder: 'エラー状態の選択',
    options: prefectureOptions,
    variant: 'error',
    errorMessage: '選択が必要です',
  },
};

export const WithSuccess = {
  args: {
    modelValue: 'tokyo',
    options: prefectureOptions,
    variant: 'success',
  },
};

export const WithHelp = {
  args: {
    placeholder: '年齢層を選択',
    options: ageOptions,
    helpText: 'あなたの年齢に最も近い範囲を選択してください',
  },
};

export const Required = {
  args: {
    placeholder: '必須項目です',
    options: prefectureOptions,
    required: true,
  },
};

export const Sizes = {
  render: () => ({
    components: { Select },
    data() {
      return {
        options: prefectureOptions,
      };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 20px; width: 300px;">
        <div>
          <h4 style="margin: 0 0 8px 0;">小サイズ</h4>
          <Select size="small" placeholder="小サイズ選択" :options="options" />
        </div>
        
        <div>
          <h4 style="margin: 0 0 8px 0;">中サイズ（デフォルト）</h4>
          <Select size="medium" placeholder="中サイズ選択" :options="options" />
        </div>
        
        <div>
          <h4 style="margin: 0 0 8px 0;">大サイズ</h4>
          <Select size="large" placeholder="大サイズ選択" :options="options" />
        </div>
      </div>
    `,
  }),
};

export const Variants = {
  render: () => ({
    components: { Select },
    data() {
      return {
        options: prefectureOptions,
      };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 20px; width: 300px;">
        <div>
          <h4 style="margin: 0 0 8px 0;">デフォルト</h4>
          <Select variant="default" placeholder="デフォルト選択" :options="options" />
        </div>
        
        <div>
          <h4 style="margin: 0 0 8px 0;">成功状態</h4>
          <Select variant="success" modelValue="tokyo" :options="options" />
        </div>
        
        <div>
          <h4 style="margin: 0 0 8px 0;">エラー状態</h4>
          <Select variant="error" placeholder="エラー選択" :options="options" error-message="選択してください" />
        </div>
      </div>
    `,
  }),
};

export const DifferentOptions = {
  render: () => ({
    components: { Select },
    data() {
      return {
        prefectures: prefectureOptions,
        ages: ageOptions,
        languages: [
          { value: 'ja', text: '日本語' },
          { value: 'en', text: 'English' },
          { value: 'ko', text: '한국어' },
          { value: 'zh', text: '中文' },
        ],
        priorities: [
          { value: 'low', text: '低' },
          { value: 'medium', text: '中' },
          { value: 'high', text: '高' },
          { value: 'urgent', text: '緊急' },
        ],
      };
    },
    template: `
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px; width: 600px;">
        <div>
          <h4 style="margin: 0 0 8px 0;">都道府県</h4>
          <Select placeholder="都道府県を選択" :options="prefectures" />
        </div>
        
        <div>
          <h4 style="margin: 0 0 8px 0;">年齢層</h4>
          <Select placeholder="年齢層を選択" :options="ages" />
        </div>
        
        <div>
          <h4 style="margin: 0 0 8px 0;">言語設定</h4>
          <Select modelValue="ja" :options="languages" />
        </div>
        
        <div>
          <h4 style="margin: 0 0 8px 0;">優先度</h4>
          <Select placeholder="優先度を選択" :options="priorities" />
        </div>
      </div>
    `,
  }),
};

export const InteractiveExample = {
  render: () => ({
    components: { Select },
    data() {
      return {
        selectedPrefecture: '',
        selectedAge: '',
        options: {
          prefectures: prefectureOptions,
          ages: ageOptions,
        }
      };
    },
    template: `
      <div style="width: 400px;">
        <h3 style="margin-top: 0;">ユーザー情報入力</h3>
        
        <div style="margin-bottom: 20px;">
          <label style="display: block; margin-bottom: 8px; font-weight: 500;">居住地域</label>
          <Select 
            v-model="selectedPrefecture"
            placeholder="都道府県を選択してください"
            :options="options.prefectures"
            help-text="あなたがお住まいの都道府県を選択してください"
          />
        </div>
        
        <div style="margin-bottom: 20px;">
          <label style="display: block; margin-bottom: 8px; font-weight: 500;">年齢層</label>
          <Select 
            v-model="selectedAge"
            placeholder="年齢層を選択してください"
            :options="options.ages"
            help-text="該当する年齢層を選択してください"
          />
        </div>
        
        <div style="padding: 15px; background-color: #f8f9fa; border-radius: 4px;">
          <h4 style="margin: 0 0 10px 0;">選択された値:</h4>
          <p style="margin: 0;"><strong>都道府県:</strong> {{ selectedPrefecture || '未選択' }}</p>
          <p style="margin: 0;"><strong>年齢層:</strong> {{ selectedAge || '未選択' }}</p>
        </div>
      </div>
    `,
  }),
};

export const AllVariants = {
  render: () => ({
    components: { Select },
    data() {
      return {
        options: prefectureOptions,
        values: {
          default: '',
          small: 'tokyo',
          large: '',
          success: 'osaka',
          error: '',
          disabled: 'kyoto',
        }
      };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 30px; width: 500px;">
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">基本的な使用例</h4>
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
            <div>
              <label style="display: block; margin-bottom: 8px; font-weight: 500;">基本選択</label>
              <Select 
                v-model="values.default"
                placeholder="選択してください"
                :options="options"
              />
            </div>
            <div>
              <label style="display: block; margin-bottom: 8px; font-weight: 500;">値あり</label>
              <Select 
                modelValue="tokyo"
                :options="options"
              />
            </div>
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">サイズバリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 15px;">
            <div>
              <label style="display: block; margin-bottom: 8px; font-weight: 500; font-size: 12px;">小サイズ</label>
              <Select 
                size="small"
                v-model="values.small"
                :options="options"
              />
            </div>
            <div>
              <label style="display: block; margin-bottom: 8px; font-weight: 500;">中サイズ</label>
              <Select 
                size="medium"
                placeholder="中サイズ選択"
                :options="options"
              />
            </div>
            <div>
              <label style="display: block; margin-bottom: 8px; font-weight: 500; font-size: 18px;">大サイズ</label>
              <Select 
                size="large"
                v-model="values.large"
                placeholder="大サイズ選択"
                :options="options"
              />
            </div>
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">状態バリエーション</h4>
          <div style="display: flex; flex-direction: column; gap: 15px;">
            <div>
              <label style="display: block; margin-bottom: 8px; font-weight: 500;">成功状態</label>
              <Select 
                variant="success"
                v-model="values.success"
                :options="options"
              />
            </div>
            <div>
              <label style="display: block; margin-bottom: 8px; font-weight: 500;">エラー状態</label>
              <Select 
                variant="error"
                v-model="values.error"
                placeholder="エラー選択"
                :options="options"
                error-message="この項目は必須です"
              />
            </div>
            <div>
              <label style="display: block; margin-bottom: 8px; font-weight: 500;">無効状態</label>
              <Select 
                v-model="values.disabled"
                :options="options"
                :disabled="true"
              />
            </div>
          </div>
        </div>
        
        <div>
          <h4 style="margin-bottom: 16px; color: #333;">ヘルプテキスト付き</h4>
          <div>
            <label style="display: block; margin-bottom: 8px; font-weight: 500;">居住地域</label>
            <Select 
              placeholder="都道府県を選択"
              :options="options"
              help-text="現在お住まいの都道府県を選択してください"
            />
          </div>
        </div>
      </div>
    `,
  }),
};
