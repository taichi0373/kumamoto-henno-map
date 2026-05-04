# 命名規則

## 一覧

| 対象 | 形式 | 例 |
|------|------|----|
| Javaクラス・インターフェース | PascalCase | `BenefitService`, `BenefitDao` |
| Javaメソッド・変数 | camelCase | `benefitId`, `findByMunicipalityCd` |
| Java定数 | UPPER_SNAKE_CASE | `DEFAULT_CATEGORY_CD` |
| Javaパッケージ | 小文字・ドット区切り | `io.github.taichi0373.kumamoto_henno_map.controller` |
| Vueコンポーネント・ファイル | `App` プレフィックス + PascalCase | `AppButton.vue`, `AppSearchBenefit.vue` |
| ページコンポーネント | PascalCase + `Page` サフィックス | `HomePage.vue`, `LoginPage.vue` |
| TS変数・関数 | camelCase | `benefitList`, `searchBenefits` |
| TS定数 | UPPER_SNAKE_CASE | `API_BASE_URL`, `MAX_SEARCH_RESULTS` |
| TS型・インターフェース | PascalCase | `BenefitDto`, `SearchParams` |
| Composable | `use` + PascalCase | `useMap`, `useAuth` |
| DBテーブル・カラム | UPPER_SNAKE_CASE | `BENEFIT`, `BENEFIT_NAME` |
| DB制約 | `テーブル名_制約種別_カラム名` | `BENEFIT_PK`, `BENEFIT_FK_MUNICIPALITY` |
| DBインデックス | `IX_テーブル名_カラム名` | `IX_BENEFIT_MUNICIPALITY_CD` |
| APIエンドポイント | kebab-case・RESTful | `/auth/login`, `/benefit/search` |
| APIクエリパラメータ・JSONフィールド | camelCase | `municipalityCd`, `benefitName` |
| Gitブランチ | `種別/説明`（kebab-case） | `feature/add-benefit-search` |
| Gitコミット | `種別: 説明` | `feat: 特典検索機能を追加` |

## Java

### Entity・DTO

```java
// Entity: テーブル名は小文字スネークケースで指定（PostgreSQLは大文字小文字を区別しない）
@Entity(metamodel = @Metamodel)
@Table(name = "benefit")
public class BenefitEntity implements Serializable {
    @Column(name = "benefit_id")
    private String benefitId;      // Javaフィールド: camelCase

    @Column(name = "municipality_cd")
    private String municipalityCd;
}

// DTO: Request / Response / Dto サフィックスで用途を明示
public class BenefitSearchRequest {}
public class BenefitDetailResponse {}
```

## TypeScript / Vue

### ファイル構成例

```
src/
  components/
    atoms/      AppButton.vue, AppTextField.vue, AppSelect.vue, AppDialog.vue ...
    molecules/  AppLicenseInfo.vue, AppInputGroupWithButton.vue
    organisms/  AppHeader.vue, AppSearchBenefit.vue, AppRouteGuidance.vue ...
  pages/        HomePage.vue, LoginPage.vue, ProfilePage.vue, SignupPage.vue ...
  dto/          benefitDto.ts, routeDto.ts, usersDto.ts ...
  router/       index.ts
  stores/       auth.ts
  utils/        api.ts, validateUtils.ts, useMap.ts, messageConstant.ts ...
```

> Composableは `utils/` に配置する（例: `useMap.ts`）

### boolean変数・関数名の規則

```typescript
// boolean: is / has / can で開始
const isLoading = ref(false)
const hasError = ref(false)

// 関数: 動詞で開始
function searchBenefits() {}
function validateInput() {}
```

## Git

### ブランチ種別

| 種別 | 用途 |
|------|------|
| `feature/` | 新機能開発 |
| `bugfix/` | バグ修正 |
| `hotfix/` | 緊急修正 |
| `docs/` | ドキュメント更新 |

### コミット種別

| 種別 | 用途 |
|------|------|
| `feat` | 新機能 |
| `fix` | バグ修正 |
| `docs` | ドキュメント |
| `refactor` | リファクタリング |
| `test` | テスト |
