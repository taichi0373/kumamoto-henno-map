<template>
    <i :class="classes" :style="styles" @click="onClick"></i>
</template>
<script setup lang="ts">
import { computed } from 'vue';

const props = withDefaults(defineProps<{
  iconName: string;
  color?: string;
  size?: string;
}>(), {
  color: "base-700",
  size: "small",
});

const emit = defineEmits<{
  (e: 'click', event: unknown): void;
}>();

const classes = computed(() =>
  "pi " + props.iconName + " icon-" + props.color
);

const sizeMap = new Map([
  ["small", "14px"],
  ["medium", "16px"],
  ["large", "24px"],
]);

const fontSize = () => {
  return sizeMap.get(props.size) || "12px";
};

const styles = computed(() => {
  return "font-size: " + fontSize();
});

const onClick = (e: unknown) => {
  emit("click", e);
};
</script>
<style lang="scss" scoped>
@use "@/assets/scss/base";

.icon-base-100 {
    color: base.$base-100;
}
.icon-base-200 {
    color: base.$base-200;
}
.icon-base-300 {
    color: base.$base-300;
}
.icon-base-400 {
    color: base.$base-400;
}
.icon-base-500 {
    color: base.$base-500;
}
.icon-base-600 {
    color: base.$base-600;
}
.icon-base-700 {
    color: base.$base-700;
}

.icon-error-100 {
    color: base.$error-100;
}
.icon-error-200 {
    color: base.$error-200;
}

.icon-success-100 {
    color: base.$success-100;
}
.icon-success-200 {
    color: base.$success-200;
}

.icon-warning-100 {
    color: base.$warning-100;
}
.icon-warning-200 {
    color: base.$warning-200;
}

.icon-chose-100 {
    color: base.$chose-100;
}
.icon-chose-200 {
    color: base.$chose-200;
}
</style>