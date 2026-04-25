package io.github.taichi0373.benefit_map.constants;

/**
 * CodeConstants
 * コード定数クラス
 */
public class CodeConstants {

    /**
     * 割引種別
     */
    public static class DiscountType {

        /** パーセンテージ割引 */
        public static final String PERCENTAGE = "0";

        /** 無料 */
        public static final String FREE = "1";
    }

    /**
     * 自治体区分
     */
    public static class MunicipalityType {

        /** 都道府県 */
        public static final String PREFECTURE = "1";

        /** 区 */
        public static final String WARD = "2";

        /** 市町村 */
        public static final String CITY = "3";
    }
}
