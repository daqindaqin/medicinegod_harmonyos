/*
 Navicat Premium Data Transfer

 Source Server         : 阿里云ubuntu
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 139.224.48.87:3306
 Source Schema         : mg

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 14/04/2022 21:46:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for daqin
-- ----------------------------
DROP TABLE IF EXISTS `daqin`;
CREATE TABLE `daqin`  (
  `KEYID` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `NAME` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IMAGE` longblob NOT NULL,
  `DESCRIPTION` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `OUTDATE` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `OTC` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `BARCODE` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `YU` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `ELABEL` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `LOVE` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `SHARE` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `MUSE` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `COMPANY` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`KEYID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
