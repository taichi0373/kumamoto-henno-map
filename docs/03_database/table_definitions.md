# テーブル定義

本システムで使用するデータベーステーブルの詳細定義です。

## 1. AGENCY（事業者マスタテーブル）

運送事業者・サービス提供者の情報を管理するマスタテーブル。

| カラム名 | 型 | NOT NULL | 主キー | 外部キー | 概要 |
|----------|----|---------:|:------:|:--------:|------|
| AGENCY_ID | VARCHAR(20) | ✓ | ✓ | | 事業者ID |
| AGENCY_NAME | VARCHAR(100) | ✓ | | | 事業者名称 |
| AGENCY_KANA | VARCHAR(200) | | | | 事業者名称かな |
| PHONE_NUMBER | VARCHAR(20) | | | | 問い合わせ電話番号 |
| AGENCY_URL | VARCHAR(1000) | | | | 事業者URL |
| OPERATOR_ID | VARCHAR(20) | | | | 運行事業者ID |
| SYS_CREATED_AT | TIMESTAMP | | | | 作成日時 |
| SYS_UPDATED_AT | TIMESTAMP | | | | 更新日時 |

**制約**:
- 主キー制約: `AGENCY_PK` (AGENCY_ID)

## 2. MUNICIPALITY（自治体マスタテーブル）

熊本県内の自治体情報を管理するマスタテーブル。

| カラム名 | 型 | NOT NULL | 主キー | 外部キー | 概要 |
|----------|----|---------:|:------:|:--------:|------|
| MUNICIPALITY_CD | VARCHAR(6) | ✓ | ✓ | | 自治体コード |
| MUNICIPALITY_NAME | VARCHAR(200) | ✓ | | | 自治体名称 |
| MUNICIPALITY_KANA | VARCHAR(200) | | | | 自治体名称かな |
| MUNICIPALITY_TYPE | VARCHAR(1) | ✓ | | | 自治体区分（1:都道府県, 2:区, 3:市町村） |
| SYS_CREATED_AT | TIMESTAMP | | | | 作成日時 |
| SYS_UPDATED_AT | TIMESTAMP | | | | 更新日時 |

**制約**:
- 主キー制約: `MUNICIPALITY_PK` (MUNICIPALITY_CD)

## 3. BENEFIT_CATEGORY（特典種別マスタテーブル）

特典のカテゴリ分類を管理するマスタテーブル。

| カラム名 | 型 | NOT NULL | 主キー | 外部キー | 概要 |
|----------|----|---------:|:------:|:--------:|------|
| CATEGORY_CD | VARCHAR(20) | ✓ | ✓ | | 種別コード |
| CATEGORY_NAME | VARCHAR(200) | ✓ | | | 種別名称 |
| DISPLAY_ORDER | INTEGER | | | | 表示順 |
| IS_ACTIVE | CHAR(1) | ✓ | | | 有効フラグ（1:有効,0:無効） |
| SYS_CREATED_AT | TIMESTAMP | | | | 作成日時 |
| SYS_UPDATED_AT | TIMESTAMP | | | | 更新日時 |

**制約**:
- 主キー制約: `BENEFIT_CATEGORY_PK` (CATEGORY_CD)
- デフォルト値: IS_ACTIVE = '1'

## 4. BENEFIT（運転免許返納特典テーブル）

自主返納特典の詳細情報を管理するデータテーブル。

| カラム名 | 型 | NOT NULL | 主キー | 外部キー | 概要 |
|----------|----|---------:|:------:|:--------:|------|
| BENEFIT_ID | VARCHAR(20) | ✓ | ✓ | | 特典ID |
| MUNICIPALITY_CD | VARCHAR(6) | ✓ | | ✓ | 自治体コード |
| CATEGORY_CD | VARCHAR(20) | ✓ | | ✓ | カテゴリコード |
| BENEFIT_NAME | VARCHAR(400) | | | | 特典名称 |
| BENEFIT_SHORT_NAME | VARCHAR(200) | | | | 特典短縮名称 |
| BENEFIT_DETAIL | VARCHAR(1000) | | | | 特典内容 |
| EXP_DETAIL | VARCHAR(200) | | | | 有効期限 |
| PHONE_NUMBER | VARCHAR(50) | | | | 問い合わせ電話番号 |
| BENEFIT_URL | VARCHAR(1000) | | | | 特典URL |
| SYS_CREATED_AT | TIMESTAMP | | | | 作成日時 |
| SYS_UPDATED_AT | TIMESTAMP | | | | 更新日時 |

**制約**:
- 主キー制約: `BENEFIT_PK` (BENEFIT_ID)
- 外部キー制約: `BENEFIT_FK_MUNICIPALITY` (MUNICIPALITY_CD → MUNICIPALITY.MUNICIPALITY_CD)
- 外部キー制約: `BENEFIT_FK_CATEGORY` (CATEGORY_CD → BENEFIT_CATEGORY.CATEGORY_CD)

