# 命名規則

本プロジェクトで統一的に使用する命名規則を定義します。一貫性のある命名により、コードの可読性と保守性を向上させます。

## 基本原則

### 1. 可読性の重視

- 略語よりも完全な単語を使用
- 文脈から意味が理解できる名前
- 一般的でない専門用語には適切なコメント

### 2. 一貫性の保持

- 同一概念には同一の命名パターンを適用
- プロジェクト全体で統一されたスタイル
- 既存コードとの整合性を維持

### 3. 言語特性の尊重

- 各プログラミング言語の慣習に従う
- フレームワークの規約を遵守
- 標準ライブラリとの親和性を考慮

## Java（バックエンド）

### クラス名

**形式**: `PascalCase`（大文字始まり）

```java
// ✅ GOOD
public class BenefitService {}
public class MunicipalityEntity {}
public class UsersController {}

// ❌ BAD
public class benefitService {}
public class municipality_entity {}
public class userscontroller {}
```

### インターフェース名

**形式**: `PascalCase`

```java
// ✅ GOOD - DAOインターフェース
public interface BenefitDao {}
public interface AgencyDao {}

// ✅ GOOD - サービスインターフェース
public interface BenefitService {}
public interface UserService {}
```

### メソッド名・変数名

**形式**: `camelCase`（小文字始まり）

```java
// ✅ GOOD - メソッド名
public List<BenefitEntity> findByMunicipalityCd(String municipalityCd) {}
public void updateBenefitStatus(String benefitId, String status) {}
public boolean isEligibleForBenefit(UserEntity user, BenefitEntity benefit) {}

// ✅ GOOD - 変数名
String benefitId = "BF001";
List<MunicipalityEntity> municipalityList = new ArrayList<>();
LocalDateTime createdAt = LocalDateTime.now();

// ❌ BAD
String benefit_id = "BF001";
List<MunicipalityEntity> municipality_list = new ArrayList<>();
LocalDateTime created_at = LocalDateTime.now();
```

### 定数名

**形式**: `UPPER_SNAKE_CASE`（全て大文字、アンダースコア区切り）

```java
// ✅ GOOD
public static final String DEFAULT_CATEGORY_CD = "DEFAULT";
public static final int MAX_SEARCH_RESULTS = 100;
public static final String LICENSE_STATUS_SURRENDERED = "2";

// ❌ BAD
public static final String defaultCategoryCd = "DEFAULT";
public static final int maxSearchResults = 100;
```

### パッケージ名

**形式**: `小文字、ドット区切り`

```java
// ✅ GOOD
package io.github.taichi0373.benefit_map.controller;
package io.github.taichi0373.benefit_map.service;
package io.github.taichi0373.benefit_map.repository.dao;
package io.github.taichi0373.benefit_map.repository.entity;
package io.github.taichi0373.benefit_map.dto;
package io.github.taichi0373.benefit_map.config;
package io.github.taichi0373.benefit_map.util;

// ❌ BAD
package io.github.taichi0373.benefitMap.Controller;
package io.github.taichi0373.benefitmap.Service;
```

### Entityクラスの特則

```java
// ✅ GOOD - Entity命名
@Entity(metamodel = @Metamodel)
@Table(name = "benefit")
public class BenefitEntity implements Serializable {
    
    @Column(name = "benefit_id")
    private String benefitId;  // Javaフィールド: camelCase
    
    @Column(name = "municipality_cd")
    private String municipalityCd;  // DBカラム: snake_case
}
```

### DTOクラスの命名

```java
// ✅ GOOD
public class BenefitSearchRequest {}
public class BenefitDetailResponse {}
public class UserRegistrationDto {}

// ❌ BAD
public class BenefitSearchReq {}
public class BenefitDetailRes {}
```

## TypeScript/JavaScript（フロントエンド）

### ファイル名

**コンポーネント**: `App` プレフィックス + `PascalCase`
**ページ**: `PascalCase` + `Page` サフィックス
**その他（TS）**: `camelCase`

```
// ✅ GOOD - components/
AppButton.vue
AppSearchBenefit.vue
AppRouteGuidance.vue

// ✅ GOOD - pages/
HomePage.vue
LoginPage.vue
ProfilePage.vue

// ✅ GOOD - utils/ / stores/
api.ts
auth.ts
validateUtils.ts

// ❌ BAD
benefitSearch.vue
userProfile.vue
benefit-search.vue
```

### コンポーネント名

**Vue.js コンポーネント**: `<script setup>` + `App` プレフィックス + `PascalCase`

```typescript
// ✅ GOOD - <script setup> を使用
<script setup lang="ts">
const props = defineProps<{
  benefitId: string
}>()
</script>

// ✅ GOOD - テンプレート内での使用
<AppSearchBenefit />
<AppRouteGuidance />
<AppButton />

// ❌ BAD - Options API は使用しない
export default defineComponent({
  name: 'BenefitSearchForm'
})
```

