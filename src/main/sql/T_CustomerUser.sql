T_ProviderUser.sql/*
 Navicat Premium Data Transfer

 Source Server         : ChatRobot
 Source Server Type    : MySQL
 Source Server Version : 50720
 Source Host           : localhost
 Source Database       : neuq_info

 Target Server Type    : MySQL
 Target Server Version : 50720
 File Encoding         : utf-8

 Date: 12/22/2017 22:41:40 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `T_CustomerUser`
-- ----------------------------
DROP TABLE IF EXISTS `T_CustomerUser`;
CREATE TABLE `T_CustomerUser` (
  `User_Id` bigint(20) NOT NULL COMMENT '用户的id唯一id号',
  `Open_Id` varchar(50) NOT NULL COMMENT '微信传来的唯一识别码',
  `AvatarUrl` varchar(200) NOT NULL DEFAULT '' COMMENT '头像url',
  `Nick_Name` varchar(50) NOT NULL COMMENT '头像昵称',
  `Gender` varchar(4) NOT NULL COMMENT '用户性别 性别 0：未知、1：男、2：女',
  `Phone_Num` varchar(11) NOT NULL DEFAULT '' COMMENT '手机号',
  `Credit_Value` int(11) NOT NULL DEFAULT '0' COMMENT '信用值',
  `Union_Id` varchar(100) NOT NULL COMMENT 'unionId',
  `Create_Time` datetime NOT NULL COMMENT '创建时间',
  `Update_Time` datetime NOT NULL COMMENT '最后一次更新时间',
  PRIMARY KEY (`User_Id`),
  KEY `Open_Id` (`Open_Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
