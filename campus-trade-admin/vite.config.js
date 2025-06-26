import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  // 指定应用的基础路径为 /admin/
  // 这将确保所有打包后的资源引用都是以 /admin/ 开头的相对路径
  base: '/admin/',
  server: {
    port: 8000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})