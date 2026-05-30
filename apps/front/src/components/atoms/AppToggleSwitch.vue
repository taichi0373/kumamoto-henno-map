<template>
  <PToggleSwitch
    v-model="computedModel"
    :disabled="disabled"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import PToggleSwitch from 'primevue/toggleswitch'

const props = withDefaults(defineProps<{
  modelValue: boolean
  disabled?: boolean
}>(), {
  disabled: false,
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

const computedModel = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
</style>
