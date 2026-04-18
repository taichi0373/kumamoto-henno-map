# 実装タスク一覧: admin-crud

## Task Format
- `(P)` = 並列実行可能（前後のサブタスクと同時実行できる）
- `- [ ]*` = 任意テストカバレッジ（MVP 後に後回し可能）

---

- [ ] 1. Foundation: DB スキーマ変更・認証基盤・フロントエンドルーティング整備

- [x] 1.1 USERS テーブルへの IS_ADMIN カラム追加
  - `config/database/DDL/TABLE/USERS.SQL` に `IS_ADMIN CHAR(1) DEFAULT '0' NOT NULL` とコメントを追加する
  - `config/database/DML/TABLE/` に管理者シードユーザー（IS_ADMIN='1'）の INSERT 文を追加する
  - `UsersEntity` に `isAdmin` フィールド（`@Column(name = "is_admin")`）を追加する
  - `docker compose down -v && docker compose up -d` で DB を再初期化し、USERS テーブルに IS_ADMIN カラムが存在し管理者シードが登録されていることを確認できる
  - _Requirements: 1.6_

- [x] 1.2 CustomUserDetails と LoginResponse の管理者ロール対応
  - `CustomUserDetails.getAuthorities()` を `"1".equals(user.getIsAdmin())` を判定し `ROLE_ADMIN` または `ROLE_USER` を返すよう変更する
  - `LoginResponseDto` に `isAdmin` フィールド（`boolean`）を追加する
  - `AuthService` がログイン時に `isAdmin` を `LoginResponseDto` にセットするよう対応する
  - 管理者ユーザーでログインすると JWT 検証後に `getAuthorities()` が `ROLE_ADMIN` を返すことを単体テストで確認できる
  - _Requirements: 1.1, 1.2_

