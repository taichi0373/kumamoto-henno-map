# 技術スタック

## アーキテクチャ

モノレポ構成（`apps/front/` + `apps/back/`）。フロントエンドとバックエンドは REST API で連携し、認証は STATELESS JWT + HttpOnly Cookie リフレッシュトークンで実装。

## コア技術

### フロントエンド（`apps/front/`）
- **言語**: TypeScript 5 (strict)
- **フレームワーク**: Vue 3（`<script setup>` + Composition API）
- **状態管理**: Pinia
- **ルーティング**: Vue Router 4（ルートガードで認証制御）
- **UIライブラリ**: PrimeVue 4（atoms 層のみで使用）
- **地図**: MapLibre GL 5
- **HTTP**: Axios（`Authorization: Bearer <token>` インターセプター）

### バックエンド（`apps/back/`）
- **言語**: Java 21
- **フレームワーク**: Spring Boot 3.3.5（STATELESS + CSRF 無効）
- **ORM**: Doma 2（Entityql + メタモデルでタイプセーフクエリ）
- **認証**: jjwt 0.12.6 / Spring Security
- **ドキュメント**: SpringDoc OpenAPI 2（`/benefit-map/api/v3/api-docs.yaml`）
- **メール**: Spring Mail（Mailtrap SMTP）

### インフラ
- **DB**: PostgreSQL（Docker Compose）
- **外部サービス**: OTP（OpenTripPlanner、経路案内）

## 開発標準

### 型安全
- フロント: `any` 禁止、明示的な型付け必須
- バック: `Object` 型多用禁止、明示的な型を使用

### コード品質
- フロント: ESLint (`plugin:vue/vue3-essential`) + Prettier
- バック: JavaDoc（日本語）必須、Lombok でボイラープレート削減

### テスト
- ユニット: Jest（フロント）/ JUnit 5（バック）
- E2E: Playwright（専用テスト DB `benefit_map_test` を使用）
- コンポーネント: Storybook 8

## 開発コマンド

```bash
# フロントエンド（localhost:3000）
npm run serve

# バックエンド（localhost:8081）
./gradlew bootRun

# Docker（PostgreSQL / pgAdmin / OTP）
docker compose up -d

# E2Eテスト
npm run test:e2e

# Storybook（localhost:6006）
npm run storybook
```

## 重要な技術的決定

- **アクセストークン**: Pinia ステート（インメモリ）にのみ保存、localStorage/sessionStorage は使用しない（XSS 耐性）
- **リフレッシュトークン**: HttpOnly Cookie + DB 管理 + ローテーション（再使用検知）
- **Doma 2**: JPQL ではなく SQL ファースト ORM。DDL・DML・Entity を必ず同時更新する
- **プロファイル**: `dev` プロファイルをローカルデフォルト、本番は `SPRING_PROFILES_ACTIVE` 環境変数で上書き

---
_Document: パターンと標準。全依存ライブラリの列挙ではなく_
