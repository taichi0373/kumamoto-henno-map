export const AuthUtils = {
  login(user, token) {
    if (user) {
      sessionStorage.setItem('user', JSON.stringify(user))
      sessionStorage.setItem('username', user.username)
      localStorage.setItem('auth_token', token) // Kong Gateway用
    }
    if (token) {
      sessionStorage.setItem('token', token)
    }
    sessionStorage.setItem('isLoggedIn', 'true')
    sessionStorage.setItem('service', 'front') // サービス識別
  },
  
  isLoggedIn() {
    return sessionStorage.getItem('isLoggedIn') === 'true' && 
           (localStorage.getItem('auth_token') || sessionStorage.getItem('token'))
  },
  
  getUser() {
    const userStr = sessionStorage.getItem('user')
    return userStr ? JSON.parse(userStr) : null
  },
  getUserId() {
    const user = this.getUser()
    return user ? user.id : null
  },
  getToken() {
    return localStorage.getItem('auth_token') || sessionStorage.getItem('token')
  },
  
  logout() {
    sessionStorage.clear()
    localStorage.removeItem('auth_token')
  }
}