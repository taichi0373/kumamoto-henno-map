import { codeConstant } from '@/utils/codeConstant'

/**
 * 経路検索インタフェース
*/
interface RouteRequestInterface {
    /** 交通手段 */
    transport: string | null;
    /** 出発地 */
    startLocation: string | null;
    /** 出発地緯度 */
    startLat: number | null;
    /** 出発地経度 */
    startLon: number | null;
    /** 目的地 */
    endLocation: string | null;
    /** 目的地緯度 */
    endLat: number | null;
    /** 目的地経度 */
    endLon: number | null;
    /** 日付 */
    date: Date | null;
    /** 時間 */
    time: Date | null;
    /** 出発時刻 or 到着時刻を示すフラグ（true: 到着時刻, false: 出発時刻） */
    departureArrival: string | null;
}

/**
 * 経路検索DTO
*/
class RouteRequestDto {
    /** 交通手段 */
    transport: string | null;
    /** 出発地 */
    startLocation: string | null;
    /** 出発地緯度 */
    startLat: number | null;
    /** 出発地経度 */
    startLon: number | null;
    /** 目的地 */
    endLocation: string | null;
    /** 目的地緯度 */
    endLat: number | null;
    /** 目的地経度 */
    endLon: number | null;
    /** 日付 */
    date: Date | null;
    /** 時間 */
    time: Date | null;
    /** 出発時刻 or 到着時刻を示すフラグ（true: 到着時刻, false: 出発時刻） */
    departureArrival: string | null;

    /**
     * コンストラクタ
     * @param routeRequestInterface 経路検索インタフェース
     */
    constructor(routeRequestInterface?: RouteRequestInterface) {
        if (routeRequestInterface != null) {
            this.transport = routeRequestInterface.transport != null ? routeRequestInterface.transport : null;
            this.startLocation = routeRequestInterface.startLocation != null ? routeRequestInterface.startLocation : null;
            this.startLat = routeRequestInterface.startLat != null ? routeRequestInterface.startLat : null;
            this.startLon = routeRequestInterface.startLon != null ? routeRequestInterface.startLon : null;
            this.endLocation = routeRequestInterface.endLocation != null ? routeRequestInterface.endLocation : null;
            this.endLat = routeRequestInterface.endLat != null ? routeRequestInterface.endLat : null;
            this.endLon = routeRequestInterface.endLon != null ? routeRequestInterface.endLon : null;
            this.date = routeRequestInterface.date != null ? routeRequestInterface.date : null;
            this.time = routeRequestInterface.time != null ? routeRequestInterface.time : null;
            this.departureArrival = routeRequestInterface.departureArrival != null ? routeRequestInterface.departureArrival : null;
        } else {
            this.transport = codeConstant.TRANSPORTATION.TRANSIT;
            this.startLocation = null;
            this.startLat = null;
            this.startLon = null;
            this.endLocation = null;
            this.endLat = null;
            this.endLon = null;
            this.date = null;
            this.time = null;
            this.departureArrival = codeConstant.DEPARTURE_ARRIVAL.DEPARTURE;
        }
    }
}

export { RouteRequestDto };