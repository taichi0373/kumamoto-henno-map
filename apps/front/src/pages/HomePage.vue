<template>
  <main>
    <!-- サイドバー -->
    <div class="sidebar" id="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <template v-if="!sidebarCollapsed">
        <AppTabView :tabs="tabsForView" :modelValue="activeTabIndex" @update:modelValue="handleTabChange" />

        <!-- 経路案内ページ -->
        <div class="sidebar-page" v-show="activeTab === 'route-guidance'">
          <AppRouteGuidance
            :startSuggestions="startSuggestions"
            :endSuggestions="endSuggestions"
            :routes="routeResults"
            :isLoading="routeSearchLoading"
            :mapSelectedLocation="mapSelectedLocation"
            :activeRouteIndex="activeRouteIndex"
            @set-marker="setMarker"
            @remove-marker="removeMarker"
            @select-on-map="selectOnMap"
            @search-route="handleSearchRoute"
            @fetch-suggestions="fetchSuggestions"
            @clear-suggestions="clearSuggestions"
            @select-route="setActiveRoute"
          />
        </div>

        <!-- 利用できる特典ページ -->
        <div class="sidebar-page" v-show="activeTab === 'users-benefit'">
          <AppUsersBenefit :users-benefits="usersBenefits" />
        </div>

        <!-- 特典を探すページ -->
        <div class="sidebar-page" v-show="activeTab === 'search-benefit'">
          <AppSearchBenefit />
        </div>

        <!-- ライセンス情報 -->
        <AppLicenseInfo />
      </template>
    </div>

    <!-- サイドバー開閉ボタン -->
    <AppIconButton :icon="sidebarCollapsed ? 'pi pi-caret-right' : 'pi pi-caret-left'" severity="secondary"
      size="sidebarToggle" shape="rounded" tooltip="サイドバーの表示切替" class="sidebar-toggle-btn" @click="toggleSidebar" />

    <!-- トーストメッセージ -->
    <AppToastMessage />

    <!-- マップ -->
    <div id="map">
      <div class="map-button-container">
        <AppButton type="button" :label="storesLoading ? '読み込み中...' : '支援協賛店'"
          :tooltip="storesLoading ? '読み込み中...' : '支援協賛店'" :show-label="false" severity="primary"
          :disabled="storesLoading" :loading="storesLoading" icon="pi pi-shop" class="map-button"
          @click="toggleStoreDisplay" />
        <AppButton type="button" label="自主返納支援制度とは" tooltip="自主返納支援制度とは" :show-label="false" severity="primary"
          size="medium" icon="pi pi-info-circle" class="map-button" @click="router.push('/support_info')" />
      </div>
      <!-- クロスヘア -->
      <div v-if="showCrossHair" class="map-select-ui">
        <div class="crosshair"></div>
        <div class="map-select-controls">
          <AppButton :label="'戻る'" :primary="false" @click="clearMapSelect" />
          <AppButton :label="'選択'" :primary="true" @click="clickMapSelect" />
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppRouteGuidance from '@/components/organisms/AppRouteGuidance.vue'
import AppUsersBenefit from '@/components/organisms/AppUsersBenefit.vue'
import AppSearchBenefit from '@/components/organisms/AppSearchBenefit.vue'
import AppTabView from '@/components/atoms/AppTabView.vue'
import AppLicenseInfo from '@/components/molecules/AppLicenseInfo.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppIconButton from '@/components/atoms/AppIconButton.vue'
import AppToastMessage from '@/components/atoms/AppToastMessage.vue'
import { useMap } from '@/utils/useMap'
import { AuthUtils } from '@/utils/auth'
import apiClient from '@/utils/api'
import { createRouteMarker, type Store, type RouteMarkerType } from '@/utils/markerConfig'
import { ValidateUtils } from '@/utils/validateUtils'
import type { AxiosError } from 'axios'
import { RouteRequestDto } from '@/dto/routeRequestDto'
import { BenefitDto } from '@/dto/benefitDto'
import { MarkerDto } from '@/dto/markerDto'
import { SuggestionDto } from '@/dto/suggestionDto'
import { RouteDto } from '@/dto/routeDto'
import type { RouteInterface } from '@/dto/routeDto'
import { codeConstant } from '@/utils/codeConstant'
import { TypeConvertUtils } from '@/utils/typeConvertUtils'
import { responseStatusConstant } from '@/utils/responseStatusConstant'

/** ルーター */
const router = useRouter()