## 5. BENEFIT_ELIGIBILITY（運転免許返納特典条件テーブル）

特典適用条件を管理するデータテーブル。

| カラム名 | 型 | NOT NULL | 主キー | 外部キー | 概要 |
|----------|----|---------:|:------:|:--------:|------|
| ID | BIGSERIAL | ✓ | ✓ | | ID（自動採番） |
| BENEFIT_ID | VARCHAR(20) | ✓ | | ✓ | 特典ID |
| LICENSE_STATUS | VARCHAR(1) | | | | 運転免許所持状況 |
| MIN_AGE | INTEGER | | | | 最低年齢 |
| MAX_AGE | INTEGER | | | | 最高年齢 |
| MUNICIPALITY_CD | VARCHAR(6) | | | | 対象自治体コード |
| NOTE | VARCHAR(1000) | | | | 備考 |
| SYS_CREATED_AT | TIMESTAMP | | | | 作成日時 |
| SYS_UPDATED_AT | TIMESTAMP | | | | 更新日時 |

**制約**:
- 主キー制約: ID (自動採番)
- 外部キー制約: `BENEFIT_ELIGIBILITY_FK` (BENEFIT_ID → BENEFIT.BENEFIT_ID)

**LICENSE_STATUS値**:
- 0: 未所持
- 1: 所持
- 2: 返納
- 3: 失効
- 4: その他

## 6. USERS（ユーザーテーブル）

システム利用者の情報を管理するデータテーブル。

| カラム名 | 型 | NOT NULL | 主キー | 外部キー | 概要 |
|----------|----|---------:|:------:|:--------:|------|
| USER_ID | BIGSERIAL | ✓ | ✓ | | ユーザーID（自動採番） |
| USERNAME | VARCHAR(30) | ✓ | | | ユーザー名 |
| PASSWORD_HASH | VARCHAR(200) | ✓ | | | パスワード（ハッシュ値） |
| EMAIL | VARCHAR(200) | ✓ | | | メールアドレス |
| BIRTH_DATE | DATE | | | | 生年月日 |
| MUNICIPALITY_CD | VARCHAR(6) | | | ✓ | 自治体コード |
| LICENSE_STATUS | VARCHAR(1) | | | | 運転免許所持状況 |
| LICENSE_SURRENDERED_AT | DATE | | | | 運転免許返納日 |
| SYS_CREATED_AT | TIMESTAMP | | | | 作成日時 |
| SYS_UPDATED_AT | TIMESTAMP | | | | 更新日時 |

**制約**:
- 主キー制約: `USERS_PK` (USER_ID)
- ユニーク制約: `USERS_USERNAME_UNIQUE` (USERNAME)
- ユニーク制約: `USERS_EMAIL_UNIQUE` (EMAIL)
- 外部キー制約: `USERS_FK` (MUNICIPALITY_CD → MUNICIPALITY.MUNICIPALITY_CD)

**LICENSE_STATUS値**:
- 0: 未所持
- 1: 所持
- 2: 返納
- 3: 失効
- 4: その他

## 7. REFRESH_TOKENS（リフレッシュトークンテーブル）

JWT認証用リフレッシュトークンを管理するデータテーブル。平文トークンはDBに保存しない（Cookie のみに保持）。ローテーション・ログアウト時に失効フラグを立てる。

| カラム名 | 型 | NOT NULL | 主キー | 外部キー | 概要 |
|----------|----|---------:|:------:|:--------:|------|
| TOKEN_ID | BIGSERIAL | ✓ | ✓ | | トークンID（自動採番） |
| USER_ID | BIGINT | ✓ | | ✓ | ユーザーID |
| TOKEN_HASH | VARCHAR(64) | ✓ | | | リフレッシュトークンのSHA-256ハッシュ値 |
| EXPIRES_AT | TIMESTAMP | ✓ | | | トークン有効期限（発行から30日） |
| REVOKED | BOOLEAN | ✓ | | | 失効フラグ（ローテーション・ログアウト時に true） |
| SYS_CREATED_AT | TIMESTAMP | | | | 作成日時 |
| SYS_UPDATED_AT | TIMESTAMP | | | | 更新日時 |

**制約**:
- 主キー制約: TOKEN_ID (自動採番)
- ユニーク制約: `REFRESH_TOKENS_TOKEN_HASH_UNIQUE` (TOKEN_HASH)
- 外部キー制約: `REFRESH_TOKENS_FK` (USER_ID → USERS.USER_ID) ON DELETE CASCADE
- インデックス: `REFRESH_TOKENS_USER_ID_IDX` (USER_ID)
- デフォルト値: REVOKED = FALSE

