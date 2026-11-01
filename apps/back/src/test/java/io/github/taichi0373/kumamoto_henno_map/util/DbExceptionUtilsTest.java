package io.github.taichi0373.kumamoto_henno_map.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.taichi0373.kumamoto_henno_map.dto.ApiResponseDto;

/** DbExceptionUtils のテスト */
class DbExceptionUtilsTest {

    private static final String FALLBACK_MESSAGE = "登録に失敗しました";
    private static final String SQL_TEXT = "insert into benefit (benefit_id) values ('x')";

    private ResponseEntity<ApiResponseDto<Void>> convert(Exception e) {
        return DbExceptionUtils.toErrorResponse(e, FALLBACK_MESSAGE);
    }

    @Test
    void 一意制約違反は409を返す() {
        var response = convert(new DuplicateKeyException(SQL_TEXT, new SQLException(SQL_TEXT, "23505")));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getMessage()).doesNotContain("insert");
    }

    @Test
    void 文字数超過は400を返す() {
        var response = convert(new DataIntegrityViolationException(SQL_TEXT, new SQLException(SQL_TEXT, "22001")));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).doesNotContain("insert");
    }

    @Test
    void NOT_NULL制約違反は400を返す() {
        var response = convert(new DataIntegrityViolationException(SQL_TEXT, new SQLException(SQL_TEXT, "23502")));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 外部キー制約違反は400を返す() {
        var response = convert(new DataIntegrityViolationException(SQL_TEXT, new SQLException(SQL_TEXT, "23503")));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void 接続エラーは500と固定メッセージを返す() {
        var response = convert(new RuntimeException(SQL_TEXT, new SQLException(SQL_TEXT, "08006")));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo(FALLBACK_MESSAGE);
    }

    @Test
    void SQLStateがない例外は500と固定メッセージを返す() {
        var response = convert(new RuntimeException(SQL_TEXT));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo(FALLBACK_MESSAGE);
    }
}
