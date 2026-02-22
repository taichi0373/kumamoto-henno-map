<template>
  <header>
    <!-- PC用ナビゲーション -->
    <nav class="nav-pc custom-navbar" id="pc-nav">
      <div class="nav-container">
        <router-link class="nav-brand" to="/">運転免許返納特典マップ</router-link>
        <div class="nav-content" id="navbarSupportedContent">
          <ul class="nav-list nav-left">
            <li class="nav-item">
              <router-link class="nav-link" to="/" exact-active-class="active">ホーム</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/signup" active-class="active">新規登録</router-link>
            </li>
            <li class="nav-item" v-if="!isLoggedIn">
              <router-link class="nav-link" to="/login" active-class="active">ログイン</router-link>
            </li>
            <li class="nav-item" v-if="isLoggedIn">
              <a class="nav-link" href="#" @click="handleLogout">ログアウト</a>
            </li>
          </ul>
          <ul class="nav-list nav-right">
            <li class="nav-item dropdown" v-if="isLoggedIn">
              <a class="nav-link dropdown-toggle" href="#" role="button" @click="toggleDropdown" aria-expanded="false">
                メニュー
              </a>
              <ul class="dropdown-menu" :class="{ show: dropdownOpen }">
                <li class="nav-item">
                  <router-link class="nav-link" to="/profile" active-class="active">プロフィール編集</router-link>
                </li>
              </ul>
            </li>
            <li class="nav-item user-info" v-if="isLoggedIn">
              <span :class="currentUser.username ? 'nav-link active-user' : 'nav-link'">
                {{ currentUser.username || 'ユーザー' }}
              </span>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <!-- スマホ用ナビゲーション -->
    <nav class="nav-mobile custom-navbar-mobile" id="sm-nav">
      <div class="nav-container-mobile">
        <router-link class="nav-brand-mobile" to="/">運転免許返納特典マップ</router-link>

        <button class="nav-toggle custom-toggler" type="button" @click="toggleMobileMenu"
          aria-label="Toggle navigation">
          <span class="nav-toggle-icon"></span>
        </button>

        <div class="mobile-menu custom-offcanvas" :class="{ show: mobileMenuOpen }" id="offcanvasDarkNavbar">
          <div class="mobile-menu-header custom-offcanvas-header">
            <h5 class="mobile-menu-title" id="offcanvasDarkNavbarLabel">メニュー</h5>
            <span class="user-name-mobile" v-if="currentUser.username">
              {{ currentUser.username }}
            </span>
            <button type="button" class="mobile-menu-close custom-btn-close" @click="closeMobileMenu"
              aria-label="Close"></button>
          </div>
          <div class="mobile-menu-body custom-offcanvas-body">
            <ul class="nav-list-mobile">
              <li class="nav-item-mobile">
                <router-link class="nav-link-mobile" to="/" exact-active-class="active">ホーム</router-link>
              </li>
              <li class="nav-item-mobile">
                <router-link class="nav-link-mobile" to="/signup" active-class="active">新規登録</router-link>
              </li>
              <li class="nav-item-mobile" v-if="!isLoggedIn">
                <router-link class="nav-link-mobile" to="/login" active-class="active">ログイン</router-link>
              </li>
              <li class="nav-item-mobile" v-if="isLoggedIn">
                <a class="nav-link-mobile" href="#" @click="handleLogout">ログアウト</a>
              </li>
              <li class="nav-item-mobile" v-if="isLoggedIn">
                <router-link class="nav-link-mobile" to="/profile" active-class="active">プロフィール編集</router-link>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </nav>
  </header>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AuthUtils } from '@/utils/auth'

const route = useRoute()
const router = useRouter()

const isLoggedIn = ref(false)
const dropdownOpen = ref(false)
const mobileMenuOpen = ref(false)
const currentUser = reactive({ username: '' })

const checkLoginStatus = () => {
  isLoggedIn.value = AuthUtils.isLoggedIn()
  console.log('Login Status:', isLoggedIn.value)
}

const loadUserInfo = () => {
  if (isLoggedIn.value) {
    const user = AuthUtils.getUser()
    currentUser.username = user.username || sessionStorage.getItem('username') || ''
  } else {
    // ログアウト状態の場合はユーザー情報をクリア
    currentUser.username = ''
  }
  console.log('Current User:', currentUser.username)
}

const onStorageChange = () => {
  checkLoginStatus()
  loadUserInfo()
}

const closeDropdownOnClickOutside = (event: MouseEvent) => {
  if (!(event.target as Element).closest('.dropdown')) {
    dropdownOpen.value = false
  }
}

const handleLogout = () => {
  AuthUtils.logout()
  isLoggedIn.value = false
  currentUser.username = ''
  dropdownOpen.value = false
  mobileMenuOpen.value = false
  router.push('/login')
}

const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value
}

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

const closeMobileMenu = () => {
  mobileMenuOpen.value = false
}

// ルートが変更されるたびに認証状態をチェック
watch(route, () => {
  checkLoginStatus()
  loadUserInfo()
})

onMounted(() => {
  checkLoginStatus()
  loadUserInfo()
  // セッションストレージの変更を監視
  window.addEventListener('storage', onStorageChange)
  // クリック外でドロップダウンを閉じる
  document.addEventListener('click', closeDropdownOnClickOutside)
})

