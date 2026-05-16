# CLAUDE.md

## 前提条件

- **回答は必ず日本語で行うこと**
- コードの変更量が200行を超える可能性がある場合は、事前にユーザーに確認をとること
- 大きな変更を加える場合は、まず計画を立ててユーザーに提案し、承認を得てから実施すること
- 調査やデバッグにはサブエージェントを活用してコンテキストを節約してください

## ワークフロー指針

- 複数ファイルにまたがる変更・アーキテクチャ変更は **Plan Mode** で設計をユーザーに提示してから実施すること
- コミットは **明示的な指示があった場合のみ** 実行すること
- コード変更後はテストを実行し、失敗したら修正してから次に進むこと（例: `cd apps/back && ./gradlew test` / `cd apps/front && npm run lint`）
- 未知のコードは変更前に必ず読んで理解すること

## アプリ概要

**くまもと自主返納特典マップ** — 熊本県の運転免許自主返納者向け特典情報を地図上で表示・検索するWebアプリ。公共交通機関の割引特典（GTFS/OTP連携による経路探索）と商業施設の割引特典（マーカー表示）の2種類を提供する。

## 開発コマンド

### フロントエンド (`apps/front/`)

```bash
npm run serve          # 開発サーバー起動 (http://localhost:3000)
npm run build          # プロダクションビルド
npm run lint           # ESLint実行
npm run storybook      # Storybook起動 (http://localhost:6006)
npm run build-storybook # Storybookビルド
```

### バックエンド (`apps/back/`)

```bash
./gradlew bootRun      # Spring Boot起動 (http://localhost:8081)
./gradlew build        # ビルド・テスト
./gradlew test         # テストのみ実行
```

### Docker環境

```bash
docker compose up -d   # 全サービス起動（DB, pgAdmin, OTP含む）
```

サービスポート: Frontend:3000 / Backend:8081 / PostgreSQL:5432 / pgAdmin:65432 / OTP:8080 / Storybook:6006

## アーキテクチャ

### 全体構成

```
kumamoto_henno_map/
├── apps/front/   # Vue 3 + TypeScript フロントエンド
├── apps/back/    # Spring Boot 3 + Java 21 バックエンド
├── config/database/  # DDL/DML SQLファイル
├── config/otp/       # OpenTripPlanner設定・GTFSデータ
└── docs/             # Docsify設計書 (01_overview〜04_interfaces)
```

### フロントエンド (`apps/front/src/`)

**レイヤー構造:**
- `components/atoms/` — 基本UIコンポーネント（AppButton, AppTextField等）
- `components/molecules/` — 複合コンポーネント（AppLicenseInfo, AppSidebarTab等）
- `components/organisms/` — 機能コンポーネント（AppSearchBenefit, AppRouteGuidance等）
- `pages/` — ページコンポーネント（Vue Routerと対応）
- `stores/` — Pinia ストア（auth等）
- `dto/` — TypeScriptのDTO定義
- `utils/` — ユーティリティ（api.ts, validateUtils.ts等）

**ルーティング** (`router/index.ts`): Vue Router 4のHistory API。`requiresAuth: true` メタを持つルートは未ログイン時に `/login?redirect=...` にリダイレクト。

**APIクライアント** (`utils/api.ts`): AxiosベースのHTTPクライアント。Base URL は `VUE_APP_API_BASE_URL` を優先し、未設定時は同一オリジン前提の `'/kumamoto-henno-map/api'` を使用（開発時は proxy / 本番時はリバースプロキシ）。`Authorization: Bearer <TOKEN>` ヘッダーを付与。401レスポンス時は自動ログアウト・`/login` にリダイレクト。

**認証** (`stores/auth.ts`): Pinia store + JWT Bearer Token。アクセストークンはメモリ（Pinia state）のみ保持（XSS耐性）。リフレッシュトークンは HttpOnly Cookie で管理。`restoreSession()` は `App.vue` の `onMounted` ではなく、`main.ts` で `app.mount()` 前に `await authStore.restoreSession()` を実行し、ルートガード判定前にセッション復元を完了させる。

