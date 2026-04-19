# 要件定義書

## Project Description (Input)
管理者画面の実装 DDL/TABLEのテーブルに画面からのCRUD機能を作成する

## はじめに

本仕様は、熊本県自主返納特典マップの管理者向け画面を実装するものです。
管理者がWebブラウザからデータベース（BENEFIT・AGENCY・MUNICIPALITY・BENEFIT_CATEGORY・BENEFIT_ELIGIBILITY・FARE_DISCOUNT・COMMUNITY_BUS・USERS テーブル）を直接参照・登録・更新・削除できる CRUD インターフェースを提供します。

## スコープ

- **対象テーブル（管理対象）**: BENEFIT、BENEFIT_ELIGIBILITY、BENEFIT_CATEGORY、MUNICIPALITY、AGENCY、FARE_DISCOUNT、COMMUNITY_BUS、USERS
- **対象外テーブル**: REFRESH_TOKENS、PASSWORD_RESET_TOKENS（システム管理テーブルのため画面管理対象外）
- **隣接システムへの影響**: 既存エンドユーザー向け画面・APIへの変更は伴わない。管理者専用エンドポイント（`/admin/**`）を追加する

---

## 要件

### 要件 1: 管理者認証・アクセス制御

**Objective:** 管理者として、専用の認証方式でログインしたい。そうすることで、一般ユーザーが管理画面に不正アクセスするリスクをなくせる。

#### 受け入れ基準

1. The 管理画面 shall 管理者ロール（ROLE_ADMIN）を持つユーザーのみがアクセスできるよう認可制御を行う
2. When 管理者が正しい認証情報を入力してログインを試みた場合, the 認証サービス shall 管理者専用JWTアクセストークンを発行し管理画面トップへリダイレクトする
3. If 管理者ロールを持たないユーザーが `/admin/**` 以下のURLへアクセスした場合, the 管理画面 shall 403 エラーを返しアクセスを拒否する
4. While 管理者セッションが有効な場合, the 管理画面 shall 各CRUD画面へのナビゲーションメニューを表示する
5. When 管理者がログアウト操作を行った場合, the 認証サービス shall セッションを無効化しログイン画面へリダイレクトする
6. The USERS テーブル shall 管理者フラグ（IS_ADMIN）カラムを持ち、管理者と一般ユーザーを区別できる

---

### 要件 2: 特典管理（BENEFIT テーブル）

**Objective:** 管理者として、運転免許返納特典の一覧確認・新規登録・編集・削除を画面から行いたい。そうすることで、最新の特典情報をリアルタイムに反映できる。

#### 受け入れ基準

1. The 特典管理画面 shall BENEFIT テーブルの全レコードを一覧表示する（特典ID・特典名・自治体・カテゴリ・有効期限を含む）
2. When 管理者が特典一覧画面で検索条件（自治体・カテゴリ）を入力して検索を実行した場合, the 特典管理画面 shall 条件に合致する特典のみ絞り込んで表示する
3. When 管理者が新規登録ボタンを押下した場合, the 特典管理画面 shall 特典登録フォーム（BENEFIT_ID・MUNICIPALITY_CD・CATEGORY_CD・BENEFIT_NAME・BENEFIT_SHORT_NAME・BENEFIT_DETAIL・EXP_DETAIL・PHONE_NUMBER・BENEFIT_URL）を表示する
4. When 管理者が必須項目を入力して登録を確定した場合, the 管理API shall BENEFIT テーブルへレコードを挿入し SYS_CREATED_AT・SYS_UPDATED_AT を自動セットする
5. If 登録時に BENEFIT_ID が既存レコードと重複した場合, the 管理API shall 重複エラーメッセージを表示し登録を中断する
6. When 管理者が特典一覧から編集対象を選択した場合, the 特典管理画面 shall 対象レコードの現在値をフォームに表示する
7. When 管理者が編集内容を確定した場合, the 管理API shall BENEFIT テーブルの対象レコードを更新し SYS_UPDATED_AT を更新する
8. When 管理者が削除操作を実行した場合, the 管理API shall 削除確認ダイアログを表示し、確認後に BENEFIT テーブルから対象レコードを物理削除する
9. If 削除対象レコードに紐付く BENEFIT_ELIGIBILITY または FARE_DISCOUNT が存在する場合, the 管理API shall 関連データも連動して削除するか、エラーを返して削除を拒否する

---

### 要件 3: 特典条件管理（BENEFIT_ELIGIBILITY テーブル）

**Objective:** 管理者として、特典ごとの対象者条件（年齢・免許ステータス・対象自治体）を管理したい。そうすることで、正確な受給条件をユーザーに提供できる。

#### 受け入れ基準

