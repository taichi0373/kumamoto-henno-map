import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { useAuthStore } from '@/stores/auth'

// Vue CLI用の環境変数（VUE_APP_プレフィックスが必要）
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8081/benefit-map/api';

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
    // リクエストインターセプター（JWT Authorization ヘッダーの追加）
    this.axiosInstance.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        const auth = useAuthStore()
        const token = auth.token
        if (token && config.headers) {
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
        
        if (error.response?.status === 401 && !error.config?.url?.includes('/auth/login')) {
          console.warn('Unauthorized access, redirecting to login...')
          const auth = useAuthStore()
          auth.logout()
          window.location.href = '/login'
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