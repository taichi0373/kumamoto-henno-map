import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: { '@': path.resolve(__dirname, 'src') },
  },
  server: {
    port: 3000,
    proxy: {
      // Spring Boot バックエンドへのプロキシ設定（ローカル開発用）
      // ブラウザから見ると localhost:3000 への同一オリジンリクエストになるため、
      // HttpOnly Cookie（SameSite=Lax）が正常に送受信される。
      // ※ このプロキシを経由せず直接 localhost:8081 を叩くと Cookie が送信されず
      //   リフレッシュトークンが機能しないため、VITE_API_BASE_URL に絶対 URL を設定しないこと。
      '/kumamoto-henno-map/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
      '/tile-proxy': {
        target: 'https://tile.openstreetmap.jp',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/tile-proxy/, ''),
      },
    },
  },
  build: {
    outDir: 'dist',
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/assets/scss/variable" as *;`,
      },
    },
  },
})
