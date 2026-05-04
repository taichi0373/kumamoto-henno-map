<template>
  <PCheckbox
    v-model="computedModel"
    :input-id="inputId"
    :name="name"
    :value="value"
    :binary="binary"
    :disabled="disabled"
    :tabindex="computedTabindex"
    @change="onChange"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue';
import PCheckbox from 'primevue/checkbox';

const props = withDefaults(defineProps<{
  modelValue: unknown[] | string | number | boolean;
  inputId?: string;
  name?: string;
  required?: boolean;
  value?: string | number | null;
  binary?: boolean;
  disabled?: boolean;
  tabindex?: number;
}>(), {
  inputId: undefined,
  name: undefined,
  required: false,
  value: null,
  binary: false,
  disabled: false,
  tabindex: 0,
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: unknown): void;
  (e: 'change'): void;
}>();

const computedModel = computed({
  get: () => {
    if (props.modelValue instanceof Array || (props.binary && typeof props.modelValue === 'boolean')) {
      return props.modelValue;
    } else {
      return [props.modelValue];
    }
  },
  set: (value) => {
    if (value !== props.modelValue) {
      emit('update:modelValue', value);
    }
  },
});

const onChange = () => {
  emit('change');
};

const computedTabindex = computed(() => {
  return props.disabled ? -1 : props.tabindex;
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.p-checkbox {
  .p-checkbox-box {
    border-color: base.$base-500;
    &.p-highlight {
      border-color: base.$chose-100;
      background-color: base.$chose-100;
    }

    &.p-disabled {
      border-color: base.$base-500;
      background-color: base.$base-300;
      opacity: 0.4;
      &.p-highlight {
        background-color: base.$base-500;
        .p-checkbox-icon {
          color: base.$base-100;
      }
      }
    }
  }
  &:not(.p-checkbox-disabled) {
    :deep(.p-checkbox-box:not(.p-highlight)) {
      &:hover {
        border-color: base.$chose-100;
      }
    }
  }
}
</style>
