--机器学习分层抽取范围对应表
DROP TABLE IF EXISTS ML_HEXT_SCOPE ;
CREATE TABLE ML_HEXT_SCOPE(
HEXTSCOPE_ID                                      DECIMAL(10) NOT NULL, --分层抽取范围编号
EXTR_VALUE                                        VARCHAR(64) NOT NULL, --抽取值
HIEREXTR_ID                                       DECIMAL(10) NOT NULL, --分层抽取编号
CONSTRAINT ML_HEXT_SCOPE_PK PRIMARY KEY(HEXTSCOPE_ID)   );

--分类变量重编码表
DROP TABLE IF EXISTS ML_CVAR_RECODE ;
CREATE TABLE ML_CVAR_RECODE(
RECODE_ID                                         DECIMAL(10) NOT NULL, --重编码编号
CATEVAR_COLUMN                                    VARCHAR(64) NOT NULL, --分类变量字段名称
CATEVAR_CODE                                      CHAR(1) NOT NULL, --分类变量编码方式
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
TAGENCODE_INFO                                    VARCHAR(512) NULL, --标签编码信息
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_CVAR_RECODE_PK PRIMARY KEY(RECODE_ID)   );

--数值变量重编码表
DROP TABLE IF EXISTS ML_NVAR_RECODE ;
CREATE TABLE ML_NVAR_RECODE(
RECODE_ID                                         DECIMAL(10) NOT NULL, --重编码编号
NUMEVAR_COLUMN                                    VARCHAR(64) NOT NULL, --数值变量字段名称
RECODE_MODE                                       CHAR(1) NOT NULL, --数值变量重编码处理方式
NEWCOLUMN_NAME                                    VARCHAR(64) NOT NULL, --新字段名称
NORM_RANGE                                        CHAR(1) NULL, --归一化范围
DISC_INFO                                         VARCHAR(1024) NULL, --离散化信息
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_NVAR_RECODE_PK PRIMARY KEY(RECODE_ID)   );

--数据预处理其它信息表
DROP TABLE IF EXISTS ML_DATAPREP_INFO_O ;
CREATE TABLE ML_DATAPREP_INFO_O(
DATAPO_INFO_ID                                    DECIMAL(10) NOT NULL, --数据预处理其它信息编号
NEWTABLE_NAME                                     VARCHAR(512) NOT NULL, --新表名称
GENERATE_IS_FLAG                                  CHAR(1) NOT NULL, --新表是否生成完成
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
DATAMAPMODE                                       CHAR(1) NOT NULL, --数据映射方式
NEW_FILEPATH                                      VARCHAR(512) NOT NULL, --新表文件路径
NEW_FILESIZE                                      DECIMAL(16) default 0 NOT NULL, --新表文件大小
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NULL, --数据表信息编号
CONSTRAINT ML_DATAPREP_INFO_O_PK PRIMARY KEY(DATAPO_INFO_ID)   );

--数据转换函数表
DROP TABLE IF EXISTS ML_DATATRANSFER_FUN ;
CREATE TABLE ML_DATATRANSFER_FUN(
TRANFUNT_NUM                                      DECIMAL(10) NOT NULL, --转换函数类型编号
FUNTYPE_NAME                                      VARCHAR(64) NOT NULL, --函数类型名称
CONSTRAINT ML_DATATRANSFER_FUN_PK PRIMARY KEY(TRANFUNT_NUM)   );

--函数对应表
DROP TABLE IF EXISTS ML_FUNCTION_MAP ;
CREATE TABLE ML_FUNCTION_MAP(
FUNMAP_ID                                         DECIMAL(10) NOT NULL, --函数对应编码
SPECIFIC_FUN                                      VARCHAR(64) NOT NULL, --具体函数
TRANSFER_METHOD                                   CHAR(3) NOT NULL, --转换方式
TRANFUNT_NUM                                      DECIMAL(10) NOT NULL, --转换函数类型编号
CONSTRAINT ML_FUNCTION_MAP_PK PRIMARY KEY(FUNMAP_ID)   );

--机器学习假设t检验表
DROP TABLE IF EXISTS ML_T_HYPOTHESIS_TEST ;
CREATE TABLE ML_T_HYPOTHESIS_TEST(
T_HYPOTEST_ID                                     DECIMAL(10) NOT NULL, --t检验编号
T_HYPO_TYPE                                       CHAR(1) NOT NULL, --t检验样本类型
SINGAAMP_COLU                                     VARCHAR(64) NULL, --单样本字段
SINGSAMP_MEAN                                     DECIMAL(16,2) default 0 NULL, --单样本均值
TWO_SAMPCF                                        VARCHAR(64) NULL, --双样本字段1
TWO_SAMPCS                                        VARCHAR(64) NULL, --双样本字段2
INDESAMP_STYLE                                    CHAR(1) NULL, --双样本独立样本类型
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_T_HYPOTHESIS_TEST_PK PRIMARY KEY(T_HYPOTEST_ID)   );

--机器学习假设f检验表
DROP TABLE IF EXISTS ML_F_HYPOTHESIS_TEST ;
CREATE TABLE ML_F_HYPOTHESIS_TEST(
F_HYPOTEST_ID                                     DECIMAL(10) NOT NULL, --f检验编号
F_COLUMN_FIRST                                    VARCHAR(64) NOT NULL, --f检验字段1
F_COLUMN_SECOND                                   VARCHAR(64) NOT NULL, --f检验字段2
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_F_HYPOTHESIS_TEST_PK PRIMARY KEY(F_HYPOTEST_ID)   );

--机器学习卡方检验表
DROP TABLE IF EXISTS ML_CHISQUARED_TEST ;
CREATE TABLE ML_CHISQUARED_TEST(
CHISQUARED_ID                                     DECIMAL(10) NOT NULL, --卡方检验编号
CREATE_DATE                                       CHAR(8) NULL, --创建日期
CREATE_TIME                                       CHAR(6) NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_CHISQUARED_TEST_PK PRIMARY KEY(CHISQUARED_ID)   );

--机器学习方差分析自变量表
DROP TABLE IF EXISTS ML_VARIANCEANAL_IV ;
CREATE TABLE ML_VARIANCEANAL_IV(
VARIANAL_IV_ID                                    DECIMAL(10) NOT NULL, --方差分析自变量编号
IV_COLUMN                                         VARCHAR(64) NOT NULL, --自变量字段
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_VARIANCEANAL_IV_PK PRIMARY KEY(VARIANAL_IV_ID)   );

--机器学习卡方检验字段表
DROP TABLE IF EXISTS ML_CHISQUARED_COLUMN ;
CREATE TABLE ML_CHISQUARED_COLUMN(
CHISQUATC_ID                                      DECIMAL(10) NOT NULL, --卡方检验字段编号
CHISQUATESTC                                      VARCHAR(64) NOT NULL, --卡方检验字段
CHISQUARED_ID                                     DECIMAL(10) NOT NULL, --卡方检验编号
CONSTRAINT ML_CHISQUARED_COLUMN_PK PRIMARY KEY(CHISQUATC_ID)   );

--机器学习方差分析因变量表
DROP TABLE IF EXISTS ML_VARIANCEANAL_DV ;
CREATE TABLE ML_VARIANCEANAL_DV(
VARIANALY_DV_ID                                   DECIMAL(10) NOT NULL, --方差分析因变量编号
DV_COLUMN                                         VARCHAR(64) NOT NULL, --因变量字段
VARIANAL_IV_ID                                    DECIMAL(10) NOT NULL, --方差分析自变量编号
CONSTRAINT ML_VARIANCEANAL_DV_PK PRIMARY KEY(VARIANALY_DV_ID)   );

--机器学习特征过滤表
DROP TABLE IF EXISTS ML_FEATURE_FILTER ;
CREATE TABLE ML_FEATURE_FILTER(
FEATFILT_ID                                       DECIMAL(10) NOT NULL, --特征过滤编号
VARIANCE_RANGE                                    DECIMAL(16,2) default 0 NULL, --方差值域
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NULL, --数据表信息编号
CONSTRAINT ML_FEATURE_FILTER_PK PRIMARY KEY(FEATFILT_ID)   );

--机器学习特征过滤字段表
DROP TABLE IF EXISTS ML_FEAT_FILT_COLUMN ;
CREATE TABLE ML_FEAT_FILT_COLUMN(
FEATFILTC_ID                                      DECIMAL(10) NOT NULL, --特征过滤字段编号
FEATF_COLUMN                                      VARCHAR(64) NOT NULL, --特征过滤字段
FEATFILT_ID                                       DECIMAL(10) NOT NULL, --特征过滤编号
CONSTRAINT ML_FEAT_FILT_COLUMN_PK PRIMARY KEY(FEATFILTC_ID)   );

--机器学习特征抽取表
DROP TABLE IF EXISTS ML_FEAT_EXTR ;
CREATE TABLE ML_FEAT_EXTR(
FEATEXTR_ID                                       DECIMAL(10) NOT NULL, --特征抽取编号
DV_COLUMN                                         VARCHAR(64) NOT NULL, --因变量字段
EXTR_METHOD                                       CHAR(1) NOT NULL, --抽取方式
K_NUMBER                                          DECIMAL(16) default 0 NOT NULL, --K的个数
PERCENT                                           DECIMAL(16,2) default 0 NOT NULL, --百分比
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NULL, --数据表信息编号
CONSTRAINT ML_FEAT_EXTR_PK PRIMARY KEY(FEATEXTR_ID)   );

--机器学习特征抽取自变量表
DROP TABLE IF EXISTS ML_FEAT_EXTR_IV ;
CREATE TABLE ML_FEAT_EXTR_IV(
FEATEXTR_IV_ID                                    DECIMAL(10) NOT NULL, --特征抽取自变量编号
IV_COLUMN                                         VARCHAR(64) NOT NULL, --自变量字段
FEATEXTR_ID                                       DECIMAL(10) NOT NULL, --特征抽取编号
CONSTRAINT ML_FEAT_EXTR_IV_PK PRIMARY KEY(FEATEXTR_IV_ID)   );

--机器学习降维表
DROP TABLE IF EXISTS ML_DREDUCTION ;
CREATE TABLE ML_DREDUCTION(
DIMEREDU_ID                                       DECIMAL(10) NOT NULL, --降维编号
KBTEST_IS_FLAG                                    CHAR(1) NULL, --KMOBartlets检验
SEREP_IS_FLAG                                     CHAR(1) NULL, --碎石图
DIMER_METHOD                                      CHAR(1) NOT NULL, --降维分析方法
EXTRFACTOR_NUM                                    DECIMAL(16) default 0 NULL, --提取因子数
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NULL, --数据表信息编号
CONSTRAINT ML_DREDUCTION_PK PRIMARY KEY(DIMEREDU_ID)   );

--机器学习降维字段表
DROP TABLE IF EXISTS ML_DREDUCTION_COLUMN ;
CREATE TABLE ML_DREDUCTION_COLUMN(
DRCOLUMN_ID                                       DECIMAL(10) NOT NULL, --降维字段编号
DIMER_COLUMN                                      VARCHAR(64) NOT NULL, --降维字段
DIMEREDU_ID                                       DECIMAL(10) NOT NULL, --降维编号
CONSTRAINT ML_DREDUCTION_COLUMN_PK PRIMARY KEY(DRCOLUMN_ID)   );

--机器学习穷举法特征搜索表
DROP TABLE IF EXISTS ML_EFSEARCH ;
CREATE TABLE ML_EFSEARCH(
EXHAFEATS_ID                                      DECIMAL(10) NOT NULL, --穷举法特征搜索编号
DV_COLUMN                                         VARCHAR(64) NOT NULL, --因变量字段
FEATMIN_NUM                                       DECIMAL(16) default 0 NULL, --最小特征个数
FEATLARG_NUM                                      DECIMAL(16) default 0 NULL, --最大特征个数
CROSSVERI_NUM                                     DECIMAL(16) default 0 NULL, --交叉验证数
ALGORITHM_TYPE                                    CHAR(1) NOT NULL, --算法类型
SPECIFIC_ALGORITHM                                CHAR(2) NOT NULL, --具体算法
ALGO_EVAL_CRIT                                    CHAR(2) NOT NULL, --算法评判标准
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NULL, --数据表信息编号
CONSTRAINT ML_EFSEARCH_PK PRIMARY KEY(EXHAFEATS_ID)   );

--机器学习穷举法特征搜索自变量表
DROP TABLE IF EXISTS ML_EFSEARCH_IV ;
CREATE TABLE ML_EFSEARCH_IV(
EXHAFS_IV_ID                                      DECIMAL(10) NOT NULL, --穷举法特征搜索自变量编号
IV_COLUMN                                         VARCHAR(64) NOT NULL, --自变量字段
EXHAFEATS_ID                                      DECIMAL(10) NOT NULL, --穷举法特征搜索编号
CONSTRAINT ML_EFSEARCH_IV_PK PRIMARY KEY(EXHAFS_IV_ID)   );

--角色信息表
DROP TABLE IF EXISTS SYS_ROLE ;
CREATE TABLE SYS_ROLE(
ROLE_ID                                           VARCHAR(32) NOT NULL, --角色ID
ROLE_NAME                                         VARCHAR(512) NOT NULL, --角色名称
ROLE_REMARK                                       VARCHAR(512) NULL, --备注
CONSTRAINT SYS_ROLE_PK PRIMARY KEY(ROLE_ID)   );

--机器学习随机森林分类模型表
DROP TABLE IF EXISTS ML_RAND_FORE_CM ;
CREATE TABLE ML_RAND_FORE_CM(
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
MODEL_NAME                                        VARCHAR(100) NOT NULL, --模型名称
DV_COLUMN                                         VARCHAR(64) NOT NULL, --因变量字段
TREE_NUM                                          DECIMAL(16) default 0 NOT NULL, --树的个数
RNUM_SEED                                         DECIMAL(16) default 0 NOT NULL, --随机种子
TREE_MAX_DEPTH                                    DECIMAL(16) default 0 NOT NULL, --树的最大深度
MODEL_RUNSTATE                                    CHAR(1) NOT NULL, --模型运行状态
NODEIMPMEAMET                                     CHAR(1) NOT NULL, --节点不纯度衡量方法
SEGMMAXINUMB                                      DECIMAL(16) default 0 NULL, --最大区段数
MODEL_PATH                                        VARCHAR(512) NULL, --模型地址
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_RAND_FORE_CM_PK PRIMARY KEY(MODEL_ID)   );

--机器学习随机森林自变量表
DROP TABLE IF EXISTS ML_RAND_FORE_IV ;
CREATE TABLE ML_RAND_FORE_IV(
IV_ID                                             DECIMAL(10) NOT NULL, --自变量编号
IV_COLUMN                                         VARCHAR(64) NOT NULL, --自变量字段
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
CONSTRAINT ML_RAND_FORE_IV_PK PRIMARY KEY(IV_ID)   );

--报表SQL条件详细分析表
DROP TABLE IF EXISTS R_COND_ANAL ;
CREATE TABLE R_COND_ANAL(
COND_ANAL_ID                                      DECIMAL(10) NOT NULL, --条件分析编号
SQL_ID                                            DECIMAL(10) NOT NULL, --SQL编号
SQL_COND_TYPE                                     CHAR(1) NOT NULL, --sql条件类型
COLUMN_NAME                                       VARCHAR(64) NOT NULL, --字段名
COLUMN_VALUE                                      VARCHAR(240) NOT NULL, --字段值
COLUMN_COND                                       VARCHAR(8) NOT NULL, --字段条件
INCIDENCE_RELA                                    VARCHAR(8) NOT NULL, --关联关系
CONSTRAINT R_COND_ANAL_PK PRIMARY KEY(COND_ANAL_ID)   );

--报表条件过滤表
DROP TABLE IF EXISTS R_COND_FILT ;
CREATE TABLE R_COND_FILT(
FILTER_ID                                         DECIMAL(10) NOT NULL, --过滤编号
REPORT_ID                                         DECIMAL(10) NOT NULL, --报表编号
CREATE_ID                                         DECIMAL(10) NOT NULL, --用户ID
COND_FILTER                                       CHAR(1) NOT NULL, --条件过滤方式
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CONSTRAINT R_COND_FILT_PK PRIMARY KEY(FILTER_ID)   );

--报表过滤结果表
DROP TABLE IF EXISTS R_FILTER_RESULT ;
CREATE TABLE R_FILTER_RESULT(
FILTERRESU_ID                                     DECIMAL(10) NOT NULL, --过滤结果编号
FILTER_ID                                         DECIMAL(10) NOT NULL, --过滤编号
COND_ANAL_ID                                      DECIMAL(10) NOT NULL, --条件分析编号
NEWCOLUMN_VALUE                                   VARCHAR(48) NOT NULL, --新字段值
NEWCOLUMN_COND                                    VARCHAR(8) NOT NULL, --新字段条件
NEWINCI_RELA                                      VARCHAR(8) NOT NULL, --新关联关系
CONSTRAINT R_FILTER_RESULT_PK PRIMARY KEY(FILTERRESU_ID)   );

--数据仓库字段信息
DROP TABLE IF EXISTS EDW_COLUMN ;
CREATE TABLE EDW_COLUMN(
COLNO                                             INTEGER default 0 NOT NULL, --字段序号
TABNAME                                           VARCHAR(512) NOT NULL, --表名称
TABLE_ID                                          DECIMAL(10) NOT NULL, --表id
ST_DT                                             CHAR(8) NOT NULL, --开始日期
ST_TIME                                           CHAR(6) NOT NULL, --开始时间
END_DT                                            CHAR(8) NOT NULL, --结束日期
COLNAME                                           VARCHAR(50) NOT NULL, --字段名称
CCOLNAME                                          VARCHAR(100) NULL, --字段中文名
COLTYPE                                           VARCHAR(20) NULL, --字段类型
IFPK                                              CHAR(1) NOT NULL, --是否主键
IFNULL                                            CHAR(1) NOT NULL, --是否允许为空
COLTRA                                            CHAR(1) NULL, --转换类型
COLFORMAT                                         VARCHAR(300) NULL, --字段格式
TRATYPE                                           VARCHAR(30) NULL, --转换后数据类型
COLUMN_SORT                                       DECIMAL(16) default 0 NULL, --字段排列顺序
IS_SORTCOLUMNS                                    CHAR(1) default '1' NOT NULL, --是否为cb的sortcolumns列
IS_DICCOL                                         CHAR(1) default '1' NOT NULL, --是否为cb的字段编码列
IS_FIRST_LEVEL_COL                                CHAR(1) default '1' NOT NULL, --是否是一级分区列
IS_SECOND_LEVEL_COL                               CHAR(1) default '1' NOT NULL, --是否是二级分区列
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT EDW_COLUMN_PK PRIMARY KEY(COLNO,TABNAME,TABLE_ID,ST_DT,ST_TIME)   );

--数据仓库表清单
DROP TABLE IF EXISTS EDW_TABLE ;
CREATE TABLE EDW_TABLE(
TABNAME                                           VARCHAR(512) NOT NULL, --表名称
TABLE_ID                                          DECIMAL(10) NOT NULL, --表id
ST_DT                                             CHAR(8) NOT NULL, --开始日期
ST_TIME                                           CHAR(6) NOT NULL, --开始时间
CTNAME                                            VARCHAR(512) NOT NULL, --表中文名
FROMSYS                                           VARCHAR(10) NULL, --系统来源
REMARK                                            VARCHAR(512) NULL, --备注
END_DT                                            CHAR(8) NULL, --结束日期
DATA_IN                                           CHAR(1) default '1' NOT NULL, --是否已加载数据
CATEGORY_ID                                       DECIMAL(10) NOT NULL, --模型分类id
CONSTRAINT EDW_TABLE_PK PRIMARY KEY(TABNAME,TABLE_ID,ST_DT,ST_TIME)   );

--数据仓库接口参数信息管理
DROP TABLE IF EXISTS EDW_INTERFACE_PARAMS ;
CREATE TABLE EDW_INTERFACE_PARAMS(
SRC_TABLE                                         VARCHAR(512) NOT NULL, --源表名
SRC_COLUMN                                        VARCHAR(100) NOT NULL, --源字段名
SRC_CD                                            VARCHAR(50) NOT NULL, --源代码
SRC_SYSTEM                                        VARCHAR(10) NOT NULL, --系统名称
SRC_DESC                                          VARCHAR(100) NULL, --源代码描述
DEST_TABLE                                        VARCHAR(1000) NULL, --目标表名
DEST_CD                                           VARCHAR(50) NULL, --目标代码
DEST_DESC                                         VARCHAR(100) NULL, --目标代码描述
CONSTRAINT EDW_INTERFACE_PARAMS_PK PRIMARY KEY(SRC_TABLE,SRC_COLUMN,SRC_CD)   );

--数据仓库接口信息管理
DROP TABLE IF EXISTS EDW_INTERFACE_INFO ;
CREATE TABLE EDW_INTERFACE_INFO(
JOBCODE                                           VARCHAR(10) NOT NULL, --作业编号
ST_DT                                             CHAR(8) NOT NULL, --开始日期
ST_TIME                                           CHAR(6) NOT NULL, --开始时间
SYSCODE                                           VARCHAR(5) NULL, --应用系统编号/来源系统编号
TABNAME                                           VARCHAR(512) NOT NULL, --表名
FILENAME                                          VARCHAR(100) NULL, --文件名
RLENGTH                                           INTEGER default 0 NULL, --单行长度
FLAGNAME                                          VARCHAR(100) NULL, --标示文件名
FILEMODE                                          CHAR(1) NULL, --文件模式
FILETYPE                                          CHAR(1) NULL, --文件类型
FILE_COD_FORMAT                                   VARCHAR(10) NULL, --文件编码格式
COL_COLDEL                                        VARCHAR(5) NULL, --分隔符
FILE_PATH                                         VARCHAR(100) NULL, --文件存储位置
FILE_TO_PATH                                      VARCHAR(100) NULL, --处理后存储位置
FILE_WHERE                                        VARCHAR(100) NULL, --增量条件
END_DT                                            CHAR(8) NULL, --结束日期
CONSTRAINT EDW_INTERFACE_INFO_PK PRIMARY KEY(JOBCODE,ST_DT,ST_TIME)   );

--模型作业信息表
DROP TABLE IF EXISTS EDW_JOB ;
CREATE TABLE EDW_JOB(
JOBCODE                                           VARCHAR(100) NOT NULL, --作业编号
JOBNAME                                           VARCHAR(100) NULL, --作业名称
JOBTYPE                                           VARCHAR(10) NULL, --作业类型
ALGORITHMCODE                                     CHAR(2) NOT NULL, --算法类型
OPER_CYCLE                                        VARCHAR(10) NULL, --作业执行周期
JOB_TEMPLATE_CODE                                 VARCHAR(50) NULL, --作业模板
JOB_STATE                                         CHAR(3) NULL, --作业运行状态
CONSTRAINT EDW_JOB_PK PRIMARY KEY(JOBCODE)   );

--表关联关系
DROP TABLE IF EXISTS EDW_TABLE_JOIN ;
CREATE TABLE EDW_TABLE_JOIN(
JOBCODE                                           VARCHAR(100) NOT NULL, --作业编号
SERIAL_NUM                                        INTEGER default 0 NOT NULL, --序号
ST_DT                                             CHAR(8) NOT NULL, --开始日期
ST_TIME                                           CHAR(6) NOT NULL, --开始时间
TABNAME                                           VARCHAR(512) NOT NULL, --表名
TABALIAS                                          VARCHAR(100) NOT NULL, --表别称
JOINTYPE                                          VARCHAR(15) NULL, --关联方式
JOIN_CONDITION                                    VARCHAR(1000) NULL, --关联规则
END_DT                                            CHAR(8) NULL, --结束日期
USERCODE                                          VARCHAR(20) NULL, --用户编号
CONSTRAINT EDW_TABLE_JOIN_PK PRIMARY KEY(JOBCODE,SERIAL_NUM,ST_DT,ST_TIME)   );

--数据映射关系临时表
DROP TABLE IF EXISTS EDW_MAPPING_TEMP ;
CREATE TABLE EDW_MAPPING_TEMP(
TMP_ID                                            DECIMAL(10) NOT NULL, --临时表id
COLNAME                                           VARCHAR(100) NOT NULL, --字段名称
ST_DT                                             CHAR(8) NOT NULL, --开始日期
ST_TIME                                           CHAR(6) NOT NULL, --开始时间
JOBNAME                                           VARCHAR(512) NOT NULL, --源表名
TABALIAS                                          VARCHAR(100) NOT NULL, --表别名
TABNAME                                           VARCHAR(512) NOT NULL, --表名称
SOURETABNAME                                      VARCHAR(512) NOT NULL, --源表名称
SOURECOLVALUE                                     VARCHAR(100) NULL, --源表字段名称
PROCESSTYPE                                       VARCHAR(15) NULL, --处理方式
MAPPING                                           VARCHAR(1000) NULL, --映射规则
END_DT                                            CHAR(8) NULL, --结束日期
USERCODE                                          VARCHAR(20) NULL, --用户编号
REMARK                                            VARCHAR(1000) NULL, --备注
IS_ADD                                            CHAR(1) NOT NULL, --是否新增
CONSTRAINT EDW_MAPPING_TEMP_PK PRIMARY KEY(TMP_ID)   );

--表关联关系临时表
DROP TABLE IF EXISTS EDW_TABLE_JOIN_TEMP ;
CREATE TABLE EDW_TABLE_JOIN_TEMP(
JOBCODE                                           VARCHAR(100) NOT NULL, --作业编号
SERIAL_NUM                                        INTEGER default 0 NOT NULL, --序号
ST_DT                                             CHAR(8) NOT NULL, --开始日期
ST_TIME                                           CHAR(6) NOT NULL, --开始时间
TABNAME                                           VARCHAR(512) NOT NULL, --表名
TABALIAS                                          VARCHAR(5) NULL, --表别称
JOINTYPE                                          VARCHAR(15) NULL, --关联方式
JOIN_CONDITION                                    VARCHAR(1000) NULL, --关联规则
END_DT                                            CHAR(8) NULL, --结束日期
USERCODE                                          VARCHAR(20) NULL, --用户编号
CONSTRAINT EDW_TABLE_JOIN_TEMP_PK PRIMARY KEY(JOBCODE,SERIAL_NUM,ST_DT,ST_TIME)   );

--翻译基础表
DROP TABLE IF EXISTS TRANSLATE_BASE ;
CREATE TABLE TRANSLATE_BASE(
TRN_ID                                            DECIMAL(10) NOT NULL, --翻译id
CHINA_NAME                                        VARCHAR(50) NOT NULL, --名称中文
ENGLISH_NAME                                      VARCHAR(50) NULL, --名词英文
ENG_NAME                                          VARCHAR(50) NULL, --名词英文简称
CONSTRAINT TRANSLATE_BASE_PK PRIMARY KEY(TRN_ID)   );

--数据映射关系表
DROP TABLE IF EXISTS EDW_MAPPING ;
CREATE TABLE EDW_MAPPING(
MAPPING_JOB_ID                                    DECIMAL(10) NOT NULL, --映射作业ID
JOBNAME                                           VARCHAR(100) NOT NULL, --作业名称
TABNAME                                           VARCHAR(512) NOT NULL, --模型表名称
COLNAME                                           VARCHAR(100) NOT NULL, --模型字段名称
ST_DT                                             CHAR(8) NOT NULL, --模型开始日期
ST_TIME                                           CHAR(6) NOT NULL, --模型开始时间
SOURETABNAME                                      VARCHAR(512) NOT NULL, --源表名称
TABALIAS                                          VARCHAR(100) NOT NULL, --表别名
SOURECOLVALUE                                     VARCHAR(100) NULL, --源表字段名称
PROCESSTYPE                                       VARCHAR(15) NULL, --处理方式
MAPPING                                           VARCHAR(1000) NULL, --映射规则
END_DT                                            CHAR(8) NULL, --结束日期
USERCODE                                          VARCHAR(20) NULL, --用户编号
REMARK                                            VARCHAR(1000) NULL, --备注
CONSTRAINT EDW_MAPPING_PK PRIMARY KEY(MAPPING_JOB_ID,JOBNAME,TABNAME,COLNAME,ST_DT,ST_TIME)   );

--仓库脚本算法
DROP TABLE IF EXISTS EDW_JOB_ALG ;
CREATE TABLE EDW_JOB_ALG(
ALGORITHMCODE                                     CHAR(2) NOT NULL, --算法编号
ALGORITHMNAME                                     VARCHAR(30) NULL, --算法名称
CONSTRAINT EDW_JOB_ALG_PK PRIMARY KEY(ALGORITHMCODE)   );

--模型工程
DROP TABLE IF EXISTS EDW_MODAL_PROJECT ;
CREATE TABLE EDW_MODAL_PROJECT(
MODAL_PRO_ID                                      DECIMAL(10) NOT NULL, --模型工程id
PRO_NAME                                          VARCHAR(512) NOT NULL, --工程名称
PRO_DESC                                          VARCHAR(200) NULL, --工程描述
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CREATE_ID                                         DECIMAL(10) NOT NULL, --创建用户
PRO_NUMBER                                        VARCHAR(512) NOT NULL, --工程编号
CONSTRAINT EDW_MODAL_PROJECT_PK PRIMARY KEY(MODAL_PRO_ID)   );

--SQL记录表
DROP TABLE IF EXISTS SQL_RECORD_TABLE ;
CREATE TABLE SQL_RECORD_TABLE(
SQL_ID                                            DECIMAL(10) NOT NULL, --SQL编号
CREATE_ID                                         DECIMAL(10) NOT NULL, --用户ID
SQL_NAME                                          VARCHAR(100) NULL, --sql别名
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
UPDATE_DATE                                       CHAR(8) NOT NULL, --更新日期
UPDATE_TIME                                       CHAR(6) NOT NULL, --更新时间
SQL_DETAILS                                       VARCHAR(1024) NOT NULL, --执行的sql语句
SQL_ORIGINAL                                      VARCHAR(1024) NULL, --原始的sql语句
CLEAN_SQL                                         VARCHAR(240) NULL, --无条件的sql语句
SELECT_AS_COLUMN                                  VARCHAR(240) NULL, --字段别名
SELECT_ANALYSE                                    VARCHAR(240) NULL, --sql选择分析结果
TABLE_ANALYSE                                     VARCHAR(240) NULL, --sql表分析结果
CONDITION_ANALYSE                                 VARCHAR(240) NULL, --sql条件分析结果
GROUPBY_ANALYSE                                   VARCHAR(240) NULL, --sql分组分析结果
ORDERBY_ANALYSE                                   VARCHAR(240) NULL, --sql排序分析结果
JOIN_ANALYSE                                      VARCHAR(240) NULL, --sql连接分析结果
LIMIT_ANALYSE                                     VARCHAR(240) NULL, --sql限数分析结果
CONSTRAINT SQL_RECORD_TABLE_PK PRIMARY KEY(SQL_ID)   );

--solr数据关联表
DROP TABLE IF EXISTS SOLR_DATA_RELATION ;
CREATE TABLE SOLR_DATA_RELATION(
FIELD_NAME                                        VARCHAR(128) NOT NULL, --字段名字
CONSTRAINT SOLR_DATA_RELATION_PK PRIMARY KEY(FIELD_NAME)   );

--机器学习决策树分类模型表
DROP TABLE IF EXISTS ML_DECTREECLASMODE ;
CREATE TABLE ML_DECTREECLASMODE(
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
MODEL_NAME                                        VARCHAR(100) NOT NULL, --模型名称
DV_COLUMN                                         VARCHAR(64) NOT NULL, --因变量字段
NODEIMPMEAMET                                     CHAR(1) NOT NULL, --节点不纯度测量方法
TREE_MAX_DEPTH                                    DECIMAL(16) default 0 NOT NULL, --树的最大深度
SEGMMAXINUMB                                      DECIMAL(16) default 0 NULL, --最大区段数
MODEL_RUNSTATE                                    CHAR(1) NOT NULL, --模型运行状态
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
MODEL_PATH                                        VARCHAR(512) NULL, --模型地址
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_DECTREECLASMODE_PK PRIMARY KEY(MODEL_ID)   );

--机器学习决策树自变量表
DROP TABLE IF EXISTS ML_DECTREECLASM_IV ;
CREATE TABLE ML_DECTREECLASM_IV(
IV_ID                                             DECIMAL(10) NOT NULL, --自变量编号
IV_COLUMN                                         VARCHAR(64) NOT NULL, --自变量字段
CLASSCOLU_ISFLAG                                  CHAR(1) NOT NULL, --是否为分类字段
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
CONSTRAINT ML_DECTREECLASM_IV_PK PRIMARY KEY(IV_ID)   );

--机器学习逻辑回归分类模型表
DROP TABLE IF EXISTS ML_LOGIREGRCLASMODE ;
CREATE TABLE ML_LOGIREGRCLASMODE(
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
MODEL_NAME                                        VARCHAR(100) NOT NULL, --模型名称
DV_COLUMN                                         VARCHAR(64) NOT NULL, --因变量字段
NUMITERATIONS                                     DECIMAL(16) default 0 NOT NULL, --迭代次数
STEPSIZE                                          DECIMAL(16,2) default 0 NOT NULL, --SGD步长
MINIBATCHFRACTION                                 DECIMAL(16,2) default 0 NOT NULL, --迭代训练样本比例
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
MODEL_RUNSTATE                                    CHAR(1) NOT NULL, --模型运行状态
MODEL_PATH                                        VARCHAR(512) NULL, --模型地址
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_LOGIREGRCLASMODE_PK PRIMARY KEY(MODEL_ID)   );

--机器学习逻辑回归分类模型自变量表
DROP TABLE IF EXISTS ML_LOGIREGRCLASM_IV ;
CREATE TABLE ML_LOGIREGRCLASM_IV(
IV_ID                                             DECIMAL(10) NOT NULL, --自变量编号
IV_COLUMN                                         VARCHAR(64) NOT NULL, --自变量字段
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
CONSTRAINT ML_LOGIREGRCLASM_IV_PK PRIMARY KEY(IV_ID)   );

--机器学习支持向量机分类模型表
DROP TABLE IF EXISTS ML_SVMCLASSI ;
CREATE TABLE ML_SVMCLASSI(
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
MODEL_NAME                                        VARCHAR(100) NOT NULL, --模型名称
DV_COLUMN                                         VARCHAR(64) NOT NULL, --因变量字段
NUMITERATIONS                                     DECIMAL(16) default 0 NOT NULL, --迭代次数
STEPSIZE                                          DECIMAL(16,2) default 0 NOT NULL, --SGD步长
MINIBATCHFRACTION                                 DECIMAL(16,2) default 0 NOT NULL, --迭代训练样本比例
REGPARAM                                          DECIMAL(16,2) default 0 NOT NULL, --正则化参数
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
MODEL_RUNSTATE                                    CHAR(1) NOT NULL, --模型运行状态
MODEL_PATH                                        VARCHAR(512) NULL, --模型地址
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_SVMCLASSI_PK PRIMARY KEY(MODEL_ID)   );

