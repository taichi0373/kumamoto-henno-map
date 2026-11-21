# ===== ビルドステージ =====
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
# VITE_API_BASE_URL は設定しない（同一オリジンの相対パス '/kumamoto-henno-map/api' を使用し、Ingress でバックエンドへ振り分ける）
RUN npm run build

# ===== 実行ステージ =====
FROM nginx:1.28-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY security-headers.conf /etc/nginx/snippets/security-headers.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
