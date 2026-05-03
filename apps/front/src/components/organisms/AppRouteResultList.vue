<template>
  <div v-if="routes.length > 0" class="p-2">
    <template v-for="(route, index) in routes" :key="index">
       <div
         class="route-card"
         role="button"
         tabindex="0"
         @click="handleCardClick(index)"
         @keydown.enter.space="handleCardClick(index)"
       >
        <AppCard class="mb-3" :hoverable="true">
          <div class="route-summary">
            <div class="route-summary__time">
              <!-- ルート番号 -->
              <span
                class="route-number"
                :class="index === props.activeRouteIndex ? 'route-number--active' : 'route-number--inactive'"
              >
                {{ index + 1 }}
              </span>
              {{ formatJapaneseTime(route.startTime) }}～{{ formatJapaneseTime(route.endTime) }} ({{ route.duration }}分)
            </div>
            <div class="route-summary__cost">
              {{ route.totalFare }}円<span v-if="route.totalDiscountFare != null" class="route-summary__discount"> → {{ route.totalDiscountFare }}円</span> / 乗り換え：{{ route.transfers }}回
            </div>
          </div>

          <!-- 展開トグル -->
          <div class="leg-toggle">
            <span class="leg-toggle__label">{{ expandedRouteIndex === index ? '▲ 閉じる' : '▼ 詳細' }}</span>
          </div>

          <!-- 経路詳細（展開時） -->
          <div v-if="expandedRouteIndex === index" class="leg-detail" @click.stop>
            <!-- 出発地 -->
            <div v-if="route.legs.length > 0" class="leg-stop">
              <div class="leg-stop__dot leg-stop__dot--start"></div>
              <div class="leg-stop__info">
                <span class="leg-stop__name">{{ route.legs[0].from }}</span>
                <span class="leg-stop__time">{{ formatJapaneseTime(route.legs[0].startTime) }}</span>
              </div>
            </div>

            <!-- 各区間 -->
            <template v-for="(leg, legIndex) in route.legs" :key="legIndex">
              <div class="leg-segment">
                <div class="leg-segment__line"></div>
                <div class="leg-segment__badge">
                  <!-- 徒歩：アイコン + 所要時間(出発時間 ~ 到着時間) -->
                  <div v-if="isWalkingMode(leg.mode)" class="leg-segment__info-row">
                    <span class="leg-segment__icon">{{ getModeIcon(leg.mode) }}</span>
                    <span v-if="leg.duration" class="leg-segment__duration">{{ leg.duration }}分</span>
                    <span v-if="leg.startTime && leg.endTime" class="leg-segment__time-range">
                      ({{ formatJapaneseTime(leg.startTime) }} ~ {{ formatJapaneseTime(leg.endTime) }})
                    </span>
                  </div>

                  <!-- 公共交通機関 -->
                  <template v-else>
                    <!-- 1段目：アイコン + 事業者名 -->
                    <div class="leg-segment__info-row">
                      <span class="leg-segment__icon">{{ getModeIcon(leg.mode) }}</span>
                      <span v-if="leg.agencyName" class="leg-segment__agency">{{ leg.agencyName }}</span>
                    </div>
                    <!-- 2段目：所要時間(出発時間 ~ 到着時間) -->
                    <div
                      v-if="leg.duration && leg.startTime && leg.endTime"
                      class="leg-segment__info-row leg-segment__info-row--indented"
                    >
                      <span class="leg-segment__duration">{{ leg.duration }}分</span>
                      <span class="leg-segment__time-range">
                        ({{ formatJapaneseTime(leg.startTime) }} ~ {{ formatJapaneseTime(leg.endTime) }})
                      </span>
                      <span v-if="leg.isRealtime" class="leg-segment__realtime-badge">リアルタイム</span>
                      <span v-if="leg.isRealtime && leg.arrivalDelay && leg.arrivalDelay > 60" class="leg-segment__delay-badge">
                        {{ Math.round((leg.arrivalDelay ?? 0) / 60) }}分遅れ
                      </span>
                    </div>
                  </template>
                  <!-- 3段目：運賃 + 割引運賃 -->
                  <div 
                    v-if="leg.fare && leg.fare > 0" 
                    class="leg-segment__info-row leg-segment__info-row--indented"
                  >
                    <span class="leg-segment__fare">運賃：{{ leg.fare }}円</span>
                    <span v-if="leg.discountFare" class="leg-segment__discount">→ {{ leg.discountFare }}円</span>
                  </div>
                </div>
              </div>

              <!-- 目的地 -->
              <div class="leg-stop">
                <div class="leg-stop__dot" :class="legIndex === route.legs.length - 1 ? 'leg-stop__dot--end' : 'leg-stop__dot--mid'"></div>
                <div class="leg-stop__info">
                  <span class="leg-stop__name">{{ leg.to }}</span>
                  <span class="leg-stop__time">{{ formatJapaneseTime(leg.endTime) }}</span>
                </div>
              </div>
            </template>
          </div>
        </AppCard>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AppCard from '../atoms/AppCard.vue'
