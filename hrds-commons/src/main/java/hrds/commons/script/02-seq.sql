-- 初始值
delete from keytable;
INSERT INTO keytable (key_name,key_value) VALUES ('roleid',20);
INSERT INTO keytable (key_name,key_value) VALUES ('tellers',50);
INSERT INTO keytable (key_name,key_value) VALUES ('batchno',20);
INSERT INTO keytable (key_name,key_value) VALUES ('hrds',20);
INSERT INTO keytable (key_name,key_value) VALUES ('paldbcount',0);

delete from etl_para;
INSERT INTO etl_para (etl_sys_cd, para_cd, para_val, para_type, para_desc) VALUES ('SYS', '#txdate', '#txdate', 'param', '当前跑批日,格式yyyyMMdd');
INSERT INTO etl_para (etl_sys_cd, para_cd, para_val, para_type, para_desc) VALUES ('SYS', '#txdate_next', '#txdate_next', 'param', '后一跑批日,格式yyyyMMdd');
INSERT INTO etl_para (etl_sys_cd, para_cd, para_val, para_type, para_desc) VALUES ('SYS', '#txdate_pre', '#txdate_pre', 'param', '前一跑批日,格式yyyyMMdd');

delete from sys_role;
INSERT INTO sys_role (role_id,role_name) VALUES ('1001','管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1002','数据采集管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1003','作业管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1004','信息爬虫管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1005','监控管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1006','全文检索管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1007','数据可视化管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1008','监控管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1009','REST接口管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1010','集市管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1011', '机器学习管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('3001', '集市数据加工管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1012', '流数据管理员');
INSERT INTO sys_role (role_id,role_name) VALUES ('1013', '数据管控管理员');

delete from component_info;
-- insert into component_info(comp_id,comp_name,comp_state,comp_version,icon_info,color_info,comp_type,comp_remark) values('A000','系统管理','1','0.98.0','fa-cog','btn-default','1','');
insert into component_info(comp_id,comp_name,comp_state,comp_version,icon_info,color_info,comp_type,comp_remark) values('B000','数据采集','1','0.98.0','fa-cog','btn-warning','1','');
insert into component_info(comp_id,comp_name,comp_state,comp_version,icon_info,color_info,comp_type,comp_remark) values('C000','作业管理','1','0.98.0','fa-book','btn-primary','1','');
insert into component_info(comp_id,comp_name,comp_state,comp_version,icon_info,color_info,comp_type,comp_remark) values('D000','报表管理','1','0.98.0','fa-bar-chart-o','btn-warning','1','');
insert into component_info(comp_id,comp_name,comp_state,comp_version,icon_info,color_info,comp_type,comp_remark) values('E000','爬虫管理','1','0.98.0','fa-bug','btn-success','1','');
insert into component_info(comp_id,comp_name,comp_state,comp_version,icon_info,color_info,comp_type,comp_remark) values('F000','监控管理','1','0.98.0','fa-search','btn-info','1','');
insert into component_info(comp_id,comp_name,comp_state,comp_version,icon_info,color_info,comp_type,comp_remark) values('B001','全文检索','1','0.98.0','fa-crosshairs','btn-danger','1','');
insert into component_info(comp_id,comp_name,comp_state,comp_version,icon_info,color_info,comp_type,comp_remark) values('G000','REST接口','1','0.98.0','fa-bell','btn-purple','1','');
insert into component_info(comp_id,comp_name,comp_state,comp_version,icon_info,color_info,comp_type,comp_remark) values('H000','集市管理','1','0.98.0','fa-briefcase','btn-pink','1','');


insert into component_info(comp_id,comp_name,comp_state,comp_version,comp_type,comp_remark) values('1000000001','查询引擎','1','0.13.1','2','Hive');
insert into component_info(comp_id,comp_name,comp_state,comp_version,comp_type,comp_remark) values('1000000002','流式计算','1','1.4','2','Spark Streaming');
insert into component_info(comp_id,comp_name,comp_state,comp_version,comp_type,comp_remark) values('1000000003','离线计算','1','2.5.1','2','Hadoop&MapReduce/Berkeley&Spark');
insert into component_info(comp_id,comp_name,comp_state,comp_version,comp_type,comp_remark) values('1000000004','键值存储','1','0.98.11','2','Hbase');
insert into component_info(comp_id,comp_name,comp_state,comp_version,comp_type,comp_remark) values('1000000005','文件存储','1','2.5.1','2','HDFS');
insert into component_info(comp_id,comp_name,comp_state,comp_version,comp_type,comp_remark) values('1000000006','资源管理','1','2.5.1','2','Hadoop&Yarn');

insert into component_info(comp_id,comp_name,comp_state,comp_version,comp_type,comp_remark) values('1000000007','日志收集系统','1','1.5','2','Cloudera&Flume');
insert into component_info(comp_id,comp_name,comp_state,comp_version,comp_type,comp_remark) values('1000000008','消息系统','1','2.9.1','2','Apache&Kafka');
insert into component_info(comp_id,comp_name,comp_state,comp_version,comp_type,comp_remark) values('1000000009','分布式服务','1','3.4.6','2','ZooKeeper');
insert into component_info(comp_id,comp_name,comp_state,comp_version,comp_type,comp_remark) values('1000000010','搜索引擎','1','5.3','2','SolrCloud/Solr');
insert into component_info(comp_id,comp_name,comp_state,comp_version,comp_type,comp_remark) values('1000000011','数据挖掘','1','0.13.1','2','Mahout');


delete from component_menu;
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000100, '/dbgly', '55', '数据对标', 'K001', 'el-icon-s-finance');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000002, '/datasourceManagement', '01', '采集管理', 'B000', 'el-icon-s-data');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000003, '/collectmonitor', '02', '数据采集', 'B000', 'el-icon-s-data');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000009, '/dataReport', '07', '数据可视化查询', 'D000', 'el-icon-search');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000010, '/reportAnalysis', '08', '数据可视化分析', 'D000', 'el-icon-dish-1');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000011, '/reportQuery', '09', '数据可视化查看', 'D000', 'el-icon-food');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000013, '/monitoringMage', '10', '监控管理', 'F000', 'el-icon-chat-round');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000014, '/serviceMage', '11', '服务接口管理', 'G000', 'el-icon-chat-line-round');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000015, '/serviceUser', '12', '服务接口用户', 'G000', 'el-icon-cpu');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000016, '/wordSegMage', '13', '分词器管理', 'B000', 'el-icon-link');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000017, '/reportMage', '06', '数据可视化管理', 'D000', 'el-icon-chicken');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000018, '/dataMart', '14', '数据集市', 'H000', 'el-icon-connection');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000019, '/machineLearn', '16', '机器学习工作台', 'I000', 'el-icon-cpu');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000020, '/dataProcess', '15', '数据加工', 'H000', 'el-icon-film');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000021, '/streamMage', '18', '流数据管理', 'J000', 'el-icon-s-operation');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000022, '/streamProd', '19', '流数据生产', 'J000', 'el-icon-s-fold');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000023, '/yhp', '20', '数据库配置(永洪)', 'Z000', 'el-icon-s-ticket');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000024, '/yhd', '21', '报表创建(永洪)', 'Z001', 'el-icon-s-management');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000025, '/yhq', '22', '报表查看(永洪)', 'Z002', 'el-icon-s-open');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000026, '/etlMage', '04', '作业调度', 'C000', 'el-icon-menu');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000027, '/machineLearnBus', '17', '机器学习业务', 'I000', 'el-icon-cpu');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000028, '/dataControl', '24', '数据管控', 'K001', 'el-icon-s-finance');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000029, '/streamConsump', '23', '流数据消费', 'J000', 'el-icon-s-unfold');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000030, '/freeAnalysisM', '25', '自主分析管理', 'L000', 'el-icon-s-claim');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000031, '/freeAnalysisU', '26', '自主分析操作', 'L001', 'el-icon-s-grid');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000033, '/fullTextSearch', '28', '全文检索', 'A000', 'el-icon-search');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000034, '/webSqlConsole', '29', 'SQL控制台', 'B000', 'el-icon-monitor');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000035, '/typeLengthContrastInfo', '94', '类型长度对比信息', 'B000', 'el-icon-search');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000036, '/dataTypeContrastInfo', '93', '数据类型对比信息', 'B000', 'el-icon-search');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000038, '/tsb', '38', '表结构对标', 'K001', 'el-icon-receiving');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000039, '/tdb', '39', '表数据对标', 'K001', 'el-icon-film');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000005, '/userManagement', '99', '用户管理', 'A000', 'el-icon-user-solid');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000096, '/dataSorting', '96', '数据整理', 'A000', 'el-icon-s-tools');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000006, '/departmentalList', '98', '部门管理', 'A000', 'el-icon-s-cooperation');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000037, '/dbm', '37', '数据对标', 'K001', 'el-icon-s-finance');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000007, 'systemParameters', '97', '系统参数管理', 'A000', 'el-icon-s-order');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000004, '/dataQuery', '27', '资源管理', 'A000', 'el-icon-files');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000008, '/dataStoreLayer', '95', '数据存储层定义', 'A000', 'el-icon-s-unfold');
INSERT INTO component_menu(menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES (1000000092,'/logReview', '92', '日志审查', 'A000', 'el-icon-document');


delete from department_info;
INSERT INTO department_info (dep_id,dep_name,create_date,create_time,dep_remark) VALUES ('1000000001','第一部门','20160101','120500','');

delete from sys_user;
INSERT INTO sys_user(USER_ID, CREATE_ID, DEP_ID, ROLE_ID, USER_NAME, USER_PASSWORD, USER_EMAIL, USER_MOBILE, useris_admin,USER_TYPE, usertype_group,LOGIN_IP, LOGIN_DATE, USER_STATE, CREATE_DATE, CREATE_TIME, UPDATE_DATE, UPDATE_TIME, USER_REMARK, TOKEN, VALID_TIME) VALUES ('1000','1000','1000000001','1001','超级管理员', '1', 'ccc@vv.com', '1234567890', '0','00', '92,99,98,97,95','11', '11', '1', '88888888', '111111', '88888888', '111111', '888','0', '0');
INSERT INTO sys_user(user_id, create_id, dep_id, role_id, user_name, user_password, user_email, user_mobile, useris_admin,user_type, usertype_group, login_ip, login_date, user_state, create_date, create_time, update_date, update_time, user_remark, token, valid_time) VALUES (1001, 1000, 1000000001, '1001', '全功能管理员', '1', 'ccc@vv.com', '1234567890','0','01', '01,04,11,55', null, null, '1', '20181015', '145752', '20181015', '145752', '','0', '0');
INSERT INTO sys_user(user_id, create_id, dep_id, role_id, user_name, user_password, user_email, user_mobile, useris_admin,user_type, usertype_group, login_ip, login_date, user_state, create_date, create_time, update_date, update_time, user_remark, token, valid_time) VALUES (2001, 1000, 1000000001, '1001', '全功能操作员', '1', 'ccc@vv.com', '1234567890','0','02', '37,02,27,12,14,04,24', null, null, '1', '20181015', '145752', '20181015', '145752', '', '0', '0');



delete from interface_info;
INSERT INTO interface_info VALUES (104, 'tableUsePermissions', '表使用权限查询接口', '1', '1', '01-123', null, 1001);
INSERT INTO interface_info VALUES (105, 'generalQuery','单表普通查询接口', '1', '1', '01-124',NULL,  1001);
INSERT INTO interface_info VALUES ('111', 'tableStructureQuery', '表结构查询接口', '1', '1', '01-130', NULL, '1001');
INSERT INTO interface_info VALUES ('114', 'fileAttributeSearch', '文件属性搜索接口', '1', '1', '01-133', NULL, '1001');
INSERT INTO interface_info VALUES ('115', 'sqlInterfaceSearch', 'sql查询接口', '1', '1', '01-134', NULL, '1001');
INSERT INTO interface_info VALUES ('118', 'rowKeySearch', 'rowkey查询', '1', '1', '01-137', null, 1001);
INSERT INTO interface_info VALUES (123, 'uuidDownload', 'UUID数据下载', '2', '1', '01-143', null, 1001);
