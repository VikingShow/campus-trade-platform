import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import AdminLayout from '../layout/index.vue';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
    meta: { guest: true }
  },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '仪表盘', requiresAuth: true }
      },
      {
        path: 'user-management',
        name: 'UserManagement',
        component: () => import('../views/user/index.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: 'product-management',
        name: 'ProductManagement',
        component: () => import('../views/product/index.vue'),
        meta: { title: '商品管理', requiresAuth: true }
      },
      {
        path: 'order-management',
        name: 'OrderManagement',
        component: () => import('../views/order/index.vue'),
        meta: { title: '订单管理', requiresAuth: true }
      },
      {
        path: 'location-management',
        name: 'LocationManagement',
        component: () => import('../views/location/index.vue'),
        meta: { title: '交易地点管理', requiresAuth: true }
      }
    ]
  }
];

// ... router 创建和 beforeEach 守卫保持不变 ...
const router = createRouter({
  history: createWebHistory(),
  routes
});
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.isAdmin) {
    next({ name: 'Login' });
  } else if (to.meta.guest && authStore.isAdmin) {
    next({ name: 'Dashboard' });
  } else {
    next();
  }
});
export default router;