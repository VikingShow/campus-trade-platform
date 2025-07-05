USE campus_trade;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `favorites`;
DROP TABLE IF EXISTS `messages`;
DROP TABLE IF EXISTS `ratings`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `meetup_location`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `user`;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `user` (
                        `id` BIGINT AUTO_INCREMENT,
                        `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名 (学号)',
                        `password` VARCHAR(255) NOT NULL COMMENT '密码 (加密存储)',
                        `nickname` VARCHAR(50) COMMENT '昵称',
                        `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色 (USER, ADMIN)',
                        `credit_score` INT NOT NULL DEFAULT 100 COMMENT '用户信誉分',
                        `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
                        `status` TINYINT DEFAULT 1 COMMENT '账户状态 (1:正常 0:禁用)',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `category` (
                            `id` INT AUTO_INCREMENT,
                            `name` VARCHAR(50) NOT NULL,
                            `sort_order` INT DEFAULT 0,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

CREATE TABLE `meetup_location` (
                                   `id` INT AUTO_INCREMENT,
                                   `name` VARCHAR(100) NOT NULL,
                                   `description` VARCHAR(255) DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园交易地点表';

CREATE TABLE `product` (
                           `id` BIGINT AUTO_INCREMENT,
                           `seller_id` BIGINT NOT NULL,
                           `category_id` INT NOT NULL,
                           `title` VARCHAR(100) NOT NULL,
                           `description` TEXT,
                           `price` DECIMAL(10, 2) NOT NULL,
                           `condition_level` TINYINT COMMENT '新旧程度 (1-5)',
                           `cover_image` VARCHAR(255) NOT NULL,
                           `status` VARCHAR(20) DEFAULT 'AVAILABLE' COMMENT '商品状态 (AVAILABLE, SOLD, DELISTED)',
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                           `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           FOREIGN KEY (`seller_id`) REFERENCES `user`(`id`),
                           FOREIGN KEY (`category_id`) REFERENCES `category`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

CREATE TABLE `orders` (
                          `id` BIGINT AUTO_INCREMENT,
                          `product_id` BIGINT NOT NULL,
                          `buyer_id` BIGINT NOT NULL,
                          `seller_id` BIGINT NOT NULL,
                          `order_status` VARCHAR(30) DEFAULT 'AWAITING_MEETUP',
                          `total_price` DECIMAL(10, 2) NOT NULL,
                          `delivery_method` VARCHAR(50) DEFAULT 'ON_CAMPUS_MEETUP',
                          `meetup_location_id` INT,
                          `meetup_time_slot` VARCHAR(50) DEFAULT NULL,
                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (`id`),
                          FOREIGN KEY (`product_id`) REFERENCES `product`(`id`),
                          FOREIGN KEY (`buyer_id`) REFERENCES `user`(`id`),
                          FOREIGN KEY (`seller_id`) REFERENCES `user`(`id`),
                          FOREIGN KEY (`meetup_location_id`) REFERENCES `meetup_location`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE `ratings` (
                           `id` BIGINT AUTO_INCREMENT,
                           `order_id` BIGINT NOT NULL,
                           `rater_id` BIGINT NOT NULL,
                           `ratee_id` BIGINT NOT NULL,
                           `score` TINYINT NOT NULL,
                           `comment` TEXT,
                           `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_order_rater` (`order_id`, `rater_id`),
                           FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
                           FOREIGN KEY (`rater_id`) REFERENCES `user`(`id`),
                           FOREIGN KEY (`ratee_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户评价表';

CREATE TABLE `messages` (
                            `id` BIGINT AUTO_INCREMENT,
                            `sender_id` BIGINT NOT NULL,
                            `receiver_id` BIGINT NOT NULL,
                            `product_id` BIGINT DEFAULT NULL,
                            `content` TEXT NOT NULL,
                            `is_read` BOOLEAN NOT NULL DEFAULT FALSE,
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            KEY `idx_conversation` (`sender_id`, `receiver_id`),
                            FOREIGN KEY (`sender_id`) REFERENCES `user`(`id`),
                            FOREIGN KEY (`receiver_id`) REFERENCES `user`(`id`),
                            FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户私信表';

CREATE TABLE `favorites` (
                             `id` BIGINT AUTO_INCREMENT,
                             `user_id` BIGINT NOT NULL,
                             `product_id` BIGINT NOT NULL,
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
                             FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
                             FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';

CREATE TABLE `notifications` (
                                 `id` BIGINT AUTO_INCREMENT,
                                 `user_id` BIGINT NOT NULL COMMENT '通知的接收者ID',
                                 `type` VARCHAR(50) NOT NULL COMMENT '通知类型 (e.g., NEW_ORDER, NEW_MESSAGE)',
                                 `content` VARCHAR(255) NOT NULL COMMENT '通知的简要内容',
                                 `related_id` BIGINT COMMENT '关联的对象ID (如订单ID, 商品ID)',
                                 `is_read` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已读',
                                 `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内通知表';

CREATE TABLE `product_images` (
                                  `id` BIGINT AUTO_INCREMENT,
                                  `product_id` BIGINT NOT NULL COMMENT '关联的商品ID',
                                  `image_url` VARCHAR(255) NOT NULL COMMENT '图片URL',
                                  `sort_order` INT DEFAULT 0 COMMENT '图片排序',
                                  PRIMARY KEY (`id`),
                                  FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

INSERT INTO `category` (`name`, `sort_order`) VALUES ('教材书籍', 1), ('电子产品', 2), ('生活用品', 3), ('代步工具', 4), ('服饰鞋包', 5), ('文具用品', 6), ('其他杂项', 99);
INSERT INTO `meetup_location` (`name`, `description`) VALUES ('图书馆正门', '图书馆正门入口处'), ('第一教学楼', '一教大厅'), ('第一食堂', '一食堂门口'), ('紫荆公寓1号楼', '宿舍楼下');

ALTER TABLE `user`
    ADD COLUMN `email` VARCHAR(255) NULL UNIQUE COMMENT '用户邮箱' AFTER `password`,
    ADD COLUMN `email_verified` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '邮箱是否已验证' AFTER `email`;

ALTER TABLE `user`
    ADD COLUMN `bio` TEXT NULL COMMENT '个人简介' AFTER `credit_score`;

CREATE TABLE `user_addresses` (
                                  `id` BIGINT AUTO_INCREMENT,
                                  `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
                                  `recipient_name` VARCHAR(100) NOT NULL COMMENT '收件人姓名',
                                  `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
                                  `province` VARCHAR(50) NOT NULL COMMENT '省份',
                                  `city` VARCHAR(50) NOT NULL COMMENT '城市',
                                  `district` VARCHAR(50) NOT NULL COMMENT '区/县',
                                  `detailed_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
                                  `is_default` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为默认地址',
                                  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`),
                                  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

ALTER TABLE `orders`
    ADD COLUMN `shipping_address_id` BIGINT NULL COMMENT '收货地址ID' AFTER `meetup_time_slot`,
    ADD COLUMN `shipping_provider` VARCHAR(50) NULL COMMENT '快递公司' AFTER `shipping_address_id`,
    ADD COLUMN `tracking_number` VARCHAR(100) NULL COMMENT '快递单号' AFTER `shipping_provider`,
    ADD CONSTRAINT `fk_order_address` FOREIGN KEY (`shipping_address_id`) REFERENCES `user_addresses`(`id`) ON DELETE SET NULL;