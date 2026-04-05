import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'

// Vue CLI用の環境変数
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL || '/benefit-map/api';

/**
 * 401 Unauthorized 発生時に呼び出されるコールバックの型
 */
type UnauthorizedHandler = () => void

/** 401ハンドラー（App.vue のセットアップ時に登録される） */
let unauthorizedHandler: UnauthorizedHandler | null = null

/**
 * 401 Unauthorized 発生時のコールバックを登録する
 * <p>
 * api.ts とストアの循環依存を避けるため、ストアへの直接依存を持たず
 * コールバック注入でハンドリングを委譲する。
 * main.tsで呼び出すこと。
 * </p>
 * @param handler ログアウト処理とリダイレクトを行うコールバック
 */
export function setUnauthorizedHandler(handler: UnauthorizedHandler): void {
  unauthorizedHandler = handler
}

/**
 * JWTトークン取得関数の型
 */
type TokenProvider = () => string | null

/** トークンプロバイダー（auth store への循環依存を避けるため注入方式） */
let tokenProvider: TokenProvider | null = null

/**
 * JWTトークンプロバイダーを登録する
 * <p>
 * main.tsで呼び出すこと。
 * </p>
 * @param provider トークンを返す関数
 */
export function setTokenProvider(provider: TokenProvider): void {
  tokenProvider = provider
}

/**
 * API リクエストボディの値の型
 */
type RequestBodyValue = string | number | boolean | null | undefined

/**
 * API リクエストボディの基本型（再帰的に入れ子可能）
 */
interface RequestBody {
  [key: string]: RequestBodyValue | RequestBody | Array<RequestBodyValue | RequestBody>
}

/**
 * API クエリパラメータの型
 */
type QueryParams = Record<string, string | number | boolean | string[] | number[] | undefined>

/**
 * API レスポンスデータの値の型
 */
type ResponseDataValue = string | number | boolean | null

/**
 * API レスポンスデータの基本型（再帰的に入れ子可能）
 */
interface ResponseData {
  [key: string]: ResponseDataValue | ResponseData | Array<ResponseDataValue | ResponseData>
}

/**
 * API レスポンスの汎用型
 */
type ApiResponse = ResponseData | ResponseDataValue

/**
 * RESTful APIクライアント(axiosベース)
 */
class RestApiClient {
  private axiosInstance: AxiosInstance

  constructor(baseURL: string) {
    this.axiosInstance = axios.create({
      baseURL,
      timeout: 10000,
      // withCredentials は設定しない（同一オリジン前提のため不要）。
      // 開発環境は vue.config.js の devServer.proxy で同一オリジンを実現。
      // 本番環境は Nginx リバースプロキシで同一オリジンを実現すること。
      // ※ SameSite=Lax の Cookie はクロスオリジン XHR では送信されないため、
      //   VUE_APP_API_BASE_URL に絶対 URL を設定して proxy をバイパスすることは禁止。
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Service-Name': 'front'
      }
    })

    this.setupInterceptors()
  }

  /**
   * リクエスト・レスポンスインターセプターの設定
   */
  private setupInterceptors(): void {
    // リクエストインターセプター（Bearerトークン追加 & ロギング）
    this.axiosInstance.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        // Authorization: Bearer ヘッダーを追加
        const token = tokenProvider ? tokenProvider() : null
        if (token) {
          config.headers['Authorization'] = `Bearer ${token}`
        }

        console.log('API Request:', config.method?.toUpperCase(), (config.baseURL || '') + (config.url || ''))
        return config
      },
      (error) => {
        console.error('API Request Error:', error)
        return Promise.reject(error)
      }
    )

    // レスポンスインターセプター（エラーハンドリング）
    this.axiosInstance.interceptors.response.use(
      (response: AxiosResponse) => {
        console.log('API Response:', response.status, response.config.url)
        return response
      },
      (error) => {
        console.error('API Response Error:', error.response?.status, error.config?.url, error.message)

        // /auth/login・/auth/logout・/auth/refresh は401を自前でハンドリングするため除外
        const isAuthEndpoint = ['/auth/login', '/auth/logout', '/auth/refresh'].some(
          path => error.config?.url?.includes(path)
        )
        if (error.response?.status === 401 && !isAuthEndpoint) {
          console.warn('Unauthorized access, redirecting to login...')
          if (unauthorizedHandler) {
            unauthorizedHandler()
          } else {
            window.location.href = '/login'
          }
        }

        return Promise.reject(error)
      }
    )
  }

  /**
   * GET リクエスト
   */
  async get<T = ApiResponse>(endpoint: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.get<T>(endpoint, config)
  }

  /**
   * POST リクエスト
   */
  async post<T = ApiResponse>(endpoint: string, data?: RequestBody, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.post<T>(endpoint, data, config)
  }

  /**
   * PUT リクエスト
   */
  async put<T = ApiResponse>(endpoint: string, data?: RequestBody, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.put<T>(endpoint, data, config)
  }

  /**
   * PATCH リクエスト
   */
  async patch<T = ApiResponse>(endpoint: string, data?: RequestBody, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.patch<T>(endpoint, data, config)
  }

  /**
   * DELETE リクエスト
   */
  async delete<T = ApiResponse>(endpoint: string, config?: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.delete<T>(endpoint, config)
  }

  /**
   * 汎用リクエストメソッド（axios互換）
   */
  async request<T = ApiResponse>(config: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.request<T>(config)
  }

  /**
   * RESTful リソース操作のヘルパーメソッド
   */

  /**
   * リソースの一覧取得
   */
  async list<T = ResponseData>(resource: string, params?: QueryParams): Promise<AxiosResponse<T[]>> {
    return this.get<T[]>(`/${resource}`, { params })
  }

  /**
   * リソースの詳細取得
   */
  async show<T = ResponseData>(resource: string, id: string | number): Promise<AxiosResponse<T>> {
    return this.get<T>(`/${resource}/${id}`)
  }

  /**
   * リソースの作成
   */
  async create<T = ResponseData>(resource: string, data: RequestBody): Promise<AxiosResponse<T>> {
    return this.post<T>(`/${resource}`, data)
  }

  /**
   * リソースの更新（PUT）
   */
  async update<T = ResponseData>(resource: string, id: string | number, data: RequestBody): Promise<AxiosResponse<T>> {
    return this.put<T>(`/${resource}/${id}`, data)
  }

  /**
   * リソースの部分更新（PATCH）
   */
  async partialUpdate<T = ResponseData>(resource: string, id: string | number, data: RequestBody): Promise<AxiosResponse<T>> {
    return this.patch<T>(`/${resource}/${id}`, data)
  }

  /**
   * リソースの削除
   */
  async destroy<T = ResponseData>(resource: string, id: string | number): Promise<AxiosResponse<T>> {
    return this.delete<T>(`/${resource}/${id}`)
  }

  /**
   * axiosインスタンスへの直接アクセス（高度な使用）
   */
  get axios(): AxiosInstance {
    return this.axiosInstance
  }
}

const apiClient = new RestApiClient(API_BASE_URL)
export default apiClient
