<template>
  <nav class="admin-sidebar">
    <div class="admin-sidebar__header">
      <AppTitle class="admin-sidebar__title">管理メニュー</AppTitle>
    </div>
    <ul class="admin-sidebar__menu">
      <li v-for="item in menuItems" :key="item.path" class="admin-sidebar__item">
        <router-link
          :to="item.path"
          class="admin-sidebar__link"
          active-class="admin-sidebar__link--active"
        >
          <i :class="[item.icon, 'admin-sidebar__icon']" />
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
  { path: '/admin/benefits',              label: '特典管理',                 icon: 'pi pi-gift'       },
  { path: '/admin/benefit-eligibilities', label: '特典条件管理',             icon: 'pi pi-filter'     },
  { path: '/admin/benefit-categories',    label: '特典カテゴリ管理',         icon: 'pi pi-tag'        },
  { path: '/admin/municipalities',        label: '自治体管理',               icon: 'pi pi-building'   },
  { path: '/admin/agencies',              label: '事業者管理',               icon: 'pi pi-briefcase'  },
  { path: '/admin/fare-discounts',        label: '運賃割引管理',             icon: 'pi pi-percentage' },
  { path: '/admin/community-buses',       label: 'コミュニティバス路線管理', icon: 'pi pi-map'        },
  { path: '/admin/users',                 label: 'ユーザー管理',             icon: 'pi pi-users'      },
]
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

/** サイドバー固有の配色 */
$sidebar-bg:          #FFFFFF;
$sidebar-border:      #e9ecef;
$sidebar-text:        #6b7280;
$sidebar-text-hover:  #111827;
$sidebar-hover-bg:    #f1f3f5;
$sidebar-active-bg:   #e5e7eb;
$sidebar-active-text: #1f2937;
$sidebar-active-bar:  #6b7280;

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
