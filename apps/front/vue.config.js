const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,

  chainWebpack: (config) => {
    // maplibre-gl v5はESMバンドル済みファイルのため、Babelによる再トランスパイルを除外する
    config.module.rule('js').exclude.add(/node_modules[\\/]maplibre-gl/)
  },
  
  // 開発サーバーの設定
  devServer: {
    port: 3000, // ポートを3000に設定
    proxy: {
      // Spring Bootバックエンドへのプロキシ設定（ローカル開発用）
      '/benefit-map/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/tile-proxy': {
        target: 'https://tile.openstreetmap.jp',
        changeOrigin: true,
        pathRewrite: { '^/tile-proxy': '' },
        secure: true
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
