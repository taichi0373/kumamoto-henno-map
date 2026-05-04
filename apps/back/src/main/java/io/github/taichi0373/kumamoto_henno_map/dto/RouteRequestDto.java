package io.github.taichi0373.kumamoto_henno_map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 経路探索リクエストDTO
 * <p>
 * OTP経路探索APIへのリクエストパラメータを保持するデータ転送オブジェクト。
 * </p>
 */
@Schema(description = "経路探索リクエスト")
@Data
public class RouteRequestDto {

    /** 交通手段 */
    @Schema(description = "OTP 交通手段指定", example = "TRANSIT,WALK", requiredMode = Schema.RequiredMode.REQUIRED)
    private String transportMode;

    /** 出発地 */
    @Schema(description = "出発地名称（表示用）", example = "熊本駅")
    private String startLocation;

    /** 出発地緯度 */
    @Schema(description = "出発地緯度", example = "32.7898", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double startLat;

    /** 出発地経度 */
    @Schema(description = "出発地経度", example = "130.6984", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double startLon;

    /** 目的地 */
    @Schema(description = "目的地名称（表示用）", example = "熊本市役所")
    private String endLocation;

    /** 目的地緯度 */
    @Schema(description = "目的地緯度", example = "32.8031", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double endLat;

    /** 目的地経度 */
    @Schema(description = "目的地経度", example = "130.7078", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double endLon;

    /** 日付 */
    @Schema(description = "出発日（または到着日）。YYYY-MM-DD 形式", example = "2025-04-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private String date;

    /** 時刻 */
    @Schema(description = "出発時刻（または到着時刻）。HH:mm 形式", example = "09:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String time;

    /** 出発時刻 or 到着時刻を示すフラグ（true: 到着時刻, false: 出発時刻） */
    @Schema(description = "true: 到着時刻指定、false: 出発時刻指定（デフォルト: false）", example = "false")
    private boolean arriveBy;

}
