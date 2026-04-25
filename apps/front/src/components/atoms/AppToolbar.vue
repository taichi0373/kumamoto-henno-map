<template>
  <Toolbar v-bind="$attrs" :class="['app-toolbar', $attrs.class]">
    <template v-for="(_, name) in $slots" #[name]="slotProps">
      <slot :name="name" v-bind="slotProps ?? {}" />
    </template>
  </Toolbar>
</template>

<script setup lang="ts">
import Toolbar from 'primevue/toolbar'
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.app-toolbar {
  margin-bottom: 1rem;
}
</style>