### 変数・関数名

**形式**: `camelCase`

```typescript
// ✅ GOOD - 変数名
const benefitList: BenefitDto[] = []
const searchKeyword: string = ''
const isLoading: boolean = false
const userProfile: UserDto | null = null

// ✅ GOOD - 関数名
function searchBenefits(keyword: string): Promise<BenefitDto[]> {}
function validateUserInput(input: string): boolean {}
function formatDisplayName(name: string): string {}

// ❌ BAD
const benefit_list: BenefitDto[] = []
function search_benefits(keyword: string) {}
```

### 型定義

**インターフェース**: `PascalCase`
**型エイリアス**: `PascalCase`

```typescript
// ✅ GOOD - インターフェース
interface BenefitDto {
  benefitId: string
  benefitName: string
  municipalityCd: string
}

interface SearchParams {
  keyword?: string
  categoryCd?: string
  municipalityCd?: string
}

// ✅ GOOD - 型エイリアス
type LicenseStatus = '0' | '1' | '2' | '3' | '4'
type ApiResponse<T> = {
  data: T
  status: 'success' | 'error'
  message?: string
}
```

### 定数

**形式**: `UPPER_SNAKE_CASE`

```typescript
// ✅ GOOD
const API_BASE_URL = 'https://api.benefit-map.example.com'
const DEFAULT_MAP_ZOOM_LEVEL = 10
const LICENSE_STATUS_SURRENDERED = '2'
const MAX_SEARCH_RESULTS = 100

// ❌ BAD
const apiBaseUrl = 'https://api.benefit-map.example.com'
const defaultMapZoomLevel = 10
```

### Composables

**形式**: `use` + `PascalCase`

```typescript
// ✅ GOOD
export function useMap() {}
export function useAuth() {}
export function useBenefitSearch() {}
export function useGeolocation() {}

// ❌ BAD
export function map() {}
export function authComposable() {}
```

## データベース

### テーブル名

**形式**: `UPPER_SNAKE_CASE`（大文字、アンダースコア区切り）

```sql
-- ✅ GOOD
CREATE TABLE BENEFIT (...)
CREATE TABLE MUNICIPALITY (...)
CREATE TABLE BENEFIT_CATEGORY (...)
CREATE TABLE BENEFIT_ELIGIBILITY (...)

-- ❌ BAD
CREATE TABLE benefit (...)
CREATE TABLE Municipality (...)
CREATE TABLE benefitCategory (...)
```

### カラム名

**形式**: `UPPER_SNAKE_CASE`

```sql
-- ✅ GOOD
CREATE TABLE BENEFIT (
  BENEFIT_ID VARCHAR(20) NOT NULL,
  MUNICIPALITY_CD VARCHAR(6) NOT NULL,
  CATEGORY_CD VARCHAR(20) NOT NULL,
  BENEFIT_NAME VARCHAR(400),
  SYS_CREATED_AT TIMESTAMP,
  SYS_UPDATED_AT TIMESTAMP
)

-- ❌ BAD
CREATE TABLE BENEFIT (
  benefitId VARCHAR(20) NOT NULL,
  municipalityCd VARCHAR(6) NOT NULL,
  categorycd VARCHAR(20) NOT NULL
)
```

### 制約名

**形式**: `テーブル名_制約種別_カラム名`

```sql
-- ✅ GOOD - 主キー制約
CONSTRAINT BENEFIT_PK PRIMARY KEY (BENEFIT_ID)
CONSTRAINT MUNICIPALITY_PK PRIMARY KEY (MUNICIPALITY_CD)

-- ✅ GOOD - 外部キー制約
CONSTRAINT BENEFIT_FK_MUNICIPALITY 
  FOREIGN KEY (MUNICIPALITY_CD) REFERENCES MUNICIPALITY (MUNICIPALITY_CD)

CONSTRAINT BENEFIT_FK_CATEGORY 
  FOREIGN KEY (CATEGORY_CD) REFERENCES BENEFIT_CATEGORY (CATEGORY_CD)

-- ✅ GOOD - ユニーク制約
CONSTRAINT USERS_USERNAME_UNIQUE UNIQUE (USERNAME)
```

### インデックス名

**形式**: `IX_テーブル名_カラム名`

```sql
-- ✅ GOOD
CREATE INDEX IX_BENEFIT_MUNICIPALITY_CD ON BENEFIT (MUNICIPALITY_CD)
CREATE INDEX IX_BENEFIT_CATEGORY_CD ON BENEFIT (CATEGORY_CD)
CREATE INDEX IX_USERS_EMAIL ON USERS (EMAIL)
```

## API設計

### エンドポイント名

**形式**: `kebab-case`、RESTful設計
**ベースURL**: `http://localhost:8081/benefit-map/api`

