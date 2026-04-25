# Java / Spring Boot コーディング規約

## 基本方針

- `Object` 型の多用は禁止 — 明示的な型を使用する
- クラス・メソッド・フィールドには日本語JavaDocコメントを記述する
- Entity は `Serializable` を実装し `@Serial private static final long serialVersionUID = 1L;` を定義する

## Doma 2 パターン

- Entityql とメタモデルクラスでタイプセーフクエリを記述する
- DAO には `@Dao` + `@ConfigAutowireable` を付与する
- Entityql を使うメソッドは `default` メソッドで実装する
- Entity の `@Table`/`@Column` は小文字スネークケースを使用（PostgreSQL は大文字小文字を区別しない）

## スキーマ変更時の手順

スキーマ変更時は以下の3つを**必ず同時に**更新する:
1. DDLファイル (`config/database/DDL/TABLE/`)
2. DMLファイル (`config/database/DML/TABLE/`)
3. Entityクラス

## API設計

- エンドポイントは kebab-case かつ RESTful に設計する
- 例外ハンドリングは各Controllerで `try-catch` を行い、`ApiResponseDto` で統一したレスポンスを返す
- 共通例外ハンドラ（`GlobalExceptionHandler` / `@ControllerAdvice` 等）を導入する場合は、実装追加後に本規約も更新する
- CORS・Security設定は `config/SecurityConfig.java` で管理する

## 認証関連クラス

| クラス | 役割 |
|--------|------|
| `security/JwtAuthenticationFilter` | Authorization: Bearer ヘッダーからJWT検証 |
| `util/JwtUtil` | トークン生成・検証 |
| `service/AuthService` | ログイン処理・アクセストークン生成 |
| `service/RefreshTokenService` | リフレッシュトークン生成・ローテーション・失効 |
| `config/SecurityConfig` | STATELESS・CSRF無効・permitAll設定 |

## Security設定方針

- `/auth/**` → permitAll（login・refresh・logout・パスワードリセット）
- `POST /users/signup` → permitAll
- `/users/**` → authenticated（JWT必須）
- `/benefit/users/**` → authenticated（JWT必須）
- その他（`/benefit`, `/municipality`, `/route` 等）→ permitAll
