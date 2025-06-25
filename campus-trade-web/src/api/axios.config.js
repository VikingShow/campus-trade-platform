import axios from 'axios';
import { useAuthStore } from '../stores/authStore';
import { ElMessage } from 'element-plus';

const apiClient = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    if (authStore.token) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

apiClient.interceptors.response.use(
  (response) => {
    // 【关键修正】如果后端返回200，但业务码不是200，也当作一个错误来处理
    if (response.data && response.data.code && response.data.code !== 200) {
      ElMessage.error(response.data.msg || '操作失败');
      return Promise.reject(new Error(response.data.msg || 'Error'));
    }
    return response;
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      if (status === 401) {
        const authStore = useAuthStore();
        authStore.logout();
        window.location.href = '/login';
        ElMessage.error('登录已过期，请重新登录');
      } else {
        // 对于其他HTTP错误，也显示后端返回的消息
        ElMessage.error(data.msg || '服务器发生错误');
      }
    } else {
        ElMessage.error('网络连接失败，请稍后重试');
    }
    return Promise.reject(error);
  }
);

export default apiClient;