CREATE TABLE `address` (
  `aid` varchar(36) NOT NULL COMMENT '地址id',
  `uid` varchar(50) NOT NULL COMMENT '用户id',
  `sjr` varchar(50) DEFAULT NULL COMMENT '收件人',
  `phone` varchar(13) DEFAULT NULL COMMENT '联系电话',
  `detail` text COMMENT '地址详情',
  `is_default` tinyint(1) DEFAULT NULL COMMENT '是否默认地址',
  PRIMARY KEY (`aid`),
  KEY `fk_address_user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='地址表'

CREATE TABLE `admin_permission` (
  `id` int DEFAULT NULL COMMENT '主键id',
  `pkey` varchar(30) DEFAULT NULL COMMENT '键',
  `name` varchar(50) DEFAULT NULL COMMENT '权限名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理端权限表'

CREATE TABLE `admin_role_permission` (
  `rid` int DEFAULT NULL,
  `pid` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限表'

CREATE TABLE `admin_statistics` (
  `id` int NOT NULL AUTO_INCREMENT,
  `data` json NOT NULL COMMENT '统计数据',
  `create_time` datetime DEFAULT NULL COMMENT '统计时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统计表'

CREATE TABLE `admin_system_config` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `config_key` varchar(50) DEFAULT NULL COMMENT '配置键',
  `config_value` varchar(100) DEFAULT NULL COMMENT '配置值',
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT (now()) COMMENT '创建时间',
  `update_time` datetime DEFAULT (now()) COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理端系统设置'

CREATE TABLE `admin_user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `phone` varchar(15) DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `role_id` int DEFAULT NULL COMMENT '角色id',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
  PRIMARY KEY (`id`),
  KEY `admin_user_id_index` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `admin_user_role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(50) DEFAULT NULL COMMENT '角色名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色'

CREATE TABLE `conversation` (
  `cid` varchar(50) NOT NULL COMMENT '会话id',
  `oid` varchar(50) DEFAULT NULL COMMENT '订单id',
  `message_counts` int NOT NULL DEFAULT '0' COMMENT '对话消息总条数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '会话创建时间',
  `last_message_content` text,
  `last_message_send_time` datetime DEFAULT (now()) COMMENT '最后一条消息送达时间',
  `last_message_read_time` datetime DEFAULT NULL COMMENT '最后一条消息已读时间',
  `status` enum('OPENING','CLOSED') DEFAULT 'OPENING' COMMENT '会话状态',
  PRIMARY KEY (`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会话表'

CREATE TABLE `message` (
  `mid` varchar(50) NOT NULL COMMENT '消息id',
  `cid` varchar(50) DEFAULT NULL COMMENT '会话id',
  `sender` varchar(50) DEFAULT NULL COMMENT '发送者',
  `receiver` varchar(50) DEFAULT NULL COMMENT '接收者',
  `type` enum('TEXT','VOICE','IMAGE') DEFAULT NULL COMMENT '消息类型',
  `content` longblob COMMENT '内容',
  `status` enum('SENDING','SENT','READ','FAILED') DEFAULT 'SENT' COMMENT '消息状态',
  `send_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送消息时间',
  `read_time` datetime DEFAULT NULL,
  PRIMARY KEY (`mid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='聊天消息表'

CREATE TABLE `order` (
  `oid` varchar(32) NOT NULL COMMENT '订单id',
  `xdr` varchar(50) DEFAULT NULL COMMENT '下单人',
  `create_time` datetime DEFAULT (now()) COMMENT '下单时间',
  `expect_time` datetime DEFAULT NULL COMMENT '期望送达时间',
  `order_type_id` varchar(50) DEFAULT NULL COMMENT '订单分类',
  `img` text COMMENT '图片url',
  `aid` varchar(20) DEFAULT NULL COMMENT '地址id',
  `detail` json DEFAULT NULL COMMENT '订单详细数据',
  `amount` decimal(5,2) DEFAULT NULL COMMENT '金额',
  `jdr` varchar(50) DEFAULT NULL COMMENT '接单人',
  `accept_time` datetime DEFAULT NULL,
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `status` char(1) DEFAULT 'D' COMMENT '订单状态（D：待接单；J：进行中；S：已完成；C：已取消）',
  `rating` decimal(2,1) DEFAULT '0.0' COMMENT '评分',
  PRIMARY KEY (`oid`),
  UNIQUE KEY `unique_oid` (`oid`),
  KEY `fk_order_address` (`aid`),
  KEY `fk_order_user_jdr` (`jdr`),
  KEY `fk_order_user_xdr` (`xdr`),
  KEY `order_order_status_status_fk` (`status`),
  CONSTRAINT `order_order_status_status_fk` FOREIGN KEY (`status`) REFERENCES `order_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单表'

CREATE TABLE `order_status` (
  `status` char(1) NOT NULL COMMENT '订单状态',
  `description` varchar(50) DEFAULT NULL COMMENT '描述',
  `color` char(7) NOT NULL DEFAULT '#FFFFFF' COMMENT '十六进制文字颜色',
  PRIMARY KEY (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单状态'

CREATE TABLE `order_type` (
  `order_type_id` varchar(50) NOT NULL COMMENT '服务id',
  `type_name` varchar(10) DEFAULT NULL COMMENT '服务名称',
  `emoji_icon` varchar(5) DEFAULT NULL COMMENT '图标',
  `bg_color` varchar(10) DEFAULT NULL COMMENT '十六进制背景色',
  `form` json DEFAULT NULL COMMENT '表单模板',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`order_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单类型'

CREATE TABLE `school` (
  `sid` int NOT NULL AUTO_INCREMENT,
  `sname` varchar(100) NOT NULL,
  PRIMARY KEY (`sid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学校表'

CREATE TABLE `transaction` (
  `tid` varchar(50) NOT NULL COMMENT '交易id',
  `oid` varchar(50) DEFAULT NULL COMMENT '订单id',
  `uid` varchar(50) DEFAULT NULL COMMENT '用户id',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '交易金额',
  `type` enum('RECHARGE','WITHDRAWAL','ORDER','COMPLETE_ORDER','CANCEL_ORDER') NOT NULL COMMENT '交易类型（充值、提现、下单、完单、取消订单）',
  `create_time` datetime DEFAULT (now()) COMMENT '交易时间',
  PRIMARY KEY (`tid`),
  KEY `transaction_order_oid_fk` (`oid`),
  KEY `transaction_user_uid_fk` (`uid`),
  CONSTRAINT `transaction_order_oid_fk` FOREIGN KEY (`oid`) REFERENCES `order` (`oid`),
  CONSTRAINT `transaction_user_uid_fk` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`),
  CONSTRAINT `ck_amount` CHECK ((`amount` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易记录表'

CREATE TABLE `user` (
  `uid` char(36) NOT NULL COMMENT '用户ID',
  `openid` char(28) DEFAULT NULL,
  `nickname` varchar(30) DEFAULT NULL COMMENT '昵称',
  `sex` smallint DEFAULT (-(1)),
  `sid` int DEFAULT NULL,
  `phone` varchar(13) DEFAULT NULL COMMENT '手机号',
  `avatar` longtext COMMENT '头像url',
  `ctime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '余额',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0禁用 1正常',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `unique_uid` (`uid`),
  UNIQUE KEY `unique_openid` (`openid`),
  CONSTRAINT `chk_amount_non_negative` CHECK ((`balance` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表'

