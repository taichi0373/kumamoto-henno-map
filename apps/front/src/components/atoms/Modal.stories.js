import Modal from './Modal.vue';
import Button from './Button.vue';

export default {
  title: 'Design System/Atoms/Modal',
  component: Modal,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    visible: {
      control: 'boolean',
    },
    title: {
      control: 'text',
    },
    content: {
      control: 'text',
    },
    size: {
      control: { type: 'select' },
      options: ['small', 'medium', 'large', 'fullscreen'],
    },
    closable: {
      control: 'boolean',
    },
    closeOnOverlay: {
      control: 'boolean',
    },
    closeButtonText: {
      control: 'text',
    },
    onClose: { action: 'closed' },
  },
};

export const Default = {
  args: {
    visible: true,
    title: 'モーダルタイトル',
    content: 'これはモーダルの内容です。ここに任意のコンテンツを配置できます。',
  },
};

export const WithoutTitle = {
  args: {
    visible: true,
    content: 'タイトルのないシンプルなモーダルです。',
  },
};

export const NotClosable = {
  args: {
    visible: true,
    title: '閉じることのできないモーダル',
    content: '閉じるボタンが表示されません。',
    closable: false,
  },
};

export const NoOverlayClose = {
  args: {
    visible: true,
    title: 'オーバーレイクリック無効',
    content: 'オーバーレイをクリックしても閉じません。',
    closeOnOverlay: false,
  },
};

export const WithCustomContent = {
  args: {
    visible: true,
    title: 'カスタムコンテンツ',
  },
  render: (args) => ({
    components: { Modal, Button },
    setup() {
      return { args };
    },
    template: `
      <Modal v-bind="args">
        <div style="text-align: center;">
          <h4 style="color: #333; margin-bottom: 20px;">確認が必要です</h4>
          <p style="margin-bottom: 30px; line-height: 1.6;">
            この操作を実行しますか？<br>
            この操作は取り消すことができません。
          </p>
          <div style="display: flex; gap: 10px; justify-content: center;">
            <Button label="キャンセル" :primary="false" />
            <Button label="実行する" :primary="true" />
          </div>
        </div>
      </Modal>
    `,
  }),
};

export const WithFooter = {
  args: {
    visible: true,
    title: 'フッター付きモーダル',
    content: 'モーダルの本文内容です。フッターにボタンが配置されます。',
  },
  render: (args) => ({
    components: { Modal, Button },
    setup() {
      return { args };
    },
    template: `
      <Modal v-bind="args">
        <template #footer>
          <Button label="キャンセル" :primary="false" />
          <Button label="保存" :primary="true" />
        </template>
      </Modal>
    `,
  }),
};

export const Sizes = {
  render: () => ({
    components: { Modal, Button },
    data() {
      return {
        showSmall: false,
        showMedium: false,
        showLarge: false,
        showFullscreen: false,
      };
    },
    template: `
      <div style="display: flex; gap: 10px; flex-wrap: wrap;">
        <Button label="小サイズ" @click="showSmall = true" />
        <Button label="中サイズ" @click="showMedium = true" />
        <Button label="大サイズ" @click="showLarge = true" />
        <Button label="フルスクリーン" @click="showFullscreen = true" />
        
        <Modal 
          :visible="showSmall" 
          size="small" 
          title="小サイズモーダル"
          content="小さなモーダルです。"
          @close="showSmall = false" 
        />
        
        <Modal 
          :visible="showMedium" 
          size="medium" 
          title="中サイズモーダル"
          content="標準的なサイズのモーダルです。"
          @close="showMedium = false" 
        />
        
        <Modal 
          :visible="showLarge" 
          size="large" 
          title="大サイズモーダル"
          content="大きなモーダルです。より多くのコンテンツを表示できます。"
          @close="showLarge = false" 
        />
        
        <Modal 
          :visible="showFullscreen" 
          size="fullscreen" 
          title="フルスクリーンモーダル"
          content="画面いっぱいに表示されるモーダルです。"
          @close="showFullscreen = false" 
        />
      </div>
    `,
  }),
};

export const FormModal = {
  render: () => ({
    components: { Modal, Button },
    data() {
      return {
        showModal: false,
      };
    },
    template: `
      <div>
        <Button label="フォームモーダルを開く" @click="showModal = true" />
        
        <Modal 
          :visible="showModal" 
          title="ユーザー情報入力"
          @close="showModal = false"
        >
          <form style="display: flex; flex-direction: column; gap: 20px;">
            <div>
              <label style="display: block; margin-bottom: 8px; font-weight: 500;">氏名</label>
              <input type="text" placeholder="山田 太郎" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px;" />
            </div>
            
            <div>
              <label style="display: block; margin-bottom: 8px; font-weight: 500;">メールアドレス</label>
              <input type="email" placeholder="example@email.com" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px;" />
            </div>
            
            <div>
              <label style="display: block; margin-bottom: 8px; font-weight: 500;">メッセージ</label>
              <textarea placeholder="メッセージを入力してください..." rows="4" style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; resize: vertical;"></textarea>
            </div>
          </form>
          
          <template #footer>
            <Button label="キャンセル" :primary="false" @click="showModal = false" />
            <Button label="送信" :primary="true" />
          </template>
        </Modal>
      </div>
    `,
  }),
};

