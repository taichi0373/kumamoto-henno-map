<template>
  <div class="profile-page">

    <div class="profile-container">
      <AppCard title="プロフィール編集">
        <form @submit.prevent="handleUpdateProfile">
          <div class="form-row">
            <div class="form-col">
                <AppLabel text="ユーザー名" html-for="username" :required="true" size="medium"
                  variant="default" />
                <AppTextarea id="username" type="text" v-model="profileData.username" placeholder="ユーザー名を入力してください"
                  :required="true" size="medium" variant="default" :error-message="validationErrors.username" />
            </div>
            <div class="form-col">
                <AppLabel text="生年月日" html-for="birthDate" :required="true" size="medium"
                  variant="default" />
                <AppTextarea id="birthDate" type="date" v-model="profileData.birthDate" :required="true"
                  size="medium" variant="default" :error-message="validationErrors.birthDate" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-col">
                <AppLabel text="居住地域" html-for="address" :required="true" size="medium"
                  variant="default" />
                <AppSelect id="address" v-model="profileData.address" placeholder="選択してください"
                  :options="addressOptions" :required="true" size="medium" variant="default"
                  :error-message="validationErrors.address" />
            </div>
            <div class="form-col">
                <AppLabel text="運転免許の所持状況" html-for="licenseStatus" :required="true" size="medium"
                  variant="default" />
                <AppSelect id="licenseStatus" v-model="profileData.licenseStatus" placeholder="選択してください"
                  :options="licenseOptions" :required="true" size="medium" variant="default"
                  :error-message="validationErrors.licenseStatus" />
            </div>
          </div>
          <div class="submit-button">
            <AppButton type="submit" :label="isLoading ? '更新中...' : 'プロフィール更新'" severity="primary" :disabled="isLoading"
              :loading="isLoading" />
          </div>
        </form>

        <AppAlert v-if="errorMessage" :message="errorMessage" variant="error" size="medium" :show="!!errorMessage" />

        <AppAlert v-if="successMessage" :message="successMessage" variant="success" size="medium"
          :show="!!successMessage" />
      </AppCard>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { AuthUtils } from '@/utils/auth'
import AppLabel from '@/components/atoms/AppLabel.vue'
import AppTextarea from '@/components/atoms/AppTextarea.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppAlert from '@/components/atoms/AppAlert.vue'
import AppSelect from '@/components/atoms/AppSelect.vue'

