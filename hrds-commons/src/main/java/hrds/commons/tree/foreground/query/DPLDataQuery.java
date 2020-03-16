package hrds.commons.tree.foreground.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.UserType;
import hrds.commons.utils.User;

import java.util.List;
import java.util.Map;

@DocClass(desc = "加工层(DPL)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:17")
public class DPLDataQuery {

	private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();

	@Method(desc = "获取加工信息",
			logicStep = "1.获取集市信息")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Return(desc = "加工信息列表", range = "无限制")
	public static List<Map<String, Object>> getDPLDataInfos(User user) {
		return getDPLDataInfos(user, null);
	}

	@Method(desc = "获取加工信息",
			logicStep = "1.获取加工信息" +
					"2.如果工程名不为空,根据工程名检索加工工程信息")
	@Param(name = "user", desc = "User", range = "登录用户User的对象实例")
	@Param(name = "proName", desc = "加工的工程名", range = "String类型,长度512")
	@Return(desc = "加工信息列表", range = "无限制")
	public static List<Map<String, Object>> getDPLDataInfos(User user, String proName) {
		asmSql.clean();
		asmSql.addSql("SELECT * from edw_modal_project");
		UserType userType = UserType.ofEnumByCode(user.getUserType());
		if (UserType.JiShiJiaGongGuanLiYuan == userType) {
			asmSql.addSql(" where create_id = ?").addParam(user.getUserId());
		}
		if (!StringUtil.isBlank(proName)) {
			asmSql.addSql(" where pro_name like ?").addParam('%' + proName + '%');
		}
		return Dbo.queryList(asmSql.sql(), asmSql.params());
	}

//	@Method(desc = "根据加工id获取加工下的分类信息",
//			logicStep = "1.根据加工id获取加工下的分类信息")
//	@Param(name = "modal_pro_id", desc = "加工id", range = "加工,唯一")
//	@Return(desc = "加工下的分类信息", range = "无限制")
//	public static List<Map<String, Object>> getDPLClassifyInfos(String modal_pro_id) {
//		//1.根据加工id获取加工下的分类信息
//		return getDPLClassifyInfos(modal_pro_id, null);
//	}
//
//	@Method(desc = "根据加工id获取加工下的分类信息",
//			logicStep = "1.根据加工id获取加工下的分类信息")
//	@Param(name = "modal_pro_id", desc = "加工id", range = "加工,唯一")
//	@Param(name = "category_name", desc = "分类名称", range = "String类型,长度512")
//	@Return(desc = "加工下的分类信息", range = "无限制")
//	public static List<Map<String, Object>> getDPLClassifyInfos(String modal_pro_id, String category_name) {
//		//1.根据加工id获取加工下的分类信息
//		Edw_modal_category edw_modal_category = new Edw_modal_category();
//		if (StringUtil.isNotBlank(modal_pro_id)) {
//			edw_modal_category.setCategory_id(modal_pro_id);
//			asmSql.clean();
//			asmSql.addSql("SELECT * FROM edw_modal_category WHERE modal_pro_id = ? AND parent_category_id = ?")
//					.addParam(edw_modal_category.getCategory_id()).addParam(edw_modal_category.getCategory_id());
//		} else if (StringUtil.isNotBlank(category_name)) {
//			edw_modal_category.setCategory_name('%' + category_name + '%');
//			edw_modal_category.setCategory_num(category_name);
//			asmSql.clean();
//			asmSql.addSql("SELECT * FROM edw_modal_category WHERE category_name like ? OR category_num = ?")
//					.addParam(edw_modal_category.getCategory_name()).addParam(edw_modal_category.getCategory_num());
//		}
//		return Dbo.queryList(asmSql.sql(), asmSql.params());
//	}
//
//	@Method(desc = "根据加工的分类id获取分类下的表信息",
//			logicStep = "1.根据加工的分类id获取分类下的表信息")
//	@Param(name = "category_id", desc = "加工分类id", range = "加工分类id,唯一")
//	@Param(name = "pageFrom", desc = "页面来源", range = "TreePageSource.webType")
//	@Return(desc = "加工分类下的表信息", range = "无限制")
//	public static Map<String, Result> getDPLTableInfos(String category_id, String pageFrom) {
//		//1.根据加工的分类id获取分类下的表信息
//		return getDPLTableInfos(category_id, pageFrom, null);
//	}
//
//	@Method(desc = "根据加工的分类id获取分类下的表信息",
//			logicStep = "1.根据加工的分类id获取分类下的表信息")
//	@Param(name = "category_id", desc = "加工分类id", range = "加工分类id,唯一")
//	@Param(name = "pageFrom", desc = "页面来源", range = "TreePageSource.webType")
//	@Param(name = "modalName", desc = "模型名称", range = "String类型,长度512")
//	@Return(desc = "加工分类下的表信息", range = "无限制")
//	public static Map<String, Result> getDPLTableInfos(String category_id, String pageFrom,
//	                                                   String modalName) {
//		//1.根据加工的分类id获取分类下的表信息
//		Map<String, Result> map = new HashMap<>();
//		DataSourceType dataSourceType = DataSourceType.ofEnumByCode(pageFrom);
//		Edw_modal_category edw_modal_category = new Edw_modal_category();
//		if (StringUtil.isNotBlank(category_id)) {
//			Edw_table edwTable = new Edw_table();
//			edwTable.setCategory_id(category_id);
//			asmSql.clean();
//			asmSql.addSql("select * from edw_table where category_id = ?").addParam(edwTable.getCategory_id());
//			if (DataSourceType.DPL != dataSourceType) {
//				asmSql.addSql(" AND data_in = ?").addParam(IsFlag.Shi.getCode());
//			}
//			map.put("edw_table", Dbo.queryResult(asmSql.sql(), asmSql.params()));
//			asmSql.clean();
//			edw_modal_category.setCategory_id(category_id);
//			asmSql.addSql("SELECT * FROM edw_modal_category WHERE parent_category_id = ?");
//			asmSql.addParam(edw_modal_category.getCategory_id());
//			map.put("edw_modal_category", Dbo.queryResult(asmSql.sql(), asmSql.params()));
//		} else if (StringUtil.isNotBlank(modalName)) {
//			Edw_table edwTable = new Edw_table();
//			edwTable.setTabname('%' + modalName + '%');
//			asmSql.clean();
//			asmSql.addSql("SELECT * FROM edw_table WHERE (lower(tabname) LIKE lower(?)  OR lower(ctname)" +
//					" like lower(?))").addParam(edwTable.getTabname()).addParam(edwTable.getTabname());
//			if (DataSourceType.DPL != dataSourceType) {
//				asmSql.addSql(" AND data_in = ?").addParam(IsFlag.Shi.getCode());
//			}
//			Result edwTableRs = Dbo.queryResult(asmSql.sql(), asmSql.params());
//			map.put("edw_table", edwTableRs);
//			Result categoryResult = new Result();
//			for (int i = 0; i < edwTableRs.getRowCount(); i++) {
//				asmSql.clean();
//				edw_modal_category.setCategory_id(edwTableRs.getString(i, "category_id"));
//				asmSql.addSql("SELECT * FROM edw_modal_category WHERE category_id = ?");
//				asmSql.addParam(edw_modal_category.getCategory_id());
//				Result categoryRs = Dbo.queryResult(asmSql.sql(), asmSql.params());
//				if (!categoryRs.isEmpty()) {
//					Result categoryRsChildes = getCategoryByChild(categoryRs);
//					categoryResult.add(categoryRsChildes);
//				}
//				categoryResult.add(categoryRs);
//			}
//			asmSql.clean();
//			edw_modal_category.setCategory_name('%' + modalName + '%');
//			asmSql.addSql("SELECT * FROM edw_modal_category WHERE category_name like ?");
//			asmSql.addParam(edw_modal_category.getCategory_name());
//			Result categoryRs = Dbo.queryResult(asmSql.sql(), asmSql.params());
//			if (!categoryRs.isEmpty()) {
//				Result categoryRsChildes = getCategoryByChild(categoryRs);
//				categoryResult.add(categoryRsChildes);
//			}
//			categoryResult.add(categoryRs);
//			map.put("edw_modal_category", categoryResult);
//		}
//		return map;
//	}
//
//	@Method(desc = "加工获取子分类信息",
//			logicStep = "1.加工获取子分类信息")
//	@Param(name = "rs", desc = "当前分类节点结果集", range = "Result值")
//	@Return(desc = "子分类信息", range = "Result值")
//	private static Result getCategoryByChild(Result rs) {
//		Result result = new Result();
//		Edw_modal_category category = new Edw_modal_category();
//		for (int i = 0; i < rs.getRowCount(); i++) {
//			String parent_category_id = rs.getString(i, "parent_category_id");
//			String modal_pro_id = rs.getString(i, "modal_pro_id");
//			category.setCategory_id(parent_category_id);
//			if (!modal_pro_id.equals(parent_category_id)) {
//				asmSql.clean();
//				asmSql.addSql("select * from edw_modal_category where category_id = ?");
//				asmSql.addParam(category.getCategory_id());
//				Result result3 = Dbo.queryResult(asmSql.sql(), asmSql.params());
//				if (!result3.isEmpty()) {
//					Result result4 = getCategoryByChild(result3);
//					result.add(result4);
//				}
//				result.add(result3);
//			}
//		}
//		return result;
//	}
}
