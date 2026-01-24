<template>
  <div v-if="visible" class="modal-overlay" @click="onOverlayClick">
    <div :class="['modal', classes]" @click.stop>
      <div v-if="showHeader" class="modal-header">
        <h3 v-if="title" class="modal-title">{{ title }}</h3>
        <slot name="header"></slot>
        <BaseButton
          v-if="closable"
          type="button"
          :label="closeButtonText"
          :primary="false"
          class="modal-close"
          @click="onClose"
        />
      </div>
      
      <div class="modal-body">
        <div v-if="content" v-html="content"></div>
        <slot></slot>
      </div>
      
      <div v-if="hasFooter" class="modal-footer">
        <slot name="footer"></slot>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, useSlots } from 'vue';
import BaseButton from './Button.vue';

export default {
  name: "BaseModal",
  components: {
    BaseButton,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
      default: "",
    },
    content: {
      type: String,
      default: "",
    },
    size: {
      type: String,
      default: 'medium',
      validator: (value) => ['small', 'medium', 'large', 'fullscreen'].includes(value),
    },
    closable: {
      type: Boolean,
      default: true,
    },
    closeOnOverlay: {
      type: Boolean,
      default: true,
    },
    closeButtonText: {
      type: String,
      default: "×",
    },
  },
  emits: ['close'],
  setup(props, { emit }) {
    const slots = useSlots();

    const classes = computed(() => {
      return {
        [`modal-${props.size}`]: true,
      };
    });

    const showHeader = computed(() => {
      return props.title || props.closable || slots.header;
    });

    const hasFooter = computed(() => {
      return slots.footer;
    });

    const onClose = () => {
      emit('close');
    };

    const onOverlayClick = () => {
      if (props.closeOnOverlay) {
        onClose();
      }
    };
    
    return {
      classes,
      showHeader,
      hasFooter,
      onClose,
      onOverlayClick,
    };
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
  backdrop-filter: blur(4px);
}

.modal {
  background: white !important;
  border-radius: 15px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-height: 90vh;
  overflow-y: auto;
  position: relative !important;
  opacity: 1 !important;
  transform: scale(1) !important;
  min-height: 200px;
  min-width: 300px;
  display: block !important;
  visibility: visible !important;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

@keyframes overlayFadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Size variants */
.modal-small {
  width: 90%;
  max-width: 400px;
}

.modal-medium {
  width: 90%;
  max-width: 600px;
}

.modal-large {
  width: 90%;
  max-width: 900px;
}

.modal-fullscreen {
  width: 95%;
  max-width: none;
  height: 95vh;
  max-height: 95vh;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 25px;
  border-bottom: 1px solid #dee2e6;
  background: linear-gradient(135deg, #000000, #333333);
  color: white;
  border-radius: 15px 15px 0 0;
}

.modal-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.modal-close {
  background: none !important;
  border: none !important;
  color: white !important;
  font-size: 1.5rem !important;
  font-weight: bold !important;
  cursor: pointer;
  padding: 0 !important;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.3s ease;
}

.modal-close:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.modal-body {
  padding: 25px;
  line-height: 1.6;
  background: #f0f0f0;
  min-height: 100px;
  display: block !important;
  visibility: visible !important;
}

.modal-footer {
  padding: 15px 25px;
  border-top: 1px solid #dee2e6;
  background-color: #f8f9fa;
  border-radius: 0 0 15px 15px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .modal-small,
  .modal-medium,
  .modal-large {
    width: 95%;
  }
  
  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 15px 20px;
  }
  
  .modal-title {
    font-size: 1.25rem;
  }
}
</style>
