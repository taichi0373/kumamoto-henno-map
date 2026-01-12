<template>
  <button
    :type="type"
    :class="['icon-button', classes]"
    :disabled="disabled"
    :title="tooltip"
    @click="onClick"
  >
    <i v-if="icon" :class="icon"></i>
    <span v-if="label" class="icon-button-label">{{ label }}</span>
    <slot></slot>
  </button>
</template>

<script>
import { computed } from 'vue';

export default {
  name: "BaseIconButton",
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
      default: "",
    },
    variant: {
      type: String,
      default: 'primary',
      validator: (value) => ['primary', 'secondary'].includes(value),
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large', 'sidebarToggle'].includes(value),
    },
    shape: {
      type: String,
      default: 'rounded',
      validator: (value) => ['rounded', 'circle', 'square'].includes(value),
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    tooltip: {
      type: String,
      default: "",
    },
  },
  emits: ['click'],
  setup(props, { emit }) {
    const classes = computed(() => {
      return {
        [`icon-button-${props.variant}`]: true,
        [`icon-button-${props.size}`]: true,
        [`icon-button-${props.shape}`]: true,
        'disabled': props.disabled,
        'has-label': props.label,
      };
    });
    
    const onClick = (event) => {
      if (!props.disabled) {
        emit('click', event);
      }
    };
    
    return {
      classes,
      onClick,
    };
  }
};
</script>

<style scoped>
.icon-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  text-decoration: none;
  outline: none;
}

.icon-button:focus {
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.2);
}

.icon-button.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

/* Size variants */
.icon-button-small {
  padding: 6px;
  font-size: 0.875rem;
  min-width: 28px;
  height: 28px;
}

.icon-button-small.has-label {
  padding: 6px 12px;
  min-width: auto;
}

.icon-button-medium {
  padding: 8px;
  font-size: 1rem;
  min-width: 36px;
  height: 36px;
}

.icon-button-medium.has-label {
  padding: 8px 16px;
  min-width: auto;
}

.icon-button-large {
  padding: 12px;
  font-size: 1.25rem;
  min-width: 48px;
  height: 48px;
}

.icon-button-large.has-label {
  padding: 12px 20px;
  min-width: auto;
}
.icon-button-sidebarToggle {
  font-size: 1rem;
  width: 23px;
  height: 40px;
}
.icon-button-sidebarToggle.has-label {
  padding: 6px 16px;
  min-width: auto;
}

/* Shape variants */
.icon-button-rounded {
  border-radius: 4px;
}

.icon-button-circle {
  border-radius: 50%;
}

.icon-button-square {
  border-radius: 0;
}

/* Color variants */
.icon-button-primary {
  background-color: #000000;
  color: white;
}

.icon-button-primary:hover:not(.disabled) {
  background-color: #333333;
}

.icon-button-secondary {
  background-color: #FEFEFE;
  color: #333333;
}

.icon-button-secondary:hover:not(.disabled) {
  background-color: #F2F2F2;
}

.icon-button-label {
  font-weight: 500;
}
</style>
