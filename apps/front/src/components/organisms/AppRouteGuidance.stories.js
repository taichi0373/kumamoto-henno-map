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
      <div style="display: flex; flex-direction: column; gap: 24px;">
        <h3 style="margin: 0;">レスポンシブ表示確認</h3>
        
        <!-- モバイルサイズ -->
        <div style="width: 360px; border: 2px dashed #007bff; padding: 12px;">
          <div style="font-size: 12px; color: #007bff; font-weight: bold; margin-bottom: 8px;">
            Mobile (360px)
          </div>
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
            <div style="background: #f8f9fa; padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6;">
              <strong>経路案内</strong>
            </div>
            <div style="padding: 16px;">
              <AppRouteGuidance />
            </div>
          </div>
        </div>
        
        <!-- タブレットサイズ -->
        <div style="width: 768px; border: 2px dashed #28a745; padding: 12px;">
          <div style="font-size: 12px; color: #28a745; font-weight: bold; margin-bottom: 8px;">
            Tablet (768px)
          </div>
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
            <div style="background: #f8f9fa; padding: 16px; text-align: center; border-bottom: 1px solid #dee2e6;">
              <strong>経路案内</strong>
            </div>
            <div style="padding: 20px;">
              <AppRouteGuidance />
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};