export const ConfirmationModal = {
  render: () => ({
    components: { Modal, Button },
    data() {
      return {
        showModal: false,
      };
    },
    template: `
      <div>
        <Button label="削除確認モーダル" @click="showModal = true" />
        
        <Modal 
          :visible="showModal" 
          title="削除の確認"
          size="small"
          @close="showModal = false"
        >
          <div style="text-align: center;">
            <div style="font-size: 48px; color: #dc3545; margin-bottom: 20px;">⚠️</div>
            <h4 style="color: #333; margin-bottom: 15px;">本当に削除しますか？</h4>
            <p style="color: #666; margin-bottom: 0; line-height: 1.5;">
              この操作は取り消すことができません。<br>
              削除されたデータは復元できません。
            </p>
          </div>
          
          <template #footer>
            <Button label="キャンセル" :primary="false" @click="showModal = false" />
            <Button label="削除する" :primary="true" style="background-color: #dc3545;" />
          </template>
        </Modal>
      </div>
    `,
  }),
};

export const AllVariants = {
  render: () => ({
    components: { Modal, Button },
    data() {
      return {
        modals: {
          basic: false,
          withFooter: false,
          confirmation: false,
          form: false,
          large: false,
        }
      };
    },
    template: `
      <div style="display: flex; gap: 10px; flex-wrap: wrap;">
        <Button label="基本モーダル" @click="modals.basic = true" />
        <Button label="フッター付き" @click="modals.withFooter = true" />
        <Button label="確認ダイアログ" @click="modals.confirmation = true" />
        <Button label="フォーム" @click="modals.form = true" />
        <Button label="大サイズ" @click="modals.large = true" />
        
        <!-- 基本モーダル -->
        <Modal 
          :visible="modals.basic" 
          title="基本的なモーダル"
          content="シンプルなモーダルウィンドウです。"
          @close="modals.basic = false" 
        />
        
        <!-- フッター付きモーダル -->
        <Modal 
          :visible="modals.withFooter" 
          title="操作確認"
          content="この操作を続行しますか？"
          @close="modals.withFooter = false"
        >
          <template #footer>
            <Button label="キャンセル" :primary="false" @click="modals.withFooter = false" />
            <Button label="続行" :primary="true" />
          </template>
        </Modal>
        
        <!-- 確認ダイアログ -->
        <Modal 
          :visible="modals.confirmation" 
          title="データの削除"
          size="small"
          @close="modals.confirmation = false"
        >
          <div style="text-align: center;">
            <div style="font-size: 40px; margin-bottom: 15px;">🗑️</div>
            <p>選択したデータを削除しますか？</p>
          </div>
          <template #footer>
            <Button label="キャンセル" :primary="false" @click="modals.confirmation = false" />
            <Button label="削除" :primary="true" style="background-color: #dc3545;" />
          </template>
        </Modal>
        
        <!-- フォームモーダル -->
        <Modal 
          :visible="modals.form" 
          title="新規作成"
          @close="modals.form = false"
        >
          <div style="display: flex; flex-direction: column; gap: 16px;">
            <input type="text" placeholder="タイトル" style="padding: 10px; border: 1px solid #ddd; border-radius: 4px;" />
            <textarea placeholder="説明" rows="3" style="padding: 10px; border: 1px solid #ddd; border-radius: 4px;"></textarea>
          </div>
          <template #footer>
            <Button label="キャンセル" :primary="false" @click="modals.form = false" />
            <Button label="作成" :primary="true" />
          </template>
        </Modal>
        
        <!-- 大サイズモーダル -->
        <Modal 
          :visible="modals.large" 
          title="詳細情報"
          size="large"
          @close="modals.large = false"
        >
          <div style="line-height: 1.8;">
            <h4>利用規約</h4>
            <p>この利用規約は、当サービスの利用条件を定めたものです。ご利用前に必ずお読みください。</p>
            <h5>第1条（目的）</h5>
            <p>本規約は、当社が提供するサービスの利用条件及び当社と利用者との間の権利義務関係を定めることを目的とします。</p>
            <h5>第2条（定義）</h5>
            <p>本規約において使用する用語の定義は、次の各号に定めるところによります。</p>
          </div>
          <template #footer>
            <Button label="同意しない" :primary="false" @click="modals.large = false" />
            <Button label="同意する" :primary="true" />
          </template>
        </Modal>
      </div>
    `,
  }),
};
