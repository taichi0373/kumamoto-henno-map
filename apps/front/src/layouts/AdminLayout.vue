<template>
  <div class="admin-layout">
    <AdminSidebar v-model:visible="sidebarVisible" />
    <div
      class="admin-layout__body"
      :class="{ 'admin-layout__body--expanded': sidebarVisible }"
    >
      <header class="admin-layout__topbar">
        <button
          class="admin-layout__toggle"
          type="button"
          aria-label="サイドバーを開閉"
          @click="sidebarVisible = !sidebarVisible"
        >
          <i class="pi pi-bars" />
        </button>
      </header>
      <main class="admin-layout__content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AdminSidebar from '@/components/organisms/AdminSidebar.vue'

/** サイドバーの表示状態（デフォルト: 展開） */
const sidebarVisible = ref(true)
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

/** サイドバー幅（AdminSidebar の :pt root style と揃える） */
$sidebar-width: 220px;

.admin-layout {
  min-height: 100vh;
}

.admin-layout__body {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: base.$base-100;
  margin-left: 0;
  transition: margin-left 0.3s ease;

  &--expanded {
    margin-left: $sidebar-width;
  }
}

.admin-layout__topbar {
  display: flex;
  align-items: center;
  padding: 0.5rem 1rem;
  border-bottom: 1px solid base.$base-400;
  background-color: base.$base-100;
  position: sticky;
  top: 0;
  z-index: 10;
  flex-shrink: 0;
}

.admin-layout__toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.4rem;
  border-radius: 4px;
  color: base.$base-600;
  font-size: 1.1rem;
  line-height: 1;
  transition: background-color 0.15s;

  &:hover {
    background-color: base.$base-300;
  }
}

.admin-layout__content {
  flex: 1;
  padding: 1.5rem;
  overflow-x: auto;
}
</style>
