<template>
  <label :for="id" class="app-label" @click="onClick">
    <span v-if="required" class="asterisk">*</span>
    <slot></slot>
  </label>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  id?: string;
  required?: boolean;
}>(), {
  id: "",
  required: false,
});

const emit = defineEmits<{
  (e: 'click'): void;
}>();

/** クリック時 */
const onClick = () => {
  emit("click");
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.app-label {
  display: inline-block;
  padding-left: 12px;
}
</style>