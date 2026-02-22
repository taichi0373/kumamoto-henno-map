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


<script lang="ts">
import { defineComponent, computed, PropType } from 'vue';
import TabView from 'primevue/tabview';
import TabPanel from 'primevue/tabpanel';

interface TabItem {
  label: string;
  content?: string;
  disabled?: boolean;
}

export default defineComponent({
  name: "AppTabView",
  components: {
    TabView,
    TabPanel,
  },
  props: {
    // アクティブタブ
    modelValue: {
      type: Number,
      default: 0,
    },
    // タブ一覧
    tabs: {
      type: Array as PropType<TabItem[]>,
      default: () => [],
    },
    // スクロール
    scrollable: {
      type: Boolean,
      default: false,
    },
    // スタイル
    inputStyle: {
      type: [Object, String] as PropType<Record<string, string> | string>,
      required: false,
      default: "",
    },
    // クラス
    inputClass: {
      type: String,
      required: false,
      default: "",
    },
  },
  emits: [
    /** アクティブタブ更新 */
    "update:modelValue",
    /** タブ変更 */
    "tab-change",
  ],
  setup(props, context) {
    const computedActiveIndex = computed({
      get: () => props.modelValue,
      set: (value) => {
        context.emit('update:modelValue', value);
      },
    });

    const onTabChange = (e: { index: number }) => {
      context.emit('update:modelValue', e.index);
      context.emit('tab-change', e);
    };

    return {
      computedActiveIndex,
      onTabChange,
    };
  }
});
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
