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

/** ルート描画に必要な区間の最小型 */
export interface RouteLeg {
  mode?: string | null
  legGeometry?: { points?: string | null } | null
}

export interface UseMapReturn {
  mapInstance: Ref<Map | null>
  storeMarkers: Ref<Marker[]>
  markerManager: any
  initializeMap: (containerId: string, config?: Partial<MapConfig>, clickEventOptions?: MapClickOptions | null) => Map
  addStoreMarkers: (stores: Store[]) => void
  removeStoreMarkers: () => void
  addRouteLines: (routes: RouteLeg[][]) => void
  removeRouteLines: () => void
  cleanup: () => void
}

/** 経路番号別カラー（1件目・2件目・3件目） */
const ROUTE_COLORS = ['#2196F3', '#FF9800', '#4CAF50']

/** Googleエンコードポリラインをデコードして[lon, lat]座標配列を返す */
const decodePolyline = (encoded: string): [number, number][] => {
  const result: [number, number][] = []
  let index = 0
  let lat = 0
  let lng = 0
  while (index < encoded.length) {
    let shift = 0; let b = 0; let byte: number
    do { byte = encoded.charCodeAt(index++) - 63; b |= (byte & 0x1f) << shift; shift += 5 } while (byte >= 0x20)
    lat += (b & 1) !== 0 ? ~(b >> 1) : b >> 1
    shift = 0; b = 0
    do { byte = encoded.charCodeAt(index++) - 63; b |= (byte & 0x1f) << shift; shift += 5 } while (byte >= 0x20)
    lng += (b & 1) !== 0 ? ~(b >> 1) : b >> 1
    result.push([lng / 1e5, lat / 1e5])
  }
  return result
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

  /** ルートラインを地図に追加（経路ごとに色分けして全件表示） */
  const addRouteLines = (routes: RouteLeg[][]): void => {
    const map = mapInstance.value
    if (!map) return
    removeRouteLines()

    console.log('[addRouteLines] routes:', routes.length)
    routes.slice(0, ROUTE_COLORS.length).forEach((legs, routeIndex) => {
      const features = legs
        .filter(leg => leg.legGeometry?.points)
        .map(leg => ({
          type: 'Feature' as const,
          properties: {},
          geometry: {
            type: 'LineString' as const,
            coordinates: decodePolyline(leg.legGeometry!.points!)
          }
        }))

      console.log(`[addRouteLines] route ${routeIndex}: ${legs.length} legs, ${features.length} features`)
      if (features.length === 0) return

      const sourceId = `route-source-${routeIndex}`
      const layerId = `route-layer-${routeIndex}`
      try {
        map.addSource(sourceId, {
          type: 'geojson',
          data: { type: 'FeatureCollection', features }
        })
        map.addLayer({
          id: layerId,
          type: 'line',
          source: sourceId,
          layout: { 'line-join': 'round', 'line-cap': 'round' },
          paint: {
            'line-color': ROUTE_COLORS[routeIndex],
            'line-width': 5,
            'line-opacity': 0.85
          }
        })
        console.log(`[addRouteLines] layer ${layerId} added`)
      } catch (e) {
        console.error(`[addRouteLines] layer ${layerId} 追加エラー:`, e)
      }
    })
  }

  /** ルートラインを地図から削除 */
  const removeRouteLines = (): void => {
    const map = mapInstance.value
    if (!map) return
    ROUTE_COLORS.forEach((_, i) => {
      const layerId = `route-layer-${i}`
      const sourceId = `route-source-${i}`
      try {
        if (map.getLayer(layerId)) map.removeLayer(layerId)
        if (map.getSource(sourceId)) map.removeSource(sourceId)
      } catch (e) {
        console.error(`[removeRouteLines] ${i} 削除エラー:`, e)
      }
    })
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
    addRouteLines,
    removeRouteLines,
    cleanup
  }
}