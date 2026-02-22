const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  
  // 開発サーバーの設定
  devServer: {
    port: 3000, // ポートを3000に設定
    proxy: {
      // Spring Bootバックエンドへのプロキシ設定（ローカル開発用）
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      // 既存のPHPバックエンドとの互換性のため（移行期間中）
      '/navi_project': {
        target: 'http://localhost', // XAMPPやMAMPのポートに合わせて調整
        changeOrigin: true,
        secure: false
      }
    }
  },
  
  // 本番ビルドの出力ディレクトリ
  outputDir: '../../dist',
  
  // 公開パスの設定
  publicPath: process.env.NODE_ENV === 'production' ? '/navi_project/dist/' : '/',
  
  // CSS関連の設定
  css: {
    extract: process.env.NODE_ENV === 'production',
    loaderOptions: {
      scss: {
        additionalData: `@use "@/assets/scss/variable" as *;`
      }
    }
  },
})
