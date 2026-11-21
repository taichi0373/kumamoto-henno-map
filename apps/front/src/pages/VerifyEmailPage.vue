<template>
  <div class="page">
    <AppBlockUI :blocked="isLoading" />
    <div class="whole">
      <AppCard title="メールアドレスの確認" :inputStyle="{ width: '100%', maxWidth: '600px', padding: '16px' }">

        <div class="form-col">
          <AppMessageBar
            v-if="barMode != ''"
            :mode="barMode"
            :message="barMsg"
            style="width: 100%;"
          ></AppMessageBar>
        </div>

        <template v-if="!isVerified">
          <div class="description">下のボタンを押して、メールアドレスの確認を完了してください。</div>

          <div class="form-btn">
            <AppButton :primary="true" label="メールアドレスを確認する" @click="onSubmit()" />
          </div>

          <div class="form-link">
            <AppLink @click="router.push('/resend-verification')">確認メールを再送する</AppLink>
          </div>
        </template>

        <div v-else class="form-link">
          <AppLink v-if="auth.isLoggedIn" @click="router.push('/profile')">プロフィールへ</AppLink>
          <AppLink v-else @click="router.push('/login')">ログインへ</AppLink>
        </div>

      </AppCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { AxiosError } from 'axios'
import AppBlockUI from '@/components/atoms/AppBlockUI.vue'
import AppButton from '@/components/atoms/AppButton.vue'
import AppCard from '@/components/atoms/AppCard.vue'
import AppLink from '@/components/atoms/AppLink.vue'
import AppMessageBar from '@/components/atoms/AppMessageBar.vue'
import { useAuthStore } from '@/stores/auth'
import { API_RESPONSE_MESSAGE } from '@/utils/messageConstant'
import apiClient from '@/utils/api'
import { responseStatusConstant } from '@/utils/responseStatusConstant'

/** ルーター */
const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

/** 確認トークン */
const token = ref('')

/** 確認完了フラグ */
const isVerified = ref(false)

/** メッセージバー */
const barMode = ref('') as Ref<string>
const barMsg = ref('') as Ref<string>

/** ローディング */
const isLoading = ref(false)

/**
 * トークンが無い場合は再送画面へ遷移する
 * リンクを開いただけでは確定せず、ボタン押下で確定する（メールのリンク先読みによる誤確定を防ぐため）
 */
onMounted(() => {
  const queryToken = route.query.token
  if (typeof queryToken !== 'string' || !queryToken) {
    router.push('/resend-verification')
    return
  }
  token.value = queryToken
})

/**
 * メールアドレス確認処理
 */
const onSubmit = async () => {
  barMode.value = ''
  barMsg.value = ''

  isLoading.value = true
  try {
    const response = await apiClient.post('/auth/email-verification/confirm', {
      token: token.value
    })
    if (response.status === responseStatusConstant.OK) {
      isVerified.value = true
      barMode.value = 'success'
      barMsg.value = API_RESPONSE_MESSAGE.EMAIL_VERIFIED
    }
  } catch (error: unknown) {
    const axiosError = error as AxiosError<{ message: string }>
    const status = axiosError.response?.status
    barMode.value = 'error'
    if (status === responseStatusConstant.BAD_REQUEST || status === responseStatusConstant.CONFLICT) {
      barMsg.value = axiosError.response?.data?.message ?? API_RESPONSE_MESSAGE.INVALID_OR_EXPIRED_TOKEN
    } else {
      barMsg.value = axiosError.response?.data?.message ?? API_RESPONSE_MESSAGE.API_ERROR
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped lang="scss">
@use "@/assets/scss/base";

.page {
  height: calc(100vh - base.$header-height);
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.whole {
  width: 100%;
  max-width: 600px;
  display: flex;
  justify-content: center;
}

.description {
  margin: 8px 0 16px;
  line-height: 1.6;
}
</style>
