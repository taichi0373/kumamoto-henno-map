package io.github.taichi0373.kumamoto_henno_map.dto.admin;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Getter;

/**
 * ジオコーディング結果DTO
 * <p>
 * AWS Location Serviceによるジオコーディング処理の結果を保持する。
 * </p>
 */
@Getter
public class GeocodingResultDto implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 成功件数 */
    private final int success;

    /** スキップ件数（既に座標保存済み） */
    private final int skipped;

    /** 失敗件数 */
    private final int failed;

    /** エラー詳細（特典IDとメッセージ） */
    private final List<String> errors;

    /**
     * コンストラクタ
     *
     * @param success 成功件数
     * @param skipped スキップ件数
     * @param failed  失敗件数
     * @param errors  エラー詳細リスト
     */
    public GeocodingResultDto(int success, int skipped, int failed, List<String> errors) {
        this.success = success;
        this.skipped = skipped;
        this.failed = failed;
        this.errors = errors;
    }
}
