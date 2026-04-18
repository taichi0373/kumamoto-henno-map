<template>
  <div class="admin-layout">
    <AdminSidebar />
    <main class="admin-layout__content">
      <router-view />
    </main>
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

  &__content {
    flex: 1;
    padding: 1.5rem;
    background-color: base.$base-100;
    overflow-x: auto;
  }
}
</style>
