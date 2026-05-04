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
        AUTH[認証サービス]
    end
    
    subgraph "Database"
        DB[PostgreSQL]
    end
    
    subgraph "External Services"
        OTP[OpenTripPlanner]
        OSM[OpenStreetMap]
        GTFS[GTFS Data Feed]
    end
    
    FE -->|HTTP/HTTPS| API
    FE -->|API Key| AUTH
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
```text
flowchart TD
    A[クライアント] -->|API Key| B[認証チェック]
    B -->|Valid| C[APIアクセス許可]
    B -->|Invalid| D[401 Unauthorized]
    C --> E[レスポンス返却]
```
