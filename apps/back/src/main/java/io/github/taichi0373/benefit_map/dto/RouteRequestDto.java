package io.github.taichi0373.benefit_map.dto;

import lombok.Data;
@Data
public class RouteRequestDto {

    /** 出発地 */
    private String startLocation;

    /** 目的地 */
    private String endLocation;

    /** 交通手段 */
    private String transportMode;

    /** 時間選択 */
    private String timeSelect;

    /** 日付 */
    private String date;

    /** 時刻 */
    private String time;

    /** 到着時刻か出発時刻かを示すフラグ */
    private boolean arriveBy;

}
