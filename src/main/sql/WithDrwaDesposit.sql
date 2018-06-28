/*
 Navicat Premium Data Transfer

 Source Server         : ChatRobot
 Source Server Type    : MySQL
 Source Server Version : 50720
 Source Host           : localhost
 Source Database       : neuq_info

 Target Server Type    : MySQL
 Target Server Version : 50720
 File Encoding         : utf-8

 Date: 12/22/2017 22:41:55 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `T_Order`
-- ----------------------------
DROP TABLE IF EXISTS `T_Order`;
CREATE TABLE `T_Order` (
  `AutoId` BIGINT(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自动增长',
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
  `Sex` char(2) CHARACTER SET utf8 NOT NULL DEFAULT '男' COMMENT '性别',
  `Comment` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '备注',
  `Fee` double(6,2) NOT NULL DEFAULT '0.00' COMMENT '费用',
  `Extra_Fee` double(6,0) NOT NULL DEFAULT '0' COMMENT '感谢费',
  `Customer_Id` bigint(11) NOT NULL COMMENT '客户id',
  `Provider_Id` bigint(11) NOT NULL COMMENT '帮客的id',
  `Order_Status` int(1) NOT NULL COMMENT '订单的状态 0 正常 1已完成 2 已取消 ',
  `Update_Time` datetime NOT NULL COMMENT '最后一次更新时间',
  `Create_Time` datetime NOT NULL COMMENT '创建订单时间',
  PRIMARY KEY (`Auto_Id`),
  KEY `Order_Id` (`Order_Id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=latin1;

SET FOREIGN_KEY_CHECKS = 1;
