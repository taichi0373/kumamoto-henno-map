import AppDialog from './AppDialog.vue';
import AppButton from './AppButton.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppDialog',
  component: AppDialog,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    modelValue: {
      control: 'boolean',
      description: '表示フラグ',
    },
    header: {
      control: 'text',
      description: 'ヘッダー',
    },
    modal: {
      control: 'boolean',
      description: 'モーダル',
    },
    closable: {
      control: 'boolean',
      description: '閉じるボタン',
    },
    draggable: {
      control: 'boolean',
      description: 'ドラッグ可',
    },
    dismissableMask: {
      control: 'boolean',
      description: 'マスククリックで閉じる',
    },
  },
};

const Template = (args) => ({
  components: { AppDialog, AppButton },
  setup() {
    const visible = ref(args.modelValue);
    const open = () => {
      visible.value = true;
    };
    return { args, visible, open };
  },
  template: `
    <div>
      <AppButton :label="'ダイアログを開く'" :primary="true" @click="open" />
      <AppDialog v-bind="args" v-model="visible">
        <p style="margin: 0;">ダイアログのコンテンツです。</p>
        <template #footer>
          <AppButton :label="'閉じる'" :primary="false" @click="visible = false" />
        </template>
      </AppDialog>
    </div>
  `,
});

export const Default = Template.bind({});
Default.args = {
  modelValue: false,
  header: 'お知らせ',
  modal: true,
  closable: true,
  draggable: false,
  dismissableMask: false,
};

export const NonModal = Template.bind({});
NonModal.args = {
  ...Default.args,
  header: '非モーダルダイアログ',
  modal: false,
};

export const MaskClosable = Template.bind({});
MaskClosable.args = {
  ...Default.args,
  header: 'マスククリックで閉じる',
  dismissableMask: true,
};
