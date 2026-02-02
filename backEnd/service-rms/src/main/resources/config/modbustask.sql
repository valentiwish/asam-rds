
-- ----------------------------
DROP TABLE IF EXISTS `dicpointtabletype`;
CREATE TABLE `dicpointtabletype`  (
  `poinTableType` int(11) NOT NULL,
  `pointTableTypeName` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `createDate` datetime(0) NULL DEFAULT NULL,
  `createPerson` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `updatePerson` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `updateDate` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`poinTableType`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dicpointtabletype
-- ----------------------------
INSERT INTO `dicpointtabletype` VALUES (201, '高区', '高区点表', '2021-02-03 10:16:41', '13511111111', NULL, NULL);

