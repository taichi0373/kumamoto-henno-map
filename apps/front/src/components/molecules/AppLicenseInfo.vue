<template>
  <div class="license-container">
    <div class="license-content">
      <p class="source-info">
        出典：
        <AppLink
          :to="'https://www.pref.kumamoto.jp/soshiki/54/51729.html?type=top'"
          text="「運転免許証自主返納者への特典」のご紹介"
          variant="secondary"
          size="small"
          target="_blank"
        />
      </p>
      <div class="yahoo-attribution">
        <!-- Begin Yahoo! JAPAN Web Services Attribution Snippet -->
        <span class="attribution-text">
          <AppLink
            :to="'https://developer.yahoo.co.jp/sitemap/'"
            text="Webサービス by Yahoo! JAPAN"
            variant="secondary"
            size="small"
            target="_blank"
          />
        </span>
        <!-- End Yahoo! JAPAN Web Services Attribution Snippet -->
      </div>

      <!-- 出典・ライセンス情報のボタン -->
      <div class="license-button-section">
        <AppButton
          type="button"
          label="その他ライセンス情報"
          :primary="false"
          size="small"
          icon="pi pi-info-circle"
          @click="showLicensePopup = true"
        />
      </div>
    </div>

    <!-- <AppModal
      :visible="showLicensePopup"
      title="その他ライセンス情報"
      @close="showLicensePopup = false"
    >
      <div class="modal-content">
        <p class="intro-text">
          本システムは、以下のオープンソースソフトウェア（OSS）を使用しています。
        </p>
        
        <div class="license-section">
          <h4 class="section-title">オープンソースソフトウェア</h4>
          <ul class="license-list">
            <li class="license-item">
              <AppLink
                :to="'https://www.opentripplanner.org'"
                text="OpenTripPlanner"
                variant="primary"
                target="_blank"
              />
              <div class="license-details">
                ライセンス：
                <AppLink
                  :to="'https://www.apache.org/licenses/LICENSE-2.0'"
                  text="Apache License 2.0"
                  variant="secondary"
                  size="small"
                  target="_blank"
                />
                ・
                <AppLink
                  :to="'https://www.gnu.org/licenses/lgpl-3.0.html'"
                  text="GNU LGPL v3"
                  variant="secondary"
                  size="small"
                  target="_blank"
                />
              </div>
            </li>
          </ul>
        </div>

        <div class="license-section">
          <h4 class="section-title">オープンデータ</h4>
          <p class="section-description">本システムは、以下のオープンデータを使用しています。</p>
          
          <ol class="data-list">
            <li class="data-item">
              <AppLink
                :to="'https://km.bus-vision.jp/kumamoto/view/opendataKuma.html'"
                text="バスきたくまさん"
                variant="primary"
                target="_blank"
              />
              <div class="license-details">
                ライセンス：
                <AppLink
                  :to="'https://creativecommons.org/licenses/by/4.0/'"
                  text="CC BY 4.0"
                  variant="secondary"
                  size="small"
                  target="_blank"
                />
              </div>
              <ul class="sub-list">
                <li>産交バス：https://www.kyusanko.co.jp/sankobus</li>
                <li>熊本電鉄バス：https://www.kumamotodentetsu.co.jp/bus</li>
                <li>熊本バス：https://www.kuma-bus.co.jp</li>
                <li>熊本都市バス：https://www.kumamoto-toshibus.co.jp</li>
              </ul>
            </li>
            
            <li class="data-item">
              <AppLink
                :to="'https://gtfs-data.jp/'"
                text="GTFSデータ公開リポジトリ"
                variant="primary"
                target="_blank"
              />
              <div class="license-details">
                ライセンス：
                <AppLink
                  :to="'https://creativecommons.org/licenses/by/2.1/jp/'"
                  text="CC BY 2.1 JP"
                  variant="secondary"
                  size="small"
                  target="_blank"
                />
              </div>
              <ul class="sub-list">
                <li>熊本市交通局：http://www.kotsu-kumamoto.jp</li>
              </ul>
              <div class="license-details">
                ライセンス：
                <AppLink
                  :to="'https://creativecommons.org/publicdomain/zero/1.0/deed.ja'"
                  text="CC0 1.0"
                  variant="secondary"
                  size="small"
                  target="_blank"
                />
              </div>
              <ul class="sub-list">
                <li>熊本電鉄：https://www.kumamotodentetsu.co.jp</li>
              </ul>
            </li>
            
            <li class="data-item">
              <AppLink
                :to="'https://gtfs-gis.jp/gtfs4research/'"
                text="研究用GTFSデータ公開ページ"
                variant="primary"
                target="_blank"
              />
              <div class="research-note">
                JR九州および南阿蘇鉄道のデータは、研究者によって公開された非公式のGTFSデータを利用しています。
                調査・研究目的に限る利用条件に基づき使用しています。
              </div>
            </li>
          </ol>
        </div>
      </div>
    </AppModal> -->
  </div>
