# 熊本県自主返納特典マップ

熊本県の運転免許自主返納者向け特典情報を地図上で表示・検索するWebアプリです。

## サービス構成

| サービス | URL |
|---------|-----|
| フロントエンド | http://localhost:3000 |
| バックエンド | http://localhost:8081 |
| PostgreSQL | localhost:5432 |
| pgAdmin | http://localhost:65432 |
| OTP | http://localhost:8080 |
| Storybook | http://localhost:6006 |

## 環境構築

### 1. Docker 起動

PostgreSQL・pgAdmin・OTP を起動します。

```bash
docker compose up -d
```

### 2. バックエンド起動

```bash
cd apps/back
./gradlew bootRun
```

### 3. フロントエンド起動

```bash
cd apps/front
npm install
npm run serve
```

## Docker 操作

```bash
# ログ確認
docker compose logs -f

# 停止・ボリューム削除
docker compose down -v
```

## DB 操作

接続情報は `docker-compose.yml` の `POSTGRES_USER` / `POSTGRES_PASSWORD` を参照してください。

```bash
docker compose exec db psql -U user -d benefit_map
```

| psql コマンド | 説明 |
|--------------|------|
| `\dt` | テーブル一覧 |
| `\dv` | ビュー一覧 |

## pgAdmin 接続設定

| 項目 | 値 |
|------|-----|
| ホスト名/アドレス | db |
| ポート番号 | 5432 |
| 管理用データベース | benefit_map |
| ユーザー名 / パスワード | `docker-compose.yml` の `POSTGRES_USER` / `POSTGRES_PASSWORD` を参照 |

## テスト用DB（benefit_map_test）

`docker compose up -d` の初回起動時に `config/database/setup-test.sh` が自動実行され、`benefit_map_test` が作成されます。

スキーマ変更時はボリュームごと削除して再起動してください。

```bash
docker compose down -v && docker compose up -d
```

### E2Eテスト実行前の設定

`apps/front/.env.test` を作成して以下を設定してください（`DB_NAME` は `_test` で終わることが必須）。

```
DB_HOST=<WSL IPアドレス>
DB_PORT=5432
DB_NAME=benefit_map_test
DB_USERNAME=<DB_USERNAME>
DB_PASSWORD=<DB_PASSWORD>
```
