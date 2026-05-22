# 🍽️ 校园食堂网站

基于 **SpringBoot + Vue 3 + Element Plus + MySQL** 的校园食堂在线点餐系统。

## ✨ 功能特性

### 前台（用户端）
- 🏠 **首页**：轮播图、美食分类、热门菜品、美食资讯
- 🔍 **菜品浏览**：搜索、分类筛选、口味筛选、餐厅筛选
- 📋 **菜品详情**：查看详情、添加到购物车、收藏、评价
- 🛒 **购物车**：增删改查、一键下单
- 📦 **订单管理**：查看订单、取消订单、状态跟踪
- 👤 **个人中心**：修改资料、修改密码
- 📍 **收货地址**：增删改查、默认地址
- 🎫 **优惠券**：领取、使用
- ⭐ **收藏夹**：收藏/取消收藏菜品
- 📰 **美食资讯**：资讯列表、详情

### 后台（管理员端）
- 📊 **控制台**：数据统计概览
- 👥 **用户管理**：CRUD、搜索
- 🏪 **餐厅管理**：CRUD、上下架
- 🏷️ **分类管理**：美食分类CRUD
- 🍜 **菜品管理**：CRUD、上下架
- 📦 **订单管理**：查看、状态变更、删除
- 🎫 **优惠券管理**：CRUD
- 🖼️ **轮播图管理**：CRUD
- 📰 **资讯管理**：CRUD

## 🛠️ 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | SpringBoot 2.7 |
| ORM | MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.0 |
| 认证 | JWT Token |
| 前端框架 | Vue 3 (Composition API) |
| UI组件 | Element Plus |
| 构建工具 | Vite 5 |
| 路由 | Vue Router 4 |
| HTTP | Axios |

## 🚀 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+

### 1. 数据库初始化
```sql
-- 在 MySQL 中执行 database/schema.sql
-- 这将创建数据库 canteen 和所有表，并插入测试数据
mysql -u root -p < database/schema.sql
```

### 2. 启动后端
```bash
cd canteen-backend

# 修改 src/main/resources/application.yml 中的数据库连接信息
# spring.datasource.username=你的MySQL用户名
# spring.datasource.password=你的MySQL密码

mvn spring-boot:run
# 后端运行在 http://localhost:8080
```

### 3. 启动前端
```bash
cd canteen-frontend
npm install
npm run dev
# 前端运行在 http://localhost:3000
```

### 4. 访问系统
- **前台页面**：http://localhost:3000
- **后台管理**：http://localhost:3000/#/admin
- **测试账号**：
  - 管理员：`admin` / `admin123`
  - 普通用户：`user1` / `123456`

## 📁 项目结构

```
web/
├── database/
│   └── schema.sql              # 数据库建表+测试数据
├── canteen-backend/            # SpringBoot后端
│   ├── pom.xml
│   └── src/main/
│       ├── resources/
│       │   └── application.yml
│       └── java/com/canteen/
│           ├── common/         # 通用类
│           ├── config/         # 配置类
│           ├── controller/     # 控制器(13个)
│           ├── entity/         # 实体类(14个)
│           ├── mapper/         # 数据访问层
│           └── service/        # 业务逻辑层
└── canteen-frontend/           # Vue3前端
    ├── package.json
    ├── vite.config.js
    └── src/
        ├── api/                # API接口封装
        ├── router/             # 路由配置
        └── views/
            ├── layout/         # 布局组件
            ├── front/          # 前台页面(11个)
            └── admin/          # 后台页面(9个)
```

## 📝 API 接口

| 模块 | 前缀 | 说明 |
|------|------|------|
| 用户 | `/user` | 登录/注册/信息管理 |
| 菜品 | `/dish` | 菜品CRUD/搜索/热门 |
| 分类 | `/foodCategory` | 美食分类 |
| 餐厅 | `/restaurant` | 餐厅管理 |
| 购物车 | `/cart` | 购物车操作 |
| 订单 | `/order` | 下单/订单管理 |
| 优惠券 | `/coupon` | 领取/管理 |
| 地址 | `/address` | 收货地址 |
| 收藏 | `/favorite` | 收藏/取消 |
| 评价 | `/review` | 菜品评价 |
| 轮播图 | `/banner` | 轮播图管理 |
| 资讯 | `/news` | 美食资讯 |
| 文件 | `/file` | 文件上传 |
