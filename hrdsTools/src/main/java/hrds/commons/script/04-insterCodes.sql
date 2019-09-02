--　系统的引用值个数:145
--　本引用值的代码个数:27 -------i==1
delete from code_info where ci_sp_class='1';
INSERT INTO CODE_INFO  VALUES ('00', '1', '用户类型', '系统管理员', 'UserType');
INSERT INTO CODE_INFO  VALUES ('01', '1', '用户类型', '采集管理', 'UserType');
INSERT INTO CODE_INFO  VALUES ('02', '1', '用户类型', '数据采集', 'UserType');
INSERT INTO CODE_INFO  VALUES ('03', '1', '用户类型', '数据查询', 'UserType');
INSERT INTO CODE_INFO  VALUES ('04', '1', '用户类型', '作业调度', 'UserType');
INSERT INTO CODE_INFO  VALUES ('05', '1', '用户类型', '作业操作员', 'UserType');
INSERT INTO CODE_INFO  VALUES ('06', '1', '用户类型', '数据可视化管理', 'UserType');
INSERT INTO CODE_INFO  VALUES ('07', '1', '用户类型', '可视化数据源', 'UserType');
INSERT INTO CODE_INFO  VALUES ('08', '1', '用户类型', '数据可视化分析', 'UserType');
INSERT INTO CODE_INFO  VALUES ('09', '1', '用户类型', '数据可视化查看', 'UserType');
INSERT INTO CODE_INFO  VALUES ('10', '1', '用户类型', '监控管理', 'UserType');
INSERT INTO CODE_INFO  VALUES ('11', '1', '用户类型', '服务接口管理', 'UserType');
INSERT INTO CODE_INFO  VALUES ('12', '1', '用户类型', '服务接口用户', 'UserType');
INSERT INTO CODE_INFO  VALUES ('13', '1', '用户类型', '分词器管理', 'UserType');
INSERT INTO CODE_INFO  VALUES ('14', '1', '用户类型', '数据集市', 'UserType');
INSERT INTO CODE_INFO  VALUES ('15', '1', '用户类型', '数据加工', 'UserType');
INSERT INTO CODE_INFO  VALUES ('16', '1', '用户类型', '机器学习工作台', 'UserType');
INSERT INTO CODE_INFO  VALUES ('17', '1', '用户类型', '机器学习业务', 'UserType');
INSERT INTO CODE_INFO  VALUES ('18', '1', '用户类型', '流数据管理', 'UserType');
INSERT INTO CODE_INFO  VALUES ('19', '1', '用户类型', '流数据生产', 'UserType');
INSERT INTO CODE_INFO  VALUES ('20', '1', '用户类型', '数据库配置(永洪)', 'UserType');
INSERT INTO CODE_INFO  VALUES ('21', '1', '用户类型', '报表创建(永洪)', 'UserType');
INSERT INTO CODE_INFO  VALUES ('22', '1', '用户类型', '报表查看(永洪)', 'UserType');
INSERT INTO CODE_INFO  VALUES ('23', '1', '用户类型', '流数据消费', 'UserType');
INSERT INTO CODE_INFO  VALUES ('24', '1', '用户类型', '数据管控', 'UserType');
INSERT INTO CODE_INFO  VALUES ('25', '1', '用户类型', '自主分析管理', 'UserType');
INSERT INTO CODE_INFO  VALUES ('26', '1', '用户类型', '自主分析操作', 'UserType');
--　本引用值的代码个数:2 -------i==2
delete from code_info where ci_sp_class='2';
INSERT INTO CODE_INFO  VALUES ('0', '2', '是否标识', '是', 'IsFlag');
INSERT INTO CODE_INFO  VALUES ('1', '2', '是否标识', '否', 'IsFlag');
--　本引用值的代码个数:4 -------i==3
delete from code_info where ci_sp_class='3';
INSERT INTO CODE_INFO  VALUES ('1', '3', '用户状态', '正常', 'UserState');
INSERT INTO CODE_INFO  VALUES ('2', '3', '用户状态', '禁用', 'UserState');
INSERT INTO CODE_INFO  VALUES ('3', '3', '用户状态', '删除', 'UserState');
INSERT INTO CODE_INFO  VALUES ('4', '3', '用户状态', '正在使用', 'UserState');
--　本引用值的代码个数:3 -------i==4
delete from code_info where ci_sp_class='4';
INSERT INTO CODE_INFO  VALUES ('1', '4', 'Agent状态', '已连接', 'AgentStatus');
INSERT INTO CODE_INFO  VALUES ('2', '4', 'Agent状态', '未连接', 'AgentStatus');
INSERT INTO CODE_INFO  VALUES ('3', '4', 'Agent状态', '正在运行', 'AgentStatus');
--　本引用值的代码个数:5 -------i==5
delete from code_info where ci_sp_class='5';
INSERT INTO CODE_INFO  VALUES ('1', '5', 'Agent类别', '数据库Agent', 'AgentType');
INSERT INTO CODE_INFO  VALUES ('2', '5', 'Agent类别', '文件系统Agent', 'AgentType');
INSERT INTO CODE_INFO  VALUES ('3', '5', 'Agent类别', 'FtpAgent', 'AgentType');
INSERT INTO CODE_INFO  VALUES ('4', '5', 'Agent类别', '数据文件Agent', 'AgentType');
INSERT INTO CODE_INFO  VALUES ('5', '5', 'Agent类别', '对象Agent', 'AgentType');
--　本引用值的代码个数:13 -------i==6
delete from code_info where ci_sp_class='6';
INSERT INTO CODE_INFO  VALUES ('01', '6', '数据库类型', 'MYSQL', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('02', '6', '数据库类型', 'Oracle9i及一下', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('03', '6', '数据库类型', 'Oracle10g及以上', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('04', '6', '数据库类型', 'SQLSERVER2000', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('05', '6', '数据库类型', 'SQLSERVER2005', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('06', '6', '数据库类型', 'DB2', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('07', '6', '数据库类型', 'SybaseASE12.5及以上', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('08', '6', '数据库类型', 'Informatic', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('09', '6', '数据库类型', 'H2', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('10', '6', '数据库类型', 'ApacheDerby', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('11', '6', '数据库类型', 'Postgresql', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('12', '6', '数据库类型', 'GBase', 'DatabaseType');
INSERT INTO CODE_INFO  VALUES ('13', '6', '数据库类型', 'TeraData', 'DatabaseType');
--　本引用值的代码个数:5 -------i==7
delete from code_info where ci_sp_class='7';
INSERT INTO CODE_INFO  VALUES ('0', '7', '频率', '自定义', 'Frequency');
INSERT INTO CODE_INFO  VALUES ('1', '7', '频率', '天', 'Frequency');
INSERT INTO CODE_INFO  VALUES ('2', '7', '频率', '周', 'Frequency');
INSERT INTO CODE_INFO  VALUES ('3', '7', '频率', '月', 'Frequency');
INSERT INTO CODE_INFO  VALUES ('4', '7', '频率', '季度', 'Frequency');
--　本引用值的代码个数:3 -------i==8
delete from code_info where ci_sp_class='8';
INSERT INTO CODE_INFO  VALUES ('1', '8', '启动方式', '按时启动', 'ExecuteWay');
INSERT INTO CODE_INFO  VALUES ('2', '8', '启动方式', '命令触发', 'ExecuteWay');
INSERT INTO CODE_INFO  VALUES ('3', '8', '启动方式', '信号文件触发', 'ExecuteWay');
--　本引用值的代码个数:2 -------i==9
delete from code_info where ci_sp_class='9';
INSERT INTO CODE_INFO  VALUES ('1', '9', '压缩范围', '全库压缩', 'ReduceScope');
INSERT INTO CODE_INFO  VALUES ('2', '9', '压缩范围', '按表压缩', 'ReduceScope');
--　本引用值的代码个数:6 -------i==10
delete from code_info where ci_sp_class='10';
INSERT INTO CODE_INFO  VALUES ('01', '10', '运行状态', '开始运行', 'ExecuteState');
INSERT INTO CODE_INFO  VALUES ('02', '10', '运行状态', '运行完成', 'ExecuteState');
INSERT INTO CODE_INFO  VALUES ('99', '10', '运行状态', '运行失败', 'ExecuteState');
INSERT INTO CODE_INFO  VALUES ('20', '10', '运行状态', '通知成功', 'ExecuteState');
INSERT INTO CODE_INFO  VALUES ('21', '10', '运行状态', '通知失败', 'ExecuteState');
INSERT INTO CODE_INFO  VALUES ('30', '10', '运行状态', '暂停运行', 'ExecuteState');
--　本引用值的代码个数:2 -------i==11
delete from code_info where ci_sp_class='11';
INSERT INTO CODE_INFO  VALUES ('1', '11', '组件状态', '启用', 'CompState');
INSERT INTO CODE_INFO  VALUES ('2', '11', '组件状态', '禁用', 'CompState');
--　本引用值的代码个数:6 -------i==12
delete from code_info where ci_sp_class='12';
INSERT INTO CODE_INFO  VALUES ('01', '12', '作业程序类型', 'c', 'ProType');
INSERT INTO CODE_INFO  VALUES ('02', '12', '作业程序类型', 'c++', 'ProType');
INSERT INTO CODE_INFO  VALUES ('03', '12', '作业程序类型', 'JAVA', 'ProType');
INSERT INTO CODE_INFO  VALUES ('04', '12', '作业程序类型', 'C#', 'ProType');
INSERT INTO CODE_INFO  VALUES ('05', '12', '作业程序类型', 'shell', 'ProType');
INSERT INTO CODE_INFO  VALUES ('06', '12', '作业程序类型', 'perl', 'ProType');
--　本引用值的代码个数:5 -------i==13
delete from code_info where ci_sp_class='13';
INSERT INTO CODE_INFO  VALUES ('1', '13', '采集类型', '数据库采集', 'CollectType');
INSERT INTO CODE_INFO  VALUES ('2', '13', '采集类型', '文件采集', 'CollectType');
INSERT INTO CODE_INFO  VALUES ('3', '13', '采集类型', '数据文件采集', 'CollectType');
INSERT INTO CODE_INFO  VALUES ('4', '13', '采集类型', '对象文件采集', 'CollectType');
INSERT INTO CODE_INFO  VALUES ('5', '13', '采集类型', 'Ftp采集', 'CollectType');
--　本引用值的代码个数:3 -------i==14
delete from code_info where ci_sp_class='14';
INSERT INTO CODE_INFO  VALUES ('1', '14', '用户优先级', '高', 'UserPriority');
INSERT INTO CODE_INFO  VALUES ('2', '14', '用户优先级', '中', 'UserPriority');
INSERT INTO CODE_INFO  VALUES ('3', '14', '用户优先级', '低', 'UserPriority');
--　本引用值的代码个数:3 -------i==15
delete from code_info where ci_sp_class='15';
INSERT INTO CODE_INFO  VALUES ('1', '15', '通知等级', '所有', 'NoticeLevel');
INSERT INTO CODE_INFO  VALUES ('2', '15', '通知等级', '失败', 'NoticeLevel');
INSERT INTO CODE_INFO  VALUES ('3', '15', '通知等级', '成功', 'NoticeLevel');
--　本引用值的代码个数:3 -------i==16
delete from code_info where ci_sp_class='16';
INSERT INTO CODE_INFO  VALUES ('1', '16', '日志等级', '所有', 'LogLevel');
INSERT INTO CODE_INFO  VALUES ('2', '16', '日志等级', '成功', 'LogLevel');
INSERT INTO CODE_INFO  VALUES ('3', '16', '日志等级', '失败', 'LogLevel');
--　本引用值的代码个数:2 -------i==17
delete from code_info where ci_sp_class='17';
INSERT INTO CODE_INFO  VALUES ('1', '17', '串行并行', '串行', 'IsSerial');
INSERT INTO CODE_INFO  VALUES ('2', '17', '串行并行', '并行', 'IsSerial');
--　本引用值的代码个数:4 -------i==18
delete from code_info where ci_sp_class='18';
INSERT INTO CODE_INFO  VALUES ('1', '18', '数据申请类型', '查看', 'ApplyType');
INSERT INTO CODE_INFO  VALUES ('2', '18', '数据申请类型', '下载', 'ApplyType');
INSERT INTO CODE_INFO  VALUES ('3', '18', '数据申请类型', '发布', 'ApplyType');
INSERT INTO CODE_INFO  VALUES ('4', '18', '数据申请类型', '重命名', 'ApplyType');
--　本引用值的代码个数:4 -------i==19
delete from code_info where ci_sp_class='19';
INSERT INTO CODE_INFO  VALUES ('1', '19', '权限类型', '允许', 'AuthType');
INSERT INTO CODE_INFO  VALUES ('2', '19', '权限类型', '不允许', 'AuthType');
INSERT INTO CODE_INFO  VALUES ('3', '19', '权限类型', '一次', 'AuthType');
INSERT INTO CODE_INFO  VALUES ('0', '19', '权限类型', '申请', 'AuthType');
--　本引用值的代码个数:3 -------i==20
delete from code_info where ci_sp_class='20';
INSERT INTO CODE_INFO  VALUES ('1', '20', '停止抓取条件', '按网页数量停止抓取', 'StopConditions');
INSERT INTO CODE_INFO  VALUES ('2', '20', '停止抓取条件', '按下载量停止抓取', 'StopConditions');
INSERT INTO CODE_INFO  VALUES ('3', '20', '停止抓取条件', '按时间停止抓取', 'StopConditions');
--　本引用值的代码个数:2 -------i==21
delete from code_info where ci_sp_class='21';
INSERT INTO CODE_INFO  VALUES ('1', '21', '组件类型', '系统内置组件', 'CompType');
INSERT INTO CODE_INFO  VALUES ('2', '21', '组件类型', '系统运行组件', 'CompType');
--　本引用值的代码个数:12 -------i==22
delete from code_info where ci_sp_class='22';
INSERT INTO CODE_INFO  VALUES ('1001', '22', '资源管理器文件类型', '全部文件', 'FileType');
INSERT INTO CODE_INFO  VALUES ('1002', '22', '资源管理器文件类型', '图片', 'FileType');
INSERT INTO CODE_INFO  VALUES ('1003', '22', '资源管理器文件类型', '文档', 'FileType');
INSERT INTO CODE_INFO  VALUES ('1013', '22', '资源管理器文件类型', 'PDF文件', 'FileType');
INSERT INTO CODE_INFO  VALUES ('1023', '22', '资源管理器文件类型', 'office文件', 'FileType');
INSERT INTO CODE_INFO  VALUES ('1033', '22', '资源管理器文件类型', '文本文件', 'FileType');
INSERT INTO CODE_INFO  VALUES ('1043', '22', '资源管理器文件类型', '压缩文件', 'FileType');
INSERT INTO CODE_INFO  VALUES ('1053', '22', '资源管理器文件类型', '日志文件', 'FileType');
INSERT INTO CODE_INFO  VALUES ('1063', '22', '资源管理器文件类型', '表数据文件', 'FileType');
INSERT INTO CODE_INFO  VALUES ('1004', '22', '资源管理器文件类型', '视频', 'FileType');
INSERT INTO CODE_INFO  VALUES ('1005', '22', '资源管理器文件类型', '音频', 'FileType');
INSERT INTO CODE_INFO  VALUES ('1006', '22', '资源管理器文件类型', '其它', 'FileType');
--　本引用值的代码个数:2 -------i==23
delete from code_info where ci_sp_class='23';
INSERT INTO CODE_INFO  VALUES ('1', '23', '清洗参数属性类型', '特殊字段转义', 'CcParameterType');
INSERT INTO CODE_INFO  VALUES ('2', '23', '清洗参数属性类型', '特殊字段去除', 'CcParameterType');
--　本引用值的代码个数:2 -------i==24
delete from code_info where ci_sp_class='24';
INSERT INTO CODE_INFO  VALUES ('0', '24', '需提取数据项类别', '单项数据', 'NeedType');
INSERT INTO CODE_INFO  VALUES ('1', '24', '需提取数据项类别', '列表项数据', 'NeedType');
--　本引用值的代码个数:3 -------i==25
delete from code_info where ci_sp_class='25';
INSERT INTO CODE_INFO  VALUES ('1', '25', '储存方式', '增量', 'StorageType');
INSERT INTO CODE_INFO  VALUES ('2', '25', '储存方式', '追加', 'StorageType');
INSERT INTO CODE_INFO  VALUES ('3', '25', '储存方式', '替换', 'StorageType');
--　本引用值的代码个数:4 -------i==26
delete from code_info where ci_sp_class='30';
INSERT INTO CODE_INFO  VALUES ('1', '30', '接口类型', '数据类', 'InterfaceType');
INSERT INTO CODE_INFO  VALUES ('2', '30', '接口类型', '功能类', 'InterfaceType');
INSERT INTO CODE_INFO  VALUES ('3', '30', '接口类型', '报表类', 'InterfaceType');
INSERT INTO CODE_INFO  VALUES ('4', '30', '接口类型', '监控类', 'InterfaceType');
--　本引用值的代码个数:2 -------i==27
delete from code_info where ci_sp_class='31';
INSERT INTO CODE_INFO  VALUES ('1', '31', '接口状态', '启用', 'InterfaceState');
INSERT INTO CODE_INFO  VALUES ('2', '31', '接口状态', '禁用', 'InterfaceState');
--　本引用值的代码个数:2 -------i==28
delete from code_info where ci_sp_class='33';
INSERT INTO CODE_INFO  VALUES ('1', '33', '补齐方式', '前补齐', 'FillingType');
INSERT INTO CODE_INFO  VALUES ('2', '33', '补齐方式', '后补齐', 'FillingType');
--　本引用值的代码个数:4 -------i==29
delete from code_info where ci_sp_class='34';
INSERT INTO CODE_INFO  VALUES ('0', '34', '上下左右', '上', 'Orientation');
INSERT INTO CODE_INFO  VALUES ('1', '34', '上下左右', '下', 'Orientation');
INSERT INTO CODE_INFO  VALUES ('2', '34', '上下左右', '左', 'Orientation');
INSERT INTO CODE_INFO  VALUES ('3', '34', '上下左右', '右', 'Orientation');
--　本引用值的代码个数:5 -------i==30
delete from code_info where ci_sp_class='35';
INSERT INTO CODE_INFO  VALUES ('1', '35', '采集编码', 'UTF-8', 'DataBaseCode');
INSERT INTO CODE_INFO  VALUES ('2', '35', '采集编码', 'GBK', 'DataBaseCode');
INSERT INTO CODE_INFO  VALUES ('3', '35', '采集编码', 'UTF-16', 'DataBaseCode');
INSERT INTO CODE_INFO  VALUES ('4', '35', '采集编码', 'GB2312', 'DataBaseCode');
INSERT INTO CODE_INFO  VALUES ('5', '35', '采集编码', 'ISO-8859-1', 'DataBaseCode');
--　本引用值的代码个数:7 -------i==31
delete from code_info where ci_sp_class='36';
INSERT INTO CODE_INFO  VALUES ('1', '36', '清洗方式', '字符补齐', 'CleanType');
INSERT INTO CODE_INFO  VALUES ('2', '36', '清洗方式', '字符替换', 'CleanType');
INSERT INTO CODE_INFO  VALUES ('3', '36', '清洗方式', '时间转换', 'CleanType');
INSERT INTO CODE_INFO  VALUES ('4', '36', '清洗方式', '码值转换', 'CleanType');
INSERT INTO CODE_INFO  VALUES ('5', '36', '清洗方式', '字符合并', 'CleanType');
INSERT INTO CODE_INFO  VALUES ('6', '36', '清洗方式', '字符拆分', 'CleanType');
INSERT INTO CODE_INFO  VALUES ('7', '36', '清洗方式', '字符trim', 'CleanType');
--　本引用值的代码个数:6 -------i==32
delete from code_info where ci_sp_class='37';
INSERT INTO CODE_INFO  VALUES ('10000', '37', '记录总数', '1万左右', 'CountNum');
INSERT INTO CODE_INFO  VALUES ('100000', '37', '记录总数', '10万左右', 'CountNum');
INSERT INTO CODE_INFO  VALUES ('1000000', '37', '记录总数', '100万左右', 'CountNum');
INSERT INTO CODE_INFO  VALUES ('10000000', '37', '记录总数', '1000万左右', 'CountNum');
INSERT INTO CODE_INFO  VALUES ('100000000', '37', '记录总数', '亿左右', 'CountNum');
INSERT INTO CODE_INFO  VALUES ('100000001', '37', '记录总数', '亿以上', 'CountNum');
--　本引用值的代码个数:2 -------i==33
delete from code_info where ci_sp_class='38';
INSERT INTO CODE_INFO  VALUES ('1', '38', '数据表生命周期', '永久', 'TableLifeCycle');
INSERT INTO CODE_INFO  VALUES ('2', '38', '数据表生命周期', '临时', 'TableLifeCycle');
--　本引用值的代码个数:2 -------i==34
delete from code_info where ci_sp_class='39';
INSERT INTO CODE_INFO  VALUES ('0', '39', '数据表存储方式', '数据表', 'TableStorage');
INSERT INTO CODE_INFO  VALUES ('1', '39', '数据表存储方式', '数据视图', 'TableStorage');
--　本引用值的代码个数:3 -------i==35
delete from code_info where ci_sp_class='40';
INSERT INTO CODE_INFO  VALUES ('0', '40', '文件夹监听类别', '增加', 'DirWatch');
INSERT INTO CODE_INFO  VALUES ('1', '40', '文件夹监听类别', '删除', 'DirWatch');
INSERT INTO CODE_INFO  VALUES ('2', '40', '文件夹监听类别', '更新', 'DirWatch');
--　本引用值的代码个数:6 -------i==36
delete from code_info where ci_sp_class='42';
INSERT INTO CODE_INFO  VALUES ('100', '42', '作业运行状态', '等待', 'JobExecuteState');
INSERT INTO CODE_INFO  VALUES ('101', '42', '作业运行状态', '运行', 'JobExecuteState');
INSERT INTO CODE_INFO  VALUES ('102', '42', '作业运行状态', '暂停', 'JobExecuteState');
INSERT INTO CODE_INFO  VALUES ('103', '42', '作业运行状态', '中止', 'JobExecuteState');
INSERT INTO CODE_INFO  VALUES ('104', '42', '作业运行状态', '完成', 'JobExecuteState');
INSERT INTO CODE_INFO  VALUES ('105', '42', '作业运行状态', '失败', 'JobExecuteState');
--　本引用值的代码个数:14 -------i==37
delete from code_info where ci_sp_class='43';
INSERT INTO CODE_INFO  VALUES ('6000', '43', '作业操作类型', '等待', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('6001', '43', '作业操作类型', '工程运行', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('6002', '43', '作业操作类型', '工程中止', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('6003', '43', '作业操作类型', '工程暂停', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('6004', '43', '作业操作类型', '工程重跑', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('6005', '43', '作业操作类型', '工程完成', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('7001', '43', '作业操作类型', '任务运行', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('7002', '43', '作业操作类型', '任务中止', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('7003', '43', '作业操作类型', '任务暂停', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('7004', '43', '作业操作类型', '任务重跑', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('7005', '43', '作业操作类型', '任务完成', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('8001', '43', '作业操作类型', '作业中止', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('8002', '43', '作业操作类型', '作业重跑', 'ProjectLevel');
INSERT INTO CODE_INFO  VALUES ('8003', '43', '作业操作类型', '作业跳过', 'ProjectLevel');
--　本引用值的代码个数:8 -------i==38
delete from code_info where ci_sp_class='45';
INSERT INTO CODE_INFO  VALUES ('ISL', '45', '数据源类型', '贴源层_01', 'DataSourceType');
INSERT INTO CODE_INFO  VALUES ('DCL', '45', '数据源类型', '贴源层', 'DataSourceType');
INSERT INTO CODE_INFO  VALUES ('DPL', '45', '数据源类型', '加工层', 'DataSourceType');
INSERT INTO CODE_INFO  VALUES ('DML', '45', '数据源类型', '集市层', 'DataSourceType');
INSERT INTO CODE_INFO  VALUES ('SFL', '45', '数据源类型', '系统层', 'DataSourceType');
INSERT INTO CODE_INFO  VALUES ('AML', '45', '数据源类型', 'AI模型层', 'DataSourceType');
INSERT INTO CODE_INFO  VALUES ('DQC', '45', '数据源类型', '管控层', 'DataSourceType');
INSERT INTO CODE_INFO  VALUES ('UDL', '45', '数据源类型', '自定义层', 'DataSourceType');
--　本引用值的代码个数:3 -------i==39
delete from code_info where ci_sp_class='46';
INSERT INTO CODE_INFO  VALUES ('1', '46', 'sql执行引擎', 'JDBC', 'SqlEngine');
INSERT INTO CODE_INFO  VALUES ('2', '46', 'sql执行引擎', 'SPARK', 'SqlEngine');
INSERT INTO CODE_INFO  VALUES ('3', '46', 'sql执行引擎', '默认', 'SqlEngine');
--　本引用值的代码个数:3 -------i==40
delete from code_info where ci_sp_class='47';
INSERT INTO CODE_INFO  VALUES ('0', '47', 'DB文件格式', '定长', 'FileFormat');
INSERT INTO CODE_INFO  VALUES ('1', '47', 'DB文件格式', '非定长', 'FileFormat');
INSERT INTO CODE_INFO  VALUES ('2', '47', 'DB文件格式', 'CSV', 'FileFormat');
--　本引用值的代码个数:2 -------i==41
delete from code_info where ci_sp_class='49';
INSERT INTO CODE_INFO  VALUES ('1', '49', '初始化聚类中心方式', '打开数据集', 'InitializeClusterMode');
INSERT INTO CODE_INFO  VALUES ('2', '49', '初始化聚类中心方式', '导入外部数据', 'InitializeClusterMode');
--　本引用值的代码个数:3 -------i==42
delete from code_info where ci_sp_class='50';
INSERT INTO CODE_INFO  VALUES ('1', '50', '独立样本类型', '配对样本t检验', 'IndependentSampleStyle');
INSERT INTO CODE_INFO  VALUES ('2', '50', '独立样本类型', '等方差双样本检验', 'IndependentSampleStyle');
INSERT INTO CODE_INFO  VALUES ('3', '50', '独立样本类型', '异方差双样本检验', 'IndependentSampleStyle');
--　本引用值的代码个数:4 -------i==43
delete from code_info where ci_sp_class='51';
INSERT INTO CODE_INFO  VALUES ('0', '51', '发布状态', '未发布', 'PublishStatus');
INSERT INTO CODE_INFO  VALUES ('1', '51', '发布状态', '已发布', 'PublishStatus');
INSERT INTO CODE_INFO  VALUES ('2', '51', '发布状态', '已下线', 'PublishStatus');
INSERT INTO CODE_INFO  VALUES ('3', '51', '发布状态', '已删除', 'PublishStatus');
--　本引用值的代码个数:2 -------i==44
delete from code_info where ci_sp_class='52';
INSERT INTO CODE_INFO  VALUES ('1', '52', '分类变量编码类型', '一位有效编码', 'EncodeTypeClassificationVariables');
INSERT INTO CODE_INFO  VALUES ('2', '52', '分类变量编码类型', '标签编码', 'EncodeTypeClassificationVariables');
--　本引用值的代码个数:9 -------i==45
delete from code_info where ci_sp_class='53';
INSERT INTO CODE_INFO  VALUES ('01', '53', '分类模型类型', '支持向量机-SVC', 'ClassificatModelType');
INSERT INTO CODE_INFO  VALUES ('02', '53', '分类模型类型', '神经网络-前馈', 'ClassificatModelType');
INSERT INTO CODE_INFO  VALUES ('03', '53', '分类模型类型', '逻辑回归', 'ClassificatModelType');
INSERT INTO CODE_INFO  VALUES ('04', '53', '分类模型类型', '朴素贝叶斯', 'ClassificatModelType');
INSERT INTO CODE_INFO  VALUES ('05', '53', '分类模型类型', '集成投票分类器', 'ClassificatModelType');
INSERT INTO CODE_INFO  VALUES ('06', '53', '分类模型类型', '堆叠分类器', 'ClassificatModelType');
INSERT INTO CODE_INFO  VALUES ('07', '53', '分类模型类型', '神经网络', 'ClassificatModelType');
INSERT INTO CODE_INFO  VALUES ('08', '53', '分类模型类型', '随机森林分类', 'ClassificatModelType');
INSERT INTO CODE_INFO  VALUES ('09', '53', '分类模型类型', '分类决策树', 'ClassificatModelType');
--　本引用值的代码个数:3 -------i==46
delete from code_info where ci_sp_class='54';
INSERT INTO CODE_INFO  VALUES ('1', '54', '估计回归方法', '岭回归', 'RegressionMethodEstimation');
INSERT INTO CODE_INFO  VALUES ('2', '54', '估计回归方法', 'Lasso回归', 'RegressionMethodEstimation');
INSERT INTO CODE_INFO  VALUES ('3', '54', '估计回归方法', '包含截距项', 'RegressionMethodEstimation');
--　本引用值的代码个数:2 -------i==47
delete from code_info where ci_sp_class='55';
INSERT INTO CODE_INFO  VALUES ('1', '55', '归一化范围', '0至1范围', 'NormalizedRange');
INSERT INTO CODE_INFO  VALUES ('2', '55', '归一化范围', '-1至1范围', 'NormalizedRange');
--　本引用值的代码个数:4 -------i==48
delete from code_info where ci_sp_class='56';
INSERT INTO CODE_INFO  VALUES ('0', '56', '机器学习数据运行状态', '未开始', 'DataTableRunState');
INSERT INTO CODE_INFO  VALUES ('1', '56', '机器学习数据运行状态', '组建中', 'DataTableRunState');
INSERT INTO CODE_INFO  VALUES ('2', '56', '机器学习数据运行状态', '组建完成', 'DataTableRunState');
INSERT INTO CODE_INFO  VALUES ('3', '56', '机器学习数据运行状态', '组建失败', 'DataTableRunState');
--　本引用值的代码个数:9 -------i==49
delete from code_info where ci_sp_class='57';
INSERT INTO CODE_INFO  VALUES ('0', '57', '机器学习项目当前操作状态', '未操作', 'MLOperationStatus');
INSERT INTO CODE_INFO  VALUES ('1', '57', '机器学习项目当前操作状态', '数据源聚焦', 'MLOperationStatus');
INSERT INTO CODE_INFO  VALUES ('2', '57', '机器学习项目当前操作状态', '数据探索', 'MLOperationStatus');
INSERT INTO CODE_INFO  VALUES ('3', '57', '机器学习项目当前操作状态', '数据预处理', 'MLOperationStatus');
INSERT INTO CODE_INFO  VALUES ('4', '57', '机器学习项目当前操作状态', '假设检验', 'MLOperationStatus');
INSERT INTO CODE_INFO  VALUES ('5', '57', '机器学习项目当前操作状态', '特征加工', 'MLOperationStatus');
INSERT INTO CODE_INFO  VALUES ('6', '57', '机器学习项目当前操作状态', '数据拆分', 'MLOperationStatus');
INSERT INTO CODE_INFO  VALUES ('7', '57', '机器学习项目当前操作状态', '模型选择', 'MLOperationStatus');
INSERT INTO CODE_INFO  VALUES ('8', '57', '机器学习项目当前操作状态', '模型评估', 'MLOperationStatus');
--　本引用值的代码个数:2 -------i==50
delete from code_info where ci_sp_class='58';
INSERT INTO CODE_INFO  VALUES ('1', '58', '激活函数类型', '双曲正切', 'ActivateFunctionType');
INSERT INTO CODE_INFO  VALUES ('2', '58', '激活函数类型', 'sigmoid', 'ActivateFunctionType');
--　本引用值的代码个数:2 -------i==51
delete from code_info where ci_sp_class='59';
INSERT INTO CODE_INFO  VALUES ('1', '59', '降维分析方法', '主成分分析', 'DimensionReductionMethod');
INSERT INTO CODE_INFO  VALUES ('2', '59', '降维分析方法', '因子分析', 'DimensionReductionMethod');
--　本引用值的代码个数:4 -------i==52
delete from code_info where ci_sp_class='60';
INSERT INTO CODE_INFO  VALUES ('01', '60', '具体算法', '逻辑回归', 'SpecificAlgorithm');
INSERT INTO CODE_INFO  VALUES ('02', '60', '具体算法', '朴素贝叶斯', 'SpecificAlgorithm');
INSERT INTO CODE_INFO  VALUES ('03', '60', '具体算法', 'SVM', 'SpecificAlgorithm');
INSERT INTO CODE_INFO  VALUES ('04', '60', '具体算法', '线性回归', 'SpecificAlgorithm');
--　本引用值的代码个数:2 -------i==53
delete from code_info where ci_sp_class='61';
INSERT INTO CODE_INFO  VALUES ('1', '61', '聚类方法', '迭代与分类', 'ClusterMethod');
INSERT INTO CODE_INFO  VALUES ('2', '61', '聚类方法', '分类', 'ClusterMethod');
--　本引用值的代码个数:1 -------i==54
delete from code_info where ci_sp_class='62';
INSERT INTO CODE_INFO  VALUES ('01', '62', '聚类模型类型', 'K-means', 'ClusterModelType');
--　本引用值的代码个数:5 -------i==55
delete from code_info where ci_sp_class='63';
INSERT INTO CODE_INFO  VALUES ('1', '63', '模型核心类型', '线性核', 'ModelCoreType');
INSERT INTO CODE_INFO  VALUES ('2', '63', '模型核心类型', '多项式核', 'ModelCoreType');
INSERT INTO CODE_INFO  VALUES ('3', '63', '模型核心类型', '径向基核', 'ModelCoreType');
INSERT INTO CODE_INFO  VALUES ('4', '63', '模型核心类型', 'sigmoid', 'ModelCoreType');
INSERT INTO CODE_INFO  VALUES ('5', '63', '模型核心类型', '预计算', 'ModelCoreType');
--　本引用值的代码个数:2 -------i==56
delete from code_info where ci_sp_class='64';
INSERT INTO CODE_INFO  VALUES ('1', '64', '模型体系结构类型', '自动体系结构选择', 'ModelArchitectureType');
INSERT INTO CODE_INFO  VALUES ('2', '64', '模型体系结构类型', '自定义体系结构', 'ModelArchitectureType');
--　本引用值的代码个数:2 -------i==57
delete from code_info where ci_sp_class='65';
INSERT INTO CODE_INFO  VALUES ('1', '65', '模型选择方式', '专家建模', 'SelectModelMode');
INSERT INTO CODE_INFO  VALUES ('2', '65', '模型选择方式', 'ARIMA', 'SelectModelMode');
--　本引用值的代码个数:6 -------i==58
delete from code_info where ci_sp_class='66';
INSERT INTO CODE_INFO  VALUES ('1', '66', '缺失值处理方式', '均值', 'MissValueApproach');
INSERT INTO CODE_INFO  VALUES ('2', '66', '缺失值处理方式', '中位数', 'MissValueApproach');
INSERT INTO CODE_INFO  VALUES ('3', '66', '缺失值处理方式', '线性插值', 'MissValueApproach');
INSERT INTO CODE_INFO  VALUES ('4', '66', '缺失值处理方式', '点处的线性趋势', 'MissValueApproach');
INSERT INTO CODE_INFO  VALUES ('5', '66', '缺失值处理方式', '序列均值', 'MissValueApproach');
INSERT INTO CODE_INFO  VALUES ('6', '66', '缺失值处理方式', '删除', 'MissValueApproach');
--　本引用值的代码个数:2 -------i==59
delete from code_info where ci_sp_class='67';
INSERT INTO CODE_INFO  VALUES ('1', '67', '缺失值处理类型', '全部', 'MissValueProcessType');
INSERT INTO CODE_INFO  VALUES ('2', '67', '缺失值处理类型', 'K的个数', 'MissValueProcessType');
--　本引用值的代码个数:2 -------i==60
delete from code_info where ci_sp_class='68';
INSERT INTO CODE_INFO  VALUES ('1', '68', '数据拆分抽取类型', '简单随机抽取', 'DataSplitExtractionType');
INSERT INTO CODE_INFO  VALUES ('2', '68', '数据拆分抽取类型', '分层抽取', 'DataSplitExtractionType');
--　本引用值的代码个数:2 -------i==61
delete from code_info where ci_sp_class='69';
INSERT INTO CODE_INFO  VALUES ('1', '69', '数据加载方式', 'SQL执行', 'DataLoadMode');
INSERT INTO CODE_INFO  VALUES ('2', '69', '数据加载方式', '调用其它程序', 'DataLoadMode');
--　本引用值的代码个数:3 -------i==62
delete from code_info where ci_sp_class='70';
INSERT INTO CODE_INFO  VALUES ('1', '70', '数值变量重编码处理方式', '标准化', 'NumericalVariableRecodeMode');
INSERT INTO CODE_INFO  VALUES ('2', '70', '数值变量重编码处理方式', '归一化', 'NumericalVariableRecodeMode');
INSERT INTO CODE_INFO  VALUES ('3', '70', '数值变量重编码处理方式', '离散化', 'NumericalVariableRecodeMode');
--　本引用值的代码个数:2 -------i==63
delete from code_info where ci_sp_class='71';
INSERT INTO CODE_INFO  VALUES ('1', '71', '算法类型', '分类', 'AlgorithmType');
INSERT INTO CODE_INFO  VALUES ('2', '71', '算法类型', '回归', 'AlgorithmType');
--　本引用值的代码个数:4 -------i==64
delete from code_info where ci_sp_class='72';
INSERT INTO CODE_INFO  VALUES ('01', '72', '算法评判标准', '准确度', 'AlgorithmCriteria');
INSERT INTO CODE_INFO  VALUES ('02', '72', '算法评判标准', 'R^2', 'AlgorithmCriteria');
INSERT INTO CODE_INFO  VALUES ('03', '72', '算法评判标准', '平均根误差', 'AlgorithmCriteria');
INSERT INTO CODE_INFO  VALUES ('04', '72', '算法评判标准', '平均绝对误差', 'AlgorithmCriteria');
--　本引用值的代码个数:4 -------i==65
delete from code_info where ci_sp_class='74';
INSERT INTO CODE_INFO  VALUES ('1', '74', '异常值处理标识条件', '百分比', 'OutlierProcessCondition');
INSERT INTO CODE_INFO  VALUES ('2', '74', '异常值处理标识条件', '固定数量', 'OutlierProcessCondition');
INSERT INTO CODE_INFO  VALUES ('3', '74', '异常值处理标识条件', '分界值', 'OutlierProcessCondition');
INSERT INTO CODE_INFO  VALUES ('4', '74', '异常值处理标识条件', '标准差', 'OutlierProcessCondition');
--　本引用值的代码个数:2 -------i==66
delete from code_info where ci_sp_class='75';
INSERT INTO CODE_INFO  VALUES ('1', '75', '异常值处理处理方式', '删除', 'OutlierProcessMode');
INSERT INTO CODE_INFO  VALUES ('2', '75', '异常值处理处理方式', '替换', 'OutlierProcessMode');
--　本引用值的代码个数:3 -------i==67
delete from code_info where ci_sp_class='76';
INSERT INTO CODE_INFO  VALUES ('1', '76', '异常值处理处理类型', '平均值', 'OutlierProcessType');
INSERT INTO CODE_INFO  VALUES ('2', '76', '异常值处理处理类型', '中位值', 'OutlierProcessType');
INSERT INTO CODE_INFO  VALUES ('3', '76', '异常值处理处理类型', '自定义', 'OutlierProcessType');
--　本引用值的代码个数:2 -------i==68
delete from code_info where ci_sp_class='77';
INSERT INTO CODE_INFO  VALUES ('1', '77', '隐藏层单位类型', '自动计算', 'HideLayerUnitType');
INSERT INTO CODE_INFO  VALUES ('2', '77', '隐藏层单位类型', '设定', 'HideLayerUnitType');
--　本引用值的代码个数:2 -------i==69
delete from code_info where ci_sp_class='78';
INSERT INTO CODE_INFO  VALUES ('1', '78', '隐藏固定数', '一层', 'HideLayerFixNumber');
INSERT INTO CODE_INFO  VALUES ('2', '78', '隐藏固定数', '二层', 'HideLayerFixNumber');
--　本引用值的代码个数:6 -------i==70
delete from code_info where ci_sp_class='79';
INSERT INTO CODE_INFO  VALUES ('01', '79', '预测模型类型', '线性模型', 'PredictModelType');
INSERT INTO CODE_INFO  VALUES ('02', '79', '预测模型类型', '支持向量机-svr', 'PredictModelType');
INSERT INTO CODE_INFO  VALUES ('03', '79', '预测模型类型', '时间序列', 'PredictModelType');
INSERT INTO CODE_INFO  VALUES ('04', '79', '预测模型类型', '随机森林回归', 'PredictModelType');
INSERT INTO CODE_INFO  VALUES ('05', '79', '预测模型类型', '堆叠回归', 'PredictModelType');
INSERT INTO CODE_INFO  VALUES ('06', '79', '预测模型类型', '梯度增强回归树', 'PredictModelType');
--　本引用值的代码个数:2 -------i==71
delete from code_info where ci_sp_class='80';
INSERT INTO CODE_INFO  VALUES ('1', '80', '重编码变量类型', '分类变量', 'RecodeVariableType');
INSERT INTO CODE_INFO  VALUES ('2', '80', '重编码变量类型', '数值变量', 'RecodeVariableType');
--　本引用值的代码个数:2 -------i==72
delete from code_info where ci_sp_class='81';
INSERT INTO CODE_INFO  VALUES ('1', '81', '字段类型', '字符型', 'ColumnType');
INSERT INTO CODE_INFO  VALUES ('2', '81', '字段类型', '数值型', 'ColumnType');
--　本引用值的代码个数:2 -------i==73
delete from code_info where ci_sp_class='82';
INSERT INTO CODE_INFO  VALUES ('1', '82', '相关性系数类型', 'Pearson相关系数', 'CorrelationCoefficientType');
INSERT INTO CODE_INFO  VALUES ('2', '82', '相关性系数类型', 'Spearman相关系数', 'CorrelationCoefficientType');
--　本引用值的代码个数:27 -------i==74
delete from code_info where ci_sp_class='84';
INSERT INTO CODE_INFO  VALUES ('001', '84', '报表类型', '折叠树图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('002', '84', '报表类型', '关系图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('003', '84', '报表类型', '序列圆图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('004', '84', '报表类型', '桑基图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('005', '84', '报表类型', 'Hierarchical', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('006', '84', '报表类型', '气泡图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('007', '84', '报表类型', '气泡菜单图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('008', '84', '报表类型', '趋势图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('009', '84', '报表类型', '柱形图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('010', '84', '报表类型', '柱形价值图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('011', '84', '报表类型', '柱形频次图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('012', '84', '报表类型', '动态散点图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('013', '84', '报表类型', '饼图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('014', '84', '报表类型', '堆叠柱状图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('015', '84', '报表类型', '散点图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('016', '84', '报表类型', '雷达图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('017', '84', '报表类型', 'k线图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('018', '84', '报表类型', '漏斗图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('019', '84', '报表类型', '平行坐标图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('020', '84', '报表类型', '柱状图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('021', '84', '报表类型', 'Miserables', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('022', '84', '报表类型', '直方图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('023', '84', '报表类型', '气泡散点图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('024', '84', '报表类型', '正负条形图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('025', '84', '报表类型', '箱线图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('026', '84', '报表类型', '折线图', 'ReportType');
INSERT INTO CODE_INFO  VALUES ('027', '84', '报表类型', '统计柱状图', 'ReportType');
--　本引用值的代码个数:2 -------i==75
delete from code_info where ci_sp_class='85';
INSERT INTO CODE_INFO  VALUES ('1', '85', 't检验样本类型', '单样本T检验', 'THypothesisTestType');
INSERT INTO CODE_INFO  VALUES ('2', '85', 't检验样本类型', '双样本T检验', 'THypothesisTestType');
--　本引用值的代码个数:2 -------i==76
delete from code_info where ci_sp_class='86';
INSERT INTO CODE_INFO  VALUES ('01', '86', '报表来源', '报表组件', 'ReportSource');
INSERT INTO CODE_INFO  VALUES ('02', '86', '报表来源', '机器学习', 'ReportSource');
--　本引用值的代码个数:16 -------i==77
delete from code_info where ci_sp_class='90';
INSERT INTO CODE_INFO  VALUES ('011', '90', '机器学习数据转换方式', '对数运算', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('012', '90', '机器学习数据转换方式', '指数运算', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('013', '90', '机器学习数据转换方式', '自然对数运算', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('014', '90', '机器学习数据转换方式', '幂运算', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('015', '90', '机器学习数据转换方式', '平方根运算', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('021', '90', '机器学习数据转换方式', '转为数值型', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('022', '90', '机器学习数据转换方式', '转为字符型', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('031', '90', '机器学习数据转换方式', '日期比较', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('041', '90', '机器学习数据转换方式', '获取年', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('042', '90', '机器学习数据转换方式', '获取月', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('043', '90', '机器学习数据转换方式', '获取周', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('051', '90', '机器学习数据转换方式', '去左空格', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('052', '90', '机器学习数据转换方式', '去右空格', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('053', '90', '机器学习数据转换方式', '获取长度', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('054', '90', '机器学习数据转换方式', '转成小写', 'DataTransferMethod');
INSERT INTO CODE_INFO  VALUES ('055', '90', '机器学习数据转换方式', '转成大写', 'DataTransferMethod');
--　本引用值的代码个数:4 -------i==78
delete from code_info where ci_sp_class='91';
INSERT INTO CODE_INFO  VALUES ('1', '91', '模型类型', '分类模型', 'ModelType');
INSERT INTO CODE_INFO  VALUES ('2', '91', '模型类型', '回归模型', 'ModelType');
INSERT INTO CODE_INFO  VALUES ('3', '91', '模型类型', '预测模型', 'ModelType');
INSERT INTO CODE_INFO  VALUES ('4', '91', '模型类型', '其它算法', 'ModelType');
--　本引用值的代码个数:3 -------i==79
delete from code_info where ci_sp_class='92';
INSERT INTO CODE_INFO  VALUES ('1', '92', 'sql条件类型', 'where条件', 'SqlConditionType');
INSERT INTO CODE_INFO  VALUES ('2', '92', 'sql条件类型', '分组条件', 'SqlConditionType');
INSERT INTO CODE_INFO  VALUES ('3', '92', 'sql条件类型', '连接条件', 'SqlConditionType');
--　本引用值的代码个数:3 -------i==80
delete from code_info where ci_sp_class='93';
INSERT INTO CODE_INFO  VALUES ('1', '93', '报表条件过滤方式', '文本', 'ConditionalFilter');
INSERT INTO CODE_INFO  VALUES ('2', '93', '报表条件过滤方式', '单选下拉', 'ConditionalFilter');
INSERT INTO CODE_INFO  VALUES ('3', '93', '报表条件过滤方式', '多选下拉', 'ConditionalFilter');
--　本引用值的代码个数:5 -------i==81
delete from code_info where ci_sp_class='94';
INSERT INTO CODE_INFO  VALUES ('0', '94', '转换类型', '不需要转换', 'ConvertType');
INSERT INTO CODE_INFO  VALUES ('1', '94', '转换类型', '转换为日期', 'ConvertType');
INSERT INTO CODE_INFO  VALUES ('2', '94', '转换类型', '转换为时间', 'ConvertType');
INSERT INTO CODE_INFO  VALUES ('3', '94', '转换类型', '参数转换', 'ConvertType');
INSERT INTO CODE_INFO  VALUES ('4', '94', '转换类型', '格式转换', 'ConvertType');
--　本引用值的代码个数:5 -------i==82
delete from code_info where ci_sp_class='95';
INSERT INTO CODE_INFO  VALUES ('F1', '95', '加工层算法类型', 'F1', 'MachiningAlgType');
INSERT INTO CODE_INFO  VALUES ('F2', '95', '加工层算法类型', 'F2', 'MachiningAlgType');
INSERT INTO CODE_INFO  VALUES ('F3', '95', '加工层算法类型', 'F3', 'MachiningAlgType');
INSERT INTO CODE_INFO  VALUES ('F5', '95', '加工层算法类型', 'F5', 'MachiningAlgType');
INSERT INTO CODE_INFO  VALUES ('I', '95', '加工层算法类型', 'I', 'MachiningAlgType');
--　本引用值的代码个数:4 -------i==83
delete from code_info where ci_sp_class='96';
INSERT INTO CODE_INFO  VALUES ('1', '96', '加工层字段处理方式', '映射', 'MachiningDeal');
INSERT INTO CODE_INFO  VALUES ('2', '96', '加工层字段处理方式', '规则mapping', 'MachiningDeal');
INSERT INTO CODE_INFO  VALUES ('3', '96', '加工层字段处理方式', '去空', 'MachiningDeal');
INSERT INTO CODE_INFO  VALUES ('4', '96', '加工层字段处理方式', '映射并添加', 'MachiningDeal');
--　本引用值的代码个数:2 -------i==84
delete from code_info where ci_sp_class='98';
INSERT INTO CODE_INFO  VALUES ('1', '98', '流数据管理Agent类别', '文本流Agent', 'SdmAgentType');
INSERT INTO CODE_INFO  VALUES ('2', '98', '流数据管理Agent类别', 'Rest接收Agent', 'SdmAgentType');
--　本引用值的代码个数:3 -------i==85
delete from code_info where ci_sp_class='99';
INSERT INTO CODE_INFO  VALUES ('1', '99', '流数据管理分区方式', '随机分布', 'SdmPatitionWay');
INSERT INTO CODE_INFO  VALUES ('2', '99', '流数据管理分区方式', 'key', 'SdmPatitionWay');
INSERT INTO CODE_INFO  VALUES ('3', '99', '流数据管理分区方式', '分区', 'SdmPatitionWay');
--　本引用值的代码个数:4 -------i==86
delete from code_info where ci_sp_class='100';
INSERT INTO CODE_INFO  VALUES ('1', '100', '流数据管理变量类型', '整数', 'SdmVariableType');
INSERT INTO CODE_INFO  VALUES ('2', '100', '流数据管理变量类型', '字符串', 'SdmVariableType');
INSERT INTO CODE_INFO  VALUES ('3', '100', '流数据管理变量类型', '浮点数', 'SdmVariableType');
INSERT INTO CODE_INFO  VALUES ('4', '100', '流数据管理变量类型', '字节数组', 'SdmVariableType');
--　本引用值的代码个数:2 -------i==87
delete from code_info where ci_sp_class='101';
INSERT INTO CODE_INFO  VALUES ('1', '101', '报表发布状态', '未发布', 'RPublishStatus');
INSERT INTO CODE_INFO  VALUES ('2', '101', '报表发布状态', '已发布', 'RPublishStatus');
--　本引用值的代码个数:2 -------i==88
delete from code_info where ci_sp_class='102';
INSERT INTO CODE_INFO  VALUES ('1', '102', '节点不纯度衡量方法', 'GINI', 'NodeImpMeaMet');
INSERT INTO CODE_INFO  VALUES ('2', '102', '节点不纯度衡量方法', '熵', 'NodeImpMeaMet');
--　本引用值的代码个数:2 -------i==89
delete from code_info where ci_sp_class='103';
INSERT INTO CODE_INFO  VALUES ('1', '103', '机器学习朴素贝叶斯类型', '多项式', 'NBModelType');
INSERT INTO CODE_INFO  VALUES ('2', '103', '机器学习朴素贝叶斯类型', '伯努利', 'NBModelType');
--　本引用值的代码个数:7 -------i==90
delete from code_info where ci_sp_class='104';
INSERT INTO CODE_INFO  VALUES ('1', '104', '流数据管理消费端目的地', '数据库', 'SdmConsumeDestination');
INSERT INTO CODE_INFO  VALUES ('2', '104', '流数据管理消费端目的地', 'hbase', 'SdmConsumeDestination');
INSERT INTO CODE_INFO  VALUES ('3', '104', '流数据管理消费端目的地', 'rest服务', 'SdmConsumeDestination');
INSERT INTO CODE_INFO  VALUES ('4', '104', '流数据管理消费端目的地', '文件', 'SdmConsumeDestination');
INSERT INTO CODE_INFO  VALUES ('5', '104', '流数据管理消费端目的地', '二进制文件', 'SdmConsumeDestination');
INSERT INTO CODE_INFO  VALUES ('6', '104', '流数据管理消费端目的地', 'Kafka', 'SdmConsumeDestination');
INSERT INTO CODE_INFO  VALUES ('7', '104', '流数据管理消费端目的地', '自定义业务类', 'SdmConsumeDestination');
--　本引用值的代码个数:2 -------i==91
delete from code_info where ci_sp_class='105';
INSERT INTO CODE_INFO  VALUES ('1', '105', '流数据管理线程与分区的关系', '一对一', 'SdmThreadPartition');
INSERT INTO CODE_INFO  VALUES ('2', '105', '流数据管理线程与分区的关系', '一对多', 'SdmThreadPartition');
--　本引用值的代码个数:9 -------i==92
delete from code_info where ci_sp_class='106';
INSERT INTO CODE_INFO  VALUES ('1', '106', '机器学习图表统计量类型', '均值', 'ChartStatisticType');
INSERT INTO CODE_INFO  VALUES ('2', '106', '机器学习图表统计量类型', '中位数', 'ChartStatisticType');
INSERT INTO CODE_INFO  VALUES ('3', '106', '机器学习图表统计量类型', '众数', 'ChartStatisticType');
INSERT INTO CODE_INFO  VALUES ('4', '106', '机器学习图表统计量类型', '计数', 'ChartStatisticType');
INSERT INTO CODE_INFO  VALUES ('5', '106', '机器学习图表统计量类型', '标准差', 'ChartStatisticType');
INSERT INTO CODE_INFO  VALUES ('6', '106', '机器学习图表统计量类型', '最小值', 'ChartStatisticType');
INSERT INTO CODE_INFO  VALUES ('7', '106', '机器学习图表统计量类型', '最大值', 'ChartStatisticType');
INSERT INTO CODE_INFO  VALUES ('8', '106', '机器学习图表统计量类型', '方差', 'ChartStatisticType');
INSERT INTO CODE_INFO  VALUES ('9', '106', '机器学习图表统计量类型', '不处理', 'ChartStatisticType');
--　本引用值的代码个数:6 -------i==93
delete from code_info where ci_sp_class='107';
INSERT INTO CODE_INFO  VALUES ('D', '107', 'ETL调度频率', '天(D)', 'Dispatch_Frequency');
INSERT INTO CODE_INFO  VALUES ('M', '107', 'ETL调度频率', '月(M)', 'Dispatch_Frequency');
INSERT INTO CODE_INFO  VALUES ('W', '107', 'ETL调度频率', '周(W)', 'Dispatch_Frequency');
INSERT INTO CODE_INFO  VALUES ('X', '107', 'ETL调度频率', '旬(X)', 'Dispatch_Frequency');
INSERT INTO CODE_INFO  VALUES ('Y', '107', 'ETL调度频率', '年(Y)', 'Dispatch_Frequency');
INSERT INTO CODE_INFO  VALUES ('F', '107', 'ETL调度频率', '频率(F)', 'Dispatch_Frequency');
--　本引用值的代码个数:5 -------i==94
delete from code_info where ci_sp_class='108';
INSERT INTO CODE_INFO  VALUES ('B', '108', 'ETL调度类型', '批前(B)', 'Dispatch_Type');
INSERT INTO CODE_INFO  VALUES ('D', '108', 'ETL调度类型', '依赖触发(D)', 'Dispatch_Type');
INSERT INTO CODE_INFO  VALUES ('T', '108', 'ETL调度类型', '定时T+1触发(T)', 'Dispatch_Type');
INSERT INTO CODE_INFO  VALUES ('Z', '108', 'ETL调度类型', '定时T+0触发(Z)', 'Dispatch_Type');
INSERT INTO CODE_INFO  VALUES ('A', '108', 'ETL调度类型', '批后(A)', 'Dispatch_Type');
--　本引用值的代码个数:6 -------i==95
delete from code_info where ci_sp_class='109';
INSERT INTO CODE_INFO  VALUES ('D', '109', 'ETL作业状态', '完成', 'Job_Status');
INSERT INTO CODE_INFO  VALUES ('E', '109', 'ETL作业状态', '错误', 'Job_Status');
INSERT INTO CODE_INFO  VALUES ('P', '109', 'ETL作业状态', '挂起', 'Job_Status');
INSERT INTO CODE_INFO  VALUES ('R', '109', 'ETL作业状态', '运行', 'Job_Status');
INSERT INTO CODE_INFO  VALUES ('S', '109', 'ETL作业状态', '停止', 'Job_Status');
INSERT INTO CODE_INFO  VALUES ('W', '109', 'ETL作业状态', '等待', 'Job_Status');
--　本引用值的代码个数:3 -------i==96
delete from code_info where ci_sp_class='110';
INSERT INTO CODE_INFO  VALUES ('Y', '110', 'ETl作业有效标志', '有效(Y)', 'Job_Effective_Flag');
INSERT INTO CODE_INFO  VALUES ('N', '110', 'ETl作业有效标志', '无效(N)', 'Job_Effective_Flag');
INSERT INTO CODE_INFO  VALUES ('V', '110', 'ETl作业有效标志', '空跑(V)', 'Job_Effective_Flag');
--　本引用值的代码个数:4 -------i==97
delete from code_info where ci_sp_class='111';
INSERT INTO CODE_INFO  VALUES ('L', '111', 'ETL主服务器同步', '锁定', 'Main_Server_Sync');
INSERT INTO CODE_INFO  VALUES ('N', '111', 'ETL主服务器同步', '不同步', 'Main_Server_Sync');
INSERT INTO CODE_INFO  VALUES ('Y', '111', 'ETL主服务器同步', '同步', 'Main_Server_Sync');
INSERT INTO CODE_INFO  VALUES ('B', '111', 'ETL主服务器同步', '备份中', 'Main_Server_Sync');
--　本引用值的代码个数:2 -------i==98
delete from code_info where ci_sp_class='112';
INSERT INTO CODE_INFO  VALUES ('Y', '112', 'ETL当天调度标志', '是(Y)', 'Today_Dispatch_Flag');
INSERT INTO CODE_INFO  VALUES ('N', '112', 'ETL当天调度标志', '否(N)', 'Today_Dispatch_Flag');
--　本引用值的代码个数:2 -------i==99
delete from code_info where ci_sp_class='113';
INSERT INTO CODE_INFO  VALUES ('T', '113', 'ETL状态', '有效(T)', 'Status');
INSERT INTO CODE_INFO  VALUES ('F', '113', 'ETL状态', '失效(F)', 'Status');
--　本引用值的代码个数:13 -------i==100
delete from code_info where ci_sp_class='114';
INSERT INTO CODE_INFO  VALUES ('GR', '114', 'ETL干预类型', '分组级续跑', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('GP', '114', 'ETL干预类型', '分组级暂停', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('GO', '114', 'ETL干预类型', '分组级重跑，从源头开始', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('JT', '114', 'ETL干预类型', '作业直接跑', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('JS', '114', 'ETL干预类型', '作业停止', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('JR', '114', 'ETL干预类型', '作业重跑', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('JP', '114', 'ETL干预类型', '作业临时调整优先级', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('JJ', '114', 'ETL干预类型', '作业跳过', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('SF', '114', 'ETL干预类型', '系统日切', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('SS', '114', 'ETL干预类型', '系统停止', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('SP', '114', 'ETL干预类型', '系统级暂停', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('SO', '114', 'ETL干预类型', '系统级重跑，从源头开始', 'Meddle_type');
INSERT INTO CODE_INFO  VALUES ('SR', '114', 'ETL干预类型', '系统级续跑', 'Meddle_type');
--　本引用值的代码个数:5 -------i==101
delete from code_info where ci_sp_class='115';
INSERT INTO CODE_INFO  VALUES ('D', '115', 'ETL干预状态', '完成', 'Meddle_status');
INSERT INTO CODE_INFO  VALUES ('E', '115', 'ETL干预状态', '异常', 'Meddle_status');
INSERT INTO CODE_INFO  VALUES ('F', '115', 'ETL干预状态', '失效', 'Meddle_status');
INSERT INTO CODE_INFO  VALUES ('T', '115', 'ETL干预状态', '有效', 'Meddle_status');
INSERT INTO CODE_INFO  VALUES ('R', '115', 'ETL干预状态', '干预中', 'Meddle_status');
--　本引用值的代码个数:2 -------i==102
delete from code_info where ci_sp_class='116';
INSERT INTO CODE_INFO  VALUES ('url', '116', 'ETL变类型', '路径', 'ParamType');
INSERT INTO CODE_INFO  VALUES ('param', '116', 'ETL变类型', '参数', 'ParamType');
--　本引用值的代码个数:10 -------i==103
delete from code_info where ci_sp_class='117';
INSERT INTO CODE_INFO  VALUES ('SHELL', '117', 'ETL作业类型', 'SHELL', 'Pro_Type');
INSERT INTO CODE_INFO  VALUES ('PERL', '117', 'ETL作业类型', 'PERL', 'Pro_Type');
INSERT INTO CODE_INFO  VALUES ('BAT', '117', 'ETL作业类型', 'BAT', 'Pro_Type');
INSERT INTO CODE_INFO  VALUES ('JAVA', '117', 'ETL作业类型', 'JAVA', 'Pro_Type');
INSERT INTO CODE_INFO  VALUES ('PYTHON', '117', 'ETL作业类型', 'PYTHON', 'Pro_Type');
INSERT INTO CODE_INFO  VALUES ('WF', '117', 'ETL作业类型', 'WF', 'Pro_Type');
INSERT INTO CODE_INFO  VALUES ('DBTRAN', '117', 'ETL作业类型', 'DBTRAN', 'Pro_Type');
INSERT INTO CODE_INFO  VALUES ('DBJOB', '117', 'ETL作业类型', 'DBJOB', 'Pro_Type');
INSERT INTO CODE_INFO  VALUES ('Yarn', '117', 'ETL作业类型', 'Yarn', 'Pro_Type');
INSERT INTO CODE_INFO  VALUES ('Thrift', '117', 'ETL作业类型', 'Thrift', 'Pro_Type');
--　本引用值的代码个数:1 -------i==104
delete from code_info where ci_sp_class='118';
INSERT INTO CODE_INFO  VALUES ('1', '118', '其它算法类型', 'PageRank', 'OtherAlgoType');
--　本引用值的代码个数:4 -------i==105
delete from code_info where ci_sp_class='119';
INSERT INTO CODE_INFO  VALUES ('1', '119', '数据映射方式', 'hive', 'DataMapMode');
INSERT INTO CODE_INFO  VALUES ('2', '119', '数据映射方式', 'spark', 'DataMapMode');
INSERT INTO CODE_INFO  VALUES ('3', '119', '数据映射方式', 'hbase', 'DataMapMode');
INSERT INTO CODE_INFO  VALUES ('4', '119', '数据映射方式', 'elk', 'DataMapMode');
--　本引用值的代码个数:4 -------i==106
delete from code_info where ci_sp_class='120';
INSERT INTO CODE_INFO  VALUES ('1', '120', '压缩格式', 'tar', 'ReduceType');
INSERT INTO CODE_INFO  VALUES ('2', '120', '压缩格式', 'gz', 'ReduceType');
INSERT INTO CODE_INFO  VALUES ('3', '120', '压缩格式', 'zip', 'ReduceType');
INSERT INTO CODE_INFO  VALUES ('4', '120', '压缩格式', 'none', 'ReduceType');
--　本引用值的代码个数:2 -------i==107
delete from code_info where ci_sp_class='122';
INSERT INTO CODE_INFO  VALUES ('1', '122', '数据类型', 'xml', 'CollectDataType');
INSERT INTO CODE_INFO  VALUES ('2', '122', '数据类型', 'json', 'CollectDataType');
--　本引用值的代码个数:2 -------i==108
delete from code_info where ci_sp_class='123';
INSERT INTO CODE_INFO  VALUES ('1', '123', '对象采集方式', '行采集', 'ObjectCollectType');
INSERT INTO CODE_INFO  VALUES ('2', '123', '对象采集方式', '对象采集', 'ObjectCollectType');
--　本引用值的代码个数:2 -------i==109
delete from code_info where ci_sp_class='124';
INSERT INTO CODE_INFO  VALUES ('1', '124', '对象数据类型', '数组', 'ObjectDataType');
INSERT INTO CODE_INFO  VALUES ('2', '124', '对象数据类型', '字符串', 'ObjectDataType');
--　本引用值的代码个数:3 -------i==110
delete from code_info where ci_sp_class='125';
INSERT INTO CODE_INFO  VALUES ('1', '125', 'ftp目录规则', '流水号', 'FtpRule');
INSERT INTO CODE_INFO  VALUES ('2', '125', 'ftp目录规则', '固定目录', 'FtpRule');
INSERT INTO CODE_INFO  VALUES ('3', '125', 'ftp目录规则', '按时间', 'FtpRule');
--　本引用值的代码个数:4 -------i==111
delete from code_info where ci_sp_class='126';
INSERT INTO CODE_INFO  VALUES ('1', '126', '时间类型', '日', 'TimeType');
INSERT INTO CODE_INFO  VALUES ('2', '126', '时间类型', '小时', 'TimeType');
INSERT INTO CODE_INFO  VALUES ('3', '126', '时间类型', '分钟', 'TimeType');
INSERT INTO CODE_INFO  VALUES ('4', '126', '时间类型', '秒', 'TimeType');
--　本引用值的代码个数:3 -------i==112
delete from code_info where ci_sp_class='127';
INSERT INTO CODE_INFO  VALUES ('1', '127', '消费周期', '无限期', 'ConsumerCyc');
INSERT INTO CODE_INFO  VALUES ('2', '127', '消费周期', '按时间结束', 'ConsumerCyc');
INSERT INTO CODE_INFO  VALUES ('3', '127', '消费周期', '按数据量结束', 'ConsumerCyc');
--　本引用值的代码个数:6 -------i==113
delete from code_info where ci_sp_class='128';
INSERT INTO CODE_INFO  VALUES ('1', '128', '海云内部消费目的地', 'hbase', 'HyrenConsumeDes');
INSERT INTO CODE_INFO  VALUES ('2', '128', '海云内部消费目的地', 'mpp', 'HyrenConsumeDes');
INSERT INTO CODE_INFO  VALUES ('3', '128', '海云内部消费目的地', 'hbaseOnSolr', 'HyrenConsumeDes');
INSERT INTO CODE_INFO  VALUES ('4', '128', '海云内部消费目的地', 'hdfs', 'HyrenConsumeDes');
INSERT INTO CODE_INFO  VALUES ('5', '128', '海云内部消费目的地', '时序数据库', 'HyrenConsumeDes');
INSERT INTO CODE_INFO  VALUES ('6', '128', '海云内部消费目的地', 'sparkd', 'HyrenConsumeDes');
--　本引用值的代码个数:2 -------i==114
delete from code_info where ci_sp_class='129';
INSERT INTO CODE_INFO  VALUES ('1', '129', '消费方向', '内部', 'ConsDirection');
INSERT INTO CODE_INFO  VALUES ('2', '129', '消费方向', '外部', 'ConsDirection');
--　本引用值的代码个数:6 -------i==115
delete from code_info where ci_sp_class='130';
INSERT INTO CODE_INFO  VALUES ('1', '130', 'hdfs文件类型', 'csv', 'HdfsFileType');
INSERT INTO CODE_INFO  VALUES ('2', '130', 'hdfs文件类型', 'parquet', 'HdfsFileType');
INSERT INTO CODE_INFO  VALUES ('3', '130', 'hdfs文件类型', 'avro', 'HdfsFileType');
INSERT INTO CODE_INFO  VALUES ('4', '130', 'hdfs文件类型', 'orcfile', 'HdfsFileType');
INSERT INTO CODE_INFO  VALUES ('5', '130', 'hdfs文件类型', 'sequencefile', 'HdfsFileType');
INSERT INTO CODE_INFO  VALUES ('6', '130', 'hdfs文件类型', '其他', 'HdfsFileType');
--　本引用值的代码个数:5 -------i==116
delete from code_info where ci_sp_class='131';
INSERT INTO CODE_INFO  VALUES ('1', '131', 'hive文件存储类型', 'TEXTFILE', 'HiveStorageType');
INSERT INTO CODE_INFO  VALUES ('2', '131', 'hive文件存储类型', 'SEQUENCEFILE', 'HiveStorageType');
INSERT INTO CODE_INFO  VALUES ('3', '131', 'hive文件存储类型', 'PARQUET', 'HiveStorageType');
INSERT INTO CODE_INFO  VALUES ('4', '131', 'hive文件存储类型', 'CSV', 'HiveStorageType');
INSERT INTO CODE_INFO  VALUES ('5', '131', 'hive文件存储类型', 'ORC', 'HiveStorageType');
--　本引用值的代码个数:5 -------i==117
delete from code_info where ci_sp_class='132';
INSERT INTO CODE_INFO  VALUES ('1', '132', '数据块编码', 'NONE', 'DataBlockEncode');
INSERT INTO CODE_INFO  VALUES ('2', '132', '数据块编码', 'PREFIX', 'DataBlockEncode');
INSERT INTO CODE_INFO  VALUES ('3', '132', '数据块编码', 'DIFF', 'DataBlockEncode');
INSERT INTO CODE_INFO  VALUES ('4', '132', '数据块编码', 'FAST_DIFF', 'DataBlockEncode');
INSERT INTO CODE_INFO  VALUES ('5', '132', '数据块编码', 'PREFIX_TREE', 'DataBlockEncode');
--　本引用值的代码个数:2 -------i==118
delete from code_info where ci_sp_class='133';
INSERT INTO CODE_INFO  VALUES ('1', '133', '预分区规则', 'SPLITNUM', 'PrePartition ');
INSERT INTO CODE_INFO  VALUES ('2', '133', '预分区规则', 'SPLITPOINS', 'PrePartition ');
--　本引用值的代码个数:4 -------i==119
delete from code_info where ci_sp_class='134';
INSERT INTO CODE_INFO  VALUES ('1', '134', '流数据管理druid时间戳格式', 'iso', 'SdmTimestampFormat');
INSERT INTO CODE_INFO  VALUES ('2', '134', '流数据管理druid时间戳格式', 'millis', 'SdmTimestampFormat');
INSERT INTO CODE_INFO  VALUES ('3', '134', '流数据管理druid时间戳格式', 'posix', 'SdmTimestampFormat');
INSERT INTO CODE_INFO  VALUES ('4', '134', '流数据管理druid时间戳格式', 'jodaTime', 'SdmTimestampFormat');
--　本引用值的代码个数:4 -------i==120
delete from code_info where ci_sp_class='135';
INSERT INTO CODE_INFO  VALUES ('1', '135', '流数据管理druid数据处理格式', 'json', 'SdmDataFormat');
INSERT INTO CODE_INFO  VALUES ('2', '135', '流数据管理druid数据处理格式', 'csv', 'SdmDataFormat');
INSERT INTO CODE_INFO  VALUES ('3', '135', '流数据管理druid数据处理格式', 'regex', 'SdmDataFormat');
INSERT INTO CODE_INFO  VALUES ('4', '135', '流数据管理druid数据处理格式', 'javascript', 'SdmDataFormat');
--　本引用值的代码个数:2 -------i==121
delete from code_info where ci_sp_class='136';
INSERT INTO CODE_INFO  VALUES ('1', '136', '流数据管理druid服务类型', '索引服务', 'SdmDruidServerType');
INSERT INTO CODE_INFO  VALUES ('2', '136', '流数据管理druid服务类型', '实时服务', 'SdmDruidServerType');
--　本引用值的代码个数:2 -------i==122
delete from code_info where ci_sp_class='137';
INSERT INTO CODE_INFO  VALUES ('0', '137', '数据质量规则级别', '警告', 'EdRuleLevel');
INSERT INTO CODE_INFO  VALUES ('1', '137', '数据质量规则级别', '严重', 'EdRuleLevel');
--　本引用值的代码个数:3 -------i==123
delete from code_info where ci_sp_class='138';
INSERT INTO CODE_INFO  VALUES ('0', '138', '数据质量校验结果', '检查通过', 'DqcVerifyResult');
INSERT INTO CODE_INFO  VALUES ('1', '138', '数据质量校验结果', '数据异常', 'DqcVerifyResult');
INSERT INTO CODE_INFO  VALUES ('2', '138', '数据质量校验结果', '执行失败', 'DqcVerifyResult');
--　本引用值的代码个数:7 -------i==124
delete from code_info where ci_sp_class='139';
INSERT INTO CODE_INFO  VALUES ('w', '139', '数据质量处理状态', '等待处理', 'DqcDlStat');
INSERT INTO CODE_INFO  VALUES ('b', '139', '数据质量处理状态', '已退回', 'DqcDlStat');
INSERT INTO CODE_INFO  VALUES ('i', '139', '数据质量处理状态', '已忽略', 'DqcDlStat');
INSERT INTO CODE_INFO  VALUES ('d', '139', '数据质量处理状态', '已处理', 'DqcDlStat');
INSERT INTO CODE_INFO  VALUES ('oki', '139', '数据质量处理状态', '处理完结', 'DqcDlStat');
INSERT INTO CODE_INFO  VALUES ('okd', '139', '数据质量处理状态', '已忽略通过', 'DqcDlStat');
INSERT INTO CODE_INFO  VALUES ('zc', '139', '数据质量处理状态', '正常', 'DqcDlStat');
--　本引用值的代码个数:2 -------i==125
delete from code_info where ci_sp_class='140';
INSERT INTO CODE_INFO  VALUES ('MAN', '140', '数据质量执行方式', '手工', 'DqcExecMode');
INSERT INTO CODE_INFO  VALUES ('AUTO', '140', '数据质量执行方式', '自动', 'DqcExecMode');
--　本引用值的代码个数:3 -------i==126
delete from code_info where ci_sp_class='141';
INSERT INTO CODE_INFO  VALUES ('0', '141', '流数据管理自定义业务类类型', 'None', 'SdmCustomBusCla');
INSERT INTO CODE_INFO  VALUES ('1', '141', '流数据管理自定义业务类类型', 'Java', 'SdmCustomBusCla');
INSERT INTO CODE_INFO  VALUES ('2', '141', '流数据管理自定义业务类类型', 'JavaScript', 'SdmCustomBusCla');
--　本引用值的代码个数:2 -------i==127
delete from code_info where ci_sp_class='142';
INSERT INTO CODE_INFO  VALUES ('1', '142', '字符拆分方式', '偏移量', 'CharSplitType');
INSERT INTO CODE_INFO  VALUES ('2', '142', '字符拆分方式', '自定符号', 'CharSplitType');
--　本引用值的代码个数:2 -------i==128
delete from code_info where ci_sp_class='143';
INSERT INTO CODE_INFO  VALUES ('1', '143', '消费者类型', 'consumer', 'ConsumerType');
INSERT INTO CODE_INFO  VALUES ('2', '143', '消费者类型', 'streams', 'ConsumerType');
--　本引用值的代码个数:2 -------i==129
delete from code_info where ci_sp_class='145';
INSERT INTO CODE_INFO  VALUES ('1', '145', '数据抽取方式', '仅数据抽取', 'DataExtractType');
INSERT INTO CODE_INFO  VALUES ('2', '145', '数据抽取方式', '数据抽取及入库', 'DataExtractType');
--　本引用值的代码个数:4 -------i==130
delete from code_info where ci_sp_class='146';
INSERT INTO CODE_INFO  VALUES ('1', '146', '流数据申请状态', '未申请', 'FlowApplyStatus');
INSERT INTO CODE_INFO  VALUES ('2', '146', '流数据申请状态', '申请中', 'FlowApplyStatus');
INSERT INTO CODE_INFO  VALUES ('3', '146', '流数据申请状态', '申请通过', 'FlowApplyStatus');
INSERT INTO CODE_INFO  VALUES ('4', '146', '流数据申请状态', '申请不通过', 'FlowApplyStatus');
--　本引用值的代码个数:2 -------i==131
delete from code_info where ci_sp_class='150';
INSERT INTO CODE_INFO  VALUES ('1', '150', 'StreamingPro输入的数据模式', '批量表', 'SdmSpDataMode');
INSERT INTO CODE_INFO  VALUES ('2', '150', 'StreamingPro输入的数据模式', '流表', 'SdmSpDataMode');
--　本引用值的代码个数:4 -------i==132
delete from code_info where ci_sp_class='151';
INSERT INTO CODE_INFO  VALUES ('1', '151', 'StreamingPro输入输出的类型', '文本文件', 'SdmSpInputOutputType');
INSERT INTO CODE_INFO  VALUES ('2', '151', 'StreamingPro输入输出的类型', '数据库表', 'SdmSpInputOutputType');
INSERT INTO CODE_INFO  VALUES ('3', '151', 'StreamingPro输入输出的类型', '消费主题', 'SdmSpInputOutputType');
INSERT INTO CODE_INFO  VALUES ('4', '151', 'StreamingPro输入输出的类型', '内部表', 'SdmSpInputOutputType');
--　本引用值的代码个数:3 -------i==133
delete from code_info where ci_sp_class='152';
INSERT INTO CODE_INFO  VALUES ('1', '152', 'StreamingPro文本文件格式', 'Csv', 'SdmSpFileType');
INSERT INTO CODE_INFO  VALUES ('2', '152', 'StreamingPro文本文件格式', 'Parquent', 'SdmSpFileType');
INSERT INTO CODE_INFO  VALUES ('3', '152', 'StreamingPro文本文件格式', 'Json', 'SdmSpFileType');
--　本引用值的代码个数:3 -------i==134
delete from code_info where ci_sp_class='153';
INSERT INTO CODE_INFO  VALUES ('1', '153', 'StreamingPro流式数据的版本', 'kafka8', 'SdmSpStreamVer');
INSERT INTO CODE_INFO  VALUES ('2', '153', 'StreamingPro流式数据的版本', 'kafka9', 'SdmSpStreamVer');
INSERT INTO CODE_INFO  VALUES ('3', '153', 'StreamingPro流式数据的版本', 'kafka', 'SdmSpStreamVer');
--　本引用值的代码个数:2 -------i==135
delete from code_info where ci_sp_class='154';
INSERT INTO CODE_INFO  VALUES ('1', '154', 'StreamingPro输出模式', 'Append', 'SdmSpOutputMode');
INSERT INTO CODE_INFO  VALUES ('2', '154', 'StreamingPro输出模式', 'Overwrite', 'SdmSpOutputMode');
--　本引用值的代码个数:3 -------i==136
delete from code_info where ci_sp_class='155';
INSERT INTO CODE_INFO  VALUES ('01', '155', '自主取数模板状态', '编辑', 'AutoTemplateStatus');
INSERT INTO CODE_INFO  VALUES ('04', '155', '自主取数模板状态', '发布', 'AutoTemplateStatus');
INSERT INTO CODE_INFO  VALUES ('05', '155', '自主取数模板状态', '注销', 'AutoTemplateStatus');
--　本引用值的代码个数:6 -------i==137
delete from code_info where ci_sp_class='156';
INSERT INTO CODE_INFO  VALUES ('01', '156', '自主取数数据展现形式', '文本框', 'AutoDataRetrievalForm');
INSERT INTO CODE_INFO  VALUES ('02', '156', '自主取数数据展现形式', '下拉选择', 'AutoDataRetrievalForm');
INSERT INTO CODE_INFO  VALUES ('03', '156', '自主取数数据展现形式', '下拉多选', 'AutoDataRetrievalForm');
INSERT INTO CODE_INFO  VALUES ('04', '156', '自主取数数据展现形式', '单选按钮', 'AutoDataRetrievalForm');
INSERT INTO CODE_INFO  VALUES ('05', '156', '自主取数数据展现形式', '复选按钮', 'AutoDataRetrievalForm');
INSERT INTO CODE_INFO  VALUES ('06', '156', '自主取数数据展现形式', '日期选择', 'AutoDataRetrievalForm');
--　本引用值的代码个数:3 -------i==138
delete from code_info where ci_sp_class='157';
INSERT INTO CODE_INFO  VALUES ('01', '157', '自主取数取数状态', '编辑', 'AutoFetchStatus');
INSERT INTO CODE_INFO  VALUES ('04', '157', '自主取数取数状态', '完成', 'AutoFetchStatus');
INSERT INTO CODE_INFO  VALUES ('05', '157', '自主取数取数状态', '注销', 'AutoFetchStatus');
--　本引用值的代码个数:3 -------i==139
delete from code_info where ci_sp_class='158';
INSERT INTO CODE_INFO  VALUES ('01', '158', '可视化源对象', '自主数据数据集', 'AutoSourceObject');
INSERT INTO CODE_INFO  VALUES ('02', '158', '可视化源对象', '系统级数据集', 'AutoSourceObject');
INSERT INTO CODE_INFO  VALUES ('03', '158', '可视化源对象', '数据组件数据集', 'AutoSourceObject');
--　本引用值的代码个数:12 -------i==140
delete from code_info where ci_sp_class='159';
INSERT INTO CODE_INFO  VALUES ('01', '159', '可视化数据操作符', '介于', 'AutoDataOperator');
INSERT INTO CODE_INFO  VALUES ('02', '159', '可视化数据操作符', '不介于', 'AutoDataOperator');
INSERT INTO CODE_INFO  VALUES ('03', '159', '可视化数据操作符', '等于', 'AutoDataOperator');
INSERT INTO CODE_INFO  VALUES ('04', '159', '可视化数据操作符', '不等于', 'AutoDataOperator');
INSERT INTO CODE_INFO  VALUES ('05', '159', '可视化数据操作符', '大于', 'AutoDataOperator');
INSERT INTO CODE_INFO  VALUES ('06', '159', '可视化数据操作符', '小于', 'AutoDataOperator');
INSERT INTO CODE_INFO  VALUES ('07', '159', '可视化数据操作符', '大于等于', 'AutoDataOperator');
INSERT INTO CODE_INFO  VALUES ('08', '159', '可视化数据操作符', '小于等于', 'AutoDataOperator');
INSERT INTO CODE_INFO  VALUES ('09', '159', '可视化数据操作符', '最大的N个', 'AutoDataOperator');
INSERT INTO CODE_INFO  VALUES ('10', '159', '可视化数据操作符', '最小的N个', 'AutoDataOperator');
INSERT INTO CODE_INFO  VALUES ('11', '159', '可视化数据操作符', '为空', 'AutoDataOperator');
INSERT INTO CODE_INFO  VALUES ('12', '159', '可视化数据操作符', '非空', 'AutoDataOperator');
--　本引用值的代码个数:7 -------i==141
delete from code_info where ci_sp_class='160';
INSERT INTO CODE_INFO  VALUES ('01', '160', '可视化数据汇总类型', '求和', 'AutoDataSumType');
INSERT INTO CODE_INFO  VALUES ('02', '160', '可视化数据汇总类型', '求平均', 'AutoDataSumType');
INSERT INTO CODE_INFO  VALUES ('03', '160', '可视化数据汇总类型', '求最大值', 'AutoDataSumType');
INSERT INTO CODE_INFO  VALUES ('04', '160', '可视化数据汇总类型', '求最小值', 'AutoDataSumType');
INSERT INTO CODE_INFO  VALUES ('05', '160', '可视化数据汇总类型', '总行数', 'AutoDataSumType');
INSERT INTO CODE_INFO  VALUES ('06', '160', '可视化数据汇总类型', '原始数据', 'AutoDataSumType');
INSERT INTO CODE_INFO  VALUES ('07', '160', '可视化数据汇总类型', '查看全部', 'AutoDataSumType');
--　本引用值的代码个数:2 -------i==142
delete from code_info where ci_sp_class='161';
INSERT INTO CODE_INFO  VALUES ('01', '161', '可视化数据运算逻辑', '且', 'AutoDataOperatLogic');
INSERT INTO CODE_INFO  VALUES ('02', '161', '可视化数据运算逻辑', '或', 'AutoDataOperatLogic');
--　本引用值的代码个数:4 -------i==143
delete from code_info where ci_sp_class='162';
INSERT INTO CODE_INFO  VALUES ('01', '162', '值类型', '字符串', 'AutoValueType');
INSERT INTO CODE_INFO  VALUES ('02', '162', '值类型', '数值', 'AutoValueType');
INSERT INTO CODE_INFO  VALUES ('03', '162', '值类型', '日期', 'AutoValueType');
INSERT INTO CODE_INFO  VALUES ('04', '162', '值类型', '枚举', 'AutoValueType');
--　本引用值的代码个数:2 -------i==144
delete from code_info where ci_sp_class='163';
INSERT INTO CODE_INFO  VALUES ('01', '163', '自主取数数据来源', '内部', 'AutoDataSource');
INSERT INTO CODE_INFO  VALUES ('02', '163', '自主取数数据来源', '外部', 'AutoDataSource');
--　本引用值的代码个数:2 -------i==145
delete from code_info where ci_sp_class='164';
INSERT INTO CODE_INFO  VALUES ('1', '164', '可视化轴类型', 'x轴', 'AxisType');
INSERT INTO CODE_INFO  VALUES ('2', '164', '可视化轴类型', 'y轴', 'AxisType');
