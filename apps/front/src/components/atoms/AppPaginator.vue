<template>
  <Paginator
    :first="first"
    :rows="rows"
    :totalRecords="totalRecords"
    :rowsPerPageOptions="rowsPerPageOptions"
    :template="template"
    :showCurrentPageReport="showCurrentPageReport"
    :currentPageReportTemplate="currentPageReportTemplate"
    class="app-paginator"
    :class="inputClass"
    :style="inputStyle"
    @page="onPage"
  />
</template>

<script setup lang="ts">
import Paginator from 'primevue/paginator';

withDefaults(defineProps<{
  first?: number;
  rows?: number;
  totalRecords?: number;
  rowsPerPageOptions?: number[];
  template?: string;
  showCurrentPageReport?: boolean;
  currentPageReportTemplate?: string;
  inputStyle?: Record<string, string> | string;
  inputClass?: string;
}>(), {
  first: 0,
  rows: 10,
  totalRecords: 0,
  rowsPerPageOptions: () => [10, 20, 50],
  template: 'PrevPageLink PageLinks NextPageLink RowsPerPageDropdown',
  showCurrentPageReport: false,
  currentPageReportTemplate: '{first} - {last} / {totalRecords}',
  inputStyle: "",
  inputClass: "",
});

const emit = defineEmits<{
  (e: 'update:first', value: number): void;
  (e: 'update:rows', value: number): void;
  (e: 'page', value: { first: number; rows: number }): void;
}>();

const onPage = (e: { first: number; rows: number }) => {
  emit('update:first', e.first);
  emit('update:rows', e.rows);
  emit('page', e);
};
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.app-paginator :deep(.p-paginator) {
  border: 1px solid base.$base-400;
  border-radius: 6px;
}
</style>
