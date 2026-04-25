<template>
  <div :class="['app-file-upload', $attrs.class]">
    <FileUpload
      ref="fileUploadRef"
      mode="basic"
      :accept="accept"
      :chooseLabel="chooseLabel"
      :chooseButtonProps="{ severity: 'secondary' }"
      :auto="false"
      :customUpload="true"
      @select="onSelect"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import FileUpload from 'primevue/fileupload'

/** コンポーネントプロパティ */
interface Props {
  /** v-model: 選択中のファイル */
  modelValue?: File | null
  /** ファイル種別フィルター（例: ".csv"） */
  accept?: string
  /** ボタンラベル */
  chooseLabel?: string
}

withDefaults(defineProps<Props>(), {
  modelValue: null,
  accept: '*',
  chooseLabel: 'ファイルを選択',
})

const emit = defineEmits<{
  /** ファイル選択時 */
  (e: 'update:modelValue', file: File | null): void
}>()

/** FileUpload の公開メソッド（利用分のみ） */
interface FileUploadWithClear {
  clear: () => void
}

const fileUploadRef = ref<FileUploadWithClear | null>(null)

/** ファイル選択時 */
const onSelect = (event: { files: File[] }) => {
  emit('update:modelValue', event.files[0] ?? null)
}

/** ファイル選択をリセットする */
const clear = () => {
  fileUploadRef.value?.clear()
  emit('update:modelValue', null)
}

defineExpose({ clear })
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";

</style>
