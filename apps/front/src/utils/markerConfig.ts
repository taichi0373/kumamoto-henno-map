import maplibregl from 'maplibre-gl'
import type { Map, Marker } from 'maplibre-gl'
import type { RouteMarkerType, MarkerOptions } from './markerTypes'

export type { MarkerType, RouteMarkerType } from './markerTypes'

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

/**
 * 特典マーカーのポップアップHTMLを組み立てる
 */
const buildBenefitPopupHtml = (
  benefitName: string,
  benefitDetail: string,
  phoneNumber?: string | null,
  benefitUrl?: string | null,
  address?: string | null
): string => {
  const textPrimary = '#374151'
  const textSecondary = '#6b7280'
  const borderColor = '#e5e7eb'
  const linkColor = '#2563eb'
  const bgLabel = '#f3f4f6'

  let html = `<div style="max-width:280px; min-width:200px; font-size:13px; color:${textPrimary}; line-height:1.6;">`

  /* ヘッダー：特典名 */
  html += `<div style="font-weight:bold; font-size:14px; text-align:center; padding:4px 0 10px; margin-bottom:10px; border-bottom:2px solid ${borderColor};">${benefitName}</div>`

  /* 特典内容 */
  html += `<div style="margin-bottom:10px;">`
  html += `<div style="font-size:11px; font-weight:bold; color:${textSecondary}; margin-bottom:4px; padding:2px 6px; background:${bgLabel}; border-radius:3px; display:inline-block;">特典内容</div>`
  html += `<div style="color:${textPrimary}; padding-left:2px;">${benefitDetail}</div>`
  html += `</div>`

  /* 情報リスト */
  const hasInfo = phoneNumber || address
  if (hasInfo) {
    html += `<div style="border-top:1px solid ${borderColor}; padding-top:8px; margin-bottom:8px;">`
    if (phoneNumber) {
      html += `<div style="display:flex; align-items:flex-start; gap:6px; margin-bottom:6px; color:${textSecondary}; font-size:12px;"><i class="pi pi-phone" style="margin-top:2px; flex-shrink:0;"></i><span>${phoneNumber}</span></div>`
    }
    if (address) {
      html += `<div style="display:flex; align-items:flex-start; gap:6px; margin-bottom:6px; color:${textSecondary}; font-size:12px;"><i class="pi pi-map-marker" style="margin-top:2px; flex-shrink:0;"></i><span>${address}</span></div>`
    }
    html += `</div>`
  }

  /* 詳細リンク */
  if (benefitUrl) {
    html += `<div style="text-align:right; padding-top:4px; border-top:1px solid ${borderColor};"><a href="${benefitUrl}" target="_blank" rel="noopener noreferrer" style="color:${linkColor}; text-decoration:underline; font-size:12px;">詳細を見る <i class="pi pi-external-link" style="font-size:10px;"></i></a></div>`
  }

  html += '</div>'
  return html
}

/**
 * 特典マーカー用のアイコン要素を作成する
 */
const createBenefitMarkerElement = (): HTMLElement => {
  const el = document.createElement('div')
  el.className = 'benefit-marker'
  el.style.cssText = 'width:30px; height:30px; border-radius:50%; background:#E91E63; display:flex; align-items:center; justify-content:center; cursor:pointer; box-shadow:0 2px 6px rgba(0,0,0,0.3); border:2px solid #fff;'
  const icon = document.createElement('i')
  icon.className = 'pi pi-shop'
  icon.style.cssText = 'color:#fff; font-size:16px;'
  el.appendChild(icon)
  return el
}

/**
 * 特典マーカーを作成する
 */
export const createBenefitMarker = (
  lat: number,
  lon: number,
  benefitName: string,
  benefitDetail: string,
  phoneNumber?: string | null,
  benefitUrl?: string | null,
  address?: string | null
): Marker => {
  const popup = new maplibregl.Popup({ offset: 25 })
    .setHTML(buildBenefitPopupHtml(benefitName, benefitDetail, phoneNumber, benefitUrl, address))

  const marker = new maplibregl.Marker({ element: createBenefitMarkerElement() })
    .setLngLat([lon, lat])
    .setPopup(popup)

  return marker
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