--机器学习支持向量机分类模型自变量表
DROP TABLE IF EXISTS ML_SVMCLASSI_IV ;
CREATE TABLE ML_SVMCLASSI_IV(
IV_ID                                             DECIMAL(10) NOT NULL, --自变量编号
IV_COLUMN                                         VARCHAR(64) NOT NULL, --自变量字段
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
CONSTRAINT ML_SVMCLASSI_IV_PK PRIMARY KEY(IV_ID)   );

--机器学习朴素贝叶斯模型表
DROP TABLE IF EXISTS ML_NBMODEL ;
CREATE TABLE ML_NBMODEL(
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
MODEL_NAME                                        VARCHAR(100) NOT NULL, --模型名称
DV_COLUMN                                         VARCHAR(64) NOT NULL, --因变量字段
LAMBDA                                            DECIMAL(16,2) default 0 NOT NULL, --平滑参数
NBMODEL_TYPE                                      CHAR(1) NOT NULL, --朴素贝叶斯模型
MODEL_RUNSTATE                                    CHAR(1) NOT NULL, --模型运行状态
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
MODEL_PATH                                        VARCHAR(512) NULL, --模型地址
REMARK                                            VARCHAR(512) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_NBMODEL_PK PRIMARY KEY(MODEL_ID)   );

--机器学习朴素贝叶斯模型自变量表
DROP TABLE IF EXISTS ML_NBMODEL_IV ;
CREATE TABLE ML_NBMODEL_IV(
IV_ID                                             DECIMAL(10) NOT NULL, --自变量编号
IV_COLUMN                                         VARCHAR(64) NOT NULL, --自变量字段
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
CONSTRAINT ML_NBMODEL_IV_PK PRIMARY KEY(IV_ID)   );

--机器学习模型预测信息表
DROP TABLE IF EXISTS ML_MODELPREDICTINFO ;
CREATE TABLE ML_MODELPREDICTINFO(
PREDICTINFO_ID                                    DECIMAL(10) NOT NULL, --预测信息编号
MODEL_TYPE                                        CHAR(1) NOT NULL, --模型类型
SPECIFICMODEL                                     CHAR(2) NOT NULL, --具体模型
PREDICT_STATUS                                    CHAR(1) NOT NULL, --模型预测状态
MODEL_PATH                                        VARCHAR(512) NOT NULL, --模型路径
PREDICT_PATH                                      VARCHAR(512) NOT NULL, --预测结果路径
FILENAME                                          VARCHAR(80) NOT NULL, --结果文件名称
PREDICTSTRATDATE                                  CHAR(8) NOT NULL, --预测开始日期
PREDICTSTARTTIME                                  CHAR(6) NOT NULL, --预测开始时间
PREDICTENDTIME                                    CHAR(6) NOT NULL, --预测结束时间
PROJECT_ID                                        DECIMAL(10) NOT NULL, --项目编号
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT ML_MODELPREDICTINFO_PK PRIMARY KEY(PREDICTINFO_ID)   );

--流数据管理Agent信息表
DROP TABLE IF EXISTS SDM_AGENT_INFO ;
CREATE TABLE SDM_AGENT_INFO(
SDM_AGENT_ID                                      DECIMAL(10) NOT NULL, --流数据管理agent_id
SDM_AGENT_STATUS                                  CHAR(1) NOT NULL, --流数据管理agent状态
SDM_AGENT_NAME                                    VARCHAR(512) NOT NULL, --流数据管理agent名称
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
SDM_AGENT_TYPE                                    CHAR(1) NOT NULL, --流数据管理agent类别
SDM_AGENT_IP                                      VARCHAR(50) NOT NULL, --流数据管理agent所在服务器ip
SDM_AGENT_PORT                                    VARCHAR(10) NOT NULL, --流数据管理agent服务器端口
REMARK                                            VARCHAR(512) NULL, --备注
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CREATE_ID                                         DECIMAL(10) NOT NULL, --创建用户ID
SDM_SOURCE_ID                                     DECIMAL(10) NULL, --数据源ID
CONSTRAINT SDM_AGENT_INFO_PK PRIMARY KEY(SDM_AGENT_ID)   );

--流数据管理接收端配置表
DROP TABLE IF EXISTS SDM_RECEIVE_CONF ;
CREATE TABLE SDM_RECEIVE_CONF(
SDM_RECEIVE_ID                                    DECIMAL(10) NOT NULL, --流数据管理
SDM_RECEIVE_NAME                                  VARCHAR(512) NOT NULL, --任务名称
SDM_REC_DES                                       VARCHAR(200) NULL, --任务描述
SDM_REC_PORT                                      VARCHAR(10) NULL, --流数据管理接收端口号
RA_FILE_PATH                                      VARCHAR(1536) NULL, --文本流文件路径
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
SDM_PARTITION                                     CHAR(1) NOT NULL, --分区方式
SDM_PARTITION_NAME                                VARCHAR(512) NULL, --分区方式类名
FILE_HANDLE                                       VARCHAR(512) NULL, --文件处理类
CODE                                              CHAR(1) NULL, --编码
FILE_READ_NUM                                     VARCHAR(200) NULL, --自定义读取行数
FILE_INITPOSITION                                 VARCHAR(2) NULL, --文件初始位置
IS_FILE_ATTR_IP                                   CHAR(1) default '1' NOT NULL, --是否包含文件属性（所在主机ip）
IS_FULL_PATH                                      CHAR(1) default '1' NOT NULL, --是否包含文件属性（全路径）
IS_FILE_NAME                                      CHAR(1) default '1' NOT NULL, --是否包含文件属性（文件名）
IS_FILE_TIME                                      CHAR(1) default '1' NOT NULL, --是否包含文件时间
IS_FILE_SIZE                                      CHAR(1) default '1' NOT NULL, --是否包含文件大小
READ_MODE                                         CHAR(1) default '1' NOT NULL, --对象采集方式
READ_TYPE                                         CHAR(1) default '0' NOT NULL, --读取方式
MONITOR_TYPE                                      CHAR(1) default '1' NOT NULL, --监听类型
THREAD_NUM                                        DECIMAL(16) default 0 NULL, --线程数
FILE_MATCH_RULE                                   VARCHAR(50) NULL, --文件匹配规则
SDM_BUS_PRO_CLA                                   VARCHAR(2000) NULL, --自定义业务类
CUS_DES_TYPE                                      CHAR(1) NOT NULL, --自定义业务类类型
IS_DATA_PARTITION                                 CHAR(1) default '1' NOT NULL, --是否行数据分割
IS_OBJ                                            CHAR(1) default '1' NOT NULL, --是否包含对象属性
SDM_DAT_DELIMITER                                 VARCHAR(200) NULL, --流数据管理数据分割符
MSGTYPE                                           VARCHAR(1) NULL, --消息类型
MSGHEADER                                         VARCHAR(200) NULL, --消息头
FILE_READTYPE                                     VARCHAR(512) NULL, --文本读取方式
REMARK                                            VARCHAR(512) NULL, --备注
SDM_EMAIL                                         VARCHAR(100) NULL, --警告发送email
CHECK_CYCLE                                       INTEGER default 0 NULL, --警告发送周期
SNMP_IP                                           VARCHAR(100) NULL, --snmp主机p
FAULT_ALARM_MODE                                  VARCHAR(200) NULL, --错误提醒方式
IS_LINE_NUM                                       CHAR(1) default '1' NOT NULL, --是否包含文件行数
RUN_WAY                                           CHAR(1) NOT NULL, --启动方式
SDM_AGENT_ID                                      DECIMAL(10) NULL, --流数据管理agent_id
CONSTRAINT SDM_RECEIVE_CONF_PK PRIMARY KEY(SDM_RECEIVE_ID)   );

--流数据管理消息信息表
DROP TABLE IF EXISTS SDM_MESS_INFO ;
CREATE TABLE SDM_MESS_INFO(
MESS_INFO_ID                                      DECIMAL(10) NOT NULL, --mess_info_id
SDM_VAR_NAME_EN                                   VARCHAR(512) NOT NULL, --英文变量名
SDM_VAR_NAME_CN                                   VARCHAR(512) NOT NULL, --中文变量名
SDM_DESCRIBE                                      VARCHAR(200) NULL, --含义
SDM_VAR_TYPE                                      CHAR(1) NOT NULL, --变量类型
SDM_IS_SEND                                       CHAR(1) NOT NULL, --是否发送
NUM                                               VARCHAR(100) NOT NULL, --变量序号
REMARK                                            VARCHAR(512) NULL, --备注
SDM_RECEIVE_ID                                    DECIMAL(10) NULL, --流数据管理
CONSTRAINT SDM_MESS_INFO_PK PRIMARY KEY(MESS_INFO_ID)   );

--流数据管理接收参数表
DROP TABLE IF EXISTS SDM_REC_PARAM ;
CREATE TABLE SDM_REC_PARAM(
REC_PARAM_ID                                      DECIMAL(10) NOT NULL, --rec_param_id
SDM_PARAM_KEY                                     VARCHAR(200) NOT NULL, --接收端参数key值
SDM_PARAM_VALUE                                   VARCHAR(200) NOT NULL, --接收端参数value值
SDM_RECEIVE_ID                                    DECIMAL(10) NULL, --流数据管理
CONSTRAINT SDM_REC_PARAM_PK PRIMARY KEY(REC_PARAM_ID)   );

--流数据管理消费端配置表
DROP TABLE IF EXISTS SDM_CONSUME_CONF ;
CREATE TABLE SDM_CONSUME_CONF(
SDM_CONSUM_ID                                     DECIMAL(10) NOT NULL, --消费端配置id
SDM_CONS_NAME                                     VARCHAR(100) NOT NULL, --消费配置名称
SDM_CONS_DESCRIBE                                 VARCHAR(200) NULL, --消费配置描述
CON_WITH_PAR                                      CHAR(1) default '1' NOT NULL, --是否按分区消费
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CONSUM_THREAD_CYCLE                               CHAR(1) NULL, --消费线程周期
DEADLINE                                          VARCHAR(20) NULL, --截止时间
RUN_TIME_LONG                                     DECIMAL(16) default 0 NULL, --运行时长
END_TYPE                                          CHAR(1) default '1' NULL, --结束类型
DATA_VOLUME                                       DECIMAL(16) default 0 NULL, --数据量
TIME_TYPE                                         CHAR(1) NULL, --时间类型
CONSUMER_TYPE                                     CHAR(1) NOT NULL, --消费类型
REMARK                                            VARCHAR(512) NULL, --备注
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT SDM_CONSUME_CONF_PK PRIMARY KEY(SDM_CONSUM_ID)   );

--流数据管理消费端参数表
DROP TABLE IF EXISTS SDM_CONS_PARA ;
CREATE TABLE SDM_CONS_PARA(
SDM_CONF_PARA_ID                                  DECIMAL(10) NOT NULL, --sdm_conf_para_id
SDM_CONF_PARA_NA                                  VARCHAR(100) NOT NULL, --参数名称
REMARK                                            VARCHAR(512) NULL, --备注
SDM_CONS_PARA_VAL                                 VARCHAR(100) NOT NULL, --参数值
SDM_CONSUM_ID                                     DECIMAL(10) NULL, --消费端配置id
CONSTRAINT SDM_CONS_PARA_PK PRIMARY KEY(SDM_CONF_PARA_ID)   );

--流数据管理数据源
DROP TABLE IF EXISTS SDM_DATA_SOURCE ;
CREATE TABLE SDM_DATA_SOURCE(
SDM_SOURCE_ID                                     DECIMAL(10) NOT NULL, --数据源ID
SDM_SOURCE_NUMBER                                 VARCHAR(100) NULL, --数据源编号
SDM_SOURCE_NAME                                   VARCHAR(512) NOT NULL, --数据源名称
SDM_SOURCE_DES                                    VARCHAR(512) NOT NULL, --数据源详细描述
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT SDM_DATA_SOURCE_PK PRIMARY KEY(SDM_SOURCE_ID)   );

--流数据管理topic信息表
DROP TABLE IF EXISTS SDM_TOPIC_INFO ;
CREATE TABLE SDM_TOPIC_INFO(
TOPIC_ID                                          DECIMAL(10) NOT NULL, --topic_id
SDM_TOP_NAME                                      VARCHAR(512) NOT NULL, --topic英文名称
SDM_TOP_CN_NAME                                   VARCHAR(512) NOT NULL, --topic中文名称
SDM_TOP_VALUE                                     VARCHAR(512) NULL, --topic描述
SDM_ZK_HOST                                       VARCHAR(512) NOT NULL, --ZK主机
SDM_PARTITION                                     DECIMAL(16) default 0 NOT NULL, --分区数
SDM_REPLICATION                                   DECIMAL(16) default 0 NOT NULL, --副本值个数
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT SDM_TOPIC_INFO_PK PRIMARY KEY(TOPIC_ID)   );

--流数据管理消费至数据库表
DROP TABLE IF EXISTS SDM_CON_TO_DB ;
CREATE TABLE SDM_CON_TO_DB(
SDM_CON_DB_ID                                     DECIMAL(10) NOT NULL, --数据库设置id
DB_BUS_CLASS                                      VARCHAR(200) NULL, --数据库业务处理类
DB_BUS_TYPE                                       CHAR(1) NOT NULL, --数据库业务类类型
SDM_DB_NUM                                        VARCHAR(100) NULL, --数据库设置编号
SDM_SYS_TYPE                                      VARCHAR(512) NULL, --操作系统类型
SDM_DB_NAME                                       VARCHAR(512) NULL, --数据库名称
SDM_TB_NAME_EN                                    VARCHAR(512) NOT NULL, --英文表名
SDM_TB_NAME_CN                                    VARCHAR(512) NOT NULL, --中文表名
SDM_DB_PWD                                        VARCHAR(100) NULL, --数据库密码
SDM_DB_DRIVER                                     VARCHAR(512) NULL, --数据库驱动
SDM_DB_TYPE                                       CHAR(2) NULL, --数据库类型
SDM_DB_USER                                       VARCHAR(512) NULL, --用户名称
SDM_DB_IP                                         VARCHAR(50) NULL, --数据库服务器IP
SDM_DB_PORT                                       VARCHAR(10) NULL, --数据库端口
SDM_DB_CODE                                       CHAR(1) NULL, --数据使用编码格式
REMARK                                            VARCHAR(512) NULL, --备注
SDM_CONSUM_ID                                     DECIMAL(10) NULL, --消费端配置id
SDM_DES_ID                                        DECIMAL(10) NOT NULL, --配置id
CONSTRAINT SDM_CON_TO_DB_PK PRIMARY KEY(SDM_CON_DB_ID)   );

--流数据管理消费字段表
DROP TABLE IF EXISTS SDM_CON_DB_COL ;
CREATE TABLE SDM_CON_DB_COL(
SDM_COL_ID                                        DECIMAL(10) NOT NULL, --sdm_col_id
CONSUMER_ID                                       DECIMAL(10) NOT NULL, --数据库设置id
SDM_COL_NAME_EN                                   VARCHAR(512) NOT NULL, --英文字段名
SDM_COL_NAME_CN                                   VARCHAR(512) NOT NULL, --中文字段名
SDM_DESCRIBE                                      VARCHAR(200) NULL, --含义
SDM_VAR_TYPE                                      CHAR(1) NOT NULL, --变量类型
IS_ROWKEY                                         CHAR(1) default '1' NOT NULL, --是否是rowkey
ROWKEY_SEQ                                        DECIMAL(16) default 0 NULL, --rowkey序号
SDM_RECEIVE_ID                                    DECIMAL(10) NULL, --流数据管理receive_id
NUM                                               DECIMAL(16) default 0 NOT NULL, --序号
IS_SEND                                           CHAR(1) default '1' NOT NULL, --是否发送
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT SDM_CON_DB_COL_PK PRIMARY KEY(SDM_COL_ID)   );

--数据仓库字段标准化表
DROP TABLE IF EXISTS EDW_STANDARD ;
CREATE TABLE EDW_STANDARD(
TABLE_ID                                          DECIMAL(10) NOT NULL, --表名ID
COLNAME                                           VARCHAR(512) NOT NULL, --列名
COLTRA                                            CHAR(1) NULL, --转换类型
TRATYPE                                           CHAR(30) NULL, --转换后数据类型
COLFORMAT                                         CHAR(300) NULL, --字段格式
CONSTRAINT EDW_STANDARD_PK PRIMARY KEY(TABLE_ID,COLNAME)   );

--作业定义表
DROP TABLE IF EXISTS ETL_JOB_DEF ;
CREATE TABLE ETL_JOB_DEF(
ETL_SYS_CD                                        VARCHAR(100) NOT NULL, --工程代码
ETL_JOB                                           VARCHAR(512) NOT NULL, --作业名
SUB_SYS_CD                                        VARCHAR(100) NOT NULL, --子系统代码
ETL_JOB_DESC                                      VARCHAR(200) NULL, --作业描述
PRO_TYPE                                          VARCHAR(50) NOT NULL, --作业程序类型
PRO_DIC                                           VARCHAR(512) NULL, --作业程序目录
PRO_NAME                                          VARCHAR(512) NULL, --作业程序名称
PRO_PARA                                          VARCHAR(1000) NULL, --作业程序参数
LOG_DIC                                           VARCHAR(512) NULL, --日志目录
DISP_FREQ                                         CHAR(1) NULL, --调度频率
DISP_OFFSET                                       INTEGER default 0 NULL, --调度时间位移
DISP_TYPE                                         CHAR(1) NULL, --调度触发方式
DISP_TIME                                         VARCHAR(30) NULL, --调度触发时间
JOB_EFF_FLAG                                      CHAR(1) NULL, --作业有效标志
JOB_PRIORITY                                      INTEGER default 0 NULL, --作业优先级
JOB_DISP_STATUS                                   CHAR(1) NULL, --作业调度状态
CURR_ST_TIME                                      VARCHAR(30) NULL, --开始时间
CURR_END_TIME                                     VARCHAR(30) NULL, --结束时间
OVERLENGTH_VAL                                    INTEGER default 0 NULL, --超长阀值
OVERTIME_VAL                                      INTEGER default 0 NULL, --超时阀值
CURR_BATH_DATE                                    VARCHAR(30) NULL, --当前批量日期
COMMENTS                                          VARCHAR(512) NULL, --备注信息
TODAY_DISP                                        CHAR(1) NULL, --当天是否调度
MAIN_SERV_SYNC                                    CHAR(1) NULL, --主服务器同步标志
JOB_PROCESS_ID                                    VARCHAR(100) NULL, --作业进程号
JOB_PRIORITY_CURR                                 INTEGER default 0 NULL, --作业当前优先级
JOB_RETURN_VAL                                    INTEGER default 0 NULL, --作业返回值
UPD_TIME                                          VARCHAR(50) NULL, --更新日期
EXE_FREQUENCY                                     DECIMAL(16) default 0 NULL, --每隔(分钟)执行
EXE_NUM                                           INTEGER default 0 NULL, --执行次数
COM_EXE_NUM                                       INTEGER default 0 NULL, --已经执行次数
LAST_EXE_TIME                                     VARCHAR(20) NULL, --上次执行时间
STAR_TIME                                         VARCHAR(20) NULL, --开始执行时间
END_TIME                                          VARCHAR(20) NULL, --结束执行时间
CONSTRAINT ETL_JOB_DEF_PK PRIMARY KEY(ETL_SYS_CD,ETL_JOB)   );

--用户信息表
DROP TABLE IF EXISTS SYS_USER ;
CREATE TABLE SYS_USER(
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CREATE_ID                                         DECIMAL(10) NOT NULL, --建立用户ID
DEP_ID                                            DECIMAL(10) NULL, --部门ID
ROLE_ID                                           VARCHAR(32) NOT NULL, --角色ID
USER_NAME                                         VARCHAR(512) NOT NULL, --用户名称
USER_PASSWORD                                     VARCHAR(100) NOT NULL, --用户密码
USER_EMAIL                                        VARCHAR(100) NULL, --邮箱
USER_MOBILE                                       VARCHAR(20) NULL, --移动电话
USERIS_ADMIN                                      CHAR(1) default '1' NOT NULL, --是否为管理员
USER_TYPE                                         CHAR(2) NULL, --用户类型
USERTYPE_GROUP                                    VARCHAR(512) NULL, --用户类型组
LOGIN_IP                                          VARCHAR(50) NULL, --登录IP
LOGIN_DATE                                        CHAR(8) NULL, --最后登录时间
USER_STATE                                        CHAR(1) NOT NULL, --用户状态
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NULL, --创建时间
UPDATE_DATE                                       CHAR(8) NULL, --更新日期
UPDATE_TIME                                       CHAR(6) NULL, --更新时间
USER_REMARK                                       VARCHAR(512) NULL, --备注
MAXIMUM_JOB                                       DECIMAL(16) default 0 NULL, --最大作业数
USER_PRIORITY                                     CHAR(1) NULL, --优先级
QUOTA_SPACE                                       VARCHAR(200) NULL, --配额空间
TOKEN                                             VARCHAR(40) default '0' NOT NULL, --token
VALID_TIME                                        VARCHAR(40) default '0' NOT NULL, --token有效时间
CONSTRAINT SYS_USER_PK PRIMARY KEY(USER_ID)   );

--作业调度表
DROP TABLE IF EXISTS ETL_JOB_CUR ;
CREATE TABLE ETL_JOB_CUR(
ETL_SYS_CD                                        VARCHAR(100) NOT NULL, --工程代码
ETL_JOB                                           VARCHAR(512) NOT NULL, --作业名
SUB_SYS_CD                                        VARCHAR(100) NOT NULL, --子系统代码
ETL_JOB_DESC                                      VARCHAR(200) NULL, --作业描述
PRO_TYPE                                          VARCHAR(50) NULL, --作业程序类型
PRO_DIC                                           VARCHAR(512) NULL, --作业程序目录
PRO_NAME                                          VARCHAR(512) NULL, --作业程序名称
PRO_PARA                                          VARCHAR(1000) NULL, --作业程序参数
LOG_DIC                                           VARCHAR(512) NULL, --日志目录
DISP_FREQ                                         CHAR(1) NULL, --调度频率
DISP_OFFSET                                       INTEGER default 0 NULL, --调度时间位移
DISP_TYPE                                         CHAR(1) NULL, --调度触发方式
DISP_TIME                                         VARCHAR(30) NULL, --调度触发时间
JOB_EFF_FLAG                                      CHAR(1) NULL, --作业有效标志
JOB_PRIORITY                                      INTEGER default 0 NULL, --作业优先级
JOB_DISP_STATUS                                   CHAR(1) NULL, --作业调度状态
CURR_ST_TIME                                      VARCHAR(30) NULL, --开始时间
CURR_END_TIME                                     VARCHAR(30) NULL, --结束时间
OVERLENGTH_VAL                                    INTEGER default 0 NULL, --超长阀值
OVERTIME_VAL                                      INTEGER default 0 NULL, --超时阀值
CURR_BATH_DATE                                    VARCHAR(30) NULL, --当前批量日期
COMMENTS                                          VARCHAR(512) NULL, --备注信息
TODAY_DISP                                        CHAR(1) NULL, --当天是否调度
MAIN_SERV_SYNC                                    CHAR(1) NULL, --主服务器同步标志
JOB_PROCESS_ID                                    VARCHAR(100) NULL, --作业进程号
JOB_PRIORITY_CURR                                 INTEGER default 0 NULL, --作业当前优先级
JOB_RETURN_VAL                                    INTEGER default 0 NULL, --作业返回值
EXE_FREQUENCY                                     DECIMAL(16) default 0 NULL, --每隔(分钟)执行
EXE_NUM                                           INTEGER default 0 NULL, --执行次数
COM_EXE_NUM                                       INTEGER default 0 NULL, --已经执行次数
LAST_EXE_TIME                                     VARCHAR(20) NULL, --上次执行时间
STAR_TIME                                         VARCHAR(20) NULL, --开始执行时间
END_TIME                                          VARCHAR(20) NULL, --结束执行时间
CONSTRAINT ETL_JOB_CUR_PK PRIMARY KEY(ETL_SYS_CD,ETL_JOB)   );

--部门信息表
DROP TABLE IF EXISTS DEPARTMENT_INFO ;
CREATE TABLE DEPARTMENT_INFO(
DEP_ID                                            DECIMAL(10) NOT NULL, --部门ID
DEP_NAME                                          VARCHAR(512) NOT NULL, --部门名称
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
DEP_REMARK                                        VARCHAR(512) NULL, --备注
CONSTRAINT DEPARTMENT_INFO_PK PRIMARY KEY(DEP_ID)   );

--作业历史表
DROP TABLE IF EXISTS ETL_JOB_DISP_HIS ;
CREATE TABLE ETL_JOB_DISP_HIS(
ETL_SYS_CD                                        VARCHAR(100) NOT NULL, --工程代码
ETL_JOB                                           VARCHAR(512) NOT NULL, --作业名
CURR_BATH_DATE                                    VARCHAR(30) NOT NULL, --当前批量日期
SUB_SYS_CD                                        VARCHAR(100) NOT NULL, --子系统代码
ETL_JOB_DESC                                      VARCHAR(200) NULL, --作业描述
PRO_TYPE                                          VARCHAR(50) NULL, --作业程序类型
PRO_DIC                                           VARCHAR(512) NULL, --作业程序目录
PRO_NAME                                          VARCHAR(512) NULL, --作业程序名称
PRO_PARA                                          VARCHAR(1000) NULL, --作业程序参数
LOG_DIC                                           VARCHAR(512) NULL, --日志目录
DISP_FREQ                                         CHAR(1) NULL, --调度频率
DISP_OFFSET                                       INTEGER default 0 NULL, --调度时间位移
DISP_TYPE                                         CHAR(1) NULL, --调度触发方式
DISP_TIME                                         VARCHAR(30) NULL, --调度触发时间
JOB_EFF_FLAG                                      CHAR(1) NULL, --作业有效标志
JOB_PRIORITY                                      INTEGER default 0 NULL, --作业优先级
JOB_DISP_STATUS                                   CHAR(1) NULL, --作业调度状态
CURR_ST_TIME                                      VARCHAR(30) NULL, --开始时间
CURR_END_TIME                                     VARCHAR(30) NULL, --结束时间
OVERLENGTH_VAL                                    INTEGER default 0 NULL, --超长阀值
OVERTIME_VAL                                      INTEGER default 0 NULL, --超时阀值
COMMENTS                                          VARCHAR(512) NULL, --备注信息
TODAY_DISP                                        CHAR(1) NULL, --当天是否调度
MAIN_SERV_SYNC                                    CHAR(1) NULL, --主服务器同步标志
JOB_PROCESS_ID                                    VARCHAR(100) NULL, --作业进程号
JOB_PRIORITY_CURR                                 INTEGER default 0 NULL, --作业当前优先级
JOB_RETURN_VAL                                    INTEGER default 0 NULL, --作业返回值
EXE_FREQUENCY                                     DECIMAL(16) default 0 NULL, --每隔(分钟)执行	exe_frequency
EXE_NUM                                           INTEGER default 0 NULL, --执行次数
COM_EXE_NUM                                       INTEGER default 0 NULL, --已经执行次数
LAST_EXE_TIME                                     VARCHAR(20) NULL, --上次执行时间
STAR_TIME                                         VARCHAR(20) NULL, --开始执行时间
END_TIME                                          VARCHAR(20) NULL, --结束执行时间
CONSTRAINT ETL_JOB_DISP_HIS_PK PRIMARY KEY(ETL_SYS_CD,ETL_JOB,CURR_BATH_DATE)   );

--数据源
DROP TABLE IF EXISTS DATA_SOURCE ;
CREATE TABLE DATA_SOURCE(
SOURCE_ID                                         DECIMAL(10) NOT NULL, --数据源ID
DATASOURCE_NUMBER                                 VARCHAR(100) NULL, --数据源编号
DATASOURCE_NAME                                   VARCHAR(512) NOT NULL, --数据源名称
SOURCE_REMARK                                     VARCHAR(512) NULL, --数据源详细描述
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT DATA_SOURCE_PK PRIMARY KEY(SOURCE_ID)   );

--作业依赖关系表
DROP TABLE IF EXISTS ETL_DEPENDENCY ;
CREATE TABLE ETL_DEPENDENCY(
ETL_SYS_CD                                        VARCHAR(100) NOT NULL, --工程代码
ETL_JOB                                           VARCHAR(512) NOT NULL, --作业名
PRE_ETL_SYS_CD                                    VARCHAR(100) NOT NULL, --上游系统代码
PRE_ETL_JOB                                       VARCHAR(512) NOT NULL, --上游作业名
STATUS                                            CHAR(1) NULL, --状态
MAIN_SERV_SYNC                                    CHAR(1) NULL, --主服务器同步标志
CONSTRAINT ETL_DEPENDENCY_PK PRIMARY KEY(ETL_SYS_CD,ETL_JOB,PRE_ETL_SYS_CD,PRE_ETL_JOB)   );

--数据源与部门关系
DROP TABLE IF EXISTS SOURCE_RELATION_DEP ;
CREATE TABLE SOURCE_RELATION_DEP(
DEP_ID                                            DECIMAL(10) NOT NULL, --部门ID
SOURCE_ID                                         DECIMAL(10) NOT NULL, --数据源ID
CONSTRAINT SOURCE_RELATION_DEP_PK PRIMARY KEY(DEP_ID,SOURCE_ID)   );

--作业资源关系表
DROP TABLE IF EXISTS ETL_JOB_RESOURCE_RELA ;
CREATE TABLE ETL_JOB_RESOURCE_RELA(
ETL_SYS_CD                                        VARCHAR(100) NOT NULL, --工程代码
ETL_JOB                                           VARCHAR(512) NOT NULL, --作业名
RESOURCE_TYPE                                     VARCHAR(100) NULL, --资源使用类型
RESOURCE_REQ                                      INTEGER default 0 NULL, --资源需求数
CONSTRAINT ETL_JOB_RESOURCE_RELA_PK PRIMARY KEY(ETL_SYS_CD,ETL_JOB)   );

--Agent信息表
DROP TABLE IF EXISTS AGENT_INFO ;
CREATE TABLE AGENT_INFO(
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
AGENT_NAME                                        VARCHAR(512) NOT NULL, --Agent名称
AGENT_TYPE                                        CHAR(1) NOT NULL, --agent类别
AGENT_IP                                          VARCHAR(50) NOT NULL, --Agent所在服务器IP
AGENT_PORT                                        VARCHAR(10) NULL, --agent服务器端口
AGENT_STATUS                                      CHAR(1) NOT NULL, --agent状态
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
SOURCE_ID                                         DECIMAL(10) NOT NULL, --数据源ID
USER_ID                                           DECIMAL(10) NULL, --用户ID
CONSTRAINT AGENT_INFO_PK PRIMARY KEY(AGENT_ID)   );

--资源登记表
DROP TABLE IF EXISTS ETL_RESOURCE ;
CREATE TABLE ETL_RESOURCE(
ETL_SYS_CD                                        VARCHAR(100) NOT NULL, --工程代码
RESOURCE_TYPE                                     VARCHAR(100) NOT NULL, --资源使用类型
RESOURCE_MAX                                      INTEGER default 0 NULL, --资源阀值
RESOURCE_USED                                     INTEGER default 0 NULL, --已使用数
MAIN_SERV_SYNC                                    CHAR(1) NOT NULL, --主服务器同步标志
CONSTRAINT ETL_RESOURCE_PK PRIMARY KEY(ETL_SYS_CD,RESOURCE_TYPE)   );

--数据库设置
DROP TABLE IF EXISTS DATABASE_SET ;
CREATE TABLE DATABASE_SET(
DATABASE_ID                                       DECIMAL(10) NOT NULL, --数据库设置id
AGENT_ID                                          DECIMAL(10) NULL, --Agent_id
HOST_NAME                                         VARCHAR(512) NULL, --主机名
DATABASE_NUMBER                                   VARCHAR(10) NOT NULL, --数据库设置编号
SYSTEM_TYPE                                       VARCHAR(512) NULL, --操作系统类型
TASK_NAME                                         VARCHAR(512) NULL, --数据库采集任务名称
DATABASE_NAME                                     VARCHAR(512) NULL, --数据库名称
DATABASE_PAD                                      VARCHAR(100) NULL, --数据库密码
DATABASE_DRIVE                                    VARCHAR(512) NULL, --数据库驱动
DATABASE_TYPE                                     CHAR(2) NULL, --数据库类型
USER_NAME                                         VARCHAR(512) NULL, --用户名称
DATABASE_IP                                       VARCHAR(50) NULL, --数据库服务器IP
DATABASE_PORT                                     VARCHAR(10) NULL, --数据库端口
DB_AGENT                                          CHAR(1) NOT NULL, --是否为平面DB数据采集
DBFILE_FORMAT                                     CHAR(1) default '1' NULL, --DB文件格式
FILE_SUFFIX                                       VARCHAR(512) NULL, --采集文件名后缀
IS_LOAD                                           CHAR(1) default '1' NOT NULL, --是否直接加载数据
DATABASE_CODE                                     CHAR(1) NULL, --数据使用编码格式
DATABASE_SEPARATORR                               VARCHAR(512) NULL, --数据采用分隔符
ROW_SEPARATOR                                     VARCHAR(512) NULL, --数据行分隔符
IS_HIDDEN                                         CHAR(1) default '1' NOT NULL, --分隔符是否为ASCII隐藏字符
PLANE_URL                                         VARCHAR(512) NULL, --DB文件源数据路径
IS_SENDOK                                         CHAR(1) NOT NULL, --是否设置完成并发送成功
CHECK_TIME                                        VARCHAR(10) NULL, --检测时间
SIGNAL_FILE_SUFFIX                                VARCHAR(200) NULL, --信号文件后缀
ANALYSIS_SIGNALFILE                               CHAR(1) default '1' NOT NULL, --是否解析信号文件
DATA_EXTRACT_TYPE                                 CHAR(1) NOT NULL, --数据抽取方式
IS_HEADER                                         CHAR(1) default '1' NOT NULL, --是否有表头
CP_OR                                             VARCHAR(512) NULL, --清洗顺序
JDBC_URL                                          VARCHAR(512) NULL, --数据库连接地址
CLASSIFY_ID                                       DECIMAL(10) NOT NULL, --分类id
CONSTRAINT DATABASE_SET_PK PRIMARY KEY(DATABASE_ID)   );

