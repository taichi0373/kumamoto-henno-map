<template>
  <Menubar :model="items" />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import Menubar from 'primevue/menubar';

const items = ref([
  {
    label: 'ファイル',
    items: [
      { label: '新規', icon: 'pi pi-fw pi-plus' },
      { label: '開く', icon: 'pi pi-fw pi-download' },
      { separator: true },
      { label: '終了', icon: 'pi pi-fw pi-times' }
    ]
  },
  {
    label: '編集',
    items: [
      { label: '元に戻す', icon: 'pi pi-fw pi-refresh' },
      { label: 'やり直し', icon: 'pi pi-fw pi-check' }
    ]
  },
  {
    label: '表示',
    items: [
      { label: 'ズームイン', icon: 'pi pi-fw pi-search-plus' },
      { label: 'ズームアウト', icon: 'pi pi-fw pi-search-minus' }
    ]
  }
]);
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";
.p-menubar {
  border-radius: 0px;
  z-index: 1000;
}
</style>