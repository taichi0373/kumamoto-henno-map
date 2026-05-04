# 自主返納特典マップ 設計書

## ドキュメント構成

### 1. 概要・アーキテクチャ
- [アプリケーション概要](01_overview/overview.md)
- [技術スタック](01_overview/tech_stack.md)
- [命名規則](01_overview/naming_conventions.md)

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
* [API仕様書](04_interfaces/api_spec.md)
* [OpenAPI 仕様書（ReDoc）](https://taichi0373.github.io/kumamoto-henno-map/openapi/)
* [サービスクラス仕様書（Javadoc）](https://taichi0373.github.io/kumamoto-henno-map/javadoc/)

## 技術スタック

**フロントエンド**: Vue 3 + TypeScript + Vue CLI

**バックエンド**: Java 21 + Spring Boot + Doma 2

**データベース**: PostgreSQL

**経路探索エンジン**: OTP 2.5.0

**その他**: Docker, GitHub Actions

## プロジェクト構成

```
kumamoto-henno-map/
├── apps/front/         # フロントエンド（Vue 3 + TypeScript + Vue CLI）
├── apps/back/          # バックエンド（Java 21 + Spring Boot + Doma 2）
├── config/database/    # データベース設定
├── config/otp/         # 経路探索設定
└── docs/               # 基本設計書
```
