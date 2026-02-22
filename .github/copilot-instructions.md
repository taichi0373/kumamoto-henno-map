
# Copilot Instructions for 熊本県自主返納特典マップ

## このドキュメントについて

- GitHub Copilot や各種 AI ツールが本リポジトリのコンテキストを理解しやすくするためのガイドです。
- 新しい機能を実装する際はここで示す技術選定・設計方針・モジュール構成を前提にしてください。
- 不確かな点がある場合は、リポジトリのファイルを探索し、ユーザーに「こういうことですか?」と確認をするようにしてください。

## ドキュメント構成

詳細な設計書は `docs/` ディレクトリに整理されており、以下の構造になっています：

### 1. アプリケーション概要・仕様
- **概要**: [docs/01_overview/overview.md](docs/01_overview/overview.md) - アプリケーションの概要説明
- **技術スタック**: [docs/01_overview/tech_stack.md](docs/01_overview/tech_stack.md) - 使用技術の詳細
- **命名規則**: [docs/01_overview/naming_conventions.md](docs/01_overview/naming_conventions.md) - プロジェクト全体の命名規則

### 2. 機能仕様
- **機能一覧**: [docs/02_functions/feature_list.md](docs/02_functions/feature_list.md) - システムの全機能一覧
- **画面一覧**: [docs/02_functions/screen_list.md](docs/02_functions/screen_list.md) - 画面の一覧と説明
- **画面遷移図**: [docs/02_functions/screen_flow.md](docs/02_functions/screen_flow.md) - 画面間の遷移フロー

### 3. データ設計
- **テーブル一覧**: [docs/03_database/table_list.md](docs/03_database/table_list.md) - データベーステーブルの一覧
- **テーブル定義**: [docs/03_database/table_definitions.md](docs/03_database/table_definitions.md) - 各テーブルの詳細定義
- **ER図**: [docs/03_database/er_diagram.md](docs/03_database/er_diagram.md) - エンティティ関係図

### 4. 外部インターフェース
- **外部インターフェース一覧**: [docs/04_interfaces/interface_list.md](docs/04_interfaces/interface_list.md) - 外部システムとの接続点一覧
- **外部インターフェース仕様**: [docs/04_interfaces/interface_spec.md](docs/04_interfaces/interface_spec.md) - APIや外部サービスの仕様

## 前提条件

- 回答は必ず日本語でしてください。
- コードの変更をする際、変更量が200行を超える可能性が高い場合は、事前に「この指示では変更量が200行を超える可能性がありますが、実行しますか?」とユーザーに確認をとるようにしてください。
- 何か大きい変更を加える場合、まず何をするのか計画を立てた上で、ユーザーに「このような計画で進めようと思います。」と提案してください。この時、ユーザーから計画の修正を求められた場合は計画を修正して、再提案をしてください。

## アプリ概要

**熊本県自主返納特典マップ** は、熊本県の自主返納特典情報を地図上で表示・検索できるWebアプリケーションです。ユーザーは地図上で特典情報を確認し、条件に応じた絞り込み検索が可能です。特典は「公共交通機関の割引特典」と「商業施設の割引特典」の2種類があります。公共交通機関の割引特典は、GTFSデータを活用し、ユーザーが受けられる割引特典を反映した運賃を表示し、経路探索機能を提供します。商業施設の割引特典は、地図上にマーカー形式で表示し、ユーザーはポップアップで特典内容を確認できます。

### 主な機能

- 地図表示: 熊本県内の特典提供施設を地図上にマーカーで表示
- 特典検索: キーワードやカテゴリ、位置情報を基に特典を検索
- 絞り込み: ユーザーの条件（年齢、運転免許返納の返納有無、居住地域など）に応じた特典の絞り込み
- 経路探索: GTFSデータを活用し、ユーザーが受けられる割引特典を反映した運賃を表示し、経路探索機能を提供
- 特典詳細: 特典提供施設の詳細情報をポップアップで表示

## 技術スタック概要

