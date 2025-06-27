import apiClient from './axios.config';

// 获取所有公开的交易地点
export const getPublicLocations = () => {
    return apiClient.get('/locations');
};
