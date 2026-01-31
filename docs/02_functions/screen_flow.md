# 画面遷移図

本システムの画面間の遷移フローと操作の流れを図示します。

## 全体画面遷移図

```text
graph TD
    Start([アプリケーション開始]) --> Home[S001: ホーム画面]
    
    %% メイン機能フロー
    Home --> Detail[S002: 特典詳細画面]
    Home --> Results[S003: 検索結果画面]
    Detail --> Route[S004: 経路案内画面]
    Results --> Detail
    
    %% 認証フロー
    Home --> Login[S011: ログイン画面]
    Login --> Register[S012: 会員登録画面]
    Login --> Home
    Register --> Home
    
    %% ユーザー機能
    Home --> Profile[S013: プロフィール画面]
    Profile --> PasswordChange[S014: パスワード変更画面]
    Home --> Favorites[S015: お気に入り画面]
    Favorites --> Detail
    
    %% サポート機能
    Home --> Support[S021: サポート情報画面]
    
    %% スタイリング
    classDef startStyle fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef mainStyle fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef authStyle fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef userStyle fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef supportStyle fill:#fce4ec,stroke:#880e4f,stroke-width:2px
    
    class Start startStyle
    class Home,Detail,Results,Route mainStyle
    class Login,Register authStyle
    class Profile,PasswordChange,Favorites userStyle
    class Support supportStyle
```
    Support --> Contact[S022: 問い合わせ画面]
    Support --> FAQ[S023: FAQ画面]
    Home --> Terms[S024: 利用規約画面]
    
    %% 管理者フロー
    AdminStart([管理者ログイン]) --> AdminLogin[A001: 管理者ログイン画面]
    AdminLogin --> Dashboard[A002: ダッシュボード画面]
    
    %% 特典管理
    Dashboard --> BenefitList[A011: 特典一覧画面]
    BenefitList --> BenefitCreate[A012: 特典登録画面]
    BenefitList --> BenefitEdit[A013: 特典編集画面]
    Dashboard --> BenefitBulk[A014: 特典一括登録画面]
    
    %% 事業者管理
    Dashboard --> AgencyList[A021: 事業者一覧画面]
    AgencyList --> AgencyCreate[A022: 事業者登録画面]
    AgencyList --> AgencyEdit[A023: 事業者編集画面]
    
    %% ユーザー管理
    Dashboard --> UserList[A031: ユーザー一覧画面]
    UserList --> UserDetail[A032: ユーザー詳細画面]
    
    %% システム管理
    Dashboard --> LogViewer[A041: ログ確認画面]
    Dashboard --> Statistics[A042: 統計画面]
    Dashboard --> Settings[A043: 設定画面]

    %% スタイル定義
    classDef userScreen fill:#e1f5fe
    classDef authScreen fill:#f3e5f5
    classDef adminScreen fill:#fff3e0
    classDef supportScreen fill:#e8f5e8
    
    class Home,Detail,Results,Route userScreen
    class Login,Register,Profile,PasswordChange,Favorites authScreen
    class Support,Contact,FAQ,Terms supportScreen
    class AdminLogin,Dashboard,BenefitList,BenefitCreate,BenefitEdit,BenefitBulk,AgencyList,AgencyCreate,AgencyEdit,UserList,UserDetail,LogViewer,Statistics,Settings adminScreen
```

## エンドユーザー向けフロー詳細

### 1. 基本利用フロー（未登録ユーザー）

```text
sequenceDiagram
    participant User as ユーザー
    participant Home as ホーム画面
    participant Detail as 特典詳細画面
    participant Route as 経路案内画面
    
    User->>Home: アクセス
    Home->>Home: 地図表示
    User->>Home: 検索条件入力
    Home->>Home: 特典マーカー表示
    User->>Detail: マーカー選択
    Detail->>Detail: 特典詳細表示
    User->>Route: 経路案内ボタン
    Route->>Route: 経路計算・表示
