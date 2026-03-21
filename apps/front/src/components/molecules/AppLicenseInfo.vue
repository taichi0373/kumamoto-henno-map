<template>
  <div class="license-container">
    <div class="license-content">
      <p class="source-info">
        出典：
        <AppLink @click="openUrl('https://www.pref.kumamoto.jp/soshiki/54/51729.html?type=top')">
          「運転免許証自主返納者への特典」のご紹介
        </AppLink>
      </p>
      <div class="yahoo-attribution">
        <!-- Begin Yahoo! JAPAN Web Services Attribution Snippet -->
        <span class="attribution-text">
          <AppLink @click="openUrl('https://developer.yahoo.co.jp/sitemap/')">
            Webサービス by Yahoo! JAPAN
          </AppLink>
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

    <!-- ダイアログ -->
    <AppDialog
      v-model="showLicensePopup"
      header="その他ライセンス情報"
      :dialog-style="{ width: '600px' }"
    >
      <div class="modal-content">
        <p class="intro-text">
          本システムは、以下のオープンソースソフトウェア（OSS）を使用しています。
        </p>

        <div class="license-section">
          <h4 class="section-title section-title--privacy">位置情報の取得について</h4>
          <p class="section-description">
            本アプリは、経路探索および現在地表示の目的のみで、お使いのデバイスの位置情報を取得します。
            取得した位置情報はルート計算にのみ使用し、サーバーへの保存や第三者への提供は行いません。
            位置情報の取得はブラウザの許可設定からいつでも無効にできます。
          </p>
        </div>

        <div class="license-section">
          <h4 class="section-title">オープンソースソフトウェア</h4>
          <ul class="license-list">
            <li class="license-item">
              <AppLink @click="openUrl('https://www.opentripplanner.org')">
                OpenTripPlanner
              </AppLink>
              <div class="license-details">
                ライセンス：
                <AppLink @click="openUrl('https://www.apache.org/licenses/LICENSE-2.0')">
                  Apache License 2.0
                </AppLink>
                ・
                <AppLink @click="openUrl('https://www.gnu.org/licenses/lgpl-3.0.html')">
                  GNU LGPL v3
                </AppLink>
              </div>
            </li>
          </ul>
        </div>

        <div class="license-section">
          <h4 class="section-title">オープンデータ</h4>
          <p class="section-description">本システムは、以下のオープンデータを使用しています。</p>

          <ol class="data-list">
            <li class="data-item">
              <AppLink @click="openUrl('https://km.bus-vision.jp/kumamoto/view/opendataKuma.html')">
                バスきたくまさん
              </AppLink>
              <div class="license-details">
                ライセンス：
                <AppLink @click="openUrl('https://creativecommons.org/licenses/by/4.0/')">
                  CC BY 4.0
                </AppLink>
              </div>
              <ul class="sub-list">
                <li>産交バス：https://www.kyusanko.co.jp/sankobus</li>
                <li>熊本電鉄バス：https://www.kumamotodentetsu.co.jp/bus</li>
                <li>熊本バス：https://www.kuma-bus.co.jp</li>
                <li>熊本都市バス：https://www.kumamoto-toshibus.co.jp</li>
              </ul>
            </li>

            <li class="data-item">
              <AppLink @click="openUrl('https://gtfs-data.jp/')">
                GTFSデータ公開リポジトリ
              </AppLink>
              <div class="license-details">
                ライセンス：
                <AppLink @click="openUrl('https://creativecommons.org/licenses/by/2.1/jp/')">
                  CC BY 2.1 JP
                </AppLink>
              </div>
              <ul class="sub-list">
                <li>熊本市交通局：http://www.kotsu-kumamoto.jp</li>
              </ul>
              <div class="license-details">
                ライセンス：
                <AppLink @click="openUrl('https://creativecommons.org/publicdomain/zero/1.0/deed.ja')">
                  CC0 1.0
                </AppLink>
              </div>
              <ul class="sub-list">
                <li>熊本電鉄：https://www.kumamotodentetsu.co.jp</li>
              </ul>
            </li>

          </ol>
        </div>
      </div>
    </AppDialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import AppButton from '@/components/atoms/AppButton.vue';
import AppLink from '@/components/atoms/AppLink.vue';
import AppDialog from '@/components/atoms/AppDialog.vue';

const showLicensePopup = ref(false);

/**
 * URLを新しいタブで開く
 */
const openUrl = (url: string) => {
  window.open(url, '_blank', 'noopener,noreferrer');
};
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.license-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 15px;
  margin: 0px;
  border: 1px solid #e2e8f0;
  animation: fadeIn 0.3s ease-out;
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

  &::before {
    content: "📋";
    font-size: 14px;
  }

  &--privacy::before {
    content: "📍";
  }
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

  li {
    color: #718096;
    font-size: 12px;
    margin-bottom: 4px;
    line-height: 1.4;
  }
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

.modal-content {
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: #cbd5e0;
    border-radius: 3px;

    &:hover {
      background: #a0aec0;
    }
  }
}

</style>
