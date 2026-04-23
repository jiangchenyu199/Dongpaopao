import { createRouter, createWebHistory } from 'vue-router'

import DashboardView from '../views/DashboardView.vue'
import OrdersView from '../views/OrdersView.vue'
import DisputesView from '../views/DisputesView.vue'
import UsersView from '../views/UsersView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/dashboard',
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
    },
    {
      path: '/orders',
      name: 'orders',
      component: OrdersView,
    },
    {
      path: '/disputes',
      name: 'disputes',
      component: DisputesView,
    },
    {
      path: '/users',
      name: 'users',
      component: UsersView,
    },
  ],
})

export default router
