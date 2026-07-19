package io.github.taichi0373.kumamoto_henno_map.dto;

import io.github.taichi0373.kumamoto_henno_map.repository.entity.SystemField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 特典適用条件DTO
 * <p>
 * 特典の適用条件（年齢・免許状況・自治体コード）を保持するデータ転送オブジェクト。
 * </p>
 */
@Schema(description = "特典検索条件")
@Data
public class BenefitEligibilityDto {

    /** 特典条件ID */
    @Schema(description = "特典条件ID", example = "1")
    private Integer id;

    /** 特典ID */
    @Schema(description = "特典ID", example = "B001")
    private String benefitId;

    /** 年齢 */
    @Schema(description = "年齢", example = "70")
    private Integer age;

    /** 最低年齢 */
    @Schema(description = "最低年齢", example = "65")
    private Integer minAge;

    /** 最高年齢 */
    @Schema(description = "最高年齢（null は上限なし）", nullable = true)
    private Integer maxAge;

    /** 運転免許所持状況 */
    @Schema(description = "運転免許所持状況コード（例: 0: 未所持, 1: 所持, 2: 返納, 3: 失効）", example = "2")
    private String licenseStatus;

    /** 対象自治体コード */
    @Schema(description = "対象自治体コード", example = "43100")
    private String municipalityCd;

    /** 備考 */
    @Schema(description = "備考", example = "熊本市内限定")
    private String note;

    /** フリーワード検索キーワード */
    @Schema(description = "フリーワード（特典名・特典内容の部分一致）", example = "バス")
    private String keyword;

    /** カテゴリコード */
    @Schema(description = "カテゴリコード", example = "TR001")
    private String categoryCd;

    /** 現在地緯度（現在地周辺フィルタリング用） */
    @Schema(description = "現在地緯度", example = "32.789")
    private Double latitude;

    /** 現在地経度（現在地周辺フィルタリング用） */
    @Schema(description = "現在地経度", example = "130.741")
    private Double longitude;

    /** 検索半径（km）。省略時は2km */
    @Schema(description = "検索半径（km）", example = "2.0")
    private Double radiusKm;

    /** システム共通フィールド */
    @Schema(hidden = true)
    private SystemField systemField;
}
