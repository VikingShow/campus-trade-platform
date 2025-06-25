import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 8000, // 我们为后台管理系统使用一个新端口 8000
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址保持不变
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})