1. The 特典条件管理画面 shall 特典ID（BENEFIT_ID）に紐付く BENEFIT_ELIGIBILITY レコードを一覧表示する
2. When 管理者が特典詳細画面から条件追加を選択した場合, the 特典条件管理画面 shall 条件登録フォーム（LICENSE_STATUS・MIN_AGE・MAX_AGE・MUNICIPALITY_CD・NOTE）を表示する
3. When 管理者が条件を登録した場合, the 管理API shall BENEFIT_ELIGIBILITY テーブルへレコードを挿入する
4. When 管理者が条件を編集・確定した場合, the 管理API shall 対象レコードを更新する
5. When 管理者が条件を削除した場合, the 管理API shall 対象レコードを物理削除する
6. If MIN_AGE が MAX_AGE より大きい値で入力された場合, the 管理画面 shall バリデーションエラーを表示し登録・更新を中断する

---

### 要件 4: 特典カテゴリ管理（BENEFIT_CATEGORY テーブル）

**Objective:** 管理者として、特典カテゴリのマスターデータを管理したい。そうすることで、特典分類の追加・変更を柔軟に行える。

#### 受け入れ基準

1. The カテゴリ管理画面 shall BENEFIT_CATEGORY テーブルの全レコードを表示順（DISPLAY_ORDER）でソートして一覧表示する
2. When 管理者が新規カテゴリを登録した場合, the 管理API shall BENEFIT_CATEGORY テーブルへレコードを挿入する
3. When 管理者がカテゴリ名・表示順・有効フラグを編集して確定した場合, the 管理API shall 対象レコードを更新する
4. If CATEGORY_CD が既存レコードと重複した場合, the 管理API shall 重複エラーを返す
5. When 管理者がカテゴリの IS_ACTIVE を 0（無効）に設定した場合, the カテゴリ管理画面 shall 無効カテゴリを視覚的に区別して表示する
6. If 削除対象カテゴリに紐付く BENEFIT レコードが存在する場合, the 管理API shall 削除を拒否し依存エラーメッセージを表示する

---

### 要件 5: 自治体管理（MUNICIPALITY テーブル）

**Objective:** 管理者として、自治体マスターデータを管理したい。そうすることで、熊本県内の自治体情報を最新状態に保てる。

#### 受け入れ基準

1. The 自治体管理画面 shall MUNICIPALITY テーブルの全レコードを一覧表示する（自治体コード・自治体名・自治体区分を含む）
2. When 管理者が新規自治体を登録した場合, the 管理API shall MUNICIPALITY テーブルへレコードを挿入する
3. When 管理者が自治体名・かな・区分を編集して確定した場合, the 管理API shall 対象レコードを更新する
4. If MUNICIPALITY_CD が既存レコードと重複した場合, the 管理API shall 重複エラーを返す
5. If 削除対象自治体に紐付く BENEFIT または USERS レコードが存在する場合, the 管理API shall 削除を拒否し依存エラーメッセージを表示する

---

### 要件 6: 事業者管理（AGENCY テーブル）

**Objective:** 管理者として、交通事業者のマスターデータを管理したい。そうすることで、路線バス・コミュニティバスの事業者情報を正確に維持できる。

#### 受け入れ基準

1. The 事業者管理画面 shall AGENCY テーブルの全レコードを一覧表示する（事業者ID・事業者名・電話番号を含む）
2. When 管理者が新規事業者を登録した場合, the 管理API shall AGENCY テーブルへレコードを挿入する
3. When 管理者が事業者情報（名称・かな・電話番号・URL・運行事業者ID）を編集して確定した場合, the 管理API shall 対象レコードを更新する
4. If 削除対象事業者に紐付く FARE_DISCOUNT または COMMUNITY_BUS レコードが存在する場合, the 管理API shall 削除を拒否し依存エラーメッセージを表示する

---

### 要件 7: 運賃割引管理（FARE_DISCOUNT テーブル）

**Objective:** 管理者として、特典に紐付く運賃割引情報を管理したい。そうすることで、交通事業者ごとの割引条件を正確に管理できる。

#### 受け入れ基準

1. The 運賃割引管理画面 shall FARE_DISCOUNT テーブルの全レコードを一覧表示する（特典ID・事業者ID・割引種別・割引値を含む）
2. When 管理者が運賃割引を新規登録した場合, the 管理API shall FARE_DISCOUNT テーブルへレコードを挿入する
3. When 管理者が割引種別・割引値を編集して確定した場合, the 管理API shall 対象レコードを更新する
4. When 管理者が運賃割引を削除した場合, the 管理API shall 対象レコードを物理削除する
5. If 指定した BENEFIT_ID または AGENCY_ID が存在しない場合, the 管理API shall 外部キーエラーメッセージを表示し登録・更新を中断する

---

