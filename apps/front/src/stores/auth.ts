import { defineStore } from 'pinia'

/** ユーザー情報の型定義 */
export interface User {
  username?: string
  email?: string
  id?: string
}

/** ストレージキー定数 */
const USER_KEY = 'user_info'

/** 初期ユーザー情報の取得 */
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
 * JWTトークンは HttpOnly Cookie に格納されるため、このストアでは管理しない。
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    /** ユーザー情報 */
    user: loadInitialUser()
  }),
  getters: {
    /** ログイン状態（ユーザー情報の有無で判定） */
    isLoggedIn: (state): boolean => !!state.user,
    /** ユーザー情報取得 */
    getUser: (state): User | null => state.user
  },
  actions: {
    /**
     * ログイン処理
     * @param user ユーザー情報
     * @param remember ログイン状態を維持するか
     */
    login(user: User, remember: boolean = false): void {
      this.user = user
      localStorage.removeItem(USER_KEY)
      sessionStorage.removeItem(USER_KEY)
      const storage = remember ? localStorage : sessionStorage
      storage.setItem(USER_KEY, JSON.stringify(user))
      if (user.username) {
        sessionStorage.setItem('username', user.username)
      }
    },
    /**
     * ログアウト処理
     * ローカルのユーザー情報をクリアし、サーバー側の HttpOnly Cookie を削除する。
     */
    async logout(): Promise<void> {
      this.user = null
      localStorage.removeItem(USER_KEY)
      sessionStorage.removeItem(USER_KEY)
      sessionStorage.removeItem('username')
      try {
        await fetch('/benefit-map/api/auth/logout', {
          method: 'POST',
          credentials: 'include'
        })
      } catch (error) {
        console.warn('Logout API call failed:', error)
        // Cookie のクリアに失敗しても JWT 期限切れにより自動無効化される
      }
    }
  }
})
