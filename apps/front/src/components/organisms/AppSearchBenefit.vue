<template>
  <div class="p-2">
    <form @submit.prevent="performSearch">
      <div class="search-header">
        <span class="search-subtitle">検索条件で絞り込む</span>
      </div>

      <div class="form-field">
        <AppLabel :id="'address'">居住地域</AppLabel>
        <AppSelect
          id="address"
          v-model="searchConditions.address"
          :options="addressOptions"
          :optionLabel="'label'"
          :optionValue="'value'"
          :filter="true"
          :placeholder="'選択してください'"
        />
      </div>

      <div class="form-field">
        <AppLabel :id="'license-status'">運転免許の所持状況</AppLabel>
        <AppSelect
          id="license-status"
          v-model="searchConditions.licenseStatus"
          :options="licenseStatusOptions"
          :optionLabel="'label'"
          :optionValue="'value'"
          :placeholder="'選択してください'"
        />
      </div>

      <div class="form-field">
        <AppLabel :id="'age'">年齢</AppLabel>
        <AppHalfWidthField
          id="age"
          v-model="searchConditions.age"
          placeholder="年齢を入力してください"
        />

      </div>

      <div class="form-actions">
        <AppButton
          type="button"
          :label="'クリア'"
          :primary="false"
          :icon="'pi pi-trash'"
          @click="clearConditions"
        />
        <AppButton
          type="submit"
          :label="'検索'"
          :primary="true"
          :icon="'pi pi-search'"
          :disabled="isLoading"
        />
      </div>

    </form>
  </div>
  
  <div class="results-section" v-if="hasSearched">
    <AppAlert
      v-if="searchResults.length === 0 && !isLoading"
      type="info"
      title="検索結果なし"
      message="条件に一致する特典が見つかりませんでした。検索条件を変更してお試しください。"
      :dismissible="false"
    />
    
    <div v-if="isLoading" class="loading-indicator">
      <i class="pi pi-spin pi-spinner"></i>
      検索中...
    </div>
    
    <div class="results-list" v-if="!isLoading">
      <div 
        v-for="benefit in searchResults" 
        :key="benefit.id"
        class="benefit-card">
        <div class="benefit-header">
          <h5 class="benefit-name">{{ benefit.name }}</h5>
          <span class="benefit-category">{{ benefit.category }}</span>
        </div>
        
        <div class="benefit-content">
          <p class="benefit-description">{{ benefit.detail }}</p>
          
          <div class="benefit-conditions">
            <span class="condition-tag" v-if="benefit.min_age">
              <i class="pi pi-user"></i>
              最低年齢: {{ benefit.min_age }}歳以上
            </span>
            <span class="condition-tag" v-if="benefit.max_age">
              <i class="pi pi-user"></i>
              最高年齢: {{ benefit.max_age }}歳以下
            </span>
            <span class="condition-tag" v-if="benefit.license_status">
              <i class="pi pi-id-card"></i>
              {{ getLicenseStatusText(benefit.license_status) }}
            </span>
          </div>
          
          <div class="benefit-info">
            <div class="benefit-location">
              <i class="pi pi-map-marker"></i>
              {{ benefit.municipality_name }}
            </div>
            <div class="benefit-contact" v-if="benefit.tel_number">
              <i class="pi pi-phone"></i>
              {{ benefit.tel_number }}
            </div>
          </div>
          
          <div class="benefit-note" v-if="benefit.note">
            <small>{{ benefit.note }}</small>
          </div>
        </div>
        
        <div class="benefit-actions">
          <AppButton
            type="button"
            label="地図で確認"
            :primary="false"
            size="small"
            icon="pi pi-map-marker"
            @click="showOnMap(benefit)"
          />
          <AppLink
            v-if="benefit.url"
            :to="benefit.url"
            text="詳細を見る"
            variant="secondary"
            size="small"
            target="_blank"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppHalfWidthField from '@/components/atoms/AppHalfWidthField.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppAlert from '@/components/atoms/AppAlert.vue'
import AppLink from '@/components/atoms/AppLink.vue'

const emit = defineEmits<{
  (e: 'show-benefit-on-map', benefit: unknown): void;
}>();

