<template>
  <main>
    <!-- Sidebar -->
    <div class="sidebar" id="sidebar" :class="{ collapsed: sidebarCollapsed }">
      <template v-if="!sidebarCollapsed">
        <SidebarTab :activeTab="activeTab" @setActiveTab="setActiveTab" />

        <!-- 経路案内ページ -->
        <div class="sidebar-page" v-show="activeTab === 'route-guidance'">
          <RouteGuidance @set-marker="handleSetMarker" @select-on-map="handleSelectOnMap" />
        </div>

        <!-- 利用できる特典ページ -->
        <div class="sidebar-page" v-show="activeTab === 'users-benefit'">
          <UsersBenefit :user-benefits="userBenefits" :loading="userBenefitsLoading" />
        </div>

        <!-- 特典を探すページ -->
        <div class="sidebar-page" v-show="activeTab === 'search-benefit'">
          <SearchBenefit />
        </div>

        <!-- ライセンス情報 -->
        <LicenseInfo />

      </template>
    </div>

    <!-- Sidebar Toggle Button -->
    <BaseIconButton :icon="sidebarCollapsed ? 'fa-solid fa-caret-right' : 'fa-solid fa-caret-left'" variant="secondary"
      size="sidebarToggle" shape="rounded" tooltip="サイドバーの表示切替" class="sidebar-toggle-btn" @click="toggleSidebar" />

    <!-- Map -->
    <div id="map">
      <div class="map-button-container">
        <BaseButton type="button" :label="storesLoading ? '読み込み中...' : '支援協賛店'"
          :tooltip="storesLoading ? '読み込み中...' : '支援協賛店'" :show-label="false" :primary="true"
          :disabled="storesLoading" icon="fa-solid fa-shop" class="map-button" @click="toggleStoreDisplay" />

        <BaseButton type="button" label="自主返納支援制度とは" tooltip="自主返納支援制度とは" :show-label="false" :primary="true"
          size="medium" icon="fa-solid fa-info-circle" class="map-button" @click="$router.push('/support_info')" />
      </div>
      <div v-if="showMapSelectUI" class="map-select-ui">
        <div class="crosshair"></div>
        <div class="map-select-controls">
          <BaseButton type="button" label="戻る" @click="handleMapSelectCancel" size="small" :primary="false" />
          <BaseButton type="button" label="選択" @click="handleMapSelectConfirm" size="small" :primary="true" />
        </div>
      </div>
    </div>
  </main>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import RouteGuidance from '../components/organisms/RouteGuidance.vue'
import UsersBenefit from '../components/organisms/UsersBenefit.vue'
import SearchBenefit from '../components/organisms/SearchBenefit.vue'
import LicenseInfo from '../components/molecules/LicenseInfo.vue'
import SidebarTab from '../components/molecules/SidebarTab.vue'
import BaseButton from '../components/atoms/Button.vue'
import BaseIconButton from '../components/atoms/IconButton.vue'
import { useMap } from '../utils/useMap'
import { AuthUtils } from '../utils/auth'
import apiClient from '../utils/api'
import { createRouteMarker } from '../utils/markerConfig'

