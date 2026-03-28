# 外部インターフェース仕様

システムが外部システムやサービスと連携するためのインターフェース詳細仕様を説明します。

## API構成図

```text
graph LR
    subgraph "Frontend"
        FE[Vue.js アプリケーション]
    end

    subgraph "Backend API"
        API[Spring Boot REST API]
        AUTH[JWT認証フィルター]
    end

    subgraph "Database"
        DB[PostgreSQL]
    end

    subgraph "External Services"
        OTP[OpenTripPlanner]
        OSM[OpenStreetMap]
        GTFS[GTFS Data Feed]
    end

    FE -->|JWT Cookie + CSRF Token| AUTH
    FE -->|HTTP/HTTPS| API
    AUTH --> API
    API -->|SQL| DB
    API -->|REST API| OTP
    OTP -->|Map Data| OSM
    OTP -->|Route Data| GTFS

    classDef frontend fill:#e3f2fd,stroke:#1976d2
    classDef backend fill:#fff3e0,stroke:#f57c00
    classDef database fill:#f1f8e9,stroke:#388e3c
    classDef external fill:#fce4ec,stroke:#c2185b

    class FE frontend
    class API,AUTH backend
    class DB database
    class OTP,OSM,GTFS external
```

## RESTful API仕様

### 認証方式

JWT (JSON Web Token) を HttpOnly Cookie で管理するステートレス認証を採用しています。

```text
sequenceDiagram
    participant C as クライアント
    participant API as Backend API
    participant DB as PostgreSQL

    C->>API: POST /auth/csrf (CSRFトークン取得)
    API-->>C: XSRF-TOKEN Cookie セット

    C->>API: POST /auth/login (username, password)
    API->>DB: ユーザー認証
    DB-->>API: 認証結果
    API-->>C: jwt Cookie (HttpOnly) セット + ユーザー情報返却

    C->>API: GET /users/{userId} (jwt Cookie 自動送信)
    API->>API: JwtAuthenticationFilter で検証
    API-->>C: ユーザー情報返却

    C->>API: PUT /users (jwt Cookie + X-XSRF-TOKEN ヘッダー)
    API->>API: CSRF + JWT 検証
    API-->>C: 更新完了

    C->>API: POST /auth/logout
    API-->>C: jwt Cookie 削除 (Max-Age=0)
```

**JWT Cookie 仕様**

| 属性 | 値 |
|---|---|
| 名前 | `jwt` |
| HttpOnly | `true`（JavaScript からアクセス不可） |
| Secure | `true`（本番）/ `false`（開発） |
| SameSite | `Strict` |
| Path | `/benefit-map/api` |
| Max-Age | 3600 秒（1時間） |

**CSRF 保護**

| 項目 | 内容 |
|---|---|
| 方式 | `CookieCsrfTokenRepository` (Spring Security) |
| Cookie 名 | `XSRF-TOKEN`（JS から読み取り可） |
| ヘッダー名 | `X-XSRF-TOKEN` |
| 適用対象 | POST / PUT / DELETE（認証不要エンドポイントを除く） |
| CSRF 除外パス | `/auth/**`、`POST /users/signup` |
