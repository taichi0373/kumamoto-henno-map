import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { AuthUtils } from '@/utils/auth'

// コンポーネント
import Home from '../pages/Home.vue'
import Login from '../pages/Login.vue'
import Register from '../pages/Register.vue'
import Profile from '../pages/Profile.vue'
import SupportInfo from '../pages/SupportInfo.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/support_info',
    name: 'SupportInfo',
    component: SupportInfo
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