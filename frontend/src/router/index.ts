import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/schedules',
    name: 'ScheduleSearch',
    component: () => import('../views/ScheduleSearch.vue')
  },
  {
    path: '/booking/:id',
    name: 'Booking',
    component: () => import('../views/Booking.vue')
  },
  {
    path: '/orders',
    name: 'MyOrders',
    component: () => import('../views/MyOrders.vue')
  },
  {
    path: '/trading',
    name: 'TradingPlatform',
    component: () => import('../views/TradingPlatform.vue')
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/AdminDashboard.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
