import SuggestionList from './SuggestionList.vue';

export default {
  title: 'Design System/Atoms/SuggestionList',
  component: SuggestionList,
  argTypes: {
    suggestions: { control: 'object' },
    activeIndex: { control: 'number' },
    select: { action: 'select' }
  },
};

const Template = (args) => ({
  components: { SuggestionList },
  setup() {
    return { args };
  },
  template: `
    <div style="position:relative;max-width:400px;">
      <SuggestionList v-bind="args" @select="args.select" />
    </div>
  `,
});

export const Default = Template.bind({});
Default.args = {
  suggestions: [
    { id: 1, name: '熊本駅', formattedAddress: '〒860-0047, 熊本市西区春日, 熊本県' },
    { id: 2, name: '通町筋', formattedAddress: '熊本市中央区, 熊本県' },
    { id: 3, name: '水前寺公園', formattedAddress: '熊本市中央区, 熊本県' },
  ],
  activeIndex: 1,
};