### バックエンド (`apps/back/src/main/java/io/github/taichi0373/kumamoto_henno_map/`)

**レイヤー構造:**
- `controller/` — REST APIエンドポイント
- `service/` — ビジネスロジック
- `repository/dao/` — Doma 2 DAOインターフェース
- `repository/entity/` — Domaエンティティ（`@Entity(metamodel = @Metamodel)`）
- `dto/` — データ転送オブジェクト
- `config/` — CORS, Security, 例外ハンドリング設定

**Doma 2パターン**: Entityqlとメタモデルクラスでタイプセーフクエリを記述。DAOは `@Dao` + `@ConfigAutowireable` を付与し、Entityqlを使うメソッドは `default` メソッドで実装。

**認証**: Spring Security STATELESS + JWT Bearer Token（jjwt 0.12.6）。アクセストークン1時間・リフレッシュトークン30日（HttpOnly Cookie・DB管理・ローテーション）。

### データベース

- PostgreSQL、テーブル名/カラム名は `UPPER_SNAKE_CASE`
- DDL: `config/database/DDL/TABLE/` / DML: `config/database/DML/TABLE/`
- Entityの `@Table`/`@Column` では小文字スネークケースを使用（PostgreSQLは大文字小文字を区別しない）
- スキーマ変更時: DDLファイル・DMLファイル・Entityクラスを同時更新

## 命名規則

| 対象 | 規則 | 例 |
|------|------|-----|
| Vueコンポーネント | `App` プレフィックス + PascalCase | `AppButton.vue`, `AppSearchBenefit.vue` |
| TS変数/関数 | camelCase | `usersModel`, `handleLogin` |
| TS定数 | UPPER_SNAKE_CASE | `MESSAGE_NO_001` |
| Javaクラス | PascalCase | `BenefitService`, `UsersEntity` |
| Javaフィールド/メソッド | camelCase | `benefitId`, `selectById` |
| Java定数 | UPPER_SNAKE_CASE | — |
| DBテーブル/カラム | UPPER_SNAKE_CASE | `BENEFIT`, `BENEFIT_NAME` |
| APIエンドポイント | kebab-case, RESTful | `/auth/login`, `/benefits` |
| Composable | `use` + PascalCase | `useMap`, `useAuth` |

## コーディング規約

詳細規約は以下を参照:

@.claude/rules/frontend.md
@.claude/rules/backend.md

**TypeScript / Vue 要点:**
- `<script setup>` + Composition API 必須、ファイル構成: `template → script → style`
- コメントは日本語で `/** */` 形式
- PrimeVue は atoms 層のみ使用、SCSS は `@use "@/assets/scss/base"` 必須

**Java 要点:**
- `Object` 型多用禁止（明示的な型を使用）
- 日本語JavaDocコメント必須
- Entity は `Serializable` 実装 + `serialVersionUID = 1L`

**Git 要点:**
- 必ず、docs/01_overview/naming_conventions.md の命名規則に沿って、`develop`からブランチ作成し、コミット、プッシュを行う
- コミットメッセージは日本語で簡潔に
例）fix: ○○不具合の修正

## 外部サービス

- **OTP (OpenTripPlanner)**: `http://localhost:8080/otp/routers/default/plan` — GTFS経路探索
- **MapLibre GL JS**: OpenStreetMapベースの地図表示

## ドキュメント

詳細設計は `docs/` ディレクトリ参照:
- `docs/01_overview/` — 概要, 技術スタック, 命名規則
- `docs/02_functions/` — 機能一覧, 画面一覧, 画面遷移図
- `docs/03_database/` — テーブル一覧, テーブル定義, ER図
- `docs/04_interfaces/` — 外部インターフェース仕様