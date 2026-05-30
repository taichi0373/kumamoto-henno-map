<template>
  <div class="p-2">
    <form @submit.prevent="handleSearchRoute(searchRoute)">
      <div class="form-row-1">
        <div class="form-col">
          <AppLabel :id="'transport'" :required="true">交通手段</AppLabel>
          <AppSelect :input-id="'transport'" v-model="searchRoute.transport" :options="transportOptions" :required="true" :show-clear="false" />
        </div>

        <div class="form-col">
          <AppLabel :id="'start-location'" :required="true">出発地</AppLabel>
          <div class="search-input-container">
            <AppInputGroupWithButton v-model="searchRoute.startLocation" :input-id="'start-location'" :type="'text'"
              :placeholder="'出発地を入力してください'" :required="true"
              :button-icon="startButtonIcon"
              :error="startLocationErrorDto" @input="onInput('start')" @focus="onFocus('start')"
              @button-click="onStartButtonClick" />
            <!-- 検索候補 -->
            <AppSuggestionList :modelValue="startSuggestions" @select="selectStartLocation" />
          </div>
        </div>

        <div class="form-col">
          <AppLabel :id="'end-location'" :required="true">目的地</AppLabel>
          <div class="search-input-container">
            <AppInputGroupWithButton v-model="searchRoute.endLocation" :input-id="'end-location'" :type="'text'"
              :placeholder="'目的地を入力してください'" :required="true"
              :button-icon="endButtonIcon"
              :error="endLocationErrorDto" @input="onInput('end')" @focus="onFocus('end')"
              @button-click="onEndButtonClick" />
            <!-- 検索候補 -->
            <AppSuggestionList :modelValue="endSuggestions" @select="selectEndLocation" />
          </div>
        </div>
      </div>

      <div class="expand-trigger" @click="toggleConditions">
        {{ showConditions ? '条件を閉じる' : '条件指定' }}
        <span :class="showConditions ? 'icon-expand-trigger rotated' : 'icon-expand-trigger'">
          {{ showConditions ? '▲' : '▼' }}
        </span>
      </div>
      <div v-if="showConditions">
        <div class="form-col">
          <AppLabel :id="'departure-arrival'">出発/到着</AppLabel>
          <AppSelect :input-id="'departure-arrival'" v-model="searchRoute.departureArrival" :options="departureArrivalOptions" :show-clear="false" />
        </div>
        <div class="form-row-2 mt-4">
          <div class="form-col">
            <AppLabel :id="'date'">日付</AppLabel>
            <AppCalendar :input-id="'date'" type="date" v-model="searchRoute.date" :placeholder="''" />
          </div>
          <div class="form-col">
            <AppLabel :id="'time'">時間</AppLabel>
            <AppTimePicker :input-id="'time'" type="time" v-model="searchRoute.time" :placeholder="''" />
          </div>
        </div>
      </div>

      <div class="form-btn">
        <AppButton :label="'クリア'" :primary="false" :icon="'pi pi-trash'" @click="clearConditions" />
        <AppButton type="submit" :label="'経路を検索'" :primary="true" :icon="'pi pi-search'" :disabled="isLoading"/>
      </div>
    </form>
  </div>

  <!-- ローディング -->
  <div v-if="isLoading" class="loading-icon">
    <AppProgressSpinner/>
  </div>
  <!-- 経路探索結果 -->
  <AppRouteResultList v-if="!isLoading" :routes="routes" :active-route-index="props.activeRouteIndex"
    @select-route="emit('select-route', $event)" />
  <AppAlert v-if="hasSearched && !isLoading && routes.length === 0" :variant="'error'" :message="'経路が見つかりませんでした'"
    class="mt-2" />

