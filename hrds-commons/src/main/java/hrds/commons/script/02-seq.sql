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
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000099','A000','/userManagement','99','用户管理','el-icon-user-solid');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000098','A000','/departmentalList','98','部门管理','el-icon-s-cooperation');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000097','A000','systemParameters','97','系统参数管理','el-icon-s-order');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000096','A000','/dataSorting','96','数据整理','el-icon-s-tools');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000095','A000','/dataStoreLayer','95','数据存储层定义','el-icon-s-unfold');

INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000002','B000','/datasourceManagement','01','采集管理','el-icon-s-data');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000003','B000','/collectmonitor','02','数据采集','el-icon-s-data');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000026','C000','/etlMage','04','作业调度','el-icon-menu');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000009','D000','/dataReport','07','数据可视化查询','el-icon-search');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000010','D000','/reportAnalysis','08','数据可视化分析','el-icon-dish-1');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000011','D000','/reportQuery','09','数据可视化查看','el-icon-food');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000017','D000','/reportMage','06','数据可视化管理','el-icon-chicken');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000013','F000','/monitoringMage','10','监控管理','el-icon-chat-round');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000014','G000','/serviceMage','11','服务接口管理','el-icon-chat-line-round');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000015','G000','/serviceUser','12','服务接口用户','el-icon-cpu');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000016','B000','/wordSegMage', '13', '分词器管理','el-icon-link');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000018','H000','/dataMart', '14', '数据集市','el-icon-connection');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000020','H000','/dataProcess', '15', '数据加工','el-icon-film');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000019','I000','/machineLearn', '16', '机器学习工作台','el-icon-cpu');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000027','I000','/machineLearnBus', '17', '机器学习业务','el-icon-cpu');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000021','J000','/streamMage', '18', '流数据管理','el-icon-s-operation');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000022','J000','/streamProd', '19', '流数据生产','el-icon-s-fold');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000029','J000','/streamConsump', '23', '流数据消费','el-icon-s-unfold');
INSERT INTO component_menu (menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES ('1000000023', '/yhp', '20', '数据库配置(永洪)', 'Z000','el-icon-s-ticket');
INSERT INTO component_menu (menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES ('1000000024', '/yhd', '21', '报表创建(永洪)', 'Z001','el-icon-s-management');
INSERT INTO component_menu (menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES ('1000000025', '/yhq', '22', '报表查看(永洪)', 'Z002','el-icon-s-open');
INSERT INTO component_menu (menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES ('1000000028', '/dataControl', '24', '数据管控', 'K001','el-icon-s-finance');
INSERT INTO component_menu (menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES ('1000000030', '/freeAnalysisM', '25', '自主分析管理', 'L000','el-icon-s-claim');
INSERT INTO component_menu (menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES ('1000000031', '/freeAnalysisU', '26', '自主分析操作', 'L001','el-icon-s-grid');

INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000032','A000','/dataQuery','27','文件资源管理器','el-icon-files');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000033','A000','/fullTextSearch','28','全文检索','el-icon-search');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000034','B000','/webSqlConsole','29','SQL控制台','el-icon-monitor');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000035','B000','/typeLengthContrastInfo','94','类型长度对比信息','el-icon-search');
INSERT INTO component_menu (menu_id,comp_id,menu_path,user_type,menu_name,menu_remark) VALUES ('1000000036','B000','/dataTypeContrastInfo','93','数据类型对比信息','el-icon-search');
INSERT INTO component_menu (menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES ('1000000037', '/databenchmarking', '37', '标准元管理', 'K001','el-icon-s-finance');
INSERT INTO component_menu (menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES ('1000000038', '/tableStructureBenchmarking', '38', '表结构对标', 'K001','el-icon-receiving');
INSERT INTO component_menu (menu_id,menu_path,user_type,menu_name,comp_id,menu_remark) VALUES ('1000000039', '/tableDataBenchmarking', '39', '表数据对标', 'K001','el-icon-film');

delete from department_info;
INSERT INTO department_info (dep_id,dep_name,create_date,create_time,dep_remark) VALUES ('1000000001','第一部门','20160101','120500','');

delete from sys_user;
INSERT INTO sys_user(USER_ID, CREATE_ID, DEP_ID, ROLE_ID, USER_NAME, USER_PASSWORD, USER_EMAIL, USER_MOBILE, useris_admin,USER_TYPE, usertype_group,LOGIN_IP, LOGIN_DATE, USER_STATE, CREATE_DATE, CREATE_TIME, UPDATE_DATE, UPDATE_TIME, USER_REMARK, TOKEN, VALID_TIME) VALUES ('1000','1000','1000000001','1001','超级管理员', '1', 'ccc@vv.com', '1234567890', '0','00', '99,98,97,96,95,94,93','11', '11', '1', '88888888', '111111', '88888888', '111111', '888','0', '0');
INSERT INTO sys_user(user_id, create_id, dep_id, role_id, user_name, user_password, user_email, user_mobile, useris_admin,user_type, usertype_group, login_ip, login_date, user_state, create_date, create_time, update_date, update_time, user_remark, token, valid_time) VALUES (1001, 1000, 1000000001, '1001', '全功能管理员', '1', 'ccc@vv.com', '1234567890','0','01', '01,04,07,10,11,13,16,18,25', null, null, '1', '20181015', '145752', '20181015', '145752', '','0', '0');
INSERT INTO sys_user(user_id, create_id, dep_id, role_id, user_name, user_password, user_email, user_mobile, useris_admin,user_type, usertype_group, login_ip, login_date, user_state, create_date, create_time, update_date, update_time, user_remark, token, valid_time) VALUES (2001, 1000, 1000000001, '1001', '全功能操作员', '1', 'ccc@vv.com', '1234567890','0','02', '02,04,08,09,12,14,15,17,19,23,24,26,27,28,29,37,38,39', null, null, '1', '20181015', '145752', '20181015', '145752', '', '0', '0');

delete from edw_job_alg;
INSERT INTO edw_job_alg VALUES ('F1', 'F1:Delete/Insert');
INSERT INTO edw_job_alg VALUES ('F2', 'F2:Upsert');
INSERT INTO edw_job_alg VALUES ('F3', 'F3:History Chain');
INSERT INTO edw_job_alg VALUES ('F5', 'F5:FullData History Chain');
INSERT INTO edw_job_alg VALUES ('I', 'I:Append');



delete from interface_info;
INSERT INTO interface_info VALUES (103, 'marketPagingQuery', '集市表分页查询', '1', '1', '01-122', null, 1001);
INSERT INTO interface_info VALUES (104, 'permissionsTable', '表使用权限查询接口', '1', '1', '01-123', null, 1001);
INSERT INTO interface_info VALUES (105, 'generalQuery','单表普通查询接口', '1', '1', '01-124',NULL,  1001);
INSERT INTO interface_info VALUES (106, 'fastQuery','单表索引查询接口', '1', '1', '01-125', NULL, 1001);
-- INSERT INTO interface_info VALUES (107, 'solrTokenizer','solr词库接口', '2', '1', '01-126', NULL, 1001);
INSERT INTO interface_info VALUES (108, 'fullTextSearch','全文检索接口', '2', '1', '01-127', NULL, 1001);
INSERT INTO interface_info VALUES ('109', 'tableDataDelete', '单表数据删除接口', '1', '1', '01-128', NULL, '1001');
INSERT INTO interface_info VALUES ('110', 'tableDelete', '单表删除接口', '1', '1', '01-129', NULL, '1001');
INSERT INTO interface_info VALUES ('111', 'tableSearch', '表结构查询接口', '1', '1', '01-130', NULL, '1001');
-- INSERT INTO interface_info VALUES ('112', 'summarySearch', '文章摘要接口', '1', '1', '01-131', NULL, '1001');
INSERT INTO interface_info VALUES ('113', 'textSimilar', '文章相似度比较接口', '2', '1', '01-132', NULL, '1001');
INSERT INTO interface_info VALUES ('114', 'smallFileSearch', '文件属性搜索接口', '1', '1', '01-133', NULL, '1001');
INSERT INTO interface_info VALUES ('115', 'sqlInterfaceSearch', 'sql查询接口', '1', '1', '01-134', NULL, '1001');
INSERT INTO interface_info VALUES ('116', 'getOriginalData', '数据下载接口', '2', '1', '01-135', NULL, '1001');
INSERT INTO interface_info VALUES ('117', 'secondIndexBatch', '二级索引日期范围查询接口', '1', '1', '01-136', NULL, '1001');
INSERT INTO interface_info VALUES ('118', 'rowKeySearch', 'rowkey查询', '1', '1', '01-137', null, 1001);
INSERT INTO interface_info VALUES ('119', 'solrSearch', 'solr查询接口', '1', '1', '01-138', NULL, '1001');
INSERT INTO interface_info VALUES ('120', 'fastQueryHbase', '单表二级索引查询接口（hyren）', '1', '1', '01-140', null, 1001);
INSERT INTO interface_info VALUES (121, 'dataBatchOper', '表数据批量更新接口', '1', '1', '01-141', null, 1001);
INSERT INTO interface_info VALUES (122, 'hbaseSolrQuery', 'Solr查询Hbase数据接口', '1', '1', '01-142', null, 1001);
INSERT INTO interface_info VALUES (123, 'uuidDownload', 'UUID数据下载', '2', '1', '01-143', null, 1001);
INSERT INTO interface_info VALUES (124, 'language', '文本语种', '2', '1', '01-144', null, 1001);
INSERT INTO interface_info VALUES (125, 'entityRecognition', '实体识别', '2', '1', '01-145', null, 1001);
INSERT INTO interface_info VALUES (126, 'emotionValue', '情感分析', '2', '1', '01-146', null, 1001);
INSERT INTO interface_info VALUES (127, 'textClassification', '文本分类', '2', '1', '01-147', null, 1001);
INSERT INTO interface_info VALUES (128, 'ocrExtract', 'OCR识别', '2', '1', '01-148', null, 1001);
INSERT INTO interface_info VALUES (129, 'pictureSeach', '以图搜图接口', '1', '1', '01-149', null, 1001);
INSERT INTO interface_info VALUES (132, 'dataTableOperate', '数据表操作接口', '1', '1', '01-152', NULL, 1001);
INSERT INTO interface_info VALUES (137, 'inserthbasedata', 'HBase数据新增接口', '1', '1', '01-157', null, 1001);
INSERT INTO interface_info VALUES (136, 'tableCreate', 'hbase表创建接口', '1', '1', '01-156', null, 1001);
INSERT INTO interface_info VALUES (138, 'createTable', '创建无溯源Spark表接口', '1', '1', '01-158', NULL, 1001);
INSERT INTO interface_info VALUES (139, 'insertTable', '插入Spark数据接口', '1', '1', '01-159', NULL, 1001);
INSERT INTO interface_info VALUES (140, 'updateTable', '更新Spark表数据接口', '1', '1', '01-160', NULL, 1001);
INSERT INTO interface_info VALUES (141, 'deleteTableData', '删除Spark表数据接口', '1', '1', '01-161', NULL, 1001);
INSERT INTO interface_info VALUES (142, 'dropTable', '删除Spark表接口', '1', '1', '01-162', NULL, 1001);
INSERT INTO interface_info VALUES (143, 'createTraceTable', '创建有溯源Spark表接口', '1', '1', '01-163', NULL, 1001);
INSERT INTO interface_info VALUES (144, 'singleFileUpload', '单文件上传接口', '1', '1', '01-150', null, 1001);




delete from ml_datatransfer_fun;
INSERT INTO ml_datatransfer_fun (tranfunt_num, funtype_name) VALUES (1, '算术');
INSERT INTO ml_datatransfer_fun (tranfunt_num, funtype_name) VALUES (2, '类型转换');
INSERT INTO ml_datatransfer_fun (tranfunt_num, funtype_name) VALUES (3, '日期运算');
INSERT INTO ml_datatransfer_fun (tranfunt_num, funtype_name) VALUES (4, '日期创建');
INSERT INTO ml_datatransfer_fun (tranfunt_num, funtype_name) VALUES (5, '字符串');

delete from dq_rule_type_def;
INSERT INTO dq_rule_type_def (CASE_TYPE, CASE_TYPE_DESC, INDEX_DESC1, INDEX_DESC2, INDEX_DESC3, REMARK) VALUES ('COL ENUM', '字段枚举检测', '不在范围内的记录数', '检查总记录数', '', '检测目标表名的 目标表关键字段是否在清单值域 内，格式转义需用户直接转义');
INSERT INTO dq_rule_type_def (CASE_TYPE, CASE_TYPE_DESC, INDEX_DESC1, INDEX_DESC2, INDEX_DESC3, REMARK) VALUES ('COL FK', '字段外键检测', '外键不存在的记录数', '检查总记录数', '', '检测目标表名的 目标表关键字段(，分割多个字段)是否在 比对表表名 的 比对表关键字段 存在 ');
INSERT INTO dq_rule_type_def (CASE_TYPE, CASE_TYPE_DESC, INDEX_DESC1, INDEX_DESC2, INDEX_DESC3, REMARK) VALUES ('COL NAN', '字段非空', '空的记录数', '检查总记录数', '', '检测目标表名的 目标表关键字段是否非空');
INSERT INTO dq_rule_type_def (CASE_TYPE, CASE_TYPE_DESC, INDEX_DESC1, INDEX_DESC2, INDEX_DESC3, REMARK) VALUES ('COL PK', '字段主键检测', '主键重复的记录数', '检查总记录数', '', '检测目标表名的 目标表关键字段(，分割多个字段)是否为主键唯一 ');
INSERT INTO dq_rule_type_def (CASE_TYPE, CASE_TYPE_DESC, INDEX_DESC1, INDEX_DESC2, INDEX_DESC3, REMARK) VALUES ('COL RANG', '字段范围检测', '不在范围内的记录数', '检查总记录数', '', '检测目标表名的 目标表关键字段是否在【范围区间的最小值，范围区间的最大值】内，格式转义需用户直接转义');
INSERT INTO dq_rule_type_def (CASE_TYPE, CASE_TYPE_DESC, INDEX_DESC1, INDEX_DESC2, INDEX_DESC3, REMARK) VALUES ('COL REGULAR', '字段正则表达式', '不在范围内的记录数', '检查总记录数', '', '检测目标表名的 目标表关键字段是否符合在清单值域 内字段正则表达式');
INSERT INTO dq_rule_type_def (CASE_TYPE, CASE_TYPE_DESC, INDEX_DESC1, INDEX_DESC2, INDEX_DESC3, REMARK) VALUES ('SQL', '指定sql', '自定义', '自定义', '', '检测指定SQL 的sql规则');
-- INSERT INTO dq_rule_type_def (CASE_TYPE, CASE_TYPE_DESC, INDEX_DESC1, INDEX_DESC2, INDEX_DESC3, REMARK) VALUES ('SQL FILE', '指定sql文件', '自定义', '自定义', '', '检测指定SQL 路径存在的规则 ');
INSERT INTO dq_rule_type_def (CASE_TYPE, CASE_TYPE_DESC, INDEX_DESC1, INDEX_DESC2, INDEX_DESC3, REMARK) VALUES ('TAB NAN', '表非空', '', '', '', '检测目标表名是否非空');
-- INSERT INTO dq_rule_type_def (CASE_TYPE, CASE_TYPE_DESC, INDEX_DESC1, INDEX_DESC2, INDEX_DESC3, REMARK) VALUES ('TAB SIZE', '表数据量增量', '当日增量记录数x', '上日增量记录数y', '(x-y)/y*100', '');
