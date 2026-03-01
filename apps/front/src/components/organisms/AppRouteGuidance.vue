<template>
  <div class="p-2">
    <form @submit.prevent="searchRoute">

      <div class="form-row-1">
        <div class="form-col">
          <AppLabel :id="'transport-mode'" :required="true">交通手段</AppLabel>
          <AppSelect id="transport-mode" v-model="transportMode" :options="transportOptions" optionLabel="text"
            optionValue="value" :required="true" />
        </div>

        <div class="form-col">
          <AppLabel :id="'start-location'" :required="true">出発地</AppLabel>
          <div class="autocomplete-container">
            <AppInputGroupWithButton v-model="startLocation" :input-id="'start-location'" :type="'text'"
              :placeholder="'出発地を入力してください'" :required="true" :button-icon="'pi pi-map-marker'"
              :button-disabled="isWaitingForMapClick" @input="onStartLocationInput" @keydown="onStartLocationKeydown"
              @button-click="toggleMapSelection('start')" />

            <AppSuggestionList :suggestions="startSuggestions" :activeIndex="startSuggestionIndex"
              @select="selectStartLocation" />
          </div>
        </div>

        <div class="form-col">
          <AppLabel :id="'end-location'" :required="true">目的地</AppLabel>
          <div class="autocomplete-container">
            <AppInputGroupWithButton v-model="endLocation" :input-id="'end-location'" :type="'text'"
              :placeholder="'目的地を入力してください'" :required="true" :button-icon="'pi pi-map-marker'"
              :button-disabled="isWaitingForMapClick" @input="onEndLocationInput" @keydown="onEndLocationKeydown"
              @button-click="toggleMapSelection('end')" />
            <AppSuggestionList :suggestions="endSuggestions" :activeIndex="endSuggestionIndex"
              @select="selectEndLocation" />
          </div>
        </div>
      </div>

      <div class="expand-trigger mt-4" @click="toggleConditions">
        {{ showConditions ? '条件を閉じる' : '条件指定' }}
        <span :class="showConditions ? 'icon-expand-trigger rotated' : 'icon-expand-trigger'">
          {{ showConditions ? '▲' : '▼' }}
        </span>
      </div>
      <div v-if="showConditions">
        <div class="form-row-1 mt-2">
          <AppLabel :id="'time-select'">出発/到着</AppLabel>
          <AppSelect id="time-select" v-model="routeConditions.timeSelect" :options="timeSelectOptions" optionLabel="text"
          optionValue="value" />
        </div>
        <div class="form-row-2">
          <div class="form-col">
            <AppLabel>日付</AppLabel>
            <AppCalendar id="date" type="date" v-model="routeConditions.selectedDate" />
          </div>
          <div class="form-col">
            <AppLabel>時間</AppLabel>
            <AppTimePicker id="time" type="time" v-model="routeConditions.selectedTime" />
          </div>
        </div>
      </div>


      <div class="form-btn">
        <AppButton
          type="button"
          :label="'クリア'"
          :primary="false"
          :icon="'pi pi-trash'"
          @click="clearConditions"
        />
        <AppButton
          type="submit"
          :label="'経路を検索'"
          :primary="true"
          :icon="'pi pi-search'"
          :disabled="isLoading"
        />
      </div>
    </form>
  </div>

  <!-- 経路探索結果 -->
  <div v-if="routes.length > 0" class="route-results">
    <h4 class="section-title">検索結果</h4>

    <div class="route-list">
      <div v-for="(route, index) in routes" :key="index" class="route-item">
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

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import AppLabel from '../atoms/AppLabel.vue'
import AppSelect from '../atoms/AppSelect.vue'
import AppButton from '../atoms/AppButton.vue'
import AppInputGroupWithButton from '../molecules/AppInputGroupWithButton.vue'
import AppSuggestionList from '../atoms/AppSuggestionList.vue'
import AppCalendar from '../atoms/AppCalendar.vue'
import AppTimePicker from '../atoms/AppTimePicker.vue'

const emit = defineEmits<{
  (e: 'set-marker', payload: { type: string; lat: number; lon: number; address: unknown }): void;
  (e: 'select-on-map', type: string): void;
}>();


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
      selectedDate: null,  // Date | null
      selectedTime: null   // Date | null
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
        try {
          const conditions = JSON.parse(savedConditions)
          // 日付文字列をDateオブジェクトに変換
          if (conditions.selectedDate) {
            conditions.selectedDate = new Date(conditions.selectedDate)
          }
          if (conditions.selectedTime) {
            conditions.selectedTime = new Date(conditions.selectedTime)
          }
          Object.assign(routeConditions, conditions)
        } catch (error) {
          console.warn('Failed to parse saved route conditions:', error)
          // 破損したデータを削除
          sessionStorage.removeItem('routeConditions')
        }
      }
      if (savedRoutes) {
        try {
          routes.value = JSON.parse(savedRoutes)
        } catch (error) {
          console.warn('Failed to parse saved route results:', error)
          // 破損したデータを削除
          sessionStorage.removeItem('routeResults')
        }
      }
    }

    // セッションストレージにデータを保存
    const saveToSession = () => {
      try {
        sessionStorage.setItem('routeTransportMode', transportMode.value)
        sessionStorage.setItem('routeStartLocation', startLocation.value)
        sessionStorage.setItem('routeEndLocation', endLocation.value)
        sessionStorage.setItem('routeConditions', JSON.stringify(routeConditions))
        sessionStorage.setItem('routeResults', JSON.stringify(routes.value))
      } catch (error) {
        console.warn('Failed to save route data to session storage:', error)
      }
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
            'User-Agent': 'benefit_map/1.0'
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

    // マップ選択のイベントハンドラ
    const handleMapSelection = (e) => {
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
    };

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
          date: routeConditions.selectedDate instanceof Date
            ? routeConditions.selectedDate.toISOString().split('T')[0]
            : '',
          time: routeConditions.selectedTime instanceof Date
            ? routeConditions.selectedTime.toTimeString().split(' ')[0].slice(0, 5)
            : '',
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

      // 現在日時をデフォルト値として設定（DatePickerはDateオブジェクトを期待する）
      const now = new Date()

      if (!routeConditions.selectedDate) {
        routeConditions.selectedDate = now
      }
      if (!routeConditions.selectedTime) {
        routeConditions.selectedTime = now
      }

      // 外部クリックで候補リストを閉じる
      document.addEventListener('click', handleOutsideClick)

      // マップからの選択結果を受け取る
      window.addEventListener('map-selected-location', handleMapSelection);
    })

    onUnmounted(() => {
      try {
        if (typeof document !== 'undefined') {
          document.removeEventListener('click', handleOutsideClick)
        }
        if (typeof window !== 'undefined') {
          window.removeEventListener('map-selected-location', handleMapSelection)
        }
        // アンマウント時にセッションストレージに保存
        saveToSession()
      } catch (error) {
        console.warn('Error during component cleanup:', error)
      }
    })


</script>

<style scoped lang="scss">
@use "@/assets/scss/base";
</style>
