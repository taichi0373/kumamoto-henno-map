import maplibregl from 'maplibre-gl'
import type { Map, Marker, Popup } from 'maplibre-gl'
import type { MarkerType, RouteMarkerType, MarkerOptions } from './markerTypes'

export { MarkerType, RouteMarkerType } from './markerTypes'

// 汎用マーカー作成関数
export const createMarker = (lat: number, lon: number, options: MarkerOptions = {}): Marker => {
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
export const createRouteMarker = (lat: number, lon: number, type: RouteMarkerType, name: string = ''): Marker => {
  const colors: Record<RouteMarkerType, string> = {
    start: '#4CAF50',
    end: '#F44336',
    stop: '#FF9800'
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
  private _markers: { [key: string]: Marker }

  constructor() {
    this._markers = {}
  }

  // マーカー一覧への読み取り専用アクセス
  get markers(): { [key: string]: Marker } {
    return { ...this._markers }
  }

  // マーカー追加
  addMarker(id: string, marker: Marker, map: Map): Marker {
    if (this._markers[id]) {
      this.removeMarker(id)
    }
    
    this._markers[id] = marker
    marker.addTo(map)
    return marker
  }

  // マーカー削除
  removeMarker(id: string): void {
    const marker = this._markers[id]
    if (marker) {
      marker.remove()
      delete this._markers[id]
    }
  }

  // 特定タイプのマーカーを全て削除
  removeMarkersByType(type: string): void {
    for (const id in this._markers) {
      if (id.startsWith(type)) {
        this._markers[id].remove()
        delete this._markers[id]
      }
    }
  }

  // 全マーカー削除
  clearAllMarkers(): void {
    for (const marker of Object.values(this._markers)) {
      marker.remove()
    }
    this._markers = {}
  }

  // マーカー取得
  getMarker(id: string): Marker | undefined {
    return this._markers[id]
  }

  // 全マーカー取得
  getAllMarkers(): Marker[] {
    return Object.values(this._markers)
  }
}