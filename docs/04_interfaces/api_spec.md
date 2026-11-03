# API仕様

システムが提供する REST API の仕様です。

- ベース URL（本番・ブラウザ同一オリジン用 / www + リライト経由）: `https://www.kumamoto-henno-map.com/kumamoto-henno-map/api`
- ベース URL（本番・バックエンド直アクセス用 / api サブドメイン）: `https://api.kumamoto-henno-map.com/kumamoto-henno-map/api`
- ブラウザからフロントエンドと同一オリジンで利用する場合は `www` 側、API クライアント等から直接利用する場合は `api` 側を使用してください
- すべてのリクエストは `application/json`。ボディを持つレスポンスは `application/json`（204 No Content 等はボディなし）
- 認証が必要なエンドポイントは `Authorization: Bearer <token>` ヘッダーを付与する
- 通常の認証付き API は `Authorization` ヘッダーで Bearer Token を送信するため、Cookie 自動送信を前提とした CSRF の主対象ではない

> **OpenAPI 仕様書（ReDoc）** は GitHub Pages で公開されています。
> → [OpenAPI 仕様書（ReDoc）](https://taichi0373.github.io/kumamoto-henno-map/openapi/)
> → [openapi.yaml（ソース）](https://taichi0373.github.io/kumamoto-henno-map/openapi/openapi.yaml)

> **サービスクラス仕様書（Javadoc）** はコントローラーメソッドの引数・例外・内部処理の詳細を参照する場合に使用してください。
> → [サービスクラス仕様書（Javadoc）](https://taichi0373.github.io/kumamoto-henno-map/javadoc/)

---

## システム構成図

```mermaid
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
        MAIL[Resend HTTP API]
        SLACK[Slack Incoming Webhook]
        AWS[AWS Location Service]
    end

    FE -->|Authorization: Bearer Token| AUTH
    FE -->|HTTP/HTTPS| API
    AUTH --> API
    API -->|SQL| DB
    API -->|REST API| OTP
    API -->|HTTP API| MAIL
    API -->|HTTP POST| SLACK
    API -->|REST API| AWS
    OTP -->|Map Data| OSM
    OTP -->|Route Data| GTFS

    classDef frontend fill:#e3f2fd,stroke:#1976d2
    classDef backend fill:#fff3e0,stroke:#f57c00
    classDef database fill:#f1f8e9,stroke:#388e3c
    classDef external fill:#fce4ec,stroke:#c2185b

    class FE frontend
    class API,AUTH backend
    class DB database
    class OTP,OSM,GTFS,MAIL,SLACK,AWS external
```

---

## 認証方式

JWT Bearer Token + リフレッシュトークン（HttpOnly Cookie）によるステートレス認証を採用しています。

```mermaid
sequenceDiagram
    participant C as クライアント
    participant API as Backend API
    participant DB as PostgreSQL

    C->>API: POST /auth/login (username, password)
    API->>DB: ユーザー認証
    DB-->>API: 認証結果
    API-->>C: { token, userId, username } + refresh_token Cookie (HttpOnly)

    C->>API: GET /users/{userId}<br>Authorization: Bearer <token>
    API->>API: JwtAuthenticationFilter で検証
    API-->>C: ユーザー情報返却

    note over C,API: ページリロード時のセッション復元
    C->>API: POST /auth/refresh (Cookie: refresh_token)
    API->>DB: リフレッシュトークン検証・ローテーション
    API-->>C: { token } + 新 refresh_token Cookie

    C->>API: POST /auth/logout (Cookie: refresh_token)
    API->>DB: リフレッシュトークン失効
    API-->>C: 204 No Content + refresh_token Cookie 削除
```

**アクセストークン仕様**

| 項目 | 値 |
|---|---|
| 送信方式 | `Authorization: Bearer <token>` ヘッダー |
| 有効期限 | 1時間（3600秒） |
| フロントエンド保持 | Pinia state（メモリのみ・XSS耐性） |

**リフレッシュトークン Cookie 仕様**

| 属性 | 値 |
|---|---|
| 名前 | `refresh_token` |
| HttpOnly | `true`（JavaScript からアクセス不可） |
| Secure | `true`（本番）/ `false`（開発） |
| SameSite | `Lax` |
| Path | `${context-path}/auth` |
| Max-Age | 30日（2592000秒） |

---

## レスポンス共通形式

```json
{
  "success": true,
  "data": { ... },
  "message": null
}
```

| フィールド | 型 | 説明 |
|---|---|---|
| success | boolean | 処理成功時: `true`、エラー時: `false` |
| data | object / array / string / number / boolean / null | レスポンスデータ。データなしの場合も `null` として常に含まれる |
| message | string / null | エラー時のメッセージ。成功時は省略 |

---

## 1. 認証 (`/auth`)

### POST /auth/login

ログイン。認証成功時に JWT アクセストークンをレスポンスボディで返し、リフレッシュトークンを HttpOnly Cookie にセットする。

- **認証**: 不要

**リクエスト**

```json
{
  "username": "string",
  "password": "string"
}
```

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "taro",
    "isAdmin": false
  }
}
```

> `refresh_token` は Set-Cookie ヘッダー（HttpOnly）で返却される。

**レスポンス 401 Unauthorized**

```json
{ "success": false, "data": null, "message": "ユーザー名またはパスワードが正しくありません" }
```

**レスポンス 429 Too Many Requests** — ログイン試行回数超過（15分後に解除）

```json
{ "success": false, "data": null, "message": "ログイン試行回数が上限を超えました。しばらく時間をおいて再度お試しください。" }
```

> ログイン失敗が10回に達すると、同一IPアドレスからのログインを15分間ロックする。

**レスポンス 500 Internal Server Error**

```json
{ "success": false, "data": null, "message": "ログイン中にエラーが発生しました" }
```

---

### POST /auth/refresh

リフレッシュトークンを使ってアクセストークンを再発行する。Cookie の `refresh_token` を使用し、ローテーションにより新しいリフレッシュトークンも発行する。

- **認証**: 不要（Cookie: `refresh_token` が必要）

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": "1",
      "username": "taro",
      "isAdmin": false
    }
  }
}
```

> 新しい `refresh_token` が Set-Cookie ヘッダー（HttpOnly）で返却される。
> `user.id` は文字列型（`POST /auth/login` の `userId` は数値型）。

**レスポンス 401 Unauthorized** — Cookie なし、または無効・失効済みトークン（無効・失効時は Cookie も削除する）

```json
{ "success": false, "data": null, "message": "リフレッシュトークンがありません" }
```

```json
{ "success": false, "data": null, "message": "リフレッシュトークンが無効または期限切れです。再ログインしてください。" }
```

---

### POST /auth/logout

ログアウト。DB 上のリフレッシュトークンを失効させ、Cookie を削除する。

- **認証**: 不要（Cookie: `refresh_token` があれば失効処理する）

**レスポンス 204 No Content**（ボディなし）

---

### POST /auth/password-reset/request

パスワードリセットメールを送信する。指定メールアドレスにリセット用トークンURLを送信する。メールアドレスの存在有無・形式の正誤・メール送信の成否に関わらず同一レスポンス（200）を返す（列挙攻撃対策）。メール送信には Resend を使用する。

- **認証**: 不要

**リクエスト**

```json
{
  "email": "taro@example.com"
}
```

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": null
}
```

**レスポンス 429 Too Many Requests** — リセット要求回数超過（15分後に解除）

```json
{ "success": false, "data": null, "message": "リセット要求回数が上限を超えました。しばらく時間をおいて再度お試しください。" }
```

---

### POST /auth/password-reset/confirm

リセットトークンを使ってパスワードを変更する。トークンは発行から30分間有効で、一度使用すると無効になる。

- **認証**: 不要

**リクエスト**

```json
{
  "token": "abc123...",
  "newPassword": "newPassword123",
  "confirmNewPassword": "newPassword123"
}
```

**レスポンス 200 OK**

```json
{ "success": true, "data": null }
```

**レスポンス 400 Bad Request** — 入力値不正、またはトークンが無効・期限切れ・使用済み

| メッセージ | 原因 |
|---|---|
| すべての項目を入力してください | 必須項目未入力 |
| パスワードは8～64文字で入力してください | パスワード長不正 |
| 新しいパスワードと確認用パスワードが一致しません | 確認用パスワード不一致 |
| このリンクは無効または期限切れです。再度お試しください。 | トークンが無効・期限切れ・使用済み |

**レスポンス 429 Too Many Requests** — 試行回数超過（15分後に解除）

```json
{ "success": false, "data": null, "message": "試行回数が上限を超えました。しばらく時間をおいて再度お試しください。" }
```

**レスポンス 500 Internal Server Error**

```json
{ "success": false, "data": null, "message": "パスワードのリセットに失敗しました" }
```

---

## 2. ユーザー (`/users`)

### POST /users/signup

ユーザー登録（新規アカウント作成）。

- **認証**: 不要

**リクエスト**

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "birthDate": "2000-01-01",
  "address": "43100",
  "licenseStatus": "2"
}
```

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| username | string | ○ | ユーザー名（一意・3〜30文字・半角英数字/ハイフン/アンダースコアのみ） |
| password | string | ○ | パスワード（8〜64文字。平文。サーバー側でハッシュ化） |
| email | string | ○ | メールアドレス（一意・形式チェックあり） |
| birthDate | string (ISO 8601) | - | 生年月日 |
| address | string | - | 居住自治体コード |
| licenseStatus | string | - | 運転免許の所持状況 |

