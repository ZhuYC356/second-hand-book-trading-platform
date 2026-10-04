-- 种子数据（REPLACE INTO 幂等，可重复执行且覆盖旧数据）
-- 默认账号：admin/123456（管理员）  user1~user6/123456（前台用户）

REPLACE INTO `user` (`id`, `username`, `password`, `nickname`, `role`, `phone`, `email`, `status`, `create_time`) VALUES
(1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'ADMIN', '13800000001', 'admin@book.com', 1, NOW() - INTERVAL 60 DAY),
(2, 'user1', 'e10adc3949ba59abbe56e057f20f883e', '张小明', 'USER', '13800000002', 'user1@book.com', 1, NOW() - INTERVAL 50 DAY),
(3, 'user2', 'e10adc3949ba59abbe56e057f20f883e', '李文静', 'USER', '13800000003', 'user2@book.com', 1, NOW() - INTERVAL 45 DAY),
(4, 'user3', 'e10adc3949ba59abbe56e057f20f883e', '王大锤', 'USER', '13800000004', 'user3@book.com', 1, NOW() - INTERVAL 30 DAY),
(5, 'user4', 'e10adc3949ba59abbe56e057f20f883e', '陈小雨', 'USER', '13800000005', 'user4@book.com', 1, NOW() - INTERVAL 20 DAY),
(6, 'user5', 'e10adc3949ba59abbe56e057f20f883e', '赵子航', 'USER', '13800000006', 'user5@book.com', 1, NOW() - INTERVAL 10 DAY),
(7, 'user6', 'e10adc3949ba59abbe56e057f20f883e', '林清雅', 'USER', '13800000007', 'user6@book.com', 0, NOW() - INTERVAL 5 DAY);

REPLACE INTO `category` (`id`, `name`) VALUES
(1, '文学小说'),
(2, '计算机技术'),
(3, '经济管理'),
(4, '教材教辅'),
(5, '儿童读物'),
(6, '艺术设计'),
(7, '考试考研'),
(8, '生活休闲');

REPLACE INTO `book` (`id`, `seller_id`, `category_id`, `title`, `author`, `isbn`, `price`, `original_price`, `condition_level`, `description`, `cover`, `status`, `create_time`) VALUES
(1,  2, 1, '三体', '刘慈欣', '9787536692930', 15.00, 45.00, '九成新', '经典科幻小说，保存完好，无笔记划线。', '/upload/三体.jpg', 'ON_SALE', NOW() - INTERVAL 25 DAY),
(2,  3, 2, 'Java核心技术 卷I', '凯·霍斯特曼', '9787111212821', 35.00, 149.00, '八成新', '计算机经典教材，少量笔记，内容完整。', '/upload/java核心技术.png', 'ON_SALE', NOW() - INTERVAL 22 DAY),
(3,  4, 1, '活着', '余华', '9787506365437', 12.00, 45.00, '九成新', '余华代表作，品相很好。', '/upload/活着.png', 'SOLD', NOW() - INTERVAL 20 DAY),
(4,  5, 3, '穷查理宝典', '查理·芒格', '9787508664330', 25.00, 88.00, '七成新', '投资理财经典，书角轻微磨损。', NULL, 'ON_SALE', NOW() - INTERVAL 18 DAY),
(5,  6, 4, '高等数学(第七版) 上册', '同济大学数学系', '9787040396614', 18.00, 45.00, '八成新', '考研复习用书，有少量铅笔笔记。', NULL, 'ON_SALE', NOW() - INTERVAL 16 DAY),
(6,  7, 5, '小王子', '圣埃克苏佩里', '9787020042494', 10.00, 30.00, '全新', '多买了一本，全新未拆封。', NULL, 'ON_SALE', NOW() - INTERVAL 14 DAY),
(7,  2, 2, '深入理解Java虚拟机', '周志明', '9787111641277', 40.00, 129.00, '九成新', 'JVM经典之作，几乎全新。', '/upload/java虚拟机.png', 'SOLD', NOW() - INTERVAL 12 DAY),
(8,  3, 6, '艺术的故事', '贡布里希', '9787508064969', 45.00, 280.00, '八成新', '艺术入门必读，精装版。', '/upload/艺术的故事.jpg', 'ON_SALE', NOW() - INTERVAL 11 DAY),
(9,  4, 7, '考研英语词汇恋练有词', '朱伟', '9787519305885', 20.00, 68.00, '七成新', '考研英语词汇书，有笔记。', '/upload/考研英语单词.jpg', 'ON_SALE', NOW() - INTERVAL 10 DAY),
(10, 5, 8, '家常菜谱大全', '编辑部', '9787538490876', 8.00, 35.00, '八成新', '实用菜谱，家庭必备。', '/upload/家常菜.jpg', 'ON_SALE', NOW() - INTERVAL 9 DAY),
(11, 6, 2, 'Spring实战(第5版)', '克雷格·沃斯', '9787115576322', 30.00, 109.00, '九成新', 'Spring学习经典，无划线。', NULL, 'SOLD', NOW() - INTERVAL 8 DAY),
(12, 7, 1, '百年孤独', '加西亚·马尔克斯', '9787544253994', 22.00, 55.00, '九成新', '魔幻现实主义代表作。', '/upload/百年孤独.jpg', 'ON_SALE', NOW() - INTERVAL 7 DAY),
(13, 2, 4, '线性代数(第六版)', '同济大学数学系', '9787040396614', 15.00, 39.00, '八成新', '线代教材，课后题有标注。', '/upload/线性代数.png', 'ON_SALE', NOW() - INTERVAL 6 DAY),
(14, 3, 3, '原则', '瑞·达利欧', '9787508684031', 38.00, 98.00, '九成新', '桥水基金创始人作品。', NULL, 'SOLD', NOW() - INTERVAL 5 DAY),
(15, 4, 5, '窗边的小豆豆', '黑柳彻子', '9787544250580', 14.00, 39.00, '九成新', '温暖的儿童文学。', '/upload/窗边的小豆豆.jpg', 'ON_SALE', NOW() - INTERVAL 4 DAY),
(16, 5, 6, '设计中的设计', '原研哉', '9787536692930', 28.00, 68.00, '七成新', '设计类经典读物。', NULL, 'SOLD', NOW() - INTERVAL 3 DAY),
(17, 6, 7, '肖秀荣考研政治1000题', '肖秀荣', '9787519305885', 25.00, 88.00, '全新', '全新未使用，考研必备。', '/upload/肖1000题.jpg', 'ON_SALE', NOW() - INTERVAL 2 DAY),
(18, 7, 8, '断舍离', '山下英子', '9787544245876', 12.00, 45.00, '八成新', '生活整理类畅销书。', '/upload/断舍离.jpg', 'ON_SALE', NOW() - INTERVAL 1 DAY),
(19, 2, 4, '会计学原理', '约翰·怀尔德', '9787300234567', 20.00, 79.00, '九成新', '会计入门教材，品相好。', NULL, 'SOLD', NOW() - INTERVAL 15 DAY),
(20, 4, 2, 'MySQL必知必会', '本·福塔', '9787115290655', 18.00, 49.00, '八成新', '数据库入门好书。', NULL, 'SOLD', NOW() - INTERVAL 13 DAY),
(21, 5, 1, '围城', '钱钟书', '9787020024759', 15.00, 36.00, '九成新', '钱钟书代表作。', NULL, 'SOLD', NOW() - INTERVAL 9 DAY),
(22, 7, 3, '经济学原理', '曼昆', '9787301234567', 30.00, 128.00, '八成新', '经济学经典教材。', NULL, 'SOLD', NOW() - INTERVAL 10 DAY);

