import AppCheckbox from './AppCheckbox.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppCheckbox',
  component: AppCheckbox,
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
      control: 'boolean',
      description: 'チェック状態（binaryがtrueの場合はboolean、falseの場合は配列）',
    },
    name: {
      control: 'text',
      description: '入力名（フォームでグループ化する際に使用）',
    },
    value: {
      control: 'text',
      description: 'チェック時の値（binaryがfalseの場合に使用される）',
    },
    binary: {
      control: 'boolean',
      description: 'boolean値として扱うかどうか（true: true/false、false: 値の配列）',
    },
    disabled: {
      control: 'boolean',
      description: '無効状態',
    },
    tabindex: {
      control: { type: 'number', min: -1, max: 20, step: 1 },
      description: 'タブインデックス',
    },
  },
};

const Template = (args) => ({
  components: { AppCheckbox },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <div>
      <AppCheckbox
        v-bind="args"
        v-model="value"
        @change="() => console.log('Changed:', value)"
      />
      <p style="margin-top: 8px; font-size: 12px; color: #666;">
        現在の値: {{ JSON.stringify(value) }}
      </p>
    </div>
  `,
});

export const BinaryMode = Template.bind({});
BinaryMode.args = {
  inputId: 'agreement-checkbox',
  modelValue: false,
  binary: true,
  tabindex: 0,
};
BinaryMode.storyName = 'バイナリモード（利用規約同意など）';

export const BinaryChecked = Template.bind({});
BinaryChecked.args = {
  inputId: 'agreement-checked-checkbox',
  modelValue: true,
  binary: true,
  tabindex: 0,
};
BinaryChecked.storyName = 'バイナリモード（チェック済み）';

const MultiTemplate = (args) => ({
  components: { AppCheckbox },
  setup() {
    const selectedCategories = ref(args.modelValue);
    return { args, selectedCategories };
  },
  template: `
    <div>
      <div style="display: flex; flex-direction: column; gap: 8px;">
        <AppCheckbox
          v-model="selectedCategories"
          input-id="category-transport"
          name="categories"
          value="transport"
          :binary="false"
        />
        <label for="category-transport" style="margin-left: 24px;">公共交通機関</label>
        
        <AppCheckbox
          v-model="selectedCategories"
          input-id="category-shopping"
          name="categories"
          value="shopping"
          :binary="false"
        />
        <label for="category-shopping" style="margin-left: 24px;">商業施設</label>
        
        <AppCheckbox
          v-model="selectedCategories"
          input-id="category-medical"
          name="categories"
          value="medical"
          :binary="false"
        />
        <label for="category-medical" style="margin-left: 24px;">医療・健康</label>
      </div>
      <p style="margin-top: 16px; font-size: 12px; color: #666;">
        選択されたカテゴリ: {{ JSON.stringify(selectedCategories) }}
      </p>
    </div>
  `,
});

export const MultipleChoice = MultiTemplate.bind({});
MultipleChoice.args = {
  modelValue: ['transport'],
};
MultipleChoice.storyName = '複数選択モード';

export const Disabled = Template.bind({});
Disabled.args = {
  inputId: 'disabled-checkbox',
  modelValue: false,
  binary: true,
  disabled: true,
  tabindex: 0,
};
Disabled.storyName = '無効状態';

export const DisabledChecked = Template.bind({});
DisabledChecked.args = {
  inputId: 'disabled-checked-checkbox',
  modelValue: true,
  binary: true,
  disabled: true,
  tabindex: 0,
};
DisabledChecked.storyName = '無効状態（チェック済み）';
