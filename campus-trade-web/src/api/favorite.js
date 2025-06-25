import apiClient from './axios.config';

// 添加收藏
export const addFavorite = (productId) => {
  return apiClient.post(`/api/products/${productId}/favorite`);
};

// 取消收藏
export const removeFavorite = (productId) => {
  return apiClient.delete(`/api/products/${productId}/favorite`);
};

// 获取当前用户收藏的所有商品ID
export const getMyFavoriteIds = () => {
  return apiClient.get('/api/me/favorites/ids');
};

// 获取当前用户收藏的所有商品详情
export const getMyFavorites = () => {
    return apiClient.get('/api/me/favorites');
};