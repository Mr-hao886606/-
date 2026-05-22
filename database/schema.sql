-- ====================================
-- 校园食堂网站 数据库设计
-- ====================================

CREATE DATABASE IF NOT EXISTS canteen DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE canteen;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `realname` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色: user-普通用户, admin-管理员',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 默认管理员账号: admin / admin123
INSERT INTO `user` (`username`, `password`, `realname`, `role`) VALUES ('admin', 'admin123', '系统管理员', 'admin');
-- 测试用户: user1 / 123456
INSERT INTO `user` (`username`, `password`, `realname`, `role`) VALUES ('user1', '123456', '小明', 'user');

-- ----------------------------
-- 2. 餐厅表
-- ----------------------------
DROP TABLE IF EXISTS `restaurant`;
CREATE TABLE `restaurant` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '餐厅ID',
  `name` VARCHAR(100) NOT NULL COMMENT '餐厅名称',
  `image` VARCHAR(255) DEFAULT NULL COMMENT '餐厅图片',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '餐厅地址',
  `contact_person` VARCHAR(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `license_image` VARCHAR(255) DEFAULT NULL COMMENT '资质证明',
  `description` TEXT DEFAULT NULL COMMENT '餐厅描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-停用, 1-营业中',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='餐厅表';

INSERT INTO `restaurant` (`name`, `address`, `contact_phone`, `description`) VALUES 
('第一食堂', '校园A区一楼', '13800001001', '主打中式快餐，品种丰富'),
('第二食堂', '校园B区二楼', '13800001002', '特色小吃、面食等'),
('清真餐厅', '校园C区一楼', '13800001003', '清真美食专区');

-- ----------------------------
-- 3. 美食分类表
-- ----------------------------
DROP TABLE IF EXISTS `food_category`;
CREATE TABLE `food_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='美食分类表';

INSERT INTO `food_category` (`name`, `sort_order`) VALUES 
('快餐', 1), ('面食', 2), ('盖浇饭', 3), ('小吃', 4), ('饮品', 5), ('清真', 6);

-- ----------------------------
-- 4. 菜品信息表
-- ----------------------------
DROP TABLE IF EXISTS `dish`;
CREATE TABLE `dish` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜品ID',
  `name` VARCHAR(100) NOT NULL COMMENT '菜品名称',
  `image` VARCHAR(255) DEFAULT NULL COMMENT '菜品图片',
  `category_id` BIGINT DEFAULT NULL COMMENT '美食分类ID',
  `taste` VARCHAR(50) DEFAULT NULL COMMENT '口味: 辣/不辣/微辣/酸甜等',
  `restaurant_id` BIGINT DEFAULT NULL COMMENT '所属餐厅ID',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `stock` INT DEFAULT 999 COMMENT '库存',
  `limit_per_order` INT DEFAULT 10 COMMENT '单次限购数量',
  `click_count` INT DEFAULT 0 COMMENT '点击次数',
  `sales_count` INT DEFAULT 0 COMMENT '销量',
  `description` TEXT DEFAULT NULL COMMENT '菜品描述',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_restaurant` (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品信息表';

INSERT INTO `dish` (`name`, `category_id`, `taste`, `restaurant_id`, `price`, `description`) VALUES 
('红烧肉盖饭', 3, '微辣', 1, 15.00, '精选五花肉，软糯入味'),
('宫保鸡丁', 1, '辣', 1, 12.00, '经典川味，花生酥脆'),
('牛肉拉面', 2, '不辣', 2, 13.00, '手工拉面，汤鲜味美'),
('麻辣香锅', 4, '辣', 2, 22.00, '自选食材，麻辣鲜香'),
('珍珠奶茶', 5, NULL, 2, 8.00, 'Q弹珍珠配浓郁奶茶'),
('兰州拉面', 6, '不辣', 3, 12.00, '正宗清真兰州拉面'),
('番茄鸡蛋盖饭', 3, '不辣', 1, 10.00, '家常美味，酸甜可口'),
('炸鸡腿', 4, '微辣', 1, 6.00, '外酥里嫩大鸡腿'),
('酸菜鱼', 1, '辣', 2, 28.00, '酸爽鲜美的鱼片'),
('绿豆汤', 5, NULL, 1, 3.00, '夏日消暑佳品');

-- ----------------------------
-- 5. 购物车表
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ----------------------------
-- 6. 收货地址表
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `address_detail` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `is_default` TINYINT DEFAULT 0 COMMENT '是否默认: 0-否, 1-是',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ----------------------------
-- 7. 订单表
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单编号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `total_price` DECIMAL(10,2) NOT NULL COMMENT '总金额',
  `coupon_id` BIGINT DEFAULT NULL COMMENT '使用的优惠券ID',
  `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
  `actual_price` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `pay_type` VARCHAR(20) DEFAULT 'offline' COMMENT '支付方式: offline-线下支付, online-在线支付',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-待处理, accepted-已接单, preparing-制作中, completed-已完成, cancelled-已取消',
  `address_id` BIGINT DEFAULT NULL COMMENT '收货地址ID',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 8. 订单明细表
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
  `dish_name` VARCHAR(100) DEFAULT NULL COMMENT '菜品名称',
  `dish_image` VARCHAR(255) DEFAULT NULL COMMENT '菜品图片',
  `price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `quantity` INT NOT NULL COMMENT '数量',
  `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计',
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ----------------------------
-- 9. 优惠券表
-- ----------------------------
DROP TABLE IF EXISTS `coupon`;
CREATE TABLE `coupon` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `name` VARCHAR(100) NOT NULL COMMENT '优惠券名称',
  `type` VARCHAR(20) DEFAULT 'discount' COMMENT '类型: discount-满减, cash-代金券',
  `min_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '满额条件',
  `discount_amount` DECIMAL(10,2) NOT NULL COMMENT '优惠金额',
  `total_count` INT DEFAULT 100 COMMENT '发放总数',
  `remain_count` INT DEFAULT 100 COMMENT '剩余数量',
  `start_time` DATETIME DEFAULT NULL COMMENT '生效时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '过期时间',
  `restaurant_id` BIGINT DEFAULT NULL COMMENT '适用餐厅ID(为空表示通用)',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-停用, 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

INSERT INTO `coupon` (`name`, `min_amount`, `discount_amount`, `start_time`, `end_time`, `remark`) VALUES 
('新用户满20减5', 20.00, 5.00, '2024-01-01 00:00:00', '2026-12-31 23:59:59', '新用户专享'),
('满30减8', 30.00, 8.00, '2024-01-01 00:00:00', '2026-12-31 23:59:59', '每日限量');

-- ----------------------------
-- 10. 用户优惠券表（用户领取的优惠券）
-- ----------------------------
DROP TABLE IF EXISTS `user_coupon`;
CREATE TABLE `user_coupon` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `coupon_id` BIGINT NOT NULL COMMENT '优惠券ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已使用, 1-未使用, 2-已过期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- ----------------------------
-- 11. 收藏表
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_dish` (`user_id`, `dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ----------------------------
-- 12. 轮播图表
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `image` VARCHAR(255) NOT NULL COMMENT '图片URL',
  `link_url` VARCHAR(255) DEFAULT NULL COMMENT '链接地址',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ----------------------------
-- 13. 美食资讯表
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资讯ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `category` VARCHAR(50) DEFAULT '通知' COMMENT '分类: 通知/活动/健康贴士',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `content` TEXT DEFAULT NULL COMMENT '内容',
  `click_count` INT DEFAULT 0 COMMENT '点击次数',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-隐藏, 1-显示',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='美食资讯表';

INSERT INTO `news` (`title`, `category`, `content`) VALUES 
('第一食堂新品上线通知', '通知', '第一食堂本周推出多款新菜品：酸汤肥牛、麻辣拌面等，欢迎品尝！'),
('食堂夏季食品安全公告', '通知', '为保障夏季食品安全，各食堂已加强卫生管理，请广大师生放心就餐。'),
('每日饮食健康小贴士', '健康贴士', '建议每天摄入300-500克蔬菜，200-350克水果，保持均衡饮食。');

-- ----------------------------
-- 14. 菜品评价表
-- ----------------------------
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `dish_id` BIGINT NOT NULL COMMENT '菜品ID',
  `order_id` BIGINT DEFAULT NULL COMMENT '订单ID',
  `rating` TINYINT NOT NULL DEFAULT 5 COMMENT '评分: 1-5',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  PRIMARY KEY (`id`),
  KEY `idx_dish` (`dish_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品评价表';