**レスポンス 201 Created**

```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "taro",
    "email": "taro@example.com",
    "birthDate": "2000-01-01",
    "address": "43100",
    "licenseStatus": "2",
    "licenseSurrenderedAt": null
  }
}
```

**レスポンス 409 Conflict** — ユーザー名またはメールアドレス重複

```json
{ "success": false, "data": null, "message": "このユーザー名は既に使用されています" }
```

```json
{ "success": false, "data": null, "message": "このメールアドレスは既に使用されています" }
```

**レスポンス 400 Bad Request** — バリデーションエラー

| メッセージ | 原因 |
|---|---|
| ユーザー名を入力してください | ユーザー名未入力 |
| ユーザー名は3文字以上で入力してください / ユーザー名は30文字以内で入力してください | ユーザー名の長さ不正 |
| ユーザー名は半角英数字・ハイフン・アンダースコアのみ使用できます | ユーザー名の文字種不正 |
| パスワードを入力してください | パスワード未入力 |
| パスワードは8～64文字で入力してください | パスワード長不正 |
| メールアドレスを入力してください | メールアドレス未入力 |
| メールアドレスの形式が正しくありません | メールアドレス形式不正 |

**レスポンス 429 Too Many Requests** — 登録試行回数超過（5回で同一IPアドレスを1時間ロック）

```json
{ "success": false, "data": null, "message": "登録試行回数が上限を超えました。しばらく時間をおいて再度お試しください。" }
```

**レスポンス 500 Internal Server Error** — 登録失敗・サーバー内部エラー

**レスポンス 503 Service Unavailable** — DB接続エラー

```json
{ "success": false, "data": null, "message": "データベース接続エラーが発生しました。しばらく時間をおいて再度お試しください。" }
```

---

### GET /users/{userId}

