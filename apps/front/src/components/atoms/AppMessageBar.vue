<template>
  <div class="font-bold m-3 p-2 message-bar" :class="{ error: mode == 'error', warning: mode == 'warning', success: mode == 'success'}">
    <AppIcon v-if="mode == 'error'" icon-name="pi-info-circle" color="error-100"></AppIcon>
    <AppIcon v-else-if="mode == 'warning'" icon-name="pi-info-circle" color="warning-100"></AppIcon>
    <AppIcon v-else-if="mode == 'success'" icon-name="pi-info-circle" color="success-100"></AppIcon>
    <AppIcon v-else icon-name="pi-info-circle"></AppIcon>
  {{ message }}
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import AppIcon from './AppIcon.vue';

export default defineComponent({
  name: "AppMessageBar",
  components: {
    AppIcon,
  },
  props: {
    // モード
    mode: {
      type: String,
      default: undefined,
    },
    // メッセージ
    message: {
      type: String,
      default: undefined,
    },
  },
  setup() {
    return {};
  },
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

.message-bar {
  background-color: base.$base-200;
}
.error {
  background-color: base.$error-200;
  color: base.$error-100;
}
.warning {
  background-color: base.$warning-200;
  color: base.$warning-100;
}
.success {
  background-color: base.$success-200;
  color: base.$success-100;
}
</style>
