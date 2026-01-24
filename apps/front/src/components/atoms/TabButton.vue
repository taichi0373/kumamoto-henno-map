<template>
  <button
    :type="type"
    :class="['tab-button', classes]"
    :disabled="disabled"
    @click="onClick"
  >
    {{ label }}
    <slot></slot>
  </button>
</template>

<script>
import { computed } from 'vue';

export default {
  name: "BaseTabButton",
  props: {
    type: {
      type: String,
      default: 'button',
      validator: (value) => ['button', 'submit', 'reset'].includes(value),
    },
    label: {
      type: String,
      required: true,
    },
    active: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    variant: {
      type: String,
      default: 'default',
      validator: (value) => ['default'].includes(value),
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value),
    },
  },
  emits: ['click'],
  setup(props, { emit }) {
    const classes = computed(() => {
      return {
        [`tab-button-${props.variant}`]: true,
        [`tab-button-${props.size}`]: true,
        'active': props.active,
        'disabled': props.disabled,
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
.tab-button {
  width: 100%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: none;
  background: transparent;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  text-decoration: none;
  outline: none;
  white-space: nowrap;
  position: relative;
}

.tab-button:focus {
  box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.15);
  border: none;
}

.tab-button.disabled {
  opacity: 0.4;
  cursor: not-allowed;
  pointer-events: none;
  border: none;
}

.tab-button:hover:not(.disabled) {
  transform: scale(1.1);
  border: none;
}
/* サイズ */
.tab-button-small {
  padding: 10px 16px;
  font-size: 0.875rem;
  min-height: 36px;
}

.tab-button-medium {
  padding: 14px 20px;
  font-size: 1rem;
  min-height: 44px;
}

.tab-button-large {
  padding: 18px 24px;
  font-size: 1.125rem;
  min-height: 52px;
}

/* デフォルト*/
.tab-button-default {
  color: #666666;
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
}

.tab-button-default:hover:not(.disabled) {
  color: #333333;
  background: linear-gradient(180deg, #ffffff 0%, #f8f9fa 100%);
  transform: translateY(-1px);
}

.tab-button-default.active {
  color: #000000;
  background: linear-gradient(180deg, #ffffff 0%, #f8f8f8 100%);
  z-index: 1;
}

/* レスポンシブデザイン */
@media (max-width: 768px) {
  .tab-button-small {
    padding: 8px 12px;
    font-size: 0.8rem;
    min-height: 32px;
  }

  .tab-button-medium {
    padding: 10px 16px;
    font-size: 0.9rem;
    min-height: 38px;
  }

  .tab-button-large {
    padding: 12px 18px;
    font-size: 1rem;
    min-height: 44px;
  }
}
</style>