ユーザー情報取得。認証されたユーザー自身のみアクセス可。

- **認証**: 必須（`Authorization: Bearer <token>`）

| パスパラメータ | 型 | 説明 |
|---|---|---|
| userId | Long | 取得対象ユーザーID |

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "taro",
    "email": "taro@example.com",
    "birthDate": "2000-01-01",
    "address": "43100",
    "licenseStatus": "2",
    "licenseSurrenderedAt": "2024-04-01",
    "lastLoginAt": "2026-10-25T09:30:00",
    "lastLoginIp": "192.168.1.1"
  }
}
```

| フィールド | 型 | 説明 |
|---|---|---|
| lastLoginAt | string (ISO 8601) \| null | 最終ログイン日時（サーバー時刻・タイムゾーンなし）。ログイン記録がない場合は null |
| lastLoginIp | string \| null | 最終ログインIPアドレス。ログイン記録がない場合は null |

**レスポンス 401 Unauthorized** — 未認証
**レスポンス 403 Forbidden** — 他ユーザーへのアクセス
**レスポンス 404 Not Found** — ユーザーが存在しない
**レスポンス 500 Internal Server Error** — `ユーザー情報の取得に失敗しました`

---

### PUT /users

ユーザー情報更新。認証されたユーザー自身のみ更新可。

- **認証**: 必須（`Authorization: Bearer <token>`）

**リクエスト**

```json
{
  "userId": 1,
  "username": "taro",
  "email": "taro@example.com",
  "birthDate": "2000-01-01",
  "address": "43100",
  "licenseStatus": "2"
}
```

**レスポンス 200 OK**

```json
{ "success": true, "data": null }
```

**レスポンス 400 Bad Request** — バリデーションエラー（ユーザー名は `POST /users/signup` と同じチェック、メールアドレス未入力・形式不正、`生年月日を入力してください`、`生年月日に未来日は登録できません`）。PUT では生年月日が必須
**レスポンス 401 Unauthorized** — 未認証
**レスポンス 403 Forbidden** — 他ユーザーへのアクセス
**レスポンス 404 Not Found** — ユーザーが存在しない
**レスポンス 409 Conflict** — ユーザー名またはメールアドレス重複

```json
{ "success": false, "data": null, "message": "このユーザー名は既に使用されています" }
```

```json
{ "success": false, "data": null, "message": "このメールアドレスは既に使用されています" }
```

---

### PUT /users/password

パスワード変更。現在のパスワード確認後に新しいパスワードに変更する。

- **認証**: 必須（`Authorization: Bearer <token>`）

**リクエスト**

```json
{
  "currentPassword": "currentPass123",
  "newPassword": "newPass456",
  "confirmNewPassword": "newPass456"
}
```

**レスポンス 200 OK**

```json
{ "success": true, "data": null }
```

**レスポンス 400 Bad Request** — 入力値不正（`すべての項目を入力してください`、`パスワードは8～64文字で入力してください`、`新しいパスワードと確認用パスワードが一致しません`）
**レスポンス 401 Unauthorized** — 未認証
**レスポンス 404 Not Found** — ユーザーが存在しない
**レスポンス 409 Conflict** — 現在のパスワードが正しくない

```json
{ "success": false, "data": null, "message": "現在のパスワードが正しくありません" }
```

**レスポンス 500 Internal Server Error** — `パスワードの変更に失敗しました`

---

### DELETE /users

ログイン中のユーザー自身のアカウントを削除する（退会）。パスワードによる本人確認が必要。管理者アカウントは退会できない。

- **認証**: 必須（`Authorization: Bearer <token>`）

**リクエスト**

```json
{
  "password": "currentPass123"
}
```

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| password | string | ○ | 現在のパスワード（本人確認用） |

**レスポンス 200 OK**

```json
{ "success": true, "data": null }
```

**レスポンス 400 Bad Request** — パスワード未入力、または管理者アカウント

```json
{ "success": false, "data": null, "message": "パスワードを入力してください" }
```

```json
{ "success": false, "data": null, "message": "管理者アカウントは退会できません" }
```

**レスポンス 401 Unauthorized** — 未認証
**レスポンス 404 Not Found** — ユーザーが存在しない
**レスポンス 409 Conflict** — パスワードが正しくない

```json
{ "success": false, "data": null, "message": "パスワードが正しくありません" }
```

**レスポンス 429 Too Many Requests** — パスワード照合の失敗が5回に達した（ユーザー単位で15分間ロック）

```json
{ "success": false, "data": null, "message": "パスワードの試行回数が上限を超えました。しばらく時間をおいて再度お試しください。" }
```

**レスポンス 500 Internal Server Error** — `アカウントの削除に失敗しました`

---

## 3. 特典 (`/benefit`)

### GET /benefit/categories

有効な特典カテゴリ一覧を表示順で取得する。

- **認証**: 不要

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": [
    {
      "categoryCd": "C001",
      "categoryName": "交通",
      "displayOrder": 1,
      "isActive": "1"
    }
  ]
}
```

**レスポンス 500 Internal Server Error** — `カテゴリ一覧の取得に失敗しました`

---

### POST /benefit/search

検索条件（年齢・免許状態・自治体コード・キーワード・現在地からの距離等）から特典一覧を取得する。

- **認証**: 不要

**リクエスト**

```json
{
  "age": 70,
  "licenseStatus": "2",
  "municipalityCd": "43100",
  "keyword": "バス",
  "categoryCd": "C001",
  "latitude": 32.8032,
  "longitude": 130.7079,
  "radiusKm": 2
}
```

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| age | Integer | - | 年齢 |
| licenseStatus | string | - | 運転免許の所持状況 |
| municipalityCd | string | - | 自治体コード |
| keyword | string | - | フリーワード検索（特典名称・特典内容の部分一致） |
| categoryCd | string | - | カテゴリコード |
| latitude | Double | - | 現在地の緯度（-90〜90）。`longitude` と両方指定時のみ距離で絞り込む |
| longitude | Double | - | 現在地の経度（-180〜180） |
| radiusKm | Double | - | 検索半径（km、0より大きく10以下）。省略時は2km |

