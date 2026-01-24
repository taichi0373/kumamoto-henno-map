import maplibregl from 'maplibre-gl'

// デフォルト設定
export const DEFAULT_MAP_CONFIG = {
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
const KUMAMOTO_BOUNDS = [
  [128.523336, 30.896306],
  [132.3318, 34.0596]
]

// MapLibre GL JSの設定
export const setupMapLibre = () => {
  // 必要に応じて追加の設定
}

// マップ初期化関数
export const createMapInstance = (containerId, config = {}) => {
  const mapConfig = { ...DEFAULT_MAP_CONFIG, ...config }
  
  return new maplibregl.Map({
    container: containerId,
    ...mapConfig
  })
}

// マップ基本設定関数（コントロール、境界、現在地取得）
export const setupMapControls = (map) => {
  map.on('load', function () {
    // コントロールを右下に配置
    const navControl = new maplibregl.NavigationControl()
    map.addControl(navControl, 'bottom-right')
    
    // 現在位置コントロール
    const geolocateControl = new maplibregl.GeolocateControl({
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
        const userLocation = [position.coords.longitude, position.coords.latitude]
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
export const setupMapClickEvents = (map, options = {}) => {
  const {
    onMapClick,
    ReverseGeocoding,
    isWaitingForInput_S,
    isWaitingForInput_E,
    start_location,
    end_location,
    placeMarker,
    startMarker,
    endMarker,
    clearMarker,
    clearRoutes,
    searchRouteEvent
  } = options

  map.on('click', async function (e) {
    const lat = e.lngLat.lat
    const lon = e.lngLat.lng
    
    if (onMapClick) {
      onMapClick(e, lat, lon)
      return
    }

    if (isWaitingForInput_S) {
      const locationData = await ReverseGeocoding(lat, lon)
      if (locationData) {
        start_location.value = locationData.name
        placeMarker(lat, lon, locationData.name, 'start')
        if (startMarker && endMarker) {
          clearMarker('stop')
          clearRoutes()
          searchRouteEvent()
        }
      } else {
        alert('位置情報データが見つかりませんでした')
      }
    } else if (isWaitingForInput_E) {
      const locationData = await ReverseGeocoding(lat, lon)
      if (locationData) {
        end_location.value = locationData.name
        placeMarker(lat, lon, locationData.name, 'end')
        if (startMarker && endMarker) {
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
export const zoomToLocation = (map, lat, lon) => {
  map.flyTo({
    center: [lon, lat],
    zoom: 16
  })
}

// 地図のルート追加関数
export const addRouteLayer = (map, routeNumber, index, coordinates) => {
  const layerId = 'route-' + routeNumber + '-' + index
  const colors = ['#1A74FD', '#757575', '#757575']
  const color = colors[routeNumber]
  
  // 既存のレイヤーとソースを削除
  if (map.getLayer(layerId)) {
    map.removeLayer(layerId)
  }
  if (map.getSource(layerId)) {
    map.removeSource(layerId)
  }
  
  // 新しいレイヤーを追加
  map.addLayer({
    id: layerId,
    type: 'line',
    source: {
      type: 'geojson',
      data: {
        type: 'Feature',
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
  })
}