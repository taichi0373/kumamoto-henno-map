<template>
  <nav class="admin-sidebar">
    <div class="admin-sidebar__header">
      <AppTitle class="admin-sidebar__title">管理メニュー</AppTitle>
    </div>
    <ul class="admin-sidebar__menu">
      <li v-for="item in menuItems" :key="item.path" class="admin-sidebar__item">
        <router-link :to="item.path" class="admin-sidebar__link" active-class="admin-sidebar__link--active">
          <span>{{ item.label }}</span>
        </router-link>
      </li>
    </ul>
  </nav>
</template>

<script setup lang="ts">
import AppTitle from '@/components/atoms/AppTitle.vue'

/** サイドバーメニュー項目 */
const menuItems = [
  { path: '/admin/benefits', label: '特典管理', },
  { path: '/admin/benefit-eligibilities', label: '特典条件管理', },
  { path: '/admin/benefit-categories', label: '特典カテゴリ管理', },
  { path: '/admin/municipalities', label: '自治体管理', },
  { path: '/admin/agencies', label: '事業者管理', },
  { path: '/admin/fare-discounts', label: '運賃割引管理', },
  { path: '/admin/community-buses', label: 'コミュニティバス路線管理', },
  { path: '/admin/users', label: 'ユーザー管理', },
]
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

/** サイドバー固有の配色 */
$sidebar-bg: base.$base-100;
$sidebar-border: base.$base-400;
$sidebar-text: base.$text-secondary;
$sidebar-text-hover: base.$text-primary;
$sidebar-hover-bg: base.$base-200;
$sidebar-active-bg: base.$chose-200 !important;
$sidebar-active-text: base.$text-primary !important;
$sidebar-active-bar: base.$chose-100 !important;

.admin-sidebar {
  width: 220px;
  flex-shrink: 0;
  background-color: $sidebar-bg;
  color: $sidebar-text;
  display: flex;
  flex-direction: column;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.08);

  &__header {
    padding: 16px;
    border-bottom: 1px solid $sidebar-border;
    flex-shrink: 0;
  }

  &__title {
    color: base.$text-primary;
    letter-spacing: 0.02em;
  }

  &__menu {
    list-style: none;
    margin: 0;
    padding: 12px 8px;
    flex: 1;
  }

  &__item {
    margin-bottom: 2px;
  }

  &__link {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 12px;
    color: $sidebar-text;
    text-decoration: none;
    font-size: 14px;
    border-radius: 6px;
    border-left: 3px solid transparent;
    transition: background-color 0.15s, color 0.15s, border-color 0.15s;

    &:hover {
      background-color: $sidebar-hover-bg;
      color: $sidebar-text-hover;
    }

    &--active {
      background-color: $sidebar-active-bg;
      color: $sidebar-active-text;
      font-weight: 600;
      border-left-color: $sidebar-active-bar;
    }
  }

  &__icon {
    font-size: 14px;
    flex-shrink: 0;
    width: 16px;
    text-align: center;
  }
}
</style>