```

### 2. 会員登録・ログインフロー

```text
stateDiagram-v2
    [*] --> ゲスト利用
    ゲスト利用 --> ログイン画面 : ログインボタン
    ログイン画面 --> 会員登録画面 : 新規登録リンク
    会員登録画面 --> ホーム画面 : 登録完了
    ログイン画面 --> ホーム画面 : 認証成功
    ログイン画面 --> パスワードリセット : パスワード忘れ
    パスワードリセット --> ログイン画面 : リセット完了
    ホーム画面 --> プロフィール画面 : 設定ボタン
    プロフィール画面 --> ホーム画面 : 保存完了
    ホーム画面 --> [*] : ログアウト
```

### 3. 特典検索・表示フロー

```text
flowchart TD
    A[ホーム画面] --> B{検索方法選択}
    B -->|キーワード検索| C[キーワード入力]
    B -->|地図操作| D[地図移動・ズーム]
    B -->|フィルター| E[条件選択]
    
    C --> F[検索実行]
    D --> F
    E --> F
    
    F --> G{結果確認}
    G -->|結果あり| H[マーカー・リスト表示]
    G -->|結果なし| I[「該当なし」メッセージ]
    
    H --> J[特典選択]
    J --> K[特典詳細画面]
    K --> L{アクション選択}
    
    L -->|経路確認| M[経路案内画面]
    L -->|お気に入り| N[ブックマーク保存]
    L -->|戻る| H
    
    I --> O[検索条件変更]
    O --> F
```

## 管理者向けフロー詳細

### 1. 管理者認証・ダッシュボード

```text
sequenceDiagram
    participant Admin as 管理者
    participant LoginPage as ログイン画面
    participant Dashboard as ダッシュボード
    participant AuthServer as 認証サーバー
    
    Admin->>LoginPage: 管理者URLアクセス
    Admin->>LoginPage: 認証情報入力
    LoginPage->>AuthServer: 認証リクエスト
    AuthServer->>LoginPage: 認証結果
    alt 認証成功
        LoginPage->>Dashboard: リダイレクト
        Dashboard->>Dashboard: 管理メニュー表示
    else 認証失敗
        LoginPage->>LoginPage: エラー表示
    end
```

### 2. 特典管理フロー

```text
stateDiagram-v2
    [*] --> ダッシュボード
    ダッシュボード --> 特典一覧画面 : 特典管理選択
    
    特典一覧画面 --> 特典登録画面 : 新規登録ボタン
    特典一覧画面 --> 特典編集画面 : 編集ボタン
    特典一覧画面 --> 特典詳細表示 : 詳細ボタン
    特典一覧画面 --> 削除確認ダイアログ : 削除ボタン
    
    特典登録画面 --> 入力確認画面 : 登録ボタン
    入力確認画面 --> 特典一覧画面 : 確定ボタン
    入力確認画面 --> 特典登録画面 : 修正ボタン
    
    特典編集画面 --> 更新確認画面 : 更新ボタン
    更新確認画面 --> 特典一覧画面 : 確定ボタン
    更新確認画面 --> 特典編集画面 : 修正ボタン
    
    削除確認ダイアログ --> 特典一覧画面 : 削除実行
    削除確認ダイアログ --> 特典一覧画面 : キャンセル
    
    特典一覧画面 --> ダッシュボード : 戻るボタン
```

### 3. 事業者管理フロー

```text
flowchart LR
    A[ダッシュボード] --> B[事業者一覧画面]
    B --> C[新規事業者登録]
    B --> D[事業者情報編集]
    B --> E[事業者削除]
    
    C --> F[基本情報入力]
    F --> G[連絡先情報入力]
    G --> H[確認画面]
    H --> I[登録完了]
    I --> B
    
    D --> J[情報更新]
    J --> K[更新確認]
    K --> B
    
    E --> L[削除確認]
    L --> B
```

## 画面遷移の詳細仕様

### 遷移方法の種類

| 遷移方法 | 説明 | 実装方式 | 使用場面 |
|----------|------|----------|----------|
| ページ遷移 | 新しい画面に完全移行 | Vue Router | メイン機能間の移動 |
| モーダル表示 | 現在画面の上にオーバーレイ | Vue Modal | 確認ダイアログ、詳細表示 |
| サイドパネル | 画面の一部をスライド表示 | CSS Transform | 検索結果、メニュー |
| タブ切り替え | 同一画面内のコンテンツ変更 | Vue Tab Component | 設定項目、詳細情報 |
| アコーディオン | 折りたたみ可能なセクション | CSS + JavaScript | FAQ、設定詳細 |

### 認証が必要な画面

```text
flowchart TD
    A[画面アクセス] --> B{認証状態確認}
    B -->|認証済み| C[画面表示]
    B -->|未認証| D{画面種別確認}
    
    D -->|パブリック画面| C
    D -->|認証必要画面| E[ログイン画面表示]
    E --> F[認証処理]
    F -->|成功| G[元の画面に遷移]
    F -->|失敗| H[エラーメッセージ表示]
    H --> E
    
    style E fill:#ffeb3b
    style G fill:#4caf50
    style H fill:#f44336