### 要件 8: コミュニティバス路線管理（COMMUNITY_BUS テーブル）

**Objective:** 管理者として、コミュニティバス路線情報を管理したい。そうすることで、地域住民の移動手段情報を最新化できる。

#### 受け入れ基準

1. The コミュニティバス路線管理画面 shall COMMUNITY_BUS テーブルの全レコードを一覧表示する（路線ID・コミュニティバスID・行先名称を含む）
2. When 管理者が新規路線を登録した場合, the 管理API shall COMMUNITY_BUS テーブルへレコードを挿入する
3. When 管理者が路線名を編集して確定した場合, the 管理API shall 対象レコードを更新する
4. When 管理者が路線を削除した場合, the 管理API shall 対象レコードを物理削除する
5. If 指定した COMMUNITY_BUS_ID（AGENCY_ID）が AGENCY テーブルに存在しない場合, the 管理API shall 外部キーエラーメッセージを表示し登録・更新を中断する

---

### 要件 9: ユーザー管理（USERS テーブル）

**Objective:** 管理者として、一般ユーザーの登録情報を参照・編集・削除したい。そうすることで、不正アカウントの対処やデータ修正を管理者権限で行える。

#### 受け入れ基準

1. The ユーザー管理画面 shall USERS テーブルの全レコードを一覧表示する（ユーザーID・ユーザー名・メールアドレス・自治体・登録日時を含む）
2. When 管理者が検索条件（ユーザー名・メールアドレス）を入力して検索を実行した場合, the ユーザー管理画面 shall 条件に合致するユーザーのみ絞り込んで表示する
3. When 管理者がユーザー詳細を選択した場合, the ユーザー管理画面 shall 対象ユーザーの全情報（パスワードハッシュを除く）を表示する
4. When 管理者がユーザー情報を編集して確定した場合, the 管理API shall USERNAME・EMAIL・BIRTH_DATE・MUNICIPALITY_CD・LICENSE_STATUS・LICENSE_SURRENDERED_AT を更新する
5. The 管理API shall パスワードハッシュ（PASSWORD_HASH）を管理画面から直接編集させない
6. When 管理者がユーザーを削除した場合, the 管理API shall 削除確認ダイアログ表示後に USERS テーブルから対象レコードを物理削除する
7. If ユーザー削除時に USERNAME または EMAIL が UNIQUE 制約違反を起こしている場合, the 管理API shall エラーメッセージを返す

---

### 要件 10: CSVインポート・エクスポート

**Objective:** 管理者として、各管理画面でデータをCSVファイルで一括インポート・エクスポートしたい。そうすることで、大量データの初期投入やマスターデータ更新を効率的に行える。

#### 受け入れ基準

1. The 管理画面 shall 全管理エンティティ（BENEFIT・BENEFIT_ELIGIBILITY・BENEFIT_CATEGORY・MUNICIPALITY・AGENCY・FARE_DISCOUNT・COMMUNITY_BUS）の一覧をCSVファイルとしてエクスポートできる
2. When 管理者がCSVインポートダイアログでCSVファイルを選択してインポート実行した場合, the 管理API shall ファイルをパースし各行をDBへ登録または更新（upsert）する
3. The インポートCSV shall UTF-8（BOMあり・なし両対応）でエンコードされている必要がある
4. When インポート処理が完了した場合, the 管理画面 shall 登録件数・更新件数・失敗件数とエラー詳細を結果として表示する
5. If CSVの一部の行が不正なデータを含む場合, the 管理API shall 該当行をスキップして他の行の処理を継続し、エラー内容を結果に含める
6. The BENEFIT_ELIGIBILITY テーブルへのインポートは shall 常に新規INSERT（シーケンスによる自動採番PK）とする
7. The 管理画面 shall インポートダイアログにCSVの必須列・任意列を説明するガイダンスを表示する

---

### 要件 11: 共通 UI・UX 要件

**Objective:** 管理者として、全管理画面で一貫した操作感を得たい。そうすることで、操作ミスを防ぎ効率的に管理業務を遂行できる。

#### 受け入れ基準

1. The 管理画面 shall 全 CRUD 操作の完了時に成功・失敗のトースト通知を表示する
2. The 管理画面 shall 削除操作前に確認ダイアログを必ず表示する
3. If API からエラーレスポンスが返却された場合, the 管理画面 shall エラー内容をユーザーに分かりやすいメッセージで表示する
4. The 管理画面 shall 一覧表示にページネーションを実装し、大量レコードでも表示できる
5. The 管理画面 shall 既存のエンドユーザー画面（Vue + PrimeVue）と統一されたデザインシステムを使用する
6. While API リクエスト処理中の場合, the 管理画面 shall ローディングインジケーターを表示し二重送信を防止する
