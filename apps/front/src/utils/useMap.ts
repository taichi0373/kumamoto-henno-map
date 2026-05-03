import { ref, shallowRef, type Ref } from 'vue'
import maplibregl, { type Map } from 'maplibre-gl'
import {
  createMapInstance,
  setupMapControls,
  setupMapClickEvents,
  type MapConfig,
  type MapClickOptions
} from '../utils/mapConfig'
import { MarkerManager } from '../utils/markerConfig'
import type { RouteLeg } from '../dto/routeDto'


/** バス車両位置情報インターフェース */
export interface VehiclePosition {
  vehicleId: string
  lat: number
  lon: number
  agencyName?: string
  routeId?: string
  /** ポップアップ用: 出発地名 */
  from?: string
  /** ポップアップ用: 目的地名 */
  to?: string
  /** ポップアップ用: 出発時刻 */
  startTime?: string
  /** ポップアップ用: 到着時刻 */
  endTime?: string
}

export interface UseMapReturn {
  /* マップインスタンス */
  mapInstance: Ref<Map | null>
  /* マーカー管理クラスのインスタンス */
  markerManager: Ref<MarkerManager>
  /* アクティブな経路インデックス */
  activeRouteIndex: Ref<number>
  /* マップ初期化関数 */
  initializeMap: (containerId: string, config?: Partial<MapConfig>, clickEventOptions?: MapClickOptions | null) => Map
  /* ルートライン追加関数 */
  addRouteLines: (routes: RouteLeg[][]) => void
  /* ルートライン削除関数 */
  removeRouteLines: (layerId: string) => void
  /* アクティブ経路を切り替える関数 */
  setActiveRoute: (routeIndex: number) => void
  /* バスマーカーを更新する関数 */
  updateBusMarkers: (vehicles: VehiclePosition[]) => void
  /* バスマーカーを全削除する関数 */
  clearBusMarkers: () => void
  /* クリーンアップ関数 */
  cleanup: () => void
}

/** 経路番号別アクティブカラー */
export const ROUTE_ACTIVE_COLORS = ['#1A74FD', '#1A74FD', '#1A74FD']
/** 非アクティブ経路のカラー */
const ROUTE_INACTIVE_COLOR = '#757575'

/** ポリラインデコード関数 */
const decodePolyline = (encoded: string): [number, number][] => {
  let points: [number, number][] = [];
  let index = 0, len = encoded.length;
  let lat = 0, lng = 0;

  while (index < len) {
    let b: number, shift: number = 0, result: number = 0;
    do {
      b = encoded.charCodeAt(index++) - 63;
      result |= (b & 0x1f) << shift;
      shift += 5;
    } while (b >= 0x20);
    let dlat = ((result & 1) ? ~(result >> 1) : (result >> 1));
    lat += dlat;

    shift = 0;
    result = 0;
    do {
      b = encoded.charCodeAt(index++) - 63;
      result |= (b & 0x1f) << shift;
      shift += 5;
    } while (b >= 0x20);
    let dlng = ((result & 1) ? ~(result >> 1) : (result >> 1));
    lng += dlng;

    points.push([lat / 1e5, lng / 1e5]);
  }
  return points;
}

