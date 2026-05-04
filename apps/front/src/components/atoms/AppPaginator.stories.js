import AppPaginator from './AppPaginator.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppPaginator',
  component: AppPaginator,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    first: {
      control: { type: 'number', min: 0, max: 200, step: 1 },
      description: '先頭位置',
    },
    rows: {
      control: { type: 'number', min: 1, max: 100, step: 1 },
      description: '1ページ件数',
    },
    totalRecords: {
      control: { type: 'number', min: 0, max: 1000, step: 10 },
      description: '全件数',
    },
    rowsPerPageOptions: {
      control: 'object',
      description: 'ページサイズ候補',
    },
    template: {
      control: 'text',
      description: 'テンプレート',
    },
    showCurrentPageReport: {
      control: 'boolean',
      description: '現在ページ表示',
    },
    currentPageReportTemplate: {
      control: 'text',
      description: '現在ページテンプレート',
    },
  },
};

const Template = (args) => ({
  components: { AppPaginator },
  setup() {
    const first = ref(args.first);
    const rows = ref(args.rows);
    return { args, first, rows };
  },
  template: `
    <AppPaginator
      v-bind="args"
      :first="first"
      :rows="rows"
      @update:first="(value) => (first = value)"
      @update:rows="(value) => (rows = value)"
      @page="(event) => console.log('Page:', event)"
    />
  `,
});

export const Default = Template.bind({});
Default.args = {
  first: 0,
  rows: 10,
  totalRecords: 125,
  rowsPerPageOptions: [10, 20, 50],
  template: 'PrevPageLink PageLinks NextPageLink RowsPerPageDropdown',
  showCurrentPageReport: false,
  currentPageReportTemplate: '{first} - {last} / {totalRecords}',
};

export const WithReport = Template.bind({});
WithReport.args = {
  ...Default.args,
  showCurrentPageReport: true,
};
