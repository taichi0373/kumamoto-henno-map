<template>
  <main :class="{ 'sidebar-collapsed': sidebarCollapsed }">
    <!-- サイドバー -->
    <div class="sidebar" id="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <template v-if="!sidebarCollapsed">
        <AppTabView 
          :tabs="tabsForView" 
          :modelValue="activeTabIndex" 
          @update:modelValue="handleTabChange" 
        />

        <!-- 経路案内ページ -->
        <div class="sidebar-page" v-show="activeTab === 'route-guidance'">
          <AppRouteGuidance @set-marker="handleSetMarker" @select-on-map="handleSelectOnMap" />
        </div>

        <!-- 利用できる特典ページ -->
        <div class="sidebar-page" v-show="activeTab === 'users-benefit'">
          <AppUsersBenefit :user-benefits="userBenefits" :loading="userBenefitsLoading" />
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
    <AppIconButton
      :icon="sidebarCollapsed ? 'pi pi-caret-right' : 'pi pi-caret-left'"
      severity="secondary"
      size="sidebarToggle"
      shape="rounded"
      tooltip="サイドバーの表示切替"
      class="sidebar-toggle-btn"
      @click="toggleSidebar"
    />

    <!-- マップ -->
    <div id="map">
      <div class="map-button-container">
        <AppButton
          type="button"
          :label="storesLoading ? '読み込み中...' : '支援協賛店'"
          :tooltip="storesLoading ? '読み込み中...' : '支援協賛店'"
          :show-label="false"
          severity="primary"
          :disabled="storesLoading"
          :loading="storesLoading"
          icon="pi pi-shop"
          class="map-button"
          @click="toggleStoreDisplay"
        />
        <AppButton
          type="button"
          label="自主返納支援制度とは"
          tooltip="自主返納支援制度とは"
          :show-label="false"
          severity="primary"
          size="medium"
          icon="pi pi-info-circle"
          class="map-button"
          @click="router.push('/support_info')"
        />
      </div>
      <div v-if="showMapSelectUI" class="map-select-ui">
        <div class="crosshair"></div>
        <div class="map-select-controls">
          <AppButton type="button" label="戻る" size="small" severity="secondary" @click="handleMapSelectCancel" />
          <AppButton type="button" label="選択" size="small" severity="primary" @click="handleMapSelectConfirm" />
        </div>
      </div>
    </div>
  </main>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import AppRouteGuidance from '@/components/organisms/AppRouteGuidance.vue'
import AppUsersBenefit from '@/components/organisms/AppUsersBenefit.vue'
import AppSearchBenefit from '@/components/organisms/AppSearchBenefit.vue'
import AppTabView from '@/components/atoms/AppTabView.vue'
import AppLicenseInfo from '@/components/molecules/AppLicenseInfo.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppIconButton from '@/components/atoms/AppIconButton.vue'
import { useMap } from '@/utils/useMap'
import { AuthUtils } from '@/utils/auth'
import apiClient from '@/utils/api'
import { createRouteMarker } from '@/utils/markerConfig'