> 現在地を指定した場合、座標を持つ特典（店舗特典）のうち指定半径内のもののみ返却する。

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": [
    {
      "benefitId": "B001",
      "municipalityCd": "43100",
      "municipalityName": "熊本市",
      "municipalityKana": "くまもとし",
      "municipalityType": "3",
      "benefitName": "バス運賃割引",
      "benefitShortName": "バス割引",
      "benefitDetail": "バス運賃を10%割引",
      "expDetail": "2025年3月31日まで",
      "phoneNumber": "096-XXX-XXXX",
      "benefitUrl": "https://example.com/benefit",
      "address": null,
      "latitude": null,
      "longitude": null,
      "lastConfirmedDate": "2026-04-01",
      "categoryCd": "C001",
      "categoryName": "交通",
      "displayOrder": 1,
      "categoryIsActive": "1",
      "eligibilityId": 1,
      "licenseStatus": "2",
      "minAge": 65,
      "maxAge": null,
      "eligibilityMunicipalityCd": "43100",
      "eligibilityNote": null
    }
  ]
}
```

| フィールド | 説明 |
|---|---|
| benefitId | 特典ID |
| municipalityCd | 自治体コード |
| municipalityName | 自治体名称 |
| municipalityKana | 自治体名称かな |
| municipalityType | 自治体区分（`1`: 都道府県, `2`: 区, `3`: 市町村） |
| benefitName | 特典名称 |
| benefitShortName | 特典短縮名称 |
| benefitDetail | 特典内容 |
| expDetail | 有効期限 |
| phoneNumber | 問い合わせ電話番号 |
| benefitUrl | 特典URL |
| address | 住所（店舗特典のみ・ジオコーディング済みの場合） |
| latitude | 緯度（店舗特典のみ・ジオコーディング済みの場合） |
| longitude | 経度（店舗特典のみ・ジオコーディング済みの場合） |
| lastConfirmedDate | 最終確認日（管理者が情報を確認した日。未確認の場合は null） |
| categoryCd | カテゴリコード |
| categoryName | カテゴリ名称 |
| displayOrder | 表示順 |
| categoryIsActive | カテゴリ有効フラグ（`"1"`: 有効） |
| eligibilityId | 利用条件ID |
| licenseStatus | 運転免許の所持状況 |
| minAge | 最低年齢 |
| maxAge | 最高年齢 |
| eligibilityMunicipalityCd | 利用条件の対象自治体コード |
| eligibilityNote | 備考 |

**レスポンス 400 Bad Request** — `現在地の座標が正しくありません`、`検索半径が正しくありません`

**レスポンス 409 / 400 / 500** — DB 例外発生時（「エラーコード一覧」参照）

---

### GET /benefit/users/{userId}

ユーザーのプロフィール情報（年齢・免許状態・居住自治体）を元に、対象ユーザーが受けられる特典一覧を取得する。認証されたユーザー自身のみアクセス可。

- **認証**: 必須（`Authorization: Bearer <token>`）

| パスパラメータ | 型 | 説明 |
|---|---|---|
| userId | Long | 対象ユーザーID |

**レスポンス 200 OK** — `POST /benefit/search` と同形式の特典配列

**レスポンス 401 Unauthorized** — 未認証
**レスポンス 403 Forbidden** — 他ユーザーへのアクセス
**レスポンス 500 Internal Server Error** — `ユーザー特典情報の取得に失敗しました`

---

### GET /benefit/markers

座標データを持つ特典を全件取得する。マップ上の店舗マーカー表示用。

- **認証**: 不要

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": [
    {
      "benefitId": "B001",
      "municipalityCd": "43100",
      "municipalityName": "熊本市",
      "municipalityKana": "くまもとし",
      "municipalityType": "3",
      "benefitName": "○○店 割引",
      "benefitShortName": "○○割引",
      "benefitDetail": "商品10%割引",
      "expDetail": "2025年3月31日まで",
      "phoneNumber": "096-XXX-XXXX",
      "benefitUrl": "https://example.com/benefit",
      "address": "熊本県熊本市中央区手取本町1-1",
      "latitude": 32.8032,
      "longitude": 130.7079,
      "categoryCd": "BS001",
      "categoryName": "ショッピング",
      "displayOrder": 1,
      "categoryIsActive": "1",
      "eligibilityId": 1,
      "licenseStatus": "2",
      "minAge": 65,
      "maxAge": null,
      "eligibilityMunicipalityCd": "43100",
      "eligibilityNote": null
    }
  ]
}
```

> レスポンス形式は `POST /benefit/search` と同一（`BenefitDetailEntity`）。`address`・`latitude`・`longitude` が追加されている。座標（`latitude`・`longitude`）が `null` でない特典のみ返却される。

**レスポンス 500 Internal Server Error** — サーバー内部エラー

```json
{ "success": false, "data": null, "message": "マーカー用特典の取得に失敗しました" }
```

---

### GET /benefit/{benefitId}

特典IDに紐づく特典詳細を取得する。特典共有URL（`/?benefitId={benefitId}`）を開いた際の表示に使用する。利用条件が複数ある特典は複数件返る。

- **認証**: 不要

| パスパラメータ | 型 | 説明 |
|---|---|---|
| benefitId | String | 特典ID |

**レスポンス 200 OK** — `POST /benefit/search` と同形式の特典配列

