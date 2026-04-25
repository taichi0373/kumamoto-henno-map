import AppUsersBenefit from './AppUsersBenefit.vue'

export default {
  title: 'Design System/Organisms/AppUsersBenefit',
  component: AppUsersBenefit,
  tags: ['autodocs'],
  parameters: {
    layout: 'centered',
  },
  argTypes: {
    usersBenefits: {
      control: 'object',
      description: 'ユーザーが利用できる特典リスト（BenefitDto型の配列）',
    },
  },
}

/** サンプル特典データ（BenefitDto形式） */
const sampleBenefits = [
  {
    benefitId: '1',
    municipalityCd: '43101',
    municipalityName: '熊本市',
    categoryCd: '01',
    benefitName: 'レストラン田舎での割引特典',
    benefitShortName: 'レストラン田舎',
    benefitDetail: '会計から10%割引いたします。',
    expDetail: '2024年12月31日まで',
    phoneNumber: '096-123-4567',
    benefitUrl: 'https://restaurant-inaka.example.com',
    licenseStatus: '03',
    minAge: 65,
    maxAge: null,
    note: '免許返納証明書の提示が必要です。',
  },
  {
    benefitId: '2',
    municipalityCd: '43101',
    municipalityName: '熊本市',
    categoryCd: '02',
    benefitName: 'ハートフル美容室の施術料金割引',
    benefitShortName: 'ハートフル美容室',
    benefitDetail: 'カット料金20%OFF、パーマ・カラー15%OFF',
    expDetail: '期限なし',
    phoneNumber: '096-987-6543',
    benefitUrl: null,
    licenseStatus: '03',
    minAge: 65,
    maxAge: null,
    note: '免許返納証明書の提示が必要です。',
  },
  {
    benefitId: '3',
    municipalityCd: '43101',
    municipalityName: '熊本市',
    categoryCd: '03',
    benefitName: 'フレッシュマート熊本での商品割引',
    benefitShortName: 'フレッシュマート',
    benefitDetail: '毎週火曜日、全商品5%割引',
    expDetail: '2025年3月31日まで',
    phoneNumber: '096-555-0123',
    benefitUrl: 'https://freshmart-kumamoto.example.com',
    licenseStatus: '03',
    minAge: 60,
    maxAge: null,
    note: 'ゴールドカード会員への追加特典です。',
  },
  {
    benefitId: '4',
    municipalityCd: '43102',
    municipalityName: '八代市',
    categoryCd: '04',
    benefitName: 'シルバー温泉の湯の入浴料割引',
    benefitShortName: 'シルバー温泉',
    benefitDetail: '入浴料半額（通常800円→400円）',
    expDetail: '期限なし',
    phoneNumber: '096-777-8899',
    benefitUrl: 'https://silver-onsen.example.com',
    licenseStatus: '03',
    minAge: 65,
    maxAge: null,
    note: '平日限定の特典です。',
  },
  {
    benefitId: '5',
    municipalityCd: '43101',
    municipalityName: '熊本市',
    categoryCd: '05',
    benefitName: '熊本バス運賃割引（シルバーパス）',
    benefitShortName: '熊本バス',
    benefitDetail: '運賃50%割引（シルバーパス）',
    expDetail: '年度更新',
    phoneNumber: '096-100-0001',
    benefitUrl: 'https://kumamoto-bus.example.com',
    licenseStatus: '03',
    minAge: 70,
    maxAge: null,
    note: '専用パス購入が必要です。',
  },
]

const Template = (args) => ({
  components: { AppUsersBenefit },
  setup() {
    return { args }
  },
  template: '<AppUsersBenefit v-bind="args" />',
})

export const Default = Template.bind({})
Default.args = {
  usersBenefits: sampleBenefits,
}

export const Empty = Template.bind({})
Empty.args = {
  usersBenefits: [],
}

export const Single = Template.bind({})
Single.args = {
  usersBenefits: [sampleBenefits[0]],
}

export const Few = Template.bind({})
Few.args = {
  usersBenefits: sampleBenefits.slice(0, 2),
}
