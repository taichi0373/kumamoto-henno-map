# くまもと自主返納特典マップ 設計書

## ドキュメント構成

### 1. 概要・アーキテクチャ
- [アプリケーション概要](01_overview/overview.md)
- [技術スタック](01_overview/tech_stack.md)
- [命名規則](01_overview/naming_conventions.md)
- [デプロイ環境変数一覧](01_overview/deploy_env_vars.md)
- [k3s デプロイ手順](01_overview/k3s_deploy.md)
- [リリースワークフロー](01_overview/release_workflow.md)
- [PDF特典抽出ツール](01_overview/pdf_extraction_tool.md)

### 2. 機能・画面仕様
- [機能一覧](02_functions/feature_list.md)
- [画面一覧](02_functions/screen_list.md)
- [画面遷移図](02_functions/screen_flow.md)

### 3. データベース設計
- [テーブル一覧](03_database/table_list.md)
- [テーブル定義](03_database/table_definitions.md)
- [ER図](03_database/er_diagram.md)

### 4. UIコンポーネント
- [コンポーネント仕様書](https://taichi0373.github.io/kumamoto-henno-map/storybook/)

### 5. API仕様
- [API仕様書](04_interfaces/api_spec.md)
- [OpenAPI 仕様書（ReDoc）](https://taichi0373.github.io/kumamoto-henno-map/openapi/)
- [サービスクラス仕様書（Javadoc）](https://taichi0373.github.io/kumamoto-henno-map/javadoc/)

## 技術スタック

**フロントエンド**: Vue 3 + TypeScript + Vite

**バックエンド**: Java 21 + Spring Boot 4 + Doma 2

**データベース**: PostgreSQL（本番: Supabase）

**経路探索エンジン**: OTP 2.5.0

**その他**: Docker, GitHub Actions, k3s + Helm

## プロジェクト構成

```
kumamoto-henno-map/
├── apps/front/         # フロントエンド（Vue 3 + TypeScript + Vite）
├── apps/back/          # バックエンド（Java 21 + Spring Boot + Doma 2）
├── config/database/    # DDL・DML・移行SQL（ALT）
├── config/otp/         # 経路探索（OpenTripPlanner）設定・GTFSデータ
├── deploy/             # k3s 用 Helm チャート・デプロイスクリプト
├── tools/              # PDF特典抽出ツール（Python）
├── docs/               # 基本設計書（Docsify）
├── .github/            # GitHub Actions ワークフロー・Dependabot 設定
└── docker-compose.yml  # ローカル開発用（PostgreSQL・OTP）
```
