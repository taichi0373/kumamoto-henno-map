const codeConstant = {

    // 運転免許の所持状況
    LICENSE_STATUS: {
        UNLICENSED: 0, // 未所持
        LICENSED: 1,   // 所持
        RETURNED: 2,   // 返納
        EXPIRED: 3,    // 失効
        SUSPENDED: 4,  // 停止
        OTHER: 5,      // その他
    },

    // 交通手段
    TRANSPORTATION: {
        TRANSIT: 'TRANSIT, WALK', // 公共交通機関
        RAIL: 'RAIL, TRAM, WALK', // 電車
        BUS: 'BUS, WALK',         // バス
        BICYCLE: 'BICYCLE',       // 自転車
        WALK: 'WALK',             // 徒歩
    },

    // 出発/到着
    DEPARTURE_ARRIVAL: {
        DEPARTURE: 'DEPARTURE', // 出発
        ARRIVAL: 'ARRIVAL',     // 到着
    },

    // 検索タイプ
    SEARCH_TYPE: {
        START: 'start',         // 出発地
        END: 'end',             // 目的地
    }

} as const;

export { codeConstant };