- **言語**: TypeScript 5.9.3, JavaScript, Java 21
- **フロントエンド**:
  - **フレームワーク**: Vue 3.5.0
  - **ルーティング**: Vue Router 4.4.0
  - **ビルドツール**: Vue CLI 5.0.0
  - **UIコンポーネント**: PrimeVue 4.2.5, Storybook 8.6.14
  - **コンポーネントAPI**: Composition API 
  - **地図表示**: MapLibre GL JS 5.6.0
  - **スタイリング**: SCSS
- **バックエンド**:
  - **フレームワーク**: Spring Boot 3.3.5
  - **ORM**: Doma 2.60.0 (Spring Boot Starter 2.1.0)
  - **ビルドツール**: Gradle
  - **Domaコンパイラ**: Doma Processor 2.60.0
- **データベース**: PostgreSQL
- **認証**: API Key認証
- **経路探索**: OTP (OpenTripPlanner) 2.5.0
- **API通信**: RESTful API (Axios 1.13.4)
- **リンター/フォーマッター**: ESLint 8.57.0 + Prettier
- **CI/CD**: GitHub Actions
- **バージョン管理**: Git + GitHub
- **ドキュメント**: Markdown (Honkit)
- **コンテナ管理**: Docker

## プロジェクト構成と役割

本アプリは機能ベースのディレクトリ構成を採用し、関心の分離とスケーラビリティを実現しています。

```
benefit_map/
├── apps/
│   ├── front/
│   │   ├── .storybook/
│   │   ├── public/  
│   │   ├── src/
│   │   │   ├── assets/
│   │   │   │   └── css/
│   │   │   ├── cache/
│   │   │   ├── components/
│   │   │   │   ├── atoms/
│   │   │   │   ├── molecules/
│   │   │   │   └── organisms/
│   │   │   ├── dto/
│   │   │   ├── layouts/
│   │   │   ├── pages/
│   │   │   ├── router/
│   │   │   ├── utils/
│   │   │   ├── App.vue
│   │   │   └── main.ts
│   │   ├── package.json
│   │   ├── tsconfig.json
│   │   ├── vue.config.js
│   │   └── README.md
│   └── back/
│       ├── src/
│       │   ├── main/java/io/github/taichi0373/benefitmap/
│       │   │   ├── config/ 
│       │   │   ├── constants/ 
│       │   │   ├── controller/ 
│       │   │   ├── dto/
│       │   │   ├── repository/
│       │   │   │   ├── dao/
│       │   │   │   └── entity/
│       │   │   ├── service/
│       │   │   ├── util/
│       │   │   └── BenefitMapApplication.java
│       │   └── main/resources/
│       │       └── application.properties
│       ├── build.gradle
│       ├── lombok.config
│       ├── settings.gradle 
│       └── README.md
├── config/
│   ├── database/
│   │   ├── DDL/
│   │   │   ├── TABLE/            # テーブル定義配置場所
│   │   │   └── VIEW/             # ビュー定義配置場所
│   │   ├── DML/
│   │   │   ├── TABLE/            # テーブル初期データ配置場所
│   │   │   └── VIEW/             # ビュー初期データ配置場所
│   │   └── setup.sql/            # データベースセットアップスクリプト
│   └── otp
│        ├── data/                # GTFSデータ・OSMデータ配置場所
│        ├── build-graphs.sh      # グラフビルド用スクリプト
│        └── README.md            # OTP設定説明 
├── docs/
|   ├── 01_overview/              # アプリケーション概要・仕様
|   │   ├── overview.md           # アプリケーションの概要説明
|   │   ├── tech_stack.md         # 使用技術の詳細
|   │   └── naming_conventions.md # プロジェクト全体の命名規則
|   ├── 02_functions/             # 機能仕様
|   │   ├── feature_list.md       # システムの全機能一覧
|   │   ├── screen_list.md        # 画面の一覧と説明
|   │   └── screen_flow.md        # 画面間の遷移フロー
|   ├── 03_database/              # データ設計
|   │   ├── table_list.md         # データベーステーブルの一覧
|   │   ├── table_definitions.md  # 各テーブルの詳細定義
|   │   └── er_diagram.md         # エンティティ関係図
|   ├── 04_interfaces/            # 外部インターフェース
|   │   ├── interface_list.md     # 外部システムとの接続点一覧
|   │   └── interface_spec.md     # APIや外部サービスの仕様
|   ├── package.json              # ドキュメント生成用の設定
|   ├── README.md                 # 基本設計書のメインページ
|   └── SUMMARY.md                # ドキュメント構成のサマリー
└── .github/
    ├── workflows/
    │    ├── build-backend.yml        ＃ バックエンドビルド・テスト
    │    ├── build-document.yml       ＃ ドキュメントビルド・デプロイ
    │    ├── build-frontend.yml       ＃ フロントエンドビルド・テスト 
    │    ├── build-otp.yml            ＃ OTPビルド・テスト
    │    ├── pull-request-review.yml  ＃ プルリクエストレビュー
    │    ├── push-review.yml          ＃ プッシュレビュー
    │    └── review-precheck.yml      ＃ レビュープリチェック
    ├── PULL_REQUEST_TEMPLATE.md      ＃ プルリクエストテンプレート
    └── copilot-instructions.md       ＃ Copilot指示書
```