const hasSearched = ref(false)
    const isLoading = ref(false)
    const searchResults = ref([])
    
    const searchConditions = reactive({
      address: '',
      licenseStatus: '',
      age: ''
    })

    // 居住地域の選択肢
    const addressOptions = [
      { value: '431001', label: '熊本市' },
      { value: '432024', label: '八代市' },
      { value: '432032', label: '人吉市' },
      { value: '432041', label: '荒尾市' },
      { value: '432059', label: '水俣市' },
      { value: '432067', label: '玉名市' },
      { value: '432083', label: '山鹿市' },
      { value: '432105', label: '菊池市' },
      { value: '432113', label: '宇土市' },
      { value: '432121', label: '上天草市' },
      { value: '432130', label: '宇城市' },
      { value: '432148', label: '阿蘇市' },
      { value: '432156', label: '天草市' },
      { value: '432164', label: '合志市' },
      { value: '433489', label: '美里町' },
      { value: '433641', label: '玉東町' },
      { value: '433675', label: '南関町' },
      { value: '433683', label: '長洲町' },
      { value: '433691', label: '和水町' },
      { value: '434035', label: '大津町' },
      { value: '434043', label: '菊陽町' },
      { value: '434230', label: '南小国町' },
      { value: '434248', label: '小国町' },
      { value: '434256', label: '産山村' },
      { value: '434281', label: '高森町' },
      { value: '434329', label: '西原村' },
      { value: '434337', label: '南阿蘇村' },
      { value: '434418', label: '御船町' },
      { value: '434426', label: '嘉島町' },
      { value: '434434', label: '益城町' },
      { value: '434442', label: '甲佐町' },
      { value: '434477', label: '山都町' },
      { value: '434680', label: '氷川町' },
      { value: '434825', label: '芦北町' },
      { value: '434841', label: '津奈木町' },
      { value: '435015', label: '錦町' },
      { value: '435058', label: '多良木町' },
      { value: '435066', label: '湯前町' },
      { value: '435074', label: '水上村' },
      { value: '435104', label: '相良村' },
      { value: '435112', label: '五木村' },
      { value: '435121', label: '山江村' },
      { value: '435139', label: '球磨村' },
      { value: '435147', label: 'あさぎり町' },
      { value: '435317', label: '苓北町' }
    ]

    // 免許状況の選択肢
    const licenseStatusOptions = [
      { value: 'licensed', label: '所持している' },
      { value: 'unlicensed', label: '所持していない' },
      { value: 'returned', label: '返納済み' },
      { value: 'suspension', label: '停止している' },
      { value: 'expired', label: '失効している' }
    ]

    // セッションストレージからデータを読み込み
    const loadFromSession = () => {
      const savedConditions = sessionStorage.getItem('searchBenefitConditions')
      const savedResults = sessionStorage.getItem('searchBenefitResults')
      const savedHasSearched = sessionStorage.getItem('searchBenefitHasSearched')

      if (savedConditions) {
        const conditions = JSON.parse(savedConditions)
        Object.assign(searchConditions, conditions)
      }
      if (savedResults) {
        searchResults.value = JSON.parse(savedResults)
      }
      if (savedHasSearched) {
        hasSearched.value = JSON.parse(savedHasSearched)
      }
    }

    // セッションストレージにデータを保存
    const saveToSession = () => {
      sessionStorage.setItem('searchBenefitConditions', JSON.stringify(searchConditions))
      sessionStorage.setItem('searchBenefitResults', JSON.stringify(searchResults.value))
      sessionStorage.setItem('searchBenefitHasSearched', JSON.stringify(hasSearched.value))
    }

    // 特典検索API呼び出し
    const searchBenefits = async (conditions) => {
      isLoading.value = true
      try {
        const response = await fetch('/api/benefits/search', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            municipalityCode: conditions.address,
            licenseStatus: conditions.licenseStatus,
            age: parseInt(conditions.age)
          })
        })

        if (response.ok) {
          const data = await response.json()
          searchResults.value = data.benefits || []
        } else {
          console.error('特典検索に失敗しました:', response.statusText)
          searchResults.value = []
        }
      } catch (error) {
        console.error('特典検索中にエラーが発生しました:', error)
        searchResults.value = []
      } finally {
        isLoading.value = false
        saveToSession()
      }
    }

    // 検索実行
    const performSearch = async () => {
      hasSearched.value = true
      await searchBenefits(searchConditions)
    }

    // 条件クリア
    const clearConditions = () => {
      searchConditions.address = ''
      searchConditions.licenseStatus = ''
      searchConditions.age = ''
      hasSearched.value = false
      searchResults.value = []
      
      // セッションストレージからも削除
      sessionStorage.removeItem('searchBenefitConditions')
      sessionStorage.removeItem('searchBenefitResults')
      sessionStorage.removeItem('searchBenefitHasSearched')
    }

    // 免許状況テキスト変換
    const getLicenseStatusText = (status) => {
      const statusMap = {
        'licensed': '免許所持者',
        'unlicensed': '免許なし',
        'returned': '免許返納済み',
        'suspension': '免許停止中',
        'expired': '免許失効',
        'any': '免許状況問わず'
      }
      return statusMap[status] || status
    }
    

    // 地図表示
    const showOnMap = (benefit) => {
      emit('show-benefit-on-map', benefit)
    }

    onMounted(() => {
      loadFromSession()
    })

    onUnmounted(() => {
      saveToSession()
    })


</script>

<style scoped lang="scss">
@use "@/assets/scss/base";
form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.search-header {
  padding-bottom: 10px;
  border-bottom: 1px solid #e2e8f0;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-top: 20px;
  padding-top: 15px;
}

</style>