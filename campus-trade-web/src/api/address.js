import apiClient from './axios.config';

// 【最终修正】移除了所有请求路径中多余的 /api 前缀

export const getAddresses = () => {
    return apiClient.get('/me/addresses');
};

export const addAddress = (data) => {
    return apiClient.post('/me/addresses', data);
};

export const updateAddress = (id, data) => {
    return apiClient.put(`/me/addresses/${id}`, data);
};

export const deleteAddress = (id) => {
    return apiClient.delete(`/me/addresses/${id}`);
};

export const setDefaultAddress = (id) => {
    return apiClient.post(`/me/addresses/${id}/set-default`);
};
