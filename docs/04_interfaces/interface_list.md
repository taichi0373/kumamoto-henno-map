# インターフェース一覧

本システムが提供する REST API エンドポイントの一覧です。

- ベース URL: `http://localhost:8081/benefit-map/api`
- すべてのリクエスト・レスポンスは `application/json`
- 認証が必要なエンドポイントは JWT を HttpOnly Cookie (`jwt`) で送信する
- 状態変更系（POST / PUT / DELETE）には CSRF トークン (`X-XSRF-TOKEN` ヘッダー) が必要（認証不要エンドポイントを除く）

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
| data | any / null | 成功時のレスポンスデータ。`null` の場合は省略 |
| message | string / null | エラー時のメッセージ。成功時は省略 |

---

## 1. 認証 (`/auth`)

### POST /auth/login

ログイン。認証成功時に JWT を HttpOnly Cookie (`jwt`) にセットする。

- **認証**: 不要
- **CSRF**: 不要

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
    "userId": 1,
    "username": "taro"
  }
}
```

> `token` フィールドは `@JsonIgnore` により JSON に含まれない。JWT は Set-Cookie ヘッダーで返却される。

**レスポンス 401 Unauthorized**

```json
{ "success": false, "message": "ユーザー名またはパスワードが正しくありません" }
```

---

### POST /auth/logout

ログアウト。JWT Cookie を無効化（`Max-Age=0`）する。

- **認証**: 不要
- **CSRF**: 不要

**レスポンス 204 No Content**（ボディなし）

---

### GET /auth/csrf

CSRF トークンを取得する。フロントエンドは状態変更系 API を呼び出す前にこのエンドポイントを呼び出し、`X-XSRF-TOKEN` ヘッダーに使用する。レスポンスと同時に `XSRF-TOKEN` Cookie も設定される。

- **認証**: 不要
- **CSRF**: 不要

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": "csrf-token-value"
}
```

---

## 2. ユーザー (`/users`)

### POST /users/signup

ユーザー登録（新規アカウント作成）。

- **認証**: 不要
- **CSRF**: 不要

**リクエスト**

```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "birthDate": "2000-01-01",
  "address": "string",
  "licenseStatus": "string"
}
```

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| username | string | ○ | ユーザー名（一意） |
| password | string | ○ | パスワード（平文。サーバー側でハッシュ化） |
| email | string | - | メールアドレス |
| birthDate | string (ISO 8601) | - | 生年月日 |
| address | string | - | 住所 |
| licenseStatus | string | - | 免許状態コード |

**レスポンス 201 Created**

```json
{
  "success": true,
  "data": {
    "userId": 1,
    "username": "taro",
    "email": "taro@example.com",
    "birthDate": "2000-01-01",
    "address": "熊本市中央区",
    "licenseStatus": "SURRENDERED",
    "licenseSurrenderedAt": null
  }
}
```

**レスポンス 409 Conflict** — ユーザー名重複

```json
{ "success": false, "message": "このユーザー名は既に使用されています" }
```

---

### GET /users/{userId}

ユーザー情報取得。JWT で認証されたユーザー自身のみアクセス可。

- **認証**: 必須（JWT Cookie）
- **CSRF**: 不要（GET）

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
    "address": "熊本市中央区",
    "licenseStatus": "SURRENDERED",
    "licenseSurrenderedAt": "2024-04-01"
  }
}
```

**レスポンス 401 Unauthorized** — 未認証
**レスポンス 403 Forbidden** — 他ユーザーへのアクセス
**レスポンス 404 Not Found** — ユーザーが存在しない

---

### PUT /users

ユーザー情報更新。JWT で認証されたユーザー自身のみ更新可。

- **認証**: 必須（JWT Cookie）
- **CSRF**: 必須（`X-XSRF-TOKEN` ヘッダー）

**リクエスト**

```json
{
  "userId": 1,
  "username": "taro",
  "email": "taro@example.com",
  "birthDate": "2000-01-01",
  "address": "熊本市中央区",
  "licenseStatus": "SURRENDERED"
}
```

**レスポンス 200 OK**

```json
{ "success": true }
```

**レスポンス 401 Unauthorized** — 未認証
**レスポンス 403 Forbidden** — 他ユーザーへのアクセス
**レスポンス 404 Not Found** — ユーザーが存在しない

---

## 3. 特典 (`/benefit`)

### POST /benefit/search

検索条件（年齢・免許状態・自治体コード）から特典一覧を取得する。

- **認証**: 不要
- **CSRF**: 不要（認証不要エンドポイントはCSRF対象外）

**リクエスト**

```json
{
  "age": 70,
  "licenseStatus": "SURRENDERED",
  "municipalityCd": "43100"
}
```

| フィールド | 型 | 必須 | 説明 |
|---|---|---|---|
| age | Integer | - | 年齢 |
| licenseStatus | string | - | 免許状態コード |
| municipalityCd | string | - | 自治体コード |

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": [
    {
      "benefitId": "B001",
      "municipalityCd": "43100",
      "benefitName": "バス運賃割引",
      "categoryName": "交通",
      "licenseStatus": "SURRENDERED",
      "minAge": 65,
      "maxAge": null
    }
  ]
}
```

