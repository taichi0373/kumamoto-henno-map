import AppMultiSelect from './AppMultiSelect.vue';
import { ref } from 'vue';

const options = [
  { label: '高齢者向け', value: 'senior' },
  { label: 'バス割引', value: 'bus' },
  { label: '買い物', value: 'shopping' },
  { label: '医療', value: 'medical' },
  { label: 'レジャー', value: 'leisure' },
];

// 熊本県市町村データ（フィルタ機能のデモ用）
const municipalityOptions = [
  { label: '熊本市', value: '431001' },
  { label: '八代市', value: '432024' },
  { label: '人吉市', value: '432032' },
  { label: '荒尾市', value: '432041' },
  { label: '水俣市', value: '432059' },
  { label: '玉名市', value: '432067' },
  { label: '山鹿市', value: '432083' },
  { label: '菊池市', value: '432105' },
  { label: '宇土市', value: '432113' },
  { label: '上天草市', value: '432121' },
  { label: '宇城市', value: '432130' },
  { label: '阿蘇市', value: '432148' },
  { label: '天草市', value: '432156' },
  { label: '合志市', value: '432164' },
  { label: '美里町', value: '433489' },
  { label: '玉東町', value: '433641' },
  { label: '南関町', value: '433675' },
  { label: '長洲町', value: '433683' },
  { label: '和水町', value: '433691' },
  { label: '大津町', value: '434035' },
  { label: '菊陽町', value: '434043' },
  { label: '南小国町', value: '434230' },
  { label: '小国町', value: '434248' },
  { label: '産山村', value: '434256' },
  { label: '高森町', value: '434281' },
  { label: '西原村', value: '434329' },
  { label: '南阿蘇村', value: '434337' },
  { label: '御船町', value: '434418' },
  { label: '嘉島町', value: '434426' },
  { label: '益城町', value: '434434' },
  { label: '甲佐町', value: '434442' },
  { label: '山都町', value: '434477' },
  { label: '氷川町', value: '434680' },
  { label: '芦北町', value: '434825' },
  { label: '津奈木町', value: '434841' },
  { label: '錦町', value: '435015' },
  { label: '多良木町', value: '435058' },
  { label: '湯前町', value: '435066' },
  { label: '水上村', value: '435074' },
  { label: '相良村', value: '435104' },
  { label: '五木村', value: '435112' },
  { label: '山江村', value: '435121' },
  { label: '球磨村', value: '435139' },
  { label: 'あさぎり町', value: '435147' },
  { label: '苓北町', value: '435317' }
];

export default {
  title: 'Design System/Atoms/AppMultiSelect',
  component: AppMultiSelect,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    inputId: {
      control: 'text',
      description: '入力要素のid',
    },
    modelValue: {
      control: 'object',
      description: '選択値',
    },
    options: {
      control: 'object',
      description: '選択肢',
    },
    optionLabel: {
      control: 'text',
      description: '表示ラベルキー',
    },
    optionValue: {
      control: 'text',
      description: '値キー',
    },
    placeholder: {
      control: 'text',
      description: 'プレースホルダー',
    },
    maxSelectedLabels: {
      control: { type: 'number', min: 1, max: 10, step: 1 },
      description: '表示ラベル数',
    },
    filter: {
      control: 'boolean',
      description: 'フィルタ表示',
    },
    disabled: {
      control: 'boolean',
      description: '無効状態',
    },
    readonly: {
      control: 'boolean',
      description: '読み取り専用',
    },
    showError: {
      control: 'boolean',
      description: 'エラー表示フラグ',
    },
    inputClass: {
      control: 'text',
      description: 'インプットのクラス',
    },
    inputStyle: {
      control: 'object',
      description: 'インプットのスタイル',
    },
    tabindex: {
      control: { type: 'number', min: -1, max: 20, step: 1 },
      description: 'タブインデックス',
    },
  },
};

