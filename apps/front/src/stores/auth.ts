import { defineStore } from 'pinia'

/** ユーザー情報の型定義 */
export interface User {
  username?: string
  email?: string
  id?: string
}

/** ストレージキー定数 */
const TOKEN_KEY = 'auth_token'
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
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    /** JWTトークン */
    token: (localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY)) as string | null,
    /** ユーザー情報 */
    user: loadInitialUser()
  }),
  getters: {
    /** ログイン状態 */
    isLoggedIn: (state): boolean => !!state.token,
    /** トークン取得 */
    getToken: (state): string | null => state.token,
    /** ユーザー情報取得 */
    getUser: (state): User | null => state.user
  },
  actions: {
    /**
     * ログイン処理
     * @param token JWTトークン
     * @param user ユーザー情報
     * @param remember ログイン状態を維持するか
     */
    login(token: string, user: User, remember: boolean = false): void {
      this.token = token
      this.user = user
      const storage = remember ? localStorage : sessionStorage
      storage.setItem(TOKEN_KEY, token)
      storage.setItem(USER_KEY, JSON.stringify(user))
      if (user.username) {
        sessionStorage.setItem('username', user.username)
      }
    },
    /**
     * ログアウト処理
     */
    logout(): void {
      this.token = null
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
      sessionStorage.removeItem(TOKEN_KEY)
      sessionStorage.removeItem(USER_KEY)
      sessionStorage.removeItem('username')
    }
  }
})
