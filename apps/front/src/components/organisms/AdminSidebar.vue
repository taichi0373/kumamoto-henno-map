<template>
  <nav class="admin-sidebar">
    <div class="admin-sidebar__header">
      <span class="admin-sidebar__title">管理メニュー</span>
    </div>
    <ul class="admin-sidebar__menu">
      <li v-for="item in menuItems" :key="item.path" class="admin-sidebar__item">
        <router-link
          :to="item.path"
          class="admin-sidebar__link"
          active-class="admin-sidebar__link--active"
        >
          {{ item.label }}
        </router-link>
      </li>
    </ul>
  </nav>
</template>

<script setup lang="ts">
/** サイドバーメニュー項目 */
const menuItems = [
  { path: '/admin/benefits', label: '特典管理' },
  { path: '/admin/benefit-eligibilities', label: '特典条件管理' },
  { path: '/admin/benefit-categories', label: '特典カテゴリ管理' },
  { path: '/admin/municipalities', label: '自治体管理' },
  { path: '/admin/agencies', label: '事業者管理' },
  { path: '/admin/fare-discounts', label: '運賃割引管理' },
  { path: '/admin/community-buses', label: 'コミュニティバス路線管理' },
  { path: '/admin/users', label: 'ユーザー管理' },
]
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.admin-sidebar {
  width: 220px;
  min-height: 100vh;
  background-color: base.$base-700;
  color: base.$base-100;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;

  &__header {
    padding: 1.25rem 1rem;
    border-bottom: 1px solid base.$base-600;
  }

  &__title {
    font-weight: bold;
    font-size: 1rem;
  }

  &__menu {
    list-style: none;
    margin: 0;
    padding: 0.5rem 0;
  }

  &__item {
    margin: 0;
  }

  &__link {
    display: block;
    padding: 0.65rem 1rem;
    color: base.$base-300;
    text-decoration: none;
    font-size: 0.875rem;
    transition: background-color 0.15s, color 0.15s;

    &:hover {
      background-color: base.$base-600;
      color: base.$base-100;
    }

    &--active {
      background-color: base.$base-600;
      color: base.$base-100;
      font-weight: bold;
    }
  }
}
</style>