--作业干预表
DROP TABLE IF EXISTS ETL_JOB_HAND ;
CREATE TABLE ETL_JOB_HAND(
EVENT_ID                                          VARCHAR(30) NOT NULL, --干预发生时间
ETL_SYS_CD                                        VARCHAR(100) NOT NULL, --工程代码
ETL_JOB                                           VARCHAR(512) NOT NULL, --作业名
ETL_HAND_TYPE                                     CHAR(2) NULL, --干预类型
PRO_PARA                                          VARCHAR(512) NULL, --干预参数
HAND_STATUS                                       CHAR(1) NULL, --干预状态
ST_TIME                                           VARCHAR(30) NULL, --开始时间
END_TIME                                          VARCHAR(30) NULL, --结束时间
WARNING                                           VARCHAR(80) NULL, --错误信息
MAIN_SERV_SYNC                                    CHAR(1) NULL, --同步标志位
CONSTRAINT ETL_JOB_HAND_PK PRIMARY KEY(EVENT_ID,ETL_SYS_CD,ETL_JOB)   );

--数据库对应表
DROP TABLE IF EXISTS TABLE_INFO ;
CREATE TABLE TABLE_INFO(
TABLE_ID                                          DECIMAL(10) NOT NULL, --表名ID
TABLE_NAME                                        VARCHAR(512) NOT NULL, --表名
TABLE_CH_NAME                                     VARCHAR(512) NOT NULL, --中文名称
TABLE_COUNT                                       VARCHAR(16) default '0' NULL, --记录数
STORAGE_TYPE                                      CHAR(1) NOT NULL, --储存方式
DATABASE_ID                                       DECIMAL(10) NOT NULL, --数据库设置id
SOURCE_TABLEID                                    VARCHAR(512) NULL, --源表ID
VALID_S_DATE                                      CHAR(8) NOT NULL, --有效开始日期
VALID_E_DATE                                      CHAR(8) NOT NULL, --有效结束日期
SQL                                               VARCHAR(6000) NULL, --自定义sql语句
IS_USER_DEFINED                                   CHAR(1) default '1' NOT NULL, --是否自定义sql采集
TI_OR                                             VARCHAR(512) NULL, --清洗顺序
IS_MD5                                            CHAR(1) NOT NULL, --是否使用MD5
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT TABLE_INFO_PK PRIMARY KEY(TABLE_ID)   );

--子系统定义表
DROP TABLE IF EXISTS ETL_SUB_SYS_LIST ;
CREATE TABLE ETL_SUB_SYS_LIST(
SUB_SYS_CD                                        VARCHAR(100) NOT NULL, --子系统代码
ETL_SYS_CD                                        VARCHAR(100) NOT NULL, --工程代码
SUB_SYS_DESC                                      VARCHAR(200) NULL, --子系统描述
COMMENTS                                          VARCHAR(512) NULL, --备注信息
CONSTRAINT ETL_SUB_SYS_LIST_PK PRIMARY KEY(SUB_SYS_CD,ETL_SYS_CD)   );

--卸数作业参数表
DROP TABLE IF EXISTS COLLECT_FREQUENCY ;
CREATE TABLE COLLECT_FREQUENCY(
CF_ID                                             DECIMAL(10) NOT NULL, --采集频率信息
COLLECT_TYPE                                      CHAR(1) NOT NULL, --采集类型
FRE_WEEK                                          VARCHAR(512) NULL, --周
FRE_MONTH                                         VARCHAR(512) NULL, --月
FRE_DAY                                           VARCHAR(512) NULL, --天
EXECUTE_TIME                                      CHAR(6) NULL, --执行时间
CRON_EXPRESSION                                   VARCHAR(512) NULL, --quartz执行表达式
FILE_PATH                                         VARCHAR(512) NULL, --文件存储路径
START_DATE                                        CHAR(8) NULL, --开始日期
END_DATE                                          CHAR(8) NOT NULL, --结束日期
AGENT_DATE                                        CHAR(8) NOT NULL, --数据采集服务器日期
AGENT_TIME                                        CHAR(6) NULL, --数据采集服务器时间
CREATE_DATE                                       CHAR(8) NULL, --创建日期
CREATE_TIME                                       CHAR(6) NULL, --创建时间
RUN_WAY                                           CHAR(1) NULL, --启动方式
CF_JOBNUM                                         VARCHAR(200) NULL, --自定义作业编号
COLLECT_SET_ID                                    DECIMAL(10) NOT NULL, --数据库设置id
RELY_JOB_ID                                       DECIMAL(10) NULL, --依赖作业id
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
COMP_ID                                           VARCHAR(20) NOT NULL, --组件编号
CONSTRAINT COLLECT_FREQUENCY_PK PRIMARY KEY(CF_ID)   );

--表对应的字段
DROP TABLE IF EXISTS TABLE_COLUMN ;
CREATE TABLE TABLE_COLUMN(
COLUMN_ID                                         DECIMAL(10) NOT NULL, --字段ID
IS_GET                                            CHAR(1) default '0' NULL, --是否采集
IS_PRIMARY_KEY                                    CHAR(1) NOT NULL, --是否为主键
COLUME_NAME                                       VARCHAR(512) NOT NULL, --列名
COLUMN_TYPE                                       VARCHAR(512) NULL, --列字段类型
COLUME_CH_NAME                                    VARCHAR(512) NULL, --列中文名称
TABLE_ID                                          DECIMAL(10) NOT NULL, --表名ID
VALID_S_DATE                                      CHAR(8) NOT NULL, --有效开始日期
VALID_E_DATE                                      CHAR(8) NOT NULL, --有效结束日期
IS_SOLR                                           CHAR(1) default '1' NOT NULL, --是否solr索引
IS_ALIVE                                          CHAR(1) default '0' NOT NULL, --是否保留原字段
IS_NEW                                            CHAR(1) default '1' NOT NULL, --是否为变化生成
IS_PRE                                            CHAR(1) default '1' NOT NULL, --是否carbondata聚合列
IS_SORTCOLUMNS                                    CHAR(1) default '1' NOT NULL, --是否为carbondata的排序列
TC_OR                                             VARCHAR(512) NULL, --清洗顺序
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT TABLE_COLUMN_PK PRIMARY KEY(COLUMN_ID)   );

--参数登记
DROP TABLE IF EXISTS ETL_PARA ;
CREATE TABLE ETL_PARA(
ETL_SYS_CD                                        VARCHAR(100) NOT NULL, --工程代码
PARA_CD                                           VARCHAR(50) NOT NULL, --变量代码
PARA_VAL                                          VARCHAR(512) NULL, --变量值
PARA_TYPE                                         VARCHAR(50) NULL, --变量类型
PARA_DESC                                         VARCHAR(200) NULL, --作业描述
CONSTRAINT ETL_PARA_PK PRIMARY KEY(ETL_SYS_CD,PARA_CD)   );

--干预历史表
DROP TABLE IF EXISTS ETL_JOB_HAND_HIS ;
CREATE TABLE ETL_JOB_HAND_HIS(
EVENT_ID                                          VARCHAR(30) NOT NULL, --干预发生时间
ETL_JOB                                           VARCHAR(512) NOT NULL, --作业名
ETL_SYS_CD                                        VARCHAR(100) NOT NULL, --工程代码
ETL_HAND_TYPE                                     CHAR(2) NULL, --干预类型
PRO_PARA                                          VARCHAR(512) NULL, --干预参数
HAND_STATUS                                       CHAR(1) NULL, --干预状态
ST_TIME                                           VARCHAR(30) NULL, --开始时间
END_TIME                                          VARCHAR(30) NULL, --结束时间
WARNING                                           VARCHAR(80) NULL, --错误信息
MAIN_SERV_SYNC                                    CHAR(1) NULL, --同步标志位
CONSTRAINT ETL_JOB_HAND_HIS_PK PRIMARY KEY(EVENT_ID,ETL_JOB,ETL_SYS_CD)   );

--工程登记表
DROP TABLE IF EXISTS ETL_SYS ;
CREATE TABLE ETL_SYS(
ETL_SYS_CD                                        VARCHAR(100) NOT NULL, --工程代码
ETL_SYS_NAME                                      VARCHAR(512) NOT NULL, --工程名称
ETL_SERV_IP                                       VARCHAR(50) NULL, --etl服务器ip
ETL_SERV_PORT                                     VARCHAR(10) NULL, --etl服务器端口
CONTACT_PERSON                                    VARCHAR(512) NULL, --联系人
CONTACT_PHONE                                     VARCHAR(20) NULL, --联系电话
COMMENTS                                          VARCHAR(512) NULL, --备注信息
CURR_BATH_DATE                                    VARCHAR(30) NULL, --当前批量日期
BATH_SHIFT_TIME                                   VARCHAR(30) NULL, --系统日切时间
MAIN_SERV_SYNC                                    CHAR(1) NULL, --主服务器同步标志
SYS_RUN_STATUS                                    CHAR(1) NULL, --系统状态
USER_NAME                                         VARCHAR(512) NULL, --主机服务器用户名
USER_PWD                                          VARCHAR(512) NULL, --主机用户密码
SERV_FILE_PATH                                    VARCHAR(512) NULL, --部署服务器路径
REMARKS                                           VARCHAR(512) NULL, --备注
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT ETL_SYS_PK PRIMARY KEY(ETL_SYS_CD)   );

--文件源设置
DROP TABLE IF EXISTS FILE_SOURCE ;
CREATE TABLE FILE_SOURCE(
FILE_SOURCE_ID                                    DECIMAL(10) NOT NULL, --文件源ID
FILE_SOURCE_PATH                                  VARCHAR(512) NOT NULL, --文件源路径
IS_PDF                                            CHAR(1) NOT NULL, --PDF文件
IS_OFFICE                                         CHAR(1) NOT NULL, --office文件
IS_TEXT                                           CHAR(1) NOT NULL, --文本文件
IS_VIDEO                                          CHAR(1) NOT NULL, --视频文件
IS_AUDIO                                          CHAR(1) NOT NULL, --音频文件
IS_IMAGE                                          CHAR(1) NOT NULL, --图片文件
IS_OTHER                                          CHAR(1) NOT NULL, --其他
FILE_REMARK                                       VARCHAR(512) NULL, --备注
FCS_ID                                            DECIMAL(10) NOT NULL, --文件系统采集ID
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
CONSTRAINT FILE_SOURCE_PK PRIMARY KEY(FILE_SOURCE_ID)   );

--机器学习pagerank算法表
DROP TABLE IF EXISTS ML_PAGERANKALGO ;
CREATE TABLE ML_PAGERANKALGO(
ALGORITHM_ID                                      DECIMAL(10) NOT NULL, --算法编号
ALGORITHM_NAME                                    VARCHAR(100) NOT NULL, --算法名称
VOTE_LINK                                         VARCHAR(64) NOT NULL, --投票链接字段
BEVOTED_LINK                                      VARCHAR(64) NOT NULL, --受投链接字段
NUMITERATIONS                                     DECIMAL(16) default 0 NOT NULL, --迭代次数
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
ALGO_RUNSTATE                                     CHAR(1) NOT NULL, --算法运行状态
RESULT_PATH                                       VARCHAR(512) NULL, --结果地址
REMARK                                            VARCHAR(512) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_PAGERANKALGO_PK PRIMARY KEY(ALGORITHM_ID)   );

--机器学习k-means模型表
DROP TABLE IF EXISTS ML_KMEANSMODEL ;
CREATE TABLE ML_KMEANSMODEL(
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
MODEL_NAME                                        VARCHAR(100) NOT NULL, --模型名称
OBSERVE_COLUMNS                                   VARCHAR(64) NULL, --观察字段
NUMITERATIONS                                     DECIMAL(16) default 0 NOT NULL, --迭代次数
CLUSTERNUM                                        DECIMAL(16) default 0 NOT NULL, --聚类个数
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
MODEL_RUNSTATE                                    CHAR(1) NOT NULL, --模型运行状态
MODEL_PATH                                        VARCHAR(512) NULL, --模型地址
REMARK                                            VARCHAR(512) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_KMEANSMODEL_PK PRIMARY KEY(MODEL_ID)   );

--压缩作业参数表
DROP TABLE IF EXISTS COLLECT_REDUCE ;
CREATE TABLE COLLECT_REDUCE(
CR_ID                                             DECIMAL(10) NOT NULL, --采集压缩信息
COLLECT_TYPE                                      CHAR(1) NOT NULL, --采集类型
IS_REDUCE                                         CHAR(1) NOT NULL, --是否压缩
REDUCE_SCOPE                                      CHAR(1) NULL, --压缩范围
FILE_SIZE                                         DECIMAL(16) default 0 NULL, --文件大于多少压缩
IS_ENCRYPT                                        CHAR(1) NOT NULL, --是否需要加密
IS_MD5CHECK                                       CHAR(1) NOT NULL, --是否使用MD5校验
RUN_WAY                                           CHAR(1) NOT NULL, --启动方式
REMARK                                            VARCHAR(512) NULL, --备注
COLLECT_SET_ID                                    DECIMAL(10) NOT NULL, --数据库设置id
RELY_JOB_ID                                       DECIMAL(10) NULL, --依赖作业id
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
COMP_ID                                           VARCHAR(20) NOT NULL, --组件编号
CONSTRAINT COLLECT_REDUCE_PK PRIMARY KEY(CR_ID)   );

--机器学习k-means特征表
DROP TABLE IF EXISTS ML_KMEANSFEAT ;
CREATE TABLE ML_KMEANSFEAT(
FEATURE_ID                                        DECIMAL(10) NOT NULL, --特征编号
FEATURE_NAME                                      VARCHAR(64) NOT NULL, --特征字段名
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
CONSTRAINT ML_KMEANSFEAT_PK PRIMARY KEY(FEATURE_ID)   );

--传送作业参数表
DROP TABLE IF EXISTS COLLECT_TRANSFER ;
CREATE TABLE COLLECT_TRANSFER(
CT_ID                                             DECIMAL(10) NOT NULL, --采集传输
COLLECT_TYPE                                      CHAR(1) NOT NULL, --采集类型
IS_PARALLEL                                       CHAR(1) NULL, --是否并行传输
FAILURE_COUNT                                     DECIMAL(16) default 0 NULL, --传输失败重试次数
IS_BIGLINK                                        CHAR(1) NULL, --是否大文件独占链路
FILE_SIZE_LINK                                    DECIMAL(16) default 0 NULL, --文件大于多少
RUN_WAY                                           CHAR(1) NOT NULL, --启动方式
REMARK                                            VARCHAR(512) NULL, --备注
COLLECT_SET_ID                                    DECIMAL(10) NOT NULL, --数据库设置id
RELY_JOB_ID                                       DECIMAL(10) NULL, --依赖作业id
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
COMP_ID                                           VARCHAR(20) NOT NULL, --组件编号
CONSTRAINT COLLECT_TRANSFER_PK PRIMARY KEY(CT_ID)   );

--机器学习用户脚本结果信息表
DROP TABLE IF EXISTS ML_CUSTSCRIINFO ;
CREATE TABLE ML_CUSTSCRIINFO(
SCRIPT_ID                                         DECIMAL(10) NOT NULL, --脚本编号
CURRENT_STEP                                      CHAR(1) NOT NULL, --当前操作步骤
NEW_TABLENAME                                     VARCHAR(100) NOT NULL, --新数据表名
GENERATE_IS_FLAG                                  CHAR(1) NOT NULL, --新表是否生成完成
NEW_FILEPATH                                      VARCHAR(512) NOT NULL, --新数据文件路径
NEW_FILESIZE                                      DECIMAL(16) default 0 NOT NULL, --新数据文件大小
SCRIPT_PATH                                       VARCHAR(512) NOT NULL, --脚本文件地址
DATAMAPMODE                                       CHAR(1) NOT NULL, --数据映射方式
UPDATE_DATE                                       CHAR(8) NOT NULL, --修改日期
UPDATE_TIME                                       CHAR(6) NOT NULL, --修改时间
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_CUSTSCRIINFO_PK PRIMARY KEY(SCRIPT_ID)   );

--清洗作业参数表
DROP TABLE IF EXISTS COLLECT_CLEAN ;
CREATE TABLE COLLECT_CLEAN(
CC_ID                                             DECIMAL(10) NOT NULL, --采集概况信息
COLLECT_TYPE                                      CHAR(1) NOT NULL, --采集类型
IS_DEL_SPACE                                      CHAR(1) NOT NULL, --是否去空格
IS_SPECIAL_ESCAPE                                 CHAR(1) NOT NULL, --是否特殊字符转义
IS_DEL_ESCAPE                                     CHAR(1) NOT NULL, --是否去除特殊字符
IS_FILLING_COLUMN                                 CHAR(1) NOT NULL, --是否字段补齐
RUN_WAY                                           CHAR(1) NOT NULL, --启动方式
CC_REMARK                                         VARCHAR(512) NULL, --备注
COLLECT_SET_ID                                    DECIMAL(10) NOT NULL, --数据库设置id
RELY_JOB_ID                                       DECIMAL(10) NULL, --依赖作业id
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
COMP_ID                                           VARCHAR(20) NOT NULL, --组件编号
CONSTRAINT COLLECT_CLEAN_PK PRIMARY KEY(CC_ID)   );

--机器学习用户脚本结果字段表
DROP TABLE IF EXISTS ML_CUSTSCRICOL ;
CREATE TABLE ML_CUSTSCRICOL(
COLUMN_ID                                         DECIMAL(10) NOT NULL, --字段编号
COLUMN_NAME                                       VARCHAR(64) NOT NULL, --字段英文名称
COLUMN_TYPE                                       CHAR(1) NOT NULL, --字段类型
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
SCRIPT_ID                                         DECIMAL(10) NOT NULL, --脚本编号
CONSTRAINT ML_CUSTSCRICOL_PK PRIMARY KEY(COLUMN_ID)   );

--hdfs存储作业参数表
DROP TABLE IF EXISTS COLLECT_HDFS ;
CREATE TABLE COLLECT_HDFS(
CHDFS_ID                                          DECIMAL(10) NOT NULL, --HDFS存储
COLLECT_TYPE                                      CHAR(1) NOT NULL, --采集类型
IS_HBASE                                          CHAR(1) NOT NULL, --是否存入HBASE
IS_HIVE                                           CHAR(1) NOT NULL, --是否存入HIVE
IS_FULLINDEX                                      CHAR(1) NOT NULL, --是否建立全文索引
RUN_WAY                                           CHAR(1) NOT NULL, --启动方式
CC_REMARK                                         VARCHAR(512) NULL, --备注
COLLECT_SET_ID                                    DECIMAL(10) NOT NULL, --数据库设置id
RELY_JOB_ID                                       DECIMAL(10) NULL, --依赖作业id
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
COMP_ID                                           VARCHAR(20) NOT NULL, --组件编号
CONSTRAINT COLLECT_HDFS_PK PRIMARY KEY(CHDFS_ID)   );

--系统采集作业结果表
DROP TABLE IF EXISTS SYS_EXEINFO ;
CREATE TABLE SYS_EXEINFO(
EXE_ID                                            DECIMAL(10) NOT NULL, --执行id
JOB_NAME                                          VARCHAR(512) NOT NULL, --作业名称名称
JOB_TABLENAME                                     VARCHAR(512) NULL, --作业表名
ETL_DATE                                          CHAR(8) NOT NULL, --执行日期
EXECUTE_STATE                                     CHAR(2) NOT NULL, --运行状态
EXE_PARAMETER                                     VARCHAR(512) NOT NULL, --参数
ERR_INFO                                          VARCHAR(512) NOT NULL, --错误信息
IS_VALID                                          CHAR(1) NOT NULL, --作业是否有效
ST_DATE                                           CHAR(14) NOT NULL, --开始日期
ED_DATE                                           CHAR(14) NOT NULL, --结束日期
DATABASE_ID                                       DECIMAL(10) NOT NULL, --数据库设置id
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
SOURCE_ID                                         DECIMAL(10) NOT NULL, --数据源ID
CONSTRAINT SYS_EXEINFO_PK PRIMARY KEY(EXE_ID)   );

--数据管理消费至Hbase
DROP TABLE IF EXISTS SDM_CON_HBASE ;
CREATE TABLE SDM_CON_HBASE(
HBASE_ID                                          DECIMAL(10) NOT NULL, --hbaseId
HBASE_BUS_CLASS                                   VARCHAR(200) NULL, --hbase业务处理类
HBASE_BUS_TYPE                                    CHAR(1) NOT NULL, --hbase业务处理类类型
HBASE_NAME                                        VARCHAR(512) NOT NULL, --hbase表名
HBASE_FAMILY                                      VARCHAR(200) NOT NULL, --列簇
PRE_PARTITION                                     VARCHAR(512) NULL, --hbase预分区
ROWKEY_SEPARATOR                                  VARCHAR(200) NULL, --rowkey分隔符
REMARK                                            VARCHAR(512) NULL, --备注
SDM_DES_ID                                        DECIMAL(10) NOT NULL, --配置id
CONSTRAINT SDM_CON_HBASE_PK PRIMARY KEY(HBASE_ID)   );

--流数据管理消费至rest服务
DROP TABLE IF EXISTS SDM_CON_REST ;
CREATE TABLE SDM_CON_REST(
REST_ID                                           DECIMAL(10) NOT NULL, --restId
REST_BUS_CLASS                                    VARCHAR(200) NULL, --rest业务处理类
REST_BUS_TYPE                                     CHAR(1) NOT NULL, --rest业务处理类类型
REST_PORT                                         VARCHAR(10) NOT NULL, --rest服务器端口
REST_IP                                           VARCHAR(50) NOT NULL, --restIP
REST_PARAMETER                                    VARCHAR(200) NULL, --标志rest服务
REMARK                                            VARCHAR(512) NULL, --备注
SDM_DES_ID                                        DECIMAL(10) NOT NULL, --配置id
CONSTRAINT SDM_CON_REST_PK PRIMARY KEY(REST_ID)   );

--组件信息表
DROP TABLE IF EXISTS COMPONENT_INFO ;
CREATE TABLE COMPONENT_INFO(
COMP_ID                                           VARCHAR(20) NOT NULL, --组件编号
COMP_NAME                                         VARCHAR(512) NOT NULL, --组件名称
COMP_STATE                                        CHAR(1) NOT NULL, --组件状态
COMP_VERSION                                      VARCHAR(100) NOT NULL, --组件版本
ICON_INFO                                         VARCHAR(512) NULL, --图标
COLOR_INFO                                        VARCHAR(512) NULL, --颜色
COMP_TYPE                                         CHAR(1) NULL, --组件类型
COMP_REMARK                                       VARCHAR(512) NULL, --备注
CONSTRAINT COMPONENT_INFO_PK PRIMARY KEY(COMP_ID)   );

--数据管理消费至文件
DROP TABLE IF EXISTS SDM_CON_FILE ;
CREATE TABLE SDM_CON_FILE(
FILE_ID                                           DECIMAL(10) NOT NULL, --fileID
FILE_BUS_CLASS                                    VARCHAR(200) NULL, --file业务处理类
FILE_BUS_TYPE                                     CHAR(1) NOT NULL, --file业务类类型
FILE_NAME                                         VARCHAR(512) NOT NULL, --file名称
FILE_PATH                                         VARCHAR(512) NOT NULL, --文件绝对路径
REMARK                                            VARCHAR(512) NULL, --备注
SPILT_FLAG                                        CHAR(1) NOT NULL, --是否分割标志
FILE_LIMIT                                        DECIMAL(16) default 0 NULL, --分割大小
SDM_DES_ID                                        DECIMAL(10) NOT NULL, --配置id
CONSTRAINT SDM_CON_FILE_PK PRIMARY KEY(FILE_ID)   );

--组件参数
DROP TABLE IF EXISTS COMPONENT_PARAM ;
CREATE TABLE COMPONENT_PARAM(
PARAM_ID                                          DECIMAL(10) NOT NULL, --主键参数id
PARAM_NAME                                        VARCHAR(512) NOT NULL, --参数名称
PARAM_VALUE                                       VARCHAR(100) NOT NULL, --参数value
IS_MUST                                           CHAR(1) NOT NULL, --是否必要
PARAM_REMARK                                      VARCHAR(512) NOT NULL, --备注
COMP_ID                                           VARCHAR(20) NULL, --组件编号
CONSTRAINT COMPONENT_PARAM_PK PRIMARY KEY(PARAM_ID)   );

--机器学习梯度增强回归树模型
DROP TABLE IF EXISTS ML_GBRTMODEL ;
CREATE TABLE ML_GBRTMODEL(
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
MODEL_NAME                                        VARCHAR(100) NOT NULL, --模型名称
DV_COLUMN                                         VARCHAR(64) NOT NULL, --因变量字段
N_ESTIMATORS                                      DECIMAL(16) default 0 NOT NULL, --估计量个数
LEARNING_RATE                                     DECIMAL(16,2) default 0 NOT NULL, --学习率
MAX_DEPTH                                         DECIMAL(16) default 0 NOT NULL, --最大深度
RANDOM_STATE                                      DECIMAL(16) default 0 NULL, --随机种子数
MAX_LEAF_NODES                                    DECIMAL(16) default 0 NULL, --最大叶子节点数
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
MODEL_RUNSTATE                                    CHAR(1) NOT NULL, --模型运行状态
MODEL_PATH                                        VARCHAR(512) NULL, --模型地址
REMARK                                            VARCHAR(512) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_GBRTMODEL_PK PRIMARY KEY(MODEL_ID)   );

--机器学习梯度增强回归树特征表
DROP TABLE IF EXISTS ML_GBRTFEAT ;
CREATE TABLE ML_GBRTFEAT(
FEATURE_ID                                        DECIMAL(10) NOT NULL, --特征编号
IV_COLUMN                                         VARCHAR(64) NOT NULL, --自变量字段
MODEL_ID                                          DECIMAL(10) NOT NULL, --模型编号
CONSTRAINT ML_GBRTFEAT_PK PRIMARY KEY(FEATURE_ID)   );

--主键生成表
DROP TABLE IF EXISTS KEYTABLE ;
CREATE TABLE KEYTABLE(
KEY_NAME                                          VARCHAR(80) NOT NULL, --key_name
KEY_VALUE                                         INTEGER default 0 NULL, --value
CONSTRAINT KEYTABLE_PK PRIMARY KEY(KEY_NAME)   );

--集市数据表外部存储信息表
DROP TABLE IF EXISTS DATATABLE_INFO_OTHER ;
CREATE TABLE DATATABLE_INFO_OTHER(
OTHER_ID                                          DECIMAL(10) NOT NULL, --外部存储id
EXTERNAL_HBASE                                    CHAR(1) default '1' NOT NULL, --是否使用外部hbase
EXTERNAL_MPP                                      CHAR(1) default '1' NOT NULL, --是否使用外部mpp
EXTERNAL_SOLR                                     CHAR(1) default '1' NOT NULL, --是否外部solr
EXTERNAL_REDIS                                    CHAR(1) default '1' NOT NULL, --是否外部redis
IS_KAFKA                                          CHAR(1) default '1' NOT NULL, --是否外部kafka
KAFKA_IP                                          VARCHAR(50) NULL, --kafkaIP
KAFKA_PORT                                        VARCHAR(10) NULL, --kafka端口
KAFKA_TOPIC                                       VARCHAR(200) NULL, --kafkatopic
TOPIC_NUMBER                                      DECIMAL(16) default 1 NULL, --topic分区数
ZK_HOST                                           VARCHAR(512) NULL, --zookeeper地址
KAFKA_BK_NUM                                      DECIMAL(16) default 1 NULL, --kafka备份数
MPP_IP                                            VARCHAR(50) NULL, --外部mpp表IP
MPP_PORT                                          VARCHAR(10) NULL, --外部mpp表端口
MPP_DATABASE                                      VARCHAR(512) NULL, --外部表mpp数据库
MPP_NAME                                          VARCHAR(512) NULL, --外部表mpp用户名
MPP_PASS                                          VARCHAR(512) NULL, --外部表mpp密码
DATABASE_TYPE                                     CHAR(2) NULL, --数据库类型
DATABASE_DRIVER                                   VARCHAR(512) NULL, --数据库驱动类型
JDBC_URL                                          VARCHAR(512) NULL, --数据连接url
SLOLR_COLLECT                                     VARCHAR(512) NULL, --外部表solr连接信息
REDIS_IP                                          VARCHAR(50) NULL, --外部redis连接ip
REDIS_PORT                                        VARCHAR(10) NULL, --外部redis端口
REDIS_USER                                        CHAR(10) NULL, --redis用户
REDIS_SEPARATOR                                   VARCHAR(200) NULL, --redis分隔符
EXHBASE_SUCCESS                                   CHAR(3) default '105' NULL, --外部hbase是否执行成功
EXMPP_SUCCESS                                     CHAR(3) default '105' NULL, --外部mpp是否成功
EXSOLR_SUCCESS                                    CHAR(3) default '105' NULL, --外部solr是否成功
EXREDIS_SUCCESS                                   CHAR(3) default '105' NULL, --外部redis是否成功
EXKAFKA_SUCCESS                                   CHAR(3) default '105' NULL, --外部kafka是否成功
HBASESITE_PATH                                    VARCHAR(512) NULL, --外部集群配置文件路径
REMARK                                            VARCHAR(512) NULL, --备注
DATATABLE_ID                                      DECIMAL(10) NULL, --数据表id
CONSTRAINT DATATABLE_INFO_OTHER_PK PRIMARY KEY(OTHER_ID)   );

--代码信息表
DROP TABLE IF EXISTS CODE_INFO ;
CREATE TABLE CODE_INFO(
CI_SP_CODE                                        VARCHAR(20) NOT NULL, --代码值
CI_SP_CLASS                                       VARCHAR(20) NOT NULL, --所属类别号
CI_SP_CLASSNAME                                   VARCHAR(80) NOT NULL, --类别名称
CI_SP_NAME                                        VARCHAR(255) NOT NULL, --代码名称
CI_SP_REMARK                                      VARCHAR(512) NULL, --备注
CONSTRAINT CODE_INFO_PK PRIMARY KEY(CI_SP_CODE,CI_SP_CLASS)   );

--文件系统设置
DROP TABLE IF EXISTS FILE_COLLECT_SET ;
CREATE TABLE FILE_COLLECT_SET(
FCS_ID                                            DECIMAL(10) NOT NULL, --文件系统采集ID
AGENT_ID                                          DECIMAL(10) NULL, --Agent_id
FCS_NAME                                          VARCHAR(512) NOT NULL, --文件系统采集任务名称
HOST_NAME                                         VARCHAR(512) NULL, --主机名称
SYSTEM_TYPE                                       VARCHAR(512) NULL, --操作系统类型
IS_SENDOK                                         CHAR(1) NOT NULL, --是否设置完成并发送成功
IS_SOLR                                           CHAR(1) NOT NULL, --是否入solr
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT FILE_COLLECT_SET_PK PRIMARY KEY(FCS_ID)   );

--对象采集对应信息
DROP TABLE IF EXISTS OBJECT_COLLECT_TASK ;
CREATE TABLE OBJECT_COLLECT_TASK(
OCS_ID                                            DECIMAL(10) NOT NULL, --对象采集任务编号
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
EN_NAME                                           VARCHAR(512) NOT NULL, --英文名称
ZH_NAME                                           VARCHAR(512) NOT NULL, --中文名称
COLLECT_DATA_TYPE                                 CHAR(1) NOT NULL, --数据类型
REMARK                                            VARCHAR(512) NULL, --备注
DATABASE_CODE                                     CHAR(1) NOT NULL, --采集编码
ODC_ID                                            DECIMAL(10) NOT NULL, --对象采集id
CONSTRAINT OBJECT_COLLECT_TASK_PK PRIMARY KEY(OCS_ID)   );

--对象采集设置
DROP TABLE IF EXISTS OBJECT_COLLECT ;
CREATE TABLE OBJECT_COLLECT(
ODC_ID                                            DECIMAL(10) NOT NULL, --对象采集id
OBJECT_COLLECT_TYPE                               CHAR(1) NOT NULL, --对象采集方式
OBJ_NUMBER                                        VARCHAR(200) NOT NULL, --对象采集设置编号
OBJ_COLLECT_NAME                                  VARCHAR(512) NOT NULL, --对象采集任务名称
SYSTEM_NAME                                       VARCHAR(512) NOT NULL, --操作系统类型
HOST_NAME                                         VARCHAR(512) NOT NULL, --主机名称
LOCAL_TIME                                        CHAR(20) NOT NULL, --本地系统时间
SERVER_DATE                                       CHAR(20) NOT NULL, --服务器日期
S_DATE                                            CHAR(8) NOT NULL, --开始日期
E_DATE                                            CHAR(8) NOT NULL, --结束日期
DATABASE_CODE                                     CHAR(1) NOT NULL, --采集编码
RUN_WAY                                           CHAR(1) NOT NULL, --启动方式
FILE_PATH                                         VARCHAR(512) NOT NULL, --采集文件路径
IS_SENDOK                                         CHAR(1) NOT NULL, --是否设置完成并发送成功
REMARK                                            VARCHAR(512) NULL, --备注
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
CONSTRAINT OBJECT_COLLECT_PK PRIMARY KEY(ODC_ID)   );

--流数据管理消费目的地管理
DROP TABLE IF EXISTS SDM_CONSUME_DES ;
CREATE TABLE SDM_CONSUME_DES(
SDM_DES_ID                                        DECIMAL(10) NOT NULL, --配置id
PARTITION                                         CHAR(10) NULL, --分区
SDM_CONS_DES                                      CHAR(1) NOT NULL, --消费端目的地
SDM_CONF_DESCRIBE                                 CHAR(1) NULL, --海云外部流数据管理消费端目的地
HYREN_CONSUMEDES                                  CHAR(1) NULL, --海云内部消费目的地
SDM_THR_PARTITION                                 CHAR(1) NOT NULL, --消费线程与分区的关系
THREAD_NUM                                        INTEGER default 0 NULL, --线程数
SDM_BUS_PRO_CLA                                   VARCHAR(2000) NULL, --业务处理类
CUS_DES_TYPE                                      CHAR(1) NOT NULL, --自定义业务类类型
DES_CLASS                                         VARCHAR(2000) NULL, --目的地业务处理类
DESCUSTOM_BUSCLA                                  CHAR(1) default '0' NOT NULL, --目的地业务类类型
HDFS_FILE_TYPE                                    CHAR(1) NULL, --hdfs文件类型
EXTERNAL_FILE_TYPE                                CHAR(1) NULL, --外部文件类型
REMARK                                            VARCHAR(512) NULL, --备注
SDM_CONSUM_ID                                     DECIMAL(10) NOT NULL, --消费端配置id
CONSTRAINT SDM_CONSUME_DES_PK PRIMARY KEY(SDM_DES_ID)   );

