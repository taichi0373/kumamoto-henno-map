/**
 * 経路情報インタフェース
 */
interface RouteInterface {
    /** 所要時間 */
    duration: string | null;
    /** 出発時刻 */
    startTime: string | null;
    /** 到着時刻 */
    endTime: string | null;
    /** 合計割引料金 */
    totalDiscountFare: number | null;
    /** 合計料金 */
    totalFare: number | null;
    /** 乗り換え回数 */
    transfers: number | null;
}

/**
 * 経路情報DTO
 */
class RouteDto {
    /** 所要時間 */
    duration: string | null;
    /** 出発時刻 */
    startTime: string | null;
    /** 到着時刻 */
    endTime: string | null;
    /** 合計割引料金 */
    totalDiscountFare: number | null;
    /** 合計料金 */
    totalFare: number | null;
    /** 乗り換え回数 */
    transfers: number | null;

    /**
     * コンストラクタ
     * @param routeInterface 経路情報インタフェース
     */
    constructor(routeInterface?: RouteInterface) {
        if (routeInterface != null) {
            this.duration = routeInterface.duration;
            this.startTime = routeInterface.startTime;
            this.endTime = routeInterface.endTime;
            this.totalDiscountFare = routeInterface.totalDiscountFare;
            this.totalFare = routeInterface.totalFare;
            this.transfers = routeInterface.transfers;
        } else {
            this.duration = null;
            this.startTime = null;
            this.endTime = null;
            this.totalDiscountFare = null;
            this.totalFare = null;
            this.transfers = null;
        }
    }
}

export { RouteInterface, RouteDto };
