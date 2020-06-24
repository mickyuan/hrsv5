# 5.0.1
### 核心包
- fdcore 新增SnowflakeImpl.java类：修改生成主键的方法，将主键全部生成为18位的long类型
- fddbdata 静态块方法：修改获取连接池参数maxPoolSize错误的bug
- fdcore Validator类：新增对isIpAddr isPort两个方法，判断ip地址、端口的合法性
- fdcore validator类：修改所有的抛出异常的方法，修改为BusinessProcessException异常，因为需要将信息抛给页面
- fdcore AppinfoConf类：添加读取appinfo配置文件中projectId参数，同时添加对配置文件中properties参数可以任意定义key-value的方式
- fdnetclient HttpClient类：添加addData方法：支持测试用例直接可以传入一个实体bean
- fdcore JsonUtil类：添加toJsonForJS方法：支持long类型的长度，如果超出16位默认转存字符串返回
- fdweb ResponseUtil类：修改writeJSON方法：支持long类型的长度，如果超出16位默认转存字符串的方式返回给页面

### commons
- 01-create_table.sql添加KEYTABLE_SNOWFLAKE表：主要配置获取主键的数据中心id和机器识别id
- 02-seq.sql添加对KEYTABLE_SNOWFLAKE表的初始化sql
- PrimayKeyGener类：添加静态块方法：主要读取数据库配置的初始数据，以便生成主键
- PrimayKeyGener类：getNextId方法：修改返回值为long类型
- DataTableUtil类：getTableInfoAndColumnInfo方法 修改switch为ifelse判断
- DataTableUtil类：getColumnByFileId方法： 修改switch为ifelse判断
- DataTableUtil类：getColumnByTableName方法： 添加DML层查询
- Node.java 修改该类为自定义实体Bean,对所有属性添加get和set
- NodeDataConvertedTreeList.java 使用get替代获取属性值,set替代设置属性值
- NodeIDComparator.java 使用get替代获取属性值
- LoginOperationLogInfo类：saveLoginLog方法：new DatabaseWrapper改为直接使用Dbo
- AgentActionUtil类：getUrl方法：将两句查询sql并为一个join关联sql
- Constant类增加常量：操作日期、操作时间、操作人三列的列名常量
- 05-sys_para_new.sql：增加文件采集或者ftp采集比较文件是否变化的方式:MD5或fileAttr；多线程采集每个任务可用线程数,这一行默认不用存库，系统默认取最大值；采集是否添加操作时间、操作日期、操作人：true或false

### B项目
- StoDestStepConfAction：saveTbStoInfo：获取主键的类型改为long,之前为String
- CollTbConfStepAction：saveAllSQL：获取主键的类型改为long,之前为String,并去除之前的Long.parseLong
- CollTbConfStepAction： saveCollTbInfo：获取主键的类型改为long,之前为String,并去除之前的Long.parseLong
- ObjectCollectAction：saveCollectColumnStruct：获取主键返回的是long,这里不在使用String接收
- CollectFIleAction：saveDataFile：回去的主键进行转换,增加String.valueOf
- DictionaryTableAction：saveTableData： 获取主键的类型改为long,之前为String,并去除之前的Long.parseLong
- DBConfStepAction：saveDbConf : 获取主键的类型改为long,之前为String
- UnstructuredFileCollectActionTest类：修改测试用例支持多线程并发测试
- ObjectCollectAction：saveCollectColumnStruct：获取主键的类型改为long,之前为String
- ObjectCollectAction：saveCollectColumnStruct:获取主键的类型改为long,之前为String
- DataSourceAction：addColumnSplit：获取主键的类型改为long,之前为String
- DataSourceAction：addColumnSplit：获取主键的类型改为long,之前为String
- DataSourceAction：addTableClean： 获取主键的类型改为long,之前为String
- DataSourceAction：addFileSource：获取主键的类型改为long,之前为String
- DataSourceAction：addObjectStorage：获取主键的类型改为long,之前为String
- DataSourceAction：addFtpTransfered：获取主键的类型改为long,之前为String
- DataSourceAction：addAgentDownInfo：获取主键的类型改为long,之前为String
- DataSourceAction：addSourceRelationDep：获取主键的类型改为long,之前为String
- DataSourceAction：getDataSource：获取主键的类型改为long,之前为String
- DataSourceAction：addAgentDownInfo：获取主键的类型改为long,之前为String
- DataSourceAction：addAgentDownInfo：获取主键的类型改为long,之前为String
- DataSourceAction：addAgentDownInfo：获取主键的类型改为long,之前为String
- DataSourceAction：addAgentDownInfo：获取主键的类型改为long,之前为String
- AgentListAction.java：sendJDBCCollectTaskById和sendDBCollectTaskById方法：发送数据到agent新增user_id

