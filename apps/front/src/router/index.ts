import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { checkAdminRouteAccess } from './adminGuard'

// コンポーネント
import HomePage from '../pages/HomePage.vue'
import LoginPage from '../pages/LoginPage.vue'
import SignupPage from '../pages/SignupPage.vue'
import ProfilePage from '../pages/ProfilePage.vue'
import SupportInfoPage from '../pages/SupportInfoPage.vue'
import ChangePasswordPage from '../pages/ChangePasswordPage.vue'
import ForgotPasswordPage from '../pages/ForgotPasswordPage.vue'
import ResetPasswordPage from '../pages/ResetPasswordPage.vue'
import AdminLayout from '../layouts/AdminLayout.vue'

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
  },
  {
    path: '/change-password',
    name: 'ChangePasswordPage',
    component: ChangePasswordPage,
    meta: { requiresAuth: true }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPasswordPage',
    component: ForgotPasswordPage
  },
  {
    path: '/reset-password',
    name: 'ResetPasswordPage',
    component: ResetPasswordPage
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAdmin: true },
    children: [
      {
        path: 'benefits',
        name: 'AdminBenefitPage',
        component: () => import('@/pages/admin/AdminBenefitPage.vue')
      },
      {
        path: 'benefit-eligibilities',
        name: 'AdminBenefitEligibilityPage',
        component: () => import('@/pages/admin/AdminBenefitEligibilityPage.vue')
      },
      {
        path: 'benefit-categories',
        name: 'AdminBenefitCategoryPage',
        component: () => import('@/pages/admin/AdminBenefitCategoryPage.vue')
      },
      {
        path: 'municipalities',
        name: 'AdminMunicipalityPage',
        component: () => import('@/pages/admin/AdminMunicipalityPage.vue')
      },
      {
        path: 'agencies',
        name: 'AdminAgencyPage',
        component: () => import('@/pages/admin/AdminAgencyPage.vue')
      },
      {
        path: 'fare-discounts',
        name: 'AdminFareDiscountPage',
        component: () => import('@/pages/admin/AdminFareDiscountPage.vue')
      },
      {
        path: 'community-buses',
        name: 'AdminCommunityBusPage',
        component: () => import('@/pages/admin/AdminCommunityBusPage.vue')
      },
      {
        path: 'users',
        name: 'AdminUsersPage',
        component: () => import('@/pages/admin/AdminUsersPage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// ルートガード
router.beforeEach((to, from, next) => {
  const auth = useAuthStore()
  const requiresAdmin = to.matched.some(record => record.meta?.requiresAdmin)
  const adminRedirect = checkAdminRouteAccess(requiresAdmin, auth.isAdmin)
  if (adminRedirect) {
    next({ path: adminRedirect })
  } else if (to.matched.some(record => record.meta?.requiresAuth) && !auth.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router