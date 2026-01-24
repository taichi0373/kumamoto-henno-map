<template>
  <router-link
    v-if="isInternalLink"
    :to="to"
    :class="['link', classes]"
    @click="onClick"
  >
    <i v-if="icon" :class="icon" class="link-icon"></i>
    {{ text }}
    <slot></slot>
  </router-link>
  <a
    v-else
    :href="linkHref"
    :class="['link', classes]"
    :target="target"
    :rel="rel"
    @click="onClick"
  >
    <i v-if="icon" :class="icon" class="link-icon"></i>
    {{ text }}
    <slot></slot>
  </a>
</template>

<script>
import { computed } from 'vue';

export default {
  name: "BaseLink",
  props: {
    text: {
      type: String,
      default: "",
    },
    to: {
      type: [String, Object],
      default: null,
    },
    href: {
      type: String,
      default: "#",
    },
    target: {
      type: String,
      default: "_self",
      validator: (value) => ['_self', '_blank', '_parent', '_top'].includes(value),
    },
    rel: {
      type: String,
      default: "",
    },
    icon: {
      type: String,
      default: "",
    },
    variant: {
      type: String,
      default: 'primary',
      validator: (value) => ['primary', 'secondary', 'success', 'warning', 'error', 'muted'].includes(value),
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value),
    },
    underline: {
      type: String,
      default: 'hover',
      validator: (value) => ['none', 'always', 'hover'].includes(value),
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['click'],
  setup(props, { emit }) {
    const classes = computed(() => {
      return {
        [`link-${props.variant}`]: true,
        [`link-${props.size}`]: true,
        [`link-underline-${props.underline}`]: true,
        'link-disabled': props.disabled,
      };
    });
    
    // 外部リンクかどうかを判定
    const isInternalLink = computed(() => {
      if (!props.to) return false;
      const url = typeof props.to === 'string' ? props.to : props.to.path || props.to.name;
      return url && !url.startsWith('http://') && !url.startsWith('https://') && !url.startsWith('//');
    });
    
    // リンクのhrefを決定
    const linkHref = computed(() => {
      if (props.to && !isInternalLink.value) {
        return typeof props.to === 'string' ? props.to : props.to.path || props.to.name;
      }
      return props.href;
    });
    
    const onClick = (event) => {
      if (props.disabled) {
        event.preventDefault();
        return;
      }
      emit('click', event);
    };
    
    return {
      classes,
      isInternalLink,
      linkHref,
      onClick,
    };
  }
};
</script>

<style scoped>
.link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
  transition: color 0.3s ease, text-decoration 0.3s ease;
  cursor: pointer;
}

.link-icon {
  display: inline-flex;
  align-items: center;
}

.link.link-disabled {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

/* Size variants */
.link-small {
  font-size: 0.875rem;
}

.link-medium {
  font-size: 1rem;
}

.link-large {
  font-size: 1.125rem;
}

/* Color variants */
.link-primary {
  color: #000000;
}

.link-primary:hover:not(.link-disabled) {
  color: #333333;
}

.link-secondary {
  color: #6c757d;
}

.link-secondary:hover:not(.link-disabled) {
  color: #495057;
}

.link-success {
  color: #28a745;
}

.link-success:hover:not(.link-disabled) {
  color: #1e7e34;
}

.link-warning {
  color: #ffc107;
}

.link-warning:hover:not(.link-disabled) {
  color: #d39e00;
}

.link-error {
  color: #dc3545;
}

.link-error:hover:not(.link-disabled) {
  color: #bd2130;
}

.link-muted {
  color: #868e96;
}

.link-muted:hover:not(.link-disabled) {
  color: #495057;
}

/* Underline variants */
.link-underline-none {
  text-decoration: none;
}

.link-underline-always {
  text-decoration: underline;
}

.link-underline-hover {
  text-decoration: none;
}

.link-underline-hover:hover:not(.link-disabled) {
  text-decoration: underline;
}
</style>
