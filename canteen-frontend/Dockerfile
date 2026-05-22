# ============================================
# 校园食堂前端 Dockerfile
# 多阶段构建：Node编译 + Nginx运行
# ============================================

# ---- 阶段1: 编译 ----
FROM node:18-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build

# ---- 阶段2: Nginx运行 ----
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
