/*
SQLyog Enterprise v12.2.6 (64 bit)
MySQL - 5.7.21 : Database - robot_system
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`robot_system` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `robot_system`;

/*Table structure for table `hum_organization` */

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

/*Data for the table `hum_organization` */

insert  into `hum_organization`(`id`,`remark`,`state`,`code`,`linker`,`name`,`parent_code`,`parent_id`,`parent_name`,`responsibility`,`short_name`,`telephone`,`is_company`,`address`,`create_id`,`update_id`,`create_user`,`update_user`,`create_time`,`update_time`,`secret_level_no`) values 
('0abf15ec731485fb58529747380bc70f',NULL,1,'210',NULL,'210所','',NULL,'',NULL,'210所',NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('1','手动创建',1,'asam','','西安航天自动化股份有限公司',NULL,NULL,NULL,'','西安航天','',1,'',NULL,NULL,NULL,NULL,NULL,NULL,1),
('1c01f71cdaca44e0ada42ccaad6f7a62',NULL,1,'workshop_two',NULL,'二车间','210','0abf15ec731485fb58529747380bc70f','210所','','二车间',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('297a9c9f7195c027209fdf2d9e9a38ea',NULL,1,'workshopten',NULL,'生产中心','smart_equip_division','e1a4385b48f10d551ea59ae235467850','智能装备事业部','十车间','生产中心',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('29ff91a245f33f2f3453698544a8aeee',NULL,1,'CRAFT_ROOM',NULL,'工艺室','210','0abf15ec731485fb58529747380bc70f','210所','','工艺室',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('2bf2505f6386daf77c0d60580b19f732',NULL,1,'information_center',NULL,'信息技术中心','210','0abf15ec731485fb58529747380bc70f','210所','信息技术中心','信息技术中心',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('2f3970d37d861777e83e34c6835ae72d',NULL,1,'jicecenter',NULL,'计量测试中心','210','0abf15ec731485fb58529747380bc70f','210所','计量测试中心','计量测试中心',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('4d6186395ea8f5a0fcb5368b89abf70a',NULL,1,'wuzi',NULL,'物资处','210','0abf15ec731485fb58529747380bc70f','210所','物资管理','物资处',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('692d8ea5fb4208c344c2bf0d0c52549b','',1,'yf','','研发中心','asam','1','西安航天自动化股份有限公司','基地保障中心','研发中心','',0,'',NULL,4,'管理员',NULL,'2022-07-23 20:17:06',NULL,1),
('726dad58ac2a88b7f4eb75b516f706fb',NULL,1,'base_support_center',NULL,'基地保障中心','210','0abf15ec731485fb58529747380bc70f','210所','发展计划处','基地保障中心',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('799b3700e0312a4bcf02ec137a02039f',NULL,1,'33',NULL,'发展计划处','210','0abf15ec731485fb58529747380bc70f','210所','','发展计划处',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('828d8c8df0d2e398768b647396a48e69',NULL,1,'keyan',NULL,'科研生产处','210','0abf15ec731485fb58529747380bc70f','210所','','科研生产处',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('a68746daf1de0534c0fcedc77425a281',NULL,1,'jianyan',NULL,'检验室','210','0abf15ec731485fb58529747380bc70f','210所','四车间','检验室',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('d4bdc19e1104a6eb52a0e56ddefbc1fd',NULL,1,'workshop_four',NULL,'四车间','210','0abf15ec731485fb58529747380bc70f','210所','复合材料研究中心','四车间',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('dee498483bd8347e36b1066556d58545',NULL,1,'fucai_center',NULL,'复合材料研究中心','210','0abf15ec731485fb58529747380bc70f','210所','智能装备事业部','复合材料研究中心',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('e1a4385b48f10d551ea59ae235467850',NULL,1,'smart_equip_division',NULL,'智能装备事业部','210','0abf15ec731485fb58529747380bc70f','210所','','智能装备事业部',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
('f6d074201ffd3f60d19f32ad28555d1a',NULL,1,'army_projectoffice',NULL,'军品项目办','smart_equip_division','e1a4385b48f10d551ea59ae235467850','智能装备事业部',NULL,'军品项目办',NULL,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

/*Table structure for table `hum_position` */

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

/*Data for the table `hum_position` */

/*Table structure for table `hum_post` */

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

/*Data for the table `hum_post` */

/*Table structure for table `hum_profession` */

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

/*Data for the table `hum_profession` */

/*Table structure for table `hum_user` */

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
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8;

/*Data for the table `hum_user` */

insert  into `hum_user`(`id`,`remark`,`state`,`birthday`,`email`,`is_account`,`job_number`,`marry_state`,`org_code`,`org_id`,`org_name`,`photo`,`politics_face_name`,`residence_type`,`sex`,`user_name`,`user_phone`,`entry_date`,`login_name`,`card_id`,`account_location`,`education_type`,`technical_type`,`user_profession`,`user_post`,`create_id`,`update_id`,`create_user`,`update_user`,`create_time`,`update_time`,`secret_level_no`,`user_position`,`native_place`,`team_id`,`team_name`,`monitor_mark`,`unique_code`,`team_code`) values 
(1,'初始化手动创建',1,NULL,NULL,1,'SYS_ADMIN',NULL,'asam','5a75b4af8310f5b27a6b3a3a16bd9d0d','西安航天自动化股份有限公司',NULL,NULL,NULL,NULL,'系统管理员',NULL,NULL,'SYS_ADMIN',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(2,'初始化手动创建',1,NULL,NULL,1,'SYS_SAFETY_ADMIN',NULL,'asam','5a75b4af8310f5b27a6b3a3a16bd9d0d','西安航天自动化股份有限公司',NULL,NULL,NULL,NULL,'安全管理员',NULL,NULL,'SYS_SAFETY_ADMIN',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(3,'初始化手动创建',1,NULL,NULL,1,'SYS_AUDITOR_ADMIN',NULL,'asam','5a75b4af8310f5b27a6b3a3a16bd9d0d','西安航天自动化股份有限公司',NULL,NULL,NULL,NULL,'安全审计员',NULL,NULL,'SYS_AUDITOR_ADMIN',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(4,'手动创建',1,NULL,NULL,1,'admin',NULL,'asam','1','西安航天自动化股份有限公司',NULL,NULL,'身份证','男','管理员','18345678900',NULL,'admin',NULL,NULL,NULL,NULL,NULL,NULL,NULL,4,NULL,'管理员',NULL,'2022-08-25 09:17:45',3,NULL,NULL,'79d0857e8802920e3aea45dd0aca336a','电修班',1,'123456',NULL),
(6,'',1,NULL,'',NULL,'admin1','','210','0abf15ec731485fb58529747380bc70f','210所','','','','','管理员1','13511111111',NULL,'admin1','','',NULL,NULL,3,6,NULL,1,'','13511111111',NULL,'2021-06-01 14:20:21',1,17,'',NULL,NULL,NULL,'6aab2a0f-317e-435d-87b4-3b1a809e4ac7',NULL),
(41,'',1,NULL,'',NULL,'wangyonggang','','keyan','828d8c8df0d2e398768b647396a48e69','科研生产处','',NULL,'','男','王永刚',NULL,NULL,'wangyonggang',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-05-17 09:22:17',NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,NULL),
(42,'',1,NULL,'',NULL,'luohui','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','男','罗辉','',NULL,'luohui','','',NULL,NULL,5,NULL,1,1,'13511111111','13511111111','2021-06-22 11:17:20','2021-06-22 11:18:17',1,NULL,'',NULL,'车一班',0,NULL,'workshopfour_che001'),
(43,'',1,NULL,'',NULL,'yuanxiaoqiang','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','男','袁晓强','',NULL,'yuanxiaoqiang','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-06-22 11:19:21','2021-08-04 19:46:13',NULL,17,'','535857d352089c8785f42a992a83e6cf','车二班',0,NULL,'workshopfour_che002'),
(44,'',1,NULL,'',NULL,'jianyan001','','jianyan','a68746daf1de0534c0fcedc77425a281','检验室','',NULL,'','男','检验员1',NULL,NULL,'jianyan001',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-06-22 11:20:08',NULL,NULL,NULL,'','463d95d229fe9d509c113926ae7bd555','精测组',0,NULL,'workshop_jianyan002'),
(45,'',1,NULL,'',NULL,'wuzi001','','wuzi','4d6186395ea8f5a0fcb5368b89abf70a','物资处','','','','男','物资人员','',NULL,'wuzi001','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-06-22 11:24:34','2021-08-18 14:25:37',NULL,NULL,'','0453d66e3337fb4e6ca40977967d23d6','下料一班',0,NULL,'wuzi_xialiao001'),
(46,'',1,NULL,'',NULL,'zhangzhizheng','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','男','张志正',NULL,NULL,'zhangzhizheng',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-06-22 11:51:53',NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,NULL),
(47,'',1,NULL,'',NULL,'yanglihe','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','男','杨主任',NULL,NULL,'yanglihe',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-06-22 11:53:12',NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,NULL),
(48,'',1,NULL,'',NULL,'dongqiang','','33','799b3700e0312a4bcf02ec137a02039f','发展计划处','',NULL,'','男','董强',NULL,NULL,'dongqiang',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-06-22 11:54:06',NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,NULL),
(49,'',1,NULL,'',NULL,'gaobo','','information_center','2bf2505f6386daf77c0d60580b19f732','信息技术中心','','','','男','高博','',NULL,'gaobo','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-08-04 19:39:35','2021-08-04 19:40:33',NULL,17,'',NULL,NULL,NULL,NULL,NULL),
(50,'',1,NULL,'',NULL,'gongsiming','','information_center','2bf2505f6386daf77c0d60580b19f732','信息技术中心','',NULL,'','女','宫思明',NULL,NULL,'gongsiming',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-04 19:41:28',NULL,NULL,16,'',NULL,NULL,NULL,NULL,NULL),
(51,'',1,NULL,'',NULL,'yaodan','','information_center','2bf2505f6386daf77c0d60580b19f732','信息技术中心','',NULL,'','女','姚丹',NULL,NULL,'yaodan',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-04 19:41:55',NULL,NULL,16,'',NULL,NULL,NULL,NULL,NULL),
(52,'',1,NULL,'',NULL,'hechao','','information_center','2bf2505f6386daf77c0d60580b19f732','信息技术中心','',NULL,'','男','何超',NULL,NULL,'hechao',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-04 19:42:17',NULL,1,16,'',NULL,NULL,NULL,'c76d4875-d5cd-4e11-9431-bea355c70e9f',NULL),
(53,'',1,NULL,'',NULL,'qutao','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','男','曲涛',NULL,NULL,'qutao',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-04 19:42:50',NULL,NULL,17,'',NULL,NULL,NULL,NULL,NULL),
(54,'',1,NULL,'',NULL,'yanglihe','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','男','杨立合',NULL,NULL,'yanglihe',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-04 19:43:23',NULL,NULL,17,'',NULL,NULL,NULL,NULL,NULL),
(55,'',1,NULL,'',NULL,'linhai','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','男','蔺海',NULL,NULL,'linhai',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-04 19:44:01',NULL,NULL,16,'',NULL,NULL,NULL,NULL,NULL),
(56,'',1,NULL,'',NULL,'weijiangna','','wuzi','4d6186395ea8f5a0fcb5368b89abf70a','物资处','','','','女','魏江娜','',NULL,'weijiangna','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-08-04 19:44:53','2021-08-18 14:25:29',NULL,16,'','0453d66e3337fb4e6ca40977967d23d6','下料一班',0,NULL,'wuzi_xialiao001'),
(57,'',1,NULL,'',NULL,'zhangzhipeng','','keyan','828d8c8df0d2e398768b647396a48e69','科研生产处','',NULL,'','男','张志鹏',NULL,NULL,'zhangzhipeng',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-04 19:45:33',NULL,NULL,17,'',NULL,NULL,NULL,NULL,NULL),
(58,'',1,NULL,'',NULL,'wanghaifeng','','jianyan','a68746daf1de0534c0fcedc77425a281','检验室','',NULL,'','男','王海峰',NULL,NULL,'wanghaifeng',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 11:34:22',NULL,NULL,17,'','463d95d229fe9d509c113926ae7bd555','精测组',0,NULL,'workshop_jianyan002'),
(59,'',1,NULL,'',NULL,'zhangdong','','jianyan','a68746daf1de0534c0fcedc77425a281','检验室','',NULL,'','男','张东',NULL,NULL,'zhangdong',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 11:34:45',NULL,NULL,17,'','463d95d229fe9d509c113926ae7bd555','精测组',0,NULL,'workshop_jianyan002'),
(60,'',1,NULL,'',NULL,'jiaxuerui','','jianyan','a68746daf1de0534c0fcedc77425a281','检验室','','','','女','贾学瑞','',NULL,'jiaxuerui','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-08-05 11:35:20','2021-08-05 14:09:39',NULL,16,'','463d95d229fe9d509c113926ae7bd555','精测组',0,NULL,'workshop_jianyan002'),
(61,'',1,NULL,'',NULL,'gaokun','','keyan','828d8c8df0d2e398768b647396a48e69','科研生产处','',NULL,'','男','高坤',NULL,NULL,'gaokun',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 11:45:16',NULL,NULL,17,'',NULL,NULL,NULL,NULL,NULL),
(62,'',1,NULL,'',NULL,'wangweiqin','','keyan','828d8c8df0d2e398768b647396a48e69','科研生产处','','','','女','王伟芹','',NULL,'wangweiqin','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-08-05 11:45:44','2021-08-05 14:10:21',NULL,17,'',NULL,NULL,NULL,NULL,NULL),
(63,'',1,NULL,'',NULL,'shengchunxiang','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','男','盛春翔','',NULL,'shengchunxiang','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-08-05 13:54:11','2021-08-19 16:11:26',NULL,17,'','535857d352089c8785f42a992a83e6cf','车二班',0,NULL,'workshopfour_che002'),
(64,'',1,NULL,'',NULL,'jiguangping','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','姬广平',NULL,NULL,'jiguangping',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 13:54:52',NULL,NULL,17,'','535857d352089c8785f42a992a83e6cf','车二班',0,NULL,'workshopfour_che002'),
(65,'',1,NULL,'',NULL,'kangxiaojun','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','男','康小军','',NULL,'kangxiaojun','','',NULL,NULL,NULL,NULL,1,1,'13511111111','管理员1','2021-08-05 13:55:16','2022-06-21 10:15:24',1,16,'',NULL,'铣工班',1,'','workshopfour_xi001'),
(66,'',1,NULL,'',NULL,'jiangyonghui','','wuzi','4d6186395ea8f5a0fcb5368b89abf70a','物资处','','','','男','姜永辉','',NULL,'jiangyonghui','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-08-05 13:56:10','2021-08-18 14:25:22',NULL,17,'','0453d66e3337fb4e6ca40977967d23d6','下料一班',0,NULL,'wuzi_xialiao001'),
(67,'',1,NULL,'',NULL,'liubing','','wuzi','4d6186395ea8f5a0fcb5368b89abf70a','物资处','','','','男','刘兵','',NULL,'liubing','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-08-05 13:56:36','2021-08-18 14:25:12',NULL,17,'','0453d66e3337fb4e6ca40977967d23d6','下料一班',0,NULL,'wuzi_xialiao001'),
(68,'',1,NULL,'',NULL,'liuyongwei','','wuzi','4d6186395ea8f5a0fcb5368b89abf70a','物资处','','','','男','刘勇为','',NULL,'liuyongwei','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-08-05 13:57:51','2021-08-18 14:25:02',NULL,16,'','0453d66e3337fb4e6ca40977967d23d6','下料一班',0,NULL,'wuzi_xialiao001'),
(69,'',1,NULL,'',NULL,'xuyufei','','wuzi','4d6186395ea8f5a0fcb5368b89abf70a','物资处','','','','男','许育飞','',NULL,'xuyufei','','',NULL,NULL,NULL,NULL,1,4,'13511111111','管理员','2021-08-05 14:04:56','2022-09-23 16:49:30',1,16,'','0453d66e3337fb4e6ca40977967d23d6','下料一班',1,NULL,'wuzi_xialiao001'),
(70,'',1,NULL,'',NULL,'lifan','','jianyan','a68746daf1de0534c0fcedc77425a281','检验室','',NULL,'','男','李凡',NULL,NULL,'lifan',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 14:08:15',NULL,NULL,16,'','463d95d229fe9d509c113926ae7bd555','精测组',0,NULL,'workshop_jianyan002'),
(71,'',1,NULL,'',NULL,'liujun','','jianyan','a68746daf1de0534c0fcedc77425a281','检验室','',NULL,'','男','刘俊',NULL,NULL,'liujun',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 14:08:39',NULL,NULL,16,'','463d95d229fe9d509c113926ae7bd555','精测组',0,NULL,'workshop_jianyan002'),
(72,'',1,NULL,'',NULL,'lilei','','jianyan','a68746daf1de0534c0fcedc77425a281','检验室','',NULL,'','男','李磊',NULL,NULL,'lilei',NULL,'',NULL,NULL,NULL,NULL,1,4,'13511111111','管理员','2021-08-05 14:08:57','2022-09-26 10:47:56',NULL,16,'','463d95d229fe9d509c113926ae7bd555','精测组',1,NULL,'workshop_jianyan002'),
(73,'',1,NULL,'',NULL,'zhouying','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','女','周英',NULL,NULL,'zhouying',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 14:11:11',NULL,NULL,16,'',NULL,NULL,NULL,NULL,NULL),
(74,'',1,NULL,'',NULL,'zhouxun','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','男','周旬',NULL,NULL,'zhouxun',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 14:11:45',NULL,NULL,16,'',NULL,NULL,NULL,NULL,NULL),
(75,'',1,NULL,'',NULL,'weilin','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','男','魏琳',NULL,NULL,'weilin',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 14:12:12',NULL,NULL,16,'',NULL,NULL,NULL,NULL,NULL),
(76,'',1,NULL,'',NULL,'hewei','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','女','何薇',NULL,NULL,'hewei',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 14:12:42',NULL,NULL,16,'',NULL,NULL,NULL,NULL,NULL),
(77,'',1,NULL,'',NULL,'linbinhui','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','女','连斌慧',NULL,NULL,'linbinhui',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 14:13:25',NULL,NULL,16,'',NULL,NULL,NULL,NULL,NULL),
(78,'',1,NULL,'',NULL,'shixiaoxia','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','女','史小霞',NULL,NULL,'shixiaoxia',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 14:13:57',NULL,NULL,16,'',NULL,NULL,NULL,NULL,NULL),
(79,'',1,NULL,'',NULL,'lijuan','','CRAFT_ROOM','29ff91a245f33f2f3453698544a8aeee','工艺室','',NULL,'','女','李娟',NULL,NULL,'lijuan',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 14:14:27',NULL,NULL,16,'',NULL,NULL,NULL,NULL,NULL),
(80,'',1,NULL,'',NULL,'sunhuangyan','','workshop_two','1c01f71cdaca44e0ada42ccaad6f7a62','二车间','',NULL,'','男','孙黄炎',NULL,NULL,'sunhuangyan',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 14:15:02',NULL,NULL,17,'',NULL,NULL,NULL,NULL,NULL),
(81,'',1,NULL,'',NULL,'lizhiyang','','workshop_two','1c01f71cdaca44e0ada42ccaad6f7a62','二车间','',NULL,'','男','李志阳',NULL,NULL,'lizhiyang',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-05 14:15:35',NULL,NULL,16,'',NULL,NULL,NULL,NULL,NULL),
(82,'',1,NULL,'',NULL,'fangliming','','workshop_two','1c01f71cdaca44e0ada42ccaad6f7a62','二车间','',NULL,'','男','方立鸣',NULL,NULL,'fangliming',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-10 15:00:22',NULL,NULL,NULL,'',NULL,NULL,NULL,NULL,NULL),
(83,'',1,NULL,'',NULL,'xuwangping','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','男','许王平','',NULL,'xuwangping','','',NULL,NULL,NULL,NULL,1,1,'13511111111','管理员1','2021-08-18 18:27:02','2022-06-22 17:07:35',1,16,'','32080161a14c68028a8419b6f38390ac','车一班',1,'','workshopfour_che001'),
(84,'',1,NULL,'',NULL,'xiongyong','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','熊勇',NULL,NULL,'xiongyong',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:27:26',NULL,1,NULL,'',NULL,'车一班',0,NULL,'workshopfour_che001'),
(85,'',1,NULL,'',NULL,'limingjun','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','李明军',NULL,NULL,'limingjun',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:29:50',NULL,1,16,'',NULL,'车一班',0,NULL,'workshopfour_che001'),
(86,'',1,NULL,'',NULL,'zhengjianwei','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','男','郑建伟','',NULL,'zhengjianwei','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-08-18 18:30:10','2021-08-18 18:31:07',1,16,'',NULL,'车一班',0,NULL,'workshopfour_che001'),
(87,'',1,NULL,'',NULL,'lina','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','女','李娜','',NULL,'lina','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-08-18 18:30:28','2021-08-18 18:30:57',1,16,'',NULL,'车一班',0,NULL,'workshopfour_che001'),
(88,'',1,NULL,'',NULL,'huashuning','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','女','华淑宁',NULL,NULL,'huashuning',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:41:55',NULL,1,16,'',NULL,'车一班',0,NULL,'workshopfour_che001'),
(89,'',1,NULL,'',NULL,'zhouyongqi','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','周永奇',NULL,NULL,'zhouyongqi',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:42:17',NULL,1,16,'',NULL,'车一班',0,NULL,'workshopfour_che001'),
(90,'',1,NULL,'',NULL,'yangbo','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','杨博',NULL,NULL,'yangbo',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:42:36',NULL,1,16,'','32080161a14c68028a8419b6f38390ac','车一班',0,NULL,'workshopfour_che001'),
(91,'',1,NULL,'',NULL,'wangweixing','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','王伟兴',NULL,NULL,'wangweixing',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:43:08',NULL,1,16,'',NULL,'铣工班',0,NULL,'workshopfour_xi001'),
(92,'',1,NULL,'',NULL,'dongxindegn','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','董新灯',NULL,NULL,'dongxindegn',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:43:39',NULL,1,16,'',NULL,'铣工班',0,NULL,'workshopfour_xi001'),
(93,'',1,NULL,'',NULL,'linan','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','李楠',NULL,NULL,'linan',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:43:58',NULL,1,16,'',NULL,'铣工班',0,NULL,'workshopfour_xi001'),
(94,'',1,NULL,'',NULL,'yuguodong','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','男','余国栋','',NULL,'yuguodong','','',NULL,NULL,NULL,NULL,1,1,'13511111111','管理员1','2021-08-18 18:44:23','2022-06-17 16:11:07',1,16,'',NULL,'铣工班',0,'','workshopfour_xi001'),
(95,'',1,NULL,'',NULL,'cuihairui','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','崔海瑞',NULL,NULL,'cuihairui',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:44:47',NULL,1,16,'',NULL,'铣工班',0,NULL,'workshopfour_xi001'),
(96,'',1,NULL,'',NULL,'yangyuxuan','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','杨宇轩',NULL,NULL,'yangyuxuan',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:45:13',NULL,1,16,'',NULL,'铣工班',0,NULL,'workshopfour_xi001'),
(97,'',1,NULL,'',NULL,'niuguoqiang','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','牛国强',NULL,NULL,'niuguoqiang',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:45:45',NULL,1,16,'',NULL,'铣工班',0,NULL,'workshopfour_xi001'),
(98,'',1,NULL,'',NULL,'malei','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','',NULL,'','男','马磊',NULL,NULL,'malei',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'13511111111',NULL,'2021-08-18 18:46:23',NULL,1,16,'',NULL,'铣工班',0,NULL,'workshopfour_xi001'),
(99,'',1,NULL,'',NULL,'yangleiyu','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','男','杨雷雨','',NULL,'yangleiyu','','',NULL,NULL,NULL,NULL,1,1,'13511111111','管理员1','2021-08-18 18:46:49','2022-07-14 20:34:01',1,16,'','c0e3998f4dc89a06bbafaa171029a285','铣工班',0,'','workshopfour_xi001'),
(100,'',1,NULL,'',NULL,'liuxudong','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','男','刘旭东','',NULL,'liuxudong','','',NULL,NULL,NULL,NULL,1,4,'13511111111','管理员','2021-08-18 18:47:08','2022-09-06 18:15:56',1,16,'','c0e3998f4dc89a06bbafaa171029a285','铣工班',1,NULL,'workshopfour_xi001'),
(101,'',1,NULL,'',NULL,'zhaolongfei','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','男','赵龙飞','',NULL,'zhaolongfei','','',NULL,NULL,NULL,NULL,1,1,'13511111111','管理员1','2021-08-18 18:56:40','2022-06-21 09:03:21',1,16,'','32080161a14c68028a8419b6f38390ac','车一班',0,'','workshopfour_che001'),
(102,'',1,NULL,'',NULL,'qinyu','','workshop_four','d4bdc19e1104a6eb52a0e56ddefbc1fd','四车间','','','','男','秦瑜','',NULL,'qinyu','','',NULL,NULL,NULL,NULL,1,1,'13511111111','13511111111','2021-08-19 19:02:59','2021-08-19 20:11:39',1,16,'','535857d352089c8785f42a992a83e6cf','车二班',0,'ed26e1a3-5795-4fae-960a-d79a64b311e2','workshopfour_che002'),
(103,'',1,NULL,'',NULL,'yujun','','jicecenter','2f3970d37d861777e83e34c6835ae72d','计量测试中心','',NULL,'','男','于军',NULL,NULL,'yujun',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2021-09-26 18:25:40',NULL,1,17,'',NULL,NULL,NULL,'2de57d86-4528-4266-b797-1bf13f4c79e5',NULL),
(104,'',1,NULL,'',NULL,'zhouxuxu','','jicecenter','2f3970d37d861777e83e34c6835ae72d','计量测试中心','',NULL,'','男','周绪绪',NULL,NULL,'zhouxuxu',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2021-09-26 18:27:00',NULL,1,16,'',NULL,NULL,NULL,'e8a09d8c-a460-4c95-9fa9-abf2799cb441',NULL),
(105,'',1,NULL,'',NULL,'zhengbiyuan','','jicecenter','2f3970d37d861777e83e34c6835ae72d','计量测试中心','','','','女','郑碧媛','',NULL,'zhengbiyuan','','',NULL,NULL,NULL,NULL,1,1,'管理员1','管理员1','2021-09-26 18:28:08','2021-12-13 20:22:30',1,16,'',NULL,NULL,NULL,'4b2b4678-2af9-4694-8dca-2c89c68f5254',NULL),
(106,'',1,NULL,'',NULL,'qibenli','','fucai_center','dee498483bd8347e36b1066556d58545','复合材料研究中心','',NULL,'','男','祁本利',NULL,NULL,'qibenli',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2021-09-26 18:28:38',NULL,1,17,'',NULL,NULL,NULL,'641a79e9-2be1-481b-8c07-0a5e27cd0ad4',NULL),
(107,'',1,NULL,'',NULL,'lisheng','','fucai_center','dee498483bd8347e36b1066556d58545','复合材料研究中心','',NULL,'','男','李生',NULL,NULL,'lisheng',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2021-09-26 18:29:04',NULL,1,16,'',NULL,NULL,NULL,'7981321a-26d6-48f3-8146-ba839dab63da',NULL),
(108,'',1,NULL,'',NULL,'dukang','','workshopten','297a9c9f7195c027209fdf2d9e9a38ea','十车间','',NULL,'','男','杜康',NULL,NULL,'dukang',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2021-10-09 10:43:33',NULL,1,NULL,'',NULL,NULL,NULL,'a88d2475-014f-4652-834f-30c52a1a2212',NULL),
(109,'',1,NULL,'',NULL,'hezhiyang','','workshopten','297a9c9f7195c027209fdf2d9e9a38ea','十车间','',NULL,'','男','何志阳',NULL,NULL,'hezhiyang',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2021-10-09 10:43:53',NULL,1,NULL,'',NULL,NULL,NULL,'ec804306-2e92-4d0e-bafa-78bc9f53e0ca',NULL),
(110,'',1,NULL,'',NULL,'caomeng','','workshopten','297a9c9f7195c027209fdf2d9e9a38ea','生产中心','',NULL,'','男','曹萌',NULL,NULL,'caomeng',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2022-01-28 16:35:00',NULL,1,NULL,'',NULL,NULL,NULL,'bdb7bd02-abe3-4006-8bdb-55d433215f37',NULL),
(111,'',1,NULL,'',NULL,'wangxiaowei','','army_projectoffice','f6d074201ffd3f60d19f32ad28555d1a','军品项目办','',NULL,'','男','王校维',NULL,NULL,'wangxiaowei',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2022-02-14 19:59:03',NULL,1,16,'',NULL,NULL,NULL,'2f4751de-75e6-4bc3-a71b-9bd0918d3397',NULL),
(112,'',1,NULL,'',NULL,'zhangweibao','','base_support_center','726dad58ac2a88b7f4eb75b516f706fb','基地保障中心','',NULL,'','男','张维保',NULL,NULL,'zhangweibao',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2022-03-01 18:03:47',NULL,1,NULL,'','0453d66e3337fb4e6ca40977967d23d6','下料一班',0,'40340e8a-dcac-4fdf-bc19-9f20ffbaa6c4','wuzi_xialiao001'),
(113,'',1,NULL,'',NULL,'xianxiubanzhang','','base_support_center','726dad58ac2a88b7f4eb75b516f706fb','基地保障中心','',NULL,'','男','电修班长',NULL,NULL,'xianxiubanzhang',NULL,'',NULL,NULL,NULL,NULL,1,4,'管理员1','管理员','2022-06-07 17:13:08','2022-09-07 10:28:24',1,NULL,'','79d0857e8802920e3aea45dd0aca336a','电修班',1,'bd431ddc-bc55-42e1-9782-a1e18c39f9ed','electric_repair'),
(114,'',1,NULL,'',NULL,'机修班长','','base_support_center','726dad58ac2a88b7f4eb75b516f706fb','基地保障中心','',NULL,'','男','机修班长',NULL,NULL,'机修班长',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2022-06-07 17:13:29',NULL,1,NULL,'',NULL,NULL,NULL,'1eb99274-d5c2-40fb-956b-7b6a2f5d2849',NULL),
(115,'',1,NULL,'',NULL,'jixiuzuyuan','','base_support_center','726dad58ac2a88b7f4eb75b516f706fb','基地保障中心','',NULL,'','男','机修组员',NULL,NULL,'jixiuzuyuan',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2022-06-08 16:25:25',NULL,1,NULL,'',NULL,NULL,NULL,'c5f9d29a-975c-4ee4-b4b7-b92db84031a6',NULL),
(116,'',1,NULL,'',NULL,'dianxiuzuyuan','','base_support_center','726dad58ac2a88b7f4eb75b516f706fb','基地保障中心','',NULL,'','男','电修组员',NULL,NULL,'dianxiuzuyuan',NULL,'',NULL,NULL,NULL,NULL,1,4,'管理员1','管理员','2022-06-08 16:25:48','2022-09-07 10:27:25',1,NULL,'','79d0857e8802920e3aea45dd0aca336a','电修班',1,'ec7680aa-c198-4746-8a72-3c045f6bc051','electric_repair'),
(117,'',1,NULL,'',NULL,'shijutao','','base_support_center','726dad58ac2a88b7f4eb75b516f706fb','基地保障中心','',NULL,'','男','史巨涛',NULL,NULL,'shijutao',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2022-06-15 18:53:22',NULL,1,NULL,'',NULL,NULL,NULL,'333d74d2-a4f1-4d1b-a007-b78c5e699584',NULL),
(118,'',0,NULL,'',NULL,'张满周','','base_support_center','726dad58ac2a88b7f4eb75b516f706fb','基地保障中心','',NULL,'','男','张满周',NULL,NULL,'张满周',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2022-06-15 18:54:07',NULL,1,NULL,'',NULL,NULL,NULL,'a7fb5981-ea32-4f8e-8ddc-8bedca68b29f',NULL),
(119,'',1,NULL,'',NULL,'zhangmanzhou','','base_support_center','726dad58ac2a88b7f4eb75b516f706fb','基地保障中心','',NULL,'','男','张满周',NULL,NULL,'zhangmanzhou',NULL,'',NULL,NULL,NULL,NULL,1,NULL,'管理员1',NULL,'2022-06-15 18:56:29',NULL,1,NULL,'',NULL,NULL,NULL,'48b49ee5-0cd9-4d5f-a5b3-36e9fb38ff9d',NULL),
(120,'',1,NULL,'',NULL,'wenjunhang','','base_support_center','726dad58ac2a88b7f4eb75b516f706fb','基地保障中心','',NULL,'','男','温俊航',NULL,NULL,'wenjunhang',NULL,'',NULL,NULL,NULL,NULL,1,4,'管理员1','管理员','2022-06-15 18:57:17','2022-09-07 10:23:18',1,NULL,'','79d0857e8802920e3aea45dd0aca336a','电修班',1,'45956049-7d28-4773-bb37-0de03efaba5a','electric_repair'),
(121,'',1,NULL,'',NULL,'mashiyuan','','base_support_center','726dad58ac2a88b7f4eb75b516f706fb','基地保障中心','',NULL,'','男','麻诗渊',NULL,NULL,'mashiyuan',NULL,'',NULL,NULL,NULL,NULL,1,4,'管理员1','管理员','2022-06-15 18:57:55','2022-09-08 16:54:49',1,NULL,'','79d0857e8802920e3aea45dd0aca336a','电修班',0,'9faabc39-0d75-433c-bae4-aa65321bc1f1','electric_repair');

/*Table structure for table `hum_user_org` */

DROP TABLE IF EXISTS `hum_user_org`;

CREATE TABLE `hum_user_org` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sys_user_id` bigint(20) DEFAULT NULL,
  `sys_org_id` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `hum_user_org` */

/*Table structure for table `sys_account` */

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
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8;

/*Data for the table `sys_account` */

insert  into `sys_account`(`id`,`remark`,`state`,`password`,`user_id`,`user_name`,`open_id`,`create_id`,`update_id`,`create_user`,`update_user`,`create_time`,`update_time`,`secret_level_no`,`login_name`,`last_login_time`,`ip`,`update_password_time`) values 
(1,'初始化手动创建',1,'96E79218965EB72C92A549DD5A330112',1,'系统管理员',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'SYS_ADMIN','2022-09-22','192.168.13.101',NULL),
(2,'初始化手动创建',1,'96E79218965EB72C92A549DD5A330112',2,'安全管理员',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'SYS_SAFETY_ADMIN',NULL,NULL,NULL),
(3,'初始化手动创建',1,'96E79218965EB72C92A549DD5A330112',3,'安全审计员',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'SYS_AUDITOR_ADMIN',NULL,NULL,NULL),
(4,'初始化手动创建',1,'96E79218965EB72C92A549DD5A330112',4,'管理员',NULL,NULL,4,NULL,'管理员','2022-07-26 20:52:13','2022-08-29 19:17:21',NULL,'admin','2023-03-28','127.0.0.1',NULL),
(8,NULL,1,'1',5,'ww',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'ww',NULL,NULL,NULL),
(23,NULL,1,'96E79218965EB72C92A549DD5A330112',41,'王永刚',NULL,NULL,NULL,'王永刚',NULL,'2021-05-17 09:22:18',NULL,NULL,'wangyonggang',NULL,NULL,NULL),
(25,NULL,1,'96E79218965EB72C92A549DD5A330112',42,'罗辉',NULL,NULL,NULL,'罗辉',NULL,'2021-06-22 11:17:21',NULL,NULL,'luohui','2022-09-26','192.168.13.101',NULL),
(27,NULL,1,'96E79218965EB72C92A549DD5A330112',43,'袁主任',NULL,NULL,NULL,'袁主任',NULL,'2021-06-22 11:19:21',NULL,NULL,'yuanzhuren',NULL,NULL,NULL),
(28,NULL,2,'96E79218965EB72C92A549DD5A330112',44,'检验员1',NULL,NULL,NULL,'检验员1','管理员1','2021-06-22 11:20:08','2021-08-18 14:15:13',NULL,'jianyan001',NULL,NULL,NULL),
(29,NULL,2,'96E79218965EB72C92A549DD5A330112',45,'物资人员',NULL,NULL,NULL,'物资人员','管理员1','2021-06-22 11:24:34','2021-08-18 14:15:14',NULL,'wuzi001',NULL,NULL,NULL),
(30,NULL,1,'96E79218965EB72C92A549DD5A330112',46,'张志正',NULL,NULL,NULL,'张志正',NULL,'2021-06-22 11:51:53',NULL,NULL,'zhangzhizheng',NULL,NULL,NULL),
(31,NULL,1,'96E79218965EB72C92A549DD5A330112',47,'杨主任',NULL,NULL,NULL,'杨主任',NULL,'2021-06-22 11:53:12',NULL,NULL,'yanglihe',NULL,NULL,NULL),
(32,NULL,1,'96E79218965EB72C92A549DD5A330112',48,'董强',NULL,NULL,NULL,'董强',NULL,'2021-06-22 11:54:06',NULL,NULL,'dongqiang',NULL,NULL,NULL),
(33,NULL,1,'96E79218965EB72C92A549DD5A330112',49,'高博',NULL,NULL,NULL,'高博',NULL,'2021-08-04 19:39:37',NULL,NULL,'gaobo',NULL,NULL,NULL),
(35,NULL,1,'96E79218965EB72C92A549DD5A330112',50,'宫思明',NULL,NULL,NULL,'宫思明',NULL,'2021-08-04 19:41:28',NULL,NULL,'gongsiming',NULL,NULL,NULL),
(36,NULL,1,'96E79218965EB72C92A549DD5A330112',51,'姚丹',NULL,NULL,NULL,'姚丹',NULL,'2021-08-04 19:41:55',NULL,NULL,'yaodan',NULL,NULL,NULL),
(37,NULL,1,'96E79218965EB72C92A549DD5A330112',52,'何超',NULL,NULL,NULL,'何超',NULL,'2021-08-04 19:42:17',NULL,NULL,'hechao',NULL,NULL,NULL),
(38,NULL,1,'96E79218965EB72C92A549DD5A330112',53,'曲涛',NULL,NULL,NULL,'曲涛',NULL,'2021-08-04 19:42:50',NULL,NULL,'qutao',NULL,NULL,NULL),
(40,NULL,1,'96E79218965EB72C92A549DD5A330112',55,'蔺海',NULL,NULL,NULL,'蔺海',NULL,'2021-08-04 19:44:01',NULL,NULL,'linhai',NULL,NULL,NULL),
(41,NULL,1,'96E79218965EB72C92A549DD5A330112',56,'魏江娜',NULL,NULL,NULL,'魏江娜',NULL,'2021-08-04 19:44:53',NULL,NULL,'weijiangna','2022-09-30','192.168.13.102',NULL),
(42,NULL,1,'96E79218965EB72C92A549DD5A330112',57,'张志鹏',NULL,NULL,NULL,'张志鹏',NULL,'2021-08-04 19:45:33',NULL,NULL,'zhangzhipeng','2022-09-23','192.168.13.102',NULL),
(43,NULL,1,'96E79218965EB72C92A549DD5A330112',580,'袁晓强',NULL,NULL,NULL,'袁晓强',NULL,'2021-08-04 19:46:13',NULL,NULL,'yuanxiaoqiang',NULL,NULL,NULL),
(44,NULL,1,'96E79218965EB72C92A549DD5A330112',58,'王海峰',NULL,NULL,NULL,'王海峰',NULL,'2021-08-05 11:34:22',NULL,NULL,'wanghaifeng',NULL,NULL,NULL),
(45,NULL,1,'96E79218965EB72C92A549DD5A330112',59,'张东',NULL,NULL,NULL,'张东',NULL,'2021-08-05 11:34:45',NULL,NULL,'zhangdong',NULL,NULL,NULL),
(46,NULL,1,'96E79218965EB72C92A549DD5A330112',60,'贾学瑞',NULL,NULL,NULL,'贾学瑞',NULL,'2021-08-05 11:35:20',NULL,NULL,'jiaxuerui',NULL,NULL,NULL),
(47,NULL,1,'96E79218965EB72C92A549DD5A330112',61,'高坤',NULL,NULL,NULL,'高坤',NULL,'2021-08-05 11:45:16',NULL,NULL,'gaokun',NULL,NULL,NULL),
(48,NULL,1,'96E79218965EB72C92A549DD5A330112',62,'王伟芹',NULL,NULL,NULL,'王伟芹',NULL,'2021-08-05 11:45:44',NULL,NULL,'wangweiqin',NULL,NULL,NULL),
(49,NULL,1,'96E79218965EB72C92A549DD5A330112',63,'胜春祥',NULL,NULL,NULL,'胜春祥',NULL,'2021-08-05 13:54:11',NULL,NULL,'shengchunxiang','2022-11-23','127.0.0.1',NULL),
(50,NULL,1,'96E79218965EB72C92A549DD5A330112',64,'姬广平',NULL,NULL,NULL,'姬广平',NULL,'2021-08-05 13:54:52',NULL,NULL,'jiguangping',NULL,NULL,NULL),
(51,NULL,1,'96E79218965EB72C92A549DD5A330112',65,'康小军',NULL,NULL,NULL,'康小军',NULL,'2021-08-05 13:55:17',NULL,NULL,'kangxiaojun',NULL,NULL,NULL),
(52,NULL,1,'96E79218965EB72C92A549DD5A330112',66,'姜永辉',NULL,NULL,NULL,'姜永辉',NULL,'2021-08-05 13:56:10',NULL,NULL,'jiangyonghui',NULL,NULL,NULL),
(53,NULL,1,'96E79218965EB72C92A549DD5A330112',67,'刘兵',NULL,NULL,NULL,'刘兵',NULL,'2021-08-05 13:56:36',NULL,NULL,'liubing',NULL,NULL,NULL),
(54,NULL,1,'96E79218965EB72C92A549DD5A330112',68,'刘勇为',NULL,NULL,NULL,'刘勇为',NULL,'2021-08-05 13:57:51',NULL,NULL,'liuyongwei',NULL,NULL,NULL),
(55,NULL,1,'96E79218965EB72C92A549DD5A330112',69,'许育飞',NULL,NULL,4,'许育飞','管理员','2021-08-05 14:04:56','2022-09-23 16:49:30',NULL,'xuyufei','2022-09-30','192.168.13.102',NULL),
(57,NULL,1,'96E79218965EB72C92A549DD5A330112',70,'李凡',NULL,NULL,NULL,'李凡',NULL,'2021-08-05 14:08:15',NULL,NULL,'lifan','2022-09-27','192.168.13.102',NULL),
(58,NULL,1,'96E79218965EB72C92A549DD5A330112',71,'刘俊',NULL,NULL,NULL,'刘俊',NULL,'2021-08-05 14:08:39',NULL,NULL,'liujun','2022-09-30','192.168.13.102',NULL),
(59,NULL,1,'96E79218965EB72C92A549DD5A330112',72,'李磊',NULL,NULL,4,'李磊','管理员','2021-08-05 14:08:57','2022-09-26 10:47:56',NULL,'lilei','2022-09-30','192.168.13.102',NULL),
(63,NULL,1,'96E79218965EB72C92A549DD5A330112',73,'周英',NULL,NULL,NULL,'周英',NULL,'2021-08-05 14:11:11',NULL,NULL,'zhouying',NULL,NULL,NULL),
(64,NULL,1,'96E79218965EB72C92A549DD5A330112',74,'周旬',NULL,NULL,NULL,'周旬',NULL,'2021-08-05 14:11:45',NULL,NULL,'zhouxun',NULL,NULL,NULL),
(65,NULL,1,'96E79218965EB72C92A549DD5A330112',75,'魏琳',NULL,NULL,NULL,'魏琳',NULL,'2021-08-05 14:12:12',NULL,NULL,'weilin',NULL,NULL,NULL),
(66,NULL,1,'96E79218965EB72C92A549DD5A330112',76,'何薇',NULL,NULL,NULL,'何薇',NULL,'2021-08-05 14:12:42',NULL,NULL,'hewei',NULL,NULL,NULL),
(67,NULL,1,'96E79218965EB72C92A549DD5A330112',77,'连斌慧',NULL,NULL,NULL,'连斌慧',NULL,'2021-08-05 14:13:25',NULL,NULL,'linbinhui',NULL,NULL,NULL),
(68,NULL,1,'96E79218965EB72C92A549DD5A330112',78,'史小霞',NULL,NULL,NULL,'史小霞',NULL,'2021-08-05 14:13:57',NULL,NULL,'shixiaoxia',NULL,NULL,NULL),
(69,NULL,1,'96E79218965EB72C92A549DD5A330112',79,'李娟',NULL,NULL,NULL,'李娟',NULL,'2021-08-05 14:14:27',NULL,NULL,'lijuan',NULL,NULL,NULL),
(70,NULL,1,'96E79218965EB72C92A549DD5A330112',80,'孙黄炎',NULL,NULL,NULL,'孙黄炎',NULL,'2021-08-05 14:15:02',NULL,NULL,'sunhuangyan',NULL,NULL,NULL),
(71,NULL,1,'96E79218965EB72C92A549DD5A330112',81,'李志阳',NULL,NULL,NULL,'李志阳',NULL,'2021-08-05 14:15:35',NULL,NULL,'lizhiyang',NULL,NULL,NULL),
(83,NULL,1,'96E79218965EB72C92A549DD5A330112',83,'许王平',NULL,NULL,NULL,'许王平',NULL,'2021-08-18 18:27:02',NULL,NULL,'xuwangping','2022-09-29','192.168.13.102',NULL),
(84,NULL,1,'96E79218965EB72C92A549DD5A330112',84,'熊勇',NULL,NULL,NULL,'熊勇',NULL,'2021-08-18 18:27:26',NULL,NULL,'xiongyong',NULL,NULL,NULL),
(85,NULL,1,'96E79218965EB72C92A549DD5A330112',85,'李明军',NULL,NULL,NULL,'李明军',NULL,'2021-08-18 18:29:50',NULL,NULL,'limingjun',NULL,NULL,NULL),
(86,NULL,1,'96E79218965EB72C92A549DD5A330112',86,'郑建伟',NULL,NULL,NULL,'郑建伟',NULL,'2021-08-18 18:30:10',NULL,NULL,'zhengjianwei',NULL,NULL,NULL),
(87,NULL,1,'96E79218965EB72C92A549DD5A330112',87,'李娜',NULL,NULL,NULL,'李娜',NULL,'2021-08-18 18:30:28',NULL,NULL,'lina',NULL,NULL,NULL),
(90,NULL,1,'96E79218965EB72C92A549DD5A330112',88,'华淑宁',NULL,NULL,NULL,'华淑宁',NULL,'2021-08-18 18:41:55',NULL,NULL,'huashuning',NULL,NULL,NULL),
(91,NULL,1,'96E79218965EB72C92A549DD5A330112',89,'周永奇',NULL,NULL,NULL,'周永奇',NULL,'2021-08-18 18:42:17',NULL,NULL,'zhouyongqi',NULL,NULL,NULL),
(92,NULL,1,'96E79218965EB72C92A549DD5A330112',90,'杨博',NULL,NULL,NULL,'杨博',NULL,'2021-08-18 18:42:36',NULL,NULL,'yangbo','2022-09-29','192.168.13.102',NULL),
(93,NULL,1,'96E79218965EB72C92A549DD5A330112',91,'王伟兴',NULL,NULL,NULL,'王伟兴',NULL,'2021-08-18 18:43:08',NULL,NULL,'wangweixing',NULL,NULL,NULL),
(94,NULL,1,'96E79218965EB72C92A549DD5A330112',92,'董新灯',NULL,NULL,NULL,'董新灯',NULL,'2021-08-18 18:43:39',NULL,NULL,'dongxindegn',NULL,NULL,NULL),
(95,NULL,1,'96E79218965EB72C92A549DD5A330112',93,'李楠',NULL,NULL,NULL,'李楠',NULL,'2021-08-18 18:43:58',NULL,NULL,'linan',NULL,NULL,NULL),
(96,NULL,1,'96E79218965EB72C92A549DD5A330112',94,'余国栋',NULL,NULL,NULL,'余国栋',NULL,'2021-08-18 18:44:23',NULL,NULL,'yuguodong',NULL,NULL,NULL),
(97,NULL,1,'96E79218965EB72C92A549DD5A330112',95,'崔海瑞',NULL,NULL,NULL,'崔海瑞',NULL,'2021-08-18 18:44:47',NULL,NULL,'cuihairui',NULL,NULL,NULL),
(98,NULL,1,'96E79218965EB72C92A549DD5A330112',96,'杨宇轩',NULL,NULL,NULL,'杨宇轩',NULL,'2021-08-18 18:45:13',NULL,NULL,'yangyuxuan',NULL,NULL,NULL),
(99,NULL,1,'96E79218965EB72C92A549DD5A330112',97,'牛国强',NULL,NULL,NULL,'牛国强',NULL,'2021-08-18 18:45:45',NULL,NULL,'niuguoqiang',NULL,NULL,NULL),
(100,NULL,1,'96E79218965EB72C92A549DD5A330112',98,'马磊',NULL,NULL,NULL,'马磊',NULL,'2021-08-18 18:46:23',NULL,NULL,'malei',NULL,NULL,NULL),
(101,NULL,1,'96E79218965EB72C92A549DD5A330112',99,'杨雷雨',NULL,NULL,NULL,'杨雷雨',NULL,'2021-08-18 18:46:49',NULL,NULL,'yangleiyu','2022-09-26','192.168.13.101',NULL),
(103,NULL,1,'96E79218965EB72C92A549DD5A330112',100,'刘旭东',NULL,NULL,NULL,'刘旭东',NULL,'2021-08-18 18:47:27',NULL,NULL,'liuxudong',NULL,NULL,NULL),
(104,NULL,1,'96E79218965EB72C92A549DD5A330112',101,'赵龙飞',NULL,NULL,NULL,'赵龙飞',NULL,'2021-08-18 18:56:40',NULL,NULL,'zhaolongfei','2022-09-29','192.168.13.102',NULL),
(106,NULL,1,'96E79218965EB72C92A549DD5A330112',102,'秦瑜',NULL,NULL,NULL,'秦瑜',NULL,'2021-08-19 20:11:50',NULL,NULL,'qinyu',NULL,NULL,NULL),
(107,NULL,1,'96E79218965EB72C92A549DD5A330112',103,'于军',NULL,NULL,NULL,'于军',NULL,'2021-09-26 18:25:42',NULL,NULL,'yujun',NULL,NULL,NULL),
(108,NULL,1,'96E79218965EB72C92A549DD5A330112',104,'周绪绪',NULL,NULL,NULL,'周绪绪',NULL,'2021-09-26 18:27:00',NULL,NULL,'zhouxuxu',NULL,NULL,NULL),
(109,NULL,1,'96E79218965EB72C92A549DD5A330112',105,'郑碧媛',NULL,NULL,NULL,'郑碧媛',NULL,'2021-09-26 18:28:09',NULL,NULL,'zhengbiyuan',NULL,NULL,NULL),
(110,NULL,1,'96E79218965EB72C92A549DD5A330112',106,'祁本利',NULL,NULL,NULL,'祁本利',NULL,'2021-09-26 18:28:38',NULL,NULL,'qibenli',NULL,NULL,NULL),
(111,NULL,1,'96E79218965EB72C92A549DD5A330112',107,'李生',NULL,NULL,NULL,'李生',NULL,'2021-09-26 18:29:04',NULL,NULL,'lisheng',NULL,NULL,NULL),
(112,NULL,1,'96E79218965EB72C92A549DD5A330112',108,'杜康',NULL,NULL,NULL,'杜康',NULL,'2021-10-09 10:43:34',NULL,NULL,'dukang',NULL,NULL,NULL),
(113,NULL,1,'96E79218965EB72C92A549DD5A330112',109,'何志阳',NULL,NULL,NULL,'何志阳',NULL,'2021-10-09 10:43:53',NULL,NULL,'hezhiyang',NULL,NULL,NULL);

/*Table structure for table `sys_account210` */

DROP TABLE IF EXISTS `sys_account210`;

CREATE TABLE `sys_account210` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_date` datetime DEFAULT NULL,
  `create_id` bigint(20) DEFAULT NULL,
  `creater` varchar(50) DEFAULT NULL,
  `remark` varchar(200) DEFAULT NULL,
  `state` int(2) DEFAULT '1',
  `update_date` datetime DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `updater` varchar(50) DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `login_name` varchar(20) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `hum_user_id` bigint(20) DEFAULT NULL,
  `level_name` varchar(255) DEFAULT NULL,
  `level_number` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_user_code` varchar(50) DEFAULT NULL,
  `create_user_id` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user_code` varchar(50) DEFAULT NULL,
  `update_user_id` bigint(20) DEFAULT NULL,
  `user_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_kxq2yy90qlponxplc8h8f2ifo` (`login_name`)
) ENGINE=InnoDB AUTO_INCREMENT=114 DEFAULT CHARSET=utf8;

/*Data for the table `sys_account210` */

insert  into `sys_account210`(`id`,`create_date`,`create_id`,`creater`,`remark`,`state`,`update_date`,`update_id`,`updater`,`ip`,`last_login_time`,`login_name`,`password`,`hum_user_id`,`level_name`,`level_number`,`create_time`,`create_user_code`,`create_user_id`,`update_time`,`update_user_code`,`update_user_id`,`user_name`) values 
(23,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'wangyonggang','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-05-17 09:22:18','王永刚',41,NULL,NULL,NULL,'王永刚'),
(25,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'luohui','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-06-22 11:17:21','罗辉',42,NULL,NULL,NULL,'罗辉'),
(27,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'yuanzhuren','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-06-22 11:19:21','袁主任',43,NULL,NULL,NULL,'袁主任'),
(28,NULL,NULL,NULL,NULL,2,NULL,NULL,NULL,NULL,NULL,'jianyan001','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-06-22 11:20:08','检验员1',44,'2021-08-18 14:15:13','管理员1',1,'检验员1'),
(29,NULL,NULL,NULL,NULL,2,NULL,NULL,NULL,NULL,NULL,'wuzi001','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-06-22 11:24:34','物资人员',45,'2021-08-18 14:15:14','管理员1',1,'物资人员'),
(30,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'zhangzhizheng','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-06-22 11:51:53','张志正',46,NULL,NULL,NULL,'张志正'),
(31,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'yanglihe','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-06-22 11:53:12','杨主任',47,NULL,NULL,NULL,'杨主任'),
(32,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'dongqiang','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-06-22 11:54:06','董强',48,NULL,NULL,NULL,'董强'),
(33,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'gaobo','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-04 19:39:37','高博',49,NULL,NULL,NULL,'高博'),
(35,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'gongsiming','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-04 19:41:28','宫思明',50,NULL,NULL,NULL,'宫思明'),
(36,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'yaodan','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-04 19:41:55','姚丹',51,NULL,NULL,NULL,'姚丹'),
(37,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'hechao','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-04 19:42:17','何超',52,NULL,NULL,NULL,'何超'),
(38,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'qutao','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-04 19:42:50','曲涛',53,NULL,NULL,NULL,'曲涛'),
(40,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'linhai','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-04 19:44:01','蔺海',55,NULL,NULL,NULL,'蔺海'),
(41,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'weijiangna','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-04 19:44:53','魏江娜',56,NULL,NULL,NULL,'魏江娜'),
(42,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'zhangzhipeng','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-04 19:45:33','张志鹏',57,NULL,NULL,NULL,'张志鹏'),
(43,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'yuanxiaoqiang','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-04 19:46:13','袁晓强',43,NULL,NULL,NULL,'袁晓强'),
(44,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'wanghaifeng','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 11:34:22','王海峰',58,NULL,NULL,NULL,'王海峰'),
(45,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'zhangdong','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 11:34:45','张东',59,NULL,NULL,NULL,'张东'),
(46,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'jiaxuerui','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 11:35:20','贾学瑞',60,NULL,NULL,NULL,'贾学瑞'),
(47,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'gaokun','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 11:45:16','高坤',61,NULL,NULL,NULL,'高坤'),
(48,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'wangweiqin','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 11:45:44','王伟芹',62,NULL,NULL,NULL,'王伟芹'),
(49,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'shengchunxiang','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 13:54:11','胜春祥',63,NULL,NULL,NULL,'胜春祥'),
(50,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'jiguangping','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 13:54:52','姬广平',64,NULL,NULL,NULL,'姬广平'),
(51,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'kangxiaojun','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 13:55:17','康小军',65,NULL,NULL,NULL,'康小军'),
(52,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'jiangyonghui','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 13:56:10','姜永辉',66,NULL,NULL,NULL,'姜永辉'),
(53,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'liubing','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 13:56:36','刘兵',67,NULL,NULL,NULL,'刘兵'),
(54,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'liuyongwei','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 13:57:51','刘勇为',68,NULL,NULL,NULL,'刘勇为'),
(55,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'xuyufei','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:04:56','许育飞',69,NULL,NULL,NULL,'许育飞'),
(57,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'lifan','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:08:15','李凡',70,NULL,NULL,NULL,'李凡'),
(58,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'liujun','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:08:39','刘俊',71,NULL,NULL,NULL,'刘俊'),
(59,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'lilei','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:08:57','李磊',72,NULL,NULL,NULL,'李磊'),
(63,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'zhouying','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:11:11','周英',73,NULL,NULL,NULL,'周英'),
(64,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'zhouxun','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:11:45','周旬',74,NULL,NULL,NULL,'周旬'),
(65,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'weilin','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:12:12','魏琳',75,NULL,NULL,NULL,'魏琳'),
(66,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'hewei','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:12:42','何薇',76,NULL,NULL,NULL,'何薇'),
(67,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'linbinhui','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:13:25','连斌慧',77,NULL,NULL,NULL,'连斌慧'),
(68,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'shixiaoxia','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:13:57','史小霞',78,NULL,NULL,NULL,'史小霞'),
(69,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'lijuan','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:14:27','李娟',79,NULL,NULL,NULL,'李娟'),
(70,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'sunhuangyan','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:15:02','孙黄炎',80,NULL,NULL,NULL,'孙黄炎'),
(71,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'lizhiyang','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-05 14:15:35','李志阳',81,NULL,NULL,NULL,'李志阳'),
(83,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'xuwangping','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:27:02','许王平',83,NULL,NULL,NULL,'许王平'),
(84,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'xiongyong','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:27:26','熊勇',84,NULL,NULL,NULL,'熊勇'),
(85,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'limingjun','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:29:50','李明军',85,NULL,NULL,NULL,'李明军'),
(86,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'zhengjianwei','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:30:10','郑建伟',86,NULL,NULL,NULL,'郑建伟'),
(87,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'lina','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:30:28','李娜',87,NULL,NULL,NULL,'李娜'),
(90,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'huashuning','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:41:55','华淑宁',88,NULL,NULL,NULL,'华淑宁'),
(91,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'zhouyongqi','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:42:17','周永奇',89,NULL,NULL,NULL,'周永奇'),
(92,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'yangbo','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:42:36','杨博',90,NULL,NULL,NULL,'杨博'),
(93,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'wangweixing','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:43:08','王伟兴',91,NULL,NULL,NULL,'王伟兴'),
(94,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'dongxindegn','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:43:39','董新灯',92,NULL,NULL,NULL,'董新灯'),
(95,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'linan','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:43:58','李楠',93,NULL,NULL,NULL,'李楠'),
(96,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'yuguodong','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:44:23','余国栋',94,NULL,NULL,NULL,'余国栋'),
(97,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'cuihairui','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:44:47','崔海瑞',95,NULL,NULL,NULL,'崔海瑞'),
(98,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'yangyuxuan','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:45:13','杨宇轩',96,NULL,NULL,NULL,'杨宇轩'),
(99,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'niuguoqiang','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:45:45','牛国强',97,NULL,NULL,NULL,'牛国强'),
(100,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'malei','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:46:23','马磊',98,NULL,NULL,NULL,'马磊'),
(101,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'yangleiyu','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:46:49','杨雷雨',99,NULL,NULL,NULL,'杨雷雨'),
(103,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'liuxudong','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:47:27','刘旭东',100,NULL,NULL,NULL,'刘旭东'),
(104,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'zhaolongfei','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-18 18:56:40','赵龙飞',101,NULL,NULL,NULL,'赵龙飞'),
(106,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'qinyu','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-08-19 20:11:50','秦瑜',102,NULL,NULL,NULL,'秦瑜'),
(107,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'yujun','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-09-26 18:25:42','于军',103,NULL,NULL,NULL,'于军'),
(108,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'zhouxuxu','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-09-26 18:27:00','周绪绪',104,NULL,NULL,NULL,'周绪绪'),
(109,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'zhengbiyuan','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-09-26 18:28:09','郑碧媛',105,NULL,NULL,NULL,'郑碧媛'),
(110,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'qibenli','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-09-26 18:28:38','祁本利',106,NULL,NULL,NULL,'祁本利'),
(111,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'lisheng','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-09-26 18:29:04','李生',107,NULL,NULL,NULL,'李生'),
(112,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'dukang','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-10-09 10:43:34','杜康',108,NULL,NULL,NULL,'杜康'),
(113,NULL,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL,NULL,'hezhiyang','96E79218965EB72C92A549DD5A330112',NULL,NULL,NULL,'2021-10-09 10:43:53','何志阳',109,NULL,NULL,NULL,'何志阳');

/*Table structure for table `sys_account_role` */

DROP TABLE IF EXISTS `sys_account_role`;

CREATE TABLE `sys_account_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sys_account_id` bigint(11) DEFAULT NULL,
  `sys_role_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=156 DEFAULT CHARSET=utf8;

/*Data for the table `sys_account_role` */

insert  into `sys_account_role`(`id`,`sys_account_id`,`sys_role_id`) values 
(1,1,1),
(2,2,2),
(3,3,3),
(9,8,4),
(11,1,2),
(12,1,3),
(13,27,11),
(14,29,8),
(15,31,10),
(16,32,15),
(17,28,16),
(18,23,7),
(19,23,22),
(20,25,11),
(21,25,23),
(22,30,9),
(23,30,22),
(24,33,20),
(25,35,20),
(26,36,20),
(27,37,5),
(28,37,20),
(29,38,9),
(30,38,21),
(31,40,9),
(32,40,22),
(33,42,7),
(34,42,21),
(35,43,11),
(36,43,21),
(37,44,16),
(38,45,16),
(39,46,24),
(40,47,7),
(41,47,21),
(42,48,7),
(43,48,21),
(46,50,11),
(47,50,21),
(48,52,8),
(49,52,21),
(50,53,8),
(51,53,21),
(52,54,8),
(53,54,22),
(54,57,24),
(55,58,24),
(57,63,9),
(58,63,22),
(59,64,9),
(60,64,22),
(61,65,9),
(62,66,9),
(63,67,9),
(64,68,9),
(65,69,9),
(68,72,5),
(71,83,11),
(72,83,23),
(73,87,22),
(74,87,11),
(75,90,11),
(76,90,22),
(77,91,11),
(78,91,22),
(79,92,11),
(80,92,22),
(81,93,11),
(82,93,22),
(83,95,11),
(84,95,22),
(85,96,11),
(86,96,22),
(87,97,11),
(88,97,22),
(89,98,11),
(90,98,22),
(91,99,11),
(92,99,22),
(93,100,11),
(94,100,22),
(95,101,11),
(96,101,22),
(97,103,11),
(98,103,22),
(99,84,22),
(100,84,11),
(101,85,22),
(102,85,11),
(103,86,22),
(104,86,11),
(105,106,11),
(106,106,22),
(107,51,11),
(108,51,23),
(109,107,11),
(110,107,21),
(111,108,11),
(112,108,22),
(113,109,11),
(114,109,22),
(115,110,11),
(116,110,21),
(117,111,11),
(118,111,22),
(119,112,11),
(120,112,21),
(121,113,11),
(122,113,22),
(123,104,11),
(124,104,22),
(125,73,2),
(126,74,3),
(127,116,26),
(128,118,26),
(129,117,26),
(130,120,26),
(131,119,26),
(132,125,26),
(133,124,26),
(134,123,28),
(135,121,27),
(140,55,23),
(141,55,13),
(145,41,13),
(146,41,22),
(148,4,27),
(149,4,28),
(150,4,4),
(151,4,23),
(152,59,18),
(153,59,24),
(154,49,21),
(155,49,16);

/*Table structure for table `sys_data` */

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

/*Data for the table `sys_data` */

insert  into `sys_data`(`text_name`,`sequence`,`data_type_id`,`data_type_code`,`data_type_name`,`id`,`create_id`,`update_id`,`create_user`,`update_user`,`create_time`,`update_time`,`state`,`remark`,`secret_level_no`) values 
('重要','1',4,'USER_SECRET','人员密级',1,4,NULL,'管理员',NULL,'2022-07-26 18:10:33',NULL,1,NULL,NULL),
('核心','2',4,'USER_SECRET','人员密级',2,4,NULL,'管理员',NULL,'2022-07-26 18:10:51',NULL,1,NULL,NULL),
('一般','3',4,'USER_SECRET','人员密级',3,4,NULL,'管理员',NULL,'2022-07-26 18:11:06',NULL,1,NULL,NULL);

/*Table structure for table `sys_data_type` */

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

/*Data for the table `sys_data_type` */

insert  into `sys_data_type`(`type_code`,`type_name`,`id`,`create_id`,`update_id`,`create_user`,`update_user`,`create_time`,`update_time`,`state`,`remark`,`secret_level_no`) values 
('USER_SECRET','人员密级',4,4,NULL,'管理员',NULL,'2022-07-26 18:09:47',NULL,1,NULL,NULL);

/*Table structure for table `sys_ip_filter` */

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

/*Data for the table `sys_ip_filter` */

insert  into `sys_ip_filter`(`ip`,`id`,`create_id`,`update_id`,`create_time`,`update_time`,`state`,`remark`,`create_user`,`update_user`,`secret_level_no`) values 
('127.0.0.2','3ee082af740086f58f19d57a3770d344',NULL,4,'2022-07-27 14:02:16','2022-07-28 09:57:49',1,'三员管理登录不拦截IP白名单','管理员','管理员',NULL);

/*Table structure for table `sys_moudle` */

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
) ENGINE=InnoDB AUTO_INCREMENT=41013 DEFAULT CHARSET=utf8;

/*Data for the table `sys_moudle` */

insert  into `sys_moudle`(`id`,`name`,`code`,`parent_id`,`parent_name`,`action_url`,`icon`,`img_url`,`sort`,`state`,`is_operation`,`is_display`,`enable`,`system_id`,`remark`,`description`,`create_id`,`update_id`,`create_user`,`update_user`,`create_time`,`update_time`,`secret_level_no`,`json_css`) values 
(1,'菜单权限',NULL,NULL,'','#',NULL,NULL,1,1,0,1,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(2,'系统设置',NULL,1,'菜单权限','#','fa fa-cogs',NULL,2,1,0,1,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(3,'账户管理',NULL,2,'权限管理','/account/list','fa fa-wheelchair-alt',NULL,1,1,0,1,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(4,'权限管理',NULL,2,'系统设置','#','fa fa-hand-spock-o',NULL,1,1,0,1,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(6,'修改','修改',3,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(7,'重置密码','重置密码',3,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(8,'分配角色','分配角色',3,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(9,'角色管理',NULL,4,'权限管理','/role/list','fa fa-deaf',NULL,2,1,0,1,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:22:59',NULL,NULL,NULL),
(10,'新增','新增',9,'角色管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:23:00',NULL,NULL,NULL),
(11,'修改','修改',9,'角色管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:23:00',NULL,NULL,NULL),
(12,'删除','删除',9,'角色管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:23:00',NULL,NULL,NULL),
(13,'查看','查看',9,'角色管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:23:00',NULL,NULL,NULL),
(14,'分配权限','分配权限',9,'角色管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:22:59',NULL,NULL,NULL),
(15,'查看权限','查看权限',55,'人员信息',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(16,'分配权限',NULL,9,'角色管理','/role/roleToAuthority','fa fa-address-book',NULL,1,1,0,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 11:49:48',NULL,NULL,NULL),
(17,'查看权限',NULL,55,'人员信息','/user/viewRoleAuthority',NULL,NULL,1,1,0,0,1,1,'手动输入',NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(18,'模块管理',NULL,4,'权限管理','/module/list','fa fa-crop',NULL,1,1,0,1,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 14:04:48',NULL,NULL,NULL),
(19,'删除','删除',18,'模块管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 14:04:48',NULL,NULL,NULL),
(20,'查看','查看',18,'模块管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 14:04:48',NULL,NULL,NULL),
(21,'修改','修改',18,'模块管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 14:04:48',NULL,NULL,NULL),
(22,'新增','新增',18,'模块管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 14:04:48',NULL,NULL,NULL),
(28,'子系统维护',NULL,4,'权限管理','/sysInfo/list','fa fa-snowflake-o',NULL,4,1,0,1,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(29,'删除','删除',28,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(30,'查看','查看',28,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(31,'修改','修改',28,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(32,'新增','新增',28,'',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(33,'数据字典',NULL,2,'系统设置','#','fa fa-building','',3,1,0,1,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(34,'数据字典内容',NULL,33,'数据字典','/sysData/list','fa fa-file-archive-o','',1,1,0,1,1,1,NULL,NULL,2,NULL,'安全管理员',NULL,'2022-07-27 19:18:11',NULL,NULL,NULL),
(35,'新增','新增',34,'数据字典内容',NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,2,NULL,'安全管理员',NULL,'2022-07-27 19:18:11',NULL,NULL,NULL),
(36,'修改','修改',34,'数据字典内容',NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,2,NULL,'安全管理员',NULL,'2022-07-27 19:18:11',NULL,NULL,NULL),
(37,'删除','删除',34,'数据字典内容',NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,2,NULL,'安全管理员',NULL,'2022-07-27 19:18:11',NULL,NULL,NULL),
(38,'查看','查看',34,'数据字典内容',NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,2,NULL,'安全管理员',NULL,'2022-07-27 19:18:11',NULL,NULL,NULL),
(39,'数据字典类型',NULL,33,'数据字典','/sysDataType/list','fa fa-files-o','',2,1,0,1,1,1,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(40,'新增','新增',39,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(41,'修改','修改',39,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(42,'删除','删除',39,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(43,'查看','查看',39,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(44,'组织人员',NULL,1,'菜单权限','#','fa fa-address-card-o','',2,1,0,1,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(50,'部门信息',NULL,44,'组织人员','/organization/list','fa fa-window-restore','',2,1,0,1,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(51,'新增','新增',50,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(52,'修改','修改',50,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(53,'删除','删除',50,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(54,'查看','查看',50,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(55,'人员信息',NULL,44,'组织人员','/user/list','fa fa-address-book-o','',3,1,0,1,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(56,'人员新增',NULL,55,'人员信息','/user/create','fa fa-address-book','',1,1,0,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(57,'人员修改',NULL,55,'人员信息','/user/update','fa fa-address-book','',1,1,0,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(58,'人员查看',NULL,55,'人员信息','/user/view','fa fa-address-book','',1,1,0,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(59,'新增','新增',55,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(60,'删除','删除',55,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(61,'修改','修改',55,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(62,'查看','查看',55,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(63,'日志管理',NULL,2,'系统设置','/busiOpsLog/list','fa fa-dedent','',5,1,0,1,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-27 13:46:18',NULL,NULL,NULL),
(64,'职务管理',NULL,44,'组织人员','/position/list','fa fa-etsy','',3,1,0,1,1,1,'',NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,''),
(65,'新增','新增',64,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(66,'修改','修改',64,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(67,'查看','查看',64,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(68,'删除','删除',64,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(69,'岗位管理',NULL,44,'组织人员','/post/list','fa fa-etsy','',4,1,0,1,1,1,'',NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,''),
(70,'新增','新增',69,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(71,'修改','修改',69,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(72,'查看','查看',69,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(73,'删除','删除',69,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(74,'工种管理',NULL,44,'组织人员','/profession/list','fa fa-etsy','',5,1,0,1,1,1,'',NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,''),
(75,'新增','新增',74,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(76,'修改','修改',74,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(77,'查看','查看',74,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(78,'删除','删除',74,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(79,'系统设置','',2,'系统设置','#','fa fa-credit-card-alt','',4,1,0,1,1,1,'','',4,NULL,'管理员',NULL,'2021-09-01 00:00:00',NULL,NULL,NULL),
(80,'三员白名单登录','',79,'系统设置','/sysIpFilter/list','fa fa-linode','',1,1,0,1,1,1,'','',4,NULL,'管理员',NULL,'2022-07-28 18:13:40',NULL,NULL,NULL),
(81,'新增','新增',80,'','','','',1,1,1,0,1,1,'','',4,NULL,'管理员',NULL,'2022-07-28 18:13:40',NULL,NULL,NULL),
(82,'修改','修改',80,'','','','',1,1,1,0,1,1,'','',4,NULL,'管理员',NULL,'2022-07-28 18:13:40',NULL,NULL,NULL),
(83,'删除','删除',80,'','','','',1,1,1,0,1,1,'','',4,NULL,'管理员',NULL,'2022-07-28 18:13:40',NULL,NULL,NULL),
(84,'查看','查看',80,'','','','',1,1,1,0,1,1,'','',4,NULL,'管理员',NULL,'2022-07-28 18:13:40',NULL,NULL,NULL),
(85,'系统变量','',79,'系统设置','/sysVar/list','fa fa-quora','',2,1,0,1,1,1,'','',4,NULL,'管理员',NULL,'2022-07-28 18:13:40',NULL,NULL,NULL),
(86,'新增','新增',85,'','','','',1,1,1,0,1,1,'','',4,NULL,'管理员',NULL,'2022-07-28 18:13:40',NULL,NULL,NULL),
(87,'修改','修改',85,'','','','',1,1,1,0,1,1,'','',4,NULL,'管理员',NULL,'2022-07-28 18:13:40',NULL,NULL,NULL),
(88,'删除','删除',85,'','','','',1,1,1,0,1,1,'','',4,NULL,'管理员',NULL,'2022-07-28 18:13:40',NULL,NULL,NULL),
(89,'查看','查看',85,'','','','',1,1,1,0,1,1,'','',4,NULL,'管理员',NULL,'2022-07-28 18:13:40',NULL,NULL,NULL),
(90,'打码','打码',55,'','','','',1,1,1,0,1,1,'','',4,NULL,'管理员',NULL,'2022-07-28 18:13:40',NULL,NULL,NULL),
(40790,'排程模块',NULL,2,'系统设置','/scheData/list','fa fa-american-sign-language-interpreting','',6,0,0,1,1,1,'',NULL,4,4,'管理员','管理员','2022-08-02 11:12:02','2022-08-02 15:29:32',NULL,''),
(40791,'新增','新增',40790,NULL,NULL,NULL,NULL,1,0,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-08-02 11:12:02',NULL,NULL,NULL),
(40792,'修改','修改',40790,NULL,NULL,NULL,NULL,1,0,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-08-02 11:12:02',NULL,NULL,NULL),
(40793,'查看','查看',40790,NULL,NULL,NULL,NULL,1,0,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-08-02 11:12:02',NULL,NULL,NULL),
(40794,'删除','删除',40790,NULL,NULL,NULL,NULL,1,0,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-08-02 11:12:02',NULL,NULL,NULL),
(40795,'排程策略',NULL,2,'系统设置','/strategy/list','fa fa-address-book','',6,0,0,1,1,1,'',NULL,4,4,'管理员','管理员','2022-08-02 15:34:16','2022-08-02 15:53:53',NULL,''),
(40796,'新增','新增',40795,NULL,NULL,NULL,NULL,1,0,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-08-02 15:34:16',NULL,NULL,NULL),
(40797,'查看','查看',40795,NULL,NULL,NULL,NULL,1,0,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-08-02 15:34:16',NULL,NULL,NULL),
(40798,'修改','修改',40795,NULL,NULL,NULL,NULL,1,0,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-08-02 15:34:16',NULL,NULL,NULL),
(40799,'删除','删除',40795,NULL,NULL,NULL,NULL,1,0,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-08-02 15:34:16',NULL,NULL,NULL),
(41011,'基础数据',NULL,1,'菜单权限','#','fa fa-address-book','',1,1,0,1,1,4,'',NULL,4,NULL,'管理员',NULL,'2023-03-28 08:23:54',NULL,NULL,''),
(41012,'机器人管理',NULL,41011,'基础数据','/robot/list','fa fa-address-book','',1,1,0,1,1,4,'',NULL,4,NULL,'管理员',NULL,'2023-03-28 09:01:06',NULL,NULL,'');

/*Table structure for table `sys_moudle_ini` */

DROP TABLE IF EXISTS `sys_moudle_ini`;

CREATE TABLE `sys_moudle_ini` (
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
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8;

/*Data for the table `sys_moudle_ini` */

insert  into `sys_moudle_ini`(`id`,`name`,`code`,`parent_id`,`parent_name`,`action_url`,`icon`,`img_url`,`sort`,`state`,`is_operation`,`is_display`,`enable`,`system_id`,`remark`,`description`,`create_id`,`update_id`,`create_user`,`update_user`,`create_time`,`update_time`,`secret_level_no`,`json_css`) values 
(1,'菜单权限',NULL,NULL,'','#',NULL,NULL,1,1,0,1,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(2,'系统设置',NULL,1,'菜单权限','#','fa fa-cogs',NULL,2,1,0,1,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(3,'账户管理',NULL,2,'权限管理','/account/list','fa fa-wheelchair-alt',NULL,1,1,0,1,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(4,'权限管理',NULL,2,'系统设置','#','fa fa-hand-spock-o',NULL,1,1,0,1,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(6,'修改','修改',3,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(7,'重置密码','重置密码',3,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(8,'分配角色','分配角色',3,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(9,'角色管理',NULL,4,'权限管理','/role/list','fa fa-deaf',NULL,2,1,0,1,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:22:59',NULL,NULL,NULL),
(10,'新增','新增',9,'角色管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:23:00',NULL,NULL,NULL),
(11,'修改','修改',9,'角色管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:23:00',NULL,NULL,NULL),
(12,'删除','删除',9,'角色管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:23:00',NULL,NULL,NULL),
(13,'查看','查看',9,'角色管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:23:00',NULL,NULL,NULL),
(14,'分配权限','分配权限',9,'角色管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-19 14:22:59',NULL,NULL,NULL),
(15,'查看权限','查看权限',55,'人员信息',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(16,'分配权限',NULL,9,'角色管理','/role/roleToAuthority','fa fa-address-book',NULL,1,1,0,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 11:49:48',NULL,NULL,NULL),
(17,'查看权限',NULL,55,'人员信息','/user/viewRoleAuthority',NULL,NULL,1,1,0,0,1,1,'手动输入',NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(18,'模块管理',NULL,4,'权限管理','/module/list','fa fa-crop',NULL,1,1,0,1,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 14:04:48',NULL,NULL,NULL),
(19,'删除','删除',18,'模块管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 14:04:48',NULL,NULL,NULL),
(20,'查看','查看',18,'模块管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 14:04:48',NULL,NULL,NULL),
(21,'修改','修改',18,'模块管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 14:04:48',NULL,NULL,NULL),
(22,'新增','新增',18,'模块管理',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,1,NULL,'管理员',NULL,'2022-07-22 14:04:48',NULL,NULL,NULL),
(28,'子系统维护',NULL,4,'权限管理','/sysInfo/list','fa fa-snowflake-o',NULL,4,1,0,1,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(29,'删除','删除',28,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(30,'查看','查看',28,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(31,'修改','修改',28,NULL,NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(32,'新增','新增',28,'',NULL,NULL,NULL,1,1,1,0,1,1,'手动输入',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(33,'数据字典',NULL,2,'系统设置','#','fa fa-building','',3,1,0,1,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(34,'数据字典内容',NULL,33,'数据字典','/sysData/list','fa fa-file-archive-o','',1,1,0,1,1,1,NULL,NULL,2,NULL,'安全管理员',NULL,'2022-07-27 19:18:11',NULL,NULL,NULL),
(35,'新增','新增',34,'数据字典内容',NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,2,NULL,'安全管理员',NULL,'2022-07-27 19:18:11',NULL,NULL,NULL),
(36,'修改','修改',34,'数据字典内容',NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,2,NULL,'安全管理员',NULL,'2022-07-27 19:18:11',NULL,NULL,NULL),
(37,'删除','删除',34,'数据字典内容',NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,2,NULL,'安全管理员',NULL,'2022-07-27 19:18:11',NULL,NULL,NULL),
(38,'查看','查看',34,'数据字典内容',NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,2,NULL,'安全管理员',NULL,'2022-07-27 19:18:11',NULL,NULL,NULL),
(39,'数据字典类型',NULL,33,'数据字典','/sysDataType/list','fa fa-files-o','',2,1,0,1,1,1,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(40,'新增','新增',39,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(41,'修改','修改',39,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(42,'删除','删除',39,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(43,'查看','查看',39,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(44,'组织人员',NULL,1,'菜单权限','#','fa fa-address-card-o','',2,1,0,1,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(50,'部门信息',NULL,44,'组织人员','/organization/list','fa fa-window-restore','',2,1,0,1,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(51,'新增','新增',50,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(52,'修改','修改',50,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(53,'删除','删除',50,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(54,'查看','查看',50,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(55,'人员信息',NULL,44,'组织人员','/user/list','fa fa-address-book-o','',3,1,0,1,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(56,'人员新增',NULL,55,'人员信息','/user/create','fa fa-address-book','',1,1,0,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(57,'人员修改',NULL,55,'人员信息','/user/update','fa fa-address-book','',1,1,0,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(58,'人员查看',NULL,55,'人员信息','/user/view','fa fa-address-book','',1,1,0,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(59,'新增','新增',55,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(60,'删除','删除',55,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(61,'修改','修改',55,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(62,'查看','查看',55,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(63,'日志管理',NULL,2,'系统设置','/busiOpsLog/list','fa fa-dedent','',5,1,0,1,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-27 13:46:18',NULL,NULL,NULL),
(64,'职务管理',NULL,44,'组织人员','/position/list','fa fa-etsy','',3,1,0,1,1,1,'',NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,''),
(65,'新增','新增',64,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(66,'修改','修改',64,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(67,'查看','查看',64,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(68,'删除','删除',64,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(69,'岗位管理',NULL,44,'组织人员','/post/list','fa fa-etsy','',4,1,0,1,1,1,'',NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,''),
(70,'新增','新增',69,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(71,'修改','修改',69,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(72,'查看','查看',69,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(73,'删除','删除',69,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(74,'工种管理',NULL,44,'组织人员','/profession/list','fa fa-etsy','',5,1,0,1,1,1,'',NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,''),
(75,'新增','新增',74,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(76,'修改','修改',74,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(77,'查看','查看',74,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(78,'删除','删除',74,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-26 20:15:58',NULL,NULL,NULL),
(79,'系统设置',NULL,2,'系统设置','#','fa fa-credit-card-alt','',4,1,0,1,1,1,'',NULL,4,NULL,'管理员',NULL,'2022-07-27 13:46:09',NULL,NULL,''),
(80,'三员白名单登录',NULL,79,'系统设置','/sysIpFilter/list','fa fa-linode','',1,1,0,1,1,1,'',NULL,4,NULL,'管理员',NULL,'2022-07-27 13:49:33',NULL,NULL,''),
(81,'新增','新增',80,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-27 13:49:33',NULL,NULL,NULL),
(82,'修改','修改',80,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-27 13:49:33',NULL,NULL,NULL),
(83,'删除','删除',80,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-27 13:49:33',NULL,NULL,NULL),
(84,'查看','查看',80,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-27 13:49:33',NULL,NULL,NULL),
(85,'系统变量',NULL,79,'系统设置','/sysVar/list','fa fa-quora','',2,1,0,1,1,1,'',NULL,4,NULL,'管理员',NULL,'2022-07-27 13:50:23',NULL,NULL,''),
(86,'新增','新增',85,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-27 13:50:23',NULL,NULL,NULL),
(87,'修改','修改',85,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-27 13:50:23',NULL,NULL,NULL),
(88,'删除','删除',85,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-27 13:50:23',NULL,NULL,NULL),
(89,'查看','查看',85,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-27 13:50:23',NULL,NULL,NULL),
(90,'打码','打码',55,NULL,NULL,NULL,NULL,1,1,1,0,1,1,NULL,NULL,4,NULL,'管理员',NULL,'2022-07-28 18:08:45',NULL,NULL,NULL);

/*Table structure for table `sys_role` */

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
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

/*Data for the table `sys_role` */

insert  into `sys_role`(`id`,`remark`,`state`,`description`,`name`,`role_code`,`role_level`,`create_id`,`update_id`,`create_user`,`update_user`,`create_time`,`update_time`,`secret_level_no`) values 
(1,'初始化手动创建',1,NULL,'系统管理员','SYS_ADMIN',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(2,'初始化手动创建',1,'','安全管理员','SYS_SAFETY_ADMIN',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(3,'初始化手动创建',1,NULL,'安全审计员','SYS_AUDITOR_ADMIN',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL),
(4,'初始化手动创建',1,NULL,'管理员','admin',1,2,4,'安全管理员','管理员','2022-07-27 19:18:21','2022-07-27 18:07:44',NULL),
(6,NULL,1,'','张飒','001',1,4,NULL,'管理员',NULL,'2022-08-30 15:36:01',NULL,NULL),
(10,'人力资源',1,NULL,'人力资源','SYS_HUMAN',1,NULL,NULL,NULL,NULL,'2020-07-09 00:00:00',NULL,NULL),
(11,NULL,1,NULL,'开发调试员','SUPER_ADMIN_asam_0000',0,NULL,NULL,NULL,NULL,'2020-07-22 00:00:00',NULL,NULL),
(12,NULL,1,'科研生产','科研生产','KEYAN_DEPT',1,1,1,'管理员1','管理员1','2021-05-17 00:00:00','2021-08-09 00:00:00',NULL),
(13,NULL,1,'管理物资信息，下料、处理领料申请','物资处','WUZI_DEPT',1,1,1,'管理员1','管理员1','2021-06-22 00:00:00','2021-08-09 00:00:00',NULL),
(14,NULL,1,'编制工艺路线、工序、工步等信息','工艺管理','GONGYI_DEPT',1,1,1,'管理员1','管理员1','2021-06-22 00:00:00','2022-01-28 00:00:00',NULL),
(15,NULL,1,'审核新建的工艺路线','工艺审核',NULL,1,1,NULL,'管理员1',NULL,'2021-06-22 00:00:00',NULL,NULL),
(16,NULL,1,'查看工单，人员和班组长派工，设备运行情况查看','生产车间','PROD_DEPT',1,1,1,'管理员1','管理员1','2021-06-22 00:00:00','2021-08-09 00:00:00',NULL),
(17,NULL,1,'领取派工单、加工、报工','车间生产人员',NULL,1,1,NULL,'管理员1',NULL,'2021-06-22 00:00:00',NULL,NULL),
(18,NULL,1,'加工完成后的检验工作','检验室领导','JIANYAN_LEADER',1,1,1,'管理员1','管理员1','2021-06-22 00:00:00','2021-08-09 00:00:00',NULL),
(19,NULL,1,'查看工单、派工单进度','生产进度查询',NULL,1,1,NULL,'管理员1',NULL,'2021-06-22 00:00:00',NULL,NULL),
(20,NULL,1,'信息中心','信息中心','XINXIHUA_DEPT',1,1,NULL,'管理员1',NULL,'2021-08-09 00:00:00',NULL,NULL),
(21,NULL,1,'部门领导','部门领导','DEPT_LEADER',1,1,NULL,'管理员1',NULL,'2021-08-09 00:00:00',NULL,NULL),
(22,NULL,1,'普通员工','普通员工','DEPT_STAFF',1,1,NULL,'管理员1',NULL,'2021-08-09 00:00:00',NULL,NULL),
(23,NULL,1,'班组长','班组长','TEAM_LEADER',1,1,NULL,'管理员1',NULL,'2021-08-09 00:00:00',NULL,NULL),
(24,NULL,1,'检验室员工','检验室员工','JIANYAN_STAFF',1,1,NULL,'管理员1',NULL,'2021-08-09 00:00:00',NULL,NULL),
(25,NULL,1,'发展计划处','发展计划处','PLAN_DEPT',1,1,NULL,'管理员1',NULL,'2022-01-25 00:00:00',NULL,NULL),
(26,NULL,1,'设备维保','设备维保','EQUIP_MAINTENANCE',1,1,NULL,'管理员1',NULL,'2022-03-01 00:00:00',NULL,NULL),
(27,NULL,1,'','基地保障中心领导','base_support_center_leader',1,1,NULL,'管理员1',NULL,'2022-06-15 00:00:00',NULL,NULL),
(28,NULL,1,'','基地保障中心技术人员','base_support_center_tech',1,1,NULL,'管理员1',NULL,'2022-06-15 00:00:00',NULL,NULL);

/*Table structure for table `sys_role_moudle` */

DROP TABLE IF EXISTS `sys_role_moudle`;

CREATE TABLE `sys_role_moudle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sys_moudle_id` bigint(11) DEFAULT NULL,
  `sys_role_id` bigint(11) DEFAULT NULL,
  `system_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16076 DEFAULT CHARSET=utf8;

/*Data for the table `sys_role_moudle` */

insert  into `sys_role_moudle`(`id`,`sys_moudle_id`,`sys_role_id`,`system_id`) values 
(78,44,1,1),
(79,74,1,1),
(80,78,1,1),
(81,77,1,1),
(82,76,1,1),
(83,75,1,1),
(84,69,1,1),
(85,73,1,1),
(86,72,1,1),
(87,71,1,1),
(88,70,1,1),
(89,64,1,1),
(90,68,1,1),
(91,67,1,1),
(92,66,1,1),
(93,65,1,1),
(94,55,1,1),
(95,62,1,1),
(96,61,1,1),
(97,60,1,1),
(98,59,1,1),
(99,58,1,1),
(100,57,1,1),
(101,56,1,1),
(102,17,1,1),
(103,15,1,1),
(104,50,1,1),
(105,54,1,1),
(106,53,1,1),
(107,52,1,1),
(108,51,1,1),
(109,2,2,1),
(110,63,2,1),
(111,4,2,1),
(112,28,2,1),
(113,32,2,1),
(114,31,2,1),
(115,30,2,1),
(116,29,2,1),
(117,18,2,1),
(118,22,2,1),
(119,21,2,1),
(120,20,2,1),
(121,19,2,1),
(122,9,2,1),
(123,16,2,1),
(124,14,2,1),
(125,13,2,1),
(126,12,2,1),
(127,11,2,1),
(128,10,2,1),
(129,3,2,1),
(130,8,2,1),
(131,7,2,1),
(132,6,2,1),
(133,2,3,1),
(134,63,3,1),
(716,40782,4,3),
(717,40785,4,3),
(718,40783,4,3),
(719,40784,4,3),
(720,40754,4,3),
(721,40753,4,3),
(722,40721,4,3),
(723,40747,4,3),
(724,40749,4,3),
(725,40748,4,3),
(726,40746,4,3),
(727,40741,4,3),
(728,40745,4,3),
(729,40742,4,3),
(730,40740,4,3),
(731,40752,4,3),
(732,40751,4,3),
(733,40738,4,3),
(734,40744,4,3),
(735,40739,4,3),
(736,40736,4,3),
(737,40743,4,3),
(738,40737,4,3),
(739,40653,4,3),
(740,40771,4,3),
(741,40781,4,3),
(742,40772,4,3),
(743,40731,4,3),
(744,40735,4,3),
(745,40734,4,3),
(746,40733,4,3),
(747,40732,4,3),
(748,40700,4,3),
(749,40720,4,3),
(750,40703,4,3),
(751,40702,4,3),
(752,40701,4,3),
(753,40695,4,3),
(754,40718,4,3),
(755,40699,4,3),
(756,40698,4,3),
(757,40697,4,3),
(758,40696,4,3),
(759,40690,4,3),
(760,40717,4,3),
(761,40694,4,3),
(762,40693,4,3),
(763,40692,4,3),
(764,40691,4,3),
(765,40685,4,3),
(766,40780,4,3),
(767,40774,4,3),
(768,40716,4,3),
(769,40705,4,3),
(770,40689,4,3),
(771,40688,4,3),
(772,40687,4,3),
(773,40686,4,3),
(774,40652,4,3),
(775,40764,4,3),
(776,40765,4,3),
(777,40726,4,3),
(778,40750,4,3),
(779,40730,4,3),
(780,40729,4,3),
(781,40728,4,3),
(782,40727,4,3),
(783,40725,4,3),
(784,40722,4,3),
(785,40724,4,3),
(786,40723,4,3),
(787,40650,4,3),
(788,40786,4,3),
(789,40788,4,3),
(790,40787,4,3),
(791,40775,4,3),
(792,40779,4,3),
(793,40778,4,3),
(794,40777,4,3),
(795,40776,4,3),
(796,40760,4,3),
(797,40761,4,3),
(798,40755,4,3),
(799,40759,4,3),
(800,40758,4,3),
(801,40757,4,3),
(802,40756,4,3),
(803,40706,4,3),
(804,40715,4,3),
(805,40709,4,3),
(806,40708,4,3),
(807,40707,4,3),
(808,40658,4,3),
(809,40714,4,3),
(810,40673,4,3),
(811,40672,4,3),
(812,40671,4,3),
(813,40657,4,3),
(814,40713,4,3),
(815,40670,4,3),
(816,40669,4,3),
(817,40668,4,3),
(818,40656,4,3),
(819,40712,4,3),
(820,40667,4,3),
(821,40666,4,3),
(822,40665,4,3),
(823,40655,4,3),
(824,40711,4,3),
(825,40664,4,3),
(826,40663,4,3),
(827,40662,4,3),
(828,40654,4,3),
(829,40710,4,3),
(830,40661,4,3),
(831,40660,4,3),
(832,40659,4,3),
(833,40642,4,3),
(834,40762,4,3),
(835,40773,4,3),
(836,40763,4,3),
(837,40680,4,3),
(838,40770,4,3),
(839,40766,4,3),
(840,40684,4,3),
(841,40683,4,3),
(842,40682,4,3),
(843,40681,4,3),
(844,40676,4,3),
(845,40789,4,3),
(846,40719,4,3),
(847,40679,4,3),
(848,40678,4,3),
(849,40677,4,3),
(850,40675,4,3),
(851,40644,4,3),
(852,40649,4,3),
(853,40648,4,3),
(854,40647,4,3),
(855,40646,4,3),
(856,40645,4,3),
(1179,44,4,1),
(1180,74,4,1),
(1181,78,4,1),
(1182,77,4,1),
(1183,76,4,1),
(1184,75,4,1),
(1185,69,4,1),
(1186,73,4,1),
(1187,72,4,1),
(1188,71,4,1),
(1189,70,4,1),
(1190,64,4,1),
(1191,68,4,1),
(1192,67,4,1),
(1193,66,4,1),
(1194,65,4,1),
(1195,55,4,1),
(1196,90,4,1),
(1197,62,4,1),
(1198,61,4,1),
(1199,60,4,1),
(1200,59,4,1),
(1201,58,4,1),
(1202,57,4,1),
(1203,56,4,1),
(1204,17,4,1),
(1205,15,4,1),
(1206,50,4,1),
(1207,54,4,1),
(1208,53,4,1),
(1209,52,4,1),
(1210,51,4,1),
(1211,2,4,1),
(1212,40795,4,1),
(1213,40799,4,1),
(1214,40798,4,1),
(1215,40797,4,1),
(1216,40796,4,1),
(1217,79,4,1),
(1218,85,4,1),
(1219,89,4,1),
(1220,88,4,1),
(1221,87,4,1),
(1222,86,4,1),
(1223,80,4,1),
(1224,84,4,1),
(1225,83,4,1),
(1226,82,4,1),
(1227,81,4,1),
(1228,63,4,1),
(1229,33,4,1),
(1230,39,4,1),
(1231,43,4,1),
(1232,42,4,1),
(1233,41,4,1),
(1234,40,4,1),
(1235,34,4,1),
(1236,38,4,1),
(1237,37,4,1),
(1238,36,4,1),
(1239,35,4,1),
(1240,4,4,1),
(1241,28,4,1),
(1242,32,4,1),
(1243,31,4,1),
(1244,30,4,1),
(1245,29,4,1),
(1246,18,4,1),
(1247,22,4,1),
(1248,21,4,1),
(1249,20,4,1),
(1250,19,4,1),
(1251,9,4,1),
(1252,16,4,1),
(1253,14,4,1),
(1254,13,4,1),
(1255,12,4,1),
(1256,11,4,1),
(1257,10,4,1),
(1258,3,4,1),
(1259,8,4,1),
(1260,7,4,1),
(1261,6,4,1),
(10637,370,13,2),
(10638,737,13,2),
(10639,809,13,2),
(10640,812,13,2),
(10641,811,13,2),
(10642,639,13,2),
(10643,403,13,2),
(10644,420,13,2),
(10645,376,13,2),
(10646,755,13,2),
(10647,648,13,2),
(10648,600,13,2),
(10649,404,13,2),
(10650,402,13,2),
(10651,401,13,2),
(10652,370,23,2),
(10653,735,23,2),
(10654,612,23,2),
(10655,704,23,2),
(10656,647,23,2),
(10657,617,23,2),
(10658,616,23,2),
(10659,615,23,2),
(10660,614,23,2),
(10661,613,23,2),
(10677,187,24,2),
(10678,813,24,2),
(10679,815,24,2),
(10680,814,24,2),
(10681,640,24,2),
(10682,410,24,2),
(10683,638,24,2),
(10684,413,24,2),
(10685,412,24,2),
(10686,411,24,2),
(10732,370,22,2),
(10733,737,22,2),
(10734,809,22,2),
(10735,812,22,2),
(10736,811,22,2),
(10737,639,22,2),
(10738,403,22,2),
(10739,420,22,2),
(10740,376,22,2),
(10741,755,22,2),
(10742,648,22,2),
(10743,600,22,2),
(10744,404,22,2),
(10745,402,22,2),
(10746,401,22,2),
(10807,370,18,2),
(10808,735,18,2),
(10809,612,18,2),
(10810,704,18,2),
(10811,647,18,2),
(10812,617,18,2),
(10813,616,18,2),
(10814,615,18,2),
(10815,614,18,2),
(10816,613,18,2),
(14488,40800,21,2),
(14489,40975,21,2),
(14490,40982,21,2),
(14491,40981,21,2),
(14492,40980,21,2),
(14493,40979,21,2),
(14494,40978,21,2),
(14495,40977,21,2),
(14496,40976,21,2),
(14497,40829,21,2),
(14498,40833,21,2),
(14499,40832,21,2),
(14500,40831,21,2),
(14501,40830,21,2),
(14502,40823,21,2),
(14503,40825,21,2),
(14504,40824,21,2),
(14505,40818,21,2),
(14506,40835,21,2),
(14507,40819,21,2),
(14508,40811,21,2),
(14509,40837,21,2),
(14510,40813,21,2),
(14511,40806,21,2),
(14512,40810,21,2),
(14513,40809,21,2),
(14514,40808,21,2),
(14515,40807,21,2),
(14516,40801,21,2),
(14517,40805,21,2),
(14518,40804,21,2),
(14519,40803,21,2),
(14520,40802,21,2),
(14521,672,21,2),
(14522,783,21,2),
(14523,816,21,2),
(14524,691,21,2),
(14525,692,21,2),
(14526,682,21,2),
(14527,685,21,2),
(14528,673,21,2),
(14529,720,21,2),
(14530,681,21,2),
(14531,534,21,2),
(14532,710,21,2),
(14533,711,21,2),
(14534,548,21,2),
(14535,709,21,2),
(14536,543,21,2),
(14537,40942,21,2),
(14538,40941,21,2),
(14539,40940,21,2),
(14540,40939,21,2),
(14541,538,21,2),
(14542,40938,21,2),
(14543,40937,21,2),
(14544,40936,21,2),
(14545,40935,21,2),
(14546,40934,21,2),
(14547,40933,21,2),
(14548,468,21,2),
(14549,792,21,2),
(14550,798,21,2),
(14551,797,21,2),
(14552,796,21,2),
(14553,795,21,2),
(14554,794,21,2),
(14555,793,21,2),
(14556,766,21,2),
(14557,782,21,2),
(14558,781,21,2),
(14559,773,21,2),
(14560,772,21,2),
(14561,771,21,2),
(14562,770,21,2),
(14563,769,21,2),
(14564,768,21,2),
(14565,767,21,2),
(14566,473,21,2),
(14567,477,21,2),
(14568,476,21,2),
(14569,475,21,2),
(14570,474,21,2),
(14571,471,21,2),
(14572,472,21,2),
(14573,469,21,2),
(14574,467,21,2),
(14575,484,21,2),
(14576,483,21,2),
(14577,482,21,2),
(14578,481,21,2),
(14579,480,21,2),
(14580,479,21,2),
(14581,232,21,2),
(14582,236,21,2),
(14583,235,21,2),
(14584,234,21,2),
(14585,233,21,2),
(14586,426,21,2),
(14587,702,21,2),
(14588,703,21,2),
(14589,701,21,2),
(14590,427,21,2),
(14591,40930,21,2),
(14592,40929,21,2),
(14593,40928,21,2),
(14594,431,21,2),
(14595,430,21,2),
(14596,429,21,2),
(14597,428,21,2),
(14598,302,21,2),
(14599,40927,21,2),
(14600,40926,21,2),
(14601,40925,21,2),
(14602,40924,21,2),
(14603,297,21,2),
(14604,40923,21,2),
(14605,40922,21,2),
(14606,40921,21,2),
(14607,40920,21,2),
(14608,292,21,2),
(14609,40919,21,2),
(14610,40918,21,2),
(14611,40917,21,2),
(14612,40916,21,2),
(14613,370,21,2),
(14614,799,21,2),
(14615,737,21,2),
(14616,809,21,2),
(14617,812,21,2),
(14618,811,21,2),
(14619,639,21,2),
(14620,403,21,2),
(14621,420,21,2),
(14622,376,21,2),
(14623,755,21,2),
(14624,648,21,2),
(14625,600,21,2),
(14626,404,21,2),
(14627,402,21,2),
(14628,401,21,2),
(14629,736,21,2),
(14630,696,21,2),
(14631,698,21,2),
(14632,697,21,2),
(14633,693,21,2),
(14634,695,21,2),
(14635,694,21,2),
(14636,662,21,2),
(14637,663,21,2),
(14638,657,21,2),
(14639,671,21,2),
(14640,670,21,2),
(14641,661,21,2),
(14642,660,21,2),
(14643,659,21,2),
(14644,658,21,2),
(14645,378,21,2),
(14646,749,21,2),
(14647,748,21,2),
(14648,747,21,2),
(14649,746,21,2),
(14650,653,21,2),
(14651,656,21,2),
(14652,655,21,2),
(14653,654,21,2),
(14654,649,21,2),
(14655,652,21,2),
(14656,651,21,2),
(14657,650,21,2),
(14658,495,21,2),
(14659,499,21,2),
(14660,498,21,2),
(14661,497,21,2),
(14662,496,21,2),
(14663,408,21,2),
(14664,735,21,2),
(14665,745,21,2),
(14666,733,21,2),
(14667,641,21,2),
(14668,707,21,2),
(14669,706,21,2),
(14670,645,21,2),
(14671,644,21,2),
(14672,643,21,2),
(14673,642,21,2),
(14674,618,21,2),
(14675,622,21,2),
(14676,621,21,2),
(14677,620,21,2),
(14678,619,21,2),
(14679,612,21,2),
(14680,704,21,2),
(14681,647,21,2),
(14682,617,21,2),
(14683,616,21,2),
(14684,615,21,2),
(14685,614,21,2),
(14686,613,21,2),
(14687,392,21,2),
(14688,721,21,2),
(14689,719,21,2),
(14690,713,21,2),
(14691,459,21,2),
(14692,705,21,2),
(14693,646,21,2),
(14694,494,21,2),
(14695,463,21,2),
(14696,462,21,2),
(14697,461,21,2),
(14698,460,21,2),
(14699,397,21,2),
(14700,377,21,2),
(14701,390,21,2),
(14702,388,21,2),
(14703,353,21,2),
(14704,40993,21,2),
(14705,40996,21,2),
(14706,40995,21,2),
(14707,40994,21,2),
(14708,786,21,2),
(14709,788,21,2),
(14710,790,21,2),
(14711,789,21,2),
(14712,787,21,2),
(14713,784,21,2),
(14714,785,21,2),
(14715,750,21,2),
(14716,40974,21,2),
(14717,754,21,2),
(14718,753,21,2),
(14719,752,21,2),
(14720,751,21,2),
(14721,553,21,2),
(14722,40973,21,2),
(14723,40972,21,2),
(14724,40971,21,2),
(14725,40970,21,2),
(14726,791,21,2),
(14727,524,21,2),
(14728,40969,21,2),
(14729,40968,21,2),
(14730,40967,21,2),
(14731,40966,21,2),
(14732,717,21,2),
(14733,715,21,2),
(14734,608,21,2),
(14735,611,21,2),
(14736,610,21,2),
(14737,609,21,2),
(14738,601,21,2),
(14739,604,21,2),
(14740,603,21,2),
(14741,602,21,2),
(14742,529,21,2),
(14743,607,21,2),
(14744,606,21,2),
(14745,605,21,2),
(14746,513,21,2),
(14747,40965,21,2),
(14748,40964,21,2),
(14749,40963,21,2),
(14750,40962,21,2),
(14751,718,21,2),
(14752,716,21,2),
(14753,522,21,2),
(14754,523,21,2),
(14755,518,21,2),
(14756,521,21,2),
(14757,520,21,2),
(14758,519,21,2),
(14759,508,21,2),
(14760,40961,21,2),
(14761,40960,21,2),
(14762,40959,21,2),
(14763,40958,21,2),
(14764,40988,21,2),
(14765,40992,21,2),
(14766,40991,21,2),
(14767,40990,21,2),
(14768,40989,21,2),
(14769,40983,21,2),
(14770,40987,21,2),
(14771,40986,21,2),
(14772,40985,21,2),
(14773,40984,21,2),
(14774,505,21,2),
(14775,40949,21,2),
(14776,40948,21,2),
(14777,365,21,2),
(14778,40957,21,2),
(14779,40956,21,2),
(14780,40955,21,2),
(14781,40954,21,2),
(14782,360,21,2),
(14783,40953,21,2),
(14784,40952,21,2),
(14785,40951,21,2),
(14786,40950,21,2),
(14787,354,21,2),
(14788,40947,21,2),
(14789,40946,21,2),
(14790,40945,21,2),
(14791,40944,21,2),
(14792,40943,21,2),
(14793,334,21,2),
(14794,726,21,2),
(14795,730,21,2),
(14796,729,21,2),
(14797,728,21,2),
(14798,727,21,2),
(14799,723,21,2),
(14800,725,21,2),
(14801,724,21,2),
(14802,678,21,2),
(14803,680,21,2),
(14804,679,21,2),
(14805,676,21,2),
(14806,677,21,2),
(14807,598,21,2),
(14808,731,21,2),
(14809,599,21,2),
(14810,585,21,2),
(14811,588,21,2),
(14812,594,21,2),
(14813,593,21,2),
(14814,592,21,2),
(14815,591,21,2),
(14816,590,21,2),
(14817,589,21,2),
(14818,587,21,2),
(14819,586,21,2),
(14820,485,21,2),
(14821,40841,21,2),
(14822,491,21,2),
(14823,490,21,2),
(14824,489,21,2),
(14825,488,21,2),
(14826,487,21,2),
(14827,421,21,2),
(14828,40849,21,2),
(14829,40848,21,2),
(14830,40847,21,2),
(14831,40846,21,2),
(14832,345,21,2),
(14833,40932,21,2),
(14834,40931,21,2),
(14835,579,21,2),
(14836,578,21,2),
(14837,577,21,2),
(14838,576,21,2),
(14839,575,21,2),
(14840,574,21,2),
(14841,573,21,2),
(14842,572,21,2),
(14843,340,21,2),
(14844,40855,21,2),
(14845,40854,21,2),
(14846,40840,21,2),
(14847,40839,21,2),
(14848,40838,21,2),
(14849,722,21,2),
(14850,597,21,2),
(14851,453,21,2),
(14852,436,21,2),
(14853,335,21,2),
(14854,40853,21,2),
(14855,40852,21,2),
(14856,40851,21,2),
(14857,40850,21,2),
(14858,266,21,2),
(14859,774,21,2),
(14860,780,21,2),
(14861,779,21,2),
(14862,778,21,2),
(14863,777,21,2),
(14864,776,21,2),
(14865,775,21,2),
(14866,686,21,2),
(14867,690,21,2),
(14868,689,21,2),
(14869,688,21,2),
(14870,687,21,2),
(14871,454,21,2),
(14872,40915,21,2),
(14873,40914,21,2),
(14874,40913,21,2),
(14875,40912,21,2),
(14876,447,21,2),
(14877,40911,21,2),
(14878,40910,21,2),
(14879,40909,21,2),
(14880,40908,21,2),
(14881,40907,21,2),
(14882,714,21,2),
(14883,708,21,2),
(14884,287,21,2),
(14885,40906,21,2),
(14886,40905,21,2),
(14887,40904,21,2),
(14888,40903,21,2),
(14889,282,21,2),
(14890,40902,21,2),
(14891,40901,21,2),
(14892,40900,21,2),
(14893,40899,21,2),
(14894,277,21,2),
(14895,40898,21,2),
(14896,40897,21,2),
(14897,40896,21,2),
(14898,40895,21,2),
(14899,272,21,2),
(14900,40894,21,2),
(14901,40893,21,2),
(14902,40892,21,2),
(14903,40891,21,2),
(14904,267,21,2),
(14905,40890,21,2),
(14906,40889,21,2),
(14907,40888,21,2),
(14908,40887,21,2),
(14909,248,21,2),
(14910,442,21,2),
(14911,40886,21,2),
(14912,40885,21,2),
(14913,40884,21,2),
(14914,40883,21,2),
(14915,437,21,2),
(14916,40882,21,2),
(14917,40881,21,2),
(14918,40880,21,2),
(14919,40879,21,2),
(14920,328,21,2),
(14921,40878,21,2),
(14922,40877,21,2),
(14923,40876,21,2),
(14924,40875,21,2),
(14925,40874,21,2),
(14926,323,21,2),
(14927,40873,21,2),
(14928,40872,21,2),
(14929,40871,21,2),
(14930,40870,21,2),
(14931,312,21,2),
(14932,40869,21,2),
(14933,40868,21,2),
(14934,40867,21,2),
(14935,40866,21,2),
(14936,307,21,2),
(14937,40865,21,2),
(14938,40864,21,2),
(14939,40863,21,2),
(14940,40862,21,2),
(14941,40861,21,2),
(14942,40860,21,2),
(14943,261,21,2),
(14944,40859,21,2),
(14945,40858,21,2),
(14946,40857,21,2),
(14947,40856,21,2),
(14948,254,21,2),
(14949,40845,21,2),
(14950,40844,21,2),
(14951,40843,21,2),
(14952,40842,21,2),
(14953,635,21,2),
(14954,253,21,2),
(14955,252,21,2),
(14956,251,21,2),
(14957,250,21,2),
(14958,249,21,2),
(14959,208,21,2),
(14960,187,21,2),
(14961,813,21,2),
(14962,815,21,2),
(14963,814,21,2),
(14964,640,21,2),
(14965,624,21,2),
(14966,630,21,2),
(14967,634,21,2),
(14968,633,21,2),
(14969,632,21,2),
(14970,631,21,2),
(14971,629,21,2),
(14972,583,21,2),
(14973,584,21,2),
(14974,415,21,2),
(14975,500,21,2),
(14976,502,21,2),
(14977,501,21,2),
(14978,419,21,2),
(14979,418,21,2),
(14980,417,21,2),
(14981,416,21,2),
(14982,410,21,2),
(14983,638,21,2),
(14984,413,21,2),
(14985,412,21,2),
(14986,411,21,2),
(14987,184,21,2),
(14988,734,21,2),
(14989,739,21,2),
(14990,741,21,2),
(14991,740,21,2),
(14992,738,21,2),
(14993,744,21,2),
(14994,743,21,2),
(14995,666,21,2),
(14996,732,21,2),
(14997,668,21,2),
(14998,667,21,2),
(14999,556,21,2),
(15000,712,21,2),
(15001,684,21,2),
(15002,623,21,2),
(15003,596,21,2),
(15004,568,21,2),
(15005,567,21,2),
(15006,492,21,2),
(15007,561,21,2),
(15008,493,21,2),
(15009,381,21,2),
(15010,562,21,2),
(15011,385,21,2),
(15012,384,21,2),
(15013,383,21,2),
(15014,382,21,2),
(15015,371,21,2),
(15016,665,21,2),
(15017,435,21,2),
(15018,375,21,2),
(15019,374,21,2),
(15020,373,21,2),
(15021,372,21,2),
(15022,183,21,2),
(15023,808,21,2),
(15024,807,21,2),
(15025,806,21,2),
(15026,805,21,2),
(15027,804,21,2),
(15028,803,21,2),
(15029,802,21,2),
(15030,800,21,2),
(15552,672,4,2),
(15553,783,4,2),
(15554,816,4,2),
(15555,691,4,2),
(15556,692,4,2),
(15557,682,4,2),
(15558,685,4,2),
(15559,673,4,2),
(15560,720,4,2),
(15561,681,4,2),
(15562,534,4,2),
(15563,710,4,2),
(15564,711,4,2),
(15565,548,4,2),
(15566,709,4,2),
(15567,543,4,2),
(15568,40942,4,2),
(15569,40941,4,2),
(15570,40940,4,2),
(15571,40939,4,2),
(15572,538,4,2),
(15573,40938,4,2),
(15574,40937,4,2),
(15575,40936,4,2),
(15576,40935,4,2),
(15577,40934,4,2),
(15578,40933,4,2),
(15579,468,4,2),
(15580,792,4,2),
(15581,798,4,2),
(15582,797,4,2),
(15583,796,4,2),
(15584,795,4,2),
(15585,794,4,2),
(15586,793,4,2),
(15587,766,4,2),
(15588,782,4,2),
(15589,781,4,2),
(15590,773,4,2),
(15591,772,4,2),
(15592,771,4,2),
(15593,770,4,2),
(15594,769,4,2),
(15595,768,4,2),
(15596,767,4,2),
(15597,473,4,2),
(15598,477,4,2),
(15599,476,4,2),
(15600,475,4,2),
(15601,474,4,2),
(15602,471,4,2),
(15603,472,4,2),
(15604,469,4,2),
(15605,467,4,2),
(15606,484,4,2),
(15607,483,4,2),
(15608,482,4,2),
(15609,481,4,2),
(15610,480,4,2),
(15611,479,4,2),
(15612,232,4,2),
(15613,236,4,2),
(15614,235,4,2),
(15615,234,4,2),
(15616,233,4,2),
(15617,426,4,2),
(15618,702,4,2),
(15619,703,4,2),
(15620,701,4,2),
(15621,427,4,2),
(15622,40930,4,2),
(15623,40929,4,2),
(15624,40928,4,2),
(15625,431,4,2),
(15626,430,4,2),
(15627,429,4,2),
(15628,428,4,2),
(15629,302,4,2),
(15630,40927,4,2),
(15631,40926,4,2),
(15632,40925,4,2),
(15633,40924,4,2),
(15634,297,4,2),
(15635,40923,4,2),
(15636,40922,4,2),
(15637,40921,4,2),
(15638,40920,4,2),
(15639,292,4,2),
(15640,40919,4,2),
(15641,40918,4,2),
(15642,40917,4,2),
(15643,40916,4,2),
(15644,370,4,2),
(15645,41002,4,2),
(15646,41006,4,2),
(15647,41005,4,2),
(15648,41004,4,2),
(15649,41003,4,2),
(15650,40997,4,2),
(15651,41010,4,2),
(15652,41009,4,2),
(15653,41001,4,2),
(15654,41000,4,2),
(15655,40999,4,2),
(15656,40998,4,2),
(15657,799,4,2),
(15658,737,4,2),
(15659,809,4,2),
(15660,812,4,2),
(15661,811,4,2),
(15662,639,4,2),
(15663,403,4,2),
(15664,420,4,2),
(15665,376,4,2),
(15666,755,4,2),
(15667,648,4,2),
(15668,600,4,2),
(15669,404,4,2),
(15670,402,4,2),
(15671,401,4,2),
(15672,736,4,2),
(15673,696,4,2),
(15674,698,4,2),
(15675,697,4,2),
(15676,693,4,2),
(15677,695,4,2),
(15678,694,4,2),
(15679,662,4,2),
(15680,663,4,2),
(15681,657,4,2),
(15682,671,4,2),
(15683,670,4,2),
(15684,661,4,2),
(15685,660,4,2),
(15686,659,4,2),
(15687,658,4,2),
(15688,378,4,2),
(15689,749,4,2),
(15690,748,4,2),
(15691,747,4,2),
(15692,746,4,2),
(15693,653,4,2),
(15694,656,4,2),
(15695,655,4,2),
(15696,654,4,2),
(15697,649,4,2),
(15698,652,4,2),
(15699,651,4,2),
(15700,650,4,2),
(15701,495,4,2),
(15702,499,4,2),
(15703,498,4,2),
(15704,497,4,2),
(15705,496,4,2),
(15706,408,4,2),
(15707,735,4,2),
(15708,745,4,2),
(15709,733,4,2),
(15710,641,4,2),
(15711,707,4,2),
(15712,706,4,2),
(15713,645,4,2),
(15714,644,4,2),
(15715,643,4,2),
(15716,642,4,2),
(15717,618,4,2),
(15718,622,4,2),
(15719,621,4,2),
(15720,620,4,2),
(15721,619,4,2),
(15722,612,4,2),
(15723,704,4,2),
(15724,647,4,2),
(15725,617,4,2),
(15726,616,4,2),
(15727,615,4,2),
(15728,614,4,2),
(15729,613,4,2),
(15730,392,4,2),
(15731,721,4,2),
(15732,719,4,2),
(15733,713,4,2),
(15734,459,4,2),
(15735,705,4,2),
(15736,646,4,2),
(15737,494,4,2),
(15738,463,4,2),
(15739,462,4,2),
(15740,461,4,2),
(15741,460,4,2),
(15742,397,4,2),
(15743,377,4,2),
(15744,390,4,2),
(15745,388,4,2),
(15746,353,4,2),
(15747,40993,4,2),
(15748,40996,4,2),
(15749,40995,4,2),
(15750,40994,4,2),
(15751,786,4,2),
(15752,788,4,2),
(15753,790,4,2),
(15754,789,4,2),
(15755,787,4,2),
(15756,784,4,2),
(15757,785,4,2),
(15758,750,4,2),
(15759,40974,4,2),
(15760,754,4,2),
(15761,753,4,2),
(15762,752,4,2),
(15763,751,4,2),
(15764,553,4,2),
(15765,40973,4,2),
(15766,40972,4,2),
(15767,40971,4,2),
(15768,40970,4,2),
(15769,791,4,2),
(15770,524,4,2),
(15771,40969,4,2),
(15772,40968,4,2),
(15773,40967,4,2),
(15774,40966,4,2),
(15775,717,4,2),
(15776,715,4,2),
(15777,608,4,2),
(15778,611,4,2),
(15779,610,4,2),
(15780,609,4,2),
(15781,601,4,2),
(15782,604,4,2),
(15783,603,4,2),
(15784,602,4,2),
(15785,529,4,2),
(15786,607,4,2),
(15787,606,4,2),
(15788,605,4,2),
(15789,513,4,2),
(15790,40965,4,2),
(15791,40964,4,2),
(15792,40963,4,2),
(15793,40962,4,2),
(15794,718,4,2),
(15795,716,4,2),
(15796,522,4,2),
(15797,523,4,2),
(15798,518,4,2),
(15799,521,4,2),
(15800,520,4,2),
(15801,519,4,2),
(15802,508,4,2),
(15803,40961,4,2),
(15804,40960,4,2),
(15805,40959,4,2),
(15806,40958,4,2),
(15807,40988,4,2),
(15808,40992,4,2),
(15809,40991,4,2),
(15810,40990,4,2),
(15811,40989,4,2),
(15812,40983,4,2),
(15813,40987,4,2),
(15814,40986,4,2),
(15815,40985,4,2),
(15816,40984,4,2),
(15817,505,4,2),
(15818,40949,4,2),
(15819,40948,4,2),
(15820,365,4,2),
(15821,40957,4,2),
(15822,40956,4,2),
(15823,40955,4,2),
(15824,40954,4,2),
(15825,360,4,2),
(15826,40953,4,2),
(15827,40952,4,2),
(15828,40951,4,2),
(15829,40950,4,2),
(15830,354,4,2),
(15831,40947,4,2),
(15832,40946,4,2),
(15833,40945,4,2),
(15834,40944,4,2),
(15835,40943,4,2),
(15836,334,4,2),
(15837,726,4,2),
(15838,730,4,2),
(15839,729,4,2),
(15840,728,4,2),
(15841,727,4,2),
(15842,723,4,2),
(15843,725,4,2),
(15844,724,4,2),
(15845,678,4,2),
(15846,680,4,2),
(15847,679,4,2),
(15848,676,4,2),
(15849,677,4,2),
(15850,598,4,2),
(15851,731,4,2),
(15852,599,4,2),
(15853,585,4,2),
(15854,588,4,2),
(15855,594,4,2),
(15856,593,4,2),
(15857,592,4,2),
(15858,591,4,2),
(15859,590,4,2),
(15860,589,4,2),
(15861,587,4,2),
(15862,586,4,2),
(15863,485,4,2),
(15864,40841,4,2),
(15865,491,4,2),
(15866,490,4,2),
(15867,489,4,2),
(15868,488,4,2),
(15869,487,4,2),
(15870,421,4,2),
(15871,40849,4,2),
(15872,40848,4,2),
(15873,40847,4,2),
(15874,40846,4,2),
(15875,345,4,2),
(15876,40932,4,2),
(15877,40931,4,2),
(15878,579,4,2),
(15879,578,4,2),
(15880,577,4,2),
(15881,576,4,2),
(15882,575,4,2),
(15883,574,4,2),
(15884,573,4,2),
(15885,572,4,2),
(15886,340,4,2),
(15887,40855,4,2),
(15888,40854,4,2),
(15889,40840,4,2),
(15890,40839,4,2),
(15891,40838,4,2),
(15892,722,4,2),
(15893,597,4,2),
(15894,453,4,2),
(15895,436,4,2),
(15896,335,4,2),
(15897,40853,4,2),
(15898,40852,4,2),
(15899,40851,4,2),
(15900,40850,4,2),
(15901,266,4,2),
(15902,774,4,2),
(15903,780,4,2),
(15904,779,4,2),
(15905,778,4,2),
(15906,777,4,2),
(15907,776,4,2),
(15908,775,4,2),
(15909,686,4,2),
(15910,690,4,2),
(15911,689,4,2),
(15912,688,4,2),
(15913,687,4,2),
(15914,454,4,2),
(15915,40915,4,2),
(15916,40914,4,2),
(15917,40913,4,2),
(15918,40912,4,2),
(15919,447,4,2),
(15920,40911,4,2),
(15921,40910,4,2),
(15922,40909,4,2),
(15923,40908,4,2),
(15924,40907,4,2),
(15925,714,4,2),
(15926,708,4,2),
(15927,287,4,2),
(15928,40906,4,2),
(15929,40905,4,2),
(15930,40904,4,2),
(15931,40903,4,2),
(15932,282,4,2),
(15933,40902,4,2),
(15934,40901,4,2),
(15935,40900,4,2),
(15936,40899,4,2),
(15937,277,4,2),
(15938,40898,4,2),
(15939,40897,4,2),
(15940,40896,4,2),
(15941,40895,4,2),
(15942,272,4,2),
(15943,40894,4,2),
(15944,40893,4,2),
(15945,40892,4,2),
(15946,40891,4,2),
(15947,267,4,2),
(15948,40890,4,2),
(15949,40889,4,2),
(15950,40888,4,2),
(15951,40887,4,2),
(15952,248,4,2),
(15953,442,4,2),
(15954,40886,4,2),
(15955,40885,4,2),
(15956,40884,4,2),
(15957,40883,4,2),
(15958,437,4,2),
(15959,40882,4,2),
(15960,40881,4,2),
(15961,40880,4,2),
(15962,40879,4,2),
(15963,328,4,2),
(15964,40878,4,2),
(15965,40877,4,2),
(15966,40876,4,2),
(15967,40875,4,2),
(15968,40874,4,2),
(15969,323,4,2),
(15970,40873,4,2),
(15971,40872,4,2),
(15972,40871,4,2),
(15973,40870,4,2),
(15974,312,4,2),
(15975,40869,4,2),
(15976,40868,4,2),
(15977,40867,4,2),
(15978,40866,4,2),
(15979,307,4,2),
(15980,40865,4,2),
(15981,40864,4,2),
(15982,40863,4,2),
(15983,40862,4,2),
(15984,40861,4,2),
(15985,40860,4,2),
(15986,261,4,2),
(15987,40859,4,2),
(15988,40858,4,2),
(15989,40857,4,2),
(15990,40856,4,2),
(15991,254,4,2),
(15992,40845,4,2),
(15993,40844,4,2),
(15994,40843,4,2),
(15995,40842,4,2),
(15996,635,4,2),
(15997,253,4,2),
(15998,252,4,2),
(15999,251,4,2),
(16000,250,4,2),
(16001,249,4,2),
(16002,187,4,2),
(16003,813,4,2),
(16004,815,4,2),
(16005,814,4,2),
(16006,640,4,2),
(16007,624,4,2),
(16008,630,4,2),
(16009,634,4,2),
(16010,633,4,2),
(16011,632,4,2),
(16012,631,4,2),
(16013,629,4,2),
(16014,583,4,2),
(16015,584,4,2),
(16016,415,4,2),
(16017,500,4,2),
(16018,502,4,2),
(16019,501,4,2),
(16020,419,4,2),
(16021,418,4,2),
(16022,417,4,2),
(16023,416,4,2),
(16024,410,4,2),
(16025,638,4,2),
(16026,413,4,2),
(16027,412,4,2),
(16028,411,4,2),
(16029,184,4,2),
(16030,734,4,2),
(16031,739,4,2),
(16032,741,4,2),
(16033,740,4,2),
(16034,738,4,2),
(16035,744,4,2),
(16036,743,4,2),
(16037,666,4,2),
(16038,732,4,2),
(16039,668,4,2),
(16040,667,4,2),
(16041,556,4,2),
(16042,712,4,2),
(16043,684,4,2),
(16044,623,4,2),
(16045,596,4,2),
(16046,568,4,2),
(16047,567,4,2),
(16048,492,4,2),
(16049,561,4,2),
(16050,493,4,2),
(16051,381,4,2),
(16052,562,4,2),
(16053,385,4,2),
(16054,384,4,2),
(16055,383,4,2),
(16056,382,4,2),
(16057,371,4,2),
(16058,665,4,2),
(16059,435,4,2),
(16060,375,4,2),
(16061,374,4,2),
(16062,373,4,2),
(16063,372,4,2),
(16064,183,4,2),
(16065,808,4,2),
(16066,807,4,2),
(16067,806,4,2),
(16068,805,4,2),
(16069,804,4,2),
(16070,803,4,2),
(16071,802,4,2),
(16072,800,4,2),
(16074,41011,4,4),
(16075,41012,4,4);

/*Table structure for table `sys_role_moudle_ini` */

DROP TABLE IF EXISTS `sys_role_moudle_ini`;

CREATE TABLE `sys_role_moudle_ini` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sys_moudle_id` bigint(11) DEFAULT NULL,
  `sys_role_id` bigint(11) DEFAULT NULL,
  `system_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=935 DEFAULT CHARSET=utf8;

/*Data for the table `sys_role_moudle_ini` */

insert  into `sys_role_moudle_ini`(`id`,`sys_moudle_id`,`sys_role_id`,`system_id`) values 
(78,44,1,1),
(79,74,1,1),
(80,78,1,1),
(81,77,1,1),
(82,76,1,1),
(83,75,1,1),
(84,69,1,1),
(85,73,1,1),
(86,72,1,1),
(87,71,1,1),
(88,70,1,1),
(89,64,1,1),
(90,68,1,1),
(91,67,1,1),
(92,66,1,1),
(93,65,1,1),
(94,55,1,1),
(95,62,1,1),
(96,61,1,1),
(97,60,1,1),
(98,59,1,1),
(99,58,1,1),
(100,57,1,1),
(101,56,1,1),
(102,17,1,1),
(103,15,1,1),
(104,50,1,1),
(105,54,1,1),
(106,53,1,1),
(107,52,1,1),
(108,51,1,1),
(109,2,2,1),
(110,63,2,1),
(111,4,2,1),
(112,28,2,1),
(113,32,2,1),
(114,31,2,1),
(115,30,2,1),
(116,29,2,1),
(117,18,2,1),
(118,22,2,1),
(119,21,2,1),
(120,20,2,1),
(121,19,2,1),
(122,9,2,1),
(123,16,2,1),
(124,14,2,1),
(125,13,2,1),
(126,12,2,1),
(127,11,2,1),
(128,10,2,1),
(129,3,2,1),
(130,8,2,1),
(131,7,2,1),
(132,6,2,1),
(133,2,3,1),
(134,63,3,1),
(857,44,4,1),
(858,74,4,1),
(859,78,4,1),
(860,77,4,1),
(861,76,4,1),
(862,75,4,1),
(863,69,4,1),
(864,73,4,1),
(865,72,4,1),
(866,71,4,1),
(867,70,4,1),
(868,64,4,1),
(869,68,4,1),
(870,67,4,1),
(871,66,4,1),
(872,65,4,1),
(873,55,4,1),
(874,90,4,1),
(875,62,4,1),
(876,61,4,1),
(877,60,4,1),
(878,59,4,1),
(879,58,4,1),
(880,57,4,1),
(881,56,4,1),
(882,17,4,1),
(883,15,4,1),
(884,50,4,1),
(885,54,4,1),
(886,53,4,1),
(887,52,4,1),
(888,51,4,1),
(889,2,4,1),
(890,79,4,1),
(891,85,4,1),
(892,89,4,1),
(893,88,4,1),
(894,87,4,1),
(895,86,4,1),
(896,80,4,1),
(897,84,4,1),
(898,83,4,1),
(899,82,4,1),
(900,81,4,1),
(901,63,4,1),
(902,33,4,1),
(903,39,4,1),
(904,43,4,1),
(905,42,4,1),
(906,41,4,1),
(907,40,4,1),
(908,34,4,1),
(909,38,4,1),
(910,37,4,1),
(911,36,4,1),
(912,35,4,1),
(913,4,4,1),
(914,28,4,1),
(915,32,4,1),
(916,31,4,1),
(917,30,4,1),
(918,29,4,1),
(919,18,4,1),
(920,22,4,1),
(921,21,4,1),
(922,20,4,1),
(923,19,4,1),
(924,9,4,1),
(925,16,4,1),
(926,14,4,1),
(927,13,4,1),
(928,12,4,1),
(929,11,4,1),
(930,10,4,1),
(931,3,4,1),
(932,8,4,1),
(933,7,4,1),
(934,6,4,1);

/*Table structure for table `sys_strategy` */

DROP TABLE IF EXISTS `sys_strategy`;

CREATE TABLE `sys_strategy` (
  `id` varchar(100) NOT NULL,
  `aps_direction` varchar(100) DEFAULT NULL,
  `aps_module` varchar(100) DEFAULT NULL,
  `aps_strategy` varchar(100) DEFAULT NULL,
  `aps_auto` varchar(1) DEFAULT NULL,
  `time_interval` int(11) DEFAULT NULL,
  `create_id` bigint(20) DEFAULT NULL,
  `update_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `update_user` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `state` int(2) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `secret_level_no` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `actable_uni_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_strategy` */

/*Table structure for table `sys_subsystem` */

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

/*Data for the table `sys_subsystem` */

insert  into `sys_subsystem`(`id`,`appid`,`appsecret`,`domain`,`file_id`,`name`,`providername`,`system_url`,`enable`,`create_id`,`update_id`,`create_user`,`update_user`,`create_time`,`update_time`,`state`,`remark`,`secret_level_no`,`sort`) values 
(1,'manager','manager','all','','综合业务系统','西安航天','/',1,1,NULL,'管理员',NULL,'2022-07-19 10:20:37',NULL,1,NULL,NULL,1),
(2,'mes','mes','all',NULL,'MES','西安航天','/',1,1,4,'管理员','管理员','2022-07-28 09:56:01','2023-03-28 08:23:10',0,NULL,NULL,2),
(3,'wms','wms','all',NULL,'WMS','西安航天','/',1,1,4,'管理员','管理员','2022-07-28 09:55:59','2023-03-28 08:23:13',0,NULL,NULL,3),
(4,'robotRms','robotRms','all','','RMS','西航','/',1,4,4,'管理员','管理员','2023-03-28 08:23:36','2023-03-28 08:37:24',1,NULL,NULL,1);

/*Table structure for table `sys_variable` */

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

/*Data for the table `sys_variable` */

insert  into `sys_variable`(`var_code`,`var_name`,`var_spec`,`data_type`,`value_type`,`var_value`,`id`,`create_id`,`update_id`,`create_time`,`update_time`,`state`,`remark`,`create_user`,`update_user`,`secret_level_no`) values 
('CHECK_PWD_OVERDUE','检验密码过期','0-不检验，1-检验',NULL,NULL,'0','7c0be1228743f1ab8613a23b1cb23091',1,4,'2022-07-27 14:00:56',NULL,1,NULL,'管理员',NULL,NULL),
('WHITELIST_FILTER','白名单过滤','0-关闭，1-开启',NULL,NULL,'0','a8f79a774953abe07c997a305290c4df',1,NULL,NULL,NULL,1,NULL,NULL,NULL,NULL),
('CHECK_PWD_COMPLEX','检验密码复杂度','0-检验，1-不检验',NULL,NULL,'0','bf8b6806839b2bb2879313652094c74b',1,1,NULL,NULL,1,NULL,NULL,NULL,NULL);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
