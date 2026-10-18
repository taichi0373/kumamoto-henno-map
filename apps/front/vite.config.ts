import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { VitePWA } from 'vite-plugin-pwa'
import path from 'path'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))

export default defineConfig({
  plugins: [
    vue(),
    VitePWA({
      registerType: 'autoUpdate',
      includeAssets: ['favicon.ico', 'icon-192x192.png', 'icon-512x512.png'],
      manifest: {
        name: 'くまもと自主返納特典マップ',
        short_name: '返納特典マップ',
        description: '熊本県の運転免許自主返納者向け特典情報を地図上で表示・検索するアプリ',
        theme_color: '#228b22',
        background_color: '#ffffff',
        display: 'standalone',
        start_url: '/',
        lang: 'ja',
        icons: [
          {
            src: '/icon-192x192.png',
            sizes: '192x192',
            type: 'image/png',
          },
          {
            src: '/icon-512x512.png',
            sizes: '512x512',
            type: 'image/png',
            purpose: 'any maskable',
          },
        ],
      },
      workbox: {
        // アプリケーションシェル（HTML/JS/CSS）をキャッシュ
        globPatterns: ['**/*.{js,css,html,ico,png,svg,woff2}'],
        // 地図タイルなど外部リソースはキャッシュしない
        navigateFallback: '/index.html',
        navigateFallbackDenylist: [/^\/kumamoto-henno-map\/api/, /^\/tile-proxy/],
      },
    }),
  ],
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
