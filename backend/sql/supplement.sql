USE `zhf`;

-- ================================
-- 1. 用户扩展信息表
-- ================================
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户 id',
  `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `province` varchar(64) DEFAULT NULL COMMENT '省份',
  `city` varchar(64) DEFAULT NULL COMMENT '城市',
  `district` varchar(64) DEFAULT NULL COMMENT '区/县',
  `detailAddress` varchar(256) DEFAULT NULL COMMENT '详细地址',
  `bio` text COMMENT '个人简介',
  `gender` tinyint DEFAULT '0' COMMENT '性别 0=未知 1=男 2=女',
  `memberLevel` varchar(32) NOT NULL DEFAULT 'normal' COMMENT '会员等级 normal/silver/gold',
  `memberExpireTime` datetime DEFAULT NULL COMMENT '会员过期时间',
  `growthValue` int NOT NULL DEFAULT '0' COMMENT '成长值',
  `points` int NOT NULL DEFAULT '0' COMMENT '积分',
  `totalSpend` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '累计消费',
  `inviteCode` varchar(32) DEFAULT NULL COMMENT '邀请码',
  `inviteCount` int NOT NULL DEFAULT '0' COMMENT '邀请人数',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_userId` (`userId`),
  UNIQUE KEY `idx_inviteCode` (`inviteCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户扩展信息';

-- ================================
-- 2. 新闻/动态表
-- ================================
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(256) NOT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `summary` varchar(512) DEFAULT NULL COMMENT '摘要',
  `coverImage` varchar(1024) DEFAULT NULL COMMENT '封面图',
  `category` varchar(64) DEFAULT NULL COMMENT '分类',
  `publishDate` date DEFAULT NULL COMMENT '发布日期',
  `viewCount` int NOT NULL DEFAULT '0' COMMENT '浏览数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0=草稿 1=已发布',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_publishDate` (`publishDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='新闻动态';

-- ================================
-- 3. 演唱会/活动表
-- ================================
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(256) NOT NULL COMMENT '活动标题',
  `city` varchar(64) DEFAULT NULL COMMENT '城市',
  `venue` varchar(256) DEFAULT NULL COMMENT '场馆',
  `eventDate` datetime DEFAULT NULL COMMENT '活动时间',
  `saleStartTime` datetime DEFAULT NULL COMMENT '开售时间',
  `priceTiers` varchar(512) DEFAULT NULL COMMENT '票价档位 JSON [380,580,780,980,1280]',
  `minPrice` decimal(10,2) DEFAULT NULL COMMENT '最低票价',
  `maxPrice` decimal(10,2) DEFAULT NULL COMMENT '最高票价',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
  `soldCount` int NOT NULL DEFAULT '0' COMMENT '已售数量',
  `coverImage` varchar(1024) DEFAULT NULL COMMENT '封面图',
  `description` text COMMENT '活动描述',
  `status` varchar(32) NOT NULL DEFAULT 'upcoming' COMMENT '状态 upcoming/onSale/ended/cancelled',
  `type` varchar(64) DEFAULT 'concert' COMMENT '类型 concert/meeting/birthday/crossover',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_eventDate` (`eventDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='演出活动';

-- ================================
-- 4. 照片分类表
-- ================================
DROP TABLE IF EXISTS `photo_category`;
CREATE TABLE `photo_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) NOT NULL COMMENT '分类名',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `sortOrder` int NOT NULL DEFAULT '0' COMMENT '排序',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='照片分类';

-- ================================
-- 5. 照片/相册表
-- ================================
DROP TABLE IF EXISTS `photo`;
CREATE TABLE `photo` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(128) DEFAULT NULL COMMENT '标题',
  `description` varchar(512) DEFAULT NULL COMMENT '描述',
  `imageUrl` varchar(1024) NOT NULL COMMENT '图片地址',
  `thumbnailUrl` varchar(1024) DEFAULT NULL COMMENT '缩略图地址',
  `categoryId` bigint NOT NULL COMMENT '分类 id（主分类）',
  `categoryName` varchar(64) DEFAULT NULL COMMENT '主分类名',
  `subCategory` varchar(64) DEFAULT NULL COMMENT '子分类',
  `sortOrder` int NOT NULL DEFAULT '0' COMMENT '排序',
  `likeCount` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `viewCount` int NOT NULL DEFAULT '0' COMMENT '浏览数',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_categoryId` (`categoryId`),
  KEY `idx_categoryName` (`categoryName`),
  KEY `idx_subCategory` (`subCategory`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='照片';

-- ================================
-- 6. 音乐专辑表
-- ================================
DROP TABLE IF EXISTS `album`;
CREATE TABLE `album` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(256) NOT NULL COMMENT '专辑名',
  `artist` varchar(128) NOT NULL DEFAULT '黄子弘凡' COMMENT '艺术家',
  `coverImage` varchar(1024) DEFAULT NULL COMMENT '封面图',
  `releaseYear` int DEFAULT NULL COMMENT '发行年份',
  `type` varchar(32) NOT NULL DEFAULT '单曲' COMMENT '类型 专辑/单曲/EP',
  `trackCount` int NOT NULL DEFAULT '0' COMMENT '歌曲数',
  `description` text COMMENT '专辑描述',
  `playCount` int NOT NULL DEFAULT '0' COMMENT '播放次数',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_releaseYear` (`releaseYear`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐专辑';

-- ================================
-- 7. 歌曲表
-- ================================
DROP TABLE IF EXISTS `track`;
CREATE TABLE `track` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(256) NOT NULL COMMENT '歌曲名',
  `albumId` bigint DEFAULT NULL COMMENT '所属专辑 id',
  `albumTitle` varchar(256) DEFAULT NULL COMMENT '专辑名',
  `duration` int DEFAULT NULL COMMENT '时长(秒)',
  `durationText` varchar(16) DEFAULT NULL COMMENT '时长文本 03:45',
  `trackNumber` int DEFAULT NULL COMMENT '曲序',
  `audioUrl` varchar(1024) DEFAULT NULL COMMENT '音频地址',
  `coverImage` varchar(1024) DEFAULT NULL COMMENT '封面图',
  `lyrics` text COMMENT '歌词',
  `playCount` int NOT NULL DEFAULT '0' COMMENT '播放次数',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_albumId` (`albumId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌曲';

-- ================================
-- 8. 会员权益表
-- ================================
DROP TABLE IF EXISTS `member_benefit`;
CREATE TABLE `member_benefit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(128) NOT NULL COMMENT '权益名称',
  `description` varchar(256) DEFAULT NULL COMMENT '权益描述',
  `icon` varchar(32) DEFAULT NULL COMMENT '图标 emoji',
  `levels` varchar(128) NOT NULL COMMENT '适用等级 JSON ["normal","silver","gold"]',
  `sortOrder` int NOT NULL DEFAULT '0' COMMENT '排序',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员权益';

-- ================================
-- 9. 成长值变动记录
-- ================================
DROP TABLE IF EXISTS `growth_record`;
CREATE TABLE `growth_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户 id',
  `value` int NOT NULL COMMENT '变动值(正或负)',
  `reason` varchar(256) DEFAULT NULL COMMENT '变动原因',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`),
  KEY `idx_createTime` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成长值记录';

-- ================================
-- 10. 消费记录表
-- ================================
DROP TABLE IF EXISTS `consumption_record`;
CREATE TABLE `consumption_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户 id',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `description` varchar(256) DEFAULT NULL COMMENT '消费描述',
  `type` varchar(32) NOT NULL DEFAULT 'purchase' COMMENT '类型 purchase/renewal/ticket/merchandise',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`),
  KEY `idx_createTime` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消费记录';

-- ================================
-- 11. 积分变动记录
-- ================================
DROP TABLE IF EXISTS `points_record`;
CREATE TABLE `points_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户 id',
  `points` int NOT NULL COMMENT '变动积分(正或负)',
  `description` varchar(256) DEFAULT NULL COMMENT '说明',
  `type` varchar(32) NOT NULL DEFAULT 'earn' COMMENT '类型 earn/spend',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`),
  KEY `idx_createTime` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分记录';

-- ================================
-- 12. 商品表（周边/门票）
-- ================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(256) NOT NULL COMMENT '商品名称',
  `description` text COMMENT '商品描述',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `originalPrice` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存',
  `soldCount` int NOT NULL DEFAULT '0' COMMENT '已售数量',
  `coverImage` varchar(1024) DEFAULT NULL COMMENT '封面图',
  `images` text COMMENT '图片列表 JSON',
  `type` varchar(32) NOT NULL DEFAULT 'merchandise' COMMENT '类型 merchandise/ticket',
  `category` varchar(64) DEFAULT NULL COMMENT '分类',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0=下架 1=上架',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品';

-- ================================
-- 13. 订单表
-- ================================
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `orderNo` varchar(64) NOT NULL COMMENT '订单号',
  `userId` bigint NOT NULL COMMENT '用户 id',
  `totalAmount` decimal(10,2) NOT NULL COMMENT '总金额',
  `itemCount` int NOT NULL DEFAULT '0' COMMENT '商品件数',
  `status` varchar(32) NOT NULL DEFAULT 'pending' COMMENT '状态 pending/paid/shipped/completed/cancelled',
  `statusText` varchar(32) DEFAULT NULL COMMENT '状态文本',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `payTime` datetime DEFAULT NULL COMMENT '支付时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_orderNo` (`orderNo`),
  KEY `idx_userId` (`userId`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单';

-- ================================
-- 14. 订单商品项
-- ================================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `orderId` bigint NOT NULL COMMENT '订单 id',
  `productId` bigint NOT NULL COMMENT '商品 id',
  `name` varchar(256) NOT NULL COMMENT '商品名称',
  `spec` varchar(64) DEFAULT NULL COMMENT '规格/类型',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '数量',
  `image` varchar(1024) DEFAULT NULL COMMENT '商品图片',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_orderId` (`orderId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项';

-- ================================
-- 15. 帖子评论表
-- ================================
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint NOT NULL COMMENT '帖子 id',
  `userId` bigint NOT NULL COMMENT '评论用户 id',
  `content` text NOT NULL COMMENT '评论内容',
  `replyTo` bigint DEFAULT NULL COMMENT '回复的评论 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_postId` (`postId`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子评论';

-- ================================
-- 16. AI聊天对话表
-- ================================
DROP TABLE IF EXISTS `chat_conversation`;
CREATE TABLE `chat_conversation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户 id',
  `title` varchar(128) NOT NULL DEFAULT '新对话' COMMENT '对话标题',
  `context` text COMMENT '上下文信息 JSON',
  `createdAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI聊天对话';

-- ================================
-- 17. AI聊天消息表
-- ================================
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `conversationId` bigint NOT NULL COMMENT '对话 id',
  `userId` bigint NOT NULL COMMENT '用户 id',
  `type` varchar(16) NOT NULL DEFAULT 'user' COMMENT '消息类型 user/ai',
  `content` text NOT NULL COMMENT '消息内容',
  `files` text COMMENT '附件 JSON',
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消息时间',
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_conversationId` (`conversationId`),
  KEY `idx_timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI聊天消息';

-- ================================
-- 18. 联系我们留言表
-- ================================
DROP TABLE IF EXISTS `contact_message`;
CREATE TABLE `contact_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) DEFAULT NULL COMMENT '姓名',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `subject` varchar(256) DEFAULT NULL COMMENT '主题',
  `message` text COMMENT '留言内容',
  `isRead` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读',
  `reply` text COMMENT '回复内容',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='联系我们留言';

-- ================================
-- 19. 用户通知表
-- ================================
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '接收用户 id',
  `content` varchar(512) NOT NULL COMMENT '通知内容',
  `type` varchar(32) NOT NULL DEFAULT 'system' COMMENT '类型 system/like/comment/follow',
  `relatedId` bigint DEFAULT NULL COMMENT '关联 id（如帖子id）',
  `isRead` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`),
  KEY `idx_createTime` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户通知';

-- ================================
-- 20. 私信表
-- ================================
DROP TABLE IF EXISTS `user_message`;
CREATE TABLE `user_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `senderId` bigint NOT NULL COMMENT '发送者 id',
  `receiverId` bigint NOT NULL COMMENT '接收者 id',
  `content` text NOT NULL COMMENT '消息内容',
  `isRead` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_senderId` (`senderId`),
  KEY `idx_receiverId` (`receiverId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私信';

-- ===============================================
-- 初始化数据
-- ===============================================

-- 照片分类初始数据
INSERT INTO `photo_category` (`name`, `description`, `sortOrder`) VALUES
('微博自拍', '黄子弘凡在社交媒体分享的精彩自拍', 1),
('演唱会现场', '演唱会精彩瞬间和舞台表现', 2),
('综艺神图', '综艺节目中的精彩画面', 3),
('活动现场', '各类活动出席的照片集锦', 4),
('专辑宣传', '专辑宣传相关的照片素材', 5);

-- 音乐专辑初始数据
INSERT INTO `album` (`title`, `artist`, `coverImage`, `releaseYear`, `type`, `trackCount`, `description`) VALUES
('惊虹', '黄子弘凡', '/src/assets/黄子弘凡/惊虹.jpg', 2024, '单曲', 1, '一首充满正能量的歌曲'),
('成为你想成为的大人', '黄子弘凡', '/src/assets/黄子弘凡/成为你想成为的大人.jpg', 2023, '单曲', 1, '关于成长与梦想的歌曲'),
('除了快乐禁止入内', '黄子弘凡', '/src/assets/黄子弘凡/除了快乐禁止入内.jpg', 2023, '单曲', 1, '快乐就是最好的通行证'),
('Do U', '黄子弘凡', '/src/assets/黄子弘凡/Do U.jpg', 2022, '单曲', 1, '一首浪漫的情歌'),
('愿你', '黄子弘凡', '/src/assets/黄子弘凡/愿你.jpg', 2021, '专辑', 8, '首张个人专辑，愿你被这个世界温柔以待'),
('礼物', '黄子弘凡', '/src/assets/黄子弘凡/礼物.jpg', 2022, 'EP', 3, '送给粉丝的礼物');

-- 歌曲初始数据
INSERT INTO `track` (`title`, `albumId`, `albumTitle`, `duration`, `durationText`, `trackNumber`, `coverImage`) VALUES
('惊虹', 1, '惊虹', 225, '03:45', 1, '/src/assets/黄子弘凡/惊虹.jpg'),
('成为你想成为的大人', 2, '成为你想成为的大人', 252, '04:12', 1, '/src/assets/黄子弘凡/成为你想成为的大人.jpg'),
('除了快乐禁止入内', 3, '除了快乐禁止入内', 238, '03:58', 1, '/src/assets/黄子弘凡/除了快乐禁止入内.jpg'),
('Do U', 4, 'Do U', 210, '03:30', 1, '/src/assets/黄子弘凡/Do U.jpg'),
('礼物', 6, '礼物', 265, '04:25', 1, '/src/assets/黄子弘凡/礼物.jpg'),
('愿你', 5, '愿你', 230, '03:50', 1, '/src/assets/黄子弘凡/愿你.jpg');

-- 会员权益初始数据
INSERT INTO `member_benefit` (`name`, `description`, `icon`, `levels`, `sortOrder`) VALUES
('优先购票', '演唱会门票优先购买权', '🎫', '["silver","gold"]', 1),
('专属折扣', '周边商品享受9折优惠', '💰', '["silver","gold"]', 2),
('生日福利', '生日当月获得500积分', '🎂', '["silver","gold"]', 3),
('免费周边', '每年获得免费周边商品', '🎁', '["gold"]', 4),
('专属客服', '24小时专属客服服务', '👨‍💼', '["gold"]', 5),
('会员活动', '参与专属会员线下活动', '🎉', '["normal","silver","gold"]', 6);

-- 示例演出活动
INSERT INTO `event` (`title`, `city`, `venue`, `eventDate`, `saleStartTime`, `priceTiers`, `minPrice`, `maxPrice`, `stock`, `coverImage`, `description`, `status`, `type`) VALUES
('黄子弘凡 2026鸟巢收官演唱会', '北京', '国家体育场', '2026-03-14 18:00:00', '2026-02-25 12:00:00', '[380,580,780,980,1280]', 380.00, 1280.00, 5000, '/src/assets/黄子弘凡/20230110201024_78f5f.thumb.1000_0.jpeg', '黄子弘凡2026年鸟巢收官演唱会', 'onSale', 'concert'),
('黄子弘凡 2024 个人演唱会', '北京', '北京工人体育馆', '2024-12-25 19:30:00', '2024-11-01 12:00:00', '[380,480,680]', 380.00, 680.00, 3000, '/src/assets/黄子弘凡/演唱会现场.jpg', '黄子弘凡2024个人演唱会火热开启', 'upcoming', 'concert'),
('黄子弘凡 2025 巡回演唱会', '上海', '上海体育馆', '2025-03-15 20:00:00', '2025-01-15 12:00:00', '[480,680,980]', 480.00, 980.00, 4000, '/src/assets/黄子弘凡/2024全国巡回演唱会即将开启.jpg', '黄子弘凡2025巡回演唱会上海站', 'upcoming', 'concert'),
('黄子弘凡 音乐分享会', '广州', '广州大剧院', '2025-01-10 14:00:00', '2024-12-01 12:00:00', '[280]', 280.00, 280.00, 1000, '/src/assets/黄子弘凡/演唱会现场 舞台表演.jpg', '近距离分享音乐创作故事', 'upcoming', 'meeting'),
('黄子弘凡 生日会', '深圳', '深圳音乐厅', '2025-05-08 19:00:00', '2025-04-01 12:00:00', '[380]', 380.00, 380.00, 800, '/src/assets/黄子弘凡/活动现场 现场表演.jpg', '与黄子弘凡一起庆祝生日', 'upcoming', 'birthday'),
('黄子弘凡 粉丝见面会', '成都', '成都体育馆', '2025-07-15 15:00:00', '2025-06-01 12:00:00', '[280]', 280.00, 280.00, 1500, '/src/assets/黄子弘凡/2025除了快乐禁止入内演唱会.jpg', '粉丝专属见面会', 'upcoming', 'meeting'),
('黄子弘凡 跨年演唱会', '杭州', '杭州奥体中心', '2024-12-31 23:00:00', '2024-11-15 12:00:00', '[580,880,1280]', 580.00, 1280.00, 6000, '/src/assets/黄子弘凡/演唱会现场 互动环节.jpg', '和黄子弘凡一起跨年', 'upcoming', 'concert');

-- 示例商品
INSERT INTO `product` (`name`, `description`, `price`, `originalPrice`, `stock`, `coverImage`, `type`, `category`) VALUES
('黄子弘凡 专辑', '精选专辑，收录多首热门歌曲', 128.00, 148.00, 50, '/src/assets/黄子弘凡/专辑.jpg', 'merchandise', '音乐'),
('黄子弘凡 应援T恤', '高品质纯棉应援T恤', 88.00, 99.00, 100, '/src/assets/黄子弘凡/应援T恤.jpg', 'merchandise', '服装'),
('黄子弘凡 海报套装', '精美海报套装，含多款设计', 48.00, NULL, 200, '/src/assets/黄子弘凡/演唱会现场 舞台表演.jpg', 'merchandise', '周边'),
('黄子弘凡 应援棒', '演唱会专属应援棒，可变色', 158.00, 188.00, 80, '/src/assets/黄子弘凡/应援棒.jpg', 'merchandise', '应援'),
('黄子弘凡 手办', '限量版精美手办', 299.00, 399.00, 30, '/src/assets/黄子弘凡/手办.jpg', 'merchandise', '手办'),
('黄子弘凡 台历', '2025年官方台历', 68.00, NULL, 150, '/src/assets/黄子弘凡/台历.jpg', 'merchandise', '周边'),
('黄子弘凡 明信片', '精美明信片套装', 38.00, NULL, 200, '/src/assets/黄子弘凡/明信片.jpg', 'merchandise', '周边');

-- 示例新闻
INSERT INTO `news` (`title`, `content`, `summary`, `coverImage`, `category`, `publishDate`, `status`) VALUES
('新专辑《梦想之旅》发布', '黄子弘凡全新专辑《梦想之旅》正式发布，包含10首原创歌曲，展现了他在音乐道路上的成长与蜕变。', '黄子弘凡全新专辑《梦想之旅》正式发布', '/src/assets/黄子弘凡/20210730204256_98832.jpg', '音乐', '2023-12-15', 1),
('2024全国巡回演唱会官宣', '黄子弘凡2024全国巡回演唱会正式官宣，将在全国20个城市举办。这是他毕业以来规模最大的一次巡演。', '黄子弘凡2024全国巡回演唱会正式官宣', '/src/assets/黄子弘凡/20230110201024_78f5f.thumb.1000_0.jpeg', '演唱会', '2023-11-30', 1),
('参与公益活动，传递爱心', '黄子弘凡参与了"音乐传递希望"公益活动，为贫困地区的孩子捐赠音乐器材和教材。', '黄子弘凡参与公益活动，传递爱心', '/src/assets/黄子弘凡/活动现场.jpg', '公益', '2023-11-15', 1);
