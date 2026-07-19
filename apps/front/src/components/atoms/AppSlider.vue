<template>
  <div class="app-slider">
    <PSlider
      :id="inputId"
      v-model="computedModel"
      :min="min"
      :max="max"
      :step="step"
      :disabled="disabled"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import PSlider from 'primevue/slider';

const props = withDefaults(defineProps<{
  modelValue?: number | null;
  inputId?: string;
  min?: number;
  max?: number;
  step?: number;
  disabled?: boolean;
}>(), {
  modelValue: null,
  inputId: undefined,
  min: 0,
  max: 100,
  step: 1,
  disabled: false,
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: number | null): void;
}>();

const computedModel = computed({
  get: () => props.modelValue ?? props.min,
  set: (value) => emit('update:modelValue', value),
});
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.app-slider {
  width: 100%;
  padding: 8px 4px;
}
</style>