const Template = (args) => ({
  components: { AppMultiSelect },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <AppMultiSelect
      v-bind="args"
      v-model="value"
      style="width: 360px"
      @focus="(event) => console.log('Focused:', event)"
      @blur="(event) => console.log('Blurred:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  inputId: 'default-multi-select',
  modelValue: [],
  placeholder: 'カテゴリを選択してください',
  options,
  optionLabel: 'label',
  optionValue: 'value',
  maxSelectedLabels: 3,
  filter: false,
  disabled: false,
  readonly: false,
  showError: true,
  inputClass: '',
  inputStyle: '',
  tabindex: 0,
};

export const WithValue = Template.bind({});
WithValue.args = {
  ...Default.args,
  inputId: 'value-multi-select',
  modelValue: ['senior', 'bus'],
};

export const Disabled = Template.bind({});
Disabled.args = {
  ...Default.args,
  inputId: 'disabled-multi-select',
  disabled: true,
};

export const WithError = Template.bind({});
WithError.args = {
  ...Default.args,
  inputId: 'error-multi-select',
  error: [{ type: 1, message: '1つ以上選択してください' }],
};

export const WithWarning = Template.bind({});
WithWarning.args = {
  ...Default.args,
  inputId: 'warning-multi-select',
  error: [{ type: 2, message: '3件まで選択できます' }],
};

export const Filterable = Template.bind({});
Filterable.args = {
  ...Default.args,
  inputId: 'filter-multi-select',
  filter: true,
};

// フィルタ機能を使った実践的な例（熊本県市町村選択）
export const FilterWithManyOptions = Template.bind({});
FilterWithManyOptions.args = {
  inputId: 'municipality-multi-select',
  modelValue: [],
  placeholder: '市町村を検索・選択してください',
  options: municipalityOptions,
  optionLabel: 'label',
  optionValue: 'value',
  maxSelectedLabels: 3,
  filter: true,
  disabled: false,
  readonly: false,
  showError: true,
  inputClass: '',
  inputStyle: '',
  tabindex: 0,
};
FilterWithManyOptions.parameters = {
  docs: {
    description: {
      story: 'フィルタ機能を有効にした市町村選択の例です。選択肢が多い場合にフィルタ機能が有用であることを示しています。',
    },
  },
};

// フィルタ機能付きで初期選択値がある例
export const FilterWithPreselected = Template.bind({});
FilterWithPreselected.args = {
  ...FilterWithManyOptions.args,
  inputId: 'preselected-municipality-multi-select',
  modelValue: ['431001', '432024', '432148'], // 熊本市、八代市、阿蘇市
};
FilterWithPreselected.parameters = {
  docs: {
    description: {
      story: '初期値が設定された状態でフィルタ機能を使う例です。既に選択されている項目を確認して、追加選択や変更を行えます。',
    },
  },
};

// カテゴリ検索のフィルタ例
const categoryOptions = [
  { label: '高齢者向け割引', value: 'senior_discount' },
  { label: '運転免許返納者特典', value: 'license_return' },
  { label: 'バス・電車割引', value: 'public_transport' },
  { label: 'タクシー割引', value: 'taxi_discount' },
  { label: '買い物・食事割引', value: 'shopping_dining' },
  { label: '医療・薬局', value: 'medical_pharmacy' },
  { label: 'レジャー・娯楽', value: 'leisure_entertainment' },
  { label: '宿泊・観光', value: 'accommodation_tourism' },
  { label: '美容・理容', value: 'beauty_barber' },
  { label: 'スポーツ・フィットネス', value: 'sports_fitness' },
  { label: '教育・文化', value: 'education_culture' },
  { label: '金融・保険', value: 'finance_insurance' },
];

export const FilterCategories = Template.bind({});
FilterCategories.args = {
  inputId: 'category-filter-multi-select',
  modelValue: [],
  placeholder: '特典カテゴリで絞り込み検索',
  options: categoryOptions,
  optionLabel: 'label',
  optionValue: 'value',
  maxSelectedLabels: 2,
  filter: true,
  disabled: false,
  readonly: false,
  showError: true,
  inputClass: '',
  inputStyle: '',
  tabindex: 0,
};
FilterCategories.parameters = {
  docs: {
    description: {
      story: '特典カテゴリの検索例です。「運転」「割引」「医療」などのキーワードで絞り込み検索ができることを示しています。',
    },
  },
};
