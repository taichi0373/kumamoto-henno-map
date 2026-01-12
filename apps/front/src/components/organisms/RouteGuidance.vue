<template>
  <div class="form-section">
    <form @submit.prevent="searchRoute">
      <BaseFormGroup size="small">
        <BaseLabel for="transport-mode" text="交通手段" :required="true" />
        <BaseSelect
          id="transport-mode"
          v-model="transportMode"
          :options="transportOptions"
          :required="true"
        />
      </BaseFormGroup>

      <BaseFormGroup size="small">
        <BaseLabel for="start-location" text="出発地" :required="true" />
        <div class="autocomplete-container">
          <BaseInput
            id="start-location"
            v-model="startLocation"
            type="text"
            placeholder="出発地を入力してください"
            :required="true"
            @input="onStartLocationInput"
            @keydown="onStartLocationKeydown"
          />
          <BaseButton
            type="button"
            label="マップで選択"
            size="small"
            :primary="false"
            class="map-select-btn"
            @click="toggleMapSelection('start')"
            :disabled="isWaitingForMapClick"
          >
            <i class="fa-solid fa-map-location-dot"></i>
          </BaseButton>

          <!-- <ul class="suggestion-list" v-show="startSuggestions.length > 0"> -->
            <SuggestionList
              :suggestions="startSuggestions"
              :activeIndex="startSuggestionIndex"
              @select="selectStartLocation"
            />
        </div>
      </BaseFormGroup>

      <BaseFormGroup size="small">
        <BaseLabel for="end-location" text="目的地" :required="true" />
        <div class="autocomplete-container">
          <BaseInput
            id="end-location"
            v-model="endLocation"
            type="text"
            placeholder="目的地を入力してください"
            :required="true"
            @input="onEndLocationInput"
            @keydown="onEndLocationKeydown"
          />
          <BaseButton
            type="button"
            label="マップで選択"
            size="small"
            :primary="false"
            class="map-select-btn"
            @click="toggleMapSelection('end')"
            :disabled="isWaitingForMapClick"
          >
            <i class="fa-solid fa-map-location-dot"></i>
          </BaseButton>
          <!-- <ul class="suggestion-list" v-show="endSuggestions.length > 0"> -->
            <SuggestionList
              :suggestions="endSuggestions"
              :activeIndex="endSuggestionIndex"
              @select="selectEndLocation"
            />
        </div>
      </BaseFormGroup>

      <div class="expand-trigger" @click="toggleConditions">
        {{ showConditions ? '条件を閉じる' : '条件指定' }}
        <span :class="showConditions ? 'icon-expand-trigger rotated' : 'icon-expand-trigger'">
          {{ showConditions ? '▲' : '▼' }}
        </span>
      </div>
      <div v-show="showConditions">
        <BaseFormGroup size="small">
          <BaseLabel for="time-select" text="時刻指定" />
          <BaseSelect
            id="time-select"
            v-model="routeConditions.timeSelect"
            :options="timeSelectOptions"
          />
        </BaseFormGroup>

        <BaseFormGroup size="small">
          <BaseLabel for="date" text="日付" />
          <BaseInput
            id="date"
            type="date"
            v-model="routeConditions.selectedDate"
          />
        </BaseFormGroup>

        <BaseFormGroup size="small">
          <BaseLabel for="time" text="時刻" />
          <BaseInput
            id="time"
            type="time"
            v-model="routeConditions.selectedTime"
          />
        </BaseFormGroup>
      </div>
      
      <div class="form-actions">
        <BaseButton
          type="submit"
          label="経路を検索"
          :primary="true"
          :full-width="true"
          icon="fa-solid fa-search"
        />
      </div>
    </form>
  </div>

  <!-- 経路探索結果 -->
  <div v-if="routes.length > 0" class="route-results">
    <h4 class="section-title">検索結果</h4>
    
    <div class="route-list">
      <div
        v-for="(route, index) in routes" 
        :key="index"
        class="route-item"
      >
        <div class="route-summary">
          <span class="route-time">{{ route.duration }}</span>
          <span class="route-cost">{{ route.cost }}円</span>
        </div>
        <div class="route-details">
          {{ route.summary }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import BaseFormGroup from '../atoms/FormGroup.vue'
import BaseLabel from '../atoms/Label.vue'
import BaseInput from '../atoms/Input.vue'
import BaseSelect from '../atoms/Select.vue'
import BaseButton from '../atoms/Button.vue'

import SuggestionList from '../atoms/SuggestionList.vue'

export default {
  name: 'RouteGuidance',
  components: {
    BaseFormGroup,
    BaseLabel,
    BaseInput,
    BaseSelect,
  BaseButton,
  SuggestionList
  },
  setup(props, { emit }) {
    // リアクティブデータ
    const showConditions = ref(false)
    const startSuggestions = ref([])
    const endSuggestions = ref([])
    const startSuggestionIndex = ref(-1)
    const endSuggestionIndex = ref(-1)
    const isWaitingForMapClick = ref(false)

    // 経路データ
    const transportMode = ref('TRANSIT, WALK')
    const startLocation = ref('')
    const endLocation = ref('')
    const routes = ref([])

    // 経路条件
    const routeConditions = reactive({
      timeSelect: 'departure',
      selectedDate: '',
      selectedTime: ''
    })

    // 座標情報
    const startCoordinates = ref(null)
    const endCoordinates = ref(null)

    // セレクトボックスのオプション
    const transportOptions = [
      { value: 'TRANSIT, WALK', text: '公共交通機関' },
      { value: 'RAIL, TRAM, WALK', text: '電車' },
      { value: 'BUS, WALK', text: 'バス' },
      { value: 'WALK', text: '徒歩' },
      { value: 'BICYCLE', text: '自転車' }
    ]

    const timeSelectOptions = [
      { value: 'departure', text: '出発' },
      { value: 'arrival', text: '到着' }
    ]

    // セッションストレージからデータを読み込み
    const loadFromSession = () => {
      const savedTransportMode = sessionStorage.getItem('routeTransportMode')
      const savedStartLocation = sessionStorage.getItem('routeStartLocation')
      const savedEndLocation = sessionStorage.getItem('routeEndLocation')
      const savedConditions = sessionStorage.getItem('routeConditions')
      const savedRoutes = sessionStorage.getItem('routeResults')

      if (savedTransportMode) {
        transportMode.value = savedTransportMode
      }
      if (savedStartLocation) {
        startLocation.value = savedStartLocation
      }
      if (savedEndLocation) {
        endLocation.value = savedEndLocation
      }
      if (savedConditions) {
        const conditions = JSON.parse(savedConditions)
        Object.assign(routeConditions, conditions)
      }
      if (savedRoutes) {
        routes.value = JSON.parse(savedRoutes)
      }
    }

    // セッションストレージにデータを保存
    const saveToSession = () => {
      sessionStorage.setItem('routeTransportMode', transportMode.value)
      sessionStorage.setItem('routeStartLocation', startLocation.value)
      sessionStorage.setItem('routeEndLocation', endLocation.value)
      sessionStorage.setItem('routeConditions', JSON.stringify(routeConditions))
      sessionStorage.setItem('routeResults', JSON.stringify(routes.value))
    }

    const toggleConditions = () => {
      showConditions.value = !showConditions.value
    }

    // デバウンス用タイマー
    let debounceTimer = null

    const debounceSearch = (callback, delay) => {
      if (debounceTimer) clearTimeout(debounceTimer)
      debounceTimer = setTimeout(callback, delay)
    }

    const onStartLocationInput = () => {
      saveToSession()
      debounceSearch(() => {
        searchLocationSuggestions(startLocation.value, 'start')
      }, 500)
    }

    const onEndLocationInput = () => {
      saveToSession()
      debounceSearch(() => {
        searchLocationSuggestions(endLocation.value, 'end')
      }, 500)
    }

    const searchLocationSuggestions = async (query, type) => {
      if (query.length < 2) {
        if (type === 'start') startSuggestions.value = []
        else endSuggestions.value = []
        return
      }

      try {
        // Nominatim APIを使用した地点検索（熊本県内を優先）
        const nominatimUrl = `https://nominatim.openstreetmap.org/search?format=json&addressdetails=1&limit=10&countrycodes=jp&q=${encodeURIComponent(query + ' 熊本県')}`
        
        const response = await fetch(nominatimUrl, {
          headers: {
            'User-Agent': 'navigation_app/1.0'
          }
        })
        
        const data = await response.json()
        console.log(`Nominatim API response for "${query}":`, data)
        
        // レスポンスを候補リスト形式に変換
        const suggestions = data.map((item, index) => ({
          id: index,
          name: item.name || extractMainName(item.display_name),
          formattedAddress: formatDisplayName(item.display_name),
          lat: parseFloat(item.lat),
          lon: parseFloat(item.lon),
          address: item.address || {},
          originalDisplayName: item.display_name
        }))
        
        if (type === 'start') {
          startSuggestions.value = suggestions
          startSuggestionIndex.value = -1
        } else {
          endSuggestions.value = suggestions
          endSuggestionIndex.value = -1
        }
      } catch (error) {
        console.error('Nominatim location search error:', error)
        if (type === 'start') startSuggestions.value = []
        else endSuggestions.value = []
      }
    }

    // display_nameから主要な名前を抽出
    const extractMainName = (displayName) => {
      if (!displayName) return ''
      const parts = displayName.split(',')
      return parts[0].trim()
    }

    // display_nameを逆順にして郵便番号を先頭に表示
    const formatDisplayName = (displayName) => {
      if (!displayName) return ''
      
      // カンマで分割
      const parts = displayName.split(',').map(part => part.trim())
      
      // 最後の「日本」を除外
      if (parts[parts.length - 1] === '日本') {
        parts.pop()
      }
      
      // 郵便番号パターンをチェック（###-####形式）
      const postalCodeRegex = /^\d{3}-\d{4}$/
      let postalCodeIndex = -1
      
      for (let i = 0; i < parts.length; i++) {
        if (postalCodeRegex.test(parts[i])) {
          postalCodeIndex = i
          break
        }
      }
      
      let formattedParts = []
      
      // 郵便番号がある場合は先頭に配置
      if (postalCodeIndex >= 0) {
        formattedParts.push(`〒${parts[postalCodeIndex]}`)
        // 郵便番号を元の配列から削除
        parts.splice(postalCodeIndex, 1)
      }
      
      // 残りの部分を逆順にして追加
      const reversedParts = parts.reverse()
      formattedParts = formattedParts.concat(reversedParts)
      
      return formattedParts.join(', ')
    }

    const selectStartLocation = (suggestion) => {
      startLocation.value = suggestion.name
      startCoordinates.value = {
        lat: suggestion.lat,
        lon: suggestion.lon,
        address: suggestion.address
      }
      // セッションストレージに保存
      sessionStorage.setItem('routeStartCoordinates', JSON.stringify(startCoordinates.value))
      startSuggestions.value = []
      startSuggestionIndex.value = -1
      saveToSession()
      emit('set-marker', { type: 'start', ...startCoordinates.value })
    }

    const selectEndLocation = (suggestion) => {
      endLocation.value = suggestion.name
      endCoordinates.value = {
        lat: suggestion.lat,
        lon: suggestion.lon,
        address: suggestion.address
      }
      // セッションストレージに保存
      sessionStorage.setItem('routeEndCoordinates', JSON.stringify(endCoordinates.value))
      endSuggestions.value = []
      endSuggestionIndex.value = -1
      saveToSession()
      emit('set-marker', { type: 'end', ...endCoordinates.value })
    }

    const handleOutsideClick = (event) => {
      // 候補リストまたは入力欄をクリックした場合は何もしない
      const startContainer = event.target.closest('.autocomplete-container')
      if (!startContainer) {
        startSuggestions.value = []
        endSuggestions.value = []
      }
    }

    const onStartLocationKeydown = (event) => {
      if (startSuggestions.value.length === 0) return
      
      if (event.key === 'ArrowDown') {
        event.preventDefault()
        startSuggestionIndex.value = Math.min(startSuggestionIndex.value + 1, startSuggestions.value.length - 1)
      } else if (event.key === 'ArrowUp') {
        event.preventDefault()
        startSuggestionIndex.value = Math.max(startSuggestionIndex.value - 1, -1)
      } else if (event.key === 'Enter') {
        event.preventDefault()
        if (startSuggestionIndex.value >= 0) {
          selectStartLocation(startSuggestions.value[startSuggestionIndex.value])
        }
      } else if (event.key === 'Escape') {
        startSuggestions.value = []
        startSuggestionIndex.value = -1
      }
    }

    const onEndLocationKeydown = (event) => {
      if (endSuggestions.value.length === 0) return
      
      if (event.key === 'ArrowDown') {
        event.preventDefault()
        endSuggestionIndex.value = Math.min(endSuggestionIndex.value + 1, endSuggestions.value.length - 1)
      } else if (event.key === 'ArrowUp') {
        event.preventDefault()
        endSuggestionIndex.value = Math.max(endSuggestionIndex.value - 1, -1)
      } else if (event.key === 'Enter') {
        event.preventDefault()
        if (endSuggestionIndex.value >= 0) {
          selectEndLocation(endSuggestions.value[endSuggestionIndex.value])
        }
      } else if (event.key === 'Escape') {
        endSuggestions.value = []
        endSuggestionIndex.value = -1
      }
    }

    const toggleMapSelection = (type) => {
      emit('select-on-map', type)
    }

    const searchRoute = async () => {
      if (!startLocation.value || !endLocation.value) {
        alert('出発地と目的地を入力してください')
        return
      }

      try {
        const requestData = {
          startLocation: startLocation.value,
          endLocation: endLocation.value,
          transportMode: transportMode.value,
          timeSelect: routeConditions.timeSelect,
          date: routeConditions.selectedDate,
          time: routeConditions.selectedTime,
          arriveBy: routeConditions.timeSelect === 'arrival'
        }

        // Spring Boot バックエンドを使用した経路検索
        const response = await fetch('/api/route/search', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(requestData),
          credentials: 'include'
        })
        
        if (response.ok) {
          const routeResults = await response.json()
          routes.value = routeResults
          saveToSession()
        } else {
          console.error('Route search failed:', response.statusText)
          alert('経路検索に失敗しました')
        }
        
      } catch (error) {
        console.error('Route search error:', error)
        alert('経路検索に失敗しました')
      }
    }

    // 監視関数でセッションストレージに自動保存
    const watchAndSave = () => {
      // transportModeの変更を監視
      const stopWatchTransport = computed(() => transportMode.value)
      const stopWatchConditions = computed(() => routeConditions)
      
      // 変更があったら保存
      const saveData = () => {
        saveToSession()
      }
      
      return { stopWatchTransport, stopWatchConditions, saveData }
    }

    onMounted(() => {
      // セッションストレージからデータを読み込み
      loadFromSession()
      
      // 現在日時をデフォルト値として設定
      const now = new Date()
      const currentDate = now.toISOString().split('T')[0]
      const currentTime = now.toTimeString().split(' ')[0].slice(0, 5)
      
      if (!routeConditions.selectedDate) {
        routeConditions.selectedDate = currentDate
      }
      if (!routeConditions.selectedTime) {
        routeConditions.selectedTime = currentTime
      }
      
      // 外部クリックで候補リストを閉じる
      document.addEventListener('click', handleOutsideClick)

      // マップからの選択結果を受け取る
      window.addEventListener('map-selected-location', (e) => {
        const { type, name, lat, lon, address } = e.detail || {};
        if (type === 'start') {
          startLocation.value = name;
          startCoordinates.value = { lat, lon, address };
          sessionStorage.setItem('routeStartCoordinates', JSON.stringify(startCoordinates.value));
          saveToSession();
          emit('set-marker', { type: 'start', lat, lon, address });
        } else if (type === 'end') {
          endLocation.value = name;
          endCoordinates.value = { lat, lon, address };
          sessionStorage.setItem('routeEndCoordinates', JSON.stringify(endCoordinates.value));
          saveToSession();
          emit('set-marker', { type: 'end', lat, lon, address });
        }
      });
    })

    onUnmounted(() => {
      document.removeEventListener('click', handleOutsideClick)
      // アンマウント時にセッションストレージに保存
      saveToSession()
    })

    return {
      showConditions,
      startSuggestions,
      endSuggestions,
      startSuggestionIndex,
      endSuggestionIndex,
      isWaitingForMapClick,
      transportMode,
      startLocation,
      endLocation,
      routes,
      routeConditions,
      transportOptions,
      timeSelectOptions,
      toggleConditions,
      onStartLocationInput,
      onEndLocationInput,
      selectStartLocation,
      selectEndLocation,
      onStartLocationKeydown,
      onEndLocationKeydown,
      toggleMapSelection,
      searchRoute
    }
  }
}
</script>

