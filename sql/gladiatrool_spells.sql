/*
 Navicat Premium Data Transfer

 Source Server         : Local
 Source Server Type    : MySQL
 Source Server Version : 50731
 Source Host           : localhost:3306
 Source Schema         : help_game

 Target Server Type    : MySQL
 Target Server Version : 50731
 File Encoding         : 65001

 Date: 10/03/2023 02:34:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gladiatrool_spells
-- ----------------------------
DROP TABLE IF EXISTS `gladiatrool_spells`;
CREATE TABLE `gladiatrool_spells`  (
  `id` int(11) NOT NULL,
  `playerId` int(11) NULL DEFAULT NULL,
  `fullMorphId` int(11) NULL DEFAULT NULL,
  `spells` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_PlayerId`(`playerId`) USING BTREE,
  INDEX `fk_FullMorphId`(`fullMorphId`) USING BTREE
) ENGINE = MyISAM CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gladiatrool_spells
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
