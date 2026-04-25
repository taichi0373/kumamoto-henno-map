# ER図

本システムのデータベース設計におけるエンティティ関係図です。

> 全テーブル共通の `SYS_CREATED_AT`（作成日時）・`SYS_UPDATED_AT`（更新日時）は図から省略しています。

## ER図

```mermaid
erDiagram

    MUNICIPALITY {
        varchar MUNICIPALITY_CD
        varchar MUNICIPALITY_NAME
        varchar MUNICIPALITY_KANA
        varchar MUNICIPALITY_TYPE
    }

    BENEFIT_CATEGORY {
        varchar CATEGORY_CD
        varchar CATEGORY_NAME
        int     DISPLAY_ORDER
        char    IS_ACTIVE
    }

    BENEFIT {
        varchar BENEFIT_ID
        varchar MUNICIPALITY_CD
        varchar CATEGORY_CD
        varchar BENEFIT_NAME
        varchar BENEFIT_SHORT_NAME
        varchar BENEFIT_DETAIL
        varchar EXP_DETAIL
        varchar PHONE_NUMBER
        varchar BENEFIT_URL
    }

    BENEFIT_ELIGIBILITY {
        bigserial ID
        varchar   BENEFIT_ID
        varchar   LICENSE_STATUS
        int       MIN_AGE
        int       MAX_AGE
        varchar   MUNICIPALITY_CD
        varchar   NOTE
    }

    USERS {
        bigserial USER_ID
        varchar   USERNAME
        varchar   PASSWORD_HASH
        varchar   EMAIL
        date      BIRTH_DATE
        varchar   MUNICIPALITY_CD
        varchar   LICENSE_STATUS
        date      LICENSE_SURRENDERED_AT
    }

    REFRESH_TOKENS {
        bigserial TOKEN_ID
        bigint    USER_ID
        varchar   TOKEN_HASH
        timestamp EXPIRES_AT
        boolean   REVOKED
    }

    PASSWORD_RESET_TOKENS {
        bigserial TOKEN_ID
        bigint    USER_ID
        varchar   TOKEN
        timestamp EXPIRES_AT
        boolean   USED
    }

    AGENCY {
        varchar AGENCY_ID
        varchar AGENCY_NAME
        varchar AGENCY_KANA
        varchar PHONE_NUMBER
        varchar AGENCY_URL
        varchar OPERATOR_ID
    }

    COMMUNITY_BUS {
        varchar ROUTE_ID
        varchar COMMUNITY_BUS_ID
        varchar ROUTE_NAME
    }

    FARE_DISCOUNT {
        varchar BENEFIT_ID
        varchar AGENCY_ID
        varchar DISCOUNT_TYPE
        int     DISCOUNT_VALUE
    }

    MUNICIPALITY      ||--o{ BENEFIT             : provides
    MUNICIPALITY      ||--o{ USERS               : has
    BENEFIT_CATEGORY  ||--o{ BENEFIT             : classifies
    BENEFIT           ||--o{ BENEFIT_ELIGIBILITY : requires
    BENEFIT           ||--o{ FARE_DISCOUNT        : includes
    AGENCY            ||--o{ COMMUNITY_BUS        : operates
    AGENCY            ||--o{ FARE_DISCOUNT        : offers
    USERS             ||--o{ REFRESH_TOKENS       : owns
    USERS             ||--o{ PASSWORD_RESET_TOKENS : requests
```

---

## データ整合性制約

### 外部キー制約

| テーブル | 外部キー列 | 参照先 | ON DELETE |
|----------|-----------|--------|-----------|
| BENEFIT | MUNICIPALITY_CD | MUNICIPALITY.MUNICIPALITY_CD | - |
| BENEFIT | CATEGORY_CD | BENEFIT_CATEGORY.CATEGORY_CD | - |
| BENEFIT_ELIGIBILITY | BENEFIT_ID | BENEFIT.BENEFIT_ID | - |
| USERS | MUNICIPALITY_CD | MUNICIPALITY.MUNICIPALITY_CD | - |
| REFRESH_TOKENS | USER_ID | USERS.USER_ID | CASCADE |
| PASSWORD_RESET_TOKENS | USER_ID | USERS.USER_ID | CASCADE |
| COMMUNITY_BUS | COMMUNITY_BUS_ID | AGENCY.AGENCY_ID | - |
| FARE_DISCOUNT | BENEFIT_ID | BENEFIT.BENEFIT_ID | - |
| FARE_DISCOUNT | AGENCY_ID | AGENCY.AGENCY_ID | - |

### FARE_DISCOUNT の複合主キー

`BENEFIT_ID` と `AGENCY_ID` の組み合わせが主キー（複合 PK）。同一特典・同一事業者の割引情報は一意となる。

### USERS のユニーク制約

- `USERNAME` に一意制約（`USERS_USERNAME_UNIQUE`）が設定されており、重複ユーザー名は登録不可。
- `EMAIL` に一意制約（`USERS_EMAIL_UNIQUE`）が設定されており、重複メールアドレスは登録不可。

### REFRESH_TOKENS のユニーク制約

`TOKEN_HASH` に一意制約（`REFRESH_TOKENS_TOKEN_HASH_UNIQUE`）が設定されており、同一ハッシュ値のトークンは登録不可。

### PASSWORD_RESET_TOKENS のユニーク制約

`TOKEN` に一意制約（`PASSWORD_RESET_TOKENS_TOKEN_UNIQUE`）が設定されており、同一ハッシュ値のトークンは登録不可。
