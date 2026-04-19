import { ref } from 'vue';
import AppFileUpload from './AppFileUpload.vue';
import AppButton from './AppButton.vue';

export default {
  title: 'Design System/Atoms/AppFileUpload',
  component: AppFileUpload,
  parameters: {
    layout: 'padded',
  },
  tags: ['autodocs'],
  argTypes: {
    accept: {
      control: 'text',
      description: 'ファイル種別フィルター（例: ".csv", "image/*"）',
    },
    chooseLabel: {
      control: 'text',
      description: 'ファイル選択ボタンのラベル',
    },
  },
};

/** デフォルト：CSV選択 */
export const Default = {
  render: (args) => ({
    components: { AppFileUpload },
    setup() {
      const file = ref(null);
      return { args, file };
    },
    template: `
      <AppFileUpload
        v-model="file"
        v-bind="args"
        accept=".csv"
        chooseLabel="CSVを選択"
      />
    `,
  }),
};

/** ファイル選択後の状態（インタラクティブ確認用） */
export const WithAction = {
  render: () => ({
    components: { AppFileUpload, AppButton },
    setup() {
      const file = ref(null);
      const fileUploadRef = ref();
      const handleImport = () => {
        if (file.value) {
          console.log('選択ファイル:', file.value.name);
        }
      };
      const handleClear = () => {
        fileUploadRef.value?.clear();
      };
      return { file, fileUploadRef, handleImport, handleClear };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem;">
        <div style="display: flex; align-items: center; gap: 1rem;">
          <AppFileUpload
            ref="fileUploadRef"
            v-model="file"
            accept=".csv"
            chooseLabel="CSVを選択"
          />
        </div>
        <div style="display: flex; gap: 0.5rem;">
          <AppButton
            label="インポート実行"
            icon="pi pi-upload"
            :primary="true"
            :disabled="!file"
            @click="handleImport"
          />
          <AppButton label="クリア" icon="pi pi-times" @click="handleClear" />
        </div>
        <p v-if="file" style="font-size: 0.875rem; color: #555;">
          選択中: {{ file.name }} ({{ Math.round(file.size / 1024) }} KB)
        </p>
      </div>
    `,
  }),
};

/** 全バリエーション */
export const AllVariants = {
  render: () => ({
    components: { AppFileUpload },
    setup() {
      const csvFile = ref(null);
      const imageFile = ref(null);
      const anyFile = ref(null);
      return { csvFile, imageFile, anyFile };
    },
    template: `
      <div style="display: flex; flex-direction: column; gap: 1.5rem;">
        <div>
          <p style="margin-bottom: 0.5rem; font-weight: bold;">CSVファイル専用</p>
          <AppFileUpload v-model="csvFile" accept=".csv" chooseLabel="CSVを選択" />
        </div>
        <div>
          <p style="margin-bottom: 0.5rem; font-weight: bold;">画像ファイル専用</p>
          <AppFileUpload v-model="imageFile" accept="image/*" chooseLabel="画像を選択" />
        </div>
        <div>
          <p style="margin-bottom: 0.5rem; font-weight: bold;">制限なし</p>
          <AppFileUpload v-model="anyFile" chooseLabel="ファイルを選択" />
        </div>
      </div>
    `,
  }),
};
