# デプロイ環境変数一覧

## フロントエンド（Vercel）

| 変数名 | 説明 | 設定例 |
|--------|------|--------|
| `VUE_APP_API_BASE_URL` | バックエンドAPIのベースURL | `https://your-back.onrender.com/benefit-map/api` |

---

## バックエンド（Render）

| 変数名 | 説明 | 設定例 | 必須 |
|--------|------|--------|------|
| `DB_HOST` | PostgreSQL ホスト名 | RenderのInternal Hostname | ✅ |
| `DB_PORT` | PostgreSQL ポート | `5432` | ✅ |
| `DB_NAME` | データベース名 | `benefit_map` | ✅ |
| `DB_USERNAME` | DBユーザー名 | RenderのDB Username | ✅ |
| `DB_PASSWORD` | DBパスワード | RenderのDB Password | ✅ |
| `JWT_SECRET` | JWTシークレットキー（32文字以上推奨） | ランダム文字列 | ✅ |
| `CORS_ALLOWED_ORIGINS` | 許可するオリジン（カンマ区切り） | `https://your-app.vercel.app` | ✅ |
| `FRONTEND_BASE_URL` | パスワードリセットメールに使用するフロントURL | `https://your-app.vercel.app` | ✅ |
| `FORWARD_HEADERS_STRATEGY` | リバースプロキシのヘッダー処理 | `FRAMEWORK` | ✅ |
| `MAIL_HOST` | SMTPサーバーホスト | `sandbox.smtp.mailtrap.io` | ✅ |
| `MAIL_PORT` | SMTPポート | `587` | ✅ |
| `MAIL_USERNAME` | SMTPユーザー名 | Mailtrapのユーザー名 | ✅ |
| `MAIL_PASSWORD` | SMTPパスワード | Mailtrapのパスワード | ✅ |
| `MAIL_FROM` | 送信元メールアドレス | `noreply@example.com` | （未設定時は `MAIL_USERNAME` を使用） |

---

> **注意**: `DB_*` 変数はRenderのPostgreSQLを使う場合、ダッシュボードの **Connect** タブに記載されている値をそのまま使用してください。Internal接続はRender内部ネットワーク経由で高速・無料です。
