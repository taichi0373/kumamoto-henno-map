<template>
  <div class="radio-wrapper">
    <input
      :id="id"
      type="radio"
      :class="['radio', classes]"
      :checked="isChecked"
      :disabled="disabled"
      :value="value"
      :name="name"
      @change="onChange"
    />
    <label v-if="label" :for="id" class="radio-label">
      {{ label }}
      <span v-if="required" class="required">*</span>
    </label>
  </div>
</template>

<script>
import { computed } from 'vue';

export default {
  name: "BaseRadio",
  props: {
    id: {
      type: String,
      required: true,
    },
    modelValue: {
      type: [String, Number, Boolean],
      default: null,
    },
    label: {
      type: String,
      default: "",
    },
    value: {
      type: [String, Number, Boolean],
      required: true,
    },
    name: {
      type: String,
      required: true,
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
    const isChecked = computed(() => {
      return props.modelValue === props.value;
    });

    const classes = computed(() => {
      return {
        [`radio-${props.size}`]: true,
        [`radio-${props.variant}`]: true,
        'disabled': props.disabled,
      };
    });
    
    const onChange = (event) => {
      if (!props.disabled) {
        emit('update:modelValue', props.value);
        emit('change', { value: props.value });
      }
    };
    
    return {
      isChecked,
      classes,
      onChange,
    };
  }
};
</script>

<style scoped>
.radio-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.radio {
  appearance: none;
  width: 16px;
  height: 16px;
  border: 2px solid #dee2e6;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  transition: all 0.2s;
}

.radio:checked {
  border-color: #000000;
}

.radio:checked::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #000000;
}

.radio:hover:not(.disabled) {
  border-color: #000000;
}

.radio:focus {
  outline: 2px solid #000000;
  outline-offset: 2px;
}

.radio.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Size variants */
.radio-small {
  width: 14px;
  height: 14px;
}

.radio-small:checked::after {
  width: 6px;
  height: 6px;
}

.radio-medium {
  width: 16px;
  height: 16px;
}

.radio-medium:checked::after {
  width: 8px;
  height: 8px;
}

.radio-large {
  width: 20px;
  height: 20px;
}

.radio-large:checked::after {
  width: 10px;
  height: 10px;
}

/* Color variants */
.radio-primary:checked {
  border-color: #000000;
}

.radio-primary:checked::after {
  background-color: #000000;
}

.radio-secondary:checked {
  border-color: #6c757d;
}

.radio-secondary:checked::after {
  background-color: #6c757d;
}

.radio-success:checked {
  border-color: #28a745;
}

.radio-success:checked::after {
  background-color: #28a745;
}

.radio-error:checked {
  border-color: #dc3545;
}

.radio-error:checked::after {
  background-color: #dc3545;
}

.radio-label {
  font-size: 14px;
  color: #333333;
  cursor: pointer;
  user-select: none;
}

.required {
  color: #dc3545;
}
</style>
