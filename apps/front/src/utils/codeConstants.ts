const codeConstants = {

    // 運転免許の所持状況
    LICENSE_STATUS: {
        UNLICENSED: 0,
        LICENSED: 1,
        RETURNED: 2,
        EXPIRED: 3,
        SUSPENDED: 4,
        OTHER: 5,
    },

} as const;

export { codeConstants };