### Agent项目
- JobConstant类：增加常量：agent写文件时缓存的行数 5000条
- JdbcToCsvFileWriter类：writeFiles方法：写文件时缓存的行数使用JobConstant类常量
- JdbcToFixedFileWriter类：writeFiles方法：写文件时缓存的行数使用JobConstant类常量
- JdbcToIncrementFileWriter类：writeFiles方法：写文件时缓存的行数使用JobConstant类常量
- JdbcToNonFixedFileWriter类：writeFiles方法：写文件时缓存的行数使用JobConstant类常量
- JdbcToOrcFileWriter类：writeFiles方法：写文件时缓存的行数使用JobConstant类常量
- JdbcToParquetFileWriter类：writeFiles方法：写文件时缓存的行数使用JobConstant类常量
- JdbcToSequenceFileWriter类：writeFiles方法：写文件时缓存的行数使用JobConstant类常量
- CsvFileParserDeal类：parserFile方法：写文件时缓存的行数使用JobConstant类常量
- FixedFileParserDeal类：parserFile方法：写文件时缓存的行数使用JobConstant类常量
- NonFixedFileParserDeal类：parserFile方法：写文件时缓存的行数使用JobConstant类常量
- ParquetFileParserDeal类：parserFile方法：写文件时缓存的行数使用JobConstant类常量
- SequenceFileParserDeal类：parserFile方法：写文件时缓存的行数使用JobConstant类常量
- ReadFileToDataBase类：读文件batch提交到数据库缓存的行数使用JobConstant类常量
- PropertyParaUtil：静态代码块：修改agent获取sysparam.conf文件到YamlMap的bug
- CollectTableBean：增加user_id:加user_id用于采集卸数数据增加操作人
- JobConstant：增加常量：是否添加操作信息isAddOperateInfo，修改md5为MD5进行比较，和系统参数的对应
- JdbcToCsvFileWriter.java：writeFiles方法：根据系统参数拼接操作日期、操作时间、操作人
- JdbcToFixedFileWriter.java：writeFiles方法：根据系统参数拼接操作日期、操作时间、操作人
- JdbcToNonFixedFileWriter.java：writeFiles方法：根据系统参数拼接操作日期、操作时间、操作人
- JdbcToOrcFileWriter.java：writeFiles方法：根据系统参数拼接操作日期、操作时间、操作人
- JdbcToParquetFileWriter.java：writeFiles方法：根据系统参数拼接操作日期、操作时间、操作人
- JdbcToSequenceFileWriter.java：writeFiles方法：根据系统参数拼接操作日期、操作时间、操作人
- FileParserAbstract.java：dealLine方法：构造方法初始化根据系统参数拼接操作日期、操作时间、操作人；过滤掉DB文本文件中的操作日期、操作时间、操作人；根据系统参数拼接操作日期、操作时间、操作人
- DFCollectTableHandleParse.java：generateTableInfo方法：转存，过滤掉数据字典中海云的操作日期、操作时间、操作人三个字段；根据系统参数拼接操作日期、操作时间、操作人
- JdbcCollectTableHandleParse.java：getFullAmountExtractTableBean方法：根据系统参数拼接操作日期、操作时间、操作人

