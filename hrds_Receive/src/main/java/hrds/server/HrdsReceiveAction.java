package hrds.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.AgentBaseAction;
import hrds.commons.entity.Collect_case;
import hrds.commons.entity.Data_store_reg;
import hrds.commons.entity.Source_file_attribute;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.PackUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@DocClass(desc = "海云服务接收端", author = "zxz", createdate = "2019/11/19 11:17")
public class HrdsReceiveAction extends AgentBaseAction {

//	@Method(desc = "保存错误信息", logicStep = "1.获取错误信息，保存到系统")
//	@Param(name = "job_rs_id", desc = "作业执行结果id", range = "不能为空")
//	@Param(name = "msg", desc = "作业执行结果信息", range = "不能为空")
//	public void saveErrorInfo(String job_rs_id, String msg) {
//		//1.获取错误信息，保存到系统
//		Error_info error = new Error_info();
//		error.setError_id(PrimayKeyGener.getNextId());
//		error.setJob_rs_id(job_rs_id);
//		//这个长度之前的程序是做了控制的，这里没有做控制
//		error.setError_msg(msg);
//		error.add(Dbo.db());
//	}

	@Method(desc = "批量添加source_file_attribute",
			logicStep = "1.解析addParamsPool为List集合" +
					"2.执行批量添加的方法")
	@Param(name = "addSql", desc = "批量添加的sql", range = "不可为空")
	@Param(name = "addParamsPool", desc = "批量导入的数据", range = "不可为空")
	public void batchAddSourceFileAttribute(String addSql, String addParamsPool) {
		addParamsPool = PackUtil.unpackMsg(addParamsPool).get("msg");
		//1.解析addParamsPool为List集合
		List<Object[]> objects = parseListArray(addParamsPool);
		//2.执行批量添加的方法
		int[] adds = Dbo.executeBatch(addSql, objects);
		for (int i : adds) {
			if (i != 1) {
				throw new BusinessException("批量添加source_file_attribute表失败");
			}
		}
	}

	@Method(desc = "批量跟新source_file_attribute",
			logicStep = "1.解析updateParamsPool为List集合" +
					"2.执行批量更新的方法")
	@Param(name = "updateSql", desc = "批量更新的sql", range = "不可为空")
	@Param(name = "updateParamsPool", desc = "批量更新的数据", range = "不可为空")
	public void batchUpdateSourceFileAttribute(String updateSql, String updateParamsPool) {
		updateParamsPool = PackUtil.unpackMsg(updateParamsPool).get("msg");
		//1.解析updateParamsPool为List集合
		List<Object[]> objects = parseListArray(updateParamsPool);
		//2.执行批量更新的方法
		int[] updates = Dbo.executeBatch(updateSql, objects);
		for (int i : updates) {
			if (i != 1) {
				throw new BusinessException("批量更新source_file_attribute表失败");
			}
		}
	}

	@Method(desc = "保存Collect_case表",
			logicStep = "1.解析collect_case字符串为Collect_case对象" +
					"2.保存Collect_case表")
	@Param(name = "collect_case", desc = "collect_case表json类型的数据", range = "不可为空")
	@Param(name = "msg", desc = "作业执行结果信息", range = "不能为空")
	public void saveCollectCase(String collect_case, String msg) {
		collect_case = PackUtil.unpackMsg(collect_case).get("msg");
		//1.解析collect_case字符串为Collect_case对象
		Collect_case collect = JSONObject.parseObject(collect_case, Collect_case.class);
		//2.查询collect_case表
		Result result = Dbo.queryResult("select * from collect_case where agent_id = ? and source_id = ?" +
						" and collect_set_id = ?  and job_type = ? and etl_date = ?  and task_classify = ? ",
				collect.getAgent_id(), collect.getSource_id(), collect.getCollect_set_id(),
				collect.getJob_type(), collect.getEtl_date(), collect.getTask_classify());
		//3.更新或者新增Collect_case表
		if (result.isEmpty()) {
			String job_rs_id = UUID.randomUUID().toString();
			collect.setJob_rs_id(job_rs_id);
			collect.setCc_remark(msg);
			//新增source_file_attribute表
			collect.add(Dbo.db());
			//新增错误信息表，保存到系统
//			Error_info error = new Error_info();
//			error.setError_id(PrimayKeyGener.getNextId());
//			error.setJob_rs_id(job_rs_id);
//			//这个长度之前的程序是做了控制的，这里没有做控制
//			error.setError_msg(msg);
//			error.add(Dbo.db());
		} else {
			String job_rs_id = result.getString(0, "job_rs_id");
			collect.setJob_rs_id(job_rs_id);
			collect.setCc_remark(msg);
			//更新source_file_attribute表
			collect.update(Dbo.db());
			//更新error_info表
//			Error_info error_info = Dbo.queryOneObject(Error_info.class, "SELECT * FROM " + Error_info.TableName
//					+ " WHERE job_rs_id = ?", job_rs_id).orElseThrow(() ->
//					new BusinessException("查询不到error_info表的数据"));
//			error_info.setError_msg(msg);
//			error_info.update(Dbo.db());
		}
	}

