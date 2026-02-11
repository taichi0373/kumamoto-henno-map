package io.github.taichi0373.benefit_map.dto;
import java.time.LocalDate;
import io.github.taichi0373.benefit_map.repository.entity.SystemField;
import lombok.Data;

@Data
public class UsersDto {

    /** ユーザーID */
    private Integer userId;

    /** ユーザー名 */
    private String username;

    /** パスワード */
    private String password;

    /** メールアドレス */
    private String email;

    /** 生年月日 */
    private LocalDate birthDate;

    /** 居住地域 */
    private String address;

    /** 運転免許の所持状況 */
    private String licenseStatus;

    /** システム共通フィールド */
    private SystemField systemField;
}
