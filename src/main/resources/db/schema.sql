-- 二手书交易平台数据库初始化脚本（幂等，可重复执行）
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '登录名',
    `password`    VARCHAR(64)  NOT NULL COMMENT '密码(MD5)',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `role`        VARCHAR(10)  NOT NULL DEFAULT 'USER' COMMENT '角色: USER-前台用户 ADMIN-管理员',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1-正常 0-禁用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

CREATE TABLE IF NOT EXISTS `category` (
    `id`   BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '图书分类表';

CREATE TABLE IF NOT EXISTS `book` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '图书ID',
    `seller_id`       BIGINT        NOT NULL COMMENT '卖家用户ID',
    `category_id`     BIGINT        DEFAULT NULL COMMENT '分类ID',
    `title`           VARCHAR(100)  NOT NULL COMMENT '书名',
    `author`          VARCHAR(50)   DEFAULT NULL COMMENT '作者',
    `isbn`            VARCHAR(30)   DEFAULT NULL COMMENT 'ISBN',
    `price`           DECIMAL(10,2) NOT NULL COMMENT '售价',
    `original_price`  DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `condition_level` VARCHAR(20)   DEFAULT '九成新' COMMENT '成色',
    `description`     TEXT          COMMENT '描述',
    `cover`           VARCHAR(255)  DEFAULT NULL COMMENT '封面图片URL',
    `status`          VARCHAR(20)   NOT NULL DEFAULT 'ON_SALE' COMMENT '状态: ON_SALE-在售 SOLD-已售 OFF_SHELF-已下架',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    PRIMARY KEY (`id`),
    KEY `idx_seller` (`seller_id`),
    KEY `idx_category` (`category_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '图书表';

CREATE TABLE IF NOT EXISTS `orders` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`    VARCHAR(40)   NOT NULL COMMENT '订单编号',
    `book_id`     BIGINT        NOT NULL COMMENT '图书ID',
    `buyer_id`    BIGINT        NOT NULL COMMENT '买家用户ID',
    `seller_id`   BIGINT        NOT NULL COMMENT '卖家用户ID',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '成交价',
    `status`      VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待付款 COMPLETED-已完成 CANCELLED-已取消',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `finish_time` DATETIME      DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_book` (`book_id`),
    KEY `idx_buyer` (`buyer_id`),
    KEY `idx_seller` (`seller_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单表';
