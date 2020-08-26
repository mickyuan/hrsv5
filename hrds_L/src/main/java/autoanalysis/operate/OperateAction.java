package autoanalysis.operate;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AutoFetchStatus;
import hrds.commons.collection.ProcessingData;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@DocClass(desc = "自主分析操作类", author = "dhw", createdate = "2020/8/24 11:29")
public class OperateAction extends BaseAction {

	@Method(desc = "查询自主取数模板信息", logicStep = "1.查询并返回自主取数模板信息")
	@Return(desc = "返回自主取数模板信息", range = "无限制")
	public List<Map<String, Object>> getAutoAccessTemplateInfo() {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.查询并返回自主取数模板信息
		return Dbo.queryList(
				"select t1.template_id,t1.template_name,t1.template_desc,t1.create_date,t1.create_time,"
						+ "t1.create_user,count(t2.fetch_sum_id) as count_number from Auto_tp_info t1 "
						+ " left join auto_fetch_sum t2 on t1.template_id = t2.template_id "
						+ " where template_status = ? group by t1.template_id "
						+ " order by t1.create_date desc,t1.create_time desc",
				AutoFetchStatus.WanCheng.toString());
	}

	@Method(desc = "模糊查询自主取数模板信息", logicStep = "1.模糊查询自主取数模板信息")
	@Param(name = "template_name", desc = "模板名称", range = "无限制")
	@Return(desc = "返回模糊查询自主取数模板信息", range = "无限制")
	public List<Map<String, Object>> getAutoAccessTemplateInfoByName(String template_name) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.模糊查询自主取数模板信息
		return Dbo.queryList(
				"select t1.template_id,t1.template_name,t1.template_desc,t1.create_date,t1.create_time,"
						+ "t1.create_user,count(t2.fetch_sum_id) as count_number from Auto_tp_info t1 "
						+ " left join auto_fetch_sum t2 on t1.template_id = t2.template_id " +
						" where template_status = ? and template_name like ? group by t1.template_id "
						+ " order by t1.create_date desc,t1.create_time desc"
						+ AutoFetchStatus.WanCheng.getCode(), "%" + template_name + "%");
	}

	@Method(desc = "根据模板ID查询自主取数信息", logicStep = "1.根据模板ID查询自主取数信息")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回根据模板ID查询自主取数信息", range = "无限制")
	public Map<String, Object> getAutoAccessInfoById(long template_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.根据模板ID查询自主取数信息
		return Dbo.queryOneObject(
				"select template_name,template_desc from Auto_tp_info where template_id = ?",
				template_id);
	}

	@Method(desc = "获取自主取数结果字段", logicStep = "1.获取自主取数结果字段")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回自主取数过滤条件", range = "无限制")
	public List<Map<String, Object>> getAutoAccessResultFields(long template_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.获取自主取数结果字段
		return Dbo.queryList(
				"select res_show_column,template_res_id from Auto_tp_res_set where template_id = ?",
				template_id);
	}

	@Method(desc = "获取自主取数选择历史信息", logicStep = "1.获取自主取数选择历史信息")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回自主取数选择历史信息", range = "无限制")
	public List<Map<String, Object>> getAutoAccessSelectHistory(long template_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.获取自主取数选择历史信息
		return Dbo.queryList(
				"select * from Auto_fetch_sum where template_id = ? "
						+ " order by create_date desc ,create_time desc limit 10",
				template_id);
	}

	@Method(desc = "通过选择历史情况 获取之前的条件以及结果配置页面", logicStep = "1.获取自主取数选择历史信息")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回自主取数选择历史信息", range = "无限制")
	public List<Map<String, Object>> getAutoAccessCondAndResultFromHistory(long fetch_sum_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.获取自主取数选择历史信息
		// 获取到用户之前在页面中 填写的参数值 用模板条件ID进行匹配 template_cond_id
		List<Map<String, Object>> tpAndCondList = Dbo.queryList(
				"select cond_cn_column,con_relation,template_cond_id,pre_value,is_required,value_type,value_size"
						+ " from Auto_tp_cond_info t1 left join Auto_tp_info t2 "
						+ " on t1.template_id = t2.template_id left join Auto_fetch_sum t3 "
						+ " on t2.template_id = t3.template_id where t3.fetch_sum_id = ?",
				fetch_sum_id);
		List<Map<String, Object>> condList = Dbo.queryList(
				"select * from Auto_fetch_cond where fetch_sum_id = ?",
				fetch_sum_id);
		// 获取用户之前在页面上 勾选的 显示结果 用模板结果ID进行匹配 template_res_id
		List<Map<String, Object>> tpResSetList = Dbo.queryList(
				"select t1.res_show_column,t1.template_res_id,t3.fetch_sum_id "
						+ " from auto_tp_res_set t1 left join auto_tp_info t2 "
						+ " on t1.template_id = t2.template_id left join auto_fetch_sum t3 "
						+ " on t2.template_id = t3.template_id where t3.fetch_sum_id = ?",
				fetch_sum_id);
		List<Map<String, Object>> fetchResList = Dbo.queryList(
				"select * from Auto_fetch_res where fetch_sum_id = ?", fetch_sum_id);
		for (int i = 0; i < tpResSetList.size(); i++) {
			Map<String, Object> tpResMap = tpResSetList.get(i);
			for (Map<String, Object> fetchResMap : fetchResList) {
				if (tpResMap.get("template_res_id").toString()
						.equals(fetchResMap.get("template_res_id").toString())) {
					tpResMap.put("checked", true);
					tpResSetList.set(i, tpResMap);
				}
			}
		}
		return null;
	}

	@Method(desc = "获取自主取数过滤条件", logicStep = "1.获取自主取数过滤条件")
	@Param(name = "template_id", desc = "自主取数模板ID", range = "新增自主取数模板时生成")
	@Return(desc = "返回自主取数过滤条件", range = "无限制")
	public List<Map<String, Object>> getAutoAccessFilterCond(long template_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.获取自主取数过滤条件
		return Dbo.queryList(
				"select is_required,cond_cn_column,con_relation,template_cond_id,pre_value,value_type,value_size "
						+ " from Auto_tp_cond_info where template_id = ?",
				template_id);
	}

