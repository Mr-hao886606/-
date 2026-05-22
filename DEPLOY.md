# 🚀 云端部署指南

## 一、域名申请（约5分钟）

### 推荐平台
| 平台 | 地址 | 便宜后缀 |
|------|------|----------|
| 阿里云万网 | https://wanwang.aliyun.com | .top 首年¥1 |
| 腾讯云 | https://dnspod.cloud.tencent.com | .xyz 首年¥6 |
| 华为云 | https://www.huaweicloud.com | .site 首年¥5 |

### 步骤
1. 注册/登录 → 搜索想要的域名 → 加入购物车 → 实名认证 → 付费
2. 域名到手后，在控制台添加 **A记录**，指向你的服务器IP

---

## 二、服务器购买（约10分钟）

### 推荐：阿里云/腾讯云轻量应用服务器
- 学生优惠约 **¥9.9/月**（1核2G足够）
- 选 CentOS 7.9 或 Ubuntu 22.04
- 开放端口：`80`（前端）、`8080`（后端）、`3306`（数据库）或只开 `80`

---

## 三、Docker 一键部署（约15分钟）

> 先确保服务器已安装 Docker 和 Docker Compose

### 1. 安装 Docker
```bash
# CentOS
yum install -y docker docker-compose
systemctl start docker

# Ubuntu
apt update && apt install -y docker.io docker-compose
```

### 2. 拉取代码
```bash
git clone https://github.com/Mr-hao886606/-.git canteen
cd canteen
```

### 3. 修改配置
编辑 `docker-compose.yml`，将 `your_password_here` 改为你的MySQL密码（两处都要改）

### 4. 一键启动
```bash
docker-compose up -d
```

### 5. 验证
```bash
# 查看运行状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 6. 访问
- 浏览器打开：`http://你的服务器IP`
- 管理员：`admin` / `admin123`
- 普通用户：`user1` / `123456`

---

## 四、免费方案（零成本）

如果不想买服务器，可以用免费平台：

| 服务 | 平台 | 部署内容 |
|------|------|----------|
| 前端 | Vercel | `npm run build` 后的 dist 目录 |
| 后端 | Railway | Docker 或 JAR 包 |
| 数据库 | PlanetScale | MySQL 8.0 |

---

## 五、HTTPS 配置（可选）

```bash
# 安装 certbot
apt install -y certbot python3-certbot-nginx

# 申请SSL证书
certbot --nginx -d your-domain.com
```
