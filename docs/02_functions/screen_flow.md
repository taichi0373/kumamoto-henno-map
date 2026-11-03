# 画面遷移図

本システムの画面間の遷移フローと操作の流れを図示します。

## 全体画面遷移図

ヘッダー（`AppHeader`）は全画面共通で表示され、「ホーム」「自主返納支援制度とは」のほか、ログイン状態・権限に応じたメニューを表示する。

```mermaid
graph TD
    Start([アプリケーション開始]) --> Home[S001: ホーム画面]

    Home -- ヘッダー: ログイン --> Login[S011: ログイン画面]
    Home -- ヘッダー: 新規登録 --> Signup[S012: 会員登録画面]
    Home -- ヘッダー: 自主返納支援制度とは --> SupportInfo[S021: サポート情報画面]
    Home -- ヘッダー: マイページ/プロフィール<br/>要ログイン --> Profile[S013: プロフィール画面]
    Home -- ヘッダー: 管理者画面<br/>管理者のみ --> Admin[SA001-SA008: 管理画面<br/>/admin/*]

    Login -- 認証成功 --> Home
    Login -- 新規登録はこちら --> Signup
    Login -- パスワードを忘れた方はこちら --> ForgotPw[S015: パスワードリセット要求画面]
    Signup -- 登録完了 / ログインリンク --> Login
    ForgotPw -- ログインに戻る --> Login
    ForgotPw -. メール内URL .-> ResetPw[S016: パスワードリセット画面]
    ResetPw -- リセット成功 --> Login
    ResetPw -- トークンなし --> ForgotPw

    Profile -- パスワードを変更する --> ChangePw[S014: パスワード変更画面]
    ChangePw -- 変更成功 / プロフィールに戻る --> Profile
    Profile -- 退会 --> Home
    Home -- ヘッダー: マイページ/ログアウト --> Login
    SupportInfo -- マップに戻る --> Home

    classDef startStyle fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef mainStyle fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef authStyle fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef userStyle fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef supportStyle fill:#fce4ec,stroke:#880e4f,stroke-width:2px
    classDef adminStyle fill:#e8eaf6,stroke:#283593,stroke-width:2px

    class Start startStyle
    class Home mainStyle
    class Login,Signup,ForgotPw,ResetPw authStyle
    class Profile,ChangePw userStyle
    class SupportInfo supportStyle
    class Admin adminStyle
```

> ヘッダーのメニューはホーム画面以外の全画面（管理画面を含む）でも同様に利用できる。図ではホーム画面からの遷移のみ記載している。

## エンドユーザー向けフロー詳細

### 1. 基本利用フロー（未登録ユーザー）

```mermaid
sequenceDiagram
    participant User as ユーザー
    participant Home as ホーム画面
    participant Sidebar as サイドバー

    User->>Home: アクセス
    Home->>Home: 地図表示（初期タブ: ルート案内）
    User->>Sidebar: 「ルート案内」タブで出発地・目的地入力
    Sidebar->>Home: 経路計算・地図上に表示
    User->>Sidebar: 「特典を探す」タブ選択・検索条件入力
    Sidebar->>Home: 検索結果の特典を表示
    User->>Sidebar: 「利用できる特典」タブ選択（ログイン時のみ）
    Sidebar->>Home: プロフィールに合致する特典を表示
```

### 2. 特典共有URLフロー（ログイン不要）

```mermaid
sequenceDiagram
    participant A as 共有する人
    participant Home as ホーム画面
    participant B as 受け取る人
    participant API as Backend API

    A->>Home: 特典カードの「URLをコピー」
    Home-->>A: /?benefitId={id} をクリップボードへコピー
    A->>B: URLを共有
    B->>Home: /?benefitId={id} を開く
    Home->>API: GET /benefit/{id}
    API-->>Home: 特典詳細
    Home->>Home: 「特典を探す」タブに「シェアされた特典」として表示
    B->>Home: 共有表示をクリア
    Home->>Home: URLから benefitId を除去
```

### 3. 会員登録・ログインフロー

```mermaid
graph TD
    Start([開始]) --> Home[S001: ホーム画面 ゲスト]
    Home -- ヘッダー: ログイン --> Login[S011: ログイン画面]
    Home -- ヘッダー: 新規登録 --> Signup[S012: 会員登録画面]
    Login -- 新規登録はこちら --> Signup
    Signup -- 登録完了 / すでにアカウントをお持ちの方はこちら --> Login
    Login -- 認証成功<br/>redirect クエリの画面 または / --> HomeAuth[S001: ホーム画面 ログイン済み]
    HomeAuth -- マイページ/プロフィール --> Profile[S013: プロフィール画面]
    Profile -- 更新（画面に留まる） --> Profile
    HomeAuth -- マイページ/ログアウト --> Login

    classDef mainStyle fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef authStyle fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef userStyle fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px

    class Home,HomeAuth mainStyle
    class Login,Signup authStyle
    class Profile userStyle
```

### 4. パスワード変更・退会フロー（ログイン済みユーザー）

```mermaid
graph TD
    Profile[S013: プロフィール画面] -- パスワードを変更する --> ChangePw[S014: パスワード変更画面]
    ChangePw -- 変更成功 --> Profile
    ChangePw -- プロフィールに戻る --> Profile
    Profile -- 退会する<br/>パスワード確認ダイアログ --> Home[S001: ホーム画面]

    classDef userStyle fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef mainStyle fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    class Profile,ChangePw userStyle
    class Home mainStyle
```