## vue コーディング規約

### 基本方針

- Vue 3 では `<script setup>` を原則使用する
- Composition API を使用する（Options APIは新規実装で使用しない）
- 1コンポーネント = 1責務とする
- 可読性・保守性を最優先とする
- ESLint / Prettier のルールに従う
- ロジックは可能な限り composables に分離する

### コンポーネント規約

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

### 命名規則

| 対象 | ルール |
|------|--------|
| ファイル名 | PascalCase |
| コンポーネント | PascalCase |
| 変数 | camelCase |
| boolean | is / has / can で開始 |
| 関数 | 動詞で開始 |
| CSSクラス | kebab-case（BEM準拠） |

### ディレクトリ規約
- `components/` はUIコンポーネント専用
  - `atoms/`: 単純なUI要素（例: ボタン、入力フィールド）
  - `molecules/`: 複数のatomsを組み合わせたコンポーネント（例: フォームグループ）
  - `organisms/`: 複数のmoleculesを組み合わせた複雑なコンポーネント（例: ヘッダー、サイドバー）
- `pages/` 画面単位は `pages`
- `layouts/` は共通レイアウトコンポーネント専用

### スタイル規約（SCSS）

- `<style scoped lang="scss">` を使用する
- 全コンポーネントで以下を必ず記述する

```scss
@use "@/assets/scss/base";
```

### PrimeVue 使用規約
- PrimeVue は atoms 層のみで使用する
- カスタムスタイルを当てる場合は、PrimeVueのクラス名を参考にする


## Javaコーディング規約

### 命名規則

- **変数・フィールド名**: `camelCase`を使用
- **クラス名**: `PascalCase`を使用
- **DBカラム名**: `snake_case`を使用（Entityの`@Column`アノテーションでマッピング）
- **定数**: `UPPER_SNAKE_CASE`を使用

### 禁止事項

- ❌ **`any`は使用しない** - 型安全性を保つため、明示的な型を使用すること

```java
// ❌ BAD
public void processData(Object any) { ... }

// ✅ GOOD
public void processData(String data) { ... }
```

### パッケージ構造

- パッケージ名: `io.github.taichi0373.benefit_map`
- レイヤー別の配置:
  - `controller`: REST APIエンドポイント
  - `service`: ビジネスロジック
  - `repository.dao`: データアクセス（Doma）
  - `repository.entity`: エンティティ（Doma）

### コメント

- クラス、メソッド、フィールドには日本語でJavaDocコメントを記述
- 複雑なロジックには説明コメントを追加


## Entity

### 基本構造

```java
@Entity(metamodel = @Metamodel)
@Table(name = "table_name")
public class EntityName implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    /** フィールド説明 */
    @Id
    @Column(name = "column_name")
    private String fieldName;
    
    /** システム共通フィールド */
    private SystemField systemField;
}
```

### 規約

- `Serializable`を実装
- `@Serial`アノテーションで`serialVersionUID`を定義
- フィールドには日本語のJavaDocコメントを記述
- `@Column(name = "...")`でDBカラム名（`snake_case`）をマッピング
- システム共通フィールドは`SystemField`型を使用

