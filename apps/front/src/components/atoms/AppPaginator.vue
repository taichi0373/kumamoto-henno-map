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

<script lang="ts">
import { defineComponent, PropType } from 'vue';
import Paginator from 'primevue/paginator';

export default defineComponent({
  name: "AppPaginator",
  components: {
    Paginator,
  },
  props: {
    // 先頭位置
    first: {
      type: Number,
      default: 0,
    },
    // 1ページ件数
    rows: {
      type: Number,
      default: 10,
    },
    // 全件数
    totalRecords: {
      type: Number,
      default: 0,
    },
    // ページサイズ候補
    rowsPerPageOptions: {
      type: Array as PropType<number[]>,
      default: () => [10, 20, 50],
    },
    // テンプレート
    template: {
      type: String,
      default: 'PrevPageLink PageLinks NextPageLink RowsPerPageDropdown',
    },
    // 現在ページ表示
    showCurrentPageReport: {
      type: Boolean,
      default: false,
    },
    // 現在ページ表示テンプレート
    currentPageReportTemplate: {
      type: String,
      default: '{first} - {last} / {totalRecords}',
    },
    // スタイル
    inputStyle: {
      type: [Object, String] as PropType<Record<string, string> | string>,
      required: false,
      default: "",
    },
    // クラス
    inputClass: {
      type: String,
      required: false,
      default: "",
    },
  },
  emits: [
    /** 先頭位置更新 */
    "update:first",
    /** 1ページ件数更新 */
    "update:rows",
    /** ページイベント */
    "page",
  ],
  setup(props, context) {
    const onPage = (e: { first: number; rows: number }) => {
      context.emit('update:first', e.first);
      context.emit('update:rows', e.rows);
      context.emit('page', e);
    };

    return {
      onPage,
    };
  }
});
</script>

<style lang="scss" scoped>
@use "@/assets/scss/base";
.app-paginator :deep(.p-paginator) {
  border: 1px solid base.$base-400;
  border-radius: 6px;
}
</style>
