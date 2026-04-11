# テーブル一覧

本システムで使用するデータベーステーブルおよびビューの一覧です。

## テーブル概要

| テーブル名 | 論理名 | 概要 | 種別 |
|------------|--------|------|------|
| AGENCY | 事業者マスタ | 運送事業者・サービス提供者の情報を管理 | マスタ |
| MUNICIPALITY | 自治体マスタ | 熊本県内の自治体情報を管理 | マスタ |
| BENEFIT_CATEGORY | 特典種別マスタ | 特典のカテゴリ分類を管理 | マスタ |
| BENEFIT | 運転免許返納特典 | 自主返納特典の詳細情報を管理 | データ |
| BENEFIT_ELIGIBILITY | 運転免許返納特典条件 | 特典適用条件を管理 | データ |
| USERS | ユーザー | システム利用者の情報を管理 | データ |
| REFRESH_TOKENS | リフレッシュトークン | JWT認証用リフレッシュトークンを管理 | データ |
| PASSWORD_RESET_TOKENS | パスワードリセットトークン | パスワードリセット用トークンを管理 | データ |
| COMMUNITY_BUS | コミュニティバス路線 | コミュニティバスの路線情報を管理 | データ |
| FARE_DISCOUNT | 運転免許返納特典運賃割引 | 運賃割引の詳細情報を管理 | データ |

## ビュー概要

| ビュー名 | 論理名 | 概要 | 元テーブル |
|----------|--------|------|-----------|
| V_BENEFIT_DETAIL | 特典詳細ビュー | 特典・自治体・カテゴリ・利用条件を結合した読み取り専用ビュー | BENEFIT ⋈ MUNICIPALITY ⋈ BENEFIT_CATEGORY ⋈ BENEFIT_ELIGIBILITY |
| V_FARE_DISCOUNT_ELIGIBILITY | 運賃割引条件ビュー | 運賃割引と利用条件を結合した読み取り専用ビュー | FARE_DISCOUNT ⋈ BENEFIT_ELIGIBILITY |

## テーブル関係

### 主要エンティティ

- **AGENCY（事業者マスタ）**: システム全体の事業者情報を管理
- **MUNICIPALITY（自治体マスタ）**: 熊本県内の自治体を管理
- **BENEFIT（運転免許返納特典）**: システムの中核となる特典情報を管理
- **USERS（ユーザー）**: ログインユーザーを管理

### 依存関係

- `BENEFIT` → `MUNICIPALITY`（自治体コードで関連）
- `BENEFIT` → `BENEFIT_CATEGORY`（カテゴリコードで関連）
- `BENEFIT_ELIGIBILITY` → `BENEFIT`（特典IDで関連）
- `USERS` → `MUNICIPALITY`（自治体コードで関連）
- `REFRESH_TOKENS` → `USERS`（ユーザーIDで関連・ON DELETE CASCADE）
- `PASSWORD_RESET_TOKENS` → `USERS`（ユーザーIDで関連・ON DELETE CASCADE）
- `COMMUNITY_BUS` → `AGENCY`（事業者IDで関連）
- `FARE_DISCOUNT` → `BENEFIT`, `AGENCY`（特典ID・事業者IDで関連）

## システム共通フィールド

全テーブルに以下の共通フィールドを持ちます：

| フィールド名 | 型 | 概要 |
|-------------|----|----- |
| SYS_CREATED_AT | TIMESTAMP | 作成日時 |
| SYS_UPDATED_AT | TIMESTAMP | 更新日時 |