</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import type { Ref } from 'vue'
import AppAlert from '../atoms/AppAlert.vue'
import AppLabel from '../atoms/AppLabel.vue'
import AppSelect from '../atoms/AppSelect.vue'
import AppButton from '../atoms/AppButton.vue'
import AppInputGroupWithButton from '../molecules/AppInputGroupWithButton.vue'
import AppRouteResultList from './AppRouteResultList.vue'
import AppSuggestionList from '../atoms/AppSuggestionList.vue'
import AppCalendar from '../atoms/AppCalendar.vue'
import AppTimePicker from '../atoms/AppTimePicker.vue'
import AppProgressSpinner from '../atoms/AppProgressSpinner.vue'
import { codeConstant } from '@/utils/codeConstant'
import { RouteRequestDto } from '@/dto/routeRequestDto'
import { SelectDto } from '@/dto/selectDto'
import { RouteDto } from '@/dto/routeDto'
import { MarkerDto } from '@/dto/markerDto'
import { SuggestionDto } from '@/dto/suggestionDto'
import { ValidateUtils } from '@/utils/validateUtils'
import { MESSAGE_LIST, MESSAGE_NO } from '@/utils/messageConstant'
import { InputFormErrorDto } from "@/dto/InputFormErrorDto";
import { MessageUtils } from '@/utils/messageUtils'

const props = withDefaults(defineProps<{
  /** 出発地候補リスト */
  startSuggestions?: SuggestionDto[];
  /** 目的地候補リスト */
  endSuggestions?: SuggestionDto[];
  /** 経路検索結果 */
  routes?: RouteDto[];
  /** 経路検索ローディング状態 */
  isLoading?: boolean;
  /** マップ選択結果 */
  mapSelectedLocation?: MarkerDto | null;
  /** マップでアクティブな経路インデックス */
  activeRouteIndex?: number;
}>(), {
  startSuggestions: () => [],
  endSuggestions: () => [],
  routes: () => [],
  isLoading: false,
  mapSelectedLocation: null,
  activeRouteIndex: 0,
});

const emit = defineEmits<{
  /** 経路探索 */
  (e: 'search-route', routeRequest: RouteRequestDto): void;
  /** マップ選択モードの切り替え */
  (e: 'select-on-map', type: string): void;
  /** マーカー設置 */
  (e: 'set-marker', marker: MarkerDto): void;
  /** マーカー削除 */
  (e: 'remove-marker', type: string): void;
  /** 候補リストの取得（ジオコーディング） */
  (e: 'fetch-suggestions', marker: MarkerDto): void;
  /** 候補リストのクリア */
  (e: 'clear-suggestions'): void;
  /** 経路探索結果のクリア */
  (e: 'clear-routes'): void;
  /** 現在地の取得 */
  (e: 'fetch-current-location', type: string): void;
  /** 経路カード選択 */
  (e: 'select-route', index: number): void;
}>();

/** エラーオブジェクト */
const startLocationErrorDto = ref([]) as Ref<InputFormErrorDto[]>
const endLocationErrorDto = ref([]) as Ref<InputFormErrorDto[]>

/** 検索条件 */
const searchRoute = ref<RouteRequestDto>(new RouteRequestDto())

/** 条件表示フラグ */
const showConditions = ref(false)

/** 検索実行済みフラグ */
const hasSearched = ref(false)

/** 交通手段のプルダウン */
const transportOptions = ref([]) as Ref<SelectDto[]>
const transportLabels = {
  [codeConstant.TRANSPORTATION.TRANSIT]: '公共交通機関',
  [codeConstant.TRANSPORTATION.RAIL]: '電車',
  [codeConstant.TRANSPORTATION.BUS]: 'バス',
  [codeConstant.TRANSPORTATION.WALK]: '徒歩',
  [codeConstant.TRANSPORTATION.BICYCLE]: '自転車',
}

/** 出発/到着のプルダウン */
const departureArrivalOptions = ref([]) as Ref<SelectDto[]>
const departureArrivalLabels = {
  [codeConstant.DEPARTURE_ARRIVAL.DEPARTURE]: '出発',
  [codeConstant.DEPARTURE_ARRIVAL.ARRIVAL]: '到着',
}

// コンポーネントアンマウント時にイベントリスナーをクリーンアップ
onUnmounted(() => {
  document.removeEventListener('click', handleOutsideClick)
})

