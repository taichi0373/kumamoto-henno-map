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

**E2Eテストの前提条件:** `.env.test` を作成してください（ルートの `README.md` 参照）。
