const codeConstants = {

    // 運転免許の所持状況
    LICENSE_STATUS: {
        UNLICENSED: 0, // 未所持
        LICENSED: 1,   // 所持
        RETURNED: 2,   // 返納
        EXPIRED: 3,    // 失効
        SUSPENDED: 4,  // 停止
        OTHER: 5,      // その他
    },

} as const;

export { codeConstants };