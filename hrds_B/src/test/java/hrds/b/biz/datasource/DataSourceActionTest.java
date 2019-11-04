package hrds.b.biz.datasource;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.SystemUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * 数据源增删改，导入、下载测试类
 *
 * @author dhw
 * @date 2019-09-18 10:48:14
 */
public class DataSourceActionTest extends WebBaseTestCase {
    // 测试登录用户ID
    private static final long UserId = 3333L;
    // 初始化创建用户ID
    private static final long CreateId = 1000L;
    // 测试数据源 SourceId
    private static final long SourceId = -100000001L;
    // 测试数据源 SourceId,新建数据源，下面没有agent
    private static final long SourceId2 = -100000002L;
    // 测试数据库 agent_id
    private static final long DBAgentId = -200000011L;
    // 测试数据文件 agent_id
    private static final long DFAgentId = -200000012L;
    // 测试非结构化 agent_id
    private static final long UnsAgentId = -200000013L;
    // 测试半结构化 agent_id
    private static final long SemiAgentId = -200000014L;
    // 测试FTP agent_id
    private static final long FTPAgentId = -200000015L;
    // 测试部门ID dep_id,测试第一部门
    private static final long DepId1 = -300000011L;
    // 测试部门ID dep_id 测试第二部门
    private static final long DepId2 = -300000012L;
    // 测试agent_down_info down_id
    private static final long DownId = -300000001L;
    // 测试 分类ID，classify_id
    private static final long ClassifyId = -400000001L;
    // 测试 数据库设置ID，DatabaseId
    private static final long DatabaseId = -500000001L;
    // 测试 ftp_collect表ID，ftp_id
    private static final long FtpId = -500000011L;
    // 测试 ftp_collect表ID，ftp_transfered_id
    private static final long FtpTransferedId = -500000012L;
    // 测试 ftp_folder表ID，ftp_folder_id
    private static final long FtpFolderId = -500000091L;
    // 测试 object_collect表ID，odc_id
    private static final long OdcId = -500000101L;
    // 测试 object_collect_task表ID，ocs_id
    private static final long OcsId = -500000102L;
    // 测试 object_storage表ID，obj_stid
    private static final long ObjStid = -500000103L;
    // 测试 object_collect_struct表ID，struct_id
    private static final long StructId = -500000104L;
    // 测试 file_collect_set表ID，fcs_id
    private static final long FcsId = -500000105L;
    // 测试 file_source表ID，file_source_id
    private static final long FileSourceId = -500000106L;
    // 测试 signal_file表ID，signal_id
    private static final long SignalId = -500000107L;
    // 测试 table_info表ID，table_id
    private static final long TableId = -500000108L;
    // 测试 column_merge表ID，col_merge_id
    private static final long ColumnMergeId = -500000109L;
    // 测试 table_storage_info表ID，storage_id
    private static final long StorageId = -500000110L;
    // 测试 table_clean表ID，table_clean_id
    private static final long TableCleanId = -500000111L;
    // 测试 table_column表ID，column_id
    private static final long ColumnId = -500000112L;
    // 测试 column_clean表ID，col_clean_id
    private static final long ColumnCleanId = -500000113L;
    // 测试 column_split表ID，col_split_id
    private static final long ColSplitId = -500000114L;
    // 测试 source_file_attribute表ID，file_id
    private static final String FileId = "200000000000";
    // 测试 data_auth表ID，da_id
    private static final long DaId = -500000115L;

