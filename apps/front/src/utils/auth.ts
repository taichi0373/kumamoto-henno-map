// ユーザー情報の型定義
export interface User {
  username?: string
  email?: string
  id?: string
}

// 認証関連のユーティリティクラス
export class AuthUtils {
  private static readonly TOKEN_KEY = 'auth_token'
  private static readonly USER_KEY = 'user_info'

  /**
   * ログイン状態を確認する
   * @returns ログイン状態
   */
  static isLoggedIn(): boolean {
    const token = localStorage.getItem(this.TOKEN_KEY) || sessionStorage.getItem(this.TOKEN_KEY)
    return !!token
  }

  /**
   * ユーザー情報を取得する
   * @returns ユーザー情報
   */
  static getUser(): User {
    try {
      const userInfo = localStorage.getItem(this.USER_KEY) || sessionStorage.getItem(this.USER_KEY)
      if (userInfo) {
        return JSON.parse(userInfo) as User
      }
    } catch (error) {
      console.error('ユーザー情報の取得に失敗しました:', error)
    }
    
    // フォールバック: sessionStorageからusernameを取得
    const username = sessionStorage.getItem('username')
    return username ? { username } : {}
  }

  /**
   * ログイン処理
   * @param token 認証トークン
   * @param user ユーザー情報
   * @param remember ログイン状態を保持するか
   */
  static login(token: string, user: User, remember: boolean = false): void {
    const storage = remember ? localStorage : sessionStorage
    
    storage.setItem(this.TOKEN_KEY, token)
    storage.setItem(this.USER_KEY, JSON.stringify(user))
    
    // 互換性のため、usernameもsessionStorageに保存
    if (user.username) {
      sessionStorage.setItem('username', user.username)
    }
  }

  /**
   * ログアウト処理
   */
  static logout(): void {
    // localStorage から削除
    localStorage.removeItem(this.TOKEN_KEY)
    localStorage.removeItem(this.USER_KEY)
    
    // sessionStorage から削除
    sessionStorage.removeItem(this.TOKEN_KEY)
    sessionStorage.removeItem(this.USER_KEY)
    sessionStorage.removeItem('username')
  }

  /**
   * 認証トークンを取得する
   * @returns 認証トークン
   */
  static getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY) || sessionStorage.getItem(this.TOKEN_KEY)
  }

  /**
   * ユーザー名を設定する（互換性のため）
   * @param username ユーザー名
   */
  static setUsername(username: string): void {
    sessionStorage.setItem('username', username)
    
    // 既存のユーザー情報を更新
    const currentUser = this.getUser()
    const updatedUser = { ...currentUser, username }
    
    const storage = localStorage.getItem(this.USER_KEY) ? localStorage : sessionStorage
    storage.setItem(this.USER_KEY, JSON.stringify(updatedUser))
  }
}

// デフォルトエクスポート（名前付きインポート対応）
export default AuthUtils