	@Method(desc = "保存source_file_attribute表",
			logicStep = "1.解析source_file_attribute" +
					"2.查询source_file_attribute" +
					"3.更新或者新增source_file_attribute表")
	@Param(name = "source_file_attribute", desc = "source_file_attribute表json类型的数据", range = "不可为空")
	public void addSourceFileAttribute(String source_file_attribute) {
		source_file_attribute = PackUtil.unpackMsg(source_file_attribute).get("msg");
		//1.解析source_file_attribute
		Source_file_attribute attribute = JSONObject.parseObject(source_file_attribute,
				Source_file_attribute.class);
		//2.查询source_file_attribute
		Result result = Dbo.queryResult("select * from source_file_attribute where agent_id =? and source_id = ? " +
						"and collect_set_id =? and lower(hbase_name) = lower(?)", attribute.getAgent_id(),
				attribute.getSource_id(), attribute.getCollect_set_id(), attribute.getHbase_name());
		//3.更新或者新增source_file_attribute表
		if (result.isEmpty()) {
			attribute.setFile_id(UUID.randomUUID().toString());
			//新增source_file_attribute表
			attribute.add(Dbo.db());
		} else {
			attribute.setFile_id(result.getString(0, "file_id"));
			//更新source_file_attribute表
			attribute.update(Dbo.db());
		}
	}

	@Method(desc = "保存data_store_reg表",
			logicStep = "1.解析data_store_reg" +
					"2.查询data_store_reg" +
					"3.更新或者新增data_store_reg表")
	@Param(name = "data_store_reg", desc = "data_store_reg表json类型的数据", range = "不可为空")
	public void addDataStoreReg(String data_store_reg) {
		data_store_reg = PackUtil.unpackMsg(data_store_reg).get("msg");
		//1.解析source_file_attribute
		Data_store_reg data_store = JSONObject.parseObject(data_store_reg,
				Data_store_reg.class);
		//2.查询source_file_attribute
		Result result = Dbo.queryResult("select * from data_store_reg where agent_id =? and source_id = ? " +
						"and database_id =? and lower(hyren_name) = lower(?)", data_store.getAgent_id(),
				data_store.getSource_id(), data_store.getDatabase_id(), data_store.getHyren_name());
		//3.更新或者新增data_store表
		if (result.isEmpty()) {
			data_store.setFile_id(UUID.randomUUID().toString());
			//新增source_file_attribute表
			data_store.add(Dbo.db());
		} else {
			data_store.setFile_id(result.getString(0, "file_id"));
			//更新source_file_attribute表
			data_store.update(Dbo.db());
		}
	}

	@Method(desc = "批量添加ftp已经传输的文件到ftp_transfered(ftp传输表)",
			logicStep = "1.解析addParamsPool为List集合" +
					"2.执行批量添加的方法")
	@Param(name = "addSql", desc = "批量添加的sql", range = "不可为空")
	@Param(name = "addParamsPool", desc = "批量导入的数据", range = "不可为空")
	public void batchAddFtpTransfer(String addSql, String addParamsPool) {
		addParamsPool = PackUtil.unpackMsg(addParamsPool).get("msg");
		//1.解析addParamsPool为List集合
		List<Object[]> objects = parseListArray(addParamsPool);
		//2.执行批量添加的方法
		int[] adds = Dbo.executeBatch(addSql, objects);
		for (int i : adds) {
			if (i != 1) {
				throw new BusinessException("批量添加ftp_transfered表失败");
			}
		}
	}

	@Method(desc = "解析需要batch提交的参数，为可以直接batch提交的类型",
			logicStep = "解析需要batch提交的参数，为可以直接batch提交的类型")
	@Param(name = "paramPool", desc = "需要batch提交的json数组字符串", range = "不可为空")
	private List<Object[]> parseListArray(String paramPool) {
		List<Object[]> arrayList = new ArrayList<>();
		for (Object aaa : JSONArray.parseArray(paramPool)) {
			JSONArray array1 = (JSONArray) aaa;
			if (array1 != null && array1.size() > 0) {
				Object[] o = new Object[array1.size()];
				for (int i = 0; i < array1.size(); i++) {
					o[i] = array1.get(i);
				}
				arrayList.add(o);
			}
		}
		return arrayList;
	}
}