**レスポンス 404 Not Found** — 該当する特典が存在しない

```json
{ "success": false, "data": null, "message": "特典が見つかりません: B001" }
```

**レスポンス 500 Internal Server Error** — `特典の取得に失敗しました`

---

### POST /benefit/{benefitId}/report

特典情報の誤り（情報が古い等）を管理者に報告する。Slack Incoming Webhook に通知する。

- **認証**: 不要

| パスパラメータ | 型 | 説明 |
|---|---|---|
| benefitId | String | 報告対象の特典ID |

リクエストボディなし。

**レスポンス 200 OK**

```json
{ "success": true, "data": null }
```

**レスポンス 400 Bad Request** — 該当する特典が存在しない

```json
{ "success": false, "data": null, "message": "特典が見つかりません: B001" }
```

**レスポンス 500 Internal Server Error** — `報告の送信に失敗しました`

> **Slack通知フォーマット例:**
> ```
> ⚠️ 特典情報の報告
> 特典名: バス運賃割引
> 特典ID: B001
> 報告日時: 2025-01-15 14:30
> ```
>
> `SLACK_WEBHOOK_URL` 環境変数未設定時はSlack送信をスキップしてサーバーログに出力する。

---

## 4. 市区町村 (`/municipality`)

### GET /municipality/all

熊本県内の全市区町村情報を取得する。

