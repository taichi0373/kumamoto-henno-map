import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'

// Vue CLI用の環境変数
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL || '/benefit-map/api';

/**
 * 401 Unauthorized 発生時に呼び出されるコールバックの型
 */
type UnauthorizedHandler = () => void

/** 401ハンドラー（App.vue のセットアップ時に登録される） */
let unauthorizedHandler: UnauthorizedHandler | null = null

/** CSRF トークンキャッシュ */
let csrfToken: string | null = null

/**
 * 401 Unauthorized 発生時のコールバックを登録する
 * <p>
 * api.ts とストアの循環依存を避けるため、ストアへの直接依存を持たず
 * コールバック注入でハンドリングを委譲する。
 * App.vue の setup() トップレベルで呼び出すこと。
 * </p>
 * @param handler ログアウト処理とリダイレクトを行うコールバック
 */
export function setUnauthorizedHandler(handler: UnauthorizedHandler): void {
  unauthorizedHandler = handler
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
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Service-Name': 'front'
      }
    })

    this.setupInterceptors()
  }

  /**
   * CSRF トークンを取得してキャッシュする
   * @returns CSRF トークン
   */
  private async fetchCsrfToken(): Promise<string> {
    if (csrfToken) {
      return csrfToken
    }
    try {
      const response = await this.axiosInstance.get('/auth/csrf')
      const token = response.data.data
      if (!token || typeof token !== 'string') {
        throw new Error('Invalid CSRF token received from server')
      }
      csrfToken = token
      return csrfToken
    } catch (error) {
      console.error('Failed to fetch CSRF token:', error)
      throw error
    }
  }

  /**
   * CSRF トークンキャッシュをクリア
   */
  private clearCsrfToken(): void {
    csrfToken = null
  }

  /**
   * リクエスト・レスポンスインターセプターの設定
   */
  private setupInterceptors(): void {
    // リクエストインターセプター（CSRFトークン追加 & ロギング）
    this.axiosInstance.interceptors.request.use(
      async (config: InternalAxiosRequestConfig) => {
        // 状態変更系リクエストにCSRFトークンを追加
        if (['post', 'put', 'patch', 'delete'].includes(config.method?.toLowerCase() || '')) {
          // 認証不要エンドポイントは除外（SecurityConfig の ignoringRequestMatchers と一致）
          const skipCsrfPaths = ['/auth/', '/users/signup']
          const needsCsrf = !skipCsrfPaths.some(path => config.url?.includes(path))
          
          if (needsCsrf) {
            try {
              const token = await this.fetchCsrfToken()
              config.headers['X-XSRF-TOKEN'] = token
            } catch (error) {
              console.error('Failed to add CSRF token:', error)
              // CSRF トークン取得失敗時はリクエストを続行（サーバーエラーで判断）
            }
          }
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
        
        if (error.response?.status === 401 && !error.config?.url?.includes('/auth/login') && !error.config?.url?.includes('/auth/logout')) {
          console.warn('Unauthorized access, redirecting to login...')
          this.clearCsrfToken() // 認証エラー時はCSRFトークンもクリア
          if (unauthorizedHandler) {
            unauthorizedHandler()
          } else {
            // ハンドラー未登録時のフォールバック
            window.location.href = '/login'
          }
        }
        
        // CSRF トークンエラー時はキャッシュクリア
        // バックエンドが CSRF 専用メッセージを返した場合（primary）、
        // またはCSRF保護対象の状態変更系リクエストが403を受けた場合（safety net）にクリアする
        if (error.response?.status === 403) {
          const isCsrfMessage = error.response?.data?.message?.includes('CSRF')
          const isStateMutating = ['post', 'put', 'patch', 'delete'].includes(
            error.config?.method?.toLowerCase() || ''
          )
          const skipCsrfPaths = ['/auth/', '/users/signup']
          const isCsrfRequired = !skipCsrfPaths.some(path => error.config?.url?.includes(path))

          if (isCsrfMessage || (isStateMutating && isCsrfRequired)) {
            console.warn('CSRF token error or state-mutating 403, clearing token cache')
            this.clearCsrfToken()
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