```

**認証必要画面**:
- S013: プロフィール画面
- S014: パスワード変更画面
- S015: お気に入り画面
- 全ての管理者画面（A001-A043）

**パブリック画面**:
- S001: ホーム画面（ゲスト利用可能）
- S002: 特典詳細画面
- S011: ログイン画面
- S012: 会員登録画面
- S021: サポート情報画面
- S024: 利用規約画面

### ブラウザ履歴管理

```javascript
// Vue Routerの履歴管理設定例
const router = createRouter({
  history: createWebHistory(),
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})
```

**戻るボタンの動作**:
- **標準動作**: ブラウザの戻るボタンで前画面に戻る
- **モーダル**: ESCキーまたは背景クリックで閉じる
- **検索結果**: 検索条件を保持して戻る
- **編集画面**: 未保存の変更がある場合は確認ダイアログ表示

### エラーハンドリングフロー

```text
flowchart TD
    A[画面操作実行] --> B{エラー発生?}
    B -->|なし| C[正常処理継続]
    B -->|あり| D{エラー種別判定}
    
    D -->|ネットワークエラー| E[リトライ機能付きエラー表示]
    D -->|認証エラー| F[ログイン画面に遷移]
    D -->|バリデーションエラー| G[入力フィールドにエラー表示]
    D -->|サーバーエラー| H[システムエラー画面表示]
    D -->|権限エラー| I[権限不足メッセージ表示]
    
    E --> J{リトライ結果}
    J -->|成功| C
    J -->|失敗| K[エラー画面表示]
    
    style E fill:#ff9800
    style F fill:#f44336
    style G fill:#ffeb3b
    style H fill:#f44336
    style I fill:#ff5722
```

### パフォーマンス最適化

**画面遷移の高速化**:
- **プリフェッチング**: 次に遷移する可能性の高い画面のデータを事前取得
- **レイジーローディング**: 必要になった時点でコンポーネントを読み込み
- **キャッシュ活用**: 一度取得したデータの再利用
- **画面分割**: 大きな画面を複数のコンポーネントに分割

```javascript
// レイジーローディングの実装例
const routes = [
  {
    path: '/benefits/:id',
    component: () => import('../views/BenefitDetail.vue')
  }
]
```

## 画面遷移のガイドライン

### UXデザイン原則

1. **予測可能性**: ユーザーが遷移先を予想できるナビゲーション
2. **一貫性**: 同様の操作は同様の結果を返す
3. **フィードバック**: 遷移中の状態を視覚的に表示
4. **復帰性**: 前の状態に簡単に戻れる設計
5. **効率性**: 最小操作数でゴールに到達

### アニメーション設計

```css
/* 画面遷移アニメーション */
.page-transition-enter-active,
.page-transition-leave-active {
  transition: all 0.3s ease;
}

.page-transition-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.page-transition-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
```

### モバイル対応

- **スワイプジェスチャー**: 左右スワイプでの画面切り替え
- **プルツーリフレッシュ**: 下向きスワイプでのデータ更新
- **タッチフレンドリー**: 最小44px以上のタッチターゲット
- **バックボタン対応**: Androidの戻るボタンハンドリング

### 状態管理

```javascript
// Vuexでの画面状態管理例
const store = createStore({
  state: {
    currentUser: null,
    searchFilters: {},
    searchResults: [],
    selectedBenefit: null
  },
  mutations: {
    SET_SEARCH_FILTERS(state, filters) {
      state.searchFilters = filters
    },
    SET_SEARCH_RESULTS(state, results) {
      state.searchResults = results
    }
  }
})
```

画面間でのデータ共有と状態の一貫性を保つため、適切な状態管理を実装します。