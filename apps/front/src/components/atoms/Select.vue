<template>
  <div class="select-wrapper">
    <select
      :id="id"
      :class="['select', classes]"
      :value="modelValue"
      :disabled="disabled"
      :required="required"
      @change="onChange"
      @focus="onFocus"
      @blur="onBlur"
    >
      <option v-if="placeholder" value="" disabled>{{ placeholder }}</option>
      <option
        v-for="option in options"
        :key="option.value"
        :value="option.value"
      >
        {{ option.text || option.label }}
      </option>
      <slot></slot>
    </select>
    <span v-if="errorMessage" class="error-message">{{ errorMessage }}</span>
    <span v-if="helpText" class="help-text">{{ helpText }}</span>
  </div>
</template>

<script>
import { computed } from 'vue';

export default {
  name: "BaseSelect",
  props: {
    id: {
      type: String,
      default: "",
    },
    modelValue: {
      type: [String, Number],
      default: "",
    },
    options: {
      type: Array,
      default: () => [],
    },
    placeholder: {
      type: String,
      default: "",
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
      default: 'default',
      validator: (value) => ['default', 'success', 'error'].includes(value),
    },
    errorMessage: {
      type: String,
      default: "",
    },
    helpText: {
      type: String,
      default: "",
    },
  },
  emits: ['update:modelValue', 'change', 'focus', 'blur'],
  setup(props, { emit }) {
    const classes = computed(() => {
      return {
        [`select-${props.size}`]: true,
        [`select-${props.variant}`]: true,
        'disabled': props.disabled,
        'has-error': props.errorMessage,
      };
    });
    
    const onChange = (event) => {
      emit('update:modelValue', event.target.value);
      emit('change', event.target.value);
    };

    const onFocus = (event) => {
      emit('focus', event);
    };

    const onBlur = (event) => {
      emit('blur', event);
    };
    
    return {
      classes,
      onChange,
      onFocus,
      onBlur,
    };
  }
};
</script>

<style scoped>
.select-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.select {
  padding: 8px 12px;
  border: 2px solid #dee2e6;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.2s;
  outline: none;
  background-color: #ffffff;
  cursor: pointer;
}

.select:focus {
  border-color: #000000;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
}

.select:hover:not(.disabled) {
  border-color: #adb5bd;
}

.select.disabled {
  background-color: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
}

.select.has-error {
  border-color: #6c757d;
}

.select.has-error:focus {
  border-color: #6c757d;
  box-shadow: 0 0 0 2px rgba(108, 117, 125, 0.1);
}

/* Size variants */
.select-small {
  padding: 6px 10px;
  font-size: 12px;
}

.select-medium {
  padding: 8px 12px;
  font-size: 14px;
}

.select-large {
  padding: 12px 16px;
  font-size: 16px;
}

/* Variant styles */
.select-default {
  border-color: #dee2e6;
}

.select-success {
  border-color: #28a745;
}

.select-success:focus {
  border-color: #28a745;
  box-shadow: 0 0 0 2px rgba(40, 167, 69, 0.1);
}

.select-error {
  border-color: #6c757d;
}

.select-error:focus {
  border-color: #6c757d;
  box-shadow: 0 0 0 2px rgba(108, 117, 125, 0.1);
}

.error-message {
  font-size: 12px;
  color: #6c757d;
  margin-top: 2px;
}

.help-text {
  font-size: 12px;
  color: #6c757d;
  margin-top: 2px;
}
</style>