export default {
  name: 'HomePage',
  components: {
    RouteGuidance,
    UsersBenefit,
    SearchBenefit,
    LicenseInfo,
    SidebarTab,
    BaseButton,
    BaseIconButton,
  },
  setup() {
    const sidebarCollapsed = ref(false)
    const activeTab = ref('route-guidance')
    const storeMarkersVisible = ref(false)
    
    // 状態管理
    const isLoggedIn = ref(false)
    const userBenefits = ref([])
    const userBenefitsLoading = ref(false)
    const supportStores = ref([])
    const storesLoading = ref(false)

    // マップ機能を使用
    const { mapInstance, markerManager, initializeMap, addStoreMarkers, removeStoreMarkers, cleanup } = useMap()

    // マップで選択モード
    const mapSelectMode = ref(null) // 'start' or 'end' or null

    // マップ選択UI表示用
    const showMapSelectUI = computed(() => !!mapSelectMode.value);

    // マップ中心取得
    const getMapCenter = () => {
      if (mapInstance.value) {
        const center = mapInstance.value.getCenter();
        return { lat: center.lat, lon: center.lng };
      }
      return { lat: 0, lon: 0 };
    };

    // 選択ボタン押下時の処理
    const handleMapSelectConfirm = async () => {
      const { lat, lon } = getMapCenter();
      const result = await reverseGeocode(lat, lon);
      if (result) {
        const event = new CustomEvent('map-selected-location', { detail: { type: mapSelectMode.value, ...result } });
        window.dispatchEvent(event);
        mapSelectMode.value = null;
      }
    };

    // 戻るボタン押下時
    const handleMapSelectCancel = () => {
      mapSelectMode.value = null;
    };

    // ユーザー特典データを取得（Kong Gateway経由）
    const fetchUserBenefits = async () => {
      if (!isLoggedIn.value) return
      const userId = AuthUtils.getUserId()
      if (!userId) {
        console.warn('User ID not found')
        return
      }

      userBenefitsLoading.value = true
      try {
        const response = await apiClient.get(`benefit/users/${userId}`)

        if (response.data.success) {
          userBenefits.value = response.data.benefits || []
          console.log('User benefits loaded:', userBenefits.value.length)
        } else {
          console.warn('特典データの取得に失敗しました:', response.data.message)
          userBenefits.value = []
        }
      } catch (error) {
        console.error('特典データの取得中にエラーが発生しました:', error)
        if (error.response?.status === 401) {
          // 認証エラーの場合はログアウト処理
          AuthUtils.logout()
          isLoggedIn.value = false
          userBenefits.value = []
        }
      } finally {
        userBenefitsLoading.value = false
      }
    }

    // 支援協賛店データを取得（Kong Gateway経由）
    const fetchSupportStores = async () => {
      storesLoading.value = true
      try {
        const response = await apiClient.get('/support-stores')

        if (response.data.success) {
          supportStores.value = response.data.stores || []
          console.log('Support stores loaded:', supportStores.value.length)
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

    // サイドバーの表示/非表示
    const toggleSidebar = () => {
      sidebarCollapsed.value = !sidebarCollapsed.value
    }

    // アクティブなタブの設定
    const setActiveTab = (tab) => {
      activeTab.value = tab
      
      // タブが切り替わったときに特典データを再取得
      if (tab === 'users-benefit' && isLoggedIn.value && userBenefits.value.length === 0) {
        fetchUserBenefits()
      }
    }

    // 店舗マーカーの表示/非表示切替
    const toggleStoreDisplay = async () => {
      try {
        storeMarkersVisible.value = !storeMarkersVisible.value

        if (storeMarkersVisible.value) {
          // 店舗データを取得してマーカーを表示
          if (supportStores.value.length === 0) {
            await fetchSupportStores()
          }
          
          if (supportStores.value && supportStores.value.length > 0) {
            addStoreMarkers(supportStores.value)
            console.log('Store markers added to map')
          } else {
            console.warn('No store data available for markers')
          }
        } else {
          // マーカーを非表示
          removeStoreMarkers()
          console.log('Store markers removed from map')
        }
      } catch (error) {
        console.error('店舗マーカーの表示切替でエラーが発生しました:', error)
        storeMarkersVisible.value = false
        removeStoreMarkers()
      }
    }

    // 特典へのスクロール
    const scrollToBenefit = (benefitID) => {
      setActiveTab('users-benefit')
      // nextTickの代替
      setTimeout(() => {
        const targetBenefit = document.querySelector(`[data-benefit-id="${benefitID}"]`)
        if (targetBenefit) {
          targetBenefit.scrollIntoView({
            behavior: 'smooth',
            block: 'center'
          })
          // ハイライト効果
          targetBenefit.style.border = '2px solid #007bff'
          targetBenefit.style.backgroundColor = '#f0f8ff'
          setTimeout(() => {
            targetBenefit.style.border = ''
            targetBenefit.style.backgroundColor = ''
          }, 2000)
        }
      }, 50)
    }

    // ログイン状態の確認
    const checkLoginStatus = () => {
      const newLoginStatus = AuthUtils.isLoggedIn()
      if (isLoggedIn.value !== newLoginStatus) {
        isLoggedIn.value = newLoginStatus
        console.log('Login status changed:', isLoggedIn.value)
        
        if (isLoggedIn.value) {
          // ログインした場合は特典データを取得
          fetchUserBenefits()
        } else {
          // ログアウトした場合はデータをクリア
          userBenefits.value = []
        }
      }
    }

    // 認証状態変更イベントのハンドラ
    const handleAuthStatusChange = (event) => {
      console.log('Auth status change event received:', event.detail)
      checkLoginStatus()
    }

    // 経路案内のマーカー設置
    const handleSetMarker = ({ type, lat, lon, address }) => {
      if (!mapInstance.value) return
      // 既存の同種マーカーを消す
      markerManager.value.removeMarker(`route-${type}`)
      // マーカー作成
      const marker = createRouteMarker(lat, lon, type)
      markerManager.value.addMarker(`route-${type}`, marker, mapInstance.value)
      // マップをその地点に移動
      mapInstance.value.flyTo({ center: [lon, lat], zoom: 16 })
    }

    // 逆ジオコーディングAPI
    const reverseGeocode = async (lat, lon) => {
      const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&accept-language=ja&addressdetails=1`;
      const res = await fetch(url, { headers: { 'User-Agent': 'navigation_app/1.0' } });
      if (!res.ok) return null;
      const data = await res.json();
      return {
        name: data.display_name || '',
        lat,
        lon,
        address: data.address || {},
      };
    };

    // RouteGuidanceからのマップ選択開始イベント
    const handleSelectOnMap = (type) => {
      mapSelectMode.value = type;
      // UI的に何かあればここで対応
    };

    // マップクリックで逆ジオコーディング＆RouteGuidanceへemit
    onMounted(() => {
      // ログイン状態を確認
      checkLoginStatus()
      
      // ログインしている場合は特典データを取得
      if (isLoggedIn.value) {
        fetchUserBenefits()
      }

      // マップ初期化
      const map = initializeMap('map')

      if (map) {
        map.on('load', () => {
          console.log('Map loaded successfully')
        })
      }

      // イベントリスナーを追加
      window.addEventListener('storage', checkLoginStatus)
      window.addEventListener('auth-status-changed', handleAuthStatusChange)

      if (mapInstance.value) {
        mapInstance.value.on('click', async (e) => {
          if (!mapSelectMode.value) return;
          const lat = e.lngLat.lat;
          const lon = e.lngLat.lng;
          const result = await reverseGeocode(lat, lon);
          if (result) {
            console.log(`Selected ${mapSelectMode.value} location:`, result);
            // RouteGuidanceに値を渡す
            const event = new CustomEvent('map-selected-location', { detail: { type: mapSelectMode.value, ...result } });
            window.dispatchEvent(event);
            mapSelectMode.value = null;
          }
        });
      }
    })

    onUnmounted(() => {
      // クリーンアップ
      cleanup()
      window.removeEventListener('storage', checkLoginStatus)
      window.removeEventListener('auth-status-changed', handleAuthStatusChange)
    })

    return {
      sidebarCollapsed,
      activeTab,
      storeMarkersVisible,
      mapInstance,
      isLoggedIn,
      userBenefits,
      userBenefitsLoading,
      supportStores,
      storesLoading,
      toggleSidebar,
      setActiveTab,
      toggleStoreDisplay,
      scrollToBenefit,
      addStoreMarkers,
      removeStoreMarkers,
      handleSetMarker,
      handleSelectOnMap,
      showMapSelectUI,
      handleMapSelectConfirm,
      handleMapSelectCancel,
    }
  }
}
</script>

<style scoped>
.sidebar {
  width: 400px;
  min-width: 400px;
  background: linear-gradient(180deg, #ffffff 0%, #f8f8f8 100%);
  border-right: 2px solid #ddd;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  z-index: 2;
}

.sidebar.collapsed {
  width: 0;
  min-width: 0;
  border-right: none;
  box-shadow: none;
}

.sidebar-tabs {
  display: flex;
  background: linear-gradient(90deg, #333333 0%, #555555 100%);
  border-bottom: 2px solid #ddd;
}

.sidebar-page {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #ffffff;
}

.sidebar-page form {
  gap: 0;
}

#map {
  flex: 1;
  position: relative;
  background: #f0f0f0;
}

.sidebar-toggle-btn {
  position: absolute;
  top: 50%;
  left: 400px;
  transform: translateY(-50%);
  z-index: 1;
  transition: left 0.3s ease;
}

.sidebar.collapsed+.sidebar-toggle-btn {
  left: 0px;
}

.map-button-container {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.map-button {
  width: 40px;
  height: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  font-weight: 600;
  padding: 12px;
  border-radius: 50%;
}

.map-select-ui {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 10;
}

.crosshair {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 34px;
  height: 34px;
  margin-left: -20px;
  margin-top: -20px;
  pointer-events: none;
  z-index: 11;
}
.crosshair::before, .crosshair::after {
  content: '';
  position: absolute;
  background: #000000;
  border-radius: 2px;
}
.crosshair::before {
  left: 50%;
  top: 0;
  width: 3px;
  height: 34px;
  margin-left: -2px;
}
.crosshair::after {
  top: 50%;
  left: 0;
  width: 34px;
  height: 3px;
  margin-top: -2px;
}

.map-select-controls {
  position: absolute;
  top: 35%;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
  z-index: 12;
  pointer-events: auto;
}
</style>