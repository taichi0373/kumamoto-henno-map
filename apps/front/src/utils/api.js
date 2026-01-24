import axios from 'axios'

// Vue CLI用の環境変数（VUE_APP_プレフィックスが必要）
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL || 
                    process.env.VUE_APP_BENEFIT_MAP_URL || 
                    'http://localhost:8000/api'

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'X-Service-Name': 'front'
  }
})

// リクエストインターセプター（JWT トークンの追加）
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token') || sessionStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // Kong Gateway用のサービス識別
    config.headers['X-Forwarded-For'] = 'front-service'
    console.log('API Request:', config.method?.toUpperCase(), config.baseURL + config.url)
    return config
  },
  (error) => {
    console.error('API Request Error:', error)
    return Promise.reject(error)
  }
)

// レスポンスインターセプター（エラーハンドリング）
apiClient.interceptors.response.use(
  (response) => {
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

export default apiClient