/*
SQLyog Community v13.3.1 (64 bit)
MySQL - 8.0.44 : Database - citywalk
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`zhf` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `zhf`;

/*Table structure for table `post` */

DROP TABLE IF EXISTS `post`;

CREATE TABLE `post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `tags` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签列表（json 数组）',
  `thumbNum` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `favourNum` int NOT NULL DEFAULT '0' COMMENT '收藏数',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_userId` (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='帖子';

/*Data for the table `post` */

/*Table structure for table `post_favour` */

DROP TABLE IF EXISTS `post_favour`;

CREATE TABLE `post_favour` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint NOT NULL COMMENT '帖子 id',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_postId` (`postId`) USING BTREE,
  KEY `idx_userId` (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='帖子收藏';

/*Data for the table `post_favour` */

/*Table structure for table `post_thumb` */

DROP TABLE IF EXISTS `post_thumb`;

CREATE TABLE `post_thumb` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint NOT NULL COMMENT '帖子 id',
  `userId` bigint NOT NULL COMMENT '创建用户 id',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_postId` (`postId`) USING BTREE,
  KEY `idx_userId` (`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='帖子点赞';

/*Data for the table `post_thumb` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号',
  `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `unionId` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信开放平台id',
  `mpOpenId` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '公众号openId',
  `userName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
  `userAvatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户头像',
  `userProfile` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户简介',
  `userRole` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_unionId` (`unionId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2015629289921245186 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户';

/*Data for the table `user` */

insert  into `user`(`id`,`userAccount`,`userPassword`,`unionId`,`mpOpenId`,`userName`,`userAvatar`,`userProfile`,`userRole`,`createTime`,`updateTime`,`isDelete`) values 
(1935152147047858177,'zhangsan','b03d227f78c0c79334fca76e7b8eb46a',NULL,NULL,'张三',NULL,NULL,'admin','2025-06-18 09:46:39','2025-06-27 14:03:30',0),
(1938481258256347138,'zhangsan1','b03d227f78c0c79334fca76e7b8eb46a',NULL,NULL,'222222222222','',NULL,'user','2025-06-27 14:15:21','2025-06-27 14:16:22',0),
(1938481322349506561,'zhangsan1','b03d227f78c0c79334fca76e7b8eb46a',NULL,NULL,'111','',NULL,'user','2025-06-27 14:15:37','2025-06-27 14:17:12',1),
(1938512849213009922,'lisi','b03d227f78c0c79334fca76e7b8eb46a',NULL,NULL,NULL,NULL,NULL,'user','2025-06-27 16:20:53','2025-06-27 16:20:53',0),
(1938513000694493186,'wangwu','b03d227f78c0c79334fca76e7b8eb46a',NULL,NULL,NULL,NULL,NULL,'user','2025-06-27 16:21:29','2025-06-27 16:21:29',0),
(1938516975548272642,'zhangziyi','b03d227f78c0c79334fca76e7b8eb46a',NULL,NULL,'章子怡',NULL,NULL,'user','2025-06-27 16:37:17','2025-06-27 16:37:17',0),
(1939569854321205250,'wangwang','b03d227f78c0c79334fca76e7b8eb46a',NULL,NULL,'王王',NULL,NULL,'user','2025-06-30 14:21:03','2025-06-30 14:21:03',0),
(2015629289921245185,'13070849125','14c8f4f580cb3653f62466011e59feaa',NULL,NULL,'13070849125',NULL,NULL,'user','2026-01-26 11:34:05','2026-01-26 11:34:05',0);

/*Table structure for table `event` */

DROP TABLE IF EXISTS `event`;

CREATE TABLE `event` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(256) NOT NULL COMMENT '演出标题',
  `city` varchar(64) NOT NULL COMMENT '城市',
  `venue` varchar(256) DEFAULT NULL COMMENT '场馆',
  `type` varchar(32) DEFAULT 'concert' COMMENT '类型: concert/meeting/musical/festival',
  `eventDate` datetime NOT NULL COMMENT '演出时间',
  `saleStartTime` datetime DEFAULT NULL COMMENT '开售时间',
  `minPrice` decimal(10,2) DEFAULT 0 COMMENT '最低票价',
  `maxPrice` decimal(10,2) DEFAULT 0 COMMENT '最高票价',
  `priceTiers` text COMMENT '票价档位 JSON',
  `stock` int DEFAULT 0 COMMENT '总票数',
  `soldCount` int DEFAULT 0 COMMENT '已售票数',
  `status` varchar(32) DEFAULT 'UPCOMING' COMMENT '状态: UPCOMING/ON_SALE/SOLD_OUT/ENDED',
  `coverImage` varchar(512) DEFAULT NULL COMMENT '封面图',
  `description` text COMMENT '演出描述',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_city` (`city`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='演出/活动';

/*Data for the table `event` */

insert into `event`(`id`,`title`,`city`,`venue`,`type`,`eventDate`,`saleStartTime`,`minPrice`,`maxPrice`,`priceTiers`,`stock`,`soldCount`,`status`,`coverImage`) values
(1,'黄子弘凡2026「除了快乐禁止入内」巡回演唱会','北京','国家体育场（鸟巢）','concert','2026-07-18 19:30:00','2026-06-01 12:00:00',380,1280,'[{\"price\":380,\"description\":\"普通票\"},{\"price\":580,\"description\":\"看台票\"},{\"price\":780,\"description\":\"VIP票\"},{\"price\":980,\"description\":\"SVIP票\"},{\"price\":1280,\"description\":\"内场票\"}]',50000,28000,'ON_SALE',NULL),
(2,'黄子弘凡2026「除了快乐禁止入内」巡回演唱会','上海','梅赛德斯奔驰文化中心','concert','2026-08-08 19:30:00','2026-06-15 12:00:00',380,1280,'[{\"price\":380,\"description\":\"普通票\"},{\"price\":580,\"description\":\"看台票\"},{\"price\":780,\"description\":\"VIP票\"},{\"price\":980,\"description\":\"SVIP票\"},{\"price\":1280,\"description\":\"内场票\"}]',20000,19500,'ON_SALE',NULL),
(3,'黄子弘凡2026「除了快乐禁止入内」巡回演唱会','广州','广州体育馆','concert','2026-08-22 19:30:00','2026-07-01 12:00:00',380,1280,'[{\"price\":380,\"description\":\"普通票\"},{\"price\":580,\"description\":\"看台票\"},{\"price\":780,\"description\":\"VIP票\"},{\"price\":980,\"description\":\"SVIP票\"},{\"price\":1280,\"description\":\"内场票\"}]',15000,0,'UPCOMING',NULL),
(4,'黄子弘凡2026「除了快乐禁止入内」巡回演唱会','成都','五粮液成都金融城演艺中心','concert','2026-09-05 19:30:00','2026-07-15 12:00:00',380,1280,'[{\"price\":380,\"description\":\"普通票\"},{\"price\":580,\"description\":\"看台票\"},{\"price\":780,\"description\":\"VIP票\"},{\"price\":980,\"description\":\"SVIP票\"},{\"price\":1280,\"description\":\"内场票\"}]',18000,3200,'ON_SALE',NULL),
(5,'黄子弘凡粉丝见面会','杭州','杭州大剧院','meeting','2026-10-01 14:00:00','2026-08-15 12:00:00',580,1280,'[{\"price\":580,\"description\":\"普通票\"},{\"price\":880,\"description\":\"VIP票\"},{\"price\":1280,\"description\":\"合影票\"}]',3000,0,'UPCOMING',NULL),
(6,'黄子弘凡2026「除了快乐禁止入内」巡回演唱会','深圳','深圳湾体育中心','concert','2026-09-19 19:30:00','2026-07-20 12:00:00',380,1280,'[{\"price\":380,\"description\":\"普通票\"},{\"price\":580,\"description\":\"看台票\"},{\"price\":780,\"description\":\"VIP票\"},{\"price\":980,\"description\":\"SVIP票\"},{\"price\":1280,\"description\":\"内场票\"}]',15000,8000,'ON_SALE',NULL);

/*Table structure for table `product` */

DROP TABLE IF EXISTS `product`;

CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(256) NOT NULL COMMENT '商品名称',
  `description` text COMMENT '描述',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `originalPrice` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `stock` int DEFAULT 0 COMMENT '库存',
  `soldCount` int DEFAULT 0 COMMENT '已售数量',
  `coverImage` varchar(512) DEFAULT NULL COMMENT '封面图',
  `images` text COMMENT '图片列表JSON',
  `type` varchar(32) DEFAULT 'merchandise' COMMENT '类型',
  `category` varchar(64) DEFAULT NULL COMMENT '分类',
  `status` int DEFAULT 1 COMMENT '状态: 0下架 1上架',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='周边商品';

insert into `product`(`id`,`name`,`type`,`price`,`stock`,`coverImage`) values
(1,'黄子弘凡官方应援棒','merchandise',128,500,NULL),
(2,'「除了快乐禁止入内」巡回演唱会纪念T恤','merchandise',198,300,NULL),
(3,'黄子弘凡签名专辑套装','merchandise',298,100,NULL),
(4,'2026年限量台历','merchandise',88,200,NULL),
(5,'黄子弘凡Q版手办','merchandise',168,150,NULL),
(6,'官方明信片套装','merchandise',58,600,NULL);

/*Table structure for table `order_info` */

DROP TABLE IF EXISTS `order_info`;

CREATE TABLE `order_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `orderNo` varchar(64) NOT NULL COMMENT '订单号',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `totalAmount` decimal(10,2) NOT NULL COMMENT '总金额',
  `itemCount` int DEFAULT 1 COMMENT '商品数量',
  `status` varchar(32) DEFAULT 'pending' COMMENT '状态: pending/paid/completed/cancelled',
  `statusText` varchar(64) DEFAULT '待付款' COMMENT '状态文本',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `payTime` datetime DEFAULT NULL COMMENT '支付时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`),
  KEY `idx_orderNo` (`orderNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单';

/*Table structure for table `order_item` */

DROP TABLE IF EXISTS `order_item`;

CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `orderId` bigint NOT NULL COMMENT '订单ID',
  `productId` bigint DEFAULT NULL COMMENT '商品ID',
  `name` varchar(256) DEFAULT NULL COMMENT '商品名称',
  `spec` varchar(128) DEFAULT NULL COMMENT '规格',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int DEFAULT 1 COMMENT '数量',
  `image` varchar(512) DEFAULT NULL COMMENT '图片',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_orderId` (`orderId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项';

/*Table structure for table `contact_message` */

DROP TABLE IF EXISTS `contact_message`;

CREATE TABLE `contact_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(128) DEFAULT NULL COMMENT '姓名',
  `email` varchar(256) DEFAULT NULL COMMENT '邮箱',
  `subject` varchar(256) DEFAULT NULL COMMENT '主题',
  `message` text COMMENT '留言内容',
  `isRead` tinyint DEFAULT 0 COMMENT '是否已读',
  `reply` text COMMENT '回复内容',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='联系我们留言';

/*Table structure for table `post_comment` */

DROP TABLE IF EXISTS `post_comment`;

CREATE TABLE `post_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `postId` bigint NOT NULL COMMENT '帖子ID',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `content` text NOT NULL COMMENT '评论内容',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_postId` (`postId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子评论';

/*Table structure for table `member_benefit` */

DROP TABLE IF EXISTS `member_benefit`;

CREATE TABLE `member_benefit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(128) NOT NULL COMMENT '权益名称',
  `description` text COMMENT '权益描述',
  `icon` varchar(64) DEFAULT NULL COMMENT '图标',
  `levels` varchar(128) DEFAULT NULL COMMENT '适用等级',
  `sortOrder` int DEFAULT 0 COMMENT '排序',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员权益';

insert into `member_benefit`(`id`,`name`,`description`,`icon`,`levels`,`sortOrder`) values
(1,'优先购票','演唱会门票优先购买权','🎫','gold,diamond',1),
(2,'专属折扣','周边商品9折优惠','💎','gold,diamond',2),
(3,'生日礼物','会员生日当月赠送专属礼物','🎁','diamond',3),
(4,'见面会资格','优先获得粉丝见面会参与资格','⭐','diamond',4),
(5,'签名周边','定期抽取会员赠送签名周边','✍️','gold,diamond',5);

/*Table structure for table `growth_record` */

DROP TABLE IF EXISTS `growth_record`;

CREATE TABLE `growth_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `value` int NOT NULL COMMENT '成长值',
  `reason` varchar(256) DEFAULT NULL COMMENT '原因',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成长记录';

/*Table structure for table `consumption_record` */

DROP TABLE IF EXISTS `consumption_record`;

CREATE TABLE `consumption_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `type` varchar(32) DEFAULT NULL COMMENT '类型',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消费记录';

/*Table structure for table `points_record` */

DROP TABLE IF EXISTS `points_record`;

CREATE TABLE `points_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `points` int NOT NULL COMMENT '积分',
  `description` varchar(256) DEFAULT NULL COMMENT '描述',
  `type` varchar(32) DEFAULT NULL COMMENT '类型: gain/use',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分记录';

/*Table structure for table `user_profile` */

DROP TABLE IF EXISTS `user_profile`;

CREATE TABLE `user_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint NOT NULL COMMENT '用户ID',
  `phone` varchar(32) DEFAULT NULL COMMENT '手机号',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `province` varchar(64) DEFAULT NULL COMMENT '省份',
  `city` varchar(64) DEFAULT NULL COMMENT '城市',
  `district` varchar(64) DEFAULT NULL COMMENT '区县',
  `detailAddress` varchar(256) DEFAULT NULL COMMENT '详细地址',
  `bio` varchar(512) DEFAULT NULL COMMENT '个人简介',
  `gender` tinyint DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
  `memberLevel` varchar(32) DEFAULT 'normal' COMMENT '会员等级',
  `memberExpireTime` datetime DEFAULT NULL COMMENT '会员到期时间',
  `growthValue` int DEFAULT 0 COMMENT '成长值',
  `points` int DEFAULT 0 COMMENT '积分',
  `totalSpend` decimal(10,2) DEFAULT 0 COMMENT '累计消费',
  `inviteCode` varchar(32) DEFAULT NULL COMMENT '邀请码',
  `inviteCount` int DEFAULT 0 COMMENT '邀请人数',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
