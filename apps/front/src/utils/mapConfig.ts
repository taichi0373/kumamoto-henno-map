import maplibregl from 'maplibre-gl'
import type { Map, NavigationControl, GeolocateControl, LngLatBoundsLike, Marker } from 'maplibre-gl'
import type { PlaceMarkerFunction, ClearMarkerFunction, ReverseGeocodingFunction } from './markerTypes'

// 型定義
export interface MapConfig {
  style: string
  center: [number, number]
  zoom: number
  minZoom: number
  maxZoom: number
  pitch: number
  bearing: number
  maxPitch: number
}

export interface MapClickOptions {
  onMapClick?: (e: maplibregl.MapMouseEvent, lat: number, lon: number) => void
  ReverseGeocoding?: ReverseGeocodingFunction
  isWaitingForInputStart?: boolean
  isWaitingForInputEnd?: boolean
  startLocation?: { value: string }
  endLocation?: { value: string }
  placeMarker?: PlaceMarkerFunction
  startMarker?: Marker
  endMarker?: Marker
  clearMarker?: ClearMarkerFunction
  clearRoutes?: () => void
  searchRouteEvent?: () => void
}

// デフォルト設定
export const DEFAULT_MAP_CONFIG: MapConfig = {
  style: 'https://tile.openstreetmap.jp/styles/osm-bright-ja/style.json',
  center: [130.741584, 32.7898], // 熊本市中心部
  zoom: 14,
  minZoom: 8,
  maxZoom: 18,
  pitch: 0,
  bearing: 0,
  maxPitch: 0,
}

// 熊本県の境界
const KUMAMOTO_BOUNDS: LngLatBoundsLike = [
  [128.523336, 30.896306],
  [132.3318, 34.0596]
]

// マップ初期化関数
export const createMapInstance = (containerId: string, config: Partial<MapConfig> = {}): Map => {
  const mapConfig = { ...DEFAULT_MAP_CONFIG, ...config }
  
  return new maplibregl.Map({
    container: containerId,
    ...mapConfig
  })
}

// マップ基本設定関数（コントロール、境界、現在地取得）
export const setupMapControls = (map: Map): void => {
  map.on('load', function () {
    // コントロールを右下に配置
    const navControl: NavigationControl = new maplibregl.NavigationControl()
    map.addControl(navControl, 'bottom-right')
    
    // 現在位置コントロール
    const geolocateControl: GeolocateControl = new maplibregl.GeolocateControl({
      positionOptions: {
        enableHighAccuracy: false
      },
      fitBoundsOptions: { maxZoom: 14 },
      trackUserLocation: true,
      showAccuracyCircle: false,
      showUserLocation: true,
    })
    map.addControl(geolocateControl, 'bottom-right')
  })

  // 現在地を取得して地図の中心に設定
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const userLocation: [number, number] = [position.coords.longitude, position.coords.latitude]
        map.setCenter(userLocation)
      },
      () => {
        map.setCenter(DEFAULT_MAP_CONFIG.center)
      }
    )
  } else {
    map.setCenter(DEFAULT_MAP_CONFIG.center)
  }

  // 熊本県の境界を設定
  map.setMaxBounds(KUMAMOTO_BOUNDS)
}

// 地図クリックイベント設定関数
export const setupMapClickEvents = (map: Map, options: MapClickOptions = {}): void => {
  const {
    onMapClick,
    ReverseGeocoding,
    isWaitingForInputStart,
    isWaitingForInputEnd,
    startLocation,
    endLocation,
    placeMarker,
    startMarker,
    endMarker,
    clearMarker,
    clearRoutes,
    searchRouteEvent
  } = options

  map.on('click', async function (e: maplibregl.MapMouseEvent) {
    const lat: number = e.lngLat.lat
    const lon: number = e.lngLat.lng

    if (onMapClick) {
      onMapClick(e, lat, lon)
      return
    }

    if (isWaitingForInputStart && ReverseGeocoding && startLocation && placeMarker) {
      const locationData = await ReverseGeocoding(lat, lon)
      if (locationData) {
        startLocation.value = locationData.name
        placeMarker(lat, lon, locationData.name, 'start')
        if (startMarker && endMarker && clearMarker && clearRoutes && searchRouteEvent) {
          clearMarker('stop')
          clearRoutes()
          searchRouteEvent()
        }
      } else {
        alert('位置情報データが見つかりませんでした')
      }
    } else if (isWaitingForInputEnd && ReverseGeocoding && endLocation && placeMarker) {
      const locationData = await ReverseGeocoding(lat, lon)
      if (locationData) {
        endLocation.value = locationData.name
        placeMarker(lat, lon, locationData.name, 'end')
        if (startMarker && endMarker && clearMarker && clearRoutes && searchRouteEvent) {
          clearMarker('stop')
          clearRoutes()
          searchRouteEvent()
        }
      } else {
        alert('位置情報データが見つかりませんでした')
      }
    }
  })
}

// 地図のズーム関数
export const zoomToLocation = (map: Map, lat: number, lon: number): void => {
  map.flyTo({
    center: [lon, lat],
    zoom: 16
  })
}
