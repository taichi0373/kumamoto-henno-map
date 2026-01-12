<template>
    <div class="sidebar-tabs">
        <BaseTabButton
          label="経路案内"
          :active="activeTab === 'route-guidance'"
          variant="default"
          size="medium"
          @click="$emit('setActiveTab', 'route-guidance')"
        />
        <BaseTabButton
          label="利用できる特典"
          :active="activeTab === 'users-benefit'"
          variant="default"
          size="medium"
          @click="$emit('setActiveTab', 'users-benefit')"
        />
        <BaseTabButton
          label="特典を探す"
          :active="activeTab === 'search-benefit'"
          variant="default"
          size="medium"
          @click="$emit('setActiveTab', 'search-benefit')"
        />
    </div>
</template>

<script>
import BaseTabButton from '../atoms/TabButton.vue'

export default {
  name: 'SidebarTab',
  components: {
    BaseTabButton
  },
  props: {
    activeTab: {
      type: String,
      required: true
    }
  },
  emits: ['setActiveTab']
}
</script>
<style scoped>
.sidebar-tabs {
  width: 100%;
  display: flex;
  justify-content: space-between;
}
</style>