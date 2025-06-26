<template>
  <router-view/>
</template>

<script setup>
import { watch } from 'vue';
import { useAuthStore } from './stores/authStore';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

/**
 * 【最终修正】使用 watch 监听认证状态的变化。
 * 这是处理程序范围内状态变更（如自动登出）并执行导航的最佳实践。
 */
watch(
  () => authStore.isAuthenticated,
  (isAuthenticated, wasAuthenticated) => {
    // 如果用户之前是登录状态，而现在变成了非登录状态，
    // 这意味着他们被登出（可能是因为Token过期被拦截器处理）。
    // 此时，我们执行页面跳转。
    if (wasAuthenticated && !isAuthenticated) {
      router.push('/login');
    }
  },
  { immediate: false } // 我们不希望在组件加载时立即执行
);
</script>

<style>
body, #app {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  background-color: #f0f2f5;
}
</style>