```
// ✅ GOOD - RESTful API
POST   /auth/login
POST   /auth/logout
GET    /auth/csrf

GET    /users/{userId}
PUT    /users
POST   /users/signup

POST   /benefit/search
GET    /benefit/users/{userId}

GET    /municipality/all

POST   /route/search

// ❌ BAD
GET    /api/v1/getBenefits
POST   /api/v1/createBenefit
GET    /api/v1/benefit_search
```

### クエリパラメータ

**形式**: `camelCase`

```
// ✅ GOOD
POST /benefit/search?municipalityCd=431001&categoryCd=TRANSPORT

// ❌ BAD
POST /benefit/search?municipality_cd=431001&category_cd=TRANSPORT
```

### JSONフィールド名

**形式**: `camelCase`

```json
// ✅ GOOD
{
  "benefitId": "BF001",
  "benefitName": "バス運賃割引",
  "municipalityCd": "431001",
  "categoryCd": "TRANSPORT",
  "createdAt": "2024-01-01T00:00:00Z"
}

// ❌ BAD
{
  "benefit_id": "BF001",
  "benefit_name": "バス運賃割引",
  "municipality_cd": "431001"
}
```

## ファイル・ディレクトリ

### ディレクトリ名

**形式**: `kebab-case` または `小文字`

```
// ✅ GOOD
src/
  components/
    atoms/
    molecules/
    organisms/
  pages/
  utils/
  dto/
  router/

docs/
  01_overview/
  02_functions/
  03_database/
  04_interfaces/

config/
  database/
    DDL/
    DML/

// ❌ BAD
src/
  Components/
  Pages/
  Utils/
```

### 設定ファイル名

```
// ✅ GOOD
vue.config.js
tsconfig.json
.eslintrc.js
.prettierrc
docker-compose.yml
application.properties
build.gradle

// ❌ BAD
vueConfig.js
tsConfig.json
eslint.config.js
```

## Git

### ブランチ名

**形式**: `種別/説明` （kebab-case）

```bash
# ✅ GOOD
git checkout -b feature/add-benefit-search
git checkout -b bugfix/fix-map-rendering
git checkout -b hotfix/security-update
git checkout -b docs/update-api-documentation

# ❌ BAD
git checkout -b addBenefitSearch
git checkout -b fix_map_rendering
git checkout -b HOTFIX-security-update
```

### コミットメッセージ

**形式**: `種別: 説明`

```bash
# ✅ GOOD
git commit -m "feat: 特典検索機能を追加"
git commit -m "fix: 地図レンダリングのバグを修正"
git commit -m "docs: API仕様書を更新"
git commit -m "refactor: 認証ロジックをリファクタリング"
git commit -m "test: ユーザー登録のテストケースを追加"

# ❌ BAD
git commit -m "add search"
git commit -m "バグ修正"
git commit -m "Update documentation"
```

## コメント・ドキュメント

### Javaドキュメント

```java
/**
 * 特典情報を管理するサービスクラス。
 * 
 * @author システム開発チーム
 * @since 1.0
 */
public class BenefitService {
    
    /**
     * 指定された自治体コードの特典一覧を取得します。
     * 
     * @param municipalityCd 自治体コード
     * @return 特典エンティティのリスト
     * @throws IllegalArgumentException 自治体コードがnullまたは空文字の場合
     */
    public List<BenefitEntity> findByMunicipalityCd(String municipalityCd) {
        // 実装
    }
}
```

### TypeScriptドキュメント

```typescript
/**
 * 特典検索のためのパラメータ
 */
interface SearchParams {
  /** 検索キーワード */
  keyword?: string
  /** カテゴリコード */
  categoryCd?: string
  /** 自治体コード */
  municipalityCd?: string
}

/**
 * 特典を検索します
 * @param params 検索パラメータ
 * @returns 特典データのPromise
 */
function searchBenefits(params: SearchParams): Promise<BenefitDto[]> {
  // 実装
}
```

## 禁止事項・注意点

### 避けるべき命名

```java
// ❌ BAD - 略語の多用
class BnftSvc {}
String munCd = "431001";

// ❌ BAD - 意味不明な名前
class Manager {}
String data = "something";
int x = 10;

// ❌ BAD - ハンガリアン記法
String strBenefitName = "割引";
int intCount = 0;
bool bIsValid = true;
```

### 一貫性の保持

```java
// ✅ GOOD - 一貫したDAO命名
public interface BenefitDao {}
public interface MunicipalityDao {}
public interface UserDao {}

// ❌ BAD - 不一貫な命名
public interface BenefitDao {}
public interface MunicipalityRepository {}
public interface UserDataAccess {}
```

### 文化的配慮

- 日本語のローマ字表記は避ける（kumamotoではなく具体的な英語表現を使用）
- 英語として自然な表現を心がける
- 必要に応じて日本語コメントで補完