onBeforeUnmount(() => {
  window.removeEventListener('storage', onStorageChange)
  document.removeEventListener('click', closeDropdownOnClickOutside)
})
</script>

<style scoped>
/* ヘッダー全体のスタイル */
header {
  position: relative;
  z-index: 2;
}

/* PC用ナビゲーション */
.nav-pc {
  background-color: #1a1a1a;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  padding: 0;
}

.nav-container {
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 70px;
}

.nav-brand {
  color: #ffffff;
  font-size: 1.5rem;
  text-decoration: none;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  transition: all 0.3s ease;
  margin-left: 8px;
}

.nav-brand:hover {
  color: #f8f9fa;
  transform: scale(1.05);
  text-shadow: 3px 3px 6px rgba(0, 0, 0, 0.7);
}

.nav-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex: 1;
  margin-left: 40px;
}

.nav-list {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  align-items: center;
}

.nav-left {
  flex: 1;
}

.nav-right {
  margin-left: auto;
}

.nav-item {
  position: relative;
  margin: 0 4px;
}

.nav-item.user-info {
  min-width: 70px;
}

.nav-link {
  color: #cccccc;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
  display: block;
}

.nav-link:hover {
  color: #ffffff;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.2) 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.1);
}

.nav-link.router-link-active,
.nav-link.active {
  color: #ffffff;
  background: transparent;
  box-shadow: none;
}

.active-user {
  color: #ffffff !important;
  border-radius: 6px;
}

/* ドロップダウンメニュー */
.dropdown {
  position: relative;
}

.dropdown-toggle::after {
  content: '▼';
  margin-left: 8px;
  font-size: 0.8em;
  transition: transform 0.3s ease;
}

.dropdown.open .dropdown-toggle::after {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  min-width: 200px;
  background: #1a1a1a;
  color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  list-style: none;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.3s ease;
  padding: 0px;
  z-index: 2;
}

.dropdown-menu.show {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-menu .nav-link {
  color: #c9c9c9;
  padding: 12px 16px;
}

.dropdown-menu .nav-link:hover {
  background: #1a1a1a;
  color: #ffffff;
  transform: none;
  box-shadow: none;
}


/* スマホ用ナビゲーション */
.nav-mobile {
  background: linear-gradient(135deg, #000000 0%, #333333 100%);
  border-bottom: 2px solid #555555;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.3);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 2;
}

.nav-container-mobile {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
}

.nav-brand-mobile {
  color: #ffffff;
  text-decoration: none;
  font-size: 1.25rem;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.5);
}

.nav-toggle {
  border: 2px solid #ffffff;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.nav-toggle:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: #f8f9fa;
  box-shadow: 0 2px 8px rgba(255, 255, 255, 0.2);
}

.nav-toggle-icon {
  display: block;
  width: 20px;
  height: 2px;
  background: #ffffff;
  position: relative;
  transition: background 0.3s ease;
}

.nav-toggle-icon::before,
.nav-toggle-icon::after {
  content: '';
  position: absolute;
  width: 20px;
  height: 2px;
  background: #ffffff;
  left: 0;
  transition: all 0.3s ease;
}

.nav-toggle-icon::before {
  top: -6px;
}

.nav-toggle-icon::after {
  top: 6px;
}

/* モバイルメニュー */
.mobile-menu {
  position: fixed;
  top: 0;
  right: -100%;
  width: 300px;
  height: 100vh;
  background: linear-gradient(180deg, #1a1a1a 0%, #000000 100%);
  border-left: 3px solid #333333;
  color: #ffffff;
  transition: right 0.3s ease;
  z-index: 2;
  overflow-y: auto;
}

.mobile-menu.show {
  right: 0;
}

.mobile-menu-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #333333 0%, #1a1a1a 100%);
  border-bottom: 2px solid #555555;
  padding: 20px;
}

.mobile-menu-title {
  color: #ffffff;
  font-size: 1.5rem;
  margin: 0;
}

.user-name-mobile {
  color: #ffffff;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.25) 100%);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 0.9rem;
}

.mobile-menu-close {
  border-radius: 50%;
  width: 32px;
  height: 32px;
  transition: all 0.3s ease;
  background: none;
  border: 2px solid #ffffff;
  color: #ffffff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mobile-menu-close:hover {
  transform: rotate(90deg);
  box-shadow: 0 2px 8px rgba(255, 255, 255, 0.3);
}

.mobile-menu-close::after {
  content: '✕';
  font-size: 16px;
}

.mobile-menu-body {
  padding: 20px;
}

.nav-list-mobile {
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-item-mobile {
  margin: 4px 0;
}

.nav-link-mobile {
  color: #cccccc;
  text-decoration: none;
  padding: 12px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  border: 1px solid transparent;
  display: block;
}

.nav-link-mobile:hover {
  color: #ffffff;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.2) 100%);
  border-color: rgba(255, 255, 255, 0.3);
  transform: translateX(8px);
}

.nav-link-mobile.router-link-active,
.nav-link-mobile.active {
  color: #ffffff;
  background: transparent;
  border-left: 4px solid #ffffff;
  border-radius: 0 8px 8px 0;
  box-shadow: none;
}

/* モバイルメニューが開いている時のオーバーレイ */
.mobile-menu.show::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  z-index: -1;
}
</style>
