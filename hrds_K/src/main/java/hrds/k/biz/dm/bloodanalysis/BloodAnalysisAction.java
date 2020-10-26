package hrds.k.biz.dm.bloodanalysis;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Dm_datatable;
import hrds.commons.entity.Dm_datatable_source;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DataTableUtil;

import java.util.List;
import java.util.Map;

@DocClass(desc = "数据管控-血缘分析", author = "BY-HLL", createdate = "2020/4/13 0013 上午 11:04")
public class BloodAnalysisAction extends BaseAction {

	@Method(desc = "根据表名称获取表与表之间的血缘关系", logicStep = "获取表与表之间的关系")
	@Param(name = "table_name", desc = "表名", range = "String类型")
	@Param(name = "search_type", desc = "搜索类型", range = "String类型, 0:表查看,1:字段查看,IsFlag代码项设置")
	@Param(name = "search_relationship", desc = "搜索关系", range = "String类型, 0:影响,1:血缘,IsFlag代码项设置")
	@Return(desc = "返回值说明", range = "返回值取值范围")
	public Map<String, Object> getTableBloodRelationship(String table_name, String search_type,
	                                                     String search_relationship) {
		//数据校验
		Validator.notBlank(table_name, "搜索表名称为空!");
		Validator.notBlank(search_type, "搜索类型为空!");
		Validator.notBlank(search_relationship, "搜索关系为空!");
		//搜索关系 0:影响,1:血缘 IsFlag代码项设置
		IsFlag is_sr = IsFlag.ofEnumByCode(search_relationship);
		//初始化返回结果
		Map<String, Object> tableBloodRelationshipMap;
		//IsFlag.Fou 代表0:影响
		if (is_sr == IsFlag.Fou) {
			tableBloodRelationshipMap = DataTableUtil.influencesDataInfo(Dbo.db(), table_name, search_type);
		}
		//IsFlag.Shi 代表1:血缘
		else if (is_sr == IsFlag.Shi) {
			tableBloodRelationshipMap = DataTableUtil.bloodlineDateInfo(Dbo.db(), table_name, search_type);
		} else {
			//搜索类型不匹配
			throw new BusinessException("搜索类型不匹配! search_type=" + search_relationship);
		}
		return tableBloodRelationshipMap;
	}

	@Method(desc = "模糊搜索表名", logicStep = "模糊搜索表名")
	@Param(name = "table_name", desc = "表名", range = "String类型", valueIfNull = "")
	@Param(name = "search_relationship", desc = "搜索关系", range = "String类型, 0:影响,1:血缘,IsFlag代码项设置")
	@Return(desc = "搜索结果List", range = "搜索结果List")
	public List<String> fuzzySearchTableName(String table_name, String search_relationship) {
		//数据校验
		Validator.notBlank(search_relationship, "搜索关系为空!");
		//初始化执行sql
		SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
		//根据搜索类型检索表名 0:影响,1:血缘 IsFlag代码项设置
		IsFlag is_sr = IsFlag.ofEnumByCode(search_relationship);
		//IsFlag.Fou 代表0:影响
		if (is_sr == IsFlag.Fou) {
			asmSql.clean();
			asmSql.addSql("select table_name from (");
			//加工
			asmSql.addSql("select own_source_table_name as table_name from " + Dm_datatable_source.TableName +
					" group by own_source_table_name ");
			asmSql.addSql(") T where ");
			asmSql.addLikeParam("table_name", "%" + table_name.toLowerCase() + "%", "");
			asmSql.addSql(" group by table_name");
		}
		//IsFlag.Shi 代表1:血缘
		else if (is_sr == IsFlag.Shi) {
			//模型表
			asmSql.clean();
			//加工血缘
			asmSql.addSql("select table_name from (");
			asmSql.addSql(" select datatable_en_name as table_name from " + Dm_datatable.TableName + " group by " +
					"datatable_en_name");
			asmSql.addSql(" ) aa where ");
			asmSql.addLikeParam("lower(table_name)", "%" + table_name + "%", "");
			asmSql.addSql(" group by table_name");
		} else {
			//搜索类型不匹配
			throw new BusinessException("搜索类型不匹配! search_relationship=" + search_relationship);
		}
		//执行SQL,获取结果
		return Dbo.queryOneColumnList(asmSql.sql(), asmSql.params());
	}

}
