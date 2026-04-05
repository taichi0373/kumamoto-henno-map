import { defineStore } from 'pinia'
import apiClient from '@/utils/api'

/** ユーザー情報の型定義 */
export interface User {
  username?: string
  email?: string
  id?: string
}

/** ストレージキー定数 */
const USER_KEY = 'user_info'
const TOKEN_KEY = 'auth_token'

/** 初期ユーザー情報の取得 */
const loadInitialUser = (): User | null => {
  try {
    const userInfo = localStorage.getItem(USER_KEY) || sessionStorage.getItem(USER_KEY)
    return userInfo ? JSON.parse(userInfo) as User : null
  } catch {
    return null
  }
}

/** 初期トークンの取得 */
const loadInitialToken = (): string | null => {
  return localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY)
}

/**
 * 認証状態を管理するPiniaストア
 * JWTトークンは Authorization: Bearer ヘッダーで送信するため、localStorage/sessionStorage で管理する。
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    /** ユーザー情報 */
    user: loadInitialUser(),
    /** JWTトークン */
    token: loadInitialToken()
  }),
  getters: {
    /** ログイン状態（トークンとユーザー情報の両方が存在する場合のみtrue） */
    isLoggedIn: (state): boolean => !!state.token && !!state.user,
    /** ユーザー情報取得 */
    getUser: (state): User | null => state.user,
    /** トークン取得 */
    getToken: (state): string | null => state.token
  },
  actions: {
    /**
     * ログイン処理
     * @param user ユーザー情報
     * @param token JWTトークン
     * @param remember ログイン状態を維持するか
     */
    login(user: User, token: string, remember: boolean = false): void {
      this.user = user
      this.token = token
      localStorage.removeItem(USER_KEY)
      sessionStorage.removeItem(USER_KEY)
      localStorage.removeItem(TOKEN_KEY)
      sessionStorage.removeItem(TOKEN_KEY)
      const storage = remember ? localStorage : sessionStorage
      storage.setItem(USER_KEY, JSON.stringify(user))
      storage.setItem(TOKEN_KEY, token)
      if (user.username) {
        sessionStorage.setItem('username', user.username)
      }
    },
    /**
     * ログアウト処理
     * ローカルのトークンとユーザー情報をクリアし、サーバーにログアウトを通知する。
     */
    async logout(): Promise<void> {
      this.user = null
      this.token = null
      localStorage.removeItem(USER_KEY)
      sessionStorage.removeItem(USER_KEY)
      localStorage.removeItem(TOKEN_KEY)
      sessionStorage.removeItem(TOKEN_KEY)
      sessionStorage.removeItem('username')
      try {
        await apiClient.post('/auth/logout')
      } catch (error) {
        console.warn('Logout API call failed:', error)
      }
    }
  }
})
