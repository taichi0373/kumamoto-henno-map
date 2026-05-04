# デプロイ環境変数一覧

## フロントエンド（Vercel）

| 変数名 | 説明 | 設定値 |
|--------|------|--------|
| `VUE_APP_API_BASE_URL` | バックエンドAPIのベースURL | **設定不要** |

---

> **注意**: 本番環境では `VUE_APP_API_BASE_URL` を Vercel に設定しないでください。未設定時はデフォルト値 `/kumamoto-henno-map/api`（相対パス）が使用され、Vercel のリライト設定によりバックエンドへプロキシされます。絶対URLを設定するとリライトを経由せず直接クロスオリジン通信となり、Cookie（`SameSite=Lax`）が送信されず認証が正常に機能しません。

## バックエンド（Render）

| 変数名 | 説明 | 設定例 | 必須 |
|--------|------|--------|------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL（SSL込み・DB_*変数より優先） | `jdbc:postgresql://<Internal Hostname>/<DB名>?sslmode=require` | ✅ |
| `DB_USERNAME` | DBユーザー名 | RenderのDB Username | ✅ |
| `DB_PASSWORD` | DBパスワード | RenderのDB Password | ✅ |
| `JWT_SECRET` | JWTシークレットキー（32文字以上推奨） | ランダム文字列 | ✅ |
| `CORS_ALLOWED_ORIGINS` | 許可するオリジン（カンマ区切り） | `https://www.kumamoto-henno-map.com` | ✅ |
| `FRONTEND_BASE_URL` | パスワードリセットメールに使用するフロントURL | `https://www.kumamoto-henno-map.com` | ✅ |
| `FORWARD_HEADERS_STRATEGY` | リバースプロキシのヘッダー処理 | `FRAMEWORK` | ✅ |
| `OTP_API_URL` | OpenTripPlanner APIのURL | `https://otp.kumamoto-henno-map.com/otp/routers/default/plan` | ✅ |
| `MAIL_HOST` | SMTPサーバーホスト | `sandbox.smtp.mailtrap.io` | ✅ |
| `MAIL_PORT` | SMTPポート | `587` | ✅ |
| `MAIL_USERNAME` | SMTPユーザー名 | Mailtrapのユーザー名 | ✅ |
| `MAIL_PASSWORD` | SMTPパスワード | Mailtrapのパスワード | ✅ |
| `MAIL_FROM` | 送信元メールアドレス | `noreply@kumamoto-henno-map.com` | （未設定時は `MAIL_USERNAME` を使用） |

---

> **注意**: `SPRING_DATASOURCE_URL` に Render PostgreSQL の **Internal Database URL** を使用してください（`postgresql://` → `jdbc:postgresql://` に変換し、`?sslmode=require` を付加）。`DB_HOST` / `DB_PORT` / `DB_NAME` は `SPRING_DATASOURCE_URL` を設定している場合は不要です。Internal接続はRender内部ネットワーク経由で高速・無料で、SSL必須です。

### SPRING_DATASOURCE_URL の組み立て方

Render の PostgreSQL ダッシュボード → **Connect** タブ → **Internal Database URL** を確認し、以下のように変換する:

```
# Render が表示する Internal Database URL（例）
postgresql://kumamoto_henno_map_user:PASSWORD@dpg-xxxxxxxxx-a/kumamoto_henno_map

# 環境変数に設定する JDBC URL
jdbc:postgresql://dpg-xxxxxxxxx-a/kumamoto_henno_map?sslmode=require
```

※ `postgresql://user:password@host/db` の形式から、`jdbc:postgresql://host/db?sslmode=require` に変換（ユーザー名・パスワードは別変数で指定）。
