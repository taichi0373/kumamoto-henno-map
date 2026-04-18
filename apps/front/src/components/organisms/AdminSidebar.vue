<template>
  <Sidebar
    v-model:visible="isVisible"
    :modal="false"
    :dismissable="false"
    position="left"
    style="width: 220px"
  >
    <template #container="{ closeCallback }">
      <div class="flex flex-column h-full admin-sidebar__container">
        <div class="flex align-items-center justify-content-between px-4 pt-3 flex-shrink-0">
          <AppTitle class="admin-sidebar__title">管理メニュー</AppTitle>
          <span>
            <Button
              type="button"
              @click="closeCallback"
              icon="pi pi-times"
              rounded
              outlined
              class="h-2rem w-2rem admin-sidebar__close"
            />
          </span>
        </div>
        <div class="overflow-y-auto">
          <ul class="list-none p-3 m-0">
            <li v-for="item in menuItems" :key="item.path">
              <router-link
                v-ripple
                :to="item.path"
                class="admin-sidebar__link flex align-items-center cursor-pointer p-3 border-round transition-duration-150 transition-colors p-ripple"
                active-class="admin-sidebar__link--active"
              >
                <i :class="[item.icon, 'mr-2']" />
                <span class="font-medium">{{ item.label }}</span>
              </router-link>
            </li>
          </ul>
        </div>
      </div>
    </template>
  </Sidebar>
</template>

<script setup lang="ts">
import Sidebar from 'primevue/sidebar'
import Button from 'primevue/button'
import AppTitle from '@/components/atoms/AppTitle.vue'

/** サイドバーの表示状態（v-model:visible で制御） */
const isVisible = defineModel<boolean>('visible', { default: true })

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

.admin-sidebar {
  &__container {
    background-color: base.$header-background-color;
    color: base.$base-100;
    width: 100%;
    height: 100%;
  }

  &__close {
    color: base.$base-400 !important;
    border-color: base.$base-600 !important;
  }

  &__title {
    color: base.$base-100;
  }

  &__link {
    color: base.$base-300;
    text-decoration: none;

    &:hover {
      background-color: base.$header-border-color;
      color: base.$base-100;
    }

    &--active {
      background-color: base.$header-border-color;
      color: base.$base-100;
      font-weight: bold;
    }
  }
}
</style>