> 管理者アカウントには「退会する」リンクを表示しない。

### 5. パスワードリセットフロー（未ログインユーザー）

```mermaid
graph TD
    Login[S011: ログイン画面] -- パスワードを忘れた方はこちら --> ForgotPw[S015: パスワードリセット要求画面]
    ForgotPw -- メール送信 --> MailSent[メール受信確認]
    MailSent -- URLクリック --> ResetPw[S016: パスワードリセット画面]
    ResetPw -- リセット成功<br/>/login?resetSuccess=1 --> Login
    ResetPw -- トークンなし --> ForgotPw
    ForgotPw -- ログインに戻る --> Login

    classDef authStyle fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef externalStyle fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    class Login,ForgotPw,ResetPw authStyle
    class MailSent externalStyle
```

## 管理者向けフロー詳細

### 6. 管理画面への遷移フロー

管理画面は共通レイアウト（`AdminLayout`）のサイドバー（`AdminSidebar`）で相互に遷移する。

```mermaid
graph TD
    Home[S001: ホーム画面<br/>管理者でログイン済み] -- ヘッダー: 管理者画面 --> Guard{ルートガード<br/>requiresAuth + requiresAdmin}
    Guard -- 未ログイン --> Login[S011: ログイン画面<br/>/login?redirect=...]
    Guard -- 管理者以外 --> Home
    Guard -- 管理者 --> SA003

    subgraph AdminLayout [AdminLayout + AdminSidebar]
        SA003[SA003: 特典管理<br/>/admin/benefits]
        SA004[SA004: 特典条件管理<br/>/admin/benefit-eligibilities]
        SA002[SA002: 特典カテゴリ管理<br/>/admin/benefit-categories]
        SA007[SA007: 自治体管理<br/>/admin/municipalities]
        SA001[SA001: 事業者管理<br/>/admin/agencies]
        SA006[SA006: 運賃割引管理<br/>/admin/fare-discounts]
        SA005[SA005: コミュニティバス路線管理<br/>/admin/community-buses]
        SA008[SA008: ユーザー管理<br/>/admin/users]
    end

    classDef authStyle fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef adminStyle fill:#e8eaf6,stroke:#283593,stroke-width:2px
    classDef mainStyle fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    class Login authStyle
    class Home mainStyle
    class SA001,SA002,SA003,SA004,SA005,SA006,SA007,SA008 adminStyle
```

> サイドバーの並び順は図の subgraph 内の順序と同じ。サイドバーから全管理画面へ相互に遷移できる（図では省略）。

### 7. 管理画面共通操作フロー

```mermaid
sequenceDiagram
    participant Admin as 管理者
    participant List as 一覧画面
    participant Form as 編集フォーム
    participant API as Backend API

    Admin->>List: 一覧画面にアクセス
    List->>API: GET /admin/{resource}?page=0&size=20
    API-->>List: ページングデータ返却

    Admin->>List: キーワード・条件で絞り込み
    List->>API: GET /admin/{resource}?keyword=...
    API-->>List: 絞り込み結果

    Admin->>Form: 新規作成 / 編集ボタン
    Form->>API: POST or PUT /admin/{resource}
    API-->>Form: 作成・更新結果
    Form-->>List: 一覧に戻る

    Admin->>List: CSVインポート
    List->>API: POST /admin/{resource}/import (multipart)
    API-->>List: インポート結果（登録件数・更新件数・失敗件数・エラー詳細）

    Admin->>List: 削除ボタン
    List->>API: DELETE /admin/{resource}/{id}
    API-->>List: 削除完了
```

- ユーザー管理（`/admin/users`）は新規作成・CSVインポートを持たず、一覧・詳細・更新・削除のみ。
- 特典管理（`/admin/benefits`）は、座標一括取得（`POST /admin/benefits/geocode`）と確認済み更新（`POST /admin/benefits/{benefitId}/confirm`）も持つ。
- 運賃割引の更新・削除は複合キー（`/admin/fare-discounts/{benefitId}/{agencyId}`）を使用する。

---

## 画面遷移の詳細仕様

### 遷移方法の種類

| 遷移方法 | 説明 | 実装方式 | 使用場面 |
|----------|------|----------|----------|
| ページ遷移 | 新しい画面に完全移行 | Vue Router | メイン機能間の移動 |
| モーダル表示 | 現在画面の上にオーバーレイ | `AppDialog`（PrimeVue Dialog） | 確認ダイアログ、フィードバック、ライセンス表示 |
| タブ切り替え | 同一画面内のコンテンツ変更 | `AppTab`（PrimeVue Tabs） | ホーム画面のサイドバー（ルート案内・利用できる特典・特典を探す） |

### 認証が必要な画面

**認証必要画面**（未ログイン時は `/login?redirect=...` にリダイレクト）:
- S013: プロフィール画面
- S014: パスワード変更画面

**管理者権限が必要な画面**（未ログイン時は `/login?redirect=...`、管理者以外は `/` にリダイレクト）:
- SA001-SA008: 管理画面（`/admin/*`）

**パブリック画面**:
- S001: ホーム画面（ゲスト利用可能。共有URL `/?benefitId={id}` もログイン不要）
- S011: ログイン画面（ログイン済みの場合は `/` にリダイレクト）
- S012: 会員登録画面
- S015: パスワードリセット要求画面
- S016: パスワードリセット画面
- S021: サポート情報画面
