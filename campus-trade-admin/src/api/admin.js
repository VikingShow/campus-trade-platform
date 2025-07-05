import apiClient from './axios.config';

// --- User Management ---
export const getAllUsers = (params) => {
    return apiClient.get('/admin/users', { params });
};
export const updateUserStatus = (id, status) => {
    return apiClient.put(`/admin/users/${id}/status`, { status });
};

// --- Product Management ---
export const getAllProductsAdmin = (params) => {
    return apiClient.get('/admin/products', { params });
};
export const updateProductStatusAdmin = (id, status) => {
    return apiClient.put(`/admin/products/${id}/status`, { status });
};

// --- Order Management ---
// 【修改】让 getAllOrdersAdmin 函数可以接收分页参数
export const getAllOrdersAdmin = (params) => {
    return apiClient.get('/admin/orders', { params });
};

// --- Location Management ---
export const getAllLocationsAdmin = () => { return apiClient.get('/admin/locations'); };
export const addLocationAdmin = (data) => { return apiClient.post('/admin/locations', data); };
export const updateLocationAdmin = (id, data) => { return apiClient.put(`/admin/locations/${id}`, data); };
export const deleteLocationAdmin = (id) => { return apiClient.delete(`/admin/locations/${id}`); };

// --- Dashboard Stats ---
export const getSummaryStats = () => {
    return apiClient.get('/admin/stats/summary');
};
export const getDailyRegistrationStats = () => {
    return apiClient.get('/admin/stats/daily-registrations');
};

export const createUserAdmin = (data) => {
    return apiClient.post('/admin/users', data);
};
export const updateUserAdmin = (id, data) => {
    return apiClient.put(`/admin/users/${id}`, data);
};
export const deleteUserAdmin = (id) => {
    return apiClient.delete(`/admin/users/${id}`);
};
