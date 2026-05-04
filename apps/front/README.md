## フロントエンド

Vue 3 + TypeScript + Pinia による SPA です。

### npm コマンド

```bash
# 依存関係のインストール
npm install

# 開発サーバー起動（http://localhost:3000）
npm run serve

# プロダクションビルド
npm run build

# Lint 実行
npm run lint

# Storybook 起動（http://localhost:6006）
npm run storybook

# Storybook ビルド
npm run build-storybook
```

### テスト

```bash
# ユニットテスト（Jest）
npm run test

# ユニットテスト（ウォッチモード）
npm run test:watch

# E2Eテスト（Playwright）
npm run test:e2e

# E2Eテスト（UIモード）
npm run test:e2e:ui
```

**E2Eテストの前提条件:** `.env.test` を作成してください（下記参照）。

### テスト用DB（kumamoto_henno_map_test）

`docker compose up -d` の初回起動時に `config/database/setup-test.sh` が自動実行され、`kumamoto_henno_map_test` が作成されます。

スキーマ変更時はボリュームごと削除して再起動してください。

```bash
docker compose down -v && docker compose up -d
```

`.env.test` をこのディレクトリ（`apps/front/`）に作成して以下を設定してください（`DB_NAME` は `_test` で終わることが必須）。

```
DB_HOST=<WSL IPアドレス>
DB_PORT=5432
DB_NAME=kumamoto_henno_map_test
DB_USERNAME=<DB_USERNAME>
DB_PASSWORD=<DB_PASSWORD>
```
