# くまもと自主返納特典マップ

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
docker compose exec db psql -U user -d kumamoto_henno_map
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
| 管理用データベース | kumamoto_henno_map |
| ユーザー名 / パスワード | `docker-compose.yml` の `POSTGRES_USER` / `POSTGRES_PASSWORD` を参照 |

