# TypeScript / Vue コーディング規約

## 基本方針

- Vue 3 では `<script setup>` を原則使用する
- Composition API を使用し、Options API（`export default`）は使用しない
- 1コンポーネント = 1責務とする
- 可読性・保守性を最優先とする
- ESLint / Prettier のルールに従う
- ロジックは可能な限り composables に分離する
- `any` は使用せず、明示的な型を付ける
- コメントは日本語で記述し、`/** */` を使用する

## コンポーネント規約

- ファイル構成は `template → script → style` の順とする
- ロジックの記述順:
  1. import
  2. props / emits
  3. 定数
  4. ref / reactive
  5. computed
  6. watch
  7. 関数
  8. ライフサイクル

## 命名規則

| 対象 | ルール |
|------|--------|
| ファイル名 | PascalCase |
| コンポーネント | PascalCase |
| 変数 | camelCase |
| boolean | is / has / can で開始 |
| 関数 | 動詞で開始 |
| CSSクラス | kebab-case（BEM準拠） |

## ディレクトリ構造

- `components/atoms/` — 単純なUI要素（ボタン、入力フィールド等）
- `components/molecules/` — atomsを組み合わせたコンポーネント
- `components/organisms/` — moleculesを組み合わせた複雑なコンポーネント
- `pages/` — 画面単位のコンポーネント（Vue Routerと対応）
- `layouts/` — 共通レイアウトコンポーネント専用
- `stores/` — Pinia ストア

## スタイル規約（SCSS）

- `<style scoped lang="scss">` を使用する
- 全コンポーネントで以下を必ず記述する:

```scss
@use "@/assets/scss/base";
```

## PrimeVue 使用規約

- PrimeVue は **atoms 層のみ** で使用する
- カスタムスタイルを当てる場合は、PrimeVueのクラス名を参考にする
