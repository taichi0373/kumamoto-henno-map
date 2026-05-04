import maplibregl from 'maplibre-gl'

// 店舗マーカー作成関数
export const createStoreMarker = (store) => {
  if (!store.latitude || !store.longitude) return null
  
  return new maplibregl.Marker()
    .setLngLat([store.longitude, store.latitude])
    .setPopup(
      new maplibregl.Popup({ offset: 25 })
        .setHTML(`<h4>${store.name}</h4><p>${store.address}</p>`)
    )
}

// 汎用マーカー作成関数
export const createMarker = (lat, lon, options = {}) => {
  const { 
    color = '#3FB1CE',
    draggable = false,
    popup = null,
    className = ''
  } = options

  const marker = new maplibregl.Marker({
    color,
    draggable
  })
  .setLngLat([lon, lat])

  if (className) {
    marker.getElement().className += ` ${className}`
  }

  if (popup) {
    marker.setPopup(popup)
  }

  return marker
}

// スタート/エンドマーカー作成関数
export const createRouteMarker = (lat, lon, type, name = '') => {
  const colors = {
    start: '#4CAF50',
    end: '#F44336',
    stop: '#FF9800'
  }

  const icons = {
    start: 'S',
    end: 'E',
    stop: 'P'
  }

  const color = colors[type] || '#3FB1CE'
  
  const popup = name ? new maplibregl.Popup({ offset: 25 })
    .setHTML(`<div><strong>${name}</strong></div>`) : null

  return createMarker(lat, lon, {
    color,
    popup,
    className: `route-marker route-marker-${type}`
  })
}

// マーカー管理クラス
export class MarkerManager {
  constructor() {
    this.markers = new Map()
  }

  // マーカー追加
  addMarker(id, marker, map) {
    if (this.markers.has(id)) {
      this.removeMarker(id)
    }
    
    this.markers.set(id, marker)
    marker.addTo(map)
    return marker
  }

  // マーカー削除
  removeMarker(id) {
    const marker = this.markers.get(id)
    if (marker) {
      marker.remove()
      this.markers.delete(id)
    }
  }

  // 特定タイプのマーカーを全て削除
  removeMarkersByType(type) {
    for (const [id, marker] of this.markers) {
      if (id.startsWith(type)) {
        marker.remove()
        this.markers.delete(id)
      }
    }
  }

  // 全マーカー削除
  clearAllMarkers() {
    for (const marker of this.markers.values()) {
      marker.remove()
    }
    this.markers.clear()
  }

  // マーカー取得
  getMarker(id) {
    return this.markers.get(id)
  }

  // 全マーカー取得
  getAllMarkers() {
    return Array.from(this.markers.values())
  }
}