<template>
  <div class="page">
    <div class="container">
      <AppTitle size="large">自主返納支援制度とは</AppTitle>

      <div class="content-sections">
        <!-- 制度について -->
        <AppCard>
          <template #title>
            <h2 class="section-title">運転免許証の自主返納制度について</h2>
          </template>
          <p class="section-description">
            運転免許証の自主返納制度は、加齢に伴う身体機能の変化等により、
            自動車等の運転に不安を感じる方が、運転免許証を自主的に返納できる制度です。
          </p>
        </AppCard>

        <!-- メリット -->
        <AppCard>
          <template #title>
            <h2 class="section-title">自主返納のメリット</h2>
          </template>
          <div class="merit-list">
            <div class="merit-item">
              <i class="pi pi-shield"></i>
              <span>交通事故のリスクを減らすことができます</span>
            </div>
            <div class="merit-item">
              <i class="pi pi-gift"></i>
              <span>様々な特典やサービスを利用できます</span>
            </div>
            <div class="merit-item">
              <i class="pi pi-truck"></i>
              <span>公共交通機関の利用促進に繋がります</span>
            </div>
            <div class="merit-item">
              <i class="pi pi-heart"></i>
              <span>地域社会の安全向上に貢献できます</span>
            </div>
          </div>
        </AppCard>

        <!-- 返納手続き -->
        <AppCard>
          <template #title>
            <h2 class="section-title">返納手続きについて</h2>
          </template>
          <div class="procedure-summary">
            <div class="summary-item">
              <h3 class="summary-title">
                <i class="pi pi-map-marker"></i>受付場所
              </h3>
              <ul class="summary-list">
                <li>運転免許センター（月〜木・日曜日）</li>
                <li>警察署（月〜金曜日）</li>
                <li>氷川幹部交番（月〜金曜日）</li>
              </ul>
              <p class="summary-note">※ 受付時間や休業日は場所により異なります</p>
            </div>

            <div class="summary-item">
              <h3 class="summary-title">
                <i class="pi pi-file"></i>必要書類（本人申請）
              </h3>
              <ul class="summary-list">
                <li>有効な運転免許証またはマイナ免許証</li>
              </ul>
              <p class="summary-note">※ 代理申請の場合は委任状等が別途必要です</p>
            </div>

            <div class="summary-item">
              <h3 class="summary-title">
                <i class="pi pi-id-card"></i>運転経歴証明書
              </h3>
              <ul class="summary-list">
                <li>返納と同時に申請可能（手数料：1,150円）</li>
                <li>写真1枚が必要（運転免許センター申請時は不要）</li>
              </ul>
            </div>
          </div>

          <div class="procedure-link-wrap">
            <AppLink to="https://www.pref.kumamoto.jp/site/police/8708.html">
              <i class="pi pi-external-link"></i>
              詳細は熊本県警察公式サイトをご確認ください
            </AppLink>
          </div>
        </AppCard>

        <!-- 特典について -->
        <AppCard>
          <template #title>
            <h2 class="section-title">特典について</h2>
          </template>
          <p class="section-description">
            熊本県内では、運転免許証を自主返納された方を対象に、
            公共交通機関や地域の店舗・施設、各市町村から多様な支援が提供されています。
            詳細・申込方法は各事業者・市町村へ直接ご確認ください。
          </p>

          <div class="benefit-categories">
            <AppCard :hoverable="true">
              <div class="category-content">
                <div class="category-icon">🚌</div>
                <h3 class="category-title">公共交通機関</h3>
                <p class="category-description">路線バス・路面電車・電車・タクシーなどの運賃割引（例：返納後2年間、運賃の半額など）</p>
              </div>
            </AppCard>

            <AppCard :hoverable="true">
              <div class="category-content">
                <div class="category-icon">🪙</div>
                <h3 class="category-title">ICカード・商品券</h3>
                <p class="category-description">市町村から交通系ICカードや商品券が支給される場合があります</p>
              </div>
            </AppCard>

            <AppCard :hoverable="true">
              <div class="category-content">
                <div class="category-icon">🚲</div>
                <h3 class="category-title">移動手段の取得支援</h3>
                <p class="category-description">電動アシスト自転車購入費の一部助成など、車に代わる移動手段の取得を支援</p>
              </div>
            </AppCard>

            <AppCard :hoverable="true">
              <div class="category-content">
                <div class="category-icon">👓</div>
                <h3 class="category-title">眼鏡・補聴器</h3>
                <p class="category-description">眼鏡・補聴器専門店での購入割引や無料メンテナンス</p>
              </div>
            </AppCard>

            <AppCard :hoverable="true">
              <div class="category-content">
                <div class="category-icon">♨️</div>
                <h3 class="category-title">温泉・宿泊</h3>
                <p class="category-description">県内温泉施設の入浴料割引（例：半額）など</p>
              </div>
            </AppCard>
          </div>

          <AppAlert
            message="※ 特典の内容・対象者・期間は事業者・市町村によって異なります。最新情報は各窓口でご確認ください。"
            variant="info"
            :closable="false"
          />
        </AppCard>

        <!-- お問い合わせ -->
        <AppCard>
          <template #title>
            <h2 class="section-title">お問い合わせ</h2>
          </template>
          <AppLink to="https://www.pref.kumamoto.jp/soshiki/54/51729.html?type=top">
            <i class="pi pi-external-link"></i>
            熊本県公式サイト（運転免許証自主返納者への特典）
          </AppLink>
        </AppCard>

        <!-- 戻るボタン -->
        <div class="back-section">
          <AppButton
            type="button"
            label="マップに戻る"
            severity="primary"
            size="large"
            icon="pi pi-map"
            @click="$router.push('/')"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppButton from '../components/atoms/AppButton.vue'
