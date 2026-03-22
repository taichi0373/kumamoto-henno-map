import AppSearchBenefit from './AppSearchBenefit.vue';

export default {
  title: 'Design System/Organisms/AppSearchBenefit',
  component: AppSearchBenefit,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
};

export const Default = {
  render: () => ({
    components: { AppSearchBenefit },
    template: `
      <div style="width: 500px; min-height: 600px; border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
        <div style="background: #f8f9fa; padding: 16px; border-bottom: 1px solid #dee2e6;">
          <h3 style="margin: 0; color: #495057;">特典検索</h3>
        </div>
        <div style="padding: 20px;">
          <AppSearchBenefit />
        </div>
      </div>
    `,
  }),
};

export const ResponsiveView = {
  render: () => ({
    components: { AppSearchBenefit },
    template: `
      <div style="display: flex; flex-direction: column; gap: 24px;">
        <h3 style="margin: 0;">レスポンシブ表示確認</h3>
        
        <!-- モバイルサイズ -->
        <div style="width: 360px; border: 2px dashed #28a745; padding: 12px;">
          <div style="font-size: 12px; color: #28a745; font-weight: bold; margin-bottom: 8px;">
            Mobile (360px)
          </div>
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
            <div style="background: #e8f5e8; padding: 12px; text-align: center; border-bottom: 1px solid #dee2e6;">
              <strong>特典検索</strong>
            </div>
            <div style="padding: 16px;">
              <AppSearchBenefit />
            </div>
          </div>
        </div>
        
        <!-- タブレットサイズ -->
        <div style="width: 768px; border: 2px dashed #17a2b8; padding: 12px;">
          <div style="font-size: 12px; color: #17a2b8; font-weight: bold; margin-bottom: 8px;">
            Tablet (768px)
          </div>
          <div style="border: 1px solid #dee2e6; border-radius: 8px; overflow: hidden;">
            <div style="background: #e8f5e8; padding: 16px; text-align: center; border-bottom: 1px solid #dee2e6;">
              <strong>特典検索</strong>
            </div>
            <div style="padding: 20px;">
              <AppSearchBenefit />
            </div>
          </div>
        </div>
      </div>
    `,
  }),
};