// 初期表示
onMounted(() => {
  // 交通手段オプションの取得
  getTransportOptions()
  // 出発/到着オプションの取得
  getDepartureArrivalOptions()

  // 外部クリック時、候補リストをクリア
  document.addEventListener('click', handleOutsideClick)
})

/** マップ選択結果の監視 */
watch(() => props.mapSelectedLocation, (location) => {
  if (ValidateUtils.isNullOrEmpty(location)) {
    return;
  }
  const displayName = location.name || location.address || null
  if (location.type === codeConstant.SEARCH_TYPE.START) {
    // 出発地に設定
    searchRoute.value.startLocation = displayName;
    searchRoute.value.startLat = location.lat;
    searchRoute.value.startLon = location.lon;
    // マーカー設置
    const marker = new MarkerDto({
      type: 'start',
      name: location.name,
      address: location.address,
      lat: location.lat,
      lon: location.lon
    });
    emit('set-marker', marker);
  } else if (location.type === codeConstant.SEARCH_TYPE.END) {
    // 目的地に設定
    searchRoute.value.endLocation = displayName;
    searchRoute.value.endLat = location.lat;
    searchRoute.value.endLon = location.lon;
    // マーカー設置
    const marker = new MarkerDto({
      type: 'end',
      name: location.name,
      address: location.address,
      lat: location.lat,
      lon: location.lon
    });
    emit('set-marker', marker);
  }
})

// 交通手段オプションを取得
const getTransportOptions = () => {
  transportOptions.value = Object.entries(codeConstant.TRANSPORTATION).map(([, value]) => ({
    value: value.toString(),
    label: transportLabels[value],
    text: transportLabels[value]
  }));
}

// 出発/到着オプションを取得
const getDepartureArrivalOptions = () => {
  departureArrivalOptions.value = Object.entries(codeConstant.DEPARTURE_ARRIVAL).map(([, value]) => ({
    value: value.toString(),
    label: departureArrivalLabels[value],
    text: departureArrivalLabels[value]
  }));
}

// 条件クリア
const clearConditions = () => {
  clearError()
  searchRoute.value = new RouteRequestDto()
  hasSearched.value = false
  emit('clear-routes')
}

// 条件の表示/非表示切り替え
const toggleConditions = () => {
  showConditions.value = !showConditions.value
}

// デバウンス用タイマー
let debounceTimer: ReturnType<typeof setTimeout> | null = null

const debounceSearch = (callback: () => void, delay: number) => {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(callback, delay)
}

// 入力時の処理
const onInput = (type: string) => {
  // マーカー削除
  emit('remove-marker', type)
  // 入力値からジオコーディング
  debounceSearch(() => {
    const input = type === codeConstant.SEARCH_TYPE.START
      ? searchRoute.value.startLocation
      : searchRoute.value.endLocation
    emit('fetch-suggestions', new MarkerDto({ name: input || '', type: type, lat: null, lon: null, address: null }))
  }, 500)
}

// フォーカス時の処理
const onFocus = (type: string) => {
  // 入力値からジオコーディング
  debounceSearch(() => {
    const input = type === codeConstant.SEARCH_TYPE.START
      ? searchRoute.value.startLocation
      : searchRoute.value.endLocation
    emit('fetch-suggestions', new MarkerDto({ name: input || '', type: type, lat: null, lon: null, address: null }))
  }, 500)
}

// 出発地が選択されたときの処理
const selectStartLocation = (item: SuggestionDto) => {
  // 現在地が選択された場合は位置情報取得
  if (item.id === -1) {
    emit('clear-suggestions')
    emit('fetch-current-location', codeConstant.SEARCH_TYPE.START)
    return
  }
  if (item.lat == null || item.lon == null) return
  searchRoute.value.startLocation = item.name;
  searchRoute.value.startLat = item.lat;
  searchRoute.value.startLon = item.lon;
  // リスト初期化
  emit('clear-suggestions');
  // マーカー設置
  const marker = new MarkerDto({
    type: 'start',
    name: item.name,
    address: item.address ?? null,
    lat: item.lat,
    lon: item.lon
  });
  emit('set-marker', marker);
}

