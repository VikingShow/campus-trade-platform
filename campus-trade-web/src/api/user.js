import apiClient from './axios.config';

export const getUserInfo = (userId) => {
    return apiClient.get(`/users/${userId}/profile`);
};

export const getUserRatings = (userId) => {
    return apiClient.get(`/api/users/${userId}/ratings`);
};

export const updateMyProfile = (data) => {
    return apiClient.put('/api/me/profile', data);
};