import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { AuthUtils } from '@/utils/auth'

// コンポーネント
import HomePage from '../pages/HomePage.vue'
import LoginPage from '../pages/LoginPage.vue'
import SignupPage from '../pages/SignupPage.vue'
import ProfilePage from '../pages/ProfilePage.vue'
import SupportInfoPage from '../pages/SupportInfoPage.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'HomePage',
    component: HomePage
  },
  {
    path: '/login',
    name: 'LoginPage',
    component: LoginPage
  },
  {
    path: '/signup',
    name: 'SignupPage',
    component: SignupPage
  },
  {
    path: '/profile',
    name: 'ProfilePage',
    component: ProfilePage,
    meta: { requiresAuth: true }
  },
  {
    path: '/support_info',
    name: 'SupportInfoPage',
    component: SupportInfoPage
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// ルートガード
router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta?.requiresAuth) && !AuthUtils.isLoggedIn()) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router