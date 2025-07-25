import axios from 'axios';
import { useAuthStore } from '../stores/authStore';
import { ElMessage } from 'element-plus';
// 【最终修正】不再导入 router，打破循环依赖
// import router from '../router'; 

const apiClient = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' }
});

apiClient.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    if (authStore.token) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

apiClient.interceptors.response.use(
  (response) => {
    if (response.data && response.data.code && response.data.code !== 200) {
      ElMessage.error(response.data.msg || '操作失败');
      return Promise.reject(new Error(response.data.msg || 'Error'));
    }
    return response;
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      if (status === 401 || status === 403) {
        const authStore = useAuthStore();
        // 【最终修正】拦截器只负责调用 logout，不再负责导航
        authStore.logout(); 
        ElMessage.error(data.msg || '认证失败或无权限访问，请重新登录');
      } else {
        ElMessage.error(data.msg || '服务器发生错误');
      }
    } else {
        ElMessage.error('网络连接失败，请稍后重试');
    }
    return Promise.reject(error);
  }
);

export default apiClient;