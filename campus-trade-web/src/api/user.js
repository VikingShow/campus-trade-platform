import apiClient from './axios.config';

export const getUserInfo = (userId) => {
    return apiClient.get(`/users/${userId}/profile`);
};

export const getUserRatings = (userId) => {
    return apiClient.get(`/api/users/${userId}/ratings`);
};

// 【新增】更新当前用户个人资料的API函数
export const updateMyProfile = (data) => {
    return apiClient.put('/api/me/profile', data);
};

export const getUserById = (id) => {
  return apiClient.get(`/users/${id}/profile`);
};