--采集情况信息表
DROP TABLE IF EXISTS COLLECT_CASE ;
CREATE TABLE COLLECT_CASE(
JOB_RS_ID                                         VARCHAR(40) NOT NULL, --作业执行结果ID
COLLECT_TYPE                                      CHAR(1) NOT NULL, --采集类型
JOB_TYPE                                          VARCHAR(10) NULL, --任务类型
COLLECT_TOTAL                                     DECIMAL(16) default 0 NULL, --总共采集(文件)表
COLECT_RECORD                                     DECIMAL(16) default 0 NOT NULL, --总共采集记录数
COLLET_DATABASE_SIZE                              VARCHAR(100) NULL, --总共采集数据大小
COLLECT_S_DATE                                    CHAR(8) NOT NULL, --开始采集日期
COLLECT_S_TIME                                    CHAR(6) NOT NULL, --开始采集时间
COLLECT_E_DATE                                    CHAR(8) NULL, --采集结束日期
COLLECT_E_TIME                                    CHAR(6) NULL, --采集结束时间
EXECUTE_LENGTH                                    VARCHAR(10) NULL, --运行总时长
EXECUTE_STATE                                     CHAR(2) NOT NULL, --运行状态
IS_AGAIN                                          CHAR(1) NOT NULL, --是否重跑
AGAIN_NUM                                         DECIMAL(16) default 0 NULL, --重跑次数
JOB_GROUP                                         VARCHAR(100) NOT NULL, --agent组ID
TABLE_NAME                                        VARCHAR(512) NULL, --表名
ETL_DATE                                          VARCHAR(18) NULL, --跑批日期
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
COLLECT_SET_ID                                    DECIMAL(10) NOT NULL, --数据库设置id
SOURCE_ID                                         DECIMAL(10) NOT NULL, --数据源ID
CC_REMARK                                         VARCHAR(512) NULL, --备注
CONSTRAINT COLLECT_CASE_PK PRIMARY KEY(JOB_RS_ID)   );

--对象采集存储设置
DROP TABLE IF EXISTS OBJECT_STORAGE ;
CREATE TABLE OBJECT_STORAGE(
OBJ_STID                                          DECIMAL(10) NOT NULL, --存储编号
IS_HBASE                                          CHAR(1) NOT NULL, --是否进hbase
IS_HDFS                                           CHAR(1) NOT NULL, --是否进hdfs
REMARK                                            VARCHAR(512) NULL, --备注
OCS_ID                                            DECIMAL(10) NULL, --对象采集任务编号
CONSTRAINT OBJECT_STORAGE_PK PRIMARY KEY(OBJ_STID)   );

--对象采集结构信息
DROP TABLE IF EXISTS OBJECT_COLLECT_STRUCT ;
CREATE TABLE OBJECT_COLLECT_STRUCT(
STRUCT_ID                                         DECIMAL(10) NOT NULL, --结构信息id
OCS_ID                                            DECIMAL(10) NOT NULL, --对象采集任务编号
COLL_NAME                                         VARCHAR(512) NOT NULL, --采集结构名称
STRUCT_TYPE                                       CHAR(1) NOT NULL, --对象数据类型
DATA_DESC                                         VARCHAR(200) NULL, --中文描述信息
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT OBJECT_COLLECT_STRUCT_PK PRIMARY KEY(STRUCT_ID)   );

--请求Agent类型
DROP TABLE IF EXISTS REQ_AGENTTYPE ;
CREATE TABLE REQ_AGENTTYPE(
REQ_ID                                            DECIMAL(10) NOT NULL, --请求ID
REQ_NAME                                          VARCHAR(512) NOT NULL, --中文名称
REQ_NO                                            CHAR(10) NULL, --请求编号
REQ_REMARK                                        VARCHAR(80) NULL, --备注
COMP_ID                                           VARCHAR(20) NOT NULL, --组件编号
CONSTRAINT REQ_AGENTTYPE_PK PRIMARY KEY(REQ_ID)   );

--数据集市文件导出
DROP TABLE IF EXISTS DATAMART_EXPORT ;
CREATE TABLE DATAMART_EXPORT(
EXPORT_ID                                         DECIMAL(10) NOT NULL, --导出表id
STORAGE_PATH                                      VARCHAR(512) NULL, --文件存储地址
FILE_HBASE                                        CHAR(1) NULL, --文件是否存放集群
DATAFILE_SEPARATOR                                VARCHAR(200) NULL, --数据文件使用分隔符
REDUCE_TYPE                                       CHAR(1) NULL, --压缩格式
FILE_CODE                                         CHAR(1) NULL, --导出文件编码
IS_FIXED                                          CHAR(1) default '1' NOT NULL, --是否定长
FILLING_CHAR                                      VARCHAR(512) NULL, --补齐字符
FILLING_TYPE                                      CHAR(1) NULL, --补齐方式
IS_INFO                                           CHAR(1) NOT NULL, --是否生成信号文件
REMARK                                            VARCHAR(512) NULL, --备注
DATATABLE_ID                                      DECIMAL(10) NULL, --数据表id
CONSTRAINT DATAMART_EXPORT_PK PRIMARY KEY(EXPORT_ID)   );

--Ftp采集设置
DROP TABLE IF EXISTS FTP_COLLECT ;
CREATE TABLE FTP_COLLECT(
FTP_ID                                            DECIMAL(10) NOT NULL, --ftp采集id
FTP_NUMBER                                        VARCHAR(200) NOT NULL, --ftp任务编号
FTP_NAME                                          VARCHAR(512) NOT NULL, --ftp采集任务名称
START_DATE                                        CHAR(8) NOT NULL, --开始日期
END_DATE                                          CHAR(8) NOT NULL, --结束日期
FTP_IP                                            VARCHAR(50) NOT NULL, --ftp服务IP
FTP_PORT                                          VARCHAR(10) NOT NULL, --ftp服务器端口
FTP_USERNAME                                      VARCHAR(512) NOT NULL, --ftp用户名
FTP_PASSWORD                                      VARCHAR(100) NOT NULL, --用户密码
FTP_DIR                                           VARCHAR(512) NOT NULL, --ftp服务器目录
LOCAL_PATH                                        VARCHAR(512) NOT NULL, --本地路径
FTP_RULE_PATH                                     CHAR(1) default '1' NOT NULL, --下级目录规则
CHILD_FILE_PATH                                   VARCHAR(512) NULL, --下级文件路径
CHILD_TIME                                        CHAR(1) NULL, --下级文件时间
FILE_SUFFIX                                       VARCHAR(200) NULL, --获取文件后缀
FTP_MODEL                                         CHAR(1) default '1' NOT NULL, --FTP推拉模式是为推模式
RUN_WAY                                           CHAR(1) NOT NULL, --启动方式
REMARK                                            VARCHAR(512) NULL, --备注
IS_SENDOK                                         CHAR(1) NOT NULL, --是否完成
IS_UNZIP                                          CHAR(1) NOT NULL, --是否解压
REDUCE_TYPE                                       CHAR(1) NULL, --解压格式
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
CONSTRAINT FTP_COLLECT_PK PRIMARY KEY(FTP_ID)   );

--ftp已传输表
DROP TABLE IF EXISTS FTP_TRANSFERED ;
CREATE TABLE FTP_TRANSFERED(
FTP_TRANSFERED_ID                                 DECIMAL(10) NOT NULL, --已传输表id
FTP_ID                                            DECIMAL(10) NOT NULL, --ftp采集id
TRANSFERED_NAME                                   VARCHAR(512) NOT NULL, --已传输文件名称
FTP_DATE                                          CHAR(8) NOT NULL, --ftp日期
FTP_TIME                                          CHAR(6) NOT NULL, --ftp时间
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT FTP_TRANSFERED_PK PRIMARY KEY(FTP_TRANSFERED_ID)   );

--源文件夹属性表
DROP TABLE IF EXISTS SOURCE_FOLDER_ATTRIBUTE ;
CREATE TABLE SOURCE_FOLDER_ATTRIBUTE(
FOLDER_ID                                         DECIMAL(10) NOT NULL, --文件夹编号
SUPER_ID                                          DECIMAL(10) NULL, --文件夹编号
FOLDER_NAME                                       VARCHAR(512) NOT NULL, --文件夹名
ORIGINAL_CREATE_DATE                              CHAR(8) NOT NULL, --文件夹生产日期
ORIGINAL_CREATE_TIME                              CHAR(6) NOT NULL, --文件夹生成时间
FOLDER_SIZE                                       DECIMAL(16,2) default 0 NOT NULL, --文件夹大小
STORAGE_DATE                                      CHAR(8) NOT NULL, --文件夹入库日期
STORAGE_TIME                                      CHAR(6) NOT NULL, --文件夹入库时间
FOLDERS_IN_NO                                     DECIMAL(16) default 0 NOT NULL, --文件夹内文件夹数量
LOCATION_IN_HDFS                                  VARCHAR(512) NOT NULL, --hdfs中存储位置
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
SOURCE_ID                                         DECIMAL(10) NOT NULL, --数据源ID
CONSTRAINT SOURCE_FOLDER_ATTRIBUTE_PK PRIMARY KEY(FOLDER_ID)   );

--ftp目录表
DROP TABLE IF EXISTS FTP_FOLDER ;
CREATE TABLE FTP_FOLDER(
FTP_FOLDER_ID                                     DECIMAL(10) NOT NULL, --目录表id
FTP_ID                                            DECIMAL(10) NOT NULL, --ftp采集id
FTP_FOLDER_NAME                                   VARCHAR(512) NOT NULL, --ftp目录名称
IS_PROCESSED                                      CHAR(1) NOT NULL, --是否处理过
FTP_DATE                                          CHAR(8) NOT NULL, --ftp日期
FTP_TIME                                          CHAR(6) NOT NULL, --ftp时间
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT FTP_FOLDER_PK PRIMARY KEY(FTP_FOLDER_ID)   );

--模型前置作业
DROP TABLE IF EXISTS EDW_RELEVANT_INFO ;
CREATE TABLE EDW_RELEVANT_INFO(
REL_ID                                            DECIMAL(10) NOT NULL, --作业相关id
PRE_WORK                                          VARCHAR(6500) NULL, --前置作业
POST_WORK                                         VARCHAR(6500) NULL, --后置作业
REMARK                                            VARCHAR(512) NULL, --备注
JOBCODE                                           VARCHAR(100) NOT NULL, --作业编号
CONSTRAINT EDW_RELEVANT_INFO_PK PRIMARY KEY(REL_ID)   );

--组件菜单表
DROP TABLE IF EXISTS COMPONENT_MENU ;
CREATE TABLE COMPONENT_MENU(
MENU_ID                                           DECIMAL(10) NOT NULL, --主键菜单id
MENU_PATH                                         VARCHAR(200) NOT NULL, --菜单path
USER_TYPE                                         CHAR(2) NOT NULL, --用户类型
MENU_NAME                                         VARCHAR(200) NOT NULL, --菜单名称
COMP_ID                                           VARCHAR(20) NOT NULL, --组件编号
MENU_REMARK                                       VARCHAR(512) NULL, --备注
CONSTRAINT COMPONENT_MENU_PK PRIMARY KEY(MENU_ID)   );

--流数据海云内部消费信息登记表
DROP TABLE IF EXISTS SDM_INNER_TABLE ;
CREATE TABLE SDM_INNER_TABLE(
TABLE_ID                                          DECIMAL(10) NOT NULL, --表id
TABLE_CN_NAME                                     VARCHAR(512) NULL, --表中文名称
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
HYREN_CONSUMEDES                                  CHAR(1) NOT NULL, --海云内部消费目的地
TABLE_EN_NAME                                     VARCHAR(512) NOT NULL, --表英文名称
EXECUTE_STATE                                     CHAR(3) default '100' NULL, --运行状态
REMARK                                            VARCHAR(512) NULL, --备注
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT SDM_INNER_TABLE_PK PRIMARY KEY(TABLE_ID)   );

--流数据海云内部字段信息登记表
DROP TABLE IF EXISTS SDM_INNER_COLUMN ;
CREATE TABLE SDM_INNER_COLUMN(
FIELD_ID                                          DECIMAL(10) NOT NULL, --字段Id
FIELD_CN_NAME                                     VARCHAR(512) NULL, --字段中文名称
FIELD_EN_NAME                                     VARCHAR(512) NOT NULL, --字段英文名称
FIELD_TYPE                                        VARCHAR(512) NOT NULL, --字段类型
FIELD_DESC                                        VARCHAR(200) NULL, --字段描述
REMARK                                            VARCHAR(512) NULL, --备注
TABLE_ID                                          DECIMAL(10) NOT NULL, --表id
CONSTRAINT SDM_INNER_COLUMN_PK PRIMARY KEY(FIELD_ID)   );

--数据权限设置表
DROP TABLE IF EXISTS DATA_AUTH ;
CREATE TABLE DATA_AUTH(
DA_ID                                             DECIMAL(10) NOT NULL, --数据权限设置ID
APPLY_DATE                                        CHAR(8) NOT NULL, --申请日期
APPLY_TIME                                        CHAR(6) NOT NULL, --申请时间
APPLY_TYPE                                        CHAR(1) NOT NULL, --申请类型
AUTH_TYPE                                         CHAR(1) NOT NULL, --权限类型
AUDIT_DATE                                        CHAR(8) NULL, --审核日期
AUDIT_TIME                                        CHAR(6) NULL, --审核时间
AUDIT_USERID                                      DECIMAL(10) NULL, --审核人ID
AUDIT_NAME                                        VARCHAR(512) NULL, --审核人名称
FILE_ID                                           VARCHAR(40) NOT NULL, --文件编号
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
DEP_ID                                            DECIMAL(10) NOT NULL, --部门ID
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
SOURCE_ID                                         DECIMAL(10) NOT NULL, --数据源ID
COLLECT_SET_ID                                    DECIMAL(10) NOT NULL, --数据库设置id
CONSTRAINT DATA_AUTH_PK PRIMARY KEY(DA_ID)   );

--爬虫数据源
DROP TABLE IF EXISTS CREEPER_SOURCE ;
CREATE TABLE CREEPER_SOURCE(
CS_ID                                             DECIMAL(10) NOT NULL, --爬虫数据源id
CREATE_ID                                         DECIMAL(10) NOT NULL, --用户ID
SOURCE_NAME                                       VARCHAR(512) NOT NULL, --数据源名称
SOURCE_IP                                         VARCHAR(50) NOT NULL, --数据源所在服务器IP
SOURCE_PORT                                       VARCHAR(10) NOT NULL, --数据源服务器端口
FRE_WEEK                                          VARCHAR(512) NULL, --周
FRE_MONTH                                         VARCHAR(512) NULL, --月
FRE_DAY                                           VARCHAR(512) NULL, --天
EXECUTE_TIME                                      CHAR(6) NOT NULL, --执行时间
SOURCE_STATUS                                     CHAR(1) NOT NULL, --数据源状态
CRON_EXPRESSION                                   VARCHAR(512) NOT NULL, --quartz执行表达式
FILE_PATH                                         VARCHAR(512) NULL, --文件存储路径
START_DATE                                        CHAR(8) NOT NULL, --开始日期
END_DATE                                          CHAR(8) NOT NULL, --结束日期
AGENT_DATE                                        CHAR(8) NULL, --数据源日期
AGENT_TIME                                        CHAR(6) NULL, --数据源时间
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CS_REMARK                                         VARCHAR(512) NULL, --备注
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT CREEPER_SOURCE_PK PRIMARY KEY(CS_ID)   );

--信号文件入库信息
DROP TABLE IF EXISTS SIGNAL_FILE ;
CREATE TABLE SIGNAL_FILE(
SIGNAL_ID                                         DECIMAL(10) NOT NULL, --信号id
IS_INTO_HBASE                                     CHAR(1) NOT NULL, --是否入hbase
IS_COMPRESSION                                    CHAR(1) default '0' NOT NULL, --Hbase是使用压缩
IS_INTO_HIVE                                      CHAR(1) NOT NULL, --是否入hive
IS_MPP                                            CHAR(1) NOT NULL, --是否为MPP
TABLE_TYPE                                        CHAR(1) NOT NULL, --是内部表还是外部表
IS_FULLINDEX                                      CHAR(1) NOT NULL, --是否创建全文索引
FILE_FORMAT                                       CHAR(1) NOT NULL, --文件格式
IS_SOLR_HBASE                                     CHAR(1) default '1' NOT NULL, --是否使用solrOnHbase
IS_CBD                                            CHAR(1) default '1' NOT NULL, --是否使用carbondata
DATABASE_ID                                       DECIMAL(10) NOT NULL, --数据库设置id
CONSTRAINT SIGNAL_FILE_PK PRIMARY KEY(SIGNAL_ID)   );

--爬虫文件源设置
DROP TABLE IF EXISTS CREEPER_FILE ;
CREATE TABLE CREEPER_FILE(
FILE_SOURCE_ID                                    DECIMAL(10) NOT NULL, --文件源ID
FILE_SOURCE_PATH                                  VARCHAR(512) NOT NULL, --文件源路径
IS_PDF                                            CHAR(1) NOT NULL, --PDF文件
IS_OFFICE                                         CHAR(1) NOT NULL, --office文件
IS_TEXT                                           CHAR(1) NOT NULL, --文本文件
IS_VIDEO                                          CHAR(1) NOT NULL, --视频文件
IS_AUDIO                                          CHAR(1) NOT NULL, --音频文件
IS_IMAGE                                          CHAR(1) NOT NULL, --图片文件
IS_OTHER                                          CHAR(1) NOT NULL, --其他
FILE_REMARK                                       VARCHAR(512) NULL, --备注
CS_ID                                             DECIMAL(10) NOT NULL, --爬虫数据源id
CONSTRAINT CREEPER_FILE_PK PRIMARY KEY(FILE_SOURCE_ID)   );

--爬虫agent
DROP TABLE IF EXISTS CREEPER_AGENT ;
CREATE TABLE CREEPER_AGENT(
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
CREATE_ID                                         DECIMAL(10) NOT NULL, --用户ID
AGENT_NAME                                        VARCHAR(512) NOT NULL, --Agent名称
AGENT_IP                                          VARCHAR(50) NOT NULL, --Agent所在服务器IP
AGENT_PORT                                        VARCHAR(10) NULL, --agent服务器端口
AGENT_STATUS                                      CHAR(1) NOT NULL, --agent状态
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
FILE_PATH                                         VARCHAR(512) NULL, --文件存储路径
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CMSG_ID                                           DECIMAL(10) NOT NULL, --配置ID
CONSTRAINT CREEPER_AGENT_PK PRIMARY KEY(AGENT_ID)   );

--站点管理
DROP TABLE IF EXISTS SITE_MSG ;
CREATE TABLE SITE_MSG(
SITE_ID                                           DECIMAL(10) NOT NULL, --站点ID
SITE_NAME                                         VARCHAR(512) NOT NULL, --名称
SITE_URL                                          VARCHAR(512) NOT NULL, --站点URL
SITE_REMARK                                       VARCHAR(512) NULL, --备注
CONSTRAINT SITE_MSG_PK PRIMARY KEY(SITE_ID)   );

--抓取配置管理
DROP TABLE IF EXISTS CONFIGURE_MSG ;
CREATE TABLE CONFIGURE_MSG(
CMSG_ID                                           DECIMAL(10) NOT NULL, --配置ID
CONFIGURE_NAME                                    VARCHAR(512) NOT NULL, --配置名称
FILES_TYPE                                        VARCHAR(100) NULL, --下载文件类型
STOP_CONDITIONS                                   CHAR(1) NULL, --停止抓取条件
PAGES_COUNT                                       DECIMAL(16) default 0 NULL, --抓取批次（网页层数）
URL_COUNT                                         DECIMAL(16) default 0 NULL, --总URL数据
DOWNLOAD_SIZE                                     DECIMAL(16,2) default 0 NULL, --总共下载页面大小
GRAB_TIME                                         CHAR(6) NOT NULL, --抓取停止时间
FRE_WEEK                                          VARCHAR(512) NULL, --周
FRE_MONTH                                         VARCHAR(512) NULL, --月
FRE_DAY                                           VARCHAR(512) NULL, --天
EXECUTE_TIME                                      CHAR(6) NOT NULL, --执行时间
CRON_EXPRESSION                                   VARCHAR(512) NULL, --quartz执行表达式
START_DATE                                        CHAR(8) NOT NULL, --开始日期
END_DATE                                          CHAR(8) NOT NULL, --结束日期
AGENT_DATE                                        CHAR(8) NOT NULL, --agent日期
AGENT_TIME                                        CHAR(6) NOT NULL, --agent时间
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
IS_CUSTOM                                         CHAR(1) NOT NULL, --是否需要自定义解析方法
CMSG_REMARK                                       VARCHAR(512) NULL, --备注
CONSTRAINT CONFIGURE_MSG_PK PRIMARY KEY(CMSG_ID)   );

--模型分类信息
DROP TABLE IF EXISTS EDW_MODAL_CATEGORY ;
CREATE TABLE EDW_MODAL_CATEGORY(
CATEGORY_ID                                       DECIMAL(10) NOT NULL, --模型分类id
CATEGORY_NAME                                     VARCHAR(512) NOT NULL, --分类名称
CATEGORY_DESC                                     VARCHAR(200) NULL, --分类描述
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CATEGORY_SEQ                                      VARCHAR(512) NULL, --分类序号
CATEGORY_NUM                                      VARCHAR(512) NOT NULL, --分类编号
CREATE_ID                                         DECIMAL(10) NOT NULL, --创建用户
PARENT_CATEGORY_ID                                DECIMAL(10) NOT NULL, --模型分类id
MODAL_PRO_ID                                      DECIMAL(10) NOT NULL, --模型工程id
CONSTRAINT EDW_MODAL_CATEGORY_PK PRIMARY KEY(CATEGORY_ID)   );

--agent和站点关系表
DROP TABLE IF EXISTS AGENT_RELATION_SITE ;
CREATE TABLE AGENT_RELATION_SITE(
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
SITE_ID                                           DECIMAL(10) NOT NULL, --站点ID
CONSTRAINT AGENT_RELATION_SITE_PK PRIMARY KEY(AGENT_ID,SITE_ID)   );

--数据消费至二进制文件
DROP TABLE IF EXISTS SDM_CONER_FILE ;
CREATE TABLE SDM_CONER_FILE(
FILE_ID                                           DECIMAL(10) NOT NULL, --二进制文件ID
FILE_NAME                                         VARCHAR(512) NOT NULL, --文件名
FILE_PATH                                         VARCHAR(512) NOT NULL, --文件路径
TIME_INTERVAL                                     VARCHAR(10) NOT NULL, --获取不到数据重发时间
REMARK                                            VARCHAR(512) NULL, --备注
SDM_DES_ID                                        DECIMAL(10) NULL, --配置id
CONSTRAINT SDM_CONER_FILE_PK PRIMARY KEY(FILE_ID)   );

--自定义页面解析方法列表
DROP TABLE IF EXISTS CM_RELATION_CP ;
CREATE TABLE CM_RELATION_CP(
CP_ID                                             DECIMAL(10) NOT NULL, --自定义解析id
METHOD_NAME                                       VARCHAR(512) NOT NULL, --方法名称
CP_EXPRESSIONS                                    VARCHAR(200) NULL, --正则表达式
PC_REMARK                                         VARCHAR(512) NULL, --备注
CONSTRAINT CM_RELATION_CP_PK PRIMARY KEY(CP_ID)   );

--流数据管理消费至druid
DROP TABLE IF EXISTS SDM_CON_DRUID ;
CREATE TABLE SDM_CON_DRUID(
DRUID_ID                                          DECIMAL(10) NOT NULL, --druid编号
TABLE_NAME                                        VARCHAR(64) NOT NULL, --druid英文表名
TABLE_CNAME                                       VARCHAR(64) NOT NULL, --druid中文表名
TIMESTAMP_COLUM                                   VARCHAR(64) NOT NULL, --时间戳字段
TIMESTAMP_FORMAT                                  CHAR(1) NOT NULL, --时间戳字段格式
TIMESTAMP_PAT                                     VARCHAR(128) NULL, --时间戳转换表达式
DATA_TYPE                                         CHAR(1) NOT NULL, --数据格式类型
DATA_COLUMNS                                      VARCHAR(512) NULL, --数据字段
DATA_PATTERN                                      VARCHAR(512) NULL, --数据格式转换表达式
DATA_FUN                                          VARCHAR(1024) NULL, --数据格式转换函数
IS_TOPICASDRUID                                   CHAR(1) NOT NULL, --是否使用topic名作为druid表名
DRUID_SERVTYPE                                    CHAR(1) NOT NULL, --druid服务类型
SDM_DES_ID                                        DECIMAL(10) NOT NULL, --配置id
CONSTRAINT SDM_CON_DRUID_PK PRIMARY KEY(DRUID_ID)   );

--配置与解析关系
DROP TABLE IF EXISTS CP_RELATION_CM ;
CREATE TABLE CP_RELATION_CM(
CMSG_ID                                           DECIMAL(10) NOT NULL, --配置ID
CP_ID                                             DECIMAL(10) NOT NULL, --自定义解析id
CONSTRAINT CP_RELATION_CM_PK PRIMARY KEY(CMSG_ID,CP_ID)   );

--druid字段配置表
DROP TABLE IF EXISTS SDM_CON_DRUID_COL ;
CREATE TABLE SDM_CON_DRUID_COL(
DRUID_COL_ID                                      DECIMAL(10) NOT NULL, --druid字段编号
COLUMN_NAME                                       VARCHAR(64) NOT NULL, --字段英文名称
COLUMN_CNAME                                      VARCHAR(64) NOT NULL, --字段中文名称
COLUMN_TYOE                                       VARCHAR(10) NOT NULL, --字段类型
DRUID_ID                                          DECIMAL(10) NOT NULL, --druid编号
CONSTRAINT SDM_CON_DRUID_COL_PK PRIMARY KEY(DRUID_COL_ID)   );

--爬虫Agent结果情况
DROP TABLE IF EXISTS CREEPER_CASE ;
CREATE TABLE CREEPER_CASE(
CC_ID                                             DECIMAL(10) NOT NULL, --爬虫情况ID
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
START_DATE                                        CHAR(8) NULL, --开始日期
START_TIME                                        CHAR(6) NULL, --开始时间
END_DATE                                          CHAR(8) NULL, --结束日期
END_TIME                                          CHAR(6) NULL, --结束时间
START_UPLOAD_DATE                                 CHAR(8) NULL, --开始上传日期
START_UPLOAD_TIME                                 CHAR(6) NULL, --开始上传时间
EXECUTE_LENGTH                                    VARCHAR(10) NULL, --运行时长
ALREADY_COUNT                                     DECIMAL(16) default 0 NOT NULL, --已经爬取站点数
GRAB_COUNT                                        DECIMAL(16) default 0 NOT NULL, --抓取网页数
DOWNLOAD_PAGE                                     DECIMAL(16) default 0 NOT NULL, --下载网页数
THREADS_COUNT                                     DECIMAL(16) default 0 NOT NULL, --启动线程数
IS_GRABSUCCEED                                    CHAR(1) NOT NULL, --是否成功抓取
EXECUTE_STATE                                     CHAR(2) NOT NULL, --运行状态
CC_REMARK                                         VARCHAR(512) NULL, --备注
CONSTRAINT CREEPER_CASE_PK PRIMARY KEY(CC_ID)   );

--模型后置作业表信息
DROP TABLE IF EXISTS EDW_HZZY_TABLEINFO ;
CREATE TABLE EDW_HZZY_TABLEINFO(
HZZY_ID                                           DECIMAL(10) NOT NULL, --后置作业表id
HZZY_TBNAME                                       VARCHAR(512) NOT NULL, --后置作业表名称
CREATE_DATE                                       CHAR(8) NOT NULL, --表创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --表创建时间
REMARK                                            VARCHAR(512) NULL, --备注
TABNAME                                           VARCHAR(512) NOT NULL, --表名称
TABLE_ID                                          DECIMAL(10) NOT NULL, --表id
ST_DT                                             CHAR(8) NOT NULL, --开始日期
ST_TIME                                           CHAR(6) NOT NULL, --开始时间
CONSTRAINT EDW_HZZY_TABLEINFO_PK PRIMARY KEY(HZZY_ID)   );

--模型后置作业表字段
DROP TABLE IF EXISTS EDW_HZZY_FIELD ;
CREATE TABLE EDW_HZZY_FIELD(
HZF_ID                                            DECIMAL(10) NOT NULL, --字段id
FIELD_NAME                                        VARCHAR(512) NOT NULL, --字段英文名称
FIELD_CN_NAME                                     VARCHAR(512) NULL, --字段中文名称
FIELD_TYPE                                        VARCHAR(512) NOT NULL, --字段类型
REMARK                                            VARCHAR(512) NULL, --备注
HZZY_ID                                           DECIMAL(10) NOT NULL, --后置作业表id
CONSTRAINT EDW_HZZY_FIELD_PK PRIMARY KEY(HZF_ID)   );

--数据消费至kafka
DROP TABLE IF EXISTS SDM_CON_KAFKA ;
CREATE TABLE SDM_CON_KAFKA(
KAFKA_ID                                          DECIMAL(10) NOT NULL, --kafka_id
KAFKA_BUS_CLASS                                   VARCHAR(200) NULL, --kafka业务处理类
KAFKA_BUS_TYPE                                    CHAR(1) NOT NULL, --kafka业务类类型
SDM_PARTITION                                     CHAR(1) NOT NULL, --分区方式
SDM_PARTITION_NAME                                VARCHAR(512) NULL, --自定义分区类
TOPIC                                             VARCHAR(512) NOT NULL, --消息主题
BOOTSTRAP_SERVERS                                 VARCHAR(512) NOT NULL, --流服务主机
ACKS                                              VARCHAR(512) NOT NULL, --成功确认等级
RETRIES                                           DECIMAL(16) default 0 NOT NULL, --重试次数
MAX_REQUEST_SIZE                                  VARCHAR(512) NOT NULL, --单条记录阀值
BATCH_SIZE                                        DECIMAL(16) default 0 NOT NULL, --批量大小
LINGER_MS                                         VARCHAR(512) NOT NULL, --批处理等待时间
BUFFER_MEMORY                                     VARCHAR(512) NOT NULL, --缓存大小
COMPRESSION_TYPE                                  VARCHAR(512) NOT NULL, --压缩类型
SYNC                                              CHAR(1) NOT NULL, --是否同步
INTERCEPTOR_CLASSES                               VARCHAR(512) NULL, --拦截器
SDM_DES_ID                                        DECIMAL(10) NOT NULL, --配置id
CONSTRAINT SDM_CON_KAFKA_PK PRIMARY KEY(KAFKA_ID)   );

--源文件属性
DROP TABLE IF EXISTS SOURCE_FILE_ATTRIBUTE ;
CREATE TABLE SOURCE_FILE_ATTRIBUTE(
FILE_ID                                           VARCHAR(40) NOT NULL, --文件编号
IS_IN_HBASE                                       CHAR(1) default '1' NOT NULL, --是否已进入HBASE
SEQENCING                                         DECIMAL(16) default 0 NOT NULL, --排序计数
COLLECT_TYPE                                      CHAR(1) NOT NULL, --采集类型
ORIGINAL_NAME                                     VARCHAR(512) NOT NULL, --原始文件名或表中文名称
ORIGINAL_UPDATE_DATE                              CHAR(8) NOT NULL, --原文件最后修改日期
ORIGINAL_UPDATE_TIME                              CHAR(6) NOT NULL, --原文件最后修改时间
TABLE_NAME                                        VARCHAR(512) NULL, --采集的原始表名
HBASE_NAME                                        VARCHAR(512) NOT NULL, --系统内对应表名
META_INFO                                         VARCHAR(6000) NULL, --META元信息
STORAGE_DATE                                      CHAR(8) NOT NULL, --入库日期
STORAGE_TIME                                      CHAR(6) NOT NULL, --入库时间
FILE_SIZE                                         DECIMAL(16,2) default 0 NOT NULL, --文件大小
FILE_TYPE                                         VARCHAR(512) NOT NULL, --文件类型
FILE_SUFFIX                                       VARCHAR(512) NOT NULL, --文件后缀
SOURCE_PATH                                       VARCHAR(512) NULL, --文件路径
FILE_MD5                                          VARCHAR(40) NULL, --文件MD5值
FILE_AVRO_PATH                                    VARCHAR(500) NULL, --所在avro文件地址
FILE_AVRO_BLOCK                                   DECIMAL(15) NULL, --所存avro文件block号
IS_BIG_FILE                                       CHAR(1) default '1' NULL, --是否为大文件
IS_CACHE                                          CHAR(1) NULL, --是否本地缓存
FOLDER_ID                                         DECIMAL(10) NULL, --文件夹编号
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
SOURCE_ID                                         DECIMAL(10) NOT NULL, --数据源ID
COLLECT_SET_ID                                    DECIMAL(10) NOT NULL, --数据库设置id
CONSTRAINT SOURCE_FILE_ATTRIBUTE_PK PRIMARY KEY(FILE_ID)   );

--系统表创建信息
DROP TABLE IF EXISTS SYS_TABLE_INFO ;
CREATE TABLE SYS_TABLE_INFO(
INFO_ID                                           DECIMAL(10) NOT NULL, --信息id
TABLE_SPACE                                       VARCHAR(512) NULL, --表空间名称
TABLE_NAME                                        VARCHAR(512) NOT NULL, --表名
CH_NAME                                           VARCHAR(512) NULL, --表中文名称
TABLE_TYPE                                        CHAR(1) NOT NULL, --表的类型
COL_FAMILY                                        VARCHAR(512) NULL, --列族
IN_MEMORY                                         CHAR(1) NULL, --IN_MEMORY
BLOOM_FILTER                                      CHAR(1) NULL, --Bloom过滤器
COMPRESS                                          CHAR(1) NULL, --是否压缩
BLOCK_SIZE                                        INTEGER default 65536 NULL, --数据块大小
DATA_BLOCK_ENCODING                               CHAR(1) NULL, --数据块编码
VERSIONS                                          INTEGER default 3 NULL, --版本数
PRE_SPLIT                                         CHAR(1) NULL, --预分区规则
SPLIT_PARM                                        CHAR(10) default '10' NULL, --分区数
IS_EXTERNAL                                       CHAR(1) NULL, --是否为外部表
STORAGE_PATH                                      VARCHAR(512) NULL, --存储位置
STORAGE_TYPE                                      CHAR(10) NULL, --存储类型
LINE_SEPARATOR                                    VARCHAR(512) NULL, --行分隔符
COLUMN_SEPARATOR                                  VARCHAR(512) NULL, --列分隔符
QUOTE_CHARACTER                                   VARCHAR(512) NULL, --引用符
ESCAPE_CHARACTER                                  VARCHAR(512) NULL, --转义符
BUCKETNUMBER                                      VARCHAR(512) NULL, --指定要创建的桶的数量
CREATE_DATE                                       CHAR(8) NOT NULL, --开始日期
END_DATE                                          CHAR(8) NOT NULL, --结束日期
TABLE_PATTERN                                     CHAR(1) NULL, --carbondta表模式
SOURCE_FORMAT                                     CHAR(1) NULL, --流表来源
KAFKA_SUBSCRIBE                                   VARCHAR(100) NULL, --kafka主题
BOOTSTRAP_SERVERS                                 VARCHAR(125) NULL, --kafka流服务主机
RECORD_FORMAT                                     VARCHAR(50) NULL, --记录格式
INTERVAL                                          VARCHAR(10) NULL, --间隔时间
HDFS_PATH                                         VARCHAR(512) NULL, --hdfs路径
FILE_FORMAT                                       VARCHAR(10) NULL, --HDFS文件格式
STORAGEDATA                                       VARCHAR(2) NULL, --进数方式
IS_TRACE                                          CHAR(1) NULL, --是否数据溯源
REMARK                                            VARCHAR(512) NULL, --备注
CREATE_ID                                         DECIMAL(10) NULL, --用户ID
CONSTRAINT SYS_TABLE_INFO_PK PRIMARY KEY(INFO_ID)   );