    @Method(desc = "初始化测试用例数据", logicStep = "1.构建数据源data_source表测试数据" +
            "2.构造department_info部门表测试数据" +
            "3.构造source_relation_dep表测试数据" +
            "4.构造agent_info表测试数据" +
            "5.构造sys_user表测试数据" +
            "6.构造database_set表测试数据" +
            "7.构造agent_down_info表测试数据" +
            "8.构造collect_job_classify表测试数据" +
            "9.构造ftp_collect表测试数据" +
            "10.构造ftp_transfered表测试数据" +
            "11.构造ftp_folder表测试数据" +
            "12.构造object_collect表测试数据" +
            "13.构造object_collect_task表测试数据" +
            "14.构造object_storage表测试数据" +
            "15.构造object_collect_struct表测试数据" +
            "16.构造file_collect_set表测试数据" +
            "17.构造file_source表测试数据" +
            "18.构造signal_file表测试数据" +
            "19.构造table_info表测试数据" +
            "20.构造column_merge表测试数据" +
            "21.构造table_storage_info表测试数据" +
            "22.构造table_clean表测试数据" +
            "23.构造table_column表测试数据" +
            "24.构造column_clean表测试数据" +
            "25.构造column_split表测试数据" +
            "26.构造source_file_attribute表测试数据" +
            "27.初始化 data_auth 数据" +
            "28.提交事务" +
            "29.模拟用户登录" +
            "测试数据：" +
            "1.data_source表:有2条数据，source_id为：SourceId,SourceId2" +
            "2.source_relation_dep表：有4条数据,source_id为SourceId,SourceId2,dep_id为DepID1,DepId2" +
            "3.agent_info表：有5条数据,agent_id有五种，数据库agent,数据文件agent,非结构化agent,半结构化agent," +
            "FTP agent,分别为DBAgentId,DFAgentId,UnsAgentId,SemiAgentId,FtpAgentId" +
            "4.department_info表，有2条数据，dep_id为DepID1,DepId2" +
            "5.agent_down_info表，有1条数据，down_id为DownId" +
            "6.database_set表，有1条数据，database_id为DatabaseId" +
            "7.collect_job_classify表，有1条数据，classify_id为ClassifyId" +
            "8.ftp_collect表，有1条数据，ftp_id为FtpId" +
            "9.ftp_transfered表，有1条数据，ftp_transfered_id为FtpTransferedId" +
            "10.ftp_folder表，有1条数据，ftp_folder_id为FtpFolderId" +
            "11.object_collect表，有1条数据，odc_id为OdcId" +
            "12.object_collect_task表，有1条数据，ocs_id为OcsId" +
            "13.object_storage表，有1条数据，obj_stid为ObjStid" +
            "14.object_collect_struct表，有1条数据，struct_id为StructId" +
            "15.file_collect_set表，有1条数据，fcs_id为FcsId" +
            "16.file_source表，有1条数据，file_source_id为FileSourceId" +
            "17.signal_file表，有1条数据，signal_id为SignalId" +
            "18.table_info表，有1条数据，table_id为TableId" +
            "19.column_merge表，有1条数据，col_merge_id为ColumnMergeId" +
            "20.table_storage_info表，有1条数据，storage_id为StorageId" +
            "21.table_clean表，有1条数据，table_clean_id为TableCleanId" +
            "22.table_column表，有1条数据，column_id为ColumnId" +
            "23.column_clean表，有1条数据，col_clean_id为ColumnCleanId" +
            "24.column_split表，有1条数据，col_split_id为ColSplitId" +
            "25.sys_user表，有1条数据，user_id为UserId" +
            "26.source_file_attribute表，有一条数据,file_id为FileId" +
            "27.data_auth表，有一条数据，da_id为DaId")
    @Before
    public void before() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 1.构建数据源data_source表测试数据
            // 创建data_source表实体对象
            Data_source data_source = new Data_source();
            // 封装data_source表数据
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    data_source.setSource_id(SourceId);
                    data_source.setDatasource_number("ds01");
                } else {
                    data_source.setSource_id(SourceId2);
                    data_source.setDatasource_number("ds02");
                }
                data_source.setDatasource_name("dsName" + i);
                data_source.setCreate_date(DateUtil.getSysDate());
                data_source.setCreate_time(DateUtil.getSysTime());
                data_source.setCreate_user_id(UserId);
                data_source.setSource_remark("数据源详细描述" + i);
                data_source.setDatasource_remark("备注" + i);
                // 初始化data_source表信息
                int num = data_source.add(db);
                assertThat("测试数据data_source初始化", num, is(1));
            }
            // 2.构造department_info部门表测试数据
            // 创建department_info表实体对象
            Department_info department_info = new Department_info();
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    department_info.setDep_id(DepId1);
                    department_info.setDep_name("测试第" + (i + 1) + "部门");
                } else {
                    department_info.setDep_id(DepId2);
                    department_info.setDep_name("测试第" + (i + 1) + "部门");
                }
                department_info.setCreate_date(DateUtil.getSysDate());
                department_info.setCreate_time(DateUtil.getSysTime());
                department_info.setDep_remark("测试");
                int diNum = department_info.add(db);
                assertThat("测试数据department_info初始化", diNum, is(1));
            }

            // 3.构造source_relation_dep数据源与部门关系表测试数据
            // 创建source_relation_dep表实体对象
            Source_relation_dep source_relation_dep = new Source_relation_dep();
            for (long i = 0; i < 4; i++) {
                if (i < 2) {
                    source_relation_dep.setSource_id(SourceId);
                    if (i == 0) {
                        source_relation_dep.setDep_id(DepId1);
                    } else {
                        source_relation_dep.setDep_id(DepId2);
                    }
                } else {
                    // 封装source_relation_dep表数据
                    source_relation_dep.setSource_id(SourceId2);
                    if (i == 2) {
                        source_relation_dep.setDep_id(DepId1);
                    } else {
                        source_relation_dep.setDep_id(DepId2);
                    }
                }
                // 初始化source_relation_dep表信息
                int srdNum = source_relation_dep.add(db);
                assertThat("测试source_relation_dep数据初始化", srdNum, is(1));
            }
            // 4.构造agent_info表测试数据
            // 创建agent_info表实体对象
            Agent_info agent_info = new Agent_info();
            for (int i = 0; i < 5; i++) {
                // 封装agent_info数据
                agent_info.setSource_id(SourceId);
                agent_info.setCreate_date(DateUtil.getSysDate());
                agent_info.setCreate_time(DateUtil.getSysTime());
                agent_info.setAgent_ip("10.71.4.51");
                agent_info.setUser_id(UserId);
                agent_info.setAgent_port("34567");
                // 初始化不同类型的agent
                if (i == 1) {
                    // 数据库 agent
                    agent_info.setAgent_id(DBAgentId);
                    agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
                    agent_info.setAgent_name("sjkAgent");
                } else if (i == 2) {
                    // 数据文件 Agent
                    agent_info.setAgent_id(DFAgentId);
                    agent_info.setAgent_type(AgentType.DBWenJian.getCode());
                    agent_info.setAgent_name("DFAgent");

                } else if (i == 3) {
                    // 非结构化 Agent
                    agent_info.setAgent_id(UnsAgentId);
                    agent_info.setAgent_type(AgentType.WenJianXiTong.getCode());
                    agent_info.setAgent_name("UnsAgent");
                } else if (i == 4) {
                    // 半结构化 Agent
                    agent_info.setAgent_id(SemiAgentId);
                    agent_info.setAgent_type(AgentType.DuiXiang.getCode());
                    agent_info.setAgent_name("SemiAgent");
                } else {
                    // FTP Agent
                    agent_info.setAgent_id(FTPAgentId);
                    agent_info.setAgent_type(AgentType.FTP.getCode());
                    agent_info.setAgent_name("FTPAgent");
                }
                // 初始化agent不同的连接状态
                if (i < 2) {
                    // 已连接
                    agent_info.setAgent_status(AgentStatus.YiLianJie.getCode());
                } else if (i >= 2 && i < 4) {
                    // 未连接
                    agent_info.setAgent_status(AgentStatus.WeiLianJie.getCode());
                } else {
                    // 正在运行
                    agent_info.setAgent_status(AgentStatus.ZhengZaiYunXing.getCode());
                }
                // 初始化agent_info数据
                int aiNum = agent_info.add(db);
                assertThat("测试agent_info数据初始化", aiNum, is(1));
            }
            // 5.构造sys_user表测试数据
            Sys_user sysUser = new Sys_user();
            sysUser.setUser_id(UserId);
            sysUser.setCreate_id(CreateId);
            sysUser.setDep_id(DepId1);
            sysUser.setCreate_date(DateUtil.getSysDate());
            sysUser.setCreate_time(DateUtil.getSysTime());
            sysUser.setRole_id("1001");
            sysUser.setUser_name("数据源功能测试");
            sysUser.setUser_password("1");
            sysUser.setUser_type(UserType.CaiJiYongHu.getCode());
            sysUser.setUseris_admin("1");
            sysUser.setUsertype_group("02,03,04,08");
            sysUser.setUser_state(IsFlag.Shi.getCode());
            sysUser.add(db);
            // 6.构造database_set表测试数据
            Database_set databaseSet = new Database_set();
            databaseSet.setDatabase_id(DatabaseId);
            databaseSet.setAgent_id(DBAgentId);
            databaseSet.setClassify_id(ClassifyId);
            databaseSet.setDatabase_code(DataBaseCode.UTF_8.getCode());
            databaseSet.setDatabase_drive("org.postgresql.Driver");
            databaseSet.setDatabase_ip("10.71.4.51");
            databaseSet.setDatabase_name("数据库采集测试");
            databaseSet.setDatabase_number("-50000000");
            databaseSet.setDatabase_pad("hrsdxg");
            databaseSet.setDatabase_port("34567");
            databaseSet.setDbfile_format(FileFormat.CSV.getCode());
            databaseSet.setIs_sendok(IsFlag.Fou.getCode());
            databaseSet.setDatabase_type(DatabaseType.Postgresql.getCode());
            databaseSet.setTask_name("数据库测试");
            databaseSet.setJdbc_url("jdbc:postgresql://10.71.4.52:31001/hrsdxgtest");
            databaseSet.setDb_agent(IsFlag.Shi.getCode());
            databaseSet.add(db);
            // 7.构造agent_down_info表测试数据
            Agent_down_info agent_down_info = new Agent_down_info();
            agent_down_info.setDown_id(DownId);
            agent_down_info.setAgent_id(DFAgentId);
            agent_down_info.setAgent_name("DFAgent");
            agent_down_info.setAgent_ip("10.71.4.51");
            agent_down_info.setAgent_port("34567");
            agent_down_info.setAgent_type(AgentType.DBWenJian.getCode());
            agent_down_info.setDeploy(IsFlag.Fou.getCode());
            agent_down_info.setLog_dir("/home/hyshf/sjkAgent_34567/log/");
            agent_down_info.setPasswd("hyshf");
            agent_down_info.setUser_id(UserId);
            agent_down_info.setAi_desc("agent部署");
            agent_down_info.setRemark("备注");
            agent_down_info.setUser_name("hyshf");
            agent_down_info.setSave_dir("/home/hyshf/sjkAgent_34567/");
            agent_down_info.setAgent_context("/agent");
            agent_down_info.setAgent_pattern("/hrds/agent/trans/biz/AgentServer/getSystemFileInfo");
            // 初始化agent_down_info表数据
            agent_down_info.add(db);
            // 8.构造collect_job_classify表测试数据
            Collect_job_classify collectJobClassify = new Collect_job_classify();
            collectJobClassify.setClassify_id(ClassifyId);
            collectJobClassify.setUser_id(UserId);
            collectJobClassify.setAgent_id(DBAgentId);
            collectJobClassify.setClassify_name("数据库采集测试");
            collectJobClassify.setRemark("测试");
            collectJobClassify.setClassify_num("sjkCs01");
            collectJobClassify.add(db);
            // 9.构造ftp_collect表测试数据
            Ftp_collect ftpCollect = new Ftp_collect();
            ftpCollect.setFtp_id(FtpId);
            ftpCollect.setFtp_number("ftpcj01");
            ftpCollect.setFtp_name("ftp采集");
            ftpCollect.setStart_date(DateUtil.getSysDate());
            ftpCollect.setEnd_date("99991231");
            ftpCollect.setFtp_ip("10.71.4.52");
            ftpCollect.setFtp_port("34567");
            ftpCollect.setFtp_username("hrsdxg");
            ftpCollect.setFtp_password("hrsdxg");
            ftpCollect.setFtp_dir("/home/hyshf/ftp_34567/");
            ftpCollect.setLocal_path("/home/hyshf/ftp_34567/");
            ftpCollect.setFtp_rule_path(FtpRule.AnShiJian.getCode());
            ftpCollect.setFtp_model("");
            ftpCollect.setRun_way(ExecuteWay.AnShiQiDong.getCode());
            ftpCollect.setIs_sendok(IsFlag.Shi.getCode());
            ftpCollect.setIs_unzip(IsFlag.Shi.getCode());
            ftpCollect.setAgent_id(DBAgentId);
            ftpCollect.setIs_read_realtime(IsFlag.Shi.getCode());
            ftpCollect.add(db);
            // 10.构造ftp_transfered表测试数据
            Ftp_transfered ftpTransfered = new Ftp_transfered();
            ftpTransfered.setFtp_transfered_id(FtpTransferedId);
            ftpTransfered.setFtp_id(FtpId);
            ftpTransfered.setFtp_date(DateUtil.getSysDate());
            ftpTransfered.setFtp_time(DateUtil.getSysTime());
            ftpTransfered.setTransfered_name("ftp传输测试");
            ftpTransfered.add(db);
            // 11.构造ftp_folder表测试数据
            Ftp_folder ftpFolder = new Ftp_folder();
            ftpFolder.setFtp_folder_id(FtpFolderId);
            ftpFolder.setFtp_id(FtpId);
            ftpFolder.setFtp_date(DateUtil.getSysDate());
            ftpFolder.setFtp_time(DateUtil.getSysTime());
            ftpFolder.setFtp_folder_name("ftp采集目录");
            ftpFolder.setIs_processed(IsFlag.Shi.getCode());
            ftpFolder.add(db);
            // 12.构造object_collect表测试数据
            Object_collect objectCollect = new Object_collect();
            objectCollect.setOdc_id(OdcId);
            objectCollect.setAgent_id(DBAgentId);
            objectCollect.setDatabase_code(DataBaseCode.UTF_8.getCode());
            objectCollect.setE_date("99991231");
            objectCollect.setFile_path("/home/hyshf/objCollect");
            objectCollect.setHost_name("10.71.4.52");
            objectCollect.setIs_sendok(IsFlag.Shi.getCode());
            objectCollect.setLocal_time(DateUtil.getSysDate());
            objectCollect.setObj_collect_name("对象采集测试");
            objectCollect.setObj_number("dxcj01");
            objectCollect.setRun_way(ExecuteWay.AnShiQiDong.getCode());
            objectCollect.setS_date(DateUtil.getSysDate());
            objectCollect.setServer_date(DateUtil.getSysDate());
            objectCollect.setSystem_name("windows");
            objectCollect.setObject_collect_type(ObjectCollectType.DuiXiangCaiJi.getCode());
            objectCollect.add(db);
            // 13.构造object_collect_task表测试数据
            Object_collect_task objectCollectTask = new Object_collect_task();
            objectCollectTask.setOcs_id(OcsId);
            objectCollectTask.setAgent_id(DBAgentId);
            objectCollectTask.setEn_name("dxcjrwmc");
            objectCollectTask.setZh_name("对象采集任务名称");
            objectCollectTask.setCollect_data_type(CollectDataType.JSON.getCode());
            objectCollectTask.setDatabase_code(DataBaseCode.UTF_8.getCode());
            objectCollectTask.setOdc_id(OdcId);
            objectCollectTask.add(db);
            // 14.构造object_storage表测试数据
            Object_storage objectStorage = new Object_storage();
            objectStorage.setObj_stid(ObjStid);
            objectStorage.setOcs_id(OcsId);
            objectStorage.setIs_hbase(IsFlag.Shi.getCode());
            objectStorage.setIs_hdfs(IsFlag.Fou.getCode());
            objectStorage.add(db);
            // 15.构造object_collect_struct表测试数据
            Object_collect_struct objectCollectStruct = new Object_collect_struct();
            objectCollectStruct.setStruct_id(StructId);
            objectCollectStruct.setOcs_id(OcsId);
            objectCollectStruct.setColl_name("对象采集结构");
            objectCollectStruct.setStruct_type(ObjectDataType.ZiFuChuan.getCode());
            objectCollectStruct.setData_desc("测试");
            objectCollectStruct.add(db);
            // 16.构造file_collect_set表测试数据
            File_collect_set fileCollectSet = new File_collect_set();
            fileCollectSet.setFcs_id(FcsId);
            fileCollectSet.setAgent_id(DBAgentId);
            fileCollectSet.setFcs_name("文件系统设置");
            fileCollectSet.setIs_sendok(IsFlag.Shi.getCode());
            fileCollectSet.setIs_solr(IsFlag.Fou.getCode());
            fileCollectSet.setHost_name(SystemUtil.getHostName());
            fileCollectSet.setSystem_type("windows");
            fileCollectSet.add(db);
            // 17.构造file_source表测试数据
            File_source fileSource = new File_source();
            fileSource.setFile_source_id(FileSourceId);
            fileSource.setAgent_id(DBAgentId);
            fileSource.setFcs_id(FcsId);
            fileSource.setIs_audio(IsFlag.Fou.getCode());
            fileSource.setIs_image(IsFlag.Fou.getCode());
            fileSource.setIs_office(IsFlag.Fou.getCode());
            fileSource.setIs_other(IsFlag.Fou.getCode());
            fileSource.setFile_source_path("/home/hyshf/fileSource/");
            fileSource.setIs_pdf(IsFlag.Fou.getCode());
            fileSource.setIs_text(IsFlag.Shi.getCode());
            fileSource.setIs_video(IsFlag.Fou.getCode());
            fileSource.add(db);
            // 18.构造signal_file表测试数据
            Signal_file signalFile = new Signal_file();
            signalFile.setSignal_id(SignalId);
            signalFile.setDatabase_id(DatabaseId);
            signalFile.setFile_format(FileFormat.CSV.getCode());
            signalFile.setIs_cbd(IsFlag.Shi.getCode());
            signalFile.setIs_compression(IsFlag.Fou.getCode());
            signalFile.setIs_fullindex(IsFlag.Fou.getCode());
            signalFile.setIs_into_hbase(IsFlag.Fou.getCode());
            signalFile.setIs_into_hive(IsFlag.Shi.getCode());
            signalFile.setIs_mpp(IsFlag.Fou.getCode());
            signalFile.setIs_solr_hbase(IsFlag.Fou.getCode());
            signalFile.setTable_type(IsFlag.Shi.getCode());
            signalFile.add(db);
            // 19.构造table_info表测试数据
            Table_info tableInfo = new Table_info();
            tableInfo.setTable_id(TableId);
            tableInfo.setDatabase_id(DatabaseId);
            tableInfo.setIs_md5(IsFlag.Shi.getCode());
            tableInfo.setIs_register(IsFlag.Fou.getCode());
            tableInfo.setIs_user_defined(IsFlag.Fou.getCode());
            tableInfo.setTable_ch_name("agent_info");
            tableInfo.setTable_name("agent_info");
            tableInfo.setValid_s_date(DateUtil.getSysDate());
            tableInfo.setValid_e_date("99991231");
            tableInfo.add(db);
            // 20.构造column_merge表测试数据
            Column_merge columnMerge = new Column_merge();
            columnMerge.setCol_merge_id(ColumnMergeId);
            columnMerge.setTable_id(TableId);
            columnMerge.setCol_name("agentAddress");
            columnMerge.setCol_type(CollectType.ShuJuKuCaiJi.getCode());
            columnMerge.setCol_zhname("agent地址");
            columnMerge.setOld_name("agent_ip");
            columnMerge.setValid_s_date(DateUtil.getSysDate());
            columnMerge.setValid_e_date("99991231");
            columnMerge.add(db);
            // 21.构造table_storage_info表测试数据
            Table_storage_info tableStorageInfo = new Table_storage_info();
            tableStorageInfo.setStorage_id(StorageId);
            tableStorageInfo.setStorage_time(DateUtil.getSysTime());
            tableStorageInfo.setTable_id(TableId);
            tableStorageInfo.setFile_format(FileFormat.CSV.getCode());
            tableStorageInfo.setIs_everyday(IsFlag.Fou.getCode());
            tableStorageInfo.setIs_zipper(IsFlag.Fou.getCode());
            tableStorageInfo.setStorage_type(StorageType.TiHuan.getCode());
            tableStorageInfo.add(db);
            // 22.构造table_clean表测试数据
            Table_clean tableClean = new Table_clean();
            tableClean.setTable_clean_id(TableCleanId);
            tableClean.setTable_id(TableId);
            tableClean.setFilling_length(1L);
            tableClean.setCharacter_filling("#");
            tableClean.setField("create_date");
            tableClean.setClean_type(CleanType.MaZhiZhuanHuan.getCode());
            tableClean.setFilling_type(FillingType.HouBuQi.getCode());
            tableClean.setReplace_feild("agent_ip");
            tableClean.add(db);
            // 23.构造table_column表测试数据
            Table_column tableColumn = new Table_column();
            tableColumn.setColumn_id(ColumnId);
            tableColumn.setTable_id(TableId);
            tableColumn.setColume_name("create_date");
            tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
            tableColumn.setValid_s_date(DateUtil.getSysDate());
            tableColumn.setValid_e_date("99991231");
            tableColumn.setIs_alive(IsFlag.Shi.getCode());
            tableColumn.setIs_new(IsFlag.Fou.getCode());
            tableColumn.add(db);
            // 24.构造column_clean表测试数据
            Column_clean columnClean = new Column_clean();
            columnClean.setCol_clean_id(ColumnCleanId);
            columnClean.setColumn_id(ColumnId);
            columnClean.setFilling_length(1L);
            columnClean.setCharacter_filling("#");
            columnClean.setClean_type(CleanType.MaZhiZhuanHuan.getCode());
            columnClean.setCodename("是");
            columnClean.setCodesys("1");
            columnClean.add(db);
            // 25.构造column_split表测试数据
            Column_split columnSplit = new Column_split();
            columnSplit.setCol_split_id(ColSplitId);
            columnSplit.setCol_name("agent_ip");
            columnSplit.setCol_clean_id(ColumnCleanId);
            columnSplit.setCol_type("1");
            columnSplit.setSplit_type(CharSplitType.ZhiDingFuHao.getCode());
            columnSplit.setValid_s_date(DateUtil.getSysDate());
            columnSplit.setValid_e_date("99991231");
            columnSplit.setColumn_id(ColumnId);
            columnSplit.add(db);
            // 26.初始化 Source_file_attribute 数据
            Source_file_attribute sourceFileAttribute = new Source_file_attribute();
            sourceFileAttribute.setFile_id(FileId);
            sourceFileAttribute.setIs_in_hbase(IsFlag.Fou.getCode());
            sourceFileAttribute.setSeqencing(0L);
            sourceFileAttribute.setCollect_type(CollectType.ShuJuKuCaiJi.getCode());
            sourceFileAttribute.setOriginal_name("文本文件");
            sourceFileAttribute.setOriginal_update_date(DateUtil.getSysDate());
            sourceFileAttribute.setOriginal_update_time(DateUtil.getSysTime());
            sourceFileAttribute.setTable_name("agentInfo");
            sourceFileAttribute.setHbase_name("agent_info");
            sourceFileAttribute.setMeta_info("init-dhw");
            sourceFileAttribute.setStorage_date(DateUtil.getSysDate());
            sourceFileAttribute.setStorage_time(DateUtil.getSysTime());
            sourceFileAttribute.setFile_size(BigDecimal.valueOf(1024));
            sourceFileAttribute.setFile_type(FileType.WenDang.getCode());
            sourceFileAttribute.setFile_suffix("txt");
            sourceFileAttribute.setSource_path("/home/hyshf/dhw");
            sourceFileAttribute.setFile_md5("fsfsdfsfsf");
            sourceFileAttribute.setFile_avro_path("/home/hyshf/dhw");
            sourceFileAttribute.setFile_avro_block(1024L);
            sourceFileAttribute.setIs_big_file(IsFlag.Fou.getCode());
            sourceFileAttribute.setIs_cache(IsFlag.Fou.getCode());
            sourceFileAttribute.setFolder_id(FtpFolderId);
            sourceFileAttribute.setAgent_id(DBAgentId);
            sourceFileAttribute.setSource_id(SourceId);
            sourceFileAttribute.setCollect_set_id(DatabaseId);
            sourceFileAttribute.add(db);
            // 27.初始化 data_auth 数据
            for (int i = 0; i < 4; i++) {
                Data_auth dataAuth = new Data_auth();
                dataAuth.setDa_id(DaId + i);
                switch (i) {
                    case 0:
                        dataAuth.setApply_type(ApplyType.ChaKan.getCode());
                        dataAuth.setAuth_type(AuthType.ShenQing.getCode());
                        break;
                    case 1:
                        dataAuth.setApply_type(ApplyType.XiaZai.getCode());
                        dataAuth.setAuth_type(AuthType.YunXu.getCode());
                        break;
                    case 2:
                        dataAuth.setApply_type(ApplyType.FaBu.getCode());
                        dataAuth.setAuth_type(AuthType.BuYunXu.getCode());
                        break;
                    case 3:
                        dataAuth.setApply_type(ApplyType.ChongMingMing.getCode());
                        dataAuth.setAuth_type(AuthType.YiCi.getCode());
                        break;
                }
                dataAuth.setApply_date(DateUtil.getSysDate());
                dataAuth.setApply_time(DateUtil.getSysTime());
                dataAuth.setFile_id(FileId);
                dataAuth.setUser_id(UserId);
                dataAuth.setDep_id(DepId1);
                dataAuth.setAgent_id(DBAgentId);
                dataAuth.setSource_id(SourceId);
                // 数据库设置ID或文件设置id
                dataAuth.setCollect_set_id(DatabaseId);
                dataAuth.add(db);
            }
            // 28.提交事务
            SqlOperator.commitTransaction(db);
        }
        // 29.模拟用户登录
        String responseValue = new HttpClient()
                .buildSession()
                .addData("user_id", UserId)
                .addData("password", "1")
                .post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login")
                .getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class);
        assertThat("用户登录", ar.get().isSuccess(), is(true));
    }

    @Method(desc = "测试完删除测试数据",
            logicStep = "1.测试完成后删除data_source表测试数据" +
                    "2.测试完成后删除source_relation_dep表测试数据" +
                    "3.测试完成后删除agent_info表测试数据" +
                    "4.测试完成后删除department_info表测试数据" +
                    "5.测试完成后删除sys_user表测试数据" +
                    "6.测试完成后删除agent_down_info表测试数据" +
                    "7.测试完成后删除database_set表测试数据" +
                    "8.测试完成后删除collect_job_classify表测试数据" +
                    "9.测试完成后删除ftp_collect表测试数据" +
                    "10.测试完成后删除ftp_transfered表测试数据" +
                    "11.测试完成后删除ftp_folder表测试数据" +
                    "12.测试完成后删除object_collect表测试数据" +
                    "13.测试完成后删除object_collect_task表测试数据" +
                    "14.测试完成后删除object_storage表测试数据" +
                    "15.测试完成后删除object_collect_struct表测试数据" +
                    "16.判断file_collect_set表数据是否被删除" +
                    "17.测试完成后删除file_source表测试数据" +
                    "18.测试完成后删除signal_file表测试数据" +
                    "19.测试完成后删除table_info表测试数据" +
                    "20.测试完成后删除column_merge表测试数据" +
                    "21.测试完成后删除table_storage_info表测试数据" +
                    "22.测试完成后删除table_clean表测试数据" +
                    "23.测试完成后删除table_column表测试数据" +
                    "24.测试完成后删除column_clean表测试数据" +
                    "25.测试完成后删除column_split表测试数据" +
                    "26.测试完成后删除source_file_attribute表测试数据" +
                    "27.测试完成后删除data_auth表测试数据" +
                    "28.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除" +
                    "29.提交事务")
    @After
    public void after() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 1.测试完成后删除data_source表测试数据
            SqlOperator.execute(db, "delete from data_source where source_id=?", SourceId);
            SqlOperator.execute(db, "delete from data_source where source_id=?", SourceId2);
            SqlOperator.execute(db, "delete from data_source where datasource_number=?", "init");
            // 判断data_source数据是否被删除
            long num = SqlOperator.queryNumber(db, "select count(1) from data_source where source_id=?",
                    SourceId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num, is(0L));
            long num2 = SqlOperator.queryNumber(db, "select count(1) from data_source where source_id=?",
                    SourceId2).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num2, is(0L));
            long num3 = SqlOperator.queryNumber(db, "select count(1) from data_source where " +
                    "datasource_number=?", "init").orElseThrow(() ->
                    new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", num3, is(0L));
            // 2.测试完成后删除source_relation_dep表测试数据
            SqlOperator.execute(db, "delete from source_relation_dep where dep_id=?", DepId1);
            SqlOperator.execute(db, "delete from source_relation_dep where dep_id=?", DepId2);
            // 判断source_relation_dep数据是否被删除
            long srdNum = SqlOperator.queryNumber(db, "select count(1) from source_relation_dep where " +
                    " dep_id=?", DepId1).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", srdNum, is(0L));
            long srdNum2 = SqlOperator.queryNumber(db, "select count(1) from source_relation_dep where" +
                    " dep_id=?", DepId2).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", srdNum2, is(0L));

            // 3.测试完成后删除agent_info表数据库agent测试数据
            SqlOperator.execute(db, "delete from agent_info where agent_id=?", DBAgentId);
            SqlOperator.execute(db, "delete from agent_info where agent_id=?", DFAgentId);
            SqlOperator.execute(db, "delete from agent_info where agent_id=?", UnsAgentId);
            SqlOperator.execute(db, "delete from agent_info where agent_id=?", SemiAgentId);
            SqlOperator.execute(db, "delete from agent_info where agent_id=?", FTPAgentId);
            SqlOperator.execute(db, "delete from agent_info where user_id=?", UserId);
            // 判断agent_info表数据是否被删除
            long DBNum = SqlOperator.queryNumber(db, "select count(1) from  agent_info where agent_id=?",
                    DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", DBNum, is(0L));
            long DFNum = SqlOperator.queryNumber(db, "select count(1) from agent_info where agent_id=?",
                    DFAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", DFNum, is(0L));
            long UnsNum = SqlOperator.queryNumber(db, "select count(1) from agent_info where agent_id=?",
                    UnsAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", UnsNum, is(0L));
            long SemiNum = SqlOperator.queryNumber(db, "select count(1) from agent_info where agent_id=?",
                    SemiAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", SemiNum, is(0L));
            long FTPNum = SqlOperator.queryNumber(db, "select count(1) from agent_info where agent_id=?",
                    FTPAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", FTPNum, is(0L));
            long aiNum = SqlOperator.queryNumber(db, "select count(1) from agent_info where user_id=?",
                    UserId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", aiNum, is(0L));
            // 4.测试完成后删除department_info表测试数据
            SqlOperator.execute(db, "delete from department_info where dep_id=?", DepId1);
            SqlOperator.execute(db, "delete from department_info where dep_id=?", DepId2);
            SqlOperator.execute(db, "delete from department_info where dep_name=?", "测试第1部门");
            SqlOperator.execute(db, "delete from department_info where dep_name=?", "测试第2部门");
            // 判断department_info表数据是否被删除
            long diNum = SqlOperator.queryNumber(db, "select count(1) from department_info where " +
                    " dep_id=?", DepId1).orElseThrow(() -> new RuntimeException("count fail!"));
            long diNum2 = SqlOperator.queryNumber(db, "select count(1) from department_info "
                    + " where dep_id=?", DepId2).orElseThrow(() -> new RuntimeException("count fail!"));
            long diNum3 = SqlOperator.queryNumber(db, "select count(1) from department_info "
                    + " where dep_name=?", "测试第1部门").orElseThrow(() ->
                    new RuntimeException("count fail!"));
            long diNum4 = SqlOperator.queryNumber(db, "select count(1) from department_info "
                    + " where dep_name=?", "测试第2部门").orElseThrow(() ->
                    new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", diNum, is(0L));
            assertThat("此条数据删除后，记录数应该为0", diNum2, is(0L));
            assertThat("此条数据删除后，记录数应该为0", diNum3, is(0L));
            assertThat("此条数据删除后，记录数应该为0", diNum4, is(0L));
            // 5.测试完成后删除sys_user表测试数据
            SqlOperator.execute(db, "delete from sys_user where user_id=?", UserId);
            // 判断sys_user表数据是否被删除
            long userNum =
                    SqlOperator.queryNumber(db, "select count(1) from sys_user where user_id=?",
                            UserId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", userNum, is(0L));
            // 6.测试完成后删除agent_down_info表测试数据
            SqlOperator.execute(db, "delete from agent_down_info where down_id=?", DownId);
            SqlOperator.execute(db, "delete from agent_down_info where user_id=?", UserId);
            // 判断agent_down_info表数据是否被删除
            long adiNum = SqlOperator.queryNumber(db, "select count(1) from agent_down_info where" +
                    " down_id=?", DownId).orElseThrow(() -> new RuntimeException("count fail!"));
            long adiNum2 = SqlOperator.queryNumber(db, "select count(1) from agent_down_info where" +
                    " user_id=?", DownId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", adiNum, is(0L));
            assertThat("此条数据删除后，记录数应该为0", adiNum2, is(0L));
            // 7.测试完成后删除database_set表测试数据
            SqlOperator.execute(db, "delete from database_set where database_id=?", DatabaseId);
            SqlOperator.execute(db, "delete from database_set where agent_id=?", DBAgentId);
            // 判断database_set表数据是否被删除
            long dsNum = SqlOperator.queryNumber(db, "select count(1) from database_set where " +
                    "database_id=?", DatabaseId).orElseThrow(() -> new RuntimeException("count fail!"));
            long dsNum2 = SqlOperator.queryNumber(db, "select count(1) from database_set where " +
                    "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", dsNum, is(0L));
            assertThat("此条数据删除后，记录数应该为0", dsNum2, is(0L));
            // 8.测试完成后删除collect_job_classify表测试数据
            SqlOperator.execute(db, "delete from collect_job_classify where classify_id=?", ClassifyId);
            SqlOperator.execute(db, "delete from collect_job_classify where agent_id=?", DBAgentId);
            // 判断collect_job_classify表数据是否被删除
            long cjcNum = SqlOperator.queryNumber(db, "select count(1) from collect_job_classify where "
                    + "classify_id=?", ClassifyId).orElseThrow(() -> new RuntimeException("count fail!"));
            long cjcNum2 = SqlOperator.queryNumber(db, "select count(1) from collect_job_classify where "
                    + "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", cjcNum, is(0L));
            assertThat("此条数据删除后，记录数应该为0", cjcNum2, is(0L));
            // 9.测试完成后删除ftp_collect表测试数据
            SqlOperator.execute(db, "delete from ftp_collect where ftp_id=?", FtpId);
            SqlOperator.execute(db, "delete from ftp_collect where agent_id=?", DBAgentId);
            // 判断ftp_collect表数据是否被删除
            long fcNum = SqlOperator.queryNumber(db, "select count(1) from ftp_collect where " +
                    "ftp_id=?", FtpId).orElseThrow(() -> new RuntimeException("count fail!"));
            long fcNum2 = SqlOperator.queryNumber(db, "select count(1) from ftp_collect where " +
                    "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", fcNum, is(0L));
            assertThat("此条数据删除后，记录数应该为0", fcNum2, is(0L));
            // 10.测试完成后删除ftp_transfered表测试数据
            SqlOperator.execute(db, "delete from ftp_transfered where ftp_transfered_id=?",
                    FtpTransferedId);
            SqlOperator.execute(db, "delete from ftp_transfered where ftp_id=?", FtpId);
            // 判断ftp_transfered表数据是否被删除
            long ftrNum = SqlOperator.queryNumber(db, "select count(1) from ftp_transfered where " +
                    "ftp_transfered_id=?", FtpTransferedId).orElseThrow(() ->
                    new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", ftrNum, is(0L));
            long ftrNum2 = SqlOperator.queryNumber(db, "select count(1) from ftp_transfered where " +
                    "ftp_id=?", FtpId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", ftrNum2, is(0L));
            // 11.测试完成后删除ftp_folder表测试数据
            SqlOperator.execute(db, "delete from ftp_folder where ftp_folder_id=?", FtpFolderId);
            SqlOperator.execute(db, "delete from ftp_folder where ftp_id=?", FtpId);
            // 判断ftp_folder表数据是否被删除
            long ffNum = SqlOperator.queryNumber(db, "select count(1) from ftp_folder where " +
                    "ftp_folder_id=?", FtpFolderId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", ffNum, is(0L));
            long ffNum2 = SqlOperator.queryNumber(db, "select count(1) from ftp_folder where " +
                    "ftp_id=?", FtpId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", ffNum2, is(0L));
            // 12.测试完成后删除object_collect表测试数据
            SqlOperator.execute(db, "delete from object_collect where odc_id=?", OdcId);
            SqlOperator.execute(db, "delete from object_collect where agent_id=?", DBAgentId);
            // 判断object_collect表数据是否被删除
            long obcNum = SqlOperator.queryNumber(db, "select count(1) from object_collect where " +
                    "odc_id=?", OdcId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", obcNum, is(0L));
            long obcNum2 = SqlOperator.queryNumber(db, "select count(1) from object_collect where " +
                    "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", obcNum2, is(0L));
            // 13.测试完成后删除object_collect_task表测试数据
            SqlOperator.execute(db, "delete from object_collect_task where ocs_id=?", OcsId);
            SqlOperator.execute(db, "delete from object_collect_task where odc_id=?", OdcId);
            // 判断object_collect_task表数据是否被删除
            long octNum = SqlOperator.queryNumber(db, "select count(1) from object_collect_task where "
                    + "ocs_id=?", OcsId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", octNum, is(0L));
            long octNum2 = SqlOperator.queryNumber(db, "select count(1) from object_collect_task where "
                    + "odc_id=?", OdcId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", octNum2, is(0L));
            // 14.测试完成后删除object_storage表测试数据
            SqlOperator.execute(db, "delete from object_storage where obj_stid=?", ObjStid);
            SqlOperator.execute(db, "delete from object_storage where ocs_id=?", OcsId);
            // 判断object_storage表数据是否被删除
            long obsNum = SqlOperator.queryNumber(db, "select count(1) from object_storage where " +
                    "obj_stid=?", ObjStid).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", obsNum, is(0L));
            long obsNum2 = SqlOperator.queryNumber(db, "select count(1) from object_storage where " +
                    "ocs_id=?", OcsId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", obsNum2, is(0L));
            // 15.测试完成后删除object_collect_struct表测试数据
            SqlOperator.execute(db, "delete from object_collect_struct where struct_id=?", StructId);
            SqlOperator.execute(db, "delete from object_collect_struct where ocs_id=?", OcsId);
            // 判断object_collect_struct表数据是否被删除
            long ocsNum = SqlOperator.queryNumber(db, "select count(1) from object_collect_struct where"
                    + " struct_id=?", StructId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", ocsNum, is(0L));
            long ocsNum2 = SqlOperator.queryNumber(db, "select count(1) from object_collect_struct where"
                    + " ocs_id=?", OcsId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", ocsNum2, is(0L));
            // 16.判断file_collect_set表数据是否被删除
            SqlOperator.execute(db, "delete from file_collect_set where fcs_id=?", FcsId);
            SqlOperator.execute(db, "delete from file_collect_set where agent_id=?", DBAgentId);
            long fcsNum = SqlOperator.queryNumber(db, "select count(1) from file_collect_set where " +
                    "fcs_id=?", FcsId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", fcsNum, is(0L));
            long fcsNum2 = SqlOperator.queryNumber(db, "select count(1) from file_collect_set where " +
                    "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", fcsNum2, is(0L));
            // 17.测试完成后删除file_source表测试数据
            SqlOperator.execute(db, "delete from file_source where file_source_id=?", FileSourceId);
            SqlOperator.execute(db, "delete from file_source where agent_id=?", DBAgentId);
            // 判断file_source表数据是否被删除
            long fsNum = SqlOperator.queryNumber(db, "select count(1) from file_source where " +
                    "file_source_id=?", FileSourceId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", fsNum, is(0L));
            long fsNum2 = SqlOperator.queryNumber(db, "select count(1) from file_source where " +
                    "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", fsNum2, is(0L));
            // 18.测试完成后删除signal_file表测试数据
            SqlOperator.execute(db, "delete from signal_file where signal_id=?", SignalId);
            SqlOperator.execute(db, "delete from signal_file where database_id=?", DatabaseId);
            // 判断signal_file表数据是否被删除
            long sigNum = SqlOperator.queryNumber(db, "select count(1) from signal_file where " +
                    "signal_id=?", SignalId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", sigNum, is(0L));
            long sigNum2 = SqlOperator.queryNumber(db, "select count(1) from signal_file where " +
                    "database_id=?", DatabaseId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", sigNum2, is(0L));
            // 19.测试完成后删除table_info表测试数据
            SqlOperator.execute(db, "delete from table_info where table_id=?", TableId);
            SqlOperator.execute(db, "delete from table_info where database_id=?", DatabaseId);
            // 判断table_info表数据是否被删除
            long tiNum = SqlOperator.queryNumber(db, "select count(1) from table_info where " +
                    "table_id=?", TableId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", tiNum, is(0L));
            long tiNum2 = SqlOperator.queryNumber(db, "select count(1) from table_info where " +
                    "database_id=?", DatabaseId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", tiNum2, is(0L));
            // 20.测试完成后删除column_merge表测试数据
            SqlOperator.execute(db, "delete from column_merge where col_merge_id=?", ColumnMergeId);
            SqlOperator.execute(db, "delete from column_merge where table_id=?", TableId);
            // 判断column_merge表数据是否被删除
            long cmNum = SqlOperator.queryNumber(db, "select count(1) from column_merge where " +
                    "col_merge_id=?", ColumnMergeId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", cmNum, is(0L));
            long cmNum2 = SqlOperator.queryNumber(db, "select count(1) from column_merge where " +
                    "table_id=?", TableId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", cmNum2, is(0L));
            // 21.测试完成后删除table_storage_info表测试数据
            SqlOperator.execute(db, "delete from table_storage_info where storage_id=?", StorageId);
            SqlOperator.execute(db, "delete from table_storage_info where table_id=?", TableId);
            // 判断table_storage_info表数据是否被删除
            long tsNum = SqlOperator.queryNumber(db, "select count(1) from table_storage_info where " +
                    "storage_id=?", StorageId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", tsNum, is(0L));
            long tsNum2 = SqlOperator.queryNumber(db, "select count(1) from table_storage_info where " +
                    "table_id=?", TableId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", tsNum2, is(0L));
            // 22.测试完成后删除table_clean表测试数据
            SqlOperator.execute(db, "delete from table_clean where table_clean_id=?", TableCleanId);
            SqlOperator.execute(db, "delete from table_clean where table_id=?", TableId);
            // 判断table_clean表数据是否被删除
            long tcNum = SqlOperator.queryNumber(db, "select count(1) from table_clean where " +
                    "table_clean_id=?", TableCleanId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", tcNum, is(0L));
            long tcNum2 = SqlOperator.queryNumber(db, "select count(1) from table_clean where " +
                    "table_id=?", TableId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", tcNum2, is(0L));
            // 23.测试完成后删除table_column表测试数据
            SqlOperator.execute(db, "delete from table_column where column_id=?", ColumnId);
            SqlOperator.execute(db, "delete from table_column where table_id=?", TableId);
            // 判断table_column表数据是否被删除
            long colNum = SqlOperator.queryNumber(db, "select count(1) from table_column where " +
                    "column_id=?", ColumnId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", colNum, is(0L));
            long colNum2 = SqlOperator.queryNumber(db, "select count(1) from table_column where " +
                    "table_id=?", TableId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", colNum2, is(0L));
            // 24.测试完成后删除column_clean表测试数据
            SqlOperator.execute(db, "delete from column_clean where col_clean_id=?", ColumnCleanId);
            SqlOperator.execute(db, "delete from column_clean where column_id=?", ColumnId);
            // 判断column_clean表数据是否被删除
            long cleanNum = SqlOperator.queryNumber(db, "select count(1) from column_clean where " +
                    "col_clean_id=?", ColumnCleanId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", cleanNum, is(0L));
            long cleanNum2 = SqlOperator.queryNumber(db, "select count(1) from column_clean where " +
                    "column_id=?", ColumnId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", cleanNum2, is(0L));
            // 25.测试完成后删除column_split表测试数据
            SqlOperator.execute(db, "delete from column_split where col_split_id=?", ColSplitId);
            SqlOperator.execute(db, "delete from column_split where column_id=?", ColumnId);
            // 判断column_split表数据是否被删除
            long csNum = SqlOperator.queryNumber(db, "select count(1) from column_split where " +
                    "col_split_id=?", ColSplitId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", csNum, is(0L));
            long csNum2 = SqlOperator.queryNumber(db, "select count(1) from column_split where " +
                    "column_id=?", ColumnId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", csNum2, is(0L));
            // 26.测试完成后删除source_file_attribute表测试数据
            SqlOperator.execute(db, "delete from source_file_attribute where file_id=?", FileId);
            // 判断source_file_attribute表数据是否被删除
            long sfaNum = SqlOperator.queryNumber(db, "select count(1) from source_file_attribute where " +
                    "file_id=?", FileId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("此条数据删除后，记录数应该为0", sfaNum, is(0L));
            // 27.测试完成后删除data_auth表测试数据
            for (int i = 0; i < 4; i++) {
                SqlOperator.execute(db, "delete from data_auth where da_id=?", DaId + i);
                // 判断data_auth表数据是否被删除
                long daNum = SqlOperator.queryNumber(db, "select count(1) from data_auth where " +
                        "da_id=?", DaId + i).orElseThrow(() -> new RuntimeException("count fail!"));
                assertThat("此条数据删除后，记录数应该为0", daNum, is(0L));
            }
            // 28.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除
            SqlOperator.execute(db, "delete from data_source where datasource_number=?",
                    "cs01");
            SqlOperator.execute(db, "delete from data_source where datasource_number=?",
                    "cs02");
            SqlOperator.execute(db, "delete from data_source where datasource_number=?",
                    "cs03");
            SqlOperator.execute(db, "delete from data_source where datasource_number=?",
                    "cs04");
            SqlOperator.execute(db, "delete from data_source where datasource_number=?",
                    "cs05");
            SqlOperator.execute(db, "delete from data_source where datasource_number=?",
                    "cs06");
            // 29.提交事务
            SqlOperator.commitTransaction(db);
        }

    }

    @Method(desc = "查询数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息，首页展示",
            logicStep = "1.正确的数据访问1，查询数据源，部门、agent,申请审批,业务用户和采集用户," +
                    "部门与数据源关系表信息(此方法没有错误数据情况，因为没有传参）")
    @Test
    public void searchDataSourceInfo() {
        // 不能保证数据库原表数据为空，目前不知道该如何验证数据正确性，只能判断请求成功
        // 1.正确的数据访问1，查询数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息
        String bodyString = new HttpClient()
                .post(getActionUrl("searchDataSourceInfo")).getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
    }

    @Method(desc = "数据管理列表，分页查询获取数据申请审批信息的集合",
            logicStep = "1.正确的数据访问1，数据管理列表，数据全有效" +
                    "2.正确的数据访问2，数据管理列表，当前页currPage为空" +
                    "3.正确的数据访问3，数据管理列表，每页显示条数pageSize为空" +
                    "4.错误的数据访问1，数据管理列表，当前页currPage不合理，currPage*pageSize超过总数" +
                    "(currPage为负数结果一样)" +
                    "5.错误的数据访问2，数据管理列表，每页显示条数pageSize不合法")
    @Test
    public void getDataAuditInfoForPage() {
        // 不能保证数据库原表数据为空，目前不知道该如何验证数据正确性，只能判断请求成功
        // 1.正确的数据访问1，数据管理列表，数据全有效
        String bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 5)
                .post(getActionUrl("getDataAuditInfoForPage")).getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 2.正确的数据访问2，数据管理列表，当前页currPage为空
        bodyString = new HttpClient()
                .addData("currPage", "")
                .addData("pageSize", 5)
                .post(getActionUrl("getDataAuditInfoForPage")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 3.正确的数据访问3，数据管理列表，每页显示条数pageSize为空
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", "")
                .post(getActionUrl("getDataAuditInfoForPage")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 4.错误的数据访问1，数据管理列表，当前页currPage不合理，超过总数(currPage为负数结果一样)
        bodyString = new HttpClient()
                .addData("currPage", 2)
                .addData("pageSize", 5)
                .post(getActionUrl("getDataAuditInfoForPage")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 5.错误的数据访问2，数据管理列表，每页显示条数pageSize不合法
        bodyString = new HttpClient()
                .addData("currPage", 2)
                .addData("pageSize", -1)
                .post(getActionUrl("getDataAuditInfoForPage")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
    }

    @Method(desc = "数据权限管理，分页查询数据源及部门关系信息",
            logicStep = "1.正确的数据访问1，数据权限管理，数据全有效" +
                    "2.正确的数据访问2，数据权限管理，当前页currPage为空" +
                    "3.正确的数据访问3，数据权限管理，每页显示条数pageSize为空" +
                    "4.错误的数据访问1，数据权限管理，当前页currPage不合理，currPage*pageSize超过总数" +
                    "(currPage为负数结果一样)" +
                    "5.错误的数据访问2，数据权限管理，每页显示条数pageSize不合法")
    @Test
    public void searchSourceRelationDepForPage() {
        // 不能保证数据库原表数据为空，目前不知道该如何验证数据正确性，只能判断请求成功
        // 1.正确的数据访问1，数据权限管理，数据全有效
        String bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", 5)
                .post(getActionUrl("searchSourceRelationDepForPage")).getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 2.正确的数据访问2，数据权限管理，当前页currPage为空
        bodyString = new HttpClient()
                .addData("currPage", "")
                .addData("pageSize", 5)
                .post(getActionUrl("searchSourceRelationDepForPage")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 3.正确的数据访问3，数据权限管理，每页显示条数pageSize为空
        bodyString = new HttpClient()
                .addData("currPage", 1)
                .addData("pageSize", "")
                .post(getActionUrl("searchSourceRelationDepForPage")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 4.错误的数据访问1，数据权限管理，当前页currPage不合理，超过总数(currPage为负数结果一样)
        bodyString = new HttpClient()
                .addData("currPage", 2)
                .addData("pageSize", 5)
                .post(getActionUrl("searchSourceRelationDepForPage")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 5.错误的数据访问2，数据权限管理，每页显示条数pageSize不合法
        bodyString = new HttpClient()
                .addData("currPage", 2)
                .addData("pageSize", -1)
                .post(getActionUrl("searchSourceRelationDepForPage")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
    }

    @Method(desc = "数据权限管理，更新数据源关系部门信息",
            logicStep = "1.正确的数据访问1，更新数据源关系部门信息，数据全有效" +
                    "2.错误的数据访问1，更新数据源关系部门信息，source_id不存在" +
                    "3.错误的数据访问2，更新数据源关系部门信息，dep_id对应的部门不存在")
    @Test
    public void updateAuditSourceRelationDep() {
        // 1.正确的数据访问1，更新数据源关系部门信息，数据全有效
        String bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .addData("dep_id", DepId2)
                .post(getActionUrl("updateAuditSourceRelationDep")).getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 验证source_relation_dep表数据是否更新成功
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Optional<Source_relation_dep> source_relation_dep = SqlOperator.queryOneObject(db,
                    Source_relation_dep.class, "select * from source_relation_dep where source_id=?",
                    SourceId);
            assertThat("更新source_relation_dep数据成功", source_relation_dep.get().
                    getSource_id(), is(SourceId));
            assertThat("更新source_relation_dep数据成功", source_relation_dep.get().getDep_id(),
                    is(DepId2));

        }
        // 2.错误的数据访问1，更新数据源关系部门信息，source_id不存在
        bodyString = new HttpClient()
                .addData("source_id", 111)
                .addData("dep_id", DepId2)
                .post(getActionUrl("updateAuditSourceRelationDep")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 3.错误的数据访问2，更新数据源关系部门信息，dep_id对应的部门不存在
        bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .addData("dep_id", "-111")
                .post(getActionUrl("updateAuditSourceRelationDep")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
    }

    @Method(desc = "数据申请审批并返回最新数据申请审批数据信息",
            logicStep = "1.正确的数据访问1，数据申请审批，数据全有效" +
                    "2.错误的数据访问1，数据申请审批，daId不存在" +
                    "3.错误的数据访问2，数据申请审批，authType不存在")
    @Test
    public void dataAudit() {
        // 不能保证数据库原表数据为空，目前不知道该如何验证数据正确性，只能判断请求成功
        // 1.正确的数据访问1，数据申请审批，数据全有效
        String bodyString = new HttpClient()
                .addData("da_id", DaId)
                .addData("auth_type", AuthType.YiCi.getCode())
                .post(getActionUrl("dataAudit")).getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Optional<Data_auth> dataAuth = SqlOperator.queryOneObject(db, Data_auth.class, "select * " +
                    " from " + Data_auth.TableName + " where da_id=? and user_id=?", DaId, UserId);
            assertThat(dataAuth.get().getAuth_type(), is(AuthType.YiCi.getCode()));
            assertThat(dataAuth.get().getDa_id(), is(DaId));
            assertThat(dataAuth.get().getUser_id(), is(UserId));
            assertThat(dataAuth.get().getAudit_name(), is("数据源功能测试"));
        }
        // 2.错误的数据访问1，数据申请审批，daId不存在
        bodyString = new HttpClient()
                .addData("da_id", 111)
                .addData("auth_type", AuthType.YiCi.getCode())
                .post(getActionUrl("dataAudit")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 3.错误的数据访问2，数据申请审批，authType不存在
        bodyString = new HttpClient()
                .addData("da_id", DaId)
                .addData("auth_type", 6)
                .post(getActionUrl("dataAudit")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
    }

    @Method(desc = "数据申请审批并返回最新数据申请审批数据信息",
            logicStep = "1.正确的数据访问1，数据申请审批，数据全有效" +
                    "2.错误的数据访问1，数据申请审批，daId不存在" +
                    "3.错误的数据访问2，数据申请审批，authType不存在")
    @Test
    public void deleteAudit() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 1.正确的数据访问1，数据申请审批，数据全有效
            // 删除前查询数据库，确认预期删除的数据存在
            OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
                    " data_auth where da_id = ?", DaId);
            assertThat("删除操作前，data_source表中的确存在这样一条数据", optionalLong.
                    orElse(Long.MIN_VALUE), is(1L));
            String bodyString = new HttpClient().addData("da_id", DaId)
                    .post(getActionUrl("deleteAudit")).getBodyString();
            Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
            assertThat(ar.get().isSuccess(), is(true));
            Map<String, Object> dataAuthMap = ar.get().getDataForMap();
            List<Map<String, Object>> dataAuditInfoList = (List<Map<String, Object>>)
                    dataAuthMap.get("dataAuditList");
            for (int i = 0; i < dataAuditInfoList.size(); i++) {
                assertThat(dataAuditInfoList.get(i).get("da_id").toString(), is(String.valueOf(DaId
                        + dataAuditInfoList.size() - i)));
                assertThat(dataAuditInfoList.get(i).get("original_name").toString(), is("文本文件"));
                assertThat(dataAuditInfoList.get(i).get("file_suffix").toString(), is("txt"));
                assertThat(dataAuditInfoList.get(i).get("file_type").toString(), is(notNullValue()));
                assertThat(dataAuditInfoList.get(i).get("user_name").toString(), is("数据源功能测试"));
                assertThat(dataAuditInfoList.get(i).get("apply_type").toString(), is(notNullValue()));
            }
            // 删除后查询数据库，确认预期删除的数据已删除
            optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
                    " data_auth where da_id = ?", DaId);
            assertThat("删除操作后，确认data_auth表这条数据已删除", optionalLong.
                    orElse(Long.MIN_VALUE), is(0L));
            // 2.错误的数据访问1，数据申请审批，daId不存在
            bodyString = new HttpClient().addData("da_id", 111)
                    .post(getActionUrl("deleteAudit")).getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
            assertThat(ar.get().isSuccess(), is(false));
        }
    }

    @Method(desc = "新增/更新数据源测试",
            logicStep = "1.正确的数据访问1，测试数据源新增功能,数据都有效" +
                    "2.错误的数据访问1，新增数据源信息,数据源名称不能为空" +
                    "3.错误的数据访问2，新增数据源信息,数据源名称不能为空格" +
                    "4.错误的数据访问3，新增数据源信息,数据源编号不能为空" +
                    "5.错误的数据访问4，新增数据源信息,数据源编号不能为空格" +
                    "6.错误的数据访问5，新增数据源信息,数据源编号长度不能超过四位" +
                    "7.错误的数据访问6，新增数据源信息,部门id不能为空,创建部门表department_info时通过主键自动生成" +
                    "8.错误的数据访问7，新增数据源信息,部门id不能为空格，创建部门表department_info时通过主键自动生成" +
                    "9.错误的数据访问8，新增数据源信息,部门id对应的部门不存在")
    @Test
    public void saveDataSource() {
        // 1.正确的数据访问1，新增数据源信息,数据都有效
        String bodyString = new HttpClient()
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName2")
                .addData("datasource_number", "cs01")
                .addData("dep_id", DepId1 + "," + DepId2)
                .post(getActionUrl("saveDataSource")).getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 验证新增数据是否成功
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 判断data_source表数据是否新增成功
            OptionalLong number = SqlOperator.queryNumber(db, "select count(*) from" +
                    " data_source where datasource_number=?", "cs01");
            assertThat("添加data_source数据成功", number.getAsLong(), is(1L));
            // 判断source_relation_dep表数据是否新增成功，初始化2条，新增一条，SourceId不同
            OptionalLong srdNumber = SqlOperator.queryNumber(db, "select count(*) from" +
                    " source_relation_dep where dep_id=?", DepId1);
            assertThat("添加data_source数据成功", srdNumber.getAsLong(), is(3L));
            //判断source_relation_dep表数据是否新增成功，初始化2条，新增一条，SourceId不同
            OptionalLong srdNumber2 = SqlOperator.queryNumber(db, "select count(*) from" +
                    " source_relation_dep where dep_id=?", DepId2);
            assertThat("添加data_source数据成功", srdNumber2.getAsLong(), is(3L));
        }
        // 2.错误的数据访问1，新增数据源信息,数据源名称不能为空
        bodyString = new HttpClient()
                .addData("source_remark", "测试")
                .addData("datasource_name", "")
                .addData("datasource_number", "cs02")
                .addData("dep_id", DepId1)
                .post(getActionUrl("saveDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 3.错误的数据访问2，新增数据源信息,数据源名称不能为空格
        bodyString = new HttpClient()
                .addData("source_remark", "测试")
                .addData("datasource_name", " ")
                .addData("datasource_number", "cs03")
                .addData("dep_id", DepId1)
                .post(getActionUrl("saveDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        ;
        assertThat(ar.get().isSuccess(), is(false));
        // 4.错误的数据访问3，新增数据源信息,数据源编号不能为空
        bodyString = new HttpClient()
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName02")
                .addData("datasource_number", "")
                .addData("dep_id", DepId1)
                .post(getActionUrl("saveDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 5.错误的数据访问4，新增数据源信息,数据源编号不能为空格
        bodyString = new HttpClient()
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName03")
                .addData("datasource_number", " ")
                .addData("dep_id", DepId1)
                .post(getActionUrl("saveDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 6.错误的数据访问5，新增数据源信息,数据源编号长度不能超过四位
        bodyString = new HttpClient()
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName04")
                .addData("datasource_number", "cs100")
                .addData("dep_id", DepId1)
                .post(getActionUrl("saveDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 7.错误的数据访问6，新增数据源信息,部门id不能为空,创建部门表department_info时通过主键自动生成
        bodyString = new HttpClient()
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName05")
                .addData("datasource_number", "cs05")
                .addData("dep_id", "")
                .post(getActionUrl("saveDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 8.错误的数据访问7，新增数据源信息,部门id不能为空格，创建部门表department_info时通过主键自动生成
        bodyString = new HttpClient()
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName06")
                .addData("datasource_number", "cs06")
                .addData("dep_id", " ")
                .post(getActionUrl("saveDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 9.错误的数据访问8，新增数据源信息,部门id对应的部门不存在
        bodyString = new HttpClient()
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName06")
                .addData("datasource_number", "cs06")
                .addData("dep_id", new String[]{"-100"})
                .post(getActionUrl("saveDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
    }

    @Method(desc = "更新数据源data_source,source_relation_dep表信息",
            logicStep = "1.正确的数据访问1，更新数据源信息，所有数据都有效" +
                    "2.错误的数据访问1，更新数据源信息，数据源名称不能为空" +
                    "3.错误的数据访问2，更新数据源信息，数据源名称不能为空格" +
                    "4.错误的数据访问3，更新数据源信息，数据源编号不能为空" +
                    "5.错误的数据访问4，更新数据源信息，数据源编号不能为空格" +
                    "6.错误的数据访问5，更新数据源信息，数据源编号长度不能超过四位" +
                    "7.错误的数据访问6，更新数据源信息，部门id不能为空,创建部门表department_info时通过主键自动生成" +
                    "8.错误的数据访问7，更新数据源信息，部门id不能为空格，创建部门表department_info时通过主键自动生成" +
                    "9.错误的数据访问8，更新数据源信息，部门id对应的部门不存在")
    @Test
    public void updateDataSource() {
        // 1.正确的数据访问1，更新数据源信息，所有数据都有效
        String bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .addData("source_remark", "测试update")
                .addData("datasource_name", "updateName")
                .addData("datasource_number", "up01")
                .addData("dep_id", DepId1)
                .post(getActionUrl("updateDataSource")).getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 验证更新数据是否成功
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 判断data_source表数据是否更新成功,这里指定类型返回会报错
            Optional<Data_source> data_source = SqlOperator.queryOneObject(db, Data_source.class,
                    "select * from data_source where source_id=?", SourceId);
            assertThat("更新data_source数据成功", data_source.get().getSource_id(), is(SourceId));
            assertThat("更新data_source数据成功", data_source.get().getDatasource_name(),
                    is("updateName"));
            assertThat("更新data_source数据成功", data_source.get().getDatasource_number(),
                    is("up01"));
            assertThat("更新data_source数据成功", data_source.get().getSource_remark(),
                    is("测试update"));
            // 判断source_relation_dep表数据是否更新成功
            Optional<Source_relation_dep> source_relation_dep = SqlOperator.queryOneObject(db,
                    Source_relation_dep.class, "select * from source_relation_dep where source_id=?",
                    SourceId);
            assertThat("更新source_relation_dep数据成功", source_relation_dep.get().
                    getSource_id(), is(SourceId));
            assertThat("更新source_relation_dep数据成功", source_relation_dep.get().getDep_id(),
                    is(DepId1));

        }
        // 2.错误的数据访问1，更新数据源信息，数据源名称不能为空
        bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .addData("source_remark", "测试")
                .addData("datasource_name", "")
                .addData("datasource_number", "up02")
                .addData("dep_id", "-1000000001")
                .post(getActionUrl("updateDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 3.错误的数据访问2，更新数据源信息，数据源名称不能为空格
        bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .addData("source_remark", "测试")
                .addData("datasource_name", " ")
                .addData("datasource_number", "up03")
                .addData("dep_id", DepId1)
                .post(getActionUrl("updateDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 4.错误的数据访问3，更新数据源信息，数据源编号不能为空
        bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName02")
                .addData("datasource_number", "")
                .addData("dep_id", DepId1)
                .post(getActionUrl("updateDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 5.错误的数据访问4，更新数据源信息，数据源编号不能为空格
        bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName03")
                .addData("datasource_number", " ")
                .addData("dep_id", DepId1)
                .post(getActionUrl("updateDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 6.错误的数据访问5，更新数据源信息，数据源编号长度不能超过四位
        bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName04")
                .addData("datasource_number", "up100")
                .addData("dep_id", DepId1)
                .post(getActionUrl("updateDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 7.错误的数据访问6，更新数据源信息，部门id不能为空,创建部门表department_info时通过主键自动生成
        bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName05")
                .addData("datasource_number", "up05")
                .addData("dep_id", "")
                .post(getActionUrl("updateDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 8.错误的数据访问7，更新数据源信息，部门id不能为空格，创建部门表department_info时通过主键自动生成
        bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName06")
                .addData("datasource_number", "up06")
                .addData("dep_id", " ")
                .post(getActionUrl("updateDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 9.错误的数据访问8，更新数据源信息，部门id对应的部门不存在
        bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .addData("source_remark", "测试")
                .addData("datasource_name", "dsName06")
                .addData("datasource_number", "up06")
                .addData("dep_id", "-101")
                .post(getActionUrl("updateDataSource")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));

    }

    @Method(desc = "查询数据源信息,该测试方法只会返回两种情况，能查到数据或者查不到数据",
            logicStep = "1.正确的数据访问1，查询数据源信息,正常返回数据" +
                    "2.正确的数据访问2，查询数据源信息,正常返回数据，source_id为空" +
                    "3.错误的数据访问1，查询数据源信息，查询不到数据")
    @Test
    public void searchDataSourceOrDepartment() {
        // 1.正确的数据访问1，查询数据源信息,正常返回数据
        String bodyString = new HttpClient().addData("source_id", SourceId)
                .post(getActionUrl("searchDataSourceOrDepartment")).getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 获取返回结果
        Map<String, Object> dataResource = ar.get().getDataForMap();
        // 验证查询结果的正确性
        assertThat(String.valueOf(SourceId), is(dataResource.get("source_id").toString()));
        assertThat(String.valueOf(UserId), is(dataResource.get("create_user_id").toString()));
        assertThat("dsName0", is(dataResource.get("datasource_name").toString()));
        assertThat("ds01", is(dataResource.get("datasource_number").toString()));
        assertThat("数据源详细描述0", is(dataResource.get("source_remark")));
        assertThat(DepId1 + "," + DepId2, is(dataResource.get("dep_id").toString()));
        // 验证部门表数据
        List<Department_info> departmentInfoList = (List<Department_info>) ar.get().getDataForMap()
                .get("departmentInfo");
        // 部门表初始化了两条数据
        assertThat(dataResource.isEmpty(), is(false));
        // 2.正确的数据访问2，查询数据源信息,正常返回数据，source_id为空
        bodyString = new HttpClient()
                .post(getActionUrl("searchDataSourceOrDepartment")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 验证部门表数据
        departmentInfoList = (List<Department_info>) ar.get().getDataForMap().get("departmentInfo");
        // 部门表初始化了两条数据,判断不为空，因为不确定原数据库是否初始化了数据，这里查询的是所有部门
        assertThat(dataResource.isEmpty(), is(false));

        // 3.错误的数据访问1，查询数据源信息，此数据源下没有数据
        bodyString = new HttpClient().addData("source_id", -1000000009L)
                .post(getActionUrl("searchDataSourceOrDepartment")).getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        dataResource = ar.get().getDataForMap();
        departmentInfoList = (List<Department_info>) ar.get().getDataForMap().get("departmentInfo");
        assertThat(dataResource.get("source_id"), nullValue());
        assertThat(dataResource.get("create_user_id"), nullValue());
        assertThat(dataResource.get("datasource_name"), nullValue());
        assertThat(dataResource.get("datasource_number"), nullValue());
        assertThat(dataResource.get("source_remark"), nullValue());
        assertThat(dataResource.get("dep_id"), nullValue());
        assertThat(departmentInfoList.isEmpty(), is(false));
    }

    @Method(desc = "查询数据采集用户信息，此方法只有一种可能",
            logicStep = "1.正确的数据访问1，查询数据采集用户信息,没有参数传递，只有一种可能")
    @Test
    public void searchDataCollectUser() {
        // 1.正确的数据访问1，查询数据源信息,正常返回数据
        String bodyString = new HttpClient()
                .post(getActionUrl("searchDataCollectUser")).getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        List<Sys_user> sysUserList = ar.get().getDataForEntityList(Sys_user.class);
        // 只需要判断不为空就行
        assertThat(sysUserList.isEmpty(), is(false));
    }

    @Method(desc = "删除数据源信息，该测试方法只有三种情况",
            logicStep = " 1.正确的数据访问1，删除数据源信息，数据为有效数据" +
                    "2.错误的数据访问1，删除数据源信息，数据源下有agent，不能删除" +
                    "3.错误的数据访问2，删除数据源信息，此数据源下没有数据")
    @Test
    public void deleteDataSource() {
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            // 1.正确的数据访问1，删除数据源信息，数据为有效数据
            // 删除前查询数据库，确认预期删除的数据存在
            OptionalLong optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
                    " data_source where source_id = ?", SourceId2);
            assertThat("删除操作前，data_source表中的确存在这样一条数据", optionalLong.
                    orElse(Long.MIN_VALUE), is(1L));
            String bodyString = new HttpClient()
                    .addData("source_id", SourceId2)
                    .post(getActionUrl("deleteDataSource"))
                    .getBodyString();
            Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
            assertThat(ar.get().isSuccess(), is(true));
            // 删除后查询数据库，确认预期数据已删除
            optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
                    " data_source where source_id = ?", SourceId2);
            assertThat("删除操作后，确认这条数据已删除", optionalLong.
                    orElse(Long.MIN_VALUE), is(0L));

            // 2.错误的数据访问1，删除数据源信息，数据源下有agent，不能删除
            bodyString = new HttpClient()
                    .addData("source_id", SourceId)
                    .post(getActionUrl("deleteDataSource"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
            assertThat(ar.get().isSuccess(), is(false));

            // 3.错误的数据访问2，删除数据源信息，此数据源下没有数据
            bodyString = new HttpClient()
                    .addData("source_id", -1000000009L)
                    .post(getActionUrl("deleteDataSource"))
                    .getBodyString();
            ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
            assertThat(ar.get().isSuccess(), is(false));
        }
    }

    @Method(desc = "下载文件（数据源下载功能使用，下载数据源给数据源导入提供上传文件），此方法只有这两种情况",
            logicStep = "1.错误的数据访问1，数据源下载,source_id存在" +
                    "2.错误的数据访问1，数据源下载,source_id不存在")
    @Test
    public void downloadFile() {
        // 1.错误的数据访问1，数据源下载,source_id存在
        String bodyString = new HttpClient()
                .addData("source_id", SourceId)
                .post(getActionUrl("downloadFile"))
                .getBodyString();
        assertThat(bodyString, is(notNullValue()));
        // 2.错误的数据访问1，数据源下载,source_id不存在
        bodyString = new HttpClient()
                .addData("source_id", -100L)
                .post(getActionUrl("downloadFile"))
                .getBodyString();
        assertThat(bodyString, is(""));
    }

    @Method(desc = "导入数据源",
            logicStep = "1.正确的数据访问1，导入数据源，数据全有效" +
                    "2.错误的数据访问1，导入数据源，agentIp不合法" +
                    "3.错误的数据访问2，导入数据源，agentPort不合法" +
                    "4.错误的数据访问3，导入数据源，userCollectId为空" +
                    "5.错误的数据访问4，导入数据源，file为空")
    @Test
    public void uploadFile() {
        // 1.正确的数据访问1，导入数据源，数据全有效
        String bodyString = new HttpClient()
                .addData("agent_ip", "10.71.4.51")
                .addData("agent_port", "4321")
                .addData("user_id", UserId)
                .addData("file", "C:\\Users\\mine\\Desktop\\1000028765.hrds")
                .post(getActionUrl("uploadFile"))
                .getBodyString();
        Optional<ActionResult> ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(true));
        // 验证导入数据是否成功，查询的数据会比初始化的数据多一倍，因为导入时主键会发生变化，新增主键
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            long num = SqlOperator.queryNumber(db, "select count(1) from data_source where" +
                    " datasource_name=?", "dsName0").orElseThrow(() ->
                    new RuntimeException("count fail!"));
            assertThat("导入data_source表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(1) from agent_info where source_id=?",
                    SourceId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入agent_info表5条数据", num, is(10L));
            num = SqlOperator.queryNumber(db, "select count(2) from agent_down_info where agent_id=?",
                    DFAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入agent_down_info表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from database_set where " +
                    "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入database_set表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from collect_job_classify where " +
                    "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入collect_job_classify表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from ftp_collect where " +
                    "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入ftp_collect表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from ftp_transfered where " +
                    "ftp_id=?", FtpId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入ftp_transfered表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from ftp_folder where " +
                    "ftp_id=?", FtpId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入ftp_folder表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from object_collect where " +
                    "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入object_collect表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from object_collect_task where " +
                    "odc_id=?", OdcId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入object_collect_task表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from object_storage where " +
                    "ocs_id=?", OcsId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入object_storage表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from object_collect_struct where " +
                    "ocs_id=?", OcsId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入object_collect_struct表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from file_collect_set where " +
                    "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入file_collect_set表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from file_source where " +
                    "agent_id=?", DBAgentId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入file_source表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from signal_file where " +
                    "database_id=?", DatabaseId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入signal_file表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from table_info where " +
                    "database_id=?", DatabaseId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入table_info表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from column_merge where " +
                    "table_id=?", TableId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入column_merge表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from table_storage_info where " +
                    "table_id=?", TableId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入table_storage_info表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from table_clean where " +
                    "table_id=?", TableId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入table_clean表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from table_column where " +
                    "table_id=?", TableId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入table_column表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from column_clean where " +
                    "column_id=?", ColumnId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入column_clean表一条数据", num, is(2L));
            num = SqlOperator.queryNumber(db, "select count(2) from column_split where " +
                    "column_id=?", ColumnId).orElseThrow(() -> new RuntimeException("count fail!"));
            assertThat("导入column_split表一条数据", num, is(2L));
        }
        // 2.错误的数据访问1，导入数据源，agentIp不合法
        bodyString = new HttpClient()
                .addData("agent_ip", "10.71.4.666")
                .addData("agent_port", "4321")
                .addData("user_id", UserId)
                .addData("file", "C:\\Users\\mine\\Desktop\\1000028765.hrds")
                .post(getActionUrl("uploadFile"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 3.错误的数据访问2，导入数据源，agentPort不合法
        bodyString = new HttpClient()
                .addData("agent_ip", "10.71.4.51")
                .addData("agent_port", "666666")
                .addData("user_id", UserId)
                .addData("file", "C:\\Users\\mine\\Desktop\\1000028765.hrds")
                .post(getActionUrl("uploadFile"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 4.错误的数据访问3，导入数据源，userCollectId为空
        bodyString = new HttpClient()
                .addData("agent_ip", "10.71.4.51")
                .addData("agent_port", "4321")
                .addData("user_id", "")
                .addData("file", "C:\\Users\\mine\\Desktop\\1000028765.hrds")
                .post(getActionUrl("uploadFile"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
        // 5.错误的数据访问4，导入数据源，file为空
        bodyString = new HttpClient()
                .addData("agent_ip", "10.71.4.51")
                .addData("agent_port", "4321")
                .addData("user_id", UserId)
                .addData("file", "")
                .post(getActionUrl("uploadFile"))
                .getBodyString();
        ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class);
        assertThat(ar.get().isSuccess(), is(false));
    }

}

