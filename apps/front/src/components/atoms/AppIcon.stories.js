import AppIcon from './AppIcon.vue';

export default {
  title: 'Design System/Atoms/AppIcon',
  component: AppIcon,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    iconName: {
      control: 'text',
      description: 'PrimeIcons のアイコン名（プレフィックス "pi pi-" は不要）',
    },
    color: {
      control: 'select',
      options: [
        'base-100', 'base-200', 'base-300', 'base-400', 'base-500', 'base-600', 'base-700',
        'error-100', 'error-200',
        'success-100', 'success-200',
        'warning-100', 'warning-200',
        'chose-100', 'chose-200'
      ],
    },
    size: {
      control: 'select',
      options: ['small', 'medium', 'large'],
    },
    onClick: { action: 'clicked' },
  },
};

export const Default = {
  args: {
    iconName: 'pi-info-circle',
    color: 'base-700',
    size: 'small',
  },
};


export const ErrorIcon = {
  args: {
    iconName: 'pi-info-circle',
    color: 'error-100',
    size: 'medium',
  },
};

export const SuccessIcon = {
  args: {
    iconName: 'pi-info-circle',
    color: 'success-100',
    size: 'medium',
  },
};

export const WarningIcon = {
  args: {
    iconName: 'pi-info-circle',
    color: 'warning-100',
    size: 'medium',
  },
};

export const ClickableIcon = {
  args: {
    iconName: 'pi-cog',
    color: 'base-600',
    size: 'large',
  },
  parameters: {
    docs: {
      description: {
        story: 'クリック可能なアイコンの例です。Actionsタブでクリックイベントを確認できます。',
      },
    },
  },
};

export const AllSizes = {
  render: () => ({
    components: { AppIcon },
    template: `
      <div style="display: flex; align-items: center; gap: 16px;">
        <div style="text-align: center;">
          <AppIcon iconName="pi-info-circle" color="base-700" size="small" />
          <div style="margin-top: 8px; font-size: 12px;">Small</div>
        </div>
        <div style="text-align: center;">
          <AppIcon iconName="pi-info-circle" color="base-700" size="medium" />
          <div style="margin-top: 8px; font-size: 12px;">Medium</div>
        </div>
        <div style="text-align: center;">
          <AppIcon iconName="pi-info-circle" color="base-700" size="large" />
          <div style="margin-top: 8px; font-size: 12px;">Large</div>
        </div>
      </div>
    `,
  }),
};

export const AllColors = {
  render: () => ({
    components: { AppIcon },
    template: `
      <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; max-width: 600px;">
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="base-100" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">base-100</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="base-200" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">base-200</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="base-300" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">base-300</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="base-400" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">base-400</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="base-500" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">base-500</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="base-600" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">base-600</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="base-700" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">base-700</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="error-100" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">error-100</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="error-200" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">error-200</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="success-100" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">success-100</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="success-200" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">success-200</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="warning-100" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">warning-100</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="warning-200" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">warning-200</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="chose-100" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">chose-100</div>
        </div>
        <div style="text-align: center; padding: 8px;">
          <AppIcon iconName="pi-info-circle" color="chose-200" size="medium" />
          <div style="margin-top: 4px; font-size: 11px;">chose-200</div>
        </div>
      </div>
    `,
  }),
};