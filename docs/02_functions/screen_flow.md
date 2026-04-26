# 画面遷移図

本システムの画面間の遷移フローと操作の流れを図示します。

## 全体画面遷移図

```mermaid
graph TD
    Start([アプリケーション開始]) --> Home[S001: ホーム画面]

    Home --> Login[S011: ログイン画面]
    Login --> Signup[S012: 会員登録画面]
    Login --> ForgotPw[S015: パスワードリセット要求画面]
    Login --> Home
    Signup --> Login
    ForgotPw --> ResetPw[S016: パスワードリセット画面]
    ResetPw --> Login

    Home --> Profile[S013: プロフィール画面]
    Profile --> ChangePw[S014: パスワード変更画面]
    ChangePw --> Profile
    Home --> SupportInfo[S021: サポート情報画面]

    classDef startStyle fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef mainStyle fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef authStyle fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef userStyle fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef supportStyle fill:#fce4ec,stroke:#880e4f,stroke-width:2px

    class Start startStyle
    class Home mainStyle
    class Login,Signup,ForgotPw,ResetPw authStyle
    class Profile,ChangePw userStyle
    class SupportInfo supportStyle
```

## エンドユーザー向けフロー詳細

### 1. 基本利用フロー（未登録ユーザー）

```mermaid
sequenceDiagram
    participant User as ユーザー
    participant Home as ホーム画面
    participant Sidebar as サイドバー

    User->>Home: アクセス
    Home->>Home: 地図表示
    User->>Sidebar: 「特典を探す」タブ選択
    Sidebar->>Home: 特典マーカー表示
    User->>Sidebar: 「ルート案内」タブ選択
    User->>Sidebar: 出発地・目的地入力
    Sidebar->>Home: 経路計算・地図上に表示
```

### 2. 会員登録・ログインフロー

```mermaid
graph TD
    Start([開始]) --> Home[S001: ホーム画面 ゲスト]
    Home -- ログインボタン --> Login[S011: ログイン画面]
    Login -- 新規登録リンク --> Signup[S012: 会員登録画面]
    Signup -- 登録完了 --> Login
    Login -- 認証成功 --> HomeAuth[S001: ホーム画面 ログイン済み]
    HomeAuth -- 設定ボタン --> Profile[S013: プロフィール画面]
    Profile -- 保存完了 --> HomeAuth
    HomeAuth -- ログアウト --> Home

    classDef mainStyle fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef authStyle fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef userStyle fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px

    class Home,HomeAuth mainStyle
    class Login,Signup authStyle
    class Profile userStyle
```

### 3. パスワード変更フロー（ログイン済みユーザー）

```mermaid
graph TD
    Profile[S013: プロフィール画面] -- パスワード変更リンク --> ChangePw[S014: パスワード変更画面]
    ChangePw -- 変更成功 --> Profile
    ChangePw -- キャンセル --> Profile

    classDef userStyle fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    class Profile,ChangePw userStyle
```

### 4. パスワードリセットフロー（未ログインユーザー）

```mermaid
graph TD
    Login[S011: ログイン画面] -- パスワードを忘れた場合 --> ForgotPw[S015: パスワードリセット要求画面]
    ForgotPw -- メール送信 --> MailSent[メール受信確認]
    MailSent -- URLクリック --> ResetPw[S016: パスワードリセット画面]
    ResetPw -- リセット成功 --> Login
    ForgotPw -- ログインに戻る --> Login

    classDef authStyle fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef externalStyle fill:#e3f2fd,stroke:#1976d2,stroke-width:2px
    class Login,ForgotPw,ResetPw authStyle
    class MailSent externalStyle
```

## 管理者向けフロー詳細

### 5. 管理者ログイン・画面遷移フロー

```mermaid
graph TD
    AdminLogin[S011: ログイン画面\n管理者アカウントで認証] --> AdminDash{管理メニュー選択}

    AdminDash --> SA001[SA001: 事業者管理画面]
    AdminDash --> SA002[SA002: 特典種別管理画面]
    AdminDash --> SA003[SA003: 特典管理画面]
    AdminDash --> SA004[SA004: 特典条件管理画面]
    AdminDash --> SA005[SA005: コミュニティバス路線管理画面]
    AdminDash --> SA006[SA006: 運賃割引管理画面]
    AdminDash --> SA007[SA007: 自治体管理画面]
    AdminDash --> SA008[SA008: ユーザー管理画面]

    SA001 -- CRUD/CSVインポート --> SA001
    SA002 -- CRUD/CSVインポート --> SA002
    SA003 -- CRUD/CSVインポート --> SA003
    SA004 -- CRUD/CSVインポート --> SA004
    SA005 -- CRUD/CSVインポート --> SA005
    SA006 -- CRUD/CSVインポート --> SA006
    SA007 -- CRUD/CSVインポート --> SA007
    SA008 -- 参照・編集・削除 --> SA008

    SA001 --> AdminDash
    SA002 --> AdminDash
    SA003 --> AdminDash
    SA004 --> AdminDash
    SA005 --> AdminDash
    SA006 --> AdminDash
    SA007 --> AdminDash
    SA008 --> AdminDash

    classDef authStyle fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef adminStyle fill:#e8eaf6,stroke:#283593,stroke-width:2px
    classDef menuStyle fill:#f3e5f5,stroke:#4a148c,stroke-width:2px

    class AdminLogin authStyle
    class AdminDash menuStyle
    class SA001,SA002,SA003,SA004,SA005,SA006,SA007,SA008 adminStyle
```

### 6. 管理画面共通操作フロー

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
    API-->>List: インポート結果（成功件数・失敗件数・エラー詳細）

    Admin->>List: 削除ボタン
    List->>API: DELETE /admin/{resource}/{id}
    API-->>List: 削除完了
```

---

## 画面遷移の詳細仕様

### 遷移方法の種類

| 遷移方法 | 説明 | 実装方式 | 使用場面 |
|----------|------|----------|----------|
| ページ遷移 | 新しい画面に完全移行 | Vue Router | メイン機能間の移動 |
| モーダル表示 | 現在画面の上にオーバーレイ | Vue Modal | 確認ダイアログ、詳細表示 |
| サイドパネル | 画面の一部をスライド表示 | CSS Transform | 検索結果、メニュー |
| タブ切り替え | 同一画面内のコンテンツ変更 | Vue Tab Component | 設定項目、詳細情報 |

### 認証が必要な画面

**認証必要画面**（未ログイン時は `/login?redirect=...` にリダイレクト）:
- S013: プロフィール画面
- S014: パスワード変更画面

**パブリック画面**:
- S001: ホーム画面（ゲスト利用可能）
- S011: ログイン画面
- S012: 会員登録画面
- S015: パスワードリセット要求画面
- S016: パスワードリセット画面
- S021: サポート情報画面
