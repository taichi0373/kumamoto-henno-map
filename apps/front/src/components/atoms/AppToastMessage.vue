<template>
    <Toast class="app-modeless-toast-message-content-class" position="bottom-center">
    </Toast>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import Toast from 'primevue/toast';
import ToastMessageUtils from '@/utils/toastMessageUtils';
import { useToast } from 'primevue/usetoast';

onMounted(() => {
  const toast = useToast();
  ToastMessageUtils.init(toast);
});
</script>
<style lang="scss" scoped>
@use "@/assets/scss/base";

.app-modeless-toast-message-content-class {
    :deep(.p-toast-message.p-toast-message-info) {
        background-color: base.$base-100;
        color: base.$base-700;
        border: initial;
    }
    :deep(.p-toast-message.p-toast-message-success) {
        background-color: base.$success-200;
        color: base.$success-100;
        border: initial;
    }
    :deep(.p-toast-message.p-toast-message-error) {
        background-color: base.$error-200;
        color: base.$error-100;
        border: initial;
    }
    :deep(.p-toast-message.p-toast-message-warn) {
        background-color: base.$warning-200;
        color: base.$warning-100;
        border: initial;
    }
    :deep(.p-toast-message > .p-toast-message-content) {
        .p-toast-message-icon.pi.pi-check {
            color: base.$success-100;
            height: 24px;
            width: 24px;
            box-sizing: border-box;
        }

        .p-toast-message-icon.pi.pi-info-circle {
            color: base.$base-700;
        }
        .p-toast-message-icon.pi.pi-times {
            color: base.$error-100;
        }
        .p-toast-message-icon.pi.pi-exclamation-triangle {
            color: base.$warning-100;
        }
        .p-toast-message-text > .p-toast-detail {
            margin: 4px 0 4px 0;
        }
    }
}
</style>