- **認証**: 不要

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": [
    {
      "municipalityCd": "43100",
      "municipalityName": "熊本市",
      "municipalityKana": "くまもとし",
      "municipalityType": "3"
    }
  ]
}
```

**レスポンス 500 Internal Server Error** — `市区町村情報の取得に失敗しました`

---

## 5. 経路探索 (`/route`)

### POST /route/search

出発地・目的地・日時を指定し、OpenTripPlanner (OTP) 経由で公共交通経路を探索する。未ログインでも利用可（ログイン時はユーザーIDがログに記録される）。

- **認証**: 任意（ログイン時はユーザーIDをログ記録）

**リクエスト**

```json
{
  "transportMode": "TRANSIT,WALK",
  "startLocation": "熊本駅",
  "startLat": 32.7898,
  "startLon": 130.6984,
  "endLocation": "熊本市役所",
  "endLat": 32.8031,
  "endLon": 130.7078,
  "date": "2025-04-01",
  "time": "09:00",
  "arriveBy": false
}
```

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| transportMode | string | ○ | OTP 交通手段指定（例: `"TRANSIT,WALK"`。英大文字・アンダースコアのカンマ区切り、50文字以内） |
| startLocation | string | - | 出発地名称（表示用・200文字以内） |
| startLat | Double | ○ | 出発地緯度 |
| startLon | Double | ○ | 出発地経度 |
| endLocation | string | - | 目的地名称（表示用・200文字以内） |
| endLat | Double | ○ | 目的地緯度 |
| endLon | Double | ○ | 目的地経度 |
| date | string (YYYY-MM-DD) | - | 出発日（または到着日）。省略時は OTP の既定（現在日） |
| time | string (HH:mm / HH:mm:ss) | - | 出発時刻（または到着時刻）。省略時は OTP の既定（現在時刻） |
| arriveBy | boolean | - | `true`: 到着時刻指定、`false`: 出発時刻指定（デフォルト: `false`） |

**レスポンス 200 OK**

OTP のレスポンスを整形した経路（itinerary）の配列を返す。到着時刻の早い順に最大3件。運賃割引マスタ（`FARE_DISCOUNT`）をもとに割引後運賃も算出する。

```json
{
  "success": true,
  "data": [
    {
      "startTime": "09:02",
      "endTime": "09:31",
      "duration": 29,
      "totalFare": 230,
      "totalDiscountFare": 115,
      "transfers": 0,
      "legs": [
        {
          "mode": "BUS",
          "startTime": "09:05",
          "endTime": "09:25",
          "duration": 20,
          "from": "熊本駅前",
          "fromLat": 32.7898,
          "fromLon": 130.6984,
          "to": "市役所前",
          "toLat": 32.8031,
          "toLon": 130.7078,
          "fare": 230,
          "discountFare": 115,
          "agencyName": "熊本市交通局",
          "agencyId": "A001",
          "communityBusId": "",
          "agencyUrl": "https://example.com",
          "routeId": "R001",
          "legGeometry": { "points": "encoded-polyline" },
          "transitLeg": true,
          "isRealtime": false,
          "arrivalDelay": null,
          "benefitUrl": "https://example.com/benefit",
          "benefitId": "B001",
          "freePass": ""
        }
      ]
    }
  ]
}
```

| フィールド | 説明 |
|---|---|
| startTime / endTime | 出発・到着時刻（`HH:mm`） |
| duration | 所要時間（分） |
| totalFare | 合計運賃 |
| totalDiscountFare | 割引後の合計運賃（割引対象区間がない場合は null） |
| transfers | 乗り換え回数 |
| legs | 区間ごとの情報（徒歩区間を含む） |
| legs[].mode | 交通手段（`WALK` / `BUS` / `TRAM` / `RAIL` 等） |
| legs[].fare / discountFare | 区間運賃・割引後運賃 |
| legs[].legGeometry.points | 区間の経路形状（Encoded Polyline） |
| legs[].transitLeg | 公共交通区間か |
| legs[].isRealtime / arrivalDelay | リアルタイム情報の有無・到着遅延（秒） |
| legs[].benefitId / benefitUrl / freePass | 区間に適用される特典の情報 |

**レスポンス 400 Bad Request** — 入力値不正（`交通手段が正しくありません`、`出発地または目的地の座標が正しくありません`、`日付が正しくありません`、`時刻が正しくありません`、`出発地または目的地の名称が長すぎます`）

**レスポンス 500 Internal Server Error** — OTP 接続エラーや経路探索失敗時（`経路探索中にエラーが発生しました`）

---

## 6. フィードバック (`/feedback`)

### POST /feedback

ユーザーからのご意見・ご要望を受け付け、Slack Incoming Webhook に通知する。未ログインユーザーでも送信可能。ログイン済みの場合はユーザー名をSlackメッセージに自動付与する。

- **認証**: 不要（ログイン済みの場合はユーザー名がSlack通知に含まれる）

**リクエスト**

```json
{
  "category": "BUG",
  "name": "山田太郎",
  "email": "taro@example.com",
  "content": "地図が表示されません"
}
```

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| category | string | - | カテゴリ（`"BUG"`: バグ報告 / `"REQUEST"`: 要望 / `"OTHER"`: その他）。未指定時はその他扱い |
| name | string | - | 送信者の名前（任意） |
| email | string | - | 送信者のメールアドレス（任意） |
| content | string | ○ | フィードバック内容（必須） |

> サーバー側では `content` の未入力チェックのみ行い、文字数の上限チェックは行わない（画面側で name 100文字・email 255文字・content 2000文字の入力上限を設定）。

**レスポンス 200 OK**

```json
{ "success": true, "data": null }
```

**レスポンス 400 Bad Request** — 内容（content）が未入力

```json
{ "success": false, "data": null, "message": "内容は必須です" }
```

**レスポンス 500 Internal Server Error** — `フィードバックの送信に失敗しました`（Slack 送信自体の失敗はログ出力のみで 200 を返す）

> **Slack通知フォーマット例:**
> ```
> *【ご意見・ご要望】*
> カテゴリ: バグ報告
> お名前: 山田太郎
> メールアドレス: taro@example.com
> 内容:
> 地図が表示されません
>
> 送信日時: 2025-01-15 14:30:00
> ユーザー情報: ログイン済み（taro）
> ```
>
> `SLACK_WEBHOOK_URL` 環境変数未設定時はSlack送信をスキップしてサーバーログに出力する。

---

## 7. ヘルスチェック (`/health`)

### GET /health

サーバーの稼働確認用エンドポイント。Renderのスリープ対策として、GASから定期的にアクセスする用途で使用する。

- **認証**: 不要

**レスポンス 200 OK**（ボディ: `"OK"` テキスト）

---

## 8. 管理API (`/admin`)

管理者アカウントによるデータ管理用APIです。

### 認証

全管理APIは `Authorization: Bearer <token>` ヘッダーが必須で、管理者権限（`ROLE_ADMIN`）が必要です。未認証の場合は 401、管理者でないユーザーの場合は 403 を返します。

### ページングリクエスト共通パラメータ

一覧取得エンドポイント（`GET /admin/{resource}`）で共通して使用できるクエリパラメータ:

| パラメータ | 型 | デフォルト | 説明 |
|---|---|---|---|
| page | Integer | 0 | ページ番号（0始まり） |
| size | Integer | 20 | 1ページのサイズ |
| sort | String | - | ソート対象カラム名 |
| order | String | - | ソート方向（`asc` / `desc`） |
| keyword | String | - | 横断フリーワード検索 |

各エンドポイントには上記共通パラメータに加え、リソース固有の絞り込みパラメータが存在します。

### ページングレスポンス形式

```json
{
  "success": true,
  "data": {
    "items": [...],
    "total": 100,
    "page": 0,
    "size": 20
  }
}
```

### CSVインポートレスポンス形式

```json
{
  "success": true,
  "data": {
    "inserted": 10,
    "updated": 2,
    "failed": 1,
    "errors": ["行3: benefitIdが重複しています"]
  }
}
```

CSVインポートは `multipart/form-data`（`file` パラメータ）でファイルを送信する。

### 共通レスポンスステータス

| ステータス | 発生条件 |
|---|---|
| 201 Created | 新規作成（POST）成功時。ボディは作成したエンティティ |
| 400 Bad Request | 入力値不正、DB の制約違反、CSV ファイルが空（`ファイルが空です`）・CSV 内容不正（`CSVインポートに失敗しました: ...`） |
| 404 Not Found | 更新・削除対象が存在しない |
| 409 Conflict | 一意制約違反（`既に登録されています`）、他テーブルから参照されているデータの削除 |
| 500 Internal Server Error | サーバー内部エラー |

---

### GET /admin/agencies

交通事業者一覧をページング取得する。

- **認証**: 必須（管理者）
- **固有パラメータ**: `agencyId`, `agencyName`, `agencyKana`, `phoneNumber`, `operatorId`

**レスポンス 200 OK** — ページングレスポンス形式（`items` に `AgencyEntity` の配列）

---

### GET /admin/agencies/all

セレクトボックス用に全事業者を一括取得する。

- **認証**: 必須（管理者）

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": [
    { "agencyId": "A001", "agencyName": "熊本バス", ... }
  ]
}
```

---

### POST /admin/agencies

事業者を新規作成する。

- **認証**: 必須（管理者）

**リクエスト**: `AgencyEntity` 形式のJSONボディ

**レスポンス 201 Created** — 作成した `AgencyEntity`

---

### PUT /admin/agencies/{agencyId}

事業者情報を更新する。

- **認証**: 必須（管理者）

| パスパラメータ | 型 | 説明 |
|---|---|---|
| agencyId | String | 更新対象の事業者ID |

**リクエスト**: `AgencyEntity` 形式のJSONボディ

**レスポンス 200 OK** — 更新後の `AgencyEntity`

---

### DELETE /admin/agencies/{agencyId}

事業者を削除する。

