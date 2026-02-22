<template>
  <div class="tab-view-wrapper">
    <TabView
      :activeIndex="computedActiveIndex"
      :scrollable="scrollable"
      class="tab-view"
      :class="inputClass"
      :style="inputStyle"
      @tab-change="onTabChange"
    >
      <template v-if="tabs.length">
        <TabPanel
          v-for="(tab, index) in tabs"
          :key="index"
          :value="index"
          :header="tab.label"
          :disabled="tab.disabled"
        >
          <div v-if="tab.content" class="tab-content" v-html="tab.content"></div>
          <slot v-else :name="`tab-${index}`"></slot>
        </TabPanel>
      </template>
      <slot v-else></slot>
    </TabView>
  </div>
</template>


<script setup lang="ts">
import { computed } from 'vue';
import TabView from 'primevue/tabview';
import TabPanel from 'primevue/tabpanel';

interface TabItem {
  label: string;
  content?: string;
  disabled?: boolean;
}

const props = withDefaults(defineProps<{
  modelValue?: number;
  tabs?: TabItem[];
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

const onTabChange = (e: { index: number }) => {
  emit('update:modelValue', e.index);
  emit('tab-change', e);
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.tab-view-wrapper {
  width: 100%;
}

.tab-view :deep(.p-tabview-nav) {
  border-bottom: 1px solid base.$base-400;
}

.tab-view :deep(.p-tabview-nav-link) {
  color: base.$base-600;
}

.tab-view :deep(.p-tabview-nav-link.p-highlight) {
  color: base.$base-700;
}

.tab-content {
  padding: 8px 0;
}
</style>
