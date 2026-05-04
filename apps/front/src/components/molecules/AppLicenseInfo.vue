<template>
  <div class="license-container">
    <div class="license-content">
      <!-- 出典・ライセンス情報のボタン -->
      <div class="license-button-section">
        <AppButton
          type="button"
          label="出典・ライセンス情報"
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
      header="出典・ライセンス情報"
      :dialog-style="{ width: '600px' }"
    >
      <div class="modal-content">
        <p class="intro-text">
          本システムは、以下のオープンソースソフトウェア（OSS）を使用しています。
        </p>

        <div class="license-section">
          <h4 class="section-title">
            <i class="pi pi-map-marker" />位置情報の取得について
          </h4>
          <p class="section-description">
            本アプリは、経路探索および現在地表示の目的のみで、お使いのデバイスの位置情報を取得します。
            取得した位置情報はルート計算にのみ使用し、サーバーへの保存や第三者への提供は行いません。
            位置情報の取得はブラウザの許可設定からいつでも無効にできます。
          </p>
        </div>

        <div class="license-section">
          <h4 class="section-title">
            <i class="pi pi-book" />自主返納特典の出典
          </h4>
          <p class="section-description">
            出典：
            <AppLink @click="openUrl('https://www.pref.kumamoto.jp/soshiki/54/51729.html?type=top')">
              「運転免許証自主返納者への特典」のご紹介
            </AppLink>
          </p>
        </div>

        <div class="license-section">
          <h4 class="section-title">
            <i class="pi pi-cog" />オープンソースソフトウェア
          </h4>
          <ul class="license-list">
            <li class="license-item">
              <AppLink @click="openUrl('https://www.opentripplanner.org')">
                OpenTripPlanner 2.5.0
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
          <h4 class="section-title">
            <i class="pi pi-database" />オープンデータ
          </h4>
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
  background: base.$base-100;
  padding: 8px;
  border-top: 1px solid base.$base-400;
}

.license-content {
  display: flex;
  flex-direction: column;
  gap: base.$margin-100;
}

.license-button-section {
  display: flex;
  justify-content: center;
}

.modal-content {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: base.$margin-100;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: base.$base-300;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: base.$base-400;
    border-radius: 3px;

    &:hover {
      background: base.$base-500;
    }
  }
}

.intro-text {
  color: base.$text-primary;
  margin-bottom: base.$margin-200;
  line-height: 1.6;
}

.license-section {
  margin-bottom: base.$margin-300;
}

.section-title {
  font-size: base.$fontsize-large;
  font-weight: 600;
  color: base.$text-primary;
  margin-bottom: base.$margin-100 + 4;
  padding-bottom: 6px;
  border-bottom: 2px solid base.$base-400;
  display: flex;
  align-items: center;
  gap: 6px;

  .pi {
    font-size: base.$fontsize-medium;
    color: base.$text-primary;
  }
}

.section-description {
  color: base.$text-primary;
  margin-bottom: base.$margin-200 - 1;
  font-size: base.$fontsize-medium;
  line-height: 1.5;
}

.license-list,
.data-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.license-item,
.data-item {
  margin-bottom: base.$margin-200;
  padding: base.$margin-200 - 1;
  background: base.$base-200;
  border: 1px solid base.$base-400;
  border-radius: 8px;
  border-left: 4px solid base.$chose-100;
}

.license-details {
  margin: base.$margin-100 0;
  font-size: base.$fontsize-medium;
  color: base.$text-primary;
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.sub-list {
  margin: base.$margin-100 0;
  padding-left: base.$margin-200;
  list-style-type: disc;

  li {
    color: base.$text-primary;
    font-size: base.$fontsize-medium;
    margin-bottom: 4px;
    line-height: 1.4;
  }
}
</style>
