import AppRouteGuidance from './AppRouteGuidance.vue';

export default {
  title: 'Design System/Organisms/AppRouteGuidance',
  component: AppRouteGuidance,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export const Default = {
  render: () => ({
    components: { AppRouteGuidance },
    template: `
      <div style="width: 500px; min-height: 600px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <div style="background: #f8f9fa; padding: 16px; border-bottom: 1px solid #dee2e6;">
          <h3 style="margin: 0; color: #495057;">経路案内</h3>
        </div>
        <div style="padding: 20px;">
          <AppRouteGuidance />
        </div>
      </div>
    `,
  }),
};

export const ResponsiveView = {
  render: () => ({
    components: { AppRouteGuidance },
    template: `
      <div>
        <h3 style="margin-bottom: 20px; text-align: center;">レスポンシブ対応の確認</h3>
        <div style="display: flex; flex-direction: column; gap: 20px;">
          <div style="width: 800px; border: 2px dashed #ccc; padding: 20px;">
            <h4 style="margin-top: 0;">デスクトップサイズ (800px)</h4>
            <AppRouteGuidance />
          </div>

          <div style="width: 400px; border: 2px dashed #ccc; padding: 20px;">
            <h4 style="margin-top: 0;">タブレットサイズ (400px)</h4>
            <AppRouteGuidance />
          </div>

          <div style="width: 300px; border: 2px dashed #ccc; padding: 20px;">
            <h4 style="margin-top: 0;">モバイルサイズ (300px)</h4>
            <AppRouteGuidance />
          </div>
        </div>
      </div>
    `,
  }),
};
