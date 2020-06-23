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

### B
- StoDestStepConfAction.java：saveTbStoInfo方法：获取主键的类型次改为long,之前为String
- CollTbConfStepAction.java：saveAllSQL、saveCollTbInfo方法：获取主键的类型次改为long,之前为String,并去除之前的Long.parse
- ObjectCollectAction.java：saveCollectColumnStruct方法：获取主键返回的是long,这里不在使用String接收


