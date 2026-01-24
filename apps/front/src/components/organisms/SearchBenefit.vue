<template>
  <div class="form-section">
    <form @submit.prevent="performSearch">
      <div class="search-header">
        <span class="search-subtitle">検索条件で絞り込む</span>
      </div>
      
      <BaseFormGroup>
        <BaseLabel for="address" text="居住地域" :required="true" />
        <BaseSelect
          id="address"
          v-model="searchConditions.address"
          :options="addressOptions"
          :required="true"
          placeholder="選択してください"
        />
      </BaseFormGroup>
      
      <BaseFormGroup>
        <BaseLabel for="license-status" text="運転免許の所持状況" :required="true" />
        <BaseSelect
          id="license-status"
          v-model="searchConditions.licenseStatus"
          :options="licenseStatusOptions"
          :required="true"
          placeholder="選択してください"
        />
      </BaseFormGroup>
      
      <BaseFormGroup>
        <BaseLabel for="age" text="年齢" :required="true" />
        <BaseInput
          id="age"
          v-model="searchConditions.age"
          type="number"
          placeholder="年齢を入力してください"
          :required="true"
        />
      </BaseFormGroup>
      
      <div class="form-actions">
        <BaseButton
          type="button"
          label="条件をクリア"
          :primary="false"
          :full-width="false"
          icon="fa-solid fa-eraser"
          @click="clearConditions"
        />
        <BaseButton
          type="submit"
          label="特典を検索"
          :primary="true"
          :full-width="false"
          icon="fa-solid fa-search"
          :disabled="isLoading"
        />
      </div>
    </form>
  </div>
  
  <div class="results-section" v-if="hasSearched">
    <BaseAlert
      v-if="searchResults.length === 0 && !isLoading"
      type="info"
      title="検索結果なし"
      message="条件に一致する特典が見つかりませんでした。検索条件を変更してお試しください。"
      :dismissible="false"
    />
    
    <div v-if="isLoading" class="loading-indicator">
      <i class="fa-solid fa-spinner fa-spin"></i>
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
              <i class="fa-solid fa-user-clock"></i>
              最低年齢: {{ benefit.min_age }}歳以上
            </span>
            <span class="condition-tag" v-if="benefit.max_age">
              <i class="fa-solid fa-user-clock"></i>
              最高年齢: {{ benefit.max_age }}歳以下
            </span>
            <span class="condition-tag" v-if="benefit.license_status">
              <i class="fa-solid fa-id-card"></i>
              {{ getLicenseStatusText(benefit.license_status) }}
            </span>
          </div>
          
          <div class="benefit-info">
            <div class="benefit-location">
              <i class="fa-solid fa-location-dot"></i>
              {{ benefit.municipality_name }}
            </div>
            <div class="benefit-contact" v-if="benefit.tel_number">
              <i class="fa-solid fa-phone"></i>
              {{ benefit.tel_number }}
            </div>
          </div>
          
          <div class="benefit-note" v-if="benefit.note">
            <small>{{ benefit.note }}</small>
          </div>
        </div>
        
        <div class="benefit-actions">
          <BaseButton
            type="button"
            label="地図で確認"
            :primary="false"
            size="small"
            icon="fa-solid fa-map-marker-alt"
            @click="showOnMap(benefit)"
          />
          <BaseLink
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

<script>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import BaseFormGroup from '../atoms/FormGroup.vue'
import BaseLabel from '../atoms/Label.vue'
import BaseInput from '../atoms/Input.vue'
import BaseSelect from '../atoms/Select.vue'
import BaseButton from '../atoms/Button.vue'
import BaseAlert from '../atoms/Alert.vue'
import BaseLink from '../atoms/Link.vue'

