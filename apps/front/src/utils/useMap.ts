import { ref, type Ref } from 'vue'
import type { Map, Marker } from 'maplibre-gl'
import { 
  createMapInstance, 
  setupMapLibre, 
  setupMapControls, 
  setupMapClickEvents,
  type MapConfig,
  type MapClickOptions
} from '../utils/mapConfig'
import { createStoreMarker, MarkerManager, type Store } from '../utils/markerConfig'

export interface UseMapReturn {
  mapInstance: Ref<Map | null>
  storeMarkers: Ref<Marker[]>
  markerManager: any
  initializeMap: (containerId: string, config?: Partial<MapConfig>, clickEventOptions?: MapClickOptions | null) => Map
  addStoreMarkers: (stores: Store[]) => void
  removeStoreMarkers: () => void
  cleanup: () => void
}

export const useMap = (): UseMapReturn => {
  const mapInstance: Ref<Map | null> = ref(null)
  const storeMarkers: Ref<Marker[]> = ref([])
  const markerManager = ref(new MarkerManager())

  const initializeMap = (
    containerId: string, 
    config: Partial<MapConfig> = {}, 
    clickEventOptions: MapClickOptions | null = null
  ): Map => {
    setupMapLibre()
    const map = createMapInstance(containerId, config)
    mapInstance.value = map
    
    // 基本的なマップコントロールを設定
    setupMapControls(map)
    
    // クリックイベントが必要な場合は設定
    if (clickEventOptions) {
      setupMapClickEvents(map, clickEventOptions)
    }
    
    return map
  }

  const addStoreMarkers = (stores: Store[]): void => {
    if (!mapInstance.value || !stores?.length) return

    // 既存マーカーを削除
    removeStoreMarkers()

    stores.forEach((store, index) => {
      const marker = createStoreMarker(store)
      if (marker && mapInstance.value) {
        marker.addTo(mapInstance.value)
        storeMarkers.value.push(marker)
        // マーカー管理クラスにも登録
        markerManager.value.addMarker(`store-${index}`, marker, mapInstance.value)
      }
    })
  }

  const removeStoreMarkers = (): void => {
    storeMarkers.value.forEach(marker => marker.remove())
    storeMarkers.value = []
    // マーカー管理クラスからも削除
    markerManager.value.removeMarkersByType('store')
  }

  const cleanup = (): void => {
    if (mapInstance.value) {
      markerManager.value.clearAllMarkers()
      mapInstance.value.remove()
      mapInstance.value = null
    }
  }

  return {
    mapInstance,
    storeMarkers,
    markerManager,
    initializeMap,
    addStoreMarkers,
    removeStoreMarkers,
    cleanup
  }
}