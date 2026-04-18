# リサーチ・設計決定記録

---

## Summary

- **Feature**: `admin-crud`
- **Discovery Scope**: Extension（既存システムへの管理者機能追加）
- **Key Findings**:
  - 対象 8 テーブルすべての Doma 2 DAO・Entity が既に存在する（新規作成不要）
  - `CustomUserDetails.getAuthorities()` が `ROLE_USER` をハードコードしており、管理者ロール追加には USERS テーブルと Entity の変更が必要
  - フロントエンドは `apiClient`（Axios + Bearer Token インターセプター）・`AppPaginator`・`AppDialog` など、管理画面に流用できる Atom コンポーネントが既に揃っている

---

## Research Log

### 既存 DAO・Entity の調査

- **Context**: 管理者向け CRUD のバックエンドに必要なクラスが既に存在するか確認
- **Findings**:
  - DAOs: `BenefitDao`, `BenefitCategoryDao`, `AgencyDao`, `BenefitEligibilityDao`, `FareDiscountDao`, `CommunityBusDao`, `MunicipalityDao`, `UsersDao` — 全対象テーブルに存在
  - `BenefitDao`・`BenefitCategoryDao` は既に `@Insert`/`@Update`/`@Delete` を持つ
  - 全 DAO が `@Dao @ConfigAutowireable` + Entityql パターンに統一されている
- **Implications**: Admin Service は既存 DAO を DI して呼び出すだけでよい。新規 DAO は不要

### 管理者認証の調査

- **Context**: Spring Security にどう管理者ロールを追加するか
- **Findings**:
  - `CustomUserDetails.getAuthorities()` は現在 `ROLE_USER` を固定返却
  - USERS テーブルに `IS_ADMIN` カラムが存在しない
  - `SecurityConfig` の `hasRole('ADMIN')` は Spring Security 標準であり変更コスト低
- **Implications**:
  - DDL/Entity/CustomUserDetails の 3 点同時変更が必須（steering ルール：スキーマ変更時は DDL・DML・Entity を同時更新）
  - 既存 JWT 発行フローの変更は不要。`CustomUserDetails.getAuthorities()` の修正で `ROLE_ADMIN` が JWT クレームに自動反映される

### フロントエンド既存コンポーネント調査

- **Context**: 管理画面で再利用できる UI 部品の確認
- **Findings**:
  - `AppPaginator.vue` — ページネーション（管理画面のリスト表示に使用可能）
  - `AppDialog.vue` — モーダルダイアログ（登録・編集フォームに使用可能）
  - `AppBlockUI.vue` — ローディングオーバーレイ
  - `AppToastMessage.vue` — トースト通知
  - `AppDataTable` は Atom として存在しない → PrimeVue `DataTable` を直接 organism 層で使用するか、新規 Atom を追加する
- **Implications**: PrimeVue `DataTable` をベースとした `AppDataTable.vue` を atoms 層に追加する必要がある

### ページネーション設計

- **Context**: 大量レコードへの対応方式
- **Findings**:
  - Doma 2 Entityql には `limit(n).offset(n)` が利用可能
  - Spring の `Pageable` 連携も可能だが、既存 DAO は Pageable 非使用
  - 管理画面の対象データ量は数百〜数千件程度（業務マスター）
- **Implications**: `page` / `size` クエリパラメータを受け取り、DAO で `limit/offset` を適用する。レスポンスは `{ items, total, page, size }` の共通ページングレスポンス DTO を使用する

---

## Architecture Pattern Evaluation

| Option | Description | Strengths | Risks / Limitations |
|--------|-------------|-----------|---------------------|
| 既存パッケージに admin サブパッケージ追加 | `controller/admin/`, `service/admin/` を追加 | 既存構造と整合。Spring スキャン対象に自動追加 | ファイル数は増えるが責務は明確 |
| 既存 Controller/Service に CRUD メソッド追加 | BenefitController に admin エンドポイント追記 | ファイル数を抑制 | 権限制御が混在し保守性低下。採用しない |
| 専用 Admin モジュール（別 Spring Context） | 独立モジュール化 | 疎結合 | オーバーエンジニアリング。採用しない |

**選定**: `admin` サブパッケージ追加（既存パターンと整合、責務が明確）

---

## Design Decisions

### Decision: IS_ADMIN カラムの追加場所

- **Context**: 管理者識別方法の選択
- **Alternatives Considered**:
  1. USERS テーブルに `IS_ADMIN CHAR(1)` カラム追加
  2. 別テーブル ADMIN_USERS を作成
- **Selected Approach**: USERS テーブルに `IS_ADMIN CHAR(1) DEFAULT '0' NOT NULL` を追加
- **Rationale**: 既存の `UsersEntity` / `CustomUserDetails` の最小変更で管理者ロールを実現。別テーブルは JOIN が増え複雑化する
- **Trade-offs**: USERS テーブルが若干複雑化するが、管理者数は少なく問題なし
- **Follow-up**: DDL・DML・Entity を同時更新することを実装タスクで明示する

### Decision: フロントエンドの管理画面ルーティング

- **Context**: 管理画面を既存エンドユーザー画面と共存させる方法
- **Alternatives Considered**:
  1. 既存ルーターに `/admin/*` を追加し `meta: { requiresAdmin: true }` でガード
  2. 管理画面専用の別 Vue アプリとして分離
- **Selected Approach**: 既存 `router/index.ts` に管理者専用ルート群を追加
- **Rationale**: 同一 Vue アプリ内で Pinia の auth ストアを共有でき、追加のビルド設定が不要
- **Trade-offs**: ルーターが膨らむが、管理画面ページはモジュールとしてまとめて管理可能

### Decision: テーブルごとの AdminController / AdminService 分割

- **Context**: 8 テーブル分のエンドポイントを 1 クラスで持つか分割するか
- **Selected Approach**: エンティティごとに `AdminXxxController` + `AdminXxxService` を分割
- **Rationale**: 既存の `BenefitController` + `BenefitService` パターンと整合。単一責任原則を守り、将来の拡張・テストが容易
- **Trade-offs**: ファイル数は増えるが、1 クラス 1 責務で保守性が高い

### Decision: FareDiscount の複合主キー更新・削除

- **Context**: FARE_DISCOUNT テーブルの PK は (BENEFIT_ID, AGENCY_ID) の複合キー
- **Selected Approach**: DELETE / PUT のパスパラメータに `/{benefitId}/{agencyId}` を使用
- **Rationale**: RESTful 設計で複合 PK を URL に表現する標準的な方法
- **Follow-up**: Doma 2 Entity の複合 PK 削除は `@Delete` + 条件指定で実現可能か確認すること

---

## Risks & Mitigations

- **IS_ADMIN カラム追加でのデータ移行**: 既存 USERS レコードに `IS_ADMIN = '0'` をデフォルト適用。DDL に `DEFAULT '0'` を設定し既存データへの影響をゼロにする
- **管理者アカウントのシード不足**: DML に最低 1 件の管理者ユーザーを INSERT するマイグレーションを含める
- **既存 SecurityConfig の変更による既存 API への影響**: `/admin/**` のルールを既存 `anyRequest().permitAll()` より前に配置することで、既存 API への影響なし
- **FareDiscount の複合 PK DELETE**: Doma 2 の `@Delete` は Entity オブジェクト単位で削除するため、複合 PK のフィールドを Entity にセットしてから DELETE を呼ぶ実装が必要

---

## References

- Doma 2 Entityql ドキュメント: https://doma.readthedocs.io/en/latest/query/select/
- Spring Security `hasRole` 設定: https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
- PrimeVue DataTable: https://primevue.org/datatable/
