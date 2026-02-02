/*
Navicat MySQL Data Transfer

Source Server         : 60
Source Server Version : 50720
Source Host           : 192.168.13.60:3306
Source Database       : mes_product_system

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2022-07-28 22:57:27
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `hum_organization`
-- ----------------------------
DROP TABLE IF EXISTS `hum_organization`;
CREATE TABLE `hum_organization` (
  `id` varchar(50) NOT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `code` varchar(50) DEFAULT NULL,
  `linker` varchar(50) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `parent_code` varchar(50) DEFAULT NULL,
  `parent_id` varchar(50) DEFAULT NULL,
  `parent_name` varchar(50) DEFAULT NULL,
  `responsibility` varchar(255) DEFAULT NULL,
  `short_name` varchar(50) DEFAULT NULL,
  `telephone` varchar(50) DEFAULT NULL,
  `is_company` int(1) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of hum_organization
-- ----------------------------
INSERT INTO `hum_organization` VALUES ('1', '手动创建', '1', 'asam', '', '西安航天自动化股份有限公司', null, null, null, '', '西安航天', '', '1', '', null, null, null, null, null, null, '1');
INSERT INTO `hum_organization` VALUES ('692d8ea5fb4208c344c2bf0d0c52549b', '', '1', 'yf', '', '研发中心', 'asam', '1', '西安航天自动化股份有限公司', '', '研发中心', '', '0', '', null, '4', '管理员', null, '2022-07-23 20:17:06', null, '1');

-- ----------------------------
-- Table structure for `hum_position`
-- ----------------------------
DROP TABLE IF EXISTS `hum_position`;
CREATE TABLE `hum_position` (
  `name` varchar(50) DEFAULT NULL,
  `duty` varchar(50) DEFAULT NULL,
  `conditions` varchar(255) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of hum_position
-- ----------------------------

-- ----------------------------
-- Table structure for `hum_post`
-- ----------------------------
DROP TABLE IF EXISTS `hum_post`;
CREATE TABLE `hum_post` (
  `name` varchar(50) DEFAULT NULL,
  `duty` varchar(50) DEFAULT NULL,
  `conditions` varchar(255) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of hum_post
-- ----------------------------

-- ----------------------------
-- Table structure for `hum_profession`
-- ----------------------------
DROP TABLE IF EXISTS `hum_profession`;
CREATE TABLE `hum_profession` (
  `name` varchar(50) DEFAULT NULL,
  `conditions` varchar(255) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of hum_profession
-- ----------------------------

-- ----------------------------
-- Table structure for `hum_user`
-- ----------------------------
DROP TABLE IF EXISTS `hum_user`;
CREATE TABLE `hum_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `remark` varchar(500) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `is_account` int(1) DEFAULT NULL,
  `job_number` varchar(50) DEFAULT NULL,
  `marry_state` varchar(10) DEFAULT NULL,
  `org_code` varchar(50) DEFAULT NULL,
  `org_id` varchar(50) DEFAULT NULL,
  `org_name` varchar(100) DEFAULT NULL,
  `photo` varchar(50) DEFAULT NULL,
  `politics_face_name` varchar(10) DEFAULT NULL,
  `residence_type` varchar(10) DEFAULT NULL,
  `sex` varchar(50) DEFAULT NULL,
  `user_name` varchar(50) DEFAULT NULL,
  `user_phone` varchar(50) DEFAULT NULL,
  `entry_date` date DEFAULT NULL,
  `login_name` varchar(50) DEFAULT NULL,
  `card_id` varchar(50) DEFAULT NULL,
  `account_location` varchar(255) DEFAULT NULL,
  `education_type` varchar(10) DEFAULT NULL,
  `technical_type` varchar(10) DEFAULT NULL,
  `user_profession` bigint(20) DEFAULT NULL,
  `user_post` bigint(20) DEFAULT NULL,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  `user_position` bigint(20) DEFAULT NULL,
  `native_place` varchar(255) DEFAULT NULL,
  `team_id` varchar(50) DEFAULT NULL,
  `team_name` varchar(50) DEFAULT NULL,
  `monitor_mark` int(11) DEFAULT NULL,
  `unique_code` varchar(100) DEFAULT NULL,
  `team_code` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of hum_user
-- ----------------------------
INSERT INTO `hum_user` VALUES ('1', '初始化手动创建', '1', null, null, '1', 'SYS_ADMIN', null, 'asam', '5a75b4af8310f5b27a6b3a3a16bd9d0d', '西安航天自动化股份有限公司', null, null, null, null, '系统管理员', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `hum_user` VALUES ('2', '初始化手动创建', '1', null, null, '1', 'SYS_SAFETY_ADMIN', null, 'asam', '5a75b4af8310f5b27a6b3a3a16bd9d0d', '西安航天自动化股份有限公司', null, null, null, null, '安全管理员', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `hum_user` VALUES ('3', '初始化手动创建', '1', null, null, '1', 'SYS_AUDITOR_ADMIN', null, 'asam', '5a75b4af8310f5b27a6b3a3a16bd9d0d', '西安航天自动化股份有限公司', null, null, null, null, '安全审计员', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `hum_user` VALUES ('4', '手动创建', '1', null, null, '1', 'admin', null, 'asam', '1', '西安航天自动化股份有限公司', null, null, '身份证', '男', '管理员', '18345678900', null, null, null, null, null, null, null, null, null, '4', null, '管理员', null, '2022-07-28 13:33:48', '3', null, null, null, null, null, '3d63cfba-04b6-4c54-a7d7-3c1f15454955', null);

-- ----------------------------
-- Table structure for `hum_user_org`
-- ----------------------------
DROP TABLE IF EXISTS `hum_user_org`;
CREATE TABLE `hum_user_org` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sys_user_id` bigint(20) DEFAULT NULL,
  `sys_org_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of hum_user_org
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_account`
-- ----------------------------
DROP TABLE IF EXISTS `sys_account`;
CREATE TABLE `sys_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `remark` varchar(500) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `user_name` varchar(50) DEFAULT NULL,
  `open_id` varchar(100) DEFAULT NULL,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  `login_name` varchar(20) DEFAULT NULL,
  `last_login_time` date DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `update_password_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `actable_uni_login_name` (`login_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_account
-- ----------------------------
INSERT INTO `sys_account` VALUES ('1', '初始化手动创建', '1', '96E79218965EB72C92A549DD5A330112', '1', '系统管理员', null, null, null, null, null, null, null, null, 'SYS_ADMIN', '2022-07-28', '192.168.13.171', null);
INSERT INTO `sys_account` VALUES ('2', '初始化手动创建', '1', '96E79218965EB72C92A549DD5A330112', '2', '安全管理员', null, null, null, null, null, null, null, null, 'SYS_SAFETY_ADMIN', null, null, null);
INSERT INTO `sys_account` VALUES ('3', '初始化手动创建', '1', '96E79218965EB72C92A549DD5A330112', '3', '安全审计员', null, null, null, null, null, null, null, null, 'SYS_AUDITOR_ADMIN', null, null, null);
INSERT INTO `sys_account` VALUES ('4', '初始化手动创建', '1', '96E79218965EB72C92A549DD5A330112', '4', '管理员', null, null, '4', null, '管理员', '2022-07-26 20:52:13', '2022-07-28 13:33:48', null, 'admin', '2022-07-28', '192.168.13.171', null);

-- ----------------------------
-- Table structure for `sys_account_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_account_role`;
CREATE TABLE `sys_account_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sys_account_id` bigint(11) DEFAULT NULL,
  `sys_role_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_account_role
-- ----------------------------
INSERT INTO `sys_account_role` VALUES ('1', '1', '1');
INSERT INTO `sys_account_role` VALUES ('2', '2', '2');
INSERT INTO `sys_account_role` VALUES ('3', '3', '3');
INSERT INTO `sys_account_role` VALUES ('4', '4', '4');

-- ----------------------------
-- Table structure for `sys_data`
-- ----------------------------
DROP TABLE IF EXISTS `sys_data`;
CREATE TABLE `sys_data` (
  `text_name` varchar(100) DEFAULT NULL,
  `sequence` varchar(2) DEFAULT NULL,
  `data_type_id` bigint(20) DEFAULT NULL,
  `data_type_code` varchar(50) DEFAULT NULL,
  `data_type_name` varchar(50) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_data
-- ----------------------------
INSERT INTO `sys_data` VALUES ('重要', '1', '4', 'USER_SECRET', '人员密级', '1', '4', null, '管理员', null, '2022-07-26 18:10:33', null, '1', null, null);
INSERT INTO `sys_data` VALUES ('核心', '2', '4', 'USER_SECRET', '人员密级', '2', '4', null, '管理员', null, '2022-07-26 18:10:51', null, '1', null, null);
INSERT INTO `sys_data` VALUES ('一般', '3', '4', 'USER_SECRET', '人员密级', '3', '4', null, '管理员', null, '2022-07-26 18:11:06', null, '1', null, null);

-- ----------------------------
-- Table structure for `sys_data_type`
-- ----------------------------
DROP TABLE IF EXISTS `sys_data_type`;
CREATE TABLE `sys_data_type` (
  `type_code` varchar(255) DEFAULT NULL,
  `type_name` varchar(50) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_data_type
-- ----------------------------
INSERT INTO `sys_data_type` VALUES ('USER_SECRET', '人员密级', '4', '4', null, '管理员', null, '2022-07-26 18:09:47', null, '1', null, null);

-- ----------------------------
-- Table structure for `sys_ip_filter`
-- ----------------------------
DROP TABLE IF EXISTS `sys_ip_filter`;
CREATE TABLE `sys_ip_filter` (
  `ip` varchar(255) DEFAULT NULL,
  `id` varchar(50) NOT NULL,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_ip_filter
-- ----------------------------
INSERT INTO `sys_ip_filter` VALUES ('127.0.0.2', '3ee082af740086f58f19d57a3770d344', null, '4', '2022-07-27 14:02:16', '2022-07-28 09:57:49', '1', '三员管理登录不拦截IP白名单', '管理员', '管理员', null);

-- ----------------------------
-- Table structure for `sys_moudle`
-- ----------------------------
DROP TABLE IF EXISTS `sys_moudle`;
CREATE TABLE `sys_moudle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `code` varchar(50) DEFAULT NULL,
  `parent_id` bigint(11) DEFAULT NULL,
  `parent_name` varchar(255) DEFAULT NULL,
  `action_url` varchar(100) DEFAULT NULL,
  `icon` varchar(50) DEFAULT NULL,
  `img_url` varchar(100) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `is_operation` int(11) DEFAULT NULL,
  `is_display` int(11) DEFAULT NULL,
  `enable` int(11) DEFAULT NULL,
  `system_id` bigint(20) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  `json_css` varchar(800) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_moudle
-- ----------------------------
INSERT INTO `sys_moudle` VALUES ('1', '菜单权限', null, null, '', '#', null, null, '1', '1', '0', '1', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('2', '系统设置', null, '1', '菜单权限', '#', 'fa fa-cogs', null, '2', '1', '0', '1', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('3', '账户管理', null, '2', '权限管理', '/account/list', 'fa fa-wheelchair-alt', null, '1', '1', '0', '1', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('4', '权限管理', null, '2', '系统设置', '#', 'fa fa-hand-spock-o', null, '1', '1', '0', '1', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('6', '修改', '修改', '3', null, null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('7', '重置密码', '重置密码', '3', null, null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('8', '分配角色', '分配角色', '3', null, null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('9', '角色管理', null, '4', '权限管理', '/role/list', 'fa fa-deaf', null, '2', '1', '0', '1', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-19 14:22:59', null, null, null);
INSERT INTO `sys_moudle` VALUES ('10', '新增', '新增', '9', '角色管理', null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-19 14:23:00', null, null, null);
INSERT INTO `sys_moudle` VALUES ('11', '修改', '修改', '9', '角色管理', null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-19 14:23:00', null, null, null);
INSERT INTO `sys_moudle` VALUES ('12', '删除', '删除', '9', '角色管理', null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-19 14:23:00', null, null, null);
INSERT INTO `sys_moudle` VALUES ('13', '查看', '查看', '9', '角色管理', null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-19 14:23:00', null, null, null);
INSERT INTO `sys_moudle` VALUES ('14', '分配权限', '分配权限', '9', '角色管理', null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-19 14:22:59', null, null, null);
INSERT INTO `sys_moudle` VALUES ('15', '查看权限', '查看权限', '55', '人员信息', null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('16', '分配权限', null, '9', '角色管理', '/role/roleToAuthority', 'fa fa-address-book', null, '1', '1', '0', '0', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-22 11:49:48', null, null, null);
INSERT INTO `sys_moudle` VALUES ('17', '查看权限', null, '55', '人员信息', '/user/viewRoleAuthority', null, null, '1', '1', '0', '0', '1', '1', '手动输入', null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('18', '模块管理', null, '4', '权限管理', '/module/list', 'fa fa-crop', null, '1', '1', '0', '1', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-22 14:04:48', null, null, null);
INSERT INTO `sys_moudle` VALUES ('19', '删除', '删除', '18', '模块管理', null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-22 14:04:48', null, null, null);
INSERT INTO `sys_moudle` VALUES ('20', '查看', '查看', '18', '模块管理', null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-22 14:04:48', null, null, null);
INSERT INTO `sys_moudle` VALUES ('21', '修改', '修改', '18', '模块管理', null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-22 14:04:48', null, null, null);
INSERT INTO `sys_moudle` VALUES ('22', '新增', '新增', '18', '模块管理', null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, '1', null, '管理员', null, '2022-07-22 14:04:48', null, null, null);
INSERT INTO `sys_moudle` VALUES ('28', '子系统维护', null, '4', '权限管理', '/sysInfo/list', 'fa fa-snowflake-o', null, '4', '1', '0', '1', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('29', '删除', '删除', '28', null, null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('30', '查看', '查看', '28', null, null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('31', '修改', '修改', '28', null, null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('32', '新增', '新增', '28', '', null, null, null, '1', '1', '1', '0', '1', '1', '手动输入', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('33', '数据字典', null, '2', '系统设置', '#', 'fa fa-building', '', '3', '1', '0', '1', '1', '1', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('34', '数据字典内容', null, '33', '数据字典', '/sysData/list', 'fa fa-file-archive-o', '', '1', '1', '0', '1', '1', '1', null, null, '2', null, '安全管理员', null, '2022-07-27 19:18:11', null, null, null);
INSERT INTO `sys_moudle` VALUES ('35', '新增', '新增', '34', '数据字典内容', null, null, null, '1', '1', '1', '0', '1', '1', null, null, '2', null, '安全管理员', null, '2022-07-27 19:18:11', null, null, null);
INSERT INTO `sys_moudle` VALUES ('36', '修改', '修改', '34', '数据字典内容', null, null, null, '1', '1', '1', '0', '1', '1', null, null, '2', null, '安全管理员', null, '2022-07-27 19:18:11', null, null, null);
INSERT INTO `sys_moudle` VALUES ('37', '删除', '删除', '34', '数据字典内容', null, null, null, '1', '1', '1', '0', '1', '1', null, null, '2', null, '安全管理员', null, '2022-07-27 19:18:11', null, null, null);
INSERT INTO `sys_moudle` VALUES ('38', '查看', '查看', '34', '数据字典内容', null, null, null, '1', '1', '1', '0', '1', '1', null, null, '2', null, '安全管理员', null, '2022-07-27 19:18:11', null, null, null);
INSERT INTO `sys_moudle` VALUES ('39', '数据字典类型', null, '33', '数据字典', '/sysDataType/list', 'fa fa-files-o', '', '2', '1', '0', '1', '1', '1', '', null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('40', '新增', '新增', '39', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('41', '修改', '修改', '39', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('42', '删除', '删除', '39', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('43', '查看', '查看', '39', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, null, null, null, null, null, null, null, null);
INSERT INTO `sys_moudle` VALUES ('44', '组织人员', null, '1', '菜单权限', '#', 'fa fa-address-card-o', '', '2', '1', '0', '1', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('50', '部门信息', null, '44', '组织人员', '/organization/list', 'fa fa-window-restore', '', '2', '1', '0', '1', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('51', '新增', '新增', '50', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('52', '修改', '修改', '50', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('53', '删除', '删除', '50', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('54', '查看', '查看', '50', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('55', '人员信息', null, '44', '组织人员', '/user/list', 'fa fa-address-book-o', '', '3', '1', '0', '1', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('56', '人员新增', null, '55', '人员信息', '/user/create', 'fa fa-address-book', '', '1', '1', '0', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('57', '人员修改', null, '55', '人员信息', '/user/update', 'fa fa-address-book', '', '1', '1', '0', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('58', '人员查看', null, '55', '人员信息', '/user/view', 'fa fa-address-book', '', '1', '1', '0', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('59', '新增', '新增', '55', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('60', '删除', '删除', '55', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('61', '修改', '修改', '55', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('62', '查看', '查看', '55', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('63', '日志管理', null, '2', '系统设置', '/busiOpsLog/list', 'fa fa-dedent', '', '5', '1', '0', '1', '1', '1', null, null, '4', null, '管理员', null, '2022-07-27 13:46:18', null, null, null);
INSERT INTO `sys_moudle` VALUES ('64', '职务管理', null, '44', '组织人员', '/position/list', 'fa fa-etsy', '', '3', '1', '0', '1', '1', '1', '', null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, '');
INSERT INTO `sys_moudle` VALUES ('65', '新增', '新增', '64', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('66', '修改', '修改', '64', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('67', '查看', '查看', '64', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('68', '删除', '删除', '64', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('69', '岗位管理', null, '44', '组织人员', '/post/list', 'fa fa-etsy', '', '4', '1', '0', '1', '1', '1', '', null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, '');
INSERT INTO `sys_moudle` VALUES ('70', '新增', '新增', '69', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('71', '修改', '修改', '69', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('72', '查看', '查看', '69', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('73', '删除', '删除', '69', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('74', '工种管理', null, '44', '组织人员', '/profession/list', 'fa fa-etsy', '', '5', '1', '0', '1', '1', '1', '', null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, '');
INSERT INTO `sys_moudle` VALUES ('75', '新增', '新增', '74', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('76', '修改', '修改', '74', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('77', '查看', '查看', '74', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('78', '删除', '删除', '74', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-26 20:15:58', null, null, null);
INSERT INTO `sys_moudle` VALUES ('79', '系统设置', null, '2', '系统设置', '#', 'fa fa-credit-card-alt', '', '4', '1', '0', '1', '1', '1', '', null, '4', null, '管理员', null, '2022-07-27 13:46:09', null, null, '');
INSERT INTO `sys_moudle` VALUES ('80', '三员白名单登录', null, '79', '系统设置', '/sysIpFilter/list', 'fa fa-linode', '', '1', '1', '0', '1', '1', '1', '', null, '4', null, '管理员', null, '2022-07-27 13:49:33', null, null, '');
INSERT INTO `sys_moudle` VALUES ('81', '新增', '新增', '80', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-27 13:49:33', null, null, null);
INSERT INTO `sys_moudle` VALUES ('82', '修改', '修改', '80', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-27 13:49:33', null, null, null);
INSERT INTO `sys_moudle` VALUES ('83', '删除', '删除', '80', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-27 13:49:33', null, null, null);
INSERT INTO `sys_moudle` VALUES ('84', '查看', '查看', '80', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-27 13:49:33', null, null, null);
INSERT INTO `sys_moudle` VALUES ('85', '系统变量', null, '79', '系统设置', '/sysVar/list', 'fa fa-quora', '', '2', '1', '0', '1', '1', '1', '', null, '4', null, '管理员', null, '2022-07-27 13:50:23', null, null, '');
INSERT INTO `sys_moudle` VALUES ('86', '新增', '新增', '85', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-27 13:50:23', null, null, null);
INSERT INTO `sys_moudle` VALUES ('87', '修改', '修改', '85', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-27 13:50:23', null, null, null);
INSERT INTO `sys_moudle` VALUES ('88', '删除', '删除', '85', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-27 13:50:23', null, null, null);
INSERT INTO `sys_moudle` VALUES ('89', '查看', '查看', '85', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-27 13:50:23', null, null, null);
INSERT INTO `sys_moudle` VALUES ('90', '打码', '打码', '55', null, null, null, null, '1', '1', '1', '0', '1', '1', null, null, '4', null, '管理员', null, '2022-07-28 18:08:45', null, null, null);

-- ----------------------------
-- Table structure for `sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `remark` varchar(500) DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `role_code` varchar(50) DEFAULT NULL,
  `role_level` int(11) DEFAULT NULL,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '初始化手动创建', '1', null, '系统管理员', 'SYS_ADMIN', '1', null, null, null, null, null, null, null);
INSERT INTO `sys_role` VALUES ('2', '初始化手动创建', '1', '', '安全管理员', 'SYS_SAFETY_ADMIN', '1', null, null, null, null, null, null, null);
INSERT INTO `sys_role` VALUES ('3', '初始化手动创建', '1', null, '安全审计员', 'SYS_AUDITOR_ADMIN', '1', null, null, null, null, null, null, null);
INSERT INTO `sys_role` VALUES ('4', '初始化手动创建', '1', null, '管理员', 'admin', '1', '2', '4', '安全管理员', '管理员', '2022-07-27 19:18:21', '2022-07-27 18:07:44', null);

-- ----------------------------
-- Table structure for `sys_role_moudle`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_moudle`;
CREATE TABLE `sys_role_moudle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sys_moudle_id` bigint(11) DEFAULT NULL,
  `sys_role_id` bigint(11) DEFAULT NULL,
  `system_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=935 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role_moudle
-- ----------------------------
INSERT INTO `sys_role_moudle` VALUES ('78', '44', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('79', '74', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('80', '78', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('81', '77', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('82', '76', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('83', '75', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('84', '69', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('85', '73', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('86', '72', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('87', '71', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('88', '70', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('89', '64', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('90', '68', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('91', '67', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('92', '66', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('93', '65', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('94', '55', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('95', '62', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('96', '61', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('97', '60', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('98', '59', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('99', '58', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('100', '57', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('101', '56', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('102', '17', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('103', '15', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('104', '50', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('105', '54', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('106', '53', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('107', '52', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('108', '51', '1', '1');
INSERT INTO `sys_role_moudle` VALUES ('109', '2', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('110', '63', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('111', '4', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('112', '28', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('113', '32', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('114', '31', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('115', '30', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('116', '29', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('117', '18', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('118', '22', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('119', '21', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('120', '20', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('121', '19', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('122', '9', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('123', '16', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('124', '14', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('125', '13', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('126', '12', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('127', '11', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('128', '10', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('129', '3', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('130', '8', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('131', '7', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('132', '6', '2', '1');
INSERT INTO `sys_role_moudle` VALUES ('133', '2', '3', '1');
INSERT INTO `sys_role_moudle` VALUES ('134', '63', '3', '1');
INSERT INTO `sys_role_moudle` VALUES ('857', '44', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('858', '74', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('859', '78', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('860', '77', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('861', '76', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('862', '75', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('863', '69', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('864', '73', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('865', '72', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('866', '71', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('867', '70', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('868', '64', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('869', '68', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('870', '67', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('871', '66', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('872', '65', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('873', '55', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('874', '90', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('875', '62', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('876', '61', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('877', '60', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('878', '59', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('879', '58', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('880', '57', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('881', '56', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('882', '17', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('883', '15', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('884', '50', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('885', '54', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('886', '53', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('887', '52', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('888', '51', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('889', '2', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('890', '79', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('891', '85', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('892', '89', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('893', '88', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('894', '87', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('895', '86', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('896', '80', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('897', '84', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('898', '83', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('899', '82', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('900', '81', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('901', '63', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('902', '33', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('903', '39', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('904', '43', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('905', '42', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('906', '41', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('907', '40', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('908', '34', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('909', '38', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('910', '37', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('911', '36', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('912', '35', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('913', '4', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('914', '28', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('915', '32', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('916', '31', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('917', '30', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('918', '29', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('919', '18', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('920', '22', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('921', '21', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('922', '20', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('923', '19', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('924', '9', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('925', '16', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('926', '14', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('927', '13', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('928', '12', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('929', '11', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('930', '10', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('931', '3', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('932', '8', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('933', '7', '4', '1');
INSERT INTO `sys_role_moudle` VALUES ('934', '6', '4', '1');

-- ----------------------------
-- Table structure for `sys_subsystem`
-- ----------------------------
DROP TABLE IF EXISTS `sys_subsystem`;
CREATE TABLE `sys_subsystem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `appid` varchar(100) DEFAULT NULL,
  `appsecret` varchar(100) DEFAULT NULL,
  `domain` varchar(50) DEFAULT NULL,
  `file_id` varchar(100) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `providername` varchar(50) DEFAULT NULL,
  `system_url` varchar(100) DEFAULT NULL,
  `enable` int(1) DEFAULT NULL,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_subsystem
-- ----------------------------
INSERT INTO `sys_subsystem` VALUES ('1', 'manager', 'manager', 'all', '', '综合业务系统', '西安航天', '/', '1', '1', null, '管理员', null, '2022-07-19 10:20:37', null, '1', null, null, '1');
INSERT INTO `sys_subsystem` VALUES ('2', 'mes', 'mes', 'all', null, 'MES', '西安航天', '/', '1', '1', null, '管理员', null, '2022-07-28 09:56:01', null, '1', null, null, '2');
INSERT INTO `sys_subsystem` VALUES ('3', 'wms', 'wms', 'all', null, 'WMS', '西安航天', '/', '1', '1', null, '管理员', null, '2022-07-28 09:55:59', null, '1', null, null, '3');

-- ----------------------------
-- Table structure for `sys_variable`
-- ----------------------------
DROP TABLE IF EXISTS `sys_variable`;
CREATE TABLE `sys_variable` (
  `var_code` varchar(255) DEFAULT NULL,
  `var_name` varchar(255) DEFAULT NULL,
  `var_spec` varchar(255) DEFAULT NULL,
  `data_type` varchar(255) DEFAULT NULL,
  `value_type` varchar(255) DEFAULT NULL,
  `var_value` varchar(255) DEFAULT NULL,
  `id` varchar(50) NOT NULL,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_variable
-- ----------------------------
INSERT INTO `sys_variable` VALUES ('CHECK_PWD_OVERDUE', '检验密码过期', '0-不检验，1-检验', null, null, '0', '7c0be1228743f1ab8613a23b1cb23091', '1', '4', '2022-07-27 14:00:56', null, '1', null, '管理员', null, null);
INSERT INTO `sys_variable` VALUES ('WHITELIST_FILTER', '白名单过滤', '0-关闭，1-开启', null, null, '0', 'a8f79a774953abe07c997a305290c4df', '1', null, null, null, '1', null, null, null, null);
INSERT INTO `sys_variable` VALUES ('CHECK_PWD_COMPLEX', '检验密码复杂度', '0-检验，1-不检验', null, null, '0', 'bf8b6806839b2bb2879313652094c74b', '1', '1', null, null, '1', null, null, null, null);
