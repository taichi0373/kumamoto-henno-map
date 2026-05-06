import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { checkAdminRouteAccess } from './adminGuard'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'HomePage',
    component: () => import('../pages/HomePage.vue')
  },
  {
    path: '/login',
    name: 'LoginPage',
    component: () => import('../pages/LoginPage.vue')
  },
  {
    path: '/signup',
    name: 'SignupPage',
    component: () => import('../pages/SignupPage.vue')
  },
  {
    path: '/profile',
    name: 'ProfilePage',
    component: () => import('../pages/ProfilePage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/support_info',
    name: 'SupportInfoPage',
    component: () => import('../pages/SupportInfoPage.vue')
  },
  {
    path: '/change-password',
    name: 'ChangePasswordPage',
    component: () => import('../pages/ChangePasswordPage.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPasswordPage',
    component: () => import('../pages/ForgotPasswordPage.vue')
  },
  {
    path: '/reset-password',
    name: 'ResetPasswordPage',
    component: () => import('../pages/ResetPasswordPage.vue')
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { requiresAdmin: true, requiresAuth: true },
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
  const requiresAuth = to.matched.some(record => record.meta?.requiresAuth)
  const requiresAdmin = to.matched.some(record => record.meta?.requiresAdmin)

  // 認証チェックを先に行う（requiresAdmin も認証が前提のため両方を対象にする）
  if ((requiresAuth || requiresAdmin) && !auth.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  // 管理者チェック
  const adminRedirect = checkAdminRouteAccess(requiresAdmin, auth.isAdmin)
  if (adminRedirect) {
    next({ path: adminRedirect })
  } else {
    next()
  }
})

export default router