- [x] 1.3 SecurityConfig の /admin/** 保護と AdminPagedResponseDto の追加
  - `SecurityConfig.filterChain()` の `authorizeHttpRequests` 設定に `.requestMatchers("/admin/**").hasRole("ADMIN")` を既存ルールより前に追加する
  - `dto/admin/AdminPagedResponseDto.java` を `{ items, total, page, size }` の汎用ジェネリッククラスとして実装する（Serializable + serialVersionUID = 1L）
  - ROLE_USER のユーザーが `GET /admin/benefits` にアクセスした場合に `ApiResponseDto.error` 形式の 403 レスポンスが返ることを確認できる
  - _Requirements: 1.1, 1.2, 1.3_

- [x] 1.4 フロントエンドの管理者認証基盤とルーティング設定
  - `stores/auth.ts` にログインレスポンスの `isAdmin` を state に保存し `isAdmin` computed ゲッターを追加する
  - `router/index.ts` に `/admin/*` のルート群（`meta: { requiresAdmin: true }`）を追加し、ルートガードで `auth.isAdmin` を確認し false の場合は `/` にリダイレクトする
  - `dto/admin/adminDto.ts` に全管理対象エンティティの TypeScript 型定義（`AdminPagedResponse<T>`、各エンティティの DTO インターフェース）を定義する
  - 管理者でないユーザーが `/admin/benefits` に直接アクセスした場合に `/` にリダイレクトされることを Router ガードのユニットテストで確認できる
  - _Requirements: 1.1, 1.3, 1.4, 1.5_

---

- [x] 2. Foundation: 管理画面共通 UI コンポーネント

- [x] 2.1 (P) AppDataTable Atom コンポーネントの実装
  - `components/atoms/AppDataTable.vue` を PrimeVue `DataTable` の薄いラッパーとして実装し、`value`・`columns`・`loading`・`totalRecords`・`rows`・`first` の props を持たせる
  - `AppDataTable.stories.js` を作成し Storybook でカラム定義とサンプルデータを渡した描画を確認できる
  - `@page-change` イベントを emit してページネーションの変更を親に伝達できる
  - Storybook で `AppDataTable` にサンプルデータとカラムを渡すと PrimeVue DataTable が正しく描画されることを確認できる
  - _Requirements: 10.4, 10.5_
  - _Boundary: AppDataTable_

- [x] 2.2 (P) AdminSidebar と AdminLayout の実装
  - `components/organisms/AdminSidebar.vue` を管理対象テーブル 8 件への `/admin/*` リンクを持つナビゲーションとして実装する（既存 `AppMenubar` または AppLink を使用）
  - `layouts/AdminLayout.vue` を AdminSidebar と `<router-view>` で構成するレイアウトとして実装し、`/admin/*` ルートに適用する
  - `/admin/benefits` など管理画面のいずれかにアクセスするとサイドバーに全管理メニューが表示されることを確認できる
  - _Requirements: 1.4, 10.5_
  - _Boundary: AdminLayout, AdminSidebar_

---

- [x] 3. Backend Core: 管理者向け CRUD API（エンティティ別並列実装）

- [x] 3.1 (P) 特典管理・特典条件管理 API
  - `AdminBenefitController` を `@RequestMapping("/admin/benefits")` で実装し、ページング付き一覧 GET（`page`・`size`・`municipalityCd`・`categoryCd` でフィルター）・POST・PUT `/{benefitId}`・DELETE `/{benefitId}` を提供する
  - `AdminBenefitService` を実装し、DELETE 前に `BenefitEligibilityDao` と `FareDiscountDao` で依存チェックを行い存在する場合は例外をスローする。登録・更新時に `SYS_CREATED_AT`・`SYS_UPDATED_AT` を自動セットする
  - `AdminBenefitEligibilityController` を `@RequestMapping("/admin/benefit-eligibilities")` で実装し CRUD を提供する
  - `AdminBenefitEligibilityService` に `MIN_AGE > MAX_AGE` のバリデーションを実装する
  - ROLE_ADMIN で `GET /admin/benefits?page=0&size=5` が `AdminPagedResponseDto` 形式で返り、依存レコードありの DELETE が 409 を返すことを確認できる
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.9, 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_
  - _Boundary: AdminBenefitController, AdminBenefitService, AdminBenefitEligibilityController, AdminBenefitEligibilityService_

- [x] 3.2 (P) 特典カテゴリ管理 API
  - `AdminBenefitCategoryController` を `@RequestMapping("/admin/benefit-categories")` で実装し、全件 GET（表示順ソート）・POST・PUT `/{categoryCd}`・DELETE `/{categoryCd}` を提供する
  - `AdminBenefitCategoryService` に DELETE 前の BENEFIT 依存チェックと IS_ACTIVE 更新のロジックを実装する
  - `POST /admin/benefit-categories` で新規カテゴリが登録でき、BENEFIT 依存ありの DELETE が 409 を返すことを確認できる
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6_
  - _Boundary: AdminBenefitCategoryController, AdminBenefitCategoryService_

- [x] 3.3 (P) 自治体管理 API
  - `AdminMunicipalityController` を `@RequestMapping("/admin/municipalities")` で実装し CRUD を提供する
  - `AdminMunicipalityService` に DELETE 前の BENEFIT・USERS 依存チェックを実装する
  - BENEFIT または USERS に依存する MUNICIPALITY_CD の DELETE が 409 を返すことを確認できる
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_
  - _Boundary: AdminMunicipalityController, AdminMunicipalityService_

- [x] 3.4 (P) 事業者管理 API
  - `AdminAgencyController` を `@RequestMapping("/admin/agencies")` で実装し CRUD を提供する
  - `AdminAgencyService` に DELETE 前の FARE_DISCOUNT・COMMUNITY_BUS 依存チェックを実装する
  - FARE_DISCOUNT または COMMUNITY_BUS に依存する AGENCY_ID の DELETE が 409 を返すことを確認できる
  - _Requirements: 6.1, 6.2, 6.3, 6.4_
  - _Boundary: AdminAgencyController, AdminAgencyService_

- [x] 3.5 (P) 運賃割引管理 API
  - `AdminFareDiscountController` を `@RequestMapping("/admin/fare-discounts")` で実装し、一覧 GET・POST・PUT `/{benefitId}/{agencyId}`・DELETE `/{benefitId}/{agencyId}` を提供する（複合 PK）
  - `AdminFareDiscountService` に POST・PUT 時の BENEFIT_ID・AGENCY_ID 存在チェックを実装する
  - 複合 PK `DELETE /admin/fare-discounts/{benefitId}/{agencyId}` が対象レコードのみ削除されることを確認できる
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_
  - _Boundary: AdminFareDiscountController, AdminFareDiscountService_

- [x] 3.6 (P) コミュニティバス路線管理 API
  - `AdminCommunityBusController` を `@RequestMapping("/admin/community-buses")` で実装し CRUD を提供する
  - `AdminCommunityBusService` に POST・PUT 時の COMMUNITY_BUS_ID（AGENCY_ID）存在チェックを実装する
  - 存在しない AGENCY_ID を指定した POST が 400 または 409 エラーを返すことを確認できる
  - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_
  - _Boundary: AdminCommunityBusController, AdminCommunityBusService_

- [x] 3.7 (P) ユーザー管理 API
  - `AdminUsersController` を `@RequestMapping("/admin/users")` で実装し、検索条件付き一覧 GET・GET `/{userId}`・PUT `/{userId}`・DELETE `/{userId}` を提供する（POST は提供しない）
  - `AdminUsersService` の PUT 処理で `PASSWORD_HASH` と `IS_ADMIN` を更新対象から除外する
  - `GET /admin/users/{userId}` のレスポンスに `password_hash` フィールドが含まれないことを確認できる
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7_
  - _Boundary: AdminUsersController, AdminUsersService_

---

- [x] 4. Frontend Core: 管理画面ページ（エンティティ別並列実装）

- [x] 4.1 (P) 特典管理画面・特典条件管理画面
  - `pages/admin/AdminBenefitPage.vue` を実装し、`AppDataTable` + `AppPaginator` による一覧表示・自治体/カテゴリフィルター検索・`AppDialog` を使った登録/編集フォーム・`AppDialog` による削除確認を提供する
  - `pages/admin/AdminBenefitEligibilityPage.vue` を実装し、特典 ID フィルター付きの一覧と CRUD を提供する。MIN_AGE > MAX_AGE のクライアントサイドバリデーションを含む
  - 全 CRUD 操作で `AppBlockUI` によるローディングと `AppToastMessage` による完了通知が動作する。`editTarget` は `{ ...editTarget.value }` のシャローコピーで編集する
  - 特典登録 → トースト通知表示 → 一覧に追加、削除確認ダイアログ → キャンセルでデータ維持 の操作が確認できる
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 2.7, 3.1, 3.2, 3.3, 3.4, 3.5, 3.6, 10.1, 10.2, 10.3, 10.4, 10.6_
  - _Boundary: AdminBenefitPage, AdminBenefitEligibilityPage_

- [x] 4.2 (P) 特典カテゴリ管理画面
  - `pages/admin/AdminBenefitCategoryPage.vue` を実装し、表示順ソート付き一覧・CRUD・IS_ACTIVE 切り替えを提供する
  - IS_ACTIVE='0'（無効）のカテゴリが一覧上で視覚的に区別されて表示される（例: 薄いテキスト・バッジ）
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 10.1, 10.2, 10.3, 10.6_
  - _Boundary: AdminBenefitCategoryPage_

- [x] 4.3 (P) 自治体管理画面
  - `pages/admin/AdminMunicipalityPage.vue` を実装し、一覧・CRUD フォームを提供する
  - 自治体区分（MUNICIPALITY_TYPE）を選択可能な AppSelect で入力できる
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 10.1, 10.2, 10.3, 10.6_
  - _Boundary: AdminMunicipalityPage_

- [x] 4.4 (P) 事業者管理画面
  - `pages/admin/AdminAgencyPage.vue` を実装し、一覧・CRUD フォームを提供する
  - 事業者名・かな・電話番号・URL・運行事業者 ID の全フィールドが登録・編集フォームに表示される
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 10.1, 10.2, 10.3, 10.6_
  - _Boundary: AdminAgencyPage_

- [x] 4.5 (P) 運賃割引管理画面
  - `pages/admin/AdminFareDiscountPage.vue` を実装し、一覧・登録・更新・削除を提供する
  - DELETE リクエストに `benefitId` と `agencyId` の両方をパスパラメータとして送信する複合 PK の処理が実装されている
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 10.1, 10.2, 10.3, 10.6_
  - _Boundary: AdminFareDiscountPage_

- [x] 4.6 (P) コミュニティバス路線管理画面
  - `pages/admin/AdminCommunityBusPage.vue` を実装し、一覧・CRUD フォームを提供する
  - COMMUNITY_BUS_ID（事業者 ID）を AppSelect で選択できる（事業者一覧 API から動的取得）
  - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5, 10.1, 10.2, 10.3, 10.6_
  - _Boundary: AdminCommunityBusPage_

- [x] 4.7 (P) ユーザー管理画面
  - `pages/admin/AdminUsersPage.vue` を実装し、ユーザー名・メールアドレスでの検索機能付き一覧・詳細表示・編集フォーム・削除確認ダイアログを提供する
  - 編集フォームにパスワードハッシュのフィールドを表示せず、IS_ADMIN の変更も受け付けない
  - パスワードハッシュが画面上のいかなる箇所にも表示されないことを確認できる
  - _Requirements: 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7, 10.1, 10.2, 10.3, 10.6_
  - _Boundary: AdminUsersPage_

---

- [x] 5. Integration: 統合テスト・バリデーション

- [x] 5.1 バックエンド認可統合テスト
  - JUnit 5 + `@SpringBootTest` + Spring Security Test で以下を確認する:
    - ROLE_ADMIN で `GET /admin/benefits` → 200 OK
    - ROLE_USER で `GET /admin/benefits` → 403 Forbidden（`ApiResponseDto.error` 形式）
    - 未認証で `GET /admin/benefits` → 401 Unauthorized
    - ROLE_ADMIN で `DELETE /admin/benefits/{id}`（依存レコードあり） → 409 Conflict
    - `PUT /admin/users/{id}` レスポンスに `passwordHash` が含まれない
  - 全 5 ケースのテストがグリーンになることを確認できる
  - _Depends: 1.3, 3.1, 3.7_
  - _Requirements: 1.1, 1.2, 1.3, 2.8, 2.9, 9.5_

- [x]* 5.2 E2E テスト（Playwright）
  - 管理者ログイン → `/admin/benefits` 表示 → 特典登録（必須項目入力）→ 登録成功トースト確認 → 特典削除（確認ダイアログ → 実行）フローを Playwright で実装する
  - 一般ユーザーで `/admin/benefits` にアクセスした場合に `/` にリダイレクトされることを確認する
  - ページネーション操作（2 ページ目に遷移）でページ 2 のデータが表示されることを確認する
  - 3 つのテストシナリオが Playwright でパスすることを確認できる
  - _Depends: 1.4, 2.2, 3.1, 4.1_
  - _Requirements: 1.1, 1.3, 2.1, 2.4, 2.8, 10.1, 10.2, 10.4_