--表字段信息
DROP TABLE IF EXISTS SYS_TABLE_COLUMN ;
CREATE TABLE SYS_TABLE_COLUMN(
FIELD_ID                                          DECIMAL(10) NOT NULL, --字段id
FIELD_CH_NAME                                     VARCHAR(512) NULL, --字段中文名称
COLUMN_NAME                                       VARCHAR(512) NOT NULL, --字段名称
COLUMN_TYPE                                       VARCHAR(25) NOT NULL, --字段类型
COLUMN_LENGTH                                     VARCHAR(200) NULL, --字段长度
PARTITION_COLUMN                                  CHAR(1) NOT NULL, --是否为分区列
IS_NULL                                           CHAR(1) NULL, --是否可为空
IS_PRIMATYKEY                                     CHAR(1) NULL, --是否为主键
IS_DICTIONARY                                     CHAR(1) NULL, --是否启用字典编码
IS_INVERTEDINDEX                                  CHAR(1) NULL, --是否禁用倒排索引
IS_BUCKETCOLUMNS                                  CHAR(1) NULL, --是否是Bucketing考虑的列
COLSOURCETAB                                      VARCHAR(512) NULL, --字段来源表名称
COLSOURCECOL                                      VARCHAR(512) NULL, --来源字段
REMARK                                            VARCHAR(512) NULL, --备注
INFO_ID                                           DECIMAL(10) NOT NULL, --信息id
CONSTRAINT SYS_TABLE_COLUMN_PK PRIMARY KEY(FIELD_ID)   );

--源文件属性清册
DROP TABLE IF EXISTS SOURCE_FILE_DETAILED ;
CREATE TABLE SOURCE_FILE_DETAILED(
SFD_ID                                            VARCHAR(40) NOT NULL, --源文件属性清册ID
FILE_ID                                           VARCHAR(40) NOT NULL, --文件编号
ORIGINAL_NAME                                     VARCHAR(512) NOT NULL, --原始文件名或表中文名称
ORIGINAL_UPDATE_DATE                              CHAR(8) NOT NULL, --原文件最后修改日期
ORIGINAL_UPDATE_TIME                              CHAR(6) NOT NULL, --原文件最后修改时间
TABLE_NAME                                        VARCHAR(512) NULL, --表名
META_INFO                                         VARCHAR(6000) NULL, --META元信息
HBASE_NAME                                        VARCHAR(512) NOT NULL, --HBase对应表名
STORAGE_DATE                                      CHAR(8) NOT NULL, --入库日期
STORAGE_TIME                                      CHAR(6) NOT NULL, --入库时间
FILE_SIZE                                         DECIMAL(16,2) default 0 NOT NULL, --文件大小
FILE_TYPE                                         VARCHAR(512) NOT NULL, --文件类型
FILE_SUFFIX                                       VARCHAR(512) NOT NULL, --文件后缀
HDFS_STORAGE_PATH                                 VARCHAR(512) NULL, --hdfs储路径
SOURCE_PATH                                       VARCHAR(512) NOT NULL, --文件路径
FILE_MD5                                          VARCHAR(40) NULL, --文件MD5值
FILE_AVRO_PATH                                    VARCHAR(500) NULL, --所在avro文件地址
FILE_AVRO_BLOCK                                   DECIMAL(15) NULL, --所存avro文件block号
IS_BIG_FILE                                       CHAR(1) default '1' NULL, --是否为大文件
FOLDER_ID                                         DECIMAL(10) NULL, --文件夹编号
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
SOURCE_ID                                         DECIMAL(10) NOT NULL, --数据源ID
COLLECT_SET_ID                                    DECIMAL(10) NOT NULL, --数据库设置id
CONSTRAINT SOURCE_FILE_DETAILED_PK PRIMARY KEY(SFD_ID)   );

--Agent下载信息
DROP TABLE IF EXISTS AGENT_DOWN_INFO ;
CREATE TABLE AGENT_DOWN_INFO(
DOWN_ID                                           DECIMAL(10) NOT NULL, --下载编号(primary)
AGENT_NAME                                        VARCHAR(512) NOT NULL, --Agent名称
AGENT_IP                                          VARCHAR(50) NOT NULL, --Agent IP
AGENT_PORT                                        VARCHAR(10) NOT NULL, --Agent端口
USER_NAME                                         VARCHAR(10) NULL, --用户名
PASSWD                                            VARCHAR(10) NULL, --密码
SAVE_DIR                                          VARCHAR(512) NOT NULL, --存放目录
LOG_DIR                                           VARCHAR(512) NOT NULL, --日志目录
DEPLOY                                            CHAR(1) NOT NULL, --是否部署
AI_DESC                                           VARCHAR(200) NULL, --描述
AGENT_TYPE                                        CHAR(1) NOT NULL, --agent类别
AGENT_ID                                          DECIMAL(10) NULL, --Agent_id
USER_ID                                           DECIMAL(10) NOT NULL, --用户id
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT AGENT_DOWN_INFO_PK PRIMARY KEY(DOWN_ID)   );

--系统参数配置
DROP TABLE IF EXISTS SYS_PARA ;
CREATE TABLE SYS_PARA(
PARA_ID                                           DECIMAL(16) default 0 NOT NULL, --参数ID
PARA_NAME                                         VARCHAR(512) NULL, --para_name
PARA_VALUE                                        VARCHAR(512) NULL, --para_value
PARA_TYPE                                         VARCHAR(512) NULL, --para_type
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT SYS_PARA_PK PRIMARY KEY(PARA_ID)   );

--采集任务分类表
DROP TABLE IF EXISTS COLLECT_JOB_CLASSIFY ;
CREATE TABLE COLLECT_JOB_CLASSIFY(
CLASSIFY_ID                                       DECIMAL(10) NOT NULL, --分类id
CLASSIFY_NUM                                      VARCHAR(512) NOT NULL, --分类编号
CLASSIFY_NAME                                     VARCHAR(512) NOT NULL, --分类名称
REMARK                                            VARCHAR(512) NULL, --备注
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
AGENT_ID                                          DECIMAL(10) NOT NULL, --Agent_id
CONSTRAINT COLLECT_JOB_CLASSIFY_PK PRIMARY KEY(CLASSIFY_ID)   );

--系统备份信息表
DROP TABLE IF EXISTS SYS_DUMP ;
CREATE TABLE SYS_DUMP(
DUMP_ID                                           DECIMAL(10) NOT NULL, --备份id
BAK_DATE                                          CHAR(8) NOT NULL, --备份日期
BAK_TIME                                          CHAR(6) NOT NULL, --备份时间
FILE_SIZE                                         VARCHAR(512) NOT NULL, --文件大小
FILE_NAME                                         VARCHAR(512) NOT NULL, --文件名称
HDFS_PATH                                         VARCHAR(512) NOT NULL, --文件存放hdfs路径
LENGTH                                            VARCHAR(10) NOT NULL, --备份时长
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT SYS_DUMP_PK PRIMARY KEY(DUMP_ID)   );

--定制爬取要求表
DROP TABLE IF EXISTS CUSTOM_REQUEST ;
CREATE TABLE CUSTOM_REQUEST(
CR_ID                                             DECIMAL(10) NOT NULL, --定制爬取要求id
CREATE_ID                                         DECIMAL(10) NOT NULL, --用户ID
URL                                               VARCHAR(512) NULL, --URL地址
FRE_WEEK                                          VARCHAR(512) NULL, --周
FRE_MONTH                                         VARCHAR(512) NULL, --月
FRE_DAY                                           VARCHAR(512) NULL, --天
EXECUTE_TIME                                      CHAR(6) NOT NULL, --执行时间
CRON_EXPRESSION                                   VARCHAR(512) NOT NULL, --quartz执行表达式
START_DATE                                        CHAR(8) NOT NULL, --开始日期
END_DATE                                          CHAR(8) NOT NULL, --结束日期
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CS_ID                                             DECIMAL(10) NOT NULL, --爬虫数据源id
CONSTRAINT CUSTOM_REQUEST_PK PRIMARY KEY(CR_ID)   );

--备份恢复信息表
DROP TABLE IF EXISTS SYS_RECOVER ;
CREATE TABLE SYS_RECOVER(
RE_ID                                             DECIMAL(10) NOT NULL, --恢复id
RE_DATE                                           CHAR(8) NOT NULL, --恢复日期
RE_TIME                                           CHAR(6) NOT NULL, --恢复时间
LENGTH                                            VARCHAR(10) NOT NULL, --恢复时长
REMARK                                            VARCHAR(512) NULL, --备注
DUMP_ID                                           DECIMAL(10) NOT NULL, --备份id
CONSTRAINT SYS_RECOVER_PK PRIMARY KEY(RE_ID)   );

--爬取数据项表
DROP TABLE IF EXISTS CUSTOM_DATA ;
CREATE TABLE CUSTOM_DATA(
CD_ID                                             DECIMAL(10) NOT NULL, --爬取数据项表id
NEED_DATA                                         VARCHAR(512) NULL, --需提取数据项名称
NEED_TYPE                                         CHAR(1) NULL, --需提取数据项类别
CR_ID                                             DECIMAL(10) NOT NULL, --定制爬取要求id
CONSTRAINT CUSTOM_DATA_PK PRIMARY KEY(CD_ID)   );

--列拆分信息表
DROP TABLE IF EXISTS COLUMN_SPLIT ;
CREATE TABLE COLUMN_SPLIT(
COL_ID                                            DECIMAL(10) NOT NULL, --字段编号
COL_NAME                                          VARCHAR(512) NOT NULL, --字段名称
COL_OFFSET                                        VARCHAR(512) NULL, --字段偏移量
SPLIT_SEP                                         VARCHAR(512) NULL, --拆分分隔符
SEQ                                               DECIMAL(16) default 0 NULL, --拆分对应序号
SPLIT_TYPE                                        CHAR(1) NOT NULL, --拆分方式
COL_ZHNAME                                        VARCHAR(512) NULL, --中文名称
REMARK                                            VARCHAR(512) NULL, --备注
COL_TYPE                                          VARCHAR(512) NOT NULL, --字段类型
VALID_S_DATE                                      CHAR(8) NOT NULL, --有效开始日期
VALID_E_DATE                                      CHAR(8) NOT NULL, --有效结束日期
C_ID                                              DECIMAL(10) NOT NULL, --清洗参数编号
COLUMN_ID                                         DECIMAL(10) NOT NULL, --字段ID
CONSTRAINT COLUMN_SPLIT_PK PRIMARY KEY(COL_ID)   );

--列合并信息表
DROP TABLE IF EXISTS COLUMN_MERGE ;
CREATE TABLE COLUMN_MERGE(
COL_ID                                            DECIMAL(10) NOT NULL, --字段编号
COL_NAME                                          VARCHAR(512) NOT NULL, --合并后字段名称
OLD_NAME                                          VARCHAR(512) NOT NULL, --要合并的字段
COL_ZHNAME                                        VARCHAR(512) NULL, --中文名称
REMARK                                            VARCHAR(512) NULL, --备注
COL_TYPE                                          VARCHAR(512) NOT NULL, --字段类型
VALID_S_DATE                                      CHAR(8) NOT NULL, --有效开始日期
VALID_E_DATE                                      CHAR(8) NOT NULL, --有效结束日期
TABLE_ID                                          DECIMAL(10) NOT NULL, --表名ID
CONSTRAINT COLUMN_MERGE_PK PRIMARY KEY(COL_ID)   );

--作业Agent下载信息
DROP TABLE IF EXISTS ETL_AGENT_DOWNINFO ;
CREATE TABLE ETL_AGENT_DOWNINFO(
DOWN_ID                                           DECIMAL(10) NOT NULL, --下载编号(primary)
AGENT_NAME                                        VARCHAR(512) NOT NULL, --Agent名称
AGENT_IP                                          VARCHAR(50) NOT NULL, --Agent IP
USER_NAME                                         VARCHAR(10) NULL, --用户名
PASSWD                                            VARCHAR(10) NULL, --密码
SAVE_DIR                                          VARCHAR(512) NOT NULL, --存放目录
AGENT_TYPE                                        CHAR(1) NOT NULL, --agent类别
USER_ID                                           DECIMAL(10) NOT NULL, --用户id
AI_DESC                                           VARCHAR(200) NULL, --描述
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT ETL_AGENT_DOWNINFO_PK PRIMARY KEY(DOWN_ID)   );

--变量配置表
DROP TABLE IF EXISTS DQ_SYS_VAR_CFG ;
CREATE TABLE DQ_SYS_VAR_CFG(
SYS_VAR_ID                                        DECIMAL(10) NOT NULL, --系统变量编号
VAR_NAME                                          VARCHAR(64) NOT NULL, --变量名
VAR_VALUE                                         VARCHAR(128) NOT NULL, --变量值
APP_UPDT_DT                                       CHAR(8) NOT NULL, --更新日期
APP_UPDT_TI                                       CHAR(6) NOT NULL, --更新时间
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT DQ_SYS_VAR_CFG_PK PRIMARY KEY(SYS_VAR_ID,VAR_NAME)   );

--数据质量规则配置清单表
DROP TABLE IF EXISTS DQ_LIST ;
CREATE TABLE DQ_LIST(
REG_NUM                                           DECIMAL(10) NOT NULL, --规则编号
REG_NAME                                          VARCHAR(100) NULL, --规则名称
LOAD_STRATEGY                                     VARCHAR(32) NULL, --加载策略
GROUP_SEQ                                         VARCHAR(64) NULL, --分组序号
TARGET_TAB                                        VARCHAR(64) NULL, --目标表名
TARGET_KEY_FIELDS                                 VARCHAR(1024) NULL, --目标表关键字段
OPPOSITE_TAB                                      VARCHAR(64) NULL, --比对表名
OPPOSITE_KEY_FIELDS                               VARCHAR(1024) NULL, --比对表关键字段
RANGE_MIN_VAL                                     VARCHAR(64) NULL, --范围区间的最小值
RANGE_MAX_VAL                                     VARCHAR(64) NULL, --范围区间的最大值
LIST_VALS                                         VARCHAR(1024) NULL, --清单值域
CHECK_LIMIT_CONDITION                             VARCHAR(1024) NULL, --检查范围限定条件
SPECIFY_SQL                                       VARCHAR(4000) NULL, --指定SQL
ERR_DATA_SQL                                      VARCHAR(4000) NULL, --异常数据SQL
INDEX_DESC1                                       VARCHAR(500) NULL, --检测指标1含义
INDEX_DESC2                                       VARCHAR(500) NULL, --检测指标2含义
INDEX_DESC3                                       VARCHAR(500) NULL, --检测指标3含义
FLAGS                                             VARCHAR(32) NULL, --标志域
REMARK                                            VARCHAR(4000) NULL, --描述
APP_UPDT_DT                                       CHAR(8) NOT NULL, --更新日期
APP_UPDT_TI                                       CHAR(6) NOT NULL, --更新时间
RULE_TAG                                          VARCHAR(20) NULL, --规则标签
MAIL_RECEIVE                                      VARCHAR(1200) NULL, --接收邮箱
RULE_SRC                                          VARCHAR(50) NULL, --规则来源
IS_SAVEINDEX1                                     CHAR(1) NULL, --是否保存指标1数据
IS_SAVEINDEX2                                     CHAR(1) NULL, --是否保存指标2数据
IS_SAVEINDEX3                                     CHAR(1) NULL, --是否保存指标3数据
CASE_TYPE                                         VARCHAR(32) NOT NULL, --规则类型
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT DQ_LIST_PK PRIMARY KEY(REG_NUM)   );

--数据质量校验结果表
DROP TABLE IF EXISTS DQ_RESULT ;
CREATE TABLE DQ_RESULT(
TASK_ID                                           VARCHAR(21) NOT NULL, --任务编号
VERIFY_DATE                                       CHAR(8) NULL, --校验会计日期
TARGET_TAB                                        VARCHAR(100) NULL, --目标表名
TARGET_KEY_FIELDS                                 VARCHAR(1024) NULL, --目标表关键字段
START_DATE                                        CHAR(8) NULL, --执行开始日期
START_TIME                                        CHAR(6) NULL, --执行开始时间
END_DATE                                          CHAR(8) NULL, --执行结束日期
END_TIME                                          CHAR(6) NULL, --执行结束时间
ELAPSED_MS                                        INTEGER default 0 NULL, --执行耗时
VERIFY_RESULT                                     CHAR(1) NULL, --校验结果
CHECK_INDEX1                                      INTEGER default 0 NULL, --检查指标1结果
CHECK_INDEX2                                      INTEGER default 0 NULL, --检查指标2结果
CHECK_INDEX3                                      VARCHAR(1024) NULL, --检查指标3结果
INDEX_DESC1                                       VARCHAR(500) NULL, --检查指标1含义
INDEX_DESC2                                       VARCHAR(500) NULL, --检查指标2含义
INDEX_DESC3                                       VARCHAR(500) NULL, --检查指标3含义
ERRNO                                             VARCHAR(1024) NULL, --校验错误码
VERIFY_SQL                                        VARCHAR(4000) NULL, --校验SQL
ERR_DTL_SQL                                       VARCHAR(4000) NULL, --异常明细SQL
REMARK                                            VARCHAR(512) NULL, --备注
DL_STAT                                           VARCHAR(5) NULL, --处理状态
EXEC_MODE                                         VARCHAR(10) NULL, --执行方式
ERR_DTL_FILE_NAME                                 VARCHAR(100) NULL, --异常数据文件名
IS_SAVEINDEX1                                     CHAR(1) NULL, --是否保存指标1数据
IS_SAVEINDEX2                                     CHAR(1) NULL, --是否保存指标2数据
IS_SAVEINDEX3                                     CHAR(1) NULL, --是否保存指标3数据
CASE_TYPE                                         VARCHAR(32) NOT NULL, --规则类型
REG_NUM                                           DECIMAL(10) NOT NULL, --规则编号
CONSTRAINT DQ_RESULT_PK PRIMARY KEY(TASK_ID)   );

--校验结果处理日志
DROP TABLE IF EXISTS DQ_DL_LOG ;
CREATE TABLE DQ_DL_LOG(
TASK_ID                                           DECIMAL(10) NOT NULL, --任务编号
DL_TIME                                           VARCHAR(32) NOT NULL, --处理时间
PRCS_STT                                          VARCHAR(100) NULL, --流程状态
ACTN                                              VARCHAR(100) NULL, --动作
DL_DSCR                                           VARCHAR(100) NULL, --处理描述
ATTC                                              VARCHAR(3072) NULL, --附件
FL_NM                                             VARCHAR(100) NULL, --文件名
IS_TOP                                            CHAR(1) NULL, --是否置顶
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
REG_NUM                                           DECIMAL(10) NOT NULL, --规则编号
CONSTRAINT DQ_DL_LOG_PK PRIMARY KEY(TASK_ID,DL_TIME)   );

--数据质量规则类型定义表
DROP TABLE IF EXISTS DQ_RULE_TYPE_DEF ;
CREATE TABLE DQ_RULE_TYPE_DEF(
CASE_TYPE                                         VARCHAR(32) NOT NULL, --规则类型
CASE_TYPE_DESC                                    VARCHAR(500) NULL, --规则类型描述
INDEX_DESC1                                       VARCHAR(500) NULL, --检测指标1含义
INDEX_DESC2                                       VARCHAR(500) NULL, --检测指标2含义
INDEX_DESC3                                       VARCHAR(500) NULL, --检测指标3含义
REMARK                                            VARCHAR(512) NULL, --说明
CONSTRAINT DQ_RULE_TYPE_DEF_PK PRIMARY KEY(CASE_TYPE)   );

--外部检查申请执行日志
DROP TABLE IF EXISTS DQ_EXT_REQ_EXE_LOG ;
CREATE TABLE DQ_EXT_REQ_EXE_LOG(
REQ_ID                                            DECIMAL(10) NOT NULL, --申请编号
TASK_ID                                           DECIMAL(10) NOT NULL, --任务编号
DL_TIME                                           VARCHAR(32) NOT NULL, --处理时间
CONSTRAINT DQ_EXT_REQ_EXE_LOG_PK PRIMARY KEY(REQ_ID)   );

--外部检查申请日志
DROP TABLE IF EXISTS DQ_EXT_REQ_LOG ;
CREATE TABLE DQ_EXT_REQ_LOG(
REQ_ID                                            DECIMAL(10) NOT NULL, --申请编号
ASS_REQ_ID                                        VARCHAR(50) NULL, --关联申请号
REQ_TYP                                           CHAR(1) NULL, --申请类型
CHK_DT                                            CHAR(8) NOT NULL, --检查日期
CHK_TIME                                          CHAR(6) NOT NULL, --检查时间
REQ_RE                                            VARCHAR(1) NULL, --受理返回状态
FIN_STS                                           VARCHAR(1) NULL, --完成状态
REQ_TM                                            VARCHAR(8) NULL, --受理时间
FIN_TM                                            VARCHAR(8) NULL, --结束时间
EXT_JOB_ID                                        DECIMAL(10) NOT NULL, --外部作业编号
TASK_ID                                           DECIMAL(10) NOT NULL, --任务编号
CONSTRAINT DQ_EXT_REQ_LOG_PK PRIMARY KEY(REQ_ID)   );

--外部检查作业与规则关系
DROP TABLE IF EXISTS DQ_EXT_JOB_RULE_RELA ;
CREATE TABLE DQ_EXT_JOB_RULE_RELA(
EXT_JOB_ID                                        DECIMAL(10) NOT NULL, --外部作业编号
TASK_ID                                           DECIMAL(10) NOT NULL, --任务编号
DL_TIME                                           VARCHAR(32) NOT NULL, --处理时间
CONSTRAINT DQ_EXT_JOB_RULE_RELA_PK PRIMARY KEY(EXT_JOB_ID,TASK_ID)   );

--系统帮助提示信息表
DROP TABLE IF EXISTS SYS_HELP_INFM ;
CREATE TABLE SYS_HELP_INFM(
HELP_INFM_ID                                      VARCHAR(8) NOT NULL, --帮助提示编号
HELP_INFM_DESC                                    VARCHAR(500) NULL, --帮助提示描述
HELP_INFM_DTL                                     VARCHAR(2000) NULL, --帮助提示详细信息
CONSTRAINT SYS_HELP_INFM_PK PRIMARY KEY(HELP_INFM_ID)   );

--数据质量指标3数据记录表
DROP TABLE IF EXISTS DQ_INDEX3RECORD ;
CREATE TABLE DQ_INDEX3RECORD(
RECORD_ID                                         DECIMAL(10) NOT NULL, --记录编号
TABLE_NAME                                        VARCHAR(64) NOT NULL, --数据表名
TABLE_COL                                         VARCHAR(10000) NOT NULL, --数据表字段
TABLE_SIZE                                        DECIMAL(16,2) default 0 NOT NULL, --数据表大小
DQC_TS                                            VARCHAR(8) NOT NULL, --表空间名
FILE_TYPE                                         CHAR(1) NOT NULL, --数据物理文件类型
FILE_PATH                                         VARCHAR(512) NOT NULL, --数据物理文件路径
RECORD_DATE                                       CHAR(8) NOT NULL, --记录日期
RECORD_TIME                                       CHAR(6) NOT NULL, --记录时间
TASK_ID                                           VARCHAR(21) NOT NULL, --任务编号
CONSTRAINT DQ_INDEX3RECORD_PK PRIMARY KEY(RECORD_ID)   );

--无效表信息
DROP TABLE IF EXISTS FAILURE_TABLE_INFO ;
CREATE TABLE FAILURE_TABLE_INFO(
FAILURE_TABLE_ID                                  DECIMAL(10) NOT NULL, --表id
TABLE_CN_NAME                                     VARCHAR(512) NULL, --表中文名
TABLE_EN_NAME                                     VARCHAR(512) NOT NULL, --表英文名
TABLE_SOURCE                                      CHAR(3) NOT NULL, --表来源
TABLE_META_INFO                                   VARCHAR(2000) NOT NULL, --表元信息
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT FAILURE_TABLE_INFO_PK PRIMARY KEY(FAILURE_TABLE_ID)   );

--无效表列信息
DROP TABLE IF EXISTS FAILURE_COLUMN_INFO ;
CREATE TABLE FAILURE_COLUMN_INFO(
FAILURE_COLUMN_ID                                 DECIMAL(10) NOT NULL, --列id
COLUMN_SOURCE                                     CHAR(3) NOT NULL, --字段来源
COLUMN_META_INFO                                  VARCHAR(5000) NOT NULL, --字段元信息
REMARK                                            VARCHAR(512) NULL, --备注
FAILURE_TABLE_ID                                  DECIMAL(10) NOT NULL, --表id
CONSTRAINT FAILURE_COLUMN_INFO_PK PRIMARY KEY(FAILURE_COLUMN_ID)   );

--接口信息表
DROP TABLE IF EXISTS INTERFACE_INFO ;
CREATE TABLE INTERFACE_INFO(
INTERFACE_ID                                      DECIMAL(10) NOT NULL, --接口ID
URL                                               VARCHAR(512) NOT NULL, --请求地址
INTERFACE_NAME                                    VARCHAR(512) NOT NULL, --接口名称
INTERFACE_TYPE                                    CHAR(1) NOT NULL, --接口类型
INTERFACE_STATE                                   CHAR(1) NOT NULL, --接口状态
INTERFACE_CODE                                    VARCHAR(100) NOT NULL, --接口代码
INTERFACE_NOTE                                    VARCHAR(512) NULL, --备注
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT INTERFACE_INFO_PK PRIMARY KEY(INTERFACE_ID)   );

--图算法作业信息
DROP TABLE IF EXISTS GRAPH_ARITHMETIC ;
CREATE TABLE GRAPH_ARITHMETIC(
ARITHMETIC_ID                                     DECIMAL(10) NOT NULL, --图算法作业ID
ARITHMETIC_NAME                                   VARCHAR(512) NOT NULL, --算法名称
VERTEX                                            VARCHAR(512) NULL, --点集数据表名称
EDGE                                              VARCHAR(512) NOT NULL, --边集数据表名称
HIVEDB                                            VARCHAR(512) NOT NULL, --Hive数据库名称
ARITHMETIC_TYPE                                   VARCHAR(512) NOT NULL, --算法类型
ARITHMETIC                                        VARCHAR(512) NOT NULL, --算法参数
ARITHMETIC_DESCRIBE                               VARCHAR(200) NULL, --算法作业描述
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT GRAPH_ARITHMETIC_PK PRIMARY KEY(ARITHMETIC_ID)   );

--接口使用信息表
DROP TABLE IF EXISTS INTERFACE_USE ;
CREATE TABLE INTERFACE_USE(
INTERFACE_USE_ID                                  DECIMAL(10) NOT NULL, --接口使用ID
URL                                               VARCHAR(512) NOT NULL, --请求地址
INTERFACE_NAME                                    VARCHAR(512) NOT NULL, --接口名称
INTERFACE_CODE                                    VARCHAR(100) NOT NULL, --接口代码
THEIR_TYPE                                        CHAR(1) NOT NULL, --接口所属类型
USER_NAME                                         VARCHAR(512) NOT NULL, --用户名称
CLASSIFY_NAME                                     VARCHAR(512) NULL, --分类名称
CREATE_ID                                         DECIMAL(10) default 0 NOT NULL, --创建者id
USE_STATE                                         CHAR(1) NOT NULL, --使用状态
START_USE_DATE                                    CHAR(8) NOT NULL, --开始使用日期
USE_VALID_DATE                                    CHAR(8) NOT NULL, --使用有效日期
INTERFACE_NOTE                                    VARCHAR(512) NULL, --备注
INTERFACE_ID                                      DECIMAL(10) NOT NULL, --接口ID
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT INTERFACE_USE_PK PRIMARY KEY(INTERFACE_USE_ID)   );

--图作业保存路劲信息
DROP TABLE IF EXISTS GRAPHSAVEPATH ;
CREATE TABLE GRAPHSAVEPATH(
GRAPH_ID                                          DECIMAL(10) NOT NULL, --图作业ID
JOB_STORAGE_PATH                                  VARCHAR(512) NOT NULL, --hdfs存储路径
TABLENAME                                         VARCHAR(512) NOT NULL, --结果表名
TABLE_SPACE                                       VARCHAR(512) NOT NULL, --表空间
REMARK                                            VARCHAR(512) NULL, --备注
ARITHMETIC_ID                                     DECIMAL(10) NULL, --图算法作业ID
CONSTRAINT GRAPHSAVEPATH_PK PRIMARY KEY(GRAPH_ID)   );

--表使用信息表
DROP TABLE IF EXISTS TABLE_USE_INFO ;
CREATE TABLE TABLE_USE_INFO(
USE_ID                                            DECIMAL(10) NOT NULL, --表使用ID
HBASE_NAME                                        VARCHAR(512) NOT NULL, --HBase表名
ORIGINAL_NAME                                     VARCHAR(512) NOT NULL, --原始文件名称
TABLE_BLSYSTEM                                    CHAR(3) NOT NULL, --数据表所属系统
TABLE_NOTE                                        VARCHAR(512) NULL, --表说明
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT TABLE_USE_INFO_PK PRIMARY KEY(USE_ID)   );

--流数据用户消费申请表
DROP TABLE IF EXISTS SDM_USER_PERMISSION ;
CREATE TABLE SDM_USER_PERMISSION(
APP_ID                                            DECIMAL(10) NOT NULL, --申请id
TOPIC_ID                                          DECIMAL(10) NOT NULL, --topic_id
PRODUCE_USER                                      DECIMAL(10) NOT NULL, --用户ID
CONSUME_USER                                      DECIMAL(10) NOT NULL, --用户ID
SDM_RECEIVE_ID                                    DECIMAL(10) NOT NULL, --流数据管理
APPLICATION_STATUS                                CHAR(1) NOT NULL, --流数据申请状态
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT SDM_USER_PERMISSION_PK PRIMARY KEY(APP_ID)   );

--HBase表参数信息
DROP TABLE IF EXISTS HBASE_PARAMETER_INFO ;
CREATE TABLE HBASE_PARAMETER_INFO(
PARAMETER_ID                                      DECIMAL(10) NOT NULL, --参数ID
TABLE_COLUMN_NAME                                 VARCHAR(20000) NOT NULL, --表列名称
IS_FLAG                                           CHAR(1) NOT NULL, --是否标识
REMARK                                            VARCHAR(512) NULL, --备注
USE_ID                                            DECIMAL(10) NOT NULL, --表使用ID
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT HBASE_PARAMETER_INFO_PK PRIMARY KEY(PARAMETER_ID)   );

--carbondata预聚合信息表
DROP TABLE IF EXISTS CB_PREAGGREGATE ;
CREATE TABLE CB_PREAGGREGATE(
AGG_ID                                            DECIMAL(10) NOT NULL, --预聚合id
DATATABLE_ID                                      DECIMAL(10) NOT NULL, --数据表id
AGG_NAME                                          VARCHAR(512) NOT NULL, --预聚合名称
AGG_SQL                                           VARCHAR(512) NOT NULL, --预聚合SQL
AGG_DATE                                          CHAR(8) NOT NULL, --日期
AGG_TIME                                          CHAR(6) NOT NULL, --时间
AGG_STATUS                                        CHAR(3) default '105' NULL, --预聚合是否成功
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT CB_PREAGGREGATE_PK PRIMARY KEY(AGG_ID)   );

--二级索引信息表
DROP TABLE IF EXISTS INDEXES_INFO ;
CREATE TABLE INDEXES_INFO(
INDEXES_ID                                        DECIMAL(10) NOT NULL, --索引ID
INDEXES_FIELD                                     VARCHAR(512) NOT NULL, --索引字段
INDEXES_SELECT_FIELD                              VARCHAR(512) NULL, --查询字段
INDEXES_TYPE                                      CHAR(10) NOT NULL, --索引类型
FILE_ID                                           VARCHAR(40) NOT NULL, --文件编号
CONSTRAINT INDEXES_INFO_PK PRIMARY KEY(INDEXES_ID)   );

--任务/topic映射表
DROP TABLE IF EXISTS SDM_KSQL_TABLE ;
CREATE TABLE SDM_KSQL_TABLE(
SDM_KSQL_ID                                       DECIMAL(10) NOT NULL, --映射表主键
SDM_RECEIVE_ID                                    DECIMAL(10) NOT NULL, --流数据管理
STRAM_TABLE                                       VARCHAR(512) NOT NULL, --表名
SDM_TOP_NAME                                      VARCHAR(100) NULL, --topic名称
IS_CREATE_SQL                                     CHAR(1) NOT NULL, --是否基于映射表创建
TABLE_TYPE                                        VARCHAR(50) NOT NULL, --数据表格式（流、表、目的地）
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
EXECUTE_SQL                                       VARCHAR(6000) NOT NULL, --执行的sql
CONSUMER_NAME                                     VARCHAR(512) NOT NULL, --消费名称
JOB_DESC                                          VARCHAR(200) NULL, --任务描述
AUTO_OFFSET                                       VARCHAR(10) NULL, --数据拉取初始位置
TABLE_REMARK                                      VARCHAR(6000) NULL, --备注
CONSTRAINT SDM_KSQL_TABLE_PK PRIMARY KEY(SDM_KSQL_ID)   );

--分词器列表
DROP TABLE IF EXISTS TOKENIZER_LIST ;
CREATE TABLE TOKENIZER_LIST(
TOKENIZER_ID                                      DECIMAL(10) NOT NULL, --分词器编号
TOKENIZER_NAME                                    VARCHAR(512) NOT NULL, --分词器名称
IS_FLAG                                           CHAR(1) NOT NULL, --是否标识
CONSTRAINT TOKENIZER_LIST_PK PRIMARY KEY(TOKENIZER_ID)   );