/** nominatim api url */
const NOMINATIM_API_URL = 'https://nominatim.openstreetmap.org'

/** サイドバー表示フラグ */
const sidebarCollapsed = ref(false)
/** アクティブタブ */
const activeTab = ref('route-guidance')
/** 店舗のマーカー表示フラグ */
const storeMarkersVisible = ref(false)
/** ログイン状態 */
const isLoggedIn = ref(false)

/** ユーザー特典データ */
const usersBenefits = ref<BenefitDto[]>([])

// タブ
const tabs = ref([
  { id: 'route-guidance', label: 'ルート案内' },
  { id: 'users-benefit', label: '利用できる特典' },
  { id: 'search-benefit', label: '特典を探す' },
])

const supportStores = ref<Store[]>([])
const storesLoading = ref(false)

// マップ選択モードのタイプ（'start' or 'end'）
const mapSelectMode = ref<string | null>(null)

// --- 経路案内関連ステート ---

/** 出発地の候補リスト */
const startSuggestions = ref<SuggestionDto[]>([])
/** 目的地の候補リスト */
const endSuggestions = ref<SuggestionDto[]>([])
/** 経路検索結果 */
const routeResults = ref<RouteDto[]>([])
/** 経路検索ローディング状態 */
const routeSearchLoading = ref(false)
/** マップ上から選択された地点 */
const mapSelectedLocation = ref<MarkerDto | null>(null)

/** マップ */
const { mapInstance, markerManager, activeRouteIndex, initializeMap, addStoreMarkers, removeStoreMarkers, addRouteLines, removeRouteLines, setActiveRoute, cleanup } = useMap()

// クロスヘア表示フラグ
const showCrossHair = computed(() => !ValidateUtils.isNullOrEmpty(mapSelectMode.value))

/** AppTabView用のタブ配列 */
const tabsForView = computed(() =>
  tabs.value.map(tab => ({ label: tab.label }))
)

/** アクティブタブのインデックス */
const activeTabIndex = computed(() =>
  tabs.value.findIndex(tab => tab.id === activeTab.value)
)


// 初期表示
onMounted(() => {
  // ログイン状態の確認
  checkLoginStatus()
  // マップの初期化
  const map = initializeMap('map')
  if (map) {
    map.on('load', () => console.log('Map loaded successfully'))
  }
})

/** マップ中心座標を取得 */
const getMapCenter = (): { lat: number; lon: number } | null => {
  if (!mapInstance.value) return null
  const center = mapInstance.value.getCenter()
  return { lat: center.lat, lon: center.lng }
}

/** 逆ジオコーディング */
const reverseGeocoding = async (marker: MarkerDto): Promise<MarkerDto | null> => {
  const url = `${NOMINATIM_API_URL}/reverse?format=json&lat=${marker.lat}&lon=${marker.lon}&accept-language=ja&addressdetails=1`
  const res = await fetch(url)
  if (!res.ok) return null
  const data = await res.json();
  const name = data.name || data.display_name?.split(',')[0]?.trim() || null
  const address = formatAddress(data.display_name || '')
  return new MarkerDto({ type: marker.type, name, address, lat: marker.lat, lon: marker.lon });
}

/** ジオコーディング */
const geocoding = async (marker: MarkerDto) => {
  // 未入力の場合
  if (ValidateUtils.isNullOrEmpty(marker.name)) {
    clearSuggestions();
    return
  }
  try {
    const url = `${NOMINATIM_API_URL}/search?format=json&addressdetails=1&limit=5&countrycodes=jp&q=${encodeURIComponent(marker.name + ' 熊本県')}`
    const response = await fetch(url)
    const data = await response.json()
    // 検索結果を候補リストに格納
    const suggestions = data.map((item: { name: string; display_name: string; lat: string; lon: string }, index: number) =>
      new SuggestionDto({
        id: index,
        name: item.name || formatPlace(item.display_name),
        address: formatAddress(item.display_name),
        lat: parseFloat(item.lat),
        lon: parseFloat(item.lon),
      })
    );
    // 入力欄を更新
    if (marker.type === codeConstant.SEARCH_TYPE.START) {
      startSuggestions.value = suggestions
    } else {
      endSuggestions.value = suggestions
    }
  } catch (error) {
    console.error('Nominatim location search error:', error)
  }
}

/** 表示用に地点名を変換 */
const formatPlace = (name: string) => {
  if (!name) return ''
  const parts = name.split(',')
  return parts[0].trim()
}

