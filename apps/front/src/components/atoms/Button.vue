<template>
  <button
    :type="type"
    :class="['btn', classes]"
    :disabled="disabled"
    :tabindex="tabindex"
    :title="tooltip"
    @click="onClick"
  >
    <i v-if="icon" :class="['btn-icon', icon]"></i>
    <span v-if="showLabel">{{ buttonLabel }}</span>
    <slot></slot>
  </button>
</template>

<script>
import { reactive, computed } from 'vue';

export default {
  name: "BaseButton",
  props: {
    type: {
      type: String,
      default: 'button',
      validator: (value) => ['button', 'submit', 'reset'].includes(value),
    },
    icon: {
      type: String,
      default: "",
    },
    label: {
      type: String,
      required: true,
    },
    tooltip: {
      type: String,
      default: "",
    },
    showLabel: {
      type: Boolean,
      default: true,
    },
    primary: {
      type: Boolean,
      default: false,
    },
    elipsis: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    tabindex: {
      type: Number,
      required: false,
      default: 0,
    },
  },
  emits: ['click'],
  setup(props, context) {
    props = reactive(props);

    const buttonLabel = computed(() => {
      if (props.elipsis) {
        return `${props.label}...`;
      } else {
        return props.label;
      }
    });

    const classes = computed(() => {
      return {
        'btn-primary': props.primary,
        'btn-secondary': !props.primary,
        'disabled': props.disabled,
      };
    });
    
    const onClick = (event) => {
      if (!props.disabled) {
        context.emit('click', event);
      }
    };
    
    return {
      buttonLabel,
      classes,
      onClick,
    };
  }
};
</script>

<style scoped>
.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  gap: 4px;
}

.btn-icon {
  margin-right: 0;
  font-size: 1.1em;
}

.btn .btn-icon + span {
  margin-left: 6px;
}

.btn:hover:not(.disabled) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.btn:active:not(.disabled) {
  transform: translateY(0);
}

.btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
/* Color variants */
.btn-primary {
  background-color: #000000;
  color: white;
}

.btn-primary:hover:not(.disabled) {
  background-color: #333333;
}

.btn-primary:active:not(.disabled) {
  background-color: #555555;
}

.btn-secondary {
  background-color: #ffffff;
  color: #000000;
  border: 1px solid #dee2e6;
}

.btn-secondary:hover:not(.disabled) {
  background-color: #f8f9fa;
}

.btn-secondary:active:not(.disabled) {
  background-color: #e9ecef;
  color: #000000;
}
</style>
