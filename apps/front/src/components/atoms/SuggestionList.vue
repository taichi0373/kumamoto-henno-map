<template>
  <ul class="suggestion-list" v-show="suggestions.length > 0">
    <li 
      v-for="(suggestion, index) in suggestions" 
      :key="suggestion.id"
      :class="['suggestion-item', { 'active': index === activeIndex }]"
      @click="$emit('select', suggestion)">
      <div class="suggestion-name">{{ suggestion.name }}</div>
      <div class="suggestion-address">{{ suggestion.formattedAddress }}</div>
    </li>
  </ul>
</template>

<script>
export default {
  name: 'SuggestionList',
  props: {
    suggestions: {
      type: Array,
      required: true
    },
    activeIndex: {
      type: Number,
      default: -1
    }
  }
}
</script>

<style scoped>
.suggestion-list {
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
.suggestion-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f1f3f4;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  line-height: 1.4;
}
.suggestion-item:last-child {
  border-bottom: none;
}
.suggestion-item:hover {
  background: linear-gradient(90deg, #f8f9fa 0%, #e9ecef 100%);
  transform: translateX(2px);
}
.suggestion-item.active {
  background: linear-gradient(90deg, #333333 0%, #555555 100%);
  color: white;
  border-color: #333;
}
.suggestion-name {
  font-weight: 600;
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}
.suggestion-address {
  font-size: 12px;
  color: #666;
  opacity: 0.8;
  line-height: 1.3;
}
.suggestion-item.active .suggestion-name {
  color: white;
}
.suggestion-item.active .suggestion-address {
  color: rgba(255, 255, 255, 0.9);
}
</style>
