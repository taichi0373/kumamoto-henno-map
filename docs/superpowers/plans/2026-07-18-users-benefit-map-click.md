# 利用できる特典カードクリック時の地図連動 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 「利用できる特典」タブの店舗特典カードをクリックした際に、地図をその店舗の座標に移動させポップアップを表示する。

**Architecture:** `AppUsersBenefit.vue` の props 型を `BenefitDto[]` から `BenefitDetailDto[]` に変更し（バックエンドは既に緯度・経度を返している）、既存の `handleShowBenefitOnMap` パターンを利用できる特典タブにも適用する。マーカーが未存在の場合はクリック時に動的に追加する。

**Tech Stack:** Vue 3 + `<script setup>` + TypeScript、Pinia、MapLibre GL JS、PrimeVue（atoms のみ）

## Global Constraints

- Vue 3 `<script setup>` + Composition API 必須。Options API 禁止。
- ファイル構成順: `template → script → style`
- コメントは日本語で `/** */` 形式
- SCSS は `@use "@/assets/scss/base"` 必須
- PrimeVue は atoms 層のみ使用
- `any` 型の使用禁止
- ブランチ: `develop` から `feature/feat-users-benefit-map-click` を作成して作業する
- コミットメッセージは日本語で簡潔に（例: `feat: ○○機能を追加`）
- 完了後 `cd apps/front && npm run lint` でエラーなしを確認する

---

## ファイル構成

| 対象 | パス | 変更種別 |
|------|------|----------|
| カテゴリ定数追加 | `apps/front/src/utils/codeConstant.ts` | 修正 |
| ユーザー特典コンポーネント | `apps/front/src/components/organisms/AppUsersBenefit.vue` | 修正 |
| メインページ | `apps/front/src/pages/HomePage.vue` | 修正 |

---

## Task 1: ブランチ作成と定数追加

**Files:**
- Modify: `apps/front/src/utils/codeConstant.ts`

**Interfaces:**
- Produces: `codeConstant.CATEGORY_CD.SHOP` （値: `'BS001'`）— Task 2 で参照する

- [ ] **Step 1: develop ブランチから作業ブランチを作成する**

```bash
cd C:/Users/ta9ns/kumamoto-henno-map
git checkout develop
git pull
git checkout -b feature/feat-users-benefit-map-click
```

期待出力: `Switched to a new branch 'feature/feat-users-benefit-map-click'`

- [ ] **Step 2: codeConstant.ts に CATEGORY_CD を追加する**

`apps/front/src/utils/codeConstant.ts` を開き、`} as const;` の直前にある最後の項目の末尾にカンマで続けて以下を追加する。

変更前（ファイル末尾付近）:
```typescript
    // ページング
    PAGINATION: {
        ADMIN_PAGE_SIZE: 10,    // 管理者画面の1ページあたり表示件数
    },

} as const;
```

変更後:
```typescript
    // ページング
    PAGINATION: {
        ADMIN_PAGE_SIZE: 10,    // 管理者画面の1ページあたり表示件数
    },

    // カテゴリコード
    CATEGORY_CD: {
        SHOP: 'BS001', // 店舗
    },

} as const;
```

- [ ] **Step 3: lint を実行してエラーがないことを確認する**

```bash
cd C:/Users/ta9ns/kumamoto-henno-map/apps/front && npm run lint
```

期待出力: エラーなし（警告のみは許容）

- [ ] **Step 4: コミットする**

```bash
cd C:/Users/ta9ns/kumamoto-henno-map
git add apps/front/src/utils/codeConstant.ts
git commit -m "feat: カテゴリコード定数 CATEGORY_CD.SHOP を追加"
```

---

## Task 2: AppUsersBenefit.vue の変更

**Files:**
- Modify: `apps/front/src/components/organisms/AppUsersBenefit.vue`

**Interfaces:**
- Consumes:
  - `BenefitDetailDto` from `@/dto/benefitDetailDto` — `categoryCd: string | null`, `latitude: number | null`, `longitude: number | null` を持つ
  - `codeConstant.CATEGORY_CD.SHOP` （値: `'BS001'`）— Task 1 で追加済み
- Produces:
  - emit `'show-benefit-on-map'` with `benefit: BenefitDetailDto` — Task 3 の HomePage で受信する

- [ ] **Step 1: script ブロックの import を変更する**

