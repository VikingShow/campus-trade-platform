import apiClient from './axios.config';

export const getProducts = (params) => {
  return apiClient.get('/products', { params });
};

export const getProductById = (id) => {
  return apiClient.get(`/products/${id}`);
};

export const createProduct = (data) => {
  return apiClient.post('/products', data);
};

export const updateProduct = (id, data) => {
  return apiClient.put(`/products/${id}`, data);
};

export const updateProductStatus = (id, status) => {
  return apiClient.put(`/products/${id}/status`, { status });
};

// 【新增】获取商品推荐的API函数
export const getRecommendations = (productId) => {
    return apiClient.get(`/products/${productId}/recommendations`);
};