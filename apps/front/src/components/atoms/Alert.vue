<template>
  <div v-if="visible" :class="['alert', classes]" role="alert">
    <div class="alert-icon"></div>
    <div class="alert-content">
      <div v-if="title" class="alert-title">{{ title }}</div>
      <div class="alert-message">
        {{ message }}
        <slot></slot>
      </div>
    </div>
    <button
      v-if="dismissible"
      type="button"
      class="alert-close"
      @click="onDismiss"
      aria-label="閉じる"
    >
      ×
    </button>
  </div>
</template>

<script>
import { ref, computed } from 'vue';

export default {
  name: "BaseAlert",
  props: {
    message: {
      type: String,
      default: "",
    },
    title: {
      type: String,
      default: "",
    },
    variant: {
      type: String,
      default: 'error',
      validator: (value) => ['success', 'info', 'warning', 'error'].includes(value),
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large'].includes(value),
    },
    dismissible: {
      type: Boolean,
      default: false,
    },
    show: {
      type: Boolean,
      default: true,
    },
  },
  emits: ['dismiss'],
  setup(props, { emit }) {
    const visible = ref(props.show);

    const classes = computed(() => {
      return {
        [`alert-${props.variant}`]: true,
        [`alert-${props.size}`]: true,
        'alert-dismissible': props.dismissible,
      };
    });

    const onDismiss = () => {
      visible.value = false;
      emit('dismiss');
    };
    
    return {
      visible,
      classes,
      onDismiss,
    };
  }
};
</script>

<style scoped>
.alert {
  padding: 15px;
  position: relative;
  border-left: 8px solid #bababa;
  background: #ededed;
  color: #a1a1a1;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: left;
  font-size: 16px;
  margin-bottom: 10px;
  transition: 0.5s ease-in-out;
  animation: showAlert 0.5s ease-in-out;
}

/* サイズ */
.alert-small {
  padding: 10px;
  font-size: 14px;
}

.alert-small .alert-icon,
.alert-small .alert-close {
  font-size: 16px;
}

.alert-medium {
  padding: 15px;
  font-size: 16px;
}

.alert-large {
  padding: 20px;
  font-size: 18px;
}

.alert-large .alert-icon,
.alert-large .alert-close {
  font-size: 22px;
}

/* 種類 */
.alert-success {
  border-color: #2ed573;
  background: #c3f3d7;
  color: #23ad5c;
}

.alert-info {
  border-color: #71c9ff;
  background: #d7f0ff;
  color: #3eb6ff;
}

.alert-warning {
  border-color: #ffa502;
  background: #ffdb9b;
  color: #ce8500;
}

.alert-error {
  border-color: #ff4757;
  background: #ffe0e3;
  color: #ff4757;
}

.alert-icon {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  margin-right: 10px;
  flex-shrink: 0;
}

/* アイコンの表示 */
.alert-success .alert-icon::before {
  content: '✓';
}

.alert-info .alert-icon::before {
  content: 'ℹ';
}

.alert-warning .alert-icon::before {
  content: '⚠';
}

.alert-error .alert-icon::before {
  content: '✕';
}

.alert-content {
  width: 100%;
  line-height: 1.5;
}

.alert-title {
  font-weight: 600;
  margin-bottom: 4px;
  font-size: 1.1em;
}

.alert-message {
  line-height: 1.5;
}

.alert-close {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: none;
  border: none;
  color: inherit;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
  padding: 0;
  margin-left: 10px;
  opacity: 0.7;
  transition: 0.5s;
  flex-shrink: 0;
}

.alert-close:hover {
  opacity: 1;
}

.alert-close:focus {
  outline: 2px solid currentColor;
  outline-offset: 2px;
  border-radius: 2px;
}

/* アニメーション */
@keyframes showAlert {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>