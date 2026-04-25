<template>
  <header>
    <Menubar :model="menuItems">
      <!-- タイトル -->
      <template #start>
        <router-link to="/" class="brand-link">
          熊本県自主返納特典マップ
        </router-link>
      </template>

      <!-- ユーザー情報 -->
      <template #end>
        <div v-if="isLoggedIn" class="user-section">
          <span class="user-name">{{ currentUsername }}</span>
        </div>
      </template>
    </Menubar>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import Menubar from 'primevue/menubar'

const router = useRouter()
const auth = useAuthStore()

/** ログイン状態（ストアから算出） */
const isLoggedIn = computed(() => auth.isLoggedIn)
/** ユーザー名（ストアから算出） */
const currentUsername = computed(() => auth.user?.username || '')

/**
 * ルーティング処理
 */
const navigateTo = (path: string): void => {
  router.push(path)
}

/**
 * ログアウト処理
 */
const handleLogout = async (): Promise<void> => {
  await auth.logout()
  router.push('/login')
}

/** メニュー項目の定義 */
const menuItems = computed(() => [
  {
    label: 'ホーム',
    icon: 'pi pi-home',
    command: () => navigateTo('/')
  },
  // ログイン状態に応じて表示されるメニュー項目
  ...(isLoggedIn.value ? [
    {
      label: 'マイページ',
      icon: 'pi pi-user',
      items: [
        {
          label: 'プロフィール',
          icon: 'pi pi-user-edit',
          command: () => navigateTo('/profile')
        },
        {
          label: '設定',
          icon: 'pi pi-cog',
          command: () => navigateTo('/settings')
        },
        {
          label: 'ログアウト',
          icon: 'pi pi-sign-out',
          command: handleLogout
        }
      ]
    }
  ] : [
    {
      label: '新規登録',
      icon: 'pi pi-user-plus',
      command: () => navigateTo('/signup')
    },
    {
      label: 'ログイン',
      icon: 'pi pi-sign-in',
      command: () => navigateTo('/login')
    }
  ])
])
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.p-menubar {
  height: 60px;
  color: base.$base-100;
  background-color: base.$header-background-color;
  border-radius: 0px;
  position: relative;
  z-index: 1000;

  :deep(.p-menubar-root-list) {
    border: none;
    background-color: base.$header-background-color;
    z-index: 1001;
  }

  // ハンバーガーアイコン
  :deep(.p-menubar-button) {
    order: 3;
    width: 35px;
    height: 35px;

    svg {
      width: 21px;
      height: 21px;
    }

    &:hover, &:focus {
      background-color: transparent;
    }
  }

  // タイトル表示欄
  :deep(.p-menubar-start) {
    .brand-link {
      font-size: base.$fontsize-large;
      color: base.$base-100;
      text-decoration: none;

      &:hover, &:focus, &.p-highlight {
        color: base.$base-200;
      }
    }
  }

  // ユーザー名表示欄
  :deep(.p-menubar-end) {
    order: 2;
    margin-left: auto;
  }

  // ドロップダウンメニュー
  :deep(.p-menubar-submenu) {
    border: 1px solid base.$header-border-color;
    background-color: base.$header-background-color;
    z-index: 1001;
  }

  // ドロップダウンのメニューアイテム
  :deep(.p-menubar-item-link) {
    height: 32px;
    background-color: base.$header-background-color;
    color: base.$base-100;

    svg {
      width: 16px;
      height: 16px;
    }

    &:hover, &:focus, &.p-highlight {
      color: base.$base-200;
    }
  }

  // ユーザー名
  :deep(.user-name) {
    margin-right: 12px;
  }
}

</style>