### K项目
- DataManageAction类：删除getDMStatistics方法,提供getTableStatistics和getRuleStatistics方法,参数名称query_num修改为statistics_layer_num
- BloodAnalysisAction：getTableBloodRelationship方法： 优化校验参数方式,使用Validator进行校验
- BloodAnalysisAction：fuzzySearchTableName方法： 优化校验参数方式,使用Validator进行校验
- BloodAnalysisAction：influencesDataInfo方法：优化校验参数方式,使用Validator进行校验
- MetaDataManageAction：getMDMTreeData 修改返回结果集类型Map<String, Object>为List<Node>
- MetaDataManageAction：getDRBTreeData 修改返回结果集类型Map<String, Object>为List<Node>
- MetaDataManageAction：getMDMTableColumnInfo 优化校验参数方式,使用Validator进行校验,修改switch为ifelse判断
- MetaDataManageAction：saveMetaData 修改switch为ifelse判断
- MetaDataManageAction：tableSetToInvalid 修改switch为ifelse判断
- MetaDataManageAction：removeCompletelyTable 删除new DatabaseWrapper()此处不需要
- MetaDataManageAction：removeCompletelyAllTable 删除new DatabaseWrapper()此处不需要
- MetaDataManageAction：createTable 修改switch为ifelse判断
- TableMetaInfoTool:  setDCLTableInvalid 删除new DatabaseWrapper()此处不需要
- TableMetaInfoTool:	setDMLTableInvalid 删除new DatabaseWrapper()此处不需要
- TableMetaInfoTool:	restoreDCLTableInfo 删除new DatabaseWrapper()此处不需要
- TableMetaInfoTool:	restoreDMLTableInfo 删除new DatabaseWrapper()此处不需要
- TableMetaInfoTool:	updateDCLTableMetaInfo 删除new DatabaseWrapper()此处不需要
- TableMetaInfoTool:	updateDMLTableMetaInfo 删除new DatabaseWrapper()此处不需要
- RuleConfigAction：addDqDefinition 优化校验参数方式,使用Validator进行校验
- RuleConfigAction：updateDqDefinition 优化校验参数方式,使用Validator进行校验
- RuleConfigAction：getColumnsByTableName 优化校验参数方式,使用Validator进行校验
- RuleConfigAction：manualExecution	删除new DatabaseWrapper()此处不需要
- RuleConfigAction：getTaskInfo 优化校验参数方式,使用Validator进行校验
- RuleConfigAction：errDataSqlCheck 修改提示信息
- RuleConfigAction：listToMap 删除,无用代码
- VariableConfigAction：addVariableConfigDat 优化校验参数方式,使用Validator进行校验,修改sql拼接方式
- VariableConfigAction：updateVariableConfigData 优化校验参数方式,使用Validator进行校验,修改sql拼接方式
- TSBAction：getTSBTreeData 修改返回结果集类型Map<String, Object>为List<Node>
- TSBAction：getColumnByFileId 添加根据数据层获取不同数据层的字段信息
- TSBAction：saveTSBConfData 使用new DatabaseWrapper()后添加回滚
- TSBAction：setDbmDtableInfo 修改变量名称,混淆定义
- ImportData：importDbmCodeTypeInfoData：获取主键id返回值类型调整long > string
- TSBAction：saveTSBConfData、setDbmNormbmDetect、setDbmNormbmdResult：获取主键id返回值类型调整long > string
  
### H项目  
- MarketInfoAction: deleteDMDataTable: 获取主键的类型改为long,之前为String
- MarketInfoAction: getDefaultFieldType: 获取主键的类型改为long,之前为String
- MarketInfoAction: saveBloodRelationToPGTable: 获取主键的类型改为long,之前为String
- MarketInfoAction: saveimportexcel: 获取主键的类型改为long,之前为String

