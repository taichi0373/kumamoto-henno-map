<template>
  <ul 
    v-show="modelValue && modelValue.length > 0"
    class="app-suggestion-list"
    :class="inputClass"
    :style="inputStyle"
  >
    <li
      v-for="(item, index) in modelValue"
      class="item"
      :key="item.id || index"
      @click="onSelect(item)"
      @mousedown.prevent
    >
      <div class="name">
        <i v-if="item.id === -1" class="pi pi-map-marker name__icon" />
        {{ item.name }}
      </div>
      <div v-if="item.address" class="address">{{ item.address }}</div>
    </li>
  </ul>
</template>

<script setup lang="ts">
import { SuggestionDto } from '@/dto/suggestionDto';

withDefaults(defineProps<{
  modelValue?: SuggestionDto[];
  inputClass?: string | string[] | Record<string, boolean>;
  inputStyle?: Record<string, string> | string;
}>(), {
  inputClass: "",
  inputStyle: "",
});

const emit = defineEmits<{
  (e: 'select', modelValue: SuggestionDto): void;
}>();

/**
 * 提案アイテム選択ハンドラ
 * @param item 選択された提案アイテム
 */
const onSelect = (item: SuggestionDto): void => {
  emit('select', item);
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.app-suggestion-list {
  background: base.$base-100;
  border: 1px solid base.$base-400;
  border-radius: 4px;
  overflow-x: hidden;
  overflow-y: auto;
  margin: 0;
  padding: 0;
  list-style: none;
}

.item {
  padding: 8px 12px;
  border-bottom: 1px solid base.$base-400;
  cursor: pointer;
}

.item:last-child {
  border-bottom: none;
}

.item:hover {
  background: base.$base-200;
}

.name {
  color: base.$text-primary;

  &__icon {
    margin-right: 4px;
    font-size: 14px;
  }
}

.address {
  color: base.$text-primary;
  margin-top: 4px;
  font-size: 12px;
}
</style>