## 8. PASSWORD_RESET_TOKENS（パスワードリセットトークンテーブル）

パスワードリセット用トークンを管理するデータテーブル。トークンはワンタイム使用で発行から30分間有効。

| カラム名 | 型 | NOT NULL | 主キー | 外部キー | 概要 |
|----------|----|---------:|:------:|:--------:|------|
| TOKEN_ID | BIGSERIAL | ✓ | ✓ | | トークンID（自動採番） |
| USER_ID | BIGINT | ✓ | | ✓ | ユーザーID |
| TOKEN | VARCHAR(64) | ✓ | | | リセットトークンのSHA-256ハッシュ値 |
| EXPIRES_AT | TIMESTAMP | ✓ | | | トークン有効期限（発行から30分） |
| USED | BOOLEAN | ✓ | | | 使用済みフラグ（ワンタイム使用制限） |
| SYS_CREATED_AT | TIMESTAMP | | | | 作成日時 |
| SYS_UPDATED_AT | TIMESTAMP | | | | 更新日時 |

**制約**:
- 主キー制約: TOKEN_ID (自動採番)
- ユニーク制約: `PASSWORD_RESET_TOKENS_TOKEN_UNIQUE` (TOKEN)
- 外部キー制約: `PASSWORD_RESET_TOKENS_FK` (USER_ID → USERS.USER_ID) ON DELETE CASCADE
- デフォルト値: USED = FALSE

## 9. COMMUNITY_BUS（コミュニティバス路線テーブル）

コミュニティバスの路線情報を管理するデータテーブル。

| カラム名 | 型 | NOT NULL | 主キー | 外部キー | 概要 |
|----------|----|---------:|:------:|:--------:|------|
| ROUTE_ID | VARCHAR(20) | ✓ | ✓ | | 路線ID |
| COMMUNITY_BUS_ID | VARCHAR(20) | | | ✓ | コミュニティバスID |
| ROUTE_NAME | VARCHAR(200) | | | | 行先名称 |
| SYS_CREATED_AT | TIMESTAMP | | | | 作成日時 |
| SYS_UPDATED_AT | TIMESTAMP | | | | 更新日時 |

**制約**:
- 主キー制約: `COMMUNITY_BUS_PK` (ROUTE_ID)
- 外部キー制約: `COMMUNITY_BUS_FK` (COMMUNITY_BUS_ID → AGENCY.AGENCY_ID)

## 10. FARE_DISCOUNT（運転免許返納特典運賃割引テーブル）

運賃割引の詳細情報を管理するデータテーブル。

| カラム名 | 型 | NOT NULL | 主キー | 外部キー | 概要 |
|----------|----|---------:|:------:|:--------:|------|
| BENEFIT_ID | VARCHAR(20) | ✓ | ✓ | ✓ | 特典ID |
| AGENCY_ID | VARCHAR(20) | ✓ | ✓ | ✓ | 事業者ID |
| DISCOUNT_TYPE | VARCHAR(1) | | | | 割引種別 |
| DISCOUNT_VALUE | INTEGER | | | | 割引値 |
| SYS_CREATED_AT | TIMESTAMP | | | | 作成日時 |
| SYS_UPDATED_AT | TIMESTAMP | | | | 更新日時 |

**制約**:
- 主キー制約: `FARE_DISCOUNT_PK` (BENEFIT_ID, AGENCY_ID)
- 外部キー制約: `FARE_DISCOUNT_BENEFIT_FK` (BENEFIT_ID → BENEFIT.BENEFIT_ID)
- 外部キー制約: `FARE_DISCOUNT_AGENCY_FK` (AGENCY_ID → AGENCY.AGENCY_ID)

**DISCOUNT_TYPE値**:
- 0: percentage（割合割引）
- 1: free（無料）

## ビュー定義

### V_BENEFIT_DETAIL（特典詳細ビュー）

特典情報・自治体情報・カテゴリ情報・利用条件を結合した読み取り専用ビュー。特典検索 API (`/benefit/search`, `/benefit/users/{userId}`) で使用する。

**結合テーブル**:
- `BENEFIT` INNER JOIN `MUNICIPALITY`
- `BENEFIT` INNER JOIN `BENEFIT_CATEGORY`
- `BENEFIT` LEFT JOIN `BENEFIT_ELIGIBILITY`

### V_FARE_DISCOUNT_ELIGIBILITY（運賃割引条件ビュー）

運賃割引と利用条件を結合した読み取り専用ビュー。経路探索時の運賃割引計算で使用する。

**結合テーブル**:
- `FARE_DISCOUNT` LEFT JOIN `BENEFIT_ELIGIBILITY`
