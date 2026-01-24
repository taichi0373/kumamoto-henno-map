<template>
    <!-- 未ログイン状態 -->
    <div v-if="!isLoggedIn" class="status-section">
      <BaseAlert
        type="info"
        title="ログインしてください"
        message=""
        :dismissible="false"
      />
      <div class="action-section">
        <BaseButton
          type="button"
          label="ログイン"
          :primary="true"
          icon="fa-solid fa-sign-in-alt"
          @click="$router.push('/login')"
        />
      </div>
    </div>
    
    <!-- ローディング状態 -->
    <div v-else-if="loading" class="status-section">
      <div class="loading-indicator">
        <i class="fa-solid fa-spinner fa-spin"></i>
        特典を取得中...
      </div>
    </div>
    
    <!-- 特典なし状態 -->
    <div v-else-if="userBenefits.length === 0" class="status-section">
      <BaseAlert
        type="warning"
        title="利用可能な特典がありません"
        message="プロフィールを設定すると、あなたに適した特典が表示されます。"
        :dismissible="false"
      />
      <div class="action-section">
        <BaseButton
          type="button"
          label="プロフィール設定"
          :primary="true"
          icon="fa-solid fa-user-cog"
          @click="$router.push('/profile')"
        />
      </div>
    </div>
    
    <!-- 特典リスト表示 -->
    <div v-else class="benefits-section">
      <h3 class="section-title">あなたの利用可能特典</h3>
      
      <div class="benefits-list">
        <div 
          v-for="benefit in userBenefits" 
          :key="benefit.id"
          :data-benefit-id="benefit.id"
          class="benefit-card">
          
          <div class="benefit-header">
            <h5 class="benefit-store-name">{{ benefit.store_name }}</h5>
            <span class="benefit-category">{{ benefit.category }}</span>
          </div>
          
          <div class="benefit-content">
            <p class="benefit-description">{{ benefit.description }}</p>
            
            <div class="benefit-conditions" v-if="benefit.age_restriction || benefit.license_condition">
              <span class="condition-tag" v-if="benefit.age_restriction">
                <i class="fa-solid fa-user-clock"></i>
                年齢制限: {{ benefit.age_restriction }}
              </span>
              <span class="condition-tag" v-if="benefit.license_condition">
                <i class="fa-solid fa-id-card"></i>
                {{ benefit.license_condition }}
              </span>
            </div>
            
            <div class="benefit-location">
              <i class="fa-solid fa-location-dot"></i>
              {{ benefit.address }}
            </div>
            
            <div class="benefit-contact" v-if="benefit.tel_number">
              <i class="fa-solid fa-phone"></i>
              {{ benefit.tel_number }}
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import BaseButton from '../atoms/Button.vue'
import BaseAlert from '../atoms/Alert.vue'
import BaseLink from '../atoms/Link.vue'
import { AuthUtils } from '../../utils/auth'

export default {
  name: 'UsersBenefit',
  components: {
    BaseButton,
    BaseAlert,
    BaseLink
  },
  props: {
    userBenefits: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['show-benefit-on-map'],
  setup(props, { emit }) {
    const isLoggedIn = ref(false)

    // ログイン状態の確認
    const checkLoginStatus = () => {
      isLoggedIn.value = AuthUtils.isLoggedIn()
    }

    // 地図表示
    const showOnMap = (benefit) => {
      emit('show-benefit-on-map', benefit)
    }

    // セッションストレージの変更を監視
    const handleStorageChange = () => {
      checkLoginStatus()
    }

    onMounted(() => {
      checkLoginStatus()
      window.addEventListener('storage', handleStorageChange)
    })

    onUnmounted(() => {
      window.removeEventListener('storage', handleStorageChange)
    })

    return {
      isLoggedIn,
      showOnMap
    }
  }
}
</script>

<style scoped>
form {
  gap: 0;
}

.status-section {
  text-align: center;
  padding: 30px 10px;
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
  font-size: 18px;
}

.action-section {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.benefits-section {
  padding: 0;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 25px;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: "🎯";
  font-size: 18px;
}

.benefits-list {
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

.benefit-store-name {
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

.benefit-location,
.benefit-contact {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #4a5568;
  margin-bottom: 8px;
}

.benefit-location i,
.benefit-contact i {
  color: #718096;
  width: 14px;
}

.benefit-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  padding-top: 15px;
  border-top: 1px solid #f1f5f9;
}
</style>