### フィールド命名

- Javaフィールド: `camelCase`
- DBカラム: `snake_case`（`@Column`で明示的に指定）

```java
// ✅ GOOD
@Column(name = "agency_id")
private String agencyId;

@Column(name = "agency_name")
private String agencyName;
```

## データベーススキーマ定義

### テーブル定義の場所

データベースのテーブル定義は以下の場所に配置されている：

- **DDL（テーブル定義）**: `config/database/DDL/TABLE/` ディレクトリ
  - 各テーブルごとに`TABLE_NAME.SQL`ファイルとして定義
  - `CREATE TABLE`文と`COMMENT`文を含む
  - 例: `BENEFIT.SQL`, `AGENCY.SQL`, `MUNICIPALITY.SQL`

- **DML（初期データ）**: `config/database/DML/TABLE/` ディレクトリ
  - 各テーブルごとに`TABLE_NAME.SQL`ファイルとして定義
  - `INSERT`文を含む

- **セットアップスクリプト**: `config/database/setup.sql`
  - DDLとDMLを順番に実行するスクリプト
  - Dockerコンテナの初期化時に使用される

### テーブル命名規則

- **テーブル名**: 大文字のスネークケース（例: `BENEFIT`, `AGENCY`, `MUNICIPALITY`）
- **カラム名**: 大文字のスネークケース（例: `BENEFIT_ID`, `BENEFIT_NAME`, `SYS_CREATED_AT`）
- **主キー制約**: `TABLE_NAME_PK`（例: `BENEFIT_PK`）
- **外部キー制約**: `TABLE_NAME_FK_REFERENCED_TABLE`（例: `BENEFIT_FK_MUNICIPALITY`）

### Entityとのマッピング

Entityクラスでは、`@Table`と`@Column`アノテーションで小文字のスネークケースを使用：

```java
// ✅ GOOD - Entityでのマッピング
@Table(name = "benefit")
public class BenefitEntity {
    @Column(name = "benefit_id")
    private String benefitId;
    
    @Column(name = "benefit_name")
    private String benefitName;
}
```

PostgreSQLは大文字小文字を区別しないため、小文字でマッピングしても正しく動作する。

### スキーマ変更時の注意

- テーブル定義を変更する場合は、`config/database/DDL/TABLE/`の該当ファイルを更新
- 初期データを変更する場合は、`config/database/DML/TABLE/`の該当ファイルを更新
- Entityクラスも同時に更新し、カラム名のマッピングを確認

## Dao

### 基本構造

```java
@Dao
@ConfigAutowireable
@SuppressWarnings("PMD.TooManyMethods")
public interface AgencyDao {
    
    /** 
     * 主キー検索
     */
    default AgencyEntity selectById(String id) {
        Entityql entityql = new Entityql(Config.get(this));
        AgencyEntity_ e = new AgencyEntity_();
        
        return entityql.from(e)
                      .where(c -> c.eq(e.id, id))
                      .fetchOne();
    }
    
    /**
     * 登録
     */
    @Insert
    int insert(AgencyEntity entity);
    
    /**
     * 更新
     */
    @Update
    int update(AgencyEntity entity);
    
    /**
     * 削除
     */
    @Delete
    int delete(AgencyEntity entity);
}
```

### 規約

- `@Dao`と`@ConfigAutowireable`を付与
- メソッドには日本語のJavaDocコメントを記述
- Entityqlを使用する場合は`default`メソッドで実装
- CRUD操作は`@Insert`, `@Update`, `@Delete`アノテーションを使用
- メタモデルクラス（`EntityName_`）を使用してタイプセーフなクエリを記述

### クエリパターン

```java
// 単一取得
return entityql.from(e)
              .where(c -> c.eq(e.id, id))
              .fetchOne();

// リスト取得
return entityql.from(e)
              .where(c -> c.eq(e.status, status))
              .fetch();

// 条件付き検索
return entityql.from(e)
              .where(c -> c.and(
                  c.eq(e.field1, value1),
                  c.like(e.field2, "%" + value2 + "%")
              ))
              .fetch();
```
