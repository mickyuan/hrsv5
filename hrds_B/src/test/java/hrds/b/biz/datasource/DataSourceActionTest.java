package hrds.b.biz.datasource;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.FileUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.SystemUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netclient.http.SubmitMediaType;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.*;
import hrds.commons.entity.*;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.*;

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
	private static final long UserId = 6666L;
	// 测试数据采集用户
	private static final long CollectUserId = 1234L;
	// 初始化创建用户ID
	private static final long CreateId = 1000L;
	// 测试数据源 SourceId
	private static final long SourceId = 10000001L;
	// 测试数据源 SourceId,新建数据源，下面没有agent
	private static final long SourceId2 = 10000003L;
	// 测试数据库 agent_id
	private static final long DBAgentId = 20000011L;
	// 测试数据文件 agent_id
	private static final long DFAgentId = 20000012L;
	// 测试非结构化 agent_id
	private static final long UnsAgentId = 20000013L;
	// 测试半结构化 agent_id
	private static final long SemiAgentId = 20000014L;
	// 测试FTP agent_id
	private static final long FTPAgentId = 20000015L;
	// 测试部门ID dep_id,测试第一部门
	private static final long DepId1 = 30000011L;
	// 测试部门ID dep_id 测试第二部门
	private static final long DepId2 = 30000012L;
	// 测试agent_down_info down_id
	private static final long DownId = 30000001L;
	// 测试 分类ID，classify_id
	private static final long ClassifyId = 40000001L;
	// 测试 数据库设置ID，DatabaseId
	private static final long DatabaseId = 50000001L;
	// 测试 ftp_collect表ID，ftp_id
	private static final long FtpId = 50000011L;
	// 测试 ftp_collect表ID，ftp_transfered_id
	private static final String FtpTransferedId = UUID.randomUUID().toString()
			.replaceAll("-", "");
	// 测试 ftp_folder表ID，ftp_folder_id
	private static final long FtpFolderId = 50000091L;
	// 测试 object_collect表ID，odc_id
	private static final long OdcId = 50000101L;
	// 测试 object_collect_task表ID，ocs_id
	private static final long OcsId = 50000102L;
	// 测试 object_storage表ID，obj_stid
	private static final long ObjStid = 50000103L;
	// 测试 object_collect_struct表ID，struct_id
	private static final long StructId = 50000104L;
	// 测试 file_collect_set表ID，fcs_id
	private static final long FcsId = 50000105L;
	// 测试 file_source表ID，file_source_id
	private static final long FileSourceId = 50000106L;
	// 测试 signal_file表ID，signal_id
	private static final long SignalId = 50000107L;
	// 测试 table_info表ID，table_id
	private static final long TableId = 50000108L;
	// 测试 column_merge表ID，col_merge_id
	private static final long ColumnMergeId = 50000109L;
	// 测试 table_storage_info表ID，storage_id
	private static final long StorageId = 50000110L;
	// 测试 table_clean表ID，table_clean_id
	private static final long TableCleanId = 50000111L;
	// 测试 table_column表ID，column_id
	private static final long ColumnId = 50000112L;
	// 测试 column_clean表ID，col_clean_id
	private static final long ColumnCleanId = 50000113L;
	// 测试 column_split表ID，col_split_id
	private static final long ColSplitId = 50000114L;
	// 测试 source_file_attribute表ID，file_id
	private static final String FileId = "200000000000";
	// 测试 data_auth表ID，da_id
	private static final long DaId = 50000115L;
	private static final String SysDate = DateUtil.getSysDate();
	private static final String SysTime = DateUtil.getSysTime();

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
			"25.sys_user表，有2条数据，user_id为UserId,CollectUserId" +
			"26.source_file_attribute表，有一条数据,file_id为FileId" +
			"27.data_auth表，有一条数据，da_id为DaId")
	@BeforeClass
	public static void before() {
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
				data_source.setCreate_date(SysDate);
				data_source.setCreate_time(SysTime);
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
				department_info.setCreate_date(SysDate);
				department_info.setCreate_time(SysTime);
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
				agent_info.setCreate_date(SysDate);
				agent_info.setCreate_time(SysTime);
				agent_info.setUser_id(UserId);
				// 初始化不同类型的agent
				if (i == 1) {
					// 数据库 agent
					agent_info.setAgent_ip("10.71.4.51");
					agent_info.setAgent_port("4561");
					agent_info.setAgent_id(DBAgentId);
					agent_info.setAgent_type(AgentType.ShuJuKu.getCode());
					agent_info.setAgent_name("sjkAgent");
				} else if (i == 2) {
					// 数据文件 Agent
					agent_info.setAgent_ip("10.71.4.52");
					agent_info.setAgent_port("4562");
					agent_info.setAgent_id(DFAgentId);
					agent_info.setAgent_type(AgentType.DBWenJian.getCode());
					agent_info.setAgent_name("DFAgent");

				} else if (i == 3) {
					// 非结构化 Agent
					agent_info.setAgent_ip("10.71.4.53");
					agent_info.setAgent_port("4563");
					agent_info.setAgent_id(UnsAgentId);
					agent_info.setAgent_type(AgentType.WenJianXiTong.getCode());
					agent_info.setAgent_name("UnsAgent");
				} else if (i == 4) {
					// 半结构化 Agent
					agent_info.setAgent_ip("10.71.4.54");
					agent_info.setAgent_port("4564");
					agent_info.setAgent_id(SemiAgentId);
					agent_info.setAgent_type(AgentType.DuiXiang.getCode());
					agent_info.setAgent_name("SemiAgent");
				} else {
					// FTP Agent
					agent_info.setAgent_ip("10.71.4.55");
					agent_info.setAgent_port("4565");
					agent_info.setAgent_id(FTPAgentId);
					agent_info.setAgent_type(AgentType.FTP.getCode());
					agent_info.setAgent_name("FTPAgent");
				}
				// 初始化agent不同的连接状态
				if (i < 2) {
					// 已连接
					agent_info.setAgent_status(AgentStatus.YiLianJie.getCode());
				} else if (i < 4) {
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
			for (int i = 0; i < 2; i++) {
				if (i == 0) {
					sysUser.setUser_id(UserId);
					sysUser.setDep_id(DepId1);
					sysUser.setUser_name("数据源功能测试");
				} else {
					sysUser.setUser_id(CollectUserId);
					sysUser.setDep_id(DepId2);
					sysUser.setUser_name("采集用户功能测试");
				}
				sysUser.setCreate_id(CreateId);
				sysUser.setCreate_date(SysDate);
				sysUser.setCreate_time(SysTime);
				sysUser.setRole_id("1001");
				sysUser.setUser_password("1");
				sysUser.setUser_type(UserType.CaiJiYongHu.getCode());
				sysUser.setUseris_admin("1");
				sysUser.setUsertype_group("02,03,04,08");
				sysUser.setUser_state(IsFlag.Shi.getCode());
				sysUser.add(db);
			}

			// 6.构造database_set表测试数据
			Database_set databaseSet = new Database_set();
			databaseSet.setDatabase_id(DatabaseId);
			databaseSet.setAgent_id(DBAgentId);
			databaseSet.setClassify_id(ClassifyId);
//            databaseSet.setDatabase_code(DataBaseCode.UTF_8.getCode());
			databaseSet.setDatabase_drive("org.postgresql.Driver");
			databaseSet.setDatabase_ip("10.71.4.51");
			databaseSet.setDatabase_name("数据库采集测试");
			databaseSet.setDatabase_number("dhw001");
			databaseSet.setDatabase_pad("hrsdxg");
			databaseSet.setUser_name("hrsdxg");
			databaseSet.setDatabase_port("34567");
//            databaseSet.setDbfile_format(FileFormat.CSV.getCode());
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
			ftpCollect.setStart_date(SysDate);
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
			ftpCollect.setRealtime_interval(1L);
			ftpCollect.add(db);
			// 10.构造ftp_transfered表测试数据
			Ftp_transfered ftpTransfered = new Ftp_transfered();
			ftpTransfered.setFtp_transfered_id(FtpTransferedId);
			ftpTransfered.setFtp_id(FtpId);
			ftpTransfered.setFtp_date(SysDate);
			ftpTransfered.setFtp_time(SysTime);
			ftpTransfered.setTransfered_name("ftp传输测试");
			ftpTransfered.setFile_path("/home/hyshf/ftp");
			ftpTransfered.add(db);
			// 11.构造object_collect表测试数据
			Object_collect objectCollect = new Object_collect();
			objectCollect.setOdc_id(OdcId);
			objectCollect.setAgent_id(DBAgentId);
			objectCollect.setDatabase_code(DataBaseCode.UTF_8.getCode());
			objectCollect.setE_date("99991231");
			objectCollect.setFile_path("/home/hyshf/objCollect");
			objectCollect.setHost_name("10.71.4.52");
			objectCollect.setIs_sendok(IsFlag.Shi.getCode());
			objectCollect.setIs_dictionary(IsFlag.Shi.getCode());
			objectCollect.setData_date(DateUtil.getSysDate());
			objectCollect.setLocal_time(DateUtil.parseStr2DateWith8Char(SysDate) + " "
					+ DateUtil.parseStr2TimeWith6Char(SysTime));
			objectCollect.setObj_collect_name("对象采集测试");
			objectCollect.setObj_number("dxcj01");
			objectCollect.setRun_way(ExecuteWay.AnShiQiDong.getCode());
			objectCollect.setS_date(SysDate);
			objectCollect.setServer_date(DateUtil.parseStr2DateWith8Char(SysDate) + " "
					+ DateUtil.parseStr2TimeWith6Char(SysTime));
			objectCollect.setSystem_name("windows");
			objectCollect.setObject_collect_type(ObjectCollectType.DuiXiangCaiJi.getCode());
			objectCollect.setIs_dictionary(IsFlag.Fou.getCode());
			objectCollect.setData_date(DateUtil.getSysDate());
			objectCollect.setFile_suffix("json");
			objectCollect.add(db);
			// 12.构造object_collect_task表测试数据
			Object_collect_task objectCollectTask = new Object_collect_task();
			objectCollectTask.setOcs_id(OcsId);
			objectCollectTask.setAgent_id(DBAgentId);
			objectCollectTask.setEn_name("dxcjrwmc");
			objectCollectTask.setZh_name("对象采集任务名称");
			objectCollectTask.setCollect_data_type(CollectDataType.JSON.getCode());
			objectCollectTask.setDatabase_code(DataBaseCode.UTF_8.getCode());
			objectCollectTask.setOdc_id(OdcId);
			objectCollectTask.setUpdatetype(UpdateType.DirectUpdate.getCode());
			objectCollectTask.add(db);
			// 13.构造object_storage表测试数据
			Object_storage objectStorage = new Object_storage();
			objectStorage.setObj_stid(ObjStid);
			objectStorage.setOcs_id(OcsId);
			objectStorage.setIs_hbase(IsFlag.Shi.getCode());
			objectStorage.setIs_hdfs(IsFlag.Fou.getCode());
			objectStorage.setIs_solr(IsFlag.Fou.getCode());
			objectStorage.add(db);
			// 14.构造object_collect_struct表测试数据
			Object_collect_struct objectCollectStruct = new Object_collect_struct();
			objectCollectStruct.setStruct_id(StructId);
			objectCollectStruct.setOcs_id(OcsId);
			objectCollectStruct.setColumn_name("对象采集结构");
			objectCollectStruct.setColumn_type(ObjectDataType.ZiFuChuan.getCode());
			objectCollectStruct.setData_desc("测试");
			objectCollectStruct.setIs_hbase(IsFlag.Fou.getCode());
			objectCollectStruct.setIs_solr(IsFlag.Fou.getCode());
			objectCollectStruct.setIs_rowkey(IsFlag.Fou.getCode());
			objectCollectStruct.setIs_operate(IsFlag.Fou.getCode());
			objectCollectStruct.setColumnposition("columns");
			objectCollectStruct.setIs_operate(IsFlag.Fou.getCode());
			objectCollectStruct.setCol_seq(1L);
			objectCollectStruct.setIs_key(IsFlag.Fou.getCode());
			objectCollectStruct.add(db);
			// 15.构造file_collect_set表测试数据
			File_collect_set fileCollectSet = new File_collect_set();
			fileCollectSet.setFcs_id(FcsId);
			fileCollectSet.setAgent_id(DBAgentId);
			fileCollectSet.setFcs_name("文件系统设置");
			fileCollectSet.setIs_sendok(IsFlag.Shi.getCode());
			fileCollectSet.setIs_solr(IsFlag.Fou.getCode());
			fileCollectSet.setHost_name(SystemUtil.getHostName());
			fileCollectSet.setSystem_type("windows");
			fileCollectSet.add(db);
			// 16.构造file_source表测试数据
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
			fileSource.setIs_compress(IsFlag.Shi.getCode());
			fileSource.add(db);
			// 17.构造signal_file表测试数据
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
			// 18.构造table_info表测试数据
			Table_info tableInfo = new Table_info();
			tableInfo.setTable_id(TableId);
			tableInfo.setDatabase_id(DatabaseId);
			tableInfo.setIs_md5(IsFlag.Shi.getCode());
			tableInfo.setIs_register(IsFlag.Fou.getCode());
			tableInfo.setIs_user_defined(IsFlag.Fou.getCode());
			tableInfo.setTable_ch_name("agent_info");
			tableInfo.setTable_name("agent_info");
			tableInfo.setValid_s_date(SysDate);
			tableInfo.setValid_e_date("99991231");
			tableInfo.setIs_parallel(IsFlag.Shi.getCode());
			tableInfo.setRec_num_date(DateUtil.getSysDate());
			tableInfo.setIs_customize_sql(IsFlag.Fou.getCode());
			tableInfo.add(db);
			// 19.构造column_merge表测试数据
			Column_merge columnMerge = new Column_merge();
			columnMerge.setCol_merge_id(ColumnMergeId);
			columnMerge.setTable_id(TableId);
			columnMerge.setCol_name("agentAddress");
			columnMerge.setCol_type("1");
			columnMerge.setCol_zhname("agent地址");
			columnMerge.setOld_name("agent_ip");
			columnMerge.setValid_s_date(SysDate);
			columnMerge.setValid_e_date("99991231");
			columnMerge.add(db);
			// 20.构造table_storage_info表测试数据
			Table_storage_info tableStorageInfo = new Table_storage_info();
			tableStorageInfo.setStorage_id(StorageId);
			tableStorageInfo.setStorage_time(1L);
			tableStorageInfo.setTable_id(TableId);
			tableStorageInfo.setFile_format(FileFormat.CSV.getCode());
			tableStorageInfo.setIs_zipper(IsFlag.Fou.getCode());
			tableStorageInfo.setStorage_type(StorageType.TiHuan.getCode());
			tableStorageInfo.add(db);
			// 21.构造table_clean表测试数据
			Table_clean tableClean = new Table_clean();
			tableClean.setTable_clean_id(TableCleanId);
			tableClean.setTable_id(TableId);
			tableClean.setFilling_length(1L);
			tableClean.setCharacter_filling("#");
			tableClean.setField("create_date");
			tableClean.setClean_type(CleanType.ZiFuBuQi.getCode());
			tableClean.setFilling_type(FillingType.HouBuQi.getCode());
			tableClean.setReplace_feild("agent_ip");
			tableClean.add(db);
			// 22.构造table_column表测试数据
			Table_column tableColumn = new Table_column();
			tableColumn.setColumn_id(ColumnId);
			tableColumn.setTable_id(TableId);
			tableColumn.setColumn_name("create_date");
			tableColumn.setColumn_ch_name("create_date");
			tableColumn.setIs_primary_key(IsFlag.Fou.getCode());
			tableColumn.setValid_s_date(SysDate);
			tableColumn.setValid_e_date("99991231");
			tableColumn.setIs_alive(IsFlag.Shi.getCode());
			tableColumn.setIs_new(IsFlag.Fou.getCode());
			tableColumn.add(db);
			// 23.构造column_clean表测试数据
			Column_clean columnClean = new Column_clean();
			columnClean.setCol_clean_id(ColumnCleanId);
			columnClean.setColumn_id(ColumnId);
			columnClean.setFilling_length(1L);
			columnClean.setCharacter_filling("#");
			columnClean.setClean_type(CleanType.ZiFuBuQi.getCode());
			columnClean.setCodename("是");
			columnClean.setCodesys("1");
			columnClean.add(db);
			// 24.构造column_split表测试数据
			Column_split columnSplit = new Column_split();
			columnSplit.setCol_split_id(ColSplitId);
			columnSplit.setCol_name("agent_ip");
			columnSplit.setCol_clean_id(ColumnCleanId);
			columnSplit.setCol_type("1");
			columnSplit.setSplit_type(CharSplitType.ZhiDingFuHao.getCode());
			columnSplit.setValid_s_date(SysDate);
			columnSplit.setValid_e_date("99991231");
			columnSplit.setColumn_id(ColumnId);
			columnSplit.add(db);
			// 25.初始化 Source_file_attribute 数据
			Source_file_attribute sourceFileAttribute = new Source_file_attribute();
			for (int i = 0; i < 4; i++) {
				switch (i) {
					case 0:
						sourceFileAttribute.setFile_id(FileId);
						sourceFileAttribute.setOriginal_name("文本文件");
						sourceFileAttribute.setFile_suffix("txt");
						sourceFileAttribute.setFile_type(FileType.WenBenFile.getCode());
						break;
					case 1:
						sourceFileAttribute.setFile_id(FileId + i);
						sourceFileAttribute.setOriginal_name("文档文件");
						sourceFileAttribute.setFile_type(FileType.WenDang.getCode());
						sourceFileAttribute.setFile_suffix("doc");
						break;
					case 2:
						sourceFileAttribute.setFile_id(FileId + i);
						sourceFileAttribute.setOriginal_name("图片");
						sourceFileAttribute.setFile_type(FileType.TuPian.getCode());
						sourceFileAttribute.setFile_suffix("jpg");
						break;
					case 3:
						sourceFileAttribute.setFile_id(FileId + i);
						sourceFileAttribute.setOriginal_name("PDF文件");
						sourceFileAttribute.setFile_type(FileType.PDFFile.getCode());
						sourceFileAttribute.setFile_suffix("pdf");
						break;
				}
			}
			sourceFileAttribute.setIs_in_hbase(IsFlag.Fou.getCode());
			sourceFileAttribute.setSeqencing(0L);
			sourceFileAttribute.setCollect_type(AgentType.ShuJuKu.getCode());
			sourceFileAttribute.setOriginal_update_date(SysDate);
			sourceFileAttribute.setOriginal_update_time(SysTime);
			sourceFileAttribute.setTable_name("agentInfo");
			sourceFileAttribute.setHbase_name("agent_info");
			sourceFileAttribute.setMeta_info("init-dhw");
			sourceFileAttribute.setStorage_date(SysDate);
			sourceFileAttribute.setStorage_time(SysTime);
			sourceFileAttribute.setFile_size(1024L);
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
			// 26.初始化 data_auth 数据
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
				dataAuth.setApply_date(SysDate);
				dataAuth.setApply_time(SysTime);
				dataAuth.setFile_id(FileId);
				dataAuth.setUser_id(UserId);
				dataAuth.setDep_id(DepId1);
				dataAuth.setAgent_id(DBAgentId);
				dataAuth.setSource_id(SourceId);
				// 数据库设置ID或文件设置id
				dataAuth.setCollect_set_id(DatabaseId);
				dataAuth.add(db);
			}
			// 27.提交事务
			SqlOperator.commitTransaction(db);
		}
		// 28.模拟用户登录
		String responseValue = new HttpClient()
				.buildSession()
				.addData("user_id", UserId)
				.addData("password", "1")
				.post("http://127.0.0.1:8888/A/action/hrds/a/biz/login/login")
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(responseValue, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat("用户登录", ar.isSuccess(), is(true));
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
	@AfterClass
	public static void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 1.测试完成后删除data_source表测试数据
			SqlOperator.execute(db, "delete from data_source where source_id=?", SourceId);
			SqlOperator.execute(db, "delete from data_source where source_id=?", SourceId2);
			SqlOperator.execute(db, "delete from data_source where datasource_number=?", "init");
			SqlOperator.execute(db, "delete from data_source where datasource_number=?", "sjy1");
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
			long num4 = SqlOperator.queryNumber(db, "select count(1) from data_source where " +
					"datasource_number=?", "sjy1").orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", num4, is(0L));
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
			SqlOperator.execute(db, "delete from agent_info where user_id=?", CollectUserId);
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
			long userNum = SqlOperator.queryNumber(db, "select count(1) from sys_user where user_id=?",
					UserId).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", userNum, is(0L));
			SqlOperator.execute(db, "delete from sys_user where user_id=?", CollectUserId);
			// 判断sys_user表数据是否被删除
			long userNum2 = SqlOperator.queryNumber(db, "select count(1) from sys_user where user_id=?",
					CollectUserId).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", userNum2, is(0L));
			// 6.测试完成后删除agent_down_info表测试数据
			SqlOperator.execute(db, "delete from agent_down_info where down_id=?", DownId);
			SqlOperator.execute(db, "delete from agent_down_info where user_id=?", UserId);
			SqlOperator.execute(db, "delete from agent_down_info where user_id=?", CollectUserId);
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
			SqlOperator.execute(db, "delete from " + Ftp_transfered.TableName + " where ftp_transfered_id=?",
					FtpTransferedId);
			SqlOperator.execute(db, "delete from " + Ftp_transfered.TableName + " where ftp_id=?", FtpId);
			// 判断ftp_transfered表数据是否被删除
			long ftrNum = SqlOperator.queryNumber(db, "select count(1) from ftp_transfered where " +
					"ftp_transfered_id=?", FtpTransferedId).orElseThrow(() ->
					new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", ftrNum, is(0L));
			long ftrNum2 = SqlOperator.queryNumber(db, "select count(1) from ftp_transfered where " +
					"ftp_id=?", FtpId).orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("此条数据删除后，记录数应该为0", ftrNum2, is(0L));
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
			for (int i = 0; i < 4; i++) {
				SqlOperator.execute(db, "delete from source_file_attribute where file_id=?",
						FileId + i);
				// 判断source_file_attribute表数据是否被删除
				long sfaNum = SqlOperator.queryNumber(db, "select count(1) from source_file_attribute " +
						" where file_id=?", FileId + i).orElseThrow(() ->
						new RuntimeException("count fail!"));
				assertThat("此条数据删除后，记录数应该为0", sfaNum, is(0L));
			}
			// 27.测试完成后删除data_auth表测试数据
			for (int i = 0; i < 4; i++) {
				SqlOperator.execute(db, "delete from data_auth where da_id=?", DaId + i);
				// 判断data_auth表数据是否被删除
				long daNum = SqlOperator.queryNumber(db, "select count(1) from data_auth where da_id=?",
						DaId + i).orElseThrow(() -> new RuntimeException("count fail!"));
				assertThat("此条数据删除后，记录数应该为0", daNum, is(0L));
			}
			// 28.单独删除新增数据，因为新增数据主键是自动生成的，所以要通过其他方式删除
			SqlOperator.execute(db, "delete from data_source where create_user_id=?",
					UserId);
			SqlOperator.execute(db, "delete from database_set where database_number=?",
					"dhw01");
			// 29.提交事务
			SqlOperator.commitTransaction(db);
		}

	}

	@Method(desc = "查询数据源，部门、agent,申请审批,业务用户和采集用户,部门与数据源关系表信息，首页展示",
			logicStep = "1.正确的数据访问1，查询数据源以及数据源对应agent个数信息，该方法只有一种情况，因为没有传参")
	@Test
	public void searchDataSourceAndAgentCount() {
		// 1.正确的数据访问1，查询数据源以及数据源对应agent个数信息，该方法只有一种情况，因为没有传参
		String bodyString = new HttpClient()
				.post(getActionUrl("searchDataSourceAndAgentCount")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		Result dataForResult = ar.getDataForResult();
		if (dataForResult.isEmpty()) {
			// TODO 不能保证数据库原表数据为空，只能校验自己造的数据
			for (int i = 0; i < dataForResult.getRowCount(); i++) {
				if (dataForResult.getLong(i, "source_id") == SourceId) {
					assertThat("datasource_name", is("dsName0"));
				} else if (dataForResult.getLong(i, "source_id") == SourceId2) {
					assertThat("datasource_name", is("dsName1"));
				}
			}
		}
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
		// 1.正确的数据访问1，数据管理列表，数据全有效
		String bodyString = new HttpClient()
//                .addData("currPage", 1)
//                .addData("pageSize", 5)
				.post(getActionUrl("getDataAuditInfoForPage")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		Result dataForResult = ar.getDataForResult();
		// 验证申请审批数据的正确性
		// TODO 不能保证数据库原表数据为空，只能测试自己造的数据，总记录数目前不知如何验证
		checkDataAuditData(dataForResult);
		// 2.正确的数据访问2，数据管理列表，当前页currPage为空
		bodyString = new HttpClient()
				.addData("currPage", "")
				.addData("pageSize", 5)
				.post(getActionUrl("getDataAuditInfoForPage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		dataForResult = ar.getDataForResult();
		// 验证申请审批数据的正确性
		checkDataAuditData(dataForResult);
		// 3.正确的数据访问3，数据管理列表，每页显示条数pageSize为空
		bodyString = new HttpClient()
				.addData("currPage", 1)
				.addData("pageSize", "")
				.post(getActionUrl("getDataAuditInfoForPage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		dataForResult = ar.getDataForResult();
		// 验证申请审批数据的正确性
		checkDataAuditData(dataForResult);
		// 4.错误的数据访问1，数据管理列表，当前页currPage不合理，超过总数(currPage为负数结果一样)
		bodyString = new HttpClient()
				.addData("currPage", 2)
				.addData("pageSize", 5)
				.post(getActionUrl("getDataAuditInfoForPage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		dataForResult = ar.getDataForResult();
		assertThat(dataForResult.getRowCount(), is(0));
		// 5.错误的数据访问2，数据管理列表，每页显示条数pageSize不合法
		bodyString = new HttpClient()
				.addData("currPage", 2)
				.addData("pageSize", -1)
				.post(getActionUrl("getDataAuditInfoForPage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "验证申请审批数据的正确性",
			logicStep = "1.判断结果集是否为空，不为空验证申请审批数据的正确性，" +
					"该方法不需要测试")
	@Param(name = "dataForResult", desc = "申请审批数据结果集", range = "取值范围")
	private void checkDataAuditData(Result dataForResult) {
		// 1.判断结果集是否为空，不为空验证申请审批数据的正确性
		if (!dataForResult.isEmpty()) {
			// TODO 不能保证数据库原表数据为空，只能测试自己造的数据,总记录数目前不知如何验证
			for (int i = 0; i < dataForResult.getRowCount(); i++) {
				if (dataForResult.getLong(i, "da_id") == DaId) {
					assertThat("文本文件", is(dataForResult.getString(i, "original_name")));
					assertThat("txt", is(dataForResult.getString(i, "file_suffix")));
					assertThat("数据源功能测试", is(dataForResult.getString(i, "user_name")));
					assertThat(ApplyType.ChaKan.getCode(), is(dataForResult.getString(i,
							"apply_type")));
					assertThat(AuthType.ShenQing.getCode(), is(dataForResult.getString(i,
							"auth_type")));
				} else if (dataForResult.getLong(i, "da_id") == DaId + 1) {
					assertThat("文档文件", is(dataForResult.getString(i, "original_name")));
					assertThat("doc", is(dataForResult.getString(i, "file_suffix")));
					assertThat(ApplyType.XiaZai.getCode(), is(dataForResult.getString(i,
							"apply_type")));
					assertThat(AuthType.YunXu.getCode(), is(dataForResult.getString(i,
							"auth_type")));
				} else if (dataForResult.getLong(i, "da_id") == DaId + 2) {
					assertThat("图片", is(dataForResult.getString(i, "original_name")));
					assertThat("jpg", is(dataForResult.getString(i, "file_suffix")));
					assertThat("数据源功能测试", is(dataForResult.getString(i, "user_name")));
					assertThat(ApplyType.FaBu.getCode(), is(dataForResult.getString(i,
							"apply_type")));
					assertThat(AuthType.BuYunXu.getCode(), is(dataForResult.getString(i,
							"auth_type")));
				} else if (dataForResult.getLong(i, "da_id") == DaId + 3) {
					assertThat("PDF文件", is(dataForResult.getString(i, "original_name")));
					assertThat("pdf", is(dataForResult.getString(i, "file_suffix")));
					assertThat("数据源功能测试", is(dataForResult.getString(i, "user_name")));
					assertThat(ApplyType.ChongMingMing.getCode(), is(dataForResult.getString(i,
							"apply_type")));
					assertThat(AuthType.YiCi.getCode(), is(dataForResult.getString(i,
							"auth_type")));
				}
			}
		}
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
		// TODO 不能保证数据库原表数据为空，只能判断自己造的数据,分页总记录数暂时不知如何验证
		// 1.正确的数据访问1，数据权限管理，数据全有效
		String bodyString = new HttpClient()
				.addData("currPage", 1)
				.addData("pageSize", 5)
				.post(getActionUrl("searchSourceRelationDepForPage")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证数据源与部门关系数据是否正确
		Result dataForResult = ar.getDataForResult();
		checkSourceRelationDepData(dataForResult);
		// 2.正确的数据访问2，数据权限管理，当前页currPage为空
		bodyString = new HttpClient()
				.addData("currPage", "")
				.addData("pageSize", 5)
				.post(getActionUrl("searchSourceRelationDepForPage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		dataForResult = ar.getDataForResult();
		checkSourceRelationDepData(dataForResult);
		// 3.正确的数据访问3，数据权限管理，每页显示条数pageSize为空
		bodyString = new HttpClient()
				.addData("currPage", 1)
				.addData("pageSize", "")
				.post(getActionUrl("searchSourceRelationDepForPage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		dataForResult = ar.getDataForResult();
		checkSourceRelationDepData(dataForResult);
		// 4.错误的数据访问1，数据权限管理，当前页currPage不合理，超过总数(currPage为负数结果一样)
		bodyString = new HttpClient()
				.addData("currPage", 2)
				.addData("pageSize", 5)
				.post(getActionUrl("searchSourceRelationDepForPage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getDataForResult().getRowCount(), is(0));
		// 5.错误的数据访问2，数据权限管理，每页显示条数pageSize不合法
		bodyString = new HttpClient()
				.addData("currPage", 2)
				.addData("pageSize", -1)
				.post(getActionUrl("searchSourceRelationDepForPage")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "验证数据源与部门关系数据是否正确", logicStep = "1.验证数据源与部门关系数据是否正确，该方法不用测试")
	@Param(name = "dataForResult", desc = "数据源与部门关系信息集合", range = "无限制")
	private void checkSourceRelationDepData(Result dataForResult) {
		// 1.验证数据源与部门关系数据是否正确,该方法不用测试
		if (dataForResult.isEmpty()) {
			for (int i = 0; i < dataForResult.getRowCount(); i++) {
				if (dataForResult.getLong(i, "source_id") == SourceId) {
					assertThat(dataForResult.getLong(i, "datasource_name"), is("dsName0"));
					assertThat(dataForResult.getLong(i, "dep_name"), is("测试第1部门,测试第2部门"));
				} else if (dataForResult.getLong(i, "source_id") == SourceId2) {
					assertThat(dataForResult.getLong(i, "datasource_name"), is("dsName1"));
					assertThat(dataForResult.getLong(i, "dep_name"), is("测试第1部门,测试第2部门"));
				}
			}
		}
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
				.addData("dep_id", new long[]{DepId1, DepId2})
				.post(getActionUrl("updateAuditSourceRelationDep")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证source_relation_dep表数据是否更新成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Source_relation_dep sourceRelationDep = SqlOperator.queryOneObject(db,
					Source_relation_dep.class, "select * from source_relation_dep where source_id=?" +
							" and dep_id=?", SourceId, DepId1).orElseThrow(() ->
					new BusinessException("sql查询错误或者映射错误"));
			assertThat("更新source_relation_dep数据成功", sourceRelationDep.getSource_id(), is(SourceId));
			assertThat("更新source_relation_dep数据成功", sourceRelationDep.getDep_id(), is(DepId1));
			sourceRelationDep = SqlOperator.queryOneObject(db,
					Source_relation_dep.class, "select * from source_relation_dep where source_id=?" +
							" and dep_id=?", SourceId, DepId2).orElseThrow(() ->
					new BusinessException("sql查询错误或者映射错误"));
			assertThat("更新source_relation_dep数据成功", sourceRelationDep.getSource_id(), is(SourceId));
			assertThat("更新source_relation_dep数据成功", sourceRelationDep.getDep_id(), is(DepId2));

		}
		// 2.错误的数据访问1，更新数据源关系部门信息，source_id不存在
		bodyString = new HttpClient()
				.addData("source_id", 111)
				.addData("dep_id", new long[]{DepId1})
				.post(getActionUrl("updateAuditSourceRelationDep")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，更新数据源关系部门信息，dep_id对应的部门不存在
		bodyString = new HttpClient()
				.addData("source_id", SourceId)
				.addData("dep_id", new String[]{"-111"})
				.post(getActionUrl("updateAuditSourceRelationDep")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "数据申请审批并返回最新数据申请审批数据信息",
			logicStep = "1.正确的数据访问1，数据申请审批，数据全有效" +
					"2.错误的数据访问1，数据申请审批，daId不存在" +
					"3.错误的数据访问2，数据申请审批，authType不存在")
	@Test
	public void dataAudit() {
		// 1.正确的数据访问1，数据申请审批，数据全有效
		String bodyString = new HttpClient()
				.addData("da_id", DaId + 2)
				.addData("auth_type", AuthType.YiCi.getCode())
				.post(getActionUrl("dataAudit")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Optional<Data_auth> dataAuth = SqlOperator.queryOneObject(db, Data_auth.class,
					"select * from " + Data_auth.TableName + " where da_id=? and user_id=?",
					DaId + 2, UserId);
			assertThat(dataAuth.get().getAuth_type(), is(AuthType.YiCi.getCode()));
			assertThat(dataAuth.get().getDa_id(), is(DaId + 2));
			assertThat(dataAuth.get().getUser_id(), is(UserId));
			assertThat(dataAuth.get().getAudit_name(), is("数据源功能测试"));
		}
		// 2.错误的数据访问1，数据申请审批，daId不存在
		bodyString = new HttpClient()
				.addData("da_id", 111)
				.addData("auth_type", AuthType.YiCi.getCode())
				.post(getActionUrl("dataAudit")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，数据申请审批，authType不存在
		bodyString = new HttpClient()
				.addData("da_id", DaId)
				.addData("auth_type", 6)
				.post(getActionUrl("dataAudit")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
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
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期删除的数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					" data_auth where da_id = ?", DaId);
			assertThat("删除操作后，确认data_auth表这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));
			// 2.错误的数据访问1，数据申请审批，daId不存在
			bodyString = new HttpClient().addData("da_id", 111)
					.post(getActionUrl("deleteAudit")).getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
		}
	}

	@Method(desc = "查询部门信息", logicStep = "1.正确的数据访问1，查询部门信息,该方法只有一种可能，因为没有参数")
	@Test
	public void searchDepartmentInfo() {
		// 1.正确的数据访问1，查询部门信息
		String bodyString = new HttpClient().post(getActionUrl("searchDepartmentInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		List<Department_info> departmentInfoList = ar.getDataForEntityList(Department_info.class);
		// TODO 不能确定原表是否有数据，目前只能测试自己造的数据
		for (Department_info departmentInfo : departmentInfoList) {
			if (departmentInfo.getDep_id() == DepId1) {
				assertThat("测试第1部门", is(departmentInfo.getDep_name()));
			}
			if (departmentInfo.getDep_id() == DepId2) {
				assertThat("测试第2部门", is(departmentInfo.getDep_name()));
			}
		}
	}

	@Method(desc = "新数据源测试",
			logicStep = "1.正确的数据访问1，测试数据源新增功能,数据都有效" +
					"2.错误的数据访问1，新增数据源信息,数据源名称不能为空" +
					"3.错误的数据访问2，新增数据源信息,数据源名称不能为空格" +
					"4.错误的数据访问3，新增数据源信息,数据源编号不能为空" +
					"5.错误的数据访问4，新增数据源信息,数据源编号不能为空格" +
					"6.错误的数据访问5，新增数据源信息,数据源编号不以字符开头" +
					"7.错误的数据访问6，新增数据源信息,部门id不能为空,创建部门表department_info时通过主键自动生成" +
					"8.错误的数据访问7，新增数据源信息,部门id不能为空格，创建部门表department_info时通过主键自动生成" +
					"9.错误的数据访问8，新增数据源信息,部门id对应的部门不存在")
	@Test
	public void saveDataSource() {
		// 1.正确的数据访问1，新增数据源信息,数据都有效
		long[] depIds = {DepId1, DepId2};
		String bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName2")
				.addData("datasource_number", "cs01")
				.addData("dep_id", depIds)
				.post(getActionUrl("saveDataSource")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证新增数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			// 判断data_source表数据是否新增成功
			Optional<Data_source> dataSource = SqlOperator.queryOneObject(db, Data_source.class,
					"select * from " + Data_source.TableName + " where datasource_number=?", "cs01");
			assertThat(dataSource.get().getDatasource_name(), is("dsName2"));
			assertThat(dataSource.get().getDatasource_number(), is("cs01"));
			assertThat(dataSource.get().getSource_remark(), is("测试"));
			// 判断source_relation_dep表数据是否新增成功
			for (long dep_id : depIds) {
				List<Source_relation_dep> relationDepList = SqlOperator.queryList(db,
						Source_relation_dep.class, "select * from " + Source_relation_dep.TableName
								+ " where dep_id=?", dep_id);
				for (Source_relation_dep srd : relationDepList) {
					if (srd.getSource_id() != SourceId && srd.getSource_id() != SourceId2) {
						assertThat(dataSource.get().getSource_id(), is(srd.getSource_id()));
						assertThat(srd.getSource_id(), is(dataSource.get().getSource_id()));
					}
				}
			}
		}
		// 2.错误的数据访问1，新增数据源信息,数据源名称不能为空
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "")
				.addData("datasource_number", "cs02")
				.addData("dep_id", depIds)
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，新增数据源信息,数据源名称不能为空格
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", " ")
				.addData("datasource_number", "cs03")
				.addData("dep_id", depIds)
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		;
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，新增数据源信息,数据源编号不能为空
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName02")
				.addData("datasource_number", "")
				.addData("dep_id", depIds)
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，新增数据源信息,数据源编号不能为空格
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName03")
				.addData("datasource_number", " ")
				.addData("dep_id", depIds)
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 6.错误的数据访问5，新增数据源信息,数据源编号不以字符开头
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName04")
				.addData("datasource_number", "100cs")
				.addData("dep_id", depIds)
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 7.错误的数据访问6，新增数据源信息,部门id不能为空,创建部门表department_info时通过主键自动生成
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName05")
				.addData("datasource_number", "cs05")
				.addData("dep_id", "")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 8.错误的数据访问7，新增数据源信息,部门id不能为空格，创建部门表department_info时通过主键自动生成
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName06")
				.addData("datasource_number", "cs06")
				.addData("dep_id", " ")
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 9.错误的数据访问8，新增数据源信息,部门id对应的部门不存在
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName06")
				.addData("datasource_number", "cs06")
				.addData("dep_id", new String[]{"-100"})
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 9.错误的数据访问8，新增数据源信息,数据源编号不是以字母开头的四位字母与数字组合
		bodyString = new HttpClient()
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName07")
				.addData("datasource_number", "1234")
				.addData("dep_id", depIds)
				.post(getActionUrl("saveDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "根据数据源编号查询数据源信息,该测试方法只会返回两种情况，有数据，没有数据",
			logicStep = "1.正确的数据访问1，查询数据源信息,正常返回数据" +
					"2.错误的数据访问1，查询数据源信息，此数据源下没有数据")
	@Test
	public void searchDataSourceById() {
		// 1.正确的数据访问1，查询数据源信息,正常返回数据
		String bodyString = new HttpClient().addData("source_id", SourceId)
				.post(getActionUrl("searchDataSourceById")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		// 获取返回结果
		Map<String, Object> dataResource = ar.getDataForMap();
		// 验证查询结果的正确性
		assertThat(String.valueOf(SourceId), is(dataResource.get("source_id").toString()));
		assertThat(String.valueOf(UserId), is(dataResource.get("create_user_id").toString()));
		assertThat("dsName0", is(dataResource.get("datasource_name").toString()));
		assertThat("ds01", is(dataResource.get("datasource_number").toString()));
		assertThat("数据源详细描述0", is(dataResource.get("source_remark")));
		List<Map<String, Object>> depNameAndId = (List<Map<String, Object>>) dataResource.get("depNameAndId");
		for (Map<String, Object> map : depNameAndId) {
			String dep_id = map.get("dep_id").toString();
			if (dep_id.equals(String.valueOf(DepId1))) {
				assertThat("测试第1部门", is(map.get("dep_name")));
			} else if (dep_id.equals(String.valueOf(DepId2))) {
				assertThat("测试第2部门", is(map.get("dep_name")));
			}
		}
		// 2.错误的数据访问1，查询数据源信息，此数据源下没有数据
		bodyString = new HttpClient().addData("source_id", 1000009L)
				.post(getActionUrl("searchDataSourceById")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
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
		long[] depIds = {DepId1};
		// 1.正确的数据访问1，更新数据源信息，所有数据都有效
		String bodyString = new HttpClient()
				.addData("source_id", SourceId)
				.addData("source_remark", "测试update")
				.addData("datasource_name", "updateName")
				.addData("datasource_number", "up01")
				.addData("dep_id", depIds)
				.post(getActionUrl("updateDataSource")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
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
					Source_relation_dep.class, "select * from " + Source_relation_dep.TableName +
							" where source_id=? and dep_id=?", SourceId, DepId1);
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
				.addData("dep_id", depIds)
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，更新数据源信息，数据源名称不能为空格
		bodyString = new HttpClient()
				.addData("source_id", SourceId)
				.addData("source_remark", "测试")
				.addData("datasource_name", " ")
				.addData("datasource_number", "up03")
				.addData("dep_id", depIds)
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，更新数据源信息，数据源编号不能为空
		bodyString = new HttpClient()
				.addData("source_id", SourceId)
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName02")
				.addData("datasource_number", "")
				.addData("dep_id", depIds)
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，更新数据源信息，数据源编号不能为空格
		bodyString = new HttpClient()
				.addData("source_id", SourceId)
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName03")
				.addData("datasource_number", " ")
				.addData("dep_id", depIds)
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 6.错误的数据访问5，更新数据源信息，数据源编号不以字符开头
		bodyString = new HttpClient()
				.addData("source_id", SourceId)
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName04")
				.addData("datasource_number", "100up")
				.addData("dep_id", depIds)
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 7.错误的数据访问6，更新数据源信息，部门id不能为空,创建部门表department_info时通过主键自动生成
		bodyString = new HttpClient()
				.addData("source_id", SourceId)
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName05")
				.addData("datasource_number", "up05")
				.addData("dep_id", "")
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 8.错误的数据访问7，更新数据源信息，部门id不能为空格，创建部门表department_info时通过主键自动生成
		bodyString = new HttpClient()
				.addData("source_id", SourceId)
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName06")
				.addData("datasource_number", "up06")
				.addData("dep_id", " ")
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 9.错误的数据访问8，更新数据源信息，部门id对应的部门不存在
		bodyString = new HttpClient()
				.addData("source_id", SourceId)
				.addData("source_remark", "测试")
				.addData("datasource_name", "dsName06")
				.addData("datasource_number", "up06")
				.addData("dep_id", new String[]{"101"})
				.post(getActionUrl("updateDataSource")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));

	}

	@Method(desc = "查询数据采集用户信息，此方法只有一种可能",
			logicStep = "1.正确的数据访问1，查询数据采集用户信息,没有参数传递，只有一种可能")
	@Test
	public void searchDataCollectUser() {
		// 1.正确的数据访问1，查询数据源信息,正常返回数据
		String bodyString = new HttpClient()
				.post(getActionUrl("searchDataCollectUser")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		List<Sys_user> sysUserList = ar.getDataForEntityList(Sys_user.class);
		if (sysUserList.isEmpty()) {
			for (Sys_user sys_user : sysUserList) {
				// TODO 不能确定原表数据为空，所以只能测试自己造的数据
				if (sys_user.getUser_id() == UserId) {
					assertThat(sys_user.getUser_name(), is("数据源功能测试"));
				}
			}
		}
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
					Data_source.TableName + " where source_id = ?", SourceId2);
			assertThat("删除操作前，data_source表中的确存在这样一条数据", optionalLong.
					orElse(Long.MIN_VALUE), is(1L));
			String bodyString = new HttpClient()
					.addData("source_id", SourceId2)
					.post(getActionUrl("deleteDataSource"))
					.getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(true));
			// 删除后查询数据库，确认预期数据已删除
			optionalLong = SqlOperator.queryNumber(db, "select count(1) from " +
					Data_source.TableName + " where source_id = ?", SourceId2);
			assertThat("删除操作后，确认这条数据已删除", optionalLong.
					orElse(Long.MIN_VALUE), is(0L));

			// 2.错误的数据访问1，删除数据源信息，数据源下有agent，不能删除
			bodyString = new HttpClient()
					.addData("source_id", SourceId)
					.post(getActionUrl("deleteDataSource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));

			// 3.错误的数据访问2，删除数据源信息，此数据源下没有数据
			bodyString = new HttpClient()
					.addData("source_id", 100009L)
					.post(getActionUrl("deleteDataSource"))
					.getBodyString();
			ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
					.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
			assertThat(ar.isSuccess(), is(false));
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
				.addData("source_id", 100L)
				.post(getActionUrl("downloadFile"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "导入数据源",
			logicStep = "1.正确的数据访问1，导入数据源，数据全有效" +
					"2.错误的数据访问1，导入数据源，agentIp不合法" +
					"3.错误的数据访问2，导入数据源，agentPort不合法" +
					"4.错误的数据访问3，导入数据源，userCollectId为空" +
					"5.错误的数据访问4，导入数据源，file为空")
	@Test
	public void uploadFile() {
		String filePath = FileUtil.getFile("src/test/java/hrds/b/biz/datasource/" +
				"uploadFile/upload.hrds").getAbsolutePath();
		// 1.正确的数据访问1，导入数据源，数据全有效
		String bodyString = new HttpClient()
				.reset(SubmitMediaType.MULTIPART)
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4321")
				.addData("user_id", CollectUserId)
				.addFile("file", new File(filePath))
				.post(getActionUrl("uploadFile"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(true));
		// 验证导入数据是否成功
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Data_source dataSource = SqlOperator.queryOneObject(db, Data_source.class,
					"select * from " + Data_source.TableName + " where datasource_name=? and source_id<>?",
					"dsName0", SourceId).orElseThrow(() -> new BusinessException("sql执行错误!"));
			// 导入会替换原来的主键，这里只判断可以确认的数据，例如时间日期取得系统时间，不好确认，主键也无法确定自动生成
			assertThat(dataSource.getDatasource_name(), is("dsName0"));
			assertThat(dataSource.getDatasource_number(), is("ds01_dhw"));
			assertThat(dataSource.getSource_remark(), is("数据源详细描述0"));
			assertThat(dataSource.getDatasource_remark(), is("备注0"));
			assertThat(dataSource.getCreate_user_id(), is(UserId));
			List<Agent_info> agentInfoList = SqlOperator.queryList(db, Agent_info.class, "select * from "
					+ Agent_info.TableName + " where source_id=?", dataSource.getSource_id());
			long dfAgentId = 0;
			long dbAgentId = 0;
			for (Agent_info agentInfo : agentInfoList) {
				if (agentInfo.getAgent_name().equals("sjkAgent")) {
					dbAgentId = agentInfo.getAgent_id();
					assertThat(agentInfo.getAgent_type(), is(AgentType.ShuJuKu.getCode()));
					assertThat(agentInfo.getAgent_status(), is(AgentStatus.YiLianJie.getCode()));
				} else if (agentInfo.getAgent_name().equals("DFAgent")) {
					dfAgentId = agentInfo.getAgent_id();
					assertThat(agentInfo.getAgent_type(), is(AgentType.DBWenJian.getCode()));
					assertThat(agentInfo.getAgent_status(), is(AgentStatus.WeiLianJie.getCode()));
				} else if (agentInfo.getAgent_name().equals("UnsAgent")) {
					assertThat(agentInfo.getAgent_type(), is(AgentType.WenJianXiTong.getCode()));
					assertThat(agentInfo.getAgent_status(), is(AgentStatus.WeiLianJie.getCode()));
				} else if (agentInfo.getAgent_name().equals("SemiAgent")) {
					assertThat(agentInfo.getAgent_type(), is(AgentType.DuiXiang.getCode()));
					assertThat(agentInfo.getAgent_status(), is(AgentStatus.ZhengZaiYunXing.getCode()));
				} else if (agentInfo.getAgent_name().equals("FTPAgent")) {
					assertThat(agentInfo.getAgent_type(), is(AgentType.FTP.getCode()));
					assertThat(agentInfo.getAgent_status(), is(AgentStatus.YiLianJie.getCode()));
				}
				assertThat(agentInfo.getAgent_ip(), is("10.71.4.51"));
				assertThat(agentInfo.getAgent_port(), is("4321"));
				assertThat(agentInfo.getUser_id(), is(CollectUserId));
				assertThat(agentInfo.getSource_id(), is(dataSource.getSource_id()));
			}
			Agent_down_info agentDownInfo = SqlOperator.queryOneObject(db, Agent_down_info.class,
					"select * from " + Agent_down_info.TableName + " where agent_id=?", dfAgentId)
					.orElseThrow(() -> new RuntimeException("sql查询错误"));
			assertThat(agentDownInfo.getAgent_ip(), is("10.71.4.51"));
			assertThat(agentDownInfo.getAgent_port(), is("4321"));
			assertThat(agentDownInfo.getAgent_name(), is("DFAgent"));
			assertThat(agentDownInfo.getAgent_pattern(), is("/hrds/agent/trans/biz/AgentServer/" +
					"getSystemFileInfo"));
			assertThat(agentDownInfo.getAgent_context(), is("/agent"));
			assertThat(agentDownInfo.getLog_dir(), is("/home/hyshf/sjkAgent_34567/log/"));
			assertThat(agentDownInfo.getPasswd(), is("hyshf"));
			assertThat(agentDownInfo.getUser_name(), is("hyshf"));
			assertThat(agentDownInfo.getAi_desc(), is("agent部署"));
			assertThat(agentDownInfo.getAgent_type(), is(AgentType.DBWenJian.getCode()));
			assertThat(agentDownInfo.getDeploy(), is(IsFlag.Fou.getCode()));
			assertThat(agentDownInfo.getSave_dir(), is("/home/hyshf/sjkAgent_34567/"));
			assertThat(agentDownInfo.getUser_id(), is(CollectUserId));
			assertThat(agentDownInfo.getRemark(), is("备注"));
			Collect_job_classify jobClassify = SqlOperator.queryOneObject(db, Collect_job_classify.class,
					"select * from " + Collect_job_classify.TableName + " where agent_id=?", dbAgentId)
					.orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(jobClassify.getClassify_name(), is("数据库采集测试"));
			assertThat(jobClassify.getClassify_num(), is("sjkCs01"));
			assertThat(jobClassify.getRemark(), is("测试"));
			assertThat(jobClassify.getUser_id(), is(UserId));
			Database_set databaseSet = SqlOperator.queryOneObject(db, Database_set.class,
					"select * from " + Database_set.TableName + " where agent_id=?", dbAgentId)
					.orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(databaseSet.getDatabase_pad(), is("hrsdxg"));
			assertThat(databaseSet.getUser_name(), is("hrsdxg"));
			assertThat(databaseSet.getDatabase_type(), is(DatabaseType.Postgresql.getCode()));
			assertThat(databaseSet.getDatabase_drive(), is("org.postgresql.Driver"));
			assertThat(databaseSet.getDatabase_name(), is("数据库采集测试"));
			assertThat(databaseSet.getJdbc_url(), is("jdbc:postgresql://10.71.4.52:31001/hrsdxgtest"));
			assertThat(databaseSet.getDatabase_ip(), is("10.71.4.51"));
			assertThat(databaseSet.getDatabase_port(), is("34567"));
			assertThat(databaseSet.getDb_agent(), is(IsFlag.Shi.getCode()));
			assertThat(databaseSet.getTask_name(), is("数据库测试"));
			assertThat(databaseSet.getIs_sendok(), is(IsFlag.Fou.getCode()));
			assertThat(databaseSet.getDatabase_number(), is("dhw01"));
			assertThat(databaseSet.getClassify_id(), is(jobClassify.getClassify_id()));
			Map<String, Object> ftpCollect = SqlOperator.queryOneObject(db,
					"select * from " + Ftp_collect.TableName + " where agent_id=?", dbAgentId);
			assertThat(ftpCollect.get("ftp_username").toString(), is("hrsdxg"));
			assertThat(ftpCollect.get("ftp_number").toString(), is("ftpcj01"));
			assertThat(ftpCollect.get("realtime_interval").toString(), is(IsFlag.Shi.getCode()));
			assertThat(ftpCollect.get("ftp_ip").toString(), is("10.71.4.52"));
			assertThat(ftpCollect.get("ftp_password").toString(), is("hrsdxg"));
			assertThat(ftpCollect.get("ftp_port").toString(), is("34567"));
			assertThat(ftpCollect.get("ftp_rule_path").toString(), is(FtpRule.AnShiJian.getCode()));
			assertThat(ftpCollect.get("local_path").toString(), is("/home/hyshf/ftp_34567/"));
			assertThat(ftpCollect.get("run_way").toString(), is(ExecuteWay.AnShiQiDong.getCode()));
			assertThat(ftpCollect.get("end_date").toString(), is("99991231"));
			Ftp_transfered ftpTransfered = SqlOperator.queryOneObject(db, Ftp_transfered.class,
					"select * from " + Ftp_transfered.TableName + " where ftp_id=?",
					ftpCollect.get("ftp_id")).orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(ftpTransfered.getTransfered_name(), is("ftp传输测试"));
			Object_collect objectCollect = SqlOperator.queryOneObject(db, Object_collect.class,
					"select * from " + Object_collect.TableName + " where agent_id=?", dbAgentId)
					.orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(objectCollect.getFile_path(), is("/home/hyshf/objCollect"));
			assertThat(objectCollect.getObject_collect_type(), is(ObjectCollectType.DuiXiangCaiJi.getCode()));
			assertThat(objectCollect.getObj_collect_name(), is("对象采集测试"));
			assertThat(objectCollect.getSystem_name(), is("windows"));
			assertThat(objectCollect.getIs_sendok(), is(IsFlag.Shi.getCode()));
			assertThat(objectCollect.getRun_way(), is(ExecuteWay.AnShiQiDong.getCode()));
			assertThat(objectCollect.getDatabase_code(), is(DataBaseCode.UTF_8.getCode()));
			assertThat(objectCollect.getObj_number(), is("dxcj01"));
			assertThat(objectCollect.getHost_name(), is("10.71.4.52"));
			Object_collect_task collectTask = SqlOperator.queryOneObject(db, Object_collect_task.class,
					"select * from " + Object_collect_task.TableName + " where odc_id=?",
					objectCollect.getOdc_id()).orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(collectTask.getEn_name(), is("dxcjrwmc"));
			assertThat(collectTask.getAgent_id(), is(dbAgentId));
			assertThat(collectTask.getCollect_data_type(), is(CollectDataType.JSON.getCode()));
			assertThat(collectTask.getDatabase_code(), is(DataBaseCode.UTF_8.getCode()));
			assertThat(collectTask.getZh_name(), is("对象采集任务名称"));
			Object_storage storage = SqlOperator.queryOneObject(db, Object_storage.class,
					"select * from " + Object_storage.TableName + " where ocs_id=?",
					collectTask.getOcs_id()).orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(storage.getIs_hbase(), is(IsFlag.Shi.getCode()));
			assertThat(storage.getIs_hdfs(), is(IsFlag.Fou.getCode()));
			Object_collect_struct collectStruct = SqlOperator.queryOneObject(db, Object_collect_struct.class,
					"select * from " + Object_collect_struct.TableName + " where ocs_id=?",
					collectTask.getOcs_id()).orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(collectStruct.getColumn_name(), is("对象采集结构"));
			assertThat(collectStruct.getData_desc(), is("测试"));
			assertThat(collectStruct.getColumn_type(), is(ObjectDataType.ZiFuChuan.getCode()));
			File_collect_set collectSet = SqlOperator.queryOneObject(db, File_collect_set.class,
					"select * from " + File_collect_set.TableName + " where agent_id=?", dbAgentId)
					.orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(collectSet.getFcs_name(), is("文件系统设置"));
			assertThat(collectSet.getHost_name(), is("DESKTOP-DE7CNE1"));
			assertThat(collectSet.getIs_sendok(), is(IsFlag.Shi.getCode()));
			assertThat(collectSet.getIs_solr(), is(IsFlag.Fou.getCode()));
			assertThat(collectSet.getSystem_type(), is("windows"));
			File_source fileSource = SqlOperator.queryOneObject(db, File_source.class,
					"select * from file_source where agent_id=?", dbAgentId).orElseThrow(() ->
					new RuntimeException("sql执行错误!"));
			assertThat(fileSource.getFcs_id(), is(collectSet.getFcs_id()));
			assertThat(fileSource.getIs_audio(), is(IsFlag.Fou.getCode()));
			assertThat(fileSource.getIs_image(), is(IsFlag.Fou.getCode()));
			assertThat(fileSource.getIs_office(), is(IsFlag.Fou.getCode()));
			assertThat(fileSource.getIs_other(), is(IsFlag.Fou.getCode()));
			assertThat(fileSource.getIs_pdf(), is(IsFlag.Fou.getCode()));
			assertThat(fileSource.getIs_text(), is(IsFlag.Shi.getCode()));
			assertThat(fileSource.getFile_source_path(), is("/home/hyshf/fileSource/"));
			Signal_file signalFile = SqlOperator.queryOneObject(db, Signal_file.class,
					"select * from " + Signal_file.TableName + " where database_id=?",
					databaseSet.getDatabase_id()).orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(signalFile.getFile_format(), is(FileFormat.CSV.getCode()));
			assertThat(signalFile.getIs_cbd(), is(IsFlag.Shi.getCode()));
			assertThat(signalFile.getIs_compression(), is(IsFlag.Fou.getCode()));
			assertThat(signalFile.getIs_fullindex(), is(IsFlag.Fou.getCode()));
			assertThat(signalFile.getIs_into_hbase(), is(IsFlag.Fou.getCode()));
			assertThat(signalFile.getIs_into_hive(), is(IsFlag.Shi.getCode()));
			assertThat(signalFile.getIs_mpp(), is(IsFlag.Fou.getCode()));
			assertThat(signalFile.getIs_solr_hbase(), is(IsFlag.Fou.getCode()));
			assertThat(signalFile.getTable_type(), is(IsFlag.Shi.getCode()));
			Table_info tableInfo = SqlOperator.queryOneObject(db, Table_info.class,
					"select * from " + Table_info.TableName + " where database_id=?",
					databaseSet.getDatabase_id()).orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(tableInfo.getTable_ch_name(), is("agent_info"));
			assertThat(tableInfo.getTable_name(), is("agent_info"));
			assertThat(tableInfo.getIs_register(), is(IsFlag.Fou.getCode()));
			assertThat(tableInfo.getIs_user_defined(), is(IsFlag.Fou.getCode()));
			assertThat(tableInfo.getTable_count(), is("0"));
			assertThat(tableInfo.getIs_md5(), is(IsFlag.Shi.getCode()));
			assertThat(tableInfo.getValid_e_date(), is("99991231"));
			Column_merge columnMerge = SqlOperator.queryOneObject(db, Column_merge.class,
					"select * from " + Column_merge.TableName + " where table_id=?",
					tableInfo.getTable_id()).orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(columnMerge.getCol_name(), is("agentAddress"));
			assertThat(columnMerge.getCol_type(), is("1"));
			assertThat(columnMerge.getCol_zhname(), is("agent地址"));
			assertThat(columnMerge.getValid_e_date(), is("99991231"));
			assertThat(columnMerge.getOld_name(), is("agent_ip"));
			Map<String, Object> storageInfo = SqlOperator.queryOneObject(db,
					"select * from " + Table_storage_info.TableName + " where table_id=?",
					tableInfo.getTable_id());
			assertThat(storageInfo.get("file_format"), is(FileFormat.CSV.getCode()));
			assertThat(storageInfo.get("is_zipper"), is(IsFlag.Fou.getCode()));
			assertThat(storageInfo.get("storage_time").toString(), is("1"));
			assertThat(storageInfo.get("storage_type"), is(StorageType.TiHuan.getCode()));
			Map<String, Object> tableClean = SqlOperator.queryOneObject(db,
					"select * from " + Table_clean.TableName + " where table_id=?",
					tableInfo.getTable_id());
			assertThat(tableClean.get("character_filling"), is("#"));
			assertThat(tableClean.get("filling_length").toString(), is("1"));
			assertThat(tableClean.get("filling_type"), is(FillingType.HouBuQi.getCode()));
			assertThat(tableClean.get("replace_feild"), is("agent_ip"));
			assertThat(tableClean.get("clean_type"), is(CleanType.ZiFuBuQi.getCode()));
			Table_column tableColumn = SqlOperator.queryOneObject(db, Table_column.class,
					"select * from " + Table_column.TableName + " where table_id=?",
					tableInfo.getTable_id()).orElseThrow(() -> new RuntimeException("sql执行错误!"));
			assertThat(tableColumn.getColumn_name(), is("create_date"));
			assertThat(tableColumn.getTable_id(), is(tableInfo.getTable_id()));
			assertThat(tableColumn.getIs_alive(), is(IsFlag.Shi.getCode()));
			assertThat(tableColumn.getIs_primary_key(), is(IsFlag.Fou.getCode()));
			assertThat(tableColumn.getIs_new(), is(IsFlag.Fou.getCode()));
			assertThat(tableColumn.getIs_get(), is(IsFlag.Fou.getCode()));
			assertThat(tableColumn.getValid_e_date(), is("99991231"));
			Map<String, Object> columnClean = SqlOperator.queryOneObject(db,
					"select * from " + Column_clean.TableName + " where column_id=?",
					tableColumn.getColumn_id());
			assertThat(columnClean.get("filling_length").toString(), is("1"));
			assertThat(columnClean.get("codesys"), is("1"));
			assertThat(columnClean.get("codename"), is("是"));
			assertThat(columnClean.get("character_filling"), is("#"));
			assertThat(columnClean.get("clean_type"), is(CleanType.ZiFuBuQi.getCode()));
			Map<String, Object> columnSplit = SqlOperator.queryOneObject(db,
					"select * from " + Column_split.TableName + " where column_id=?",
					tableColumn.getColumn_id());
			assertThat(columnSplit.get("col_type"), is("1"));
			assertThat(columnSplit.get("col_clean_id"), is(columnClean.get("col_clean_id")));
			assertThat(columnSplit.get("col_name"), is("agent_ip"));
			assertThat(columnSplit.get("split_type"), is(CharSplitType.ZhiDingFuHao.getCode()));
			assertThat(columnSplit.get("seq").toString(), is("0"));
			assertThat(columnSplit.get("valid_e_date"), is("99991231"));
		}
		// 2.错误的数据访问1，导入数据源，agentIp不合法
		bodyString = new HttpClient()
				.addData("agent_ip", "10.71.4.666")
				.addData("agent_port", "4321")
				.addData("user_id", UserId)
				.addData("file", filePath)
				.post(getActionUrl("uploadFile"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));

		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，导入数据源，agentPort不合法
		bodyString = new HttpClient()
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "666666")
				.addData("user_id", UserId)
				.addData("file", filePath)
				.post(getActionUrl("uploadFile"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，导入数据源，userCollectId为空
		bodyString = new HttpClient()
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4321")
				.addData("user_id", "")
				.addData("file", filePath)
				.post(getActionUrl("uploadFile"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，导入数据源，file为空
		bodyString = new HttpClient()
				.addData("agent_ip", "10.71.4.51")
				.addData("agent_port", "4321")
				.addData("user_id", UserId)
				.addData("file", "")
				.post(getActionUrl("uploadFile"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class)
				.orElseThrow(() -> new BusinessException("json对象转换成实体对象失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

}