`apps/front/src/components/organisms/AppUsersBenefit.vue` の `<script setup>` 内で `BenefitDto` の import を `BenefitDetailDto` に置き換える。

変更前:
```typescript
import { BenefitDto } from '@/dto/benefitDto'
```

変更後:
```typescript
import { BenefitDetailDto } from '@/dto/benefitDetailDto'
```

※ `BenefitDto` の import 行のみ削除する。他の import は変更しない。

- [ ] **Step 2: props の型を変更し、emits と isShopBenefit を追加する**

`<script setup>` 内の `props` 定義 〜 `openCategories` の宣言付近を以下のように変更する。

変更前（該当箇所、`AppUsersBenefit.vue` の 86〜96 行目付近）:
```typescript
const props = withDefaults(defineProps<{
  usersBenefits?: BenefitDto[];
}>(), {
  usersBenefits: () => [],
});

/** カテゴリリスト */
const categories = ref<BenefitCategory[]>([])
```

変更後:
```typescript
const props = withDefaults(defineProps<{
  usersBenefits?: BenefitDetailDto[];
}>(), {
  usersBenefits: () => [],
});

/** 店舗特典かつ座標ありの場合に地図連動を有効にする */
const emit = defineEmits<{
  (e: 'show-benefit-on-map', benefit: BenefitDetailDto): void
}>()

/** カテゴリリスト */
const categories = ref<BenefitCategory[]>([])
```

- [ ] **Step 3: isShopBenefit ヘルパーを追加する**

`toggleCategory` 関数の直前に以下を追加する（`/** アコーディオンの開閉を切り替える */` の手前）。

```typescript
/** 店舗特典かつ座標ありの場合に true を返す（地図連動クリック判定） */
const isShopBenefit = (benefit: BenefitDetailDto): boolean =>
  benefit.categoryCd === codeConstant.CATEGORY_CD.SHOP
  && benefit.latitude != null
  && benefit.longitude != null
```

- [ ] **Step 4: groupedBenefits の型注釈を更新する**

`groupedBenefits` computed の Map 型を `BenefitDetailDto` に変更する。

変更前:
```typescript
const groupedBenefits = computed(() => {
  const groups = new Map<string, { categoryCd: string; categoryName: string; displayOrder: number; benefits: BenefitDto[] }>()
  for (const benefit of props.usersBenefits) {
```

変更後:
```typescript
const groupedBenefits = computed(() => {
  const groups = new Map<string, { categoryCd: string; categoryName: string; displayOrder: number; benefits: BenefitDetailDto[] }>()
  for (const benefit of props.usersBenefits) {
```

- [ ] **Step 5: template の AppCard に @click ハンドラを追加する**

template 内の `<AppCard class="mb-3" :hoverable="true">` を以下のように変更する。

変更前:
```html
<AppCard class="mb-3" :hoverable="true">
```

変更後:
```html
<AppCard
  class="mb-3"
  :hoverable="true"
  @click="isShopBenefit(benefit) ? emit('show-benefit-on-map', benefit) : undefined"
>
```

- [ ] **Step 6: lint を実行してエラーがないことを確認する**

```bash
cd C:/Users/ta9ns/kumamoto-henno-map/apps/front && npm run lint
```

期待出力: エラーなし

- [ ] **Step 7: コミットする**

```bash
cd C:/Users/ta9ns/kumamoto-henno-map
git add apps/front/src/components/organisms/AppUsersBenefit.vue
git commit -m "feat: 利用できる特典の店舗カードに地図連動クリックを追加"
```

---

## Task 3: HomePage.vue の変更

**Files:**
- Modify: `apps/front/src/pages/HomePage.vue`

**Interfaces:**
- Consumes:
  - `BenefitDetailDto` from `@/dto/benefitDetailDto`
  - `emit 'show-benefit-on-map'` from `AppUsersBenefit` — benefit: `BenefitDetailDto`
  - `createBenefitMarker(lat, lon, name, detail, phone, url, address)` from `@/utils/markerConfig` — 既存関数、既に import 済み
  - `markerManager.value.getMarker(id: string)` — 既存メソッド
  - `markerManager.value.addMarker(id: string, marker, map)` — 既存メソッド
- Produces: なし（終端）

- [ ] **Step 1: usersBenefits の型を BenefitDetailDto[] に変更する**

`apps/front/src/pages/HomePage.vue` の 148 行目付近。

