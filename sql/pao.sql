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

CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `config_type` varchar(20) DEFAULT 'string' COMMENT 'string/number/boolean/json',
  `description` varchar(200) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统参数';

CREATE TABLE `sys_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `status` tinyint DEFAULT 0 COMMENT '0成功 1失败',
  `ip` varchar(50) DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `browser` varchar(100) DEFAULT NULL,
  `msg` varchar(500) DEFAULT NULL,
  `login_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sys_login_log_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='登录日志';

CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  `name` varchar(50) NOT NULL COMMENT '菜单名称',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `permission` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `type` char(1) NOT NULL COMMENT 'M目录 C菜单 F按钮',
  `icon` varchar(100) DEFAULT NULL,
  `sort` int DEFAULT 0,
  `visible` tinyint NOT NULL DEFAULT 1 COMMENT '0隐藏 1显示',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0停用 1正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限';

CREATE TABLE `sys_oper_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) DEFAULT NULL,
  `business_type` varchar(50) DEFAULT NULL,
  `method` varchar(200) DEFAULT NULL,
  `request_method` varchar(10) DEFAULT NULL,
  `operator_id` bigint DEFAULT NULL,
  `operator_name` varchar(50) DEFAULT NULL,
  `request_url` varchar(500) DEFAULT NULL,
  `request_param` text,
  `response_result` text,
  `status` tinyint DEFAULT 0 COMMENT '0成功 1失败',
  `error_msg` text,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sys_oper_log_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志';

CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_key` varchar(50) NOT NULL COMMENT '角色标识',
  `sort` int DEFAULT 0,
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0停用 1正常',
  `remark` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色';

CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色菜单关联';

CREATE TABLE `sys_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `snapshot_type` varchar(50) NOT NULL COMMENT '统计类型',
  `data` json NOT NULL COMMENT '统计数据',
  `snapshot_date` date DEFAULT NULL COMMENT '统计日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sys_statistics_type_date` (`snapshot_type`, `snapshot_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='统计快照';

CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `avatar` varchar(500) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0停用 1正常',
  `remark` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员';

CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联';

CREATE TABLE `app_splash` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `logo_url` varchar(500) DEFAULT NULL COMMENT '开屏 Logo 图片地址',
  `slogan` varchar(200) DEFAULT NULL COMMENT '开屏文案',
  `background_color` varchar(20) DEFAULT '#f9f9f9' COMMENT '背景色',
  `primary_color` varchar(20) DEFAULT '#666666' COMMENT '主色（loading、次要文字）',
  `slogan_font_size` int DEFAULT NULL COMMENT '文案字号（px），空则用客户端默认',
  `slogan_color` varchar(20) DEFAULT NULL COMMENT '文案颜色，空则用 primary_color',
  `show_copyright` tinyint NOT NULL DEFAULT 1 COMMENT '0 隐藏 1 显示底部版权',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0 停用 1 启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='App 开屏配置';

CREATE TABLE `app_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` varchar(500) NOT NULL COMMENT '通知内容',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0 停用 1 启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_app_notice_status_sort` (`status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='App 滚动通知';

CREATE TABLE `app_business_promotion` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '商家名称',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `image` varchar(500) DEFAULT NULL COMMENT '封面图 URL',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签，JSON 数组如 ["专业","快捷"]',
  `min_price` varchar(50) DEFAULT NULL COMMENT '最低价展示，如 ¥100起',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0 停用 1 启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_app_business_promotion_status_sort` (`status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='首页商家推广';

CREATE TABLE `app_banner` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `image` varchar(500) NOT NULL COMMENT '图片 URL',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0停用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_app_banner_status_sort` (`status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='首页轮播';
