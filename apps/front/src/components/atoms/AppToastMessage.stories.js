import AppToastMessage from './AppToastMessage.vue';
import ToastMessageUtils from '@/utils/toastMessageUtils';

export default {
  title: 'Design System/Atoms/AppToastMessage',
  component: AppToastMessage,
  parameters: {
    layout: 'fullscreen', // トーストメッセージは画面全体に表示されるため
  },
  tags: ['autodocs'],
  decorators: [
    () => ({
      template: `
        <div>
          <story />
        </div>
      `,
    }),
  ],
};

export const Default = {
  render: () => ({
    components: { AppToastMessage },
    methods: {
      showSuccess() {
        ToastMessageUtils.success('保存処理が正常に完了しました。');
      },
      showNotice() {
        ToastMessageUtils.notice('新しいお知らせがあります。');
      },
      showWarning() {
        ToastMessageUtils.warning('入力内容を確認してください。');
      },
      showError() {
        ToastMessageUtils.error('エラーが発生しました。もう一度お試しください。');
      },
      clearAll() {
        ToastMessageUtils.remove();
      },
    },
    template: `
      <div style="padding: 20px;">
        <AppToastMessage />
        <h3 style="margin-bottom: 16px;">トーストメッセージのテスト</h3>
        <p style="margin-bottom: 20px; color: #666;">
          各ボタンをクリックして、画面下部にトーストメッセージが表示されることを確認してください。
        </p>
        <div style="display: flex; gap: 12px; flex-wrap: wrap;">
          <button 
            @click="showSuccess"
            style="background: #28a745; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer;"
          >
            成功メッセージ
          </button>
          <button 
            @click="showNotice"
            style="background: #007bff; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer;"
          >
            お知らせメッセージ
          </button>
          <button 
            @click="showWarning"
            style="background: #ffc107; color: black; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer;"
          >
            警告メッセージ
          </button>
          <button 
            @click="showError"
            style="background: #dc3545; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer;"
          >
            エラーメッセージ
          </button>
          <button 
            @click="clearAll"
            style="background: #6c757d; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer;"
          >
            全て削除
          </button>
        </div>
      </div>
    `,
  }),
};

// export const Success = {
//   render: () => ({
//     components: { AppToastMessage },
//     mounted() {
//       // ストーリー表示時に自動でトーストを表示
//       setTimeout(() => {
//         ToastMessageUtils.success('データが正常に保存されました。');
//       }, 500);
//     },
//     template: `
//       <div style="padding: 20px;">
//         <AppToastMessage />
//         <h3>Success Toast</h3>
//         <p>成功メッセージのサンプルです。自動的にトーストが表示されます。</p>
//       </div>
//     `,
//   }),
// };

// export const Notice = {
//   render: () => ({
//     components: { AppToastMessage },
//     mounted() {
//       setTimeout(() => {
//         ToastMessageUtils.notice('システムメンテナンスのお知らせです。');
//       }, 500);
//     },
//     template: `
//       <div style="padding: 20px;">
//         <AppToastMessage />
//         <h3>Notice Toast</h3>
//         <p>お知らせメッセージのサンプルです。自動的にトーストが表示されます。</p>
//       </div>
//     `,
//   }),
// };

// export const Warning = {
//   render: () => ({
//     components: { AppToastMessage },
//     mounted() {
//       setTimeout(() => {
//         ToastMessageUtils.warning('必須項目が入力されていません。');
//       }, 500);
//     },
//     template: `
//       <div style="padding: 20px;">
//         <AppToastMessage />
//         <h3>Warning Toast</h3>
//         <p>警告メッセージのサンプルです。自動的にトーストが表示されます。</p>
//       </div>
//     `,
//   }),
// };

// export const Error = {
//   render: () => ({
//     components: { AppToastMessage },
//     mounted() {
//       setTimeout(() => {
//         ToastMessageUtils.error('ネットワークエラーが発生しました。');
//       }, 500);
//     },
//     template: `
//       <div style="padding: 20px;">
//         <AppToastMessage />
//         <h3>Error Toast</h3>
//         <p>エラーメッセージのサンプルです。自動的にトーストが表示されます。</p>
//       </div>
//     `,
//   }),
// };

// export const MultipleMessages = {
//   render: () => ({
//     components: { AppToastMessage },
//     methods: {
//       showAll() {
//         ToastMessageUtils.success('1つ目: 保存完了');
//         setTimeout(() => ToastMessageUtils.warning('2つ目: 注意事項があります'), 500);
//         setTimeout(() => ToastMessageUtils.error('3つ目: エラーが発生'), 1000);
//         setTimeout(() => ToastMessageUtils.notice('4つ目: お知らせです'), 1500);
//       },
//       clearAll() {
//         ToastMessageUtils.remove();
//       },
//     },
//     template: `
//       <div style="padding: 20px;">
//         <AppToastMessage />
//         <h3>Multiple Toast Messages</h3>
//         <p>複数のトーストメッセージを順番に表示するテストです。</p>
//         <div style="display: flex; gap: 12px; margin-top: 16px;">
//           <button 
//             @click="showAll"
//             style="background: #17a2b8; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer;"
//           >
//             複数メッセージ表示
//           </button>
//           <button 
//             @click="clearAll"
//             style="background: #6c757d; color: white; border: none; padding: 8px 16px; border-radius: 4px; cursor: pointer;"
//           >
//             全て削除
//           </button>
//         </div>
//       </div>
//     `,
//   }),
// };