変更前:
```typescript
/** ユーザー特典データ */
const usersBenefits = ref<BenefitDto[]>([])
```

変更後:
```typescript
/** ユーザー特典データ */
const usersBenefits = ref<BenefitDetailDto[]>([])
```

- [ ] **Step 2: fetchUserBenefits 内の型キャストを変更する**

`fetchUserBenefits` 関数内（351 行目付近）。

変更前:
```typescript
usersBenefits.value = ((response.data as unknown) as { data: BenefitDto[] }).data || []
```

変更後:
```typescript
usersBenefits.value = ((response.data as unknown) as { data: BenefitDetailDto[] }).data || []
```

- [ ] **Step 3: BenefitDto の import を削除する**

HomePage.vue の 114 行目付近にある `BenefitDto` の import 行を削除する（`BenefitDetailDto` の import は残す）。

削除対象:
```typescript
import { BenefitDto } from '@/dto/benefitDto'
```

- [ ] **Step 4: template の AppUsersBenefit に @show-benefit-on-map を追加する**

template 内（31 行目付近）。

変更前:
```html
<AppUsersBenefit :users-benefits="usersBenefits" />
```

変更後:
```html
<AppUsersBenefit
  :users-benefits="usersBenefits"
  @show-benefit-on-map="handleShowBenefitOnMap"
/>
```

- [ ] **Step 5: handleShowBenefitOnMap にマーカー未存在時の追加ロジックを実装する**

`handleShowBenefitOnMap` 関数（557 行目付近）を以下に置き換える。

変更前:
```typescript
/** 特典カードクリック時に地図をパン＋ポップアップ表示 */
const handleShowBenefitOnMap = (benefit: BenefitDetailDto) => {
  if (!mapInstance.value || benefit.latitude == null || benefit.longitude == null) return
  mapInstance.value.flyTo({ center: [benefit.longitude, benefit.latitude], zoom: 16 })
  const marker = markerManager.value.getMarker(`benefit-${benefit.benefitId}`)
  if (marker) {
    marker.togglePopup()
  }
}
```

変更後:
```typescript
/** 特典カードクリック時に地図をパン＋ポップアップ表示（マーカー未存在時は追加） */
const handleShowBenefitOnMap = (benefit: BenefitDetailDto) => {
  if (!mapInstance.value || benefit.latitude == null || benefit.longitude == null) return
  mapInstance.value.flyTo({ center: [benefit.longitude, benefit.latitude], zoom: 16 })

  const markerId = `benefit-${benefit.benefitId}`
  let marker = markerManager.value.getMarker(markerId)
  if (!marker) {
    marker = createBenefitMarker(
      benefit.latitude,
      benefit.longitude,
      benefit.benefitName ?? '',
      benefit.benefitDetail ?? '',
      benefit.phoneNumber,
      benefit.benefitUrl,
      benefit.address
    )
    markerManager.value.addMarker(markerId, marker, mapInstance.value)
  }
  marker.togglePopup()
}
```

- [ ] **Step 6: lint を実行してエラーがないことを確認する**

```bash
cd C:/Users/ta9ns/kumamoto-henno-map/apps/front && npm run lint
```

期待出力: エラーなし

- [ ] **Step 7: コミットする**

```bash
cd C:/Users/ta9ns/kumamoto-henno-map
git add apps/front/src/pages/HomePage.vue
git commit -m "feat: HomePage でユーザー特典の地図連動イベントを処理"
```

---

## 動作確認チェックリスト

実装完了後、開発サーバーで以下を手動確認する。

```bash
cd C:/Users/ta9ns/kumamoto-henno-map/apps/front && npm run serve
```

- [ ] ログイン後「利用できる特典」タブに切り替える
- [ ] 店舗特典カード（例: 割引店舗）をクリックすると地図がその座標にフライして移動する
- [ ] 店舗ポップアップ（特典名・内容・電話番号・住所・URLを含む）が表示される
- [ ] 特典マーカートグルがオフの状態でクリックしてもマーカーが追加されポップアップが表示される
- [ ] 公共交通機関等の非店舗特典カードをクリックしても地図は動かない（何も起きない）
- [ ] 「特典を探す」タブの既存のカードクリック地図連動が引き続き動作する
- [ ] 特典マーカートグルをオフにすると、クリックで追加されたマーカーも削除される
