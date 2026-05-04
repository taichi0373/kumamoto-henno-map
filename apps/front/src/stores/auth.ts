import { defineStore } from 'pinia'
import apiClient from '@/utils/api'
import type { AxiosError } from 'axios'

/** ユーザー情報の型定義 */
export interface User {
  username?: string
  email?: string
  id?: string
  isAdmin?: boolean
}

/** ストレージキー定数 */
const USER_KEY = 'user_info'

/**
 * セキュリティ注意事項:
 * - アクセストークンは XSS によるトークン窃取を防ぐため、localStorage/sessionStorage には保存しない。
 *   Pinia の state（JS メモリ）のみで管理する。
 * - ページリフレッシュ時はトークンが消えるため、再ログインまたはリフレッシュトークンが必要。
 * - ユーザー情報（username・id）は認証に関わらない表示用データのため sessionStorage に保存する。
 *   remember=true の場合のみ localStorage に保存する。
 * - 根本的な XSS 対策として、厳格な CSP・入力サニタイズ・依存ライブラリ監査を必ず行うこと。
 */

/** 初期ユーザー情報の取得（表示用のみ。認証判定には token を使う） */
const loadInitialUser = (): User | null => {
  try {
    const userInfo = localStorage.getItem(USER_KEY) || sessionStorage.getItem(USER_KEY)
    return userInfo ? JSON.parse(userInfo) as User : null
  } catch {
    return null
  }
}

/**
 * 認証状態を管理するPiniaストア
 *
 * ## トークン管理方針
 * - アクセストークン: JS メモリのみ（XSS 耐性のため storage に保存しない）
 * - ユーザー情報: sessionStorage（remember=true 時は localStorage）
 * - ページリフレッシュ後: トークンは消えるため未認証状態になる
 *   → リフレッシュトークン実装後は自動復元される予定
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    /** ユーザー情報（表示用） */
    user: loadInitialUser(),
    /**
     * JWTアクセストークン（メモリのみ・storage非保存）
     * ページリフレッシュで消えることを許容する設計。
     */
    token: null as string | null
  }),
  getters: {
    /** ログイン状態（メモリ上のトークン存在で判定） */
    isLoggedIn: (state): boolean => !!state.token,
    /** ユーザー情報取得 */
    getUser: (state): User | null => state.user,
    /** トークン取得 */
    getToken: (state): string | null => state.token,
    /** 管理者判定（ログインレスポンスのisAdminフラグで判断） */
    isAdmin: (state): boolean => state.user?.isAdmin ?? false
  },
  actions: {
    /**
     * ログイン処理
     * @param user ユーザー情報（表示用）
     * @param token JWTアクセストークン（メモリのみに保持）
     * @param remember ユーザー情報をlocalStorageに保持するか（トークンは対象外）
     */
    login(user: User, token: string, remember: boolean = false): void {
      this.token = token
      this.user = user
      localStorage.removeItem(USER_KEY)
      sessionStorage.removeItem(USER_KEY)
      const storage = remember ? localStorage : sessionStorage
      storage.setItem(USER_KEY, JSON.stringify(user))
    },
    /**
     * ページリフレッシュ時のセッション復元
     * <p>
     * HttpOnly Cookie のリフレッシュトークンで新しいアクセストークンを取得する。
     * App.vue の onMounted で呼び出すこと。
     * </p>
     * @returns セッション復元に成功した場合はtrue
     */
    async restoreSession(): Promise<boolean> {
      try {
        const response = await apiClient.post<{ data: { token: string, user?: User } }>('/auth/refresh')
        const newToken = response.data?.data?.token
        if (!newToken) return false

        this.token = newToken
        
        // ユーザー情報の復元: APIレスポンス > ストレージ の優先順位
        const responseUser = response.data?.data?.user
        if (responseUser) {
          this.user = responseUser
          // APIから取得したユーザー情報をストレージにも保存（remember状態は維持）
          const hasRememberStorage = localStorage.getItem(USER_KEY)
          const storage = hasRememberStorage ? localStorage : sessionStorage
          storage.setItem(USER_KEY, JSON.stringify(responseUser))
        } else {
          // APIにユーザー情報が含まれない場合、ストレージから復元（局所的にtry-catch）
          const stored = localStorage.getItem(USER_KEY) || sessionStorage.getItem(USER_KEY)
          if (stored) {
            try {
              this.user = JSON.parse(stored) as User
            } catch (parseError) {
              console.warn('Failed to parse stored user info, continuing without user data:', parseError)
              this.user = null
              // 破損したストレージをクリア
              localStorage.removeItem(USER_KEY)
              sessionStorage.removeItem(USER_KEY)
            }
          } else {
            this.user = null
          }
        }
        return true
      } catch (error: unknown) {
        const axiosError = error as AxiosError<{ message: string }>
        // 401はリフレッシュトークンが無い/無効な正常系 → 静かに未ログイン扱い
        if (axiosError?.response?.status === 401) {
          return false
        }
        
        // その他のエラー（ネットワークエラー等）は状態をクリアして静かに処理
        this.token = null
        this.user = null
        localStorage.removeItem(USER_KEY)
        sessionStorage.removeItem(USER_KEY)
        return false
      }
    },
    /**
     * ログアウト処理
     * DBのリフレッシュトークンを失効させ、メモリ上のトークンとユーザー情報をクリアする。
     */
    async logout(): Promise<void> {
      this.token = null
      this.user = null
      localStorage.removeItem(USER_KEY)
      sessionStorage.removeItem(USER_KEY)
      try {
        // サーバー側でリフレッシュトークンを失効させ Cookie をクリア
        await apiClient.post('/auth/logout')
      } catch (error) {
        console.warn('Logout API call failed:', error)
      }
    }
  }
})
