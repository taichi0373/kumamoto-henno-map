# 画面遷移図

本システムの画面間の遷移フローと操作の流れを図示します。

## 全体画面遷移図

```mermaid
graph TD
    Start([アプリケーション開始]) --> Home[S001: ホーム画面]

    Home --> Login[S011: ログイン画面]
    Login --> Signup[S012: 会員登録画面]
    Login --> Home
    Signup --> Login

    Home --> Profile[S013: プロフィール画面]
    Home --> SupportInfo[S021: サポート情報画面]
    classDef startStyle fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef mainStyle fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef authStyle fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef userStyle fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef supportStyle fill:#fce4ec,stroke:#880e4f,stroke-width:2px

    class Start startStyle
    class Home mainStyle
    class Login,Signup authStyle
    class Profile userStyle
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

## 画面遷移の詳細仕様

### 遷移方法の種類

| 遷移方法 | 説明 | 実装方式 | 使用場面 |
|----------|------|----------|----------|
| ページ遷移 | 新しい画面に完全移行 | Vue Router | メイン機能間の移動 |
| モーダル表示 | 現在画面の上にオーバーレイ | Vue Modal | 確認ダイアログ、詳細表示 |
| サイドパネル | 画面の一部をスライド表示 | CSS Transform | 検索結果、メニュー |
| タブ切り替え | 同一画面内のコンテンツ変更 | Vue Tab Component | 設定項目、詳細情報 |

### 認証が必要な画面

**認証必要画面**:
- S013: プロフィール画面

**パブリック画面**:
- S001: ホーム画面（ゲスト利用可能）
- S011: ログイン画面
- S012: 会員登録画面
- S021: サポート情報画面
