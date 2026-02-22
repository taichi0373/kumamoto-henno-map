# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 前提条件

- **回答は必ず日本語で行うこと**
- コードの変更量が200行を超える可能性がある場合は、事前にユーザーに確認をとること
- 大きな変更を加える場合は、まず計画を立ててユーザーに提案し、承認を得てから実施すること

## アプリ概要

**熊本県自主返納特典マップ** — 熊本県の運転免許自主返納者向け特典情報を地図上で表示・検索するWebアプリ。公共交通機関の割引特典（GTFS/OTP連携による経路探索）と商業施設の割引特典（マーカー表示）の2種類を提供する。

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
benefit_map/
├── apps/front/   # Vue 3 + TypeScript フロントエンド
├── apps/back/    # Spring Boot 3 + Java 21 バックエンド
├── config/database/  # DDL/DML SQLファイル
├── config/otp/       # OpenTripPlanner設定・GTFSデータ
└── docs/             # HonKit設計書 (01_overview〜04_interfaces)
```

### フロントエンド (`apps/front/src/`)

**レイヤー構造:**
- `components/atoms/` — 基本UIコンポーネント（AppButton, AppTextField等）
- `components/molecules/` — 複合コンポーネント（AppLicenseInfo, AppSidebarTab等）
- `components/organisms/` — 機能コンポーネント（AppSearchBenefit, AppRouteGuidance等）
- `pages/` — ページコンポーネント（Vue Routerと対応）
- `dto/` — TypeScriptのDTO定義
- `utils/` — ユーティリティ（api.ts, auth.ts, validateUtils.ts等）

**ルーティング** (`router/index.ts`): Vue Router 4のHistory API。`requiresAuth: true` メタを持つルートは未ログイン時に `/login?redirect=...` にリダイレクト。

**APIクライアント** (`utils/api.ts`): AxiosベースのHTTPクライアント。Base URL: `http://localhost:8081/benefit-map/api`。リクエスト時にlocalStorageから `X-API-Key` ヘッダーを付与。401レスポンス時は自動的にログアウト処理して `/login` にリダイレクト。

**認証** (`utils/auth.ts`): Vuex/Piniaなし。`localStorage`（remember me）または `sessionStorage` に `auth_token` / `user_info` を保存するシンプルなトークン方式（ダミートークン: `"session-authenticated"`）。

### バックエンド (`apps/back/src/main/java/io/github/taichi0373/benefit_map/`)

**レイヤー構造:**
- `controller/` — REST APIエンドポイント
- `service/` — ビジネスロジック
- `repository/dao/` — Doma 2 DAOインターフェース
- `repository/entity/` — Domaエンティティ（`@Entity(metamodel = @Metamodel)`）
- `dto/` — データ転送オブジェクト
- `config/` — CORS, Security, 例外ハンドリング設定

**Doma 2パターン**: Entityqlとメタモデルクラスでタイプセーフクエリを記述。DAOは `@Dao` + `@ConfigAutowireable` を付与し、Entityqlを使うメソッドは `default` メソッドで実装。

**認証**: Spring Security + JDBC Session（30分タイムアウト）。

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

### TypeScript / Vue

#### 基本方針

- Vue 3 では `<script setup>` を原則使用する
- Composition API を使用
- 1コンポーネント = 1責務とする
- 可読性・保守性を最優先とする
- ESLint / Prettier のルールに従う
- ロジックは可能な限り composables に分離する

#### コンポーネント規約

- 1コンポーネント = 1ファイル（PascalCase）
- ファイル構成は `template → script → style` の順とする
- `export default` は使用しない
- ロジックの記述順は以下とする

1. import  
2. props / emits  
3. 定数  
4. ref / reactive  
5. computed  
6. watch  
7. 関数  
8. ライフサイクル  

#### 命名規則

| 対象 | ルール |
|------|--------|
| ファイル名 | PascalCase |
| コンポーネント | PascalCase |
| 変数 | camelCase |
| boolean | is / has / can で開始 |
| 関数 | 動詞で開始 |
| CSSクラス | kebab-case（BEM準拠） |

#### ディレクトリ規約
- `components/` はUIコンポーネント専用
  - `atoms/`: 単純なUI要素（例: ボタン、入力フィールド）
  - `molecules/`: 複数のatomsを組み合わせたコンポーネント（例: フォームグループ）
  - `organisms/`: 複数のmoleculesを組み合わせた複雑なコンポーネント（例: ヘッダー、サイドバー）
- `pages/` 画面単位は `pages`
- `layouts/` は共通レイアウトコンポーネント専用

#### スタイル規約（SCSS）

- `<style scoped lang="scss">` を使用する
- 全コンポーネントで以下を必ず記述する

```scss
@use "@/assets/scss/base";
```

#### PrimeVue 使用規約
- PrimeVue は atoms 層のみで使用する
- カスタムスタイルを当てる場合は、PrimeVueのクラス名を参考にする



### Java
- `any` 相当（`Object`型の多用）は禁止 — 明示的な型を使用
- クラス・メソッド・フィールドには日本語JavaDocコメントを記述
- Entityは `Serializable` を実装し `@Serial private static final long serialVersionUID = 1L;` を定義

## 外部サービス

- **OTP (OpenTripPlanner)**: `http://localhost:8080/otp/routers/default/plan` — GTFS経路探索
- **Yahoo Local Search API**: POI検索（APIキー設定が必要）
- **MapLibre GL JS**: OpenStreetMapベースの地図表示

## ドキュメント

詳細設計は `docs/` ディレクトリ参照:
- `docs/01_overview/` — 概要, 技術スタック, 命名規則
- `docs/02_functions/` — 機能一覧, 画面一覧, 画面遷移図
- `docs/03_database/` — テーブル一覧, テーブル定義, ER図
- `docs/04_interfaces/` — 外部インターフェース仕様
