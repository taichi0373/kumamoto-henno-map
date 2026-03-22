# 自主返納特典マップ

## システム概要

熊本県の自主返納特典情報を地図上で表示・検索できるWebアプリケーション。特典施設の地図表示、条件絞り込み検索、管理機能、経路探索機能を提供。

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
- [コンポーネントカタログ](https://taichi0373.github.io/benefit-map/storybook/)

### 5. 外部インターフェース
- [インターフェース一覧](04_interfaces/interface_list.md)
- [インターフェース仕様](04_interfaces/interface_spec.md)

## 技術スタック

**フロントエンド**: Vue 3 + TypeScript + Vue CLI

**バックエンド**: Java 21 + Spring Boot + Doma 2

**データベース**: PostgreSQL

**経路探索エンジン**: OTP 2.5.0

**その他**: Docker, GitHub Actions

## プロジェクト構成

```
benefit_map/
├── apps/front/         # フロントエンド（Vue 3 + TypeScript + Vue CLI）
├── apps/back/          # バックエンド（Java 21 + Spring Boot + Doma 2）
├── config/database/    # データベース設定
├── config/otp/         # 経路探索設定
└── docs/               # 基本設計書
```
