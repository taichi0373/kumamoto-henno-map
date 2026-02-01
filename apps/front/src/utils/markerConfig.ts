import maplibregl from 'maplibre-gl'
import type { Map, Marker, Popup } from 'maplibre-gl'

// 型定義
export interface Store {
  latitude?: number
  longitude?: number
  name: string
  address: string
}

export interface MarkerOptions {
  color?: string
  draggable?: boolean
  popup?: Popup | null
  className?: string
}

export type RouteMarkerType = 'start' | 'end' | 'stop'

// 店舗マーカー作成関数
export const createStoreMarker = (store: Store): Marker | null => {
  if (!store.latitude || !store.longitude) return null
  
  return new maplibregl.Marker()
    .setLngLat([store.longitude, store.latitude])
    .setPopup(
      new maplibregl.Popup({ offset: 25 })
        .setHTML(`<h4>${store.name}</h4><p>${store.address}</p>`)
    )
}

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

  const icons: Record<RouteMarkerType, string> = {
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
  private markers: { [key: string]: Marker }

  constructor() {
    this.markers = {}
  }

  // マーカー追加
  addMarker(id: string, marker: Marker, map: Map): Marker {
    if (this.markers[id]) {
      this.removeMarker(id)
    }
    
    this.markers[id] = marker
    marker.addTo(map)
    return marker
  }

  // マーカー削除
  removeMarker(id: string): void {
    const marker = this.markers[id]
    if (marker) {
      marker.remove()
      delete this.markers[id]
    }
  }

  // 特定タイプのマーカーを全て削除
  removeMarkersByType(type: string): void {
    for (const id in this.markers) {
      if (id.startsWith(type)) {
        this.markers[id].remove()
        delete this.markers[id]
      }
    }
  }

  // 全マーカー削除
  clearAllMarkers(): void {
    for (const marker of Object.values(this.markers)) {
      marker.remove()
    }
    this.markers = {}
  }

  // マーカー取得
  getMarker(id: string): Marker | undefined {
    return this.markers[id]
  }

  // 全マーカー取得
  getAllMarkers(): Marker[] {
    return Object.values(this.markers)
  }
}