--ksql字段配置
DROP TABLE IF EXISTS SDM_CON_KSQL ;
CREATE TABLE SDM_CON_KSQL(
SDM_COL_KSQL                                      DECIMAL(10) NOT NULL, --ksql字段编号
SDM_KSQL_ID                                       DECIMAL(10) NOT NULL, --映射表主键
COLUMN_NAME                                       VARCHAR(64) NOT NULL, --字段英文名称
COLUMN_HY                                         VARCHAR(512) NULL, --字段含义
COLUMN_CNAME                                      VARCHAR(64) NOT NULL, --字段中文名称
COLUMN_TYPE                                       VARCHAR(10) NOT NULL, --字段类型
IS_KEY                                            CHAR(1) NOT NULL, --是否为key
IS_TIMESTAMP                                      CHAR(1) NOT NULL, --是否为时间戳
SDM_REMARK                                        VARCHAR(512) NULL, --备注
CONSTRAINT SDM_CON_KSQL_PK PRIMARY KEY(SDM_COL_KSQL)   );

--词条文件表
DROP TABLE IF EXISTS WORD_FILE ;
CREATE TABLE WORD_FILE(
WORD_FILE_ID                                      DECIMAL(10) NOT NULL, --词条文件编号
TOKENIZER_ID                                      DECIMAL(10) NOT NULL, --分词器编号
WORD_FILE_NAME                                    VARCHAR(512) NOT NULL, --词条文件名
CREATE_ID                                         VARCHAR(32) NOT NULL, --创建人编号
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CONSTRAINT WORD_FILE_PK PRIMARY KEY(WORD_FILE_ID)   );

--窗口信息登记表
DROP TABLE IF EXISTS SDM_KSQL_WINDOW ;
CREATE TABLE SDM_KSQL_WINDOW(
SDM_WIN_ID                                        DECIMAL(10) NOT NULL, --窗口信息登记id
SDM_KSQL_ID                                       DECIMAL(10) NOT NULL, --映射表主键
WINDOW_TYPE                                       VARCHAR(50) NULL, --窗口类别
WINDOW_SIZE                                       DECIMAL(16) default 0 NOT NULL, --窗口大小
ADVANCE_INTERVAL                                  DECIMAL(16) default 0 NOT NULL, --窗口滑动间隔
WINDOW_REMARK                                     VARCHAR(512) NULL, --备注
CONSTRAINT SDM_KSQL_WINDOW_PK PRIMARY KEY(SDM_WIN_ID)   );

--词条库表
DROP TABLE IF EXISTS WORD_DATABASE ;
CREATE TABLE WORD_DATABASE(
WORD_ID                                           DECIMAL(10) NOT NULL, --词条编号
CREATE_ID                                         DECIMAL(10) NOT NULL, --用户ID
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
WORD                                              VARCHAR(512) NOT NULL, --词条
WORD_NATURE                                       VARCHAR(512) NOT NULL, --词性
IS_FLAG_PRODUCT                                   CHAR(1) NOT NULL, --是否已生成
IS_FLAG_SELECT                                    CHAR(1) NOT NULL, --是否已选择
WORD_PROPERTY                                     VARCHAR(100) NOT NULL, --词的属性
WORLD_FREQUENCY                                   VARCHAR(100) NOT NULL, --词的频率
WORD_FILE_ID                                      DECIMAL(10) NOT NULL, --词条文件编号
CONSTRAINT WORD_DATABASE_PK PRIMARY KEY(WORD_ID)   );

--接口文件生成信息表
DROP TABLE IF EXISTS INTERFACE_FILE_INFO ;
CREATE TABLE INTERFACE_FILE_INFO(
FILE_ID                                           VARCHAR(36) NOT NULL, --file_id
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
FILE_PATH                                         VARCHAR(512) NOT NULL, --文件路径
DATA_CLASS                                        VARCHAR(10) NOT NULL, --输出数据类型
DATA_OUTPUT                                       VARCHAR(20) NOT NULL, --数据数据形式
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT INTERFACE_FILE_INFO_PK PRIMARY KEY(FILE_ID)   );

--词条选择结果表
DROP TABLE IF EXISTS WORD_PRODUCTION ;
CREATE TABLE WORD_PRODUCTION(
WORD_ID                                           DECIMAL(10) NOT NULL, --词条编号
TOKENIZER_ID                                      DECIMAL(10) NOT NULL, --分词器编号
WORD                                              VARCHAR(512) NOT NULL, --词条
WORD_NATURE                                       VARCHAR(512) NOT NULL, --词性
CONSTRAINT WORD_PRODUCTION_PK PRIMARY KEY(WORD_ID)   );

--数据加工spark语法提示
DROP TABLE IF EXISTS EDW_SPARKSQL_GRAM ;
CREATE TABLE EDW_SPARKSQL_GRAM(
ESG_ID                                            DECIMAL(10) NOT NULL, --序号
FUNCTION_NAME                                     VARCHAR(512) NOT NULL, --函数名称
FUNCTION_EXAMPLE                                  VARCHAR(512) NOT NULL, --函数例子
FUNCTION_DESC                                     VARCHAR(512) NOT NULL, --函数描述
IS_AVAILABLE                                      CHAR(1) NOT NULL, --是否可用
IS_UDF                                            CHAR(1) NOT NULL, --是否udf
CLASS_URL                                         VARCHAR(512) NULL, --函数类路径
JAR_URL                                           VARCHAR(512) NULL, --jar路径
HIVEDB_NAME                                       VARCHAR(100) NULL, --hive库名
IS_SPARKSQL                                       CHAR(1) NOT NULL, --是否同时使用sparksql
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT EDW_SPARKSQL_GRAM_PK PRIMARY KEY(ESG_ID)   );

--错误信息表
DROP TABLE IF EXISTS ERROR_INFO ;
CREATE TABLE ERROR_INFO(
ERROR_ID                                          DECIMAL(10) NOT NULL, --错误ID
JOB_RS_ID                                         VARCHAR(40) NULL, --作业执行结果ID
ERROR_MSG                                         VARCHAR(15555) NULL, --error_msg
CONSTRAINT ERROR_INFO_PK PRIMARY KEY(ERROR_ID)   );

--接口使用信息日志表
DROP TABLE IF EXISTS INTERFACE_USE_LOG ;
CREATE TABLE INTERFACE_USE_LOG(
LOG_ID                                            DECIMAL(10) NOT NULL, --日志ID
INTERFACE_NAME                                    VARCHAR(512) NOT NULL, --接口名称
REQUEST_STATE                                     VARCHAR(200) NOT NULL, --请求状态
RESPONSE_TIME                                     DECIMAL(10) NOT NULL, --响应时间
BROWSER_TYPE                                      VARCHAR(512) NULL, --浏览器类型
BROWSER_VERSION                                   VARCHAR(512) NULL, --浏览器版本
SYSTEM_TYPE                                       VARCHAR(512) NULL, --系统类型
REQUEST_MODE                                      VARCHAR(512) NULL, --请求方式
REMOTEADDR                                        VARCHAR(512) NULL, --客户端的IP
PROTOCOL                                          VARCHAR(512) NULL, --超文本传输协议版本
REQUEST_INFO                                      VARCHAR(6000) NULL, --请求信息
REQUEST_STIME                                     VARCHAR(512) NULL, --请求起始时间
REQUEST_ETIME                                     VARCHAR(512) NULL, --请求结束时间
REQUEST_TYPE                                      VARCHAR(512) NULL, --请求类型
INTERFACE_USE_ID                                  DECIMAL(10) NOT NULL, --接口使用ID
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
USER_NAME                                         VARCHAR(512) NULL, --用户名称
CONSTRAINT INTERFACE_USE_LOG_PK PRIMARY KEY(LOG_ID)   );

--进库信息统计表
DROP TABLE IF EXISTS NUM_INFO ;
CREATE TABLE NUM_INFO(
ICM_ID                                            DECIMAL(10) NOT NULL, --信息id
HBASE_NAME                                        VARCHAR(512) NOT NULL, --Hbase表名
EXEC_DATE                                         CHAR(8) NOT NULL, --日期
MAINTYPE                                          VARCHAR(25) NOT NULL, --储存方式
INCREASE_NUM                                      DECIMAL(16) default 0 NOT NULL, --增加的条数
DECREASE_NUM                                      DECIMAL(16) default 0 NOT NULL, --删除的条数
CONSTRAINT NUM_INFO_PK PRIMARY KEY(ICM_ID)   );

--对标数据表
DROP TABLE IF EXISTS BENCHMARK_DATA_TABLE ;
CREATE TABLE BENCHMARK_DATA_TABLE(
BENCHMARK_ID                                      DECIMAL(10) NOT NULL, --对标数据表id
SOURCE_TABLE_NAME                                 VARCHAR(512) NOT NULL, --源表名
SOURCE_COLUMN_NAME                                VARCHAR(64) NOT NULL, --源字段名
SOURCE_CH_COLUMN                                  VARCHAR(512) NULL, --源字段中文名
CATEGORY                                          VARCHAR(64) NOT NULL, --表字段分类
TRANS_COLUMN_NAME                                 VARCHAR(64) NULL, --翻译后的字段名
CONSTRAINT BENCHMARK_DATA_TABLE_PK PRIMARY KEY(BENCHMARK_ID)   );

--数据消费至SparkD
DROP TABLE IF EXISTS SDM_CON_SPARKD ;
CREATE TABLE SDM_CON_SPARKD(
SPARKD_ID                                         DECIMAL(10) NOT NULL, --sparkd_id
SPARKD_BUS_TYPE                                   CHAR(1) NOT NULL, --SparkD业务处理类类型
SPARKD_BUS_CLASS                                  VARCHAR(512) NULL, --SparkD业务处理类
TABLE_EN_NAME                                     VARCHAR(512) NOT NULL, --SparkD的表名
TABLE_SPACE                                       VARCHAR(200) NULL, --SparkD表空间
SDM_DES_ID                                        DECIMAL(10) NOT NULL, --配置id
CONSTRAINT SDM_CON_SPARKD_PK PRIMARY KEY(SPARKD_ID)   );

--表存储信息
DROP TABLE IF EXISTS TABLE_STORAGE_INFO ;
CREATE TABLE TABLE_STORAGE_INFO(
STORAGE_ID                                        DECIMAL(10) NOT NULL, --储存编号
IS_INTO_HBASE                                     CHAR(1) NOT NULL, --是否入hbase
IS_COMPRESSION                                    CHAR(1) default '0' NOT NULL, --Hbase是使用压缩
IS_INTO_HIVE                                      CHAR(1) NOT NULL, --是否入hive
IS_MPP                                            CHAR(1) NOT NULL, --是否为MPP
TABLE_TYPE                                        CHAR(1) NOT NULL, --是内部表还是外部表
IS_FULLINDEX                                      CHAR(1) NOT NULL, --是否创建全文索引
FILE_FORMAT                                       CHAR(1) NOT NULL, --文件格式
IS_SOLR_HBASE                                     CHAR(1) default '1' NOT NULL, --是否使用solrOnHbase
IS_CBD                                            CHAR(1) default '1' NOT NULL, --是否使用carbondata
TABLE_ID                                          DECIMAL(10) NULL, --表名ID
CONSTRAINT TABLE_STORAGE_INFO_PK PRIMARY KEY(STORAGE_ID)   );

--StreamingPro作业信息表
DROP TABLE IF EXISTS SDM_SP_JOBINFO ;
CREATE TABLE SDM_SP_JOBINFO(
SSJ_JOB_ID                                        DECIMAL(10) NOT NULL, --作业id
SSJ_JOB_NAME                                      VARCHAR(512) NOT NULL, --作业名称
SSJ_JOB_DESC                                      VARCHAR(200) NULL, --作业描述
SSJ_STRATEGY                                      VARCHAR(512) NOT NULL, --作业执行策略
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT SDM_SP_JOBINFO_PK PRIMARY KEY(SSJ_JOB_ID)   );

--StreamingPro作业输入信息表
DROP TABLE IF EXISTS SDM_JOB_INPUT ;
CREATE TABLE SDM_JOB_INPUT(
SDM_INFO_ID                                       DECIMAL(10) NOT NULL, --作业输入信息表id
INPUT_NUMBER                                      DECIMAL(16) default 0 NOT NULL, --序号
INPUT_TYPE                                        CHAR(1) NOT NULL, --输入类型
INPUT_EN_NAME                                     VARCHAR(512) NOT NULL, --输入英文名称
INPUT_CN_NAME                                     VARCHAR(512) NULL, --输入中文名称
INPUT_TABLE_NAME                                  VARCHAR(64) NOT NULL, --输出表名
INPUT_SOURCE                                      VARCHAR(512) NOT NULL, --数据来源
INPUT_DATA_TYPE                                   CHAR(1) NOT NULL, --数据模式
SSJ_JOB_ID                                        DECIMAL(10) NOT NULL, --作业id
CONSTRAINT SDM_JOB_INPUT_PK PRIMARY KEY(SDM_INFO_ID)   );

--StreamingPro作业分析信息表
DROP TABLE IF EXISTS SDM_SP_ANALYSIS ;
CREATE TABLE SDM_SP_ANALYSIS(
SSA_INFO_ID                                       DECIMAL(10) NOT NULL, --分析信息表id
ANALYSIS_NUMBER                                   DECIMAL(16) default 0 NOT NULL, --序号
ANALYSIS_TABLE_NAME                               VARCHAR(64) NOT NULL, --输出表名
ANALYSIS_SQL                                      VARCHAR(8000) NOT NULL, --分析sql
SSJ_JOB_ID                                        DECIMAL(10) NOT NULL, --作业id
CONSTRAINT SDM_SP_ANALYSIS_PK PRIMARY KEY(SSA_INFO_ID)   );

--报表数据表
DROP TABLE IF EXISTS REPORT_DATA_TABLE ;
CREATE TABLE REPORT_DATA_TABLE(
REPORT_ID                                         DECIMAL(10) NOT NULL, --报表编号
CREATE_ID                                         DECIMAL(10) NOT NULL, --用户ID
SQL_ID                                            DECIMAL(10) NOT NULL, --SQL编号
FOLDER_ID                                         DECIMAL(10) NULL, --文件夹编号
REPORT_TYPE_ID                                    DECIMAL(10) NOT NULL, --报表类型编号
CONFIGURATION_ID                                  DECIMAL(10) NULL, --配置编号
REPORT_SOURCE                                     CHAR(2) NULL, --报表来源
R_PUBLISH_STATUS                                  CHAR(1) NOT NULL, --报表发布状态
REPORT_NAME                                       VARCHAR(512) NOT NULL, --报表名称
SQL_CHOICE_DETAILS                                VARCHAR(1000) NOT NULL, --SQL选择内容
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CONSTRAINT REPORT_DATA_TABLE_PK PRIMARY KEY(REPORT_ID)   );

--StreamingPro作业输出信息表
DROP TABLE IF EXISTS SDM_SP_OUTPUT ;
CREATE TABLE SDM_SP_OUTPUT(
SDM_INFO_ID                                       DECIMAL(10) NOT NULL, --作业输出信息表id
OUTPUT_NUMBER                                     DECIMAL(16) default 0 NOT NULL, --序号
OUTPUT_TYPE                                       CHAR(1) NOT NULL, --输出类型
OUTPUT_MODE                                       CHAR(1) NOT NULL, --输出模式
OUTPUT_TABLE_NAME                                 VARCHAR(512) NOT NULL, --输入表名称
STREAM_TABLENAME                                  VARCHAR(100) NULL, --输出到流表的表名
SSJ_JOB_ID                                        DECIMAL(10) NOT NULL, --作业id
CONSTRAINT SDM_SP_OUTPUT_PK PRIMARY KEY(SDM_INFO_ID)   );

--StreamingPro文本文件信息表
DROP TABLE IF EXISTS SDM_SP_TEXTFILE ;
CREATE TABLE SDM_SP_TEXTFILE(
TSST_EXTFILE_ID                                   DECIMAL(10) NOT NULL, --文本文件信息表id
SST_FILE_TYPE                                     CHAR(1) NOT NULL, --文件格式
SST_FILE_PATH                                     VARCHAR(512) NOT NULL, --文件输入输出路径
SST_IS_HEADER                                     CHAR(1) NOT NULL, --是否有表头
SST_SCHEMA                                        VARCHAR(1024) NULL, --schema
SDM_INFO_ID                                       DECIMAL(10) NOT NULL, --作业输入信息表id
CONSTRAINT SDM_SP_TEXTFILE_PK PRIMARY KEY(TSST_EXTFILE_ID)   );

--StreamingPro数据库数据信息表
DROP TABLE IF EXISTS SDM_SP_DATABASE ;
CREATE TABLE SDM_SP_DATABASE(
SSD_INFO_ID                                       DECIMAL(10) NOT NULL, --数据库信息表id
SSD_TABLE_NAME                                    VARCHAR(100) NOT NULL, --表名称
SSD_DATABASE_TYPE                                 CHAR(2) NOT NULL, --数据库类型
SSD_DATABASE_DRIVE                                VARCHAR(64) NOT NULL, --数据库驱动
SSD_DATABASE_NAME                                 VARCHAR(100) NOT NULL, --数据库名称
SSD_IP                                            VARCHAR(50) NOT NULL, --数据库ip
SSD_PORT                                          VARCHAR(10) NOT NULL, --端口
SSD_USER_NAME                                     VARCHAR(100) NOT NULL, --数据库用户名
SSD_USER_PASSWORD                                 VARCHAR(100) NOT NULL, --用户密码
SSD_JDBC_URL                                      VARCHAR(512) NOT NULL, --数据库jdbc连接的url
SDM_INFO_ID                                       DECIMAL(10) NOT NULL, --作业输入信息表id
CONSTRAINT SDM_SP_DATABASE_PK PRIMARY KEY(SSD_INFO_ID)   );

--文件目录表
DROP TABLE IF EXISTS FLODER_TABLE ;
CREATE TABLE FLODER_TABLE(
FOLDER_ID                                         DECIMAL(10) NOT NULL, --文件夹编号
CREATE_ID                                         DECIMAL(10) NOT NULL, --用户ID
FLODER_NAME                                       VARCHAR(100) NOT NULL, --文件夹名称
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CONSTRAINT FLODER_TABLE_PK PRIMARY KEY(FOLDER_ID)   );

--StreamingPro流数据信息表
DROP TABLE IF EXISTS SDM_SP_STREAM ;
CREATE TABLE SDM_SP_STREAM(
SSS_STREAM_ID                                     DECIMAL(10) NOT NULL, --流数据信息表id
SSS_KAFKA_VERSION                                 CHAR(1) NOT NULL, --kafka版本
SSS_TOPIC_NAME                                    VARCHAR(64) NOT NULL, --主题
SSS_BOOTSTRAP_SERVER                              VARCHAR(256) NOT NULL, --流服务主机
SSS_CONSUMER_OFFSET                               VARCHAR(64) NOT NULL, --偏移量设置
SDM_INFO_ID                                       DECIMAL(10) NOT NULL, --作业输入信息表id
CONSTRAINT SDM_SP_STREAM_PK PRIMARY KEY(SSS_STREAM_ID)   );

--StreamingPro作业启动参数表
DROP TABLE IF EXISTS SDM_SP_PARAM ;
CREATE TABLE SDM_SP_PARAM(
SSP_PARAM_ID                                      DECIMAL(10) NOT NULL, --作业启动参数表id
SSP_PARAM_KEY                                     VARCHAR(64) NOT NULL, --参数key
SSP_PARAM_VALUE                                   VARCHAR(5000) NULL, --参数值
IS_CUSTOMIZE                                      CHAR(1) NOT NULL, --是否是自定义参数
SSJ_JOB_ID                                        DECIMAL(10) NOT NULL, --作业id
CONSTRAINT SDM_SP_PARAM_PK PRIMARY KEY(SSP_PARAM_ID)   );

--报表类型表
DROP TABLE IF EXISTS REPORT_TYPE_TABLE ;
CREATE TABLE REPORT_TYPE_TABLE(
REPORT_TYPE_ID                                    DECIMAL(10) NOT NULL, --报表类型编号
REPORT_TYPE                                       CHAR(3) NULL, --报表类型
REPORT_TYPE_NAME                                  VARCHAR(100) NOT NULL, --报表类型名称
REPORT_TITLE                                      VARCHAR(512) NOT NULL, --报表标题
REPORT_PRINT_POSITION                             VARCHAR(48) NULL, --报表图片所在位置
REPORT_DATA_FILED                                 VARCHAR(1600) NOT NULL, --报表数据构成字段
REPORT_STYLE_FILED                                VARCHAR(10000) NULL, --报表样式构成字段
CONSTRAINT REPORT_TYPE_TABLE_PK PRIMARY KEY(REPORT_TYPE_ID)   );

--作业模版参数表
DROP TABLE IF EXISTS ETL_JOB_TEMP_PARA ;
CREATE TABLE ETL_JOB_TEMP_PARA(
ETL_TEMP_PARA_ID                                  DECIMAL(10) NOT NULL, --模版参数主键
ETL_PARA_TYPE                                     VARCHAR(512) NOT NULL, --参数类型
ETL_JOB_PRO_PARA                                  VARCHAR(512) NOT NULL, --参数名称
ETL_JOB_PARA_SIZE                                 VARCHAR(512) NOT NULL, --参数
ETL_PRO_PARA_SORT                                 DECIMAL(16) default 0 NOT NULL, --参数排序
ETL_TEMP_ID                                       DECIMAL(10) NOT NULL, --模版ID
CONSTRAINT ETL_JOB_TEMP_PARA_PK PRIMARY KEY(ETL_TEMP_PARA_ID)   );

--模版作业信息表
DROP TABLE IF EXISTS ETL_JOB_TEMP ;
CREATE TABLE ETL_JOB_TEMP(
ETL_TEMP_ID                                       DECIMAL(10) NOT NULL, --模版ID
ETL_TEMP_TYPE                                     VARCHAR(512) NOT NULL, --模版名称
PRO_DIC                                           VARCHAR(512) NOT NULL, --模版shell路径
PRO_NAME                                          VARCHAR(512) NOT NULL, --模版shell名称
CONSTRAINT ETL_JOB_TEMP_PK PRIMARY KEY(ETL_TEMP_ID)   );

--清洗作业参数属性表
DROP TABLE IF EXISTS CLEAN_PARAMETER ;
CREATE TABLE CLEAN_PARAMETER(
C_ID                                              DECIMAL(10) NOT NULL, --清洗参数编号
CLEAN_TYPE                                        CHAR(1) NOT NULL, --清洗方式
FILLING_TYPE                                      CHAR(1) NULL, --补齐方式
CHARACTER_FILLING                                 VARCHAR(512) NULL, --补齐字符
FILLING_LENGTH                                    DECIMAL(16) default 0 NULL, --补齐长度
FIELD                                             VARCHAR(512) NULL, --原字段
REPLACE_FEILD                                     VARCHAR(512) NULL, --替换字段
DATABASE_ID                                       DECIMAL(10) NOT NULL, --数据库设置id
CONSTRAINT CLEAN_PARAMETER_PK PRIMARY KEY(C_ID)   );

--数据采集阶段表
DROP TABLE IF EXISTS DATASTAGE ;
CREATE TABLE DATASTAGE(
JOBKEY                                            DECIMAL(10) NOT NULL, --数据库设置id
TABLENAME                                         VARCHAR(512) NOT NULL, --表名字
STAGE                                             VARCHAR(512) NOT NULL, --采集阶段
STATE                                             VARCHAR(2) NOT NULL, --所处状态
PREVIOUSSTAGE                                     VARCHAR(512) NULL, --上一阶段
NEXTSTAGE                                         VARCHAR(512) NULL, --下一阶段
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT DATASTAGE_PK PRIMARY KEY(JOBKEY,TABLENAME,STAGE)   );

--表清洗参数信息
DROP TABLE IF EXISTS TABLE_CLEAN ;
CREATE TABLE TABLE_CLEAN(
C_ID                                              DECIMAL(10) NOT NULL, --清洗参数编号
CLEAN_TYPE                                        CHAR(1) NOT NULL, --清洗方式
FILLING_TYPE                                      CHAR(1) NULL, --补齐方式
CHARACTER_FILLING                                 VARCHAR(512) NULL, --补齐字符
FILLING_LENGTH                                    DECIMAL(16) default 0 NULL, --补齐长度
FIELD                                             VARCHAR(512) NULL, --原字段
REPLACE_FEILD                                     VARCHAR(512) NULL, --替换字段
TABLE_ID                                          DECIMAL(10) NOT NULL, --表名ID
CONSTRAINT TABLE_CLEAN_PK PRIMARY KEY(C_ID)   );

--编码信息表
DROP TABLE IF EXISTS HYREN_CODE_INFO ;
CREATE TABLE HYREN_CODE_INFO(
CODE_CLASSIFY                                     VARCHAR(100) NOT NULL, --编码分类
CODE_VALUE                                        VARCHAR(100) NOT NULL, --编码类型值
CODE_CLASSIFY_NAME                                VARCHAR(512) NOT NULL, --编码分类名称
CODE_TYPE_NAME                                    VARCHAR(512) NOT NULL, --编码名称
CODE_REMARK                                       VARCHAR(512) NULL, --编码描述
CONSTRAINT HYREN_CODE_INFO_PK PRIMARY KEY(CODE_CLASSIFY,CODE_VALUE)   );

--列清洗参数信息
DROP TABLE IF EXISTS COLUMN_CLEAN ;
CREATE TABLE COLUMN_CLEAN(
C_ID                                              DECIMAL(10) NOT NULL, --清洗参数编号
CONVERT_FORMAT                                    VARCHAR(512) NULL, --转换格式
OLD_FORMAT                                        VARCHAR(512) NULL, --原始格式
CLEAN_TYPE                                        CHAR(1) NOT NULL, --清洗方式
FILLING_TYPE                                      CHAR(1) NULL, --补齐方式
CHARACTER_FILLING                                 VARCHAR(512) NULL, --补齐字符
FILLING_LENGTH                                    DECIMAL(16) default 0 NULL, --补齐长度
CODENAME                                          VARCHAR(512) NULL, --码值名称
CODESYS                                           VARCHAR(512) NULL, --码值所属系统
FIELD                                             VARCHAR(512) NULL, --原字段
REPLACE_FEILD                                     VARCHAR(512) NULL, --替换字段
COLUMN_ID                                         DECIMAL(10) NOT NULL, --字段ID
CONSTRAINT COLUMN_CLEAN_PK PRIMARY KEY(C_ID)   );

--源系统编码信息
DROP TABLE IF EXISTS ORIG_CODE_INFO ;
CREATE TABLE ORIG_CODE_INFO(
ORIG_ID                                           DECIMAL(10) NOT NULL, --源系统编码主键
ORIG_SYS_CODE                                     VARCHAR(100) NULL, --码值系统编码
CODE_CLASSIFY                                     VARCHAR(100) NOT NULL, --编码分类
CODE_VALUE                                        VARCHAR(100) NOT NULL, --编码类型值
ORIG_VALUE                                        VARCHAR(100) NOT NULL, --源系统编码值
CODE_REMARK                                       VARCHAR(512) NULL, --系统编码描述
CONSTRAINT ORIG_CODE_INFO_PK PRIMARY KEY(ORIG_ID)   );

--源系统信
DROP TABLE IF EXISTS ORIG_SYSO_INFO ;
CREATE TABLE ORIG_SYSO_INFO(
ORIG_SYS_CODE                                     VARCHAR(100) NOT NULL, --码值系统编码
ORIG_SYS_NAME                                     VARCHAR(100) NOT NULL, --码值系统名称
ORIG_SYS_REMARK                                   VARCHAR(512) NULL, --码值系统描述
CONSTRAINT ORIG_SYSO_INFO_PK PRIMARY KEY(ORIG_SYS_CODE)   );

--我的收藏
DROP TABLE IF EXISTS USER_FAV ;
CREATE TABLE USER_FAV(
FAV_ID                                            DECIMAL(10) NOT NULL, --收藏ID
ORIGINAL_NAME                                     VARCHAR(512) NOT NULL, --原始文件名称
FILE_ID                                           VARCHAR(40) NOT NULL, --文件编号
USER_ID                                           DECIMAL(10) NULL, --用户ID
FAV_FLAG                                          CHAR(1) NOT NULL, --是否有效
CONSTRAINT USER_FAV_PK PRIMARY KEY(FAV_ID)   );

--模板信息表
DROP TABLE IF EXISTS AUTO_TP_INFO ;
CREATE TABLE AUTO_TP_INFO(
TEMPLATE_ID                                       DECIMAL(10) NOT NULL, --模板ID
TEMPLATE_NAME                                     VARCHAR(80) NULL, --模板名称
TEMPLATE_DESC                                     VARCHAR(512) NULL, --模板描述
DATA_SOURCE                                       CHAR(2) NOT NULL, --数据来源
TEMPLATE_SQL                                      VARCHAR(512) NULL, --模板sql语句
TEMPLATE_STATUS                                   CHAR(2) NOT NULL, --模板状态
CREATE_DATE                                       CHAR(8) NULL, --创建日期
CREATE_TIME                                       CHAR(6) NULL, --创建时间
CREATE_USER                                       VARCHAR(10) NULL, --创建用户
LAST_UPDATE_DATE                                  CHAR(8) NULL, --最后更新日期
LAST_UPDATE_TIME                                  CHAR(6) NULL, --最后更新时间
UPDATE_USER                                       VARCHAR(10) NULL, --更新用户
CONSTRAINT AUTO_TP_INFO_PK PRIMARY KEY(TEMPLATE_ID)   );

--模板条件信息表
DROP TABLE IF EXISTS AUTO_TP_COND_INFO ;
CREATE TABLE AUTO_TP_COND_INFO(
TEMPLATE_COND_ID                                  DECIMAL(10) NOT NULL, --模板条件ID
CON_ROW                                           VARCHAR(32) NULL, --行号
COND_PARA_NAME                                    VARCHAR(100) NULL, --条件参数名称
COND_EN_COLUMN                                    VARCHAR(100) NULL, --条件对应的英文字段
COND_CN_COLUMN                                    VARCHAR(100) NULL, --条件对应的中文字段
CI_SP_NAME                                        VARCHAR(100) NULL, --代码项表名
CI_SP_CLASS                                       VARCHAR(100) NULL, --代码项类别
CON_RELATION                                      VARCHAR(10) NULL, --关联关系
VALUE_TYPE                                        CHAR(2) NULL, --值类型
VALUE_SIZE                                        VARCHAR(64) NULL, --值大小
SHOW_TYPE                                         CHAR(2) NULL, --展现形式
PRE_VALUE                                         VARCHAR(100) NULL, --预设值
IS_REQUIRED                                       CHAR(1) NULL, --是否必填
IS_DEPT_ID                                        CHAR(1) NULL, --是否为部门ID
TEMPLATE_ID                                       DECIMAL(10) NOT NULL, --模板ID
CONSTRAINT AUTO_TP_COND_INFO_PK PRIMARY KEY(TEMPLATE_COND_ID)   );

--模板结果设置表
DROP TABLE IF EXISTS AUTO_TP_RES_SET ;
CREATE TABLE AUTO_TP_RES_SET(
TEMPLATE_RES_ID                                   DECIMAL(10) NOT NULL, --模板结果ID
TEMPLATE_ID                                       DECIMAL(10) NOT NULL, --模板ID
COLUMN_EN_NAME                                    VARCHAR(100) NULL, --字段英文名
COLUMN_CN_NAME                                    VARCHAR(100) NULL, --字段中文名
RES_SHOW_COLUMN                                   VARCHAR(100) NULL, --结果显示字段
SOURCE_TABLE_NAME                                 VARCHAR(64) NULL, --字段来源表名
COLUMN_TYPE                                       VARCHAR(60) NULL, --字段类型
IS_DESE                                           CHAR(1) NULL, --是否脱敏
DESE_RULE                                         VARCHAR(512) NULL, --脱敏规则
CREATE_DATE                                       CHAR(8) NULL, --创建日期
CREATE_TIME                                       CHAR(6) NULL, --创建时间
CREATE_USER                                       VARCHAR(10) NULL, --创建用户
LAST_UPDATE_DATE                                  CHAR(8) NULL, --最后更新日期
LAST_UPDATE_TIME                                  CHAR(6) NULL, --最后更新时间
UPDATE_USER                                       VARCHAR(10) NULL, --更新用户
CONSTRAINT AUTO_TP_RES_SET_PK PRIMARY KEY(TEMPLATE_RES_ID)   );

--通讯监控信息表
DROP TABLE IF EXISTS COMMUNICATION_INFO ;
CREATE TABLE COMMUNICATION_INFO(
COM_ID                                            DECIMAL(10) NOT NULL, --通讯id
NAME                                              VARCHAR(512) NOT NULL, --agent名称
AGENT_TYPE                                        CHAR(1) NOT NULL, --agent类型
AGENT_IP                                          VARCHAR(50) NOT NULL, --AgentIP
COM_DATE                                          CHAR(8) NOT NULL, --通讯日期
COM_TIME                                          CHAR(6) NOT NULL, --通讯时间
AGENT_PORT                                        VARCHAR(10) NOT NULL, --agent服务器端口
IS_NORMAL                                         CHAR(1) NOT NULL, --通讯是否正常
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT COMMUNICATION_INFO_PK PRIMARY KEY(COM_ID)   );

--取数汇总表
DROP TABLE IF EXISTS AUTO_FETCH_SUM ;
CREATE TABLE AUTO_FETCH_SUM(
FETCH_SUM_ID                                      DECIMAL(10) NOT NULL, --取数汇总ID
FETCH_SQL                                         VARCHAR(512) NULL, --取数sql
USER_ID                                           VARCHAR(10) NULL, --用户ID
FETCH_NAME                                        VARCHAR(100) NULL, --取数名称
FETCH_DESC                                        VARCHAR(512) NULL, --取数用途
CREATE_DATE                                       CHAR(8) NULL, --创建日期
CREATE_TIME                                       CHAR(6) NULL, --创建时间
CREATE_USER                                       VARCHAR(10) NULL, --创建用户
LAST_UPDATE_DATE                                  CHAR(8) NULL, --最后更新日期
LAST_UPDATE_TIME                                  CHAR(6) NULL, --最后更新时间
UPDATE_USER                                       VARCHAR(10) NULL, --更新用户
FETCH_STATUS                                      CHAR(2) NOT NULL, --取数状态
TEMPLATE_ID                                       DECIMAL(10) NOT NULL, --模板ID
CONSTRAINT AUTO_FETCH_SUM_PK PRIMARY KEY(FETCH_SUM_ID)   );

--目录监控信息表
DROP TABLE IF EXISTS DIRECTORY_INFO ;
CREATE TABLE DIRECTORY_INFO(
ID                                                DECIMAL(10) NOT NULL, --目录id
SET_ID                                            DECIMAL(10) NOT NULL, --配置id
DIR_NAME                                          VARCHAR(512) NULL, --目录名称
DIR_PATH                                          VARCHAR(512) NOT NULL, --目录路径
DIR_DESC                                          VARCHAR(512) NULL, --目录描述
DIR_STATUS                                        VARCHAR(10) NOT NULL, --目录状态
DIR_DATE                                          CHAR(8) NOT NULL, --监控日期
DIR_TIME                                          CHAR(6) NOT NULL, --监控时间
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT DIRECTORY_INFO_PK PRIMARY KEY(ID)   );

