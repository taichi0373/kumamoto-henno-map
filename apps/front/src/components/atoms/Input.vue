<template>
  <div class="input-wrapper">
    <input
      :id="id"
      :type="type"
      :class="['input', classes]"
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      :readonly="readonly"
      :required="required"
      :min="min"
      :max="max"
      :step="step"
      :autocomplete="autocomplete"
      @input="onInput"
      @change="onChange"
      @focus="onFocus"
      @blur="onBlur"
    />
    <span v-if="errorMessage" class="error-message">{{ errorMessage }}</span>
    <span v-if="helpText" class="help-text">{{ helpText }}</span>
  </div>
</template>

<script>
import { computed } from 'vue';

export default {
  name: "BaseInput",
  props: {
    id: {
      type: String,
      default: "",
    },
    type: {
      type: String,
      default: 'text',
      validator: (value) => [
        'text', 'email', 'password', 'number', 'tel', 'url', 'search', 'date', 'time', 'datetime-local'
      ].includes(value),
    },
    modelValue: {
      type: [String, Number],
      default: "",
    },
    placeholder: {
      type: String,
      default: "",
    },
    disabled: {
      type: Boolean,
      default: false,
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    required: {
      type: Boolean,
      default: false,
    },
    min: {
      type: [String, Number],
      default: null,
    },
    max: {
      type: [String, Number],
      default: null,
    },
    step: {
      type: [String, Number],
      default: null,
    },
    autocomplete: {
      type: String,
      default: "",
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
        [`input-${props.size}`]: true,
        [`input-${props.variant}`]: true,
        'disabled': props.disabled,
        'readonly': props.readonly,
        'has-error': props.errorMessage,
      };
    });
    
    const onInput = (event) => {
      emit('update:modelValue', event.target.value);
    };

    const onChange = (event) => {
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
      onInput,
      onChange,
      onFocus,
      onBlur,
    };
  }
};
</script>

<style scoped>
.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.input {
  padding: 8px 12px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.2s;
  outline: none;
  background-color: #ffffff;
}

.input:focus {
  border-color: #000000;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
}

.input:hover:not(.disabled):not(.readonly) {
  border-color: #adb5bd;
}

.input.disabled {
  background-color: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
}

.input.readonly {
  background-color: #f8f9fa;
  cursor: default;
}

.input.has-error {
  border-color: #dc3545;
}

.input.has-error:focus {
  border-color: #dc3545;
  box-shadow: 0 0 0 2px rgba(220, 53, 69, 0.1);
}

/* Size variants */
.input-small {
  padding: 6px 10px;
  font-size: 12px;
}

.input-medium {
  padding: 8px 12px;
  font-size: 14px;
}

.input-large {
  padding: 12px 16px;
  font-size: 16px;
}

/* Variant styles */
.input-default {
  border-color: #dee2e6;
}

.input-success {
  border-color: #28a745;
}

.input-success:focus {
  border-color: #28a745;
  box-shadow: 0 0 0 2px rgba(40, 167, 69, 0.1);
}

.input-error {
  border-color: #dc3545;
}

.input-error:focus {
  border-color: #dc3545;
  box-shadow: 0 0 0 2px rgba(220, 53, 69, 0.1);
}

.error-message {
  font-size: 12px;
  color: #dc3545;
  margin-top: 2px;
}

.help-text {
  font-size: 12px;
  color: #6c757d;
  margin-top: 2px;
}
</style>