import AppTitle from '../components/atoms/AppTitle.vue'
import AppCard from '../components/atoms/AppCard.vue'
import AppLink from '../components/atoms/AppLink.vue'
import AppAlert from '../components/atoms/AppAlert.vue'
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.page {
  height: calc(100vh - 60px);
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  overflow-y: auto;
  padding: 0;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100%;
  box-sizing: border-box;
}

.app-title {
  justify-content: center;
  margin: 30px 0;
  padding-bottom: 10px;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 80px;
    height: 4px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
    border-radius: 2px;
  }
}

.content-sections {
  display: flex;
  flex-direction: column;
  gap: 25px;

  > * {
    animation: fadeInUp 0.6s ease-out;
  }
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 20px;
  border-left: 4px solid base.$chose-100;
  padding-left: 15px;
  text-align: left;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title::before {
  content: "📋";
  font-size: 18px;
}

.section-description {
  color: #4a5568;
  line-height: 1.7;
  margin-bottom: 0;
  font-size: 15px;
}

/* メリットリスト */
.merit-list {
  display: grid;
  gap: 15px;
}

.merit-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.merit-item:hover {
  background: #edf2f7;
  transform: translateX(5px);
}

.merit-item i {
  font-size: 18px;
  width: 20px;
}

.merit-item span {
  color: #4a5568;
  font-weight: 500;
}

/* 返納手続き概要 */
.procedure-summary {
  display: grid;
  gap: 15px;
  margin-top: 10px;
}

.summary-item {
  padding: 15px 20px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.summary-title {
  font-size: 15px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  gap: 8px;

}

.summary-list {
  margin: 0 0 6px 0;
  padding-left: 20px;
  color: #4a5568;
  font-size: 14px;
  line-height: 1.8;
}

.summary-note {
  margin: 6px 0 0 0;
  color: #718096;
  font-size: 12px;
}

.procedure-link-wrap {
  margin-top: 15px;
  text-align: right;
}

/* 特典カテゴリー */
.benefit-categories {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 20px;
  margin-top: 25px;
  margin-bottom: 20px;
}

.category-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;

  .category-icon {
    font-size: 32px;
    margin-bottom: 12px;
  }

  .category-title {
    font-size: 16px;
    font-weight: 600;
    color: #2d3748;
    margin: 0 0 8px 0;
  }

  .category-description {
    color: #718096;
    margin: 0;
    font-size: 13px;
    line-height: 1.5;
  }
}

/* 戻るボタンセクション */
.back-section {
  text-align: center;
  margin-top: 40px;
  padding: 20px;
}

/* アニメーション */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* レスポンシブデザイン */
@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }

  .benefit-categories {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .page {
    padding: 10px 0;
  }

  .content-sections {
    gap: 15px;
  }

  .section-title {
    font-size: 18px;
  }
}
</style>
