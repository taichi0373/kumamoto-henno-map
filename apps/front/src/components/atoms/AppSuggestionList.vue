<template>
  <ul 
    v-show="suggestions.length > 0"
    class="app-suggestion-list"
    :class="inputClass"
    :style="inputStyle"
  >
    <li 
      v-for="(suggestion, index) in suggestions" 
      :key="suggestion.id"
      :class="['app-suggestion-item', { 'active': index === activeIndex }]"
      @click="onSelect(suggestion)"
      @mousedown.prevent
    >
      <div class="app-suggestion-name">{{ suggestion.name }}</div>
      <div class="app-suggestion-address">{{ suggestion.formattedAddress }}</div>
    </li>
  </ul>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue';

interface SuggestionItem {
  id: string | number;
  name: string;
  formattedAddress?: string;
  [key: string]: unknown;
}

export default defineComponent({
  name: "AppSuggestionList",
  props: {
    // 提案リスト
    suggestions: {
      type: Array as PropType<SuggestionItem[]>,
      required: true,
    },
    // アクティブなインデックス
    activeIndex: {
      type: Number,
      default: -1,
    },
    // 追加CSSクラス
    inputClass: {
      type: [String, Array, Object] as PropType<string | string[] | Record<string, boolean>>,
      default: "",
    },
    // 追加スタイル
    inputStyle: {
      type: [String, Object] as PropType<string | Record<string, string>>,
      default: "",
    },
  },
  emits: {
    // 提案選択時
    select: (suggestion: SuggestionItem) => true,
  },
  setup(props, { emit }) {
    /**
     * 提案アイテム選択ハンドラ
     * @param suggestion 選択された提案アイテム
     */
    const onSelect = (suggestion: SuggestionItem): void => {
      emit('select', suggestion);
    };

    return {
      onSelect,
    };
  },
});
</script>

<style scoped>
.app-suggestion-list {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  z-index: 2;
  background: linear-gradient(180deg, #ffffff 0%, #f8f8f8 100%);
  border: 2px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  max-height: 250px;
  overflow-y: auto;
  margin: 4px 0 0 0;
  padding: 0;
  list-style: none;
}

.app-suggestion-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f1f3f4;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  line-height: 1.4;
}

.app-suggestion-item:last-child {
  border-bottom: none;
}

.app-suggestion-item:hover {
  background: linear-gradient(90deg, #f8f9fa 0%, #e9ecef 100%);
  transform: translateX(2px);
}

.app-suggestion-item.active {
  background: linear-gradient(90deg, #333333 0%, #555555 100%);
  color: white;
  border-color: #333;
}

.app-suggestion-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.app-suggestion-address {
  font-size: 12px;
  color: #666;
  opacity: 0.8;
  line-height: 1.3;
}

.app-suggestion-item.active .app-suggestion-name {
  color: white;
}

.app-suggestion-item.active .app-suggestion-address {
  color: rgba(255, 255, 255, 0.9);
}
</style>