import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('../views/Orders.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'schools',
        name: 'Schools',
        component: () => import('../views/Schools.vue'),
        meta: { title: '学校管理' }
      },
      {
        path: 'order-types',
        name: 'OrderTypes',
        component: () => import('../views/OrderTypes.vue'),
        meta: { title: '订单类型' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('../views/Settings.vue'),
        meta: { title: '系统设置' }
      },
      {
        path: 'system/roles',
        name: 'Roles',
        component: () => import('../views/system/Roles.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'system/admins',
        name: 'Admins',
        component: () => import('../views/system/Admins.vue'),
        meta: { title: '后台账号管理' }
      },
      {
        path: 'operate/splash',
        name: 'Splash',
        component: () => import('../views/operate/Splash.vue'),
        meta: { title: '开屏设置' }
      },
      {
        path: 'operate/notices',
        name: 'Notices',
        component: () => import('../views/operate/Notices.vue'),
        meta: { title: '滚动通知' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta?.public) return next()
  const token = localStorage.getItem('admin_token')
  if (!token && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router
