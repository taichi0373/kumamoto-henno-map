import AppLicenseInfo from './AppLicenseInfo.vue';

export default {
  title: 'Design System/Molecules/AppLicenseInfo',
  component: AppLicenseInfo,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export const Default = {
  render: () => ({
    components: { AppLicenseInfo },
    template: `
      <div style="width: 600px; min-height: 400px;">
        <AppLicenseInfo />
      </div>
    `,
  }),
};

export const Responsive = {
  render: () => ({
    components: { AppLicenseInfo },
    template: `
      <div>
        <h3 style="margin-bottom: 20px; text-align: center;">レスポンシブ対応の確認</h3>
        <div style="display: flex; flex-direction: column; gap: 20px;">
          <div style="width: 800px; border: 2px dashed #ccc; padding: 20px;">
            <h4 style="margin-top: 0;">デスクトップサイズ (800px)</h4>
            <AppLicenseInfo />
          </div>
          
          <div style="width: 400px; border: 2px dashed #ccc; padding: 20px;">
            <h4 style="margin-top: 0;">タブレットサイズ (400px)</h4>
            <AppLicenseInfo />
          </div>
          
          <div style="width: 300px; border: 2px dashed #ccc; padding: 20px;">
            <h4 style="margin-top: 0;">モバイルサイズ (300px)</h4>
            <AppLicenseInfo />
          </div>
        </div>
      </div>
    `,
  }),
};