/** 表示用に住所を変換 */
const formatAddress = (address: string) => {
  if (!address) return ''

  const parts = address.split(',').map(part => part.trim())
  if (parts[parts.length - 1] === '日本') {
    parts.pop()
  }
  const postalCodeRegex = /^\d{3}-\d{4}$/
  let postalCodeIndex = -1
  for (let i = 0; i < parts.length; i++) {
    if (postalCodeRegex.test(parts[i])) {
      postalCodeIndex = i
      break
    }
  }

  let formattedParts: string[] = []
  if (postalCodeIndex >= 0) {
    formattedParts.push(`〒${parts[postalCodeIndex]}`)
    parts.splice(postalCodeIndex, 1)
  }
  const reversedParts = parts.reverse()
  formattedParts = formattedParts.concat(reversedParts)

  return formattedParts.join(', ')
}

/** 経路検索 */
const handleSearchRoute = async (routeRequest: RouteRequestDto) => {
  routeSearchLoading.value = true
  try {
    const response = await apiClient.post('/route/search', {
      startLocation: routeRequest.startLocation,
      startLat: routeRequest.startLat,
      startLon: routeRequest.startLon,
      endLocation: routeRequest.endLocation,
      endLat: routeRequest.endLat,
      endLon: routeRequest.endLon,
      transportMode: routeRequest.transport,
      date: routeRequest.date instanceof Date
        ? routeRequest.date.toISOString().split('T')[0]
        : '',
      time: routeRequest.time instanceof Date
        ? routeRequest.time.toTimeString().split(' ')[0].slice(0, 5)
        : '',
      arriveBy: routeRequest.departureArrival === codeConstant.DEPARTURE_ARRIVAL.ARRIVAL ? true : false,
    })
    if (response.status === responseStatusConstant.OK) {
      const routes = ((response.data as unknown) as { data: RouteInterface[] }).data || []
      // 経路探索結果（サイドバー表示用）
      routeResults.value = routes
      console.log(routeResults.value);
      // 全経路を色分けして地図に描画
      const routeLegs = routes.map(r => r.legs ?? [])
      if (routeLegs.length > 0) {
        addRouteLines(routeLegs)
      }
    } else {
      console.error('Route search failed:', response.statusText)
      alert('経路検索に失敗しました')
    }
  } catch (error) {
    console.error('Route search error:', error)
    alert('経路検索に失敗しました')
  } finally {
    routeSearchLoading.value = false
  }
}

/** ユーザー特典データを取得 */
const fetchUserBenefits = async () => {
  const userId = AuthUtils.getUser()?.id;
  // ログイン状態でない、またはユーザーIDが取得できない場合は処理しない
  if (!isLoggedIn.value || ValidateUtils.isNullOrEmpty(userId)) {
    return
  }

  try {
    const response = await apiClient.get(`benefit/users`)
    if ((response.data as { success: boolean }).success) {
      usersBenefits.value = ((response.data as unknown) as { data: BenefitDto[] }).data || []
    } else {
      console.warn('特典データの取得に失敗しました:', ((response.data as unknown) as { message: string }).message)
      usersBenefits.value = []
    }
  } catch (error: unknown) {
    console.error('特典データの取得中にエラーが発生しました:', error)
    if ((error as AxiosError).response?.status === 401) {
      AuthUtils.logout()
      isLoggedIn.value = false
      usersBenefits.value = []
    }
  }
}

/** 支援協賛店データを取得 */
const fetchSupportStores = async () => {
  storesLoading.value = true
  try {
    const response = await apiClient.get('/support-stores')
    if ((response.data as { success: boolean }).success) {
      supportStores.value = ((response.data as unknown) as { data: Store[] }).data || []
    } else {
      console.warn('協賛店データの取得に失敗しました:', ((response.data as unknown) as { message: string }).message)
      supportStores.value = []
    }
  } catch (error) {
    console.error('協賛店データの取得中にエラーが発生しました:', error)
    supportStores.value = []
  } finally {
    storesLoading.value = false
  }
}


/** サイドバーの表示/非表示切替 */
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

/** アクティブタブの設定 */
const setActiveTab = (tab: string) => {
  activeTab.value = tab
  if (tab === 'users-benefit' && isLoggedIn.value && usersBenefits.value.length === 0) {
    fetchUserBenefits()
  }
}

