import Button from './Button.vue';

export default {
  title: 'Design System/Atoms/Button',
  component: Button,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    type: {
      control: { type: 'select' },
      options: ['button', 'submit', 'reset'],
    },
    label: {
      control: 'text',
    },
    icon: {
      control: 'text',
    },
    primary: {
      control: 'boolean',
    },
    elipsis: {
      control: 'boolean',
    },
    disabled: {
      control: 'boolean',
    },
    tabindex: {
      control: 'number',
    },
    onClick: { action: 'clicked' },
  },
};

export const Primary = {
  args: {
    primary: true,
    label: 'プライマリボタン',
  },
};

export const Secondary = {
  args: {
    primary: false,
    label: 'セカンダリボタン',
  },
};

export const WithIcon = {
  args: {
    primary: true,
    icon: '🚀',
    label: 'アイコン付きボタン',
  },
};

export const Elipsis = {
  args: {
    primary: true,
    label: '読み込み中',
    elipsis: true,
  },
};

export const Disabled = {
  args: {
    label: '無効化ボタン',
    disabled: true,
  },
};

export const Submit = {
  args: {
    type: 'submit',
    primary: true,
    label: '送信ボタン',
  },
};

export const Reset = {
  args: {
    type: 'reset',
    label: 'リセットボタン',
  },
};

export const AllVariants = {
  render: () => ({
    components: { Button },
    template: `
      <div style="display: flex; flex-direction: column; gap: 16px; width: 400px;">
        <div style="display: flex; gap: 8px; flex-wrap: wrap;">
          <Button :primary="true" label="Primary" />
          <Button :primary="false" label="Secondary" />
        </div>
        <div style="display: flex; gap: 8px; flex-wrap: wrap;">
          <Button icon="🚀" label="With Icon" />
          <Button :elipsis="true" label="Loading" />
          <Button :disabled="true" label="Disabled" />
        </div>
        <div style="display: flex; gap: 8px; flex-wrap: wrap;">
          <Button type="button" label="Button" />
          <Button type="submit" :primary="true" label="Submit" />
          <Button type="reset" label="Reset" />
        </div>
      </div>
    `,
  }),
};