export default defineComponent({
  name: 'HomePage',
  components: {
    AppRouteGuidance,
    AppUsersBenefit,
    AppSearchBenefit,
    AppTabView,
    AppLicenseInfo,
    AppButton,
    AppIconButton,
  },
  setup() {
    // --- ルーター ---
    const router = useRouter()

    // --- 状態 ---
    const sidebarCollapsed = ref(false)
    const activeTab = ref('route-guidance')
    const storeMarkersVisible = ref(false)
    const isLoggedIn = ref(false)
    const userBenefits = ref<any[]>([])
    const tabs = ref([
      { id: 'route-guidance', label: 'ルート案内' },
      { id: 'users-benefit', label: '利用できる特典' },
      { id: 'search-benefit', label: '特典を探す' },
    ])
    const userBenefitsLoading = ref(false)
    const supportStores = ref<any[]>([])
    const storesLoading = ref(false)
    const mapSelectMode = ref<string | null>(null)

    // --- マップ ---
    const { mapInstance, markerManager, initializeMap, addStoreMarkers, removeStoreMarkers, cleanup } = useMap()

    // --- computed ---
    const showMapSelectUI = computed(() => !!mapSelectMode.value)
    
    /** AppTabView用のタブ配列 */
    const tabsForView = computed(() => 
      tabs.value.map(tab => ({ label: tab.label }))
    )
    
    /** アクティブタブのインデックス */
    const activeTabIndex = computed(() => 
      tabs.value.findIndex(tab => tab.id === activeTab.value)
    )

    // --- ヘルパー ---

    /** マップ中心座標を取得 */
    const getMapCenter = () => {
      if (mapInstance.value) {
        const center = mapInstance.value.getCenter()
        return { lat: center.lat, lon: center.lng }
      }
      return { lat: 0, lon: 0 }
    }

    /** 逆ジオコーディング */
    const reverseGeocode = async (lat: number, lon: number) => {
      const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&accept-language=ja&addressdetails=1`
      const res = await fetch(url, { headers: { 'User-Agent': 'benefit_map/1.0' } })
      if (!res.ok) return null
      const data = await res.json()
      return {
        name: data.display_name || '',
        lat,
        lon,
        address: data.address || {},
      }
    }

    // --- API ---

    /** ユーザー特典データを取得 */
    const fetchUserBenefits = async () => {
      if (!isLoggedIn.value) return
      const userId = AuthUtils.getUser()?.id
      if (!userId) {
        console.warn('User ID not found')
        return
      }
      userBenefitsLoading.value = true
      try {
        const response = await apiClient.get(`benefit/users/${userId}`)
        if (response.data.success) {
          userBenefits.value = response.data.benefits || []
        } else {
          console.warn('特典データの取得に失敗しました:', response.data.message)
          userBenefits.value = []
        }
      } catch (error: any) {
        console.error('特典データの取得中にエラーが発生しました:', error)
        if (error.response?.status === 401) {
          AuthUtils.logout()
          isLoggedIn.value = false
          userBenefits.value = []
        }
      } finally {
        userBenefitsLoading.value = false
      }
    }

    /** 支援協賛店データを取得 */
    const fetchSupportStores = async () => {
      storesLoading.value = true
      try {
        const response = await apiClient.get('/support-stores')
        if (response.data.success) {
          supportStores.value = response.data.stores || []
        } else {
          console.warn('協賛店データの取得に失敗しました:', response.data.message)
          supportStores.value = []
        }
      } catch (error) {
        console.error('協賛店データの取得中にエラーが発生しました:', error)
        supportStores.value = []
      } finally {
        storesLoading.value = false
      }
    }

    // --- イベントハンドラ ---

    /** サイドバーの表示/非表示切替 */
    const toggleSidebar = () => {
      sidebarCollapsed.value = !sidebarCollapsed.value
    }

    /** アクティブタブの設定 */
    const setActiveTab = (tab: string) => {
      activeTab.value = tab
      if (tab === 'users-benefit' && isLoggedIn.value && userBenefits.value.length === 0) {
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

    /** マップ選択確定 */
    const handleMapSelectConfirm = async () => {
      const { lat, lon } = getMapCenter()
      const result = await reverseGeocode(lat, lon)
      if (result) {
        window.dispatchEvent(new CustomEvent('map-selected-location', { detail: { type: mapSelectMode.value, ...result } }))
        mapSelectMode.value = null
      }
    }

    /** マップ選択キャンセル */
    const handleMapSelectCancel = () => {
      mapSelectMode.value = null
    }

    /** 経路案内マーカーの設置 */
    const handleSetMarker = ({ type, lat, lon }: { type: string; lat: number; lon: number; address: string }) => {
      if (!mapInstance.value) return
      markerManager.value.removeMarker(`route-${type}`)
      const marker = createRouteMarker(lat, lon, type as any)
      markerManager.value.addMarker(`route-${type}`, marker, mapInstance.value)
      mapInstance.value.flyTo({ center: [lon, lat], zoom: 16 })
    }

    /** マップ選択モード開始 */
    const handleSelectOnMap = (type: string) => {
      mapSelectMode.value = type
    }

    /** ログイン状態の確認・更新 */
    const checkLoginStatus = () => {
      const newLoginStatus = AuthUtils.isLoggedIn()
      if (isLoggedIn.value === newLoginStatus) return
      isLoggedIn.value = newLoginStatus
      if (isLoggedIn.value) {
        fetchUserBenefits()
      } else {
        userBenefits.value = []
      }
    }

    // --- ライフサイクル ---

    onMounted(() => {
      checkLoginStatus()

      const map = initializeMap('map')
      if (map) {
        map.on('load', () => console.log('Map loaded successfully'))
        map.on('click', async (e) => {
          if (!mapSelectMode.value) return
          const { lat, lng: lon } = e.lngLat
          const result = await reverseGeocode(lat, lon)
          if (result) {
            window.dispatchEvent(new CustomEvent('map-selected-location', { detail: { type: mapSelectMode.value, ...result } }))
            mapSelectMode.value = null
          }
        })
      }

      window.addEventListener('storage', checkLoginStatus)
      window.addEventListener('auth-status-changed', checkLoginStatus)
    })

    onUnmounted(() => {
      cleanup()
      window.removeEventListener('storage', checkLoginStatus)
      window.removeEventListener('auth-status-changed', checkLoginStatus)
    })

    return {
      tabs,
      tabsForView,
      activeTabIndex,
      router,
      sidebarCollapsed,
      activeTab,
      storeMarkersVisible,
      isLoggedIn,
      userBenefits,
      userBenefitsLoading,
      supportStores,
      storesLoading,
      mapSelectMode,
      showMapSelectUI,
      toggleSidebar,
      setActiveTab,
      handleTabChange,
      toggleStoreDisplay,
      handleMapSelectConfirm,
      handleMapSelectCancel,
      handleSetMarker,
      handleSelectOnMap,
    }
  },
})
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
