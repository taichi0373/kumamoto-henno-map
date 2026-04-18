# プロジェクト構造

## 構成哲学

モノレポ + レイヤードアーキテクチャ。フロントエンドは Atomic Design、バックエンドは MVC + サービス層で統一。

## ディレクトリパターン

### フロントエンド — Atomic Design（`apps/front/src/`）

**atoms** (`components/atoms/`)
- 役割: PrimeVue を薄くラップした最小 UI 要素（ビジネスロジックなし）
- 命名: `App` プレフィックス + PascalCase（例: `AppButton.vue`, `AppTextField.vue`）
- 各コンポーネントに `.stories.js` を対応させる

**molecules** (`components/molecules/`)
- 役割: atoms を組み合わせたドメイン機能コンポーネント（例: `AppSearchBenefit.vue`）

**organisms** (`components/organisms/`)
- 役割: 画面セクション単位の複合コンポーネント（例: `AppHeader.vue`）

**pages** (`pages/`)
- 役割: Vue Router のルートと 1:1 対応する画面コンポーネント
- 命名: `PascalCase` + `Page` サフィックス（例: `HomePage.vue`）

**stores** (`stores/`)
- 役割: Pinia ストア（状態管理）
- 命名: camelCase（例: `auth.ts`）

**utils** (`utils/`)
- 役割: Composables・ユーティリティ関数・定数
- テストファイルは同一ディレクトリに配置（例: `validateUtils.ts` + `validateUtils.test.ts`）

### バックエンド — レイヤードアーキテクチャ（`apps/back/src/main/java/...`）

**controller**
- REST エンドポイント定義、例外は `try-catch` + `ApiResponseDto` で統一
- エンドポイントは kebab-case + RESTful 設計

**service**
- ビジネスロジック、トランザクション管理

**repository/dao**
- Doma 2 DAO（`@Dao` + `@ConfigAutowireable`）+ Entityql タイプセーフクエリ
- `default` メソッドで Entityql クエリを実装

**repository/entity**
- Doma 2 エンティティ（`@Table`/`@Column` は小文字スネークケース）
- `Serializable` 実装 + `serialVersionUID = 1L`

**security / util / config**
- `JwtAuthenticationFilter`, `JwtUtil`, `SecurityConfig` など横断的関心事

### データベース（`config/database/`）
- `DDL/TABLE/` — テーブル定義 SQL
- `DML/TABLE/` — マスターデータ INSERT SQL
- `setup.sql` — 全テーブル・ビュー作成スクリプト

**ルール**: スキーマ変更時は DDL・DML・Entity を必ず同時更新する。

## 命名規則

| 対象 | ルール | 例 |
|------|--------|----|
| Vue ファイル | PascalCase | `BenefitCard.vue` |
| TypeScript 変数 | camelCase | `isLoggedIn` |
| boolean | `is` / `has` / `can` プレフィックス | `isLoading` |
| 関数 | 動詞で開始 | `fetchBenefits()` |
| CSS クラス | kebab-case (BEM) | `benefit-card__title` |
| Java クラス | PascalCase + 役割サフィックス | `BenefitController`, `BenefitEntity` |
| API エンドポイント | kebab-case RESTful | `/benefit-map/api/v1/benefit-users` |

## インポート規則（フロントエンド）

```typescript
// 外部ライブラリ
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

// 内部モジュール（エイリアス）
import { useAuthStore } from '@/stores/auth'
import AppButton from '@/components/atoms/AppButton.vue'

// 同一ディレクトリ（相対）
import { validateEmail } from './validateUtils'
```

`@/` は `apps/front/src/` にマップ。

## Vue コンポーネント記述順

テンプレート → スクリプト → スタイル の順で記述。スクリプト内は: import → props/emits → 定数 → ref/reactive → computed → watch → 関数 → ライフサイクル。

## SCSS 規則

全コンポーネントで `@use "@/assets/scss/base"` を先頭に記述し、`<style scoped lang="scss">` を使用。

---
_Document: 組織パターンと命名規則。ファイルツリーの列挙ではなく_
