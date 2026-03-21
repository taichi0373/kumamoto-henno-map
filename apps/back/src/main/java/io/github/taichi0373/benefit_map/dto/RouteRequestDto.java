package io.github.taichi0373.benefit_map.dto;

import lombok.Data;
@Data
public class RouteRequestDto {

    /** 交通手段 */
    private String transportMode;

    /** 出発地 */
    private String startLocation;

    /** 出発地緯度 */
    private Double startLat;

    /** 出発地経度 */
    private Double startLon;

    /** 目的地 */
    private String endLocation;

    /** 目的地緯度 */
    private Double endLat;

    /** 目的地経度 */
    private Double endLon;

    /** 日付 */
    private String date;

    /** 時刻 */
    private String time;

    /** 出発時刻 or 到着時刻を示すフラグ（true: 到着時刻, false: 出発時刻） */
    private boolean arriveBy;

    /** ユーザーID */
    private Long userId;

}
