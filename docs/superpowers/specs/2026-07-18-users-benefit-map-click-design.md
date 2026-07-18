# 設計書：利用できる特典カードクリック時の地図連動

**作成日:** 2026-07-18
**対象ブランチ:** develop から作成

---

## 概要

「利用できる特典」タブに表示される特典カードのうち、店舗特典（categoryCd = 'BS001'）をクリックした際に、地図をその店舗の座標に移動させ、ポップアップを表示する機能を追加する。

---

## 背景・課題

### 現状

- バックエンドの `/benefit/users/{userId}` は `BenefitDetailEntity`（緯度・経度を含む）を返している
- フロントエンドの `AppUsersBenefit.vue` は `BenefitDto[]` を受け取っており、`BenefitDto` に緯度・経度フィールドが存在しないため、座標データが捨てられている
- 「特典を探す」タブ（`AppSearchBenefit.vue`）には同様の地図連動機能が既に実装されている

### 解決方針

`AppUsersBenefit.vue` が受け取る props の型を `BenefitDetailDto[]` に変更し、既存の地図連動パターンを流用する（Approach A）。

---

## 変更ファイル一覧

| ファイル | 変更内容 |
|----------|----------|
| `apps/front/src/utils/codeConstant.ts` | `CATEGORY_CD.SHOP = 'BS001'` を追加 |
| `apps/front/src/pages/HomePage.vue` | `usersBenefits` の型を `BenefitDetailDto[]` に変更、`AppUsersBenefit` に `@show-benefit-on-map` イベントハンドラを追加、`handleShowBenefitOnMap` にマーカー未存在時の追加ロジックを追加 |
| `apps/front/src/components/organisms/AppUsersBenefit.vue` | props型を `BenefitDetailDto[]` に変更、店舗特典カードにクリックハンドラを追加、`show-benefit-on-map` イベントをemit |

---

## 詳細設計

### 1. codeConstant.ts

`CATEGORY_CD` を追加する。

```typescript
CATEGORY_CD: {
  SHOP: 'BS001', // 店舗
} as const,
```

### 2. AppUsersBenefit.vue

#### Props の変更

```typescript
// 変更前
const props = withDefaults(defineProps<{
  usersBenefits?: BenefitDto[];
}>(), { usersBenefits: () => [] })

// 変更後
const props = withDefaults(defineProps<{
  usersBenefits?: BenefitDetailDto[];
}>(), { usersBenefits: () => [] })
```

#### Emits の追加

```typescript
const emit = defineEmits<{
  (e: 'show-benefit-on-map', benefit: BenefitDetailDto): void
}>()
```

#### 店舗判定ヘルパー

```typescript
/** 店舗特典かつ座標ありの場合にクリック可能とする */
const isShopBenefit = (benefit: BenefitDetailDto): boolean =>
  benefit.categoryCd === codeConstant.CATEGORY_CD.SHOP
  && benefit.latitude != null
  && benefit.longitude != null
```

#### テンプレートの変更

```html
<AppCard
  class="mb-3"
  :hoverable="true"
  @click="isShopBenefit(benefit) ? emit('show-benefit-on-map', benefit) : undefined"
>
```

### 3. HomePage.vue

#### 型の変更

```typescript
// 変更前
const usersBenefits = ref<BenefitDto[]>([])

// 変更後
const usersBenefits = ref<BenefitDetailDto[]>([])
```

#### API レスポンスのキャスト変更

```typescript
// 変更前
usersBenefits.value = ((response.data as unknown) as { data: BenefitDto[] }).data || []

// 変更後
usersBenefits.value = ((response.data as unknown) as { data: BenefitDetailDto[] }).data || []
```

#### テンプレートのイベントバインディング追加

```html
<AppUsersBenefit
  :users-benefits="usersBenefits"
  @show-benefit-on-map="handleShowBenefitOnMap"
/>
```

#### handleShowBenefitOnMap の拡張

マーカーが存在しない場合にも作成してポップアップを表示するよう拡張する。
この変更は `AppSearchBenefit` 側でも同様に機能改善となる。

```typescript
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

---

## イベントフロー

```
AppUsersBenefit: 店舗カード（lat/lng あり）をクリック
  └─ emit('show-benefit-on-map', benefit: BenefitDetailDto)

HomePage: @show-benefit-on-map="handleShowBenefitOnMap" で受信
  └─ mapInstance.flyTo({ center: [longitude, latitude], zoom: 16 })
  └─ markerManager.getMarker('benefit-${id}') を確認
       ├─ 存在する → marker.togglePopup()
       └─ 存在しない → createBenefitMarker() → addMarker() → togglePopup()
```

---

## 考慮事項

- **店舗以外の特典（公共交通機関等）**: クリックしても何も起きない（座標なし）
- **座標が null の店舗特典**: `isShopBenefit` で false となるためクリック不可
- **マーカートグルとの共存**: クリックで追加されたマーカーは既存のマーカー管理に組み込まれ、トグルオフ（`removeMarkersByType('benefit-')`）で削除される
- **重複マーカー防止**: `markerManager.getMarker(markerId)` で事前確認するため重複追加なし
- **既存テストへの影響**: バックエンドのレスポンス型変更なし。フロントエンドの型変更のみのため、lintエラーがないことを `npm run lint` で確認する

---

## テスト観点

- [ ] 店舗特典カードをクリックすると地図が該当店舗に移動する
- [ ] ポップアップが表示される（マーカートグルがオフの場合も含む）
- [ ] 公共交通機関等の非店舗特典カードをクリックしても何も起きない
- [ ] 既存の「特典を探す」の地図連動が引き続き動作する
- [ ] `npm run lint` でエラーなし