export default {
  name: 'SearchBenefit',
  components: {
    BaseFormGroup,
    BaseLabel,
    BaseInput,
    BaseSelect,
    BaseButton,
    BaseAlert,
    BaseLink
  },
  emits: ['show-benefit-on-map'],
  setup(props, { emit }) {
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
      { value: '431001', text: '熊本市' },
      { value: '432024', text: '八代市' },
      { value: '432032', text: '人吉市' },
      { value: '432041', text: '荒尾市' },
      { value: '432059', text: '水俣市' },
      { value: '432067', text: '玉名市' },
      { value: '432083', text: '山鹿市' },
      { value: '432105', text: '菊池市' },
      { value: '432113', text: '宇土市' },
      { value: '432121', text: '上天草市' },
      { value: '432130', text: '宇城市' },
      { value: '432148', text: '阿蘇市' },
      { value: '432156', text: '天草市' },
      { value: '432164', text: '合志市' },
      { value: '433489', text: '美里町' },
      { value: '433641', text: '玉東町' },
      { value: '433675', text: '南関町' },
      { value: '433683', text: '長洲町' },
      { value: '433691', text: '和水町' },
      { value: '434035', text: '大津町' },
      { value: '434043', text: '菊陽町' },
      { value: '434230', text: '南小国町' },
      { value: '434248', text: '小国町' },
      { value: '434256', text: '産山村' },
      { value: '434281', text: '高森町' },
      { value: '434329', text: '西原村' },
      { value: '434337', text: '南阿蘇村' },
      { value: '434418', text: '御船町' },
      { value: '434426', text: '嘉島町' },
      { value: '434434', text: '益城町' },
      { value: '434442', text: '甲佐町' },
      { value: '434477', text: '山都町' },
      { value: '434680', text: '氷川町' },
      { value: '434825', text: '芦北町' },
      { value: '434841', text: '津奈木町' },
      { value: '435015', text: '錦町' },
      { value: '435058', text: '多良木町' },
      { value: '435066', text: '湯前町' },
      { value: '435074', text: '水上村' },
      { value: '435104', text: '相良村' },
      { value: '435112', text: '五木村' },
      { value: '435121', text: '山江村' },
      { value: '435139', text: '球磨村' },
      { value: '435147', text: 'あさぎり町' },
      { value: '435317', text: '苓北町' }
    ]

    // 免許状況の選択肢
    const licenseStatusOptions = [
      { value: 'licensed', text: '所持している' },
      { value: 'unlicensed', text: '所持していない' },
      { value: 'returned', text: '返納済み' },
      { value: 'suspension', text: '停止している' },
      { value: 'expired', text: '失効している' }
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

    return {
      hasSearched,
      isLoading,
      searchResults,
      searchConditions,
      addressOptions,
      licenseStatusOptions,
      performSearch,
      clearConditions,
      getLicenseStatusText,
      showOnMap
    }
  }
}
</script>

<style scoped>
form {
  gap: 0;
}

.search-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e2e8f0;
}

.search-subtitle {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
}

.loading-indicator {
  text-align: center;
  padding: 40px 20px;
  color: #718096;
  font-size: 14px;
}

.loading-indicator i {
  margin-right: 8px;
  color: #3182ce;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 20px;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 10px;
}

.search-form {
  background: #f8fafc;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  margin-bottom: 20px;
}

.form-row {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.form-col {
  flex: 1;
  min-width: 200px;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #e2e8f0;
}

.results-section {
  margin-top: 30px;
}

.results-list {
  display: grid;
  gap: 20px;
}

.benefit-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.benefit-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.benefit-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 10px;
}

.benefit-name {
  font-size: 18px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
  flex: 1;
}

.benefit-category {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.benefit-content {
  margin-bottom: 15px;
}

.benefit-description {
  color: #4a5568;
  line-height: 1.6;
  margin-bottom: 15px;
}

.benefit-conditions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
}

.condition-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 4px 12px;
  font-size: 12px;
  color: #4a5568;
  font-weight: 500;
}

.condition-tag i {
  font-size: 10px;
  color: #718096;
}

.benefit-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
}

.benefit-location,
.benefit-contact {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #4a5568;
}

.benefit-location i,
.benefit-contact i {
  color: #718096;
  width: 14px;
}

.benefit-note {
  background: #f7fafc;
  border-left: 4px solid #cbd5e0;
  padding: 8px 12px;
  margin: 10px 0;
  font-size: 12px;
  color: #718096;
  font-style: italic;
  border-radius: 0 4px 4px 0;
}

.benefit-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding-top: 15px;
  border-top: 1px solid #f1f5f9;
}
</style>