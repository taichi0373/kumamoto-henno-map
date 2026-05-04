/**
 * 経路区間（leg）インタフェース
 */
export interface RouteLeg {
    /** 交通手段 */
    mode?: string | null;
    /** ポリライン形状 */
    legGeometry?: { points?: string | null } | null;
    /** 表示用アイコン（バックエンドのレスポンスには含まれない） */
    icon?: string | null;
    /** 出発地名 */
    from?: string | null;
    /** 目的地名 */
    to?: string | null;
    /** 出発地緯度 */
    fromLat?: number | null;
    /** 出発地経度 */
    fromLon?: number | null;
    /** 目的地緯度 */
    toLat?: number | null;
    /** 目的地経度 */
    toLon?: number | null;
    /** 出発時刻 */
    startTime?: string | null;
    /** 到着時刻 */
    endTime?: string | null;
    /** 所要時間（分） */
    duration?: number | null;
    /** 運賃 */
    fare?: number | null;
    /** 割引運賃 */
    discountFare?: number | null;
    /** 事業者ID */
    agencyId?: string | null;
    /** 事業者名 */
    agencyName?: string | null;
    /** 事業者URL */
    agencyUrl?: string | null;
    /** 特典ID */
    benefitId?: string | null;
    /** 特典URL */
    benefitUrl?: string | null;
    /** コミュニティバスID */
    communityBusId?: string | null;
    /** フリーパス */
    freePass?: string | null;
    /** 路線ID */
    routeId?: string | null;
    /** 交通機関フラグ */
    transitLeg?: boolean | null;
    /** リアルタイムデータフラグ */
    isRealtime?: boolean | null;
    /** 遅延時間（秒、正値=遅延・負値=早着） */
    arrivalDelay?: number | null;
}

/**
 * 経路情報インタフェース
 */
interface RouteInterface {
    /** 所要時間 */
    duration: number | null;
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
    /** 経路区間リスト */
    legs: RouteLeg[];
}

/**
 * 経路情報DTO
 */
class RouteDto {
    /** 所要時間 */
    duration: number | null;
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
    /** 経路区間リスト */
    legs: RouteLeg[];

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
            this.legs = routeInterface.legs ?? [];
        } else {
            this.duration = null;
            this.startTime = null;
            this.endTime = null;
            this.totalDiscountFare = null;
            this.totalFare = null;
            this.transfers = null;
            this.legs = [];
        }
    }
}

export { RouteInterface, RouteDto };
