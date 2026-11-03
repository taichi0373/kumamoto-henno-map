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

---

> **注意（SLACK_WEBHOOK_URL）**: 未設定の場合はSlack送信をスキップしてサーバーログにのみ出力します（サービス継続に影響なし）。Webhook URLはSlackアプリの管理画面 → **Incoming Webhooks** で発行してください。

> **注意**: DBはSupabase（PostgreSQL）を使用しています。SupabaseはSSL接続が必須のため `DB_SSL_PARAMS=?sslmode=require` を必ず設定してください。接続情報はSupabaseダッシュボード → **Project Settings** → **Database** → **Connection string** から確認できます。

### Supabase接続情報の確認方法

Supabaseダッシュボード → **Project Settings** → **Database** に表示される接続文字列から各変数を取り出す:

```
# Supabaseが表示する接続文字列（例）
postgresql://postgres:PASSWORD@db.xxxxx.supabase.co:5432/postgres?sslmode=require

# 各環境変数への対応
DB_HOST     = db.xxxxx.supabase.co
DB_PORT     = 5432
DB_NAME     = postgres
DB_USERNAME = postgres
DB_PASSWORD = PASSWORD
DB_SSL_PARAMS = ?sslmode=require
```

### SendGrid から Resend への移行手順

SendGrid の無料試用期間終了に伴い、メール送信を SendGrid HTTP API から Resend HTTP API に変更しました。既存環境をデプロイする前に以下を実施してください。

1. Resend ダッシュボード → **Domains** で送信元ドメイン（例: `kumamoto-henno-map.com`）を追加し、表示された DKIM・SPF の DNS レコードを登録して認証を完了する
2. Resend ダッシュボード → **API Keys** で APIキーを発行する（権限: Sending access、対象ドメインを限定）
3. Render の環境変数を以下のとおり更新し、バックエンドを再デプロイする

**追加する変数:**

| 変数名 | 値 |
|--------|-----|
| `RESEND_API_KEY` | 手順2で発行した APIキー |

**削除してよい変数:**

| 変数名 |
|--------|
| `SENDGRID_API_KEY` |

`MAIL_FROM` は変更不要（手順1で認証したドメインのアドレスであること）。

> **Resend ドメイン認証**: `MAIL_FROM` のドメインが Resend で認証済みでない場合、メール送信は 403 で失敗します。DNS で Cloudflare 等のプロキシ機能を使用している場合、CNAME レコードはプロキシを無効にしてください。

> **注意**: 送信に失敗してもパスワードリセット要求の API は列挙攻撃対策のため 200 を返します。移行後は実際にリセットメールが届くことを必ず確認し、届かない場合はバックエンドのログで `Resendメール送信エラー` を確認してください。

### SMTP から SendGrid HTTP API への移行手順（v1.0.1・旧手順）

v1.0.1 以降、メール送信を SMTP（`spring-boot-starter-mail`）から SendGrid HTTP API に変更しました。既存環境をデプロイする際は以下の手順で環境変数を更新してください。

**追加する変数:**

| 変数名 | 値 |
|--------|-----|
| `SENDGRID_API_KEY` | SendGrid ダッシュボード → Settings → API Keys で発行（Mail Send 権限） |
| `MAIL_FROM` | SendGrid で認証済みの送信元アドレス（例: `noreply@kumamoto-henno-map.com`） |

**削除してよい変数（SMTP設定）:**

| 変数名 |
|--------|
| `MAIL_HOST` |
| `MAIL_PORT` |
| `MAIL_USERNAME` |
| `MAIL_PASSWORD` |

> **SendGrid 送信者認証**: `MAIL_FROM` に設定するアドレスは SendGrid の Sender Authentication（Single Sender または Domain Authentication）で事前に認証が必要です。未認証のアドレスを設定するとメール送信が失敗します。
