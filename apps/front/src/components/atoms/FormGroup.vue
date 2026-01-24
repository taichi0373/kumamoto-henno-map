<template>
  <div :class="['form-group', classes]">
    <slot></slot>
  </div>
</template>

<script>
import { computed } from 'vue';

export default {
  name: "BaseFormGroup",
  props: {
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value),
    },
    variant: {
      type: String,
      default: 'default',
      validator: (value) => ['default', 'inline', 'floating'].includes(value),
    },
    required: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  setup(props) {
    const classes = computed(() => {
      return {
        [`form-group-${props.size}`]: true,
        [`form-group-${props.variant}`]: true,
        'form-group-required': props.required,
        'form-group-disabled': props.disabled,
      };
    });
    
    return {
      classes,
    };
  }
};
</script>

<style scoped>
.form-group {
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
}

/* Size variants */
.form-group-small {
  margin-bottom: 16px;
}

.form-group-medium {
  margin-bottom: 24px;
}

.form-group-large {
  margin-bottom: 32px;
}

/* Layout variants */
.form-group-default {
  display: flex;
  flex-direction: column;
}

.form-group-inline {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 12px;
}

.form-group-floating {
  position: relative;
}

/* State variants */
.form-group-disabled {
  opacity: 0.6;
  pointer-events: none;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .form-group-inline {
    flex-direction: column;
    align-items: stretch;
  }
  
  .form-group-small {
    margin-bottom: 12px;
  }
  
  .form-group-medium {
    margin-bottom: 20px;
  }
  
  .form-group-large {
    margin-bottom: 28px;
  }
}
</style>
