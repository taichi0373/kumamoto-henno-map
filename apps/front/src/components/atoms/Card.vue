<template>
  <div :class="['card', classes]">
    <div v-if="hasHeader" class="card-header">
      <slot name="header">
        <h3 v-if="title">{{ title }}</h3>
      </slot>
    </div>
    <div class="card-body">
      <slot></slot>
    </div>
    <div v-if="hasFooter" class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script>
import { computed, useSlots } from 'vue';

export default {
  name: "BaseCard",
  props: {
    title: {
      type: String,
      default: "",
    },
    variant: {
      type: String,
      default: 'default',
      validator: (value) => ['default', 'primary', 'secondary', 'success', 'warning', 'error'].includes(value),
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value),
    },
    hoverable: {
      type: Boolean,
      default: true,
    },
    shadow: {
      type: String,
      default: 'medium',
      validator: (value) => ['none', 'small', 'medium', 'large'].includes(value),
    },
  },
  setup(props) {
    const slots = useSlots();

    const hasHeader = computed(() => {
      return props.title || slots.header;
    });

    const hasFooter = computed(() => {
      return slots.footer;
    });

    const classes = computed(() => {
      return {
        [`card-${props.variant}`]: true,
        [`card-${props.size}`]: true,
        [`card-shadow-${props.shadow}`]: true,
        'card-hoverable': props.hoverable,
      };
    });
    
    return {
      hasHeader,
      hasFooter,
      classes,
    };
  }
};
</script>

<style scoped>
.card {
  border-radius: 15px;
  border: none;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  overflow: hidden;
}

.card-hoverable:hover {
  transform: translateY(-5px);
}

/* Shadow variants */
.card-shadow-none {
  box-shadow: none;
}

.card-shadow-small {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-shadow-medium {
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.card-shadow-large {
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.card-hoverable:hover.card-shadow-medium {
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.card-hoverable:hover.card-shadow-large {
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
}

/* Size variants */
.card-small {
  border-radius: 8px;
}

.card-medium {
  border-radius: 15px;
}

.card-large {
  border-radius: 20px;
}

/* Variant styles */
.card-default {
  background: rgba(255, 255, 255, 0.95);
}

.card-primary {
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(0, 123, 255, 0.2);
}

.card-secondary {
  background: rgba(248, 249, 250, 0.95);
}

.card-success {
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(40, 167, 69, 0.2);
}

.card-warning {
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(255, 193, 7, 0.2);
}

.card-error {
  background: rgba(255, 255, 255, 0.98);
  border: 1px solid rgba(220, 53, 69, 0.2);
}

.card-header {
  background: linear-gradient(135deg, #000000, #333333);
  color: white;
  text-align: center;
  padding: 25px;
  border: none;
}

.card-header h3 {
  margin: 0;
  font-weight: 600;
  font-size: 1.5rem;
  letter-spacing: 0.5px;
}

.card-small .card-header {
  padding: 15px;
  border-radius: 8px 8px 0 0;
}

.card-medium .card-header {
  padding: 25px;
  border-radius: 15px 15px 0 0;
}

.card-large .card-header {
  padding: 35px;
  border-radius: 20px 20px 0 0;
}

.card-body {
  padding: 35px;
}

.card-small .card-body {
  padding: 20px;
}

.card-medium .card-body {
  padding: 35px;
}

.card-large .card-body {
  padding: 45px;
}

.card-footer {
  padding: 20px 35px;
  background-color: rgba(248, 249, 250, 0.5);
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.card-small .card-footer {
  padding: 15px 20px;
}

.card-large .card-footer {
  padding: 25px 45px;
}

@media (max-width: 768px) {
  .card-body {
    padding: 25px;
  }
  
  .card-header {
    padding: 20px;
  }
  
  .card-footer {
    padding: 15px 25px;
  }
}
</style>