import { codeConstant } from '@/utils/codeConstant'
import { RouteDto } from '@/dto/routeDto'

const props = withDefaults(defineProps<{
  /** 経路検索結果 */
  routes?: RouteDto[];
  /** マップでアクティブな経路インデックス */
  activeRouteIndex?: number;
}>(), {
  routes: () => [],
  activeRouteIndex: 0,
});

const emit = defineEmits<{
  /** 経路カード選択 */
  (e: 'select-route', index: number): void;
}>();

/** 展開中の経路インデックス */
const expandedRouteIndex = ref<number | null>(null)

/** 経路カードクリック時の処理 */
const handleCardClick = (index: number) => {
  emit('select-route', index)
  expandedRouteIndex.value = expandedRouteIndex.value === index ? null : index
}

/** 交通手段アイコン変換 */
const getModeIcon = (mode: string | null | undefined): string => {
  return codeConstant.MODE_ICON[mode ?? ''] ?? ''
}

/** 徒歩モードかどうかの判定 */
const isWalkingMode = (mode: string | null | undefined): boolean => {
  return mode === 'WALK'
}

/** "HH:mm" 形式を "HH時mm分" 形式に変換 */
const formatJapaneseTime = (time: string | null | undefined): string => {
  if (!time) return ''
  const [hour, minute] = time.split(':')
  return `${hour}時${minute}分`
}
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

// CSS変数定義
.route-card {
  cursor: pointer;
  border-radius: 8px;
}

// 経路概要
.route-summary {
  &__time {
    display: flex;
    align-items: center;
    margin-bottom: 4px;
    font-size: 14px;
  }

  &__cost {
    color: base.$text-primary;
    font-size: 14px;
    margin-bottom: 4px;
  }

  &__discount {
    color: base.$end-marker-color;
    font-weight: 600;
    margin-left: 6px;
  }
}

.route-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  color: #fff;
  font-size: 12px;
  font-weight: bold;
  margin-right: 8px;
  flex-shrink: 0;

  &--active {
    background-color: base.$route-active-color;
  }

  &--inactive {
    background-color: base.$route-inactive-color;
  }
}

// 詳細/閉じるトグル
.leg-toggle {
  margin-top: 8px;
  text-align: right;

  &__label {
    font-size: 12px;
    color: base.$text-secondary;
    cursor: pointer;
    user-select: none;
    
    &:hover {
      color: base.$text-primary;
    }
  }
}

// 経路詳細
.leg-detail {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid base.$base-400;
}

// 停車駅
.leg-stop {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 2px;

  &__dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background-color: base.$text-disabled;
    flex-shrink: 0;
    margin-left: 10px;

    &--start {
      background-color: base.$start-marker-color;
    }

    &--mid {
      background-color: base.$text-disabled;
    }

    &--end {
      background-color: base.$end-marker-color;
    }
  }

  &__info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex: 1;
    min-height: 20px;
  }

  &__name {
    font-size: 13px;
    font-weight: 500;
    color: base.$text-primary;
  }

  &__time {
    font-size: 12px;
    color: base.$text-secondary;
    white-space: nowrap;
  }
}

// 経路区間
.leg-segment {
  display: flex;
  align-items: stretch;
  gap: 12px;
  min-height: 36px;
  margin-bottom: 2px;

  &__line {
    width: 2px;
    background-color: base.$base-400;
    margin-left: 14px;
    flex-shrink: 0;
  }

  &__badge {
    display: flex;
    flex-direction: column;
    gap: 6px;
    font-size: 12px;
    color: base.$text-primary;
    background-color: base.$base-200;
    border-radius: 6px;
    padding: 8px 10px;
    flex: 1;
    margin: 3px 0;
  }

  &__info-row {
    display: flex;
    align-items: center;
    gap: 6px;
    width: 100%;

    &--indented {
      margin-left: 20px;
    }
  }

  &__icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 16px;
    height: 16px;
    font-size: 14px;
  }

  &__agency {
    color: base.$text-secondary;
    font-size: 11px;
    font-weight: 500;
  }

  &__duration {
    color: base.$text-secondary;
    font-weight: 600;
  }

  &__time-range {
    color: base.$text-secondary;
    font-size: 11px;
  }

  &__fare {
    font-weight: 600;
    color: base.$text-primary;
  }

  &__discount {
    color: base.$end-marker-color;
    font-weight: 600;
  }

  &__realtime-badge {
    display: inline-block;
    padding: 1px 6px;
    font-size: 10px;
    color: base.$base-100;
    background-color: base.$chose-100;
    border-radius: 3px;
    white-space: nowrap;
  }

  &__delay-badge {
    display: inline-block;
    padding: 1px 6px;
    font-size: 10px;
    color: base.$base-100;
    background-color: base.$end-marker-color;
    border-radius: 3px;
    white-space: nowrap;
  }
}
</style>