// 目的地が選択されたときの処理
const selectEndLocation = (item: SuggestionDto) => {
  // 現在地が選択された場合は位置情報取得
  if (item.id === -1) {
    emit('clear-suggestions')
    emit('fetch-current-location', codeConstant.SEARCH_TYPE.END)
    return
  }
  if (item.lat == null || item.lon == null) return
  searchRoute.value.endLocation = item.name;
  searchRoute.value.endLat = item.lat;
  searchRoute.value.endLon = item.lon;
  // リスト初期化
  emit('clear-suggestions');
  // マーカー設置
  const marker = new MarkerDto({
    type: 'end',
    name: item.name,
    address: item.address ?? null,
    lat: item.lat,
    lon: item.lon
  });
  emit('set-marker', marker);
}

/** 出発地ボタンアイコン（入力値があればクリア、なければ地図選択） */
const startButtonIcon = computed(() =>
  searchRoute.value.startLocation ? 'pi pi-times' : 'pi pi-map-marker'
)

/** 目的地ボタンアイコン（入力値があればクリア、なければ地図選択） */
const endButtonIcon = computed(() =>
  searchRoute.value.endLocation ? 'pi pi-times' : 'pi pi-map-marker'
)

/** 出発地ボタンクリック時の処理 */
const onStartButtonClick = () => {
  if (searchRoute.value.startLocation) {
    clearStartLocation();
  } else {
    emit('select-on-map', codeConstant.SEARCH_TYPE.START);
  }
}

/** 目的地ボタンクリック時の処理 */
const onEndButtonClick = () => {
  if (searchRoute.value.endLocation) {
    clearEndLocation();
  } else {
    emit('select-on-map', codeConstant.SEARCH_TYPE.END);
  }
}

// 出発地をクリア
const clearStartLocation = () => {
  searchRoute.value.startLocation = null;
  searchRoute.value.startLat = null;
  searchRoute.value.startLon = null;
  emit('remove-marker', codeConstant.SEARCH_TYPE.START);
  emit('clear-suggestions');
}

// 目的地をクリア
const clearEndLocation = () => {
  searchRoute.value.endLocation = null;
  searchRoute.value.endLat = null;
  searchRoute.value.endLon = null;
  emit('remove-marker', codeConstant.SEARCH_TYPE.END);
  emit('clear-suggestions');
}

// 外部クリック時、候補リストをクリア
const handleOutsideClick = (event: MouseEvent) => {
  const startContainer = (event.target as HTMLElement).closest('.search-input-container')
  if (!startContainer) {
    emit('clear-suggestions')
  }
}

// 経路検索
const handleSearchRoute = (route: RouteRequestDto) => {
  // エラーチェック
  const hasError = checkError()
  if (!hasError) {
    hasSearched.value = true
    emit('search-route', route)
  }
}

/**
 * エラークリア
 */
const clearError = () => {
  startLocationErrorDto.value.splice(0)
  endLocationErrorDto.value.splice(0)
}


/**
 * エラーチェック
 * @returns {boolean} エラーがある場合はtrue
 */
function checkError(): boolean {
  // エラー初期化
  clearError()
  let hasError = false

  // 出発地が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(searchRoute.value.startLocation)) {
    startLocationErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "出発地")
    )
    hasError = true
  }

  // 目的地が未入力の場合はエラー
  if (ValidateUtils.isNullOrEmpty(searchRoute.value.endLocation)) {
    endLocationErrorDto.value.push(
      MessageUtils.getMessageDto(MESSAGE_LIST, MESSAGE_NO.MSG_001, "目的地")
    )
    hasError = true
  }

  return hasError
}

</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

// 条件指定 / 閉じる
.expand-trigger {
  color: base.$text-primary;
  cursor: pointer;
  display: flex;
  justify-content: flex-end;
  padding-right: 12px;
  margin: 12px 0px;
}

// ローディング
.loading-icon {
  display: flex;
  justify-content: center;
  margin: 24px 0;
}

</style>
