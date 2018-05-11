/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50721
 Source Host           : localhost
 Source Database       : neuq

 Target Server Type    : MySQLF
 Target Server Version : 50721
 File Encoding         : utf-8

 Date: 02/14/2018 11:07:13 AM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `T_CustomerQueue`
-- ----------------------------
DROP TABLE IF EXISTS `T_CustomerQueue`;
CREATE TABLE `T_CustomerQueue` (
  `Auto_Id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自动增长',
  `Customer_Id` bigint(11) NOT NULL COMMENT '客户id',
  `Restaurant_Name` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '商家名字',
  `Restaurant_Location` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '商家地址 后面可以改成经纬度',
  `Longitude` decimal(10,7) NOT NULL DEFAULT '0.0000000' COMMENT '经度',
  `Latitude` decimal(10,7) NOT NULL DEFAULT '0.0000000' COMMENT '纬度',
  `Restaurant_People` int(2) NOT NULL DEFAULT '0' COMMENT '就餐人数',
  `Queue_Type` int(1) NOT NULL COMMENT '取号或者取号加占座',
  `Start_Time` datetime NOT NULL COMMENT '开始取号时间',
  `Arrive_Time` datetime NOT NULL COMMENT '到达餐厅时间',
  `Contact_Name` varchar(11) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '联系人',
  `Phone_Num` varchar(11) NOT NULL DEFAULT '' COMMENT '手机号',
  `Sex` char(2) CHARACTER SET utf8 NOT NULL DEFAULT '男' COMMENT '性别',
  `Comment` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '备注',
  `Fee` double(6,2) NOT NULL DEFAULT '0.00' COMMENT '费用',
  `Extra_Fee` double(6,0) NOT NULL DEFAULT '0' COMMENT '感谢费',
  `Queue_Status` int(1) NOT NULL COMMENT '客户发布的状态 0 正常 1卖家接受',
  `Update_Time` datetime NOT NULL COMMENT '最后一次更新时间',
  `Create_Time` datetime NOT NULL COMMENT '创建订单时间',
  PRIMARY KEY (`Auto_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `T_CustomerUser`
-- ----------------------------
DROP TABLE IF EXISTS `T_CustomerUser`;
CREATE TABLE `T_CustomerUser` (
  `User_Id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户的id唯一id号',
  `Open_Id` varchar(50) NOT NULL COMMENT '微信传来的唯一识别码',
  `AvatarUrl` varchar(200) NOT NULL DEFAULT '' COMMENT '头像url',
  `Nick_Name` varchar(50) NOT NULL COMMENT '头像昵称',
  `Gender` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户性别 性别 0：未知、1：男、2：女',
  `Phone_Num` varchar(11) NOT NULL DEFAULT '' COMMENT '手机号',
  `Credit_Value` int(11) NOT NULL DEFAULT '0' COMMENT '信用值',
  `Union_Id` varchar(100) NOT NULL COMMENT 'unionId',
  `Create_Time` datetime NOT NULL COMMENT '创建时间',
  `Update_Time` datetime NOT NULL COMMENT '最后一次更新时间',
  PRIMARY KEY (`User_Id`),
  KEY `Open_Id` (`Open_Id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `T_Order`
-- ----------------------------
DROP TABLE IF EXISTS `T_Order`;
CREATE TABLE `T_Order` (
  `Auto_Id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自动增长',
  `Order_Id` varchar(18) NOT NULL DEFAULT '' COMMENT '订单号',
  `Restaurant_Name` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '商家名字',
  `Restaurant_Location` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '商家地址 后面可以改成经纬度',
  `Longitude` decimal(10,7) NOT NULL DEFAULT '0.0000000' COMMENT '经度',
  `Latitude` decimal(10,7) NOT NULL DEFAULT '0.0000000' COMMENT '纬度',
  `Restaurant_People` int(2) NOT NULL DEFAULT '0' COMMENT '就餐人数',
  `Queue_Type` int(1) NOT NULL COMMENT '取号或者取号加占座',
  `Start_Time` datetime NOT NULL COMMENT '开始取号时间',
  `Arrive_Time` datetime NOT NULL COMMENT '到达餐厅时间',
  `Contact_Name` varchar(11) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '联系人',
  `Phone_Num` varchar(11) NOT NULL DEFAULT '' COMMENT '手机号',
  `Gender` tinyint(4) NOT NULL DEFAULT '1' COMMENT '性别 1男 2 女 3 未知',
  `Comment` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '备注',
  `Fee` double(6,2) NOT NULL DEFAULT '0.00' COMMENT '费用',
  `Extra_Fee` double(6,0) NOT NULL DEFAULT '0' COMMENT '感谢费',
  `Customer_Id` bigint(11) NOT NULL COMMENT '客户id',
  `Provider_Id` bigint(11) NOT NULL COMMENT '帮客的id',
  `Order_Status` int(1) NOT NULL DEFAULT '0' COMMENT '订单的状态 0 正常 1已完成 2 已取消 ',
  `Pay_Status` int(1) NOT NULL DEFAULT '0' COMMENT '0未支付 1已支付',
  `Update_Time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `Create_Time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建订单时间',
  PRIMARY KEY (`Auto_Id`),
  KEY `Order_Id` (`Order_Id`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `T_ProviderQueue`
-- ----------------------------
DROP TABLE IF EXISTS `T_ProviderQueue`;
CREATE TABLE `T_ProviderQueue` (
  `Auto_Id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自动增长',
  `Provider_Id` bigint(11) NOT NULL COMMENT '商家id',
  `Restaurant_Name` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '商家名字',
  `Restaurant_Location` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '商家地址 后面可以改成经纬度',
  `Longitude` decimal(10,7) NOT NULL DEFAULT '0.0000000' COMMENT '经度',
  `Latitude` decimal(10,7) NOT NULL DEFAULT '0.0000000' COMMENT '纬度',
  `Queue_Type` int(1) NOT NULL COMMENT '取号或者取号加占座',
  `Contact_Name` varchar(11) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '联系人',
  `Phone_Num` varchar(11) NOT NULL DEFAULT '' COMMENT '手机号',
  `Sex` char(2) CHARACTER SET utf8 NOT NULL DEFAULT '男' COMMENT '性别',
  `Comment` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '备注',
  `Fee` double(6,2) NOT NULL DEFAULT '0.00' COMMENT '费用',
  `Extra_Fee` double(6,0) NOT NULL DEFAULT '0' COMMENT '感谢费',
  `Queue_Status` int(1) NOT NULL COMMENT '客户发布的状态 0 正常 1卖家接受',
  `Update_Time` datetime NOT NULL COMMENT '最后一次更新时间',
  `Create_Time` datetime NOT NULL COMMENT '创建订单时间',
  PRIMARY KEY (`Auto_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
--  Table structure for `T_ProviderUser`
-- ----------------------------
DROP TABLE IF EXISTS `T_ProviderUser`;
CREATE TABLE `T_ProviderUser` (
  `User_Id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户的id唯一id号 自动增长',
  `Open_Id` varchar(50) NOT NULL COMMENT '微信传来的唯一识别码',
  `AvatarUrl` varchar(200) NOT NULL DEFAULT '' COMMENT '头像url',
  `Nick_Name` varchar(50) NOT NULL COMMENT '头像昵称',
  `Gender` tinyint(4) NOT NULL COMMENT '用户性别 性别 0：未知、1：男、2：女',
  `Phone_Num` varchar(11) NOT NULL DEFAULT '' COMMENT '手机号',
  `Credit_Value` int(11) NOT NULL DEFAULT '0' COMMENT '信用值',
  `Union_Id` varchar(100) NOT NULL COMMENT 'unionId',
  `Create_Time` datetime NOT NULL COMMENT '创建时间',
  `Update_Time` datetime NOT NULL COMMENT '最后一次更新时间',
  PRIMARY KEY (`User_Id`),
  KEY `Open_Id` (`Open_Id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `T_User`
-- ----------------------------
DROP TABLE IF EXISTS `T_User`;
CREATE TABLE `T_User` (
  `User_Id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `Open_Id` varchar(100) NOT NULL COMMENT 'openId',
  `AvatarUrl` varchar(200) NOT NULL COMMENT '头像url',
  `Nick_Name` varchar(50) NOT NULL COMMENT '用户昵称',
  `Gender` tinyint(4) NOT NULL COMMENT '用户性别 性别 0：未知、1：男、2：女',
  `city` varchar(50) NOT NULL COMMENT '用户城市',
  `language` varchar(255) NOT NULL COMMENT '语言',
  `province` varchar(50) NOT NULL COMMENT '用户省份',
  `country` varchar(50) NOT NULL COMMENT '用户国家',
  `Union_Id` varchar(100) DEFAULT NULL COMMENT 'unionId',
  `create_Value` int(11) NOT NULL DEFAULT '0' COMMENT '信用值',
  `money` float NOT NULL DEFAULT '0' COMMENT '账户余额',
  `create_Time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_Time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '跟新时间',
  PRIMARY KEY (`User_Id`),
  KEY `idx_create_time` (`create_Time`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
--  Table structure for `comment`
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `commentId` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论id',
  `postId` bigint(20) NOT NULL COMMENT '文章id',
  `avatarUrl` varchar(200) NOT NULL COMMENT '头像url',
  `userId` bigint(20) NOT NULL COMMENT '创建者id',
  `like_count` int(4) NOT NULL DEFAULT '0' COMMENT '点赞数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`commentId`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_like_count` (`like_count`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='评论表';

-- ----------------------------
--  Table structure for `post`
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `postId` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文章id',
  `userId` bigint(20) NOT NULL COMMENT '创建者id',
  `title` varchar(30) NOT NULL COMMENT '文章标题',
  `content` varchar(120) NOT NULL COMMENT '文章内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `secret` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否匿名 0：匿名 1：非匿名',
  `comment_count` int(4) NOT NULL DEFAULT '0' COMMENT '评论数量',
  `like_count` int(4) NOT NULL DEFAULT '0' COMMENT '点赞数量',
  PRIMARY KEY (`postId`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_like_count` (`like_count`),
  KEY `idx_comment_count` (`comment_count`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='文章表';

-- ----------------------------
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userId` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `openId` varchar(100) NOT NULL COMMENT 'openId',
  `avatarUrl` varchar(200) NOT NULL COMMENT '头像url',
  `nickName` varchar(50) NOT NULL COMMENT '用户昵称',
  `gender` varchar(4) NOT NULL COMMENT '用户性别 性别 0：未知、1：男、2：女',
  `city` varchar(50) NOT NULL COMMENT '用户城市',
  `language` varchar(255) NOT NULL COMMENT '语言',
  `province` varchar(50) NOT NULL COMMENT '用户省份',
  `country` varchar(50) NOT NULL COMMENT '用户国家',
  `unionId` varchar(100) NOT NULL COMMENT 'unionId',
  `jwUser` varchar(100) NOT NULL COMMENT '教务系统用户名',
  `jwPwd` varchar(100) NOT NULL COMMENT '教务系统密码',
  PRIMARY KEY (`userId`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1011 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
--  Table structure for `user_like_post`
-- ----------------------------
DROP TABLE IF EXISTS `user_like_post`;
CREATE TABLE `user_like_post` (
  `postId` bigint(20) NOT NULL COMMENT '文章id',
  `userId` bigint(20) NOT NULL COMMENT '用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`postId`,`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='点赞表';

SET FOREIGN_KEY_CHECKS = 1;