--取数条件表
DROP TABLE IF EXISTS AUTO_FETCH_COND ;
CREATE TABLE AUTO_FETCH_COND(
FETCH_COND_ID                                     DECIMAL(10) NOT NULL, --取数条件ID
FETCH_SUM_ID                                      DECIMAL(10) NOT NULL, --取数汇总ID
COND_VALUE                                        VARCHAR(100) NULL, --条件值
TEMPLATE_COND_ID                                  DECIMAL(10) NOT NULL, --模板条件ID
CONSTRAINT AUTO_FETCH_COND_PK PRIMARY KEY(FETCH_COND_ID)   );

--取数结果表
DROP TABLE IF EXISTS AUTO_FETCH_RES ;
CREATE TABLE AUTO_FETCH_RES(
FETCH_RES_ID                                      DECIMAL(10) NOT NULL, --取数结果ID
FETCH_RES_NAME                                    VARCHAR(100) NULL, --取数结果名称
SHOW_NUM                                          INTEGER default 0 NULL, --显示顺序
TEMPLATE_RES_ID                                   DECIMAL(10) NOT NULL, --模板结果ID
FETCH_SUM_ID                                      DECIMAL(10) NOT NULL, --取数汇总ID
CONSTRAINT AUTO_FETCH_RES_PK PRIMARY KEY(FETCH_RES_ID)   );

--报表详细配置表
DROP TABLE IF EXISTS REPORT_CONFIGURATION_TABLE ;
CREATE TABLE REPORT_CONFIGURATION_TABLE(
CONFIGURATION_ID                                  DECIMAL(10) NOT NULL, --配置编号
CREATE_ID                                         DECIMAL(10) NOT NULL, --用户ID
REPORT_TYPE_ID                                    DECIMAL(10) NOT NULL, --报表类型编号
CONFIGURATION_NAME                                VARCHAR(100) NOT NULL, --配置名称
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
MINVALUE                                          VARCHAR(10) NULL, --最小值
MAXVALUE                                          VARCHAR(10) NULL, --最大值
CIRCLETHICKNESS                                   VARCHAR(10) NULL, --圆圈边框厚度
CIRCLEFILLGAP                                     VARCHAR(10) NULL, --圆圈间隙
CIRCLECOLOR                                       VARCHAR(10) NULL, --圆圈颜色
WAVEHEIGHT                                        VARCHAR(10) NULL, --波浪高度
WAVECOUNT                                         VARCHAR(10) NULL, --波浪个数
WAVECOLOR                                         VARCHAR(20) NULL, --波浪颜色
WAVEOFFSET                                        CHAR(1) NULL, --波浪微调
TEXTVERTPOSITION                                  CHAR(1) NULL, --文本位置
TEXTSIZE                                          VARCHAR(5) NULL, --圆文字大小
DISPLAYPERCENT                                    VARCHAR(5) NULL, --是否显示百分比
TEXTCOLOR                                         VARCHAR(20) NULL, --文本颜色
WAVETEXTCOLOR                                     VARCHAR(20) NULL, --波浪文本颜色
REDIUS                                            VARCHAR(5) NULL, --圆半径(百分比)
Y                                                 VARCHAR(10) NULL, --说明所在y轴位置
ROSETYPE                                          VARCHAR(6) NULL, --展现类型
AVOIDLABELOVERLAP                                 VARCHAR(5) NULL, --是否不允许标注重叠
FONTSIZE                                          VARCHAR(5) NULL, --图表文字大小
FONTWEIGHT                                        VARCHAR(7) NULL, --图表文字粗细
FUNNELALIGN                                       VARCHAR(6) NULL, --漏斗图水平方向对齐布局类型
XAXISMININTERVAL                                  INTEGER default 0 NULL, --x轴刻度最小值
XAXISMAXINTERVAL                                  INTEGER default 0 NULL, --x轴刻度最大值
SPAN                                              DECIMAL(16,2) default 0 NULL, --柱条间距
YAXISMININTERVAL                                  INTEGER default 0 NULL, --y轴刻度最小值
YAXISMAXINTERVAL                                  INTEGER default 0 NULL, --y轴刻度最大值
XAXISPOSITION                                     VARCHAR(7) NULL, --x轴位置
YAXISPOSITION                                     VARCHAR(7) NULL, --y轴位置
TICKSIZE                                          INTEGER default 0 NULL, --标记大小
TICKPADDING                                       INTEGER default 0 NULL, --标记内距
GRAPHWIDTH                                        INTEGER default 0 NULL, --图宽
GRAPHHEIGHT                                       INTEGER default 0 NULL, --图高
POSITIVECOLOR                                     VARCHAR(20) NULL, --正数颜色
NEGATIVECOLOR                                     VARCHAR(20) NULL, --负数颜色
TITLETEXT                                         VARCHAR(48) NULL, --主标题文本
TITLELINK                                         VARCHAR(480) NULL, --主标题文本超链接
TITILEFONTCOLOR                                   VARCHAR(20) NULL, --标题字体颜色
TITLEFONTSTYLE                                    VARCHAR(30) NULL, --标题字体风格
TITLEFONTSIZE                                     VARCHAR(5) default '0' NULL, --标题文字大小
TITLETEXTALIGN                                    VARCHAR(7) NULL, --标题文本水平对齐方式
TITLETEXTBASELINE                                 VARCHAR(7) NULL, --标题文本垂直对齐方式
TITLETOP                                          VARCHAR(10) NULL, --标题离上侧距离
TITLESUBTEXT                                      VARCHAR(48) NULL, --副标题文本
BACKGROUNDCOLOR                                   VARCHAR(20) NULL, --背景色
X                                                 VARCHAR(10) NULL, --说明所在x轴位置
LEGENDSHOW                                        VARCHAR(5) NULL, --是否显示说明
MINANGLE                                          INTEGER default 0 NULL, --最小扇区角度
FONTCOLOR                                         VARCHAR(20) NULL, --图表字体颜色
FONTSTYLE                                         VARCHAR(10) NULL, --图表字体风格
TITLEX                                            VARCHAR(10) NULL, --标题所在x轴对齐方式
RADIUS                                            VARCHAR(5) NULL, --圆半径
XAXISNAME                                         VARCHAR(20) NULL, --x坐标轴名称
XAXISNAMELOCATION                                 VARCHAR(7) NULL, --x坐标轴名称显示位置
XAXISFONTSIZE                                     INTEGER default 0 NULL, --x轴坐标名称文字大小
XAXISNAMEGAP                                      INTEGER default 0 NULL, --x轴坐标名称与轴线之间的距离
XAXISINVERSE                                      VARCHAR(5) NULL, --x轴是否为反向坐标轴
YAXISNAME                                         VARCHAR(20) NULL, --y坐标轴名称
YAXISNAMELOCATION                                 VARCHAR(7) NULL, --y坐标轴名称显示位置
YAXISFONTSIZE                                     INTEGER default 0 NULL, --y轴坐标名称文字大小
YAXISNAMEGAP                                      INTEGER default 0 NULL, --y轴坐标名称与轴线之间的距离
BARHEIGHT                                         INTEGER default 0 NULL, --柱高
YAXISINVERSE                                      VARCHAR(7) NULL, --y轴是否为反向坐标轴
GAPBETWEENGROUPS                                  INTEGER default 0 NULL, --每组柱条间距
SPACEFORLABELS                                    INTEGER default 0 NULL, --标签空间
SPACEFORLEGEND                                    INTEGER default 0 NULL, --图例空间
LEGENDRECTSIZE                                    INTEGER default 0 NULL, --图例大小
LEGENDSPACING                                     INTEGER default 0 NULL, --图例间距
TOOLBOXSHOW                                       VARCHAR(5) NULL, --是否显示提示框
SERIESLEFT                                        INTEGER default 0 NULL, --图表左边的距离
SERIESTOP                                         INTEGER default 0 NULL, --图表顶部的距离
SERIESBOTTOM                                      INTEGER default 0 NULL, --图表底部的距离
SERIESSORT                                        VARCHAR(20) NULL, --漏斗图的排序
SERIESBORDERCOLOR                                 VARCHAR(11) NULL, --图表的边框颜色
SERIESBORDERWIDTH                                 INTEGER default 0 NULL, --图表的边框宽度
FLINEWIDTH                                        INTEGER default 0 NULL, --线宽度
FLINECOLOR                                        VARCHAR(10) NULL, --线颜色
EXPENDCOLOR                                       VARCHAR(10) NULL, --展开节点颜色
COLLAPSECOLOR                                     VARCHAR(10) NULL, --折叠节点颜色
MYSHAPE                                           VARCHAR(10) NULL, --节点形状
NODEWIDTH                                         INTEGER default 0 NULL, --节点宽度
NODEPADDING                                       INTEGER default 0 NULL, --节点间距
TITLELEFT                                         INTEGER default 0 NULL, --主标题文本的距离左边的距离
ISMERGE                                           VARCHAR(5) NULL, --是否合并重复
BARCOLOR                                          VARCHAR(10) NULL, --柱条颜色
SHAPE                                             VARCHAR(20) NULL, --雷达绘制类型
AXISLABELCOLOR                                    CHAR(10) NULL, --标签颜色
ISAXISLABEL                                       CHAR(10) NULL, --是否显示标签
RADIALGRADIENTCOLOR0                              CHAR(10) NULL, --0处径向渐变颜色
RADIALGRADIENTCOLOR1                              CHAR(10) NULL, --100处径向渐变颜色
ISDATAVALUE                                       CHAR(10) NULL, --是否显示数据值
CONSTRAINT REPORT_CONFIGURATION_TABLE_PK PRIMARY KEY(CONFIGURATION_ID)   );

--组件分组表
DROP TABLE IF EXISTS AUTO_COMP_GROUP ;
CREATE TABLE AUTO_COMP_GROUP(
COMPONENT_GROUP_ID                                DECIMAL(10) NOT NULL, --分组ID
COLUMN_NAME                                       VARCHAR(100) NOT NULL, --字段名
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CREATE_USER                                       VARCHAR(10) NULL, --创建用户
LAST_UPDATE_DATE                                  CHAR(8) NULL, --最后更新日期
LAST_UPDATE_TIME                                  CHAR(6) NULL, --最后更新时间
UPDATE_USER                                       VARCHAR(10) NULL, --更新用户
COMPONENT_ID                                      DECIMAL(10) NULL, --组件ID
CONSTRAINT AUTO_COMP_GROUP_PK PRIMARY KEY(COMPONENT_GROUP_ID)   );

--目录监控配置表
DROP TABLE IF EXISTS DIRECTORY_SET ;
CREATE TABLE DIRECTORY_SET(
SET_ID                                            DECIMAL(10) NOT NULL, --配置id
WATCH_DIR                                         VARCHAR(512) NOT NULL, --要监控的目录
WATCH_TYPE                                        VARCHAR(10) NOT NULL, --监控类型
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT DIRECTORY_SET_PK PRIMARY KEY(SET_ID)   );

--组件条件表
DROP TABLE IF EXISTS AUTO_COMP_COND ;
CREATE TABLE AUTO_COMP_COND(
COMPONENT_COND_ID                                 DECIMAL(10) NOT NULL, --组件条件ID
ARITHMETIC_LOGIC                                  VARCHAR(100) NULL, --运算逻辑
COND_EN_COLUMN                                    VARCHAR(100) NULL, --条件字段英文名称
COND_CN_COLUMN                                    VARCHAR(100) NULL, --条件字段中文名称
COND_VALUE                                        VARCHAR(100) NULL, --条件值
OPERATOR                                          VARCHAR(100) NULL, --操作符
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CREATE_USER                                       VARCHAR(10) NULL, --创建用户
LAST_UPDATE_DATE                                  CHAR(8) NULL, --最后更新日期
LAST_UPDATE_TIME                                  CHAR(6) NULL, --最后更新时间
UPDATE_USER                                       VARCHAR(10) NULL, --更新用户
COMPONENT_ID                                      DECIMAL(10) NULL, --组件ID
CONSTRAINT AUTO_COMP_COND_PK PRIMARY KEY(COMPONENT_COND_ID)   );

--数据集市信息表
DROP TABLE IF EXISTS DATA_MART_INFO ;
CREATE TABLE DATA_MART_INFO(
DATA_MART_ID                                      DECIMAL(10) NOT NULL, --数据集市id
MART_NAME                                         VARCHAR(512) NOT NULL, --数据集市名称
MART_NUMBER                                       VARCHAR(512) NOT NULL, --数据库编号
MART_DESC                                         VARCHAR(512) NULL, --数据集市描述
MART_STORAGE_PATH                                 VARCHAR(512) NOT NULL, --数据集市存储路径
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CREATE_ID                                         DECIMAL(10) NOT NULL, --用户ID
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT DATA_MART_INFO_PK PRIMARY KEY(DATA_MART_ID)   );

--外部数据库访问信息表
DROP TABLE IF EXISTS AUTO_DB_ACCESS_INFO ;
CREATE TABLE AUTO_DB_ACCESS_INFO(
ACCESS_INFO_ID                                    DECIMAL(10) NOT NULL, --数据库访问信息表id
DB_TYPE                                           CHAR(2) NOT NULL, --数据库类型
DB_NAME                                           VARCHAR(100) NOT NULL, --数据库名称
DB_IP                                             VARCHAR(100) NOT NULL, --数据库服务ip
DB_PORT                                           VARCHAR(100) NOT NULL, --数据服访问端口
DB_USER                                           VARCHAR(100) NOT NULL, --数据库访问用户名
DB_PASSWORD                                       VARCHAR(100) NULL, --数据库访问密码
JDBCURL                                           VARCHAR(100) NOT NULL, --jdbcurl
COMPONENT_ID                                      DECIMAL(10) NULL, --组件ID
CONSTRAINT AUTO_DB_ACCESS_INFO_PK PRIMARY KEY(ACCESS_INFO_ID)   );

--数据表信息
DROP TABLE IF EXISTS DATATABLE_INFO ;
CREATE TABLE DATATABLE_INFO(
DATATABLE_ID                                      DECIMAL(10) NOT NULL, --数据表id
DATA_MART_ID                                      DECIMAL(10) NOT NULL, --数据集市id
DATATABLE_CN_NAME                                 VARCHAR(512) NOT NULL, --数据表中文名称
DATATABLE_EN_NAME                                 VARCHAR(512) NOT NULL, --数据表英文名称
DATATABLE_DESC                                    VARCHAR(200) NULL, --数据表描述
DATATABLE_CREATE_DATE                             CHAR(8) NOT NULL, --数据表创建日期
DATATABLE_CREATE_TIME                             CHAR(6) NOT NULL, --数据表创建时间
DATATABLE_DUE_DATE                                CHAR(8) NOT NULL, --数据表到期日期
DDLC_DATE                                         CHAR(8) NOT NULL, --DDL最后变更日期
DDLC_TIME                                         CHAR(6) NOT NULL, --DDL最后变更时间
DATAC_DATE                                        CHAR(8) NOT NULL, --数据最后变更日期
DATAC_TIME                                        CHAR(6) NOT NULL, --数据最后变更时间
DATATABLE_LIFECYCLE                               CHAR(1) NOT NULL, --数据表的生命周期
IS_PARTITION                                      CHAR(1) NOT NULL, --是否分区
SORUCE_SIZE                                       DECIMAL(16,2) default 0 NOT NULL, --资源大小
IS_HYREN_DB                                       CHAR(1) NOT NULL, --是否为HyrenDB
HY_SUCCESS                                        CHAR(3) default '105' NULL, --HyrenDB是否成功
IS_KV_DB                                          CHAR(1) NOT NULL, --是否为KeyValueDB
KV_SUCCESS                                        CHAR(3) default '105' NULL, --KeyValueDB是否成功
IS_SOLR_DB                                        CHAR(1) NOT NULL, --是否为solrDB
SOLR_SUCCESS                                      CHAR(3) default '105' NULL, --solrDB是否成功
IS_ELK_DB                                         CHAR(1) NOT NULL, --是否为elkDB
ELK_SUCCESS                                       CHAR(3) default '105' NULL, --elk是否成功
IS_SOLR_HBASE                                     CHAR(1) default '1' NOT NULL, --是否solrOnHbase
SOLRBASE_SUCCESS                                  CHAR(3) default '105' NULL, --solrOnHbase是否完成
IS_CARBONDATA_DB                                  CHAR(1) default '1' NOT NULL, --是否入carbondata
CARBONDATA_SUCCESS                                CHAR(3) default '105' NULL, --carbondata是否成功
ROWKEY_SEPARATOR                                  VARCHAR(10) NULL, --rowkey分隔符
PRE_PARTITION                                     VARCHAR(512) NULL, --预分区
ETL_DATE                                          CHAR(8) NOT NULL, --跑批日期
SQL_ENGINE                                        CHAR(1) NULL, --sql执行引擎
DATATYPE                                          CHAR(1) NOT NULL, --存储类型
IS_APPEND                                         CHAR(1) default '1' NOT NULL, --数据存储方式
IS_CURRENT_CLUSTER                                CHAR(1) default '1' NOT NULL, --是否是本集群表
IS_DATA_FILE                                      CHAR(1) default '1' NOT NULL, --是否是数据文件
EXFILE_SUCCESS                                    CHAR(3) default '105' NULL, --导出文件是否成功
REMARK                                            VARCHAR(6000) NULL, --备注
CONSTRAINT DATATABLE_INFO_PK PRIMARY KEY(DATATABLE_ID)   );

--组件数据汇总信息表
DROP TABLE IF EXISTS AUTO_COMP_DATA_SUM ;
CREATE TABLE AUTO_COMP_DATA_SUM(
COMP_DATA_SUM_ID                                  DECIMAL(10) NOT NULL, --组件数据汇总ID
COLUMN_NAME                                       VARCHAR(100) NOT NULL, --字段名
SUMMARY_TYPE                                      CHAR(2) NOT NULL, --汇总类型
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CREATE_USER                                       VARCHAR(10) NULL, --创建用户
LAST_UPDATE_DATE                                  CHAR(8) NULL, --最后更新日期
LAST_UPDATE_TIME                                  CHAR(6) NULL, --最后更新时间
UPDATE_USER                                       VARCHAR(10) NULL, --更新用户
COMPONENT_ID                                      DECIMAL(10) NULL, --组件ID
CONSTRAINT AUTO_COMP_DATA_SUM_PK PRIMARY KEY(COMP_DATA_SUM_ID)   );

--数据表字段信息
DROP TABLE IF EXISTS DATATABLE_FIELD_INFO ;
CREATE TABLE DATATABLE_FIELD_INFO(
DATATABLE_FIELD_ID                                DECIMAL(10) NOT NULL, --数据表字段id
DATATABLE_ID                                      DECIMAL(10) NOT NULL, --数据表id
FIELD_CN_NAME                                     VARCHAR(512) NOT NULL, --字段中文名称
FIELD_EN_NAME                                     VARCHAR(512) NOT NULL, --字段英文名称
IS_ROWKEY                                         CHAR(1) default '1' NOT NULL, --是否作为hbaseRowkey
FIELD_TYPE                                        VARCHAR(30) NOT NULL, --字段类型
FIELD_DESC                                        VARCHAR(200) NULL, --字段描述
FIELD_PROCESS                                     VARCHAR(10) NOT NULL, --处理方式
PROCESS_PARA                                      VARCHAR(512) NULL, --处理方式对应参数
FIELD_LENGTH                                      VARCHAR(200) NULL, --字段长度
FIELD_SEQ                                         DECIMAL(16) default 0 NOT NULL, --字段序号
ROWKEY_SEQ                                        DECIMAL(16) default 0 NOT NULL, --rowkey序号
REDIS_SEQ                                         DECIMAL(16) default 0 NOT NULL, --redis序号
IS_REDISKEY                                       CHAR(1) default '1' NOT NULL, --是否是rediskey
IS_SOLR_INDEX                                     CHAR(1) default '1' NOT NULL, --是否作为hbaseOnsolr索引
IS_CB                                             CHAR(1) default '1' NOT NULL, --是否carbondata聚合列
IS_SORTCOLUMNS                                    CHAR(1) default '1' NOT NULL, --是否为carbondata的排序列
IS_FIRST_LEVEL_COL                                CHAR(1) default '1' NOT NULL, --是否是一级分区列
IS_SECOND_LEVEL_COL                               CHAR(1) default '1' NOT NULL, --是否是二级分区列
REMARK                                            VARCHAR(6000) NULL, --备注
CONSTRAINT DATATABLE_FIELD_INFO_PK PRIMARY KEY(DATATABLE_FIELD_ID)   );

--横轴纵轴字段信息表
DROP TABLE IF EXISTS AUTO_AXIS_COL_INFO ;
CREATE TABLE AUTO_AXIS_COL_INFO(
AXIS_COLUMN_ID                                    DECIMAL(10) NOT NULL, --横轴纵轴字段ID
SERIAL_NUMBER                                     INTEGER default 0 NOT NULL, --序号
COLUMN_NAME                                       VARCHAR(100) NULL, --字段名称
SHOW_TYPE                                         CHAR(2) NOT NULL, --字段显示类型
COMPONENT_ID                                      DECIMAL(10) NULL, --组件ID
CONSTRAINT AUTO_AXIS_COL_INFO_PK PRIMARY KEY(AXIS_COLUMN_ID)   );

--数据表已选数据源信息
DROP TABLE IF EXISTS DATATABLE_OWN_SOURCE_INFO ;
CREATE TABLE DATATABLE_OWN_SOURCE_INFO(
OWN_DOURCE_TABLE_ID                               DECIMAL(10) NOT NULL, --已选数据源表id
DATATABLE_ID                                      DECIMAL(10) NOT NULL, --数据表id
OWN_SOURCE_TABLE_NAME                             VARCHAR(512) NOT NULL, --已选数据源表名
SOURCE_TYPE                                       CHAR(3) NOT NULL, --数据源类型
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT DATATABLE_OWN_SOURCE_INFO_PK PRIMARY KEY(OWN_DOURCE_TABLE_ID)   );

--组件样式属性表
DROP TABLE IF EXISTS AUTO_COMP_STYLE_ATTR ;
CREATE TABLE AUTO_COMP_STYLE_ATTR(
COMPONENT_STYLE_ID                                DECIMAL(10) NOT NULL, --组件样式ID
TITLE                                             VARCHAR(100) NULL, --标题
LEGEND                                            VARCHAR(100) NULL, --图例
HORIZONTAL_GRID_LINE                              VARCHAR(100) NULL, --横向网格线
VERTICAL_GRID_LINE                                VARCHAR(100) NULL, --纵向网格线
AXIS                                              VARCHAR(100) NULL, --轴线
COMPONENT_ID                                      DECIMAL(10) NULL, --组件ID
CONSTRAINT AUTO_COMP_STYLE_ATTR_PK PRIMARY KEY(COMPONENT_STYLE_ID)   );

--数据源表字段
DROP TABLE IF EXISTS OWN_SOURCE_FIELD ;
CREATE TABLE OWN_SOURCE_FIELD(
OWN_FIELD_ID                                      DECIMAL(10) NOT NULL, --字段id
OWN_DOURCE_TABLE_ID                               DECIMAL(10) NOT NULL, --已选数据源表id
FIELD_NAME                                        VARCHAR(512) NOT NULL, --字段名称
FIELD_TYPE                                        VARCHAR(512) NOT NULL, --字段类型
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT OWN_SOURCE_FIELD_PK PRIMARY KEY(OWN_FIELD_ID)   );

--图形属性
DROP TABLE IF EXISTS AUTO_GRAPHIC_ATTR ;
CREATE TABLE AUTO_GRAPHIC_ATTR(
GRAPHIC_ATTR_ID                                   DECIMAL(10) NOT NULL, --图形属性id
COLOR                                             CHAR(2) NULL, --图形颜色
SIZE                                              INTEGER default 0 NULL, --图形大小
CONNECTION                                        VARCHAR(100) NULL, --图形连线
LABEL                                             VARCHAR(100) NULL, --图形标签
PROMPT                                            VARCHAR(100) NULL, --图形提示
COMPONENT_ID                                      DECIMAL(10) NULL, --组件ID
CONSTRAINT AUTO_GRAPHIC_ATTR_PK PRIMARY KEY(GRAPHIC_ATTR_ID)   );

--数据操作信息表
DROP TABLE IF EXISTS SOURCE_OPERATION_INFO ;
CREATE TABLE SOURCE_OPERATION_INFO(
ID                                                DECIMAL(10) NOT NULL, --信息表id
DATATABLE_ID                                      DECIMAL(10) NOT NULL, --数据表id
EXECUTE_SQL                                       VARCHAR(6000) NOT NULL, --执行的sql语句
SEARCH_NAME                                       VARCHAR(512) NULL, --join类型
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT SOURCE_OPERATION_INFO_PK PRIMARY KEY(ID)   );

--仪表板信息表
DROP TABLE IF EXISTS AUTO_DASHBOARD_INFO ;
CREATE TABLE AUTO_DASHBOARD_INFO(
DASHBOARD_ID                                      DECIMAL(10) NOT NULL, --仪表板id
USER_ID                                           VARCHAR(10) NOT NULL, --创建用户
DASHBOARD_NAME                                    VARCHAR(100) NOT NULL, --仪表板名称
DASHBOARD_THEME                                   VARCHAR(100) NULL, --仪表板主题
DASHBOARD_DESC                                    VARCHAR(512) NULL, --仪表板描述
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
LAST_UPDATE_DATE                                  CHAR(8) NULL, --最后更新日期
LAST_UPDATE_TIME                                  CHAR(6) NULL, --最后更新时间
UPDATE_USER                                       VARCHAR(10) NULL, --更新用户
BORDERTYPE                                        CHAR(2) NULL, --边框类型
BACKGROUND                                        VARCHAR(16) NULL, --背景色
BORDERCOLOR                                       CHAR(2) NULL, --边框颜色
BORDERWIDTH                                       CHAR(2) NULL, --边框宽度
DASHBOARD_STATUS                                  CHAR(2) NULL, --仪表盘发布状态
CONSTRAINT AUTO_DASHBOARD_INFO_PK PRIMARY KEY(DASHBOARD_ID)   );

--结果映射信息表
DROP TABLE IF EXISTS ETLMAP_INFO ;
CREATE TABLE ETLMAP_INFO(
ETL_ID                                            DECIMAL(10) NOT NULL, --表id
DATATABLE_ID                                      DECIMAL(10) NOT NULL, --数据表id
OWN_DOURCE_TABLE_ID                               DECIMAL(10) NOT NULL, --已选数据源表id
TARGETFIELD_NAME                                  VARCHAR(512) NULL, --目标字段名称
SOURCEFIELDS_NAME                                 VARCHAR(512) NOT NULL, --来源字段名称
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT ETLMAP_INFO_PK PRIMARY KEY(ETL_ID)   );

--仪表板组件关联信息表
DROP TABLE IF EXISTS AUTO_ASSO_INFO ;
CREATE TABLE AUTO_ASSO_INFO(
ASSO_INFO_ID                                      DECIMAL(10) NOT NULL, --关联信息表id
SERIAL_NUMBER                                     INTEGER default 0 NOT NULL, --序号
X_AXIS_COORD                                      INTEGER default 0 NOT NULL, --X轴坐标
Y_AXIS_COORD                                      INTEGER default 0 NOT NULL, --Y轴坐标
LENGTH                                            INTEGER default 0 NOT NULL, --长度
WIDTH                                             INTEGER default 0 NOT NULL, --宽度
DASHBOARD_ID                                      DECIMAL(10) NULL, --仪表板id
COMPONENT_ID                                      DECIMAL(10) NULL, --组件ID
CONSTRAINT AUTO_ASSO_INFO_PK PRIMARY KEY(ASSO_INFO_ID)   );

--字段操作类型信息表
DROP TABLE IF EXISTS FIELD_OPERATION_INFO ;
CREATE TABLE FIELD_OPERATION_INFO(
INFO_ID                                           DECIMAL(10) NOT NULL, --序号
OWN_FIELD_ID                                      DECIMAL(10) NOT NULL, --字段id
OPERATION_TYPE                                    VARCHAR(512) NOT NULL, --操作类型
OPERATION_DETAIL                                  VARCHAR(200) NOT NULL, --具体操作
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT FIELD_OPERATION_INFO_PK PRIMARY KEY(INFO_ID)   );

--仪表板标题表
DROP TABLE IF EXISTS AUTO_LABEL_INFO ;
CREATE TABLE AUTO_LABEL_INFO(
LABEL_ID                                          CHAR(10) NOT NULL, --标题id
LABEL_CONTENT                                     VARCHAR(100) NULL, --标题文字
SERIAL_NUMBER                                     INTEGER default 0 NOT NULL, --序号
X_AXIS_COORD                                      INTEGER default 0 NOT NULL, --X轴坐标
Y_AXIS_COORD                                      INTEGER default 0 NOT NULL, --Y轴坐标
LENGTH                                            INTEGER default 0 NOT NULL, --标题长度
WIDTH                                             INTEGER default 0 NOT NULL, --标题宽度
LABEL_COLOR                                       VARCHAR(16) NULL, --标题背景颜色
LABEL_SIZE                                        VARCHAR(32) NULL, --标题大小
DASHBOARD_ID                                      DECIMAL(10) NULL, --仪表板id
CONSTRAINT AUTO_LABEL_INFO_PK PRIMARY KEY(LABEL_ID)   );

--全文检索排序表
DROP TABLE IF EXISTS SEARCH_INFO ;
CREATE TABLE SEARCH_INFO(
SI_ID                                             DECIMAL(10) NOT NULL, --si_id
FILE_ID                                           VARCHAR(40) NOT NULL, --文件编号
WORD_NAME                                         VARCHAR(1024) NOT NULL, --关键字
SI_COUNT                                          DECIMAL(16) default 0 NOT NULL, --点击量
SI_REMARK                                         VARCHAR(512) NULL, --备注
CONSTRAINT SEARCH_INFO_PK PRIMARY KEY(SI_ID)   );

--组件汇总表
DROP TABLE IF EXISTS AUTO_COMP_SUM ;
CREATE TABLE AUTO_COMP_SUM(
COMPONENT_ID                                      DECIMAL(10) NOT NULL, --组件ID
CHART_THEME                                       VARCHAR(64) NULL, --图形主题
USER_ID                                           VARCHAR(10) NULL, --用户ID
COMPONENT_NAME                                    VARCHAR(100) NOT NULL, --组件名称
COMPONENT_DESC                                    VARCHAR(512) NULL, --组件描述
DATA_SOURCE                                       CHAR(2) NOT NULL, --数据来源
COMPONENT_STATUS                                  CHAR(2) NOT NULL, --组件状态
SOURCES_OBJ                                       VARCHAR(100) NULL, --数据源对象
EXE_SQL                                           VARCHAR(512) NULL, --执行sql
CHART_TYPE                                        VARCHAR(100) NOT NULL, --图表类型
BACKGROUND                                        VARCHAR(16) NULL, --背景色
COMPONENT_BUFFER                                  VARCHAR(512) NULL, --组件缓存
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
CREATE_USER                                       VARCHAR(10) NULL, --创建用户
LAST_UPDATE_DATE                                  CHAR(8) NULL, --最后更新日期
LAST_UPDATE_TIME                                  CHAR(6) NULL, --最后更新时间
UPDATE_USER                                       VARCHAR(10) NULL, --更新用户
CONDITION_SQL                                     VARCHAR(800) NULL, --条件SQL
CONSTRAINT AUTO_COMP_SUM_PK PRIMARY KEY(COMPONENT_ID)   );

--仪表板分割线表
DROP TABLE IF EXISTS AUTO_LINE_INFO ;
CREATE TABLE AUTO_LINE_INFO(
LINE_ID                                           DECIMAL(10) NOT NULL, --分割线id
SERIAL_NUMBER                                     INTEGER default 0 NOT NULL, --序号
X_AXIS_COORD                                      INTEGER default 0 NOT NULL, --X轴坐标
Y_AXIS_COORD                                      INTEGER default 0 NOT NULL, --Y轴坐标
LINE_COLOR                                        CHAR(2) NULL, --颜色
LINE_TYPE                                         VARCHAR(32) NULL, --分割线样式
LINE_LENGTH                                       INTEGER default 0 NOT NULL, --分割线长度
LINE_WEIGHT                                       INTEGER default 0 NOT NULL, --分割线宽度
DASHBOARD_ID                                      DECIMAL(10) NOT NULL, --仪表板id
CONSTRAINT AUTO_LINE_INFO_PK PRIMARY KEY(LINE_ID)   );

--仪表板边框组件信息表
DROP TABLE IF EXISTS AUTO_FRAME_INFO ;
CREATE TABLE AUTO_FRAME_INFO(
FRAME_ID                                          DECIMAL(10) NOT NULL, --边框id
SERIAL_NUMBER                                     INTEGER default 0 NOT NULL, --序号
X_AXIS_COORD                                      INTEGER default 0 NOT NULL, --X轴坐标
Y_AXIS_COORD                                      INTEGER default 0 NOT NULL, --y轴坐标
BORDER_STYLE                                      VARCHAR(16) NOT NULL, --边框风格
BORDER_COLOR                                      VARCHAR(16) NOT NULL, --边框颜色
BORDER_WIDTH                                      INTEGER default 0 NOT NULL, --边框宽度
BORDER_RADIUS                                     INTEGER default 0 NULL, --边框圆角大小
IS_SHADOW                                         CHAR(1) NOT NULL, --是否启用阴影效果
LENGTH                                            INTEGER default 0 NULL, --组件长度
WIDTH                                             INTEGER default 0 NULL, --组件宽度
DASHBOARD_ID                                      DECIMAL(10) NOT NULL, --仪表板id
CONSTRAINT AUTO_FRAME_INFO_PK PRIMARY KEY(FRAME_ID)   );

--字体属性信息表
DROP TABLE IF EXISTS AUTO_FONT_INFO ;
CREATE TABLE AUTO_FONT_INFO(
FONT_ID                                           DECIMAL(10) NOT NULL, --字体信息id
COLOR                                             VARCHAR(32) NULL, --字体颜色
FONTFAMILY                                        VARCHAR(32) NULL, --字体系列
FONTSTYLE                                         VARCHAR(8) NULL, --字体风格
FONTWEIGHT                                        VARCHAR(16) NULL, --字体粗细
ALIGN                                             VARCHAR(16) NULL, --字体对齐方式
VERTICALALIGN                                     VARCHAR(16) NULL, --文字垂直对齐方式
LINEHEIGHT                                        INTEGER default 0 NULL, --行高
BACKGROUNDCOLOR                                   VARCHAR(16) NULL, --文字块背景色
BORDERCOLOR                                       VARCHAR(16) NULL, --文字块边框颜色
BORDERWIDTH                                       INTEGER default 0 NULL, --文字块边框宽度
BORDERRADIUS                                      INTEGER default 0 NULL, --文字块圆角
FONTSIZE                                          INTEGER default 0 NULL, --字体大小
FONT_CORR_TNAME                                   VARCHAR(32) NOT NULL, --字体属性对应的表名
FONT_CORR_ID                                      DECIMAL(10) NOT NULL, --字体属性对应的编号
CONSTRAINT AUTO_FONT_INFO_PK PRIMARY KEY(FONT_ID)   );

