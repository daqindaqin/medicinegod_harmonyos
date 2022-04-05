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

 Date: 27/03/2022 13:57:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for USERINFO
-- ----------------------------
DROP TABLE IF EXISTS `USERINFO`;
CREATE TABLE `USERINFO`  (
  `ID` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `LNAME` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `SNAME` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `PWD` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `HEAD` longblob NULL,
  `HEAD70` longblob NULL,
  `FRIEND` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `PHONE` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `MAIL` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `RGTIME` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `ONLINE` int(1) UNSIGNED NULL DEFAULT NULL,
  `HAS` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `VIP` int(1) UNSIGNED NULL DEFAULT NULL,
  `VIPYU` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