---

### GET /benefit/users/{userId}

ユーザーのプロフィール情報（年齢・免許状態・居住自治体）を元に、対象ユーザーが受けられる特典一覧を取得する。JWT で認証されたユーザー自身のみアクセス可。

- **認証**: 必須（JWT Cookie）
- **CSRF**: 不要（GET）

| パスパラメータ | 型 | 説明 |
|---|---|---|
| userId | Long | 対象ユーザーID |

**レスポンス 200 OK** — `/benefit/search` と同形式の特典配列

**レスポンス 401 Unauthorized** — 未認証
**レスポンス 403 Forbidden** — 他ユーザーへのアクセス

---

## 4. 市区町村 (`/municipality`)

### GET /municipality/all

熊本県内の全市区町村情報を取得する。

- **認証**: 不要
- **CSRF**: 不要（GET）

**レスポンス 200 OK**

```json
{
  "success": true,
  "data": [
    {
      "municipalityCd": "43100",
      "municipalityName": "熊本市",
      "municipalityKana": "くまもとし",
      "municipalityType": "市"
    }
  ]
}
```

---

## 5. 経路探索 (`/route`)

### POST /route/search

出発地・目的地・日時を指定し、OpenTripPlanner (OTP) 経由で公共交通経路を探索する。未ログインでも利用可（ログイン時はユーザーIDがログに記録される）。

- **認証**: 任意（ログイン時はユーザーIDをログ記録）
- **CSRF**: 不要（認証不要エンドポイントはCSRF対象外）

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
| transportMode | string | ○ | OTP 交通手段指定（例: `"TRANSIT,WALK"`） |
| startLocation | string | - | 出発地名称（表示用） |
| startLat | Double | ○ | 出発地緯度 |
| startLon | Double | ○ | 出発地経度 |
| endLocation | string | - | 目的地名称（表示用） |
| endLat | Double | ○ | 目的地緯度 |
| endLon | Double | ○ | 目的地経度 |
| date | string (YYYY-MM-DD) | ○ | 出発日（または到着日） |
| time | string (HH:mm) | ○ | 出発時刻（または到着時刻） |
| arriveBy | boolean | - | `true`: 到着時刻指定、`false`: 出発時刻指定（デフォルト: `false`） |

**レスポンス 200 OK**

OTP から返却される経路情報をそのまま返す。`data` フィールドに OTP レスポンス（`plan.itineraries` 配列など）が格納される。

```json
{
  "success": true,
  "data": {
    "plan": {
      "itineraries": [
        {
          "duration": 1800,
          "legs": [ ... ]
        }
      ]
    }
  }
}
```

**レスポンス 500 Internal Server Error** — OTP 接続エラーや経路探索失敗時

---

## エラーコード一覧

| HTTP ステータス | 主な原因 |
|---|---|
| 400 Bad Request | リクエストパラメータ不正 |
| 401 Unauthorized | JWT Cookie なし、または無効 |
| 403 Forbidden | 他ユーザーのリソースへのアクセス、CSRF トークン無効 |
| 404 Not Found | 対象リソースが存在しない |
| 409 Conflict | ユーザー名の重複登録 |
| 500 Internal Server Error | サーバー内部エラー、DB 接続失敗、OTP 接続失敗 |
| 503 Service Unavailable | DB 接続エラー（サインアップ時） |
