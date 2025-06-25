import apiClient from './axios.config';

export const getMyPurchases = () => {
  return apiClient.get(`/orders/my-purchases`);
};

export const getMySales = () => {
  return apiClient.get(`/orders/my-sales`);
};

export const updateOrderStatus = (orderId, status) => {
    return apiClient.put(`/orders/${orderId}/status`, { status });
};

export const createOrder = (data) => {
    return apiClient.post('/orders', data);
};