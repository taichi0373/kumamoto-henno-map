import AppToggleSwitch from './AppToggleSwitch.vue';
import { ref } from 'vue';

export default {
  title: 'Design System/Atoms/AppToggleSwitch',
  component: AppToggleSwitch,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    modelValue: {
      control: 'boolean',
      description: 'スイッチの状態（true: ON、false: OFF）',
    },
    disabled: {
      control: 'boolean',
      description: '無効状態',
    },
  },
};

const Template = (args) => ({
  components: { AppToggleSwitch },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <div>
      <AppToggleSwitch
        v-model="value"
        :disabled="args.disabled"
      />
      <p style="margin-top: 8px; font-size: 12px; color: #666;">
        現在の値: {{ value }}
      </p>
    </div>
  `,
});

export const Default = Template.bind({});
Default.args = {
  modelValue: false,
  disabled: false,
};
Default.storyName = 'デフォルト（OFF）';

export const On = Template.bind({});
On.args = {
  modelValue: true,
  disabled: false,
};
On.storyName = 'ON状態';

export const Disabled = Template.bind({});
Disabled.args = {
  modelValue: false,
  disabled: true,
};
Disabled.storyName = '無効状態（OFF）';

export const DisabledOn = Template.bind({});
DisabledOn.args = {
  modelValue: true,
  disabled: true,
};
DisabledOn.storyName = '無効状態（ON）';

const WithLabelTemplate = (args) => ({
  components: { AppToggleSwitch },
  setup() {
    const value = ref(args.modelValue);
    return { args, value };
  },
  template: `
    <div style="display: flex; align-items: center; gap: 8px;">
      <i class="pi pi-shop" style="font-size: 15px; color: #6b7280;"></i>
      <span style="font-size: 13px; font-weight: 500;">店舗マーカー</span>
      <AppToggleSwitch
        v-model="value"
        :disabled="args.disabled"
      />
    </div>
  `,
});

export const WithLabel = WithLabelTemplate.bind({});
WithLabel.args = {
  modelValue: false,
  disabled: false,
};
WithLabel.storyName = 'ラベル付き（マップ上での使用例）';