- **認証**: 必須（管理者）

**レスポンス 200 OK**

---

### POST /admin/agencies/import

CSVファイルで事業者を一括登録・更新する。

- **認証**: 必須（管理者）
- **リクエスト**: `multipart/form-data`（`file` パラメータ）

**レスポンス 200 OK** — CSVインポートレスポンス形式

---

### GET /admin/benefit-categories

特典カテゴリ一覧をページング取得する。

- **認証**: 必須（管理者）
- **固有パラメータ**: `categoryCd`, `categoryName`, `displayOrder`

**レスポンス 200 OK** — ページングレスポンス形式（`items` に `BenefitCategoryEntity` の配列）

---

### POST /admin/benefit-categories

特典カテゴリを新規作成する。

- **認証**: 必須（管理者）

**レスポンス 201 Created** — 作成した `BenefitCategoryEntity`

---

### PUT /admin/benefit-categories/{categoryCd}

特典カテゴリを更新する。

- **認証**: 必須（管理者）

**レスポンス 200 OK** — 更新後の `BenefitCategoryEntity`

---

### DELETE /admin/benefit-categories/{categoryCd}

特典カテゴリを削除する。

- **認証**: 必須（管理者）

**レスポンス 200 OK**

---

### POST /admin/benefit-categories/import

CSVファイルで特典カテゴリを一括登録・更新する。

- **認証**: 必須（管理者）

**レスポンス 200 OK** — CSVインポートレスポンス形式

---

### GET /admin/benefits

特典一覧をページング取得する。

- **認証**: 必須（管理者）
- **固有パラメータ**: `benefitId`, `benefitName`, `municipalityCd`, `categoryCd`, `expDetail`

**レスポンス 200 OK** — ページングレスポンス形式（`items` に `BenefitEntity` の配列）

---

### POST /admin/benefits

特典を新規作成する。

- **認証**: 必須（管理者）

**レスポンス 201 Created** — 作成した `BenefitEntity`

---

### PUT /admin/benefits/{benefitId}

特典情報を更新する。

- **認証**: 必須（管理者）

**レスポンス 200 OK** — 更新後の `BenefitEntity`

---

### DELETE /admin/benefits/{benefitId}

特典を削除する。

- **認証**: 必須（管理者）

**レスポンス 200 OK**

---

### POST /admin/benefits/import

CSVファイルで特典を一括登録・更新する。

- **認証**: 必須（管理者）

**レスポンス 200 OK** — CSVインポートレスポンス形式

---

### POST /admin/benefits/geocode

AWS Location Serviceを使用して、店舗特典（カテゴリ: BS001）の店舗名から住所・緯度・経度を一括取得し、BENEFITテーブルに保存する。

- **認証**: 必須（管理者）