-- 订单：COMPLETED-已完成(成交)  PENDING-待付款  CANCELLED-已取消
REPLACE INTO `orders` (`id`, `order_no`, `book_id`, `buyer_id`, `seller_id`, `price`, `status`, `create_time`, `finish_time`) VALUES
(1,  'SB20260926000001', 3,  3, 4, 12.00, 'CANCELLED', NOW() - INTERVAL 6 DAY, NULL),
(2,  'SB20260926000002', 16, 7, 5, 28.00, 'COMPLETED', NOW() - INTERVAL 6 DAY, DATE_ADD(NOW() - INTERVAL 6 DAY, INTERVAL 2 HOUR)),
(3,  'SB20260927000001', 7,  4, 2, 40.00, 'COMPLETED', NOW() - INTERVAL 5 DAY, DATE_ADD(NOW() - INTERVAL 5 DAY, INTERVAL 1 HOUR)),
(4,  'SB20260927000002', 2,  6, 3, 35.00, 'CANCELLED', NOW() - INTERVAL 5 DAY, NULL),
(5,  'SB20260928000001', 3,  5, 4, 12.00, 'COMPLETED', NOW() - INTERVAL 4 DAY, DATE_ADD(NOW() - INTERVAL 4 DAY, INTERVAL 3 HOUR)),
(6,  'SB20260928000002', 5,  4, 6, 18.00, 'CANCELLED', NOW() - INTERVAL 4 DAY, NULL),
(7,  'SB20260929000001', 14, 6, 3, 38.00, 'CANCELLED', NOW() - INTERVAL 3 DAY, NULL),
(8,  'SB20260929000002', 8,  2, 3, 45.00, 'CANCELLED', NOW() - INTERVAL 3 DAY, NULL),
(9,  'SB20260929000003', 22, 5, 7, 30.00, 'COMPLETED', NOW() - INTERVAL 3 DAY, DATE_ADD(NOW() - INTERVAL 3 DAY, INTERVAL 2 HOUR)),
(10, 'SB20260930000001', 14, 2, 3, 38.00, 'COMPLETED', NOW() - INTERVAL 2 DAY, DATE_ADD(NOW() - INTERVAL 2 DAY, INTERVAL 1 HOUR)),
(11, 'SB20260930000002', 9,  3, 4, 20.00, 'CANCELLED', NOW() - INTERVAL 2 DAY, NULL),
(12, 'SB20260930000003', 12, 4, 7, 22.00, 'CANCELLED', NOW() - INTERVAL 2 DAY, NULL),
(13, 'SB20261001000001', 11, 3, 6, 30.00, 'PENDING',   NOW() - INTERVAL 1 DAY, NULL),
(14, 'SB20261001000002', 18, 5, 7, 12.00, 'CANCELLED', NOW() - INTERVAL 1 DAY, NULL),
(15, 'SB20261001000003', 13, 6, 2, 15.00, 'CANCELLED', NOW() - INTERVAL 1 DAY, NULL),
(16, 'SB20261001000004', 19, 3, 2, 20.00, 'COMPLETED', NOW() - INTERVAL 1 DAY, DATE_ADD(NOW() - INTERVAL 1 DAY, INTERVAL 1 HOUR)),
(17, 'SB20261001000005', 21, 6, 5, 15.00, 'COMPLETED', NOW() - INTERVAL 1 DAY, DATE_ADD(NOW() - INTERVAL 1 DAY, INTERVAL 4 HOUR)),
(18, 'SB20261002000001', 1,  4, 2, 15.00, 'CANCELLED', NOW(), NULL),
(19, 'SB20261002000002', 6,  3, 7, 10.00, 'CANCELLED', NOW(), NULL),
(20, 'SB20261002000003', 20, 2, 4, 18.00, 'COMPLETED', NOW(), DATE_ADD(NOW(), INTERVAL 30 MINUTE));
