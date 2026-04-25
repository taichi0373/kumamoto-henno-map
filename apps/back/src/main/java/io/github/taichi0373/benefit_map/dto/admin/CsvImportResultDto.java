package io.github.taichi0373.benefit_map.dto.admin;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Getter;

/**
 * CSVインポート結果DTO
 * <p>
 * CSVインポート処理の結果（登録・更新・失敗件数およびエラー詳細）を保持する。
 * </p>
 */
@Getter
public class CsvImportResultDto implements Serializable {

    /** シリアルバージョンUID */
    @Serial
    private static final long serialVersionUID = 1L;

    /** 登録件数 */
    private final int inserted;

    /** 更新件数 */
    private final int updated;

    /** 失敗件数 */
    private final int failed;

    /** エラー詳細（行番号とメッセージ） */
    private final List<String> errors;

    /**
     * コンストラクタ
     *
     * @param inserted 登録件数
     * @param updated  更新件数
     * @param failed   失敗件数
     * @param errors   エラー詳細リスト
     */
    public CsvImportResultDto(int inserted, int updated, int failed, List<String> errors) {
        this.inserted = inserted;
        this.updated = updated;
        this.failed = failed;
        this.errors = errors;
    }
}
