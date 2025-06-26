import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import apiClient from '../api/axios.config';

// JWT解码函数
function decodeJwt(token) {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch (e) {
    console.error('Error decoding JWT', e);
    return null;
  }
}

export const useAuthStore = defineStore('adminAuth', () => {
    const token = ref(localStorage.getItem('adminToken'));
    const user = ref(JSON.parse(localStorage.getItem('adminUser')));
    const roles = ref(JSON.parse(localStorage.getItem('adminRoles')) || []);

    const isAuthenticated = computed(() => !!token.value);
    const isAdmin = computed(() => roles.value.includes('ROLE_ADMIN'));

    function setAuth({ token: newToken, user: newUserInfo }) {
        token.value = newToken;
        user.value = newUserInfo;
        
        const decodedToken = decodeJwt(newToken);
        if (decodedToken && decodedToken.roles) {
          roles.value = decodedToken.roles.split(',');
        }

        localStorage.setItem('adminToken', newToken);
        localStorage.setItem('adminUser', JSON.stringify(newUserInfo));
        localStorage.setItem('adminRoles', JSON.stringify(roles.value));
    }

     function logout() {
        token.value = null;
        user.value = null;
        roles.value = [];
        localStorage.removeItem('adminToken');
        localStorage.removeItem('adminUser');
        localStorage.removeItem('adminRoles');
    }
    
    async function login(credentials) {
      try {
        const payload = { ...credentials };
        const response = await apiClient.post('/users/authenticate', payload);
        
        const { token: apiToken, nickname, id } = response.data.data;
        const userInfo = { nickname, id };

        const decodedToken = decodeJwt(apiToken);
        if (!decodedToken || !decodedToken.roles || !decodedToken.roles.includes('ROLE_ADMIN')) {
          throw new Error('非管理员用户，禁止登录');
        }

        setAuth({ token: apiToken, user: userInfo });
        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    }

    return { token, user, isAuthenticated, isAdmin, setAuth, logout, login };
});