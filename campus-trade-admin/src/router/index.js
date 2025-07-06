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
    redirect: { name: 'Dashboard' },
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
      },
      {
        path: 'delivery-management',
        name: 'DeliveryManagement',
        component: () => import('../views/delivery/index.vue'),
        meta: { title: '配送管理', requiresAuth: true }
      }
    ]
  }
];

const router = createRouter({
  // 使用 import.meta.env.BASE_URL 来自动获取 Vite 配置中的 base 路径
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.isAdmin) {
    // 明确使用命名路由进行跳转，并传递重定向参数
    next({ name: 'Login', query: { redirect: to.fullPath } });
  } else if (to.meta.guest && authStore.isAdmin) {
    next({ name: 'Dashboard' });
  } else {
    next();
  }
});

// 【关键修正】将 router 实例导出，以便其他文件可以导入和使用它
export default router;