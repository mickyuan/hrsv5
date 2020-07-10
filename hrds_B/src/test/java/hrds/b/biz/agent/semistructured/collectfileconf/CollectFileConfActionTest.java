package hrds.b.biz.agent.semistructured.collectfileconf;

import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.*;
import hrds.commons.entity.Object_collect_struct;
import hrds.commons.entity.Object_collect_task;
import hrds.commons.entity.Object_handle_type;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@DocClass(desc = "半结构化采集文件配置类测试", author = "dhw", createdate = "2020/7/7 17:02")
public class CollectFileConfActionTest extends WebBaseTestCase {

	private final InitObjectCollectData initObjectCollectData = new InitObjectCollectData();

	@Before
	public void before() {

		initObjectCollectData.initData();
		//		模拟登陆
		ActionResult actionResult = login();
		assertThat("模拟登陆", actionResult.isSuccess(), is(true));
	}

	@Method(desc = "根据对象采集id查询对象采集对应信息的合集(采集文件配置）",
			logicStep = "1.正确的数据访问1，无数据字典" +
					"2.正确的数据访问2，有数据字典" +
					"3.错误的数据访问1，odc_id不存在")
	@Test
	public void searchObjectCollectTask() {
		// 1.正确的数据访问1，无数据字典
		String bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID)
				.post(getActionUrl("searchObjectCollectTask"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		Map<Object, Object> dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("is_dictionary"), is(IsFlag.Fou.getCode()));
		List<Object_collect_task> objectCollectTasks =
				JsonUtil.toObject(dataForMap.get("tableInfo").toString(),
						new TypeReference<List<Object_collect_task>>() {
						}.getType());
		assertThat(objectCollectTasks.get(0).getFirstline(), is(notNullValue()));
		assertThat(objectCollectTasks.get(0).getEn_name(), is("no_dictionary"));
		assertThat(objectCollectTasks.get(0).getZh_name(), is("no_dictionary"));
		// 2.正确的数据访问2，有数据字典
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.post(getActionUrl("searchObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		dataForMap = ar.getDataForMap();
		assertThat(dataForMap.get("is_dictionary"), is(IsFlag.Shi.getCode()));
		objectCollectTasks =
				JsonUtil.toObject(dataForMap.get("tableInfo").toString(),
						new TypeReference<List<Object_collect_task>>() {
						}.getType());
		List<String> tableNameList = new ArrayList<>();
		for (Object_collect_task objectCollectTask : objectCollectTasks) {
			tableNameList.add(objectCollectTask.getEn_name());
		}
		assertThat(tableNameList.contains("t_executedpersons"), is(true));
		assertThat(tableNameList.contains("t_executedpersons2"), is(true));
		// 3.错误的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123141")
				.post(getActionUrl("searchObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "保存采集文件设置信息", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.正确的数据访问1，odc_id不存在" +
			"3.正确的数据访问2，agent_id不存在" +
			"4.正确的数据访问3，update_type不存在" +
			"5.正确的数据访问4，collect_data_type不存在" +
			"6.正确的数据访问5，update_type不存在")
	@Test
	public void saveObjectCollectTask() {
		List<Object_collect_task> objectCollectTaskList = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			Object_collect_task object_collect_task = new Object_collect_task();
			object_collect_task.setOcs_id(initObjectCollectData.OCS_ID + i);
			if (i == 1) {
				object_collect_task.setEn_name("t_executedpersons");
				object_collect_task.setZh_name("t_executedpersons");
			} else {
				object_collect_task.setEn_name("t_executedpersons2");
				object_collect_task.setZh_name("t_executedpersons2");
			}
			object_collect_task.setUpdatetype(UpdateType.IncrementUpdate.getCode());
			object_collect_task.setCollect_data_type(CollectDataType.XML.getCode());
			object_collect_task.setDatabase_code(DataBaseCode.ISO_8859_1.getCode());
			objectCollectTaskList.add(object_collect_task);
		}
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("agent_id", initObjectCollectData.AGENT_ID)
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Object_collect_task> objectCollectTasks = SqlOperator.queryList(db, Object_collect_task.class,
					"select * from " + Object_collect_task.TableName + " where odc_id=?",
					initObjectCollectData.ODC_ID + 1);
			for (Object_collect_task objectCollectTask : objectCollectTasks) {
				if (objectCollectTask.getOcs_id() == initObjectCollectData.OCS_ID + 1) {
					assertThat(objectCollectTask.getEn_name(), is("t_executedpersons"));
					assertThat(objectCollectTask.getZh_name(), is("t_executedpersons"));
				} else {
					assertThat(objectCollectTask.getEn_name(), is("t_executedpersons2"));
					assertThat(objectCollectTask.getZh_name(), is("t_executedpersons2"));
				}
				assertThat(objectCollectTask.getDatabase_code(), is(DataBaseCode.ISO_8859_1.getCode()));
				assertThat(objectCollectTask.getCollect_data_type(), is(CollectDataType.XML.getCode()));
				assertThat(objectCollectTask.getUpdatetype(), is(UpdateType.IncrementUpdate.getCode()));
			}
		}
		// 2.正确的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.addData("agent_id", initObjectCollectData.AGENT_ID)
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.正确的数据访问2，agent_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("agent_id", "123")
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.正确的数据访问3，update_type不存在
		objectCollectTaskList = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			Object_collect_task object_collect_task = new Object_collect_task();
			object_collect_task.setOcs_id(initObjectCollectData.OCS_ID + i);
			if (i == 1) {
				object_collect_task.setEn_name("t_executedpersons");
				object_collect_task.setZh_name("t_executedpersons");
			} else {
				object_collect_task.setEn_name("t_executedpersons2");
				object_collect_task.setZh_name("t_executedpersons2");
			}
			object_collect_task.setUpdatetype("-1");
			object_collect_task.setCollect_data_type(CollectDataType.XML.getCode());
			object_collect_task.setDatabase_code(DataBaseCode.ISO_8859_1.getCode());
			objectCollectTaskList.add(object_collect_task);
		}
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("agent_id", initObjectCollectData.AGENT_ID)
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.正确的数据访问4，collect_data_type不存在
		objectCollectTaskList = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			Object_collect_task object_collect_task = new Object_collect_task();
			object_collect_task.setOcs_id(initObjectCollectData.OCS_ID + i);
			if (i == 1) {
				object_collect_task.setEn_name("t_executedpersons");
				object_collect_task.setZh_name("t_executedpersons");
			} else {
				object_collect_task.setEn_name("t_executedpersons2");
				object_collect_task.setZh_name("t_executedpersons2");
			}
			object_collect_task.setUpdatetype(UpdateType.DirectUpdate.getCode());
			object_collect_task.setCollect_data_type("-1");
			object_collect_task.setDatabase_code(DataBaseCode.ISO_8859_1.getCode());
			objectCollectTaskList.add(object_collect_task);
		}
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("agent_id", initObjectCollectData.AGENT_ID)
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 6.正确的数据访问5，update_type不存在
		objectCollectTaskList = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			Object_collect_task object_collect_task = new Object_collect_task();
			object_collect_task.setOcs_id(initObjectCollectData.OCS_ID + i);
			if (i == 1) {
				object_collect_task.setEn_name("t_executedpersons");
				object_collect_task.setZh_name("t_executedpersons");
			} else {
				object_collect_task.setEn_name("t_executedpersons2");
				object_collect_task.setZh_name("t_executedpersons2");
			}
			object_collect_task.setUpdatetype(UpdateType.DirectUpdate.getCode());
			object_collect_task.setCollect_data_type(CollectDataType.JSON.getCode());
			object_collect_task.setDatabase_code("-1");
			objectCollectTaskList.add(object_collect_task);
		}
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("agent_id", initObjectCollectData.AGENT_ID)
				.addData("objectCollectTasks", JsonUtil.toJson(objectCollectTaskList))
				.post(getActionUrl("saveObjectCollectTask"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "有数据字典时获取半结构化采集列结构信息", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，odc_id不存在" +
			"3.错误的数据访问2，ocs_id不存在" +
			"4.错误的数据访问3，en_name不存在" +
			"5.错误的数据访问4，当前采集任务数据字典不存在")
	@Test
	public void getObjectCollectStruct() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("ocs_id", initObjectCollectData.OCS_ID + 1)
				.addData("en_name", "t_executedpersons2")
				.post(getActionUrl("getObjectCollectStruct"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		List<Object_collect_struct> objectCollectStructs = ar.getDataForEntityList(Object_collect_struct.class);
		for (Object_collect_struct objectCollectStruct : objectCollectStructs) {
			String column_name = objectCollectStruct.getColumn_name();
			if ("case_number".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("case_number"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Shi.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,case_number"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else if ("ops_flag".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("ops_flag"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,ops_flag"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else if ("status".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("status"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,status"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else {
				assertThat(objectCollectStruct.getData_desc(), is("operate"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,operate"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			}
		}
		// 2.错误的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.addData("ocs_id", initObjectCollectData.OCS_ID + 1)
				.addData("en_name", "t_executedpersons2")
				.post(getActionUrl("getObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，ocs_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("ocs_id", "123")
				.addData("en_name", "t_executedpersons2")
				.post(getActionUrl("getObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，en_name不存在
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("ocs_id", initObjectCollectData.OCS_ID + 1)
				.addData("en_name", "aaa")
				.post(getActionUrl("getObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 5.错误的数据访问4，当前采集任务数据字典不存在
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID)
				.addData("ocs_id", initObjectCollectData.OCS_ID)
				.addData("en_name", "no_dictionary")
				.post(getActionUrl("getObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "没有数据字典时获取采集列结构(树展示)", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，odc_id不存在" +
			"3.错误的数据访问2，ocs_id不存在" +
			"4.错误的数据访问3，odc_id为有数据字典任务")
	@Test
	public void getFirstLineTreeInfo() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID)
				.addData("ocs_id", initObjectCollectData.OCS_ID)
				.post(getActionUrl("getFirstLineTreeInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat("解析第一行数据存在", ar.getData(), is(notNullValue()));
		// 2.错误的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.addData("ocs_id", initObjectCollectData.OCS_ID)
				.post(getActionUrl("getFirstLineTreeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，ocs_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID)
				.addData("ocs_id", "123")
				.post(getActionUrl("getFirstLineTreeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，odc_id为有数据字典任务
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("ocs_id", initObjectCollectData.OCS_ID + 1)
				.post(getActionUrl("getFirstLineTreeInfo"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "无数据字典时查询采集列结构信息(与getFirstLineTreeInfo一起使用)测试",
			logicStep = "1.正确的数据访问1，数据都有效")
	@Test
	public void getObjectCollectStructById() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("ocs_id", initObjectCollectData.OCS_ID + 1)
				.post(getActionUrl("getObjectCollectStructById"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		List<Object_collect_struct> objectCollectStructs = ar.getDataForEntityList(Object_collect_struct.class);
		for (Object_collect_struct objectCollectStruct : objectCollectStructs) {
			assertThat(objectCollectStruct.getOcs_id(), is(initObjectCollectData.OCS_ID + 1));
			String column_name = objectCollectStruct.getColumn_name();
			if ("case_number".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("case_number"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Shi.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,case_number"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else if ("ops_flag".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("ops_flag"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,ops_flag"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else if ("status".equals(column_name)) {
				assertThat(objectCollectStruct.getData_desc(), is("status"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,status"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			} else {
				assertThat(objectCollectStruct.getData_desc(), is("operate"));
				assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
				assertThat(objectCollectStruct.getColumnposition(), is("fields,operate"));
				assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
			}
		}
	}

	@Method(desc = "获取有数据字典时操作码表（采集数据处理类型对应表）信息）",
			logicStep = "1.正确的数据访问1，数据都有效" +
					"2.错误的数据访问1，odc_id不存在" +
					"3.错误的数据访问2，en_name不存在" +
					"4.错误的数据访问3，odc_id对应的任务数据字典不存在")
	@Test
	public void searchObjectHandleType() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("en_name", "t_executedpersons")
				.post(getActionUrl("searchObjectHandleType"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		List<Object_handle_type> objectHandleTypes = ar.getDataForEntityList(Object_handle_type.class);
		assertThat(objectHandleTypes.size(), is(3));
		for (Object_handle_type objectHandleType : objectHandleTypes) {
			if (OperationType.INSERT == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
				assertThat(objectHandleType.getHandle_value(), is("INSERT"));
			} else if (OperationType.UPDATE == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
				assertThat(objectHandleType.getHandle_value(), is("UPDATE"));
			} else if (OperationType.DELETE == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
				assertThat(objectHandleType.getHandle_value(), is("DELETE"));
			}
		}
		// 2.错误的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.addData("en_name", "t_executedpersons")
				.post(getActionUrl("searchObjectHandleType"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，en_name不存在
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("en_name", "aaa")
				.post(getActionUrl("searchObjectHandleType"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，odc_id对应的任务数据字典不存在
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID)
				.addData("en_name", "no_dictionary")
				.post(getActionUrl("searchObjectHandleType"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "保存操作码表（采集数据处理类型对应表）信息", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，ocs_id不存在")
	@Test
	public void saveObjectHandleType() {
		List<Object_handle_type> objectHandleTypes = new ArrayList<>();
		OperationType[] operationTypes = OperationType.values();
		for (OperationType operationType : operationTypes) {
			Object_handle_type object_handle_type = new Object_handle_type();
			object_handle_type.setHandle_type(operationType.getCode());
			object_handle_type.setHandle_value(operationType.getValue());
			objectHandleTypes.add(object_handle_type);
		}
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("ocs_id", initObjectCollectData.OCS_ID + 1)
				.addData("objectHandleTypes", JsonUtil.toJson(objectHandleTypes))
				.post(getActionUrl("saveObjectHandleType"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Object_handle_type> handleTypeList = Dbo.queryList(db, Object_handle_type.class,
					"select * from " + Object_handle_type.TableName + " where ocs_id=?",
					initObjectCollectData.OCS_ID + 1);
			for (Object_handle_type objectHandleType : handleTypeList) {
				if (OperationType.INSERT == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
					assertThat(objectHandleType.getHandle_value(), is("INSERT"));
				} else if (OperationType.UPDATE == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
					assertThat(objectHandleType.getHandle_value(), is("UPDATE"));
				} else if (OperationType.DELETE == OperationType.ofEnumByCode(objectHandleType.getHandle_type())) {
					assertThat(objectHandleType.getHandle_value(), is("DELETE"));
				}
			}
		}
		// 2.错误的数据访问1，ocs_id不存在
		bodyString = new HttpClient()
				.addData("ocs_id", "123")
				.addData("objectHandleTypes", JsonUtil.toJson(objectHandleTypes))
				.post(getActionUrl("saveObjectHandleType"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Method(desc = "获取对象采集树节点信息", logicStep = "")
	@Test
	public void getObjectCollectTreeInfo() {
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID)
				.addData("ocs_id", initObjectCollectData.OCS_ID)
				.addData("location", "fields")
				.post(getActionUrl("getObjectCollectTreeInfo"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		assertThat(ar.getData(), is(notNullValue()));
	}

	@Method(desc = "保存对象采集结构信息（采集列结构）测试", logicStep = "1.正确的数据访问1，数据都有效" +
			"2.错误的数据访问1，odc_id不存在" +
			"3.错误的数据访问1，ocs_id不存在" +
			"4.错误的数据访问3，操作字段不为一个")
	@Test
	public void saveObjectCollectStruct() {
		List<Object_collect_struct> objectCollectStructList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Object_collect_struct object_collect_struct = new Object_collect_struct();
			if (i == 0) {
				object_collect_struct.setColumn_name("case_number");
				object_collect_struct.setData_desc("case_number");
				object_collect_struct.setIs_operate(IsFlag.Shi.getCode());
				object_collect_struct.setColumnposition("fields,case_number");
			} else if (i == 1) {
				object_collect_struct.setColumn_name("ops_flag");
				object_collect_struct.setData_desc("ops_flag");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,ops_flag");
			} else if (i == 2) {
				object_collect_struct.setColumn_name("status");
				object_collect_struct.setData_desc("status");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,status");
			} else {
				object_collect_struct.setColumn_name("operate");
				object_collect_struct.setData_desc("operate");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,operate");
			}
			object_collect_struct.setColumn_type("decimal(38,18)");
			objectCollectStructList.add(object_collect_struct);
		}
		// 1.正确的数据访问1，数据都有效
		String bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("ocs_id", initObjectCollectData.OCS_ID + 1)
				.addData("objectCollectStructs", JsonUtil.toJson(objectCollectStructList))
				.post(getActionUrl("saveObjectCollectStruct"))
				.getBodyString();
		ActionResult ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			List<Object_collect_struct> objectCollectStructs =
					SqlOperator.queryList(db, Object_collect_struct.class,
							"select * from " + Object_collect_struct.TableName + " where ocs_id=?",
							initObjectCollectData.OCS_ID + 1);
			assertThat(objectCollectStructList.size(), is(4));
			for (Object_collect_struct objectCollectStruct : objectCollectStructs) {
				String column_name = objectCollectStruct.getColumn_name();
				if ("case_number".equals(column_name)) {
					assertThat(objectCollectStruct.getData_desc(), is("case_number"));
					assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Shi.getCode()));
					assertThat(objectCollectStruct.getColumnposition(), is("fields,case_number"));
					assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
				} else if ("ops_flag".equals(column_name)) {
					assertThat(objectCollectStruct.getData_desc(), is("ops_flag"));
					assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
					assertThat(objectCollectStruct.getColumnposition(), is("fields,ops_flag"));
					assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
				} else if ("status".equals(column_name)) {
					assertThat(objectCollectStruct.getData_desc(), is("status"));
					assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
					assertThat(objectCollectStruct.getColumnposition(), is("fields,status"));
					assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
				} else {
					assertThat(objectCollectStruct.getData_desc(), is("operate"));
					assertThat(objectCollectStruct.getIs_operate(), is(IsFlag.Fou.getCode()));
					assertThat(objectCollectStruct.getColumnposition(), is("fields,operate"));
					assertThat(objectCollectStruct.getColumn_type(), is("decimal(38,18)"));
				}
			}
		}
		// 2.错误的数据访问1，odc_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", "123")
				.addData("ocs_id", initObjectCollectData.OCS_ID + 1)
				.addData("objectCollectStructs", JsonUtil.toJson(objectCollectStructList))
				.post(getActionUrl("saveObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 3.错误的数据访问2，ocs_id不存在
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("ocs_id", "123")
				.addData("objectCollectStructs", JsonUtil.toJson(objectCollectStructList))
				.post(getActionUrl("saveObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
		// 4.错误的数据访问3，操作字段不为一个
		objectCollectStructList = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			Object_collect_struct object_collect_struct = new Object_collect_struct();
			if (i == 0) {
				object_collect_struct.setColumn_name("case_number");
				object_collect_struct.setData_desc("case_number");
				object_collect_struct.setIs_operate(IsFlag.Shi.getCode());
				object_collect_struct.setColumnposition("fields,case_number");
			} else if (i == 1) {
				object_collect_struct.setColumn_name("ops_flag");
				object_collect_struct.setData_desc("ops_flag");
				object_collect_struct.setIs_operate(IsFlag.Shi.getCode());
				object_collect_struct.setColumnposition("fields,ops_flag");
			} else if (i == 2) {
				object_collect_struct.setColumn_name("status");
				object_collect_struct.setData_desc("status");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,status");
			} else {
				object_collect_struct.setColumn_name("operate");
				object_collect_struct.setData_desc("operate");
				object_collect_struct.setIs_operate(IsFlag.Fou.getCode());
				object_collect_struct.setColumnposition("fields,operate");
			}
			object_collect_struct.setColumn_type("decimal(38,18)");
			objectCollectStructList.add(object_collect_struct);
		}
		bodyString = new HttpClient()
				.addData("odc_id", initObjectCollectData.ODC_ID + 1)
				.addData("ocs_id", initObjectCollectData.OCS_ID + 1)
				.addData("objectCollectStructs", JsonUtil.toJson(objectCollectStructList))
				.post(getActionUrl("saveObjectCollectStruct"))
				.getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(false));
	}

	@Test
	public void getObjectHandleType() {
	}

	@After
	public void after() {
		initObjectCollectData.deleteInitData();
	}
}