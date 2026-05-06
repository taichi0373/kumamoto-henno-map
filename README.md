# くまもと自主返納特典マップ

熊本県の運転免許自主返納者向け特典情報を地図上で表示・検索するWebアプリです。

## 環境構成

| サービス | URL |
|---------|-----|
| フロントエンド | http://localhost:3000 |
| Storybook | http://localhost:6006 |
| バックエンド | http://localhost:8081 |
| PostgreSQL | localhost:5432 |
| pgAdmin | http://localhost:65432 |
| OTP | http://localhost:8080 |

## セットアップ

### Docker 起動

```bash
docker compose up -d
```

### ログ確認

```bash
docker compose logs -f
```

### Docker 停止・削除

```bash
docker compose down -v
```

## DB 接続確認

DB接続情報は `docker-compose.yml` を参照してください。

```bash
docker compose exec db bash
psql -U <DB_USERNAME> -d benefit_map
```

### テーブル確認

```bash
\dt
```

### ビュー確認

```bash
\dv
```

## pgAdmin 接続設定

| 項目 | 値 |
|------|-----|
| 名前 | benefit_map_db |
| ホスト名/アドレス | db |
| ポート番号 | 5432 |
| 管理用データベース | benefit_map |
| ユーザー名 | `docker-compose.yml` の `POSTGRES_USER` を参照 |
| パスワード | `docker-compose.yml` の `POSTGRES_PASSWORD` を参照 |

## テスト用DB（benefit_map_test）

E2Eテスト（Playwright）用のDBとして `benefit_map_test` が自動作成されます。

`docker compose up -d` の **初回起動時** に `config/database/setup-test.sh` が自動実行され、DDL・マスターデータが適用されます。

### テスト用DB接続確認

```bash
docker compose exec db psql -U <DB_USERNAME> -d benefit_map_test
```

### テスト用DBの再作成

スキーマ変更時はボリュームごと削除して再起動してください。

```bash
docker compose down -v
docker compose up -d
```

### E2Eテスト実行前の設定

`apps/front/.env.test` に以下を設定してください（`DB_NAME` は `_test` で終わることが必須）。

```
DB_HOST=<WSL IPアドレス>
DB_PORT=5432
DB_NAME=benefit_map_test
DB_USERNAME=<DB_USERNAME>
DB_PASSWORD=<DB_PASSWORD>
```
