import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import Home from '../views/Home.vue';
import Login from '../views/Login.vue';

const routes = [
    { path: '/', name: 'Home', component: Home },
    { path: '/login', name: 'Login', component: Login, meta: { guest: true } },
    { path: '/register', name: 'Register', component: () => import('../views/Register.vue'), meta: { guest: true } },
    { path: '/product/:id', name: 'ProductDetail', component: () => import('../views/ProductDetail.vue'), props: true },
    { path: '/publish', name: 'PublishProduct', component: () => import('../views/PublishProduct.vue'), meta: { requiresAuth: true } },
    { path: '/edit-product/:id', name: 'EditProduct', component: () => import('../views/PublishProduct.vue'), props: true, meta: { requiresAuth: true } },
    { path: '/confirm-order/:productId', name: 'OrderConfirmation', component: () => import('../views/OrderConfirmation.vue'), props: true, meta: { requiresAuth: true } },
    {
      path: '/dashboard',
      component: () => import('../views/Dashboard.vue'),
      redirect: '/dashboard/purchases',
      meta: { requiresAuth: true },
      children: [
        { path: 'purchases', name: 'MyPurchases', component: () => import('../components/MyPurchases.vue') },
        { path: 'sales', name: 'MySales', component: () => import('../components/MySales.vue') },
        { path: 'profile', name: 'UserProfile', component: () => import('../components/UserProfile.vue') },
        // 【关键修正】这里是我的收藏页面的正确路由定义
        { path: 'favorites', name: 'MyFavorites', component: () => import('../components/MyFavorites.vue'), meta: { title: '我的收藏' } }
      ]
    },
    { path: '/user/:id', name: 'UserProfilePage', component: () => import('../views/UserProfilePage.vue'), props: true },
    {
        path: '/messages',
        name: 'Messages',
        component: () => import('../views/messages/index.vue'),
        meta: { requiresAuth: true },
        children: [
            {
                path: ':otherUserId',
                name: 'ChatWindow',
                component: () => import('../views/messages/ChatWindow.vue'),
                props: true
            }
        ]
    }
];

const router = createRouter({ 
    history: createWebHistory(), 
    routes 
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next({ name: 'Login', query: { redirect: to.fullPath } });
    } else if (to.meta.guest && authStore.isAuthenticated) {
        next({ name: 'Home' });
    } else {
        next();
    }
});

export default router;