<style scoped>
/* RouteGuidance コンテナスタイル */
form {
  gap: 0;
}
.form-section {
  margin-bottom: 20px;
}
.section-title {
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid #333;
  font-size: 1.25rem;
  font-weight: 700;
  color: #333;
  background: linear-gradient(90deg, #333333 0%, #666666 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* RouteGuidance 固有のスタイル */
.autocomplete-container {
  position: relative;
}

.map-select-btn, .map-select-btn:hover {
  position: absolute;
  top: 50%;
  right: 2px;
  padding: 4px;
  transform: translateY(-50%) !important;
  z-index: 1;
}

/* 条件指定の表示/非表示切り替えスタイル */
.expand-trigger {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  text-align: right;
  margin: 0 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 15px 0 10px 0;
  font-weight: 600;
  color: #333;
}

.expand-trigger:hover {
  transform: translateY(-1px);
}

.icon-expand-trigger {
  transition: transform 0.3s ease;
  font-size: 1.2em;
  font-weight: bold;
  color: #333;
  margin-left: 8px;
}

.icon-expand-trigger.rotated {
  transform: rotate(180deg);
}

.form-actions {
  margin-top: 10px;
  display: flex;
  justify-content: center;
  align-items: center;

}

/* 経路結果のスタイル */
.route-results {
  margin-top: 25px;
  padding: 20px;
  background: linear-gradient(180deg, #ffffff 0%, #f8f8f8 100%);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #dee2e6;
}

.route-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.route-item {
  padding: 16px 20px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border: 1px solid #dee2e6;
  border-radius: 10px;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.route-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  border-color: #adb5bd;
}

.route-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f1f3f4;
}

.route-time {
  color: #333;
  font-size: 16px;
  font-weight: 700;
}

.route-cost {
  color: #666;
  font-size: 16px;
  font-weight: 700;
  background: linear-gradient(90deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 4px 12px;
  border-radius: 15px;
  border: 1px solid #dee2e6;
}

.route-details {
  font-size: 14px;
  color: #555;
  line-height: 1.5;
  font-weight: 400;
}
</style>
