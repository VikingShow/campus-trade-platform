import apiClient from './axios.config';

// 发送验证码到指定邮箱
export const sendVerificationCode = (email) => {
  return apiClient.post('/email/send-code', { email });
};