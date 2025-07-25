import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import apiClient from '../api/axios.config';
import { updateMyProfile } from '../api/user';
import { getMyFavoriteIds, addFavorite, removeFavorite } from '../api/favorite'; // 【新增】

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token'));
    const user = ref(JSON.parse(localStorage.getItem('user')));
    const favoriteIds = ref(new Set()); // 【新增】使用 Set 存储收藏的商品ID，查询效率高


    // 这个计算属性用于判断用户是否已登录
    const isAuthenticated = computed(() => !!token.value);

    // 设置认证信息（token和user），并存入localStorage
    function setAuth({ token: newToken, user: newUserInfo }) {
        token.value = newToken;
        user.value = newUserInfo;
        localStorage.setItem('token', newToken);
        localStorage.setItem('user', JSON.stringify(newUserInfo));
    }

    // 清除认证信息
    function logout() {
        token.value = null;
        user.value = null;
        favoriteIds.value = new Set(); // 登出时清空收藏
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }
    
    // 登录
    async function login(credentials) {
      try {
        const payload = { ...credentials };
        const response = await apiClient.post('/users/authenticate', payload);
        
        // 【最终修正】从API响应中获取完整的用户信息，并直接使用
        const { token: apiToken, ...userInfo } = response.data.data;

        setAuth({ token: apiToken, user: userInfo });
        await fetchFavoriteIds();
        return true;
      } catch (error) {
        return false;
      }
    }

    // 注册
    async function register(data) {
        const payload = { ...data };
        return await apiClient.post('/users/register', payload);
    }
    
    // 更新个人资料
   async function updateUserProfile(profileData) {
        try {
            const response = await updateMyProfile(profileData);
            const updatedUser = response.data.data;
            // 更新本地存储的用户信息，确保包含了所有字段
            const currentUserInfo = { 
                nickname: updatedUser.nickname, 
                id: updatedUser.id, 
                avatar: updatedUser.avatar ,
                bio: updatedUser.bio

            };
            setAuth({ token: token.value, user: currentUserInfo });
            return true;
        } catch (error) {
            console.error("更新个人信息失败:", error);
            return false;
        }
    }

    // 获取并设置用户收藏的商品ID列表
    async function fetchFavoriteIds() {
        if (!isAuthenticated.value) return;
        try {
            const response = await getMyFavoriteIds();
            favoriteIds.value = new Set(response.data.data);
        } catch (error) {
            console.error("获取收藏列表失败:", error);
            favoriteIds.value = new Set();
        }
    }

    // 添加到收藏
    async function addToFavorites(productId) {
        try {
            await addFavorite(productId);
            favoriteIds.value.add(String(productId));
        } catch (error) {
            console.error("添加收藏失败:", error);
        }
    }

    // 从收藏中移除
    async function removeFromFavorites(productId) {
        try {
            await removeFavorite(productId);
            favoriteIds.value.delete(String(productId));
        } catch (error) {
            console.error("取消收藏失败:", error);
        }
    }

    // 【新增】一个专门用于更新本地用户信息的 action
    async function updateUserProfile(profileData) {
        try {
            const response = await updateMyProfile(profileData);
            const updatedUser = response.data.data;
            
            // 更新Pinia和localStorage中的用户信息
            const currentUserInfo = { 
                ...user.value, // 保留旧信息，如id, username等
                nickname: updatedUser.nickname, 
                avatar: updatedUser.avatar,
                bio: updatedUser.bio
            };
            setAuth({ token: token.value, user: currentUserInfo });
            return true;
        } catch (error) {
            console.error("更新个人信息失败:", error);
            return false;
        }
    }
    return { 
        token, user, isAuthenticated, setAuth, logout, login, register, updateUserProfile,
        favoriteIds, fetchFavoriteIds, addToFavorites, removeFromFavorites
    };
});