--轴配置信息表
DROP TABLE IF EXISTS AUTO_AXIS_INFO ;
CREATE TABLE AUTO_AXIS_INFO(
AXIS_ID                                           DECIMAL(10) NOT NULL, --轴编号
AXIS_TYPE                                         VARCHAR(1) NOT NULL, --轴类型
SHOW                                              VARCHAR(5) NOT NULL, --是否显示
POSITION                                          VARCHAR(8) NULL, --轴位置
AXISOFFSET                                        INTEGER default 0 NULL, --轴偏移量
NAME                                              VARCHAR(32) NULL, --轴名称
NAMELOCATION                                      VARCHAR(8) NULL, --轴名称位置
NAMEGAP                                           INTEGER default 0 NULL, --名称与轴线距离
NAMEROTATE                                        INTEGER default 0 NULL, --轴名字旋转角度
MIN                                               INTEGER default 0 NULL, --轴刻度最小值
MAX                                               INTEGER default 0 NULL, --轴刻度最大值
SILENT                                            VARCHAR(5) NULL, --坐标轴是否静态
COMPONENT_ID                                      DECIMAL(10) NOT NULL, --组件ID
CONSTRAINT AUTO_AXIS_INFO_PK PRIMARY KEY(AXIS_ID)   );

--轴线配置信息表
DROP TABLE IF EXISTS AUTO_AXISLINE_INFO ;
CREATE TABLE AUTO_AXISLINE_INFO(
AXISLINE_ID                                       DECIMAL(10) NOT NULL, --轴线编号
SHOW                                              VARCHAR(5) NOT NULL, --是否显示
ONZERO                                            VARCHAR(5) NULL, --是否在0 刻度
SYMBOL                                            VARCHAR(32) NULL, --轴线箭头显示方式
SYMBOLOFFSET                                      INTEGER default 0 NULL, --轴线箭头偏移量
AXIS_ID                                           DECIMAL(10) NULL, --轴编号
CONSTRAINT AUTO_AXISLINE_INFO_PK PRIMARY KEY(AXISLINE_ID)   );

--轴标签配置信息表
DROP TABLE IF EXISTS AUTO_AXISLABEL_INFO ;
CREATE TABLE AUTO_AXISLABEL_INFO(
LABLE_ID                                          DECIMAL(10) NOT NULL, --标签编号
SHOW                                              VARCHAR(5) NOT NULL, --是否显示
INSIDE                                            VARCHAR(5) NULL, --是否朝内
ROTATE                                            INTEGER default 0 NULL, --旋转角度
MARGIN                                            INTEGER default 0 NULL, --标签与轴线距离
FORMATTER                                         VARCHAR(80) NULL, --内容格式器
AXIS_ID                                           DECIMAL(10) NULL, --轴编号
CONSTRAINT AUTO_AXISLABEL_INFO_PK PRIMARY KEY(LABLE_ID)   );

--表格配置信息表
DROP TABLE IF EXISTS AUTO_TABLE_INFO ;
CREATE TABLE AUTO_TABLE_INFO(
CONFIG_ID                                         DECIMAL(10) NOT NULL, --配置编号
TH_BACKGROUND                                     VARCHAR(16) NULL, --表头背景色
IS_GRIDLINE                                       CHAR(1) NOT NULL, --是否使用网格线
IS_ZEBRALINE                                      CHAR(1) NOT NULL, --是否使用斑马线
ZL_BACKGROUND                                     VARCHAR(16) NULL, --斑马线颜色
COMPONENT_ID                                      DECIMAL(10) NULL, --组件ID
CONSTRAINT AUTO_TABLE_INFO_PK PRIMARY KEY(CONFIG_ID)   );

--更新信息表
DROP TABLE IF EXISTS UPDATE_INFO ;
CREATE TABLE UPDATE_INFO(
UPDATE_TYPE                                       VARCHAR(40) NOT NULL, --update_type
IS_UPDATE                                         CHAR(1) default '1' NOT NULL, --is_update
REMARK                                            VARCHAR(512) NULL, --备注
CONSTRAINT UPDATE_INFO_PK PRIMARY KEY(UPDATE_TYPE)   );

--组件图例信息表
DROP TABLE IF EXISTS AUTO_LEGEND_INFO ;
CREATE TABLE AUTO_LEGEND_INFO(
LEGEND_ID                                         DECIMAL(10) NOT NULL, --图例编号
TYPE                                              VARCHAR(8) NOT NULL, --图例类型
SHOW                                              VARCHAR(5) NULL, --是否显示
Z                                                 INTEGER default 0 NULL, --z值
LEFT_DISTANCE                                     VARCHAR(8) NULL, --左侧距离
TOP_DISTANCE                                      VARCHAR(8) NULL, --上侧距离
RIGHT_DISTANCE                                    VARCHAR(8) NULL, --右侧距离
BOTTOM_DISTANCE                                   VARCHAR(8) NULL, --下侧距离
WIDTH                                             VARCHAR(8) NULL, --宽度
HEIGHT                                            VARCHAR(8) NULL, --高度
ORIENT                                            VARCHAR(16) NULL, --布局朝向
ALIGN                                             VARCHAR(8) NULL, --标记和文本的对齐
PADDING                                           INTEGER default 0 NULL, --内边距
ITEMGAP                                           INTEGER default 0 NULL, --图例间隔
INTERVALNUMBER                                    INTEGER default 0 NULL, --图例个数
INTERVAL                                          INTEGER default 0 NULL, --图例容量
ITEMWIDTH                                         INTEGER default 0 NULL, --图形宽度
ITEMHEIGHT                                        INTEGER default 0 NULL, --图形高度
FORMATTER                                         VARCHAR(80) NULL, --格式化内容
SELECTEDMODE                                      VARCHAR(8) NULL, --图例选择
INACTIVECOLOR                                     VARCHAR(16) NULL, --图例关闭时颜色
TOOLTIP                                           VARCHAR(5) NULL, --是否显示提示
BACKGROUNDCOLOR                                   VARCHAR(32) NULL, --背景色
BORDERCOLOR                                       VARCHAR(32) NULL, --边框颜色
BORDERWIDTH                                       INTEGER default 0 NULL, --边框线宽
BORDERRADIUS                                      INTEGER default 0 NULL, --圆角半径
ANIMATION                                         VARCHAR(5) NULL, --图例翻页动画
COMPONENT_ID                                      DECIMAL(10) NULL, --组件ID
CONSTRAINT AUTO_LEGEND_INFO_PK PRIMARY KEY(LEGEND_ID)   );

--图表配置信息表
DROP TABLE IF EXISTS AUTO_CHARTSCONFIG ;
CREATE TABLE AUTO_CHARTSCONFIG(
CONFIG_ID                                         DECIMAL(10) NOT NULL, --配置编号
TYPE                                              VARCHAR(16) NOT NULL, --图表类型
PROVINCENAME                                      VARCHAR(32) NULL, --地图省份
XAXISINDEX                                        INTEGER default 0 NULL, --x轴索引号
YAXISINDEX                                        INTEGER default 0 NULL, --y轴索引号
SYMBOL                                            VARCHAR(16) NULL, --标记图形
SYMBOLSIZE                                        INTEGER default 0 NULL, --标记大小
SYMBOLROTATE                                      INTEGER default 0 NULL, --标记旋转角度
SHOWSYMBOL                                        VARCHAR(5) NULL, --显示标记
STACK                                             VARCHAR(8) NULL, --数据堆叠
CONNECTNULLS                                      VARCHAR(5) NULL, --连接空数据
STEP                                              VARCHAR(5) NULL, --是阶梯线图
SMOOTH                                            VARCHAR(5) NULL, --平滑曲线显示
Z                                                 INTEGER default 0 NULL, --z值
SILENT                                            VARCHAR(5) NULL, --触发鼠标事件
LEGENDHOVERLINK                                   VARCHAR(5) NULL, --启用图例联动高亮
CLOCKWISE                                         VARCHAR(5) NULL, --是顺时针排布
ROSETYPE                                          VARCHAR(5) NULL, --是南丁格尔图
CENTER                                            VARCHAR(24) NULL, --圆心坐标
RADIUS                                            VARCHAR(24) NULL, --半径
LEFT_DISTANCE                                     INTEGER default 0 NULL, --左侧距离
TOP_DISTANCE                                      INTEGER default 0 NULL, --上侧距离
RIGHT_DISTANCE                                    INTEGER default 0 NULL, --右侧距离
BOTTOM_DISTANCE                                   INTEGER default 0 NULL, --下侧距离
WIDTH                                             INTEGER default 0 NULL, --宽度
HEIGHT                                            INTEGER default 0 NULL, --高度
LEAFDEPTH                                         INTEGER default 0 NULL, --下钻层数
NODECLICK                                         VARCHAR(16) NULL, --点击节点行为
VISIBLEMIN                                        INTEGER default 0 NULL, --最小面积阈值
SORT                                              VARCHAR(8) NULL, --块数据排序方式
LAYOUT                                            VARCHAR(16) NULL, --布局方式
POLYLINE                                          VARCHAR(5) NULL, --是多段线
COMPONENT_ID                                      DECIMAL(10) NULL, --组件ID
CONSTRAINT AUTO_CHARTSCONFIG_PK PRIMARY KEY(CONFIG_ID)   );

--图表配置区域样式信息表
DROP TABLE IF EXISTS AUTO_AREASTYLE ;
CREATE TABLE AUTO_AREASTYLE(
STYLE_ID                                          DECIMAL(10) NOT NULL, --样式编号
COLOR                                             VARCHAR(16) NULL, --填充颜色
ORIGIN                                            VARCHAR(8) NULL, --区域起始位置
OPACITY                                           DECIMAL(3,2) NULL, --图形透明度
SHADOWBLUR                                        INTEGER default 0 NULL, --阴影模糊大小
SHADOWCOLOR                                       VARCHAR(16) NULL, --阴影颜色
SHADOWOFFSETX                                     INTEGER default 0 NULL, --阴影水平偏移距离
SHADOWOFFSETY                                     INTEGER default 0 NULL, --阴影垂直偏移距离
CONFIG_ID                                         DECIMAL(10) NULL, --配置编号
CONSTRAINT AUTO_AREASTYLE_PK PRIMARY KEY(STYLE_ID)   );

--图形文本标签表
DROP TABLE IF EXISTS AUTO_LABEL ;
CREATE TABLE AUTO_LABEL(
LABLE_ID                                          DECIMAL(10) NOT NULL, --标签编号
SHOW_LABEL                                        VARCHAR(5) NOT NULL, --显示文本标签
POSITION                                          VARCHAR(16) NULL, --标签位置
FORMATTER                                         VARCHAR(80) NULL, --标签内容格式器
SHOW_LINE                                         VARCHAR(5) NOT NULL, --显示视觉引导线
LENGTH                                            INTEGER default 0 NULL, --引导线第一段长度
LENGTH2                                           INTEGER default 0 NULL, --引导线第二段长度
SMOOTH                                            VARCHAR(5) NOT NULL, --平滑引导线
LABEL_CORR_TNAME                                  VARCHAR(32) NOT NULL, --标签对应的表名
LABEL_CORR_ID                                     DECIMAL(10) NOT NULL, --标签对于的编号
CONSTRAINT AUTO_LABEL_PK PRIMARY KEY(LABLE_ID)   );

--运行主机
DROP TABLE IF EXISTS RUN_HOST ;
CREATE TABLE RUN_HOST(
RUN_ID                                            DECIMAL(10) NOT NULL, --运行主机ID
RUN_HOST_IP                                       VARCHAR(50) NOT NULL, --运行主机IP
BIG_JOBCOUNT                                      DECIMAL(16) default 0 NOT NULL, --最大同时作业数
LONGEST_RUNTIME                                   VARCHAR(10) NOT NULL, --最长运行时间
RUNHOST_REMARK                                    VARCHAR(512) NULL, --备注
CONSTRAINT RUN_HOST_PK PRIMARY KEY(RUN_ID)   );

--作业agent
DROP TABLE IF EXISTS JOB_AGENT ;
CREATE TABLE JOB_AGENT(
AGENT_ID                                          DECIMAL(10) NOT NULL, --作业agent
RUN_ID                                            DECIMAL(10) NULL, --运行主机ID
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
AGENT_NAME                                        VARCHAR(512) NOT NULL, --作业agent名称
JOB_AGENT_PORT                                    VARCHAR(10) NOT NULL, --agent服务器端口
AGENT_STATUS                                      CHAR(1) NOT NULL, --agent状态
AGENT_REMARK                                      VARCHAR(512) NULL, --备注
CONSTRAINT JOB_AGENT_PK PRIMARY KEY(AGENT_ID)   );

--任务信息表
DROP TABLE IF EXISTS TASK_INFO ;
CREATE TABLE TASK_INFO(
TASK_ID                                           DECIMAL(10) NOT NULL, --任务ID
CREATE_ID                                         DECIMAL(10) NOT NULL, --创建用户
TASK_NAME                                         VARCHAR(512) NOT NULL, --任务名称
TASK_DESC                                         VARCHAR(200) NULL, --任务描述
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
PROJECT_ID                                        DECIMAL(10) NOT NULL, --工程ID
AGENT_ID                                          DECIMAL(10) NOT NULL, --作业agent
CONSTRAINT TASK_INFO_PK PRIMARY KEY(TASK_ID)   );

--工程信息表
DROP TABLE IF EXISTS PROJECT_INFO ;
CREATE TABLE PROJECT_INFO(
PROJECT_ID                                        DECIMAL(10) NOT NULL, --工程ID
CREATE_ID                                         DECIMAL(10) NOT NULL, --创建用户
PROJECT_NAME                                      VARCHAR(512) NOT NULL, --工程名称
PROJECT_DESC                                      VARCHAR(200) NOT NULL, --工程描述
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
AGENT_ID                                          DECIMAL(10) NOT NULL, --作业agent
CONSTRAINT PROJECT_INFO_PK PRIMARY KEY(PROJECT_ID)   );

--作业运行情况
DROP TABLE IF EXISTS JOB_OPERATION ;
CREATE TABLE JOB_OPERATION(
JOB_RS_ID                                         VARCHAR(40) NOT NULL, --作业执行id
PROCESS_ID                                        VARCHAR(10) NULL, --进程号
EXECUTE_S_DATE                                    CHAR(8) NOT NULL, --运行开始日期
EXECUTE_S_TIME                                    CHAR(6) NOT NULL, --运行开始时间
EXECUTE_LENGTH                                    VARCHAR(10) NULL, --运行总时长
EXECUTE_E_DATE                                    CHAR(8) NOT NULL, --运行结束日期
EXECUTE_E_TIME                                    CHAR(8) NOT NULL, --运行结束时间
EXECUTE_STATE                                     CHAR(3) NULL, --作业运行状态
IS_AGAIN                                          CHAR(1) NOT NULL, --是否重跑
ETL_DATE                                          CHAR(8) NOT NULL, --跑批日期
PRO_OPERTYPE                                      CHAR(4) NULL, --工程操作
TASK_OPERTYPE                                     CHAR(4) NULL, --任务操作
JOB_ID                                            DECIMAL(10) NOT NULL, --作业Id
COMP_ID                                           VARCHAR(20) NOT NULL, --组件编号
CONSTRAINT JOB_OPERATION_PK PRIMARY KEY(JOB_RS_ID)   );

--作业信息表
DROP TABLE IF EXISTS JOB_INFO ;
CREATE TABLE JOB_INFO(
JOB_ID                                            DECIMAL(10) NOT NULL, --作业Id
CREATE_ID                                         DECIMAL(10) NOT NULL, --创建用户
JOB_DESC                                          VARCHAR(200) NULL, --作业描述
JOB_PATH                                          VARCHAR(512) NULL, --作业目录
JOB_NAME                                          VARCHAR(512) NOT NULL, --作业名称
PRO_PARA                                          VARCHAR(200) NULL, --作业参数
LOG_DIC                                           VARCHAR(512) NULL, --日志路径
FRE_WEEK                                          VARCHAR(512) NULL, --周
FRE_MONTH                                         VARCHAR(512) NULL, --月
FRE_DAY                                           VARCHAR(512) NULL, --天
DISP_TIME                                         CHAR(6) NOT NULL, --触发时间
CRON_EXPRESSION                                   VARCHAR(512) NULL, --quartz执行表达式
JOB_S_DATE                                        CHAR(8) NOT NULL, --开始日期
JOB_E_DATE                                        CHAR(8) NOT NULL, --结束日期
RELY_JOB_ID                                       VARCHAR(512) NULL, --作业运行依赖作业ID
RUN_WAY                                           CHAR(1) NOT NULL, --启动方式
LOG_LEVEL                                         CHAR(1) NULL, --日志等级
EXCLUDING_INFO                                    VARCHAR(512) NULL, --排斥条件
JOB_PRIORITY                                      VARCHAR(10) NULL, --作业优先级
JOB_EFF_FLAG                                      CHAR(1) NOT NULL, --作业有效标志
AGENT_ID                                          DECIMAL(10) NOT NULL, --作业agent
PROJECT_ID                                        DECIMAL(10) NOT NULL, --工程ID
TASK_ID                                           DECIMAL(10) NOT NULL, --任务ID
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT JOB_INFO_PK PRIMARY KEY(JOB_ID)   );

--作业日志表
DROP TABLE IF EXISTS JOB_LOG ;
CREATE TABLE JOB_LOG(
LOG_ID                                            DECIMAL(10) NOT NULL, --日志id
OPERATIONER                                       VARCHAR(512) NOT NULL, --操作用户
OPERATION_DATE                                    CHAR(8) NOT NULL, --操作日期
OPER_TIME                                         CHAR(6) NOT NULL, --操作时间
PROCESS_ID                                        VARCHAR(10) NOT NULL, --进程号
EXECUTE_S_DATE                                    CHAR(8) NOT NULL, --运行开始日期
EXECUTE_S_TIME                                    CHAR(6) NOT NULL, --运行开始时间
EXECUTE_LENGTH                                    VARCHAR(10) NOT NULL, --运行总时长
EXECUTE_E_DATE                                    CHAR(8) NOT NULL, --运行结束日期
EXECUTE_E_TIME                                    CHAR(6) NOT NULL, --运行结束时间
EXECUTE_STATE                                     CHAR(3) NULL, --运行状态
IS_AGAIN                                          CHAR(1) NOT NULL, --是否重跑
ETL_DATE                                          CHAR(8) NOT NULL, --跑批日期
PRO_OPERTYPE                                      CHAR(4) NOT NULL, --工程操作
TASK_OPERTYPE                                     CHAR(4) NOT NULL, --任务操作
JOB_RS_ID                                         VARCHAR(40) NOT NULL, --作业执行id
CONSTRAINT JOB_LOG_PK PRIMARY KEY(LOG_ID)   );

--机器学习数据拆分简单随机抽取表
DROP TABLE IF EXISTS ML_DATAS_SREX ;
CREATE TABLE ML_DATAS_SREX(
REXTR_ID                                          DECIMAL(10) NOT NULL, --随机抽取编号
REXTRSEED_NUM                                     DECIMAL(16) default 0 NOT NULL, --随机抽取种子总数
TRAINSET_PERC                                     DECIMAL(16,2) default 0 NOT NULL, --训练集占比
TESTSET_PERC                                      DECIMAL(16,2) default 0 NOT NULL, --测试集占比
DATASPLIT_ID                                      DECIMAL(10) NULL, --数据拆分编号
CONSTRAINT ML_DATAS_SREX_PK PRIMARY KEY(REXTR_ID)   );

--机器学习数据拆分分层抽取表
DROP TABLE IF EXISTS ML_DATAS_HEXT ;
CREATE TABLE ML_DATAS_HEXT(
HIEREXTR_ID                                       DECIMAL(10) NOT NULL, --分层抽取编号
HIEREXTR_SEEDN                                    DECIMAL(16) default 0 NOT NULL, --分层抽取种子总数
TRAINSET_PERC                                     DECIMAL(16,2) default 0 NOT NULL, --训练集占比
EXTR_COLUMN                                       VARCHAR(64) NOT NULL, --抽取字段
DATASPLIT_ID                                      DECIMAL(10) NULL, --数据拆分编号
CONSTRAINT ML_DATAS_HEXT_PK PRIMARY KEY(HIEREXTR_ID)   );

--机器学习数据拆分表
DROP TABLE IF EXISTS ML_DATA_SPLIT ;
CREATE TABLE ML_DATA_SPLIT(
DATASPLIT_ID                                      DECIMAL(10) NOT NULL, --数据拆分编号
DSPLIT_EXTRTYPE                                   CHAR(1) NOT NULL, --数据拆分抽取类型
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NULL, --数据表信息编号
CONSTRAINT ML_DATA_SPLIT_PK PRIMARY KEY(DATASPLIT_ID)   );

--分类变量字段重编码表
DROP TABLE IF EXISTS ML_CV_COLUMN_RECODE ;
CREATE TABLE ML_CV_COLUMN_RECODE(
COLUMNRECO_ID                                     DECIMAL(10) NOT NULL, --字段重编码编号
VALUE_RECODE                                      VARCHAR(64) NULL, --编码前的值
NEWCOLUMN_NAME                                    VARCHAR(64) NOT NULL, --新字段名称
RECODE_ID                                         DECIMAL(10) NOT NULL, --重编码编号
CONSTRAINT ML_CV_COLUMN_RECODE_PK PRIMARY KEY(COLUMNRECO_ID)   );

--机器学习项目表
DROP TABLE IF EXISTS ML_PROJECT_INFO ;
CREATE TABLE ML_PROJECT_INFO(
PROJECT_ID                                        DECIMAL(10) NOT NULL, --项目编号
PROJECT_NAME                                      VARCHAR(512) NOT NULL, --项目名称
PROJECT_DESC                                      VARCHAR(200) NOT NULL, --项目描述
LAST_EXEC_DATE                                    CHAR(8) NOT NULL, --上次运行日期
LAST_EXEC_TIME                                    CHAR(6) NOT NULL, --上次运行时间
NOW_EXEC_DATE                                     CHAR(8) NOT NULL, --本次运行日期
NOW_EXEC_TIME                                     CHAR(6) NOT NULL, --本次运行时间
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
PUBLISH_STATUS                                    CHAR(1) NOT NULL, --发布状态
GOODNESS_OF_FIT                                   VARCHAR(64) NULL, --拟合度
REMARK                                            VARCHAR(512) NULL, --备注
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT ML_PROJECT_INFO_PK PRIMARY KEY(PROJECT_ID)   );

--机器学习数据信息表
DROP TABLE IF EXISTS ML_DATATABLE_INFO ;
CREATE TABLE ML_DATATABLE_INFO(
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
STABLE_CN_NAME                                    VARCHAR(512) NOT NULL, --数据源表中文名
STABLE_EN_NAME                                    VARCHAR(512) NOT NULL, --数据源表英文名
DSTORAGE_MODE                                     CHAR(1) NOT NULL, --数据存储方式
DATA_LOAD_MODE                                    CHAR(1) NOT NULL, --数据加载方式
DTABLE_RUNSTATE                                   CHAR(1) NOT NULL, --数据表运行状态
DTABLE_CDATE                                      CHAR(8) NOT NULL, --数据表创建日期
DTABLE_CTIME                                      CHAR(6) NOT NULL, --数据表创建时间
DATAMAPMODE                                       CHAR(1) NOT NULL, --数据映射方式
FILE_SIZE                                         DECIMAL(16) default 0 NULL, --数据表文件大小
FILE_PATH                                         VARCHAR(512) NULL, --数据源文件路径
RUN_DATE                                          CHAR(8) NULL, --运行日期
RUN_TIME                                          CHAR(6) NULL, --运行时间
RUN_END_DATE                                      CHAR(8) NULL, --结束运行日期
RUN_END_TIME                                      CHAR(6) NULL, --结束运行时间
PROGRAM_PATH                                      VARCHAR(1536) NULL, --程序路径
REMARK                                            VARCHAR(512) NULL, --备注
PROJECT_ID                                        DECIMAL(10) NULL, --项目编号
CONSTRAINT ML_DATATABLE_INFO_PK PRIMARY KEY(DTABLE_INFO_ID)   );

--机器学习数据探索图表数据表
DROP TABLE IF EXISTS ML_CHART_DATA ;
CREATE TABLE ML_CHART_DATA(
CHART_DATA_ID                                     DECIMAL(10) NOT NULL, --图表数据编号
CATEGORYAXISX                                     VARCHAR(64) NOT NULL, --类别轴x
CLUSTERING                                        VARCHAR(64) NOT NULL, --聚类
CHARTSTATISTICTYPE                                CHAR(1) NOT NULL, --图表统计量类型
CHART_NAME                                        VARCHAR(64) NOT NULL, --图表名称
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
RELATION_ID                                       DECIMAL(10) NOT NULL, --图表关系编号
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_CHART_DATA_PK PRIMARY KEY(CHART_DATA_ID)   );

--机器学习数据表字段信息表
DROP TABLE IF EXISTS ML_SOURTABLE_COLUMN ;
CREATE TABLE ML_SOURTABLE_COLUMN(
TABLE_COLUMN_ID                                   DECIMAL(10) NOT NULL, --数据源表字段编号
OCOLUMN_NAME                                      VARCHAR(512) NULL, --原始字段名称
COLUMN_CN_NAME                                    VARCHAR(64) NOT NULL, --字段中文名
COLUMN_EN_NAME                                    VARCHAR(64) NOT NULL, --字段英文名
COLUMN_TYPE                                       CHAR(1) NOT NULL, --字段类型
COLUMN_DESC                                       VARCHAR(200) NULL, --字段描述
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_SOURTABLE_COLUMN_PK PRIMARY KEY(TABLE_COLUMN_ID)   );

--机器学习数据探索统计分析表
DROP TABLE IF EXISTS ML_STATANALYSE ;
CREATE TABLE ML_STATANALYSE(
STATISTICS_ID                                     DECIMAL(10) NOT NULL, --统计表编号
STAT_ANAL_COLUMN                                  VARCHAR(64) NOT NULL, --统计性分析字段
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
AVERAGE                                           CHAR(1) NULL, --均值
MEDIAN                                            CHAR(1) NULL, --中位数
MODE                                              CHAR(1) NULL, --众数
COUNT                                             CHAR(1) NULL, --计数
STANDARD_DEVIATION                                CHAR(1) NULL, --标准差
MAXIMUM                                           CHAR(1) NULL, --最大值
MINIMUM                                           CHAR(1) NULL, --最小值
VARIANCE                                          CHAR(1) NULL, --方差
FOUR_QUANTILE                                     CHAR(1) NULL, --四位分位数
CUT_POINT                                         CHAR(1) NULL, --割点
QUANTILE_NUMBER                                   DECIMAL(16) default 0 NULL, --分位数位数
PP_PLOT                                           CHAR(1) NULL, --p-p图
QQ_PLOT                                           CHAR(1) NULL, --q-q图
REMARK                                            VARCHAR(512) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_STATANALYSE_PK PRIMARY KEY(STATISTICS_ID)   );

--机器学习数据探索相关性分析字段表
DROP TABLE IF EXISTS ML_CANALYSE_COLUMN ;
CREATE TABLE ML_CANALYSE_COLUMN(
CORRANALC_ID                                      DECIMAL(10) NOT NULL, --相关性分析字段编号
CORRANAL_COLU                                     VARCHAR(64) NOT NULL, --相关性分析字段
CORRELATION_ID                                    DECIMAL(10) NOT NULL, --相关性表编号
CONSTRAINT ML_CANALYSE_COLUMN_PK PRIMARY KEY(CORRANALC_ID)   );

--机器学习数据探索相关性分析表
DROP TABLE IF EXISTS ML_CORRANALYSE ;
CREATE TABLE ML_CORRANALYSE(
CORRELATION_ID                                    DECIMAL(10) NOT NULL, --相关性表编号
CORR_TYPE                                         CHAR(1) NOT NULL, --系数类型
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(512) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_CORRANALYSE_PK PRIMARY KEY(CORRELATION_ID)   );

--数据预处理数据转换表
DROP TABLE IF EXISTS ML_DATA_TRANSFER ;
CREATE TABLE ML_DATA_TRANSFER(
DATATRAN_ID                                       DECIMAL(10) NOT NULL, --数据转换编号
DATATRAN_COLUMN                                   VARCHAR(64) NOT NULL, --数据转换字段
NEWCOLUMN_NAME                                    VARCHAR(64) NOT NULL, --新字段名称
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
FUNMAP_ID                                         DECIMAL(10) NOT NULL, --函数对应编码
CONSTRAINT ML_DATA_TRANSFER_PK PRIMARY KEY(DATATRAN_ID)   );

--机器学习SQL记录表
DROP TABLE IF EXISTS ML_SQL_RECORD ;
CREATE TABLE ML_SQL_RECORD(
SQL_ID                                            DECIMAL(10) NOT NULL, --SQL编号
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
SQL_ORIGINAL                                      VARCHAR(1024) NOT NULL, --原始的sql语句
SELECT_AS_COLUMN                                  VARCHAR(1024) NULL, --字段与别名分析结果
TABLE_ANALYSE                                     VARCHAR(1024) NULL, --sql表分析结果
CONDITION_ANALYSE                                 VARCHAR(1024) NULL, --sql条件分析结果
GROUPBY_ANALYSE                                   VARCHAR(1024) NULL, --sql分组分析结果
ORDERBY_ANALYSE                                   VARCHAR(1024) NULL, --sql排序分析结果
JOIN_ANALYSE                                      VARCHAR(1024) NULL, --sql连接分析结果
HAVING_ANALYSE                                    VARCHAR(1024) NULL, --sql连接条件分析结果
LIMIT_ANALYSE                                     VARCHAR(1024) NULL, --sql限数分析结果
PROJECT_ID                                        DECIMAL(10) NULL, --项目编号
USER_ID                                           DECIMAL(10) NOT NULL, --用户ID
CONSTRAINT ML_SQL_RECORD_PK PRIMARY KEY(SQL_ID)   );

--数据预处理数据异常值处理表
DROP TABLE IF EXISTS ML_DATA_EXCEPTION ;
CREATE TABLE ML_DATA_EXCEPTION(
DATAEXCE_ID                                       DECIMAL(10) NOT NULL, --数据异常处理编号
DATAEXCE_COLUMN                                   VARCHAR(64) NOT NULL, --数据异常值处理字段
IDENTIFY_COND                                     CHAR(1) NOT NULL, --标识条件
MAX_CONDITION                                     DECIMAL(16,2) default 0 NULL, --最高条件值
MIN_CONDITION                                     DECIMAL(16,2) default 0 NULL, --最低条件值
FIXED_QUANTITY                                    DECIMAL(16) default 0 NULL, --固定数量
STANDARD_DEVI                                     DECIMAL(16,2) default 0 NULL, --标准差
PROCESS_MODE                                      CHAR(1) NOT NULL, --处理方式
NEWCOLUMN_NAME                                    VARCHAR(64) NOT NULL, --新字段名称
REPLACEMENT                                       CHAR(1) NULL, --替换方式
REPLACE_PERCENT                                   DECIMAL(16,2) default 0 NULL, --替换百分数
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
REMARK                                            VARCHAR(80) NULL, --备注
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_DATA_EXCEPTION_PK PRIMARY KEY(DATAEXCE_ID)   );

--数据预处理数据缺失处理表
DROP TABLE IF EXISTS ML_MISS_DATA ;
CREATE TABLE ML_MISS_DATA(
MISSVALUE_ID                                      DECIMAL(10) NOT NULL, --缺失值编号
MISSDATA_COLUMN                                   VARCHAR(64) NOT NULL, --数据缺失处理字段
MISSVALUE_PROC                                    CHAR(1) NOT NULL, --缺失值处理方式
NEWCOLUMN_NAME                                    VARCHAR(64) NOT NULL, --新字段名称
MISSPROC_TYPE                                     CHAR(1) NULL, --缺失值处理类型
K_NUM                                             DECIMAL(16) default 0 NULL, --k的个数
REMARK                                            VARCHAR(80) NULL, --备注
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
DTABLE_INFO_ID                                    DECIMAL(10) NOT NULL, --数据表信息编号
CONSTRAINT ML_MISS_DATA_PK PRIMARY KEY(MISSVALUE_ID)   );

--机器学习预处理新字段信息表
DROP TABLE IF EXISTS ML_DTABLE_NEWCOLUMN ;
CREATE TABLE ML_DTABLE_NEWCOLUMN(
TABLE_COLUMN_ID                                   DECIMAL(10) NOT NULL, --数据源表字段编号
COLUMN_CN_NAME                                    VARCHAR(64) NOT NULL, --字段中文名
OCOLUMN_EN_NAME                                   VARCHAR(64) NOT NULL, --原始字段英文名
OCOLUMN_TYPE                                      CHAR(1) NOT NULL, --字段类型
NEWCOLUMN_NAME                                    VARCHAR(64) NOT NULL, --新字段英文名称
USE_ISFLAG                                        CHAR(1) NOT NULL, --是否使用
CREATE_DATE                                       CHAR(8) NOT NULL, --创建日期
CREATE_TIME                                       CHAR(6) NOT NULL, --创建时间
DATAPO_INFO_ID                                    DECIMAL(10) NOT NULL, --数据预处理其它信息编号
CONSTRAINT ML_DTABLE_NEWCOLUMN_PK PRIMARY KEY(TABLE_COLUMN_ID)   );

--机器学习项目状态表
DROP TABLE IF EXISTS ML_PROJECT_STATUS ;
CREATE TABLE ML_PROJECT_STATUS(
STATUS_ID                                         DECIMAL(10) NOT NULL, --状态编号
PROJECT_ID                                        DECIMAL(10) NOT NULL, --项目编号
PROJECT_STATUS                                    CHAR(1) NOT NULL, --项目状态
START_IS_FLAG                                     CHAR(1) NOT NULL, --是否开始
END_IS_FLAG                                       CHAR(1) NOT NULL, --是否结束
UPDATE_DATE                                       CHAR(8) NOT NULL, --修改日期
UPDATE_TIME                                       CHAR(6) NOT NULL, --修改时间
CONSTRAINT ML_PROJECT_STATUS_PK PRIMARY KEY(STATUS_ID)   );