export default {
  name: 'ProfilePage',
  components: {
    AppLabel,
    AppTextarea,
    AppButton,
    AppCard,
    AppAlert,
    AppSelect,
  },
  setup() {
    const router = useRouter()
    const profileData = ref({
      username: '',
      birthDate: '',
      address: '',
      licenseStatus: ''
    })

    const isLoading = ref(false)
    const errorMessage = ref('')
    const successMessage = ref('')
    const validationErrors = ref({
      username: '',
      birthDate: '',
      address: '',
      licenseStatus: ''
    })

    // 居住地域の選択肢
    const addressOptions = [
      { value: '431001', text: '熊本市中央区' },
      { value: '431002', text: '熊本市東区' },
      { value: '431003', text: '熊本市西区' },
      { value: '431004', text: '熊本市南区' },
      { value: '431005', text: '熊本市北区' },
      { value: '432024', text: '八代市' },
      { value: '432032', text: '人吉市' },
      { value: '432041', text: '荒尾市' },
      { value: '432059', text: '水俣市' },
      { value: '432067', text: '玉名市' },
      { value: '432091', text: '山鹿市' },
      { value: '432105', text: '菊池市' },
      { value: '432113', text: '宇土市' },
      { value: '432121', text: '上天草市' },
      { value: '432130', text: '宇城市' },
      { value: '432148', text: '阿蘇市' },
      { value: '432156', text: '天草市' },
      { value: '432164', text: '合志市' },
      { value: '433616', text: '美里町' },
      { value: '434035', text: '玉東町' },
      { value: '434051', text: '南関町' },
      { value: '434060', text: '長洲町' },
      { value: '434078', text: '和水町' },
      { value: '434213', text: '大津町' },
      { value: '434221', text: '菊陽町' },
      { value: '434311', text: '南小国町' },
      { value: '434329', text: '小国町' },
      { value: '434337', text: '産山村' },
      { value: '434345', text: '高森町' },
      { value: '434361', text: '西原村' },
      { value: '434370', text: '南阿蘇村' },
      { value: '434434', text: '御船町' },
      { value: '434442', text: '嘉島町' },
      { value: '434451', text: '益城町' },
      { value: '434469', text: '甲佐町' },
      { value: '434493', text: '山都町' },
      { value: '434639', text: '氷川町' },
      { value: '434647', text: '芦北町' },
      { value: '434655', text: '津奈木町' },
      { value: '434671', text: '錦町' },
      { value: '434680', text: '多良木町' },
      { value: '434698', text: '湯前町' },
      { value: '434701', text: '水上村' },
      { value: '434710', text: '相良村' },
      { value: '434728', text: '五木村' },
      { value: '434736', text: '山江村' },
      { value: '434744', text: '球磨村' },
      { value: '434752', text: 'あさぎり町' },
      { value: '434876', text: '苓北町' }
    ]

    // 免許状況の選択肢
    const licenseOptions = [
      { value: 'licensed', text: '所持している' },
      { value: 'unlicensed', text: '所持していない' },
      { value: 'returned', text: '返納済み' },
      { value: 'suspension', text: '停止している' },
      { value: 'expired', text: '失効している' }
    ]

    // セッションストレージからプロフィールデータを読み込み
    const loadFromSession = () => {
      const savedProfile = sessionStorage.getItem('userProfile')
      if (savedProfile) {
        const profile = JSON.parse(savedProfile)
        profileData.value = {
          username: profile.username || '',
          birthDate: profile.birth_date || profile.birthDate || '',
          address: profile.address || '',
          licenseStatus: profile.license_status || profile.licenseStatus || ''
        }
      }
    }

    // セッションストレージにプロフィールデータを保存
    const saveToSession = (updatedProfile) => {
      sessionStorage.setItem('userProfile', JSON.stringify(updatedProfile))
    }

    const loadProfile = async () => {
      try {
        console.log('プロフィール読み込み開始');

        // ログイン状態確認
        if (!AuthUtils.isLoggedIn()) {
          console.log('ログインしていません。ログイン画面にリダイレクトします。');
          errorMessage.value = 'ログインしてください'
          router.push('/login')
          return
        }

        // セッションストレージからまず読み込み
        loadFromSession()

        // APIからプロフィール情報を取得して最新の状態を確認
        const response = await fetch('/api/user/profile', {
          method: 'GET',
          credentials: 'include',
          headers: {
            'Accept': 'application/json',
            'Authorization': `Bearer ${AuthUtils.getToken()}`
          }
        })

        console.log('プロフィール取得レスポンス:', {
          status: response.status,
          ok: response.ok
        });

        if (response.status === 401 || response.status === 403) {
          // セッション切れの場合はログアウト処理
          AuthUtils.logout()
          errorMessage.value = 'セッションが切れました。再度ログインしてください'
          router.push('/login')
          return
        }

        if (response.ok) {
          const data = await response.json()
          console.log('プロフィール取得データ:', data);

          if (data.success && data.profile) {
            const profile = data.profile
            profileData.value = {
              username: profile.username || '',
              birthDate: profile.birth_date || profile.birthDate || '',
              address: profile.address || '',
              licenseStatus: profile.license_status || profile.licenseStatus || ''
            }

            // セッションストレージも更新
            saveToSession(profile)
            console.log('プロフィールデータを更新:', profileData.value);
          }
        } else {
          console.error('プロフィール取得エラー:', response.statusText);
          errorMessage.value = 'プロフィール情報の取得に失敗しました'
        }
      } catch (error) {
        console.error('Profile load error:', error)
        errorMessage.value = 'プロフィール情報の取得に失敗しました'
      }
    }

    const handleUpdateProfile = async () => {
      isLoading.value = true
      errorMessage.value = ''
      successMessage.value = ''

      try {
        console.log('プロフィール更新データ:', profileData.value);

        // ログイン状態確認
        if (!AuthUtils.isLoggedIn()) {
          errorMessage.value = 'ログインしてください'
          router.push('/login')
          return
        }

        const response = await fetch('/api/user/profile', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${AuthUtils.getToken()}`
          },
          body: JSON.stringify(profileData.value),
          credentials: 'include'
        })

        console.log('プロフィール更新レスポンス:', {
          status: response.status,
          ok: response.ok
        });

        if (response.status === 401 || response.status === 403) {
          // セッション切れの場合はログアウト処理
          AuthUtils.logout()
          errorMessage.value = 'セッションが切れました。再度ログインしてください'
          router.push('/login')
          return
        }

        const result = await response.json()
        console.log('プロフィール更新結果:', result);

        if (result.success) {
          successMessage.value = 'プロフィールが更新されました'

          // 更新されたデータでセッションストレージも更新
          if (result.user) {
            const updatedProfile = {
              ...result.user,
              birth_date: result.user.birth_date || result.user.birthDate,
              license_status: result.user.license_status || result.user.licenseStatus
            }
            saveToSession(updatedProfile)

            // ローカルのprofileDataも更新
            profileData.value = {
              username: updatedProfile.username || '',
              birthDate: updatedProfile.birth_date || updatedProfile.birthDate || '',
              address: updatedProfile.address || '',
              licenseStatus: updatedProfile.license_status || updatedProfile.licenseStatus || ''
            }
          }

          // 成功メッセージを3秒後に消す
          setTimeout(() => {
            successMessage.value = ''
          }, 3000)

        } else {
          errorMessage.value = result.message || '更新に失敗しました'
        }
      } catch (error) {
        console.error('Profile update error:', error)
        errorMessage.value = 'ネットワークエラーが発生しました'
      } finally {
        isLoading.value = false
      }
    }

    onMounted(async () => {
      // ページロード時にプロフィール読み込み
      await loadProfile()
    })

    return {
      profileData,
      isLoading,
      errorMessage,
      successMessage,
      validationErrors,
      addressOptions,
      licenseOptions,
      handleUpdateProfile
    }
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.profile-page {
  min-height: calc(100vh - 70px); /* ヘッダーの高さを考慮 */
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-container {
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
}

/* フォームレイアウト */
.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-col {
  flex: 1;
  min-width: 0;
}
.submit-button {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>