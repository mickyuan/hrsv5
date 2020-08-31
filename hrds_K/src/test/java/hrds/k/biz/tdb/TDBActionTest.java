package hrds.k.biz.tdb;

import fd.ng.core.annotation.Method;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.AgentType;
import hrds.commons.codes.DataSourceType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Data_store_reg;
import hrds.commons.entity.Dbm_dtable_info;
import hrds.commons.entity.Dbm_normbm_detect;
import hrds.commons.entity.Table_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;
import hrds.k.biz.tdb.bean.TdbTableBean;
import hrds.testbase.LoadGeneralTestData;
import hrds.testbase.WebBaseTestCase;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TDBActionTest extends WebBaseTestCase {

	//当前线程的id
	private static long THREAD_ID = Thread.currentThread().getId() * 1000000;
	//获取模拟登陆的URL
	private static final String LOGIN_URL = agentInitConfig.getString("login_url");
	//登录用户id
	private static final long USER_ID = agentInitConfig.getLong("general_oper_user_id");
	//登录用户密码
	private static final long PASSWORD = agentInitConfig.getLong("general_password");
	//初始化加载通用测试数据
	private static final LoadGeneralTestData loadGeneralTestData = new LoadGeneralTestData();
	//初始化通用 file_id
	private static String FILE_ID = String.valueOf(PrimayKeyGener.getNextId());
	//初始化通用 table_id
	private static String TABLE_ID = String.valueOf(PrimayKeyGener.getNextId());
	//初始化集市通用数据表id datatable_id
	private static String DATATABLE_ID = String.valueOf(PrimayKeyGener.getNextId());
	//初始化管控检测结果指标3记录id
	private static String RECORD_ID = String.valueOf(PrimayKeyGener.getNextId());
	//初始化自定义表id
	private static String UDL_TABLE_ID = String.valueOf(PrimayKeyGener.getNextId());

	@Method(desc = "初始化测试用例依赖表数据", logicStep = "初始化测试用例依赖表数据")
	@Before
	public void before() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//提交所有数据库执行操作
			db.commit();
			//模拟登陆
			String bodyString = new HttpClient()
					.addData("user_id", USER_ID)
					.addData("password", PASSWORD)
					.post(LOGIN_URL).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
					-> new BusinessException("模拟登陆失败!"));
			assertThat(ar.isSuccess(), is(true));
		}
	}

	@Method(desc = "测试案例执行完成后清理测试数据", logicStep = "测试案例执行完成后清理测试数据")
	@AfterClass
	public static void after() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//清理测试数据
			// Dbm_dtable_info
			SqlOperator.execute(db, "delete from " + Dbm_dtable_info.TableName + " where table_id in (?,?,?,?)",
					FILE_ID, DATATABLE_ID, RECORD_ID, UDL_TABLE_ID);
			long num = SqlOperator.queryNumber(db,
					"select count(1) from " + Dbm_dtable_info.TableName + " where table_id in (?,?,?,?)",
					FILE_ID, DATATABLE_ID, RECORD_ID, UDL_TABLE_ID)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Dbm_dtable_info 表此条数据删除后,记录数应该为0", num, is(0L));
			//提交数据库操作
			db.commit();
		}
	}

	@Test
	public void getTDBTreeData() {
		String bodyString = new HttpClient()
				.addData("tree_source", "dataBenchmarking")
				.post(getActionUrl("getTDBTreeData")).getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
				new BusinessException("获取树信息失败!"));
		assertThat(ar.isSuccess(), is(true));
	}

	@Test
	public void saveTDBTable() {
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			//加载测试保存DCL层对标检测表信息
			Data_store_reg data_store_reg = loadTestSaveDCLDbmDtableInfo(db);
			//TODO 初始化数据层对应表
			//设置数据对标表信息
			List<TdbTableBean> tdbTableBeans = new ArrayList<>();
			TdbTableBean tdbTableBean;
			tdbTableBean = new TdbTableBean();
			tdbTableBean.setFile_id(FILE_ID);
			tdbTableBean.setData_layer(DataSourceType.DCL.getCode());
			tdbTableBean.setHyren_name("dcl_table_name" + THREAD_ID);
			tdbTableBean.setTable_cn_name("dcl_table_cn_name_" + THREAD_ID);
			tdbTableBeans.add(tdbTableBean);
			tdbTableBean = new TdbTableBean();
			tdbTableBean.setFile_id(DATATABLE_ID);
			tdbTableBean.setData_layer(DataSourceType.DML.getCode());
			tdbTableBean.setHyren_name("dml_table_name" + THREAD_ID);
			tdbTableBean.setTable_cn_name("dml_table_cn_name_" + THREAD_ID);
			tdbTableBeans.add(tdbTableBean);
			tdbTableBean = new TdbTableBean();
			tdbTableBean.setFile_id(RECORD_ID);
			tdbTableBean.setData_layer(DataSourceType.DQC.getCode());
			tdbTableBean.setHyren_name("dqc_table_name" + THREAD_ID);
			tdbTableBean.setTable_cn_name("dqc_table_cn_name_" + THREAD_ID);
			tdbTableBeans.add(tdbTableBean);
			tdbTableBean = new TdbTableBean();
			tdbTableBean.setFile_id(UDL_TABLE_ID);
			tdbTableBean.setData_layer(DataSourceType.UDL.getCode());
			tdbTableBean.setHyren_name("udl_table_name" + THREAD_ID);
			tdbTableBean.setTable_cn_name("udl_table_cn_name_" + THREAD_ID);
			tdbTableBeans.add(tdbTableBean);
			//正确数据
			String bodyString = new HttpClient()
					.addData("tdb_table_bean_s", JsonUtil.toJson(tdbTableBeans))
					.post(getActionUrl("saveTDBTable")).getBodyString();
			ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(() ->
					new BusinessException("获取ActionResult结果失败!"));
			assertThat(ar.isSuccess(), is(true));
			long detect_id = (long) ar.getData();
			//清理接口产生的数据
			// Dbm_normbm_detect
			SqlOperator.execute(db, "delete from " + Dbm_normbm_detect.TableName + " where detect_id=?)", detect_id);
			long num = SqlOperator.queryNumber(db,
					"select count(1) from " + Dbm_normbm_detect.TableName + " where detect_id=?", detect_id)
					.orElseThrow(() -> new RuntimeException("count fail!"));
			assertThat("Dbm_normbm_detect 表此条数据删除后,记录数应该为0", num, is(0L));
			//提交数据库操作
			db.commit();
		}
	}

	@Method(desc = "加载测试保存DCL层数据表数据", logicStep = "加载测试保存数据表数据")
	private Data_store_reg loadTestSaveDCLDbmDtableInfo(DatabaseWrapper db) {
		//DCL层表依赖数据
		// Data_store_reg
		Data_store_reg dsr = new Data_store_reg();
		dsr.setFile_id(String.valueOf(FILE_ID + THREAD_ID));
		dsr.setCollect_type(AgentType.DBWenJian.getCode());
		dsr.setOriginal_update_date(DateUtil.getSysDate());
		dsr.setOriginal_update_time(DateUtil.getSysTime());
		dsr.setOriginal_name("测试数据对标保存DCL层表" + THREAD_ID);
		dsr.setHyren_name("dcl_tdb_save_table" + THREAD_ID);
		dsr.setStorage_date(DateUtil.getSysDate());
		dsr.setStorage_time(DateUtil.getSysTime());
		dsr.setFile_size(10000L);
		dsr.setAgent_id(loadGeneralTestData.getAgent_info().getAgent_id());
		dsr.setSource_id(loadGeneralTestData.getData_source().getSource_id());
		dsr.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
		dsr.setTable_id(TABLE_ID + THREAD_ID);
		dsr.add(db);
		// Table_info
		Table_info table_info = new Table_info();
		table_info.setTable_id(dsr.getTable_id());
		table_info.setTable_name(dsr.getTable_name());
		table_info.setTable_ch_name(dsr.getOriginal_name());
		table_info.setRec_num_date(dsr.getStorage_date());
		table_info.setDatabase_id(loadGeneralTestData.getDatabase_set().getDatabase_id());
		table_info.setValid_s_date(DateUtil.getSysDate());
		table_info.setValid_e_date("99991231");
		table_info.setIs_md5(IsFlag.Fou.getCode());
		table_info.setIs_register(IsFlag.Fou.getCode());
		table_info.setIs_customize_sql(IsFlag.Fou.getCode());
		table_info.setIs_parallel(IsFlag.Fou.getCode());
		table_info.setIs_user_defined(IsFlag.Fou.getCode());
		table_info.add(db);
		return dsr;
	}
}