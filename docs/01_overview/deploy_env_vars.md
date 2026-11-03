# デプロイ環境変数一覧

## フロントエンド（Vercel）

| 変数名 | 説明 | 設定値 |
|--------|------|--------|
| `VITE_API_BASE_URL` | バックエンドAPIのベースURL | **設定不要** |

---

> **注意**: 本番環境では `VITE_API_BASE_URL` を Vercel に設定しないでください。未設定時はデフォルト値 `/kumamoto-henno-map/api`（相対パス）が使用され、Vercel のリライト設定によりバックエンドへプロキシされます。絶対URLを設定するとリライトを経由せず直接クロスオリジン通信となり、Cookie（`SameSite=Lax`）が送信されず認証が正常に機能しません。

## バックエンド（Render）

| 変数名 | 説明 | 設定例 | 必須 |
|--------|------|--------|------|
| `DB_HOST` | SupabaseのDBホスト名 | `db.xxxxx.supabase.co` | ✅ |
| `DB_PORT` | DBポート番号 | `5432` | ✅ |
| `DB_NAME` | DB名 | `postgres` | ✅ |
| `DB_USERNAME` | DBユーザー名 | `postgres` | ✅ |
| `DB_PASSWORD` | DBパスワード | Supabaseダッシュボードのパスワード | ✅ |
| `DB_SSL_PARAMS` | SSL接続パラメータ | `?sslmode=require` | ✅ |
| `JWT_SECRET` | JWTシークレットキー（32文字以上推奨） | ランダム文字列 | ✅ |
| `CORS_ALLOWED_ORIGINS` | 許可するオリジン（カンマ区切り） | `https://www.kumamoto-henno-map.com` | ✅ |
| `FRONTEND_BASE_URL` | パスワードリセットメールに使用するフロントURL | `https://www.kumamoto-henno-map.com` | ✅ |
| `FORWARD_HEADERS_STRATEGY` | リバースプロキシのヘッダー処理 | `FRAMEWORK` | ✅ |
| `OTP_API_URL` | OpenTripPlanner APIのURL | `https://otp.kumamoto-henno-map.com/otp/routers/default/plan` | ✅ |
| `RESEND_API_KEY` | Resend APIキー（`re_`から始まる文字列） | Resendダッシュボードで発行 | ✅ |
| `MAIL_FROM` | 送信元メールアドレス（Resendで認証済みドメインのアドレス） | `noreply@kumamoto-henno-map.com` | ✅ |
| `SLACK_WEBHOOK_URL` | フィードバック通知先のSlack Incoming Webhook URL | Slackアプリ設定 → Incoming Webhooks で発行 | - |
| `AWS_ACCESS_KEY_ID` | AWS アクセスキーID | IAMユーザーのアクセスキー | ✅ |
| `AWS_SECRET_ACCESS_KEY` | AWS シークレットアクセスキー | IAMユーザーのシークレットキー | ✅ |
| `AWS_REGION` | AWSリージョン | `us-east-1`（デフォルト） | - |
| `AWS_LOCATION_PLACE_INDEX` | Place Index名 | `kumamoto-henno-map-index`（デフォルト） | - |
| `ADMIN_USERNAME` | 起動時に作成・更新する管理者のユーザー名 | 任意の値 | - |
| `ADMIN_PASSWORD` | 管理者のパスワード | 強固なパスワード | - |
| `ADMIN_EMAIL` | 管理者のメールアドレス | `admin@kumamoto-henno-map.com` | - |
| `PORT` | 待受ポート（Renderが自動設定） | `8081`（デフォルト） | - |

---

> **注意（SLACK_WEBHOOK_URL）**: 未設定の場合はSlack送信をスキップしてサーバーログにのみ出力します（サービス継続に影響なし）。Webhook URLはSlackアプリの管理画面 → **Incoming Webhooks** で発行してください。

> **注意（ADMIN_USERNAME / ADMIN_PASSWORD / ADMIN_EMAIL）**: 3つすべてが設定されている場合のみ、起動時に管理者ユーザーを作成します。同名のユーザーが既に存在する場合は、パスワード・メールアドレスを上書きし管理者フラグを付与します。いずれかが未設定の場合は管理者の初期化をスキップします。

> **注意**: DBはSupabase（PostgreSQL）を使用しています。SupabaseはSSL接続が必須のため `DB_SSL_PARAMS=?sslmode=require` を必ず設定してください。接続情報はSupabaseダッシュボード → **Project Settings** → **Database** → **Connection string** から確認できます。