</template>

<script>
import { ref } from 'vue'
import AppButton from '../atoms/AppButton.vue'
import AppLink from '../atoms/AppLink.vue'

export default {
  name: 'AppLicenseInfo',
  components: {
    AppButton,
    AppLink,
  },
  setup() {
    const showLicensePopup = ref(false)

    return {
      showLicensePopup
    }
  }
}
</script>

<style scoped>
.license-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 15px;
  margin: 0px;
  border: 1px solid #e2e8f0;
}

.license-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.source-info {
  font-size: 14px;
  color: #4a5568;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.note-text {
  font-size: 12px;
  color: #718096;
  margin: 0;
  font-style: italic;
}

.attribution-text {
  font-size: 12px;
  color: #4a5568;
}

.license-button-section {
  display: flex;
  justify-content: center;
  padding-top: 10px;
  border-top: 1px solid #e2e8f0;
}

/* モーダルコンテンツのスタイル */
.modal-content {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 8px;
}

.intro-text {
  color: #4a5568;
  margin-bottom: 20px;
  line-height: 1.6;
}

.license-section {
  margin-bottom: 25px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 12px;
  padding-bottom: 6px;
  border-bottom: 2px solid #e2e8f0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.section-title::before {
  content: "📋";
  font-size: 14px;
}

.section-description {
  color: #4a5568;
  margin-bottom: 15px;
  font-size: 14px;
  line-height: 1.5;
}

.license-list,
.data-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.data-list {
  counter-reset: data-counter;
}

.license-item,
.data-item {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  border-left: 4px solid #4299e1;
}

.data-item {
  counter-increment: data-counter;
  position: relative;
}

.license-details {
  margin: 8px 0;
  font-size: 13px;
  color: #4a5568;
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.sub-list {
  margin: 10px 0;
  padding-left: 20px;
  list-style-type: disc;
}

.sub-list li {
  color: #718096;
  font-size: 12px;
  margin-bottom: 4px;
  line-height: 1.4;
}

.research-note {
  margin-top: 10px;
  padding: 10px;
  background: #fff5f5;
  border: 1px solid #fed7d7;
  border-radius: 6px;
  font-size: 12px;
  color: #c53030;
  line-height: 1.5;
}

/* スクロールバーのスタイル */
.modal-content::-webkit-scrollbar {
  width: 6px;
}

.modal-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.modal-content::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

.modal-content::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

/* レスポンシブデザイン */
@media (max-width: 768px) {
  .license-container {
    padding: 15px;
    margin: 15px 0;
  }
  
  .source-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
  
  .license-details {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .data-item {
    padding: 15px 20px 15px 15px;
  }
  
  .modal-content {
    max-height: 60vh;
  }
}

/* アニメーション */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.license-container {
  animation: fadeIn 0.3s ease-out;
}
</style>
