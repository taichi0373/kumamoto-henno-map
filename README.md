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

## 技術スタック

| レイヤー | 技術 |
|---------|------|
| フロントエンド | Vue 3 + TypeScript + Vite + Pinia + MapLibre GL JS |
| バックエンド | Java 21 + Spring Boot 4 + Doma 2 + JWT認証 |
| データベース | PostgreSQL 18 |
| 経路探索 | OpenTripPlanner 2.5.0（GTFS） |
| インフラ | Docker / Docker Compose |

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

# 停止・ボリューム削除
docker compose down -v
```

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

