declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare module './router' {
  import { Router } from 'vue-router'
  const router: Router
  export default router
}

declare module './router/index' {
  import { Router } from 'vue-router'
  const router: Router
  export default router
}

declare module './router/index.js' {
  import { Router } from 'vue-router'
  const router: Router
  export default router
}

declare module '@/utils/auth' {
  interface User {
    id: string
    username: string
    [key: string]: unknown
  }
  
  export const AuthUtils: {
    login(user: User | null, token: string): void
    isLoggedIn(): boolean
    getUser(): User | null
    getUserId(): string | null
    getToken(): string | null
    logout(): void
  }
}

declare module './utils/auth' {
  interface User {
    id: string
    username: string
    [key: string]: unknown
  }
  
  export const AuthUtils: {
    login(user: User | null, token: string): void
    isLoggedIn(): boolean
    getUser(): User | null
    getUserId(): string | null
    getToken(): string | null
    logout(): void
  }
}