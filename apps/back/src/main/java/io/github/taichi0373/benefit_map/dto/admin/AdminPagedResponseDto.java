package io.github.taichi0373.benefit_map.dto.admin;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Getter;

/**
 * 管理者向けページングレスポンスDTO
 * <p>
 * 管理者API一覧エンドポイントで共通使用する汎用ページングレスポンス。
 * {@code items}（ページ内アイテム）・{@code total}（総レコード数）・
 * {@code page}（現在ページ番号）・{@code size}（ページあたり件数）を保持する。
 * </p>
 *
 * @param <T> ページ内アイテムの型
 */
@Getter
public class AdminPagedResponseDto<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** ページ内アイテムリスト */
    private final List<T> items;

    /** 総レコード数 */
    private final long total;

    /** 現在のページ番号（0始まり） */
    private final int page;

    /** ページあたりのレコード数 */
    private final int size;

    /**
     * コンストラクタ（privateでファクトリメソッド経由のみ生成を許可する）
     *
     * @param items アイテムリスト
     * @param total 総レコード数
     * @param page  現在のページ番号
     * @param size  ページあたりのレコード数
     */
    private AdminPagedResponseDto(List<T> items, long total, int page, int size) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    /**
     * AdminPagedResponseDtoのインスタンスを生成するファクトリメソッド
     *
     * @param <T>   アイテムの型
     * @param items アイテムリスト
     * @param total 総レコード数
     * @param page  現在のページ番号（0始まり）
     * @param size  ページあたりのレコード数
     * @return AdminPagedResponseDtoインスタンス
     */
    public static <T> AdminPagedResponseDto<T> of(List<T> items, long total, int page, int size) {
        return new AdminPagedResponseDto<>(items, total, page, size);
    }
}
