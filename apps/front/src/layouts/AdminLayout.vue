<template>
  <div class="admin-layout">
    <AdminSidebar />
    <div class="admin-layout__body">
      <main class="admin-layout__content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import AdminSidebar from '@/components/organisms/AdminSidebar.vue'

</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.admin-layout {
  display: flex;
  min-height: 100vh;
}

.admin-layout__body {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f6f8;
  overflow: hidden;
}

.admin-layout__topbar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0.25rem;
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: base.$base-100;
  border-bottom: 1px solid base.$base-400;
  flex-shrink: 0;
}

.admin-layout__toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.4rem;
  border-radius: 4px;
  color: base.$base-600;
  line-height: 1;
  transition: background-color 0.15s;

  .pi {
    font-size: 1.75rem;
  }

  &:hover {
    background-color: base.$base-300;
  }
}

.admin-layout__content {
  flex: 1;
  padding: 0 1.5rem;
  overflow-x: auto;
}
</style>