//	@Method(desc = "获取自主取数清单查询结果", logicStep = "")
//	@Param(name = "", desc = "", range = "")
//	@Return(desc = "", range = "")
//	public void getAutoAccessQueryResult(long fetch_sum_id) {
//		// 获取条件
//		Map<String, Object> sqlMap = Dbo.queryOneObject(
//				"select fetch_sql from auto_fetch_sum where fetch_sum_id = ?",
//				fetch_sum_id);
//		if (!sqlMap.isEmpty()) {
//			String fetch_sql = Arrays.toString(Base64.getDecoder().decode(sqlMap.get("fetch_sql").toString()));
//			List<String> dataLayer = new ProcessingData() {
//				@Override
//				public void dealLine(Map<String, Object> map) throws Exception {
//
//				}
//			}.getDataLayer(fetch_sql, Dbo.db());
//			//TODO 使用db.getDBType();做类型判断，不要在创建新的db连接
//			if (connection.toString().contains("postgresql")) {
//				//TODO 确认下一行的TODO是干嘛的
//				fetch_sql = fetch_sql.replace("`", "");// TODO
//				String sql = URLEncoder.encode(fetch_sql, "UTF-8");
//				auto_fetch_sum.setFetch_sql(sql);
//				if (dbo.update(auto_fetch_sum, db) != 1) {
//					throw new BusinessException("保存取数结果失败");
//				}
//			}
//			dbo.clear();
//			fetch_sql = new StringBuffer("select * from ( " + fetch_sql + " )").append(TempTableName).append(" limit ")
//					.append(limitnumber).toString();
//			//TODO 数据库查询的字段顺序与result的字段顺序不一致
//			Result result = db2.execute(fetch_sql);
//		}
//	}

	@Method(desc = "保存自主取数清单查询入库信息", logicStep = "")
	@Param(name = "", desc = "", range = "")
	@Return(desc = "", range = "")
	public void saveAutoAccessInfo() {

	}

	@Method(desc = "查询我的取数信息", logicStep = "1.查询自主取数信息")
	@Return(desc = "返回自主取数模板信息", range = "无限制")
	public List<Map<String, Object>> getMyAccessInfo() {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.查询我的取数信息
		return Dbo.queryList(
				"select * from auto_fetch_sum where user_id = ? and fetch_name is not null "
						+ " order by create_date desc,create_time desc",
				getUserId());
	}

	@Method(desc = "查看我的取数信息", logicStep = "1.查看我的取数信息")
	@Param(name = "fetch_sum_id", desc = "取数汇总表ID", range = "新增我的取数信息时生成")
	@Return(desc = "返回查看我的取数信息", range = "无限制")
	public List<Map<String, Object>> getMyAccessInfoById(long fetch_sum_id) {
		// 数据可访问权限处理方式，该方法不需要进行权限控制
		// 1.查看我的取数信息
		return Dbo.queryList("select fetch_name, fetch_desc from  Auto_fetch_sum where fetch_sum_id = ?",
				fetch_sum_id);
	}
}
