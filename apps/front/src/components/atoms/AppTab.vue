<template>
  <div class="tab-view-wrapper">
    <Tabs
      :value="computedActiveIndex"
      class="tab-view"
      :class="inputClass"
      :style="inputStyle"
      @update:value="onTabChange"
    >
      <TabList :scrollable="scrollable">
        <template v-if="tabs.length">
          <Tab
            v-for="(tab, index) in tabs"
            :key="index"
            :value="index"
          >
            {{ tab.label }}
          </Tab>
        </template>
      </TabList>
    </Tabs>
  </div>
</template>


<script setup lang="ts">
import { computed } from 'vue';
import Tabs from 'primevue/tabs';
import Tab from 'primevue/tab';
import TabList from 'primevue/tablist';
import { TabDto } from '@/dto/tabDto';

const props = withDefaults(defineProps<{
  modelValue?: number;
  tabs?: TabDto[];
  scrollable?: boolean;
  inputStyle?: Record<string, string> | string;
  inputClass?: string;
}>(), {
  modelValue: 0,
  tabs: () => [],
  scrollable: false,
  inputStyle: "",
  inputClass: "",
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: number): void;
  (e: 'tab-change', event: { index: number }): void;
}>();

const computedActiveIndex = computed({
  get: () => props.modelValue,
  set: (value) => {
    emit('update:modelValue', value);
  },
});

const onTabChange = (value: string | number) => {
  const index = Number(value);
  emit('update:modelValue', index);
  emit('tab-change', { index });
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.tab-view-wrapper {
  width: 100%;
}

.tab-view :deep(.p-tablist) {
  border-bottom: 1px solid base.$base-400;
}

.tab-view :deep(.p-tab) {
  color: base.$text-primary;
}

.tab-view :deep(.p-tab-active) {
  color: base.$text-primary;
}
</style>
