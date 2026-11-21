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
| Storybook | http://localhost:6006（`npm run storybook` で起動） |

## 技術スタック

| レイヤー | 技術 |
|---------|------|
| フロントエンド | Vue 3 + TypeScript + Vite + Pinia + MapLibre GL JS |
| バックエンド | Java 21 + Spring Boot 4 + Doma 2 + JWT認証 |
| データベース | PostgreSQL 18 |
| 経路探索 | OpenTripPlanner 2.5.0（GTFS） |
| インフラ | Docker / Docker Compose / k3s + Helm |

## 環境構築

### 1. Docker 起動

PostgreSQL・pgAdmin・OTP を起動します。

```bash
docker compose up -d
```

PostgreSQL は `config/database/` を `/docker-entrypoint-initdb.d` にマウントしており、コンテナ初回作成時に `setup.sql` から DDL（テーブル・ビュー）と DML（初期データ）が投入されます。

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

## 開発コマンド

### フロントエンド (`apps/front/`)

```bash
npm run serve          # 開発サーバー起動 (http://localhost:3000)
npm run build          # プロダクションビルド
npm run lint           # ESLint実行
npm run test           # ユニットテスト実行
npm run test:watch     # ユニットテスト（ウォッチモード）
npm run test:e2e       # E2Eテスト実行
npm run test:e2e:ui    # E2EテストUI起動
npm run storybook      # Storybook起動 (http://localhost:6006)
npm run build-storybook # Storybookビルド
```

### バックエンド (`apps/back/`)

```bash
./gradlew bootRun      # Spring Boot起動 (http://localhost:8081)
./gradlew clean build  # ビルド・テスト
./gradlew test         # テストのみ実行
```

詳細は [`apps/back/README.md`](apps/back/README.md) を参照してください。

## Docker 操作

```bash
# ログ確認
docker compose logs -f

# 停止・コンテナと匿名ボリュームの削除
docker compose down -v
```

> DB のデータは postgres イメージの匿名ボリュームに保持されます（`docker-compose.yml` の `db-data` は宣言のみで未使用）。`docker compose down` でコンテナを削除すると、次回の `up` では新しいボリュームで DB が初期化されます。データを残したい場合は `docker compose stop` を使ってください。

## DB 操作

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
| ユーザー名 | user |
| パスワード | pass |

## ドキュメント

設計書は `docs/` に Docsify 形式で配置しており、[GitHub Pages](https://taichi0373.github.io/kumamoto-henno-map/) で公開しています。ローカルで確認する場合:

```bash
cd docs
npm install
npm run serve   # http://localhost:3000（フロントエンドと同じポートのため同時起動時は `npx docsify serve . -p 3001`）
```

| ドキュメント | 内容 |
|-------------|------|
| [`docs/`](docs/README.md) | 基本設計書（概要・機能・DB・API） |
| [`docs/01_overview/deploy_env_vars.md`](docs/01_overview/deploy_env_vars.md) | デプロイ環境変数一覧 |
| [`docs/01_overview/k3s_deploy.md`](docs/01_overview/k3s_deploy.md) | k3s デプロイ手順 |
| [`docs/01_overview/pdf_extraction_tool.md`](docs/01_overview/pdf_extraction_tool.md) | PDF特典抽出ツール（`tools/`） |
| [`config/otp/README.md`](config/otp/README.md) | OpenTripPlanner の設定・グラフビルド |
| [`CLAUDE.md`](CLAUDE.md) | 開発ルール・アーキテクチャ要点 |