| クエリパラメータ | 型 | デフォルト | 説明 |
|---|---|---|---|
| skipExisting | boolean | true | `true`: 既に緯度・経度が保存済みの特典をスキップ |

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": {
    "success": 45,
    "skipped": 12,
    "failed": 3,
    "errors": [
      "BEN001234: 検索結果なし（クエリ: 熊本市 テスト店舗）"
    ]
  }
}
```

| フィールド | 型 | 説明 |
|---|---|---|
| success | int | ジオコーディング成功件数 |
| skipped | int | スキップ件数（既に座標保存済み） |
| failed | int | 失敗件数 |
| errors | string[] | エラー詳細（`特典ID: エラーメッセージ` 形式） |

**レスポンス 500 Internal Server Error** — ジオコーディング処理全体の失敗

```json
{ "success": false, "data": null, "message": "ジオコーディングに失敗しました" }
```

---

### POST /admin/benefits/{benefitId}/confirm

特典情報を確認済みとしてマークする。対象特典の最終確認日（`LAST_CONFIRMED_DATE`）を当日の日付に更新する。

- **認証**: 必須（管理者）

| パスパラメータ | 型 | 説明 |
|---|---|---|
| benefitId | String | 確認対象の特典ID |

**レスポンス 200 OK**

```json
{ "success": true, "data": null }
```

**レスポンス 404 Not Found**

```json
{ "success": false, "data": null, "message": "特典が見つかりません: B001" }
```

**レスポンス 500 Internal Server Error** — `確認処理に失敗しました`

---

### GET /admin/benefit-eligibilities

特典利用条件一覧をページング取得する。

- **認証**: 必須（管理者）
- **固有パラメータ**: `id`, `benefitId`, `licenseStatus`, `minAge`, `maxAge`, `municipalityCd`

**レスポンス 200 OK** — ページングレスポンス形式（`items` に `BenefitEligibilityEntity` の配列）

---

### POST /admin/benefit-eligibilities

特典利用条件を新規作成する。

- **認証**: 必須（管理者）

**レスポンス 201 Created** — 作成した `BenefitEligibilityEntity`

---

### PUT /admin/benefit-eligibilities/{id}

特典利用条件を更新する。

- **認証**: 必須（管理者）

**レスポンス 200 OK** — 更新後の `BenefitEligibilityEntity`

---

### DELETE /admin/benefit-eligibilities/{id}

特典利用条件を削除する。

- **認証**: 必須（管理者）

**レスポンス 200 OK**

---

### POST /admin/benefit-eligibilities/import

CSVファイルで特典利用条件を一括登録・更新する。

- **認証**: 必須（管理者）

**レスポンス 200 OK** — CSVインポートレスポンス形式

---

### GET /admin/community-buses

コミュニティバス路線一覧をページング取得する。

- **認証**: 必須（管理者）
- **固有パラメータ**: `routeId`, `routeName`, `communityBusId`

**レスポンス 200 OK** — ページングレスポンス形式（`items` に `CommunityBusEntity` の配列）

---

### POST /admin/community-buses

コミュニティバス路線を新規作成する。

- **認証**: 必須（管理者）

**レスポンス 201 Created** — 作成した `CommunityBusEntity`

---

### PUT /admin/community-buses/{routeId}

コミュニティバス路線を更新する。

- **認証**: 必須（管理者）

**レスポンス 200 OK** — 更新後の `CommunityBusEntity`

---

### DELETE /admin/community-buses/{routeId}

コミュニティバス路線を削除する。

- **認証**: 必須（管理者）

**レスポンス 200 OK**

---

### POST /admin/community-buses/import

CSVファイルでコミュニティバス路線を一括登録・更新する。

- **認証**: 必須（管理者）

**レスポンス 200 OK** — CSVインポートレスポンス形式

---

### GET /admin/fare-discounts

運賃割引一覧をページング取得する。

- **認証**: 必須（管理者）
- **固有パラメータ**: `benefitId`, `agencyId`, `discountType`, `discountValue`

**レスポンス 200 OK** — ページングレスポンス形式（`items` に `FareDiscountEntity` の配列）

---

### POST /admin/fare-discounts

運賃割引を新規作成する。

- **認証**: 必須（管理者）

**レスポンス 201 Created** — 作成した `FareDiscountEntity`

---

### PUT /admin/fare-discounts/{benefitId}/{agencyId}

運賃割引を更新する。複合主キー（`benefitId` + `agencyId`）を使用。

- **認証**: 必須（管理者）

| パスパラメータ | 型 | 説明 |
|---|---|---|
| benefitId | String | 対象の特典ID |
| agencyId | String | 対象の事業者ID |

**レスポンス 200 OK** — 更新後の `FareDiscountEntity`

---

### DELETE /admin/fare-discounts/{benefitId}/{agencyId}

運賃割引を削除する。複合主キー（`benefitId` + `agencyId`）を使用。

- **認証**: 必須（管理者）

**レスポンス 200 OK**

---

### POST /admin/fare-discounts/import

CSVファイルで運賃割引を一括登録・更新する。

- **認証**: 必須（管理者）

**レスポンス 200 OK** — CSVインポートレスポンス形式

---

### GET /admin/municipalities

自治体一覧をページング取得する。

- **認証**: 必須（管理者）
- **固有パラメータ**: `municipalityCd`, `municipalityName`, `municipalityKana`, `municipalityType`

**レスポンス 200 OK** — ページングレスポンス形式（`items` に `MunicipalityEntity` の配列）

---

### POST /admin/municipalities

自治体を新規作成する。

- **認証**: 必須（管理者）

**レスポンス 201 Created** — 作成した `MunicipalityEntity`

---

### PUT /admin/municipalities/{municipalityCd}

自治体情報を更新する。

- **認証**: 必須（管理者）

**レスポンス 200 OK** — 更新後の `MunicipalityEntity`

---

### DELETE /admin/municipalities/{municipalityCd}

自治体を削除する。

- **認証**: 必須（管理者）

**レスポンス 200 OK**

---

### POST /admin/municipalities/import

CSVファイルで自治体を一括登録・更新する。

- **認証**: 必須（管理者）

**レスポンス 200 OK** — CSVインポートレスポンス形式

---

### GET /admin/users

ユーザー一覧をページング取得する。

- **認証**: 必須（管理者）
- **固有パラメータ**: `userId`, `username`, `email`, `birthDate`, `municipalityCd`, `licenseStatus`

**レスポンス 200 OK** — ページングレスポンス形式（`items` に `AdminUserResponseDto` の配列）

---

### GET /admin/users/{userId}

特定ユーザーの詳細情報を取得する。

- **認証**: 必須（管理者）

| パスパラメータ | 型 | 説明 |
|---|---|---|
| userId | Long | 取得対象ユーザーID |

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "taro",
    "email": "taro@example.com",
    "birthDate": "2000-01-01",
    "municipalityCd": "43100",
    "licenseStatus": "2",
    "licenseSurrenderedAt": "2024-04-01",
    "isAdmin": "0",
    "sysCreatedAt": "2026-01-01T00:00:00",
    "sysUpdatedAt": "2026-01-01T00:00:00"
  }
}
```

> `isAdmin` は文字列（`"1"`: 管理者, `"0"`: 一般ユーザー）。

---

### PUT /admin/users/{userId}

ユーザー情報を管理者権限で更新する。

- **認証**: 必須（管理者）

**リクエスト**: `UsersEntity` 形式のJSONボディ

**レスポンス 200 OK** — 更新後の `AdminUserResponseDto`

---

### DELETE /admin/users/{userId}

ユーザーアカウントを削除する。

- **認証**: 必須（管理者）

**レスポンス 200 OK**

---

## エラーコード一覧

| HTTP ステータス | 主な原因 |
|---|---|
| 400 Bad Request | リクエストパラメータ不正、バリデーションエラー、DB の制約違反（SQLState 22xxx / 23xxx） |
| 401 Unauthorized | Bearer Token なし、または無効・期限切れ（`認証が必要です`） |
| 403 Forbidden | 他ユーザーのリソースへのアクセス、管理者権限なしでの管理APIアクセス（`アクセス権限がありません`） |
| 404 Not Found | 対象リソースが存在しない |
| 409 Conflict | ユーザー名・メールアドレスの重複登録、パスワード不一致、DB の一意制約違反（`既に登録されています`）、参照中データの削除 |
| 429 Too Many Requests | レート制限超過（ログイン・登録・パスワードリセット・退会） |
| 500 Internal Server Error | サーバー内部エラー、OTP 接続失敗 |
| 503 Service Unavailable | DB 接続エラー |
