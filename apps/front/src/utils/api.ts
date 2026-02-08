import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'

// Vue CLI用の環境変数（VUE_APP_プレフィックスが必要）
const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8081/benefit-map/api';

/**
 * RESTful APIクライアント（axiosベース）
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
   * リクエスト・レスポンスインターセプターの設定
   */
  private setupInterceptors(): void {
    // リクエストインターセプター（API Key の追加）
    this.axiosInstance.interceptors.request.use(
      (config: InternalAxiosRequestConfig) => {
        const apiKey = localStorage.getItem('api_key')
        if (apiKey && config.headers) {
          config.headers['X-API-Key'] = apiKey
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
        
        if (error.response?.status === 401) {
          console.warn('Unauthorized access, redirecting to login...')
          localStorage.removeItem('auth_token')
          sessionStorage.clear()
          window.location.href = '/login'
        }
        return Promise.reject(error)
      }
    )
  }

  /**
   * GET リクエスト
   */
  async get<T = any>(endpoint: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.axiosInstance.get<T>(endpoint, config)
    return response.data
  }

  /**
   * POST リクエスト
   */
  async post<T = any>(endpoint: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.axiosInstance.post<T>(endpoint, data, config)
    return response.data
  }

  /**
   * PUT リクエスト
   */
  async put<T = any>(endpoint: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.axiosInstance.put<T>(endpoint, data, config)
    return response.data
  }

  /**
   * PATCH リクエスト
   */
  async patch<T = any>(endpoint: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.axiosInstance.patch<T>(endpoint, data, config)
    return response.data
  }

  /**
   * DELETE リクエスト
   */
  async delete<T = any>(endpoint: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.axiosInstance.delete<T>(endpoint, config)
    return response.data
  }

  /**
   * 汎用リクエストメソッド（axios互換）
   */
  async request<T = any>(config: AxiosRequestConfig): Promise<AxiosResponse<T>> {
    return this.axiosInstance.request<T>(config)
  }

  /**
   * RESTful リソース操作のヘルパーメソッド
   */
  
  /**
   * リソースの一覧取得
   */
  async list<T = any>(resource: string, params?: any): Promise<T[]> {
    return this.get<T[]>(`/${resource}`, { params })
  }

  /**
   * リソースの詳細取得
   */
  async show<T = any>(resource: string, id: string | number): Promise<T> {
    return this.get<T>(`/${resource}/${id}`)
  }

  /**
   * リソースの作成
   */
  async create<T = any>(resource: string, data: any): Promise<T> {
    return this.post<T>(`/${resource}`, data)
  }

  /**
   * リソースの更新（PUT）
   */
  async update<T = any>(resource: string, id: string | number, data: any): Promise<T> {
    return this.put<T>(`/${resource}/${id}`, data)
  }

  /**
   * リソースの部分更新（PATCH）
   */
  async partialUpdate<T = any>(resource: string, id: string | number, data: any): Promise<T> {
    return this.patch<T>(`/${resource}/${id}`, data)
  }

  /**
   * リソースの削除
   */
  async destroy<T = any>(resource: string, id: string | number): Promise<T> {
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