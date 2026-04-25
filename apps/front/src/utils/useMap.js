import { ref } from 'vue'
import { createMapInstance, setupMapLibre, setupMapControls, setupMapClickEvents } from '../utils/mapConfig'
import { createStoreMarker, MarkerManager } from '../utils/markerConfig'

export const useMap = () => {
  const mapInstance = ref(null)
  const storeMarkers = ref([])
  const markerManager = ref(new MarkerManager())

  const initializeMap = (containerId, config = {}, clickEventOptions = null) => {
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

  const addStoreMarkers = (stores) => {
    if (!mapInstance.value || !stores?.length) return

    // 既存マーカーを削除
    removeStoreMarkers()

    stores.forEach((store, index) => {
      const marker = createStoreMarker(store)
      if (marker) {
        marker.addTo(mapInstance.value)
        storeMarkers.value.push(marker)
        // マーカー管理クラスにも登録
        markerManager.value.addMarker(`store-${index}`, marker, mapInstance.value)
      }
    })
  }

  const removeStoreMarkers = () => {
    storeMarkers.value.forEach(marker => marker.remove())
    storeMarkers.value = []
    // マーカー管理クラスからも削除
    markerManager.value.removeMarkersByType('store')
  }

  const cleanup = () => {
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