<template>
  <div class="checkbox-wrapper">
    <input
      :id="id"
      type="checkbox"
      :class="['checkbox', classes]"
      :checked="modelValue"
      :disabled="disabled"
      :value="value"
      @change="onChange"
    />
    <label v-if="label" :for="id" class="checkbox-label">
      {{ label }}
      <span v-if="required" class="required">*</span>
    </label>
  </div>
</template>

<script>
import { computed } from 'vue';

export default {
  name: "BaseCheckbox",
  props: {
    id: {
      type: String,
      required: true,
    },
    modelValue: {
      type: Boolean,
      default: false,
    },
    label: {
      type: String,
      default: "",
    },
    value: {
      type: [String, Number, Boolean],
      default: true,
    },
    disabled: {
      type: Boolean,
      default: false,
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
      default: 'primary',
      validator: (value) => ['primary', 'secondary', 'success', 'error'].includes(value),
    },
  },
  emits: ['update:modelValue', 'change'],
  setup(props, { emit }) {
    const classes = computed(() => {
      return {
        [`checkbox-${props.size}`]: true,
        [`checkbox-${props.variant}`]: true,
        'disabled': props.disabled,
      };
    });
    
    const onChange = (event) => {
      if (!props.disabled) {
        const checked = event.target.checked;
        emit('update:modelValue', checked);
        emit('change', { checked, value: props.value });
      }
    };
    
    return {
      classes,
      onChange,
    };
  }
};
</script>

<style scoped>
.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.checkbox {
  appearance: none;
  width: 16px;
  height: 16px;
  border: 2px solid #dee2e6;
  border-radius: 3px;
  cursor: pointer;
  position: relative;
  transition: all 0.2s;
}

.checkbox:checked {
  background-color: #000000;
  border-color: #000000;
}

.checkbox:checked::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.checkbox:hover:not(.disabled) {
  border-color: #000000;
}

.checkbox:focus {
  outline: 2px solid #000000;
  outline-offset: 2px;
}

.checkbox.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Size variants */
.checkbox-small {
  width: 14px;
  height: 14px;
}

.checkbox-small:checked::after {
  font-size: 10px;
}

.checkbox-medium {
  width: 16px;
  height: 16px;
}

.checkbox-medium:checked::after {
  font-size: 12px;
}

.checkbox-large {
  width: 20px;
  height: 20px;
}

.checkbox-large:checked::after {
  font-size: 14px;
}

/* Color variants */
.checkbox-primary:checked {
  background-color: #000000;
  border-color: #000000;
}

.checkbox-secondary:checked {
  background-color: #6c757d;
  border-color: #6c757d;
}

.checkbox-success:checked {
  background-color: #28a745;
  border-color: #28a745;
}

.checkbox-error:checked {
  background-color: #dc3545;
  border-color: #dc3545;
}

.checkbox-label {
  font-size: 14px;
  color: #333333;
  cursor: pointer;
  user-select: none;
}

.required {
  color: #dc3545;
}
</style>
