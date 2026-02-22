import type { Marker, Popup } from 'maplibre-gl'

/** マーカーの種類 */
export type MarkerType = 'start' | 'end' | 'stop' | 'store'

/** ルートマーカーの種類（経路表示用） */
export type RouteMarkerType = 'start' | 'end' | 'stop'

/** マーカー配置関数の型定義 */
export type PlaceMarkerFunction = (lat: number, lon: number, name: string, type: string) => void

/** マーカー削除関数の型定義 */
export type ClearMarkerFunction = (type: string) => void

/** マーカー関連のオプション */
export interface MarkerOptions {
  color?: string
  draggable?: boolean
  popup?: Popup | null
  className?: string
}

/** ルートマーカーの型定義 */
export interface RouteMarkerOptions {
  startMarker?: Marker
  endMarker?: Marker
  placeMarker?: PlaceMarkerFunction
  clearMarker?: ClearMarkerFunction
}

/** 逆ジオコーディング関数の型定義 */
export type ReverseGeocodingFunction = (lat: number, lon: number) => Promise<{ name: string } | null>