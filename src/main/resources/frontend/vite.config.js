import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 开发时: npm run dev (5173, 代理到 8080)
// 构建时: npm run build (产物输出到 src/main/resources/static, 由 Spring Boot 托管)
export default defineConfig({
  plugins: [vue()],
  base: './',
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
      '/upload': 'http://localhost:8080'
    }
  },
  build: {
    outDir: '../static',
    emptyOutDir: true
  }
})
