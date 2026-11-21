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
| `FRONTEND_BASE_URL` | パスワードリセットメール・メールアドレス確認メールに使用するフロントURL | `https://www.kumamoto-henno-map.com` | ✅ |
| `FORWARD_HEADERS_STRATEGY` | リバースプロキシのヘッダー処理（k3s 構成では `NATIVE`。[k3s デプロイ手順](k3s_deploy.md) 参照） | `FRAMEWORK` | ✅ |
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

> **注意（デフォルト値）**: `JWT_SECRET` / `RESEND_API_KEY` / `MAIL_FROM` は本番プロファイル（`application.properties`）にデフォルト値がなく、未設定だと起動に失敗します。その他の項目は `localhost` 等のデフォルト値で起動しますが、本番では表のとおり設定してください。

## バックエンド（ローカル開発 / dev プロファイル）

`./gradlew bootRun` では dev プロファイル（`application-dev.properties`）が有効になり、以下のデフォルト値が使われます。

| 変数名 | 説明 | デフォルト値 |
|--------|------|-------------|
| `JWT_SECRET` | JWTシークレットキー | 開発用の固定値 |
| `RESEND_API_KEY` | Resend APIキー | `dummy-key-for-dev` |
| `RESEND_SANDBOX` | `true` の場合はメールを実際に送信せずログ出力のみ（本番は `false` 固定） | `true` |
| `MAIL_FROM` | 送信元メールアドレス | `noreply@example.com` |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` / `ADMIN_EMAIL` | 起動時に作成する管理者 | `admin` / `admin` / `admin@example.com` |

## OpenTripPlanner（Docker Compose）

| 変数名 | 説明 | デフォルト値 |
|--------|------|-------------|
| `OTP_HEAP_SIZE` | OTP コンテナの最大ヒープサイズ（`-Xmx` に渡す） | `3g` |

## k3s

`deploy/scripts/deploy.sh` は `deploy/.env` を読み込み、Helm の values に変換して渡します。手順は [k3s デプロイ手順](k3s_deploy.md) を参照してください。

### `deploy/.env`

| 変数名 | 説明 | 必須 |
|--------|------|------|
| `VALUES_FILE` | 使用する values ファイル（例: `values-home.yaml`） | ✅ |
| `IMAGE_TAG` | デプロイするイメージタグ（コミット SHA。ローカル検証は `local`） | ✅ |
| `RELEASE_NAME` | Helm リリース名（デフォルト: `khm`） | - |
| `NAMESPACE` | デプロイ先の Namespace（デフォルト: `khm`） | - |
| `DB_ENABLED` | クラスタ内 DB を使うか（`false` で外部 DB に接続） | - |
| `EXTERNAL_DB_HOST` | 外部 DB（Supabase 等）のホスト名 | `DB_ENABLED=false` 時 |
| `EXTERNAL_DB_PORT` / `EXTERNAL_DB_NAME` / `EXTERNAL_DB_USERNAME` | 外部 DB のポート・DB名・ユーザー名（未設定時は values の `5432` / `postgres` / `postgres`。Supabase の Session pooler ではユーザー名が `postgres.<project-ref>` になるため設定が必要） | - |
| `DB_PASSWORD` | DB パスワード | ✅ |
| `JWT_SECRET` | JWTシークレットキー | ✅ |
| `RESEND_API_KEY` | Resend APIキー | ✅ |
| `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` | AWS Location Service の認証情報 | ✅ |
| `SLACK_WEBHOOK_URL` | Slack 通知先 | - |
| `ADMIN_USERNAME` / `ADMIN_PASSWORD` / `ADMIN_EMAIL` | 起動時に作成する管理者（本番 DB 共有中は設定しない） | - |
| `CLOUDFLARED_TOKEN` | Cloudflare Tunnel のトークン | cloudflared 有効時 |
| `DB_BACKUP_ENABLED` | R2 への日次バックアップを有効にするか | - |
| `R2_ENDPOINT` / `R2_ACCESS_KEY_ID` / `R2_SECRET_ACCESS_KEY` | Cloudflare R2 の接続情報 | バックアップ有効時 |

`ENV_FILE` を環境変数で指定すると、`deploy/.env` 以外のファイルを読み込めます。既に環境変数として設定されている値は `.env` より優先されます。

### values ファイルから back に渡す環境変数

Helm チャートは、DB 接続情報・アプリ設定・秘密情報（Render の表と同じ変数名）に加えて、以下を back コンテナに設定します（`values.yaml` の `back.*`）。

| 変数名 | values のキー | 説明 | デフォルト値 |
|--------|--------------|------|-------------|
| `JAVA_TOOL_OPTIONS` | `back.javaOpts` | JVM オプション | `-Xmx512m` |
| `TZ` | `back.timeZone` | タイムゾーン（Supabase を Render と共有する間は `UTC`） | `Asia/Tokyo` |
| `SPRING_DATASOURCE_HIKARI_MAXIMUMPOOLSIZE` | `back.dbMaxPoolSize` | DB 接続プールの最大数 | `10` |

`OTP_API_URL` はクラスタ内の OTP Service を指す値が自動で設定されます。