/** AppTabViewからのタブ変更ハンドラ */
const handleTabChange = (index: number) => {
  const tab = tabs.value[index]
  if (tab) {
    setActiveTab(tab.id)
  }
}

/** 店舗マーカーの表示/非表示切替 */
const toggleStoreDisplay = async () => {
  try {
    storeMarkersVisible.value = !storeMarkersVisible.value
    if (storeMarkersVisible.value) {
      if (supportStores.value.length === 0) {
        await fetchSupportStores()
      }
      if (supportStores.value.length > 0) {
        addStoreMarkers(supportStores.value)
      } else {
        console.warn('No store data available for markers')
      }
    } else {
      removeStoreMarkers()
    }
  } catch (error) {
    console.error('店舗マーカーの表示切替でエラーが発生しました:', error)
    storeMarkersVisible.value = false
    removeStoreMarkers()
  }
}

/** マップ：「選択」ボタン押下時 */
const clickMapSelect = async () => {
  // マップの中心座標を取得
  const center = getMapCenter()
  // 中心座標が取得できない場合
  if (ValidateUtils.isNullOrEmpty(center)) {
    return
  }
  // 逆ジオコーディングで地点情報を取得
  const result = await reverseGeocoding(new MarkerDto({ name: null, address: null, lat: TypeConvertUtils.toNumberNullorEmptyToZero(center.lat), lon: TypeConvertUtils.toNumberNullorEmptyToZero(center.lon), type: mapSelectMode.value }));
  if (!ValidateUtils.isNullOrEmpty(result)) {
    result.type = mapSelectMode.value
    mapSelectedLocation.value = result
  }
  // マップ選択モード終了
  clearMapSelect();
}

/** マップ：「戻る」ボタン押下時 */
const clearMapSelect = () => {
  mapSelectMode.value = null
}

/** マーカー設置 */
const setMarker = (marker: MarkerDto) => {
  if (!mapInstance.value) return
  markerManager.value.removeMarker(`route-${marker.type}`);
  const newMarker = createRouteMarker(marker.lat!, marker.lon!, marker.type as RouteMarkerType);
  markerManager.value.addMarker(`route-${marker.type}`, newMarker, mapInstance.value)
  mapInstance.value.flyTo({ center: [marker.lon!, marker.lat!], zoom: 16 })
}


/** マーカー削除 */
const removeMarker = (type: string) => {
  markerManager.value.removeMarker(`route-${type}`)
}

/** マップ選択モード開始 */
const selectOnMap = (type: string) => {
  mapSelectMode.value = type
}

/** 候補リストの取得（ジオコーディング） */
const fetchSuggestions = (marker: MarkerDto) => {
  geocoding(new MarkerDto({ name: marker.name, type: marker.type, lat: null, lon: null, address: null }))
}

/** 候補リストのクリア */
const clearSuggestions = () => {
  startSuggestions.value = []
  endSuggestions.value = []
}

/** ログイン状態の確認・更新 */
const checkLoginStatus = () => {
  const newLoginStatus = AuthUtils.isLoggedIn()
  if (isLoggedIn.value === newLoginStatus) return
  isLoggedIn.value = newLoginStatus
  if (isLoggedIn.value) {
    fetchUserBenefits()
  } else {
    usersBenefits.value = []
  }
}

</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

$sidebar-width: 380px;

/* メインレイアウト: サイドバー左 + マップ右 */
main {
  display: flex;
  flex: 1;
  overflow: hidden;
  position: relative;
}

/* サイドバー */
.sidebar {
  width: $sidebar-width;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  flex-shrink: 0;
  background: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  z-index: 1;

  &.collapsed {
    width: 0;
  }
}

/* サイドバー内ページ */
.sidebar-page {
  overflow-y: auto;
  flex: 1;
}

/* マップ */
#map {
  flex: 1;
  height: 100%;
  position: relative;
}

/* マップ上ボタン群 */
.map-button-container {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 1;
}

/* マップ選択UI */
.map-select-ui {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  z-index: 1;
}

.crosshair {
  width: 40px;
  height: 40px;
  position: relative;

  &::before,
  &::after {
    content: '';
    position: absolute;
    background: #333;
  }

  &::before {
    width: 2px;
    height: 100%;
    left: 50%;
    transform: translateX(-50%);
  }

  &::after {
    height: 2px;
    width: 100%;
    top: 50%;
    transform: translateY(-50%);
  }
}

.map-select-controls {
  pointer-events: all;
  display: flex;
  gap: 8px;
  margin-top: 16px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}
</style>
