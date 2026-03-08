/**
 * マーカーインタフェース
 */
interface MarkerInterface {
    /** タイプ */
    type: string | null;
    /** 名称 */
    name: string | null;
    /** 住所 */
    address: string | null;
    /** 緯度 */
    lat: number | null;
    /** 経度 */
    lon: number | null;
}

/**
 * マーカーDTO
 */
class MarkerDto {
    /** タイプ */
    type: string | null;
    /** 名称 */
    name: string | null;
    /** 住所 */
    address: string | null;
    /** 緯度 */
    lat: number | null;
    /** 経度 */
    lon: number | null;

    /**
     * コンストラクタ
     * @param markerInterface マーカー情報インタフェース
     */
    constructor(markerInterface?: MarkerInterface) {
        if (markerInterface != null) {
            this.type = markerInterface.type;
            this.name = markerInterface.name;
            this.address = markerInterface.address;
            this.lat = markerInterface.lat;
            this.lon = markerInterface.lon;
        } else {
            this.type = null;
            this.name = null;
            this.address = null;
            this.lat = null;
            this.lon = null;
        }
    }
}

export { MarkerInterface, MarkerDto };
