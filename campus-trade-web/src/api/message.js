import apiClient from './axios.config';

export const sendMessage = (data) => {
  return apiClient.post('/api/messages', data);
};

export const getMessageHistory = (otherUserId) => {
  return apiClient.get(`/api/messages/history/${otherUserId}`);
};

// 【新增】获取会话列表的API函数
export const getConversations = () => {
  return apiClient.get('/api/messages/conversations');
};