export const useMap = (): UseMapReturn => {
  const mapInstance: Ref<Map | null> = ref(null)
  const markerManager = shallowRef(new MarkerManager())
  const activeRouteIndex: Ref<number> = ref(0)
  /** 経路インデックスとレイヤーIDのマッピング */
  const routeLayerMap: Ref<Record<number, string[]>> = ref({})
  /** レイヤーIDごとのクリックハンドラ参照（map.off で解除するために保持） */
  const layerClickHandlers: Ref<Record<string, (e: maplibregl.MapMouseEvent & { features?: maplibregl.MapGeoJSONFeature[] }) => void>> = ref({})

  const initializeMap = (
    containerId: string,
    config: Partial<MapConfig> = {},
    clickEventOptions: MapClickOptions | null = null
  ): Map => {
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

  /** ルートラインを地図に追加 */
  const addRouteLines = (routes: RouteLeg[][]): void => {
    const map = mapInstance.value
    if (!map) return

    let allCoordinates: [number, number][] = []
    // 既存レイヤーのクリックハンドラをすべて解除してからリセット
    Object.keys(layerClickHandlers.value).forEach(layerId => removeRouteLines(layerId))
    routeLayerMap.value = {}
    activeRouteIndex.value = 0

    routes.forEach((routeLegs, routeIndex) => {
      routeLayerMap.value[routeIndex] = []
      routeLegs.forEach((leg, legIndex) => {
        if (leg.legGeometry?.points) {
          const latlngs = decodePolyline(leg.legGeometry.points)
          if (latlngs.length > 0) {
            const coordinates = latlngs.map(
              (point) => [point[1], point[0]] as [number, number]
            )
            coordinates.forEach((coord) => {
              allCoordinates.push(coord)
            })
            const layerId = addRouteLayer(routeIndex, legIndex, coordinates)
            routeLayerMap.value[routeIndex].push(layerId)
          }
        }
      })
    })
    // すべての経路の座標を含むバウンディングボックスを計算してマップの表示範囲を調整
    if (allCoordinates.length > 0) {
      const bounds = new maplibregl.LngLatBounds()
      allCoordinates.forEach((coord) => bounds.extend(coord))
      map.fitBounds(bounds, { padding: 50 })
    }

    // 1件目の経路レイヤーを最前面に移動
    const firstLayerIds = routeLayerMap.value[0] || []
    firstLayerIds.forEach(id => {
      if (map.getLayer(id)) map.moveLayer(id)
    })

    // 全経路にクリックイベントを登録（選択中の経路を切り替え）
    routes.forEach((_, routeIndex) => {
      const layerIds = routeLayerMap.value[routeIndex] || []
      layerIds.forEach(layerId => {
        const handler = () => { setActiveRoute(routeIndex) }
        layerClickHandlers.value[layerId] = handler
        map.on('click', layerId, handler)
      })
    })
  }

  /** アクティブ経路を切り替え、地図上の色を更新する */
  const setActiveRoute = (routeIndex: number): void => {
    const map = mapInstance.value
    if (!map || activeRouteIndex.value === routeIndex) return
    // 現在アクティブな経路をグレーに戻す
    const prevLayerIds = routeLayerMap.value[activeRouteIndex.value] || []
    prevLayerIds.forEach((id: string) => {
      if (map.getLayer(id)) map.setPaintProperty(id, 'line-color', ROUTE_INACTIVE_COLOR)
    })
    // 新しい経路を固有カラーにして最前面へ
    const nextLayerIds = routeLayerMap.value[routeIndex] || []
    nextLayerIds.forEach(id => {
      if (map.getLayer(id)) {
        map.setPaintProperty(id, 'line-color', ROUTE_ACTIVE_COLORS[routeIndex])
        map.moveLayer(id)
      }
    })
    activeRouteIndex.value = routeIndex
  }


  // 地図のルート追加
  function addRouteLayer(routeIndex: number, legIndex: number, coordinates: [number, number][]): string {
    const map = mapInstance.value
    const layerId: string = 'route-' + routeIndex + '-' + legIndex
    if (!map) return layerId
    const color: string = routeIndex === 0 ? ROUTE_ACTIVE_COLORS[routeIndex] : ROUTE_INACTIVE_COLOR;

    // レイヤー初期化
    removeRouteLines(layerId)

    // レイヤー追加
    map.addLayer({
      id: layerId,
      type: 'line',
      source: {
        type: 'geojson',
        data: {
          type: 'Feature',
          properties: null, // ルートの属性を必要に応じて追加（路線名、交通手段種別など）
          geometry: {
            type: 'LineString',
            coordinates: coordinates
          }
        }
      },
      layout: {
        'line-join': 'round',
        'line-cap': 'round'
      },
      paint: {
        'line-color': color,
        'line-width': 5
      }
    });
    return layerId
  }

  /** ルートラインを地図から削除 */
  const removeRouteLines = (layerId: string): void => {
    const map = mapInstance.value
    if (!map) return
    // クリックハンドラを解除
    const handler = layerClickHandlers.value[layerId]
    if (handler) {
      map.off('click', layerId, handler)
      delete layerClickHandlers.value[layerId]
    }
    if (map.getLayer(layerId)) {
      map.removeLayer(layerId);
    }
    if (map.getSource(layerId)) {
      map.removeSource(layerId);
    }
  }

  /** バスマーカーのDOM要素を作成 */
  const createBusMarkerElement = (agencyName: string): HTMLElement => {
    const el = document.createElement('div')
    el.className = 'bus-marker'
    el.textContent = '🚌'
    el.title = agencyName
    return el
  }

  /** バスマーカーのポップアップHTMLを生成 */
  const buildBusPopupHtml = (v: VehiclePosition): string => {
    const rows: string[] = []
    if (v.agencyName) rows.push(`<div class="bus-popup__row bus-popup__agency">${v.agencyName}</div>`)
    if (v.routeId)    rows.push(`<div class="bus-popup__row">路線ID: ${v.routeId}</div>`)
    if (v.from || v.to) rows.push(`<div class="bus-popup__row">${v.from ?? ''} → ${v.to ?? ''}</div>`)
    if (v.startTime && v.endTime) rows.push(`<div class="bus-popup__row">${v.startTime} ～ ${v.endTime}</div>`)
    return `<div class="bus-popup">${rows.join('')}</div>`
  }

  /** バスマーカーIDを生成 */
  const getBusMarkerId = (vehicleId: string): string => {
    return `bus-${vehicleId}`
  }

  /** バスマーカーを差分更新する */
  const updateBusMarkers = (vehicles: VehiclePosition[]): void => {
    const map = mapInstance.value
    if (!map) return

    const activeMarkerIds = new Set<string>()

    vehicles.forEach(v => {
      const markerId = getBusMarkerId(v.vehicleId)
      activeMarkerIds.add(markerId)

      const existingMarker = markerManager.value.getMarker(markerId)
      if (existingMarker) {
        existingMarker.setLngLat([v.lon, v.lat])
        existingMarker.getElement().title = v.agencyName ?? ''
        return
      }

      const el = createBusMarkerElement(v.agencyName ?? '')
      const popup = new maplibregl.Popup({ offset: 25, closeButton: false, maxWidth: '220px' })
        .setHTML(buildBusPopupHtml(v))
      const marker = new maplibregl.Marker({ element: el })
        .setLngLat([v.lon, v.lat])
        .setPopup(popup)
      markerManager.value.addMarker(markerId, marker, map)
    })

    Object.keys(markerManager.value.markers)
      .filter(id => id.startsWith('bus-') && !activeMarkerIds.has(id))
      .forEach(id => markerManager.value.removeMarker(id))
  }

  /** バスマーカーを全削除 */
  const clearBusMarkers = (): void => {
    markerManager.value.removeMarkersByType('bus-')
  }

  /** マップ上のすべてのリソースを初期化 */
  const cleanup = (): void => {
    if (mapInstance.value) {
      markerManager.value.clearAllMarkers()
      mapInstance.value.remove()
      mapInstance.value = null
    }
  }

  return {
    mapInstance,
    markerManager,
    activeRouteIndex,
    initializeMap,
    addRouteLines,
    removeRouteLines,
    setActiveRoute,
    updateBusMarkers,
    clearBusMarkers,
    cleanup
  }
}
