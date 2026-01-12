<template>
  <label
    :for="htmlFor"
    :class="['label', classes]"
  >
    {{ text }}
    <slot></slot>
  </label>
</template>

<script>
import { computed } from 'vue';

export default {
  name: "BaseLabel",
  props: {
    text: {
      type: String,
      default: "",
    },
    htmlFor: {
      type: String,
      default: "",
    },
    required: {
      type: Boolean,
      default: false,
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value),
    },
    variant: {
      type: String,
      default: 'default',
      validator: (value) => ['default'].includes(value),
    },
    alignConfig: {
      type: String,
      default: 'left',
      validator: (value) => ['left', 'center', 'right'].includes(value),
    },
  },
  setup(props) {
    const classes = computed(() => {
      return {
        [`label-${props.size}`]: true,
        [`label-${props.variant}`]: true,
        [`label-${props.alignConfig}`]: true,
        'label-required': props.required,
      };
    });
    
    return {
      classes,
    };
  }
};
</script>

<style scoped>
.label {
  display: inline-block;
  font-weight: 500;
  margin-bottom: 4px;
  cursor: pointer;
  transition: color 0.2s;
  background-color: #FEFDFE;
}

/* Size variants */
.label-small {
  font-size: 12px;
}

.label-medium {
  font-size: 14px;
}

.label-large {
  font-size: 16px;
}

/* Color variants */
.label-default {
  color: #333333;
}

.label-required::after {
  content: ' *';
  color: #dc3545;
}

.label:hover {
  opacity: 0.8;
}

/* Alignment variants */
.label-left {
  text-align: left;
  margin-left: 10px;
}

.label-center {
  text-align: center;
}

.label-right {
  text-align: right;
  margin-right: 10px;
}
</style>
