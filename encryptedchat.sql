DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `userid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) CHARACTER SET utf8 NOT NULL,
  `password` varchar(45) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`userid`),
  UNIQUE KEY `userid_UNIQUE` (`userid`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
LOCK TABLES `accounts` WRITE;
INSERT INTO `accounts` VALUES (1,'test','testpassword'),(2,'test1','testpassword1'),(3,'中文测试用户','中文测试密码');
UNLOCK TABLES;