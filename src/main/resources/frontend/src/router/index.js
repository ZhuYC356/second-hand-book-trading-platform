import { createRouter, createWebHashHistory } from 'vue-router'
import { getUser } from '../utils/auth'

const routes = [
  { path: '/login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: () => import('../layouts/FrontLayout.vue'),
    children: [
      { path: '', component: () => import('../views/front/Home.vue') },
      { path: 'my-books', component: () => import('../views/front/MyBooks.vue') },
      { path: 'my-orders', component: () => import('../views/front/MyOrders.vue') },
      { path: 'profile', component: () => import('../views/front/Profile.vue') }
    ]
  },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    children: [
      { path: '', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'users', component: () => import('../views/admin/Users.vue') },
      { path: 'books', component: () => import('../views/admin/BooksAdmin.vue') },
      { path: 'orders', component: () => import('../views/admin/OrdersAdmin.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.path === '/login') return true
  const user = getUser()
  if (!user) return '/login'
  if (to.path.startsWith('/admin') && user.role !== 'ADMIN') return '/'
  return true
})

export default router
